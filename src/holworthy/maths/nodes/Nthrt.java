package holworthy.maths.nodes;

import java.math.BigInteger;

import holworthy.maths.DivideByZeroException;

public class Nthrt extends FunctionNode {
	private int exponent;

	public Nthrt(Node node, int exponent) {
		super(node);
		this.exponent = exponent;
	}

	@Override
	public boolean isConstant() {
		return getNode().isConstant();
	}

	@Override
	public String toString() {
		return exponent+"rt(" + getNode() + ")";
	}

	@Override
	public Node expand() throws DivideByZeroException{
		Node node = getNode().expand();
		
		if(node instanceof Negative)
			throw new Error("negative in nth root idk what maths is");

		if(node instanceof Number) {
			// int n = ((Number) node).getValue();
			// int s = (int) Math.floor(Math.pow(n, 1f/exponent));
			// if(Math.pow(s, exponent) == n)
			// 	return new Number(s);
			BigInteger n = ((Number) node).getValue();
			if(n.pow(iroot(n, BigInteger.valueOf(exponent))).compareTo(n) == 0)
				return new Number(iroot(n, BigInteger.valueOf(exponent)));
				
			// TODO: fix this to work with larger numbers even tho TB of data
		}

		return new Nthrt(node, exponent);
	}

	public int iroot(BigInteger k, BigInteger n){
		BigInteger k1 = k.subtract(BigInteger.ONE);
		BigInteger s = n.add(BigInteger.ONE);
		BigInteger u = n;
		while(u.compareTo(s) < 0){
			s = u;
			u = ((u.multiply(k1)).add(n)).divide((u.multiply(k1))).divide(k);
		}
		return s.intValue();
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}
}