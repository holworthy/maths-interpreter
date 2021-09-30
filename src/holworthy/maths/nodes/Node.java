package holworthy.maths.nodes;

public abstract class Node {
	public Node simplify() {
		return this;
	}

	public boolean matches(Node node) {
		return node == this;
	}

	public abstract boolean isConstant();
}
