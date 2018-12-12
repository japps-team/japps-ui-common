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

/**
 *
 * @author Williams Lopez - JApps
 */
public class Log {
    
    public static void debug(String message, Throwable err){
        //@TODO IMPLEMENTAR DEBUG, NO USAR CLASE LOGGER
        boolean debug = true;
        
        try {
           debug = Resources.p("app.debug")==null || Resources.p("app.debug").equals("true"); 
        } catch (Throwable e) {}
        
        
        if(debug){
            System.out.println(message);
            if(err!=null){
                err.printStackTrace();
            }
        }
    }
    
    public static void debug(String message){
        debug(message,null);
    }
    
}
