package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Sec extends TrigNode {
	public Sec(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Sec(getNode().copy());
	}
}
