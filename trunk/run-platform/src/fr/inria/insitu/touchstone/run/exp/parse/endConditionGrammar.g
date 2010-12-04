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


package fr.inria.insitu.touchstone.exp.parse;

import java.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;

import fr.inria.insitu.touchstone.Platform.EndCondition;
import fr.inria.insitu.touchstone.endConditions.AndEndCondition;
import fr.inria.insitu.touchstone.endConditions.OrEndCondition;
import fr.inria.insitu.touchstone.endConditions.XorEndCondition;
import fr.inria.insitu.touchstone.endConditions.NotEndCondition;
import fr.inria.insitu.touchstone.endConditions.ErrorEndCondition;
import fr.inria.insitu.touchstone.endConditions.EndConditionFactory;

import fr.inria.insitu.touchstone.exp.defaults.ExperimentPartFactory;
import fr.inria.insitu.touchstone.utils.BasicFactory;

/**
 * Parser and Lexer for expressions to define end conditions.
 *
 * @author Caroline Appert
 */
}



class EndConditionParser extends Parser;

options {
    k = 2;
}
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
}

expr returns [EndCondition endCondition]
{endCondition = null;
 EndCondition ec1 = null;
 EndCondition ec2 = null; }
	: 	ec1=mexpr		{ endCondition = ec1; }
		(
		IMPLY s:STRING { endCondition = new ErrorEndCondition(fixString(s.getText()), ec1); }
		| AND ec2=mexpr 	{ endCondition = new AndEndCondition(ec1, ec2); }
		| OR ec2=mexpr 	{ endCondition = new OrEndCondition(ec1, ec2); }
		| XOR ec2=mexpr	{ endCondition = new XorEndCondition(ec1, ec2); }
		)*
	;

mexpr returns [EndCondition endCondition]
{endCondition = null;
 EndCondition ec = null;}
	: 	NOT ec=atom	{ endCondition = new NotEndCondition(ec); }
	|	ec=atom		{ endCondition = ec; }
	;
	
atom returns [EndCondition endCondition]
{endCondition=null;
 EndCondition ec=null; 
 Object o = null; }
	:   LPAREN ec=expr RPAREN		{ endCondition = ec; }
    |	o=functionCall		{ endCondition = (EndCondition)o; }
    ;

functionCall returns [Object o]
{
	o = null;
	Vector params = null; }
	:	w:WORD (LPAREN params=list RPAREN)?
    			{
    				try {
    				o = build(w.getText(), params);
    				} catch(Exception e) { 
    					e.printStackTrace();
    				};
    			}
    ;
    
fnCall returns [Object o]
{	o=null; }
	: o=functionCall
	;
    
list returns [Vector params]
{params = new Vector();
 Object w1 = null;}
	:	( w1=param { params.add(w1); } (COLON)? )*
    ;
    
param returns [Object p]
{p=null;}
	:	w:WORD 		{ p = w.getText(); }
	|	s:STRING		{ p = fixString(s.getText()); }
	|	i:INT 		{ p = Integer.parseInt(i.getText()); }
	|	d:NUMBER 	{ p = Double.parseDouble(d.getText()); }
	|	c:CHAR_LITERAL		{ p = ""+c.getText().charAt(1); }
	;
    


class EndConditionLexer extends Lexer;

options {
    k=2; // needed for newline junk
    charVocabulary='\u0000'..'\u007F'; // allow ascii
}

CHAR_LITERAL	:	'\'' ( ESC | ~('\''|'\n'|'\r'|'\\') ) '\'';
DOT		: '.';
NOT   : '!' ;
LPAREN: '(' ;
RPAREN: ')' ;
COLON: ',' ;
AND : '&' ;
OR  : '|' ;
XOR  : "^" ;
IMPLY : "=>" ;
INT
	:	('0'..'9')+ // everything starts with a digit sequence
		(
			(	'.' {$setType(NUMBER);}	// dot means we are float
				('0'..'9')+ (EXPONENT)?
			)?
		|	EXPONENT {$setType(NUMBER);}	// 'E' means we are float
		)
	;
	
// a couple protected methods to assist in matching floating point numbers
protected
EXPONENT
	:	('e') ('+'|'-')? ('0'..'9')+
	;

WORD		:('a'..'z'| '_' | 'A'..'Z')('.' | 'a'..'z' | '_' | 'A'..'Z' | '0'..'9')* ;
STRING	:	'{'! (ESC|~('"'|'\\'|'\n'|'\r' | '}'))* '}'! ;
ESC
	:	'\\'
		(	'n'
		|	'r'
		|	't'
		|	'b'
		|	'f'
		|	'"'
		|	'\''
		|	'\\'
		|	('u')+ HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
		|	'0'..'3'
			(
				options {
					warnWhenFollowAmbig = false;
				}
			:	'0'..'7'
				(
					options {
						warnWhenFollowAmbig = false;
					}
				:	'0'..'7'
				)?
			)?
		|	'4'..'7'
			(
				options {
					warnWhenFollowAmbig = false;
				}
			:	'0'..'7'
			)?
		)
	;
WS    : ( ' '
        | '\r' '\n'
        | '\n'
        | '\t'
        )
        {$setType(Token.SKIP);}
        ;

// hexadecimal digit (again, note it's protected!)
protected
HEX_DIGIT
	:	('0'..'9'|'A'..'F'|'a'..'f')
	;      