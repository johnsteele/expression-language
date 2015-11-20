package calculator;

import java.util.HashMap;
import java.util.Map;

/**
 * CalculatorToken is used to encapsulate a recognized sequence of characters
 * that form the terminals within the calculator expression language. 
 * 
 * Used within the calculator parser and scanner.
 * 
 * @author John Steele <programjsteele@gmail.com>
 */
public class CalculatorToken {
	
	/**
	 * For reverse lookup purposes e.g. typeMap.get("add") == Type.ADD.
	 */
	private static final Map<String, Type> typeMap = new HashMap<String, Type>();
	
	/**
	 * The type this token represents.
	 */
	private Type type;
	
	/**
	 * Creates a new Token with the specified token type.
	 * 
	 * @param type the type of this token.
	 */
	public CalculatorToken(Type type) {
		this.type = type;
	}
	
	/**
	 * @return the type this token represents.
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * An enum to represent different token types.
	 * 
	 * @author John Steele <programjsteele@gmail.com>
	 */
	public static enum Type {
		ADD("add"),
		SUBTACT("sub"),
		MULTIPLY("mult"),
		DIVIDE("div"),
		LET("let"),
		EOF(""),
		EXPRESSION(""),
		INTEGER(""),
		VARIABLE(""),
		LEFT_PAREN(""),
		RIGHT_PAREN(""),
		COMMA(""),
		KEYWORD("");

		/**
		 * The terminal's keyword string. This maybe an empty string since some terminal 
		 * token ID's are not needed within the parser.
		 */
		private String keyword;
		
		/**
		 * Creates a new Type enum with the specified keyword.
		 * 
		 * @param keyword the keyword this type represents.
		 */
		private Type(String keyword) {
			setId(keyword);
		}
		
		/**
		 * Internal helper that updates the {@link #typeMap}.
		 * 
		 * @param id the keyword 
		 */
		private void setId(String keyword) {
			if (!keyword.isEmpty()) {
				typeMap.put(this.keyword = keyword, this);
			}
		}
		
		/**
		 * Utility method that enables clients to do a reverse lookup
		 * of a Type.
		 * 
		 * @param keyword The keyword to attempt to match to a type.
		 * @return the matched Type, null if the keyword does not match a type.
		 */
		public static Type getTypeWithSequence(String keyword) {
			for (Map.Entry<String, Type> entry : typeMap.entrySet()) {
				if (entry.getKey().equals(keyword)) {
					return entry.getValue();
				}
			}
			return null;
		}
		
		/**
		 * Returns the keyword this type represents.
		 * 
		 * @return this type's keyword.
		 */
		public String getKeyword() {
			return keyword;
		}
	}
	
	/**
	 * AbstractExpression represents the base class for all {@link IExpression}s.
	 * The base class stores the left and right {@link IOperand}s. 
	 * An {@link IExpression} can be evaluated via {@link IEvaluatable}. 
	 * Evaluating expression is done differently depending on the type of expression; 
	 * Therefore, it is the responsibility of the derived base class to implement {@link #evaluate()}. 
	 * 
	 * @author John Steele <programjsteele@gmail.com>
	 */
	public static abstract class AbstractExpression extends CalculatorToken implements IExpression {
		
		/**
		 * The left operand in the expression.
		 */
		private IOperand leftOperand;
		
		/**
		 * The right operand in the expression.
		 */
		private IOperand rightOperand;
		
		/**
		 * Creates a new AbstractExpression with the specified type and left/right operands.
		 * 
		 * @param type The token type this expression represents.
		 * @param leftOperand the expression's left operand.
		 * @param rightOperand the expressions' right operand.
		 */
		public AbstractExpression(Type type, IOperand leftOperand, IOperand rightOperand) {
			super(type);
			this.leftOperand = leftOperand;
			this.rightOperand = rightOperand;
		}
		
		/*
		 * (non-Javadoc)
		 * @see calculator.Token.IEvaluatable#evaluate()
		 */
		@Override
		public abstract int evaluate();
		
		/*
		 * (non-Javadoc)
		 * @see calculator.Token.IExpression#getLeftOperand()
		 */
		@Override
		public IOperand getLeftOperand() {
			return leftOperand;
		}
		
		/*
		 * (non-Javadoc)
		 * @see calculator.Token.IExpression#getRightOperand()
		 */
		@Override
		public IOperand getRightOperand() {
			return rightOperand;
		}
	}
	
	/**
	 * An {@link IExpression} that adds it's left and right operands.
	 * 
	 * @author John Steele <programjsteele@gmail.com>
	 */
	public static class AddExpression extends AbstractExpression {
		
