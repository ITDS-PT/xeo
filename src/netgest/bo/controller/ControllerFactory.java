/*Enconding=UTF-8*/
package netgest.bo.controller;

import java.lang.reflect.Constructor;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.controller.basic.BasicController;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.dochtml.docHTML;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;

/**
 * <p>Title: ControllerFactory </p>
 * <p>Description: Classe que resolve qual o controlador a usar no contexto em questão.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public final class ControllerFactory 
{    
    /**
     *  Chave do parâmetro que especifica o controller a ser utilizado. 
     */
    public final static String CONTROLLER_NAME_KEY = "controllerName";
    /**
     *  Chave do parâmetro que obriga a manter o controller utilizado no request anterior. 
     */
    public final static String CONTROLLER_STICK_KEY = "controllerStick";
    
    /**
     * Devolve o controlador a usar neste contexto.
     * @param dochtml , docHTML no contexto.
     * @return Controller, controlador a ser utilizado neste contexto.
     */
    public static Controller getController(docHTML dochtml)  throws boRuntimeException
    {                
        return findController(dochtml); 
    }
    
    /**
     * Descobre o controlador a usar neste contexto. Se existir um XwfController
     * na pool, estamos no contexto do xwf assim usa sempre esse controlador.
     * @param dochtml , docHTML no contexto.
     * @return Controller, controlador a ser utilizado neste contexto.
     */    
    private static Controller findController(docHTML dochtml) throws boRuntimeException
    {
        Controller controller = null;
        if(dochtml.getControllerQueue().containsKey(XwfKeys.CONTROLLER_NAME_KEY))
        {
            controller = getXwfController(dochtml);
        }
        else
        {
            if(isStickyController(dochtml.getEboContext()))
            {
                controller = dochtml.getController();
                if(controller == null)
                {
                    controller = findControllerByName(dochtml);
                }
            }
            else
            {
                controller = findControllerByName(dochtml); 
            }
        }
        return controller;
    }
    /**
     * Descobre o controlador a usar neste contexto pelo nome no passado no request.
     * @param dochtml , docHTML no contexto.
     * @return Controller, controlador a ser utilizado neste contexto.
     */    
    private static Controller findControllerByName(docHTML dochtml) throws boRuntimeException
    {
        Controller controller = null;  
        String controllerName = getControllerName(dochtml.getEboContext());
        if(controllerName != null)
        {
            if(XwfKeys.CONTROLLER_NAME_KEY.equals(controllerName))
            {
                controller = getXwfController(dochtml);
            }
            else 
            {
                controller = getBasicController(dochtml);
            }            
        }
        else
        {
            controller = getBasicController(dochtml);               
        }        
        return controller;
    }    
    /**
     * Descobre se o controlador a usar neste contexto, é o mesmo do contexto anterior.
     * @param ctx , EboContext contexto em causa.
     * @return result, TRUE se for para manter o controllador do request anterior, FALSE caso contrátio.
     */     
    private static boolean isStickyController(EboContext ctx) throws boRuntimeException
    {
        boolean result = false;
        HttpServletRequest request = ctx.getRequest();
        String sticky = (String)request.getAttribute(CONTROLLER_STICK_KEY);
        if(sticky == null || "".equals(sticky))
        {
            sticky = (String)request.getParameter(CONTROLLER_STICK_KEY);
        }                   
        if(sticky != null && "true".equalsIgnoreCase(sticky))
        {
            result = true;
        }
        return result;
    }
    /**
     * Devolve o controlador a usar neste contexto.
     * @param ctx , EboContext contexto em causa.
     * @return controllerName, nome do controlador a ser utilizado neste contexto.
     */    
    private static String getControllerName(EboContext ctx)
    {
        HttpServletRequest request = ctx.getRequest();
        String controllerName = (String) request.getAttribute(CONTROLLER_NAME_KEY);
        if(controllerName == null || "".equals(controllerName))
        {
            controllerName = (String)request.getParameter(CONTROLLER_NAME_KEY);
        }      
        return controllerName;
    }
    /**
     * Devolve o controlador default.
     * @param dochtml , docHTML no contexto.
     * @return BasicController, controlador default.
     */        
    private static Controller getBasicController(docHTML dochtml)
    {
        Controller controller = null;
        if(dochtml.getController() == null || !"BasicController".equals(dochtml.getController().getName()))
        {
             try
             {
                 Constructor controlConst = Class.forName( "netgest.bo.workflow.oracle.ActivityController" ).getConstructor( new Class[] { docHTML.class } );
                 controller = (Controller)controlConst.newInstance(new Object[] { dochtml });                 
             }
             catch (Exception e)
             {
             } 
             if( controller == null )
             {
                 if(dochtml.getControllerQueue().containsKey("BasicController"))
                 {
                    controller = (Controller)dochtml.getControllerQueue().get("BasicController");
                 }
                 else
                 {
                     controller = new BasicController(dochtml);
                     dochtml.getControllerQueue().put("BasicController", controller);
                 }
             }
        }
        if(controller == null)
        {
            controller = dochtml.getController();
        }        
        return controller;
    }     
    /**
     * Devolve o controlador do workflow.
     * @param dochtml , docHTML no contexto.
     * @return XwfController, controlador workflow.
     */            
    private static Controller getXwfController(docHTML dochtml) throws boRuntimeException
    {
        Controller controller = null;
        HttpServletRequest request = dochtml.getEboContext().getRequest();         
        String runtimeProgramBoui = request.getParameter(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY);
        
        if(dochtml.getController() == null || !XwfKeys.CONTROLLER_NAME_KEY.equals(dochtml.getController().getName()))
        {
            if(dochtml.getControllerQueue().containsKey(XwfKeys.CONTROLLER_NAME_KEY))
            {
                controller = (Controller)dochtml.getControllerQueue().get(XwfKeys.CONTROLLER_NAME_KEY);
            }
            else
            {        
                if(runtimeProgramBoui != null && !"".equals(runtimeProgramBoui))
                {
                    controller = new XwfController(dochtml,runtimeProgramBoui);
                }
                else
                {
                    controller = new XwfController(dochtml);   
                }
                dochtml.getControllerQueue().put(XwfKeys.CONTROLLER_NAME_KEY,controller);
            }
        }
        if(controller == null)
        {
            controller = dochtml.getController();
        }        
        return controller;
    }    
    /**
     * Devolve o controlador pedido caso exista na queue de controladores.
     * @param dochtml , docHTML no contexto.
     * @param controllerName , nome do controllador pretendido.
     * @return Controller, controlador pretendido, caso não exista devolve null.
     */  
    public static Controller getControllerByForce(docHTML dochtml,String controllerName)
    {
        return (Controller)dochtml.getControllerQueue().get(controllerName);        
    }
    /**
     * Define o controlador no dochtml, verifica se já existe , 
     * se é omesmo que esta ser utilizado e se existe na queue de controladores.
     * @param dochtml , docHTML no contexto.
     * @param controller , o Controller pretendido.
     */    
    public static void setControllerByForce(docHTML dochtml, Controller controller)  throws boRuntimeException
    {                
         if(dochtml.getController() == null)
         {
             if(!dochtml.getControllerQueue().contains(controller))
             {
                dochtml.setController(controller);
                dochtml.getControllerQueue().put(controller.getName(),controller);
             }
             else
             {
                dochtml.setController(controller); 
             }
         }
         else if(dochtml.getController() != controller)
         {
             if(!dochtml.getControllerQueue().contains(controller))
             {
                dochtml.setController(controller);
                dochtml.getControllerQueue().put(controller.getName(),controller);
             }
             else
             {
                dochtml.setController(controller); 
             }
         }
         else
         {
             if(!dochtml.getControllerQueue().contains(controller))
             {
                dochtml.getControllerQueue().put(controller.getName(),controller);
             }             
         }
    }    
}