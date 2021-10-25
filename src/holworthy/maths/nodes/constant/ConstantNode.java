package holworthy.maths.nodes.constant;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Variable;

public abstract class ConstantNode extends Node {
	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Number(0);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().toLowerCase();
	}

	@Override
	public boolean matches(Node node) {
		return node.getClass() == getClass() || super.matches(node);
	}

	@Override
	public Node copy() {
		return this;
	}

	@Override
	public boolean contains(Variable variable) {
		return false;
	}
}
