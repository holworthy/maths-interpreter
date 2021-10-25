package holworthy.maths.nodes.trig;

import holworthy.maths.exceptions.DivideByZeroException;
import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Cosh extends TrigNode {
	public Cosh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Cosh(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Acosh)
			return ((UnaryNode) node).getNode();
		return new Cosh(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(getNode().differentiate(wrt), new Sinh(getNode())).simplify();
	}
}
