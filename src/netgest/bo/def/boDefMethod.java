/*Enconding=UTF-8*/
package netgest.bo.def;

import org.w3c.dom.Node;


public interface boDefMethod
{

    public String getName();

    public boolean getRequireTransaction();

    public String[] getAssinatureClassNames();

    public Class[] getAssinatureClasses();

    public String[] getAssinatureArgNames();

    public boolean getIsNative();

    public boolean getIsNativeOverwrited();

    public boolean getIsMenu();

    public boolean getIsToolbar();

    public String getBody();

    public String getReturnType();

    public String getLabel();
    
    public boDefXeoCode getHiddenWhen();

    public String getJavaScriptToRunBefore(String viewerName);

    public String getJavaScriptToRunAfter(String viewerName);

    public boolean templateMode();

    public boolean openDoc();
    
    public String getObjectName();
    
    public boDefAttribute getParentAttribute();
    
    public boDefHandler   getBoDefHandler();
    
    public Node           getNode();
}
