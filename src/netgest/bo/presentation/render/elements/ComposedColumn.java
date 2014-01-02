package netgest.bo.presentation.render.elements;

import netgest.bo.runtime.EboContext;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.utils.ngtXMLHandler;

public class ComposedColumn extends ColumnProvider
{
    public ComposedColumn( String name , String label , boDefAttribute attrdef, boDefHandler bodef, EboContext boctx )
    {
        explorerAtt = new ExplorerAttribute( name , label , attrdef, bodef, boctx );
    }
}