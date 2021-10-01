package holworthy.maths.nodes;

public class Multiply extends BinaryNode {
	public Multiply(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return (getLeft() instanceof Add ? "(" + getLeft() + ")" : getLeft()) + "*" + (getRight() instanceof Add ? "(" + getRight() + ")" : getRight());
	}

	@Override
	public Node simplify() {
		Node left = getLeft().simplify();
		Node right = getRight().simplify();

		// Multiply two numbers together
		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue() * ((Number) right).getValue());
	
		// Make tree left leaning
		if(right instanceof Multiply)
			return new Multiply(new Multiply(left, ((Multiply) right).getLeft().simplify()).simplify(), ((Multiply) right).getRight());

		// Expanding brackets
		if(right instanceof Add)
			return new Add(new Multiply(left, ((Add) right).getLeft().simplify()).simplify(), new Multiply(left, ((Add) right).getRight().simplify()).simplify());

		// Putting brackets on the right (which then calls the rule above)
		if(left instanceof Add)
			return new Multiply(right, left).simplify();

		return new Multiply(left, right);
	}
}

