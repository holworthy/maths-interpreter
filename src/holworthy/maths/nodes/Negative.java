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
		return new Negative(getNode().expand());
	}
}
