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



import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * 
 * Class to manage resource files like, configuration properties, language texts, icons, etc.
 *
 * @author Williams Lopez - JApps
 */
public class Resources {
    
    
    private static Properties config;
    private static Properties lang;
    public  static String appName;
    private static Map<String,Image> iconCache;
    
    
    /**
     * Get the user resource path
     * @return
     * @throws IOException 
     */
    public static Path getUserAppPath() throws IOException {
        Path userAppPath = Paths.get(System.getProperty("user.home"), ".japps", appName);
        Path homeResources = userAppPath.resolve("res");

        if (!Files.exists(homeResources)) {
            Log.debug("First time execution. No configuration files found in .japps folder: " + userAppPath);
            try {
                //copy default japps libraries embedded resource folder
                Files.createDirectories(userAppPath);
                String defaultResources = "/res_default";
                Log.debug("Copying default configuration files from "+defaultResources+" to "+userAppPath.resolve("res"));
                extractResourcesInClasspathFolder(defaultResources, userAppPath.resolve("res"));
                
                //copy custom resource folder
                //Path localResources = Paths.get("res");
                String customResources = "/res";
                Log.debug("Copying specific configuration files from "+customResources+" to "+userAppPath.resolve("res"));
                extractResourcesInClasspathFolder(customResources, userAppPath.resolve("res"));
            } catch (IOException err) {
                Log.error("Cant copy, inJar files", err);
            }
        }
        return userAppPath;
    }
    
    
    /**
     * Find a resource in classpath
     * @param resourcePath
     * @return 
     */
    public static URL findResource(String resourcePath) {
        Log.debug("Looking up resources: " + resourcePath);
        URL resourceURL = Resources.class.getResource(resourcePath);
        return resourceURL;
    }
    
    /**
     * Extracts all files and directries inside a folder in java classpath to a normal folder in file system
     * @param from java folder inside jar
     * @param to folder in file system
     */
    public static void extractResourcesInClasspathFolder(String from, Path to){
        //Log.debug("Copying file (jar)"+from+" to "+to);
        URL resource = Resources.class.getResource(from);
        Log.debug("Source: "+resource);
        if(resource == null){
            return;
        }
        
        String log = "";
        
        //This is for developer environment in IDE like Netbeans or Eclipse
        //where classpath is not packaged in a jar
        try {
            log += "Validating whether is a JAR or not.";
            if (Files.exists(Paths.get(resource.toURI()))) {
                FileUtils.copy(Paths.get(resource.toURI()), to);
                return;
            }
        } catch (FileSystemNotFoundException e) {
            log += ". It is a JAR";
        } catch (URISyntaxException | IOException ex) {
            log += ". Error extracting resources from " + from + " to " + to+": "+ex.getMessage();
        }
        
        Log.debug(log);
        
        //Copy from packaged jar
        try {
            extractResourceFolderFromJar(from, to);
        } catch (IOException | URISyntaxException ex) {
            Log.error("Error extracting folder resources: "+from+" to "+to, ex);
        }
        
        
    }
    
    public static void extractResourceFolderFromJar(String resFolder, Path destFolder) throws MalformedURLException, URISyntaxException, IOException {
       
        if(resFolder.endsWith("/")){
            resFolder = resFolder.substring(0, resFolder.length()-1);
        }
        
        URL r = Resources.class.getResource(resFolder);
        if (r == null || r.getFile() == null) {
            return;
        }

        String jarPath = r.getFile().substring(0, r.getFile().indexOf('!'));

        //Creamos el folder destino
        Files.createDirectories(destFolder);

        URL u = new URL(jarPath);
        Path p = Paths.get(u.toURI());
        JarInputStream jis = new JarInputStream(Files.newInputStream(p));
        JarEntry je;
        while ((je = jis.getNextJarEntry()) != null) {
            
            String pathInJar = "/" + je.getName();
            
            if (!(pathInJar).startsWith(resFolder)) {
                continue;
            }
            
            String relativePath = pathInJar.substring(resFolder.length()+1);
            relativePath = relativePath.replaceAll("[/]+", "\\"+File.separator);
            Path newPath = destFolder.resolve(relativePath);
            //if it is folder
            if (je.getName().endsWith("/")) {
                Files.createDirectories(newPath);
            } else {
                if(!Files.exists(newPath.getParent())){
                    Files.createDirectories(newPath.getParent());
                }
                Files.copy(jis, newPath);
            }

        }

    }


