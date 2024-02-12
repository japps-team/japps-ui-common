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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Williams Lopez - JApps
 */
public class FileUtils {
    
    /**
     * Copy a file or directory to the specified pathTo, if pathTo does not exists, it will be created
     * @param from to or directory to be copied
     * @param to
     * @throws IOException 
     */
    public static void copy(Path from, Path to) throws IOException{
        Log.debug("Copying "+from+" to "+to);
        if(Files.isDirectory(from)){
            Files.createDirectories(to);
            Files.list(from).forEach( (p)-> { 
                try{
                    String name = p.toFile().getName();
                    if(!name.equals(".") && 
                       !name.equals("..")){
                        copy(p,to.resolve(name)); 
                    }
                }catch(IOException err){
                    Log.error("Error copying file: "+p.toAbsolutePath()+" to "+to, err);
                }
            }  );
        }else{
            Log.debug("Copying file: "+from.toString());
            Files.copy(from, to,StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
  
    

    
    /**
     * Deletes a file or a directory recursively
     * @param path 
     */
    public static void deleteFileOrDirectory(Path path){
        try{
            
            if(!Files.exists(path)){
                return;
            }
            
            if(Files.isDirectory(path)){
                Files.list(path).forEach((p)->{ deleteFileOrDirectory(p); });
                Files.delete(path);
            }else{
                Files.delete(path);
            }
        }catch(IOException err){
            Log.error("Error al eliminar archivos",err);
        }
    }
    
    /**
     * List all files in a package directory
     * @param path
     * @return
     * @throws IOException 
     */
    public static List<String> listFilesInPath(Path path) throws Exception{
        List<String> files = Files.walk(path)
                                 .map(Path::getFileName)
                                 .map(Path::toString)
                                 .collect(Collectors.toList());
        return files;
    }
    
    
    
}
