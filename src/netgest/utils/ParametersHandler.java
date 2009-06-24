/*Enconding=UTF-8*/
package netgest.utils;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
 
 public class ParametersHandler implements java.io.Serializable,Cloneable
{
    private Hashtable ht; 
    
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }
    public ParametersHandler()
    {
        
    }
    
    private final void initialize() {
    	if( ht == null ) {
    		ht = new Hashtable( 1,0.5f );
    	}
    }
    
    public void setParameter(String name,String value) {
    	initialize();
        ht.put(name,value);
    }
    
    public String getParameter(String parametername) {
    	if(ht != null ) {
        return (String)ht.get(parametername);
    }
    	return null;
    }
    public void setParameters(String[] parnames,String[] parvalues) {
    	initialize();
        for (short i = 0;parnames!=null && i < parnames.length; i++)  {
            ht.put(parnames[i],parvalues[i]);
        }
    }
    public String[] getParametersNames() {
    	if(ht != null ) {
        Enumeration keys = ht.keys();
        ArrayList akeys = new ArrayList(); 
        while(keys.hasMoreElements()) {
            akeys.add(keys.nextElement());
        }
        return (String[])akeys.toArray(new String[akeys.size()]);
    }
    	return new String[0];
    }
    public String[] getParametersValues() {
    	if(ht != null ) {
        Enumeration keys = ht.elements();
        ArrayList akeys = new ArrayList(); 
        while(keys.hasMoreElements()) {
            akeys.add(keys.nextElement());
        }
        return (String[])akeys.toArray(new String[akeys.size()]);
    }
    	return new String[0];
    }
    public void removeParameter(String paramname) {
    	if(ht != null ) {
        ht.remove(paramname);
    }
     }
     
}