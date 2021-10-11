package holworthy.maths.nodes.constant;

import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Variable;

public abstract class ConstantNode extends Node {
	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public Node differentiate(Variable wrt) {
		return new Number(0);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().toLowerCase();
	}
}
