/*Enconding=UTF-8*/
package netgest.bo.runtime;
import netgest.bo.def.boDefAttribute;

public class boDynBridgeMasterAttribute extends boBridgeMasterAttribute 
{
    public boDynBridgeMasterAttribute( boObject parent, boDefAttribute def, String bridgeName )
    {
        super(parent, def, bridgeName );
    }

    public boolean valid() throws boRuntimeException
    {
        return true;
    }
}