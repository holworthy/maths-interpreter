package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Sech extends TrigNode {
	public Sech(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Sech(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Asech)
			return ((UnaryNode) node).getNode();
		return new Sech(node);
	}
}
