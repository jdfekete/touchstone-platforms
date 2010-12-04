/*   TouchStone run platform is a software to run lab experiments. It is         *
 *   published under the terms of a BSD license (see details below)              *
 *   Author: Caroline Appert (appert@lri.fr)                                     *
 *   Copyright (c) 2010 Caroline Appert and INRIA, France.                       *
 *   TouchStone run platform reuses parts of an early version which were         *
 *   programmed by Jean-Daniel Fekete under the terms of a MIT (X11) Software    *
 *   License (Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France)           *
 *********************************************************************************/
/* Redistribution and use in source and binary forms, with or without            * 
 * modification, are permitted provided that the following conditions are met:   *

 *  - Redistributions of source code must retain the above copyright notice,     *
 *    this list of conditions and the following disclaimer.                      *
 *  - Redistributions in binary form must reproduce the above copyright notice,  *
 *    this list of conditions and the following disclaimer in the documentation  *
 *    and/or other materials provided with the distribution.                     *
 *  - Neither the name of the INRIA nor the names of its contributors   *
 * may be used to endorse or promote products derived from this software without *
 * specific prior written permission.                                            *

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"   *
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE     *
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE    *
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE     *
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR           *
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF          *
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS      *
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN       *
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)       *
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE    *
 * POSSIBILITY OF SUCH DAMAGE.                                                   *
 *********************************************************************************/
header {


package fr.inria.insitu.touchstone.input.parser;

import org.apache.commons.lang.StringEscapeUtils;
import fr.inria.insitu.touchstone.input.AxisExpr;
import fr.inria.insitu.touchstone.input.expr.*;


/**
 * Parser and Lexer for expressions to define end Axis Expressions.
 *
 * @author Jean-Daniel Fekete
 */
}

class AxisExprParser extends Parser;

options {
	k = 2;
}

expr returns [AxisExpr e]
{ e = null; }
	:	e=conditionalExpression
	;

conditionalExpression returns [AxisExpr e]
{ e = null;
  AxisExpr e2 = null;
  AxisExpr e3 = null; }
	:	e=logicalOrExpression
		( QUESTION e2=logicalOrExpression COLON e3=logicalOrExpression
			{ e = new CondAxisExpr(e, e2, e3); }
		)* // odd STAR to avoid errors from ANTLR
	;


logicalOrExpression returns [AxisExpr e]
{ e = null;
  AxisExpr e2 = null; }
	:	e=logicalAndExpression 
		(
			OR e2=logicalAndExpression	{ e = new OrAxisExpr(e, e2); }
		)*
	;


logicalAndExpression returns [AxisExpr e]
{ e = null;
  AxisExpr e2 = null; }
	:	e=exclusiveOrExpression 
		(
			AND e2=exclusiveOrExpression { e = new AndAxisExpr(e, e2); }
		)*
	;

exclusiveOrExpression returns [AxisExpr e]
{ e = null;
  AxisExpr e2 = null; }
	:	e=equalityExpression
		(
			XOR e2=equalityExpression { e = new XorAxisExpr(e, e2); }
		)*
	;

equalityExpression returns [AxisExpr e]
{ e = null;
  AxisExpr e2 = null; }
	:	e=relationalExpression (
			NOT_EQUAL e2=relationalExpression	{ e = new NotEqualAxisExpr(e, e2); }
		|	EQUAL e2=relationalExpression		{ e = new EqualAxisExpr(e, e2); }
		)*
	;


relationalExpression returns [AxisExpr e]
{ e = null;
  AxisExpr e2 = null; }
	:	e=addExpression
		(	LT e2=addExpression	{ e = new LtAxisExpr(e, e2); }
		|	GT e2=addExpression	{ e = new GtAxisExpr(e, e2); }
		|	LE e2=addExpression	{ e = new LEAxisExpr(e, e2); }
		|	GE e2=addExpression	{ e = new GEAxisExpr(e, e2); }
		)*
	;
	
addExpression returns [AxisExpr e]
{ e = null;
  AxisExpr e2 = null; }
	:	e=mexpr
	(	PLUS e2=mexpr { e = new PlusAxisExpr(e, e2); }
	|	MINUS e2=mexpr { e = new MinusAxisExpr(e, e2); }
	)*
	;
	
mexpr returns [AxisExpr e]
{ e = null;
  AxisExpr e2 = null; }
	:	e=expExpr
	(	MULT e2=expExpr { e = new MultAxisExpr(e, e2); }
	|	DIV  e2=expExpr { e = new DivAxisExpr(e, e2); }
	|	MOD  e2=expExpr { e = new ModAxisExpr(e, e2); }
	)*
	;

expExpr returns [ AxisExpr e]
{ e = null;
  AxisExpr e2 = null; }
	:	e=unaryExpr 
	(
		POW e2=unaryExpr { e=new PowAxisExpr(e, e2); }
	)*
	;

