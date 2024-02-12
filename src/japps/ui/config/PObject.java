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

import japps.ui.util.Log;
import japps.ui.util.Resources;
import java.awt.Font;
import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 *
 * @author Williams Lopez - JApps
 */
public class PObject {

    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected PObject parent;
    protected Map properties;
    protected String id;
    protected HashMap<String,PList> cachedLists;

    /**
     * Creates a new ConfigurableObject
     * @param id
     * @param parent
     * @param properties 
     */
    public PObject(String id, PObject parent,Map properties){
        __construct(id, parent, properties);
    }
    
    protected void __construct(String id, PObject parent,Map properties){
        this.id = id;
        this.properties = properties;
        this.parent = parent;
        this.cachedLists = new HashMap<>();
    }
    
    protected PObject(){
        throw new RuntimeException("Unsuported constructor ConfigurableObject()");
    }

    /**
     * Get the id of this activity option
     * @return 
     */
    public String getId() {
        return id;
    }
    
    /**
     * Get a key in this activity option
     * @param key
     * @return 
     */
    public String get(String key) {
        String v = getString(key);
        return v;
    }
    
    
    /**
     * Set a property in this activity option
     * @param key
     * @param value 
     */
    public void set(String key, Object value) {
        

        String svalue = value==null?"null":value.toString();
        
        if(value instanceof Date){
            svalue = dateFormat.format(value);
        }
        
        if(value instanceof Calendar){
            Calendar c = (Calendar) value;
            svalue = dateFormat.format(c.getTime());
        }
        
        if(value instanceof Path){
            svalue = value.toString();
            svalue = svalue.replaceAll("["+File.separator+"]", "/");
        }
        
        if(value instanceof Font){
            Font f = (Font)value;
            String family = f.getFamily();
            String style  = "PLAIN";
            int size   = f.getSize();
            switch(f.getStyle()){
                case Font.BOLD: style = "BOLD"; break;
                case Font.ITALIC: style = "ITALIC"; break;
                case (Font.BOLD+Font.ITALIC): style = "BOLDITALIC"; break;
                    
            }
            svalue = family+"-"+style+"-"+size;
        }
        

        properties.put(id+"."+key, svalue);
    }
    
    /**
     * Remove the PObject data in the "setted" Properties object
     */
    public void remove(){
        remove(properties);
    }
    
    /**
     * Remove the PObject data in the properties object
     * @param properties 
     */
    public void remove(Map<String,String> properties){
        java.util.List<String> toremove = new ArrayList<>();
        for(Map.Entry<String,String> e : properties.entrySet()){
            if(e.getKey().startsWith(id)){
                toremove.add(e.getKey());
            }
        }
        toremove.forEach((k)->{ properties.remove(k); });
    }
    
    /**
     * Get a property value and parse to boolean
     * @param propertyName
     * @return 
     */
    public boolean getBool(String propertyName){
        String v = get(propertyName);
        if(v!=null){
            return Boolean.parseBoolean(v.toLowerCase());
        }
        return false;
    }
    
    /**
     * Get a property value and parse to int
     * -1 if cant find property
     * @param propertyName
     * @return 
     */
    public int getInt(String propertyName){
        String v = get(propertyName);
        if(v!=null){
            return Integer.parseInt(v);
        }
        return -1;
    }
    
    /**
     * Get a property value and parse to double
     * -1 if property does not exist
     * @param propertyName
     * @return 
     */
    public double getDouble(String propertyName){
        String v = get(propertyName);
        if(v!=null){
            return Double.parseDouble(v);
        }
        return -1;
    }
    
    /**
     * Get a property with date format
     * @param propertyName
     * @return
     * @throws ParseException 
     */
    public Date getDate(String propertyName) throws ParseException{
        String v = get(propertyName);
        if(v!=null){
            return dateFormat.parse(v);
        }
        return null;
    }
    
    /**
     * Decode a font into the string value in format
     * Family-STYLE-size
     * @param propertyName
     * @return 
     */
    public Font getFont(String propertyName){
        String v = get(propertyName);
        if(v!=null){
            return Font.decode(v);
        }
        return null;
    }
    
