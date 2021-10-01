package holworthy.maths;

import java.util.HashMap;

import holworthy.maths.nodes.Node;

public class Testing {
	public static void main(String[] args) throws Exception {
		HashMap<String, String> tests = new HashMap<>();
		tests.put("1+2", "3");
		tests.put("3*a+3*b", "3*(a+b)");

		for(String test : tests.keySet()) {
			Node before = Maths.parseInput(test);
			Node after = Maths.parseInput(tests.get(test));
			System.out.println(before + ", " + after + ", " + before.simplify() + ", " + before.simplify().matches(after));
		}
	}
}
