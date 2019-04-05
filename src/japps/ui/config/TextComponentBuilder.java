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
import java.awt.Dimension;
import javax.swing.JComponent;

/**
 *
 * @author Williams Lopez - JApps
 */
public class TextComponentBuilder implements ComponentBuilder{


    
    @Override
    public void addValidator(JComponent comp, ComponentField.Validatior validator) {
        TextField tf = (TextField)comp;
        tf.addValidator(validator);
    }

    @Override
    public void setValue(JComponent comp, Object value) {
        TextField tf = (TextField)comp;
        if(value!=null){
            tf.setValue(value.toString());
        }else{
            tf.setValue(null);
        }
    }

    @Override
    public Object getValue(JComponent comp) {
        TextField tf = (TextField)comp;
        return tf.getValue();
    }

    @Override
    public JComponent createNewComponent(PInfo pinfo) {
        TextField tf;
        if(pinfo.type.equals(PInfo.TYPE_STRING_LONG)){
            tf = new TextField(true);
            tf.setMinimumSize(new Dimension(30, 75));
            tf.setMaximumSize(new Dimension(300000, 30000));
        }else{
            tf = new TextField();
            tf.setMinimumSize(minFieldDimension);
            tf.setMaximumSize(maxFieldDimension);
        }
        if(pinfo.defaultValue != null){
            tf.setValue(pinfo.defaultValue);
        }
        return tf;
    }
    
    @Override
    public Object getValue(PObject pobject, String property) {
        return pobject.getString(property);
    }
    
    @Override
    public boolean isValidValue(JComponent comp) {
        return ((ComponentField)comp).validateField();
    }

    
}
