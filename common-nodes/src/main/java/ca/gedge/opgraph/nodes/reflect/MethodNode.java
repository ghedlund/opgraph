package ca.gedge.opgraph.nodes.reflect;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
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

public class MethodNode extends AbstractReflectNode {
	
	private static final Logger LOGGER = Logger
			.getLogger(MethodNode.class.getName());
	
	// internal method
	private Method method;
	
	private InputField objField;
	
	private OutputField outputField;
	
	private List<InputField> argumentInputs = new ArrayList<InputField>();
	
	public MethodNode() {
		super();
	}
	
	public MethodNode(Method method) {
		super();
		setClassMember(method);
	}
	
	@Override
	public void setClassMember(Member classMember) {
		if(classMember instanceof Method) {
			super.setDeclaredClass(classMember.getDeclaringClass());
			super.setClassMember(classMember);
			setMethod(method);
		}
	}

	public void setMethod(Method method) {
		this.method = method;
		super.setName(method.getDeclaringClass().getSimpleName() + "#" + ReflectUtil.getSignature(method));
		// optional object instance
		final Class<?> inputObjType = method.getDeclaringClass();
		objField = new InputField("obj", "The object instance", inputObjType);
		objField.setOptional(false);
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
			Class<?> returnType = method.getReturnType();
			if(returnType.isPrimitive()) {
				returnType = ReflectUtil.wrapperClassForPrimitive(returnType);
			}
			outputField = new OutputField("value", "return value of method", true, returnType);
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

}

