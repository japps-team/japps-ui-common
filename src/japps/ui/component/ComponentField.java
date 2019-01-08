/*
 * Copyright (C) 2018 Williams Lopez - JApps
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
package japps.ui.component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 *
 * @author Williams Lopez - JApps
 * @param <V>
 */
public abstract class ComponentField<V> extends JComponent implements IComponent{

    
    
    public ComponentField() {
        this.setBorder(new RoundedBorder());
        this.setMinimumSize(new Dimension(35, 35));
        this.setPreferredSize(new Dimension(100, 35));
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }
    
    
    
    /**
     * Sets the value for the field
     * @param value 
     */
    public abstract void setValue(V value);
    
    /**
     * Gets the field current value
     * @return 
     */
    public abstract V getValue();
   
    
    /**
     * Set this element editable
     * @param editable 
     */
    public abstract void setEditable(boolean editable);
    
    /**
     * Check whether this component is editable or not
     * @return 
     */
    public abstract boolean isEditable();
    
    /**
     * Set this component enabled or not
     * @param enabled 
     */
    public abstract void setEnabled(boolean enabled);
    
    /**
     * Check whether this component is enabled or not
     * @return 
     */
    public abstract boolean isEnabled();

    @Override
    public JComponent getComponent() {
        return this;
    }

    
    
    
}
