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
		tests.put("49^(3/2)", "343"); // failed 
		tests.put("(1/3)+(2/5)", "11/15"); // doesn't solve

		// basic algebra
		tests.put("3*a+3*b", "3*(a+b)");
		tests.put("3*x+5*x", "8*x");
		// tests.put("6*i+2*j-3*k+5*j+4*k","6*i+7*j+k"); crashes
		tests.put("(z+3)*2","2*z+6");
		tests.put("2*(x+5)", "2*x+10");
		tests.put("2*x+5=15", "x=5"); //doesn't solve
		tests.put("(x+2)^2+3","x^2+4*x+7"); //doesn't do anything
		tests.put("(3*x+2)/(3*x+2)", "1");
		// tests.put("(3*x*2)/(3*x*5)", "(2)/(5)"); crashes



		// run the tests
		runTests(tests);
	}
}
