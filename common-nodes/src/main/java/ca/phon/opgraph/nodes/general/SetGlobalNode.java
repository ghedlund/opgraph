package ca.phon.opgraph.nodes.general;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OpNodeInfo;
import ca.phon.opgraph.exceptions.ProcessingException;

@OpNodeInfo(name="Set Global", description = "Set global variable", category = "General", showInLibrary = true)
public class SetGlobalNode extends OpNode {

	private final InputField globalNameInput = new InputField("globalName", "Global variable name",
		false, true, String.class);

	private final InputField globalValueInput = new InputField("value", "Value for global variable",
			false, true, Object.class);

	@Override
	public void operate(OpContext context) throws ProcessingException {
		OpContext globalContext = context;
		while(globalContext.getParent() != null)
			globalContext = globalContext.getParent();

		String globalName = context.get(globalNameInput).toString();
		Object value = context.get(globalValueInput);

		globalContext.put(globalName, value);
	}

}
