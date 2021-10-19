package holworthy.maths.nodes;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

import holworthy.maths.DivideByZeroException;

public class Add extends BinaryNode {
	public Add(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return getLeft() + " + " + getRight();
	}

	@Override
	public Node normalise() {
		Node left = getLeft().normalise();
		Node right = getRight().normalise();

		// make tree left leaning
		if(right instanceof Add)
			return new Add(new Add(left, ((Add) right).getLeft().normalise()).normalise(), ((Add) right).getRight().normalise());

		return new Add(left, right);
	}

	private ArrayList<Node> flatten2(Node node) {
		if(node instanceof Multiply && ((BinaryNode) node).getLeft() instanceof Multiply) {
			ArrayList<Node> items = flatten2(((Multiply) node).getLeft());
			items.add(((BinaryNode) node).getRight());
			return items;
		} else {
			ArrayList<Node> items = new ArrayList<>();
			if(node instanceof Multiply) {
				items.add(((BinaryNode) node).getLeft());
				items.add(((BinaryNode) node).getRight());
			} else {
				items.add(node);
			}
			return items;
		}
	}

	private boolean shouldSwap(Node left, Node right) {
		if(left.matches(right))
			return false;

		if(left instanceof Number && right instanceof Number)
			return ((Number) left).getValue().compareTo(((Number) right).getValue()) < 0;
		if(left instanceof Number && !(right instanceof Number))
			return true;

		if(left instanceof Number && (right instanceof Multiply || right instanceof Power))
			return true;

		if(left instanceof Variable && right instanceof Variable)
			return ((Variable) left).getName().compareTo(((Variable) right).getName()) > 0;

		if(left instanceof Negative)
			return shouldSwap(((UnaryNode) left).getNode(), right);
		if(right instanceof Negative)
			return shouldSwap(left, ((UnaryNode) right).getNode());

		ArrayList<Node> flattenedLeft = flatten2(left);
		ArrayList<Node> flattenedRight = flatten2(right);

		int leftIndex = 0;
		int rightIndex = 0;

		// remove constants
		while(flattenedLeft.size() > 0 && flattenedLeft.get(0).matches(new Matching.Constant()))
			flattenedLeft.remove(0);
		while(flattenedRight.size() > 0 && flattenedRight.get(0).matches(new Matching.Constant()))
			flattenedRight.remove(0);

		while(leftIndex < flattenedLeft.size() && rightIndex < flattenedRight.size()) {
			Power leftPower = flattenedLeft.get(leftIndex) instanceof Variable ? new Power(flattenedLeft.get(leftIndex), new Number(1)) : (Power) flattenedLeft.get(leftIndex);
			Power rightPower = flattenedRight.get(rightIndex) instanceof Variable ? new Power(flattenedRight.get(rightIndex), new Number(1)) : (Power) flattenedRight.get(rightIndex);

			if(leftPower.getLeft().matches(rightPower.getLeft())) {
				if(leftPower.matches(rightPower)) {
					leftIndex++;
					rightIndex++;
				} else if(shouldSwap(leftPower.getRight(), rightPower.getRight())) {
					return true;
				} else {
					return false;
				}
			} else {
				if(shouldSwap(leftPower.getLeft(), rightPower.getLeft()))
					return true;
				else if(shouldSwap(rightPower.getLeft(), leftPower.getLeft()))
					return false;
			}
		}

		return false;
	}

	@Override
	public Node expand() throws DivideByZeroException{
		Node left = getLeft().expand();
		Node right = getRight().expand();

		if(left.matches(new Number(0)))
			return right;
		if(right.matches(new Number(0)))
			return left;

		// constant folding
		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue().add(((Number) right).getValue()));
		if(left instanceof Add && right instanceof Number && (((Add) left).getRight() instanceof Number || ((Add) left).getRight() instanceof Negative))
			return new Add(((Add) left).getLeft(), new Add(((Add) left).getRight(), right)).expand();
		if(left instanceof Number && right instanceof Negative && ((Negative) right).getNode() instanceof Number) {
			if(((Number) ((Negative) right).getNode()).getValue().compareTo(((Number) left).getValue())  <= 0)
				return new Number(((Number) left).getValue().subtract(((Number) ((Negative) right).getNode()).getValue()));
			else
				return new Negative(new Number(((Number) ((Negative) right).getNode()).getValue().subtract(((Number) left).getValue())));
		}
		if(left instanceof Negative && right instanceof Number && ((Negative) left).getNode() instanceof Number) {
			if(((Number) ((Negative) left).getNode()).getValue().compareTo(((Number) right).getValue()) <= 0)
				return new Number(((Number) right).getValue().subtract(((Number) ((Negative) left).getNode()).getValue()));
			else
				return new Negative(new Number(((Number) ((Negative) left).getNode()).getValue().subtract(((Number) right).getValue())));
		}
		if(left instanceof Negative && right instanceof Negative && ((Negative) left).getNode() instanceof Number && ((Negative) right).getNode() instanceof Number)
			return new Negative(new Number(((Number) ((Negative) left).getNode()).getValue().add(((Number) ((Negative) right).getNode()).getValue())));

