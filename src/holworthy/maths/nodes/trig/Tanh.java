package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Tanh extends TrigNode {
	public Tanh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Tanh(getNode().copy());
	}
}
