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

import japps.ui.component.action.DropActionListener;
import japps.ui.util.Dnd;
import japps.ui.util.Log;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;
import java.awt.Component;
import javax.swing.JComponent;

/**
 * This is a panel component used as a container
 * It uses MigLayout,
 * The default method to set elements is setComponents
 * 
 * @author Williams Lopez - JApps
 */
public class Panel extends JComponent implements japps.ui.component.IComponent, IDraggable {

    public static final String GROW = "grow";
    public static final String GROWX = "growx";
    public static final String GROWY = "growy";
    public static final String FILL = "fill";
    public static final String CENTER = "center";
    public static final String RIGHT = "right";
    public static final String LEFT = "left";
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    public static final String NONE = "";
    
    public static final String FILL_GROW_CENTER = FILL +","+GROW+","+CENTER;

    Component[][] components;
    //private TitledBorder border;
    
    private DropActionListener dropAction;

    public Panel() {
        this.setLayout(new MigLayout());
        //this.border = new TitledBorder("");
        //this.setBorder(border);
    }



    /**
     * Set the components in this Panel with default constraints
     *
     * @param comps
     */
    public void setComponents(java.awt.Component[][] comps) {
        
        if(comps.length==0){
            return;
        }
        
        String[] colConstraints = new String[comps[0].length];
        String[] rowConstraints = new String[comps.length];
        
        for(int i=0;i<colConstraints.length;i++){
            colConstraints[i] = FILL_GROW_CENTER;
        }
        
        for(int i=0;i<rowConstraints.length;i++){
            rowConstraints[i] = FILL_GROW_CENTER;
        }
        
        
        setComponents(comps, colConstraints,rowConstraints);
        revalidate();
    }
    
    
    /**
     * Set the components in this Panel.<br/>
     * 
     * You can add a column or row constraint and it can be applyied to every cell.<br/>
     * 
     * @param comps
     * @param columnConstraint
     * @param rowConstraint 
     */
    public void setComponents(Component[][] comps, String columnConstraint, String rowConstraint){
        
        String[] rows = new String[comps.length];
        String[] cols = new String[comps[0].length];
        
        if (rowConstraint != null) {
            for (int i=0;i<rows.length;i++) {
                rows[i]=rowConstraint; 
            }
        }
        if (columnConstraint != null) {
            for (int i=0;i<cols.length;i++) {
                cols[i]=columnConstraint; 
            }
        }
        
        setComponents(comps, cols, rows);
        
    }
    

    /**
     * Set the components in this Panel<br/>
     *<br/>
     * You can add constraints for every column and every row<br/>
     * <br/>
     * columnConstraint must have the same count of elements as columns in the comp matrix<br/>
     * Every columnConstraint value must have the constraints, example:<br/>
     * <br/>
     * columnConstraint[0] = Panel.FILL_GROW_CENTER<br/>
     * columnConstraint[1] = Panel.CENTER<br/>
     * columnConstraint[2] = Panel.GROW + "," + Panel.CENTER<br/>
     * columnConstraint[3] = Panel.CENTER + "," + "width 10:20:40"   //minimum:preferred:maximum size<br/>
     * <br/>
     * rowConstraints must  have the same count of elements as rows in the comp matrix<br/>
     * Every element must have the constraints, example:<br/>
     * <br/>
     * rowConstraint[0] = Panel.NONE<br/>
     * rowConstraint[1] = Panel.FILL_GROW_CENTER<br/>
     * rowConstraint[2] = "width 10:20:40" (min:preferred:max), or height.<br/>
     * 
     * @param comps
     * @param columnConstraints 
     * @param rowConstraints 
     */
    public void setComponents(Component[][] comps, String[] columnConstraints, String[] rowConstraints) {

        this.removeAll();
        
        String sc = "", sr = "";
        if (columnConstraints != null) {
            for (String s : columnConstraints) {
                sc += "[" + s + "]";
            }
        }
        if (rowConstraints != null) {
            for (String s : rowConstraints) {
                sr += "[" + s + "]";
            }
        }

        System.out.println("sc:"+sc);
        System.out.println("sr:" +sr);

        setLayout(new MigLayout("insets 5", sc, sr));

        List<Component> processed = new ArrayList<>();
        for (int r = 0; r < comps.length; r++) {
            for (int c = 0; c < comps[r].length; c++) {
                Component comp = comps[r][c];
                if (!processed.contains(comp)) {
                    
                    if(comp == null){
                        comp = new Component(){};
                    }

                    int colspan = getWidthExpand(r, c, comps);
                    int rowspan = getHeightExpand(r, c, comps);
                    if (colspan == 1 && rowspan == 1) {
                        add(comp, "cell " + c + " " + r);
                    } else {
                        add(comp, "cell " + c + " " + r + " " + colspan + " " + rowspan);
                    }
                    processed.add(comp);
                }
            }
        }
    }

    /**
     * Get how many cols use the component in (row,col) in a row
     *
     * @param row
     * @param col
     * @param comp
     * @return
     */
    protected int getWidthExpand(int row, int col, Component[][] comp) {
        Component c = comp[row][col];
        int expand = 0;
        do {
            expand++;
            col = col + 1;
        } while (col < comp[row].length && comp[row][col] == c);
        return expand;
    }

    /**
     * Get how manu rows uses the component in (row,col) in a column
     *
     * @param row
     * @param col
     * @param comp
     * @return
     */
    protected int getHeightExpand(int row, int col, Component[][] comp) {
        Component c = comp[row][col];
        int expand = 0;
        do {
            expand++;
            row = row + 1;
        } while (row < comp.length && comp[row][col] == c);
        return expand;
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

}
