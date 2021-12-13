package holworthy.maths.nodes.trig;

import java.util.HashMap;

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
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node instanceof Acosh)
			return ((UnaryNode) node).getNode();
		return new Cosh(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(getNode().differentiate(wrt), new Sinh(getNode())).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return Math.cosh(getNode().evaluate(values));
	}

	@Override
	public Node replace(Node before, Node after) {
		if(matches(before))
			return after;
		return new Cosh(getNode().replace(before, after));
	}
}
