/*
 * Copyright (C) 2012-2020 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.phon.opgraph.app.commands;

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
