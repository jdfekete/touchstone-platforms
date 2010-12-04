/*****************************************************************************
 * Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France                  *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license.txt file.                                                         *
 *****************************************************************************/
package fr.inria.insitu.touchstone.run.exp.parse;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * <b>KeyNames</b> manage the name of keys.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class KeyNames {
    HashMap<String, Integer> keyNames = new HashMap<String, Integer>();
    private static final KeyNames instance = new KeyNames();
   
    protected KeyNames() {
        Class c = KeyEvent.class;
        Field[] fields = c.getFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (f.getType() == int.class 
                    && Modifier.isPublic(f.getModifiers())
                    && Modifier.isStatic(f.getModifiers())) {
                String name = f.getName();
                try {
                    keyNames.put(name, new Integer(f.getInt(null)));
                }
                catch(IllegalAccessException e) {
                    ;//
                }
            }
        }
    }

    /**
     * @return the singleton instance of this class.
     */
    public static KeyNames getInstance() {
        return instance;
    }
    
    /**
     * Returns the key name of a specified keycode or null.
     * @param keyCode the keyCode
     * @return the key name of null
     */
    public String getKeyText(int keyCode) {
        return KeyEvent.getKeyText(keyCode);
    }
    
    /**
     * Returns the keyCode of a specified key name or
     * -1 if it is not defined.
     * @param keyText the key text
     * @return the keyCode or -1
     */
    public int getKeyCode(String keyText) {
        Integer i = keyNames.get(keyText);
        if (i == null) {
            return -1;
        }
        return i.intValue();
    }
    
}
