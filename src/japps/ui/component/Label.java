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
import japps.ui.component.action.DropActionListener;
import japps.ui.util.Dnd;
import japps.ui.util.Resources;
import japps.ui.util.Util;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 *
 * @author Williams Lopez - JApps
 */
public class Label extends JLabel implements IComponent, IDraggable{


    private String icon;
    private boolean scaleImage = false;
    private boolean proportionalScaleImage = true;
    private DropActionListener dropAction;
    

    public Label(String text) {
        super(text);
        setVerticalTextPosition(CENTER);
        setVerticalAlignment(CENTER);
        setHorizontalTextPosition(CENTER);
        setHorizontalAlignment(CENTER);
    }

    public Label() {
    }
    
    


    /**
     * Set an scaled image
     * @param img
     * @param w
     * @param h 
     */
    public void setImage(Image img, int w, int h){
        Image simg = null;
        if(img != null){
            simg = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        }
        setImage(simg);
    }
    
    
    /**
     * Set an image to this component
     * @param img 
     */
    public void setImage(Image img){
        if(img == null){
            super.setIcon(null);
            repaint(0, 0, getWidth(), getHeight());
        }else{
            super.setIcon(new ScalableImageIcon(img,this));
        }
        
    }
    
    
    /**
     * Sets an icon from the iconset by its name
     * @param iconName 
     */
    public void setIcon(String iconName){
        this.icon = iconName;
        Image image = Resources.icon(iconName);
        if(image!=null){
            setImage(image);
        }
    }
    
    /**
     * Sets an icon from the iconset by its name
     * @param iconName 
     * @param width 
     * @param height 
     */
    public void setIcon(String iconName, int width, int height){
        this.icon = iconName;
        Image image = Resources.icon(iconName);
        if(image!=null){
            setImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        }
    }

    /**
     * Wether the image of this label is scalable or not
     * @return 
     */
    public boolean isScaleImage() {
        return scaleImage;
    }

    /**
     * Sets the image scalable
     * Default is false
     * @param scaleImage 
     */
    public void setScaleImage(boolean scaleImage) {
        this.scaleImage = scaleImage;
    }

    /**
     * Wether the image is proportional scaling
     * @return 
     */
    public boolean isProportionalScaleImage() {
        return proportionalScaleImage;
    }

    /**
     * Sets the scale proportional or not
     * Default is true
     * @param proportionalScaleImage 
     */
    public void setProportionalScaleImage(boolean proportionalScaleImage) {
        this.proportionalScaleImage = proportionalScaleImage;
    }
    

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void setDndMode(int dndMode) {
        Dnd.mode(this, dndMode);
    }

    @Override
    public void setDropAction(DropActionListener action) {
        this.dropAction = action;
    }

    @Override
    public DropActionListener getDropAction() {
        return dropAction;
    }

    /**
     * A Scalable image icon
     */
    class ScalableImageIcon extends ImageIcon{
        
        Image originalImage;
        Label label;

        public ScalableImageIcon(Image image, Label label) {
            super(image);
            originalImage = image;
            this.label = label;
        }

        @Override
        public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
            
            if (isScaleImage()) {
                if (isProportionalScaleImage()) {
                    double factor = 1;
                    int w, h;
                    if (getWidth() > getHeight()) {
                        factor = (getHeight() / (double) originalImage.getHeight(label));
                    } else {
                        factor = (getWidth() / (double) originalImage.getWidth(label));
                    }
                    w = (int) (factor * (double) originalImage.getWidth(label));
                    h = (int) (factor * (double) originalImage.getHeight(label));
                    setImage(originalImage.getScaledInstance(w, h,Image.SCALE_SMOOTH));
                }else{
                    setImage(originalImage.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH));
                }

            }
            super.paintIcon(c, g, x, y);
        }
        
    }
    
    
    
    
}
 