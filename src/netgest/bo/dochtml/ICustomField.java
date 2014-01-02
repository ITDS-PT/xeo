package netgest.bo.dochtml;
import java.io.PrintWriter;
import netgest.bo.runtime.*;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */

 
public interface ICustomField  
{

    static final int RENDER_CONTINUE = 1;
    static final int RENDER_SKIP_KOW = 2;

    public int render( EboContext ctx, docHTML_controler doccont, docHTML doc , boObject object, PrintWriter out, AttributeHandler relatedAtt ) throws boRuntimeException; 
    public String getRelatedAttribute() throws boRuntimeException;
    
}