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
package japps.ui;

import japps.ui.component.Label;
import static japps.ui.util.Resources.*;
import japps.ui.util.Log;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author Williams López
 */
public class DesktopApp{
    
    public static DesktopApp APP;
    private  final JFrame mainWindow;
    private JComponent mainPanel;
    private boolean fullscreen = false;
   
    private JDialog messageDialog;
    
    /**
     * Constructs a DesktopApp
     */
    public DesktopApp(){
        this.mainWindow = new JFrame(p("app.name"));
        this.mainWindow.setTitle(p("app.name"));
        this.mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainWindow.setLocationByPlatform(true);
        this.mainWindow.setLayout(new BoxLayout(this.mainWindow.getContentPane(), BoxLayout.Y_AXIS));
        Image img = icon(p("app.icon"));
        this.mainWindow.setIconImage(img);
        this.mainWindow.setMinimumSize(new Dimension(100, 100));
        this.mainWindow.setBackground(Color.white);
        setFullscreen(p("app.fullscreen").equalsIgnoreCase("true"));
    }
    
    /**
     * Sets wether this app is fullscreen or not
     * @param fullscreen 
     */
    public void setFullscreen(boolean fullscreen){
        this.fullscreen = fullscreen;
        if(this.fullscreen){
            mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH); 
            //mainWindow.setUndecorated(true);
        }else{
            mainWindow.setExtendedState(JFrame.NORMAL); 
            mainWindow.setUndecorated(false);
        }
    }
    
    /**
     * This is the first line in the main method of a japp application
     * 
     * @param appName the application name
     * @param args command line arguments
     */
    public static void init(String appName,String[] args){
        load(appName, args);
        try {
            String lf=p("app.lookandfeel");
            if(lf==null || lf.trim().equals("")||lf.equals("default")){
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }else{
                UIManager.setLookAndFeel(lf);
            }
        } catch (Throwable e) {
            Log.debug("Error al colocar el look and feel", e);
        }
        
        APP = new DesktopApp();
        
    }
    
    /**
     * Starts a desktop app, remember init() app befor start
     * @param mainPanel 
     */
    public static void start(JComponent mainPanel){
        APP.setMainPanel(mainPanel);
        APP.mainWindow.pack();

        SwingUtilities.invokeLater(()->{
            APP.mainWindow.setVisible(true);
        });

    }

    /**
     * Get the main window
     * @return 
     */
    public JFrame getMainWindow() {
        return mainWindow;
    }

    /**
     * Get the main panel
     * @return 
     */
    public Component getMainPanel() {
        return mainPanel;
    }

    /**
     * Set the main panel of this DesktopApp
     * @param mainPanel 
     */
    public void setMainPanel(JComponent mainPanel) {
        this.mainPanel = mainPanel;
        this.mainWindow.setContentPane(mainPanel);
    }

    /**
     * Get the application name
     * @return 
     */
    public String getAppName() {
        return appName;
    }
    
    
}
