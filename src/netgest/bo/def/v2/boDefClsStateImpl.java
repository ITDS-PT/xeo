/*Enconding=UTF-8*/
package netgest.bo.def.v2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import netgest.bo.boConfig;
import netgest.bo.builder.boBuilder;
import netgest.bo.def.boDef;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefBridge;
import netgest.bo.def.boDefClsState;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.v2.boDefAttributeImpl;
import netgest.bo.def.v2.boDefHandlerImpl;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectState;

import netgest.bo.system.boApplication;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;

import org.w3c.dom.Node;

public class boDefClsStateImpl extends boDefAttributeImpl implements boDefClsState, boDef 
{
    private boDefHandlerImpl    p_bodef;
    private boDefClsStateImpl[] p_childstates;
    private boDefClsState       p_parent;
    
    private String              p_name;
    private String              p_label;
    private int                 p_value;
    private String              p_labelAction;

    public static final String METHOD_FIRSTOCCURS=  "firstoccurs";
    public static final String METHOD_LASTOCCURS=   "lastoccurs";
    public static final String METHOD_PARALLEL=     "parallel";

    public static final byte TYPE_GROUPSTATES=0;
    public static final byte TYPE_STATEATTRIBUTE=1;
    
    private boolean classnotfound=false;
    
    
    private static Hashtable p_cachedef = new Hashtable();
    
    public static void clearCache() 
    {
        p_cachedef.clear();
    }
    
    public static boDefClsStateImpl loadClsStates(String statename) 
    {
        try {
            boDefClsStateImpl ret = (boDefClsStateImpl)p_cachedef.get(statename);
            if(ret == null) {
                boConfig boconf = new boConfig();
                XMLDocument doc = ngtXMLUtils.loadXMLFile(boconf.getDeploymentDir() + statename + boBuilder.TYPE_STATE );
                boDefHandlerImpl def = new boDefHandlerImpl(doc,true,true);
                def.refresh();
                p_cachedef.put(statename,def.getBoClsState());
                ret = (boDefClsStateImpl)def.getBoClsState();
            }
            return ret;
        } catch (Exception e) {
            throw new RuntimeException("Error reading XML of ["+statename+"]\n",e);
        }
    }
    
    public boDefClsStateImpl(boDefHandlerImpl bodef,Node x,boDefClsState parent) 
    {
        super(bodef,x);
        p_bodef=bodef;
        p_parent=parent;
        parse();
    }
    
    private void parse() 
    {
    
    
        p_name          = getAttribute("name");
        p_value         = Integer.parseInt( getAttribute("value","-1") );
        p_label         = getChildNodeText("label", p_name );
        p_labelAction   = getChildNodeText("labelAction", null );

        LinkedList linked = new LinkedList();
        ngtXMLHandler[] stateNode =  super.getChildNodes();
        ngtXMLHandler optionsNode = getChildNode("options");
        if( optionsNode != null )
        {
            ngtXMLHandler[] optionNode = optionsNode.getChildNodes();
            for (int z = 0; z < optionNode.length; z++) 
            {
                linked.add( new boDefClsStateImpl( p_bodef, optionNode[z].getNode(), this  ) );
            }
        }
        ngtXMLHandler subStatesNode = getChildNode("subStates");
        if( subStatesNode != null ) 
        {
            ngtXMLHandler[] subState = subStatesNode.getChildNodes();
            for (int i = 0; i < subState.length; i++) 
            {
                linked.add( new boDefClsStateImpl( p_bodef, subState[i].getNode(), this  ) );
            }
        }
        p_childstates = (boDefClsStateImpl[])linked.toArray( new boDefClsStateImpl[ linked.size() ] );
    }


    public boObjectState getStateManager( boObject object) 
    {
        if(!classnotfound)
        {
            try
            {
                boDefHandler bodef = this.getBoDefHandler();
                String version = "v"+bodef.getBoVersion().replace('.','_');
                String name = version+"."+bodef.getName()+"StateManager";
                Class xcls = Class.forName(name,false, boApplication.currentContext().getApplication().getClassLoader() );
                Constructor con =  xcls.getConstructor(new Class[] { boObject.class } );
                Object xret = con.newInstance( new Object[] { object }  );
                return (boObjectState)xret;
            }
            catch (InvocationTargetException e)
            {
                throw new RuntimeException(e.getClass().getName()+"\n"+e.getMessage());
            }
            catch (InstantiationException e)
            {
                throw new RuntimeException(e.getClass().getName()+"\n"+e.getMessage());
            }
            catch (NoSuchMethodException e)
            {
                throw new RuntimeException(e.getClass().getName()+"\n"+e.getMessage());
            }
            catch (IllegalAccessException e)
            {
//                classnotfound = true;
                throw new RuntimeException(e.getClass().getName()+"\n"+e.getMessage());
            }
            catch (ClassNotFoundException e)
            {
                classnotfound = true;
                //throw new RuntimeException(e.getClass().getName()+"\n"+e.getMessage());
            }
        }
        return null;
    }
    
    
    public String getMethod() 
    {
        return METHOD_LASTOCCURS;
    }
    
