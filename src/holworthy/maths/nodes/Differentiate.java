package holworthy.maths.nodes;

import holworthy.maths.exceptions.MathsInterpreterException;

public class Differentiate extends FunctionNode {
	private Variable wrt;

	public Differentiate(Node node, Variable wrt) {
		super(node);
		this.wrt = wrt;
	}

	public Variable getWrt() {
		return wrt;
	}

	@Override
	public boolean isConstant() {
		try {
			return getNode().differentiate(wrt).isConstant();
		} catch(MathsInterpreterException e) {
			return false;
		}
	}

	@Override
	public Node copy() {
		return new Differentiate(getNode().copy(), (Variable) getWrt().copy());
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		// TODO: implement
		return null;
	}
}
