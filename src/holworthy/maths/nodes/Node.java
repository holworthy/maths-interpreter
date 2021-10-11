package holworthy.maths.nodes;

import holworthy.maths.DivideByZeroException;

public abstract class Node {
	public Node normalise() {
		return this;
	}

	public Node expand() throws DivideByZeroException {
		return this;
	}

	public Node collapse() {
		return this;
	}

	public final Node simplify() throws DivideByZeroException {
		return normalise().expand().normalise().collapse().normalise();
	}

	public boolean matches(Node node) {
		return node == this;
	}

	public abstract boolean isConstant();
	public abstract Node differentiate(Variable wrt);
}
