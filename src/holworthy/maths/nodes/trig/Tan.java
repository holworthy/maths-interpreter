package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Tan extends TrigNode {
	public Tan(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Tan(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Atan)
			return ((UnaryNode) node).getNode();
		return new Tan(node);
	}
}
