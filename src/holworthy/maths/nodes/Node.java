package holworthy.maths.nodes;

import holworthy.maths.exceptions.DivideByZeroException;
import holworthy.maths.exceptions.MathsInterpreterException;

public abstract class Node {
	public abstract Node copy();

	public boolean matches(Node node) {
		return node == this || node instanceof Matching.Anything || (node instanceof Matching.Constant && isConstant());
	}

	public abstract boolean isConstant();

	public abstract boolean contains(Variable variable);

	public Node normalise() {
		return this;
	}

	// adds common terms
	// expands brackets
	// moves negatives towards the leaves
	public Node expand() throws DivideByZeroException {
		return this;
	}

	// takes out common factors
	// moves negatives towards the root
	public Node collapse() throws DivideByZeroException {
		return this;
	}

	public final Node simplify() throws DivideByZeroException {
		return normalise().expand().normalise().collapse().normalise();
	}

	public abstract Node differentiate(Variable wrt) throws MathsInterpreterException;
}
