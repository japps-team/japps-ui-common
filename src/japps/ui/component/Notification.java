/*
 * Copyright (C) 2023 admin
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
import japps.ui.component.action.AbstractMouseListener;
import japps.ui.util.Resources;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.Timer;


/**
 *
 * @author admin
 */
public class Notification{
    
    public static final int ERROR=1;
    public static final int SUCCESS=2;
    public static final int WARNING=3;
    public static final int INFORMATION=4;
    
    
    protected List<NotificationItem> notificationQueue;
    protected List<NotificationItem> notificationPool;
    protected static Notification me;
    private int maxDisplayedNotifications = 10;
    private static int defaultTimeToShow = 15000;
    
    private Notification(){
        this.notificationQueue = new ArrayList<>();
        this.notificationPool = new ArrayList<>();
    }
    
    private static Notification getInstance(){
        if(me==null){
            me=new Notification();
        }
        return me;
    }
    
    public static void setDefaultTimeToShow(int timeInMillis){
        defaultTimeToShow = timeInMillis;
    }

    public int getMaxDisplayedNotifications() {
        return maxDisplayedNotifications;
    }
    
    public void setMaxDisplayedNotifications(int max) {
        this.maxDisplayedNotifications = max;
    }

    /**
     * Display an error message
     * @param message 
     */
    public static void error(String message){
        getInstance().showMessage(ERROR, message,defaultTimeToShow);
    }
    
    /**
     * Display a waring message
     * @param message 
     */
    public static void warning(String message){
        getInstance().showMessage(WARNING, message,defaultTimeToShow);
    }
    
    /**
     * Display a successs message
     * @param message 
     */
    public static void success(String message){
        getInstance().showMessage(SUCCESS, message,defaultTimeToShow);
    }
    
    /**
     * Show a notification in this app
     * 
     * @param type type of notification ERROR,SUCCESS,WARNING, INFORMATION
     * @param message message to display
     * @param time time to show this message, 0 to NO time
     */
    public static synchronized void show(int type, String message, int time){
        getInstance().showMessage(type, message, time);
    }
    
    private void showMessage(int type, String message, int time) {
        NotificationItem item = getFreeForUseNotification();
        notificationQueue.add(item);
        item.setMessage(type, message);
        item.showNotification(time);
    }
    
    
    private void orderAndDisplayMessages(){
        JFrame mainWindow = DesktopApp.APP.getMainWindow();
        Point l = mainWindow.getLocation();
        Dimension d = mainWindow.getSize();
        
        double x = l.getX()+d.getWidth();
        double y = l.getY();
        double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        
        if(x < 0) x = 10;
        if(y < 0) y = 10;
        
        //First remove from queue all no displaying notifications
        for(int i=0;i<notificationQueue.size();i++){
            NotificationItem ni = notificationQueue.get(i);
            if(!ni.isDisplaying()){
                notificationQueue.remove(ni);
                if(ni.isVisible()){
                    ni.setVisible(false);
                }
            }
        }
        
        for(NotificationItem ni : notificationQueue){
            
            double itemX = x-ni.getWidth();
            double itemY = y+35;
            
            if((x+ni.getWidth()) > screenWidth){
                itemX = screenWidth-ni.getWidth();
            }
            
            if((y+ni.getHeight()) > screenWidth){
                itemY = screenHeight-ni.getHeight();
            }
            
            ni.setLocation((int)itemX-10,(int)itemY);
            y = y + ni.getSize().getHeight();
            if(!ni.isVisible()){
                ni.setVisible(true);
            }
        }
    }
    
    
    private NotificationItem getFreeForUseNotification(){
        NotificationItem notification = null;
        
        //Verify if there is a free NOtificationItem
        for(NotificationItem n : notificationPool){
            if(!n.isDisplaying()){
                return n;
            }
        }
        
        //If NOtificationItem pool is full then return oldest
        if(notificationPool.size() >= maxDisplayedNotifications){
            notification = findOldestNotificationItem();
            notification.hideNotification();
            return notification;
        }
        
        //Otherwise create and add to the pool
        notification = new NotificationItem(this);
        notificationPool.add(notification);
        notificationQueue.remove(notification); //In case this notification was already queued
        return notification;
    }
    
    private NotificationItem findOldestNotificationItem(){
        NotificationItem oldest = null;
        for(NotificationItem n: notificationPool){
            if(oldest == null || n.lastShowingStartTime < oldest.lastShowingStartTime){
                oldest = n;
            }
        }
        return oldest;
    }
    
