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



import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.imageio.ImageIO;

/**
 *
 * @author Williams Lopez - JApps
 */
public class Resources {
    
    
    private static Properties config;
    private static Properties lang;
    public  static String appName;
    private static Map<String,Image> iconCache;
    
    
    
    public static Path getUserAppPath() throws IOException{
        Path userAppPath = Paths.get(System.getProperty("user.home"), ".japps", appName);

        if (!Files.exists(userAppPath)) {
            Log.debug("Creationg userAppPath: "+userAppPath);
            Path sharedConfigDir = Paths.get("res");
            if (!Files.exists(sharedConfigDir)) {
                try {
                    Log.debug("You have not defined a res directory, creating");
                    /*
                    Path configFile = Paths.get(Resources.class.getResource("/res/config/default.properties").toURI());
                    Path langFile = Paths.get(Resources.class.getResource("/res/lang/lang.properties").toURI());
                    Path config = Paths.get("res","config");
                    Path lang   = Paths.get("res","lang");
                    
                    Files.createDirectories(config);
                    Files.createDirectories(lang);
                    
                    FileUtils.copy(configFile, config);
                    FileUtils.copy(langFile, lang);
                    */
                    Path resJar = Paths.get(Resources.class.getResource("/res").toURI());
                    Path resDir = Paths.get("res").toAbsolutePath().getParent();
                    Files.createDirectories(resDir);
                    FileUtils.copy(resJar, resDir);
                    
                } catch (Throwable err) {
                    Log.debug("Cant copy, inJar files",err);
                }
            }
            Log.debug("Resources files does not exists, copying...");
            FileUtils.copy(sharedConfigDir, userAppPath);
        }

        return userAppPath;
    }    
    
    
    /**
     * Get a text from lang files, searching for the default language
     * @param textKey
     * @return 
     */
    public static String $(String textKey){
        if(lang.containsKey(textKey)){
            return lang.getProperty(textKey);
        }
        return textKey;
    }
    
    /**
     * Find a property in the config files
     * @param propertyKey
     * @return
     * @throws Exception 
     */
    public static String p(String propertyKey){
        if(config.containsKey(propertyKey)){
            return config.getProperty(propertyKey);
        }
        return propertyKey;
    }
    
    /**
     * Get an Image in classpath
     * @param iconName
     * @return 
     */
    public static Image icon(String imageName){
        try{
            if(iconCache == null){
                iconCache = new HashMap<>();
            }
            
            Image img = null;
            
            if(iconCache.containsKey(imageName)){
                img = iconCache.get(imageName);
            }else{
                Log.debug("Get icon: "+imageName);
                URL url = iconURL(imageName);
                img = ImageIO.read(url);
                iconCache.put(imageName, img);
            }
            
            return img;
        }catch(Throwable err){
            Log.debug("Error al buscar el icono: "+imageName, err);
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
     */
    public static void speech(String text) {
        try {
            Path speech = Resources.getSpeech(text);
            if (speech != null && Files.exists(speech)) {
                Sound.play(speech);
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
            
            String l = p("app.language");
            
            if(l.startsWith("es")) l="es-ES";
            if(l.startsWith("en")) l="en-US";
            if(l.startsWith("de")) l="de-DE";
            if(l.startsWith("fr")) l="fr-FR";
            if(l.startsWith("it")) l="it-IT";    
            
            Path userSpeechPath = Paths.get(System.getProperty("user.home"), ".japps", appName,"speech");
            
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
            Log.debug("Error getting the speech for: "+text,err);
        }
        return null;
    }

    /**
     * Load all resources
     * @param _appName
     * @param args 
     */
    public static void load(String _appName,String[] args){
        appName = _appName;
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
                        Log.debug("Cant delete directory "+Paths.get(System.getProperty("user.home"), ".japps", appName), err);
                    }
                    break;
            }
        }
        }catch(Exception err){
            Log.debug("Error al procesar los argumentos",err);
        }
    }
    
    /**
     * Load all configuration files
     */
    private static void loadConfig(){
        
        
        
        try{
            
            Path userAppPath = getUserAppPath();
                     
            config = new Properties();
            
            Log.debug("Loading jar:/res/config/default.properties file");
            config.load(Resources.class.getResourceAsStream("/res/config/default.properties"));
            
            Files.list(userAppPath.resolve("res").resolve("config")).forEach((p)->{
                try {
                    Log.debug("Loading "+p+" file");
                    config.load(Files.newInputStream(p));
                } catch (IOException ex) {
                    Log.debug("Cant load configuration file: "+ p, ex);
                }
            });

        }catch(Throwable err){
            Log.debug("Error loading configuration files ", err);
        }
    }
    
    /**
     * Load all lang files
     */
    private static void loadLang() {
        try{
            
            Path userAppPath = getUserAppPath();
            
            String language  = p("app.language");
            
            if(language.equalsIgnoreCase("default")){
                language = Locale.getDefault().getLanguage();
            }
            
            lang = new Properties();
            
            Log.debug("Loading default lang files");
            Resources.loadPropertiesInClasspath("/res/lang",lang);
            Log.debug("Loading default locale lang files: " + language);
            Resources.loadPropertiesInClasspath("/res/lang/"+language, lang);
            
            Log.debug("Loading app default lang files");
            Resources.loadPropertiesFiles(userAppPath.resolve("res").resolve("lang"), lang);
            
            Log.debug("Loading app default lang files");
            Path lp = userAppPath.resolve("res").resolve("lang").resolve(language);
            if(!Files.exists(lp)){
                Files.createDirectories(lp);
            }
            Resources.loadPropertiesFiles(lp, lang);
            
            
        }catch(Exception err){
            err.printStackTrace();
        }
    }
    
    /**
     * Load all properties inside a package in classpath
     * @param pack
     * @param props
     * @throws Exception 
     */
    private static void loadPropertiesInClasspath(String pack, Properties props){
        try{
            URL packurl = Util.class.getResource(pack);
            File file = new File(packurl.toURI());
            Path path = file.toPath();
            loadPropertiesFiles(path, props);
        }catch(Exception e){
            Log.debug("Cant load properties package: "+pack, e);
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
            List<String> files=FileUtils.listFilesInPath(path);
                for(String f: files){
                    if(!f.endsWith(".properties")) continue;
                    URL propertiesFile = path.resolve(f).toUri().toURL();
                    props.load(propertiesFile.openStream());
                }
        }catch(Exception e){
            Log.debug("Cant load properties file: "+path, e);
        }
    }
    
    
    
    
    
}
