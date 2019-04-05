/*
 * Copyright (C) 2019 Williams Lopez - JApps
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package japps.ui.config;

import japps.ui.component.ComponentField;
import japps.ui.component.TextField;
import japps.ui.util.Resources;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;

/**
 *
 * @author Williams Lopez - JApps
 */
public class NumberComponentBuilder implements ComponentBuilder{
    

    @Override
    public void addValidator(JComponent comp, ComponentField.Validatior validator) {
        ((TextField)comp).addValidator(validator);
    }

    @Override
    public void setValue(JComponent comp, Object object) {
        if(object !=null ){
            ((TextField)comp).setValue(object.toString());
        }else{
            ((TextField)comp).setValue("");
        }
    }

    @Override
    public Object getValue(JComponent comp) {
        String v = ((TextField)comp).getValue();
        if(v != null ){
            try {
                if(v.indexOf('.')>=0){
                    return Double.parseDouble(v);
                }else{
                    return Integer.parseInt(v);
                }
                
            } catch (Exception e) {
            }
        }
        return v;
    }

    @Override
    public JComponent createNewComponent(PInfo pinfo) {
            TextField tf = new TextField();
            
            if(pinfo.type.equals(PInfo.TYPE_NUMBER) || pinfo.type.equals(PInfo.TYPE_DECIMAL)){
                tf.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        if (Character.isDigit(e.getKeyChar())
                                || e.getKeyCode() == KeyEvent.VK_BACK_SPACE
                                || e.getKeyCode() == KeyEvent.VK_DELETE
                                || e.getKeyChar() == '.') {
                        } else {
                            e.consume();
                        }
                    }
                });
                tf.addValidator((c) -> {
                    try {
                        Double.parseDouble(((ComponentField)c).getValue().toString());
                    } catch (Exception e) {
                        throw new RuntimeException(Resources.$("Invalid value, must be a Number"));
                    }
                    return true;
                });
            }else{
                tf.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        if (Character.isDigit(e.getKeyChar()) || 
                                e.getKeyCode() == KeyEvent.VK_BACK_SPACE || 
                                e.getKeyCode() == KeyEvent.VK_DELETE ){
                        }else{
                            e.consume();
                        }
                    }
                });
                tf.addValidator((c) -> {
                    try {
                        Integer.parseInt(((ComponentField)c).getValue().toString());
                    } catch (Exception e) {
                        throw new RuntimeException(Resources.$("Invalid value, must be an Integer"));
                    }
                    return true;
                });
            }
            tf.setMinimumSize(minFieldDimension);
            tf.setMaximumSize(maxFieldDimension);
            tf.setValue(pinfo.defaultValue);
            return tf;
    }
    
    @Override
    public Object getValue(PObject pobject, String property) {
        int i = pobject.getInt(property);
        return (i>=0)?i:null;
    }
    
    
    @Override
    public boolean isValidValue(JComponent comp) {
        return ((ComponentField)comp).validateField();
    }
    
}
