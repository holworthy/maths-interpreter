package holworthy.maths.nodes;

import holworthy.maths.nodes.constant.I;

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

		// 2^3 = 8
		if(left instanceof Number && right instanceof Number)
			return new Number((int) Math.pow(((Number) left).getValue(), ((Number) right).getValue()));
		
		if(left instanceof Number && right instanceof Divide && ((BinaryNode) right).getLeft() instanceof Number && ((BinaryNode) right).getRight() instanceof Number){
			return new Nthrt(new Power(left, ((BinaryNode) right).getLeft()), ((Number) ((BinaryNode) right).getRight()).getValue()).expand();
		}

		// i*i = -1
		if(matches(new Power(new I(), new Number(2))))
			return new Negative(new Number(1));

		// TODO: handle 0^0
		// x^0 = 1
		if(right instanceof Number && ((Number) right).getValue() == 0)
			return new Number(1);

		// x^1 = x
		if(right instanceof Number && ((Number) right).getValue() == 1)
			return left;

		// (a+b)^3 = (a+b)^2*(a+b)
		if(left instanceof Add && right instanceof Number)
			return new Multiply(new Power(left, new Subtract(right, new Number(1))), left).expand();

		return new Power(left, right);
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}
}
