/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
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
/**
 * 
 */
package ca.phon.opgraph.nodes.general.script;

import java.util.logging.Level;
import java.util.logging.Logger;

import ca.phon.opgraph.Processor;

/**
 */
public class LoggingHelper {
	@SuppressWarnings("unused")
	private static class ScriptPrinter {
		private final Logger LOGGER = Logger.getLogger(Processor.class.getName());

		/** The level for logged messages */
		private Level level;

		/**
		 * Constructs a script printer that logs message at the given level.
		 * 
		 * @param level  the level of logs
		 */
		public ScriptPrinter(Level level) {
			this.level = level;
		}

		/**
		 * Prints a given message with a new line.
		 * 
		 * @param message  the message
		 */
		public void println(Object message) {
			String stringMessage = (message == null ? "null" : message.toString());
			LOGGER.log(level, stringMessage + "\n");
		}

		/**
		 * Prints a given message.
		 * 
		 * @param message  the message
		 */
		public void print(Object message) {
			String stringMessage = (message == null ? "null" : message.toString());
			LOGGER.log(level, stringMessage);
		}

		/**
		 * Formats a set of objects given a specified format string.
		 * 
		 * @param format  the format string
		 * @param args  the arguments to use in the format string
		 */
		public void format(String format, Object... args) {
			LOGGER.log(level, String.format(format, args));
		}
	}

	/** STDOUT printer */
	public final ScriptPrinter out = new ScriptPrinter(Level.INFO);

	/** STDERR printer */
	public final ScriptPrinter err = new ScriptPrinter(Level.SEVERE);
}
