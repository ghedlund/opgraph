package ca.phon.opgraph.nodes.reflect;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.Processor;
import ca.phon.opgraph.ProcessorListener;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.components.canvas.NodeStyle;
import ca.phon.opgraph.app.extensions.NodeSettings;
import ca.phon.opgraph.exceptions.ProcessingException;
import ca.phon.opgraph.extensions.CustomProcessing;
import ca.phon.opgraph.extensions.CustomProcessing.CustomProcessor;
import ca.phon.opgraph.nodes.general.MacroNode;
import ca.phon.opgraph.util.ReflectUtil;
import ca.phon.opgraph.validators.TypeValidator;

/**
 * 
 */
public class IterableClassNode extends MacroNode implements NodeSettings, ReflectNode, CustomProcessing, CustomProcessor {
	
	private static final Logger LOGGER = Logger
			.getLogger(IterableClassNode.class.getName());
	
	static {
		NodeStyle.installStyleForNode(IterableClassNode.class, NodeStyle.ITERATION);
	}
	
	/** 
	 * {@link OpContext} key for the current value
	 */
	public static final String CURRENT_VALUE_KEY = "currentValue";
	
	/** Input field for the value */
	private InputField inputValueField;
	
	/** Output (pass-through) field */
	private OutputField outputValueField;
	
	/** List of scanned input fields from class */
	private List<ObjectNodePropertyInputField> classInputs;
	
	/** List of scanned output fields from class */
	private List<ObjectNodePropertyOutputField> classOutputs;
	
	private Class<?> type;
	
	public IterableClassNode() {
		super();
		putExtension(NodeSettings.class, this);
		putExtension(CustomProcessing.class, this);
	}
	
	public IterableClassNode(OpGraph graph) {
		super(graph);
		putExtension(NodeSettings.class, this);
		putExtension(CustomProcessing.class, this);
	}
	
	public IterableClassNode(Class<?> clazz) {
		super();
		setDeclaredClass(clazz);
		putExtension(NodeSettings.class, this);
		putExtension(CustomProcessing.class, this);
	}
	
	public IterableClassNode(OpGraph graph, Class<?> clazz) {
		super(graph);
		setDeclaredClass(clazz);
		putExtension(NodeSettings.class, this);
		putExtension(CustomProcessing.class, this);
	}
	
