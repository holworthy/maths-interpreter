package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.FunctionNode;
import holworthy.maths.nodes.Node;

public abstract class TrigNode extends FunctionNode {
	public TrigNode(Node node) {
		super(node);
	}

	@Override
	public String toString() {
		return getName() + "(" + getNode() + ")";
	}
}
