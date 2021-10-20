package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Atanh extends TrigNode {
	public Atanh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Atanh(getNode().copy());
	}
}
