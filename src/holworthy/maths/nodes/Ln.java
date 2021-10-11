package holworthy.maths.nodes;

import holworthy.maths.nodes.constant.E;

public class Ln extends Log {
	public Ln(Node node) {
		super(node, new E());
	}
}
