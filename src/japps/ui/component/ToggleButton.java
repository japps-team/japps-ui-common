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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;
import javax.swing.Icon;
import javax.swing.JRadioButton;

/**
 *
 * @author Williams Lopez - JApps
 */
public class ToggleButton extends Button implements ISelectable{
    
    private boolean selected;
    
    
    private Image markSelection = null;
    
    
    public ToggleButton(SelectStateListener action) {
        super(null);
        build();
        addSelectStateListener(action);
    }

    public ToggleButton(String text, SelectStateListener action) {
        super(text, null);
        build();
        addSelectStateListener(action);
    }

    public ToggleButton() {
        build();
    }


    

    private void build(){
        //setOpaque(true);
        markSelection = Resources.icon("done.png");
       // markSelection = Util.readImage("/home/fernando/NetBeansProjects/japps-ui-common-test/res/img/great.png");
                
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        int w=5, h=5;
        int x=0, y=0;
        //Calculating width and height
        if (markSelection != null) {
            double factor = 1;
            if (getWidth() > getHeight()) {
                factor = (getHeight() / (double) markSelection.getHeight(this)) / 5;
            } else {
                factor = (getWidth() / (double) markSelection.getWidth(this)) / 5;
            }
            w = (int) (factor * (double) markSelection.getWidth(this));
            h = (int) (factor * (double) markSelection.getHeight(this));

            w = w < 15 ? 15 : w;
            h = h < 15 ? 15 : h;
            
            x = getWidth()-getInsets().right-w;
            y = getInsets().top;

            g.clearRect(x, y, w, h);
        }

        super.paintComponent(g);

        if(markSelection != null && isSelected()){
            Image image = markSelection.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            g.drawImage(image,x , y, w, h, this);
        }

    }

    
    /**
     * Wether this component is selected or not
     * @return 
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set this component state to selected
     * @param selected 
     */
    @Override
    public void setSelected(boolean selected) {
        setSelectedWithoutFiringEvent(selected);
        fireSelectStateListener(new ActionEvent(this, 0, Boolean.toString(selected)));
    }

   
    @Override
    public void setImage(Image img) {
        if(img == null){
            Icon icon = null;
            super.setIcon(icon);
            super.setDisabledIcon(null);
            repaint(0, 0, getWidth(), getHeight());
        }else{
            super.setIcon(new ScalableImageIcon(img,this));
            super.setDisabledIcon(super.getIcon());
        }
    }

    /**
     * Gets the image mark for this component when is selected
     * @return 
     */
    public Image getMarkSelection() {
        return markSelection;
    }

    /**
     * Get the image mark showed when this component is selected
     * @param markSelection 
     */
    public void setMarkSelection(Image markSelection) {
        this.markSelection = markSelection;
    }

    @Override
    public void fireActionListener(ActionEvent e) {
        setSelected(!selected);
        super.fireActionListener(e);
    }
    
    /**
     * Set this component to selected without firing select event
     * @param selected 
     */
    public void setSelectedWithoutFiringEvent(boolean selected){
        this.selected = selected;
        this.repaint();
    }

    @Override
    public void addSelectStateListener(SelectStateListener listener) {
        listenerList.add(SelectStateListener.class, listener);
    }

    @Override
    public void removeSelectStateListener(SelectStateListener listener) {
        listenerList.remove(SelectStateListener.class, listener);
    }
    
    public SelectStateListener[] getSelectStateListener(){
        return listenerList.getListeners(SelectStateListener.class);
    }
    
    public void fireSelectStateListener(ActionEvent e){
        SelectStateListener[] list = getSelectStateListener();
        for(SelectStateListener l : list){
            l.state(e);
        }
    }
    
    
    
}
