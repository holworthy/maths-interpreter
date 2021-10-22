package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Cosh extends TrigNode {
	public Cosh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Cosh(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Acosh)
			return ((UnaryNode) node).getNode();
		return new Cosh(node);
	}
}
