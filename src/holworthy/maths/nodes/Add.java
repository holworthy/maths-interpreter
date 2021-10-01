package holworthy.maths.nodes;

public class Add extends BinaryNode {
	public Add(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return getLeft() + "+" + getRight();
	}

	@Override
	public Node simplify() {
		Node left = getLeft().simplify();
		Node right = getRight().simplify();

		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue() + ((Number) right).getValue());

		if(right instanceof Add)
			return new Add(new Add(left, ((Add) right).getLeft().simplify()).simplify(), ((Add) right).getRight().simplify());

		if(left instanceof Multiply && right instanceof Multiply && ((Multiply) left).getRight().matches(((Multiply) right).getRight()))
			return new Multiply(new Add(((Multiply) left).getLeft(), ((Multiply) right).getLeft()).simplify(), ((Multiply) left).getRight());

		return new Add(left, right);
	}
}
