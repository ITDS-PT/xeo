/*Enconding=UTF-8*/
package netgest.bo.plugins.data;
import netgest.bo.plugins.data.MapType2Def.ObjectDS;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

public interface IDataType2Writer 
{
    public void writeObject( ObjectDS ds, boObject object ) throws boRuntimeException;
}