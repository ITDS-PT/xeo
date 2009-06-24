package netgest.bo.def.v2;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefDataTypeMapping;
import netgest.utils.ngtXMLHandler;

public class GeneralParseUtils 
{

    public static final byte parseType( String type )
    {
        byte ret = boDefHandlerImpl.TYPE_CLASS;
        if ( "object".equalsIgnoreCase( type) )
        {
            ret = boDefHandlerImpl.TYPE_CLASS;
        }
        else if ( "abstract".equalsIgnoreCase( type) )
        {
            ret = boDefHandlerImpl.TYPE_ABSTRACT_CLASS;
        }
        else if ( "interface".equalsIgnoreCase( type ) )
        {
            ret = boDefHandlerImpl.TYPE_INTERFACE;
        }
        else
        {
            throw new RuntimeException("Type ["+ type +"] is unknown");
        }
        return ret;
    }
    
    public static final boDefXeoCodeImpl parseCode( ngtXMLHandler xml )
    {
        if( xml != null )
        {
            String      code     = xml.getText();
            String      language = xml.getAttribute("language","BOL");
            String[]    depends  = null;
    
            ngtXMLHandler dep = xml.getChildNode( "depends" );
            if( dep != null )
            {
                ngtXMLHandler[] depNodes    = dep.getChildNodes();
                String[]        depAtt      = new String[ depNodes.length ];
                for (int i = 0; i < depNodes.length; i++) 
                {
                    depAtt[i] = depNodes[i].getText();
                }
                depends = depAtt;
            }
            return new boDefXeoCodeImpl( language, depends, code );
        }
        return null;
    }
    
    public static final byte parseSetParent( String value )
    {
        if( value == null ) return boDefAttribute.SET_PARENT_DEFAULT;
        else if ( "true".equalsIgnoreCase( value ) ) return boDefAttribute.SET_PARENT_YES;
        return boDefAttribute.SET_PARENT_NO;
    }
    
    public static final String parseDbFieldName( String dbname, String attName )
    {
        if( dbname.trim().length() == 0 )
        {
            dbname = attName;
        }
        return dbname;
    }
    public static final byte parseValueType( String type )
    {
        return boDefDataTypeMapping.getValueType( type );    
    }
    
    public static final String parseAttributeNode( String nodeName, String type, int len, int decimals )
    {
        if( boDefAttribute.ATTRIBUTE_BINARYDATA.equalsIgnoreCase( nodeName ) )
        {
            return "ifile";
        }
        else if( boDefAttribute.ATTRIBUTE_BOOLEAN.equalsIgnoreCase( nodeName ) )
        {
            return "boolean";
        }
        else if( boDefAttribute.ATTRIBUTE_CURRENCY.equalsIgnoreCase( nodeName ) )
        {
            return "currency";
        }
        else if( boDefAttribute.ATTRIBUTE_DATE.equalsIgnoreCase( nodeName ) )
        {
            return "date";
        }
        else if( boDefAttribute.ATTRIBUTE_DATETIME.equalsIgnoreCase( nodeName ) )
        {
            return "datetime";
        }
        else if( boDefAttribute.ATTRIBUTE_DURATION.equalsIgnoreCase( nodeName ) )
        {
            return "duration";
        }
        else if( boDefAttribute.ATTRIBUTE_LONGTEXT.equalsIgnoreCase( nodeName ) )
        {
            return "clob";
        }
        else if( boDefAttribute.ATTRIBUTE_NUMBER.equalsIgnoreCase( nodeName ) )
        {
            return "number("+len+","+decimals+")";
        }
        else if( 
                    boDefAttribute.ATTRIBUTE_OBJECT.equalsIgnoreCase( nodeName ) 
                    ||
                    boDefAttribute.ATTRIBUTE_OBJECTCOLLECTION.equalsIgnoreCase( nodeName ) 
            )
        {
            return type;
        }
        else if( boDefAttribute.ATTRIBUTE_SEQUENCE.equalsIgnoreCase( nodeName ) )
        {
            return "sequence";
        }
        else if( boDefAttribute.ATTRIBUTE_TEXT.equalsIgnoreCase( nodeName ) )
        {
            return "char("+len+")";
        }
        throw new RuntimeException("Type :" + nodeName + " " );
    }
}