    /**
     * Get user configuration path for this application
     * @return
     * @throws IOException 
     */
    public static Path getConfigPath() throws IOException{
        return getUserAppPath().resolve("res").resolve("config").toAbsolutePath();
    }
    
    /**
     * Get user text languages path for this user
     * @return
     * @throws IOException 
     */
    public static Path getLangPath() throws IOException{
        return getUserAppPath().resolve("res").resolve("lang").toAbsolutePath();
    }
    
    
    /**
     * Get a text from lang files, searching for the default language
     * @param textKey
     * @return 
     */
    public static String $(String textKey){
        if(textKey == null){
            return null;
        }
        String clean = textKey.trim();
        if(clean.startsWith("$(") && clean.endsWith(")")){
            textKey = clean.substring(2, clean.length()-1);
        }
        if(lang.containsKey(textKey)){
            return lang.getProperty(textKey);
        }
        return textKey;
    }
    
    /**
     * Find a property in the config files
     * @param propertyKey
     * @return 
     */
    public static String p(String propertyKey){
        if(config == null){
            throw new RuntimeException("You must place DestopApp.init function before use japps properties");
        }
        if(config.containsKey(propertyKey)){
            String p = config.getProperty(propertyKey);
            //support localization for configuration files
            String clean = p.trim();
            if(clean.startsWith("$(") && clean.endsWith(")")){
                clean = clean.substring(2, clean.length()-1);
                p = Resources.$(clean);
            }
            return p;
        }
        return null;
    }
    
    /**
     * Get an Image in classpath
     * @param imageName
     * @return 
     */
    public static Image icon(String imageName){
        try{
            if(iconCache == null){
                iconCache = new HashMap<>();
            }
            Image img;
            if(iconCache.containsKey(imageName)){
                img = iconCache.get(imageName);
            }else{
                Log.debug("Get icon: "+imageName);
                URL url = iconURL(imageName);
                img = ImageIO.read(url);
                iconCache.put(imageName, img);
            }
            return img;
        }catch(IOException err){
            Log.error("Error al buscar el icono: "+imageName, err);
        }
        return null;
    }
    
    
    /**
     * Get an Image in classpath
     * @param iconName
     * @return 
     */
    public static URL iconURL(String iconName){
        String resource = "/"+p("app.iconset")+"/icon/"+iconName;
        URL url = Resources.class.getResource(resource);
        return url;
    }
    
    /**
     * Get an Image in classpath
     * @param imageName
     * @param width
     * @param height
     * @return 
     */
    public static Image icon(String imageName, int width, int height){
        Image image = icon(imageName);
        if(image!=null){
            return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        }
        return null;
    }
    
    /**
     * Speech a text
     * @param text 
     * @param showWindow 
     */
    public static void speech(String text, boolean showWindow) {
        try {
            Path speech = Resources.getSpeech(text);
            if (speech != null && Files.exists(speech)) {
                
                if(showWindow){
                    Sound.playInWindow(speech);
                }else{
                    Sound.play(speech);
                }
            }
        } catch (Exception e) {
            Log.debug("Cant speech text: "+text);
        }
    }
    
