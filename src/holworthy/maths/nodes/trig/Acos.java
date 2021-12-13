package holworthy.maths.nodes.trig;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.Sqrt;
import holworthy.maths.nodes.Subtract;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Acos extends TrigNode {
	public Acos(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acos(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node instanceof Cos)
			return ((UnaryNode) node).getNode();
		return new Acos(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Negative(new Divide(getNode().differentiate(wrt), new Sqrt(new Subtract(new Number(1), new Power(getNode(), new Number(2)))))).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return Math.acos(getNode().evaluate(values));
	}

	@Override
	public Node replace(Node before, Node after) {
		if(matches(before))
			return after;
		return new Acos(getNode().replace(before, after));
	}
}
