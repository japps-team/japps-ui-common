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
import japps.ui.util.Resources;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;

/**
 *
 * @author Williams Lopez - JApps
 */
public class Dialogs {
    
    private static MessageDialog messageDialog;
    private static QuestionDialog questionDialog;
    
    public static final int APPROVE = 1;
    public static final int CANCEL  = 2;
    
    /**
     * Creates a dialog linked to the main frame
     * @param content Component to show
     * @param title
     * @param modal
     * @return 
     */
    public static JDialog create(JComponent content, String title,boolean modal, ActionListener closingAction){
        JDialog dialog = new JDialog(DesktopApp.APP.getMainWindow());
        dialog.setModal(modal);
        dialog.setContentPane(content);
        dialog.setTitle(title);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationByPlatform(true);
        dialog.setBackground(Color.WHITE);
        dialog.addKeyListener(new AbstractKeyListener(){
                @Override
                public void keyReleased(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                        dialog.setVisible(false);
                    }
                }
            });
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(closingAction!=null){
                    closingAction.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "closing"));
                }
            }

            @Override
            public void windowOpened(WindowEvent e) {
                center(dialog);
            }
            
        });
        return dialog;
    }
    
    /**
     * Show a message dialog
     * @param title
     * @param content 
     * @param image 
     */
    public static void message(String title, String content, Image image){
        if(messageDialog == null){   
            messageDialog = new MessageDialog();
        }
        messageDialog.show(title, content, image);
    }
    
    /**
     * Show a message dialog
     * @param title
     * @param content 
     */
    public static void message(String title, String content){
        message(title, content, null);
    }
    
    /**
     * Shows an error message dialog
     * @param title
     * @param content 
     */
    public static void error(String title, String content){
        Image error = Resources.icon("error.png",100,100);
        message(title, content, error);
    }
    
    /**
     * Question message dialog
     * @param title
     * @param content
     * @param image
     * @return 
     */
    public static int question(String title, String content, Image image){
        if(questionDialog == null){
            questionDialog = new QuestionDialog();
        }
        return questionDialog.show(title, content, image);
    }
    
    /**
     * Question message dialog
     * @param title
     * @param content
     * @return 
     */
    public static int question(String title, String content){
        Image image = Resources.icon("help.png",100,100);
        return question(title, content, image);
    }
    
    /**
     * 
     * @param dialog 
     */
    public static void center(JDialog dialog){
            double screenW = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
            double screenH = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
            
            int w = dialog.getWidth();
            int h = dialog.getHeight();
            
            int posX = ((int)screenW - w)<=0?0:((int)screenW - w)/2;
            int posY = ((int)screenH - h)<=0?0:((int)screenH - h)/2;
            
            dialog.setLocation(posX, posY);
        }

    
    private  static class MessageDialog extends JDialog{

        Label label;
        
        public MessageDialog() {
            super(DesktopApp.APP.getMainWindow());
            setModal(true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setLocationByPlatform(true);
            setBackground(Color.WHITE);
            label = new Label();
            label.setHorizontalTextPosition(Label.CENTER);
            label.setVerticalTextPosition(Label.BOTTOM);
            label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            addKeyListener(new AbstractKeyListener(){
                @Override
                public void keyReleased(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                        setVisible(false);
                    }
                }
            });
            setContentPane(label);
        }
        
        public void show(String title, String content, Image image){
            label.setText(content);
            this.setTitle(title);
            label.setImage(image);
            pack();
            center(this);
            setVisible(true);
        }

    }
    
    private static class QuestionDialog extends JDialog{

        int result;
        
        Label label;
        Button buttonAccept;
        Button buttonCancel;
        
        public QuestionDialog() {
            super(DesktopApp.APP.getMainWindow());
            buttonAccept = new Button(Resources.$("Accept"),(e)->{result = APPROVE; QuestionDialog.this.setVisible(false);});
            buttonCancel = new Button(Resources.$("Cancel"),(e)->{result = CANCEL;  QuestionDialog.this.setVisible(false);} );
            label = new Label();
            
            label.setHorizontalTextPosition(Label.CENTER);
            label.setVerticalTextPosition(Label.BOTTOM);
            label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            Panel panel = new Panel();
            
            panel.setComponents(new Component[][]{
                {label,label},
                {buttonCancel,buttonAccept}
            }, 
                new String[]{ Panel.RIGHT+","+Panel.GROW, Panel.LEFT+","+Panel.GROW  },
                new String[]{ Panel.FILL_GROW_CENTER, Panel.NONE  }
            );
            addKeyListener(new AbstractKeyListener(){
                @Override
                public void keyReleased(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                        setVisible(false);
                    }
                }
            });
            setContentPane(panel);
            setModal(true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setLocationByPlatform(true);
            setBackground(Color.WHITE);
            
        }
        
        public int show(String title, String content, Image image){
            result = CANCEL;
            label.setText(content);
            this.setTitle(title);
            label.setImage(image);
            pack();
            center(this);
            setVisible(true);
            return result;
        }
    }
}
