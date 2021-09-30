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

	@Override
	public boolean matches(Node node) {
		if(node instanceof Matching.Constant)
			return isConstant();
		return node.getClass().equals(getClass()) && getLeft().matches(((BinaryNode) node).getLeft()) && getRight().matches(((BinaryNode) node).getRight());
	}

	@Override
	public boolean isConstant() {
		return getLeft().isConstant() && getRight().isConstant();
	}
}
