package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Sec extends TrigNode {
	public Sec(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Sec(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Asec)
			return ((UnaryNode) node).getNode();
		return new Sec(node);
	}
}
