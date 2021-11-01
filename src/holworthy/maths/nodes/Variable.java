package holworthy.maths.nodes;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;

public class Variable extends Node {
	private final String name;

	public Variable(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Variable && name.equals(((Variable) obj).getName());
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public Node copy() {
		return this;
	}

	@Override
	public boolean matches(Node node) {
		return (node instanceof Variable && ((Variable) node).getName().equals(getName())) || super.matches(node);
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean contains(Variable variable) {
		return matches(variable);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return matches(wrt) ? new Number(1) : new Number(0);
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return values.get(this).evaluate(values);
	}
}
