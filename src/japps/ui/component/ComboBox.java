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

import japps.ui.DesktopApp;
import japps.ui.component.action.AbstractKeyListener;
import japps.ui.component.action.AbstractMouseListener;
import japps.ui.util.Resources;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import java.awt.Component;

/**
 *
 * @author Williams Lopez - JApps
 */
public class ComboBox<T> extends ComponentField<T>{
    

    private List<T> values;
    private List<ComboBoxItem> items;
    private Popup popup;
    private Panel panel;
    private ActionListener action;
    private TextField textField;
    private TextField textFieldSearch;
    private Button button;
    private Filter filter;
    private int mode = TABLE;
    private T value;
    private int maxDisplayedValues = 100;
    private Label statusLabel;

    
    public static int ROW=1;
    public static int TABLE=2;
    
    
    
    /**
     * Creates a new ComboBox
     * @param searchable true to add a search fiel to this combobox
     */
    public ComboBox(boolean searchable){
        
        Label iconSearch = new Label();
        panel = new Panel();
        textField = new TextField();
        textFieldSearch = new TextField();
        popup = new Popup();
        statusLabel = new Label();

        
        button = new Button();
        button.setAction((e)->{ showPopup(); });
        button.setImage(Resources.icon("expand-more.png"),20,20);
        button.setBorder(null);
        iconSearch.setImage(Resources.icon("search.png"),15,15);
        textFieldSearch.addKeyListener(new AbstractKeyListener(){
            public void keyReleased(KeyEvent e) { filter(textFieldSearch.getValue()); }
        });
        
        textField.setEditable(false);
        textField.setBorder(BorderFactory.createEmptyBorder());
        textField.addMouseListener(new AbstractMouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                showPopup();
            }
        });
        
        
        Panel popupMainPanel = new Panel();
        popupMainPanel.setBorder(BorderFactory.createEmptyBorder());
        if(searchable){
            popupMainPanel.setComponents(new Component[][]{
                { textFieldSearch, iconSearch },
                { panel, panel },
                { statusLabel, statusLabel}
            });
        }else{
            popupMainPanel.setComponents(new Component[][]{
                { panel}
            });
        }

        panel.setBorder(BorderFactory.createEmptyBorder());
        popup.setMainPanel(popupMainPanel);
        
        this.add(textField);
        this.add(button);
        this.setFilter((k,v)->{ 
            if( v == null || k == null) return false;
            return v.toString().toLowerCase().contains(k.toLowerCase()); 
        });
        
        
        
    }

    
    private void organizeElements(List<ComboBoxItem> _items){
        
        if(_items.size()>maxDisplayedValues){
            ArrayList<ComboBoxItem> values_trunqued = new ArrayList<>();
            for(int i=0;i<maxDisplayedValues;i++){
                values_trunqued.add(_items.get(i));
            }
            statusLabel.setText((_items.size()-maxDisplayedValues)+" "+Resources.$("more elements"));
            _items = values_trunqued;
        }else{
            statusLabel.setText("");
        }
        
        panel.removeAll();
        
        if(_items.isEmpty()) return;
        
        Component[][] comp;
        int rows, cols;
        
        if(mode == ROW){
           rows = _items.size();
           cols = 1;
        }else{
            Double sqrt = Math.sqrt(_items.size());
            rows = (sqrt - sqrt.intValue() == 0) ? sqrt.intValue(): sqrt.intValue()+1;
            cols = sqrt.intValue();
            if(_items.size() > rows*cols){
                cols++;
            }
        }
        
        int rc = 0, cc = 0;
        comp = new Component[rows][cols];
        
        for (ComboBoxItem item : _items) {
                comp[rc][cc] = item.component;
                cc++;
                if ((cc) % (cols) == 0) {
                    cc = 0;
                    rc++;
                }
        }
        
        panel.setComponents(comp);
    }
    
    
    /**
     * Set the view mode, ROW or TABLE
     * @param i 
     */
    public void setViewMode(int mode){
        this.mode = mode;
    }
    
    /**
     * Get all values
     * @return 
     */
    public List<T> getValues() {
        return this.values;
    }

    /**
     * Set values
     * @param values 
     */
    public void setValues(List<T> values) {
        this.values = values;
        items = new ArrayList<>();
        int id = 0;
        for(T v : values){
            ComboBoxItem item = new ComboBoxItem();
            item.id = id;
            item.value = v;
            item.component = new Button(v.toString(), (e)->{ popup.setVisible(false); setValue(v); if(action!=null) action.actionPerformed(e); });
            items.add(item);
            id++;
        }
        
    }
    
    /**
     * Set values
     * @param values 
     */
    public void setValues(T... values){
        setValues(Arrays.asList(values));
    }
    

    @Override
    public void setValue(T value) {
        this.value = value;
        this.textField.setValue(value.toString());
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public void setEditable(boolean editable) {
        this.textField.setEditable(editable);
    }

    @Override
    public boolean isEditable() {
        return this.textField.isEditable();
    }

    @Override
    public void setEnabled(boolean enabled) {
        textField.setEnabled(enabled);
        button.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return button.isEnabled();
    }

    /**
     * Get the action
     * @return 
     */
    public ActionListener getAction() {
        return action;
    }

    /**
     * Set the action
     * @param action 
     */
    public void setAction(ActionListener action) {
        this.action = action;
    }

    /**
     * Get the filter
     * @return 
     */
    public Filter<T> getFilter() {
        return filter;
    }

    /**
     * Set an object to filter the combobox list when editable
     * Default:
     * 
     * return obj.toString().contains("keys");
     * 
     * @param filter 
     */
    public final void setFilter(Filter<T> filter) {
        this.filter = filter;
    }

    /**
     * Get maximum number of values to display
     * @return 
     */
    public int getMaxDisplayedValues() {
        return maxDisplayedValues;
    }

    /**
     * Set maximum number of values to display
     * @param maxDisplayedValues 
     */
    public void setMaxDisplayedValues(int maxDisplayedValues) {
        this.maxDisplayedValues = maxDisplayedValues;
    }
    
    /**
     * Display the combobox popup
     */
    private void showPopup(){
        this.organizeElements(items);
        this.popup.pack();
        this.popup.show(this, 0, this.getHeight());
        SwingUtilities.invokeLater(()->{ this.textFieldSearch.requestFocus(); });
    }
    
    /**
     * Filter elements filtered
     * @param filter 
     */
    private void filter(String filter){
        
        List<ComboBoxItem> filtered = new ArrayList<>();
        for(ComboBoxItem v: items){
            if(getFilter().filter(filter, v.value)){
                filtered.add(v);
            }
        }
        this.organizeElements(filtered);
        this.popup.pack();
        SwingUtilities.invokeLater(()->{ this.textFieldSearch.requestFocus(); });
    }

    
    
    
    /**
     * A class to manage items in the combo box
     */
    class ComboBoxItem {
        int id;
        T value;
        Component component;

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof ComboBox.ComboBoxItem){
                ComboBoxItem obj2 = (ComboBoxItem)obj;
                return this.id == obj2.id;
            }
            return false;
        }
        
    }
    
}
