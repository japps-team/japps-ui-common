package japps.ui.util;

import java.sql.Connection;
import java.util.List;
import static japps.ui.util.Resources.*;
import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author admin
 */
public class DatabaseUtils {
    
    public static final int ROW_AS_ARRAY = 1;
    public static final int ROW_AS_MAP = 2;
    
    
    /**
     * Execute an insert/update/delete script to database
     * cnname is the pefix in properties file to get connection information.
     * Example in properties file you should have the nex property to connecto to a sqlite database
     * app.jdbc.url=jdbc:sqlite:mydatabase.db
     * 
     * and you can include properties for login:
     * 
     * #app.jdbc.user=USERNAME
     * #app.jdbc.password=PASSWORD
     * 
     * @param cnname
     * @param sql
     * @param params
     * @return 
     */
    public static boolean execute(String cnname, String sql, Object... params){
        
        Log.debug("Executing script over "+p(cnname+".jdbc.url")+": " + sql);
        Log.debug("Parameters: "+Arrays.toString(params));
        
        
        try(Connection cnn = getConnection(cnname)){
             if(params == null || params.length == 0){
                Statement st = cnn.createStatement();
                return st.execute(sql);
            }else{
                PreparedStatement st = cnn.prepareStatement(sql);
                int index = 1;
                for(Object p : params){
                    st.setObject(index, p);
                    index++;
                }
                return st.execute();
            }
        }catch(Exception err){
            Log.error("Error executing script "+sql, err);
        }
        return false;
    }
    
    /**
     * Execute a select script to database.
     * 
     * cnname is the pefix in properties file to get connection information.
     * Example in properties file you should have the nex property to connecto to a sqlite database
     * app.jdbc.url=jdbc:sqlite:mydatabase.db
     * 
     * and you can include properties for login:
     * 
     * #app.jdbc.user=USERNAME
     * #app.jdbc.password=PASSWORD
     * 
     * @param cnname
     * @param sql
     * @param params
     * @return 
     */
    public static List<Object[]> query(String cnname, String sql, Object... params){
        List result =  __query(ROW_AS_ARRAY,cnname,sql,params);
        return (List<Object[]>)result;
    }
    
    /**
     * Similar to query function but you will get a HashMap for every row, c
     * containing the column name an its value
     * @param cnname
     * @param sql
     * @param params
     * @return 
     */
    public static List<Map<String,Object>> queryAsMap(String cnname, String sql, Object... params){
        List result =  __query(ROW_AS_MAP,cnname,sql,params);
        return (List<Map<String,Object>>)result;
    }
    
    private static List __query(int rowResultType,String cnname,String sql, Object... params){
        
        Log.debug("Executing query over "+p(cnname+".jdbc.url")+": " + sql);
        Log.debug("Parameters: "+Arrays.toString(params));
        
        if(cnname == null || sql == null ){
            Log.debug("There is not a connection or query configured ("+sql+"/"+cnname+")");
            return null;
        }

        try(Connection cnn = getConnection(cnname)) {
            
            if(cnn == null){
                throw new Exception("Cant create connection for "+cnname);
            }
            
            ResultSet rs;
            if(params == null || params.length == 0){
                Statement st = cnn.createStatement();
                rs = st.executeQuery(sql);
            }else{
                PreparedStatement st = cnn.prepareStatement(sql);
                int index = 1;
                for(Object p : params){
                    st.setObject(index, p);
                    index++;
                }
                rs = st.executeQuery();
            }
            
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            List result = new ArrayList<>();
            //Get all resultset
            while(rs.next()){
                
                if( rowResultType == ROW_AS_MAP){
                    Map<String,Object> hm = new LinkedHashMap<>();
                    for(int i=1;i<=cols;i++){
                        hm.put((String)md.getColumnLabel(i), rs.getObject(i));
                    }
                    result.add(hm);
                }else{
                    Object[] obj = new Object[cols];
                    for(int i=1;i<=cols;i++){
                        obj[i-1] = rs.getObject(i);
                    }
                    result.add(obj);
                }
            }
            
            return result;
        } catch (Exception e) {
            Log.error("Error executing query: "+sql+" ["+cnname+"]",e);
        }
        return null;
    }
    
    
    private static Connection getConnection(String cnname) throws SQLException{
        
        String jdbcurl=p(cnname+".jdbc.url");
        
        if(jdbcurl == null){
            throw new RuntimeException("jdbc connection with name "+cnname+" not found in properties");
        }
        
        String jdbcuser = p(cnname+".jdbc.user");
        String jdbcpass = p(cnname+".jdbc.password");
        
        if(jdbcurl.contains("integratedSecurity=true")){
            return DriverManager.getConnection(jdbcurl);
        }else if(jdbcuser != null && jdbcpass != null){
            return DriverManager.getConnection(jdbcurl, jdbcuser, jdbcpass);
        }else{
            return DriverManager.getConnection(jdbcurl);
        }
    }
    
    public static String dbTypeToString(Object obj){
        
        if(obj == null) return "";
        
        if(obj instanceof LocalDateTime){
            return Util.dateToLocaleString((LocalDateTime)obj);
        }
        
        if(obj instanceof LocalDate){
            return Util.dateToLocaleString((LocalDate)obj);
        }
        
        if(obj instanceof Date){
            return Util.dateToLocaleString((Date)obj);
        }
        if(obj instanceof Double || obj instanceof Float || obj instanceof BigDecimal){
            return NumberFormat.getInstance().format(((Number)obj).doubleValue());
        }
        
        return obj.toString();
        
    }
    
    
    
}
