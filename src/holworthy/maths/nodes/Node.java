package holworthy.maths.nodes;

public abstract class Node {
	public Node normalise() {
		return this;
	}

	public Node expand() {
		return this;
	}

	public Node collapse() {
		return this;
	}

	public final Node simplify() {
		return normalise().expand().normalise().collapse().normalise();
	}

	public boolean matches(Node node) {
		return node == this;
	}

	public abstract boolean isConstant();
	public abstract Node differentiate(Variable wrt);
}
