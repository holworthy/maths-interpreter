package holworthy.maths.nodes;

import holworthy.maths.exceptions.MathsInterpreterException;

public class Integrate extends FunctionNode {
	// TODO: add stuff to this
	public Integrate(Node node) {
		super(node);
	}

	@Override
	public boolean isConstant() {
		return false; // TODO: verify this
	}

	@Override
	public Node copy() {
		return new Integrate(getNode().copy());
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		// TODO: implement
		return null;
	}
}
