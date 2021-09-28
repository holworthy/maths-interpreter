package holworthy.maths.nodes;

public class Multiply extends BinaryNode {
	public Multiply(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return "(" + getLeft() + "*" + getRight() + ")";
	}
}
