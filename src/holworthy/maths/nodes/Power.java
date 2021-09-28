package holworthy.maths.nodes;

public class Power extends BinaryNode {
	public Power(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return "(" + getLeft() + "^" + getRight() + ")";
	}
}
