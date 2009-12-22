/*Enconding=UTF-8*/
package netgest.bo.def.v1;

import netgest.bo.builder.boBuildDB;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefBridge;
import netgest.bo.def.boDefMethod;
import netgest.bo.def.v1.boDefAttributeImpl;
import netgest.bo.def.v1.boDefHandlerImpl;
import netgest.bo.def.v1.boDefMethodImpl;

import netgest.utils.ngtXMLHandler;

import netgest.bo.system.Logger;


public class boDefBridgeImpl extends ngtXMLHandler implements boDefBridge
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.def.boDefBridge");

    private boDefAttribute p_atthandler;
    private boDefHandlerImpl p_fatherdef;
    private boDefHandlerImpl p_childdef;
    public  boDefAttribute[] p_attributes;
    private boDefMethodImpl[] p_methods;

    public boDefBridgeImpl(boDefAttributeImpl atthandler)
    {
        super( atthandler.getChildNode("bridge") );
        p_fatherdef     = (boDefHandlerImpl)atthandler.getBoDefHandler();
        p_childdef      = (boDefHandlerImpl)atthandler.getReferencedObjectDef();
        p_atthandler = atthandler;

        if (super.getNode() != null)
        {
            ngtXMLHandler atts = super.getChildNode("attributes");

            if (atts != null)
            {
                ngtXMLHandler[] a_atts = atts.getChildNodes();
                p_attributes = new boDefAttribute[a_atts.length];

                for (int i = 0; i < a_atts.length; i++)
                {
                    p_attributes[i] = new boDefAttributeImpl( (boDefHandlerImpl)p_atthandler.getBoDefHandler(),
                            a_atts[i].getNode());
                }
            }

            ngtXMLHandler node = super.getChildNode("Methods");

            if (node != null)
            {
                ngtXMLHandler[] mths = node.getChildNodes();

                if (mths != null)
                {
                    p_methods = new boDefMethodImpl[mths.length];

                    for (byte i = 0; i < mths.length; i++)
                    {
                        p_methods[i] = new boDefMethodImpl(p_fatherdef,
                                mths[i].getNode());
                    }
                }
            }
        }
    }
    
    public boDefAttribute[] getBridgeAttributes()
    {
        return this.p_attributes;
    }
    
    public boDefMethod[] getMethods()
    {
        return p_methods;
    }

    public boolean haveBridgeAttributes()
    {
        if( p_attributes == null )
        {
            return false;
        }
        else
        {
            if( p_attributes.length == 0 )    
            {
                return false;
            }
            else if ( p_attributes.length == 1 && p_attributes[0].getName().equals("LIN") )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        
    }

    public String getName()
    {
        return this.p_atthandler.getName();
    }

    public String getBoPhisicalMasterTable()
    {
        //return super.getChildNodeText("tablename",p_atthandler.getBoDefHandler().getBoName()+"$"+p_atthandler.getReferencedObjectDef().getBoName());
        String x1 = p_atthandler.getBoDefHandler().getBoPhisicalMasterTable();
        String x2 = p_atthandler.getName();

        //if(x1.compareToIgnoreCase(x2)>0)
        return boBuildDB.encodeObjectName(x1 + "$" + x2);

        //else 
        //return x2+"$"+x1;
    }

    public String getBoMasterTable()
    {
        String aux = getBoPhisicalMasterTable();

        if (aux.length() <= 29)
        {
            return "O" + aux;
        }
        else
        {
            return "O" + boBuildDB.encodeObjectName_25(aux);
        }
    }

    public boDefAttribute[] getBoAttributes()
    {
        return p_attributes;
    }

    public String getFatherFieldName()
    {
        return "PARENT$";
    }

    public String getChildFieldName()
    {
        if( p_atthandler.getRelationType() == boDefAttribute.RELATION_1_TO_N_WBRIDGE )
        {
            return "CHILD$";
        }
        else
        {
            return "BOUI";
        }
    }

    public String OLDgetFatherFieldName()
    {
        if ((p_fatherdef == null) || (p_fatherdef.getBoName() == null) ||
                (p_childdef == null) || (p_childdef.getBoName() == null))
        {
            logger.finer(this.p_atthandler.getReferencedObjectName());
            logger.finer(this.p_atthandler.getName());
            logger.finer("Null ");
        }

        if (p_fatherdef.getBoName().equals(p_childdef.getBoName()))
        {
            return p_fatherdef.getBoName() + "$0";
        }

        return p_fatherdef.getBoName() + "$";
    }

    public String OLDgetChildFieldName()
    {
        boDefHandlerImpl xtopmost = p_fatherdef;

        while ((xtopmost.getBoSuperBo() != null) &&
                (xtopmost.getBoSuperBo().length() > 0) &&
                (boDefHandlerImpl.getBoDefinition(xtopmost.getBoSuperBo()) != null))
        {
            xtopmost = (boDefHandlerImpl)boDefHandlerImpl.getBoDefinition( xtopmost.getBoSuperBo());
        }

        if (xtopmost.getBoName().equals(p_childdef.getBoName()))
        {
            return p_childdef.getBoName() + "$1";
        }

        return p_childdef.getBoName() + "$";
    }

    public boolean hasAttribute(String attributeName)
    {
        for (int i = 0; (p_attributes != null) && (i < p_attributes.length);
                i++)
        {
            if (p_attributes[i].getName().equalsIgnoreCase(attributeName))
            {
                return true;
            }
        }

        return false;
    }

    public byte getAttributeType(String attributeName)
    {
        for (int i = 0; i < p_attributes.length; i++)
        {
            if (p_attributes[i].getName().equalsIgnoreCase(attributeName))
            {
                return p_attributes[i].getAtributeType();
            }
        }

        return -1;
    }

    public boDefAttribute getAttributeRef(String attributeName)
    {
        for (int i = 0;p_attributes != null && i < p_attributes.length; i++)
        {
            if (p_attributes[i].getName().equalsIgnoreCase(attributeName))
            {
                return p_attributes[i];
            }
        }

        return null;
    }

    // Not implemented in this context
    public String getType()
    {
        return null;
    }

    // Not implemented in this context
    public String getDbName()
    {
        return null;
    }

    public String getExtendsClass()
    {
        return super.getChildNodeText("classextends", null);
    }

    public String getClassName()
    {
        String ret = super.getChildNodeText("classname", null);

        if (ret == null)
        {
            return "Handler" + this.getName();
        }

        return ret;
    }
}
