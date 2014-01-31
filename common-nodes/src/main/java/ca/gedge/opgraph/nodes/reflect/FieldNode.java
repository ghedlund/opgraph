package ca.gedge.opgraph.nodes.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Member;

import ca.gedge.opgraph.InputField;
import ca.gedge.opgraph.OpContext;
import ca.gedge.opgraph.OutputField;
import ca.gedge.opgraph.app.extensions.NodeSettings;
import ca.gedge.opgraph.exceptions.ProcessingException;

/**
 * Provide access to a non-static {@link Field} for an instance
 * of the declared class.
 *
 */
public class FieldNode extends AbstractReflectNode {
	
	protected InputField objInputField;
	
	protected OutputField outputField;
	
	private Field field;
	
	public FieldNode() {
		super();
	}
	
	public FieldNode(Field field) {
		super();
		setClassMember(field);
	}
	
	@Override
	public void setClassMember(Member member) {
		if(member instanceof Field) {
			super.setDeclaredClass(member.getDeclaringClass());
			super.setClassMember(member);
			setField((Field)member);
		}
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

}
