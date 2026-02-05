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
package ca.phon.opgraph.nodes.math.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Visitor that evaluates a parsed math expression tree.
 */
public class MathExpressionEvalVisitor extends MathExpressionBaseVisitor<Double> {

	private final Map<String, Number> values = new HashMap<>();

	public void putValue(String name, Number value) {
		values.put(name, value);
	}

	@Override
	public Double visitProg(MathExpressionParser.ProgContext ctx) {
		if (ctx.expr() != null) {
			return visit(ctx.expr());
		}
		return 0.0;
	}

	@Override
	public Double visitNegate(MathExpressionParser.NegateContext ctx) {
		return -visit(ctx.expr());
	}

	@Override
	public Double visitMulDivMod(MathExpressionParser.MulDivModContext ctx) {
		double left = visit(ctx.expr(0));
		double right = visit(ctx.expr(1));
		return switch (ctx.op.getText()) {
			case "*" -> left * right;
			case "/" -> left / right;
			case "%" -> left % right;
			default -> throw new RuntimeException("Unknown operator: " + ctx.op.getText());
		};
	}

	@Override
	public Double visitAddSub(MathExpressionParser.AddSubContext ctx) {
		double left = visit(ctx.expr(0));
		double right = visit(ctx.expr(1));
		return switch (ctx.op.getText()) {
			case "+" -> left + right;
			case "-" -> left - right;
			default -> throw new RuntimeException("Unknown operator: " + ctx.op.getText());
		};
	}

	@Override
	public Double visitParens(MathExpressionParser.ParensContext ctx) {
		return visit(ctx.expr());
	}

	@Override
	public Double visitReal(MathExpressionParser.RealContext ctx) {
		return Double.parseDouble(ctx.REAL().getText());
	}

	@Override
	public Double visitInt(MathExpressionParser.IntContext ctx) {
		return (double) Integer.parseInt(ctx.INT().getText());
	}

	@Override
	public Double visitVariable(MathExpressionParser.VariableContext ctx) {
		String name = ctx.ID().getText();
		Number v = values.get(name);
		if (v == null)
			throw new NullPointerException("Undefined variable in math expression: " + name);
		return v.doubleValue();
	}
}
