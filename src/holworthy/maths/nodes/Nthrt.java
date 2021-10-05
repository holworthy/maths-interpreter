package holworthy.maths.nodes;

public class Nthrt extends UnaryNode {
	private int exponent;

	public Nthrt(Node node, int exponent) {
		super(node);
		this.exponent = exponent;
	}

	@Override
	public boolean isConstant() {
		return getNode().isConstant();
	}

	@Override
	public String toString() {
		return exponent+"rt(" + getNode() + ")";
	}

	@Override
	public Node expand() {
		Node node = getNode().expand();
		

		if(node instanceof Negative)
			throw new Error("negative in nth root idk what maths is");

		if(node instanceof Number) {
			int n = ((Number) node).getValue();
			int s = (int) Math.floor(Math.pow(n, 1f/exponent));
			if(Math.pow(s, exponent) == n)
				return new Number(s);
		}

		return new Nthrt(node, exponent);
	}
}