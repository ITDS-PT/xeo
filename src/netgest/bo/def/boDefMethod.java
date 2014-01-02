/*Enconding=UTF-8*/
package netgest.bo.def;

import org.w3c.dom.Node;


/**
 * 
 * Metadata about a XEO Object Method 
 *
 */
public interface boDefMethod
{

    /**
     * 
     * Retrieves the name of the method
     * 
     * @return
     */
    public String getName();

    public boolean getRequireTransaction();

    public String[] getAssinatureClassNames();

    public Class[] getAssinatureClasses();

    public String[] getAssinatureArgNames();

    public boolean getIsNative();

    public boolean getIsNativeOverwrited();

    public boolean getIsMenu();

    /**
     * 
     * Whether the method should be displayed in the object's
     * toolbar (when editing an instance)
     * 
     * @return True if the method should be displayed in the toolbar
     * and false otherwise
     */
    public boolean getIsToolbar();

    public String getBody();

    public String getReturnType();

    /**
     * 
     * Retrieve the label of the method
     * 
     * @return
     */
    public String getLabel();
    
    /**
     * 
     * Get the definition of code that will be evaluated
     * to determine if the Method should be hidden or not
     * 
     * @return
     */
    public boDefXeoCode getHiddenWhen();

    public String getJavaScriptToRunBefore(String viewerName);

    public String getJavaScriptToRunAfter(String viewerName);

    public boolean templateMode();

    public boolean openDoc();
    
    public String getObjectName();
    
    public boDefAttribute getParentAttribute();
    
    /**
     * 
     * Retrieves the XEO Models metadata definition
     * 
     * @return A reference to the method's parent model
     * metadata
     */
    public boDefHandler   getBoDefHandler();
    
    public Node           getNode();
    
    /**
     * 
     * Retrives the path to the icon associated with
     * this method
     * 
     * @return The path to the icon
     */
    public String getPathToIcon();
}
