package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Acosh extends TrigNode {
	public Acosh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acosh(getNode().copy());
	}
}
