package netgest.bo.localized;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import netgest.bo.system.boSession;

public class JSPMessages extends Thread
{

    public JSPMessages()
    {
    }

    public static String getString(String key)
    {
        try
        {
            String s = boSession.getResourceBundle( BUNDLE_NAME ).getString(key);
            return s;
        }
        catch(MissingResourceException e)
        {
            String s1 = '!' + key + '!';
            return s1;
        }
    }
    private static final String BUNDLE_NAME = "netgest.bo.localized.JSPMessages";
    
}