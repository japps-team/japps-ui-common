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

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *
 * @author Williams Lopez - JApps
 */
public class Util {
    /**
     * Date format for show to users
     */
    public static SimpleDateFormat localeDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aa", Locale.getDefault());
    /**
     * Date format for application internal
     */
    public static SimpleDateFormat internalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    
    /**
     * Converts the date object to string in a format for user
     * @param date
     * @return 
     */
    public static String dateToLocaleString(Date date){
        return localeDateFormat.format(date);
    }
    
    /**
     * Converts the date object to string in standar format
     * @param date
     * @return 
     */
    public static String dateToInternalString(Date date){
        return internalDateFormat.format(date);
    }
    
    /**
     * Execute process
     * @param workingDirectory
     * @param command
     * @return
     * @throws Exception 
     */
    public static String execute(Path workingDirectory, String... command) throws Exception{
        
        Log.debug("Executing command: "+Arrays.toString(command));
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workingDirectory.toFile());
        Process process= pb.start();
        process.waitFor();
        InputStream is = process.getInputStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int c = -1;
        while ((c = is.read()) > 0) {
            os.write(c);
        }
        os.write('\n');
        is = process.getErrorStream();
        while ((c = is.read()) > 0) {
            os.write(c);
        }
        return os.toString();
    
    }
    
    /**
     * Get the parent JFrame of a component
     * @param comp
     * @return 
     */
    public static JFrame getParentWindow(java.awt.Component comp){
        java.awt.Component parent = comp.getParent();
        do{
            if(parent instanceof JFrame){
                return (JFrame)parent;
            }
        }while((parent=parent.getParent())!=null);
        return null;
    }
    
    /**
     * Read an image from a string path
     * @param path
     * @return 
     */
    public static Image readImage(String path){
        return readImage(Paths.get(path));
    }
    
    /**
     * Reads an image from a path
     * @param path
     * @return 
     */
    public static Image readImage(Path path){
        try {
            return ImageIO.read(path.toUri().toURL());
        } catch (Exception e) {
            Log.debug("Error reading image "+path,e);
        }
        return null;
    }
        
    
}
