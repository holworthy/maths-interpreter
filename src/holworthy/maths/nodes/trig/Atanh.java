package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Atanh extends TrigNode {
	public Atanh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Atanh(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Tanh)
			return ((UnaryNode) node).getNode();
		return new Atanh(node);
	}
}
