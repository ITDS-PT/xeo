package netgest.bo.impl.document.print;
import java.io.File;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

public class PrintQueueJobItemEvents 
{
    public static void onBeforeSave( boObject object ) throws boRuntimeException
    {
        if( !object.exists() )
        {
            boObject obj = object.getAttribute("job").getObject();
            long pages = obj.getAttribute("pages").getValueLong();            
            obj.getAttribute("pages").setValueLong( pages + 1 );
        }
    }

    public static boolean onAfterDestroy( boObject queueJobItem ) throws boRuntimeException
    {
        String sfile = queueJobItem.getAttribute( "targetFile" ).getValueString();

        boObject obj = queueJobItem.getAttribute("job").getObject();
        if(  obj != null )
        {
            long pages = obj.getAttribute("pages").getValueLong();            
            obj.getAttribute("pages").setValueLong( pages - 1 );
        }

        if( !"".equals( sfile ) )
        {
            File file = new File( sfile );
            file.delete();
        }
        return true;
    }

}