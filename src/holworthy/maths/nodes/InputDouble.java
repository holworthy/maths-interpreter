package holworthy.maths.nodes;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;

public class InputDouble extends Node {
	private double value;

	public InputDouble(double value) {
		this.value = value;
	}

	@Override
	public Node copy() {
		return this;
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
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Number(0);
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return value;
	}
}