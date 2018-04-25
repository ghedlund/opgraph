package ca.phon.opgraph.app.edits.node;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ca.phon.opgraph.OpNode;

public class BreakpointEdit extends AbstractUndoableEdit {

	private static final long serialVersionUID = -5367065464117238764L;
	
	private OpNode node;
	
	private boolean shouldBreak;

	public BreakpointEdit(OpNode node, boolean shouldBreak) {
		super();
		
		this.node = node;
		this.shouldBreak = shouldBreak;
	}
	
	public void perform() {
		node.setBreakpoint(shouldBreak);
	}
	
	@Override
	public boolean isSignificant() {
		return node.isBreakpoint() != shouldBreak;
	}
	
	@Override
	public String getPresentationName() {
		return "Toggle breakpoint";
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		perform();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		node.setBreakpoint(!shouldBreak);
	}
	
}
