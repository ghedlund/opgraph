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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import ca.phon.opgraph.util.DefaultServiceDiscovery;

/**
 * 
 */
public abstract class HookableCommand extends AbstractAction {

	private static final long serialVersionUID = -6236042663851907396L;

	
	private static final Logger LOGGER = Logger
			.getLogger(HookableCommand.class.getName());
	
	public HookableCommand() {
		super();
	}

	public HookableCommand(String name, Icon icon) {
		super(name, icon);
	}

	public HookableCommand(String name) {
		super(name);
	}

	private List<CommandHook> findCommandHooks() {
		final Class<? extends HookableCommand> myType = getClass();
		
		final List<Class<? extends CommandHook>> commandHookTypes = 
				DefaultServiceDiscovery.getInstance().findProviders(CommandHook.class);
		final List<CommandHook> commandHooks = new ArrayList<CommandHook>();
		for(Class<? extends CommandHook> commandHookType:commandHookTypes) {
			final Hook hook = commandHookType.getAnnotation(Hook.class);
			if(hook != null && hook.command() == myType) {
				try {
					final CommandHook commandHook = commandHookType.newInstance();
					commandHooks.add(commandHook);
				} catch (InstantiationException e) {
					LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
				} catch (IllegalAccessException e) {
					LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			}
		}
		return Collections.unmodifiableList(commandHooks);
	}
	
	/**
	 * This method is finalized.  Sub-classes should override the
	 * hookableActionPerformed() method instead.  This method
	 * handles calling {@link CommandHook}s before and after
	 * the hookableActionPerformed() method is called.
	 * 
	 * @param arg0
	 */
	@Override
	public final void actionPerformed(ActionEvent arg0) {
		final List<CommandHook> commandHooks = findCommandHooks();
		for(CommandHook hook:commandHooks) {
			if(hook.startCommand(this, arg0)) {
				// quit if the hook tells us
				return;
			}
		}
		
		hookableActionPerformed(arg0);
		
		for(CommandHook hook:commandHooks) {
			hook.endCommand(this, arg0);
		}
	}
	
	/**
	 * Sub-classes should override this method instead of actionPerformed
	 * 
	 * @param arg0
	 */
	public abstract void hookableActionPerformed(ActionEvent arg0);
	
}
