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
		if(node instanceof Number)
			return new Number(-((Number) node).getValue());
		return new Negative(node);
	}
}
