package ca.gedge.opgraph.app.commands;

import java.awt.event.ActionEvent;

/**
 * Interface for command hooks.  Hooks are performed
 * before and after commands are performed and may
 * stop an action for happening.
 *
 */
public interface CommandHook {
	
	/**
	 * Start the given command
	 * 
	 * @param command
	 * @param the action event
	 * @return {@link Boolean} true if the action was
	 *  handeled by the hook and execution should end,
	 *  <code>false</code> otherwise
	 */
	public boolean startCommand(HookableCommand command, ActionEvent evt);
	
	/**
	 * End the given command
	 * 
	 * @param command
	 * @param the action event
	 */
	public void endCommand(HookableCommand command, ActionEvent evt);

}
