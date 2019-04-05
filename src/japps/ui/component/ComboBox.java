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
import japps.ui.component.action.TransferActionListener;
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;

/**
 *
 * @author Williams Lopez - JApps
 * @param <T>
 */
public class ComboBox<T> extends ComponentField<T>{
    

    private List<T> values;
    private List<ComboBoxItem> items;
    private Popup popup;
    private Panel panel;
    private Panel popupMainPanel;
   
    private TextField textField;
    private TextField textFieldSearch;
    private Button button;
    private Filter filter;
    private int mode = TABLE;
    private T value;
    private int maxDisplayedValues = 100;
    private Label statusLabel;
    private boolean searchable;
    private Button btnDelete;

    
    public static int ROW=1;
    public static int TABLE=2;
    
    
    
    /**
     * Creates a new ComboBox
     * @param searchable true to add a search fiel to this combobox
     */
    public ComboBox(boolean searchable){
        this.searchable = searchable;
        Label iconSearch = new Label();
        panel = new Panel();
        textField = new TextField();
        textFieldSearch = new TextField();
        popup = new Popup();
        statusLabel = new Label();

        
        button = new Button();
        button.addActionListener((e)->{ showPopup(); });
        button.setImage(Resources.icon("expand-more.png"),20,20);
        button.setBorder(BorderFactory.createEmptyBorder());
        iconSearch.setImage(Resources.icon("search.png"),15,15);
        textFieldSearch.addKeyListener(new AbstractKeyListener(){
            public void keyReleased(KeyEvent e) { filter(textFieldSearch.getValue()); }
        });
        
        btnDelete = new Button();
        btnDelete.setImage(Resources.icon("delete.png"),15,15);
        btnDelete.setSize(20, 20);
        btnDelete.addActionListener((e)->{ setValue(null); });
        
        textField.setEditable(false);
        textField.setBorder(BorderFactory.createEmptyBorder());
        textField.addMouseListener(new AbstractMouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                showPopup();
            }
        });
        
        
        popupMainPanel = new Panel();
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
        popup.setBorder(new RoundedBorder(popup));
        
        this.add(btnDelete);
        this.add(textField);
        this.add(button);
        this.setFilter((k,v)->{ 
            if( v == null || k == null) return false;
            return v.toString().toLowerCase().contains(k.toLowerCase()); 
        });
        
        
        TransferActionListener transferableListener = new TransferActionListener(this);

        this.textField.addFocusListener(transferableListener);
        this.textField.addKeyListener(transferableListener);
        this.textField.addMouseListener(transferableListener);
        this.textField.addMouseMotionListener(transferableListener);
        this.textField.addMouseWheelListener(transferableListener);

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
    
    
    public void setCleanButtonVisible(boolean visible){
        Component c = this.getComponent(0);
        if(visible){
            if(c != btnDelete) add(btnDelete,0);
        }else{
            if(c == btnDelete) remove(0);
        }
        revalidate();
        repaint();
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
            item.component = new Button(v.toString(), (e)->{ 
                popup.setVisible(false); 
                setValue(v); 
                fireActionListener(new ActionEvent(this, 0, "selected"));
            });
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
        this.textField.setValue(value==null?"":value.toString());
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
        this.panel.setMinimumSize(new Dimension(getWidth(), 25));
        this.popup.pack();
        this.popup.show(this, 0, this.getHeight());
        if(this.searchable){
            SwingUtilities.invokeLater(()->{ this.textFieldSearch.requestFocus();});
        }
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
        SwingUtilities.invokeLater(()->{ this.textFieldSearch.requestFocus();});
        
    }
    
    /**
     * Adds an action listener to this component, action listener in combobox is launched when an element in 
     * combobox is selected
     * @param l 
     */
    public void addActionListener(ActionListener l){
        listenerList.add(ActionListener.class, l);
    }
    
    /**
     * Removes an ActionListener
     * @param l 
     */
    public void removeActionListener(ActionListener l){
        listenerList.remove(ActionListener.class, l);
    }
    
    /**
     * Get all ActionListener in this component
     * @return 
     */
    public ActionListener[] getActionListener(){
        return listenerList.getListeners(ActionListener.class);
    }
    
    /**
     * Fire all action listener in this component
     * @param e 
     */
    public void fireActionListener(ActionEvent e){
        ActionListener[] ls = getActionListener();
        if(ls== null)return; 
        for(ActionListener l: ls){
            l.actionPerformed(e);
        }
    }

    @Override
    public void setToolTipText(String text) {
        textField.setToolTipText(text);
        super.setToolTipText(text);
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
