package holworthy.maths.nodes;

public class Power extends BinaryNode {
	public Power(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return (getLeft() instanceof Power || getLeft() instanceof Variable || getLeft() instanceof Number ? getLeft() : "(" + getLeft() + ")") + "^" + (getRight() instanceof Power || getRight() instanceof Number || getRight() instanceof Variable ? getRight() : "(" + getRight() + ")");
	}

	@Override
	public Node expand() {
		Node left = getLeft().expand();
		Node right = getRight().expand();

		if(left instanceof Number && right instanceof Number)
			return new Number((int) Math.pow(((Number) left).getValue(), ((Number) right).getValue()));

		return new Power(left, right);
	}
}
