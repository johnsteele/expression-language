package calculator;

import java.math.BigInteger;

import calculator.CalculatorToken.Type;

/**
 * A utility class used by the scanner and tokens.
 * 
 * @author John Steele <programjsteele@gmail.com>
 */
public class CalculatorUtil {
	
	/**
	 * The message logged when an arithmetic exception occurs.
	 */
	private static final String OPERATION_ERROR_MESSAGE = "Exception while performing operation (%s) with values (%s, %s)";
	
	/**
	 * The message logged when an int is too small.
	 */
	private static final String INT_MIN_MESSAGE = "int too small. int value must not be less than Integer.MIN_VALUE";
	
	/**
	 * The message logged when division is attempted by zero.
	 */
	private static final String DIVIDE_ZERO_MESSAGE = "Division error - cannot divide by zero.";
	
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
	
	/**
	 * Creates an <code>int</code> with the specified value.
	 * 
	 * @param sequence the string representation of the value.
	 * @return the int value.
	 * @throws ArithmeticException if an overflow occurs.
	 */
	public static int createInteger(String sequence) throws ArithmeticException {
		return new BigInteger(sequence).intValueExact();
	}
	
	/**
	 * Tries to safely negate the provided int.
	 * 
	 * @param value the value to negate.
	 * @return the negated value.
	 * @throws Error if negation causes value to be less than Integer.MIN_VALUE
	 */
	public static int safelyNegate(int value) throws Error {
		BigInteger negated = new BigInteger(String.valueOf(value)).negate();
		try {
			int result = negated.intValueExact();
			return result;
		} catch (ArithmeticException e) {
			throw new Error(INT_MIN_MESSAGE);
		}
	}
	
	/**
	 * Tries to safely perform the operation specified by the type.
	 * 
	 * @param type the type of operation.
	 * @param leftValue the left operand in the expression.
	 * @param rightValue the right operand in the expression.
	 * @return the result of the operation if it succeeded.
	 * @throws Error if the operation failed.
	 */
	public static int peformSafeOperation(Type type, int leftValue, int rightValue) throws Error {
		switch (type) {
			case ADD: {
				try {
					int result = Math.addExact(leftValue, rightValue);
					return result;
				} catch (ArithmeticException e) {
					reportOperationError(type, leftValue, rightValue);
				}
			}
			case SUBTACT: {
				try {
					int result = Math.subtractExact(leftValue, rightValue);
					return result;
				} catch (ArithmeticException e) {
					reportOperationError(type, leftValue, rightValue);
				}
			}
			case MULTIPLY: {
				try {
					int result = Math.multiplyExact(leftValue, rightValue);
					return result;
				} catch (ArithmeticException e) {
					reportOperationError(type, leftValue, rightValue);
				}
			}
			case DIVIDE: {
				if (rightValue == 0) {
					throw new Error(DIVIDE_ZERO_MESSAGE);
				}
				BigInteger bigLeft = new BigInteger(String.valueOf(leftValue));
				BigInteger bigRight = new BigInteger(String.valueOf(rightValue));
				BigInteger result = bigLeft.divide(bigRight);
				try {
					int divisionResult = result.intValueExact();
					return divisionResult;
				} catch (ArithmeticException e) {
					reportOperationError(type, leftValue, rightValue);
				}
			}
			default: {
				// do nothing;
			}
		}
		throw new Error("Unsupported operation: " + type.getKeyword());
	}
	
	/**
	 * Helper to report the error associated with the type operation.
	 * 
	 * @param type the type of operation that failed.
	 * @param leftValue the left value used in the operation.
	 * @param rightValue the right value used in the operation. 
	 * @throws Error the error indicating the operation failed.
	 */
	private static void reportOperationError(Type type, int leftValue, int rightValue) throws Error {
		String msg = String.format(OPERATION_ERROR_MESSAGE, type.getKeyword(), leftValue, rightValue);
		throw new Error(msg);
	}
}
