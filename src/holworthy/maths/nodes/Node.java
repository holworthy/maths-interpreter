package holworthy.maths.nodes;

public abstract class Node {
	public Node simplify() {
		return normalise().expand().collapse().normalise();
	}

	public Node normalise() {
		return this;
	}

	public Node expand() {
		return this;
	}

	public Node collapse() {
		return this;
	}

	public boolean matches(Node node) {
		return node == this;
	}

	public abstract boolean isConstant();
}
