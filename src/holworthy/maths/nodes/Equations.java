package holworthy.maths.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.exceptions.NotDifferentiableException;

public class Equations extends Node {
	private ArrayList<Node> equations = new ArrayList<>();

	public ArrayList<Node> getEquations() {
		return equations;
	}

	public void addEquation(Node equation) {
		equations.add(equation);
	}

	@Override
	public Node copy() {
		return null;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean contains(Variable variable) {
		return false;
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		throw new NotDifferentiableException("Cannot differentiate multiple equations");
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return 0;
	}

	@Override
	public Node replace(Node before, Node after) {
		return null;
	}

	@Override
	public String toString() {
		return String.join(", ", equations.stream().map(node -> node.toString()).toArray(String[]::new));
	}

	public Equations replaceVariables() throws MathsInterpreterException{
		HashMap<Node,Node> values = new HashMap<>();
		Equations replacedEquations = new Equations();
		for(Node e : equations){
			if (e instanceof Equation && ((BinaryNode) e).getLeft() instanceof Variable && !(values.containsKey(((BinaryNode) e).getLeft()))){
				Node value = ((BinaryNode) e).getRight();
				for (var entry : values.entrySet()){
					value = value.replace(entry.getKey(), entry.getValue());
				}
				values.put(((BinaryNode) e).getLeft(), value);
			}
			else{
				Node n = e;
				for (Entry<Node, Node> entry : values.entrySet()){
					n = n.replace(entry.getKey(), entry.getValue());
				}
				replacedEquations.addEquation(n);
			}
			
		}
		return replacedEquations;
	}
}
