/*Enconding=UTF-8*/
package netgest.io;

public class iFilePermissionDenied extends Exception  {
    public iFilePermissionDenied(String error) {
        super(error);
    }
    
    public iFilePermissionDenied(Throwable e)
    {
    	super(e);
    }
}