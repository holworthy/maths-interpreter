package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Acsch extends TrigNode {
	public Acsch(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acsch(getNode().copy());
	}
}
