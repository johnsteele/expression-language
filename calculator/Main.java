package calculator;

import java.util.logging.Logger;

import calculator.CalculatorToken.IExpression;

/**
 * A calculator program that supports a basic integer expression language.
 * 
 * Warning: This program has not been vigorously tested, use at your own risk.
 * 
 * @author John Steele <programjsteele@gmail.com>
 */
public class Main {
	
	/**
	 * For reporting parser errors.
	 */
	private static final Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());
	
	public static void main(String[] args) {
		if (args.length == 1) {
			Main.processExpression(args[0]);
		}
		else {
			System.out.println("Example usage: java calculator.Main add(5, 5)");
		}
	}
	
	/**
	 * Takes the provided expression, feeds it to the parser, and outputs
	 * evaluated result to the console.
	 * 
	 * @param expression the expression to parse.
	 */
	private static void processExpression(String expression) {
		try {
			IExpression result = new CalculatorParser(expression).run();
			System.out.println(result.evaluate());
		}
		catch (Throwable e) {
			LOGGER.severe("Parser exited with error: " + e.getMessage());
		}
	}
}
