package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Sech extends TrigNode {
	public Sech(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Sech(getNode().copy());
	}
}
