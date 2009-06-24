/*Enconding=UTF-8*/
package netgest.bo.events;

import java.math.BigDecimal;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.BridgeObjAttributeHandler;
import netgest.bo.runtime.ObjAttHandler;
import netgest.bo.runtime.boBridgeRow;
import netgest.bo.runtime.boEvent;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.impl.*;
import netgest.utils.ClassUtils;


public final class sysBridges
{
    private static boolean onEvent = false;

    public sysBridges()
    {
    }

    
    public static final void changedTOinGuinding(ObjAttHandler attr, boEvent event)
    throws boRuntimeException
    {
        long value = attr.getValueLong();
        boObject targetobj = attr.getParent();
        long v =targetobj.getAttribute("assignedQueue").getValueLong(); 
        targetobj.getAttribute("assignedQueue").setValueLong(value); 
    }
    public static final void changedAssignedQueue(ObjAttHandler attr, boEvent event)
        throws boRuntimeException
    {
        boObject targetobj = attr.getParent();
        if("assignedQueue".equals(attr.getName()) && 
        attr.getInputType() != AttributeHandler.INPUT_FROM_DEFAULT &&
        targetobj != null)

        {
            boObject[] parents = targetobj.getParents();
            boObject auxParent;
            bridgeHandler bh;
            int row;
            for (int i = 0; i < parents.length; i++) 

            {
                auxParent = parents[i];
                bh = auxParent.getBridge("DAO");
                if(bh != null)

                {
                    row = bh.getRow();
                    bh.beforeFirst();
                    while(bh.next())

                    {
                        if(bh.getObject().getBoui() == targetobj.getBoui())
                        {
                            if(bh.getAttribute("DAO_assignedQueue") != null)
                            {
                                bh.getAttribute("DAO_assignedQueue").setValueObject(attr.getValueObject(), AttributeHandler.INPUT_FROM_INTERNAL);


                            }
                        }
                    }

                    bh.moveTo(row);
                }
            }
        }
    }
    
    public static final void afterAddGuiding(boObject xobj, boEvent event)
    throws boRuntimeException
    {
     
     if ((event.getValue() != null) && (xobj.getMode() != boObject.MODE_EDIT_TEMPLATE))
        {
            boObject activity = boObject.getBoManager().loadObject(
                    xobj.getEboContext(), (( BigDecimal ) event.getValue()).longValue());

            if (activity.getName().equals("Ebo_Template"))
            {
                boObject newobj = ((Ebo_TemplateImpl ) activity).loadTemplate(xobj, null);
                (( ObjAttHandler ) event.getSourceObject()).setValue(newobj);
                activity = newobj;
            }

            
        }
    }
    public static final void changedDAOAssignedQueue(BridgeObjAttributeHandler attr, boEvent event)

        throws boRuntimeException
    {
        boObject object = attr.getParent();
        if(object.getMode() != boObject.MODE_EDIT_TEMPLATE)
        {        

            bridgeHandler dao = object.getBridge("DAO");
            int rowSave = dao.getRow();
            dao.moveTo(attr.getLine());
            if ( event.getNewValue() !=null )

            {
            if(dao.getAttribute("DAO_assignedQueue").getInputType() == AttributeHandler.INPUT_FROM_USER)
            {
                
                boObject obj = dao.getObject();
                
                if(dao.getObject() != null && dao.getObject().getAttribute("assignedQueue") != null)
                {
                    if(!ClassUtils.isEqual(dao.getObject().getAttribute("assignedQueue").getValueObject(),
                                dao.getAttribute("DAO_assignedQueue").getValueObject()
                        )
                    )                    
                    {
                        dao.getObject().getAttribute("assignedQueue").setValueObject(dao.getAttribute("DAO_assignedQueue").getValueObject());
                        dao.getObject().getAttribute("assignedQueue").setInputType(AttributeHandler.INPUT_FROM_INTERNAL);










                    }
                }
            }
            }
            dao.moveTo(rowSave);
        }
    }

   

    
  
