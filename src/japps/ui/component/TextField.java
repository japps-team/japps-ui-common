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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Williams Lopez - JApps
 */
public class TextField extends ComponentField<String>{

    JTextComponent component;
    boolean multiline;
    
    public TextField() {
        super();
        setMultiline(false);

    }
    
    @Override
    public void setValue(String value) {
        component.setText(value);
    }

    @Override
    public String getValue() {
        return component.getText();
    }
    
    /**
     * Set the text field as multiline or not
     * @param multiline 
     */
    public void setMultiline(boolean multiline){
        this.multiline = multiline;
        
        String text = (component!=null)?component.getText():"";
        
        if(multiline){
            if(!(this.component instanceof JTextArea)){
                if(this.component!=null) this.remove(this.component);
                this.component = new JTextArea();
                JScrollPane scroll = new JScrollPane();
                scroll.setViewportView(this.component);
                scroll.setMinimumSize(new Dimension(10, 40));
                scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
                this.add(scroll);
            }
        }else{
            if(!(this.component instanceof JTextField)){
                if(this.component!=null) this.remove(this.component);
                this.component = new JTextField();
                this.add(this.component);
            }
        }
        
       this.component.setBorder(new EmptyBorder(0, 0, 0, 0));
       //this.component.setOpaque(false);
       this.component.setText(text);
    }
    
    /**
     * Get if this field is multiline or not
     * @return 
     */
    public boolean isMultiline(){
        return this.multiline;
    }
    
    /**
     * Append a string in this textfield
     * @param str 
     */
    public void append(String str){
        if(this.component instanceof JTextArea){
            ((JTextArea)component).append(str);
        }else{
            this.component.setText(this.component.getText()+str);
        }
    }
    
    /**
     * Set this element editable
     * @param editable 
     */
    public void setEditable(boolean editable){
        this.component.setEditable(editable);
        this.component.setBackground(Color.white);
        //this.component.setOpaque(true);
    }
    
    /**
     * Check whether this component is editable or not
     * @return 
     */
    @Override
    public boolean isEditable(){
        return this.component.isEditable();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.component.setEnabled(enabled);
        //this.component.setOpaque(true);
        this.component.setBackground(Color.white);
    }

    @Override
    public boolean isEnabled() {
        return this.component.isEnabled();
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        if(this.component!=null) this.component.addMouseListener(l);
    }

    @Override
    public synchronized void addKeyListener(KeyListener l) {
        if(this.component!=null) this.component.addKeyListener(l);
    }

    @Override
    public void requestFocus() {
        this.component.requestFocus();
    }

    @Override
    public void grabFocus() {
        this.component.grabFocus();
    }
    
    
    
}
