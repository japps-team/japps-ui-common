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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;


/**
 * Paint an image into a component, scale
 * 
 * @author Williams Lopez - JApps
 */
public class ImageComponent extends JComponent implements IComponent{
    
    private Image image;
    private float scaleFactor = 1;
    private boolean scalableImage = true;
    private boolean proportionalScaling = true;
    private Insets insets;
    private int lastW, lastH;
    private Image lastScaled;
            
    
    public ImageComponent(){
        setBorder(BorderFactory.createEmptyBorder());
        setInsets(new Insets(1, 1, 1, 1));
        setOpaque(true);
    }
    
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(isShowing() && image!=null){
            Image scaled = image;
            if(scalableImage){
                int w = getWidth()-(getInsets().left+getInsets().right);
                int h = getHeight()-(getInsets().top+getInsets().bottom);
                if(w <= 0) w = 5;
                if(h <= 0) h = 5;
                w = Math.round(w * scaleFactor);
                h = Math.round(h * scaleFactor);
                if(w == lastW && h == lastH){
                    scaled = lastScaled;
                }else{
                    scaled = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                    lastScaled = scaled;
                    lastW = w;
                    lastH = h;
                }
                
            }
            g.drawImage(scaled, getInsets().left, getInsets().top, this);
        }
    }


    /**
     * Get the scale factor, this factor is proportional to the
     * component size
     * @return 
     */
    public float getScaleFactor() {
        return scaleFactor;
    }

    /**
     * Set the scale factor, this factor is proportional to the
     * component size
     * @param scaleFactor 
     */
    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * Get the image
     * @return 
     */
    public Image getImage() {
        return image;
    }

    /**
     * Set the image
     * @param image 
     */
    public void setImage(Image image) {
        this.image = image;
        this.lastScaled = null;
        this.lastH = -1;
        this.lastW = -1;
                
        try {
            getGraphics().clearRect(0, 0, getWidth(), getHeight());
        } catch (Exception e) {
        }
        this.repaint();
    }

    /**
     * Check wether the image is scalable or not
     * @return 
     */
    public boolean isScalableImage() {
        return scalableImage;
    }

    /**
     * If true, image will be scaled into the component with the scaleFactor
     * @param scalable 
     */
    public void setScalableImage(boolean scalable) {
        this.scalableImage = scalable;
    }

    /**
     * Set the insets of this ImageComponent
     * @param insets 
     */
    public void setInsets(Insets insets) {
        this.insets = insets;
    }

    @Override
    public Insets getInsets() {
        return insets; //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Is the image scaling proportional
     * @return 
     */
    public boolean isProportionalScaling() {
        return proportionalScaling;
    }

    /**
     * Sets wether the image is proportional scaling or not
     * @param proportionalScaling 
     */
    public void setProportionalScaling(boolean proportionalScaling) {
        this.proportionalScaling = proportionalScaling;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
    

    
}
