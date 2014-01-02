package netgest.bo.def.v1;
import netgest.bo.def.boDefXeoCode;

public class boDefXeoCodeImpl implements boDefXeoCode 
{
    private String p_source;
    private String[] p_depends;
    private int p_language;
    
    public boDefXeoCodeImpl( int language, String[] depends, String source )
    {
        this.p_language = language;
        this.p_depends  = depends;
        this.p_source   = source;
        if( p_source != null && this.p_source.trim().startsWith("CODE_JAVA") )
        {
            int startPar = p_source.indexOf("(")+1;
            int endPar =    p_source.lastIndexOf(")");
            p_source = p_source.substring(startPar + 1, endPar);
        }
    }

    public int getLanguage()
    {
        return p_language;
    }

    public String getSource()
    {
        return p_source;
    }

    public String[] getDepends()
    {
        return p_depends;
    }

    public boolean getBooleanValue()
    {
        if( p_language == LANG_BOL )
        {
            return p_source.toLowerCase().startsWith("y") ||p_source.toLowerCase().startsWith("t");
        }
        return false;
    }
    
    public boolean needsClass()
    {
        if( this.p_source == null || this.p_source.trim().length() == 0 )
        {
            return false;
        }
        return !(p_source.toLowerCase().startsWith("y") 
               ||
               p_source.toLowerCase().startsWith("n") 
               ||
               p_source.toLowerCase().startsWith("t") 
               ||
               p_source.toLowerCase().startsWith("f"));
    }
}