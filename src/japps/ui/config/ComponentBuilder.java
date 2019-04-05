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
import japps.ui.component.IComponent;
import java.awt.Dimension;
import javax.swing.JComponent;
/**
 *
 * @author Williams Lopez - JApps
 */
public interface ComponentBuilder{
    
    public static Dimension  maxFieldDimension = new Dimension(3000, 25);
    public static Dimension  minFieldDimension = new Dimension(10, 25);
    
    
    /**
     * Add a new Validator in this component
     * @param comp
     * @param validator 
     */
    public void addValidator(JComponent comp, ComponentField.Validatior validator);
    
    /**
     * Set the value in comp
     * @param comp
     * @param object
     */
    public void setValue(JComponent comp, Object object);
    
    /**
     * Get the value from Component
     * @param comp
     * @return 
     */
    public Object getValue(JComponent comp);
    
    /**
     * Get the value from a pobject
     * @param pobject
     * @return 
     */
    public Object getValue(PObject pobject, String property);
    
    /**
     * Create a new component, this method sets the default value for the component
     * @param pinfo
     * @return 
     */
    public JComponent createNewComponent(PInfo pinfo);
    
    /**
     * Checks whether the value of the component comp is valid or not
     * @param comp
     * @return 
     */
    public boolean isValidValue(JComponent comp);
    
    
}
