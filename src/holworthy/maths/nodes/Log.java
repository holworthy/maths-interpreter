package holworthy.maths.nodes;

public class Log extends FunctionNode {
	private Node base;

	public Log(Node node, Node base) {
		super(node);
		this.base = base;
	}

	public Node getBase() {
		return base;
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}
}
