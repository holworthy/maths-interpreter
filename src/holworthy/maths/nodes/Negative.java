package holworthy.maths.nodes;

public class Negative extends UnaryNode {
	public Negative(Node node) {
		super(node);
	}

	@Override
	public boolean isConstant() {
		return getNode().isConstant();
	}

	@Override
	public String toString() {
		return "(-" + getNode() + ")";
	}

	@Override
	public Node simplify() {
		Node node = getNode().simplify();
		if(node instanceof Number)
			return new Number(-((Number) node).getValue());
		return new Negative(node);
	}
}
