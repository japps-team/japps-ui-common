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
import javax.swing.JComponent;
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

    JScrollPane scroll;
    JTextArea component;
    boolean multiline;
    private TransferActionListener transferableListener;

    public TextField() {
        super();
        this.component = new TextComponent();
        this.transferableListener = new TransferActionListener(this);
        scroll = new JScrollPane();

        this.component.addFocusListener(transferableListener);
        this.component.addKeyListener(transferableListener);
        this.component.addMouseListener(transferableListener);
        this.component.addMouseMotionListener(transferableListener);
        this.component.addMouseWheelListener(transferableListener);

        scroll.setViewportView(this.component);
        scroll.setMinimumSize(new Dimension(10, 40));
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        scroll.getViewport().setBackground(Color.white);
        this.add(scroll);
    }

    @Override
    public void setValue(String value) {
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
     * Set the text field as multiline or not
     *
     * @param multiline
     */
    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
        if (this.multiline) {
            this.component.setRows(3);
            remove(component);
            add(scroll);
        } else {
            this.component.setRows(1);
            remove(scroll);
            add(component);
        }
        repaint();
    }

    /**
     * Get if this field is multiline or not
     *
     * @return
     */
    public boolean isMultiline() {
        return this.multiline;
    }

    /**
     * Append a string in this textfield
     *
     * @param str
     */
    public void append(String str) {
        if (this.component instanceof JTextArea) {
            ((JTextArea) component).append(str);
        } else {
            this.component.setText(this.component.getText() + str);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.component.setFont(font);
    }

    class TextComponent extends JTextArea {
        
        boolean toolTipErased = false;

        public TextComponent() {
            
            addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    if(!toolTipErased && !TextComponent.this.getText().equals("")){
                        System.out.println("clened1");
                        TextComponent.this.repaint();
                        toolTipErased = true;
                    }
                    if(toolTipErased && TextComponent.this.getText().equals("")){
                        System.out.println("clened2");
                        TextComponent.this.repaint();
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
                    System.out.println(line + ":" + new String(buff, 0, count));
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
                    g.drawString(new String(buff,0,count), 2 + getInsets().left , fontHeight*line + fontHeight + 3*line + getInsets().top);
                } else {
                    buff[count] = c;
                    count++;
                }
            }

        }

    }

}
