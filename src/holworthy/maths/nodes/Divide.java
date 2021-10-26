package holworthy.maths.nodes;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

import holworthy.maths.exceptions.DivideByZeroException;
import holworthy.maths.exceptions.MathsInterpreterException;

public class Divide extends BinaryNode {
	public Divide(Node left, Node right) {
		super(left, right);
	}

	@Override
	public boolean matches(Node node) {
		if(node instanceof Divide)
			return super.matches(node);
		return super.matches(node);
	}

	@Override
	public Node copy() {
		return new Divide(getLeft().copy(), getRight().copy());
	}

	private BigInteger gcd(BigInteger a, BigInteger b) {
		return b.compareTo(BigInteger.ZERO) == 0 ? a : gcd(b, a.mod(b));
	}

	@Override
	public Node expand() throws MathsInterpreterException{
		Node left = getLeft().expand();
		Node right = getRight().expand();

		// undefined behaviour
		if(right instanceof Number && ((Number) right).getValue().compareTo(BigInteger.ZERO) == 0)
			throw new DivideByZeroException("You Can't divide by zero");

		if(right.matches(new Number(1)))
			return left;
		if(left.matches(new Number(0)))
			return new Number(0);
		if(left.matches(right))
			return new Number(1);
		if(left instanceof Negative && right instanceof Negative)
			return new Divide(((Negative) left).getNode(), ((Negative) right).getNode()).expand();
		if(left instanceof Negative)
			return new Negative(new Divide(((Negative) left).getNode(), right)).expand();
		if(right instanceof Negative)
			return new Negative(new Divide(left, ((Negative) right).getNode())).expand();

		if(left instanceof Number && right instanceof Number) {
			BigInteger a = ((Number) left).getValue();
			BigInteger b = ((Number) right).getValue();
			BigInteger divisor = gcd(a, b);
			if(a.mod(b).compareTo(BigInteger.ZERO) == 0)
				return new Number(a.divide(b));

			return new Divide(new Number(a.divide(divisor)), new Number(b.divide(divisor)));
		}

		if(left instanceof Divide)
			return new Divide(((BinaryNode) left).getLeft(), new Multiply(((BinaryNode) left).getRight(), right)).expand();

		if(left.isConstant() && right.isConstant())
			return new Divide(left, right);

		if(left instanceof Add || right instanceof Add)
			return new Divide(left, right);

		return new Multiply(left, new Power(right, new Negative(new Number(1)))).expand();
	}

	public ArrayList<Node> flatten(Multiply root){
		if(!(root.getLeft() instanceof Multiply)){
			return new ArrayList<Node>(Arrays.asList(root.getLeft(), root.getRight()));
		}
		else{
			ArrayList<Node> list = new ArrayList<>(Arrays.asList(root.getRight()));
			list.addAll(flatten((Multiply) root.getLeft()));
			return list;
		}
	}

	public Node unFlatten(ArrayList<Node> list){
		if (list.size() == 1)
			return list.get(0);
		if (list.size() == 2)
			return new Multiply(list.get(0), list.get(1));
		else{
			Multiply multi = new Multiply(list.get(0), list.get(1));
			for (int i = 2; i < list.size(); i++){
				multi = new Multiply(multi, list.get(i));
			}
			return multi;
		}
	}

	@Override
	public Node collapse() throws MathsInterpreterException {
		Node left = getLeft().collapse();
		Node right = getRight().collapse();

		// simplify fraction
		if(left instanceof Number && right instanceof Number) {
			BigInteger a = ((Number) left).getValue();
			BigInteger b = ((Number) right).getValue();
			BigInteger divisor = gcd(a, b);
			if(a.mod(b).compareTo(BigInteger.ZERO) == 0)
				return new Number(a.divide(b));

			return new Divide(new Number(a.divide(divisor)), new Number(b.divide(divisor)));
		}

		// x/y / z/w = x/y * w/z.collapse()
		if (left instanceof Divide && right instanceof Divide){
			return new Multiply(left, new Divide(((BinaryNode) right).getRight(), ((BinaryNode) right).getLeft())).collapse();
		}

		// x/y / z = (x*z)/y
		if (left instanceof Divide && !(right instanceof Divide))
			return new Divide(new Multiply(((BinaryNode) left).getLeft(), right).expand(), ((BinaryNode) left).getRight()).collapse();
		// x / y/z = (x*z)/y
		if (!(left instanceof Divide) && right instanceof Divide)
			return new Divide(new Multiply(left, ((BinaryNode) right).getRight()).expand(), ((BinaryNode) right).getLeft()).collapse();

		// remove common factors
		if (left instanceof Multiply && right instanceof Multiply){
			ArrayList<Node> leftList = flatten((Multiply) left);
			ArrayList<Node> rightList = flatten((Multiply) right);
			ListIterator<Node> leftItr = leftList.listIterator();
			
			while(leftItr.hasNext()){
				Node n = leftItr.next();
				ListIterator<Node> rightItr = rightList.listIterator();
				while(rightItr.hasNext()){
					Node o = rightItr.next();
					if(n.matches(o)){
						rightItr.remove();
						leftItr.remove();
						break;
					}
					if(n instanceof Number && o instanceof Number && gcd(((Number) n).getValue(), ((Number) o).getValue()).compareTo(BigInteger.ONE) != 0){
						BigInteger gcd = gcd(((Number) n).getValue(), ((Number) o).getValue());
						rightItr.remove();
						rightItr.add(new Number(((Number) o).getValue().divide(gcd)));
						leftItr.remove();
						leftItr.add(new Number(((Number) n).getValue().divide(gcd)));
						break;
					}
				}
			}
			return new Divide(unFlatten(leftList), unFlatten(rightList));
		}

		return new Divide(left, right);
	}

	@Override
	public String toString() {
		return (getLeft() instanceof Number | getLeft() instanceof Variable | getLeft() instanceof Multiply ? getLeft() : "(" + getLeft() + ")") + "/" + (getRight() instanceof Number | getRight() instanceof Variable ? getRight() : "(" + getRight() + ")");
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		if(getRight().matches(new Number(0)))
			throw new DivideByZeroException("Cannot differentiate a function which divides by 0");
		return new Divide(new Subtract(new Multiply(getLeft().differentiate(wrt), getRight()), new Multiply(getLeft(), getRight().differentiate(wrt))), new Power(getRight(), new Number(2))).simplify();
	}
}
