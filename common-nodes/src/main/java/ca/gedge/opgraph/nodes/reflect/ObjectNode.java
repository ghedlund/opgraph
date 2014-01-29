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
 * Creates a node which provides access to the get/set property methods
 * of an instance of the declared class.
 */
@OpNodeInfo(name="ClassNode", category="general", description="", showInLibrary=false)
public class ObjectNode extends AbstractReflectNode {

	private static final Logger LOGGER = Logger.getLogger(ObjectNode.class
			.getName());
	
	/** Input field for the value */
	protected InputField inputValueField;
	
	/** Output (pass-through) field */
	protected OutputField outputValueField;
	
	/** List of scanned input fields from class */
	protected List<ObjectNodePropertyInputField> classInputs;
	
	/** List of scanned output fields from class */
	protected List<ObjectNodePropertyOutputField> classOutputs;
	
	private Class<?> type;
	
	public ObjectNode() {
		super();
		putExtension(NodeSettings.class, this);
	}
	
	public ObjectNode(Class<?> clazz) {
		super();
		setDeclaredClass(clazz);
		putExtension(NodeSettings.class, this);
	}
	
	public void setDeclaredClass(Class<?> clazz) {
		super.setDeclaredClass(clazz);
		super.setName(clazz.getSimpleName());
		
		this.type = clazz;
		
		inputValueField = new InputField("obj", "object instance", clazz);
		inputValueField.setOptional(false);
		inputValueField.setFixed(true);
		putField(inputValueField);
		
		outputValueField = new OutputField("obj", "object instance", true, clazz);
		putField(outputValueField);
		
		final ObjectNodeFieldGenerator fieldGenerator = new ObjectNodeFieldGenerator();
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
		
		if(obj == null)
			throw new ProcessingException(new NullPointerException(inputValueField.getKey()));
		
		for(ObjectNodePropertyInputField classInput:classInputs) {
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
		
		for(ObjectNodePropertyOutputField classOutput:classOutputs) {
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

}
