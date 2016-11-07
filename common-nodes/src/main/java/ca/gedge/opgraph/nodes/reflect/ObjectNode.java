package ca.gedge.opgraph.nodes.reflect;

import java.awt.Component;
import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ca.gedge.opgraph.InputField;
import ca.gedge.opgraph.OpContext;
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
	
	private final static String PROP_NAME = "contextKey";
	
	private JTextField contextKeyField;
	
	private String contextKey = null;
	
	private Object value = null;
	
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
		if(getName().equals("ClassNode"))
			super.setName(clazz.getSimpleName());
		
		this.type = clazz;
		
		inputValueField = new InputField("obj", "object instance", clazz);
		inputValueField.setOptional(true);
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
	
	public String getContextKey() {
		return (contextKeyField != null ? contextKeyField.getText() : contextKey);
	}
	
	public void setContextKey(String key) {
		this.contextKey = key;
		if(contextKeyField != null) {
			contextKeyField.setText(key);
		}
	}
	
	public Object getValue() {
		return this.value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public Class<?> getDeclaredClass() {
		return this.type;
	}
	
	@Override
	public void operate(OpContext context) throws ProcessingException {
		Object obj = 
				(this.value == null ? context.get(inputValueField) : value);
		if(obj == null) {
			// look for value in context
			obj = context.get(getContextKey());
		}
		
		if(obj == null)
			throw new ProcessingException(null, new NullPointerException(inputValueField.getKey()));
		
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

	private JPanel createSettingsPanel() {
		final JPanel panel = new JPanel(new FlowLayout());

		final String oldContextKey = getContextKey();
		contextKeyField = new JTextField();
		contextKeyField.setColumns(20);
		if(oldContextKey != null)
			contextKeyField.setText(oldContextKey);
		
		panel.add(new JLabel("Context Key"));
		panel.add(contextKeyField);
		
		return panel;
	}
	
	@Override
	public Component getComponent(GraphDocument document) {
		return createSettingsPanel();
	}

	@Override
	public Properties getSettings() {
		final Properties props = super.getSettings();
		
		if(getContextKey() != null) {
			props.put(PROP_NAME, getContextKey());
		}
		
		return props;
	}

	@Override
	public void loadSettings(Properties properties) {
		super.loadSettings(properties);
		
		if(properties.containsKey(PROP_NAME)) {
			this.contextKey = properties.getProperty(PROP_NAME);
		}
	}

}
