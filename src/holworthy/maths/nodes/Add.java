package holworthy.maths.nodes;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ListIterator;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.constant.ConstantNode;
import holworthy.maths.nodes.trig.TrigNode;

public class Add extends BinaryNode {
	public Add(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return getLeft() + " + " + getRight();
	}

	@Override
	public boolean matches(Node node) {
		return node instanceof Matching.AddOrSubtract || super.matches(node);
	}

	@Override
	public Node copy() {
		return new Add(getLeft().copy(), getRight().copy());
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

	private Node unflatten2(ArrayList<Node> list) {
		if(list.size() == 1)
			return list.get(0);
		Node node = new Multiply(list.get(0), list.get(1));
		for(int i = 2; i < list.size(); i++)
			node = new Multiply(node, list.get(i));
		return node;
	}

	private boolean shouldSwap(Node left, Node right) {
		if(left.matches(right))
			return false;

		// numbers on the right
		if(left instanceof Number && right instanceof Number)
			return ((Number) left).getValue().compareTo(((Number) right).getValue()) < 0;
		if(left instanceof Number && !(right instanceof Number))
			return true;

		if(left instanceof Number && (right instanceof Multiply || right instanceof Power))
			return true;

		// constants before numbers
		if(left instanceof Number && right instanceof ConstantNode)
			return true;

		// variables alphabetically
		if(left instanceof Variable && right instanceof Variable)
			return ((Variable) left).getName().compareTo(((Variable) right).getName()) > 0;
		if(left instanceof ConstantNode && right instanceof Variable)
			return true;

		if(left instanceof TrigNode && right instanceof TrigNode)
			return ((FunctionNode) left).getName().compareTo(((FunctionNode) right).getName()) > 0;

		if(left instanceof FunctionNode && right instanceof Variable)
			return true;
		if(left instanceof Variable && right instanceof FunctionNode)
			return false;

		if(left instanceof Negative)
			return shouldSwap(((UnaryNode) left).getNode(), right);
		if(right instanceof Negative)
			return shouldSwap(left, ((UnaryNode) right).getNode());

		ArrayList<Node> flattenedLeft = flatten2(left);
		ArrayList<Node> flattenedRight = flatten2(right);

		int index = 0;

		// remove constants
		while(flattenedLeft.size() > 0 && flattenedLeft.get(0).matches(new Matching.Constant()))
			flattenedLeft.remove(0);
		while(flattenedRight.size() > 0 && flattenedRight.get(0).matches(new Matching.Constant()))
			flattenedRight.remove(0);

		while(index < flattenedLeft.size() && index < flattenedRight.size()) {
			Power leftPower = flattenedLeft.get(index) instanceof Power ? (Power) flattenedLeft.get(index) : new Power(flattenedLeft.get(index), new Number(1));
			Power rightPower = flattenedRight.get(index) instanceof Power ? (Power) flattenedRight.get(index) : new Power(flattenedRight.get(index), new Number(1));

			if(leftPower.getLeft().matches(rightPower.getLeft())) {
				if(leftPower.matches(rightPower))
					index++;
				else if(shouldSwap(leftPower.getRight(), rightPower.getRight()))
					return true;
				else
					return false;
			} else {
				if(shouldSwap(leftPower.getLeft(), rightPower.getLeft()))
					return true;
				else if(shouldSwap(rightPower.getLeft(), leftPower.getLeft()))
					return false;
			}
		}

		return false;
	}

	private boolean canCombine(Node left, Node right) {
		if(left instanceof Negative)
			return canCombine(new Multiply(new Negative(new Number(1)), ((UnaryNode) left).getNode()), right);
		if(right instanceof Negative)
			return canCombine(left, new Multiply(new Negative(new Number(1)), ((UnaryNode) right).getNode()));

		ArrayList<Node> flattenedLeft = flatten2(left);
		ArrayList<Node> flattenedRight = flatten2(right);

		while(flattenedLeft.size() > 0 && flattenedLeft.get(0).matches(new Matching.Constant()))
			flattenedLeft.remove(0);
		while(flattenedRight.size() > 0 && flattenedRight.get(0).matches(new Matching.Constant()))
			flattenedRight.remove(0);

		if(flattenedLeft.size() != flattenedRight.size() || flattenedLeft.size() == 0)
			return false;
		for(int i = 0; i < flattenedLeft.size(); i++)
			if(!flattenedLeft.get(i).matches(flattenedRight.get(i)))
				return false;
		return true;
	}

	private Node combine(Node left, Node right) throws MathsInterpreterException {
		if(!canCombine(left, right))
			throw new MathsInterpreterException("Cannot combine " + left + " and " + right);
		if(left instanceof Negative)
			return combine(new Multiply(new Negative(new Number(1)), ((UnaryNode) left).getNode()), right);
		if(right instanceof Negative)
			return combine(left, new Multiply(new Negative(new Number(1)), ((UnaryNode) right).getNode()));

		ArrayList<Node> flattenedLeft = flatten2(left);
		ArrayList<Node> flattenedRight = flatten2(right);

		ArrayList<Node> leftConstants = new ArrayList<>();
		leftConstants.add(new Number(1));
		ArrayList<Node> rightConstants = new ArrayList<>();
		rightConstants.add(new Number(1));

		while(flattenedLeft.size() > 0 && flattenedLeft.get(0).matches(new Matching.Constant()))
			leftConstants.add(flattenedLeft.remove(0));
		while(flattenedRight.size() > 0 && flattenedRight.get(0).matches(new Matching.Constant()))
			rightConstants.add(flattenedRight.remove(0));

		System.out.println(left);
		System.out.println(right);

		return new Multiply(new Add(unflatten2(leftConstants), unflatten2(rightConstants)), unflatten2(flattenedLeft)).expand();
	}

	@Override
	public Node expand() throws MathsInterpreterException {
		Node left = getLeft().expand();
		Node right = getRight().expand();

		// make tree left leaning
		if(right instanceof Add)
			return new Add(new Add(left, ((Add) right).getLeft()), ((Add) right).getRight()).expand();

		// constant folding
		if(left.matches(new Number(0)))
			return right;
		if(right.matches(new Number(0)))
			return left;
		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue().add(((Number) right).getValue()));
		
		// x + number + number = x + number
		if(left instanceof Add && right instanceof Number && (((Add) left).getRight() instanceof Number || ((Add) left).getRight() instanceof Negative))
			return new Add(((Add) left).getLeft(), new Add(((Add) left).getRight(), right).expand());
		
		// number + -number
		if(left instanceof Number && right instanceof Negative && ((Negative) right).getNode() instanceof Number) {
			if(((Number) ((Negative) right).getNode()).getValue().compareTo(((Number) left).getValue())  <= 0)
				return new Number(((Number) left).getValue().subtract(((Number) ((Negative) right).getNode()).getValue()));
			else
				return new Negative(new Number(((Number) ((Negative) right).getNode()).getValue().subtract(((Number) left).getValue())));
		}
		// -number + number
		if(left instanceof Negative && right instanceof Number && ((Negative) left).getNode() instanceof Number) {
			if(((Number) ((Negative) left).getNode()).getValue().compareTo(((Number) right).getValue()) <= 0)
				return new Number(((Number) right).getValue().subtract(((Number) ((Negative) left).getNode()).getValue()));
			else
				return new Negative(new Number(((Number) ((Negative) left).getNode()).getValue().subtract(((Number) right).getValue())));
		}
		// -number + -number = -(number + number)
		if(left instanceof Negative && right instanceof Negative && ((UnaryNode) left).getNode() instanceof Number && ((UnaryNode) right).getNode() instanceof Number)
			return new Negative(new Add(((UnaryNode) left).getNode(), ((UnaryNode) right).getNode())).expand();

		// x+x=2*x
		if(left.matches(right))
			return new Multiply(new Number(2), left).expand();
		if(left instanceof Multiply && ((BinaryNode) left).getLeft() instanceof Number && ((BinaryNode) left).getRight().matches(right))
			return new Multiply(new Add(((BinaryNode) left).getLeft(), new Number(1)), ((BinaryNode) left).getRight()).expand();

		// TODO: i think this can go
		// // a+b+b=a+2*b
		// if(left instanceof Add && ((Add) left).getRight().matches(right))
		// 	return new Add(((Add) left).getLeft(), new Multiply(new Number(2), ((Add) left).getRight())).expand();
		// // a*x^n+x^n
		// if(left instanceof Multiply && ((Multiply) left).getLeft().isConstant() && ((Multiply) left).getRight().matches(right))
		// 	return new Multiply(new Add(((Multiply) left).getLeft(), new Number(1)), ((Multiply) left).getRight()).expand();
		// // x+a*x = (1+a)*x
		// if(!(left instanceof Multiply) && right instanceof Multiply && ((BinaryNode) right).getLeft().isConstant() && left.matches(((BinaryNode) right).getRight()))
		// 	return new Multiply(new Add(new Number(1),((BinaryNode) right).getLeft()), left).expand();
		// // x-x = 0
		// if(right instanceof Negative && left.matches(((UnaryNode) right).getNode()))
		// 	return new Number(0);
		// // -x+x = 0
		// if(left instanceof Negative && right.matches(((UnaryNode) left).getNode()))
		// 	return new Number(0);
		// // x + -a*x = (1-a)*x
		// if (right instanceof Negative && ((UnaryNode) right).getNode() instanceof Multiply && ((BinaryNode) ((UnaryNode) right).getNode()).getRight().matches(left))
		// 	return new Multiply(new Add(new Number(1), new Negative(((BinaryNode) ((UnaryNode) right).getNode()).getLeft())), left).expand();
		// // -a*x + x = (1-a)*x
		// if(left instanceof Negative && ((UnaryNode) left).getNode() instanceof Multiply && ((BinaryNode) ((UnaryNode) left).getNode()).getRight().matches(right))
		// 	return new Multiply(new Add(new Number(1), new Negative(((BinaryNode) ((UnaryNode) left).getNode()).getLeft())), right).expand();
		// // a+b*x^n+x^n
		// if(left instanceof Add && ((Add) left).getRight() instanceof Multiply && ((Multiply) ((Add) left).getRight()).getLeft().isConstant() && ((Multiply) ((Add) left).getRight()).getRight().matches(right))
		// 	return new Add(((Add) left).getLeft(), new Multiply(new Add(((Multiply) ((Add) left).getRight()).getLeft(), new Number(1)), ((Multiply) ((Add) left).getRight()).getRight())).expand();
		// // a*x^n+b*x^n=(a+b)*x^n
		// // if(left instanceof Multiply && ((BinaryNode) left).getLeft().isConstant() && right instanceof Multiply && ((BinaryNode) right).getLeft().isConstant() && ((BinaryNode) left).getRight().matches(((BinaryNode) right).getRight()))
		// // 	return new Multiply(new Add(((BinaryNode) left).getLeft(), ((BinaryNode) right).getLeft()), ((BinaryNode) left).getRight()).expand();
		// // make negative(x) = negative(1*x) for next few checks to avoid duplication logic
		// if (left instanceof Negative && !(((UnaryNode) left).getNode() instanceof Multiply)){
		// 	left = new Negative(new Multiply(new Number(1), ((UnaryNode) left).getNode()));
		// }
		// if (left instanceof Add && ((BinaryNode) left).getRight() instanceof Negative && !(((UnaryNode) ((BinaryNode) left).getRight()).getNode() instanceof Multiply) && !(((UnaryNode) ((BinaryNode) left).getRight()).getNode().isConstant())){
		// 	left = new Add(((BinaryNode) left).getLeft(), new Negative(new Multiply(new Number(1), ((UnaryNode) ((BinaryNode) left).getRight()).getNode())));
		// }
		// if (right instanceof Negative && !(((UnaryNode) right).getNode() instanceof Multiply)){
		// 	right = new Negative(new Multiply(new Number(1), ((UnaryNode) right).getNode()));
		// }
		// // a*x^n+-b*x^n=(a-b)*x^n
		// if (left instanceof Multiply && ((BinaryNode) left).getLeft().isConstant() && right instanceof Negative && ((UnaryNode) right).getNode() instanceof Multiply && ((BinaryNode) ((UnaryNode) right).getNode()).getLeft().isConstant() && ((BinaryNode) left).getRight().matches(((BinaryNode) ((UnaryNode) right).getNode()).getRight())){
		// 	return new Multiply(new Add(((BinaryNode) left).getLeft(), new Negative(((BinaryNode) ((UnaryNode) right).getNode()).getLeft())), ((BinaryNode) left).getRight()).expand();
		// }
		// // -a*x^n+b*x^n=(a-b)*x^n
		// if (right instanceof Multiply && ((BinaryNode) right).getLeft().isConstant() && left instanceof Negative && ((UnaryNode) left).getNode() instanceof Multiply && ((BinaryNode) ((UnaryNode) left).getNode()).getLeft().isConstant() && ((BinaryNode) right).getRight().matches(((BinaryNode) ((UnaryNode) left).getNode()).getRight())){
		// 	return new Multiply(new Add(((BinaryNode) right).getLeft(), new Negative(((BinaryNode) ((UnaryNode) left).getNode()).getLeft())), ((BinaryNode) right).getRight()).expand();
		// }
		// // -a*x + -b*x = (-a-b)*x
		// if (left instanceof Negative && ((UnaryNode) left).getNode() instanceof Multiply && ((BinaryNode) ((UnaryNode) left).getNode()).getLeft().isConstant() && right instanceof Negative && ((UnaryNode) right).getNode() instanceof Multiply && ((BinaryNode) ((UnaryNode) right).getNode()).getLeft().isConstant() && ((BinaryNode) ((UnaryNode) left).getNode()).getRight().matches(((BinaryNode) ((UnaryNode) right).getNode()).getRight())){
		// 	return new Multiply(new Add(new Negative(((BinaryNode) ((UnaryNode) left).getNode()).getLeft()), new Negative(((BinaryNode) ((UnaryNode) right).getNode()).getLeft())), ((BinaryNode) ((UnaryNode) right).getNode()).getRight()).expand();
		// }
		// // (c + a*x^n) + -b*x^n=(a-b)*x^n
		// if (left instanceof Add && ((BinaryNode) left).getRight() instanceof Multiply && ((BinaryNode) ((BinaryNode) left).getRight()).getLeft().isConstant() && right instanceof Negative && ((UnaryNode) right).getNode() instanceof Multiply && ((BinaryNode) ((UnaryNode) right).getNode()).getLeft().isConstant() && ((BinaryNode) ((BinaryNode) left).getRight()).getRight().matches(((BinaryNode) ((UnaryNode) right).getNode()).getRight())){
		// 	return new Add(((BinaryNode) left).getLeft(), new Multiply(new Add(((BinaryNode) ((BinaryNode) left).getRight()).getLeft(), new Negative(((BinaryNode) ((UnaryNode) right).getNode()).getLeft())), ((BinaryNode) ((BinaryNode) left).getRight()).getRight())).expand();
		// }
		// // (c + -a*x^n) + b*x^n=(a-b)*x^n
		// if (right instanceof Multiply && ((BinaryNode) right).getLeft().isConstant() && left instanceof Add && ((BinaryNode) left).getRight() instanceof Negative && ((UnaryNode) ((BinaryNode) left).getRight()).getNode() instanceof Multiply && ((BinaryNode) ((UnaryNode) ((BinaryNode) left).getRight()).getNode()).getLeft().isConstant() && ((BinaryNode) right).getRight().matches(((BinaryNode) ((UnaryNode) ((BinaryNode) left).getRight()).getNode()).getRight())){
		// 	return new Add(((BinaryNode) left).getLeft(), new Multiply(new Add(((BinaryNode) right).getLeft(), new Negative(((BinaryNode) ((UnaryNode) ((BinaryNode) left).getRight()).getNode()).getLeft())), ((BinaryNode) right).getRight())).expand();
		// }
		// // (c + -a*x) + -b*x = c + (-a-b)*x
		// if (left instanceof Add && ((BinaryNode) left).getRight() instanceof Negative && ((UnaryNode) ((BinaryNode) left).getRight()).getNode() instanceof Multiply && ((BinaryNode) ((UnaryNode) ((BinaryNode) left).getRight()).getNode()).getLeft().isConstant() && right instanceof Negative && ((UnaryNode) right).getNode() instanceof Multiply && ((BinaryNode) ((UnaryNode) right).getNode()).getLeft().isConstant() && ((BinaryNode) ((UnaryNode) ((BinaryNode) left).getRight()).getNode()).getRight().matches(((BinaryNode) ((UnaryNode) right).getNode()).getRight())){
		// 	return new Add(((BinaryNode) left).getLeft(), new Multiply(new Add(new Negative(((BinaryNode) ((UnaryNode) ((BinaryNode) left).getRight()).getNode()).getLeft()), new Negative(((BinaryNode) ((UnaryNode) right).getNode()).getLeft())), ((BinaryNode) ((UnaryNode) right).getNode()).getRight()));
		// }
		// // make negative(1*x) = negative(x) after previous few checks to avoid duplication logic
		// if (left instanceof Negative && ((UnaryNode) left).getNode() instanceof Multiply && ((BinaryNode) ((UnaryNode) left).getNode()).getLeft() instanceof Number && ((Number) ((BinaryNode) ((UnaryNode) left).getNode()).getLeft()).getValue().equals(BigInteger.ONE)){
		// 	left = new Negative(((BinaryNode) ((UnaryNode) left).getNode()).getRight());
		// }
		// if (left instanceof Add && ((BinaryNode) left).getRight() instanceof Negative && ((UnaryNode) ((BinaryNode) left).getRight()).getNode() instanceof Multiply && ((BinaryNode) ((UnaryNode) ((BinaryNode) left).getRight()).getNode()).getLeft() instanceof Number && ((Number) ((BinaryNode) ((UnaryNode) ((BinaryNode) left).getRight()).getNode()).getLeft()).getValue().equals(BigInteger.ONE)){
		// 	left = new Add(((BinaryNode) left).getLeft(), new Negative(((BinaryNode) left).getRight()));
		// }
		// if (right instanceof Negative && ((UnaryNode) right).getNode() instanceof Multiply && ((BinaryNode) ((UnaryNode) right).getNode()).getLeft() instanceof Number && ((Number) ((BinaryNode) ((UnaryNode) right).getNode()).getLeft()).getValue().equals(BigInteger.ONE)){
		// 	right = new Negative(((BinaryNode) ((UnaryNode) right).getNode()).getRight());
		// }
		// // if(left instanceof Add && ((BinaryNode) left).getRight() instanceof Multiply && ((BinaryNode) ((BinaryNode) left).getRight()).getLeft().isConstant() && right instanceof Multiply && ((BinaryNode) right).getLeft().isConstant() && ((BinaryNode) ((BinaryNode) left).getRight()).getRight().matches(((BinaryNode) right).getRight()))
		// // 	return new Multiply(new Add(((BinaryNode) ((BinaryNode) left).getRight()).getLeft(), ((BinaryNode) right).getLeft()), ((BinaryNode) ((BinaryNode) left).getRight()).getRight()).expand();
		// // 2*x + -(3*x)
		// if(left instanceof Multiply && right instanceof Negative && ((UnaryNode) right).getNode() instanceof Multiply && ((BinaryNode) left).getLeft().isConstant() && ((BinaryNode) ((UnaryNode) right).getNode()).getLeft().isConstant() && ((BinaryNode) left).getRight().matches(((BinaryNode) ((UnaryNode) right).getNode()).getRight()))
		// 	return new Multiply(new Subtract(((BinaryNode) left).getLeft(), ((BinaryNode) ((UnaryNode) right).getNode()).getLeft()), ((BinaryNode) left).getRight()).expand();
		// // a + 2*x - 3*x
		// if(left instanceof Add && ((BinaryNode) left).getRight() instanceof Multiply && right instanceof Negative && ((UnaryNode) right).getNode() instanceof Multiply && ((BinaryNode) ((BinaryNode) left).getRight()).getLeft().isConstant() && ((BinaryNode) ((UnaryNode) right).getNode()).getLeft().isConstant() && ((BinaryNode) ((BinaryNode) left).getRight()).getRight().matches(((BinaryNode) ((UnaryNode) right).getNode()).getRight()))
		// 	return new Add(((BinaryNode) left).getLeft(), new Multiply(new Subtract(((BinaryNode) ((BinaryNode) left).getRight()).getLeft(), ((BinaryNode) ((UnaryNode) right).getNode()).getLeft()), ((BinaryNode) ((BinaryNode) left).getRight()).getRight())).expand();
		// // c+a*x^n+b*x^n=c+(a+b)*x^n
		// if(left instanceof Add && ((Add) left).getRight() instanceof Multiply && ((BinaryNode) ((BinaryNode) left).getRight()).getLeft().isConstant() && right instanceof Multiply && ((BinaryNode) right).getLeft().isConstant() && ((BinaryNode) ((BinaryNode) left).getRight()).getRight().matches(((BinaryNode) right).getRight()))
		// 	return new Add(((Add) left).getLeft(), new Multiply(new Add(((BinaryNode) ((BinaryNode) left).getRight()).getLeft(), ((BinaryNode) right).getLeft()), ((BinaryNode) ((BinaryNode) left).getRight()).getRight())).expand();

		// a/b+c/d
		if(left instanceof Divide && right instanceof Divide){
			Divide newLeft = new Divide(new Multiply(((Divide) left).getLeft(), ((Divide) right).getRight()), new Multiply(((Divide) left).getRight(), ((Divide) right).getRight()));
			Divide newRight = new Divide(new Multiply(((Divide) right).getLeft(), ((Divide) left).getRight()), new Multiply(((Divide) right).getRight(),((Divide) left).getRight()));
			return new Divide(new Add(newLeft.getLeft(), newRight.getLeft()), newLeft.getRight()).expand();
		}

		// combine terms which can be combined
		if(canCombine(left, right))
			return combine(left, right);
		if(left instanceof Add && canCombine(((BinaryNode) left).getRight(), right))
			return new Add(((BinaryNode) left).getLeft(), combine(((BinaryNode) left).getRight(), right)).expand();

		// sort terms
		if(!(left instanceof Add) && shouldSwap(left, right))
			return new Add(right, left).expand();
		if(left instanceof Add && shouldSwap(((Add) left).getRight(), right))
			return new Add(new Add(((Add) left).getLeft(), right), ((Add) left).getRight()).expand();

		return new Add(left, right);
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
	public Node collapse() throws MathsInterpreterException{
		Node left = getLeft().collapse();
		Node right = getRight().collapse();

		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue().add(((Number) right).getValue()));

		if(left instanceof Negative && right instanceof Negative)
			return new Negative(new Add(((UnaryNode) left).getNode(), ((UnaryNode) right).getNode()));

		if(left instanceof Multiply && right instanceof Multiply && ((Multiply) left).getRight().matches(((Multiply) right).getRight()))
			return new Multiply(new Add(((Multiply) left).getLeft(), ((Multiply) right).getLeft()).collapse(), ((Multiply) left).getRight());

		if(left instanceof Variable && right instanceof Variable && left.matches(right))
			return new Multiply(new Number(2), left);

		// a + -b = a - b
		if(right instanceof Negative)
			return new Subtract(left, ((UnaryNode) right).getNode()).collapse();

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
					if(n instanceof Number && o instanceof Number && !(gcd(((Number) n).getValue(), ((Number) o).getValue()).equals(BigInteger.ONE))){
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
				return new Multiply(unFlatten(removeList), new Add(unFlatten(leftList), unFlatten(rightList)).simplify());
		}

		return new Add(left, right);
	}

