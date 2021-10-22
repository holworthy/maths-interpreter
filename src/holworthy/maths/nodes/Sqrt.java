package holworthy.maths.nodes;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.constant.I;

public class Sqrt extends Nthrt {
	public Sqrt(Node node) {
		super(node, 2);
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

		if(node instanceof Negative)
			return new Multiply(new I(), new Sqrt(((Negative) node).getNode())).expand();

		if(node.matches(new Number(0)))
			return new Number(0);
		if(node.matches(new Number(1)))
			return new Number(1);

		super.expand();
		// TODO: check this is ok

		// if(node instanceof Number) {
		// 	int n = ((Number) node).getValue();
		// 	int s = (int) Math.floor(Math.sqrt(n));
		// 	if(Math.pow(s, 2) == n)
		// 		return new Number(s);
		// }

		return new Sqrt(node);
	}
}