    public synchronized static final boolean addToObjConfig(BridgeObjAttributeHandler attr, boEvent event)
        throws boRuntimeException
    {
        /*
         * A primeira vez que entra neste metodo vai retornar false.
         * É só para analisar qual o DAO_code da dependencia que estamos a adicionar
         * 
         */
      
        try
        {
            if (!onEvent)
            {
                long xboui = ((BigDecimal) event.getValue()).longValue();

                boObject o = boObject.getBoManager().loadObject(attr.getParent().getEboContext(), xboui);
                if ( o.getName().equals("Ebo_DAOdepends") )
                {
                    return true;
                }
//                if( o.getParentBridgeRow() == null ) return true;

                bridgeHandler bridge = o.getParentBridgeRow().getBridge();

                bridge.beforeFirst();

                while (bridge.next() )
                {
                    if (bridge.getObject() == o)
                    {
                        break;
                    }
                }

                //     boObject o = attr.getObject();
                bridgeHandler bridge2 = attr.getBridge();
                onEvent = true;

                boObject o2 = bridge2.addNewObject();
                onEvent = false;

                String xcode = bridge.getAttribute("code").getValueString();
                o2.getAttribute("DAO_code").setValueString(xcode);

                return false;
            }
            else
            {
                return true;
            }
        }
        finally
        {
            onEvent = false;
        }
    }

    public synchronized static final boolean addToObjConfigExtAtt(BridgeObjAttributeHandler attr, boEvent event)

