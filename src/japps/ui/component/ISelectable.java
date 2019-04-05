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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;


/**
 *
 * @author Williams Lopez - JApps
 */
public interface ISelectable {
        
    /**
     * Sets whether this component is selected or not
     * @param b 
     */
    public void setSelected(boolean b);
    
    /**
     * Checks if this component is selected or not
     * @return 
     */
    public boolean isSelected();
    
    /**
     * Adds an action listener
     * @param listener 
     */
    public void addSelectStateListener(SelectStateListener listener);
    
    /**
     * Removes action listener
     * @param listener 
     */
    public void removeSelectStateListener(SelectStateListener listener);
    
    /**
     * Listener for ISelectable components to watch when select state changes
     */
    public static interface SelectStateListener extends EventListener{
        
        public void state(ActionEvent e);
        
    }
    
    
}
