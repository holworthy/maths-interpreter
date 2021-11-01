package holworthy.maths.nodes.trig;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Sinh extends TrigNode {
	public Sinh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Sinh(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node instanceof Asinh)
			return ((UnaryNode) node).getNode();
		return new Sinh(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(getNode().differentiate(wrt), new Cosh(getNode())).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return Math.sinh(getNode().evaluate(values));
	}
}
