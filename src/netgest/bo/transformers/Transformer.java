/*Enconding=UTF-8*/
package netgest.bo.transformers;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public interface Transformer 
{
    public String transformsTo();
    public long transform(EboContext ctx, boObject parentObject, long objToTransform) throws netgest.bo.runtime.boRuntimeException;
}