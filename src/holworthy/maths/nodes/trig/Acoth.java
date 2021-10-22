package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Acoth extends TrigNode {
	public Acoth(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acoth(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Coth)
			return ((UnaryNode) node).getNode();
		return new Acoth(node);
	}
}
