package holworthy.maths.nodes.trig;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Add;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Acot extends TrigNode {
	public Acot(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acot(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node instanceof Cot)
			return ((UnaryNode) node).getNode();
		return new Acot(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Negative(new Divide(getNode().differentiate(wrt), new Add(new Power(getNode(), new Number(2)), new Number(1)))).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return Math.atan(1.0 / getNode().evaluate(values));
	}
}
