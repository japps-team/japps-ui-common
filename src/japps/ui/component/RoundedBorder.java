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

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Williams Lopez - JApps
 */
public class RoundedBorder extends LineBorder{
    
    Component comp;
    
    private Insets borderInsets = new Insets(5, 5, 5, 5);

    public RoundedBorder(Component comp) {
        super(Color.lightGray, 1, true);
        this.comp = comp;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return borderInsets;
    }
    
    public void setBorderInsets(Insets insets) {
        this.borderInsets = insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public Color getLineColor() {
        return this.lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }
    
    
    
    
}
