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

import japps.ui.util.Log;
import japps.ui.util.Resources;
import javax.swing.JComponent;

/**
 *
 * @author Williams Lopez - JApps
 */
public class PInfo {

        public static final String TYPE_BOOLEAN = "BOOLEAN";
        public static final String TYPE_INTEGER = "INTEGER";
        public static final String TYPE_DECIMAL = "DECIMAL";
        public static final String TYPE_NUMBER = "NUMBER";
        public static final String TYPE_STRING = "STRING";
        public static final String TYPE_STRING_LONG = "STRING_LONG";
        public static final String TYPE_FILE = "FILE";
        public static final String TYPE_PLIST = "PLIST";
        public static final String TYPE_OPTION = "OPTION";
        public static final String TYPE_DATE = "DATE";
        

        public String property;
        public String type;
        public boolean editable;
        public boolean required;
        public String label;
        public String tooltipText;
        public String defaultValue;
        public JComponent comp;
        public ComponentBuilder builder;
        public PObject pobject;
        public PObjectEditor editor;
        
        public void saveValue(){
            validateValueInComponent();
            Object o = builder.getValue(comp);
            pobject.set(property, o);
        }
        
        
        public Object getObjectValue(){
            return builder.getValue(pobject, property);
        }
        
        public Object getComponentValue(){
            return builder.getValue(comp);
        }
        
        public void validateValueInComponent(){
            if(!builder.isValidValue(comp)){
                throw new RuntimeException(Resources.$("Invalid value for")+property+": "+getComponentValue());
            }
        }
        

        public static PInfo parse(String property, String info) {

            PInfo pinfo = new PInfo();
            //Default values
            pinfo.property = property;
            pinfo.type = TYPE_STRING;
            pinfo.editable = true;
            pinfo.required = true;
            pinfo.label = property;
            pinfo.tooltipText = property;
            pinfo.defaultValue = "";

            if (info == null || info.trim().isEmpty()) {
                return pinfo;
            }

            String[] arrayInfo = info.split("[;]");

            try {
                pinfo.property = property;
                pinfo.type = arrayInfo[0].trim();
                pinfo.editable = Boolean.parseBoolean(arrayInfo[1].trim());
                pinfo.required = Boolean.parseBoolean(arrayInfo[2].trim());
                pinfo.label = Resources.$(arrayInfo[3].trim());
                pinfo.tooltipText = Resources.$(arrayInfo[4].trim());
                if(arrayInfo.length>5){
                    pinfo.defaultValue = Resources.$(arrayInfo[5].trim());
                }
            } catch (Throwable err) {
                Log.error("Error parsing property info  "+pinfo.property+" - "+info, err);
            }

            return pinfo;
        }

    }
