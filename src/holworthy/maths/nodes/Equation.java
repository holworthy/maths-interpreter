package holworthy.maths.nodes;

import java.util.ArrayList;
import java.util.HashSet;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.Maths;

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
	public Node expand() throws DivideByZeroException {
		return new Equation(getLeft().expand(), getRight().expand());
	}

	private HashSet<Variable> getVariables(Node node) {
		HashSet<Variable> hashSet = new HashSet<>();

		if(node instanceof Variable)
			hashSet.add((Variable) node);
		else if(node instanceof UnaryNode)
			hashSet.addAll(getVariables(((UnaryNode) node).getNode()));
		else if(node instanceof BinaryNode) {
			hashSet.addAll(getVariables(((BinaryNode) node).getLeft()));
			hashSet.addAll(getVariables(((BinaryNode) node).getRight()));
		}

		return hashSet;
	}

	public ArrayList<Equation> solve() throws DivideByZeroException {
		Node start = new Equation(new Subtract(getLeft(), getRight()).simplify(), new Number(0));
		ArrayList<Variable> variables = new ArrayList<>(getVariables(start));
		variables.sort((a, b) -> a.getName().compareTo(b.getName()));

		ArrayList<Equation> solutions = new ArrayList<>();

		for(Variable variable : variables) {
			Equation equation = (Equation) start.copy();

			System.out.println("solving for: " + variable);
			System.out.println("starting with: " + equation);

			while(!equation.getLeft().matches(variable)) {
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
				
				// x * a = b -> x = b / a
				} else if(equation.getLeft() instanceof Multiply) {
					if(((BinaryNode) equation.getLeft()).getLeft().contains(variable))
						equation = new Equation(((BinaryNode) equation.getLeft()).getLeft(), new Divide(equation.getRight(), ((BinaryNode) equation.getLeft()).getRight()));
					else if(((BinaryNode) equation.getLeft()).getRight().contains(variable))
						equation = new Equation(((BinaryNode) equation.getLeft()).getRight(), new Divide(equation.getRight(), ((BinaryNode) equation.getLeft()).getLeft()));

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
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}

	public static void main(String[] args) throws Exception {
		Node input = Maths.parseInput("3*x=y/2");

		System.out.println(input);
		System.out.println(input.simplify());

		System.out.println(((Equation) input).solve());
	}
}
