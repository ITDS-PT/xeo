package netgest.bo.def.v2;
import netgest.bo.def.boDefXeoCode;
import netgest.utils.StringUtils;

public class boDefXeoCodeImpl implements boDefXeoCode
{
    private int         language;
    private String      source;
    private String      parsedSource;
    private String[]    depends;

    public boDefXeoCodeImpl( String language, String[] depends, String source )
    {
        if( "XEP".equalsIgnoreCase( language )  )
            this.language = LANG_XEP;
        else if ( "JAVA".equalsIgnoreCase( language ) )
            this.language = LANG_JAVA;
        else
            this.language = LANG_BOL;
        
        this.depends  = depends;
        this.source   = source;
    }

    public int getLanguage()
    {
        return language;
    }

    public void setLanguage(int language)
    {
        this.language = language;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String[] getDepends()
    {
        return depends;
    }

    public void setDepends(String[] depends)
    {
        this.depends = depends;
    }
    
    public boolean getBooleanValue()
    {
        return GenericParseUtils.parseBoolean( source );
    }
    
    public boolean needsClass()
    {
        if( this.getLanguage() != LANG_XEP )
        {
            return !(StringUtils.isEmpty( source ) || source.equalsIgnoreCase("true") || source.equalsIgnoreCase("false"));
        }
        return false;
    }
}