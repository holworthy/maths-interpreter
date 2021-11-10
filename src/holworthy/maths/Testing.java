package holworthy.maths;

import java.util.LinkedHashMap;

import holworthy.maths.nodes.Node;

public class Testing {

	public static void runTests(LinkedHashMap<String, String> tests) throws Exception {
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
		tests.put("19.14+8.256", "6849/250"); 
		tests.put("946.9+381.6", "2657/2");
		tests.put("0.2313+0.7567", "247/250");

		// complex arithmetic
		tests.put("6^6*36^3", "2176782336");
		tests.put("27^6*81^(-7)", "1/59049");
		tests.put("36^-1/36^(-2)", "36");
		tests.put("(22/12)*(30/8)/(34/12)", "165/68");
		tests.put("(46/18)/(22/9)/(24/7)","161/528");

		// basic algebra
		tests.put("3*a+3*b", "3*(a+b)");
		tests.put("3*x+5*x", "8*x");
		tests.put("6*l+2*j-3*k+5*j+4*k","7*j+k+6*l");
		tests.put("2*x*5*y*7*z","70*x*y*z");
		tests.put("(z+3)*2","2*z+6");
		tests.put("2*(x+5)", "2*x+10");
		tests.put("2*x+5=15", "x=5"); //doesn't solve
		tests.put("(x+2)^2+3","x^2+4*x+7");
		tests.put("(3*x+2)/(3*x+2)", "1");
		tests.put("(3*x*2)/(3*x*5)", "(2)/(5)");
		tests.put("a*a", "a^2");
		tests.put("a*a^7", "a^8");
		tests.put("b*a*b^3*c*a^2", "a^3*b^4*c");
		tests.put("(x+3)*(x-2)", "x^2+x-6");
		tests.put("2*j+3*k+10*k+9*j+8*j", "19*j+13*k");
		tests.put("-8*t+6*v-t-7*v+8*v", "-(9*t)+7*v");
		tests.put("k-9*k-5*n+3*m-k", "-(9*k)+3*m-5*n");
		tests.put("5*(4*h-7)-4*(3*h-6)", "8*h-11");
		tests.put("8*(7*x^2-8*y)-8*(3*x^2-9*y)", "8*(4*x^2+y)");
		tests.put("5*(13*j-3*k^2)-9*(7*j-6*k^2)", "2*j+39*k^2");
		tests.put("x+x","2*x");
		tests.put("x+2*x", "3*x");
		tests.put("x+-x","0");
		tests.put("2*x+-x","x");
		tests.put("x+-2*x","-x");
		tests.put("2*x+x","3*x");
		tests.put("2*x+2*x","4*x");
		tests.put("2*x-x","x");
		tests.put("3*x-x","2*x");
		tests.put("4*x+-2*x","2*x");
		tests.put("4*x-2*x","2*x");
		tests.put("2*x-3*x","-x");
		tests.put("2*x-4*x","-(2*x)");
		tests.put("-x+x","0");
		tests.put("-x+2*x","x");
		tests.put("-x+-x","-(2*x)");
		tests.put("-x-x","-(2*x)");
		tests.put("-x+-4*x","-(5*x)");
		tests.put("-x-4*x","-(5*x)");
		tests.put("-2*x+x","-x");
		tests.put("-2*x+2*x","0");
		tests.put("-2*x+3*x","x");
		tests.put("-2*x-x","-(3*x)");
		tests.put("-2*x-2*x","-(4*x)");
		tests.put("(x+5)*(a-6)","a*(x + 5) - 6*x - 30");
		tests.put("5*(-3*x-2)-(x-3)=-4*(4*x+5)+13","0=0"); //TODO: probably shouldnt just kill program

		// negatives
		tests.put("-5", "-5");
		tests.put("5-2", "3");
		tests.put("2-5", "-3");
		tests.put("-5+2", "-3");
		tests.put("-5+7", "2");
		tests.put("-5-2", "-7");
		tests.put("-5-7", "-12");
		tests.put("-1--5", "4");

		// division
		tests.put("-(2/-3)", "2/3");
		tests.put("-0.7=f/-2", "f=7/5");

		// complex numbers
		tests.put("sqrt(-16)","4*i");
		tests.put("((sqrt(3)+i)/2)^6+((i-sqrt(3))/2)^6","-2");

		// logs
		tests.put("ln(2)+ln(2)", "2*ln(2)");
		
		// run the tests
		runTests(tests);
	}
}
