package holworthy.maths.nodes;

import holworthy.maths.exceptions.DivideByZeroException;
import holworthy.maths.exceptions.MathsInterpreterException;

public class Log extends FunctionNode {
	private Node base;

	public Log(Node node, Node base) {
		super(node);
		this.base = base;
	}

	public Node getBase() {
		return base;
	}

	@Override
	public String toString() {
		return "log(" + getNode() + ", " + getBase() + ")";
	}

	@Override
	public Node copy() {
		return new Log(getNode().copy(), getBase().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		Node base = getBase().expand();

		if(node.matches(base))
			return new Number(1);
		if(node instanceof Power && ((BinaryNode) node).getLeft().matches(base))
			return ((BinaryNode) node).getRight();

		return new Log(node, base);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Divide(getNode().differentiate(wrt), new Multiply(new Ln(getBase()), getNode())).simplify();
	}
}
