package ca.phon.opgraph.app.edits.node;

import ca.phon.opgraph.ContextualItem;

import javax.swing.undo.*;

/**
 * Modify field key
 *
 */
public class FieldKeyEdit extends AbstractUndoableEdit {

	private ContextualItem field;

	private final String origKey;

	private final String newKey;

	public FieldKeyEdit(ContextualItem field, String newKey) {
		super();

		this.origKey = field.getKey();
		this.newKey = newKey;

		perform();
	}

	private void perform() {
		field.setKey(newKey);
	}

	@Override
	public void undo() throws CannotUndoException {
		field.setKey(origKey);
	}

	@Override
	public void redo() throws CannotRedoException {
		perform();
	}
	
}
