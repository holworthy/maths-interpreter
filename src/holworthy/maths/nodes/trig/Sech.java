package holworthy.maths.nodes.trig;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Sech extends TrigNode {
	public Sech(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Sech(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node instanceof Asech)
			return ((UnaryNode) node).getNode();
		return new Sech(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(new Multiply(getNode().differentiate(wrt), new Tanh(getNode())), new Negative(new Sech(getNode()))).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return 1.0 / Math.cosh(getNode().evaluate(values));
	}
}
