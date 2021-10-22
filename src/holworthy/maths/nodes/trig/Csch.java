package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Csch extends TrigNode {
	public Csch(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Csch(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Acsch)
			return ((UnaryNode) node).getNode();
		return new Csch(node);
	}
}
