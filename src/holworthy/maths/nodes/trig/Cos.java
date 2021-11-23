package holworthy.maths.nodes.trig;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;
import holworthy.maths.nodes.constant.Pi;

public class Cos extends TrigNode {
	public Cos(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Cos(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node.matches(new Number(0)))
			return new Number(1);
		if(node.matches(new Pi()))
			return new Negative(new Number(1));
		if(node instanceof Acos)
			return ((UnaryNode) node).getNode();
		if(node instanceof Negative)
			return new Cos(((UnaryNode) node).getNode());
		return new Cos(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(getNode().differentiate(wrt), new Negative(new Sin(getNode()))).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return Math.cos(getNode().evaluate(values));
	}
}