		// x+x=2*x
		if(left.matches(right))
			return new Multiply(new Number(2), left).expand();
		// a+b+b=a+2*b
		if(left instanceof Add && ((Add) left).getRight().matches(right))
			return new Add(((Add) left).getLeft(), new Multiply(new Number(2), ((Add) left).getRight())).expand();
		// a*x^n+x^n
		if(left instanceof Multiply && ((Multiply) left).getLeft().isConstant() && ((Multiply) left).getRight().matches(right))
			return new Multiply(new Add(((Multiply) left).getLeft(), new Number(1)), ((Multiply) left).getRight()).expand();
		// a+b*x^n+x^n
		if(left instanceof Add && ((Add) left).getRight() instanceof Multiply && ((Multiply) ((Add) left).getRight()).getLeft().isConstant() && ((Multiply) ((Add) left).getRight()).getRight().matches(right))
			return new Add(((Add) left).getLeft(), new Multiply(new Add(((Multiply) ((Add) left).getRight()).getLeft(), new Number(1)), ((Multiply) ((Add) left).getRight()).getRight())).expand();
		// a*x^n+b*x^n=(a+b)*x^n
		if(left instanceof Multiply && ((BinaryNode) left).getLeft().isConstant() && right instanceof Multiply && ((BinaryNode) right).getLeft().isConstant() && ((BinaryNode) left).getRight().matches(((BinaryNode) right).getRight()))
			return new Multiply(new Add(((BinaryNode) left).getLeft(), ((BinaryNode) right).getLeft()), ((BinaryNode) left).getRight()).expand();
		if(left instanceof Add && ((BinaryNode) left).getRight() instanceof Multiply && ((BinaryNode) ((BinaryNode) left).getRight()).getLeft().isConstant() && right instanceof Multiply && ((BinaryNode) right).getLeft().isConstant() && ((BinaryNode) ((BinaryNode) left).getRight()).getRight().matches(((BinaryNode) right).getRight()))
			return new Multiply(new Add(((BinaryNode) ((BinaryNode) left).getRight()).getLeft(), ((BinaryNode) right).getLeft()), ((BinaryNode) ((BinaryNode) left).getRight()).getRight()).expand();
		// 2*x + -(3*x)
		if(left instanceof Multiply && right instanceof Negative && ((UnaryNode) right).getNode() instanceof Multiply && ((BinaryNode) left).getLeft().isConstant() && ((BinaryNode) ((UnaryNode) right).getNode()).getLeft().isConstant() && ((BinaryNode) left).getRight().matches(((BinaryNode) ((UnaryNode) right).getNode()).getRight()))
			return new Multiply(new Subtract(((BinaryNode) left).getLeft(), ((BinaryNode) ((UnaryNode) right).getNode()).getLeft()), ((BinaryNode) left).getRight()).expand();
		// a + 2*x - 3*x
		if(left instanceof Add && ((BinaryNode) left).getRight() instanceof Multiply && right instanceof Negative && ((UnaryNode) right).getNode() instanceof Multiply && ((BinaryNode) ((BinaryNode) left).getRight()).getLeft().isConstant() && ((BinaryNode) ((UnaryNode) right).getNode()).getLeft().isConstant() && ((BinaryNode) ((BinaryNode) left).getRight()).getRight().matches(((BinaryNode) ((UnaryNode) right).getNode()).getRight()))
			return new Add(((BinaryNode) left).getLeft(), new Multiply(new Subtract(((BinaryNode) ((BinaryNode) left).getRight()).getLeft(), ((BinaryNode) ((UnaryNode) right).getNode()).getLeft()), ((BinaryNode) ((BinaryNode) left).getRight()).getRight())).expand();
		// c+a*x^n+b*x^n=c+(a+b)*x^n
		if(left instanceof Add && ((Add) left).getRight() instanceof Multiply && ((BinaryNode) ((BinaryNode) left).getRight()).getLeft().isConstant() && right instanceof Multiply && ((BinaryNode) right).getLeft().isConstant() && ((BinaryNode) ((BinaryNode) left).getRight()).getRight().matches(((BinaryNode) right).getRight()))
			return new Add(((Add) left).getLeft(), new Multiply(new Add(((BinaryNode) ((BinaryNode) left).getRight()).getLeft(), ((BinaryNode) right).getLeft()), ((BinaryNode) ((BinaryNode) left).getRight()).getRight())).expand();

