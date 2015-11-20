package calculator;

/**
 * A utility class used by the {@link CalculatorScanner}.
 * 
 * @author John Steele <programjsteele@gmail.com>
 */
public class CalculatorUtil {
	
	/**
	 * Returns whether the provided character can be used to 
	 * start an identifier.
	 * 
	 * @param input the character to test.
	 * @return true if can start identifier, false otherwise.
	 */
	public static boolean canStartIdentifier(char input) {
		return !isDigit(input) && isValidIdentifierCharacter(input);
	}

	/**
	 * Returns whether the provided character can be used somewhere
	 * within an identifier's name.
	 * 
	 * @param input the character to test.
	 * @return true if the character can be within an identifier,
	 * false otherwise.
	 */
	public static boolean isValidIdentifierCharacter(char input) {
		if ('a' <= input && input <= 'z') {
			return true;
		}
		if (isDigit(input)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the provided character is a digit (0 to 9).
	 * 
	 * @param input the character to test if is digit.
	 * @return true if input character is 0 .. 9, false otherwise.
	 */
	public static boolean isDigit(char input) {
		return '0' <= input && input <= '9';
	}
}
