package holworthy.maths.nodes;

import java.util.ArrayList;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.trig.Acos;
import holworthy.maths.nodes.trig.Cos;

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

	public ArrayList<Equation> solve() throws MathsInterpreterException {
		Node start = new Equation(new Subtract(getLeft(), getRight()).simplify(), new Number(0));
		ArrayList<Variable> variables = getVariables(start);
		variables.sort((a, b) -> a.getName().compareTo(b.getName()));

		ArrayList<Equation> solutions = new ArrayList<>();
		for(Variable variable : variables) {
			Equation equation = (Equation) start.copy();
			
			while(!equation.getLeft().matches(variable)) {
				System.out.println(equation);
				equation = (Equation) equation.simplify();
				System.out.println(equation);

				// a*x^2 + b*x + c = d
				if(equation.getLeft().matches(new Add(new Add(new Multiply(new Matching.Constant(), new Power(new Matching.Anything(), new Number(2))), new Multiply(new Matching.Constant(), new Matching.Anything())), new Matching.Constant())) && equation.getRight().matches(new Matching.Constant()) && ((BinaryNode) ((BinaryNode) ((BinaryNode) ((BinaryNode) equation.getLeft()).getLeft()).getLeft()).getRight()).getLeft().matches(((BinaryNode) ((BinaryNode) ((BinaryNode) equation.getLeft()).getLeft()).getRight()).getRight())) {
					Node a = ((BinaryNode) ((BinaryNode) ((BinaryNode) equation.getLeft()).getLeft()).getLeft()).getLeft();
					Node b = ((BinaryNode) ((BinaryNode) ((BinaryNode) equation.getLeft()).getLeft()).getRight()).getLeft();
					Node c = ((BinaryNode) equation.getLeft()).getRight();
					Node d = equation.getRight();
					Node x = ((BinaryNode) ((BinaryNode) ((BinaryNode) equation.getLeft()).getLeft()).getRight()).getRight();

					// x = (-b +- sqrt(b^2 - 4 * a * (c - d))) / (2 * a)
					solutions.addAll(new Equation(x, new Divide(new Add(new Negative(b), new Sqrt(new Subtract(new Power(b, new Number(2)), new Multiply(new Multiply(new Number(4), a), new Subtract(c, d))))), new Multiply(new Number(2), a))).solve());
					solutions.addAll(new Equation(x, new Divide(new Subtract(new Negative(b), new Sqrt(new Subtract(new Power(b, new Number(2)), new Multiply(new Multiply(new Number(4), a), new Subtract(c, d))))), new Multiply(new Number(2), a))).solve());

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
				} else if(equation.getLeft() instanceof Subtract) {
					if(((BinaryNode) equation.getLeft()).getLeft().contains(variable)) {
						equation = new Equation(((BinaryNode) equation.getLeft()).getLeft(), new Add(equation.getRight(), ((BinaryNode) equation.getLeft()).getRight()));
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

				// cos(x) = a -> x = acos(a)
				} else if(equation.getLeft() instanceof Cos) {
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Acos(equation.getRight()));

				} else {
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
}
