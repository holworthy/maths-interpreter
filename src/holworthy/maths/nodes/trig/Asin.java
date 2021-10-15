package holworthy.maths.nodes.trig;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.Node;

public class Asin extends TrigNode {
	public Asin(Node arg) {
		super(arg);
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		return new Asin(node);
	}
}
