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
import japps.ui.component.RoundedBorder;
import japps.ui.component.ToggleButton;
import java.awt.Dimension;
import javax.swing.JComponent;

/**
 *
 * @author Williams Lopez - JApps
 */
public class BooleanComponentBuilder implements ComponentBuilder{

    @Override
    public void addValidator(JComponent comp, ComponentField.Validatior validator) {
        // doesnt need a validator
    }

    @Override
    public void setValue(JComponent comp, Object object) {
        ((ToggleButton) comp).setSelected((Boolean)object);
    }

    @Override
    public Object getValue(JComponent comp) {
        return ((ToggleButton) comp).isSelected();
    }

    @Override
    public JComponent createNewComponent(PInfo pinfo) {
        boolean def = false;
        try {
            def = Boolean.parseBoolean(pinfo.defaultValue);
        } catch (Exception e) {
        }
        
        ToggleButton tb = new ToggleButton(def+"", (e) -> {
            ToggleButton b = (ToggleButton) e.getSource();
            if (b.isSelected()) {
                b.setText("true");
            } else {
                b.setText("false");
            }
        });
        
        tb.setSelected(def);
        tb.setText(def?"true":"false");
        tb.setBorder(new RoundedBorder(tb));
        tb.setMinimumSize(minFieldDimension);
        tb.setMaximumSize(maxFieldDimension);
        return tb;
    }
    
    @Override
    public Object getValue(PObject pobject, String property) {
        
        if(!pobject.containsProperty(property)){
            return null;
        }
        
        return pobject.getBool(property);
    }

    @Override
    public boolean isValidValue(JComponent comp) {
        return true;
    }
    
    
}
