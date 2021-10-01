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
	public Node simplify() {
		Node left = getLeft().simplify();
		Node right = getRight().simplify();

		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue() + ((Number) right).getValue());

		if(right instanceof Add)
			return new Add(new Add(left, ((Add) right).getLeft().simplify()).simplify(), ((Add) right).getRight().simplify());

		if(left instanceof Multiply && right instanceof Multiply && ((Multiply) left).getRight().matches(((Multiply) right).getRight()))
			return new Multiply(new Add(((Multiply) left).getLeft(), ((Multiply) right).getLeft()).simplify(), ((Multiply) left).getRight());

		if(left instanceof Variable && right instanceof Variable && left.matches(right))
			return new Multiply(new Number(2), left);

		// if(left instanceof Multiply && right instanceof Multiply){
		// 	if(((BinaryNode) left).getLeft() instanceof Number && ((BinaryNode) left).getRight() instanceof Variable && ((BinaryNode) right).getLeft() instanceof Number && ((BinaryNode) right).getRight() instanceof Variable && ((BinaryNode) left).getRight().matches(((BinaryNode) right).getRight())){
		// 		Add a = new Add(((BinaryNode) left).getLeft(), ((BinaryNode) right).getLeft());
		// 		return new Multiply(a.simplify(), ((BinaryNode) left).getRight());
		// 	}
		// 	if(left.getLeft() instanceof Number)
		// }

		if(left instanceof Multiply && right instanceof Multiply){
			if(((BinaryNode) left).getRight().matches(((BinaryNode) right).getRight())){
				Add a = new Add(((BinaryNode) left).getLeft(), ((BinaryNode) right).getLeft());
				return new Multiply(a.simplify(), ((BinaryNode) right).getRight());
			}
			if(((BinaryNode) left).getLeft().matches(((BinaryNode) right).getLeft())){
				Add a = new Add(((BinaryNode) left).getRight(), ((BinaryNode) right).getRight());
				return new Multiply(a.simplify(), ((BinaryNode) right).getLeft());
			}
		}

		return new Add(left, right);
	}
}