		// a/b+c/d
		if(left instanceof Divide && right instanceof Divide){
			Divide newLeft = new Divide(new Multiply(((Divide) left).getLeft(), ((Divide) right).getRight()), new Multiply(((Divide) left).getRight(), ((Divide) right).getRight()));
			Divide newRight = new Divide(new Multiply(((Divide) right).getLeft(), ((Divide) left).getRight()), new Multiply(((Divide) right).getRight(),((Divide) left).getRight()));
			return new Divide(new Add(newLeft.getLeft(), newRight.getLeft()), newLeft.getRight()).expand();
		}

		// make tree left leaning
		if(right instanceof Add)
			return new Add(new Add(left, ((Add) right).getLeft()), ((Add) right).getRight()).expand();

		// sort terms
		if(!(left instanceof Add) && shouldSwap(left, right))
			return new Add(right, left).expand();
		if(left instanceof Add && shouldSwap(((Add) left).getRight(), right)) // {
			// System.out.println("swapping " + ((BinaryNode) left).getRight() + " with " + right);
			return new Add(new Add(((Add) left).getLeft(), right), ((Add) left).getRight()).expand();
		// }

		return new Add(left, right);
	}
	// TODO: refactor as wetted in divide
	private BigInteger gcd(BigInteger a, BigInteger b) {
		return b.compareTo(BigInteger.ZERO) == 0 ? a : gcd(b, a.mod(b));
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
	public Node collapse() throws DivideByZeroException{
		Node left = getLeft().collapse();
		Node right = getRight().collapse();

		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue().add(((Number) right).getValue()));

		if(left instanceof Multiply && right instanceof Multiply && ((Multiply) left).getRight().matches(((Multiply) right).getRight()))
			return new Multiply(new Add(((Multiply) left).getLeft(), ((Multiply) right).getLeft()).collapse(), ((Multiply) left).getRight());

		if(left instanceof Variable && right instanceof Variable && left.matches(right))
			return new Multiply(new Number(2), left);

		if(left instanceof Multiply && right instanceof Multiply){
			if(((BinaryNode) left).getRight().matches(((BinaryNode) right).getRight())){
				Add a = new Add(((BinaryNode) left).getLeft(), ((BinaryNode) right).getLeft());
				return new Multiply(a.collapse(), ((BinaryNode) right).getRight());
			}
			if(((BinaryNode) left).getLeft().matches(((BinaryNode) right).getLeft())){
				Add a = new Add(((BinaryNode) left).getRight(), ((BinaryNode) right).getRight());
				return new Multiply(((BinaryNode) right).getLeft(), a.collapse());
			}
		
			ArrayList<Node> leftList = flatten((Multiply) left);
			ArrayList<Node> rightList = flatten((Multiply) right);
			ArrayList<Node> removeList = new ArrayList<>();
			ListIterator<Node> leftItr = leftList.listIterator();
			
			while(leftItr.hasNext()){
				Node n = leftItr.next();
				ListIterator<Node> rightItr = rightList.listIterator();
				while(rightItr.hasNext()){
					Node o = rightItr.next();
					if(n.matches(o)){
						removeList.add(o);
						rightItr.remove();
						leftItr.remove();
						break;
					}
					if(n instanceof Number && o instanceof Number && gcd(((Number) n).getValue(), ((Number) o).getValue()).compareTo(BigInteger.ONE) == 0){
						BigInteger gcd = gcd(((Number) n).getValue(), ((Number) o).getValue());
						removeList.add(new Number(gcd));
						rightItr.remove();
						rightItr.add(new Number(((Number) o).getValue().divide(gcd)));
						leftItr.remove();
						leftItr.add(new Number(((Number) n).getValue().divide(gcd)));
						break;
					}
				}
			}
			if(!(removeList.isEmpty()))
				return new Multiply(unFlatten(removeList), new Add(unFlatten(leftList), unFlatten(rightList)));
		}

		return new Add(left, right);
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}
}
