package calculator;

import static calculator.CalculatorToken.Type.COMMA;
import static calculator.CalculatorToken.Type.EOF;
import static calculator.CalculatorToken.Type.LEFT_PAREN;
import static calculator.CalculatorToken.Type.RIGHT_PAREN;
import calculator.CalculatorToken.IntegerOperand;
import calculator.CalculatorToken.KeywordToken;

/**
 * Interacts with the underlying {@link CalculatorCharacterStream} to produce
 * {@link CalculatorToken}s.
 * 
 * Used by the {@link CalculatorTokenStream}
 * 
 * @author John Steele <programjsteele@gmail.com>
 */
public class CalculatorScanner {

	/**
	 * The character representing white space.
	 */
	public final static char WHITE_SPACE  = ' ';
	
	/**
	 * The underlying character-by-character stream we read from.
	 */
	private CalculatorCharacterStream calculatorCharacterStream;
	
	/**
	 * Creates a new CalculatorScanner instance using the
	 * provided input for the underlying character stream.
	 * 
	 * @param input The input to be used by the underlying character stream.
	 */
	public CalculatorScanner(String input) {
		calculatorCharacterStream = new CalculatorCharacterStream(input);
	}
	
	/**
	 * Reads characters from the underlying character stream character-by-character
	 * while trying to form a valid token after each advancement in the input stream.
	 * 
	 * @return the next input Token
	 * 
	 * @throws Error If a lexical error occurs.
	 */
	public CalculatorToken scanNextToken() {
		skipWhitespace();
		
		// Check if we've reach the end of the input.
		if (calculatorCharacterStream.isEOF()) {
			return new CalculatorToken(EOF);
		}
		
		// If the next character is a positive number
		if (CalculatorUtil.isDigit(calculatorCharacterStream.peek())) {
			// scan the numbers into a token.
			return scanIntegerToken("");
		}
		
		// Check for negative integer, parenthesis, or comma.
		switch (calculatorCharacterStream.peek()) {
			case '(': {
				calculatorCharacterStream.advance();
				return new CalculatorToken(LEFT_PAREN);
			}
			case ')': {
				calculatorCharacterStream.advance();
				return new CalculatorToken(RIGHT_PAREN);
			}
			case ',': {
				calculatorCharacterStream.advance();
				return new CalculatorToken(COMMA); 
			}
			case '-': {
				calculatorCharacterStream.advance();
				return scanIntegerToken("-");
			}
		}
		
		// Lastly, check for keyword.
		if (CalculatorUtil.isValidIdentifierCharacter(calculatorCharacterStream.peek())) {
			return scanKeywordToken();
		}
	
		throw new Error("Lexical error - Cannot match character sequence to a terminal.");
	}
	
	/**
	 * Our parser does not recognize white space, so here we advance 
	 * the character input stream's next character until the next 
	 * character is not whitespace. 
	 */
	private void skipWhitespace() {
		while (calculatorCharacterStream.peek() == WHITE_SPACE) {
			calculatorCharacterStream.advance();
		}
	}
	
	/**
	 * Reads the consecutive digits from the input stream to 
	 * form the integer token.
	 * 
	 * @return the token representing the scanned digits. 
	 */
	private IntegerOperand scanIntegerToken(String sequence) {
		while (CalculatorUtil.isDigit(calculatorCharacterStream.peek())) {
			sequence += calculatorCharacterStream.advance();
		}
		try {
			int value = CalculatorUtil.createInteger(sequence);
			return new IntegerOperand(value);
		}
		catch (ArithmeticException e) {
			throw new Error("Lexical error - Integer overflow for value: " + sequence);
		}
	}
	
	/**
	 * Reads the consecutive keyword characters from the input stream
	 * to form the keyword token.
	 * 
	 * @return the token representing the scanned keyword.
	 */
	private CalculatorToken scanKeywordToken() {
		String sequence = "";
		while (CalculatorUtil.isValidIdentifierCharacter(calculatorCharacterStream.peek())) {
			sequence += calculatorCharacterStream.advance();
		} 
		return new KeywordToken(sequence);
	}
}
