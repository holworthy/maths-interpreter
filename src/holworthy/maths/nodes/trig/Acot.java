package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Acot extends TrigNode {
	public Acot(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acot(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Cot)
			return ((UnaryNode) node).getNode();
		return new Acot(node);
	}
}
