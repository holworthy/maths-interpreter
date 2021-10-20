package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Tan extends TrigNode {
	public Tan(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Tan(getNode().copy());
	}
}
