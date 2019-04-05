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

import japps.ui.component.action.TransferActionListener;
import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 *
 * @author Williams Lopez - JApps
 * @param <V>
 */
public abstract class ComponentField<V> extends JComponent implements IComponent{

    private List<Validatior> validator;
    private static Popup popupValidation;
    private Label labelValidation;
    private RoundedBorder border;
    
    public ComponentField() {
        this.setRequestFocusEnabled(true);
        this.border = new RoundedBorder(this);
        this.labelValidation = new Label();
        this.validator = new ArrayList<>();
        this.setBorder(border);
        this.setMinimumSize(new Dimension(35, 35));
        this.setPreferredSize(new Dimension(100, 35));
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                fireValidator(false,false);
            }
        });
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                fireValidator(false,true);
            }
        });
        this.setMaximumSize(new Dimension(10000, 35));
    }
    
    
    /**
     * Sets the value for the field
     * @param value 
     */
    public abstract void setValue(V value);
    
    /**
     * Gets the field current value
     * @return 
     */
    public abstract V getValue();
   
    
    /**
     * Set this element editable
     * @param editable 
     */
    public abstract void setEditable(boolean editable);
    
    /**
     * Check whether this component is editable or not
     * @return 
     */
    public abstract boolean isEditable();
    
    /**
     * Set this component enabled or not
     * @param enabled 
     */
    public abstract void setEnabled(boolean enabled);
    
    /**
     * Check whether this component is enabled or not
     * @return 
     */
    public abstract boolean isEnabled();

    @Override
    public JComponent getComponent() {
        return this;
    }
    
    
    
    /**
     * Adds a validator to this component field
     * @param validator 
     */
    public void addValidator(Validatior validator){
        this.validator.add(validator);
    }
    
    /**
     * Validate this fields with the validaros provided
     * @return 
     */
    public boolean validateField(){
        return fireValidator(false, false);
    }
    
    
    

    /**
     * Fire validations on this component 
     * @param onlyValidate if false it doesn show the validations errors
     * @return 
     */
    private boolean fireValidator(boolean onlyValidate, boolean showPopup){
        if (validator == null || validator.isEmpty()) {
            return true;
        }
        StringBuilder messages = new StringBuilder();
        boolean valid = true;
        try {
            for(Validatior v : validator){
                if(!v.validate(this)){
                    valid = false;
                    break;
                }
            }
        } catch (Exception e) {
            valid = false;
            messages.append(e.getMessage());
            messages.append("\n");
        }
        
        if (!onlyValidate) {
            launchInfoValidation(valid, messages.toString());
            if (!valid && showPopup) {
                getPopupInstance().removeAll();
                getPopupInstance().add(labelValidation);
                getPopupInstance().pack();
                getPopupInstance().show(ComponentField.this, 0, ComponentField.this.getHeight());
                ComponentField.this.getComponent().requestFocusInWindow();
            }
        }
        
        
        
        return valid;
    }
    
   
    private void launchInfoValidation(boolean valid,String message){
        if(valid){
            this.border.setLineColor(Color.GREEN);
            getPopupInstance().setVisible(false);
        }else{
            labelValidation.setText(message);
            labelValidation.setIcon("info.png", 20, 20);
            this.border.setLineColor(Color.RED);
        }
        this.repaint();
        
    }
    
    private Popup getPopupInstance(){
        if(popupValidation == null){
            popupValidation = new Popup();
            RoundedBorder border =  new RoundedBorder(this);
            border.setLineColor(Color.RED);
            popupValidation.setBorder(border);
        }
        return popupValidation;
    }
    
    /**
     * A validator interfaces, used to determinate if this component value is correct or not
     */
    public static interface Validatior{
        /**
         * Validate a component field, it means this method has the logic to determinate if
         * the ComponentField content is correct or not
         * If the method throws a RuntimeException the message of this exception will be catched
         * and displayed as a message
         * @param comp
         * @return 
         */
        public boolean validate(JComponent comp);
    }
    
  
    
    
}
