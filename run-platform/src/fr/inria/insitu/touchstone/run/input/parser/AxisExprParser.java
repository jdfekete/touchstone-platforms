// $ANTLR : "axisExpr.g" -> "AxisExprParser.java"$



package fr.inria.insitu.touchstone.run.input.parser;

import org.apache.commons.lang.StringEscapeUtils;

import fr.inria.insitu.touchstone.run.input.AxisExpr;
import fr.inria.insitu.touchstone.run.input.expr.*;


/**
 * Parser and Lexer for expressions to define end Axis Expressions.
 *
 * @author Jean-Daniel Fekete
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

public class AxisExprParser extends antlr.LLkParser       implements AxisExprParserTokenTypes
 {

protected AxisExprParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public AxisExprParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected AxisExprParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public AxisExprParser(TokenStream lexer) {
  this(lexer,2);
}

public AxisExprParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final AxisExpr  expr() throws RecognitionException, TokenStreamException {
		AxisExpr e;
		
		e = null;
		
		try {      // for error handling
			e=conditionalExpression();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return e;
	}
	
	public final AxisExpr  conditionalExpression() throws RecognitionException, TokenStreamException {
		AxisExpr e;
		
		e = null;
		AxisExpr e2 = null;
		AxisExpr e3 = null;
		
		try {      // for error handling
			e=logicalOrExpression();
			{
			_loop210:
			do {
				if ((LA(1)==QUESTION)) {
					match(QUESTION);
					e2=logicalOrExpression();
					match(COLON);
					e3=logicalOrExpression();
					e = new CondAxisExpr(e, e2, e3);
				}
				else {
					break _loop210;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return e;
	}
	
	public final AxisExpr  logicalOrExpression() throws RecognitionException, TokenStreamException {
		AxisExpr e;
		
		e = null;
		AxisExpr e2 = null;
		
		try {      // for error handling
			e=logicalAndExpression();
			{
			_loop213:
			do {
				if ((LA(1)==OR)) {
					match(OR);
					e2=logicalAndExpression();
					e = new OrAxisExpr(e, e2);
				}
				else {
					break _loop213;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		return e;
	}
	
	public final AxisExpr  logicalAndExpression() throws RecognitionException, TokenStreamException {
		AxisExpr e;
		
		e = null;
		AxisExpr e2 = null;
		
		try {      // for error handling
			e=exclusiveOrExpression();
			{
			_loop216:
			do {
				if ((LA(1)==AND)) {
					match(AND);
					e2=exclusiveOrExpression();
					e = new AndAxisExpr(e, e2);
				}
				else {
					break _loop216;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return e;
	}
	
	public final AxisExpr  exclusiveOrExpression() throws RecognitionException, TokenStreamException {
		AxisExpr e;
		
		e = null;
		AxisExpr e2 = null;
		
		try {      // for error handling
			e=equalityExpression();
			{
			_loop219:
			do {
				if ((LA(1)==XOR)) {
					match(XOR);
					e2=equalityExpression();
					e = new XorAxisExpr(e, e2);
				}
				else {
					break _loop219;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		return e;
	}
	
	public final AxisExpr  equalityExpression() throws RecognitionException, TokenStreamException {
		AxisExpr e;
		
		e = null;
		AxisExpr e2 = null;
		
		try {      // for error handling
			e=relationalExpression();
			{
			_loop222:
			do {
				switch ( LA(1)) {
				case NOT_EQUAL:
				{
					match(NOT_EQUAL);
					e2=relationalExpression();
					e = new NotEqualAxisExpr(e, e2);
					break;
				}
				case EQUAL:
				{
					match(EQUAL);
					e2=relationalExpression();
					e = new EqualAxisExpr(e, e2);
					break;
				}
				default:
				{
					break _loop222;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
		return e;
	}
	
	public final AxisExpr  relationalExpression() throws RecognitionException, TokenStreamException {
		AxisExpr e;
		
		e = null;
		AxisExpr e2 = null;
		
		try {      // for error handling
			e=addExpression();
			{
			_loop225:
			do {
				switch ( LA(1)) {
				case LT:
				{
					match(LT);
					e2=addExpression();
					e = new LtAxisExpr(e, e2);
					break;
				}
				case GT:
				{
					match(GT);
					e2=addExpression();
					e = new GtAxisExpr(e, e2);
					break;
				}
				case LE:
				{
					match(LE);
					e2=addExpression();
					e = new LEAxisExpr(e, e2);
					break;
				}
				case GE:
				{
					match(GE);
					e2=addExpression();
					e = new GEAxisExpr(e, e2);
					break;
				}
				default:
				{
					break _loop225;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		return e;
	}
	
	public final AxisExpr  addExpression() throws RecognitionException, TokenStreamException {
		AxisExpr e;
		
		e = null;
		AxisExpr e2 = null;
		
		try {      // for error handling
			e=mexpr();
			{
			_loop228:
			do {
				switch ( LA(1)) {
				case PLUS:
				{
					match(PLUS);
					e2=mexpr();
					e = new PlusAxisExpr(e, e2);
					break;
				}
				case MINUS:
				{
					match(MINUS);
					e2=mexpr();
					e = new MinusAxisExpr(e, e2);
					break;
				}
				default:
				{
					break _loop228;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		return e;
	}
	
	public final AxisExpr  mexpr() throws RecognitionException, TokenStreamException {
		AxisExpr e;
		
		e = null;
		AxisExpr e2 = null;
		
		try {      // for error handling
			e=expExpr();
			{
			_loop231:
			do {
				switch ( LA(1)) {
				case MULT:
				{
					match(MULT);
					e2=expExpr();
					e = new MultAxisExpr(e, e2);
					break;
				}
				case DIV:
				{
					match(DIV);
					e2=expExpr();
					e = new DivAxisExpr(e, e2);
					break;
				}
				case MOD:
				{
					match(MOD);
					e2=expExpr();
					e = new ModAxisExpr(e, e2);
					break;
				}
				default:
				{
					break _loop231;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return e;
	}
	
	public final  AxisExpr  expExpr() throws RecognitionException, TokenStreamException {
		 AxisExpr e;
		
		e = null;
		AxisExpr e2 = null;
		
		try {      // for error handling
			e=unaryExpr();
			{
			_loop234:
			do {
				if ((LA(1)==POW)) {
					match(POW);
					e2=unaryExpr();
					e=new PowAxisExpr(e, e2);
				}
				else {
					break _loop234;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		return e;
	}
	
	public final AxisExpr  unaryExpr() throws RecognitionException, TokenStreamException {
		AxisExpr e;
		
		e = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case MINUS:
			{
				match(MINUS);
				e=primaryExpr();
				e = new UnaryMinusAxisExpr(e);
				break;
			}
			case LPAREN:
			case AXIS:
			case NUMBER:
			case LITERAL_MIN_VALUE:
			case LITERAL_MAX_VALUE:
			case LITERAL_abs:
			case LITERAL_sqrt:
			case LITERAL_min:
			case LITERAL_max:
			case LITERAL_hypot:
			case LITERAL_int:
			case LITERAL_diff:
			{
				e=primaryExpr();
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
			recover(ex,_tokenSet_9);
		}
		return e;
	}
	
	public final AxisExpr  primaryExpr() throws RecognitionException, TokenStreamException {
		AxisExpr e;
		
		Token  a = null;
		e = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NUMBER:
			case LITERAL_MIN_VALUE:
			case LITERAL_MAX_VALUE:
			{
				e=number();
				break;
			}
			case LPAREN:
			{
				match(LPAREN);
				e=expr();
				match(RPAREN);
				e = new UnaryAxisExpr(e);
				break;
			}
			case LITERAL_abs:
			case LITERAL_sqrt:
			case LITERAL_min:
			case LITERAL_max:
			case LITERAL_hypot:
			case LITERAL_int:
			case LITERAL_diff:
			{
				e=fnCall();
				break;
			}
			case AXIS:
			{
				a = LT(1);
				match(AXIS);
				e = new NamedAxisExpr(StringEscapeUtils.unescapeJava(a.getText()));
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
			recover(ex,_tokenSet_9);
		}
		return e;
	}
	
	public final ConstantAxisExpr  number() throws RecognitionException, TokenStreamException {
		ConstantAxisExpr e;
		
		Token  n = null;
		e = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NUMBER:
			{
				n = LT(1);
				match(NUMBER);
				e = new ConstantAxisExpr(Float.parseFloat(n.getText()));
				break;
			}
			case LITERAL_MIN_VALUE:
			{
				match(LITERAL_MIN_VALUE);
				e = ConstantAxisExpr.MIN_VALUE;
				break;
			}
			case LITERAL_MAX_VALUE:
			{
				match(LITERAL_MAX_VALUE);
				e = ConstantAxisExpr.MAX_VALUE;
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
			recover(ex,_tokenSet_9);
		}
		return e;
	}
	
	public final AxisExpr  fnCall() throws RecognitionException, TokenStreamException {
		AxisExpr e;
		
		e = null; 
		AxisExpr e2 = ConstantAxisExpr.MIN_VALUE; // for int()
		AxisExpr e3 = ConstantAxisExpr.MAX_VALUE;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_abs:
			{
				match(LITERAL_abs);
				match(LPAREN);
				e=expr();
				match(RPAREN);
				e = new AbsAxisExpr(e);
				break;
			}
			case LITERAL_sqrt:
			{
				match(LITERAL_sqrt);
				match(LPAREN);
				e=expr();
				match(RPAREN);
				e = new SqrtAxisExpr(e);
				break;
			}
			case LITERAL_min:
			{
				match(LITERAL_min);
				match(LPAREN);
				e=expr();
				match(COMMA);
				e2=expr();
				match(RPAREN);
				e = new MinAxisExpr(e, e2);
				break;
			}
			case LITERAL_max:
			{
				match(LITERAL_max);
				match(LPAREN);
				e=expr();
				match(COMMA);
				e2=expr();
				match(RPAREN);
				e = new MaxAxisExpr(e, e2);
				break;
			}
			case LITERAL_hypot:
			{
				match(LITERAL_hypot);
				match(LPAREN);
				e=expr();
				match(COMMA);
				e2=expr();
				match(RPAREN);
				e = new HypotAxisExpr(e, e2);
				break;
			}
			case LITERAL_int:
			{
				match(LITERAL_int);
				match(LPAREN);
				e=expr();
				{
				switch ( LA(1)) {
				case COMMA:
				{
					match(COMMA);
					e2=expr();
					{
					switch ( LA(1)) {
					case COMMA:
					{
						match(COMMA);
						e3=expr();
						break;
					}
					case RPAREN:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(RPAREN);
				e = new IntAxisExpr(e, e2, e3);
				break;
			}
			case LITERAL_diff:
			{
				match(LITERAL_diff);
				match(LPAREN);
				e=expr();
				match(RPAREN);
				e = new DiffAxisExpr(e);
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
			recover(ex,_tokenSet_9);
		}
		return e;
	}
	
	public final void id() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(ID);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"QUESTION",
		"COLON",
		"OR",
		"AND",
		"XOR",
		"NOT_EQUAL",
		"EQUAL",
		"LT",
		"GT",
		"LE",
		"GE",
		"PLUS",
		"MINUS",
		"MULT",
		"DIV",
		"MOD",
		"POW",
		"LPAREN",
		"RPAREN",
		"AXIS",
		"NUMBER",
		"\"MIN_VALUE\"",
		"\"MAX_VALUE\"",
		"\"abs\"",
		"\"sqrt\"",
		"\"min\"",
		"COMMA",
		"\"max\"",
		"\"hypot\"",
		"\"int\"",
		"\"diff\"",
		"ID",
		"WS",
		"STRING",
		"ESC",
		"LETTER",
		"EXPONENT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 1077936128L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 1077936176L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 1077936240L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 1077936368L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 1077936624L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 1077938160L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 1077968880L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 1078067184L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 1078984688L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 1080033264L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	
	}
