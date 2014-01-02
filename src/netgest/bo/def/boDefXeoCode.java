package netgest.bo.def;

public interface boDefXeoCode 
{
    public static final int LANG_JAVA       = 1;
    public static final int LANG_XEP        = 2;
    public static final int LANG_BOL        = 3;

    public int      getLanguage();
    public String   getSource();
    public String[] getDepends();
    public boolean  getBooleanValue();
    public boolean  needsClass();
    
}