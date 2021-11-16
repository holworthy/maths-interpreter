package holworthy.maths.nodes;

import java.util.ArrayList;
import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
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

public class Equation extends BinaryNode {
	public Equation(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return getLeft() + " = " + getRight();
	}

	@Override
	public Node copy() {
		return new Equation(getLeft().copy(), getRight().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		return new Equation(getLeft().expand(), getRight().expand());
	}

	@Override
	public Node collapse() throws MathsInterpreterException {
		return new Equation(getLeft().collapse(), getRight().collapse());
	}

	private ArrayList<Variable> getVariables(Node node) {
		ArrayList<Variable> variables = new ArrayList<>();

		if(node instanceof Variable) {
			if(!variables.contains((Variable) node))
				variables.add((Variable) node);
		} else if(node instanceof UnaryNode) {
			for(Variable variable : getVariables(((UnaryNode) node).getNode()))
				if(!variables.contains(variable))
					variables.add(variable);
		} else if(node instanceof BinaryNode) {
			for(Variable variable : getVariables(((BinaryNode) node).getLeft()))
				if(!variables.contains(variable))
					variables.add(variable);

			for(Variable variable : getVariables(((BinaryNode) node).getRight()))
				if(!variables.contains(variable))
						variables.add(variable);
		}

		return variables;
	}

	public int numVariables() {
		return getVariables(this).size();
	}

	private Equation quadraticSolution1(Node x, Node a, Node b, Node c, Node d) {
		// x = (-b + sqrt(b^2 - 4 * a * (c - d))) / (2 * a)
		return new Equation(x, new Divide(new Add(new Negative(b), new Sqrt(new Subtract(new Power(b, new Number(2)), new Multiply(new Multiply(new Number(4), a), new Subtract(c, d))))), new Multiply(new Number(2), a)));
	}

	private Equation quadraticSolution2(Node x, Node a, Node b, Node c, Node d) {
		// x = (-b - sqrt(b^2 - 4 * a * (c - d))) / (2 * a)
		return new Equation(x, new Divide(new Subtract(new Negative(b), new Sqrt(new Subtract(new Power(b, new Number(2)), new Multiply(new Multiply(new Number(4), a), new Subtract(c, d))))), new Multiply(new Number(2), a)));
	}

	public ArrayList<Equation> solve() throws MathsInterpreterException {
		// TODO: we need to output when we cannot solve an equation

		Node start = new Equation(new Subtract(getLeft(), getRight()), new Number(0)).simplify();
		ArrayList<Variable> variables = getVariables(start);
		variables.sort((a, b) -> a.getName().compareTo(b.getName()));

		ArrayList<Equation> solutions = new ArrayList<>();
		for(Variable variable : variables) {
			Equation equation = (Equation) start.copy();
			
			while(!equation.getLeft().matches(variable)) {
				equation = (Equation) equation.simplify();
				Equation expandedEquation = (Equation) equation.expand(); // helps reduce the number of rules for quadratics

				// (3*z/x)*(14/(z*x))=(1/3)*y
				// 37*x^2+42*c-42*c+(1/37)*x=0 pretty sure this is cos quadratics

				// quadratics
				// negatives too

				// a*x^2 + b*x + c = d
				// a*x^2 + b*x = d
				if(expandedEquation.getLeft().matches(new Add(new Add(new Multiply(new Matching.Constant(), new Power(new Matching.Anything(), new Number(2))), new Multiply(new Matching.Constant(), new Matching.Anything())), new Matching.Constant())) && expandedEquation.getRight().matches(new Matching.Constant()) && ((BinaryNode) ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft()).getRight()).getLeft().matches(((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight()).getRight())) {
					Node a = ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft()).getLeft();
					Node b = ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight()).getLeft();
					Node c = ((BinaryNode) expandedEquation.getLeft()).getRight();
					Node d = expandedEquation.getRight();
					Node x = ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight()).getRight();
					solutions.addAll(quadraticSolution1(x, a, b, c, d).solve());
					solutions.addAll(quadraticSolution2(x, a, b, c, d).solve());
					break;
				// a*x^2 + b*x = 0
				} else if(expandedEquation.getLeft().matches(new Add(new Multiply(new Matching.Constant(), new Power(new Matching.Anything(), new Number(2))), new Multiply(new Matching.Constant(), new Matching.Anything()))) && expandedEquation.getRight().matches(new Matching.Constant()) && ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight()).getLeft().matches(((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getRight()).getRight())) {
					Node a = ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft();
					Node b = ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getRight()).getLeft();
					Node c = new Number(0);
					Node d = expandedEquation.getRight();
					Node x = ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getRight()).getRight();
					solutions.addAll(quadraticSolution1(x, a, b, c, d).solve());
					solutions.addAll(quadraticSolution2(x, a, b, c, d).solve());
					break;
				// a*x^2 + x + c = d
				// a*x^2 + x = d
				} else if(expandedEquation.getLeft().matches(new Add(new Add(new Multiply(new Matching.Constant(), new Power(new Matching.Anything(), new Number(2))), new Matching.Anything()), new Matching.Anything())) && expandedEquation.getRight().matches(new Matching.Constant()) && ((BinaryNode) ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft()).getRight()).getLeft().matches(((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight())) {
					Node a = ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft()).getLeft();
					Node b = new Number(1);
					Node c = ((BinaryNode) expandedEquation.getLeft()).getRight();
					Node d = expandedEquation.getRight();
					Node x = ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight();
					solutions.addAll(quadraticSolution1(x, a, b, c, d).solve());
					solutions.addAll(quadraticSolution2(x, a, b, c, d).solve());
					break;
				// a*x^2 + x = 0
				} else if(expandedEquation.getLeft().matches(new Add(new Multiply(new Matching.Constant(), new Power(new Matching.Anything(), new Number(2))), new Matching.Anything())) && expandedEquation.getRight().matches(new Matching.Constant()) && ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight()).getLeft().matches(((BinaryNode) expandedEquation.getLeft()).getRight())) {
					Node a = ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft();
					Node b = new Number(1);
					Node c = new Number(0);
					Node d = expandedEquation.getRight();
					Node x = ((BinaryNode) expandedEquation.getLeft()).getRight();
					solutions.addAll(quadraticSolution1(x, a, b, c, d).solve());
					solutions.addAll(quadraticSolution2(x, a, b, c, d).solve());
					break;
				// x^2 + b*x + c = d
				// x^2 + b*x = d
				} else if(expandedEquation.getLeft().matches(new Add(new Add(new Power(new Matching.Anything(), new Number(2)), new Multiply(new Matching.Constant(), new Matching.Anything())), new Matching.Constant())) && expandedEquation.getRight().matches(new Matching.Constant()) && ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft()).getLeft().matches(((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight()).getRight())) {
					Node a = new Number(1);
					Node b = ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight()).getLeft();
					Node c = ((BinaryNode) expandedEquation.getLeft()).getRight();
					Node d = expandedEquation.getRight();
					Node x = ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight()).getRight();
					solutions.addAll(quadraticSolution1(x, a, b, c, d).solve());
					solutions.addAll(quadraticSolution2(x, a, b, c, d).solve());
					break;
				// x^2 + b*x = 0
				} else if(expandedEquation.getLeft().matches(new Add(new Power(new Matching.Anything(), new Number(2)), new Multiply(new Matching.Constant(), new Matching.Anything()))) && expandedEquation.getRight().matches(new Matching.Constant()) && ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft().matches(((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getRight()).getRight())) {
					Node a = new Number(1);
					Node b = ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getRight()).getLeft();
					Node c = new Number(0);
					Node d = new Number(0);
					Node x = ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft();
					solutions.addAll(quadraticSolution1(x, a, b, c, d).solve());
					solutions.addAll(quadraticSolution2(x, a, b, c, d).solve());
					break;
				// x^2 + x + c = d
				// x^2 + x = d
				} else if(expandedEquation.getLeft().matches(new Add(new Add(new Power(new Matching.Anything(), new Number(2)), new Matching.Anything()), new Matching.Constant())) && expandedEquation.getRight().matches(new Matching.Constant()) && ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft()).getLeft().matches(((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight())) {
					Node a = new Number(1);
					Node b = new Number(1);
					Node c = ((BinaryNode) expandedEquation.getLeft()).getRight();
					Node d = expandedEquation.getRight();
					Node x = ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft()).getLeft();
					solutions.addAll(quadraticSolution1(x, a, b, c, d).solve());
					solutions.addAll(quadraticSolution2(x, a, b, c, d).solve());
					break;
				// x^2 + x = 0
				} else if(expandedEquation.getLeft().matches(new Add(new Power(new Matching.Anything(), new Number(2)), new Matching.Anything())) && expandedEquation.getRight().matches(new Matching.Constant()) && ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft().matches(((BinaryNode) expandedEquation.getLeft()).getRight())) {
					Node a = new Number(1);
					Node b = new Number(1);
					Node c = new Number(0);
					Node d = expandedEquation.getRight();
					Node x = ((BinaryNode) expandedEquation.getLeft()).getRight();
					solutions.addAll(quadraticSolution1(x, a, b, c, d).solve());
					solutions.addAll(quadraticSolution2(x, a, b, c, d).solve());
					break;
				// -x = a -> x = -a
				} else if(equation.getLeft() instanceof Negative) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Negative(equation.getRight()));

				// x + a = b -> x = b - a
				// a + x = b -> x = b - a
				} else if(equation.getLeft() instanceof Add) {
					if(((BinaryNode) equation.getLeft()).getLeft().contains(variable))
						equation = new Equation(((BinaryNode) equation.getLeft()).getLeft(), new Subtract(equation.getRight(), ((BinaryNode) equation.getLeft()).getRight()));
					else if(((BinaryNode) equation.getLeft()).getRight().contains(variable))
						equation = new Equation(((BinaryNode) equation.getLeft()).getRight(), new Subtract(equation.getRight(), ((BinaryNode) equation.getLeft()).getLeft()));

				// x - a = b -> x = b + a
				// a - x = b -> -x = b - a
				} else if(equation.getLeft() instanceof Subtract) {
					if(((BinaryNode) equation.getLeft()).getLeft().contains(variable)) {
						equation = new Equation(((BinaryNode) equation.getLeft()).getLeft(), new Add(equation.getRight(), ((BinaryNode) equation.getLeft()).getRight()));
					} else if(((BinaryNode) equation.getLeft()).getRight().contains(variable)) {
						equation = new Equation(new Negative(((BinaryNode) equation.getLeft()).getRight()), new Subtract(equation.getRight(), ((BinaryNode) equation.getLeft()).getLeft()));
					}
				
				// x * a = b -> x = b / a
				// TODO: check this okay
				} else if(equation.getLeft() instanceof Multiply) {
					if(((BinaryNode) equation.getLeft()).getLeft().contains(variable))
						equation = new Equation(((BinaryNode) equation.getLeft()).getLeft(), new Divide(equation.getRight(), ((BinaryNode) equation.getLeft()).getRight()));
					else if(((BinaryNode) equation.getLeft()).getRight().contains(variable))
						equation = new Equation(((BinaryNode) equation.getLeft()).getRight(), new Divide(equation.getRight(), ((BinaryNode) equation.getLeft()).getLeft()));

				// x / a = b -> x = a * b
				} else if(equation.getLeft() instanceof Divide) {
					// a / x = b -> x = a / b
					if(!(((BinaryNode) equation.getLeft()).getLeft().contains(variable)) && ((BinaryNode) equation.getLeft()).getRight().contains(variable))
						equation = new Equation(((BinaryNode) equation.getLeft()).getRight(), new Divide(((BinaryNode) equation.getLeft()).getLeft(), equation.getRight()));
					else if(((BinaryNode) equation.getLeft()).getLeft().contains(variable) && ((BinaryNode) equation.getLeft()).getRight().contains(variable))
						equation = new Equation(new Subtract(((BinaryNode) equation.getLeft()).getLeft(), new Multiply(equation.getRight(), ((BinaryNode) equation.getLeft()).getRight())), new Number(0));
					else
						equation = new Equation(((BinaryNode) equation.getLeft()).getLeft(), new Multiply(((BinaryNode) equation.getLeft()).getRight(), equation.getRight()));

				} else if(equation.getLeft() instanceof Power) {
					if(((BinaryNode) equation.getLeft()).getLeft().contains(variable)) {
						equation = new Equation(((BinaryNode) equation.getLeft()).getLeft(), new Nthrt(equation.getRight(), ((BinaryNode) equation.getLeft()).getRight()));
					}
					// TODO: logs

				// sin(x) = a -> x = asin(a)
				} else if(equation.getLeft() instanceof Sin) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Asin(equation.getRight()));
				// cos(x) = a -> x = acos(a)
				} else if(equation.getLeft() instanceof Cos) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Acos(equation.getRight()));
				// tan(x) = a -> x = atan(a)
				} else if(equation.getLeft() instanceof Tan) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Atan(equation.getRight()));
				// sinh(x) = a -> x = asinh(a)
				} else if(equation.getLeft() instanceof Sinh) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Asinh(equation.getRight()));
				// cosh(x) = a -> x = acosh(a)
				} else if(equation.getLeft() instanceof Cosh) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Acosh(equation.getRight()));
				// tanh(x) = a -> x = atanh(a)
				} else if(equation.getLeft() instanceof Tanh) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Atanh(equation.getRight()));
				// sec(x) = a -> x = asec(a)
				} else if(equation.getLeft() instanceof Sec) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Asec(equation.getRight()));
				// csc(x) = a -> x = acsc(a)
				} else if(equation.getLeft() instanceof Csc) {
					equation =  new Equation(((UnaryNode) equation.getLeft()).getNode(), new Acsc(equation.getRight()));
				// cot(x) = a -> x = acot(a)
				} else if(equation.getLeft() instanceof Cot) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Acot(equation.getRight()));
				// sech(x) = a -> x = asech(a)
				} else if(equation.getLeft() instanceof Sech) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Asech(equation.getRight()));
				// csch(x) = a -> x = acsch(a)
				} else if(equation.getLeft() instanceof Csch) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Acsch(equation.getRight()));
				// coth(x) = a -> x = acoth(a)
				} else if(equation.getLeft() instanceof Coth) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Acoth(equation.getRight()));
				// asin(x) = a -> x = sin(x)
				} else if(equation.getLeft() instanceof Asin) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Sin(equation.getRight()));
				// acos(x) = a -> x = cos(a)
				} else if(equation.getLeft() instanceof Acos) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Cos(equation.getRight()));
				// atan(x) = a -> x = tan(a)
				} else if(equation.getLeft() instanceof Atan) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Tan(equation.getRight()));
				// asinh(x) = a -> x = sinh(a)
				} else if(equation.getLeft() instanceof Asinh) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Sinh(equation.getRight()));
				// acosh(x) = a -> x = cosh(a)
				} else if(equation.getLeft() instanceof Acosh) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Cosh(equation.getRight()));
				// atanh(x) = a -> x = tanh(a)
				} else if(equation.getLeft() instanceof Atanh) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Tanh(equation.getRight()));
				// asec(x) = a -> x = sec(a)
				} else if(equation.getLeft() instanceof Asec) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Sec(equation.getRight()));
				// acsc(x) = a -> x = csc(a)
				} else if(equation.getLeft() instanceof Acsc) {
					equation =  new Equation(((UnaryNode) equation.getLeft()).getNode(), new Csc(equation.getRight()));
				// acot(x) = a -> x = cot(a)
				} else if(equation.getLeft() instanceof Acot) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Cot(equation.getRight()));
				// asech(x) = a -> x = sech(a)
				} else if(equation.getLeft() instanceof Asech) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Sech(equation.getRight()));
				// acsch(x) = a -> x = csch(a)
				} else if(equation.getLeft() instanceof Acsch) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Csch(equation.getRight()));
				// acoth(x) = a -> x = coth(a)
				} else if(equation.getLeft() instanceof Acoth) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Coth(equation.getRight()));
				} else {
					System.out.println("cannot handle " + equation);
					break;
				}
			}

			if(equation.getLeft().matches(variable))
				solutions.add((Equation) equation.simplify());
		}

		return solutions;
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		throw new MathsInterpreterException("Can't differentiate a equation");
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return getRight().evaluate(values);
	}
}