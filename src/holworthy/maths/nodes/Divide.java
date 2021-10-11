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
	public Node expand() {
		Node left = getLeft().expand();
		Node right = getRight().expand();

		// TODO: handle divide by 0

		if(left.matches(right))
			return new Number(1);

		if(left instanceof Negative && right instanceof Negative)
			return new Divide(((Negative) left).getNode(), ((Negative) right).getNode()).expand();
		if(left instanceof Negative)
			return new Negative(new Divide(((Negative) left).getNode(), right)).expand();
		if(right instanceof Negative)
			return new Negative(new Divide(left, ((Negative) right).getNode())).expand();

		if(left instanceof Number && right instanceof Number) {
			int a = ((Number) left).getValue();
			int b = ((Number) right).getValue();

			int divisor = gcd(a, b);

			if(a % b == 0)
				return new Number(a / b);

			return new Divide(new Number(a / divisor), new Number(b / divisor));
		}

		return new Multiply(left, new Power(right, new Number(-1))).expand();
	}

	@Override
	public Node collapse() {
		return new Divide(getLeft().collapse(), getRight().collapse());
	}

	@Override
	public String toString() {
		return (getLeft() instanceof Number | getLeft() instanceof Variable | getLeft() instanceof Multiply ? getLeft() : "(" + getLeft() + ")") + "/" + (getRight() instanceof Number | getRight() instanceof Variable ? getRight() : "(" + getRight() + ")");
	}
}
