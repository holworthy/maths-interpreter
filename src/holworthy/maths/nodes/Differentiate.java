package holworthy.maths.nodes;

public class Differentiate extends FunctionNode {
	private Variable wrt;

	public Differentiate(Node node, Variable wrt) {
		super(node);
		this.wrt = wrt;
	}

	@Override
	public boolean isConstant() {
		return getNode().differentiate(wrt).isConstant();
	}

	@Override
	public Node differentiate(Variable wrt) {
		return null;
	}
}
