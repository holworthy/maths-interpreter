package holworthy.maths.nodes.trig;

import java.util.ArrayList;
import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;
import holworthy.maths.nodes.constant.Pi;

public class Tan extends TrigNode {
	public Tan(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Tan(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node.matches(new Number(0)))
			return new Number(0);
		if(node.matches(new Pi()))
			return new Number(0);
		if(node instanceof Atan)
			return ((UnaryNode) node).getNode();
		return new Tan(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(getNode().differentiate(wrt), new Power(new Sec(getNode()), new Number(2))).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return Math.tan(getNode().evaluate(values));
	}

	@Override
	public ArrayList<Node> otherForms() throws MathsInterpreterException {
		ArrayList<Node> otherForms = super.otherForms();
		otherForms.add(new Divide((new Sin(getNode())).simplify(), (new Cos(getNode())).simplify()));
		return otherForms;
	}
}
