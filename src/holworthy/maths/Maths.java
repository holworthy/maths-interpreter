package holworthy.maths;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Add;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Equation;
import holworthy.maths.nodes.Equations;
import holworthy.maths.nodes.Factorial;
import holworthy.maths.nodes.Ln;
import holworthy.maths.nodes.Log;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Nthrt;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.Sqrt;
import holworthy.maths.nodes.Subtract;
import holworthy.maths.nodes.Variable;
import holworthy.maths.nodes.constant.E;
import holworthy.maths.nodes.constant.I;
import holworthy.maths.nodes.constant.Pi;
import holworthy.maths.nodes.trig.Acos;
import holworthy.maths.nodes.trig.Acosh;
import holworthy.maths.nodes.trig.Acot;
import holworthy.maths.nodes.trig.Acoth;
import holworthy.maths.nodes.trig.Acsc;
import holworthy.maths.nodes.trig.Acsch;
import holworthy.maths.nodes.trig.Asec;
import holworthy.maths.nodes.trig.Asech;
import holworthy.maths.nodes.trig.Asin;
import holworthy.maths.nodes.trig.Asinh;
import holworthy.maths.nodes.trig.Atan;
import holworthy.maths.nodes.trig.Atanh;
import holworthy.maths.nodes.trig.Cos;
import holworthy.maths.nodes.trig.Cosh;
import holworthy.maths.nodes.trig.Cot;
import holworthy.maths.nodes.trig.Coth;
import holworthy.maths.nodes.trig.Csc;
import holworthy.maths.nodes.trig.Csch;
import holworthy.maths.nodes.trig.Sec;
import holworthy.maths.nodes.trig.Sech;
import holworthy.maths.nodes.trig.Sin;
import holworthy.maths.nodes.trig.Sinh;
import holworthy.maths.nodes.trig.Tan;
import holworthy.maths.nodes.trig.Tanh;

/*

<equation> ::= <expression> | <expression> "=" <expression>
<expression> ::= <add-or-subtract>
<add-or-subtract> ::= <add-or-subtract> "+" <multiply-or-divide> | <add-or-subtract> "-" <multiply-or-divide> | <multiply-or-divide>
<multiply-or-divide> ::= <multiply-or-divide> "*" <negative> | <multiply-or-divide> "/" <negative> | <negative>
<negative> ::= "-" <negative> | <power>
<power> ::= <factorial> "^" <negative> | <factorial>
<factorial> ::= <value> "!" | <value>
<value> ::= <number> | <brackets> | <variable> | <function>
<brackets> ::= "(" <expression> ")"

<function> ::= <name> "(" <parameter-list> ")"
<parameter-list> ::= <parameter-list> "," <equation> | <equation>

<variable> ::= <name>
<name> ::= <name> <letter> | <letter>
<letter> ::= "a" | "b" | "c" | "d" | "e" | "f" | "g" | "h" | "i" | "j" | "k" | "l" | "m" | "n" | "o" | "p" | "q" | "r" | "s" | "t" | "u" | "v" | "w" | "x" | "y" | "z"

<number> ::= <number> <digit> | <digit>
<digit> ::= "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"

*/

public abstract class Maths {
	private static ArrayList<String> CONSTANTS = new ArrayList<>(Arrays.asList(new String[]{"e", "i", "pi"}));
	private static ArrayList<String> FUNCTIONS = new ArrayList<>(Arrays.asList(new String[]{"nthrt", "sqrt", "log", "ln", "factorial", "acos", "acosh", "acot", "acoth", "acsc", "acsch", "asec", "asech", "asin", "asinh", "atan", "atanh", "cos", "cosh", "cot", "coth", "csc", "csch", "sec", "sech", "sin", "sinh", "tan", "tanh"}));

