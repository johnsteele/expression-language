package calculator;

import static calculator.CalculatorToken.Type.ADD;
import static calculator.CalculatorToken.Type.COMMA;
import static calculator.CalculatorToken.Type.DIVIDE;
import static calculator.CalculatorToken.Type.EOF;
import static calculator.CalculatorToken.Type.INTEGER;
import static calculator.CalculatorToken.Type.KEYWORD;
import static calculator.CalculatorToken.Type.LEFT_PAREN;
import static calculator.CalculatorToken.Type.MULTIPLY;
import static calculator.CalculatorToken.Type.RIGHT_PAREN;
import static calculator.CalculatorToken.Type.SUBTRACT;

import java.util.HashMap;
import java.util.Map;

import calculator.CalculatorToken.Expression;
import calculator.CalculatorToken.IExpression;
import calculator.CalculatorToken.IOperand;
import calculator.CalculatorToken.IntegerOperand;
import calculator.CalculatorToken.KeywordToken;
import calculator.CalculatorToken.LetExpression;
import calculator.CalculatorToken.OperandPair;
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
		}
		
		// Match the scanned keyword to an operation.
		KeywordToken keywordToken = (KeywordToken)token;
		Type next = Type.getTypeWithSequence(keywordToken.getKeyword());
		
		// The operation must be one of the following:
		switch (next) {
			case ADD:      expression = newExpression(ADD, scope); break;
			case SUBTRACT: expression = newExpression(SUBTRACT, scope); break;
			case MULTIPLY: expression = newExpression(MULTIPLY, scope); break;
			case DIVIDE:   expression = newExpression(DIVIDE, scope); break;
			case LET:      expression = letExpression(scope); break;
			default : {
				error("Operation not supported. Expected add, sub, mult, div, let");
			}
		}
		return expression;
	}
	
	/**
	 * Creates a new {@link Expression} with the specified type. The scope
	 * is used to build the left and right operands of the expression.
	 * 
	 * @param type the type of expression to create.
	 * @param scope the scope for the left and right operands.
	 * @return the new Expression of the specified type.
	 */
	private IExpression newExpression(Type type, Map<String, IOperand> scope) {
		OperandPair pair = operandPair(scope);
		return new Expression(type, pair.getLeftOperand(), pair.getRightOperand());
	}

	/**
	 * Parses and returns an {@link OperandPair} that wrap the 
	 * left and right operands.
	 * 
	 * @param scope the scope available to the operands.
	 * @return the pair containing the left and right operands.
	 */
	private OperandPair operandPair(Map<String, IOperand> scope) {
		expect(KEYWORD);
		expect(LEFT_PAREN);
		IOperand leftOperand = operand(scope);
		expect(COMMA);
		IOperand rightOperand = operand(scope);
		expect(RIGHT_PAREN);
		return new OperandPair(leftOperand, rightOperand);
	}
	
	/**
	 * Parses and returns an operand. The operand returned
	 * will be one of type integer, expression, or keyword.
	 * 
	 * @param scope The scope available to the operand.
	 * @return The operand corresponding to the type.
	 */
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
				expression = parseLetExpressionOperands(scope, variableKeywordToken);
			}
		}
		if (expression == null) {
			error("error parsing let expression");
		}
		return expression;
	}
	
	/**
	 * Completes the parsing of the {@link LetExpression} using the provided scope and token.
	 * 
	 * @param scope the scope available to the expression.
	 * @param variableKeywordToken the variable token.
	 * @return the new expression, or null if the expression had parsing errors.
	 * @throws Error if parsing failed.
	 */
	private IExpression parseLetExpressionOperands(Map<String, IOperand> scope, KeywordToken variableKeywordToken) {
		
		IExpression expression = null;
		
		// Variable value expression
		CalculatorToken valueToken = tokenStream.peekToken();
		Type valueType = valueToken.getType();
		
		// Could be a Integer value or an expression.
		if (valueType == INTEGER || valueType == KEYWORD) {
			
			IOperand leftOperand = null;
			
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
			IOperand rightOperand = expression(new HashMap<String, IOperand>(scope));
			expect(RIGHT_PAREN);
			expression = new LetExpression(variableKeywordToken.getKeyword(), leftOperand, rightOperand);
		}
		
		return expression;
	}
	
	/**
	 * A helper method that throws an error if the next
	 * token returned from the token stream is not what
	 * we'd expect.
	 * 
	 * @param type The expected Token type.
	 * @throws Error if the token stream doesn't return the type we expect.
	 */
	private void expect(Type type) {
		CalculatorToken t = tokenStream.advance();
		if (t.getType() != type) {
			error("Expected type " + type.getKeyword() + ", but instead was type " + t.getType().getKeyword());
		}
	}

	/***
	 * Throws an error. If we later on introduce backtracking we might want to
	 * log this error instead of bailing.
	 * 
	 * @param message the message to be included in the error.
	 * @throws Error the error with the specified error message.
	 */
	private void error(String message) throws Error {
		throw new Error(message);
	}
}
