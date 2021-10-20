package holworthy.maths.nodes;

import java.math.BigInteger;

public class Number extends Node {
	private final BigInteger value;

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
	public Node copy() {
		return this;
	}

	@Override
	public boolean matches(Node node) {
		return (node instanceof Number && ((Number) node).getValue().equals(getValue())) || super.matches(node);
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public boolean contains(Variable variable) {
		return false;
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}
}
