package calculator;

import static calculator.CalculatorToken.Type.ADD;
import static calculator.CalculatorToken.Type.COMMA;
import static calculator.CalculatorToken.Type.DIVIDE;
import static calculator.CalculatorToken.Type.EOF;
import static calculator.CalculatorToken.Type.INTEGER;
import static calculator.CalculatorToken.Type.KEYWORD;
import static calculator.CalculatorToken.Type.LEFT_PAREN;
import static calculator.CalculatorToken.Type.LET;
import static calculator.CalculatorToken.Type.MULTIPLY;
import static calculator.CalculatorToken.Type.RIGHT_PAREN;
import static calculator.CalculatorToken.Type.SUBTACT;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import calculator.CalculatorToken.AddExpression;
import calculator.CalculatorToken.DivideExpression;
import calculator.CalculatorToken.IExpression;
import calculator.CalculatorToken.IOperand;
import calculator.CalculatorToken.IntegerOperand;
import calculator.CalculatorToken.KeywordToken;
import calculator.CalculatorToken.LetExpression;
import calculator.CalculatorToken.MultiplyExpression;
import calculator.CalculatorToken.OperandPair;
import calculator.CalculatorToken.SubtractExpression;
import calculator.CalculatorToken.Type;
import calculator.CalculatorToken.VariableOperand;

/**
 * Represents a basic parser that reads tokens from the calculator token stream,
 * and attempts to match tokens to grammatical elements of the 
 * calculator expression language.
 * 
 * Calculator operations (add, sub, mult, div, let) are all reserved keywords.
 * Variable names declared within the let operation cannot have the same 
 * name as any of the reserved keywords.
 *
 * @author John Steele <programjsteele@gmail.com>
 */
public class CalculatorParser {

	/**
	 * For reporting parse information and errors.
	 */
	private static final Logger LOGGER = Logger.getLogger(CalculatorParser.class.getSimpleName());

	/**
	 * The calculator token stream this parser reads from.
	 */
	private CalculatorTokenStream tokenStream;

	/**
	 * Creates a new Parser instance with the specified input.
	 * 
	 * @param input the text this parser will be reading from and parsing.
	 */
	public CalculatorParser(String input) {
		tokenStream = new CalculatorTokenStream(input);
	}

	/**
	 * Starts the parser. This is the main entry point to the parser.
	 * 
	 * @Throws Error if parsing the input fails.
	 */
	public IExpression run() {
		IExpression compilationUnit = null;
		if (tokenStream.peek() == KEYWORD) {
			compilationUnit = expression(new HashMap<String, IOperand>());
			expect(EOF);
		}
		if (compilationUnit == null) {
			error("expected add, sub, mult, div, let");
		}
		return compilationUnit;
	}
	
	/**
	 * Parses and returns an {@link IExpression} representing
	 * one of the following:
	 * <ul>
	 * 	<li>{@link AddExpression}</li>
	 *  <li>{@link SubtractExpression}</li>
	 *  <li>{@link MultiplyExpression}</li>
	 *  <li>{@link DivideExpression}</li>
	 *  <li>{@link LetExpression}</li>
	 * </ul>
	 * 
	 * @param scope The local scope to the returned expression containing
	 *        variables it has access to.
	 * @return a new expression instances corresponding to the appropriate type.
	 */
	private IExpression expression(Map<String, IOperand> scope) {
		IExpression expression = null;
		
		CalculatorToken token = tokenStream.peekToken();
		
		// If the scanner failed, or we're not looking at a keyword.
		if (token == null || !(token instanceof KeywordToken)) {
			error("Parsing expression failed.");
			return null;
		}
		
		// Match the scanned keyword to an operation.
		KeywordToken keywordToken = (KeywordToken)token;
		Type next = Type.getTypeWithSequence(keywordToken.getKeyword());
		
		// The operation must be one of the following:
		if (next == ADD) {
			expression = addExpression(scope);
		}
		else if (next == SUBTACT) {
			expression = subtractExpression(scope);
		}
		else if (next == MULTIPLY) {
			expression = multiplyExpression(scope);
		}
		else if (next == DIVIDE) {
			expression = divideExpression(scope);
		}
		else if (next == LET) {
			expression = letExpression(scope);
		}
		// Operation not supported by our language.
		else {
			error("Operation not supported. Expected add, sub, mult, div, let");
		}
		return expression;
	}
	
	private IExpression addExpression(Map<String, IOperand> scope) {
		OperandPair pair = operandPair(scope);
		return new AddExpression(pair.getLeftOperand(), pair.getRightOperand());
	}
	