        throws boRuntimeException
    {
        try
        {
            if (!onEvent)
            {
                long xboui = ((BigDecimal) event.getValue()).longValue();
                boObject o = boObject.getBoManager().loadObject(attr.getParent().getEboContext(), xboui);


                boObject DAO_Config = attr.getParent();
                boBridgeRow bridgeDAO = DAO_Config.getParentBridgeRow();
                if( bridgeDAO == null )
                {
                    return true; // não tem pai .. está no caso provavel de estar atrocar as posicoes dos extended attributos
                }
                boObject ActionObject = bridgeDAO.getParent();

                boObject extAttr = null;
                bridgeHandler bridge = ActionObject.getBridge("extendAttribute");
                bridge.beforeFirst();
                boolean found=false;
                while (bridge.next() )
                {
                    extAttr = bridge.getObject();

                    if (extAttr == o)
                    {
                        found=true;
                        break;
                    }
                }

                if ( found )
                {
                    bridgeHandler bridge2 = DAO_Config.getBridge("extAtt_parameter");
                    onEvent = true;
    
                    boObject parameter = bridge2.addNewObject();
                    onEvent = false;
    
                    String shortAlias = extAttr.getAttribute("shortAlias").getValueString();
                    parameter.getAttribute("extAttrAliasParent").setValueString(shortAlias);
                    parameter.getAttribute("extAttrParentBoui").setValueLong(extAttr.getBoui());
                    
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }
        finally
        {
            onEvent = false;
        }
    }

    

 
    public static final boolean existsShortName(boObject object)
        throws boRuntimeException
    {
        boolean result = true;

        /*
                String shortAlias = object.getAttribute("shortAlias").getValueString();
                boObject exists = boObject.getBoManager().loadObject(object.getEboContext(),"SELECT ebo_extendAttribute WHERE ebo_extendAttribute.shortAlias = '"+ shortAlias +"' AND ebo_extendAttribute.template ='1'");


                if(exists != null && exists.exists() && object.getBoui() != exists.getBoui() )
                {
                   //object.addErrorMessage(object.getAttribute("shortAlias").getName() + " : " + shortAlias + " já está definido.");
                   object.addErrorMessage(object.getAttribute("shortAlias"),shortAlias + " já está definido.");
                   result = false;
                }
          */
        /*
        else
        {
            boObjectList list = boObjectList.list(object.getEboContext(),"SELECT ebo_extendAttribute WHERE ebo_extendAttribute.shortAlias = '"+ shortAlias +"' AND ebo_extendAttribute.template ='0'");
            boObject toChangeObj;
            list.beforeFirst();
            while(list.next())
            {
                toChangeObj = list.getObject();
                toChangeObj.getAttribute("shortAlias").setValueString(shortAlias);
            }
        }*/
        return result;
    }



    public static final StringBuffer getAliasChildLovImpl(boObject objParameter)
        throws boRuntimeException
    {
        long bouiTypeParent = -1;
        long cardinalidadeParent = -1;
        long attributeTypeParent = -1;
        boObject actionobject = objParameter.getParentBridgeRow().getParent().getParentBridgeRow().getParent();
        String eAttParent = objParameter.getAttribute("extAttrAliasParent").getValueString();


        bridgeHandler bHandParent = actionobject.getBridge("extendAttribute"); //extend Attributes do pai
        bHandParent.beforeFirst();

        boObject extAttObj = null;
        String shortAlias;

        while (bHandParent.next())
        {
            extAttObj = bHandParent.getObject();
            shortAlias = extAttObj.getAttribute("shortAlias").getValueString();

            if (eAttParent.equals(shortAlias))
            {
                bouiTypeParent = extAttObj.getAttribute("object").getValueLong();
                cardinalidadeParent = extAttObj.getAttribute("attributeCardinal").getValueLong();
                attributeTypeParent = extAttObj.getAttribute("attributeType").getValueLong();


            }
        }

        
        boBridgeRow extendAttributesParameters=objParameter.getParentBridgeRow();
        boObject objconfig = extendAttributesParameters.getParent(); 
        boBridgeRow bH = objconfig.getParentBridgeRow(); //DAO bridge
        boObject dao = bH.getObject();
        bridgeHandler bHandler = dao.getBridge("extendAttribute"); //BridgeExtend Attributes do objecto da DAO Bridge
        java.util.ArrayList internal = new java.util.ArrayList();
        java.util.ArrayList external = new java.util.ArrayList();
        long attributeType;
        long bouiType;
        long cardinalidade;
        bHandler.beforeFirst(); //

        while (bHandler.next())
        {
            boObject objHandler = bHandler.getObject();
            
            bouiType = objHandler.getAttribute("object").getValueLong();
            cardinalidade = objHandler.getAttribute("attributeCardinal").getValueLong();
            attributeType = objHandler.getAttribute("attributeType").getValueLong();
            
            if ((attributeTypeParent == attributeType))            


            {
                if((attributeType == 0))
                {
                    if((bouiTypeParent == bouiType) && (cardinalidadeParent == cardinalidade))

                    {
                        internal.add(new StringBuffer(objHandler.getAttribute("shortAlias").getValueString()));
                        external.add(new StringBuffer(objHandler.getAttribute("alias").getValueString()));     
                    }               



                }
                else
                {
                    internal.add(new StringBuffer(objHandler.getAttribute("shortAlias").getValueString()));
                    external.add(new StringBuffer(objHandler.getAttribute("alias").getValueString()));                     


                }
            }
        }

        StringBuffer[] xInternal = new StringBuffer[internal.size() + 1];
        StringBuffer[] xExternal = new StringBuffer[external.size() + 1];
        xInternal[0] = new StringBuffer("");
        xExternal[0] = new StringBuffer("");

        for (int i = 1; i < (internal.size() + 1); i++)
        {
            xInternal[i] = (StringBuffer) internal.get(i - 1);
            xExternal[i] = (StringBuffer) external.get(i - 1);
        }

        StringBuffer toPrint = new StringBuffer();
        netgest.bo.dochtml.docHTML_renderFields.writeHTML_forCombo(toPrint, new StringBuffer(objParameter.getAttribute("extAttrAliasChild").getValueString()),
            new StringBuffer(objParameter.getName()+ "__" + objParameter.getBoui() + "__extAttrAliasChild"), new StringBuffer(""), 1, xExternal, xInternal, false, false, true,




            false, new StringBuffer(""), false, false, new java.util.Hashtable());

        return toPrint;
    }
}

