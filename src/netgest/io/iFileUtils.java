/*Enconding=UTF-8*/
package netgest.io;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 */
public class iFileUtils 
{
    public static final String getParentPath(String path,String sep) 
    {
        if(path.endsWith(sep)) path=path.substring(0,path.length()-1);
        path = path.substring(0,path.indexOf(sep));
        return path;
    }
    public static final String concatPath(String path,String subpath,String sep) {
        if(path.endsWith(sep)) 
            path = path.concat(subpath);
        else
            path = path.concat(sep).concat(subpath);
        return path;
    }
}