package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Acos extends TrigNode {
	public Acos(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acos(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Cos)
			return ((UnaryNode) node).getNode();
		return new Acos(node);
	}
}
