package holworthy.maths.nodes;

public class Divide extends BinaryNode {
	public Divide(Node left, Node right) {
		super(left, right);
	}

	@Override
	public boolean matches(Node node) {
		if(node instanceof Divide)
			return super.matches(node);
		if(node instanceof Matching.Constant)
			return this.isConstant();
		return super.matches(node);
	}

	private int gcd(int a, int b) {
		return b == 0 ? a : gcd(b, a % b);
	}

	@Override
	public Node simplify() {
		Node left = getLeft().simplify();
		Node right = getRight().simplify();

		// TODO: handle divide by 0

		if(left instanceof Number && right instanceof Number) {
			int a = ((Number) getLeft()).getValue();
			int b = ((Number) getRight()).getValue();
			int divisor = gcd(a, b);

			if(a % b == 0)
				return new Number(a / b);

			return new Divide(new Number(a / divisor), new Number(b / divisor));
		}

		return new Divide(left, right);
	}

	@Override
	public String toString() {
		return "(" + getLeft() + ")/(" + getRight() + ")";
	}
}