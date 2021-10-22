package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Acsch extends TrigNode {
	public Acsch(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acsch(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Csch)
			return ((UnaryNode) node).getNode();
		return new Acsch(node);
	}
}
