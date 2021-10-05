package holworthy.maths;

import java.util.LinkedHashMap;

import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.Sqrt;

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
		tests.put("49^(3/2)", "343"); 
		tests.put("(1/3)+(2/5)", "11/15");

		// basic algebra
		tests.put("3*a+3*b", "3*(a+b)");
		tests.put("3*x+5*x", "8*x");
		// tests.put("6*i+2*j-3*k+5*j+4*k","6*i+7*j+k"); crashes
		tests.put("(z+3)*2","2*z+6");
		tests.put("2*(x+5)", "2*x+10");
		// tests.put("2*x+5=15", "x=5"); //doesn't solve
		tests.put("(x+2)^2+3","x^2+4*x+7"); //doesn't do anything
		tests.put("(3*x+2)/(3*x+2)", "1");
		// tests.put("(3*x*2)/(3*x*5)", "(2)/(5)"); crashes


		tests.put("a*a", "a^2");
		tests.put("a*a^7", "a^8");
		tests.put("b*a*b^3*c*a^2", "a^3*b^4*c");
		// tests.put("(x+3)*(x-2)", "x^2+x-6");

		// negatives
		tests.put("-5", "-5");
		tests.put("5-2", "3");
		tests.put("2-5", "-3");
		tests.put("-5+2", "-3");
		tests.put("-5+7", "2");
		tests.put("-5-2", "-7");
		tests.put("-5-7", "-12");
		tests.put("-1--5", "4");

		// complex numbers
		System.out.println(new Sqrt(new Negative(new Number(16))).simplify());

		// run the tests
		runTests(tests);
	}
}
