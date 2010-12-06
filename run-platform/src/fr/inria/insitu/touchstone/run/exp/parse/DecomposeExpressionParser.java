// $ANTLR : "decomposeExpressions.g" -> "DecomposeExpressionParser.java"$



package fr.inria.insitu.touchstone.run.exp.parse;

import java.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;


import fr.inria.insitu.touchstone.run.Platform.EndCondition;
import fr.inria.insitu.touchstone.run.endConditions.AndEndCondition;
import fr.inria.insitu.touchstone.run.endConditions.EndConditionFactory;
import fr.inria.insitu.touchstone.run.endConditions.ErrorEndCondition;
import fr.inria.insitu.touchstone.run.endConditions.NotEndCondition;
import fr.inria.insitu.touchstone.run.endConditions.OrEndCondition;
import fr.inria.insitu.touchstone.run.endConditions.XorEndCondition;
import fr.inria.insitu.touchstone.run.exp.defaults.ExperimentPartFactory;
import fr.inria.insitu.touchstone.run.utils.BasicFactory;

/**
 * Parser and Lexer for expressions to define end conditions.
 *
 * @author Caroline Appert
 */

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

public class DecomposeExpressionParser extends antlr.LLkParser       implements DecomposeExpressionParserTokenTypes
 {

	
	String fixString(String arg) {
		return arg.replaceAll("\\\\n", "\n").replaceAll("\\\\r", "\r").replaceAll("\\\\t", "\t");
	}

protected DecomposeExpressionParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public DecomposeExpressionParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected DecomposeExpressionParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public DecomposeExpressionParser(TokenStream lexer) {
  this(lexer,2);
}

public DecomposeExpressionParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final Vector<String>  expr() throws RecognitionException, TokenStreamException {
		Vector<String> expressions;
		
		Token  s = null;
		expressions = new Vector<String>();
		Vector<String> expressions1 = null;
		Vector<String> expressions2 = null;
		
		try {      // for error handling
			expressions1=mexpr();
			expressions.addAll(expressions1);
			{
			_loop3:
			do {
				switch ( LA(1)) {
				case IMPLY:
				{
					match(IMPLY);
					s = LT(1);
					match(STRING);
					break;
				}
				case AND:
				{
					match(AND);
					expressions2=mexpr();
					expressions.addAll(expressions2);
					break;
				}
				case OR:
				{
					match(OR);
					expressions2=mexpr();
					expressions.addAll(expressions2);
					break;
				}
				case XOR:
				{
					match(XOR);
					expressions2=mexpr();
					expressions.addAll(expressions2);
					break;
				}
				default:
				{
					break _loop3;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return expressions;
	}
	
	public final Vector<String>  mexpr() throws RecognitionException, TokenStreamException {
		Vector<String> expressions;
		
		expressions = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NOT:
			{
				match(NOT);
				expressions=atom();
				break;
			}
			case LPAREN:
			case WORD:
			{
				expressions=atom();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		return expressions;
	}
	
	public final Vector<String>  atom() throws RecognitionException, TokenStreamException {
		Vector<String> expressions;
		
		expressions = new Vector<String>();
		String func = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LPAREN:
			{
				match(LPAREN);
				expressions=expr();
				match(RPAREN);
				break;
			}
			case WORD:
			{
				func=functionCall();
				expressions.add(func);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		return expressions;
	}
	
	public final String  functionCall() throws RecognitionException, TokenStreamException {
		String expression;
		
		Token  w = null;
		expression = null;
		String l = null;
		
		try {      // for error handling
			w = LT(1);
			match(WORD);
			{
			_loop8:
			do {
				if ((LA(1)==LPAREN)) {
					match(LPAREN);
					l=list();
					match(RPAREN);
				}
				else {
					break _loop8;
				}
				
			} while (true);
			}
			
						// It should not be a '*' in this rule but a '?' and I don't understand why it doesn't work
						if(l==null || l.trim().length() == 0) 
							expression = w.getText(); 
						else 
							expression = w.getText()+"("+l+")";
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		return expression;
	}
	
	public final String  list() throws RecognitionException, TokenStreamException {
		String expression;
		
		expression = "";
		String p = "";
		String p1;
		
		try {      // for error handling
			{
			_loop12:
			do {
				if ((_tokenSet_2.member(LA(1)))) {
					p1=param();
					if(p.length() != 0) p = p+","+p1; else p = p+p1;
					{
					switch ( LA(1)) {
					case COLON:
					{
						match(COLON);
						break;
					}
					case STRING:
					case RPAREN:
					case WORD:
					case INT:
					case NUMBER:
					case CHAR_LITERAL:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
				}
				else {
					break _loop12;
				}
				
			} while (true);
			}
			expression = p;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return expression;
	}
	
	public final String  param() throws RecognitionException, TokenStreamException {
		String expression;
		
		Token  w = null;
		Token  s = null;
		Token  i = null;
		Token  d = null;
		Token  c = null;
		expression = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case WORD:
			{
				w = LT(1);
				match(WORD);
				expression = w.getText();
				break;
			}
			case STRING:
			{
				s = LT(1);
				match(STRING);
				expression = s.getText();
				break;
			}
			case INT:
			{
				i = LT(1);
				match(INT);
				expression = i.getText();
				break;
			}
			case NUMBER:
			{
				d = LT(1);
				match(NUMBER);
				expression = d.getText();
				break;
			}
			case CHAR_LITERAL:
			{
				c = LT(1);
				match(CHAR_LITERAL);
				expression = c.getText();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		return expression;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"IMPLY",
		"STRING",
		"AND",
		"OR",
		"XOR",
		"NOT",
		"LPAREN",
		"RPAREN",
		"WORD",
		"COLON",
		"INT",
		"NUMBER",
		"CHAR_LITERAL",
		"DOT",
		"EXPONENT",
		"ESC",
		"WS",
		"HEX_DIGIT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2048L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 2512L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 118816L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 129056L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	
	}
