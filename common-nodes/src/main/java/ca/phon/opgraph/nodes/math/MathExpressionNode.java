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
package ca.phon.opgraph.nodes.math;

import java.awt.*;
import java.beans.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.event.*;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import ca.phon.opgraph.nodes.math.parser.*;
import ca.phon.opgraph.*;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.edits.node.NodeSettingsEdit;
import ca.phon.opgraph.app.extensions.NodeSettings;
import ca.phon.opgraph.exceptions.ProcessingException;

/**
 * A node that computes a value from a mathematical expression.
 */
@OpNodeInfo(
	name="Math Expression",
	description="Computes the value of a mathematical expression.",
	category="Math"
)
public class MathExpressionNode
	extends OpNode
	implements NodeSettings
{
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(MathExpressionNode.class.getName());

	/** Output field for the expression result */
	public final OutputField RESULT_OUTPUT_FIELD = new OutputField("result", "expression result", true, Number.class); 

	/** The math expression */
	private String expression;

	/** The expression parser that parsed the expression when it was set */
	private MathExpressionParser expressionParser;

	/** The parsed expression tree */
	private Object expressionTree;

	/** The number of decimal places that are significant in the expression result */
	private int significantDigits;

	/** The default number of decimal places that are significant the expression result */
	private static final int DEFAULT_SIGNIFICANT_DIGITS = -1;

	/**
	 * Constructs a math expression node with no expression.
	 */
	public MathExpressionNode() {
		this(null);
	}

	/**
	 * Constructs a math expression node with a given expression.
	 * 
	 * @param expression  the math expression
	 */
	public MathExpressionNode(String expression) {
		setExpression(expression);
		setSignificantDigits(DEFAULT_SIGNIFICANT_DIGITS);

		putField(RESULT_OUTPUT_FIELD);
		putExtension(NodeSettings.class, this);
	}

	/**
	 * Gets the math expression to evaluate.
	 * 
	 * @return the math expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Sets the math expression to evaluate.
	 * 
	 * @param expression  the math expression
	 */
	public void setExpression(String expression) {
		this.expression = (expression == null ? "" : expression);

		final ANTLRStringStream stream = new ANTLRStringStream(this.expression);
		final MathExpressionLexer lexer = new MathExpressionLexer(stream);
		final CommonTokenStream tokens = new CommonTokenStream(lexer);

		expressionParser = new MathExpressionParser(tokens);

		try {
			expressionTree = expressionParser.prog().getTree();
			if(expressionTree != null)
				LOGGER.info(((org.antlr.runtime.tree.CommonTree)expressionTree).toStringTree());

			// Remove any input fields that correspond to non-existant variables
			final ArrayList<InputField> inputFieldsCopy = new ArrayList<InputField>(getInputFields());
			for(InputField field : inputFieldsCopy) {
				if(!expressionParser.getVariables().contains(field.getKey()))
					removeField(field);
			}

			// Insert new input fields
			for(String variable : expressionParser.getVariables()) {
				if(getInputFieldWithKey(variable) == null)
					putField(new InputField(variable, "expression variable", false, true, Number.class));
			}
		} catch(RecognitionException exc) {
			expressionParser = null;
		}
	}

	/**
	 * Gets the number of decimal places that are significant in the expression
	 * result. If negative, all decimal places are significant. If zero, the
	 * result will always be an integer.
	 * 
	 * @return the number of significant digits
	 */
	public int getSignificantDigits() {
		return significantDigits;
	}

	/**
	 * Sets the number of decimal places that are significant in the expression
	 * result. If negative, all decimal places are significant. If set to zero,
	 * the result will always be an integer.
	 * 
	 * @param significantDigits  the number of significant digits.
	 */
	public void setSignificantDigits(int significantDigits) {
		this.significantDigits = significantDigits;
	}

	/**
	 * Rounds a double to a specified number of significant digits past the
	 * decimal place. Given a value <code>x</code>, the computed value
	 * <code>x'</code> will satisfy:
	 * <blockquote>
	 *   <code>Math.abs(x - x') < Math.pow(1, -significantDigits)</code>
	 * </blockquote>
	 * If the number of significant digits is negative then all decimal places
	 * are significant, and the result is returned as-is.
	 * 
	 * @param val  the value
	 * @param significantDigits  the number of significant digits
	 * 
	 * @return The value rounded to the given number of significant digits.
	 *         If the rounded value is an integer, an integral value is
	 *         returned, otherwise a decimal value is returned. 
	 */
	private static Number roundToSignificantDigits(double val, int significantDigits) {
		// If negative significant digits, return value as-is
		if(significantDigits < 0)
			return val;

		// If zero significant digits, just return the value rounded
		if(significantDigits == 0)
			return Math.round(val);

		// Take advantage of the rounding facilities of BigDecimal
		final BigDecimal bigValue = new BigDecimal(val);
		final BigDecimal scaledBigValue = bigValue.setScale(significantDigits, BigDecimal.ROUND_HALF_UP);

		// Try to get an integer out of it
		Number retVal = scaledBigValue;
		try {
			retVal = scaledBigValue.toBigIntegerExact();
		} catch(ArithmeticException exc) { }

		return retVal;
	}

	//
	// OpNode
	//

	@Override
	public void operate(OpContext context) throws ProcessingException {
		if(expressionParser == null || expressionTree == null)
			throw new NullPointerException("Math expression could not be parsed");

		//
		final CommonTreeNodeStream stream = new CommonTreeNodeStream(expressionTree);
		final MathExpressionEval expressionEval = new MathExpressionEval(stream);

		// Add variable bindings
		for(String variable : expressionParser.getVariables())
			expressionEval.putValue(variable, (Number)context.get(variable));

		// Evaluate, and round to the number of significant decimal places
		try {
			expressionEval.prog();

			final Number result = roundToSignificantDigits(expressionEval.getResult(), significantDigits);
			context.put(RESULT_OUTPUT_FIELD, result);
		} catch(RecognitionException exc) {
			throw new ProcessingException(null, "Could not evaluate math expression", exc);
		}
	}

	//
	// NodeSettings
	//

	private static final String EXPRESSION_KEY = "expression";

	private static final String SIGNIFICANT_DIGITS_KEY = "significantDigits";

	/**
	 * A formatter that checks whether or not a given math expression is valid. 
	 */
	public static class MathExpressionFormatter extends AbstractFormatter {
		@Override
		public Object stringToValue(String text) throws ParseException {
			final ANTLRStringStream stream = new ANTLRStringStream(text);
			final MathExpressionLexer lexer = new MathExpressionLexer(stream);
			final CommonTokenStream tokens = new CommonTokenStream(lexer);
			final MathExpressionParser expressionParser = new MathExpressionParser(tokens);

			try {
				expressionParser.prog();
			} catch(RecognitionException exc) {
				setEditValid(false);
				invalidEdit();
				throw new ParseException(expressionParser.getErrorHeader(exc), 0);
			}

			if(expressionParser.getNumberOfSyntaxErrors() == 0) {
				setEditValid(true);
			} else {
				setEditValid(false);
				throw new ParseException("Could not parser expression: " + text, 0);
			}

			return text;
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			return (value == null ? "" : value.toString());
		}
	}

	/**
	 * Constructs a math expression settings for the given node.
	 */
	public static class MathExpressionSettings extends JPanel {
		
		private GraphDocument document;
		
		/**
		 * Constructs this component for a given math expression node .
		 * 
		 * @param node  the math expression node
		 */
		public MathExpressionSettings(final MathExpressionNode node, final GraphDocument document) {
			super(new GridBagLayout());
			
			this.document = document;

			// A text field for the mathematical expression
			final JLabel expressionLabel = new JLabel("Expression: ");
			expressionLabel.setToolTipText("The mathematical expression (e.g., x+y)");

			final JFormattedTextField expressionText = new JFormattedTextField(new MathExpressionFormatter());
			expressionText.setValue(node.getExpression());
			expressionText.addPropertyChangeListener("value", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					if(document != null) {
						final Properties settings = new Properties();
						settings.put(EXPRESSION_KEY, e.getNewValue().toString());
						document.getUndoSupport().postEdit(new NodeSettingsEdit(node, settings));
					}
				}
			});

			// An integer spinner for the number of significant digits in the result
			final JLabel significantDigitsLabel = new JLabel("Significant digits: ");
			significantDigitsLabel.setToolTipText("The number of significant decimal places to maintain in the result. If zero, the result will always be an integer. If negative, all decimal places are significant.");

			final SpinnerModel spinnerModel = new SpinnerNumberModel(node.getSignificantDigits(), -1, 100, 1);
			final JSpinner significantDigitsSpinner = new JSpinner(spinnerModel);
			significantDigitsSpinner.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if(document != null) {
						final Properties settings = new Properties();
						settings.put(EXPRESSION_KEY, spinnerModel.getValue());
						document.getUndoSupport().postEdit(new NodeSettingsEdit(node, settings));
					}
				}
			});

			// Add components
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.EAST;
			add(expressionLabel, gbc);

			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			add(expressionText, gbc);

			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.EAST;
			add(significantDigitsLabel, gbc);

			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.weightx = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			add(significantDigitsSpinner, gbc);
		}
	}

	//
	// NodeSettings
	//

	@Override
	public Component getComponent(GraphDocument document) {
		return new MathExpressionSettings(this, document);
	}

	@Override
	public Properties getSettings() {
		final Properties props = new Properties();
		props.setProperty(EXPRESSION_KEY, getExpression());
		props.setProperty(SIGNIFICANT_DIGITS_KEY, "" + getSignificantDigits());
		return props;
	}

	@Override
	public void loadSettings(Properties properties) {
		if(properties.containsKey(EXPRESSION_KEY))
			setExpression(properties.getProperty(EXPRESSION_KEY));

		if(properties.containsKey(SIGNIFICANT_DIGITS_KEY))
			setSignificantDigits(Integer.parseInt(properties.getProperty(SIGNIFICANT_DIGITS_KEY)));
	}
}
