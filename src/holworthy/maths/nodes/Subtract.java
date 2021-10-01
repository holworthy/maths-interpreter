package holworthy.maths.nodes;

public class Subtract extends BinaryNode {
	public Subtract(Node left, Node right) {
		super(left, right);
	}

	@Override
	public Node normalise() {
		return new Add(getLeft(), new Negative(getRight()));
	}

	@Override
	public Node expand() {
		return normalise();
	}

	@Override
	public String toString() {
		return getLeft() + "-" + getRight();
	}
}
