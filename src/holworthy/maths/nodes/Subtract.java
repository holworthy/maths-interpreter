package holworthy.maths.nodes;

import holworthy.maths.exceptions.MathsInterpreterException;

public class Subtract extends BinaryNode {
	public Subtract(Node left, Node right) {
		super(left, right);
	}

	@Override
	public Node copy() {
		return new Subtract(getLeft().copy(), getRight().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException{
		return new Add(getLeft(), new Negative(getRight())).expand();
	}

	@Override
	public String toString() {
		return getLeft() + " - " + getRight();
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Subtract(getLeft().differentiate(wrt), getRight().differentiate(wrt)).simplify();
	}
}
