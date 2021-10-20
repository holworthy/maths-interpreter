package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Asech extends TrigNode {
	public Asech(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Asech(getNode().copy());
	}
}
