package holworthy.maths.nodes.trig;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Tan extends TrigNode {
	public Tan(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Tan(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node instanceof Atan)
			return ((UnaryNode) node).getNode();
		return new Tan(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(getNode().differentiate(wrt), new Power(new Sec(getNode()), new Number(2))).simplify();
	}
}
