package holworthy.maths.nodes;

import java.math.BigInteger;

import holworthy.maths.exceptions.MathsInterpreterException;

public class Nthrt extends FunctionNode {
	private Node degree;

	public Nthrt(Node node, Node degree) {
		super(node);
		this.degree = degree;
	}

	public Node getDegree() {
		return degree;
	}

	@Override
	public String toString() {
		return "nthrt(" + getNode() + ", " + degree + ")";
	}

	@Override
	public Node copy() {
		return new Nthrt(getNode().copy(), degree);
	}

	@Override
	public boolean isConstant() {
		return getNode().isConstant();
	}

	public BigInteger iroot(BigInteger k, BigInteger n){
		BigInteger k1 = k.subtract(BigInteger.ONE);
		BigInteger s = n.add(BigInteger.ONE);
		BigInteger u = n;
		while(u.compareTo(s) < 0){
			s = u;
			u = (u.multiply(k1).add(n.divide(u.pow(k1.intValue())))).divide(k);
		}
		return s;
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node node = getNode().expand();
		Node degree = getDegree().expand();

		if(degree.matches(new Number(0)))
			throw new MathsInterpreterException("Cannot have a 0th root");

		if(node instanceof Negative && !(((UnaryNode) node).getNode().matches(new Number(1)))){
			return new Multiply(new Nthrt(((UnaryNode) node).getNode(), degree), new Nthrt(new Negative(new Number(1)), degree)).expand();
		}
		
		// nth root where n is 1 does nothing
		if(degree.matches(new Number(1)))
			return node;

		if(node instanceof Number && degree instanceof Number && ((Number) degree).getValue().compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) < 0) {
			BigInteger exponent = ((Number) degree).getValue();
			BigInteger n = ((Number) node).getValue();
			if(iroot(exponent, n).pow(exponent.intValue()).compareTo(n) == 0)
				return new Number(iroot(exponent, n));
		}

		return new Nthrt(node, degree);
	}
	
	@Override
	public Node collapse() throws MathsInterpreterException {
		Node node = getNode().collapse();
		if(degree.matches(new Number(2)))
			return new Sqrt(node);
		return new Nthrt(node, degree);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Divide(new Multiply(new Power(getNode(), new Subtract(new Divide(new Number(1), degree), new Number(1))), getNode().differentiate(wrt)), degree).simplify();
	}

	// public static void main(String[] args) throws MathsInterpreterException {
	// 	// System.out.println(new Nthrt(new Number(5), new Number(2)).simplify());
	// }
}