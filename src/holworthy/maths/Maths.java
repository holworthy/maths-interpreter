package holworthy.maths;

import holworthy.maths.nodes.Add;
import holworthy.maths.nodes.Brackets;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Equation;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.Subtract;
import holworthy.maths.nodes.Variable;

public class Maths {
	private Node parseValue(Parser parser) throws Exception {
		if(parser.getChar() == '(') {
			parser.incrementCursor();
			Node expression = parseExpression(parser);
			Node brackets = new Brackets(expression);
			if(parser.getChar() != ')') {
				throw new Exception("Need a closing bracket");
			}
			parser.incrementCursor();
			return brackets;
		} else if(parser.getChar() >= 'a' && parser.getChar() <= 'z') {
			int start = parser.getCursor();
			while(parser.hasMore() && parser.getChar() >= 'a' && parser.getChar() <= 'z')
				parser.incrementCursor();
			String name = parser.getInput().substring(start, parser.getCursor());
			Node variable = new Variable(name);
			return variable;
		} else if(parser.getChar() >= '0' && parser.getChar() <= '9') {
			int start = parser.getCursor();
			while(parser.hasMore() && parser.getChar() >= '0' && parser.getChar() <= '9')
				parser.incrementCursor();
			String value = parser.getInput().substring(start, parser.getCursor());
			Node number = new Number(Integer.parseInt(value));
			return number;
		} else {
			throw new Exception("Syntax Error");
		}
	}

	private Node parsePower(Parser parser) throws Exception {
		Node left = parseValue(parser);
		while(parser.hasMore() && parser.getChar() == '^') {
			parser.incrementCursor();
			Node right = parseValue(parser);
			left = new Power(left, right);
		}
		return left;
	}

	private Node parseMultiplyOrDivide(Parser parser) throws Exception {
		Node left = parsePower(parser);
		while(true) {
			if(parser.hasMore() && parser.getChar() == '*') {
				parser.incrementCursor();
				Node right = parsePower(parser);
				left = new Multiply(left, right);
			} else if(parser.hasMore() && parser.getChar() == '/') {
				parser.incrementCursor();
				Node right = parsePower(parser);
				left = new Divide(left, right);
			} else {
				break;
			}
		}
		return left;
	}

	private Node parseAddOrSubtract(Parser parser) throws Exception {
		Node left = parseMultiplyOrDivide(parser);
		while(true) {
			if(parser.hasMore() && parser.getChar() == '+') {
				parser.incrementCursor();
				Node right = parseMultiplyOrDivide(parser);
				left = new Add(left, right);
			} else if(parser.hasMore() && parser.getChar() == '-') {
				parser.incrementCursor();
				Node right = parseMultiplyOrDivide(parser);
				left = new Subtract(left, right);
			} else {
				break;
			}
		}
		return left;
	}

	private Node parseExpression(Parser parser) throws Exception {
		return parseAddOrSubtract(parser);
	}
	
	private Node parseEquation(Parser parser) throws Exception {
		Node left = parseExpression(parser);
		if(parser.hasMore() && parser.getChar() == '=') {
			parser.incrementCursor();
			Node right = parseExpression(parser);
			return new Equation(left, right);
		}
		return left;
	}

	private Node parseInput(String input) throws Exception {
		Parser parser = new Parser(input);
		Node equation = parseEquation(parser);
		assert parser.getInput().length() == parser.getCursor();
		return equation;
	}

	public Maths() throws Exception {
		Node input = parseInput("2+3");
		System.out.println(input);
		System.out.println(input.simplify());
	}

	public static void main(String[] args) throws Exception {
		new Maths();
	}
}
