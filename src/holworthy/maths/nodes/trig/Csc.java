package holworthy.maths.nodes.trig;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Csc extends TrigNode {
	public Csc(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Csc(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node instanceof Acsc)
			return ((UnaryNode) node).getNode();
		return new Csc(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(new Multiply(getNode().differentiate(wrt), new Negative(new Cot(getNode()))), new Csc(getNode())).simplify();
	}
}
