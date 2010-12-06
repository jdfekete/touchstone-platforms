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

public interface AxisExprParserTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int QUESTION = 4;
	int COLON = 5;
	int OR = 6;
	int AND = 7;
	int XOR = 8;
	int NOT_EQUAL = 9;
	int EQUAL = 10;
	int LT = 11;
	int GT = 12;
	int LE = 13;
	int GE = 14;
	int PLUS = 15;
	int MINUS = 16;
	int MULT = 17;
	int DIV = 18;
	int MOD = 19;
	int POW = 20;
	int LPAREN = 21;
	int RPAREN = 22;
	int AXIS = 23;
	int NUMBER = 24;
	int LITERAL_MIN_VALUE = 25;
	int LITERAL_MAX_VALUE = 26;
	int LITERAL_abs = 27;
	int LITERAL_sqrt = 28;
	int LITERAL_min = 29;
	int COMMA = 30;
	int LITERAL_max = 31;
	int LITERAL_hypot = 32;
	int LITERAL_int = 33;
	int LITERAL_diff = 34;
	int ID = 35;
	int WS = 36;
	int STRING = 37;
	int ESC = 38;
	int LETTER = 39;
	int EXPONENT = 40;
}
