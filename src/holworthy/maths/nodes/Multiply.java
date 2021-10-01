package holworthy.maths.nodes;

public class Multiply extends BinaryNode {
	public Multiply(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return getLeft() + "*" + getRight();
	}

	@Override
	public Node simplify() {
		Node left = getLeft().simplify();
		Node right = getRight().simplify();

		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue() * ((Number) right).getValue());
	
		if(right instanceof Multiply)
			return new Multiply(new Multiply(left, ((Multiply) right).getLeft().simplify()).simplify(), ((Multiply) right).getRight());

		if(right instanceof Add)
			return new Add(new Multiply(left, ((Add) right).getLeft()), new Multiply(left, ((Add) right).getRight()));

		return new Multiply(left, right);
	}
}