    public boDefClsState getParent() 
    {
        return p_parent;
/*        if( p_parent != null )
        {
            if ( p_parent.getMethod().equals( METHOD_PARALLEL ) )
            {
                return p_parent.getParent();       
            }
            else
            {
                return p_parent;
            }
        }
        return p_parent;*/
    }
    
    public String getDescription() 
    {
        return p_label;
    }
    
    public int getNumericForm()
    {
        return p_value;
    }
    
    public String getName() 
    {
        return p_name;
    }
    
    public boDefClsState[] getChildStates() 
    {
        return p_childstates;
    }
    
    public boDefClsState getChildState(String statename) 
    {
        boDefClsState[] xstates = this.getChildStates();
        for (byte k=0;k<xstates.length;k++)  {
            if(xstates[k].getName().equals(statename))
                return xstates[k];
        }
        return null;
    }
    
    public String getType() 
    {
        return "number";
    }
    
    public boolean getIsDefault() 
    {
        return true;
        //String ret = super.getChildNode("config").getAttribute("default","no");
        //return (ret.equalsIgnoreCase("yes") || ret.equalsIgnoreCase("y") || ret.equalsIgnoreCase("true"));
    }

    public boDefClsState getChildStateAttributes(String name) 
    {
        boDefClsState[] childs = this.getChildStateAttributes();
        for (int i = 0; i < childs.length; i++)  {
            if(childs[i].getName().equals(name))
                return childs[i];
        }
        return null;
    }
    
    public boDefClsState[] getChildStateAttributes() 
    {
        return (boDefClsState[])fillStateAttributes(this,true,new ArrayList()).toArray(new boDefClsState[0]);
    }
    
    private ArrayList fillStateAttributes(boDefClsStateImpl state,boolean build,ArrayList atts) 
    {   
        if( build )
        {
            atts.add( state );
        }
        boDefClsState[] childstates=state.getChildStates();
        for(int i=0;i<childstates.length;i++) 
        {
            fillStateAttributes( (boDefClsStateImpl)childstates[(i)] , !build , atts );
        }
        return atts;
    }

    public boDefHandler getBoDefHandler() 
    {
        return super.getBoDefHandler();
    }

    public byte getAtributeType() 
    {
        return boDefAttribute.TYPE_STATEATTRIBUTE;
    }

    public String getLabelAction() 
    {
        return p_labelAction;
    }
    
    private Vector retriveAllStateMethods(Vector s, boDefClsState[] childStates )
    {
        for (int i = 0; i < childStates.length; i++) 
        {
            boolean descend = true;
            if( childStates[i].getLabelAction() != null )
            {
                s.add( childStates[i].getLabelAction() );
            }
            retriveAllStateMethods( s, childStates[i].getChildStates() );
        }
        return s;
    }
    
    public String[] getAllStateMethods()
    {
        Vector s = retriveAllStateMethods(new Vector(), getChildStates() );
        return (String[])s.toArray(new String[s.size()]);
    }


    // Not implemented in this context
    public String getFatherFieldName() {
        return null;
    }

    // Not implemented in this context
    public String getChildFieldName() {
        return null;
    }

    // Not implemented in this context
    public String getBoMasterTable() {
        return null;
    }

    // Not implemented in this context
    public boolean hasAttribute(String attributeName) {
        return false;
    }

    // Not implemented in this context
    public byte getAttributeType(String attributeName) {
        return boDefAttribute.TYPE_STATEATTRIBUTE;
    }

    // Not implemented in this context
    public boDefAttribute getAttributeRef(String attributeName) {
        return null;
    }

    // Not implemented in this context
    public boDefAttribute[] getBoAttributes() 
    {
        return null;
    }

    // Not implemented in this context
    public String getDbName() {
        return this.getName().toUpperCase();
    }

    public String getTransform() {
        return "";
    }

    public String getTooltip() 
    {
        return "";
    }

    public byte getRelationType() 
    {
        return boDefAttribute.NO_RELATION;
    }

    public String getReferencedObjectName() {
        return null;
    }

    public boDefHandler getReferencedObjectDef() 
    {
        return null;
    }

    public byte getPermissions() 
    {
        return 0;
    }

    public int getMinOccurs() 
    {
        return 0;
    }

    public int getMaxOccurs() 
    {
        return 1;
    }

    public String getMask() {
        return "";
    }

    public boolean getLOVrequired() 
    {
        return false;
    }

    public String getLabel() 
    {
        return this.getDescription();
    }
    
    public String getLOVName() 
    {
        return null;
    }

    public String getGUIindividual() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
        return null;
    }

    public String getGUIgroup() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
        return null;
    }

    public boDefBridge getBridge() 
    {
        return null;
    }
    
    public String getBridgeFilter()
    {
        return null;
    }
    
    public byte getValueType()
    {
        return boDefAttribute.VALUE_NUMBER;
    }

}