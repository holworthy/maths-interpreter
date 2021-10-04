package holworthy.maths.nodes;

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

		// move constants to left
		if(right.matches(new Matching.Constant()) && !left.matches(new Matching.Constant()))
			return new Multiply(right, left).normalise();
		
		// Make tree left leaning
		if(right instanceof Multiply)
			return new Multiply(new Multiply(left, ((Multiply) right).getLeft().normalise()).normalise(), ((Multiply) right).getRight());

		return new Multiply(left, right);
	}

	@Override
	public Node expand() {
		Node left = getLeft().expand();
		Node right = getRight().expand();

		// Expanding brackets
		if(right instanceof Add)
			return new Add(new Multiply(left, ((Add) right).getLeft()).normalise(), new Multiply(left, ((Add) right).getRight()).normalise()).expand().normalise();

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
				return new Multiply(new Multiply(((BinaryNode) left).getLeft(), right).expand(), ((BinaryNode) left).getRight()).expand();
			else if(((BinaryNode) left).getRight() instanceof Power && right instanceof Variable)
				return new Multiply(new Multiply(((BinaryNode) left).getLeft(), right).expand(), ((BinaryNode) left).getRight()).expand();
		}

		return new Multiply(left, right);
	}

	public boolean needSwitching(Node left, Node right){
		// swap variables and numbers
		if((left instanceof Variable && right instanceof Number) || (left instanceof Power && right instanceof Number))
			return true;
		// swap powers by variables
		if(left instanceof Variable && right instanceof Power && ((Variable) left).getName().compareTo(((Variable) ((BinaryNode) right).getLeft()).getName()) > 0)
			return true;
		if(left instanceof Power && right instanceof Power && ((Variable) ((BinaryNode) left).getLeft()).getName().compareTo(((Variable) ((BinaryNode) right).getLeft()).getName()) > 0)
			return true;
		if(left instanceof Variable && right instanceof Variable)
			if (((Variable) left).getName().compareTo(((Variable) right).getName()) > 0)
				return true;
		return false;
	}

	@Override
	public Node collapse() {
		Node left = getLeft().collapse();
		Node right = getRight().collapse();

		// Multiply two numbers together
		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue() * ((Number) right).getValue());
	
		// x*x = x^2
		if(left.matches(right))
			return new Power(left, new Number(2));

		if(left instanceof Divide && right instanceof Number)
			return new Divide(new Multiply(((Divide) left).getLeft(), right), ((Divide) left).getRight()).collapse();

		return new Multiply(left, right);
	}
}

