package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Cot extends TrigNode {
	public Cot(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Cot(getNode().copy());
	}
}
