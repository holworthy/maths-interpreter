package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Acot extends TrigNode {
	public Acot(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acot(getNode().copy());
	}
}
