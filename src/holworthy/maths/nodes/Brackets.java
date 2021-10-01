package holworthy.maths.nodes;

public class Brackets extends UnaryNode {
	public Brackets(Node node) {
		super(node);
	}

	@Override
	public String toString() {
		return "(" + getNode() + ")";
	}

	@Override
	public Node expand() {
		return getNode().expand();
	}
}
