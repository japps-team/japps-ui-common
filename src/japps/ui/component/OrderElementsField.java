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
package japps.ui.component;

import japps.ui.DesktopApp;
import japps.ui.component.action.DropActionListener;
import japps.ui.util.Dnd;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;

/**
 *
 * @author Williams Lopez - JApps
 * @param <T>
 */
public class OrderElementsField<T> extends ComponentField<T[][]>{
    
    private boolean editable = true;
    private boolean enabled = true;
    private T[][] elements;
    private Panel container = new Panel();
    private SelectionGroup group;
    private List<ElementListener<T>> elementListener;
    
    

    public OrderElementsField() {
        super();
        this.group = new SelectionGroup();
        this.elementListener = new ArrayList<>();
        
        this.container = new Panel();
        
        JScrollPane scroll = new JScrollPane();
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setViewportView(this.container);
        scroll.getViewport().setBackground(Color.white);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(scroll);
        this.setMaximumSize(new Dimension(20000, 20000));
    }
    
    
    /**
     * Sets the elements in an initial order
     * @param elements 
     */
    @Override
    public void setValue(T[][] elements) {
        
        this.elements = elements;
        
        if(elements == null || elements.length==0){
            this.container.removeAll();
            return;
        }
        
        T selected = getSelectedElement();
        this.group.removeAll();
        
        Component[][] comp = new Component[elements.length][elements[0].length];
        int row=0;
        for(T[] r : elements){
            int col = 0;
            for(T e: r){
                Item item = new Item(row,col);
                item.setElement(e);
                group.add(item);
                comp[row][col] = item;
                
                if(col==0 && row == 0){ item.setSelected(true); }
                if(selected !=null && e.equals(selected)){ item.setSelected(true); }
                col++;
            }
            row++;
        }
        this.container.setComponents(comp);
        
        this.revalidate();
        this.repaint();
        
    }
    
    /**
     * Get the current selected element in this field
     * @return 
     */
    public T getSelectedElement(){
        Item i = (Item)group.getSelected();
        if(i==null) return null;
        return i.getElement();
    }
    

    @Override
    public T[][] getValue() {
        return this.elements;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean isEditable() {
        return this.editable;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
    
    /**
     * Renders the display data from elements in this field
     * @param elementLabel
     * @param element 
     */
    protected void renderElement(int row, int column, Label elementLabel, T element){
        if(element != null){
            elementLabel.setText(element.toString());
        }else{
            elementLabel.setText("");
        }
        elementLabel.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1, true));
    }
    
    
    public void addElementListener(ElementListener listener){
        this.elementListener.add(listener);
    }
    
    public void removeElementListener(ElementListener listener){
        this.elementListener.remove(listener);
    }
    
    public ElementListener[] getElementListener(){
        ElementListener[] array = new ElementListener[this.elementListener.size()];
        array = this.elementListener.toArray(array);
        return array;
    }
    
    protected void fireElementListener_Clicked(T element){
        this.elementListener.forEach((l)->{ l.clicked(element); });
    }
    
    protected void fireElementListener_positionChanged(T e1, T e2){
        this.elementListener.forEach((l)->{ l.positionChanged(e1, e2); });
    }
    
    private class Item extends ToggleButton{
        
        T element;
        int row, column;
        
        public Item(int row,int column) {
            this.row = row;
            this.column = column;
            setDndMode(Dnd.BOTH);   
            setDropAction(new DropActionListener() {
                @Override
                public void drop(JComponent droped, JComponent dropedIn) {
                    Item item1 = (Item)droped;
                    Item item2 = (Item)dropedIn;
                    
                    T e1 = item1.getElement();
                    T e2 = item2.getElement();
                    
                    item2.setElement(e1);
                    item1.setElement(e2);
                    
                    elements[item2.row][item2.column] = e1;
                    elements[item1.row][item1.column] = e2;
                    
                    fireElementListener_positionChanged(e1, e2);
                    
                }
            });
            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    fireElementListener_Clicked(Item.this.element);
                }
                
            });
            setHorizontalAlignment(Label.CENTER);
            setHorizontalTextPosition(Label.CENTER);
            setVerticalAlignment(Label.CENTER);
            setVerticalTextPosition(Label.CENTER);
        }
        
        public void setElement(T element){
            
            this.element = element;
            
            if(element == null){
                this.setText("");
                this.setImage(null);
            }else{
                renderElement(this.row,this.column,this, element);
            }
        }
        
        public T getElement(){
            return this.element;
        }
    }
    
    
    public interface ElementListener<T> extends EventListener{
        
        public void clicked(T element);
        
        public void positionChanged(T e1, T e2);
        
    }
    
    public static void main(String[] args) {
        DesktopApp.init("test", args);
        OrderElementsField<String> field = new OrderElementsField<>();
        field.setValue(new String[][]{{"uno","dos"},{"tres","cuatro"}});
        DesktopApp.start(field);
    }
}
