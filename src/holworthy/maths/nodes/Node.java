package holworthy.maths.nodes;

import holworthy.maths.DivideByZeroException;

public abstract class Node {
	public abstract Node copy();

	public boolean matches(Node node) {
		return node == this;
	}

	public abstract boolean isConstant();

	public Node normalise() {
		return this;
	}

	public Node expand() throws DivideByZeroException {
		return this;
	}

	public Node collapse() throws DivideByZeroException{
		return this;
	}

	public final Node simplify() throws DivideByZeroException {
		return normalise().expand().normalise().collapse().normalise();
	}

	public abstract Node differentiate(Variable wrt);
}
