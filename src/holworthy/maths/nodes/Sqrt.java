package holworthy.maths.nodes;

public class Sqrt extends UnaryNode {
	public Sqrt(Node node) {
		super(node);
	}

	@Override
	public boolean isConstant() {
		return getNode().isConstant();
	}

	@Override
	public String toString() {
		return "sqrt(" + getNode() + ")";
	}

	@Override
	public Node expand() {
		Node node = getNode().expand();

		if(node instanceof Negative)
			return new Multiply(new I(), new Sqrt(((Negative) node).getNode())).expand();

		if(node instanceof Number) {
			int n = ((Number) node).getValue();
			int s = (int) Math.floor(Math.sqrt(n));
			if(Math.pow(s, 2) == n)
				return new Number(s);
		}

		return new Sqrt(node);
	}
}
