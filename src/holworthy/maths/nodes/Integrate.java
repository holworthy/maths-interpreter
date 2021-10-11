package holworthy.maths.nodes;

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
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}
}
