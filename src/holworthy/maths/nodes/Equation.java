package holworthy.maths.nodes;

import java.util.ArrayList;
import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.trig.Acos;
import holworthy.maths.nodes.trig.Asin;
import holworthy.maths.nodes.trig.Cos;
import holworthy.maths.nodes.trig.Sin;

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
		Node start = new Equation(new Subtract(getLeft(), getRight()).simplify(), new Number(0));
		ArrayList<Variable> variables = getVariables(start);
		variables.sort((a, b) -> a.getName().compareTo(b.getName()));

		ArrayList<Equation> solutions = new ArrayList<>();
		for(Variable variable : variables) {
			Equation equation = (Equation) start.copy();
			
			while(!equation.getLeft().matches(variable)) {
				equation = (Equation) equation.simplify();
				Equation expandedEquation = (Equation) equation.expand(); // helps reduce the number of rules for quadratics

				// quadratics
				// a*x^2 + b*x + c = d
				// a*x^2 + b*x = d
				// a*x^2 + x + c = d
				// a*x^2 + x = d
				// x^2 + b*x + c = d
				// x^2 + b*x = d
				// x^2 + x + c = d
				// x^2 + x = d
				if(expandedEquation.getLeft().matches(new Add(new Add(new Multiply(new Matching.Constant(), new Power(new Matching.Anything(), new Number(2))), new Multiply(new Matching.Constant(), new Matching.Anything())), new Matching.Constant())) && expandedEquation.getRight().matches(new Matching.Constant()) && ((BinaryNode) ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft()).getRight()).getLeft().matches(((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight()).getRight())) {
					Node a = ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft()).getLeft();
					Node b = ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight()).getLeft();
					Node c = ((BinaryNode) expandedEquation.getLeft()).getRight();
					Node d = expandedEquation.getRight();
					Node x = ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight()).getRight();
					solutions.addAll(quadraticSolution1(x, a, b, c, d).solve());
					solutions.addAll(quadraticSolution2(x, a, b, c, d).solve());
					break;
				} else if(expandedEquation.getLeft().matches(new Add(new Multiply(new Matching.Constant(), new Power(new Matching.Anything(), new Number(2))), new Multiply(new Matching.Constant(), new Matching.Anything()))) && expandedEquation.getRight().matches(new Matching.Constant()) && ((BinaryNode) ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getRight()).getLeft().matches(((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getRight()).getRight())) {
					Node a = ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getLeft()).getLeft();
					Node b = ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getRight()).getLeft();
					Node c = new Number(0);
					Node d = expandedEquation.getRight();
					Node x = ((BinaryNode) ((BinaryNode) expandedEquation.getLeft()).getRight()).getRight();
					solutions.addAll(quadraticSolution1(x, a, b, c, d).solve());
					solutions.addAll(quadraticSolution2(x, a, b, c, d).solve());
					break;
				// TODO: havent finished this one yet still working on it
				// } else if(expandedEquation.matches(new Equation(new Add(new Add(new Multiply(new Matching.Constant(), new Power(new Matching.Anything(), new Number(2))), new Matching.Anything()), new Matching.Constant()), new Matching.Constant()))) {

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
					if(((BinaryNode) equation.getLeft()).getLeft().contains(variable) && ((BinaryNode) equation.getLeft()).getRight().contains(variable))
						equation = new Equation(new Subtract(((BinaryNode) equation.getLeft()).getLeft(), new Multiply(equation.getRight(), ((BinaryNode) equation.getLeft()).getRight())), new Number(0));
					else
						equation = new Equation(((BinaryNode) equation.getLeft()).getLeft(), new Multiply(((BinaryNode) equation.getLeft()).getRight(), equation.getRight()));

				// 
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

// x^4+x^2 = 1