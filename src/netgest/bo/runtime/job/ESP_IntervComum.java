package netgest.bo.runtime.job;
import netgest.bo.runtime.*;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class ESP_IntervComum implements BoJob
{
    long boui = -1;
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public ESP_IntervComum()
    {
    }
    
    public void run(EboContext ctx) throws boRuntimeException
    {
        boObject obj = boObject.getBoManager().createObject(ctx,"ESP_PedidoInterv");
        obj.getAttribute("grau").setValueString("C");
        obj.getAttribute("userCreated").setValueString("1", AttributeHandler.INPUT_FROM_USER);
        boui = obj.getBoui();
    }
    
    public String getNextUrl(EboContext ctx) throws boRuntimeException
    {        
        return "esp_pedidointerv_generaledit.jsp?method=edit&boui="+boui;
    }
}