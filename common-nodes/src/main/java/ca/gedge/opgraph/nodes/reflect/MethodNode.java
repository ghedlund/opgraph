package ca.gedge.opgraph.nodes.reflect;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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

public class MethodNode extends OpNode implements NodeSettings {
	
	private static final Logger LOGGER = Logger
			.getLogger(MethodNode.class.getName());
	
	// internal method
	private Method method;
	
	private InputField objField;
	
	private OutputField outputField;
	
	private List<InputField> argumentInputs = new ArrayList<InputField>();
	
	public MethodNode() {
		super();
		putExtension(NodeSettings.class, this);
	}
	
	public MethodNode(Method method) {
		setMethod(method);
		putExtension(NodeSettings.class, this);
	}
	
	public void setMethod(Method method) {
		this.method = method;
		super.setName(method.getDeclaringClass().getSimpleName() + "#" + ReflectUtil.getSignature(method));
		// optional object instance
		final Class<?> inputObjType = method.getDeclaringClass();
		objField = new InputField("obj", "The object instance", inputObjType);
		objField.setOptional(true);
		putField(objField);
		
		// setup parameters as inputs
		argumentInputs.clear();
		final Class<?> paramTypes[] = method.getParameterTypes();
		for(int i = 0; i < paramTypes.length; i++) {
			final InputField inputField = new InputField("arg" + (i+1), "", paramTypes[i]);
			inputField.setOptional(true);
			putField(inputField);
			argumentInputs.add(inputField);
		}
		
		if(method.getReturnType() != null && method.getReturnType() != void.class) {
			outputField = new OutputField("value", "return value of method", false, method.getReturnType());
			putField(outputField);
		}
	}

	@Override
	public void operate(OpContext context) throws ProcessingException {
		final Object[] args = new Object[argumentInputs.size()];
		for(int i = 0; i < argumentInputs.size(); i++) {
			final InputField argumentInput = argumentInputs.get(i);
			final Object val = context.get(argumentInput);
			args[i] = val;
		}
		
		try {
			final Object retVal = invokeMethod(context.get(objField), args);
			context.put(outputField, retVal);
		} catch (IllegalArgumentException e) {
			throw new ProcessingException(e);
		} catch (IllegalAccessException e) {
			throw new ProcessingException(e);
		} catch (InvocationTargetException e) {
			throw new ProcessingException(e);
		}
	}
	
	protected Object invokeMethod(Object instance, Object[] args)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException  {
			final Object retVal = method.invoke(instance, args);
			return retVal;
	}

	/*
	 * NodeSettings
     */
	private final static String CLASSNAME_SETTINGS_KEY =
			ConstructorNode.class.getName() + ".className";
	private final static String METHOD_SIGNATURE_KEY  =
			ConstructorNode.class.getName() + ".methodSig";
	
	@Override
	public Component getComponent(GraphDocument document) {
		return null;
	}

	@Override
	public Properties getSettings() {
		final Properties retVal = new Properties();
		if(method != null) {
			final String className = method.getDeclaringClass().getName();
			final String methodSig = ReflectUtil.getSignature(method, true);
			
			retVal.put(CLASSNAME_SETTINGS_KEY, className);
			retVal.put(METHOD_SIGNATURE_KEY, methodSig);
		}
		return retVal;
	}

	@Override
	public void loadSettings(Properties properties) {
		if(properties.containsKey(CLASSNAME_SETTINGS_KEY) &&
				properties.containsKey(METHOD_SIGNATURE_KEY)) {
			final String className = properties.getProperty(CLASSNAME_SETTINGS_KEY, "java.lang.Object");
			final String methodSig = properties.getProperty(METHOD_SIGNATURE_KEY, "toString()");
			
			try {
				setMethod(ReflectUtil.getMethodFromSignature(Class.forName(className), methodSig));
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
