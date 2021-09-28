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
}
