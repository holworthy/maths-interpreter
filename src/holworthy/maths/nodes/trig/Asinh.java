package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Asinh extends TrigNode {
	public Asinh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Asinh(getNode().copy());
	}
}
