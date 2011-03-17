package netgest.bo.def.v2;
import netgest.utils.ngtXMLHandler;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.Logger;

public class GenericParseUtils 
{
    private static Logger logger = Logger.getLogger( GenericParseUtils.class.getName() );

    public static final boolean parseBoolean( String value )
    {
        return "true".equalsIgnoreCase( value );
    }

    public static final long parseLong( String value )
    {
        if( value != null )
        {
            try 
            {
                return Long.parseLong( value );
            } 
            catch (NumberFormatException ex) 
            {
                return 0;
            }
        }
        return 0;
    }

    public static final int parseInt( String value )
    {
        if( value != null )
        {
            try 
            {
                return Integer.parseInt( value );
            } 
            catch (NumberFormatException ex) 
            {
                logger.warn(LoggerMessageLocalizer.getMessage("UNPARSABLE_NUMBER")+"["+value+"]" );
                return 0;
            }
        }
        return 0;
    }

    public static final int parseIntOrN( String value )
    {
        if( value != null )
        {
            try 
            {
                if( "".equalsIgnoreCase( value ) )
                {
                    return 0;
                }
                else if( "N".equalsIgnoreCase( value ) ) 
                {
                    return Integer.MAX_VALUE;
                }
                else
                {
                    return Integer.parseInt( value );
                }
            }
            catch (NumberFormatException ex) 
            {
                logger.warn(LoggerMessageLocalizer.getMessage("UNPARSABLE_NUMBER")+"["+value+"]" );
                return 0;
            }
        }
        return 0;
    }
    
    public static final ngtXMLHandler getChildOfName( ngtXMLHandler node, String childName )
    {
        ngtXMLHandler[] childs = node.getChildNodes();
        for (int i = 0;childs != null && i < childs.length; i++) 
        {
            if( childName.equalsIgnoreCase( childs[i].getAttribute("name") ) )
            {
                return childs[i];
            }
        }
        return null;
    }
}