	private static Node parseValue(Parser parser) throws Exception {
		if(parser.hasMore() && parser.getChar() == '(') {
			parser.incrementCursor();
			Node expression = parseExpression(parser);
			if(!parser.hasMore() || parser.getChar() != ')') {
				throw new Exception("Need a closing bracket");
			}
			parser.incrementCursor();
			return expression;
		} else if(parser.hasMore() && parser.getChar() >= 'a' && parser.getChar() <= 'z') {
			int start = parser.getCursor();
			while(parser.hasMore() && parser.getChar() >= 'a' && parser.getChar() <= 'z')
				parser.incrementCursor();
			String name = parser.getInput().substring(start, parser.getCursor());
			if(CONSTANTS.contains(name)) {
				switch(name) {
					case "e":
						return new E();
					case "i":
						return new I();
					case "pi":
						return new Pi();
				}
			}

			if(FUNCTIONS.contains(name)) {
				if(!parser.hasMore() || parser.getChar() != '(') {
					throw new Error("Expected brackets after function name");
				}
				parser.incrementCursor();

				ArrayList<Node> params = new ArrayList<>();
				params.add(parseEquation(parser));
				while(parser.hasMore() && parser.getChar() == ',') {
					parser.incrementCursor();
					params.add(parseEquation(parser));
				}

				if(!parser.hasMore() || parser.getChar() != ')')
					throw new Error("Expected closing bracket");
				parser.incrementCursor();

				if(params.size() == 1) {
					switch(name) {
						case "sqrt":
							return new Sqrt(params.get(0));
						case "ln":
							return new Ln(params.get(0));
						case "factorial":
							return new Factorial(params.get(0));
						case "acos":
							return new Acos(params.get(0));
						case "acosh":
							return new Acosh(params.get(0));
						case "acot":
							return new Acot(params.get(0));
						case "acoth":
							return new Acoth(params.get(0));
						case "acsc":
							return new Acsc(params.get(0));
						case "acsch":
							return new Acsch(params.get(0));
						case "asec":
							return new Asec(params.get(0));
						case "asech":
							return new Asech(params.get(0));
						case "asin":
							return new Asin(params.get(0));
						case "asinh":
							return new Asinh(params.get(0));
						case "atan":
							return new Atan(params.get(0));
						case "atanh":
							return new Atanh(params.get(0));
						case "cos":
							return new Cos(params.get(0));
						case "cosh":
							return new Cosh(params.get(0));
						case "cot":
							return new Cot(params.get(0));
						case "coth":
							return new Coth(params.get(0));
						case "csc":
							return new Csc(params.get(0));
						case "csch":
							return new Csch(params.get(0));
						case "sec":
							return new Sec(params.get(0));
						case "sech":
							return new Sech(params.get(0));
						case "sin":
							return new Sin(params.get(0));
						case "sinh":
							return new Sinh(params.get(0));
						case "tan":
							return new Tan(params.get(0));
						case "tanh":
							return new Tanh(params.get(0));
					}
				} else if(params.size() == 2) {
					switch(name) {
						case "nthrt":
							return new Nthrt(params.get(0), params.get(1));
						case "log":
							return new Log(params.get(0), params.get(1));
					}
				} else {
					throw new Error("wrong number of parameters for " + name);
				}
			}

			Node variable = new Variable(name);
			return variable;
		} else if(parser.hasMore() && parser.getChar() >= '0' && parser.getChar() <= '9') {
			int start = parser.getCursor();
			boolean decimal = false;
			while(parser.hasMore() && (parser.getChar() >= '0' && parser.getChar() <= '9' || (parser.getChar() == '.' && !decimal))){
				if(parser.getChar() == '.')
					decimal = true;
				parser.incrementCursor();
			}

			String value = parser.getInput().substring(start, parser.getCursor());
			if(decimal)
				return decimalToFraction(Double.parseDouble(value));
			Node number = new Number(new BigInteger(value));
			return number;
		} else {
			throw new Exception("Syntax Error");
		}
	}

	private static Node decimalToFraction(double parseDouble) {
		if (parseDouble < 0)
			return new Negative(decimalToFraction(-parseDouble));
		double tolerance = 1.0E-10;
		double h1 = 1, h2 = 0, k1 = 0, k2 = 1, b = parseDouble;
		do {
			double a = Math.floor(b);
			double aux = h1;
			h1 = a*h1+h2;
			h2 = aux;
			aux = k1;
			k1 = a*k1+k2;
			k2 = aux;
			b = 1/(b-a);
		} while (Math.abs(parseDouble-h1/k1) > parseDouble*tolerance);

		return new Divide(new Number(BigDecimal.valueOf(h1).toBigInteger()), new Number(BigDecimal.valueOf(k1).toBigInteger()));
	}

