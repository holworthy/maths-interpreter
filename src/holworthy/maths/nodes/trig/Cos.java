package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Cos extends TrigNode {
	public Cos(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Cos(getNode().copy());
	}
}
