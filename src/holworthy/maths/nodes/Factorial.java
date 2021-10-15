package holworthy.maths.nodes;

import holworthy.maths.DivideByZeroException;

public class Factorial extends UnaryNode {
	public Factorial(Node node) {
		super(node);
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node.matches(new Number(0)) || node.matches(new Number(1)))
			return new Number(1);
		if(node instanceof Number)
			return new Multiply(node, new Factorial(new Subtract(node, new Number(1)))).expand();
		return new Factorial(node);
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}

	@Override
	public String toString() {
		return "(" + getNode() + ")!";
	}
}
