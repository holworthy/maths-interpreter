package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Atan extends TrigNode {
	public Atan(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Atan(getNode().copy());
	}
}
