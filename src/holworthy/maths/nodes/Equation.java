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

	public boolean isQuadratic() {
		return matches(
			new Equation(
				new Add(
					new Add(
						new Multiply(
							new Matching.Constant(),
							new Power(
								new Variable("x"),
								new Number(2)
							)
						),
						new Multiply(
							new Matching.Constant(),
							new Variable("x")
						)
					),
					new Matching.Constant()
				),
				new Number(0)
			)
		);
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

	public void solve() throws DivideByZeroException {
		Node start = new Equation(new Subtract(getLeft(), getRight()).simplify(), new Number(0));
		ArrayList<Variable> variables = new ArrayList<>(getVariables(start));
		variables.sort((a, b) -> a.getName().compareTo(b.getName()));

		for(Variable variable : variables) {
			Equation equation = (Equation) start.copy();

			System.out.println("solving for: " + variable);
			System.out.println("starting with: " + equation);

			while(!equation.getLeft().matches(variable)) {
				// -x = a -> x = -a
				if(equation.getLeft() instanceof Negative)
					equation = new Equation(((UnaryNode) equation.getLeft()).getNode(), new Negative(equation.getRight()));

				// x + a = b -> x = b - a
				// a + x = b -> x = b - a
				else if(equation.getLeft() instanceof Add) {
					if(((BinaryNode) equation.getLeft()).getLeft().contains(variable))
						equation = new Equation(((BinaryNode) equation.getLeft()).getLeft(), new Subtract(equation.getRight(), ((BinaryNode) equation.getLeft()).getRight()));
					else if(((BinaryNode) equation.getLeft()).getRight().contains(variable))
						equation = new Equation(((BinaryNode) equation.getLeft()).getRight(), new Subtract(equation.getRight(), ((BinaryNode) equation.getLeft()).getLeft()));
				}

				else
					break;
			}


			System.out.println(equation.simplify());
		}
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}

	public static void main(String[] args) throws Exception {
		Node input = Maths.parseInput("x+2=0");
		System.out.println(input);
		System.out.println(input.simplify());
		((Equation) input).solve();
	}
}
