/*Enconding=UTF-8*/
package netgest.bo.def;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectState;

import netgest.utils.ngtXMLHandler;

public interface boDefClsState extends boDefAttribute
{

    public static final String METHOD_FIRSTOCCURS="firstoccurs";
    public static final String METHOD_LASTOCCURS="lastoccurs";
    public static final String METHOD_PARALLEL="parallel";

    public static final byte TYPE_GROUPSTATES=0;
    public static final byte TYPE_STATEATTRIBUTE=1;
    
    public boObjectState getStateManager( boObject object);
    
    public String getMethod();
    
    public boDefClsState getParent();
    
    public String getDescription();
    
//    public String getQuery();
    
    public int getNumericForm();
    
    public String getName();
    
    public boDefClsState[] getChildStates();
    
    public boDefClsState getChildState(String statename);
    
//    public byte getStateType();
    
    public String getType();
    
    public boolean getIsDefault();

    public boDefClsState getChildStateAttributes(String name);
    
    public boDefClsState[] getChildStateAttributes();
    
    // Not implemented in this context
    public String getFatherFieldName();

    // Not implemented in this context
    public String getChildFieldName();
    // Not implemented in this context
    public String getBoMasterTable();

    // Not implemented in this context
    public boolean hasAttribute(String attributeName);

    // Not implemented in this context
    public byte getAttributeType(String attributeName);

    // Not implemented in this context
    public boDefAttribute getAttributeRef(String attributeName);

    // Not implemented in this context
    public boDefAttribute[] getBoAttributes();
    // Not implemented in this context
    public String getDbName();

    public String getTransform();

    public String getTooltip();

    public byte getRelationType();

    public String getReferencedObjectName();
    
    public boDefHandler getReferencedObjectDef();

    public byte getPermissions();
    public int getMinOccurs();
    public int getMaxOccurs();

    public String getMask();
    
    public boolean getLOVrequired();
    public String getLabel();
    
    public String getLabelAction();

    public String getLOVName();

    public String getGUIindividual();

    public String getGUIgroup();

//    public ngtXMLHandler[] getDefaultValues();

    public boDefBridge getBridge();

    public boDefHandler getBoDefHandler();

    public byte getAtributeType();
    
    public String[] getAllStateMethods();
    
}