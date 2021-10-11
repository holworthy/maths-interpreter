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
	public Node expand() {
		Node node = getNode().expand();
		Node base = getBase().expand();

		if(node.matches(base))
			return new Number(1);
		if(node instanceof Power && ((BinaryNode) node).getLeft().matches(base))
			return ((BinaryNode) node).getRight();

		return new Log(node, base);
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}
}