	private IExpression subtractExpression(Map<String, IOperand> scope) {
		OperandPair pair = operandPair(scope);
		return new SubtractExpression(pair.getLeftOperand(), pair.getRightOperand());
	}
	
	private IExpression multiplyExpression(Map<String, IOperand> scope) {
		OperandPair pair = operandPair(scope);
		return new MultiplyExpression(pair.getLeftOperand(), pair.getRightOperand());
	}
	
	private IExpression divideExpression(Map<String, IOperand> scope) {
		OperandPair pair = operandPair(scope);
		return new DivideExpression(pair.getLeftOperand(), pair.getRightOperand());
	}
	
	private OperandPair operandPair(Map<String, IOperand> scope) {
		expect(KEYWORD);
		expect(LEFT_PAREN);
		IOperand leftOperand = operand(scope);
		expect(COMMA);
		IOperand rightOperand = operand(scope);
		expect(RIGHT_PAREN);
		return new OperandPair(leftOperand, rightOperand);
	}
	
	private IOperand operand(Map<String, IOperand> scope) {
		IOperand operand = null;
		
		CalculatorToken peekToken = tokenStream.peekToken();
		Type peekType = peekToken.getType();
		
		if(peekType == INTEGER) {
			operand = (IntegerOperand)tokenStream.advance();
		}
		// A scope variable or an expression
		else if (peekType == KEYWORD) {
			// Check if the keyword is an operation.
			KeywordToken keywordToken = (KeywordToken)peekToken;
			Type operation = Type.getTypeWithSequence(keywordToken.getKeyword());
			// If operation, operand is an embedded expression.
			if (operation != null) {
				operand = expression(scope);
			}
			// Otherwise, check for a variable from within the scope.
			else if (scope.containsKey(keywordToken.getKeyword())){
				expect(KEYWORD);
				operand = new VariableOperand(keywordToken.getKeyword(), 
						scope.get(keywordToken.getKeyword()));
			}
		}
		
		if (operand == null) {
			error("error parsing operand");
		}
		return operand;
	}
	
	/**
	 * Parses and returns the {@link LetExpression} grammatical rule.
	 * 
	 * @param scope the local scope for the let function.
	 * @return the initialized let expression.
	 * 
	 * @throws Error If parsing the let expression fails.
	 */
	private IExpression letExpression(Map<String, IOperand> scope) {
		// new LetExpression(variable, leftOperand, rightOperand)
		IOperand leftOperand = null;
		IOperand rightOperand = null;
		
		IExpression expression = null;
		expect(KEYWORD);
		expect(LEFT_PAREN);
		CalculatorToken variableToken = tokenStream.advance();
		Type variableType = variableToken.getType();
		// Variable declaration.
		if (variableType == KEYWORD && tokenStream.peek() == COMMA) {
			KeywordToken variableKeywordToken = (KeywordToken)variableToken;
			if(scope.get(variableKeywordToken.getKeyword()) == null) {
				expect(COMMA);
				
				// Variable value expression
				CalculatorToken valueToken = tokenStream.peekToken();
				Type valueType = valueToken.getType();
				
				// Could be a Integer value or an expression.
				if (valueType == INTEGER || valueType == KEYWORD) {
					
					// Check Integer 
					if (valueType == INTEGER && valueToken instanceof IntegerOperand) {
						tokenStream.advance();
						leftOperand = (IntegerOperand)valueToken;
					}
					// Otherwise, operation keyword
					else if (valueType == KEYWORD) {
						leftOperand = expression(new HashMap<String, IOperand>(scope));
					}
					
					// The operand responsible for evaluating the variable's value.
					IOperand variableEvaluationOperand = new VariableOperand(variableKeywordToken.getKeyword(), leftOperand);
					// Now we add it to the scope.
					scope.put(variableKeywordToken.getKeyword(), variableEvaluationOperand);

					expect(COMMA);
					
					// Expression where the variable is used, the variable is in the scope.
					rightOperand = expression(new HashMap<String, IOperand>(scope));
					expect(RIGHT_PAREN);
					expression = new LetExpression(variableKeywordToken.getKeyword(), leftOperand, rightOperand);
				}
			}
		}
		if (expression == null) {
			error("error parsing let expression");
		}
		return expression;
	}
	
	private void expect(Type type) {
		CalculatorToken t = tokenStream.advance();
		if (t.getType() != type) {
			throw new Error("Expected type " + type.getKeyword() + ", but instead was type " + t.getType().getKeyword());
		}
	}

	private void error(String message) {
		LOGGER.severe(message);
		throw new Error(message);
	}
}