    /**
     * Get the a wav file path with the voice of text
     * @param text
     * @return 
     */
    public static Path getSpeech(String text){
        try{
            
            if(text==null || text.isEmpty()){
                return null;
            }
            
            String l = p("app.language");
            
            if(l.startsWith("es")) l="es-ES";
            if(l.startsWith("en")) l="en-US";
            if(l.startsWith("de")) l="de-DE";
            if(l.startsWith("fr")) l="fr-FR";
            if(l.startsWith("it")) l="it-IT";    
            
            Path userSpeechPath = Paths.get(System.getProperty("user.home"), ".japps", appName,"speech");
            
            text = text.replaceAll("[<][^>]*[>]","");
            
            Log.debug("Creating speech for "+text);
            Log.debug("Directory: "+userSpeechPath);

            if(!Files.exists(userSpeechPath)){
                Files.createDirectories(userSpeechPath);
            }
            
            
            Path wavFile = userSpeechPath.resolve(text.hashCode()+".wav");
            
            if(Files.exists(wavFile)) return wavFile;
            String propertyTool = p("app.tool.voice");
            if(propertyTool==null){
                return null;
            }
            
            propertyTool = propertyTool.replaceAll("[%][f]", wavFile.toAbsolutePath().toString());
            propertyTool = propertyTool.replaceAll("[%][l]", l);
            propertyTool = propertyTool.replaceAll("[%][t]", text);
            
            String  out = Util.execute(userSpeechPath,propertyTool.split("[|][|]"));
            Log.debug(out);
            return wavFile;
            
        }catch(Exception err){
            Log.error("Error getting the speech for: "+text,err);
        }
        return null;
    }

    /**
     * Load all resources
     * @param _appName
     * @param args  
     */
    public static void load(String _appName,String[] args){
        Resources.appName = _appName;
        processOptions(args);
        loadConfig();
        loadLang();
    }
    
    /**
     * Process command line arguments
     * @param args 
     */
    private static void processOptions(String[] args){
        try{
        if(args.length>0){
            switch(args[0]){
                case "reconfigure":
                    try{
                        FileUtils.deleteFileOrDirectory(Paths.get(System.getProperty("user.home"), ".japps", appName));
                    }catch(Exception err){
                        Log.error("Cant delete directory "+Paths.get(System.getProperty("user.home"), ".japps", appName), err);
                    }
                    break;
            }
        }
        }catch(Exception err){
            Log.error("Error al procesar los argumentos",err);
        }
    }
    
    /**
     * Load all configuration files
     */
    private static void loadConfig(){
        try{
            config = new Properties();
            Resources.loadPropertiesFiles(getConfigPath(), config);
        }catch(IOException err){
            Log.error("Error loading configuration files ", err);
        }
    }
    
    /**
     * Load all lang files
     */
    private static void loadLang() {
        try{
            
            String language  = p("app.language");
            if(language.equalsIgnoreCase("default")){
                language = Locale.getDefault().getLanguage();
            }
            lang = new Properties();
            //next load language specific folder
            Resources.loadPropertiesFiles(getLangPath().resolve(language), lang);
            
        }catch(IOException err){
            Log.error("Cant load lang files", err);
        }
    }
    

    
    /**
     * Load all properties inside a package in classpath
     * @param path
     * @param props
     * @throws Exception 
     */
    private static void loadPropertiesFiles(Path path, Properties props) {
        try{
            
            Log.debug("Loading folder: "+path);
            
            if(!Files.exists(path)){
                Log.debug("Fileo "+path+" does not exist");
            }
            
            //first load default.properties
            Path def = path.resolve("default.properties");
            if(Files.exists(def)){
                Log.debug("Loading file: "+def);
                props.load(Files.newInputStream(def));
            }
            
            //next load every .properties file
            List<String> files=FileUtils.listFilesInPath(path);
                for(String f: files){
                    Log.debug("Loading file: "+f);
                    if(!f.endsWith(".properties")) continue;
                    if(f.equals("default.properties")) continue;
                    URL propertiesFile = path.resolve(f).toUri().toURL();
                    props.load(propertiesFile.openStream());
                }
        }catch(Exception e){
            Log.error("Cant load properties file: "+path, e);
        }
    }
    
    /**
     * Gets the properties objet with all the config 
     * @return 
     */
    public static Properties getConfigProperties(){
        return config;
    }
    
    
    public static Font getDefaultFont(){
        String strfont = p("app.font");
        Font f = null;
        try {
            f = Font.decode(strfont);
        } catch (Exception e) {
        }
        if(f == null){
            f = new Font("Arial", Font.PLAIN, 14);
        }
        return f;
    }
    
    
}
