package holworthy.maths.nodes.trig;

import holworthy.maths.exceptions.DivideByZeroException;
import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Power;
import holworthy.maths.nodes.Sqrt;
import holworthy.maths.nodes.Subtract;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Asec extends TrigNode {
	public Asec(Node arg) {
		super(arg);
	}

	@Override
	public Node copy() {
		return new Asec(getNode().copy());
	}

	@Override
	public Node expand() throws DivideByZeroException {
		Node node = getNode().expand();
		if(node instanceof Sec)
			return ((UnaryNode) node).getNode();
		return new Asec(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Divide(getNode().differentiate(wrt), new Multiply(new Sqrt(new Subtract(new Number(1), new Divide(new Number(1), new Power(getNode(), new Number(2))))), new Power(getNode(), new Number(2)))).simplify();
	}
}
