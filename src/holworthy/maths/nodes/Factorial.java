package holworthy.maths.nodes;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.exceptions.NotDifferentiableException;

public class Factorial extends UnaryNode {
	public Factorial(Node node) {
		super(node);
	}

	@Override
	public Node copy() {
		return new Factorial(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node.matches(new Number(0)) || node.matches(new Number(1)))
			return new Number(1);
		if(node instanceof Number)
			return new Multiply(node, new Factorial(new Subtract(node, new Number(1)))).expand();
		return new Factorial(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		Node node = simplify();
		if(node instanceof Factorial)
			throw new NotDifferentiableException("Cannot differentiate a non-constant factorial");
		
		return node.differentiate(wrt);
	}

	@Override
	public String toString() {
		return "(" + getNode() + ")!";
	}
}
