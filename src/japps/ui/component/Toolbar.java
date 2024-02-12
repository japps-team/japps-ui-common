/*
 * Copyright (C) 2023 admin
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;


/**
 *
 * @author admin
 */
public class Toolbar extends JComponent {
    
    public static final int ORGANIZATION_HORIZONTAL = 1;
    public static final int ORGANIZATION_VERTICAL = 2;
    
    private boolean organizationHasChanged = false;
    private int organization=1;
    private final List<Component> componentList;
    
    public Toolbar(){
        componentList = new ArrayList<>();
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        //this.setBorder(BorderFactory.createTitledBorder(""));
    }
    
    /**
     * Add any component but prefer using Button and Label
     * @param comp 
     * @return  
     */
    public Toolbar add(JComponent comp){
        componentList.add(comp);
        if(comp instanceof JLabel){
            ((JLabel)comp).setHorizontalAlignment(Button.CENTER);
        }
        refresh();
        return this;
    }
    
    /**
     * Removes a component from this toolbar
     * @param comp 
     */
    public void remove(JComponent comp){
        componentList.remove(comp);
        refresh();
    }
    
    /**
     * Add a standar separator imeddiatly after the last element added
     * @return 
     */
    public Toolbar addSeparator(){
        Label label = new Label(" ");
        label.setBackground(Color.LIGHT_GRAY);
        componentList.add(label);
        refresh();
        return this;
    }
    
    /**
     * Set organization for this toolbar
     * ORGANIZATION_HORIZONTAL
     * ORGANIZATION_VERTICAL
     * @param organization 
     */
    public void setOrganization(int organization){
        if(organization < 1 || organization > 3){
            throw new RuntimeException("Not supported organization: "+organization);
        }
        this.organizationHasChanged = this.organization != organization;
        this.organization = organization;
        this.refresh();
        
    }
    
    /**
     * Get components organization for this toolbar
     * @return 
     */
    public int getOrganization(){
        return this.organization;
    }
    
    /**
     * Reorganize components
     */
    private void refresh(){
        
        this.removeAll();
        if(organizationHasChanged){
            this.setLayout(new BoxLayout(this,(organization==Toolbar.ORGANIZATION_HORIZONTAL)?BoxLayout.X_AXIS:BoxLayout.Y_AXIS));
        }
        
        for(int i=0;i<componentList.size();i++){
            add(componentList.get(i));
        }
    }
    
}
