package holworthy.maths.nodes;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.constant.E;
import holworthy.maths.nodes.constant.I;
import holworthy.maths.nodes.constant.Pi;

public class Power extends BinaryNode {
	public Power(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return (getLeft() instanceof Power || getLeft() instanceof Variable || getLeft() instanceof Number ? getLeft() : "(" + getLeft() + ")") + "^" + (getRight() instanceof Power || getRight() instanceof Number || getRight() instanceof Variable ? getRight() : "(" + getRight() + ")");
	}

	@Override
	public Node expand() throws DivideByZeroException{
		Node left = getLeft().expand();
		Node right = getRight().expand();

		if(left.matches(new Number(0)) && right.matches(new Number(0)))
			throw new DivideByZeroException("0^0 is undefined");

		// 2^3 = 8
		if(left instanceof Number && right instanceof Number)
			return new Number((int) Math.pow(((Number) left).getValue(), ((Number) right).getValue()));
		
		if(left instanceof Number && right instanceof Divide && ((BinaryNode) right).getLeft() instanceof Number && ((BinaryNode) right).getRight() instanceof Number){
			return new Nthrt(new Power(left, ((BinaryNode) right).getLeft()), ((Number) ((BinaryNode) right).getRight()).getValue()).expand();
		}

		// i*i = -1
		if(matches(new Power(new I(), new Number(2))))
			return new Negative(new Number(1));

		// x^0 = 1
		if(right instanceof Number && ((Number) right).getValue() == 0)
			return new Number(1);

		// x^1 = x
		if(right instanceof Number && ((Number) right).getValue() == 1)
			return left;

		// binomial theorum
		if(left instanceof Add && !(((BinaryNode) left).getLeft() instanceof Add) && right instanceof Number) {
			Node temp = new Number(0);
			for(int k = 0; k < ((Number) right).getValue() + 1; k++)
				temp = new Add(temp, new Multiply(new Multiply(new Divide(new Factorial(right), new Multiply(new Factorial(new Number(k)), new Factorial(new Subtract(right, new Number(k))))), new Power(((BinaryNode) left).getLeft(), new Subtract(right, new Number(k)))), new Power(((BinaryNode) left).getRight(), new Number(k))).expand());
			return temp.expand();
		}

		// (a+b)^3 = (a+b)^2*(a+b)
		if(left instanceof Add && right instanceof Number)
			return new Multiply(new Power(left, new Subtract(right, new Number(1))), left).expand();

		// e^(i*pi) = -1
		if(left instanceof E && right instanceof Multiply && ((BinaryNode) right).getLeft() instanceof I && ((BinaryNode) right).getRight() instanceof Pi)
			return new Negative(new Number(1));

		if(left instanceof Multiply)
			return new Multiply(new Power(((BinaryNode) left).getLeft(), right), new Power(((BinaryNode) left).getRight(), right)).expand();




		return new Power(left, right);
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}
}
