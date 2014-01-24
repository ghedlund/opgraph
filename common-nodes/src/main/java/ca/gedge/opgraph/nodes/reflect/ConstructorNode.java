package ca.gedge.opgraph.nodes.reflect;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
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

/**
 * Node for object constructors.
 */
public class ConstructorNode extends OpNode implements NodeSettings {
	
	private static final Logger LOGGER = Logger
			.getLogger(ConstructorNode.class.getName());

	private Constructor<?> constructor;
	
	private OutputField outputField;
	
	public ConstructorNode() {
		super();
		putExtension(NodeSettings.class, this);
	}
	
	public ConstructorNode(Constructor<?> constructor) {
		super();
		setConstructor(constructor);
		putExtension(NodeSettings.class, this);
	}
	
	public void setConstructor(Constructor<?> constructor) {
		this.constructor = constructor;
		
		setName(constructor.getDeclaringClass().getSimpleName() + "#" + ReflectUtil.getSignature(constructor, false));
		
		final Class<?> type = constructor.getDeclaringClass();
		final OutputField outputField = new OutputField("value", "constructor", true, type);
		putField(outputField);
		
		for(int i = 0; i < constructor.getParameterTypes().length; i++) {
			final Class<?> paramType = constructor.getParameterTypes()[i];
			final InputField inputField = new InputField("arg" + (i+1), "", paramType);
			inputField.setOptional(true);
			putField(inputField);
		}
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		final Object[] args = new Object[getInputFields().size()];
		for(int i = 0; i < args.length; i++) {
			final InputField inputField = getInputFields().get(i);
			args[0] = context.get(inputField);
		}
		
		try {
			final Object val = constructor.newInstance(args);
			context.put(outputField, val);
		} catch (IllegalArgumentException e) {
			throw new ProcessingException(e);
		} catch (InstantiationException e) {
			throw new ProcessingException(e);
		} catch (IllegalAccessException e) {
			throw new ProcessingException(e);
		} catch (InvocationTargetException e) {
			throw new ProcessingException(e);
		}
	}
	
	/*
	 * NodeSettings
     */
	private final static String CLASSNAME_SETTINGS_KEY =
			ConstructorNode.class.getName() + ".className";
	private final static String CONSTRUCTOR_SIGNATURE_KEY =
			ConstructorNode.class.getName() + ".constructorSig";
	
	@Override
	public Component getComponent(GraphDocument document) {
		return null;
	}

	@Override
	public Properties getSettings() {
		final Properties retVal = new Properties();
		if(constructor != null) {
			final String className = constructor.getDeclaringClass().getName();
			final String constructorSig = ReflectUtil.getSignature(constructor, true);
			
			retVal.put(CLASSNAME_SETTINGS_KEY, className);
			retVal.put(CONSTRUCTOR_SIGNATURE_KEY, constructorSig);
		}
		return retVal;
	}

	@Override
	public void loadSettings(Properties properties) {
		if(properties.containsKey(CLASSNAME_SETTINGS_KEY) && 
				properties.containsKey(CONSTRUCTOR_SIGNATURE_KEY)) {
			final String className = properties.getProperty(CLASSNAME_SETTINGS_KEY, "java.lang.Object");
			final String cstrSig = properties.getProperty(CONSTRUCTOR_SIGNATURE_KEY, "<init>()");
			
			try {
				final Constructor<?> constructor = ReflectUtil.getConstructorFromSignature(Class.forName(className), cstrSig);
				setConstructor(constructor);
			} catch (SecurityException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			} catch (NoSuchMethodException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}
	
}
