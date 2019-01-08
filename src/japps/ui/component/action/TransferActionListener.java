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
package japps.ui.component.action;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;
import javax.swing.JComponent;

/**
 *
 * @author Williams Lopez - JApps
 */
public class TransferActionListener implements KeyListener, MouseListener, MouseWheelListener, FocusListener, MouseMotionListener{

        JComponent parent;

        public TransferActionListener(JComponent parent) {
            this.parent = parent;
        }
        
        
        @Override
        public void keyTyped(KeyEvent e) { Arrays.stream(parent.getKeyListeners()).forEach((l)->{ l.keyTyped(e); }); }

        @Override
        public void keyPressed(KeyEvent e) { Arrays.stream(parent.getKeyListeners()).forEach((l)->{ l.keyPressed(e); }); }

        @Override
        public void keyReleased(KeyEvent e) { Arrays.stream(parent.getKeyListeners()).forEach((l)->{ l.keyReleased(e); }); }

        @Override
        public void mouseClicked(MouseEvent e) { Arrays.stream(parent.getMouseListeners()).forEach((l)->{ l.mouseClicked(e); }); }

        @Override
        public void mousePressed(MouseEvent e) { Arrays.stream(parent.getMouseListeners()).forEach((l)->{ l.mousePressed(e); }); }

        @Override
        public void mouseReleased(MouseEvent e) { Arrays.stream(parent.getMouseListeners()).forEach((l)->{ l.mouseReleased(e); }); }

        @Override
        public void mouseEntered(MouseEvent e) { Arrays.stream(parent.getMouseListeners()).forEach((l)->{ l.mouseEntered(e); }); }

        @Override
        public void mouseExited(MouseEvent e) { Arrays.stream(parent.getMouseListeners()).forEach((l)->{ l.mouseExited(e); }); }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) { Arrays.stream(parent.getMouseWheelListeners()).forEach((l)->{ l.mouseWheelMoved(e); }); }

        @Override
        public void focusGained(FocusEvent e) { Arrays.stream(parent.getFocusListeners()).forEach((l)->{ l.focusGained(e); }); }

        @Override
        public void focusLost(FocusEvent e) { Arrays.stream(parent.getFocusListeners()).forEach((l)->{ l.focusLost(e); }); }

        @Override
        public void mouseDragged(MouseEvent e) { Arrays.stream(parent.getMouseMotionListeners()).forEach((l)->{ l.mouseDragged(e); }); }

        @Override
        public void mouseMoved(MouseEvent e) { Arrays.stream(parent.getMouseMotionListeners()).forEach((l)->{ l.mouseMoved(e); }); }



        
    }