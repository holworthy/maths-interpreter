package holworthy.maths.nodes;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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

	public static ArrayList<BigInteger> primeFactors(BigInteger number){
		BigInteger n = number;
		ArrayList<BigInteger> factors = new ArrayList<>();
		if(!(n.isProbablePrime(100))){
			for(BigInteger i = BigInteger.TWO; i.compareTo(n) <= 0; i = i.add(BigInteger.ONE)){
				while(n.mod(i).equals(BigInteger.ZERO)){
					factors.add(i);
					n = n.divide(i);
				}
			}
		}
		else
			factors.add(n);
		return factors;
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
			else{
				ArrayList<BigInteger> roots = primeFactors(n);
				if(roots.size() > 1){
					ArrayList<BigInteger> removed = new ArrayList<>();
					ArrayList<BigInteger> checked = new ArrayList<>();
					for (int i = 0; i < roots.size(); i++){
						if (!(checked.contains(roots.get(i))) && BigInteger.valueOf(Collections.frequency(roots, roots.get(i))).compareTo(((Number) degree).getValue()) >= 0){
							removed.add(roots.get(i));
							checked.add(roots.get(i));
						}
					}
					BigInteger removeTotal = BigInteger.ONE;
					for (int i = 0; i < removed.size(); i++){
						for (BigInteger j = ((Number) degree).getValue(); j.compareTo(BigInteger.ZERO) > 0; j = j.subtract(BigInteger.ONE)){
							roots.remove(removed.get(i));
						}
						removeTotal = removeTotal.multiply(removed.get(i));
					}
					BigInteger rootTotal = BigInteger.ONE;
					for (int i = 0; i < roots.size(); i++){
						rootTotal = rootTotal.multiply(roots.get(i));
					}
					if(removeTotal.compareTo(BigInteger.ONE) != 0 && rootTotal.compareTo(BigInteger.ONE) != 0){
						return new Multiply(new Number(removeTotal), new Nthrt(new Number(rootTotal), degree)).simplify();
					}
				}
			}
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
		if(getNode().isConstant())
			return new Number(0);
		return new Divide(new Multiply(new Power(getNode(), new Subtract(new Divide(new Number(1), degree), new Number(1))), getNode().differentiate(wrt)), degree).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return Math.pow(getNode().evaluate(values), 1.0 / degree.evaluate(values));
	}

	// public static void main(String[] args) throws MathsInterpreterException {
	// 	// System.out.println(new Nthrt(new Number(5), new Number(2)).simplify());
	// 	// System.out.println(primeFactors(BigInteger.valueOf(1212121217)));
	// 	// System.out.println(new Nthrt(new Number(4), new Number(2)).simplify());
	// 	System.out.println(new Multiply(new I(), new Nthrt(new Number(8), new Number(2))).simplify());
	// }
}