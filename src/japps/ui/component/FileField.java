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

import japps.ui.component.action.TransferActionListener;
import japps.ui.util.Resources;
import java.io.File;
import static japps.ui.util.Resources.*;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Williams Lopez - JApps
 */
public class FileField extends ComponentField<File>{

    private List<File> files;
    private TextField textField;
    private static JFileChooser fileChooser = new JFileChooser();
    private Button button; 
    private Button btnDelete;
    
    
    private int mode;
    private int type;
    
    public static final int MODE_FILE_SINGLE = 1;
    public static final int MODE_FILE_MULTIPLE = 2;
    public static final int MODE_DIRECTORY_SINGLE = 3;
    public static final int MODE_DIRECTORY_MULTIPLE = 4;
    public static final int MODE_FILES_AND_DIRS = 5;
    
    public static final int TYPE_OPEN = 1;
    public static final int TYPE_SAVE = 2;
    

    public FileField(int mode, int type) {
        super();
        files = new ArrayList<>();
        BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
        this.setLayout(layout);
        textField = createTextField(mode);
        button = new Button();
        button.addActionListener((e) -> { showFileChooser(getFiles(), FileField.this); });
        button.setImage(Resources.icon("folder-open.png"),15,15);
        button.setSize(20, 20);
        
        btnDelete = new Button();
        btnDelete.setImage(Resources.icon("delete.png"),15,15);
        btnDelete.setSize(20, 20);
        btnDelete.addActionListener((e)->{ setValue(null); });
        
        textField.setBorder(null);
        button.setBorder(null);
        
        this.add(btnDelete);
        this.add(textField);
        this.add(button);
        
        setType(type);
        
        TransferActionListener transferableListener = new TransferActionListener(this);

        this.textField.addFocusListener(transferableListener);
        this.textField.addKeyListener(transferableListener);
        this.textField.addMouseListener(transferableListener);
        this.textField.addMouseMotionListener(transferableListener);
        this.textField.addMouseWheelListener(transferableListener);
        
        
    }
    
    /**
     * Set file extensions
     * @param extensions 
     */
    public void setFileExtensions(String... extensions){

        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if(f.isDirectory()){
                    return true;
                }
                for(String ext: extensions){
                    
                    if(ext == null  || f == null || f.getName() == null){
                        continue;
                    }
                    
                    if(ext.equals("*")){
                        return true;
                    }
                    
                    if(f.getName().toLowerCase().endsWith("."+ext.toLowerCase())){
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String getDescription() {
                return Arrays.toString(extensions);
            }
        });
    }
    

