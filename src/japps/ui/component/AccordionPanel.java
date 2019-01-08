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

import japps.ui.util.Resources;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

/**
 * 
 * Component like Accordion with items
 *
 * @author Williams Lopez - JApps
 */
public class AccordionPanel extends JScrollPane implements IComponent{
    
    private JComponent main;
    private Image iconGroupNoExpanded;
    private Image iconGroupExpanded;
    private List<Group> groups;
    

    public AccordionPanel() {
        
        groups = new ArrayList<>();
        iconGroupNoExpanded = Resources.icon("arrow-keyboard-right.png",20,20);
        iconGroupExpanded  = Resources.icon("arrow-down-keyboard.png",20,20);
        main = new JComponent() {};
        BoxLayout box = new BoxLayout(main, BoxLayout.Y_AXIS);
        main.setLayout(box);
        setViewportView(main);
        getViewport().setBackground(Color.white);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        main.add(new Panel());
        
    }
    
    /**
     * Adds a group to this accordion panel with header = "title" and container = panel
     * @param title
     * @param panel 
     */
    public void addGroup(String title, Panel panel){
        Group group = new Group(title, panel);
        main.add(group,main.getComponentCount()-1);
        groups.add(group);
        group.setExpanded(false);
        
        repaint();
    }
    
    /**
     * Adds a group to this accordion panel with header = "title" and the compoents
     * @param title
     * @param comps 
     */
    public void addGroup(String title, Component[][] comps){
        addGroup(title, comps, null, null);
    }
    
    /**
     * Add a group to this accordion panel with header = "title" and the components
     * See Panel.setComponents(Component[][] comps, String[] colConst, String[] rowConst)
     * @param title
     * @param comps
     * @param colConst Column constraints
     * @param rowConst Row constraints
     * 
     * @see Panel
     */
    public void addGroup(String title, Component[][] comps, String[] colConst, String[] rowConst){
        //Calculating height
        
        Panel panel = new Panel() {
            /*
            @Override
            public Dimension getPreferredSize() {
                int height = 0;
                int extra = 10;
                for (Component[] r : comps) {
                    int maxrowheight = 0;
                    for (Component c : r) {
                        if (c != null && c.getPreferredSize().height > maxrowheight) {
                            maxrowheight = c.getPreferredSize().height;
                        }
                    }
                    extra += 2;
                    height += maxrowheight;
                }
                if(height>0){
                    return new Dimension(AccordionPanel.this.getWidth()-AccordionPanel.this.getInsets().left-AccordionPanel.this.getInsets().right-5, height+extra);
                }
                
                return super.getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getSize() {
                return getPreferredSize();
            }
            
            
            
            
            */
        };

        
        if(colConst==null || rowConst == null){
            panel.setComponents(comps);
        }else{
            panel.setComponents(comps,colConst,rowConst);
        }
        addGroup(title, panel);
        
    }
    
    
    /**
     * Remove a group by title
     * @param title 
     */
    public void removeGroup(String title){
        Group g = findGroupByTitle(title);
        removeGroup(g);
    }
    
    /**
     * Removes a group from this accordion panel
     * @param g 
     */
    private void removeGroup(Group g){
        groups.remove(g);
        main.remove(g);
    }

    
    /**
     * Expand all groups
     * @param expanded 
     */
    public void setExpanded(boolean expanded){
        for(Group g:groups){
            g.setExpanded(expanded);
        }
    }
    
    /**
     * Expand a group for group title
     * @param group
     * @param expanded 
     */
    public void setExpanded(String group, boolean expanded){
        Group g = findGroupByTitle(group);
        g.setExpanded(expanded);
    }

    /**
     * Removes all groups from this accordion panel
     */
    @Override
    public void removeAll() {
        if(groups !=null){
            groups.forEach((g)->{ main.remove(g); });
            groups.clear();
        }
    }
    
    
    

    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     * An internal group compoennt
     */
    class Group extends JComponent{
        String title;
        ToggleButton header;
        Component container;

        public Group(String title, Panel container) {
            this.title = title;
            //headerContainer = new Panel();
            header = new ToggleButton(title, null);
            header.addActionListener((e)->{
                setExpanded(header.isSelected());
            });
            header.setMarkSelection(null);
            header.setBorder(BorderFactory.createEmptyBorder());
            //headerContainer.setComponents(new Component[][]{{button}});
            this.container = container;
            header.setPreferredSize(new Dimension(AccordionPanel.this.getWidth()-20,30));
            
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
            add(header);
            add(container);

            
            
        }
        
        private void setExpanded(boolean expanded){
            Image image = expanded?iconGroupExpanded:iconGroupNoExpanded;
            header.setImage(image);
            this.container.setVisible(expanded);
            header.setSelected(expanded);
        }
    }

    
    private Group findGroupByTitle(String title){
        for(Group g:groups){
            if(g.title.equals(title)){
                return g;
            }
        }
        return null;
    }
    
}
