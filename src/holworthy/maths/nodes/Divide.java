package holworthy.maths.nodes;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ListIterator;

import holworthy.maths.exceptions.DivideByZeroException;
import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.trig.Cos;
import holworthy.maths.nodes.trig.Sin;
import holworthy.maths.nodes.trig.Tan;

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

		if(left instanceof Sin && right instanceof Cos && ((UnaryNode) left).getNode().matches(((UnaryNode) right).getNode()))
			return new Tan(((UnaryNode) left).getNode());

		if(left instanceof Divide)
			return new Divide(((BinaryNode) left).getLeft(), new Multiply(((BinaryNode) left).getRight(), right)).expand();

		// if(left instanceof Add){
		// 	return new Add(new Divide(((BinaryNode) left).getLeft(), right), new Divide(((BinaryNode) left).getRight(), right)).expand();
		// }

		// if(left instanceof Subtract){
		// 	return new Subtract(new Divide(((BinaryNode) left).getLeft(), right), new Divide(((BinaryNode) left).getRight(), right)).expand();
		// }

		if(left.isConstant() && right.isConstant())
			return new Divide(left, right);

		if(left instanceof Add || right instanceof Add)
			return new Divide(left, right);

		// return new Multiply(left, new Power(right, new Negative(new Number(1)))).expand();
		return new Divide(left, right);
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
			return removeCommonFactors(leftList, rightList);
		}

		if (left instanceof Multiply && right instanceof Node && !(right instanceof BinaryNode) && (!(right instanceof UnaryNode) || right instanceof FunctionNode)){
			ArrayList<Node> leftList = flatten((Multiply) left);
			ArrayList<Node> rightList = new ArrayList<>();
			rightList.add(right);
			return removeCommonFactors(leftList, rightList);
		}

		if (left instanceof Node && !(left instanceof BinaryNode) && (!(left instanceof UnaryNode) || left instanceof FunctionNode) && right instanceof Multiply){
			ArrayList<Node> leftList = new ArrayList<>();
			leftList.add(left);
			ArrayList<Node> rightList = flatten((Multiply) right);
			return removeCommonFactors(leftList, rightList);
		}

		if (left instanceof Negative && ((UnaryNode) left).getNode() instanceof Multiply && right instanceof Node && !(right instanceof BinaryNode) && (!(right instanceof UnaryNode) || right instanceof FunctionNode)){
			ArrayList<Node> leftList = flatten(new Multiply(((UnaryNode) left).getNode(), new Negative(new Number(1))));
			ArrayList<Node> rightList = new ArrayList<>();
			rightList.add(right);
			return removeCommonFactors(leftList, rightList);
		}

		return new Divide(left, right);
	}

	public Node removeCommonFactors(ArrayList<Node> leftList, ArrayList<Node> rightList){
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
		if (rightList.size() > 0 && leftList.size() > 0){
			return new Divide(unFlatten(leftList), unFlatten(rightList));
		}
		else if(leftList.size() == 0 && rightList.size() > 0){
			leftList.add(new Number(1));
			return new Divide(unFlatten(leftList), unFlatten(rightList));
		}
		return unFlatten(leftList);
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

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return getLeft().evaluate(values) / getRight().evaluate(values);
	}
}
