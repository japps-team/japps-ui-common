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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.EventListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;

/**
 *
 * @author Williams Lopez - JApps
 */
public class ElementList<T> extends ComponentField<T[][]>{
    
    private boolean editable = true;
    private boolean enabled = true;
    private T[][] elements;
    private Panel container = new Panel();
    private SelectionGroup group;
    private ElementBuilder builder;

    public ElementList() {
        super();
        this.group = new SelectionGroup();
        
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
        
        setBuilder(new ElementBuilder() {

            @Override
            public Element build(Object element, int row, int col) {
                Element label = new Element();
                label.setHorizontalAlignment(Label.CENTER);
                label.setHorizontalTextPosition(Label.CENTER);
                label.setVerticalAlignment(Label.CENTER);
                label.setVerticalTextPosition(Label.CENTER);
                label.setMaximumSize(new Dimension(50000, 25));
                if(element!=null){
                    label.setText(element.toString());
                }
                return label;
            }
        });
        
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
                Element item = builder.build(e, row, col);
                item.addActionListener((ev)->{
                    fireElementListener_Clicked(e);
                });
                group.add(item);
                comp[row][col] = (Component)item;
                
                if(col==0 && row == 0){ item.setSelected(true); }
                if(selected !=null && e.equals(selected)){ item.setSelected(true); }
                col++;
            }
            row++;
        }
        
        String[] rowConstraint = new String[comp.length];
        for(int i=0;i<rowConstraint.length;i++){
            rowConstraint[i] = Panel.TOP;
        }
        
        this.container.setComponents(comp,new String[]{Panel.FILL_GROW_CENTER},rowConstraint);
        
        this.revalidate();
        this.repaint();
        
    }
    
    
    
    
    public void removeAll(){
        container.removeAll();
        this.revalidate();
        this.repaint();
    }


    /**
     * Get the current selected element in this field
     * @return 
     */
    public T getSelectedElement(){
        Element i = (Element)group.getSelected();
        if(i==null) return null;
        return (T)i.getElement();
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

    public ElementBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(ElementBuilder builder) {
        this.builder = builder;
    }
    
    
    
    
    public void addElementListener(ElementListener l){
        listenerList.add(ElementListener.class, l);
    }
    
    public void removeElementListener(ElementListener l){
        listenerList.remove(ElementListener.class, l);
    }
    
    public ElementListener[] getElementListener(){
        return listenerList.getListeners(ElementListener.class);
    }
    
    protected void fireElementListener_Clicked(T element){
        ElementListener[] l =getElementListener();
        if(l!=null){
            for(ElementListener e:l){
                e.clicked(element);
            }
        }
    }
    
    protected void fireElementListener_positionChanged(T e1, T e2){
        ElementListener[] l =getElementListener();
        if(l!=null){
            for(ElementListener e:l){
                e.positionChanged(e1, e2);
            }
        }
    }
    
    public interface ElementListener<T> extends EventListener{
        
        public void clicked(T element);
        
        public void positionChanged(T e1, T e2);
        
    }
    
    public static class Element extends ToggleButton{
        
        Object element;
        
        public void setElement(Object e){
            this.element = e;
            if(e!=null){
                this.setText(e.toString());
            }else{
                this.setText("");
            }
        }
        
        public Object getElement(){
            return this.element;
        }
    }
    
    public interface ElementBuilder{
        
        public ElementList.Element build(Object element, int row, int col);
        
    }
    
    
    public static void main(String[] args) {
        DesktopApp.init("test2", args);
        Label label = new Label("Lado izquierdo");
        Panel panel = new Panel();
        ElementList list = new ElementList();
        
        panel.setComponents(new Component[][]{{label},{list}}, 
                new String[]{Panel.FILL_GROW_CENTER},
                new String[]{"30:30:30,"+Panel.FILL_GROW_CENTER,Panel.FILL_GROW_CENTER});
        
        list.setValue(new Object[][]{{"uno"}});
        DesktopApp.start(panel);
    }
    
}
