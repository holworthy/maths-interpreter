package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Csc extends TrigNode {
	public Csc(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Csc(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Acsc)
			return ((UnaryNode) node).getNode();
		return new Csc(node);
	}
}
