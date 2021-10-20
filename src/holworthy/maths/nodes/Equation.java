package holworthy.maths.nodes;

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
		Node start = new Equation(new Subtract(getLeft(), getRight()).expand(), new Number(0));
		HashSet<Variable> variables = getVariables(start);

		for(Variable variable : variables) {
			Equation variableStart = (Equation) start.copy();
			// solve this
			// output it
		}
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}

	public static void main(String[] args) throws Exception {
		Node input = Maths.parseInput("3+y=x");
		System.out.println(input);
		System.out.println(input.simplify());
		((Equation) input).solve();
	}
}
