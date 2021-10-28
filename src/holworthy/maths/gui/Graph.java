package holworthy.maths.gui;

import java.util.HashMap;

import holworthy.maths.Maths;
import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.BinaryNode;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.UnaryNode;

public class Graph {
	private HashMap<Double, Double> values = new HashMap<>();
	private String equation;

	Graph(String equation){
		this.equation = equation;
	}

	public HashMap<Double, Double> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return values.toString();
	}

	// Assumes y = equation
	public void findValues(int upperBound, int lowerBound) throws Exception{
		for (int x = lowerBound; x < upperBound + 1; x++){
			String replaced = equation.replaceAll("x", "("+Integer.toString(x)+")");
			Node answer = Maths.parseInput(replaced).simplify();
			if(answer instanceof Number){
				values.put((double) x,  ((Number) answer).getValue().doubleValue());
			}
			else if(answer instanceof Negative && ((UnaryNode) answer).getNode() instanceof Number){
				values.put((double) x,  -((Number) ((UnaryNode) answer).getNode()).getValue().doubleValue());
			}
			else if(answer instanceof Divide && ((BinaryNode) answer).getLeft() instanceof Number && ((BinaryNode) answer).getRight() instanceof Number){
				Double value = ((Number) ((BinaryNode) answer).getLeft()).getValue().doubleValue() / ((Number) ((BinaryNode) answer).getRight()).getValue().doubleValue();
				values.put((double) x, value);
			}
			else if(answer instanceof Negative && ((UnaryNode) answer).getNode() instanceof Divide && ((BinaryNode) ((UnaryNode) answer).getNode()).getLeft() instanceof Number && ((BinaryNode) ((UnaryNode) answer).getNode()).getRight() instanceof Number){
				Double value = -((Number) ((BinaryNode) ((UnaryNode) answer).getNode()).getLeft()).getValue().doubleValue() / ((Number) ((BinaryNode) ((UnaryNode) answer).getNode()).getRight()).getValue().doubleValue();
				values.put((double) x, value);
			}
			else{
				throw new MathsInterpreterException("Something is very wrong with this equation.");
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Graph graph = new Graph("2*x^2+x");
		graph.findValues(10, -10);
		System.out.println(graph);
	}
}
