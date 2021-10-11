package holworthy.maths.nodes;

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

	private boolean shouldSwap(Node left, Node right) {
		if(left.matches(right))
			return false;
		if(left instanceof Number && right instanceof Number)
			return ((Number) left).getValue() < ((Number) right).getValue();
		if(left instanceof Number && !(right instanceof Number))
			return true;
		if(right instanceof Number)
			return false;
		if(left instanceof Variable && right instanceof Variable)
			return ((Variable) left).getName().compareTo(((Variable) right).getName()) > 0;
		if(left instanceof Multiply && ((BinaryNode) left).getLeft().isConstant() && ((BinaryNode) left).getRight() instanceof Variable && right instanceof Variable)
			return ((BinaryNode) left).getRight().matches(right) ? false : shouldSwap(((BinaryNode) left).getRight(), right);
		if(left instanceof Power && right instanceof Power) {
			if(((BinaryNode) left).getLeft() instanceof Variable && ((BinaryNode) right).getLeft() instanceof Variable) {
				return ((BinaryNode) left).getLeft().matches(((BinaryNode) right).getLeft()) ? shouldSwap(((BinaryNode) left).getRight(), ((BinaryNode) right).getRight()) : shouldSwap(((BinaryNode) left).getLeft(), ((BinaryNode) right).getLeft());
			}
			// TODO: 2^x + 2^z + 2^y
		}
		if(left instanceof Variable && right instanceof Power && ((BinaryNode) right).getLeft() instanceof Variable)
			return left.matches(((BinaryNode) right).getLeft()) ? true : shouldSwap(left, ((BinaryNode) right).getLeft());
		if(left instanceof Multiply && ((BinaryNode) left).getLeft().isConstant() && ((BinaryNode) left).getRight() instanceof Power)
			return shouldSwap(((BinaryNode) left).getRight(), right);
		if(right instanceof Multiply && ((BinaryNode) right).getLeft().isConstant() && ((BinaryNode) right).getRight() instanceof Power)
			return shouldSwap(left, ((BinaryNode) right).getRight());
		

		return false;
	}

	@Override
	public Node expand() {
		Node left = getLeft().expand();
		Node right = getRight().expand();

		// constant folding
		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue() + ((Number) right).getValue());
		if(left instanceof Add && right instanceof Number && (((Add) left).getRight() instanceof Number || ((Add) left).getRight() instanceof Negative))
			return new Add(((Add) left).getLeft(), new Add(((Add) left).getRight(), right)).expand();
		if(left instanceof Number && right instanceof Negative && ((Negative) right).getNode() instanceof Number) {
			if(((Number) ((Negative) right).getNode()).getValue() <= ((Number) left).getValue())
				return new Number(((Number) left).getValue() - ((Number) ((Negative) right).getNode()).getValue());
			else
				return new Negative(new Number(((Number) ((Negative) right).getNode()).getValue() - ((Number) left).getValue()));
		}
		if(left instanceof Negative && right instanceof Number && ((Negative) left).getNode() instanceof Number) {
			if(((Number) ((Negative) left).getNode()).getValue() <= ((Number) right).getValue())
				return new Number(((Number) right).getValue() - ((Number) ((Negative) left).getNode()).getValue());
			else
				return new Negative(new Number(((Number) ((Negative) left).getNode()).getValue() - ((Number) right).getValue()));
		}
		if(left instanceof Negative && right instanceof Negative && ((Negative) left).getNode() instanceof Number && ((Negative) right).getNode() instanceof Number)
			return new Negative(new Number(((Number) ((Negative) left).getNode()).getValue() + ((Number) ((Negative) right).getNode()).getValue()));

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
		if(left instanceof Add && shouldSwap(((Add) left).getRight(), right)) {
			System.out.println("swapping " + ((BinaryNode) left).getRight() + " with " + right);
			return new Add(new Add(((Add) left).getLeft(), right), ((Add) left).getRight()).expand();
		}

		return new Add(left, right);
	}

	@Override
	public Node collapse() {
		Node left = getLeft().collapse();
		Node right = getRight().collapse();

		// if(left instanceof Number && right instanceof Number)
		// 	return new Number(((Number) left).getValue() + ((Number) right).getValue());

		// if(left instanceof Multiply && right instanceof Multiply && ((Multiply) left).getRight().matches(((Multiply) right).getRight()))
		// 	return new Multiply(new Add(((Multiply) left).getLeft(), ((Multiply) right).getLeft()).collapse(), ((Multiply) left).getRight());

		// if(left instanceof Variable && right instanceof Variable && left.matches(right))
		// 	return new Multiply(new Number(2), left);

		// if(left instanceof Multiply && right instanceof Multiply){
		// 	if(((BinaryNode) left).getRight().matches(((BinaryNode) right).getRight())){
		// 		Add a = new Add(((BinaryNode) left).getLeft(), ((BinaryNode) right).getLeft());
		// 		return new Multiply(a.collapse(), ((BinaryNode) right).getRight());
		// 	}
		// 	if(((BinaryNode) left).getLeft().matches(((BinaryNode) right).getLeft())){
		// 		Add a = new Add(((BinaryNode) left).getRight(), ((BinaryNode) right).getRight());
		// 		return new Multiply(((BinaryNode) right).getLeft(), a.collapse());
		// 	}
		// }

		return new Add(left, right);
	}
}
