/*Enconding=UTF-8*/
package netgest.bo.builder;
import netgest.bo.*;
import netgest.utils.*;
import java.util.*;

public class boBuildDBUtils  {
    public static String[] parseFieldType(String type) {
        type = type.toUpperCase();
        String[] ret = new String[2];
        if(type.startsWith("CHAR")) {
            //Vector xvect = tools.Split(type,"(");
            String[] xvect = type.split("\\(");
            String[] xvect1=null;
            if(xvect.length > 1) {
                //xvect1 = tools.Split(((String)xvect.get(1)),")");
                xvect1 = xvect[1].split("\\)");
                ret[0] = "C";
                ret[1] = xvect1[0]; //.get(0);
            } else if(xvect1 !=null && xvect1.length==1 && xvect != null && xvect.length== 1) {
                String xstr = xvect[1];
                ret[0] = "C";
                ret[1] = xvect1[0];
            } else {
                String[] args = {type};
                throw new boException("netgest.bo.builder.boBuildUtils.parseFieldType(String)","BO-1306",null,args);
            }
        } 
        else if (type.startsWith("TIMESTAMP")) 
        {
                ret[0] = "TIMESTAMP";
                ret[1] = "";
        } 
        else if (type.startsWith("RAW")) {
        
          //  Vector xvect = tools.Split(type,"(");
          
            //Vector xvect1=null;
            String[] xvect = type.split("\\(");
            String[] xvect1=null;
            if(xvect.length > 1) {
                xvect1 = xvect[1].split("\\)");
                ret[0] = "RAW";
                ret[1] = xvect1[0];
            } else if(xvect1 !=null && xvect1.length==1 && xvect != null && xvect.length == 1) {
                String xstr = xvect[1];
                ret[0] = "RAW";
                ret[1] = xvect1[0];
            } else {
                String[] args = {type};
                throw new boException("netgest.bo.builder.boBuildUtils.parseFieldType(String)","BO-1306",null,args);
            }
        } else if (type.startsWith("BOOLEAN")) {
                ret[0] = "C";
                ret[1] = "1";
        } else if (type.startsWith("DURATION")) {
                ret[0] = "N";
                ret[1] = "";
        } else if (type.startsWith("CURRENCY")) {
                ret[0] = "N";
                ret[1] = "";
        } else if (type.startsWith("NUMBER") || type.startsWith("SEQUENCE") ) {
                ret[0] = "N";
                ret[1] = "";
        } else if (type.startsWith("DATE")) {
                ret[0] = "D";
                ret[1] = "";
        } else if (type.startsWith("CLOB") || type.startsWith("LONGTEXT")) {
                ret[0] = "CL";
                ret[1] = "";
        } else if (type.startsWith("BLOB") || type.startsWith("LONGBINARY")) {
                ret[0] = "BL";
                ret[1] = "";
        } else if (type.startsWith("IFILE") || type.startsWith("IFILE")) {
                ret[0] = "C";
                ret[1] = "500";
        } else {
            String[] args = {type};
            throw new boException("netgest.bo.builder.boBuildUtols.parseFieldType(String)","BO-1305",null,args);
        }
        return ret;
    }
    
}