		/**
		 * Creates a new AddExpression instance with the specified
		 * left and right operands.
		 * 
		 * @param leftOperand the expression's left operand.
		 * @param rightOperand the expression's right operand.
		 */
		public AddExpression(IOperand leftOperand, IOperand rightOperand) {
			super (Type.ADD, leftOperand, rightOperand);
		}
		
		/*
		 * (non-Javadoc)
		 * @see calculator.Token.AbstractExpression#evaluate()
		 */
		@Override
		public int evaluate() {
			return getLeftOperand().evaluate() + getRightOperand().evaluate();
		}
	}
	
	/**
	 * An {@link IExpression} that subtracts it's left and right operands.
	 * 
	 * @author John Steele <programjsteele@gmail.com>
	 */
	public static class SubtractExpression extends AbstractExpression {
		
		/**
		 * Creates a new SubtractExpression instance with the specified
		 * left and right operands.
		 * 
		 * @param leftOperand the expression's left operand.
		 * @param rightOperand the expression's right operand.
		 */
		public SubtractExpression(IOperand leftOperand, IOperand rightOperand) {
			super (Type.SUBTACT, leftOperand, rightOperand);
		}
		
		/*
		 * (non-Javadoc)
		 * @see calculator.Token.AbstractExpression#evaluate()
		 */
		@Override
		public int evaluate() {
			return getLeftOperand().evaluate() - getRightOperand().evaluate();
		}
	}
	
	/**
	 * An {@link IExpression} that multiplies it's left and right operands.
	 * 
	 * @author John Steele <programjsteele@gmail.com>
	 */
	public static class MultiplyExpression extends AbstractExpression {
		
		/**
		 * Creates a new MultiplyExpression instance with the specified
		 * left and right operands.
		 * 
		 * @param leftOperand the expression's left operand.
		 * @param rightOperand the expression's right operand.
		 */
		public MultiplyExpression(IOperand leftOperand, IOperand rightOperand) {
			super (Type.MULTIPLY, leftOperand, rightOperand);
		}
		
		/*
		 * (non-Javadoc)
		 * @see calculator.Token.AbstractExpression#evaluate()
		 */
		@Override
		public int evaluate() {
			return getLeftOperand().evaluate() * getRightOperand().evaluate();
		}
	}
	
	/**
	 * An {@link IExpression} that divides it's left and right operands.
	 * 
	 * @author John Steele <programjsteele@gmail.com>
	 */
	public static class DivideExpression extends AbstractExpression {

		/**
		 * Creates a new DivideExpression instance with the specified
		 * left and right operands.
		 * 
		 * @param leftOperand the expression's left operand.
		 * @param rightOperand the expression's right operand.
		 */		
		public DivideExpression(IOperand leftOperand, IOperand rightOperand) {
			super (Type.DIVIDE, leftOperand, rightOperand);
		}
		
		/*
		 * (non-Javadoc)
		 * @see calculator.Token.AbstractExpression#evaluate()
		 */
		@Override
		public int evaluate() {
			int leftValue = getLeftOperand().evaluate();
			int rightValue = getRightOperand().evaluate();
			if (rightValue == 0) {
				throw new Error("Division error - cannot divide by zero.");
			}
			return leftValue / rightValue;
		}
	}
	
	/**
	 * An {@link IExpression} that stores a variable, which can then be used 
	 * within it's right operand.
	 * 
	 * @author John Steele <programjsteele@gmail.com>
	 */
	public static class LetExpression extends AbstractExpression {
		
		/**
		 * The variable this expression defines that can be used within 
		 * the right operand.
		 */
		private String variable;
		
		/**
		 * Creates a new LetExpression instance with the specified
		 * variable name, and the left and right operands.
		 * 
		 * @param variable the declared variable.
		 * @param leftOperand the expression's left operand. This operand is responsible
		 * for evaluating the value the specified variable represents.
		 * @param rightOperand the expression's right operand.
		 */
		public LetExpression(String variable, IOperand leftOperand, IOperand rightOperand) {
			super (Type.LET, leftOperand, rightOperand);
			this.variable = variable;
		}
		
		/*
		 * (non-Javadoc)
		 * @see calculator.Token.AbstractExpression#evaluate()
		 */
		@Override
		public int evaluate() {
			return getRightOperand().evaluate();
		}
		
		/**
		 * @return the variable this let expression has defined.
		 */
		public String getVariable() {
			return variable;
		}
	}
	
	/**
	 * An interface to represent an expression in the calculator language.
	 * 
	 * @author John Steele <programjsteele@gmail.com>
	 */
	public static interface IExpression extends IOperand {
		/**
		 * Returns the left {@link IOperand} of this expression.
		 * 
		 * @return the left operand.
		 */
		IOperand getLeftOperand();
		/**
		 * Returns the right {@link IOperand} of this expression.
		 * 
		 * @return the right operand.
		 */
		IOperand getRightOperand();
	}
	
