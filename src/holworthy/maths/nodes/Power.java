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
		
		if(left instanceof Number && right instanceof Divide && ((BinaryNode) right).getLeft() instanceof Number && ((BinaryNode) right).getRight() instanceof Number){
			return new Nthrt(new Power(left, ((BinaryNode) right).getLeft()), ((Number) ((BinaryNode) right).getRight()).getValue()).expand();
		}

		// i*i = -1
		if(matches(new Power(new I(), new Number(2))))
			return new Negative(new Number(1));

		return new Power(left, right);
	}
}
