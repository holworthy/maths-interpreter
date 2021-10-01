package holworthy.maths.nodes;

public class Add extends BinaryNode {
	public Add(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return getLeft() + "+" + getRight();
	}

	@Override
	public Node collapse() {
		Node left = getLeft().collapse();
		Node right = getRight().collapse();

		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue() + ((Number) right).getValue());

		if(right instanceof Add)
			return new Add(new Add(left, ((Add) right).getLeft().collapse()).collapse(), ((Add) right).getRight().collapse());

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
