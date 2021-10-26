package holworthy.maths.nodes.trig;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.BinaryNode;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Multiply;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;
import holworthy.maths.nodes.constant.Pi;

public class Sin extends TrigNode {
	public Sin(Node node) {
		super(node);
	}

	@Override
	public Node copy() {
		return new Sin(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();

		if(node.matches(new Number(0)))
			return new Number(0);
		if(node.matches(new Divide(new Pi(), new Number(6))))
			return new Divide(new Number(1), new Number(2));
		if(node.matches(new Divide(new Pi(), new Number(2))))
			return new Number(1);
		if(node.matches(new Divide(new Multiply(new Number(5), new Pi()), new Number(6))))
			return new Divide(new Number(1), new Number(2));
		if(node.matches(new Pi()))
			return new Number(0);
		if(node.matches(new Divide(new Multiply(new Number(7), new Pi()), new Number(6))))
			return new Negative(new Divide(new Number(1), new Number(2)));
		if(node.matches(new Divide(new Multiply(new Number(3), new Pi()), new Number(2))))
			return new Negative(new Number(1));
		if(node.matches(new Divide(new Multiply(new Number(11), new Pi()), new Number(6))))
			return new Negative(new Divide(new Number(1), new Number(2)));
		if(node.matches(new Multiply(new Number(2), new Pi())))
			return new Number(0);
		if(node instanceof Multiply && ((BinaryNode) node).getLeft() instanceof Number && ((BinaryNode) node).getRight() instanceof Pi)
			return new Number(0);
		
		if(node instanceof Asin)
			return ((UnaryNode) node).getNode();

		return new Sin(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Multiply(getNode().differentiate(wrt), new Cos(getNode())).simplify();
	}
}
