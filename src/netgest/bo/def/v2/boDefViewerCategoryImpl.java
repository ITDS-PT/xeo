package netgest.bo.def.v2;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefViewer;
import netgest.bo.def.boDefViewerCategory;
import netgest.utils.ngtXMLHandler;
import org.w3c.dom.Node;

public class boDefViewerCategoryImpl implements boDefViewerCategory 
{
    private Node                xmlNode;
    private ngtXMLHandler       xmlHndl;
    
    private boDefViewer         viewer;
    private boDefAttribute[]    attributes;
    private String[]            attsStr;

    private boDefViewerCategoryImpl[] childCatgories;
    private boDefViewerCategoryImpl   parent;

    private String  name;
    private String  label;
    private String  tooltip;
    private String  description;
    

    public boDefViewerCategoryImpl( boDefAttribute att, boDefViewer viewer )
    {
        this( null, null, viewer, att );
    }
    
    public boDefViewerCategoryImpl( boDefViewerCategoryImpl parent, Node xmlNode, boDefViewer viewer )
    {
        this( parent, xmlNode, viewer, null );
    }

    public boDefViewerCategoryImpl( Node xmlNode, boDefViewer viewer )
    {
        this( null, xmlNode, viewer, null );
    }

    private boDefViewerCategoryImpl( boDefViewerCategoryImpl parent, Node xmlNode, boDefViewer viewer, boDefAttribute att )
    {
        this.xmlHndl = new ngtXMLHandler( xmlNode );
        this.viewer  = viewer;
        this.xmlNode = xmlNode;
        this.parent  = parent;
        
        if( xmlNode != null )
        {
            this.name    = this.xmlHndl.getAttribute("name");    
        }
        else
        {
            this.name    = att.getName();
        }
        
        // Check if the category attribute is an object atribute or a category attribute.
        if( att != null ) parseAttribute( att );
        else if( xmlNode != null ) parseXml();
        else parseNull();
    }

    public void parseNull()
    {
        this.label = "";
        this.description = "";
        this.tooltip = "";
        attributes = new boDefAttribute[0];
        attsStr = new String[0];
    }

    public void parseAttribute( boDefAttribute att )
    {
        this.label          = att.getLabel();
        this.description    = att.getDescription();
        this.tooltip        = att.getTooltip();
        
        attributes = new boDefAttribute[1];
        attributes[ 0 ] = att;
        
        attsStr = new String[1];
        attsStr[ 0 ]    = att.getName();
    }
    
    public void parseXml()
    {
        // Parse category attributes
        ngtXMLHandler child=xmlHndl.getChildNode("attributes");
        if ( child != null )
        {
            ngtXMLHandler[] childs=child.getChildNodes();
            attsStr=new String[ childs.length ];
            attributes = new boDefAttribute[ childs.length ];
            for (int i = 0; i <  childs.length ; i++) 
            {
                attsStr[ i ]    = childs[i].getNodeName();
                attributes[ i ] = viewer.getBoDefHandler().getAttributeRef( attsStr[ i ] );
            }
            
            ngtXMLHandler childCat = xmlHndl.getChildNode("categories");
            if( childCat != null )
            {
                ngtXMLHandler[] childCats = childCat.getChildNodes();
                if( childCats != null )
                {
                    childCatgories = new boDefViewerCategoryImpl[ childCats.length ];
                    for (int i = 0;childCats != null &&  i < childCats.length; i++)
                    {
                        childCatgories[i] = new boDefViewerCategoryImpl( this, childCats[i].getNode(), viewer );
                    }
                }
            }
            
        }
        else
        {
            attsStr     = new String[0];
            attributes  = new boDefAttribute[0];
        }
        
        // Parse SubCatgories;
        
        // Parse Category Label
        label           = this.xmlHndl.getAttribute("label");
        description     = this.xmlHndl.getAttribute("description");
        tooltip         = this.xmlHndl.getAttribute("tooltip");
    }

    public String getLabel()
    {
        return label;
    }

    public String getDescription()
    {
        return description;
    }

    public String getTooltip()
    {
        return tooltip;
    }

    public String[] getAttributesName()
    {
        return attsStr;
    }

    public boDefAttribute[] getAttributes()
    {
        return attributes;
    }
    
    public String getName()
    {
        return name;
    }
    
    public boDefViewerCategory[] getChildCategories()
    {
        return childCatgories;        
    }
    
    public boDefViewerCategory getParent()
    {
        return parent;
    }
    
}