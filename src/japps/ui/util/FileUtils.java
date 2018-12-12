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
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Williams Lopez - JApps
 */
public class FileUtils {
    
    /**
     * Copy a file or directory into a pathTo directory, if pathTo does not exists, it will be created
     * @param from File or directory to be copied
     * @param pathTo Path to put the file or directory
     * @throws IOException 
     */
    public static void copy(Path from, Path pathTo) throws IOException{
        
        Path target = pathTo.toAbsolutePath().resolve(from.getFileName());
        if(Files.isDirectory(from)){
            Files.createDirectories(target);
            Files.list(from).forEach( (p)-> { 
                try{
                    copy(p,target); 
                }catch(IOException err){
                    Log.debug("Error copying file: "+p.toAbsolutePath()+" to "+target, err);
                }
            }  );
        }else{
            Log.debug("Copying file: "+from.toString());
            Files.copy(from, target);
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
        }catch(Exception err){
            Log.debug("Error al eliminar archivos",err);
        }
    }
    
    /**
     * List all files in a package directory
     * @param pack
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
