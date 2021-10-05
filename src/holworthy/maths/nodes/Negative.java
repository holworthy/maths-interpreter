package holworthy.maths.nodes;

public class Negative extends UnaryNode {
	public Negative(Node node) {
		super(node);
	}

	@Override
	public String toString() {
		return "-" + getNode();
	}

	@Override
	public Node expand() {
		Node node = getNode().expand();
		if(node instanceof Negative)
			return ((Negative) node).getNode();
		return new Negative(node);
	}

	@Override
	public boolean matches(Node node) {
		if(node instanceof Matching.Constant)
			return isConstant();
		if(node instanceof Negative)
			return getNode().matches(((Negative) node).getNode());
		return false;
	}

	@Override
	public boolean isConstant() {
		return getNode().isConstant();
	}
}
