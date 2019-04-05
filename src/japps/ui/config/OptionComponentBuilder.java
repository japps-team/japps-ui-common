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

import japps.ui.component.ComboBox;
import japps.ui.component.ComponentField;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Williams Lopez - JApps
 */
public class OptionComponentBuilder implements ComponentBuilder{

    @Override
    public void addValidator(JComponent comp, ComponentField.Validatior validator) {
        ComboBox<String> cb = ((ComboBox<String>) comp); 
        cb.addValidator(validator);
    }

    @Override
    public void setValue(JComponent comp, Object object) {
        if(object == null) return;
        ComboBox<String> cb = ((ComboBox<String>) comp); 
        String v = object.toString();
        List<String> options = cb.getValues();
        if(options != null){
            for(String o:options){
                if(o.startsWith(v+"-")){
                    cb.setValue(o);
                }
            }
        }
    }

    @Override
    public Object getValue(JComponent comp) {
        String v = ((ComboBox<String>) comp).getValue();
        if (v == null) {
            return null;
        }
        String[] vi = v.split("[-]");
        return vi[0];
    }

    @Override
    public JComponent createNewComponent(PInfo pinfo) {
        String[] sv = pinfo.defaultValue.split("[,]");

        ComboBox<String> cb = new ComboBox<>(sv.length > 20);
        pinfo.comp = cb;

        cb.setValues(sv);

        //setting current value
        String cv = pinfo.pobject.getString(pinfo.property);
        for (String v : sv) {
            if (v.startsWith(cv + "-")) {
                cv = v;
            }
        }
        cb.setValue(cv);
        cb.setMinimumSize(minFieldDimension);
        cb.setMaximumSize(maxFieldDimension);
        return cb;
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
