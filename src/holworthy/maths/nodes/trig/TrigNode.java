package holworthy.maths.nodes.trig;

import holworthy.maths.nodes.FunctionNode;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Variable;

public abstract class TrigNode extends FunctionNode {
	public TrigNode(Node node) {
		super(node);
	}

	@Override
	public Node differentiate(Variable wrt) {
		return null;
	}

	@Override
	public String toString() {
		return getName() + "(" + getNode() + ")";
	}
}
