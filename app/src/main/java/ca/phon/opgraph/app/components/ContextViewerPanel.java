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
package ca.phon.opgraph.app.components;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import ca.phon.opgraph.InputField;
import ca.phon.opgraph.OpContext;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.OutputField;
import ca.phon.opgraph.Processor;
import ca.phon.opgraph.exceptions.ProcessingException;

/**
 * A component to show inputs and output values from a node's {@link OpContext}.
 */
public class ContextViewerPanel extends JEditorPane {
	/** The processing context being debugged */
	private Processor processor;

	/** The node currently being viewed */
	private OpNode node;

	/**
	 * Default constructor.
	 */
	public ContextViewerPanel() {
		this.processor = null;
		this.node = null;

		// Set up editor kit
		final HTMLEditorKit kit = new HTMLEditorKit();
		final StyleSheet style = kit.getStyleSheet();
		style.addRule("body { padding: 5px; }");
		style.addRule("ul { margin: 5px 15px; }");
		style.addRule(".error { font-family: Courier,Courier New,Console,System; color: #ff0000; white-space: pre; }");

		// Initialize
		setEditorKit(kit);
		setEditable(false);

		updateDebugInfo();
	}

	/**
	 * Gets the processing context this component is using for displaying
	 * debug information on a node.
	 * 
	 * @return the context, or <code>null</code> if no context is being used
	 */
	public Processor getProcessingContext() {
		return processor;
	}

	/**
	 * Sets the processing context this component should use for displaying
	 * debug information on a node.
	 * 
	 * @param context  the context, or <code>null</code> if no context should be used
	 */
	public void setProcessingContext(Processor context) {
		if(this.processor != context) {
			this.processor = context;
			updateDebugInfo();
		}
	}

	/**
	 * Gets the node whose debug information this component is currently displaying.
	 * 
	 * @return the node
	 */
	public OpNode getNode() {
		return node;
	}

	/**
	 * Sets the node whose debug information this component should display.
	 * 
	 * @param node the node to set
	 */
	public void setNode(OpNode node) {
		if(this.node != node) {
			this.node = node;
			updateDebugInfo();
		}
	}

	/**
	 * Updates the debug info for the current node/processing context.
	 */
	public void updateDebugInfo() {
		String text = "<html><body><i>No debug info available</i></body></html>";
		if(processor != null) {
			final OpContext context = processor.getContext().findChildContext(node);
			if(context != null) {
				final StringBuilder sb = new StringBuilder();
				sb.append("<html><body>");
				
				// check for error
				final ProcessingException processException = processor.getError();
				if(processException != null) {
					final ByteArrayOutputStream out = new ByteArrayOutputStream();
					final PrintStream ps = new PrintStream(out);
					processException.printStackTrace(ps);
					sb.append("<h1 style='color:red;'>Error</h1>");
					try {
						sb.append("<p>Stack Trace:<pre>" + new String(out.toByteArray(), "UTF-8") + "</pre></p>");
					} catch (UnsupportedEncodingException e) {
					}
				}
				
				sb.append("Inputs:<ul>");
				for(InputField field : node.getInputFields()) {
					sb.append("<li><b>");
					sb.append(field.getKey());
					sb.append("</b>: ");

					final Object value = context.get(field);
					String valueTxt = (value != null ? value.toString() : "undefined");
					if(valueTxt.length() > 200)
						valueTxt = valueTxt.substring(0, 200) + '\u2026';
					sb.append(valueTxt);

					sb.append("</li>");
				}
				sb.append("</ul>Outputs:<ul>");
				for(OutputField field : node.getOutputFields()) {
					sb.append("<li><b>");
					sb.append(field.getKey());
					sb.append("</b>: ");

					final Object value = context.get(field);
					String valueTxt = (value != null ? value.toString() : "undefined");
					if(valueTxt.length() > 200)
						valueTxt = valueTxt.substring(0, 200) + '\u2026';
					sb.append(valueTxt);

					sb.append("</li>");
				}
				sb.append("</ul>");
				sb.append("</body></html>");

				text = sb.toString();
			}
		}

		setText(text);
	}
}
