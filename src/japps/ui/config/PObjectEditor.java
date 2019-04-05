/*
 * Copyright (C) 2019 Williams Lopez - JApps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package japps.ui.config;

import japps.ui.DesktopApp;
import japps.ui.component.Button;
import japps.ui.component.ComboBox;
import japps.ui.component.ComponentField;
import japps.ui.component.DateField;
import japps.ui.component.Dialogs;
import japps.ui.component.FileField;
import japps.ui.component.Label;
import japps.ui.component.OrderElementsField;
import japps.ui.component.Panel;
import japps.ui.component.Popup;
import japps.ui.component.RoundedBorder;
import japps.ui.component.TextField;
import japps.ui.component.ToggleButton;
import japps.ui.util.Log;
import japps.ui.util.Resources;
import java.awt.Component;
import static japps.ui.util.Resources.p;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 *
 * @author Williams Lopez - JApps
 */
public class PObjectEditor extends Panel {

    private JDialog mainDialog;
    private PObjectEditor childProperties;
    private PObject pobject;
    private String idConfig;
    List<PInfo> pinfoList;
    Map<String, ComponentBuilder> componentBuilder;
    
    Button btnConfirm;
    Button btnCancel;
    Button btnConfirmAndClose;
    JScrollPane scrollPane;

    public PObjectEditor() {
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        this.componentBuilder = new HashMap<>();
        
        btnConfirmAndClose = new Button();
        btnConfirmAndClose.setText(Resources.$("Save and Close"));
        btnConfirmAndClose.addActionListener((e)->{ if(save()) mainDialog.setVisible(false);  });
        btnConfirmAndClose.setHorizontalAlignment(SwingConstants.CENTER);
        btnConfirmAndClose.setHorizontalTextPosition(SwingConstants.CENTER);
        btnConfirmAndClose.setBorder(new RoundedBorder(btnConfirm));
        
        btnConfirm = new Button();
        btnConfirm.setText(Resources.$("Save"));
        btnConfirm.addActionListener((e)->{ save();  });
        btnConfirm.setHorizontalAlignment(SwingConstants.CENTER);
        btnConfirm.setHorizontalTextPosition(SwingConstants.CENTER);
        btnConfirm.setBorder(new RoundedBorder(btnConfirm));
        
        btnCancel = new Button();
        btnCancel.setText(Resources.$("Cancel"));
        btnCancel.addActionListener((e)->{  mainDialog.setVisible(false);  });
        btnCancel.setHorizontalAlignment(SwingConstants.CENTER);
        btnCancel.setHorizontalTextPosition(SwingConstants.CENTER);
        btnCancel.setBorder(new RoundedBorder(btnConfirm));
        
        
        
        
        scrollPane = new JScrollPane();
        
        setComponents(new Component[][]{
            {scrollPane,scrollPane,scrollPane,scrollPane},   
            {null,btnConfirmAndClose,btnConfirm,btnCancel}
        }, 
        new String[]{Panel.FILL_GROW_CENTER,"150:150:150,FILL,"+Panel.RIGHT,"150:150:150,FILL,"+Panel.RIGHT,"150:150:150,FILL,"+Panel.LEFT}, 
        new String[]{Panel.FILL_GROW_CENTER,"30:30:30,"+Panel.FILL_GROW_CENTER});
        
        
        addDefaultBuilders();
        
    }
    
    private void buildUI() {
        
        Panel content = new Panel();
        
        this.pinfoList = new ArrayList<>();

        String ps = p(idConfig + ".properties");
        if (ps == null || ps.trim().isEmpty()) {
            return;
        }

        String[] properties = ps.split("[,]");

        Component[][] comps = new Component[properties.length][2];
        String[] colConstraint = {"200:200:200," + Panel.FILL_GROW_CENTER, Panel.FILL_GROW_CENTER};
        String[] rowConstraint = new String[properties.length];

        int index = 0;
        for (String p : properties) {

            PInfo pinfo = PInfo.parse(p, p(idConfig + "." + p));
            pinfo.pobject = pobject;
            pinfo.editor = this;

            rowConstraint[index] = Panel.FILL_GROW_CENTER;
            /*
            if (pinfo.type.equals(PInfo.TYPE_STRING_LONG)) {
                rowConstraint[index] = "75:75:75," + Panel.FILL_GROW_CENTER;
            }else if (pinfo.type.equals(PInfo.TYPE_PLIST)) {
                rowConstraint[index] = Panel.FILL_GROW_CENTER;
            } else {
                rowConstraint[index] = "35:35:35," + Panel.FILL_GROW_CENTER;
            }*/

            Label label = new Label();
            buildEditor(pinfo);

            if(pinfo.required){
                label.setText(pinfo.label + " (*) ");
            }else{
                label.setText(pinfo.label);
            }
            
            pinfo.comp.setToolTipText(pinfo.tooltipText);

            comps[index][0] = label;
            comps[index][1] = pinfo.comp;
            
            pinfoList.add(pinfo);
            
            index++;
        }

        content.setComponents(comps, colConstraint, rowConstraint);
        
        scrollPane.setViewportView(content);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        scrollPane.updateUI();

    }

