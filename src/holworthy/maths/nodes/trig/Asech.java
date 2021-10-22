package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Asech extends TrigNode {
	public Asech(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Asech(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Sech)
			return ((UnaryNode) node).getNode();
		return new Asech(node);
	}
}
