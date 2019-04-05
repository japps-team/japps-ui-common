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
import java.awt.Dimension;
import javax.swing.JComponent;

/**
 *
 * @author Williams Lopez - JApps
 */
public class PListComponentBuilder implements ComponentBuilder{

    @Override
    public void addValidator(JComponent comp, ComponentField.Validatior validator) {
        
    }

    @Override
    public void setValue(JComponent comp, Object object) {
        
    }

    @Override
    public Object getValue(JComponent comp) {
        return null;
    }

    @Override
    public JComponent createNewComponent(PInfo pinfo) {
        Class c = PObject.class;
        try {
            c = Class.forName(pinfo.defaultValue);
        } catch (Exception e) {
        }

        PObject.PList plist = pinfo.pobject.getList(pinfo.property, c);
        PListComponent pc = new PListComponent(plist,pinfo.editor);
        
        pc.setMinimumSize(new Dimension(30, 250));
        pc.setMaximumSize(new Dimension(300000, 300000));
        
        return pc;
    }
    
    @Override
    public Object getValue(PObject pobject, String property) {
        return null;
    }
    
    
    @Override
    public boolean isValidValue(JComponent comp) {
        return true;
    }
}
