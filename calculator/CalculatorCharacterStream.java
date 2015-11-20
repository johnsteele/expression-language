package calculator;

import java.io.CharArrayReader;
import java.io.Reader;
import java.util.logging.Logger;

/**
 * Breaks the input into an array and provides a character by character sequence.
 * 
 * Used by the {@link CalculatorScanner}
 * 
 * @author John Steele <programjsteele@gmail.com>
 */
public class CalculatorCharacterStream {

	/**
	 * For reporting I/O errors.
	 */
	private static final Logger LOGGER = Logger.getLogger(CalculatorCharacterStream.class.getSimpleName());
	
	/**
	 * Flag indicating the end of input stream.
	 * 
	 * @see {@link Reader#read()}
	 */
	private static final int EOS = -1;
	
	/**
	 * The underlying stream we are reading from and traversing.
	 */
	private final Reader inputStream;

	/**
	 * Holds the next character read from the input stream.
	 */
	private char nextCharacter = 0;
	
	/**
	 * A flag indicating the stream has reach the end of file.
	 */
	private boolean eof = false;

	/**
	 * Creates a new CalculatorCharacterStream instance using the
	 * character array produced by the provided input.
	 *  
	 * @param input The input to read and traverse.
	 */
	public CalculatorCharacterStream(String input) {
		this.inputStream = new CharArrayReader(input.toCharArray());
		// prime the first character.
		advance();
	}

	/**
	 * Returns the next character in the input stream. This does not cause
	 * the next character pointer to advance.
	 * 
	 * @return the next character.
	 */
	public char peek() {
		return nextCharacter;
	}
	
	/**
	 * The <code>eof</code> flag will be set to true once we've reached the end
	 * of the input stream or an I/O error occurred while reading the input.
	 * 
	 * @return true if we've reached the end of input, false otherwise.
	 */
	public boolean isEOF() {
		return eof;
	}

	/**
	 * Returns the next character in the input. This method will return the 
	 * same character returned by {@link #peek()}, except it will also cause the
	 * next character to be updated to the next character in the input stream.
	 * If an I/O error occurs while internally reading the next character,
	 * this character stream will be marked as having reached the end of file.
	 * 
	 * @return the next character in the input.
	 */
	public char advance() {
		char advance = nextCharacter;
		try {
			internalAdvance();
		}
		catch (Throwable e) {
			LOGGER.severe("I/O error while reading next input character: " + e.getMessage());
			//  We end input on any I/O error.
			eof = true;
			return 0;
		}
		return advance;
	}
	
	/**
	 * Updates the internally stored <code>nextCharacter</code> to the
	 * next character in the input stream.
	 * 
	 * @throws Throwable If an I/O error occurs
	 */
	private void internalAdvance() throws Throwable {
		int next = inputStream.read();
		if (next == EOS) {
			eof = true;
			nextCharacter = 0;
		}
		else {
			nextCharacter = (char) next;
		}
	}
}
