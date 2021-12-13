package holworthy.maths.nodes;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.constant.ConstantNode;

public class Negative extends UnaryNode {
	public Negative(Node node) {
		super(node);
	}

	@Override
	public String toString() {
		if(getNode() instanceof Number || getNode() instanceof ConstantNode || getNode() instanceof Variable)
			return "-" + getNode();
		return "-(" + getNode() + ")";
	}

	@Override
	public Node copy() {
		return new Negative(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException{
		Node node = getNode().expand();
		if(node.matches(new Number(0)))
			return new Number(0);
		if(node instanceof Negative)
			return ((Negative) node).getNode();
		if(node instanceof Add)
			return new Add(new Negative(((BinaryNode) node).getLeft()), new Negative(((BinaryNode) node).getRight())).expand();
		if(node instanceof Multiply)
			return new Multiply(new Negative(((BinaryNode) node).getLeft()), ((BinaryNode) node).getRight()).expand();
		return new Negative(node);
	}

	@Override
	public Node collapse() throws MathsInterpreterException {
		Node node = getNode().collapse();
		if(node instanceof Negative)
			return ((UnaryNode) node).getNode();
		return new Negative(node);
	}

	@Override
	public boolean matches(Node node) {
		if(node instanceof Negative)
			return getNode().matches(((Negative) node).getNode());
		return super.matches(node);
	}

	@Override
	public boolean isConstant() {
		return getNode().isConstant();
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Negative(getNode().differentiate(wrt)).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return -getNode().evaluate(values);
	}

	@Override
	public Node replace(Node before, Node after) {
		if(matches(before))
			return after;
		return new Negative(getNode().replace(before, after));
	}
}
