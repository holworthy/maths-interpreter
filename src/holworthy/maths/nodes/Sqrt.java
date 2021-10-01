package holworthy.maths.nodes;

public class Sqrt extends UnaryNode {
	public Sqrt(Node node) {
		super(node);
	}

	@Override
	public boolean isConstant() {
		return getNode().isConstant();
	}

	@Override
	public String toString() {
		return "sqrt(" + getNode() + ")";
	}

	@Override
	public Node expand() {
		Node node = getNode().expand();
		if(node instanceof Number && ((Number) node).getValue() == 0)
			return new Number(0);
		if(node instanceof Number && ((Number) node).getValue() == 1)
			return new Number(1);
		return new Sqrt(node);
	}
}
