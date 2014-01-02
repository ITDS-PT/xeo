/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.sql.*;
import netgest.bo.*;

public class boQLRuntimeEval  {
    public static final boolean evalBoolean(boObject bobj,String qlstring) {
        PreparedStatement pstm=null;
        ResultSet rslt=null;
        try {
            pstm = bobj.getEboContext().getConnectionData().prepareStatement("select rowid from "+bobj.getBoDefinition().getBoMasterTable()+" where ("+qlstring+") and boui = ?");
//            pstm.setString(1,qlstring);
            pstm.setLong(1,bobj.bo_boui);
            rslt = pstm.executeQuery();
            if(rslt.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new boException("boQLRuntimeEval.evalBoolean","BO-1702",e,qlstring);
        } finally {
            try {if(rslt!=null) rslt.close();}catch(SQLException e) {};
            try {if(pstm!=null) pstm.close();}catch(SQLException e) {};
        }
    }
}