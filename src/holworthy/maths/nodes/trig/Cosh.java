package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Cosh extends TrigNode {
	public Cosh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Cosh(getNode().copy());
	}
}
