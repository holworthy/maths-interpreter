package holworthy.maths.nodes;

public class Equation extends BinaryNode {
	public Equation(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return getLeft() + "=" + getRight();
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
	public Node expand() {
		return new Equation(getLeft().expand(), getRight().expand());
	}
}
