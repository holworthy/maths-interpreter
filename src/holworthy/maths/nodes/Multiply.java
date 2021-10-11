package holworthy.maths.nodes;

import holworthy.maths.DivideByZeroException;
import holworthy.maths.nodes.constant.I;

public class Multiply extends BinaryNode {
	public Multiply(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return (getLeft() instanceof Add ? "(" + getLeft() + ")" : getLeft()) + "*" + (getRight() instanceof Add ? "(" + getRight() + ")" : getRight());
	}

	@Override
	public Node normalise() {
		Node left = getLeft().normalise();
		Node right = getRight().normalise();

		// constant folding
		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue() * ((Number) right).getValue());
		if(left.matches(new Number(0)))
			return new Number(0);
		if(left.matches(new Number(1)))
			return right;

		// move constants to left
		if(right.matches(new Matching.Constant()) && !left.matches(new Matching.Constant()))
			return new Multiply(right, left).normalise();
		
		// Make tree left leaning
		if(right instanceof Multiply)
			return new Multiply(new Multiply(left, ((Multiply) right).getLeft().normalise()).normalise(), ((Multiply) right).getRight());

		// x*x = x^2
		if(left instanceof Variable && left.matches(right))
			return new Power(left, new Number(2)).normalise();
		// x*x^n = x^(n+1)
		if(left instanceof Variable && right instanceof Power && ((Power) right).getLeft().matches(left))
			return new Power(left, new Add(new Number(1), ((Power) right).getRight())).normalise();

		return new Multiply(left, right);
	}

	@Override
	public Node expand() throws DivideByZeroException{
		Node left = getLeft().expand();
		Node right = getRight().expand();

		// Make tree left leaning
		if(right instanceof Multiply)
			return new Multiply(new Multiply(left, ((Multiply) right).getLeft()), ((Multiply) right).getRight()).expand();

		// constant folding
		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue() * ((Number) right).getValue());
		if(left.matches(new Number(0)))
			return new Number(0);
		if(right.matches(new Number(0)))
			return new Number(0);
		if(left.matches(new Number(1)))
			return right;
		if(right.matches(new Number(1)))
			return left;
		// negative constant handling
		if(left instanceof Negative && !(right instanceof Negative))
			return new Negative(new Multiply(((UnaryNode) left).getNode(), right)).expand();
		if(!(left instanceof Negative) && right instanceof Negative)
			return new Negative(new Multiply(left, ((UnaryNode) right).getNode())).expand();
		if(left instanceof Negative && right instanceof Negative)
			return new Multiply(((UnaryNode) left).getNode(), ((UnaryNode) right).getNode());

		// x*x = x^2
		if(left instanceof Variable && left.matches(right))
			return new Power(left, new Number(2)).expand();
		// a*b*b = a*b^2
		if(left instanceof Multiply && ((Multiply) left).getRight() instanceof Variable && ((Multiply) left).getRight().matches(right))
			return new Multiply(((Multiply) left).getLeft(), new Power(((Multiply) left).getRight(), new Number(2))).expand();
		// x*x^n = x^(n+1)
		if(left instanceof Variable && right instanceof Power && ((Power) right).getLeft().matches(left))
			return new Power(left, new Add(new Number(1), ((Power) right).getRight())).expand();
		// a*x*x^n = a*x^(n+1)
		if(left instanceof Multiply && ((Multiply) left).getRight() instanceof Variable && right instanceof Power && ((Power) right).getLeft().matches(((Multiply) left).getRight()))
			return new Multiply(((Multiply) left).getLeft(), new Power(((Multiply) left).getRight(), new Add(new Number(1), ((Power) right).getRight()))).expand();
		// x^a*x^b = x^(a+b)
		if(left instanceof Power && right instanceof Power && ((Power) left).getLeft().matches(((Power) right).getLeft()))
			return new Power(((BinaryNode) left).getLeft(), new Add(((Power) left).getRight(), ((Power) right).getRight())).expand();
		// // c*x^a*x^b = c*x^(a+b)
		if(left instanceof Multiply && ((Multiply) left).getRight() instanceof Power && right instanceof Power && ((Power) ((Multiply) left).getRight()).getLeft().matches(((Power) right).getLeft()))
			return new Multiply(((Multiply) left).getLeft(), new Power(((Power) ((Multiply) left).getRight()).getLeft(), new Add(((Power) ((Multiply) left).getRight()).getRight(), ((Power) right).getRight()))).expand();

		// Expanding brackets
		if(right instanceof Add)
			return new Add(new Multiply(left, ((Add) right).getLeft()), new Multiply(left, ((Add) right).getRight())).expand();
		// Putting brackets on the right (which then calls the rule above)
		if(left instanceof Add)
			return new Multiply(right, left).expand();

		// sort terms
		if(needSwitching(left, right))
			return new Multiply(right, left);
		else if(left instanceof Power && right instanceof Variable)
			return new Multiply(right, left).expand();
		// swap with lower multiply nodes
		if(left instanceof Multiply){
			if(needSwitching(((BinaryNode) left).getRight(), right))
				return new Multiply(new Multiply(((BinaryNode) left).getLeft(), right), ((BinaryNode) left).getRight()).expand();
			// else if(((BinaryNode) left).getRight() instanceof Power && right instanceof Variable)
			// 	return new Multiply(new Multiply(((BinaryNode) left).getLeft(), right), ((BinaryNode) left).getRight()).expand();
		}

		if(left instanceof I && right instanceof I)
			return new Negative(new Number(1));

		if(left instanceof Divide && right instanceof Number)
			return new Divide(new Multiply(((Divide) left).getLeft(), right).expand(), ((Divide) left).getRight());

		return new Multiply(left, right);
	}

	public boolean needSwitching(Node left, Node right){
		// swap variables and numbers
		if((left instanceof Variable && (right instanceof Number || right instanceof Negative)) || (left instanceof Power && (right instanceof Number || right instanceof Negative)))
			return true;
		// swap powers by variables
		if(left instanceof Variable && right instanceof Power && ((BinaryNode) right).getLeft() instanceof Variable && ((Variable) left).getName().compareTo(((Variable) ((BinaryNode) right).getLeft()).getName()) > 0)
			return true;
		if(left instanceof Power && right instanceof Power && ((Variable) ((BinaryNode) left).getLeft()).getName().compareTo(((Variable) ((BinaryNode) right).getLeft()).getName()) > 0)
			return true;
		// swap variables
		if(left instanceof Variable && right instanceof Variable)
			if (((Variable) left).getName().compareTo(((Variable) right).getName()) > 0)
				return true;
		return false;
	}

	@Override
	public Node collapse() {
		Node left = getLeft().collapse();
		Node right = getRight().collapse();

		if(right instanceof Power && ((BinaryNode) right).getRight() instanceof Negative){
			return new Divide(left, new Power(((BinaryNode) right).getLeft(), ((UnaryNode) ((BinaryNode) right).getRight()).getNode())).collapse();
		}

		return new Multiply(left, right);
	}

	@Override
	public Node differentiate(Variable wrt) {
		// TODO: implement
		return null;
	}
}

