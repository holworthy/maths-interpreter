package holworthy.maths;

public class Parser {
	private String input;
	private int cursor = 0;

	public Parser(String input) {
		this.input = input;
	}

	public String getInput() {
		return input;
	}

	public int getCursor() {
		return cursor;
	}

	public void incrementCursor() {
		cursor++;
	}

	public char getChar() {
		return input.charAt(cursor);
	}

	public boolean hasMore() {
		return cursor < input.length();
	}
}
