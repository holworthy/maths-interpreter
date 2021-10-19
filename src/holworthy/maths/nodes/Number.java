package holworthy.maths.nodes;

import java.math.BigInteger;

public class Number extends Node {
	private BigInteger value;

	public Number(BigInteger value) {
		this.value = value;
		if(value.compareTo(BigInteger.ZERO) < 0)
			throw new Error("Negative number");
	}

	public Number(int value){
		this(BigInteger.valueOf(value));
	}

	public BigInteger getValue() {
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
		return node instanceof Number && ((Number) node).getValue().equals(getValue());
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}
}
