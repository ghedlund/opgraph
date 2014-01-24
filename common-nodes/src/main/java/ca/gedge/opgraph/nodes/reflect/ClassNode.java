package ca.gedge.opgraph.nodes.reflect;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.gedge.opgraph.InputField;
import ca.gedge.opgraph.OpContext;
import ca.gedge.opgraph.OpNode;
import ca.gedge.opgraph.OpNodeInfo;
import ca.gedge.opgraph.OutputField;
import ca.gedge.opgraph.app.GraphDocument;
import ca.gedge.opgraph.app.extensions.NodeSettings;
import ca.gedge.opgraph.exceptions.ProcessingException;

/**
 * Create a new based on the given class definition. Input/output nodes
 * are create for the properties of the class.
 */
@OpNodeInfo(name="ClassNode", category="general", description="", showInLibrary=false)
public class ClassNode extends OpNode implements ClassNodeProtocol, NodeSettings {

	private static final Logger LOGGER = Logger.getLogger(ClassNode.class
			.getName());
	
	/** Input field for the value */
	protected InputField inputValueField;
	
	/** Output (pass-through) field */
	protected OutputField outputValueField;
	
	/** List of scanned input fields from class */
	protected List<ClassInputField> classInputs;
	
	/** List of scanned output fields from class */
	protected List<ClassOutputField> classOutputs;
	
	private Class<?> type;
	
	public ClassNode() {
		super();
		putExtension(NodeSettings.class, this);
	}
	
	public ClassNode(Class<?> clazz) {
		super();
		setClass(clazz);
		putExtension(NodeSettings.class, this);
	}
	
	public void setClass(Class<?> clazz) {
		super.setName(clazz.getSimpleName());
		
		this.type = clazz;
		
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
	public Class<?> getDeclaredClass() {
		return this.type;
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
