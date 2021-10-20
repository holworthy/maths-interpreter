package holworthy.maths.nodes;

import holworthy.maths.DivideByZeroException;

public class Negative extends UnaryNode {
	public Negative(Node node) {
		super(node);
	}

	@Override
	public String toString() {
		return "-" + getNode();
	}

	@Override
	public Node copy() {
		return new Negative(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException{
		Node node = getNode().expand();
		if(node.matches(new Number(0)))
			return new Number(0);
		if(node instanceof Negative)
			return ((Negative) node).getNode();
		if(node instanceof Add)
			return new Add(new Negative(((BinaryNode) node).getLeft()), new Negative(((BinaryNode) node).getRight())).expand();
		return new Negative(node);
	}

	@Override
	public boolean matches(Node node) {
		if(node instanceof Negative)
			return getNode().matches(((Negative) node).getNode());
		return super.matches(node);
	}

	@Override
	public boolean isConstant() {
		return getNode().isConstant();
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}
}
