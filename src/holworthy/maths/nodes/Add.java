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

	private Variable getVariable(Node node) {
		if(node instanceof Variable)
			return (Variable) node;
		if(node instanceof Power)
			return (Variable) ((Power) node).getLeft();
		if(node instanceof Multiply && ((Multiply) node).getLeft().matches(new Matching.Constant()))
			return getVariable(((Multiply) node).getRight());
		if(node instanceof Multiply)
			return getVariable(((Multiply) node).getLeft());
		
		System.out.println(node);
		return null;
	}

	private int countVariables(Node node) {
		if(node instanceof Number)
			return 0;
		if(node instanceof Variable)
			return 1;
		if(node instanceof Power)
			return countVariables(((Power) node).getLeft());
		if(node instanceof Multiply)
			return countVariables(((BinaryNode) node).getLeft()) + countVariables(((BinaryNode) node).getRight());
		return 0;
	}

	private boolean compareVariables(Node left, Node right) {
		if(left instanceof Variable && right instanceof Variable)
			return ((Variable) left).getName().compareTo(((Variable) right).getName()) > 0;
		if(left instanceof Power)
			return compareVariables(((Power) left).getLeft(), right);
		if(right instanceof Power)
			return compareVariables(left, ((Power) right).getLeft());
		if(left instanceof Multiply && right instanceof Multiply)
			return compareVariables(((Multiply) left).getLeft(), ((Multiply) right).getLeft()) || compareVariables(((Multiply) left).getRight(), ((Multiply) right).getRight());
		return false;
	}

	private boolean shouldSwap(Node left, Node right) {
		if(!(left instanceof Number) && right instanceof Number)
			return false;
		if(left instanceof Number && right instanceof Variable)
			return true;
		if(left instanceof Variable && right instanceof Power)
			return true;
		if(countVariables(left) < countVariables(right))
			return true;
		if(countVariables(left) == countVariables(right))
			return compareVariables(left, right);
		if(getVariable(left).getName().compareTo(getVariable(right).getName()) > 0)
			return true;
		
		return false;
	}

	@Override
	public Node expand() {
		Node left = getLeft().expand();
		Node right = getRight().expand();

		// constant folding
		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue() + ((Number) right).getValue());
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

		// make tree left leaning
		if(right instanceof Add)
			return new Add(new Add(left, ((Add) right).getLeft()), ((Add) right).getRight());

		// sort terms
		if(!(left instanceof Add) && shouldSwap(left, right))
			return new Add(right, left).expand();
		if(left instanceof Add && shouldSwap(((Add) left).getRight(), right))
			return new Add(new Add(((Add) left).getLeft(), right), ((Add) left).getRight()).expand();

		if(left instanceof Divide && right instanceof Divide){
			Divide newLeft = new Divide(new Multiply(((Divide) left).getLeft(), ((Divide) right).getRight()), new Multiply(((Divide) left).getRight(), ((Divide) right).getRight()));
			Divide newRight = new Divide(new Multiply(((Divide) right).getLeft(), ((Divide) left).getRight()), new Multiply(((Divide) right).getRight(),((Divide) left).getRight()));
			return new Divide(new Add(newLeft.getLeft(), newRight.getLeft()), newLeft.getRight()).expand();
		}

		return new Add(left, right);
	}

	@Override
	public Node collapse() {
		Node left = getLeft().collapse();
		Node right = getRight().collapse();

		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue() + ((Number) right).getValue());

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
		}

		return new Add(left, right);
	}
}
