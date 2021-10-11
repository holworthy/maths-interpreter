package holworthy.maths.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

import holworthy.maths.DivideByZeroException;

public class Divide extends BinaryNode {
	public Divide(Node left, Node right) {
		super(left, right);
	}

	@Override
	public boolean matches(Node node) {
		if(node instanceof Divide)
			return super.matches(node);
		if(node instanceof Matching.Constant)
			return this.isConstant();
		return super.matches(node);
	}

	private int gcd(int a, int b) {
		return b == 0 ? a : gcd(b, a % b);
	}

	@Override
	public Node expand() throws DivideByZeroException{
		Node left = getLeft().expand();
		Node right = getRight().expand();

		if (right instanceof Number && ((Number) right).getValue() == 0){
			throw new DivideByZeroException("You Can't divide by zero");
		}

		if(left.matches(right))
			return new Number(1);

		if(left instanceof Negative && right instanceof Negative)
			return new Divide(((Negative) left).getNode(), ((Negative) right).getNode()).expand();
		if(left instanceof Negative)
			return new Negative(new Divide(((Negative) left).getNode(), right)).expand();
		if(right instanceof Negative)
			return new Negative(new Divide(left, ((Negative) right).getNode())).expand();

		if(left instanceof Number && right instanceof Number) {
			int a = ((Number) left).getValue();
			int b = ((Number) right).getValue();
			int divisor = gcd(a, b);
			if(a % b == 0)
				return new Number(a / b);

			return new Divide(new Number(a / divisor), new Number(b / divisor));
		}

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
	public Node collapse() {
		Node left = getLeft().collapse();
		Node right = getRight().collapse();

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
					if(n instanceof Number && o instanceof Number && gcd(((Number) n).getValue(), ((Number) o).getValue()) != 1){
						int gcd = gcd(((Number) n).getValue(), ((Number) o).getValue());
						rightItr.remove();
						rightItr.add(new Number(((Number) o).getValue()/gcd));
						leftItr.remove();
						leftItr.add(new Number(((Number) n).getValue()/gcd));
						break;
					}
				}
			}
			return new Divide(unFlatten(leftList), unFlatten(rightList));
		}

		return new Divide(getLeft().collapse(), getRight().collapse());
	}

	@Override
	public String toString() {
		return (getLeft() instanceof Number | getLeft() instanceof Variable | getLeft() instanceof Multiply ? getLeft() : "(" + getLeft() + ")") + "/" + (getRight() instanceof Number | getRight() instanceof Variable ? getRight() : "(" + getRight() + ")");
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}
}
