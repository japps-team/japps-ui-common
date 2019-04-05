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

import japps.ui.component.action.AbstractKeyListener;
import japps.ui.component.action.AbstractMouseListener;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;


/**
 * 
 * Button component 
 *
 * @author Williams Lopez - JApps
 */
public class Button extends Label{
    
    private String command;

    
    public Button(String text, ActionListener action){
        super();
        _construct(text,action);
    }
    
    public Button(ActionListener action){
        super();
        _construct(null,action);
    }
    
    public Button(){
        super();
        _construct(null,null);
    }
    
    /**
     * Creates a new Button component.
     * Ej: new Button("MyButton",(e)->{  //do something  })
     * @param text
     * @param action 
     */
    public final void _construct(String text, ActionListener action) {
        
        this.setFocusable(true);
        this.setRequestFocusEnabled(true);
        
        
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        //this.setBorder(new RoundedBorder());
        this.addMouseListener(new AbstractMouseListener(){
            
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
                if(!isEnabled()) return;
                fireActionListener(new ActionEvent(e.getSource(), e.getID(), getCommand()));
            }
        });
        this.addKeyListener(new AbstractKeyListener(){
            @Override
            public void keyReleased(KeyEvent e) {
                if(!isEnabled()) return;
                if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE){
                    fireActionListener(new ActionEvent(e.getSource(), e.getID(), getCommand()));
                }
            }
        });
        
        this.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.gray, 1, true));
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(BorderFactory.createEmptyBorder());
            }
        });
        
        this.setText(text);
        this.addActionListener(action);

    }
    
    /**
     * Get the actions of this button
     * @return 
     */
    public ActionListener[] getActionListener() {
        return this.listenerList.getListeners(ActionListener.class);
    }

    /**
     * Add actionListener listener
     * @param action 
     */
    public void addActionListener(ActionListener action) {
        if(action!=null)
        this.listenerList.add(ActionListener.class, action);
    }
    
    /**
     * Removes an actionListener listener
     * @param action 
     */
    public void removeActionListener(ActionListener action){
        this.listenerList.remove(ActionListener.class, action);
    }

    /**
     * Set actionListener commando of this button
     * @return 
     */
    public String getCommand() {
        return command;
    }

    /**
     * Get actionListener command of this button
     * @param command 
     */
    public void setCommand(String command) {
        this.command = command;
    }
    
    /**
     * Fires all actionListener listeners
     * @param e 
     */
    public void fireActionListener(ActionEvent e){
        for(ActionListener l: listenerList.getListeners(ActionListener.class)){
            l.actionPerformed(e);
        }
    }
    

    
}
