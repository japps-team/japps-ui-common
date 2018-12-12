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
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;
import org.jdatepicker.DateModel;
import org.jdatepicker.JDatePicker;

/**
 *
 * @author Williams Lopez - JApps
 * @param <Date>
 */
public class DateField<Date> extends ComponentField{
    
    JDatePicker picker;
    
    public DateField(){
        picker = new JDatePicker();
        picker.getButton().setOpaque(true);
        picker.getButton().setBackground(Color.WHITE);
        picker.getButton().setBorder(new EmptyBorder(0, 0, 0, 0));
        picker.getButton().setText("");
        picker.getButton().setIcon(new ImageIcon(Resources.icon("today.png",20,20)));
        picker.getFormattedTextField().setBorder(new EmptyBorder(0,0, 0,0));
        picker.getFormattedTextField().setBackground(Color.white);
        this.add(picker);
    }
    

    @Override
    public void setValue(Object value) {
        DateModel<Date> model = (DateModel<Date>)this.picker.getModel();
        model.setValue((Date)value);
    }

    @Override
    public Date getValue() {
        return (Date)this.picker.getModel().getValue();
    }

    @Override
    public void setEditable(boolean editable) {
        this.picker.getButton().setEnabled(editable);
    }

    @Override
    public boolean isEditable() {
        return this.picker.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.picker.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return this.picker.isEnabled();
    }

    
    
    
}
