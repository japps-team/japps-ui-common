/*
 * Copyright (C) 2019 Williams Lopez - JApps
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
package japps.ui.config;

import japps.ui.component.ComponentField;
import japps.ui.component.FileField;
import java.awt.Dimension;
import java.io.File;
import java.nio.file.Path;
import javax.swing.JComponent;

/**
 *
 * @author Williams Lopez - JApps
 */
public class FileComponentBuilder implements ComponentBuilder{
    

    @Override
    public void addValidator(JComponent comp, ComponentField.Validatior validator) {
        ((FileField)comp).addValidator(validator);
    }

    @Override
    public void setValue(JComponent comp, Object object) {
        if(object !=null){
            ((FileField)comp).setValue((((Path)object).toFile()));
        }
    }

    @Override
    public Object getValue(JComponent comp) {
        File f = ((FileField)comp).getValue(); 
        return f==null?null:f.toPath();
    }

    @Override
    public JComponent createNewComponent(PInfo pinfo) {
        FileField ff = null;
        if (pinfo.defaultValue.startsWith("open")) {
            ff = new FileField(FileField.MODE_FILE_SINGLE, FileField.TYPE_OPEN);
            /*ff.addValidator((c) -> {
                File f = (File) ((ComponentField)c).getValue();
                if (f == null) {
                    throw new RuntimeException("File doesn't exist");
                }
                return f.exists();
            });*/
        } else {
            ff = new FileField(FileField.MODE_FILE_SINGLE, FileField.TYPE_SAVE);
        }
        
        int pos = pinfo.defaultValue.indexOf("-")+1;
        String ext = pinfo.defaultValue.substring(pos);    
        ff.setFileExtensions(ext.split("[,]"));
        
        ff.setMinimumSize(minFieldDimension);
        ff.setMaximumSize(maxFieldDimension);
        
        return ff;
    }
    
    @Override
    public Object getValue(PObject pobject, String property) {
        return pobject.getPath(property);
    }
    
    
    @Override
    public boolean isValidValue(JComponent comp) {
        return ((ComponentField)comp).validateField();
    }
    
}
