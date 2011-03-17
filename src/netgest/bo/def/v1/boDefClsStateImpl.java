/*Enconding=UTF-8*/
package netgest.bo.def.v1;

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
import netgest.bo.def.boDefXeoCode;
import netgest.bo.def.v1.boDefAttributeImpl;
import netgest.bo.def.v1.boDefHandlerImpl;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectState;

import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;

import org.w3c.dom.Node;

public class boDefClsStateImpl extends boDefAttributeImpl implements boDefClsState, boDef 
{
    private boDefHandlerImpl    p_bodef;
    private boDefClsStateImpl[] p_childstates;
    private byte p_type;
    private boDefClsState p_parent;

    public static final String METHOD_FIRSTOCCURS="firstoccurs";
    public static final String METHOD_LASTOCCURS="lastoccurs";
    public static final String METHOD_PARALLEL="parallel";

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
                XMLDocument doc = ngtXMLUtils.loadXMLFile(boconf.getDeploymentDir()+statename+"$state.xml" );
                boDefHandlerImpl def = new boDefHandlerImpl(doc,true,true);
                def.refresh();
                p_cachedef.put(statename,def.getBoClsState());
                ret = (boDefClsStateImpl)def.getBoClsState();
            }
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(MessageLocalizer.getMessage("ERROR_READING_XML_OF")+" ["+statename+"]\n"+e.getClass().getName()+"\n"+e.getMessage());
        }
    }
    public boDefClsStateImpl(boDefHandlerImpl bodef,Node x,boDefClsState parent) {
        super(bodef,x);
        p_bodef=bodef;
        p_parent=parent;
        parse();
    }
    private void parse() {
        ngtXMLHandler[] nodes =  super.getChildNodes();
        LinkedList linked = new LinkedList();
        p_type = TYPE_GROUPSTATES;
        for(int i=0;i<nodes.length;i++) {
            if("config".indexOf(nodes[i].getNodeName())==-1) {
                linked.add(new boDefClsStateImpl(p_bodef,nodes[i].getNode(),this));
            } else {
                p_type = TYPE_STATEATTRIBUTE;
            }
        }
        p_childstates =  (boDefClsStateImpl[])linked.toArray(new boDefClsStateImpl[(0)]);
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
                
                Class xcls = Class.forName(name);
                
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
    
    
    public String getMethod() {
        return super.getAttribute("method",METHOD_LASTOCCURS);
    }
    public boDefClsState getParent() {
        if( p_parent != null )
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
        return p_parent;
    }
    public String getDescription() {
        String ret = super.getNodeName();
        ngtXMLHandler node = super.getChildNode("config");
        ngtXMLHandler labnode;
        if( node != null && (labnode=node.getChildNode("label")) != null) 
        {
//        if(this.getStateType()==boDefClsState.TYPE_GROUPSTATES) {
//            return super.getNodeName();
//        } else {
            if((labnode.getChildNode(p_bodef.getBoLanguage()))!=null)
                ret = (labnode.getChildNode(p_bodef.getBoLanguage())).getText();
                
            else if (labnode.getChildNode(p_bodef.getBoDefaultLanguage())!=null)
                ret = (labnode.getChildNode(p_bodef.getBoDefaultLanguage())).getText();

            
//            return node.getChildNode("label").getText();
        }
        return ret;
//        }
    }
    public String getQuery() {
        ngtXMLHandler cfg = super.getChildNode("config");
        String ret = "";
        if(cfg!=null) {
            ret = cfg.getChildNodeText("qry","");
        }
        return ret;
    }
    public int getNumericForm(){
        ngtXMLHandler cfg = super.getChildNode("config");
        String ret = "0";
        if(cfg!=null) {
            ret = cfg.getChildNodeText("numericForm","0");
        }
        return Integer.parseInt(ret);
    }
    
    public String getName() {
        return super.getNodeName();
    }
    public boDefClsState[] getChildStates() {
        return p_childstates;
    }
    public boDefClsState getChildState(String statename) {
        boDefClsState[] xstates = this.getChildStates();
        for (byte k=0;k<xstates.length;k++)  {
            if(xstates[k].getName().equals(statename))
                return xstates[k];
        }
        return null;
    }
    public byte getStateType() {
        return p_type;
    }
    public String getType() {
        return "number";
    }
    
    public boolean getIsDefault() {
        String ret = super.getChildNode("config").getAttribute("default","no");
        return (ret.equalsIgnoreCase("yes") || ret.equalsIgnoreCase("y") || ret.equalsIgnoreCase("true"));
    }

    public boDefClsState getChildStateAttributes(String name) {
        boDefClsState[] childs = this.getChildStateAttributes();
        for (int i = 0; i < childs.length; i++)  {
            if(childs[i].getName().equals(name))
                return childs[i];
        }
        return null;
    }
    public boDefClsState[] getChildStateAttributes() {
        return (boDefClsState[])fillStateAttributes(this,true,new ArrayList()).toArray(new boDefClsState[0]);
    }
    private ArrayList fillStateAttributes(boDefClsStateImpl state,boolean build,ArrayList atts) {
        if(build && !state.getMethod().equals(boDefClsState.METHOD_PARALLEL)) {
            atts.add(state);
        } else {
            build=false;
        }
        boDefClsState[] childstates=state.getChildStates();
        for(int i=0;i<childstates.length;i++) 
        {
            fillStateAttributes( (boDefClsStateImpl)childstates[(i)] , !build , atts );
        }
        return atts;
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
    public boDefAttribute[] getBoAttributes() {
        return null;
    }

    // Not implemented in this context
    public String getDbName() {
        return this.getName().toUpperCase();
    }

    public String getTransform() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
        return "";
    }

    public String getTooltip() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
        return null;
    }

    public boDefXeoCode getRequired() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
        return null;
    }

    public byte getRelationType() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
        return 0;
    }

    public String getReferencedObjectName() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
        return null;
    }

    public boDefHandler getReferencedObjectDef() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
        return null;
    }

    public byte getPermissions() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
        return 0;
    }

    public int getMinOccurs() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
        return 1;
    }

    public int getMaxOccurs() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
        return 1;
    }

    public String getMask() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
        return "";
    }

    public boolean getLOVrequired() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
        return false;
    }

    public String getLabel() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
        return this.getDescription();
    }
    
    public String getLabelAction() {
        if(this.getStateType()==boDefClsState.TYPE_GROUPSTATES) {
            return super.getNodeName();
        } else {
            ngtXMLHandler node = super.getChildNode("config").getChildNode("labelAction");
            if ( node==null) return getLabel();
            else return node.getChildNode(p_bodef.getBoDefaultLanguage()).getText();
        }
        
       
    }

    public String getLOVName() {
        // TODO:  Override this netgest.bo.def.boDefAttribute method
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

    public ngtXMLHandler[] getDefaultValues() {
        return null;
    }

    public boDefBridge getBridge() {
        return null;
    }

    public boDefHandler getBoDefHandler() {
        return super.getBoDefHandler();
    }

    public byte getAtributeType() {
        return boDefAttribute.TYPE_STATEATTRIBUTE;
    }
    
    private Vector retriveAllStateMethods(Vector s, ngtXMLHandler[] xmlh)
    {
          for (int i = 0; i < xmlh.length; i++) 
          {
              boolean descend = true;
              
              if(xmlh[i].getChildNode("labelAction")!=null && (xmlh[i].getAttribute("reg","N").equalsIgnoreCase("Y") || xmlh[i].getAttribute("reg","N").equalsIgnoreCase("YES")))
              {
                s.add(xmlh[i].getChildNode("labelAction").getChildNode("pt").getText());
              }              
              
              retriveAllStateMethods(s, xmlh[i].getChildNodes());
          }
          
          return s;
      
    }
    
    
      public String[] getAllStateMethods()
      {
          Vector s = retriveAllStateMethods(new Vector(), super.getChildNodes());
      
          return (String[])s.toArray(new String[s.size()]);
      }
    
    
}