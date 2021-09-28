package holworthy.maths.nodes;

public class Subtract extends BinaryNode {
	public Subtract(Node left, Node right) {
		super(left, right);
	}

	@Override
	public Node simplify() {
		Node left = getLeft().simplify();
		Node right = getRight().simplify();

		if(left instanceof Number && right instanceof Number) {
			return new Number(((Number) left).getValue() - ((Number) right).getValue());
		}

		return super.simplify();
	}
}
