package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Acsc extends TrigNode {
	public Acsc(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acsc(getNode().copy());
	}
}
