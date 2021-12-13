package holworthy.maths.nodes.trig;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Add;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.Sqrt;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Asinh extends TrigNode {
	public Asinh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Asinh(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node instanceof Sinh)
			return ((UnaryNode) node).getNode();
		return new Asinh(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Divide(getNode().differentiate(wrt), new Sqrt(new Add(new Power(getNode(), new Number(2)), new Number(1)))).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		double x = getNode().evaluate(values);
		return Math.log(x + Math.sqrt(x * x + 1));
	}

	@Override
	public Node replace(Node before, Node after) {
		if(matches(before))
			return after;
		return new Asinh(getNode().replace(before, after));
	}
}
