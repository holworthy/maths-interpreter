package holworthy.maths.nodes;

public abstract class BinaryNode extends Node {
	private Node left;
	private Node right;

	public BinaryNode(Node left, Node right) {
		this.left = left;
		this.right = right;
	}

	public Node getLeft() {
		return left;
	}

	public Node getRight() {
		return right;
	}
}
