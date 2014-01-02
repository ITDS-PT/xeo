package netgest.bo.def;

public interface boDefViewerCategory 
{
    public String getLabel();
    
    public String getDescription();
    
    public String getTooltip();
    
    public boDefAttribute[] getAttributes();
    
    public String[] getAttributesName();
    
    public String   getName();
    
    public boDefViewerCategory[] getChildCategories();
    
    public boDefViewerCategory   getParent();
}