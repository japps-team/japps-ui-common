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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Williams Lopez - JApps
 */
public class Log {
    
    private static SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Write relevant info always
     * @param message 
     */
    public static void info(String message){
        printtoLog("ERROR", message, null);
    }
    
    /**
     * Write always error messages
     * @param message
     * @param err 
     */
    public static void error(String message, Throwable err){
        printtoLog("ERROR", message, err);
    }
    
    /**
     * Write only if debug is enabled
     * @param message
     * @param err 
     */
    private static void debug(String message, Throwable err){
        //@TODO IMPLEMENTAR DEBUG, NO USAR CLASE LOGGER
        boolean debug = true;
        
        try {
           String v = Resources.p("app.debug");
           debug = v==null || Boolean.parseBoolean(v);
        } catch (Throwable e) {
            debug = true;
        }
        
        
        if(debug){
            printtoLog("DEBUG", message, err);
        }
    }
    
    /**
     * Write to log only if debug is enabled
     * @param message 
     */
    public static void debug(String message){
        debug(message,null);
    }
    
    private static void printtoLog(String level, String message, Throwable err){
        
        StringBuilder sb = new StringBuilder();
        sb.append(formatDate.format(new Date()));
        sb.append(" - ");
        sb.append("[").append(level).append("] ");
        sb.append("[Thread: ").append(Thread.currentThread().getId()).append("] ");
        sb.append(message);
        
        if(err!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            err.printStackTrace(new PrintStream(baos));
            sb.append("\n").append(baos.toString());
            try {
                baos.close();
            } catch (Exception e) {}
        }
        
        if(err != null){
            System.err.println(sb.toString());
        }else{
            System.out.println(sb.toString());
        }
    }
    
}
