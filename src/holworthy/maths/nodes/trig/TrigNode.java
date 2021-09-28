package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.Node;

public class TrigNode extends Node {
	private Node arg;

	public TrigNode(Node arg) {
		this.arg = arg;
	}

	public Node getArg() {
		return arg;
	}
}
