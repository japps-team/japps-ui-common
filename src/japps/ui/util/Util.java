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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
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
    public static SimpleDateFormat localeDateFormat;
    public static DateTimeFormatter localDateTimeFormatter;
    public static DateTimeFormatter localDateFormatter;
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
        if(localeDateFormat == null){
             localeDateFormat = new SimpleDateFormat(Resources.p("app.datetime.format"), Locale.getDefault());
        }
        return localeDateFormat.format(date);
    }
    
    /**
     * Converts the date object to string in a format for user
     * @param date
     * @return 
     */
    public static String dateToLocaleString(LocalDateTime date){
        if(localDateTimeFormatter == null){
            localDateTimeFormatter =  java.time.format.DateTimeFormatter.ofPattern(Resources.p("app.datetime.format"));
        }
        return localDateTimeFormatter.format(date);
    }
    
    /**
     * Converts the date object to string in a format for user
     * @param date
     * @return 
     */
    public static String dateToLocaleString(LocalDate date){
        if(localDateFormatter == null){
            localDateFormatter =  java.time.format.DateTimeFormatter.ofPattern(Resources.p("app.date.format"));
        }
        return localDateFormatter.format(date);
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
        int c;
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
        } catch (IOException e) {
            Log.error("Error reading image "+path,e);
        }
        return null;
    }
    
    /**
     * Creates a unique hash id based on data and time
     * @param data
     * @return 
     */
    public static String createUniqueHashId(String data){
        String input = Calendar.getInstance().getTimeInMillis()+data; 

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input.getBytes());
            byte[] hashBytes = md.digest();
            byte[] truncatedHashBytes = new byte[16];
            System.arraycopy(hashBytes, 0, truncatedHashBytes, 0, truncatedHashBytes.length);
            StringBuilder hashString = new StringBuilder();
            for (byte b : truncatedHashBytes) {
                hashString.append(String.format("%02x", b));
            }
            return hashString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
        
    
}
