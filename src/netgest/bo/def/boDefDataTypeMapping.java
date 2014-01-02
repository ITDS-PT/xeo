/*Enconding=UTF-8*/
package netgest.bo.def;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.boAttributesArray;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 */
public class boDefDataTypeMapping extends boAttributesArray 
{
    static final String[] alias = {"LONGTEXT","LONGBINNARY"};
    static final String[] maps  = {"CLOB",    "BLOB"};

    static final String  nbotype[] = {"CHAR",     "BOOLEAN", "DURATION",     "CURRENCY",     "NUMBER",       "DATE",     "CLOB",     "BLOB", "IFILE" , "SEQUENCE" };
    static final String  dbmtype[] = {"C",        "C",       "N",            "N",            "N",            "D",        "CL",       "BL",   "C(500)", "N" };
    static final String  siztype[] = {"",          "1",      "" ,            "" ,            "" ,            "" ,        ""  ,       ""  ,   "500"   , "" };
    static final String  objtype[] = {"String",   "String",  "Long",         "Double",       "Double",       "Date",     "String",   "",     "iFile" , "Long" };
    static final String  smptype[] = {"String",   "String",  "long",         "double",       "double",       "Date",     "String",   "",     "iFile" , "long" };
    static final String  dattype[] = {"String",   "String",  "BigDecimal",   "BigDecimal",   "BigDecimal",   "Timestamp","String",   "",     "String", "BigDecimal"};
    static final boolean hvstype[] = {false,      false,     true,           true,           true,           true,       false,      false,  true    ,  true };
    static final boolean rsztype[] = {true,       false,     false,          false,          false,          false,      false,      false,  false   ,  false };
    
    private static final String checkAlias(String atttype) 
    {
        byte i;
        //atttype = atttype.toUpperCase();
        for (i = 0; i < alias.length; i++) 
        {
            if(atttype.startsWith(alias[i]))
                break;
        }
        if(i>=alias.length) 
        {
            return atttype;
        }
        return maps[i];
    }
    
    private static final  byte scanType(String atttype) 
    {
        byte i;
        atttype = checkAlias(atttype.toUpperCase());
        for (i = 0; i < nbotype.length; i++) 
        {
            if(atttype.startsWith(nbotype[i]))
                break;
        }
        if(i>=nbotype.length) 
        {
            throw new RuntimeException(MessageLocalizer.getMessage("DATA_TYPE_NOT_RECOGNIZED")+" ["+atttype+"]");
        }
        return i;
    }
    
    public static final String getDbmType(String atttype) 
    {
        return dbmtype[scanType(atttype)];
    }
    public static final  String getObjectType(String atttype) 
    {
        return objtype[scanType(atttype)];
    }
    public static final  String getSimpleType(String atttype) 
    {
        return smptype[scanType(atttype)];
    }
    public static final  String getDataType(String atttype) 
    {
        return dattype[scanType(atttype)];
    }
    public static final boolean getHaveSimpleType(String atttype) 
    {
        return hvstype[scanType(atttype)];
    }
    public static final boolean getResizable(String atttype) 
    {
        return rsztype[scanType(atttype)];
    }
    public static final String getSize(String atttype) 
    {
        return siztype[scanType(atttype)];
    }
    
        
    public static byte getValueType( String type) {
        byte ret = 0;
        type = type.toUpperCase();
        if (type.startsWith("OBJECT.")) 
        {
            ret = boDefAttribute.VALUE_NUMBER;
        } 
        else if(type.startsWith("CHAR")) 
        {
            ret = boDefAttribute.VALUE_CHAR;
        } 
        else if (type.startsWith("BOOLEAN")) 
        {
            ret = boDefAttribute.VALUE_BOOLEAN;
        } 
        else if (type.startsWith("DURATION")) 
        {
            ret = boDefAttribute.VALUE_DURATION;
        } 
        else if (type.startsWith("CURRENCY")) 
        {
            ret = boDefAttribute.VALUE_CURRENCY;
        } 
        else if (type.startsWith("NUMBER")) 
        {
            ret = boDefAttribute.VALUE_NUMBER;
        } 
        else if (type.startsWith("DATETIME")) 
        {
            ret = boDefAttribute.VALUE_DATETIME;
        } 
        else if (type.startsWith("DATE")) 
        {
            ret = boDefAttribute.VALUE_DATE;
        } 
        else if (type.startsWith("CLOB") || type.startsWith("LONGTEXT")) 
        {
            ret = boDefAttribute.VALUE_CLOB;
        } 
        else if (type.startsWith("BLOB") || type.startsWith("LONGBINARY")) 
        {
            ret = boDefAttribute.VALUE_BLOB;
        } 
        else if (type.startsWith("IFILE")) 
        {
            ret = boDefAttribute.VALUE_IFILELINK;
        }
        else if (type.startsWith("SEQUENCE")) 
        {
            ret = boDefAttribute.VALUE_SEQUENCE;
        }
        return ret;
    }

}