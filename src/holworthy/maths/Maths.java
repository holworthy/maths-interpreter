package holworthy.maths;

import java.util.Scanner;

import holworthy.maths.nodes.Add;
import holworthy.maths.nodes.BinaryNode;
import holworthy.maths.nodes.Brackets;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Equation;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.Sqrt;
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

	private Node parseNegative(Parser parser) throws Exception {
		if(parser.hasMore() && parser.getChar() == '-') {
			parser.incrementCursor();
			Node node = parseNegative(parser);
			return new Negative(node);
		}
		return parseValue(parser);
	}

	private Node parsePower(Parser parser) throws Exception {
		Node left = parseNegative(parser);
		while(parser.hasMore() && parser.getChar() == '^') {
			parser.incrementCursor();
			Node right = parseNegative(parser);
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
		Scanner scanner = new Scanner(System.in);
		Node input = parseInput(scanner.nextLine());
		scanner.close();

		System.out.println(input);
		Node simplified = input.simplify();
		System.out.println(simplified);
		
		if(simplified instanceof Equation) {
			Equation before = (Equation) simplified;
			if(before.isQuadratic()) {
				Equation after = new Equation(
					new Variable("x"),
					new Divide(
						new Add(
							new Negative(
								((BinaryNode) ((BinaryNode) ((BinaryNode) before.getLeft()).getLeft()).getRight()).getLeft()
							),
							new Sqrt(
								new Subtract(
									new Power(
										((BinaryNode) ((BinaryNode) ((BinaryNode) before.getLeft()).getLeft()).getRight()).getLeft(),
										new Number(2)
									),
									new Multiply(
										new Multiply(
											new Number(4),
											((BinaryNode) ((BinaryNode) ((BinaryNode) before.getLeft()).getLeft()).getLeft()).getLeft()
										),
										((BinaryNode) before.getLeft()).getRight()
									)
								)
							)
						),
						new Multiply(
							new Number(2),
							((BinaryNode) ((BinaryNode) ((BinaryNode) before.getLeft()).getLeft()).getLeft()).getLeft()
						)
					)
				);

				System.out.println(after);
				System.out.println(after.simplify());

				after = new Equation(
					new Variable("x"),
					new Divide(
						new Subtract(
							new Negative(
								((BinaryNode) ((BinaryNode) ((BinaryNode) before.getLeft()).getLeft()).getRight()).getLeft()
							),
							new Sqrt(
								new Subtract(
									new Power(
										((BinaryNode) ((BinaryNode) ((BinaryNode) before.getLeft()).getLeft()).getRight()).getLeft(),
										new Number(2)
									),
									new Multiply(
										new Multiply(
											new Number(4),
											((BinaryNode) ((BinaryNode) ((BinaryNode) before.getLeft()).getLeft()).getLeft()).getLeft()
										),
										((BinaryNode) before.getLeft()).getRight()
									)
								)
							)
						),
						new Multiply(
							new Number(2),
							((BinaryNode) ((BinaryNode) ((BinaryNode) before.getLeft()).getLeft()).getLeft()).getLeft()
						)
					)
				);

				System.out.println(after);
				System.out.println(after.simplify());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new Maths();
	}
}