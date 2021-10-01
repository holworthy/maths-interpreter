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
	public Node normalise() {
		Node left = getLeft().normalise();
		Node right = getRight().normalise();

		// move constants to left
		if(right.matches(new Matching.Constant()) && !left.matches(new Matching.Constant()))
			return new Multiply(right, left).normalise();
		
		// Make tree left leaning
		if(right instanceof Multiply)
			return new Multiply(new Multiply(left, ((Multiply) right).getLeft().normalise()).normalise(), ((Multiply) right).getRight());

		return new Multiply(left, right);
	}

	@Override
	public Node expand() {
		Node left = getLeft().expand();
		Node right = getRight().expand();

		// Expanding brackets
		if(right instanceof Add)
			return new Add(new Multiply(left, ((Add) right).getLeft().expand()).expand(), new Multiply(left, ((Add) right).getRight().expand()).expand());

		// Putting brackets on the right (which then calls the rule above)
		if(left instanceof Add)
			return new Multiply(right, left).expand();

		return new Multiply(left, right);
	}

	@Override
	public Node collapse() {
		Node left = getLeft().collapse();
		Node right = getRight().collapse();

		// Multiply two numbers together
		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue() * ((Number) right).getValue());
	
		// x*x = x^2
		if(left.matches(right))
			return new Power(left, new Number(2));

		return new Multiply(left, right);
	}
}