	/**
	 * An interface to enable evaluating values of {@link IOperand}s.
	 *
	 * @author John Steele <programjsteele@gmail.com>
	 */
	public static interface IEvaluatable {
		/**
		 * Evaluates and returns the result.
		 * 
		 * @return the result of evaluating the {@link IOperand}.
		 */
		int evaluate();
	}
	
	/**
	 * An interface to represent an {@link IExpression}'s operand.
	 * 
	 * @author John Steele <programjsteele@gmail.com>
	 */
	public static interface IOperand extends IEvaluatable {}
	
	/**
	 * An IntegerOperand represents an integer value.
	 * 
	 * @author John Steele <programjsteele@gmail.com>
	 */
	public static class IntegerOperand extends CalculatorToken implements IOperand {
		
		/**
		 * The value this operand represents.
		 */
		private int value;
		
		/**
		 * Creates a new IntegerOperand instance with the specified value.
		 * 
		 * @param value the Integer value.
		 */
		public IntegerOperand(int value) {
			super(Type.INTEGER);
			this.value = value;
		}
		
		/*
		 * (non-Javadoc)
		 * @see calculator.Token.IEvaluatable#evaluate()
		 */
		@Override
		public int evaluate() {
			return getValue();
		}
		
		/**
		 * @return the stored Integer value.
		 */
		public int getValue() {
			return value;
		}
		
		/**
		 * Sets this operand's Integer value to negative.
		 */
		public void setNegative() {
			this.value = -value;
		}
	}
	
	/**
	 * A VariableOperand wraps a variable declaration and stores the {@link IOperand}
	 * that is responsible for computing the value the variable represents.
	 * 
	 * @author John Steele <programjsteele@gmail.com>
	 */
	public static class VariableOperand extends CalculatorToken implements IOperand {
		
		/**
		 * The defined variable.
		 */
		private String variable;
		
		/**
		 * The evaluating operand of the {@link #variable}.
		 */
		private IOperand operand;
		
		/**
		 * Creates a new VariableOperand that wraps the variable name
		 * and stores the evaluating operand.
		 * 
		 * @param variable the variable this VariableOperand wraps.
		 * @param operand the evaluating operand of the defined variable.
		 */
		public VariableOperand(String variable, IOperand operand) {
			super(Type.VARIABLE);
			this.variable = variable;
			this.operand = operand;
		}
		
		/*
		 * (non-Javadoc)
		 * @see calculator.Token.IEvaluatable#evaluate()
		 */
		@Override
		public int evaluate() {
			return operand.evaluate();
		}
		
		/**
		 * @return the variable this variable operand wraps.
		 */
		public String getVariable() {
			return variable;
		}
		
		/**
		 * @return the evaluating operand of the wrapped variable.
		 */
		public IOperand getOperand() {
			return operand;
		}
	}
	
	/**
	 * KeywordToken represents a {@link CalculatorToken} of type {@link Type#KEYWORD},
	 * which stores the value of the keyword.
	 *  
	 * @author John Steele <programjsteele@gmail.com>
	 */
	public static class KeywordToken extends CalculatorToken {
		
		/**
		 * The keyword value.
		 */
		private String keyword;
		
		/**
		 * Creates a new KeywordToken instance with the specified keyword value.
		 * 
		 * @param keyword the value of the keyword.
		 */
		public KeywordToken(String keyword) {
			super(Type.KEYWORD);
			this.keyword = keyword;
		}
		
		/**
		 * @return the keyword value.
		 */
		public String getKeyword() {
			return keyword;
		}
	}
	
	/**
	 * A utility class to encapsulate an {@link IExpression}s 
	 * left and right {@link IOperand}s.
	 * 
	 * @author John Steele <programjsteele@gmail.com>
	 */
	public static class OperandPair {
		
		/**
		 * The left operand.
		 */
		private IOperand leftOperand;
		
		/**
		 * The right operand.
		 */
		private IOperand rightOperand;
		
		/**
		 * Creates a new OperandPair instance with the specified
		 * left and right operands.
		 *  
		 * @param leftOperand the left operand.
		 * @param rightOperand the right operand.
		 */
		public OperandPair(IOperand leftOperand, IOperand rightOperand) {
			this.leftOperand = leftOperand;
			this.rightOperand = rightOperand;
		}
		
		/**
		 * @return the left operand.
		 */
		public IOperand getLeftOperand() {
			return leftOperand;
		}
		
		/**
		 * @return the right operand.
		 */
		public IOperand getRightOperand() {
			return rightOperand;
		}
	}
}
