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
package fr.inria.insitu.touchstone.run.utils;

import java.awt.event.KeyEvent;
import java.util.HashMap;


public class KeyMapJInputAWT {
	
	public static HashMap<Integer, String> mappings = new HashMap<Integer, String>();
	static {
		mappings.put(new Integer(KeyEvent.VK_0), "0");
		mappings.put(new Integer(KeyEvent.VK_1), "1");
		mappings.put(new Integer(KeyEvent.VK_2), "2");
		mappings.put(new Integer(KeyEvent.VK_3), "3");
		mappings.put(new Integer(KeyEvent.VK_4), "4");
		mappings.put(new Integer(KeyEvent.VK_5), "5");
		mappings.put(new Integer(KeyEvent.VK_6), "6");
		mappings.put(new Integer(KeyEvent.VK_7), "7");
		mappings.put(new Integer(KeyEvent.VK_8), "8");
		mappings.put(new Integer(KeyEvent.VK_9), "9");
		mappings.put(new Integer(KeyEvent.VK_A), "A");
		mappings.put(new Integer(KeyEvent.VK_B), "B");
		mappings.put(new Integer(KeyEvent.VK_C), "C");
		mappings.put(new Integer(KeyEvent.VK_D), "D");
		mappings.put(new Integer(KeyEvent.VK_E), "E");
		mappings.put(new Integer(KeyEvent.VK_F), "F");
		mappings.put(new Integer(KeyEvent.VK_G), "G");
		mappings.put(new Integer(KeyEvent.VK_H), "H");
		mappings.put(new Integer(KeyEvent.VK_I), "I");
		mappings.put(new Integer(KeyEvent.VK_J), "J");
		mappings.put(new Integer(KeyEvent.VK_K), "K");
		mappings.put(new Integer(KeyEvent.VK_L), "L");
		mappings.put(new Integer(KeyEvent.VK_M), "M");
		mappings.put(new Integer(KeyEvent.VK_N), "N");
		mappings.put(new Integer(KeyEvent.VK_O), "O");
		mappings.put(new Integer(KeyEvent.VK_P), "P");
		mappings.put(new Integer(KeyEvent.VK_Q), "Q");
		mappings.put(new Integer(KeyEvent.VK_R), "R");
		mappings.put(new Integer(KeyEvent.VK_S), "S");
		mappings.put(new Integer(KeyEvent.VK_T), "T");
		mappings.put(new Integer(KeyEvent.VK_U), "U");
		mappings.put(new Integer(KeyEvent.VK_V), "V");
		mappings.put(new Integer(KeyEvent.VK_W), "W");
		mappings.put(new Integer(KeyEvent.VK_X), "X");
		mappings.put(new Integer(KeyEvent.VK_Y), "Y");
		mappings.put(new Integer(KeyEvent.VK_Z), "Z");
		mappings.put(new Integer(KeyEvent.VK_ADD), "Add");
		mappings.put(new Integer(KeyEvent.VK_AT), "At");
		mappings.put(new Integer(KeyEvent.VK_BACK_SPACE), "Back");
		mappings.put(new Integer(KeyEvent.VK_BACK_SLASH), "Backslash");
		mappings.put(new Integer(KeyEvent.VK_CAPS_LOCK), "Capital");
		mappings.put(new Integer(KeyEvent.VK_CIRCUMFLEX), "Circumflex");
		mappings.put(new Integer(KeyEvent.VK_COLON), "Colon");
		mappings.put(new Integer(KeyEvent.VK_COMMA), "Comma");
		mappings.put(new Integer(KeyEvent.VK_CONVERT), "Convert");
		mappings.put(new Integer(KeyEvent.VK_DECIMAL), "Decimal");
		mappings.put(new Integer(KeyEvent.VK_DELETE), "Delete");
		mappings.put(new Integer(KeyEvent.VK_DIVIDE), "Divide");
		mappings.put(new Integer(KeyEvent.VK_DOWN), "Down");
		mappings.put(new Integer(KeyEvent.VK_END), "End");
		mappings.put(new Integer(KeyEvent.VK_EQUALS), "Equals");
		mappings.put(new Integer(KeyEvent.VK_ESCAPE), "Escape");
		mappings.put(new Integer(KeyEvent.VK_F1), "F1");
		mappings.put(new Integer(KeyEvent.VK_F10), "F10");
		mappings.put(new Integer(KeyEvent.VK_F11), "F11");
		mappings.put(new Integer(KeyEvent.VK_F12), "F12");
		mappings.put(new Integer(KeyEvent.VK_F2), "F2");
		mappings.put(new Integer(KeyEvent.VK_F3), "F3");
		mappings.put(new Integer(KeyEvent.VK_F4), "F4");
		mappings.put(new Integer(KeyEvent.VK_F5), "F5");
		mappings.put(new Integer(KeyEvent.VK_F6), "F6");
		mappings.put(new Integer(KeyEvent.VK_F7), "F7");
		mappings.put(new Integer(KeyEvent.VK_F8), "F8");
		mappings.put(new Integer(KeyEvent.VK_F9), "F9");
		mappings.put(new Integer(KeyEvent.VK_DEAD_GRAVE), "Grave");
		mappings.put(new Integer(KeyEvent.VK_HOME), "Home");
		mappings.put(new Integer(KeyEvent.VK_INSERT), "Insert");
		mappings.put(new Integer(KeyEvent.VK_ALT), "Alt");
		mappings.put(new Integer(KeyEvent.VK_OPEN_BRACKET), "Lbracket");
		mappings.put(new Integer(KeyEvent.VK_CONTROL), "Control");
		mappings.put(new Integer(KeyEvent.VK_LEFT), "Left");
		mappings.put(new Integer(KeyEvent.VK_SHIFT), "Shift");
		mappings.put(new Integer(KeyEvent.VK_MINUS), "Minus");
		mappings.put(new Integer(KeyEvent.VK_MULTIPLY), "Mutliply");
		mappings.put(new Integer(KeyEvent.VK_NONCONVERT), "Noconvert");
		mappings.put(new Integer(KeyEvent.VK_NUM_LOCK), "Numlock");
		mappings.put(new Integer(KeyEvent.VK_NUMPAD0), "Numpad0");
		mappings.put(new Integer(KeyEvent.VK_NUMPAD1), "Numpad1");
		mappings.put(new Integer(KeyEvent.VK_NUMPAD2), "Numpad2");
		mappings.put(new Integer(KeyEvent.VK_NUMPAD3), "Numpad3");
		mappings.put(new Integer(KeyEvent.VK_NUMPAD4), "Numpad4");
		mappings.put(new Integer(KeyEvent.VK_NUMPAD5), "Numpad5");
		mappings.put(new Integer(KeyEvent.VK_NUMPAD6), "Numpad6");
		mappings.put(new Integer(KeyEvent.VK_NUMPAD7), "Numpad7");
		mappings.put(new Integer(KeyEvent.VK_NUMPAD8), "Numpad8");
		mappings.put(new Integer(KeyEvent.VK_NUMPAD9), "Numpad9");
		mappings.put(new Integer(KeyEvent.VK_COMMA), "NumpadComma");
		mappings.put(new Integer(KeyEvent.VK_EQUALS), "NumpadEqual");
		mappings.put(new Integer(KeyEvent.VK_PAGE_DOWN), "PageDown");
		mappings.put(new Integer(KeyEvent.VK_PAGE_UP), "PageUp");
		mappings.put(new Integer(KeyEvent.VK_PAUSE), "Pause");
		mappings.put(new Integer(KeyEvent.VK_PERIOD), "Period");
		mappings.put(new Integer(KeyEvent.VK_CLOSE_BRACKET), "Rbracket");
		mappings.put(new Integer(KeyEvent.VK_ENTER), "Return");
		mappings.put(new Integer(KeyEvent.VK_RIGHT), "Right");
		mappings.put(new Integer(KeyEvent.VK_SCROLL_LOCK), "Scroll");
		mappings.put(new Integer(KeyEvent.VK_SEMICOLON), "Semicolon");
		mappings.put(new Integer(KeyEvent.VK_SLASH), "Slash");
		mappings.put(new Integer(KeyEvent.VK_SPACE), "Space");
		mappings.put(new Integer(KeyEvent.VK_STOP), "Stop");
		mappings.put(new Integer(KeyEvent.VK_SUBTRACT), "Subtract");
		mappings.put(new Integer(KeyEvent.VK_TAB), "Tab");
		mappings.put(new Integer(KeyEvent.VK_UNDERSCORE), "Underscore");
		mappings.put(new Integer(KeyEvent.VK_UP), "Up");
	}
	
	public static String keyDescriptor(int keyCode) {
		return mappings.get(keyCode);
	}
	
}
