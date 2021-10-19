package holworthy.maths.nodes;

public abstract class UnaryNode extends Node {
	private Node node;

	public UnaryNode(Node node) {
		this.node = node;
	}

	public Node getNode() {
		return node;
	}

	@Override
	public boolean isConstant() {
		return getNode().isConstant();
	}
}