unaryExpr returns [AxisExpr e]
{ e = null; }
	:
	MINUS e=primaryExpr { e = new UnaryMinusAxisExpr(e); }
	|	e=primaryExpr
	;
	
primaryExpr returns [AxisExpr e]
{ e = null; }
	:	e=number
	|	LPAREN e=expr RPAREN { e = new UnaryAxisExpr(e); } // just for pretty printing
	|	e=fnCall
	|	a:AXIS { e = new NamedAxisExpr(StringEscapeUtils.unescapeJava(a.getText())); }
	;
	
number returns [ConstantAxisExpr e]
{ e = null; }
	:	n:NUMBER	{ e = new ConstantAxisExpr(Float.parseFloat(n.getText())); }
	|	"MIN_VALUE"	{ e = ConstantAxisExpr.MIN_VALUE; }
	|	"MAX_VALUE"	{ e = ConstantAxisExpr.MAX_VALUE; }
	;

fnCall returns [AxisExpr e]
{ e = null; 
  AxisExpr e2 = ConstantAxisExpr.MIN_VALUE; // for int()
  AxisExpr e3 = ConstantAxisExpr.MAX_VALUE; }// for int()
	:	"abs" LPAREN e=expr RPAREN { e = new AbsAxisExpr(e); }
	|	"sqrt" LPAREN e=expr RPAREN { e = new SqrtAxisExpr(e); }
	|	"min" LPAREN e=expr COMMA e2=expr RPAREN { e = new MinAxisExpr(e, e2); }
	|	"max" LPAREN e=expr COMMA e2=expr RPAREN { e = new MaxAxisExpr(e, e2); }
	|	"hypot" LPAREN e=expr COMMA e2=expr RPAREN { e = new HypotAxisExpr(e, e2); }
	|	"int" LPAREN e=expr 
		(
			COMMA e2=expr (COMMA e3=expr)? 
		)? RPAREN { e = new IntAxisExpr(e, e2, e3); }
	|	"diff" LPAREN e=expr RPAREN { e = new DiffAxisExpr(e); }
	;

//list
//	:	expr ( COMMA expr )*
//	;

id
	:	ID
	;

class AxisExprLexer extends Lexer;

options {
	k=3;
	charVocabulary='\u0003'..'\u7FFE';
	// without inlining some bitset tests, couldn't do unicode;
	// I need to make ANTLR generate smaller bitsets; see
	// bottom of JavaLexer.java
	codeGenBitsetTestThreshold=20;
}

LPAREN			:	'('		;
RPAREN			:	')'		;
COLON			:	':'		;
//SEMI			:	';'		;
COMMA			:	','		;
PLUS			:	'+'		;
MINUS			:	'-'		;
MULT			:   "*"		;
DIV 			:	'/'		;
MOD 			:	'%'		;
POW 			:	"**"	;
QUESTION		:   '?'     ;
OR				:   '|'     ;
AND 			:   '&'     ;
XOR 			:   '^'     ;
EQUAL			:	"=="	;
NOT_EQUAL		:	"!="	;
LT	 			:   '<'     ;
GT	 			:   '>'     ;
LE	 			:   "<="    ;
GE	 			:   ">="    ;

WS    : ( ' '
        | '\r' '\n'
        | '\n'
        | '\t'
        )
        {$setType(Token.SKIP);}
        ;

ID
	options {testLiterals=true;}
	:	('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
		(	'.'	{$setType(AXIS); }
			(	(LETTER)+
			|	STRING
			)
		)?
	;

AXIS
	:	STRING 
		'.'
		(	(LETTER)+
		|	STRING
		)
	;

protected STRING	:	'"'! (ESC|~('"'|'\\'))* '"'! ;
ESC
	:	'\\'
		(	'"'
		|	'\\'
		)
	;

protected LETTER
    :   '\u0024' |
        '\u0041'..'\u005a' |
        '\u005f' |
        '\u0061'..'\u007a' |
        '\u00c0'..'\u00d6' |
        '\u00d8'..'\u00f6' |
        '\u00f8'..'\u00ff' |
        '\u0100'..'\u1fff' |
        '\u3040'..'\u318f' |
        '\u3300'..'\u337f' |
        '\u3400'..'\u3d2d' |
        '\u4e00'..'\u9fff' |
        '\uf900'..'\ufaff'
//		'a'..'z'|'A'..'Z'|'0'..'9'|'_'
    ;

NUMBER
	:	('0'..'9')+
		(
			(	'.' ('0'..'9')+ (EXPONENT)?
			)?
		|	EXPONENT
		)
	;

// a couple protected methods to assist in matching floating point numbers
protected
EXPONENT
	:	('e') ('+'|'-')? ('0'..'9')+
	;
