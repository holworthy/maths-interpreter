package holworthy.maths.nodes.trig;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Add;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Atan extends TrigNode {
	public Atan(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Atan(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node instanceof Tan)
			return ((UnaryNode) node).getNode();
		return new Atan(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Divide(getNode().differentiate(wrt), new Add(new Power(getNode(), new Number(2)), new Number(1))).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return Math.atan(getNode().evaluate(values));
	}
}
