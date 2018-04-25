package ca.phon.opgraph.nodes.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class StaticFieldNode extends FieldNode {

	public StaticFieldNode() {
		super();
	}

	public StaticFieldNode(Field field) {
		super(field);
	}

	@Override
	public void setField(Field field) {
		if(!Modifier.isStatic(field.getModifiers()))
			throw new IllegalArgumentException("Field must be static");
		super.setField(field);
		super.removeField(objInputField);
	}
	
}