	private BigInteger gcd(BigInteger a, BigInteger b) {
		return b.compareTo(BigInteger.ZERO) == 0 ? a : gcd(b, a.mod(b));
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Add(getLeft().differentiate(wrt), getRight().differentiate(wrt)).simplify();
	}

	// public static void main(String[] args) throws Exception {
	// 	LinkedHashMap<String, String> tests = new LinkedHashMap<>();
	//	// var + var
	// 	tests.put("x+x","2*x");
	// 	// var + mul(var)
	// 	tests.put("x+2*x", "3*x");
	// 	// var + neg(var)
	// 	tests.put("x+-x","0");
	// 	tests.put("2*x+-x","x");
	// 	// var + neg(mul(var))
	// 	tests.put("x+-2*x","-x");
	// 	// mul(var) + var
	// 	tests.put("2*x+x","3*x");
	// 	// mul(var) + mul(var)
	// 	tests.put("2*x+2*x","4*x");
	// 	// mul(var) + neg(var)
	// 	tests.put("2*x-x","x");
	// 	tests.put("3*x-x","2*x");
	// 	// mul(var) + neg(mul(var))
	// 	tests.put("4*x+-2*x","2*x");
	// 	tests.put("4*x-2*x","2*x");
	// 	tests.put("2*x-3*x","-x");
	// 	tests.put("2*x-4*x","-(2*x)");
	// 	// neg(var) + var
	// 	tests.put("-x+x","0");
	// 	// neg(var) + mul(var)
	// 	tests.put("-x+2*x","x");
	// 	// neg(var) + neg(var)
	// 	tests.put("-x+-x","-(2*x)");
	// 	tests.put("-x-x","-(2*x)");
	// 	// neg(var) + neg(mul(var))
	// 	tests.put("-x+-4*x","-(5*x)");
	// 	tests.put("-x-4*x","-(5*x)");
	// 	// neg(mul(var)) + var
	// 	tests.put("-2*x+x","-x");
	// 	// neg(mul(var)) + mul(var)
	// 	tests.put("-2*x+2*x","0");
	// 	tests.put("-2*x+3*x","x");
	// 	// neg(mul(var)) + neg(var)
	// 	tests.put("-2*x-x","-(3*x)");
	// 	// neg(mul(var)) + neg(mul(var))
	// 	tests.put("-2*x-2*x","-(4*x)");

	// 	Testing.runTests(tests);
	// }

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return getLeft().evaluate(values) + getRight().evaluate(values);
	}
}
