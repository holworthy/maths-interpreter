package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Csc extends TrigNode {
	public Csc(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Csc(getNode().copy());
	}
}
