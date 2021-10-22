package holworthy.maths.nodes;

public abstract class FunctionNode extends UnaryNode {
	public FunctionNode(Node node) {
		super(node);
	}

	public String getName() {
		return getClass().getSimpleName().toLowerCase();
	}
}
