package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Cos extends TrigNode {
	public Cos(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Cos(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Acos)
			return ((UnaryNode) node).getNode();
		return new Cos(node);
	}
}
