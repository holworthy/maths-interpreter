package holworthy.maths.nodes.trig;

import holworthy.maths.exceptions.DivideByZeroException;
import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

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

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(getNode().differentiate(wrt), new Negative(new Power(new Csch(getNode()), new Number(2)))).simplify();
	}
}