	private static Node parseFactorialAsPoint(Parser parser) throws Exception{
		Node left = parseValue(parser);
		if(parser.hasMore() && parser.getChar() == '!'){
			parser.incrementCursor();
			left = new Factorial(left);
		}
		return left;
	}

	private static Node parsePower(Parser parser) throws Exception {
		Node left = parseFactorialAsPoint(parser);
		if(parser.hasMore() && parser.getChar() == '^') {
			parser.incrementCursor();
			Node right = parseNegative(parser);
			return new Power(left, right);
		}
		return left;
	}

	private static Node parseNegative(Parser parser) throws Exception {
		if(parser.hasMore() && parser.getChar() == '-') {
			parser.incrementCursor();
			Node node = parseNegative(parser);
			return new Negative(node);
		}
		return parsePower(parser);
	}

	private static Node parseMultiplyOrDivide(Parser parser) throws Exception {
		Node left = parseNegative(parser);
		while(true) {
			if(parser.hasMore() && parser.getChar() == '*') {
				parser.incrementCursor();
				Node right = parseNegative(parser);
				left = new Multiply(left, right);
			} else if(parser.hasMore() && parser.getChar() == '/') {
				parser.incrementCursor();
				Node right = parseNegative(parser);
				left = new Divide(left, right);
			} else {
				break;
			}
		}
		return left;
	}

	private static Node parseAddOrSubtract(Parser parser) throws Exception {
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

	private static Node parseExpression(Parser parser) throws Exception {
		return parseAddOrSubtract(parser);
	}
	
	private static Node parseEquation(Parser parser) throws Exception {
		Node left = parseExpression(parser);
		if(parser.hasMore() && parser.getChar() == '=') {
			parser.incrementCursor();
			Node right = parseExpression(parser);
			return new Equation(left, right);
		}
		return left;
	}

	private static Node parseEquations(Parser parser) throws Exception {
		Equations equations = new Equations();

		Node equation = parseEquation(parser);
		equations.addEquation(equation);
		while(parser.hasMore() && parser.getChar() == ',') {
			parser.incrementCursor();
			equation = parseEquation(parser);
			equations.addEquation(equation);
		}

		if(equations.getEquations().size() == 1)
			return equation;

		return equations;
	}

	public static Node parseInput(String input) throws Exception {
		Parser parser = new Parser(removeSpaces(input));
		Node equation = parseEquations(parser);
		if(parser.getInput().length() != parser.getCursor())
			throw new MathsInterpreterException("Invalid syntax");
		return equation;
	}

	public static String removeSpaces(String input) throws MathsInterpreterException {
		input = input.trim();
		for (int i = 0; i < input.length(); i++){
			if (input.charAt(i) == ' '){
				if (Character.isLetterOrDigit(input.charAt(i-1))){
					while(i + 1 < input.length()){
						i++;
						if(input.charAt(i) != ' ' && Character.isLetterOrDigit(input.charAt(i))){
							throw new MathsInterpreterException("Invalid use of whitespace");
						}
						else if (!(Character.isLetterOrDigit(input.charAt(i)))){
							break;
						}
					}
				}
				else if(!(Character.isLetterOrDigit(input.charAt(i-1)))){
					while(i + 1 < input.length()){
						i++;
						if(input.charAt(i) != ' ' && !(Character.isLetterOrDigit(input.charAt(i)))){
							throw new MathsInterpreterException("Invalid use of whitespace");
						}
						else if (Character.isLetterOrDigit(input.charAt(i))){
							break;
						}
					}
				}
			}
		}
		return input.replace(" ", "");
	}

	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);

		Node input = parseInput(scanner.nextLine());
		System.out.println(input);
		Node expanded = input.expand();
		System.out.println(expanded);
		Node simplified = expanded.collapse();
		System.out.println(simplified);

		scanner.close();
	}
}
