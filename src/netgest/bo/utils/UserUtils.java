/*Enconding=UTF-8*/
package netgest.bo.utils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;

import netgest.bo.system.Logger;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class UserUtils 
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.utils.UserUtils");
    
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public UserUtils()
    {
    }
    
    public static long getUser(EboContext ctx, String userName)
    {
        Connection cn=null;
        ResultSet rslt=null;
        PreparedStatement pstm=null;
        long toReturn=0;
        try 
        {
              cn = ctx.getConnectionData();
              
              QLParser qp = new QLParser();
              pstm = cn.prepareStatement(qp.toSql("SELECT iXEOUser WHERE USERNAME=?", ctx));
              pstm.setString(1,userName);
              rslt = pstm.executeQuery();
              if(rslt.next()) {
                  toReturn = rslt.getLong(1);
              }
        }
        catch (SQLException e) {
            logger.severe("Erro:", e);
        }
        finally {
            try {
                if(rslt!=null) rslt.close();
            }
            catch (Exception e) {
                // Doesn't do anything continue to realease resources;
            }
            try {
                if(pstm!=null) pstm.close();
            }
            catch (Exception e) {
                // Doesn't do anything continue to realease resources;
            }
        }        
        return toReturn;
    }
}