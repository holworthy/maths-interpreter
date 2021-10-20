package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Csch extends TrigNode {
	public Csch(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Csch(getNode().copy());
	}
}
