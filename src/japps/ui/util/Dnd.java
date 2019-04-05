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
package japps.ui.util;

import japps.ui.DesktopApp;
import japps.ui.component.IDraggable;
import japps.ui.component.action.DropActionListener;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author Williams Lopez - JApps
 */
public class Dnd {

    public static final int DRAGGABLE = 1;
    public static final int DROPPABLE = 2;
    public static final int BOTH = 4;
    public static final int NONE = 3;

    public static JComponent DRAGGED = null;
    public static JComponent OVER = null;
    private static final JDialog frame;

    static {
        frame = new JDialog(DesktopApp.APP.getMainWindow());
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.setOpacity(0.7f);
    }
    
    /**
     * Sets the mode for component "comp"
     * DROPPABLE, DRAGGABLE, NONE
     * @param comp
     * @param mode 
     */
    public static void mode(JComponent comp, int mode){
        switch(mode){
            case DROPPABLE: droppable(comp); break;
            case DRAGGABLE: draggable(comp); break;
            case BOTH: droppable(comp); draggable(comp); break;
            case NONE: removeAdapters(comp); break;
        }
    }
    
    public static void removeAdapters(JComponent comp){
        MouseListener[] listeners = comp.getMouseListeners();
        boolean already = false;
        if (listeners != null) {
            for (MouseListener l : listeners) {
                if (l instanceof DnDDraggableAdapter || l instanceof DndDropableAdapter) {
                    comp.removeMouseListener(l);
                    comp.removeMouseMotionListener((MouseMotionListener)l);
                }
            }
        }
    }

    private static void draggable(JComponent comp) {
        MouseListener[] listeners = comp.getMouseListeners();
        boolean already = false;
        if (listeners != null) {
            for (MouseListener l : listeners) {
                if (l instanceof DnDDraggableAdapter) {
                    already = true;
                }
            }
        }

        if (!already) {
            DnDDraggableAdapter adapter = new DnDDraggableAdapter(comp);
            comp.addMouseListener(adapter);
            comp.addMouseMotionListener(adapter);

        }

    }

    private static void droppable(JComponent comp) {
        MouseListener[] listeners = comp.getMouseListeners();
        boolean already = false;
        if (listeners != null) {
            for (MouseListener l : listeners) {
                if (l instanceof DndDropableAdapter) {
                    already = true;
                }
            }
        }

        if (!already) {
            DndDropableAdapter adapter = new DndDropableAdapter(comp);
            comp.addMouseListener(adapter);
            comp.addMouseMotionListener(adapter);
            comp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    private static void drop() {
        JComponent c1 = DRAGGED;
        JComponent c2 = OVER;
        DRAGGED = null;
        OVER = null;

        try {
            if (c1 != null && c2 != null && c1 != c2) {
                if(c2 instanceof IDraggable){
                    DropActionListener action = ((IDraggable)c2).getDropAction();
                    if(action!=null){
                        action.drop(c1,c2);
                    }
                }
            }
        } catch (Exception e) {
        }

        frame.setVisible(false);

    }

    private static class DnDDraggableAdapter extends MouseAdapter {

        JComponent comp;

        public DnDDraggableAdapter(JComponent comp) {
            this.comp = comp;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            
            comp.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            
            DRAGGED = comp;
            frame.setSize(comp.getSize());
            frame.setLocation(MouseInfo.getPointerInfo().getLocation());
            if (!frame.isVisible()) {
                frame.setVisible(true);
            }
            comp.paint(frame.getGraphics());

            if (OVER == null) {
                frame.getGraphics().setColor(Color.red);
            } else {
                frame.getGraphics().setColor(Color.green);
            }

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if(DRAGGED != null){
                DRAGGED.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            if(OVER!=null){
                OVER.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            comp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            drop();
        }

    }

    private static class DndDropableAdapter extends MouseAdapter {

        JComponent comp;

        public DndDropableAdapter(JComponent comp) {
            this.comp = comp;
        }

        @Override
        public void mouseEntered(MouseEvent e) {        
            if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
                if(DRAGGED != comp){
                    comp.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    Dnd.OVER = comp;
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
                comp.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                Dnd.OVER = null;
            }
        }

    }

}