	public void setDeclaredClass(Class<?> clazz) {
		if(!Iterable.class.isAssignableFrom(clazz))
			throw new IllegalArgumentException("Class must implement the Iterable interface.");

		this.type = clazz;
		super.setName(clazz.getSimpleName());
	
		final List<ParameterizedType> parameterizedTypes = ReflectUtil.getParameterizedTypesForClass(type);
		final Set<Class<?>> paramTypes = new HashSet<Class<?>>();
		for(ParameterizedType parameterizedType:parameterizedTypes) {
			for(Type actualType:parameterizedType.getActualTypeArguments()) {
				final String typeName = actualType.toString();
				if(typeName.startsWith("class")) {
					try {
						final Class<?> t = Class.forName(typeName.split("\\p{Space}")[1]);
						paramTypes.add(t);
					} catch (ClassNotFoundException e) {
						LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				}
			}
		}
		
		// we only handle one type of iterable object, if there are more default to Object.class
		final Class<?> genericType = (paramTypes.size() == 1 ? paramTypes.iterator().next() : Object.class);
		
		// XXX Change this to a new iteration value node
		// create a new node inside the graph that show depicts the current value
		final ContextualItemClassNode node = new ContextualItemClassNode(CURRENT_VALUE_KEY, genericType);
				
		if(graph.getVertices().size() == 0) {
			graph.add(node);
		}
		
		inputValueField = new InputField("collection", "input value", new TypeValidator() {
			
			@Override
			public boolean isAcceptable(Class<?> cls) {
				return clazz.isAssignableFrom(cls);
			}
			
			@Override
			public boolean isAcceptable(Object obj) {
				return isAcceptable(obj.getClass());
			}
			
		});
		
		inputValueField.setOptional(false);
		inputValueField.setFixed(true);
		putField(1, inputValueField);
		
		outputValueField = new OutputField("collection", "output value", true, clazz);
		putField(0, outputValueField);
		
		final ObjectNodeFieldGenerator fieldGenerator = new ObjectNodeFieldGenerator();
		fieldGenerator.scanClass(clazz);
		classInputs = fieldGenerator.getInputFields();
		for(InputField inputField:classInputs) putField(inputField);
		
		classOutputs = fieldGenerator.getOutputFields();
		for(OutputField outputField:classOutputs) putField(outputField);
	}
	
	@Override
	public void operate(OpContext context) throws ProcessingException {
		Object obj = context.get(inputValueField);
		if(obj == null) {
			// attempt to instantiate a new object
			try {
				obj = type.newInstance();
			} catch (InstantiationException e) {
				throw new ProcessingException(null, e);
			} catch (IllegalAccessException e) {
				throw new ProcessingException(null, e);
			}
		}
		
		for(ObjectNodePropertyInputField classInput:classInputs) {
			final Object val = context.get(classInput);
			if(val != null) {
				final Method setMethod = classInput.setMethod;
				try {
					setMethod.invoke(obj, val);
				} catch (IllegalArgumentException e) {
					throw new ProcessingException(null, e);
				} catch (IllegalAccessException e) {
					throw new ProcessingException(null, e);
				} catch (InvocationTargetException e) {
					throw new ProcessingException(null, e);
				}
			}
		}
		
		// Process
		if(graph != null) {
			final Processor processor = new Processor(graph);
			for(ProcessorListener listener:getProcessorListeners())
				processor.addProcessorListener(listener);
			
			final Iterable<?> iterable = (Iterable<?>)obj;
			final Iterator<?> itr = iterable.iterator();
			while(itr.hasNext()) {
				processor.reset(context);
				
				mapInputs(context);
				
				final Object currentValue = itr.next();
				context.put(CURRENT_VALUE_KEY, currentValue);

				processor.stepAll();
				if(processor.getError() != null)
					throw processor.getError();
				
				mapOutputs(context);
			}
		}
		
		for(ObjectNodePropertyOutputField classOutput:classOutputs) {
			try {
				final Object val = classOutput.getMethod.invoke(obj, new Object[0]);
				context.put(classOutput, val);
			} catch (IllegalArgumentException e) {
				throw new ProcessingException(null, e);
			} catch (IllegalAccessException e) {
				throw new ProcessingException(null, e);
			} catch (InvocationTargetException e) {
				throw new ProcessingException(null, e);
			}
		}
		
		context.put(outputValueField, obj);
	}

	@Override
	public Class<?> getDeclaredClass() {
		return type;
	}
	
	@Override
	public Member getClassMember() {
		return null;
	}

	/*
	 * NodeSettings
	 */
	private final static String CLASSNAME_SETTINGS_KEY = 
			IterableClassNode.class.getName() + ".className";
	
	@Override
	public Component getComponent(GraphDocument document) {
		return null;
	}

	@Override
	public Properties getSettings() {
		final Properties retVal = new Properties();
		if(type != null) {
			retVal.put(CLASSNAME_SETTINGS_KEY, type.getName());
		}
		return retVal;
	}

	@Override
	public void loadSettings(Properties properties) {
		if(properties.containsKey(CLASSNAME_SETTINGS_KEY)) {
			final String className = properties.getProperty(CLASSNAME_SETTINGS_KEY, "java.lang.Object");
			try {
				final Class<?> clazz = Class.forName(className);
				setDeclaredClass(clazz);
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}
	
	/*
	 * Custom processing
	 */
	private Object obj;
	
	private OpContext globalContext;
	private Iterator<?> iterator;
	private Iterator<OpNode> processIterator;
	private Object currentValue;

	@Override
	public boolean hasNext() {
		boolean hasMoreElements = (iterator != null && iterator.hasNext());
		boolean hasMoreNodes = (processIterator != null && processIterator.hasNext());
		return hasMoreElements || hasMoreNodes;
	}

	@Override
	public OpNode next() {
		if(currentValue == null || !processIterator.hasNext()) {
			currentValue = iterator.next();
			processIterator = graph.getVertices().iterator();
			
			globalContext.put(CURRENT_VALUE_KEY, currentValue);
		}
		return processIterator.next();
	}

	@Override
	public void initialize(OpContext context) {
		globalContext = context;
		obj = context.get(inputValueField);
		if(obj == null) {
			// attempt to instantiate a new object
			try {
				obj = type.newInstance();
			} catch (InstantiationException e) {
				throw new ProcessingException(null, e);
			} catch (IllegalAccessException e) {
				throw new ProcessingException(null, e);
			}
		}
		
		for(ObjectNodePropertyInputField classInput:classInputs) {
			final Object val = context.get(classInput);
			if(val != null) {
				final Method setMethod = classInput.setMethod;
				try {
					setMethod.invoke(obj, val);
				} catch (IllegalArgumentException e) {
					throw new ProcessingException(null, e);
				} catch (IllegalAccessException e) {
					throw new ProcessingException(null, e);
				} catch (InvocationTargetException e) {
					throw new ProcessingException(null, e);
				}
			}
		}
		
		mapInputs(context);
		
		final Iterable<?> iterable = (Iterable<?>)obj;
		iterator = iterable.iterator();
		if(graph != null) {
			processIterator = graph.getVertices().iterator();
		}
	}

	@Override
	public void terminate(OpContext context) {
		for(ObjectNodePropertyOutputField classOutput:classOutputs) {
			try {
				final Object val = classOutput.getMethod.invoke(obj, new Object[0]);
				context.put(classOutput, val);
			} catch (IllegalArgumentException e) {
				throw new ProcessingException(null, e);
			} catch (IllegalAccessException e) {
				throw new ProcessingException(null, e);
			} catch (InvocationTargetException e) {
				throw new ProcessingException(null, e);
			}
		}
		
		context.put(outputValueField, obj);
	}
	
	@Override
	public CustomProcessor getCustomProcessor() {
		return this;
	}
	
}
