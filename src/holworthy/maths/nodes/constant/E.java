package holworthy.maths.nodes.constant;

import java.util.HashMap;

import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Variable;

public class E extends ConstantNode {
	@Override
	public double evaluate(HashMap<Variable, Node> values) {
		return Math.E;
	}
}
