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
	}
}