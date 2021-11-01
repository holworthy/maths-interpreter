package holworthy.maths.nodes.trig;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.Subtract;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Atanh extends TrigNode {
	public Atanh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Atanh(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node instanceof Tanh)
			return ((UnaryNode) node).getNode();
		return new Atanh(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Divide(getNode().differentiate(wrt), new Subtract(new Number(1), new Power(getNode(), new Number(2)))).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		double x = getNode().evaluate(values);
		return 0.5 * Math.log(x + 1) - 0.5 * Math.log(1 - x);
	}
}
