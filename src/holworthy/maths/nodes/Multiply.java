package holworthy.maths.nodes;

public class Multiply extends BinaryNode {
	public Multiply(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return "(" + getLeft() + "*" + getRight() + ")";
	}

	@Override
	public Node simplify() {
		Node left = getLeft().simplify();
		Node right = getRight().simplify();

		if(left instanceof Number && right instanceof Number) {
			return new Number(((Number) left).getValue() * ((Number) right).getValue());
		}

		return new Multiply(left, right);
	}
}
