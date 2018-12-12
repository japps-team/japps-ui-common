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
import japps.ui.util.Resources;
import japps.ui.util.Util;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 *
 * @author Williams Lopez - JApps
 */
public class ToggleButton extends Button{
    
    private boolean selected;
    
    
    private Image markSelected = null;
    

    public ToggleButton(ActionListener action) {
        super(action);
        build();
    }

    public ToggleButton(String text, ActionListener action) {
        super(text, action);
        build();
    }

    public ToggleButton() {
        build();
    }


    

    private void build(){
        //setOpaque(true);
        markSelected = Resources.icon("done.png");
       // markSelected = Util.readImage("/home/fernando/NetBeansProjects/japps-ui-common-test/res/img/great.png");
        
                
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!isSelected()) {
            return;
        }
        if(markSelected == null){
            return;
        }
        double factor = 1;
        int w, h;
        if(getWidth()> getHeight()){
            factor = (getHeight()/(double)markSelected.getHeight(this))/5;
        }else{
            factor = (getWidth()/(double)markSelected.getWidth(this))/5;
        }
        w = (int)(factor*(double)markSelected.getWidth(this));
        h = (int)(factor*(double)markSelected.getHeight(this));
        
        w = w<2?w=2:w;
        h = h<2?h=2:h;
        
        Image image = markSelected.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        
        g.drawImage(image, getWidth()-getInsets().right-5-w, getInsets().top+5, w, h, this);
        //g.fillRect(0, 0, w, h);

    }


    /**
     * Wether this component is selected or not
     * @return 
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set this component state to selected
     * @param selected 
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        this.repaint();
    }

    @Override
    protected void fireAction(ActionEvent e) {
        setSelected(!selected);
        super.fireAction(e);
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

    
}
