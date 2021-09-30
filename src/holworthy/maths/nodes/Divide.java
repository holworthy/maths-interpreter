package holworthy.maths.nodes;

public class Divide extends BinaryNode {
	public Divide(Node left, Node right) {
		super(left, right);
	}

	@Override
	public boolean matches(Node node) {
		if(node instanceof Divide)
			return super.matches(node);
		if(node instanceof Matching.Constant)
			return this.isConstant();
		return super.matches(node);
	}

	@Override
	public Node simplify() {
		Node left = getLeft().simplify();
		Node right = getRight().simplify();
		return new Divide(left, right);
	}

	@Override
	public String toString() {
		return "(" + getLeft() + ")/(" + getRight() + ")";
	}
}
