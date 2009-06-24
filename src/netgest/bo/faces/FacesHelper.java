/*Enconding=UTF-8*/
package netgest.bo.faces;
import netgest.bo.def.*;
import netgest.bo.runtime.*;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class FacesHelper 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public FacesHelper()
    {
    }
    
    public static void facesDispatcher(boObject object) throws boRuntimeException
    {
        boDefHandler def[] = object.getBoDefinition().getBoInterfaces();
        for (int i = 0; i < def.length; i++) 
        {
            if("iESP_SMCE".equalsIgnoreCase(def[i].getName()) &&
                "S".equalsIgnoreCase(object.getAttribute("implements_iESP_SMCE").getValueString()))
            {
                boObject solicitation = object.getAttribute("solicitation").getObject();
                boObject interv = object.getAttribute("intervention").getObject();
                if(interv != null && !interv.getBridge("tasks").haveBoui(object.getBoui()))
                {
                    interv.getBridge("tasks").add(object.getBoui());
                }
            }
            else if("iGP_Proj".equalsIgnoreCase(def[i].getName()) &&
                "S".equalsIgnoreCase(object.getAttribute("implements_iGP_Proj").getValueString()))
            {
                boObject proj = object.getAttribute("project").getObject();
                boObject fase = object.getAttribute("fase").getObject();
                boObject modulo = object.getAttribute("modulo").getObject();
                if(modulo != null && !modulo.getBridge("tasks").haveBoui(object.getBoui()))
                {
                    modulo.getBridge("tasks").add(object.getBoui());
                }
            }
        }
        
    }
}