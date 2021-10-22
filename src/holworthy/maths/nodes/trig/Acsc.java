package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Acsc extends TrigNode {
	public Acsc(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acsc(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Csc)
			return ((UnaryNode) node).getNode();
		return new Acsc(node);
	}
}
