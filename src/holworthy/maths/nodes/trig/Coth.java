package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Coth extends TrigNode {
	public Coth(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Coth(getNode().copy());
	}
}
