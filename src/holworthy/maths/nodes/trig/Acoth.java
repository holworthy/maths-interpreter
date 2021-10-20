package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Acoth extends TrigNode {
	public Acoth(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acoth(getNode().copy());
	}
}
