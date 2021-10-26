package holworthy.maths.nodes.trig;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Cot extends TrigNode {
	public Cot(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Cot(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node instanceof Acot)
			return ((UnaryNode) node).getNode();
		return new Cot(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(getNode().differentiate(wrt), new Negative(new Power(new Csc(getNode()), new Number(2)))).simplify();
	}
}
