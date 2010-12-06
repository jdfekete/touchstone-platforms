// $ANTLR : "endConditionGrammar.g" -> "EndConditionLexer.java"$



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

public interface EndConditionParserTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int IMPLY = 4;
	int STRING = 5;
	int AND = 6;
	int OR = 7;
	int XOR = 8;
	int NOT = 9;
	int LPAREN = 10;
	int RPAREN = 11;
	int WORD = 12;
	int COLON = 13;
	int INT = 14;
	int NUMBER = 15;
	int CHAR_LITERAL = 16;
	int DOT = 17;
	int EXPONENT = 18;
	int ESC = 19;
	int WS = 20;
	int HEX_DIGIT = 21;
}
