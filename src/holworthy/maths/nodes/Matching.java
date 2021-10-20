package holworthy.maths.nodes;

public abstract class Matching {
	public static class Constant extends Node {
		@Override
		public boolean isConstant() {
			return true;
		}

		@Override
		public String toString() {
			return "CONSTANT";
		}

		@Override
		public Node copy() {
			return this;
		}

		@Override
		public Node differentiate(Variable wrt) {
			// we can return null here becuase this should never be called
			return null;
		}
	}

	public static class Anything extends Node {
		@Override
		public Node copy() {
			return this;
		}

		@Override
		public boolean isConstant() {
			return false;
		}

		@Override
		public Node differentiate(Variable wrt) {
			return null;
		}
	}
}
