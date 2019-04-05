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

import japps.ui.component.action.DropActionListener;
import japps.ui.component.action.TransferActionListener;
import japps.ui.util.Log;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EventListener;
import java.util.EventObject;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import sun.font.FontManagerFactory;

/**
 *
 * @author Williams Lopez - JApps
 */
public class TextField extends ComponentField<String> {

    public static final int PASSWORD = 3;
    public static final int TEXT_AREA = 2;
    public static final int TEXT_FIELD = 1;
    
    JScrollPane scroll;
    JTextComponent component;
    boolean multiline = false;
    private TransferActionListener transferableListener;

    public TextField(){
        build(TEXT_FIELD);
    }
    
    public TextField(boolean multiline) {
        super();
        build(TEXT_AREA);
    }
    
    public TextField(int type){
        build(type);
    }
    
    private void build(int type){
        this.multiline = type == TEXT_AREA;
        
        switch(type){
            case TEXT_AREA: 
                this.scroll = new JScrollPane();
                this.component = new TextComponentArea();
                scroll.setViewportView(this.component);
                scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
                scroll.getViewport().setOpaque(false);
                scroll.setOpaque(false);
                this.add(scroll);
                this.setMaximumSize(new Dimension(200000,200000));
                break;
            case PASSWORD:
                this.component = new JPasswordField();
                this.add(component);
                break;
            default:
                this.component = new TextComponentField();
                this.add(component);
                break;
                
        }
        
        this.component.setEditable(true);
        
        this.component.setBorder(BorderFactory.createEmptyBorder());
        
        this.transferableListener = new TransferActionListener(this);
        this.component.addFocusListener(transferableListener);
        this.component.addKeyListener(transferableListener);
        this.component.addMouseListener(transferableListener);
        this.component.addMouseMotionListener(transferableListener);
        this.component.addMouseWheelListener(transferableListener);
        
    }

    @Override
    public void setValue(String value) {
        String old = getValue();
        if (value == null) {
            component.setText("");
        } else {
            component.setText(value);
        }
    }

    @Override
    public String getValue() {
        return component.getText();
    }

   
    /**
     * Append a string in this textfield
     *
     * @param str
     */
    public void append(String str) {
        String old = getValue();
        if(multiline){
            ((TextComponentArea)this.component).append(str);
        }else{
            this.component.setText(this.component.getText()+str);
        }
    }

    /**
     * Set this element editable
     *
     * @param editable
     */
    public void setEditable(boolean editable) {
        this.component.setEditable(editable);
        this.component.setBackground(Color.white);
        //this.component.setOpaque(true);
    }

    /**
     * Check whether this component is editable or not
     *
     * @return
     */
    @Override
    public boolean isEditable() {
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
    public void requestFocus() {
        this.component.requestFocus();
    }

    @Override
    public void grabFocus() {
        this.component.grabFocus();
    }

    @Override
    public JComponent getComponent() {
        return this.component;
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.component.setFont(font);
    }
    
    /**
     * Set line wrape to true or false when this text component is multiline
     * if it is not multiline then a runtime exception will be launched
     * @param wrp 
     */
    public void setLineWrap(boolean wrp){
        
        if(this.component instanceof JTextArea){
            ((JTextArea)this.component).setLineWrap(wrp);
        }else{
            throw new RuntimeException("setLineWrap error: The component is not multiline");
        }
        
    }
    
    
    
    class TextComponentArea extends JTextArea {
        
        boolean toolTipErased = false;

        public TextComponentArea() {
            
            addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    if(!toolTipErased && !TextComponentArea.this.getText().equals("")){
                        TextComponentArea.this.repaint();
                        toolTipErased = true;
                    }
                    if(toolTipErased && TextComponentArea.this.getText().equals("")){
                        TextComponentArea.this.repaint();
                        toolTipErased = false;
                    }
                }
            });
            
        }

        
        

        @Override
        protected void paintComponent(Graphics g) {            


            super.paintComponent(g);

            if (TextField.this.getValue() != null && !TextField.this.getValue().equals("")) {
                return;
            }
            

            String tooltiptext = TextField.this.getToolTipText();
            
            if(tooltiptext == null || tooltiptext.equals("")){
                return;
            }
            
            g.setColor(Color.lightGray);
            g.setFont(TextField.this.getFont());
            
            FontMetrics fm = g.getFontMetrics();
            int width = getWidth();
            int fontHeight = fm.getHeight();

            char[] buff = new char[1000];
            int count = 0;
            int line = 0;

            for (int i = 0; i < tooltiptext.length(); i++) {

                char c = tooltiptext.charAt(i);

                if (c == '\n') {
                    line++;
                    count = 0;
                } else if (c == ' ' || c == '\t') {
                    int w = fm.charsWidth(buff, 0, count)+50;
                    if (w >= width) {
                        g.drawString(new String(buff,0,count), getInsets().left , fontHeight*line + fontHeight + 3*line + getInsets().top-3);
                        line++;
                        count = 0;
                    } else {
                        buff[count] = c;
                        count++;
                    }
                } else if (i == tooltiptext.length() - 1) {
                    buff[count] = c;
                    g.drawString(new String(buff,0,count+1), 2 + getInsets().left , fontHeight*line + fontHeight + 3*line + getInsets().top);
                } else {
                    buff[count] = c;
                    count++;
                }
            }

        }

    }
    
    
    
    class TextComponentField extends JTextField {
        
        boolean toolTipErased = false;

        public TextComponentField() {
            
            addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    if(!toolTipErased && !TextComponentField.this.getText().equals("")){
                        TextComponentField.this.repaint();
                        toolTipErased = true;
                    }
                    if(toolTipErased && TextComponentField.this.getText().equals("")){
                        TextComponentField.this.repaint();
                        toolTipErased = false;
                    }
                }
            });
            
        }

        
        

        @Override
        protected void paintComponent(Graphics g) {            
            super.paintComponent(g);
            if (TextField.this.getValue() != null && !TextField.this.getValue().equals("")) {
                return;
            }
            String tooltiptext = TextField.this.getToolTipText();
            
            if(tooltiptext == null || tooltiptext.equals("")){
                return;
            }
            g.setColor(Color.lightGray);
            g.setFont(TextField.this.getFont());
            FontMetrics fm = g.getFontMetrics();
            int fontHeight = fm.getHeight();
            g.drawString(tooltiptext, getInsets().left , fontHeight + 3 + getInsets().top-3);
        }
    }

    
    
    

}
