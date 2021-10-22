package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Coth extends TrigNode {
	public Coth(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Coth(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Acoth)
			return ((UnaryNode) node).getNode();
		return new Coth(node);
	}
}
