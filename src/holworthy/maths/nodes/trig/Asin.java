package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Asin extends TrigNode {
	public Asin(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Asin(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Sin)
			return ((UnaryNode) node).getNode();
		return new Asin(node);
	}
}
