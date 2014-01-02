package netgest.bo.runtime.job;
import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public interface BoJob 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public void run(EboContext ctx) throws boRuntimeException;
    public String getNextUrl(EboContext ctx) throws boRuntimeException;
}