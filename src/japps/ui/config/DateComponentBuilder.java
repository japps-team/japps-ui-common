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
import japps.ui.component.DateField;
import java.awt.Dimension;
import javax.swing.JComponent;

/**
 *
 * @author Williams Lopez - JApps
 */
public class DateComponentBuilder implements ComponentBuilder{

    @Override
    public void addValidator(JComponent comp, ComponentField.Validatior validator) {
        ((DateField)comp).addValidator(validator);
    }

    @Override
    public void setValue(JComponent comp, Object object) {
        try {
            ((DateField)comp).setValue(object);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public Object getValue(JComponent comp) {
        return ((DateField)comp).getValue();
    }

    @Override
    public JComponent createNewComponent(PInfo pinfo) {
        DateField df = new DateField();
        df.setMinimumSize(minFieldDimension);
        df.setMaximumSize(maxFieldDimension);
        
        return df;
    }

    @Override
    public Object getValue(PObject pobject, String property) {
        try {
            return pobject.getDate(property);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isValidValue(JComponent comp) {
        return ((ComponentField)comp).validateField();
    }
    
}
