package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;

public class Asec extends TrigNode {
	public Asec(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Asec(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Sec)
			return ((UnaryNode) node).getNode();
		return new Asec(node);
	}
}
