package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Atan extends TrigNode {
	public Atan(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Atan(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Tan)
			return ((UnaryNode) node).getNode();
		return new Atan(node);
	}
}
