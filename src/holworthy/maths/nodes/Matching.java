package holworthy.maths.nodes;

import holworthy.maths.exceptions.MathsInterpreterException;

public abstract class Matching {
	public static abstract class MatchingNode extends Node {
		@Override
		public boolean isConstant() {
			return false;
		}

		@Override
		public Node copy() {
			return this;
		}

		@Override
		public boolean contains(Variable variable) {
			return false;
		}

		@Override
		public Node differentiate(Variable wrt) throws MathsInterpreterException {
			throw new MathsInterpreterException("Matching classes are not meant to be used!");
		}
	}
	
	public static class Constant extends MatchingNode {
		@Override
		public boolean isConstant() {
			return true;
		}
	}

	public static class Anything extends MatchingNode {
		
	}
}
