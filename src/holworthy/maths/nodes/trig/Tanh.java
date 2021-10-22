package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Tanh extends TrigNode {
	public Tanh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Tanh(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Atanh)
			return ((UnaryNode) node).getNode();
		return new Tanh(node);
	}
}
