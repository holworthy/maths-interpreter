package holworthy.maths.nodes;

import holworthy.maths.DivideByZeroException;

public class Subtract extends BinaryNode {
	public Subtract(Node left, Node right) {
		super(left, right);
	}

	@Override
	public Node copy() {
		return new Subtract(getLeft().copy(), getRight().copy());
	}

	@Override
	public Node normalise() {
		return new Add(getLeft(), new Negative(getRight()));
	}

	@Override
	public Node expand() throws DivideByZeroException{
		return normalise().expand();
	}

	@Override
	public String toString() {
		return getLeft() + " - " + getRight();
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}
}
