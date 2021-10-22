package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Sinh extends TrigNode {
	public Sinh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Sinh(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Asinh)
			return ((UnaryNode) node).getNode();
		return new Sinh(node);
	}
}
