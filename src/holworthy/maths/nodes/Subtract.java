package holworthy.maths.nodes;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;

public class Subtract extends BinaryNode {
	public Subtract(Node left, Node right) {
		super(left, right);
	}

	@Override
	public boolean matches(Node node) {
		return node instanceof Matching.AddOrSubtract || super.matches(node);
	}

	@Override
	public Node copy() {
		return new Subtract(getLeft().copy(), getRight().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException{
		return new Add(getLeft(), new Negative(getRight())).expand();
	}

	@Override
	public String toString() {
		return getLeft() + " - " + getRight();
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Subtract(getLeft().differentiate(wrt), getRight().differentiate(wrt)).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return getLeft().evaluate(values) - getRight().evaluate(values);
	}

	@Override
	public Node replace(Node before, Node after) {
		if(matches(before))
			return after;
		return new Subtract(getLeft().replace(before, after), getRight().replace(before, after));
	}
}
