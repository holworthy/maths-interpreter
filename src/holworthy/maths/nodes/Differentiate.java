package holworthy.maths.nodes;

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
		return getNode().differentiate(wrt).isConstant();
	}

	@Override
	public Node copy() {
		return new Differentiate(getNode().copy(), (Variable) getWrt().copy());
	}

	@Override
	public Node differentiate(Variable wrt) {
		return null;
	}
}
