/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.http.*;

public class ParametersHandler  {
    private Hashtable Paramaters=new Hashtable();
    public ParametersHandler(HttpServletRequest request) {
        Enumeration oEnum = request.getParameterNames();
        while(oEnum.hasMoreElements()) {
            String key = (String)oEnum.nextElement();
            Paramaters.put(key,request.getParameter(key));
        }
    }
    public String getParameter(String key) {
        return (String)Paramaters.get(key);
    }
}