    private void buildEditor(PInfo pinfo) {
        
        
        pinfo.builder = this.componentBuilder.get(pinfo.type);
        
        if(pinfo.builder == null){
            pinfo.builder = this.componentBuilder.get(PInfo.TYPE_STRING);
        }
        
        pinfo.comp = pinfo.builder.createNewComponent(pinfo);
        
        Object value = pinfo.builder.getValue(pinfo.pobject, pinfo.property);
        
        if(value!=null){
            pinfo.builder.setValue(pinfo.comp, value);
        }
        
        final ComponentBuilder cb = pinfo.builder;
        pinfo.builder.addValidator(pinfo.comp, (c)->{
            Object v = cb.getValue(c);
            if((v==null || v.toString().equals("")) && pinfo.required){
                throw new RuntimeException(pinfo.label+ " " + Resources.$("can not be empty"));
            }
            return true;
        });
    }
    
    private void addDefaultBuilders(){
        addBuilder(PInfo.TYPE_STRING, new TextComponentBuilder());
        addBuilder(PInfo.TYPE_STRING_LONG, this.componentBuilder.get(PInfo.TYPE_STRING));
        addBuilder(PInfo.TYPE_BOOLEAN, new BooleanComponentBuilder());
        addBuilder(PInfo.TYPE_DATE, new DateComponentBuilder());
        addBuilder(PInfo.TYPE_FILE, new FileComponentBuilder());
        addBuilder(PInfo.TYPE_NUMBER, new NumberComponentBuilder());
        addBuilder(PInfo.TYPE_INTEGER, this.componentBuilder.get(PInfo.TYPE_NUMBER));
        addBuilder(PInfo.TYPE_DECIMAL, this.componentBuilder.get(PInfo.TYPE_NUMBER));
        addBuilder(PInfo.TYPE_OPTION, new OptionComponentBuilder());
        addBuilder(PInfo.TYPE_PLIST, new PListComponentBuilder());
    }
    
    /**
     * Launch a PObjectEditor with a child PObject
     * @param obj
     * @param idConfig 
     */
    public void launchChildProperties(PObject obj,String idConfig){
        if(childProperties == null){
            final PObjectEditor parent = this;
            childProperties = new PObjectEditor();
            childProperties.addSaveActionListener((e)->{
                parent.fireSaveActionListener(e);
            });
        }
        childProperties.setPObject(obj, idConfig);
        childProperties.show();
    }
    
    
    /**
     * Save the properties values into Map object
     * @return 
     */
    private boolean save(){
        
        String message = "";
        
        if (pinfoList == null) {
            return false;
        }
        try {
            for (PInfo p : pinfoList) {
                message = p.property;
                p.saveValue();
            }
            fireSaveActionListener(new ActionEvent(this, 0, "save"));
           return true;
        } catch (Throwable err) {
            
            if (err instanceof NullPointerException) {
                message = Resources.$("Please validete your inputs");
            }else{
                message += ": "+ err.getMessage();
            }
            Log.debug(message, err);
        }
        Dialogs.error(Resources.$("Input error"), Resources.$("Input validation error for:")+"\n"+message);
        return false;
    }

    /**
     * Adds a component builder
     * @param propertyOrType the type or property key where this builder is goning to be applied
     * @param builder 
     */
    public void addBuilder(String propertyOrType, ComponentBuilder builder){
        this.componentBuilder.put(propertyOrType, builder);
    }
    
    @Override
    public void show(){
        if(mainDialog == null){
            mainDialog = Dialogs.create(this, pobject.toString(), true, null);
            mainDialog.setLocationByPlatform(false);
            //mainDialog.setMinimumSize(new Dimension(800, 600));
        }
        
        mainDialog.pack();
        mainDialog.setVisible(true);
    }
    
    /**
     * Return the pobject in this PObjectEditor
     */
    public PObject getPObject(){
        return this.pobject;
    }

    /**
     * Set a PObject to edit
     * @param pobject
     * @param idConfig 
     */
    public void setPObject(PObject pobject, String idConfig) {
        this.pobject = pobject;
        this.idConfig = idConfig;
        buildUI();
    }
    
    /**
     * Returns the id used to find configuration in properties files
     * @return 
     */
    public String getIdConfig(){
        return this.idConfig;
    }

    
    /**
     * Adds a ne SaveActionListener to this component
     * @param l 
     * @see SaveActionListener
     */
    public void addSaveActionListener(SaveActionListener l){
        listenerList.add(SaveActionListener.class, l);
    }
    
    /**
     * Removes a SaveACtionListener from this component
     * @param l 
     * @see SaveActionListener
     */
    public void removeSaveActionListener(SaveActionListener l){
        listenerList.remove(SaveActionListener.class, l);
    }
    
    /**
     * Get all SaveActionListener
     * @return 
     * @see SaveActionListener
     */
    public SaveActionListener[] getSaveActionListener(){
        return listenerList.getListeners(SaveActionListener.class);
    }
    
    /**
     * Fire all SaveActionListener on this component
     * @param e 
     * @see SaveActionListener
     */
    public void fireSaveActionListener(ActionEvent e){
        SaveActionListener[] list = getSaveActionListener();
        if(list!=null){
            for(SaveActionListener l: list){
                l.saveAction(e);
            }
        }
    }
    
    

    

    
    
    
    public static void main(String[] args) {

            DesktopApp.init("educore", args);

            PObject l = new PObject("test",null, new Properties());
            PObject a = l.getList("activities", PObject.class).addNew();
            PObjectEditor editor = new PObjectEditor();
            editor.setPObject(a, "japps.ui.educore.component.ReadActivityPanel");
            editor.show();

        }

}
