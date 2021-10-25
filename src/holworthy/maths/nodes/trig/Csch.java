package holworthy.maths.nodes.trig;

import holworthy.maths.exceptions.DivideByZeroException;
import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

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

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(new Multiply(getNode().differentiate(wrt), new Negative(new Coth(getNode()))), new Csch(getNode())).simplify();
	}
}
