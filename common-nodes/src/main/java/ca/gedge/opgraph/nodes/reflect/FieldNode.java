package ca.gedge.opgraph.nodes.reflect;

import java.awt.Component;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.gedge.opgraph.InputField;
import ca.gedge.opgraph.OpContext;
import ca.gedge.opgraph.OpNode;
import ca.gedge.opgraph.OutputField;
import ca.gedge.opgraph.app.GraphDocument;
import ca.gedge.opgraph.app.extensions.NodeSettings;
import ca.gedge.opgraph.exceptions.ProcessingException;
import ca.gedge.opgraph.util.ReflectUtil;

public class FieldNode extends OpNode implements NodeSettings {
	
	private static final Logger LOGGER = Logger.getLogger(FieldNode.class
			.getName());
	
	protected InputField objInputField;
	
	protected OutputField outputField;
	
	private Field field;
	
	public FieldNode() {
		super();
		putExtension(NodeSettings.class, this);
	}
	
	public FieldNode(Field field) {
		super();
		setField(field);
		putExtension(NodeSettings.class, this);
	}
	
	public void setField(Field field) {
		this.field = field;
		setName(field.getDeclaringClass().getSimpleName() + "." + field.getName());
		
		objInputField = new InputField("obj", "", field.getDeclaringClass());
		putField(objInputField);
		
		outputField = new OutputField("value", "", true, field.getType());
		putField(outputField);
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		final Object instance = context.get(objInputField);
		
		try {
			final Object outputVal = field.get(instance);
			context.put(outputField, outputVal);
		} catch (IllegalArgumentException e) {
			throw new ProcessingException(e);
		} catch (IllegalAccessException e) {
			throw new ProcessingException(e);
		}
	}

	/*
	 * NodeSettings
     */
	private final static String CLASSNAME_SETTINGS_KEY =
			ConstructorNode.class.getName() + ".className";
	private final static String FIELD_NAME_KEY  =
			ConstructorNode.class.getName() + ".fieldName";
	
	@Override
	public Component getComponent(GraphDocument document) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getSettings() {
		final Properties retVal = new Properties();
		if(field != null) {
			final String className = field.getDeclaringClass().getName();
			final String fieldName = field.getName();
			
			retVal.put(CLASSNAME_SETTINGS_KEY, className);
			retVal.put(FIELD_NAME_KEY, fieldName);
		}
		return retVal;
	}

	@Override
	public void loadSettings(Properties properties) {
		if(properties.containsKey(CLASSNAME_SETTINGS_KEY) &&
				properties.containsKey(FIELD_NAME_KEY)) {
			final String className = properties.getProperty(CLASSNAME_SETTINGS_KEY, "java.lang.Object");
			final String fieldName = properties.getProperty(FIELD_NAME_KEY, "");
			
			try {
				final Class<?> clazz = Class.forName(className);
				final Field field = clazz.getDeclaredField(fieldName);
				setField(field);
			} catch (SecurityException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			} catch (NoSuchFieldException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}

}
