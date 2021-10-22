package holworthy.maths.nodes;

import java.math.BigInteger;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.constant.I;

public class Sqrt extends Nthrt {
	public Sqrt(Node node) {
		super(node, BigInteger.TWO);
	}

	@Override
	public boolean isConstant() {
		return getNode().isConstant();
	}

	@Override
	public String toString() {
		return "sqrt(" + getNode() + ")";
	}

	@Override
	public Node copy() {
		return new Sqrt(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException{
		Node node = getNode().expand();
		if(node.matches(new Number(0)))
			return new Number(0);
		if(node.matches(new Number(1)))
			return new Number(1);
		if(node instanceof Negative)
			return new Multiply(new I(), new Sqrt(((Negative) node).getNode())).expand();
		return super.expand();
	}
}
