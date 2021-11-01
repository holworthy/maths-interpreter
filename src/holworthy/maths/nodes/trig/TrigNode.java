package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.FunctionNode;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public abstract class TrigNode extends FunctionNode {
	public TrigNode(Node node) {
		super(node);
	}

	@Override
	public boolean matches(Node node) {
		return super.matches(node) || (node.getClass().equals(getClass()) && getNode().matches(((UnaryNode) node).getNode()));
	}

	@Override
	public String toString() {
		return getName() + "(" + getNode() + ")";
	}
}
