package holworthy.maths.nodes;

import java.util.HashMap;

import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.constant.ConstantNode;
import holworthy.maths.nodes.constant.I;

public class Multiply extends BinaryNode {
	public Multiply(Node left, Node right) {
		super(left, right);
	}

	@Override
	public String toString() {
		return ((getLeft() instanceof Add || getLeft() instanceof Subtract) ? "(" + getLeft() + ")" : getLeft()) + "*" + ((getRight() instanceof Add || getRight() instanceof Subtract) ? "(" + getRight() + ")" : getRight());
	}

	@Override
	public Node copy() {
		return new Multiply(getLeft().copy(), getRight().copy());
	}

	@Override
	public Node expand() throws MathsInterpreterException{
		Node left = getLeft().expand();
		Node right = getRight().expand();

		// make tree left leaning
		if(right instanceof Multiply)
			return new Multiply(new Multiply(left, ((Multiply) right).getLeft()), ((Multiply) right).getRight()).expand();

		// constant folding
		if(left instanceof Number && right instanceof Number)
			return new Number(((Number) left).getValue().multiply(((Number) right).getValue()));
		if(left.matches(new Number(0)))
			return new Number(0);
		if(right.matches(new Number(0)))
			return new Number(0);
		if(left.matches(new Number(1)))
			return right;
		if(right.matches(new Number(1)))
			return left;
		if(left.matches(new Negative(new Number(1))))
			return new Negative(right).expand();
		if(right.matches(new Negative(new Number(1))))
			return new Negative(left).expand();

		// negative handling
		if(left instanceof Negative && right instanceof Negative)
			return new Multiply(((UnaryNode) left).getNode(), ((UnaryNode) right).getNode());
		if(left instanceof Negative && ((UnaryNode) left).getNode() instanceof Number && right instanceof Number)
			return new Negative(new Multiply(((UnaryNode) left).getNode(), right)).expand();
		if(right instanceof Negative)
			return new Multiply(new Negative(left), ((UnaryNode) right).getNode()).expand();

		// x*x = x^2
		if(left instanceof Variable && left.matches(right))
			return new Power(left, new Number(2)).expand();
		// a*b*b = a*b^2
		if(left instanceof Multiply && ((Multiply) left).getRight() instanceof Variable && ((Multiply) left).getRight().matches(right))
			return new Multiply(((Multiply) left).getLeft(), new Power(((Multiply) left).getRight(), new Number(2))).expand();
		// x*x^n = x^(n+1)
		if(left instanceof Variable && right instanceof Power && ((Power) right).getLeft().matches(left))
			return new Power(left, new Add(new Number(1), ((Power) right).getRight())).expand();
		if(left instanceof Multiply && right instanceof Variable && ((BinaryNode) left).getRight() instanceof Power && ((BinaryNode) ((BinaryNode) left).getRight()).getLeft().matches(right))
			return new Multiply(((BinaryNode) left).getLeft(), new Power(right, new Add(new Number(1), ((BinaryNode) ((BinaryNode) left).getRight()).getRight()))).expand();
		// a*x*x^n = a*x^(n+1)
		if(left instanceof Multiply && ((Multiply) left).getRight() instanceof Variable && right instanceof Power && ((Power) right).getLeft().matches(((Multiply) left).getRight()))
			return new Multiply(((Multiply) left).getLeft(), new Power(((Multiply) left).getRight(), new Add(new Number(1), ((Power) right).getRight()))).expand();
		// x^a*x^b = x^(a+b)
		if(left instanceof Power && right instanceof Power && ((Power) left).getLeft().matches(((Power) right).getLeft()))
			return new Power(((BinaryNode) left).getLeft(), new Add(((Power) left).getRight(), ((Power) right).getRight())).expand();
		// // c*x^a*x^b = c*x^(a+b)
		if(left instanceof Multiply && ((Multiply) left).getRight() instanceof Power && right instanceof Power && ((Power) ((Multiply) left).getRight()).getLeft().matches(((Power) right).getLeft()))
			return new Multiply(((Multiply) left).getLeft(), new Power(((Power) ((Multiply) left).getRight()).getLeft(), new Add(((Power) ((Multiply) left).getRight()).getRight(), ((Power) right).getRight()))).expand();

		// expanding brackets
		if(right instanceof Add)
			return new Add(new Multiply(left, ((Add) right).getLeft()), new Multiply(left, ((Add) right).getRight())).expand();
		// putting brackets on the right (which then calls the rule above)
		if(left instanceof Add)
			return new Multiply(right, left).expand();

		// sort terms
		if(shouldSwap(left, right))
			return new Multiply(right, left);
		else if(left instanceof Power && right instanceof Variable)
			return new Multiply(right, left).expand();
		// swap with lower multiply nodes
		if(left instanceof Multiply){
			if(shouldSwap(((BinaryNode) left).getRight(), right))
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

	public boolean shouldSwap(Node left, Node right) {
		// swap numbers and other constants
		if(left instanceof ConstantNode && right.isConstant() && !(right instanceof ConstantNode) && !(right instanceof FunctionNode))
			return true;
		// swap variables and numbers
		if((left instanceof Variable && (right instanceof Number || right instanceof Negative)) || (left instanceof Power && (right instanceof Number || right instanceof Negative)))
			return true;
		// swap powers by variables
		if(left instanceof Variable && right instanceof Power && ((BinaryNode) right).getLeft() instanceof Variable && left.getName().compareTo(((Variable) ((BinaryNode) right).getLeft()).getName()) > 0)
			return true;
		if(left instanceof Power && right instanceof Power && ((BinaryNode) right).getLeft() instanceof Variable && ((BinaryNode) left).getLeft() instanceof Variable && ((BinaryNode) left).getLeft().getName().compareTo(((BinaryNode) right).getLeft().getName()) > 0)
			return true;
		// swap variables
		if(left instanceof Variable && right instanceof Variable)
			if (left.getName().compareTo(right.getName()) > 0)
				return true;
		if(left instanceof ConstantNode && right instanceof ConstantNode)
			return left.toString().compareTo(right.toString()) > 0;
		// swap functions and constants
		if(left instanceof FunctionNode && right.isConstant())
			return true;
		// swap functions and variables
		if(left instanceof FunctionNode && right instanceof Variable)
			return true;
		// sort functions alphabetically
		if(left instanceof FunctionNode && right instanceof FunctionNode)
			return left.getName().compareTo(right.getName()) > 0;

		return false;
	}

	@Override
	public Node collapse() throws MathsInterpreterException{
		Node left = getLeft().collapse();
		Node right = getRight().collapse();

		if(left instanceof Negative)
			return new Negative(new Multiply(((UnaryNode) left).getNode(), right)).collapse();
		if(right instanceof Negative)
			return new Negative(new Multiply(left, ((UnaryNode) right).getNode())).collapse();
		// x * y/z = (x*y)/z
		if (right instanceof Divide && !(left instanceof Divide))
			return new Divide(new Multiply(left, ((BinaryNode) right).getLeft()).expand(), ((BinaryNode) right).getRight()).collapse();
		if (left instanceof Divide && !(right instanceof Divide))
			return new Divide(new Multiply(((BinaryNode) left).getLeft(), right).expand(), ((BinaryNode) left).getRight()).collapse();
		// x/y * z/w = x*z / y*w
		if (left instanceof Divide && right instanceof Divide)
			return new Divide(new Multiply(((BinaryNode) left).getLeft(), ((BinaryNode) right).getLeft()).expand(), new Multiply(((BinaryNode) left).getRight(), ((BinaryNode) right).getRight()).expand()).collapse();
		// x^-1 * y^-1 = (x*y)^-1
		if (left instanceof Power && right instanceof Power && ((BinaryNode) left).getRight().matches(((BinaryNode) right).getRight()))
			return new Power(new Multiply(((BinaryNode) left).getLeft(), ((BinaryNode) right).getLeft()), ((BinaryNode) left).getRight()).collapse();
		// x * y^-1 = x/y
		if(right instanceof Power && ((BinaryNode) right).getRight() instanceof Negative)
			return new Divide(left, new Power(((BinaryNode) right).getLeft(), ((UnaryNode) ((BinaryNode) right).getRight()).getNode())).expand().collapse();
		// x^-1 * y = y/x
		if(left instanceof Power && ((BinaryNode) left).getRight() instanceof Negative)
			return new Divide(right, new Power(((BinaryNode) left).getLeft(), ((UnaryNode) ((BinaryNode) left).getRight()).getNode())).expand().collapse();

		return new Multiply(left, right);
	}

	@Override
	public Node differentiate(Variable wrt) throws MathsInterpreterException {
		return new Add(new Multiply(getLeft().differentiate(wrt), getRight()), new Multiply(getLeft(), getRight().differentiate(wrt))).simplify();
	}

	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return getLeft().evaluate(values) * getRight().evaluate(values);
	}

	@Override
	public Node replace(Node before, Node after) {
		if(matches(before))
			return after;
		return new Multiply(getLeft().replace(before, after), getRight().replace(before, after));
	}
}
