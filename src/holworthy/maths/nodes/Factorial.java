package holworthy.maths.nodes;

import java.math.BigInteger;
import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.exceptions.NotDifferentiableException;

public class Factorial extends FunctionNode {
	public Factorial(Node node) {
		super(node);
	}

	@Override
	public Node copy() {
		return new Factorial(getNode().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		if(node.matches(new Number(0)) || node.matches(new Number(1)))
			return new Number(1);
		if(node instanceof Number) {
			BigInteger sum = BigInteger.ONE;
			for(BigInteger i = BigInteger.TWO; i.compareTo(((Number) node).getValue()) <= 0; i = i.add(BigInteger.ONE))
				sum = sum.multiply(i);
			return new Number(sum);
		}
		return new Factorial(node);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		Node node = simplify();
		if(node instanceof Factorial)
			throw new NotDifferentiableException("Cannot differentiate a non-constant factorial");
		
		return node.differentiate(wrt);
	}

	@Override
	public String toString() {
		return "(" + getNode() + ")!";
	}

	public double factorial(double value){
		int v = (int) value;
		if (v == 0)
			return 1.0;
		double f = 1;
		for (int i = 1; i <= v; i++){
			f *= i;
		}
		return f;
	}

	// gamma function via lanczos approximation
	public double gamma(double value){
		double[] p = {0.99999999999980993, 676.5203681218851, -1259.1392167224028,
						771.32342877765313, -176.61502916214059, 12.507343278686905,
						-0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7};
		int g = 7;
		if (value < 0.5)
			return Math.PI / (Math.sin(Math.PI * value) * gamma(1 - value));

		value -= 1;
		double a = p[0];
		double t = value+g+0.5;
		for (int i = 1; i < p.length; i++){
			a += p[i]/(value+i);
		}

		return Math.sqrt(2*Math.PI)*Math.pow(t, value+0.5)*Math.exp(-t)*a;
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		double value = getNode().evaluate(values);
		if ((int) value == value && value >= 0)
			return factorial(value);
		
		return gamma(value);
	}
}
