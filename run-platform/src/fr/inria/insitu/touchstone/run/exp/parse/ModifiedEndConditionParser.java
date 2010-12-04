package fr.inria.insitu.touchstone.run.exp.parse;

import java.util.Vector;

import fr.inria.insitu.touchstone.run.Platform.EndCondition;
import fr.inria.insitu.touchstone.run.endConditions.AndEndCondition;
import fr.inria.insitu.touchstone.run.endConditions.ErrorEndCondition;
import fr.inria.insitu.touchstone.run.endConditions.NotEndCondition;
import fr.inria.insitu.touchstone.run.endConditions.OrEndCondition;
import fr.inria.insitu.touchstone.run.endConditions.XorEndCondition;

import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;

public class ModifiedEndConditionParser extends EndConditionParser {

	public ModifiedEndConditionParser(ParserSharedInputState state) {
		super(state);
	}

	public ModifiedEndConditionParser(TokenBuffer tokenBuf, int k) {
		super(tokenBuf, k);
	}

	public ModifiedEndConditionParser(TokenBuffer tokenBuf) {
		super(tokenBuf);
	}

	public ModifiedEndConditionParser(TokenStream lexer, int k) {
		super(lexer, k);
	}

	public ModifiedEndConditionParser(TokenStream lexer) {
		super(lexer);
	}
	
	public Object modifiedFunctionCall() throws Exception {
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
			
			o = build(w.getText(), params);
						
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return o;
	}
	
	public Object  modifiedFnCall() throws Exception {
		return modifiedFunctionCall();
	}
	
	public EndCondition modifiedExpr() throws Exception {
		EndCondition endCondition;
		
		Token  s = null;
		endCondition = null;
		EndCondition ec1 = null;
		EndCondition ec2 = null;
		
		try {      // for error handling
			ec1=modifiedMexpr();
			endCondition = ec1;
			{
			_loop3:
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
					ec2=modifiedMexpr();
					endCondition = new AndEndCondition(ec1, ec2);
					break;
				}
				case OR:
				{
					match(OR);
					ec2=modifiedMexpr();
					endCondition = new OrEndCondition(ec1, ec2);
					break;
				}
				case XOR:
				{
					match(XOR);
					ec2=modifiedMexpr();
					endCondition = new XorEndCondition(ec1, ec2);
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
		return endCondition;
	}
	
	public final EndCondition  modifiedMexpr() throws Exception {
		EndCondition endCondition;
		
		endCondition = null;
		EndCondition ec = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NOT:
			{
				match(NOT);
				ec=modifiedAtom();
				endCondition = new NotEndCondition(ec);
				break;
			}
			case LPAREN:
			case WORD:
			{
				ec=modifiedAtom();
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

	public EndCondition modifiedAtom() throws Exception {
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
				o=modifiedFunctionCall();
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

}
