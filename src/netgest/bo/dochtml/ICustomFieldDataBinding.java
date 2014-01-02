package netgest.bo.dochtml;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;

public interface ICustomFieldDataBinding extends ICustomField
{

    public void processRequestData( EboContext eboctx, String id, String value );
    
    public String getHtmlInputName( AttributeHandler oAtt );
}