    /**
     * Get a property with path format
     * It replaces all / for Files.separator
     * @param key
     * @return 
     */
    public Path getPath(String key) {
        String v = get(key);
        if (v == null) {
            return null;
        }
        v = v.replaceAll("[/]", File.separator);
        Path p = null;
        try {
            p = Resources.getUserAppPath().resolve(v);
            if (!Files.exists(p)) {
                p = Paths.get(v);
            }
        } catch (Exception err) {
            Log.error("Error resolvin path: " + v, err);
        }
        System.out.println("Accessing path: " + p.toString());
        return p;
    }
    
    public String getString(String key){
        String v = (String)properties.get(id+"."+key);
        if(v==null || v.equals("null")){
            return null;
        }
        return v;
    }
    
    /**
     * Get a list of configurable objects, if the list doesnt exists, then an
     * empty list will be created
     * @param <T>
     * @param key
     * @return 
     */
    public <T extends PObject>PList getList(String key, Class<T> clazz){
        PList list = cachedLists.get(key);
        if(list == null){
            list = new PList(key,clazz);
            cachedLists.put(key, list);
        }
        return list;
    }
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if(obj instanceof String){
            return id.equals((String)obj);
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PObject other = (PObject) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    /**
     * Get the parent of a PObject
        For example
        A parent of an PObject is an Activity
        A parent of an Activity is a Learning
     * @return 
     */
    public PObject getParent() {
        return parent;
    }    
    
    /**
     * Get all property keys of this object
     *
     * @return
     */
    public List<String> properties() {
        List<String> props = new ArrayList<>();
        
        for(Object o: properties.keySet()){
            String key = (String)o;
            if(key.startsWith(key+".")){
                props.add(key);
            }
        }
        return props;
    }
    
    /**
     * Check if a property exists
     * @param key
     * @return 
     */
    public boolean containsProperty(String key){
        return this.properties.containsKey(id+"."+key);
    }
    


    @Override
    public String toString() {
        List<String> props = properties();
        StringBuilder sb = new StringBuilder(id);
        for(String k:props){
            sb.append(k);
            sb.append("=");
            sb.append(get(k));
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public class PList<T extends PObject> implements Iterable<T>{
        
        private String key;
        private int size;
        private PObject[] cachedList;
        private int maxChildIndex;
        private Class<T> clazz;
        
        /**
         * Creates a new object list
         * @param key 
         */
        private PList(String key, Class<T> clazz){
            this.key = key;
            this.cachedList = new PObject[20];
            this.maxChildIndex = 0;
            this.size = 0;
            this.clazz = clazz;
            String objectString = PObject.this.get(key);
            if(objectString!=null){
                String[] objectIds = objectString.split("[,]");
                for(String objid : objectIds){
                    //ConfigurableObject obj = new PObject(objid, PObject.this, properties);
                    PObject obj = instanceObject(objid);
                    cachedList[size] = obj;
                    size++;
                    
                    try {
                        int index = Integer.parseInt(objid.substring(objid.lastIndexOf(".")+1));
                        if(index > maxChildIndex){
                            maxChildIndex = index;
                        }
                    } catch (Exception e) {
                        Log.error("Error checking max List child index", e);
                    }
                    
                }
            }else{
                updateObjectStringProperty();
            }
        }
        
        
        private T instanceObject(String id){
            T o = null;
            try {
                o = this.clazz.getConstructor(String.class,PObject.class,Map.class).newInstance(id, PObject.this, properties);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return o;
        }
        
        private void updateObjectStringProperty() {
            
            String objString = "";
            
            if(cachedList!=null && size>0){
                objString = cachedList[0].id;
                for(int i=1;i<size;i++){
                    objString += ","+cachedList[i].id;
                }
            }
            PObject.this.set(this.key, objString);
        }

        /**
         * Adds a new object at the end of this list
         * @param clazz
         * @return 
         */
        public T addNew(){
            //Check array capacity
            if (size >= cachedList.length) {
                //Expanding array capacity
                PObject[] newCachedList = new PObject[cachedList.length + 20];
                for (int i = 0; i < cachedList.length; i++) {
                    newCachedList[i] = cachedList[i];
                }
                cachedList = newCachedList;
            }
            
            //Adding new object
            String objectId = id+(".")+(maxChildIndex+1);
            
            //ConfigurableObject object = new PObject(objectId, PObject.this, properties);
            PObject object = instanceObject(objectId);
                    
            cachedList[size] = object;
            size++;
            maxChildIndex++;
            
            updateObjectStringProperty();
            
            return (T)object;
        }
        
        /**
         * Removes an object of this list
         * @param obj true if the object was found
         * @return 
         */
        public boolean remove(PObject obj) {
            int i2 = 0, found = 0;
            for(int i=0;i<size;i++){
                PObject obj2 = cachedList[i];
                if(!obj2.equals(obj)){
                    cachedList[i2] = cachedList[i];
                    i2++;
                }else{
                    obj.remove();
                    found++;
                }
            }
            size = size-found;
            updateObjectStringProperty();
            return found > 0;
        }
        
        /**
         * Changes o1 to o2 position, and o2 to o1 position
         * @param o1
         * @param o2
         * @return 
         */
        public boolean swap(PObject o1, PObject o2){
            int i1 = indexOf(o1);
            int i2 = indexOf(o2);
            
            if(i1 < 0 || i2 < 0){
                return false;
            }
            cachedList[i1] = o2;
            cachedList[i2] = o1;
            updateObjectStringProperty();
            return true;
        }
        
        /**
         * Changes object in index 1 to index2 and object in index2 to index1
         * @param index1
         * @param index2
         * @return 
         */
        public boolean swap(int index1, int index2){
            PObject o1 = cachedList[index1];
            PObject o2 = cachedList[index2];
            cachedList[index1] = o2;
            cachedList[index2] = o1;
            updateObjectStringProperty();
            return true;
        }
        
        /**
         * Get the index of this object o
         * @param o
         * @return 
         */
        public int indexOf(PObject o){
            if(size == 0 || cachedList == null || cachedList.length == 0 || o == null ){
                return -1;
            }
            int index = 0;
            for(int i=0;i<size;i++){
                PObject o2 = cachedList[i];
                if(o.equals(o2)){
                    return index;
                }
                index++;
            }
            return index;
        }

        /**
         * Get the list size
         * @return 
         */
        public int size() {
            return size;
        }
        
        /**
         * Removes all elements in this list
         */
        public void removeAll(){
            for(int i=0;i<size;i++){
                PObject o = cachedList[i];
                o.remove();
            }
            size = 0;
            updateObjectStringProperty();
        }
        
        /**
         * Gets an array with the objects of this list
         * @return 
         */
        public T[] toArray(){
            T[] array =  (T[])Array.newInstance(clazz, size);
            
            for(int i=0;i<size;i++){
                array[i] = (T)cachedList[i];
            }
            return array;
         }

        /**
         * Get an element by id property
         * @param id
         * @return 
         */
        public T getById(String id){
            for(T o : this){
                if(o.getId().equals(id)){
                    return o;
                }
            }
            return null;
        }
        
        /**
         * Get an element by index
         * @param index
         * @return 
         */
        public T get(int index){
            if(index < 0 || index >= size){
                throw new RuntimeException("Index out of bounds");
            }
            return (T)cachedList[index];
        }
        
        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {

                int indexIterator = 0;
                
                @Override
                public boolean hasNext() {
                    return indexIterator < size();
                }

                @Override
                public T next() {
                    PObject obj= cachedList[indexIterator];
                    indexIterator++;
                    return (T)obj;
                }
            };
        }

        public boolean isEmpty() {
            return size <= 0;
        }
        
        /**
         * Get the key of this list
         * @return 
         */
        public String key(){
            return key;
        }
        

    }
    
    
}
