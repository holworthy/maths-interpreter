package holworthy.maths.nodes;

import java.math.BigInteger;
import java.util.HashMap;

import holworthy.maths.exceptions.DivideByZeroException;
import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.constant.ConstantNode;
import holworthy.maths.nodes.constant.E;
import holworthy.maths.nodes.constant.I;
import holworthy.maths.nodes.constant.Pi;

public class Power extends BinaryNode {
	public Power(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return (getLeft() instanceof Power || getLeft() instanceof Variable || getLeft() instanceof Number || getLeft() instanceof ConstantNode ? getLeft() : "(" + getLeft() + ")") + "^" + (getRight() instanceof Power || getRight() instanceof Number || getRight() instanceof Variable ? getRight() : "(" + getRight() + ")");
	}

	@Override
	public Node copy() {
		return new Power(getLeft().copy(), getRight().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node left = getLeft().expand();
		Node right = getRight().expand();

		// undefined behaviour
		if(left.matches(new Number(0)) && right.matches(new Number(0)))
			throw new DivideByZeroException("0^0 is undefined");

		// constant folding
		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue().pow(((Number) right).getValue().intValue()));

		// -2^3 = -8 & -2^2 = 4
		if(left instanceof Negative && right instanceof Number){
			if(((UnaryNode) left).getNode() instanceof Number){
				if(((Number) right).getValue().mod(BigInteger.TWO).equals(BigInteger.ZERO))
					return new Number(((Number) ((UnaryNode) left).getNode()).getValue().pow(((Number) right).getValue().intValue()));
				return new Negative(new Number(((Number) ((UnaryNode) left).getNode()).getValue().pow(((Number) right).getValue().intValue())));
			}
		}

		// 2^-2 = 1/4
		if(left instanceof Number && right instanceof Negative)
			return new Divide(new Number(1), new Power(left, ((UnaryNode) right).getNode())).expand();
		
		// -2^-2 = 1/-2^2.expand()
		if(left instanceof Negative && right instanceof Negative)
			return new Divide(new Number(1), new Power(left, ((UnaryNode) right).getNode())).expand();
		
		// fractional powers
		if(left instanceof Number && right instanceof Divide && ((BinaryNode) right).getLeft() instanceof Number && ((BinaryNode) right).getRight() instanceof Number)
			return new Nthrt(new Power(left, ((BinaryNode) right).getLeft()), ((Number) ((BinaryNode) right).getRight())).expand();

		if(left instanceof Power)
			return new Power(((BinaryNode) left).getLeft(), new Multiply(((BinaryNode) left).getRight(), right)).expand();

		// i^0 = 1
		// i^1 = i
		// i^2 = -1
		// i^3 = -i
		if(left instanceof I && right instanceof Number) {
			Number mod = new Number(((Number) right).getValue().mod(new BigInteger("4")));
			if(mod.matches(new Number(0)))
				return new Number(1);
			if(mod.matches(new Number(1)))
				return new I();
			if(mod.matches(new Number(2)))
				return new Negative(new Number(1));
			if(mod.matches(new Number(3)))
				return new Negative(new I());
		}

		// x^0 = 1
		if(right.matches(new Number(0)))
			return new Number(1);

		// x^1 = x
		if(right.matches(new Number(1)))
			return left;

		if(left instanceof Divide && right instanceof Number){
			return new Divide(new Power(((BinaryNode) left).getLeft(), right), new Power(((BinaryNode) left).getRight(), right)).expand();
		}

		if(left instanceof Negative && ((UnaryNode) left).getNode() instanceof Divide && right instanceof Number){
			return new Divide(new Power(new Negative(((BinaryNode) ((UnaryNode) left).getNode()).getLeft()), right), new Power(((BinaryNode) ((UnaryNode) left).getNode()).getRight(), right)).expand();
		}

		// binomial theorum which works as multinomial theorum
		if(left instanceof Add && right instanceof Number) {
			Node temp = new Number(0);
			for(BigInteger k = BigInteger.ZERO; k.compareTo(((Number) right).getValue().add(BigInteger.ONE)) < 0; k = k.add(BigInteger.ONE))
				temp = new Add(temp, new Multiply(new Multiply(new Divide(new Factorial(right), new Multiply(new Factorial(new Number(k)), new Factorial(new Subtract(right, new Number(k))))), new Power(((BinaryNode) left).getLeft(), new Subtract(right, new Number(k)))), new Power(((BinaryNode) left).getRight(), new Number(k))).expand());
			return temp.expand();
		}

		// (a+b)^3 = (a+b)^2*(a+b)
		if(left instanceof Add && right instanceof Number)
			return new Multiply(new Power(left, new Subtract(right, new Number(1))), left).expand();

		// e^(i*pi) = -1
		if(left instanceof E && right.matches(new Multiply(new I(), new Pi())))
			return new Negative(new Number(1));

		if(left instanceof Multiply)
			return new Multiply(new Power(((BinaryNode) left).getLeft(), right), new Power(((BinaryNode) left).getRight(), right)).expand();

		if(left instanceof Nthrt)
			return new Power(((Nthrt) left).getNode(), new Divide(right, ((Nthrt) left).getDegree())).expand();

		if(right instanceof Number && left instanceof Negative) {
			if(((Number) right).getValue().mod(BigInteger.TWO).equals(BigInteger.ZERO))
				return new Power(((UnaryNode) left).getNode(), right).expand();
			else
				return new Negative(new Power(((UnaryNode) left).getNode(), right)).expand();
		}
		
		return new Power(left, right);
	}

	@Override
	public Node collapse() throws MathsInterpreterException {
		Node left = getLeft().collapse();
		Node right = getRight().collapse();

		if(right instanceof Negative)
			return new Divide(new Number(1), new Power(left, ((UnaryNode) right).getNode()).simplify()).collapse();
		if(right instanceof Divide)
			return new Nthrt(new Power(left, ((BinaryNode) right).getLeft()).simplify(), ((BinaryNode) right).getRight()).collapse();

		return new Power(left, right);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(this, new Add(new Multiply(getLeft().differentiate(wrt), new Divide(getRight(), getLeft())), new Multiply(getRight().differentiate(wrt), new Ln(getLeft())))).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return Math.pow(getLeft().evaluate(values), getRight().evaluate(values));
	}

	@Override
	public Node replace(Node before, Node after) {
		if(matches(before))
			return after;
		return new Power(getLeft().replace(before, after), getRight().replace(before, after));
	}
}
