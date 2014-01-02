/*Enconding=UTF-8*/
package netgest.bo.runtime;
import javax.naming.*;

public class boContextFactory  
{
    private static ThreadLocal ic = new ThreadLocal() {
        protected synchronized Object initialValue() {
            try {
                 return new InitialContext();
            } catch (NamingException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    };
    public static final Context getContext() {
            return (Context)ic.get();
    }
    
}