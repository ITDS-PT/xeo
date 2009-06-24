package netgest.bo.presentation.render.elements;
import netgest.bo.runtime.EboContext;
import netgest.bo.def.boDefHandler;
import netgest.utils.ngtXMLHandler;

public class ObjectColumn extends ColumnProvider
{
    public ObjectColumn( ngtXMLHandler attr , boDefHandler bodef, EboContext boctx )
    {
        explorerAtt = new ExplorerAttribute(attr , bodef, boctx );
    }
}