    private static void showFileChooser(List<File> files, FileField field){

        File selectedFile = (files==null || files.isEmpty()) ? null : files.get(files.size()-1);
        File currentDirectory = (selectedFile==null) ? null : selectedFile.getParentFile();
        File[] filesArray = (files !=null) ? files.toArray(new File[files.size()]): new File[0];
        
        if(field.type == FileField.TYPE_SAVE){
            fileChooser.setDialogType(FileField.TYPE_SAVE);
        }else{
            fileChooser.setDialogType(FileField.TYPE_OPEN);
        }
        
        switch(field.mode){
            case MODE_FILE_SINGLE:
                fileChooser.setApproveButtonText($("Select file"));
                fileChooser.setCurrentDirectory(currentDirectory);
                fileChooser.setDialogTitle($("Please select a file"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setSelectedFile(selectedFile);
                break;
            case MODE_FILE_MULTIPLE:
                fileChooser.setApproveButtonText($("Select files"));
                fileChooser.setCurrentDirectory(currentDirectory);
                fileChooser.setDialogTitle($("Please select the files"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setSelectedFiles(filesArray);
                break;
            case MODE_DIRECTORY_SINGLE:
                fileChooser.setApproveButtonText($("Select directory"));
                fileChooser.setCurrentDirectory(currentDirectory);
                fileChooser.setDialogTitle($("Please select a directory"));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setSelectedFile(selectedFile);
                break;
            case MODE_DIRECTORY_MULTIPLE:
                fileChooser.setApproveButtonText($("Select directories"));
                fileChooser.setCurrentDirectory(currentDirectory);
                fileChooser.setDialogTitle($("Please select the directories"));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setSelectedFiles(filesArray);
                break;
            case MODE_FILES_AND_DIRS:
                fileChooser.setApproveButtonText($("Select files or directories"));
                fileChooser.setCurrentDirectory(currentDirectory);
                fileChooser.setDialogTitle($("Please select the files or directories"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setSelectedFiles(filesArray);
                break;
                
        }
        
        
        
        int result = -1;
        
        result = fileChooser.showDialog(field, fileChooser.getApproveButtonText());
        
        if(result == JFileChooser.APPROVE_OPTION){
            
            switch(field.getMode()){
                case MODE_DIRECTORY_SINGLE: case MODE_FILE_SINGLE:
                    field.setFiles(Arrays.asList(new File[]{ fileChooser.getSelectedFile() }));
                    break;
                case MODE_DIRECTORY_MULTIPLE: case MODE_FILES_AND_DIRS: case MODE_FILE_MULTIPLE:
                    field.setFiles(Arrays.asList(fileChooser.getSelectedFiles()));
                    break;
            }
            
            
            
            
        }
        
    }
    
    private void refreshTextField(){
        this.textField.setValue("");
        this.files.forEach((f)->{ this.textField.append(f.getName()+";");  });
    }

    /**
     * Get the mode
     * 
     * FileField.MODE_FILE_SINGLE = 1;
     * FileField.MODE_FILE_MULTIPLE = 2;
     * FileField.MODE_DIRECTORY_SINGLE = 3;
     * FileField.MODE_DIRECTORY_MULTIPLE = 4;
     * FileField.MODE_FILES_AND_DIRS = 5;
     * 
     * @return 
     */
    public int getMode(){
        return this.mode;
    }
    
    /**
     * Set the mode
     * FileField.MODE_FILE_SINGLE = 1;
     * FileField.MODE_FILE_MULTIPLE = 2;
     * FileField.MODE_DIRECTORY_SINGLE = 3;
     * FileField.MODE_DIRECTORY_MULTIPLE = 4;
     * FileField.MODE_FILES_AND_DIRS = 5;
     * @param mode 
     */
    private TextField createTextField(int mode){
        this.mode = mode;
        
        switch(mode){
            case MODE_FILE_SINGLE: 
                this.textField = new TextField();
                this.setMaximumSize(new Dimension(100000, 35));
                break;
            case MODE_FILE_MULTIPLE: 
                this.textField = new TextField(true);
                this.textField.setLineWrap(true);
                this.setMaximumSize(new Dimension(100000, 100000));
                break;
            case MODE_DIRECTORY_SINGLE: 
                this.textField = new TextField();
                this.setMaximumSize(new Dimension(100000, 35));
                break;
            case MODE_DIRECTORY_MULTIPLE: 
                this.textField = new TextField(true);
                this.textField.setLineWrap(true);
                this.setMaximumSize(new Dimension(100000, 100000));
                break;
            case MODE_FILES_AND_DIRS: 
                this.textField = new TextField(true);
                this.textField.setLineWrap(true);
                this.setMaximumSize(new Dimension(100000, 100000));
                break;
            default:
                throw new RuntimeException("Unsuported mode "+mode);
        }
        this.textField.setEditable(false);
        
        return this.textField;
    }

    /**
     * Get the type
     * FileField.TYPE_OPEN
     * FileField.TYPE_SAVE
     * @return 
     */
    public int getType() {
        return type;
    }

    /**
     * Set the type
     * FileField.TYPE_OPEN
     * FileField.TYPE_SAVE
     * @param type 
     */
    public void setType(int type) {
        this.type = type;
    }
    
    
    /**
     * Get the selected files in this component
     * @return 
     */
    public List<File> getFiles(){
        return this.files;
    }
    
    /**
     * Set the selected files in this component
     * @param files 
     */
    public void setFiles(List<File> files){
        this.files.clear();
        this.files.addAll(files);
        this.refreshTextField();
    }
    
    /**
     * Set the selected file in this file component
     * @param value 
     */
    @Override
    public void setValue(File value) {
        
        files.clear();
        if(value != null){
            files.add(value);
        }
        this.refreshTextField();
    }
    
    

    @Override
    public File getValue() {
        return (files.isEmpty() ? null : files.get(files.size()-1));
    }

    @Override
    public void setToolTipText(String text) {
        this.textField.setToolTipText(text); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getToolTipText() {
        return this.textField.getToolTipText(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setEditable(boolean editable) {
        this.textField.setEditable(editable);
    }

    @Override
    public boolean isEditable() {
        return this.textField.isEditable();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.textField.setEnabled(enabled);
        this.button.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return this.button.isEnabled();
    }
    
    
    
    
}
