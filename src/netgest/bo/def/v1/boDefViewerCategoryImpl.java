package netgest.bo.def.v1;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefViewer;
import netgest.bo.def.boDefViewerCategory;
import netgest.utils.ngtXMLHandler;
import org.w3c.dom.Node;

public class boDefViewerCategoryImpl implements boDefViewerCategory 
{
    private Node                xmlNode;
    private ngtXMLHandler       xmlHndl;
    
    private boDefViewerImpl     viewer;
    private boDefAttribute[]    attributes;
    private String[]            attsStr;
    
    
    private boDefViewerCategoryImpl[] childCatgories;
    private boDefViewerCategoryImpl   parent;

    private String name;
    private String label;
    private String tooltip;
    private String description;
    

    public boDefViewerCategoryImpl( boDefAttribute att, boDefViewerImpl viewer )
    {
        this( null, viewer, att );
    }
    
    public boDefViewerCategoryImpl( Node xmlNode, boDefViewerImpl viewer )
    {
        this( xmlNode, viewer, null );
    }

    private boDefViewerCategoryImpl( Node xmlNode, boDefViewerImpl viewer, boDefAttribute att )
    {
        this.xmlNode = xmlNode;
        this.xmlHndl = new ngtXMLHandler( xmlNode );
        this.viewer  = viewer;
        this.name    = xmlNode.getNodeName();
        
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
        }
        else
        {
            attsStr     = new String[0];
            attributes  = new boDefAttribute[0];
        }
        
        
        // Parse Category Label
        label   = "";
        String  lang    = viewer.getBoDefHandler().getBoDefaultLanguage();
        ngtXMLHandler auxNode=xmlHndl.getChildNode("label");
        if(auxNode!=null) 
        {
            label=auxNode.getText();
            auxNode=auxNode.getChildNode( lang );
        }
        if(auxNode!=null) label=auxNode.getText();

        // Parse Category Description
        description     = "";
        auxNode         = xmlHndl.getChildNode("description");
        if(auxNode!=null) 
        {
            description=auxNode.getText();
            auxNode=auxNode.getChildNode( lang );
        }
        if(auxNode!=null) description=auxNode.getText();

        // Parse Category tooltip
        tooltip = "";
        auxNode = xmlHndl.getChildNode("tooltip");
        if( auxNode != null ) 
        {
            tooltip=auxNode.getText();
            auxNode=auxNode.getChildNode( lang );
        }
        if( auxNode!=null ) tooltip=auxNode.getText();

        ngtXMLHandler childCat = xmlHndl.getChildNode("categories");
        if( childCat != null )
        {
            ngtXMLHandler[] childCats = childCat.getChildNodes();
            if( childCats != null )
            {
                childCatgories = new boDefViewerCategoryImpl[ childCats.length ];
                for (int i = 0;childCats != null &&  i < childCats.length; i++)
                {
                    childCatgories[i] = new boDefViewerCategoryImpl( childCats[i].getNode(), viewer );
                }
            }
        }
        
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
    
    public boDefViewerCategory   getParent()
    {
        return parent;
    }
   
}