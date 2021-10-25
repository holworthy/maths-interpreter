package holworthy.maths.nodes.trig;

import holworthy.maths.exceptions.DivideByZeroException;
import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Add;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Sqrt;
import holworthy.maths.nodes.Subtract;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Acosh extends TrigNode {
	public Acosh(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Acosh(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Cosh)
			return ((UnaryNode) node).getNode();
		return new Acosh(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Divide(getNode().differentiate(wrt), new Multiply(new Sqrt(new Subtract(getNode(), new Number(1))), new Sqrt(new Add(getNode(), new Number(1))))).simplify();
	}
}
