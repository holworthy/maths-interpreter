package holworthy.maths.nodes;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;

public abstract class Node {
	public abstract Node copy();

	public boolean matches(Node node) {
		return node == this || node instanceof Matching.Anything || (node instanceof Matching.Constant && isConstant());
	}

	public abstract boolean isConstant();

	public abstract boolean contains(Variable variable);

	// adds common terms
	// expands brackets
	// moves negatives towards the leaves
	public Node expand() throws MathsInterpreterException {
		return this;
	}

	// takes out common factors
	// moves negatives towards the root
	public Node collapse() throws MathsInterpreterException {
		return this;
	}

	public final Node simplify() throws MathsInterpreterException {
		return expand().collapse();
	}

	public abstract Node differentiate(Variable wrt) throws MathsInterpreterException;
	public Node differentiate(String wrt) throws MathsInterpreterException {
		return differentiate(new Variable(wrt));
	}

	public abstract double evaluate(HashMap<Variable, Node> values);
}
