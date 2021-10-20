package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Sinh extends TrigNode {
	public Sinh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Sinh(getNode().copy());
	}
}
