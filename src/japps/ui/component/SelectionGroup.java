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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Group for ISelectable components
 * @author Williams Lopez - JApps
 */
public class SelectionGroup{
    
    private boolean disabled = false;
    private List<ISelectable> elements;
    private SelectionListenerGroup listenerGroup;
    private ISelectable selected;

    /**
     * Creates a new Selection group
     */
    public SelectionGroup() {
        elements = new ArrayList<>();
        listenerGroup = new SelectionListenerGroup();
    }
    
    /**
     * Adds a selectable component to this group
     * @param selectable 
     */
    public void add(ISelectable selectable){
        elements.add(selectable);
        selectable.addSelectStateListener(listenerGroup);
    }
    
    /**
     * Removes a selectable component from this group
     * @param selectable 
     */
    public void remove(ISelectable selectable){
        elements.remove(selectable);
        selectable.removeSelectStateListener(listenerGroup);
    }
    
    /**
     * Remove all elements in group
     */
    public void removeAll(){
        if(elements.isEmpty()) return;
        ISelectable[] array = new ISelectable[elements.size()];
        array = elements.toArray(array);
        for(ISelectable s: array){
            remove(s);
        }
    }
    
    /**
     * Gets the current selected component in this group
     * @return 
     */
    public ISelectable getSelected(){
        return this.selected;
    }
    
    /**
     * Set the selected value in this group
     * @param selected
     */
    public void setSelected(ISelectable selected) {
        this.disabled = true;
        try {
            this.selected = selected;
            for (ISelectable s : elements) {
                if (selected != s) {
                    s.setSelected(false);
                } else {
                    s.setSelected(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.disabled = false;
    }
    
    class SelectionListenerGroup implements ISelectable.SelectStateListener{

        @Override
        public void state(ActionEvent e) {
            if(!disabled){
                setSelected((ISelectable)e.getSource());
            }
        }

        
    }
    
}
