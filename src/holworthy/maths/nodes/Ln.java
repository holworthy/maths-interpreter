package holworthy.maths.nodes;

import holworthy.maths.nodes.constant.E;

public class Ln extends Log {
	public Ln(Node node) {
		super(node, new E());
	}

	@Override
	public String toString() {
		return "ln(" + getNode() + ")";
	}

	@Override
	public Node copy() {
		return new Ln(getNode().copy());
	}
}
