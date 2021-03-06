package holworthy.maths.nodes.trig;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Tanh extends TrigNode {
	public Tanh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Tanh(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node instanceof Atanh)
			return ((UnaryNode) node).getNode();
		return new Tanh(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(getNode().differentiate(wrt), new Power(new Sech(getNode()), new Number(2))).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return Math.tanh(getNode().evaluate(values));
	}

	@Override
	public Node replace(Node before, Node after) {
		if(matches(before))
			return after;
		return new Tanh(getNode().replace(before, after));
	}
}
