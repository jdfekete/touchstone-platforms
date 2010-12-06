// $ANTLR : "endConditionGrammar.g" -> "EndConditionParser.java"$



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

public class EndConditionParser extends antlr.LLkParser       implements EndConditionParserTokenTypes
 {

    BasicFactory factory;

	public void setFactory(BasicFactory f) {
		this.factory = f;
	}
	
	Object build(String name, Vector params) throws Exception {
        Object built = factory == null ? null : factory.createFor(name, params == null ? null : params.toArray());
        if (built != null) return built;
        built = EndConditionFactory.create(name, params == null ? null : params.toArray());
        if (built != null) return built;
		built = ExperimentPartFactory.create(name, params == null ? null : params.toArray());
		if (built != null) return built;
		Class objectClass = null;
		if(params == null) {
				objectClass = Class.forName(name);
				built = objectClass.newInstance();
				return built;
		} else {
			int nbArgsParsed = params.size();
			Class[] argsClasses = new Class[nbArgsParsed];
			Object[] args = params.toArray();
				for(int i = 0; i < nbArgsParsed; i++) {
					argsClasses[i] = args[i].getClass();
				}					
				objectClass = Class.forName(name);
				Constructor c = objectClass.getConstructor(argsClasses);
				built = c.newInstance(args);
		}
		return built;
	}
	
	String fixString(String arg) {
		return arg.replaceAll("\\\\n", "\n").replaceAll("\\\\r", "\r").replaceAll("\\\\t", "\t");
	}

protected EndConditionParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public EndConditionParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected EndConditionParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public EndConditionParser(TokenStream lexer) {
  this(lexer,2);
}

public EndConditionParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final EndCondition  expr() throws RecognitionException, TokenStreamException {
		EndCondition endCondition;
		
		Token  s = null;
		endCondition = null;
		EndCondition ec1 = null;
		EndCondition ec2 = null;
		
		try {      // for error handling
			ec1=mexpr();
			endCondition = ec1;
			{
			_loop61:
			do {
				switch ( LA(1)) {
				case IMPLY:
				{
					match(IMPLY);
					s = LT(1);
					match(STRING);
					endCondition = new ErrorEndCondition(fixString(s.getText()), ec1);
					break;
				}
				case AND:
				{
					match(AND);
					ec2=mexpr();
					endCondition = new AndEndCondition(ec1, ec2);
					break;
				}
				case OR:
				{
					match(OR);
					ec2=mexpr();
					endCondition = new OrEndCondition(ec1, ec2);
					break;
				}
				case XOR:
				{
					match(XOR);
					ec2=mexpr();
					endCondition = new XorEndCondition(ec1, ec2);
					break;
				}
				default:
				{
					break _loop61;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return endCondition;
	}
	
	public final EndCondition  mexpr() throws RecognitionException, TokenStreamException {
		EndCondition endCondition;
		
		endCondition = null;
		EndCondition ec = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NOT:
			{
				match(NOT);
				ec=atom();
				endCondition = new NotEndCondition(ec);
				break;
			}
			case LPAREN:
			case WORD:
			{
				ec=atom();
				endCondition = ec;
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
		return endCondition;
	}
	
	public final EndCondition  atom() throws RecognitionException, TokenStreamException {
		EndCondition endCondition;
		
		endCondition=null;
		EndCondition ec=null; 
		Object o = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LPAREN:
			{
				match(LPAREN);
				ec=expr();
				match(RPAREN);
				endCondition = ec;
				break;
			}
			case WORD:
			{
				o=functionCall();
				endCondition = (EndCondition)o;
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
		return endCondition;
	}
	
	public final Object  functionCall() throws RecognitionException, TokenStreamException {
		Object o;
		
		Token  w = null;
		
			o = null;
			Vector params = null;
		
		try {      // for error handling
			w = LT(1);
			match(WORD);
			{
			switch ( LA(1)) {
			case LPAREN:
			{
				match(LPAREN);
				params=list();
				match(RPAREN);
				break;
			}
			case EOF:
			case IMPLY:
			case AND:
			case OR:
			case XOR:
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
			
							try {
							o = build(w.getText(), params);
							} catch(Exception e) { 
								e.printStackTrace();
							};
						
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return o;
	}
	
	public final Vector  list() throws RecognitionException, TokenStreamException {
		Vector params;
		
		params = new Vector();
		Object w1 = null;
		
		try {      // for error handling
			{
			_loop70:
			do {
				if ((_tokenSet_3.member(LA(1)))) {
					w1=param();
					params.add(w1);
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
					break _loop70;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return params;
	}
	
	public final Object  fnCall() throws RecognitionException, TokenStreamException {
		Object o;
		
			o=null;
		
		try {      // for error handling
			o=functionCall();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
		return o;
	}
	
	public final Object  param() throws RecognitionException, TokenStreamException {
		Object p;
		
		Token  w = null;
		Token  s = null;
		Token  i = null;
		Token  d = null;
		Token  c = null;
		p=null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case WORD:
			{
				w = LT(1);
				match(WORD);
				p = w.getText();
				break;
			}
			case STRING:
			{
				s = LT(1);
				match(STRING);
				p = fixString(s.getText());
				break;
			}
			case INT:
			{
				i = LT(1);
				match(INT);
				p = Integer.parseInt(i.getText());
				break;
			}
			case NUMBER:
			{
				d = LT(1);
				match(NUMBER);
				p = Double.parseDouble(d.getText());
				break;
			}
			case CHAR_LITERAL:
			{
				c = LT(1);
				match(CHAR_LITERAL);
				p = ""+c.getText().charAt(1);
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
			recover(ex,_tokenSet_5);
		}
		return p;
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
		long[] data = { 2514L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 118816L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 129056L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	
	}
