package holworthy.maths;

import java.util.LinkedHashMap;

import holworthy.maths.nodes.Node;

public class Testing {

	private static void runTests(LinkedHashMap<String, String> tests) throws Exception {
		boolean passingAll = true;
		for(String test : tests.keySet()) {
			Node before = Maths.parseInput(test);
			Node after = Maths.parseInput(tests.get(test));
			boolean passed = before.simplify().matches(after);
			if(!passed)
				passingAll = false;
			System.out.println((passed ? "[PASS]" : "[FAIL]") + ": " + before + ", " + after + ", " + before.simplify());
		}
		System.out.println(passingAll ? "[PASSED]" : "[FAILED]");
	}
	public static void main(String[] args) throws Exception {
		LinkedHashMap<String, String> tests = new LinkedHashMap<>();

		// basic arithmetic
		tests.put("1+2", "3");
		tests.put("7-5", "2");
		tests.put("7*10", "70");
		tests.put("8/2", "4");
		tests.put("2^3", "8");
		tests.put("1/4*5", "5/4");

		// basic algebra
		tests.put("3*a+3*b", "3*(a+b)");
		tests.put("3*x+5*x", "8*x");

		// run the tests
		runTests(tests);
	}
}
