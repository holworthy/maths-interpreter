package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class Asec extends TrigNode {
	public Asec(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Asec(getNode().copy());
	}
}
