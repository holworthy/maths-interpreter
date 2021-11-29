package holworthy.maths.nodes;

import java.util.ArrayList;
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

	public ArrayList<Variable> getVariables() {
		ArrayList<Variable> variables = new ArrayList<>();

		if(this instanceof Variable) {
			if(!variables.contains((Variable) this))
				variables.add((Variable) this);
		} else if(this instanceof UnaryNode) {
			for(Variable variable : ((UnaryNode) this).getNode().getVariables())
				if(!variables.contains(variable))
					variables.add(variable);
			if(this instanceof Log){
				for(Variable variable : ((Log) this).getBase().getVariables())
					if(!variables.contains(variable))
						variables.add(variable);
			}
		} else if(this instanceof BinaryNode) {
			for(Variable variable : ((BinaryNode) this).getLeft().getVariables())
				if(!variables.contains(variable))
					variables.add(variable);
			for(Variable variable : ((BinaryNode) this).getRight().getVariables())
				if(!variables.contains(variable))
						variables.add(variable);
		}

		return variables;
	}

	public int numVariables() {
		return getVariables().size();
	}

	public abstract double evaluate(HashMap<Variable, Node> values);

	public ArrayList<Node> otherForms() throws MathsInterpreterException {
		return new ArrayList<>();
	}

	public String getName() {
		return getClass().getSimpleName().toLowerCase();
	}
}
