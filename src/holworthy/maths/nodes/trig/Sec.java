package holworthy.maths.nodes.trig;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Sec extends TrigNode {
	public Sec(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Sec(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node instanceof Asec)
			return ((UnaryNode) node).getNode();
		return new Sec(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(new Multiply(getNode().differentiate(wrt), new Tan(getNode())), new Sec(getNode())).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return 1.0 / Math.cos(getNode().evaluate(values));
	}
}
