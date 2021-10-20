package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Acos extends TrigNode {
	public Acos(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acos(getNode().copy());
	}
}
