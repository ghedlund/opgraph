package ca.gedge.opgraph.nodes.reflect;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
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

import ca.gedge.opgraph.InputField;
import ca.gedge.opgraph.OpContext;
import ca.gedge.opgraph.OpGraph;
import ca.gedge.opgraph.OutputField;
import ca.gedge.opgraph.Processor;
import ca.gedge.opgraph.app.GraphDocument;
import ca.gedge.opgraph.app.extensions.NodeSettings;
import ca.gedge.opgraph.exceptions.ProcessingException;
import ca.gedge.opgraph.nodes.general.MacroNode;
import ca.gedge.opgraph.util.ReflectUtil;

/**
 * 
 */
public class IterableClassNode extends MacroNode implements ClassNodeProtocol, NodeSettings {
	
	private static final Logger LOGGER = Logger
			.getLogger(IterableClassNode.class.getName());
	
	/** 
	 * {@link OpContext} key for the current value
	 */
	public static final String CURRENT_VALUE_KEY = "currentValue";
	
	/** Input field for the value */
	private InputField inputValueField;
	
	/** Output (pass-through) field */
	private OutputField outputValueField;
	
	/** List of scanned input fields from class */
	private List<ClassInputField> classInputs;
	
	/** List of scanned output fields from class */
	private List<ClassOutputField> classOutputs;
	
	private Class<?> type;
	
	public IterableClassNode() {
		super();
		putExtension(NodeSettings.class, this);
	}
	
	public IterableClassNode(OpGraph graph) {
		super(graph);
		putExtension(NodeSettings.class, this);
	}
	
	public IterableClassNode(Class<?> clazz) {
		super();
		setClass(clazz);
		putExtension(NodeSettings.class, this);
	}
	
	public IterableClassNode(OpGraph graph, Class<?> clazz) {
		super(graph);
		setClass(clazz);
		putExtension(NodeSettings.class, this);
	}
	
	public void setClass(Class<?> clazz) {
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
		
		// create a new node inside the graph that show depicts the current value
		final ContextualItemClassNode node = new ContextualItemClassNode(CURRENT_VALUE_KEY, genericType);
		final String nodeName = node.getName();
		
		if(getGraph().getNodeById(nodeName, false) == null) {
			graph.add(node);
		}
		inputValueField = new InputField("value", "input value", clazz);
		inputValueField.setOptional(true);
		inputValueField.setFixed(true);
		putField(inputValueField);
		
		outputValueField = new OutputField("value", "output value", true, clazz);
		putField(outputValueField);
		
		final ClassNodeFieldGenerator fieldGenerator = new ClassNodeFieldGenerator();
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
				throw new ProcessingException(e);
			} catch (IllegalAccessException e) {
				throw new ProcessingException(e);
			}
		}
		
		for(ClassInputField classInput:classInputs) {
			final Object val = context.get(classInput);
			if(val != null) {
				final Method setMethod = classInput.setMethod;
				try {
					setMethod.invoke(obj, val);
				} catch (IllegalArgumentException e) {
					throw new ProcessingException(e);
				} catch (IllegalAccessException e) {
					throw new ProcessingException(e);
				} catch (InvocationTargetException e) {
					throw new ProcessingException(e);
				}
			}
		}
		
		// Process
		if(graph != null) {
			final Processor processor = new Processor(graph);
			
			final Iterable<?> iterable = (Iterable<?>)obj;
			final Iterator<?> itr = iterable.iterator();
			while(itr.hasNext()) {
				processor.reset(context);
				
				final Object currentValue = itr.next();
				context.put(CURRENT_VALUE_KEY, currentValue);

				processor.stepAll();
				if(processor.getError() != null)
					throw processor.getError();
			}
		}
		
		for(ClassOutputField classOutput:classOutputs) {
			try {
				final Object val = classOutput.getMethod.invoke(obj, new Object[0]);
				context.put(classOutput, val);
			} catch (IllegalArgumentException e) {
				throw new ProcessingException(e);
			} catch (IllegalAccessException e) {
				throw new ProcessingException(e);
			} catch (InvocationTargetException e) {
				throw new ProcessingException(e);
			}
		}
		
		context.put(outputValueField, obj);
	}

	@Override
	public Class<?> getDeclaredClass() {
		return type;
	}

	/*
	 * NodeSettings
	 */
	private final static String CLASSNAME_SETTINGS_KEY = 
			ClassNode.class.getName() + ".className";
	
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
				setClass(clazz);
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}
}