    public static class NotificationItem extends JDialog{
        
        private final Panel panel;
        private final Label labelIcon;
        private final JTextArea labelText;
        private long lastShowingStartTime;
        private final Timer timer;
        private boolean displaying = false;
        private final Notification parent;
        private Font font;
        
        private NotificationItem(Notification parent){
            this.parent = parent;
            timer = new Timer(5000, (e)->{
                hideNotification();
            });
            timer.setRepeats(false);
            panel= new Panel();
            panel.addMouseListener(new AbstractMouseListener(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    hideNotification();
                }
            });
            panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            labelIcon = new Label();
            labelText = new JTextArea();
            Component[][] comps = { {labelIcon,labelText} };
            panel.setComponents(comps,
                    new String[]{ Panel.FILL_GROW_CENTER },
                    new String[]{ Panel.FILL_GROW_CENTER+",30:30:30",Panel.FILL_GROW_CENTER});
            this.setContentPane(panel);
            this.setSize(400, 100);
            this.setModalityType(Dialog.ModalityType.MODELESS);
            this.setUndecorated(true);
            this.setAlwaysOnTop(true);
            this.setBackground(Color.white);
            this.setOpacity(0.85f);
            this.labelText.setLineWrap(true);
            this.labelText.setEditable(false);
            this.font = Resources.getDefaultFont();
        }
        
        public void setMessage(int type, String message){
            labelText.setFont(font);
            labelText.setText(message); 
            Image icon = Resources.icon("chat.png", 25, 25);
            switch (type) {
                case ERROR:   icon = Resources.icon("error.png", 25, 25); break;
                case WARNING: icon = Resources.icon("warning.png", 25, 25); break;
                case SUCCESS: icon = Resources.icon("done.png", 25, 25); break;
            }
            labelIcon.setIcon(new ImageIcon(icon));
        }
        
        /**
         * Show this notification
         * @param time time in milliseconds
         */
        public void showNotification(int time){
            this.displaying = true;
            this.lastShowingStartTime = Calendar.getInstance().getTimeInMillis();
            if(time > 0){
                timer.setInitialDelay(time);
                timer.setDelay(time);
                timer.start();
            }
            this.parent.orderAndDisplayMessages();
        }
        
        /**
         * Hide this notification
         */
        public void hideNotification(){
            this.displaying = false;
            this.lastShowingStartTime = 0;
            this.timer.stop();
            this.parent.orderAndDisplayMessages();
        }


        public boolean isDisplaying() {
            return displaying;
        }

        @Override
        public Font getFont() {
            return font;
        }

        @Override
        public void setFont(Font font) {
            this.font = font;
            super.setFont(font);
        }
        
        
        
        
        
    }
    
//    
//    public static void main(String[] args) {
//            DesktopApp.init("test", args);
//            Panel p = new Panel();
//            p.setSize(new Dimension(1000,800));
//            p.setBounds(100, 100, 1000, 800);
//            DesktopApp.APP.getMainWindow().setSize(1000, 800);
//            
//            Timer timer = new Timer(2000,(e)->{
//                Notification.show(ERROR,"Mensaje de prueba success 1",15000);
//                Notification.show(ERROR,"Mensaje de prueba success 2",14000);
//                Notification.show(ERROR,"Mensaje de prueba success 3",13000);
//                Notification.show(ERROR,"Mensaje de prueba success 4",12000);
//                Notification.show(ERROR,"Mensaje de prueba success 5",110000);
//                Notification.show(ERROR,"Mensaje de prueba success 6",10000);
//                Notification.show(ERROR,"Mensaje de prueba success 7",9000);
//                Notification.show(ERROR,"Mensaje de prueba success 8",8000);
//                Notification.show(ERROR,"Mensaje de prueba success 9",7000);
//                Notification.show(ERROR,"Mensaje de prueba success 10",6000);
//                Notification.show(ERROR,"Mensaje de prueba success 11",5000);
//                Notification.show(ERROR,"Mensaje de prueba success 12",4000);
//                Notification.show(ERROR,"Mensaje de prueba success 13",3000);
//                Notification.show(ERROR,"Mensaje de prueba success 14",2000);
//                Notification.show(ERROR,"Mensaje de prueba success 15",1000);
//            });
//            timer.setRepeats(false);
//            timer.start();
//            
//            DesktopApp.start(p);
//            
//        }
    
    
}
