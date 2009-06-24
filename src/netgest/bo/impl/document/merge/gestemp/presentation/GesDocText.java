package netgest.bo.impl.document.merge.gestemp.presentation;


public class GesDocText extends GesDocObj
{
    private boolean longText; 
    public GesDocText(GesDocViewer clfViewer, long gesDocboui, String internalName, String name, boolean longText, boolean required, String validation)
    {
        this.name = name;
        this.longText = longText;
        this.required = required;
        this.gesDocBoui = gesDocboui;
        this.validation = validation;
        this.clfViewer = clfViewer;
        this.internalName = internalName;
    }
    
    public boolean isLong()
    {
        return this.longText;
    }
    
    public boolean isRequired()
    {
        return this.required;
    }
}