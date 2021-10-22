package holworthy.maths.nodes;

import java.math.BigInteger;

import holworthy.maths.DivideByZeroException;

public class Nthrt extends FunctionNode {
	private BigInteger exponent;

	public Nthrt(Node node, BigInteger exponent) {
		super(node);
		this.exponent = exponent;
	}

	@Override
	public String toString() {
		return "nthrt(" + getNode() + ", " + exponent + ")";
	}

	@Override
	public Node copy() {
		return new Nthrt(getNode().copy(), exponent);
	}

	@Override
	public boolean isConstant() {
		return getNode().isConstant();
	}

	@Override
	public Node expand() throws DivideByZeroException{
		Node node = getNode().expand();
		
		if(node instanceof Negative)
			throw new Error("negative in nth root idk what maths is");

		if(node instanceof Number && exponent.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) < 0) {
			BigInteger n = ((Number) node).getValue();
			if(iroot(exponent, n).pow(exponent.intValue()).compareTo(n) == 0)
				return new Number(iroot(exponent, n));
		}

		return new Nthrt(node, exponent);
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
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}

	// public static void main(String[] args) throws DivideByZeroException {
	// 	System.out.println(new Nthrt(new Number(4), BigInteger.valueOf(2)).simplify());
	// }
}