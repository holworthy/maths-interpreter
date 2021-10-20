package holworthy.maths.nodes;

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
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}
}
