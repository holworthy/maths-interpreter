package holworthy.maths.nodes;

public class Number extends Node {
	private int value;

	public Number(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value + "";
	}

	@Override
	public boolean matches(Node node) {
		if(node instanceof Matching.Constant)
			return isConstant();
		return node instanceof Number && ((Number) node).getValue() == getValue();
	}

	@Override
	public boolean isConstant() {
		return true;
	}
}
