package holworthy.maths.nodes.trig;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Add;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.Sqrt;
import holworthy.maths.nodes.Subtract;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Asech extends TrigNode {
	public Asech(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Asech(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node instanceof Sech)
			return ((UnaryNode) node).getNode();
		return new Asech(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Negative(new Divide(getNode().differentiate(wrt), new Multiply(new Multiply(new Sqrt(new Subtract(new Divide(new Number(1), getNode()), new Number(1))), new Sqrt(new Add(new Divide(new Number(1), getNode()), new Number(1)))), new Power(getNode(), new Number(2)))));
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		double x = 1.0 / getNode().evaluate(values);
		return Math.log(x + Math.sqrt(x * x - 1));
	}

	@Override
	public Node replace(Node before, Node after) {
		if(matches(before))
			return after;
		return new Asech(getNode().replace(before, after));
	}
}
