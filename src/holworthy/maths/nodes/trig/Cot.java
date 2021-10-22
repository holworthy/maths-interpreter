package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Cot extends TrigNode {
	public Cot(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Cot(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Acot)
			return ((UnaryNode) node).getNode();
		return new Cot(node);
	}
}
