package calculator;

import calculator.CalculatorToken.Type;


/**
 * Produces tokens from the underlying {@link CalculatorScanner}.
 * 
 * Used by the {@link CalculatorParser}
 * 
 * @author John Steele <programjsteele@gmail.com>
 */
public class CalculatorTokenStream {

	/**
	 * The scanner that reads from the underlying character stream.
	 */
	private CalculatorScanner scanner;
	
	/**
	 * The last read token from the scanner.
	 */
	private CalculatorToken nextToken;
	
	/**
	 * Creates a new CalculatorTokenStream instance using the
	 * provided input as the input to the underlying scanner.
	 * 
	 * @param input The input to be used by the underlying scanner.
	 */
	public CalculatorTokenStream(String input) {
		scanner = new CalculatorScanner(input);
		// prime the first token.
		advance();
	}
	
	/**
	 * Returns the next token type in the input. This does not cause
	 * the next token to be advanced.
	 * 
	 * @return the next token {@link Type} in the input.
	 */
	public Type peek() {
		return nextToken.getType();
	}
	
	/**
	 * Returns the next token in the input. This does not cause 
	 * the next token to be advanced.
	 * 
	 * @return the next {@link CalculatorToken} in the input.
	 */
	public CalculatorToken peekToken() {
		return nextToken;
	}
	
	/**
	 * Returns the next token in the input. This method will return the 
	 * same token returned by {@link #peek()}, except it will also cause the
	 * next token to be updated to the next token in the input.
	 * 
	 * @return the next token in the input.
	 */
	public CalculatorToken advance() {
		CalculatorToken ans = nextToken;
		internalAdvance();
		return ans;
	}
	
	/**
	 * Advances the underlying token stream to the next token and
	 * stores it in {@link #nextToken}.
	 */
	private void internalAdvance() {
		nextToken = scanner.scanNextToken();
	}
}
