package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Acosh extends TrigNode {
	public Acosh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acosh(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Cosh)
			return ((UnaryNode) node).getNode();
		return new Acosh(node);
	}
}
