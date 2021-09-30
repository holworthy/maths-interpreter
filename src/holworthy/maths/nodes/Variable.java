package holworthy.maths.nodes;

public class Variable extends Node {
	private String name;

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
	public boolean matches(Node node) {
		return node instanceof Variable && ((Variable) node).getName().equals(getName());
	}

	@Override
	public boolean isConstant() {
		return false;
	}
}
