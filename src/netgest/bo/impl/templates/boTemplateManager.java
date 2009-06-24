/*Enconding=UTF-8*/
package netgest.bo.impl.templates;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import netgest.bo.def.boDefAttribute;
import netgest.bo.impl.Ebo_TemplateImpl;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.BridgeObjAttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boAttributesArray;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectUpdateQueue;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.utils.ClobUtils;

import netgest.utils.ClassUtils;



public final class boTemplateManager
{

    public static void saveTemplates(boObject lastobj,boObject previous_lastobj) throws boRuntimeException
    {
        saveTemplate(lastobj, previous_lastobj);
        saveTemplateChilds(previous_lastobj);
    }

    public static void setTemplatesMode(boObject lastobj,boolean forceCreation) throws boRuntimeException
    {
        setTemplateMode(lastobj,forceCreation);
        setTemplateModeChilds(lastobj,forceCreation);
    }

    public static void removeTemplate(boObject lastobj) throws boRuntimeException
    {
        removeTemplateChilds(lastobj);
        removeExtendAttributes(lastobj);
        lastobj.removeTemplate();
    }

    public static boObject applyTemplates(EboContext ctx, boObject lastobj, long applyTemplate, String poolUniqueId, HttpServletRequest request)throws boRuntimeException
    {
        lastobj = applyTemplate(ctx,lastobj,applyTemplate,poolUniqueId,request);
        applyTemplateChilds(ctx,lastobj,poolUniqueId,request);
        return lastobj;
    }

    private static void saveTemplateChilds(boObject lastobj) throws boRuntimeException
    {
        boObject parentTemplate = lastobj.getAttribute("TEMPLATE").getObject();
        if(parentTemplate != null)
        {
            boObject attObject = null;
            AttributeHandler attHandler = null;
            AttributeHandler refObject = null;
            bridgeHandler bridge = null;
            boObject[] attBoObjects = null;
            boObject template = null;

            Enumeration oEnum = lastobj.getAttributes().elements();
            while( oEnum.hasMoreElements() )
            {
                template = null;
                attHandler = (AttributeHandler)oEnum.nextElement();
                if(!"TEMPLATE".equals(attHandler.getName()) && !"PARENTCTX".equals(attHandler.getName()) && !"PARENT".equals(attHandler.getName()))
                {
                    if(attHandler.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                    {
                        if(attHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1)
                        {
                            attObject = attHandler.getObject();
                            if(attObject != null)
                            {
                                if(!attObject.getBoDefinition().getBoCanBeOrphan())
                                {
                                    template = attObject.getAttribute("TEMPLATE").getObject();
                                    if(template != null )
                                    {
                                        template.getAttribute("PARENT").setValueLong(parentTemplate.getBoui());
                                        parentTemplate.getUpdateQueue().add(template,boObjectUpdateQueue.MODE_SAVE_FORCED);
                                        saveTemplate(template,attObject);
                                        saveTemplateChilds(attObject);
                                    }
                                }
                            }
                        }
                        else if ( attHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_N )
                        {
                            bridgeHandler b = lastobj.getBridge( attHandler.getName() );
                            b.beforeFirst();
                            while ( b.next() )
                            {
                                attObject = b.getObject();
                                if(attObject != null)
                                {
                                    if(!attObject.getBoDefinition().getBoCanBeOrphan())
                                    {
                                        template = attObject.getAttribute("TEMPLATE").getObject();
                                        if(template != null )
                                        {
                                            template.getAttribute("PARENT").setValueLong(parentTemplate.getBoui());
                                            parentTemplate.getUpdateQueue().add(template,boObjectUpdateQueue.MODE_SAVE_FORCED);
                                            saveTemplate(template,attObject);
                                            saveTemplateChilds(attObject);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public static void destroyTemplateChilds(boObject template) throws boRuntimeException
    {
        boObject templateChild = null;
        long value = 0;
        bridgeHandler maps = template.getBridge("mappingAttributes");
        maps.beforeFirst();
        boObject map = null;
        while (maps.next())
        {
            map = maps.getObject();
            value = ClassUtils.convertToLong(map.getAttribute("value").getValueString());
            if(value != 0)
            {
                try
                {
                    templateChild = boObject.getBoManager().loadObject(template.getEboContext(),value);
                }
                catch (Exception ex)
                {
                }
                if(templateChild != null && templateChild.exists())
                {
                    if("Ebo_Template".equals(templateChild.getName())
                        && template.getBoui() != templateChild.getBoui())
                    {
                            destroyTemplateChilds(templateChild);
                            //templateChild.destroy();
                            template.getUpdateQueue().add(templateChild,boObjectUpdateQueue.MODE_DESTROY);

                    }
                }
            }
        }
    }
    private static void removeTemplateChilds(boObject lastobj) throws boRuntimeException
    {
        boObject attObject = null;
        AttributeHandler attHandler = null;
        AttributeHandler refObject = null;
        bridgeHandler bridge = null;
        boObject[] attBoObjects = null;
        boObject template = null;

        Enumeration oEnum = lastobj.getAttributes().elements();
        while( oEnum.hasMoreElements() )
        {
            template = null;
            attHandler = (AttributeHandler)oEnum.nextElement();
            if(!"TEMPLATE".equals(attHandler.getName()) && !"PARENTCTX".equals(attHandler.getName()) && !"PARENT".equals(attHandler.getName()))
            {
                if(attHandler.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                {
                    if(attHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1)
                    {
                        attObject = attHandler.getObject();
                        if(attObject != null)
                        {
                            if(!attObject.getBoDefinition().getBoCanBeOrphan())
                            {
                                template = attObject.getAttribute("TEMPLATE").getObject();
                                if(template != null )
                                {
                                    removeTemplateChilds(attObject);
                                    lastobj.getUpdateQueue().add(attObject,boObjectUpdateQueue.MODE_SAVE);
                                    attObject.removeTemplate();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private static void setTemplateModeChilds(boObject lastobj,boolean forceCreation) throws boRuntimeException
    {
        boObject attObject = null;
        AttributeHandler attHandler = null;
        AttributeHandler refObject = null;
        bridgeHandler bridge = null;
        boObject[] attBoObjects = null;
        boObject template = null;

        Enumeration oEnum = lastobj.getAttributes().elements();
        while( oEnum.hasMoreElements() )
        {
            template = null;
            attHandler = (AttributeHandler)oEnum.nextElement();
            if(!"TEMPLATE".equals(attHandler.getName()) && !"PARENTCTX".equals(attHandler.getName()) && !"PARENT".equals(attHandler.getName()))
            {
                if(attHandler.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                {
                    if (  attHandler.getDefAttribute().getTransformClassMap() == null )
                    {
                        if(attHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1)
                        {
                            attObject = attHandler.getObject();
                            if(attObject != null)
                            {
                                if(!attObject.getBoDefinition().getBoCanBeOrphan()  )
                                {
                                    template = attObject.getAttribute("TEMPLATE").getObject();
                                    if(template == null )
                                    {
                                        setTemplateMode(attObject,forceCreation);
                                        setTemplateModeChilds(attObject,forceCreation);
                                    }
                                }
                            }
                        }
                        else if (attHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_N)
                        {
                            bridgeHandler b = lastobj.getBridge( attHandler.getName() );
                            b.beforeFirst();
                            while ( b.next() )
                            {
                                attObject = b.getObject();
                                if(!attObject.getBoDefinition().getBoCanBeOrphan()  )
                                {
                                    template = attObject.getAttribute("TEMPLATE").getObject();
                                    if(template == null )
                                    {
                                        setTemplateMode(attObject,forceCreation);
                                        setTemplateModeChilds(attObject,forceCreation);
                                    }
                                }
                            }
                        }

                    }

                }
            }
        }
    }

    private static void applyTemplateChilds(EboContext ctx, boObject lastobj, String poolUniqueId, HttpServletRequest request)throws boRuntimeException
    {
        boObject attObject = null;
        AttributeHandler attHandler = null;
        AttributeHandler refObject = null;
        bridgeHandler bridge = null;
        boObject[] attBoObjects = null;
        boObject template = null;

        Enumeration oEnum = lastobj.getAttributes().elements();
        while( oEnum.hasMoreElements() )
        {
            template = null;
            attHandler = (AttributeHandler)oEnum.nextElement();
            if(!"TEMPLATE".equals(attHandler.getName()) && !"PARENTCTX".equals(attHandler.getName()) && !"PARENT".equals(attHandler.getName()))
            {
                if(attHandler.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                {
                    if(attHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1)
                    {
                        attObject = attHandler.getObject();
                        if(attObject != null)
                        {
                            if("Ebo_Template".equals(attObject.getName()))
                            {
                                String attName = attHandler.getDefAttribute().getType();
                                String s[] = attName.split("\\.");
                                boObject newObject = boObject.getBoManager().createObject(ctx,s[1]);
                                newObject.getAttribute("TEMPLATE").setValueLong(attObject.getBoui());
                                attObject = newObject;
                                attHandler.setValueLong(attObject.getBoui());
                            }
                            if(!attObject.getBoDefinition().getBoCanBeOrphan())
                            {
                                template = attObject.getAttribute("TEMPLATE").getObject();
                                if(template != null )
                                {
                                    applyTemplate(ctx,attObject,template.getBoui(),poolUniqueId,request);
                                    applyTemplateChilds(ctx,attObject,poolUniqueId,request);
                                    lastobj.getUpdateQueue().add(attObject,boObjectUpdateQueue.MODE_SAVE);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static boObject getTemplate(boObject parentTemplate , boObject child) throws boRuntimeException
    {
        boObject templateChild = null;
        long value = 0;
        boolean found = false;
        bridgeHandler maps = parentTemplate.getBridge("mappingAttributes");
        maps.beforeFirst();
        boObject map = null;
        while (maps.next() && !found)
        {
            templateChild = null;
            map = maps.getObject();
            value = ClassUtils.convertToLong(map.getAttribute("value").getValueString());
            if(value != 0)
            {
                try
                {
                    templateChild = boObject.getBoManager().loadObject(parentTemplate.getEboContext(),value);
                }
                catch (Exception ex)
                {
                }
                if(templateChild != null && templateChild.exists())
                {
                    if("Ebo_Template".equals(templateChild.getName())
                        && parentTemplate.getBoui() != templateChild.getBoui()
                        && parentTemplate.getAttribute("PARENT").getValueLong() != templateChild.getBoui()
                       )
                    {
                        boObject eboClsreg = templateChild.getAttribute("masterObjectClass").getObject();
                        String newName = eboClsreg.getAttribute("name").getValueString();
                        String lastClass = child.getName();
                        if(newName.equals(child.getName()))
                        {
                            found = true;
                        }
                    }
                }
            }
        }
        if(!found)
        {
            templateChild = null;
        }
        return templateChild;
    }

    private static void saveTemplate(boObject template, boObject relatedObj) throws boRuntimeException
    {
        if ( template.getName().equals("Ebo_Template") && relatedObj!=null && !relatedObj.getName().equals("Ebo_Template") )
        {
            boObject relBo=relatedObj;

            boObject tmpl=template;

            boAttributesArray xatr=relBo.getAttributes();
            Enumeration xenum=xatr.elements();
            bridgeHandler details=tmpl.getBridge("mappingAttributes");
            bridgeHandler maps=details;

            String validBouis="";
            AttributeHandler xat;
            while (xenum.hasMoreElements())
            {
                xat=(AttributeHandler)xenum.nextElement();
                if (
                    (  xat.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE ||
                    ( xat.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE &&
                    xat.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1 )
                    )
                    && !xat.getName().equalsIgnoreCase("BOUI")
                    && !xat.getName().equals("KEYS")
                    && !xat.getName().equals("SYS_DTCREATE")
                    && !xat.getName().equals("SYS_DTSAVE")
                    && !xat.getName().equals("SYS_ORIGIN")
                    && !xat.getName().equals("SYS_FROMOBJ")
                    && !xat.getName().equals("CREATOR")
                    && !xat.getName().equals("CLASSNAME")
                    && !xat.getName().equals("PARENT")
                    && !xat.getName().equals("fromRef")
                    && !xat.getName().equals("already_read")
                    && !xat.getName().equals("readyForRes")
                    && !xat.getName().equals("resId")
                    && !xat.getName().equals("forwarding")
                    && !xat.getName().equals("createdSystemOrigin")
                    && !xat.getName().equals("register")
                    && !xat.getName().equals("statusMessage")
                    && !xat.getName().equals("messageid")
                    && !xat.getName().equals("replyTo")
                    && !xat.getName().equals("replyToRef")
                    && !xat.getName().equals("XEOID")
                    && !xat.getName().equals("signedMsg")
                )
                {
                    String xvalue=xat.getValueString();
                    // Template Childs
                    if( xat.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE &&
                        xat.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1)
                    {
                            if(xvalue != null && !"".equals(xvalue))
                            {
                                boObject objAux = boObject.getBoManager().getObjectInContext(template.getEboContext(),Long.parseLong(xvalue));
                                if(objAux != null)
                                {
                                    if(!objAux.getBoDefinition().getBoCanBeOrphan())
                                    {
                                        AttributeHandler attHandler =  objAux.getAttribute("TEMPLATE");
                                        if(attHandler != null)
                                        {
                                            boObject templateAux = attHandler.getObject();
                                            if(templateAux != null)
                                            {
                                                xvalue = String.valueOf(templateAux.getBoui());
                                                //tmpl.getUpdateQueue().add(templateAux,boObjectUpdateQueue.MODE_SAVE_FORCED);
                                            }
                                        }
                                    }
                                }
                            }
                    }

                    if("clob".equalsIgnoreCase(xat.getDefAttribute().getType()))
                    {
                        //clob
                        if(xvalue != null)
                        {
                            if(ClobUtils.isEmpty(xat))
                            {
                                xvalue = null;
                            }
                        }
                    }

                    boolean find=false;
                    maps.beforeFirst();
                    boObject map=null;
                    while ( !maps.isEmpty() && maps.next() )
                    {
                        map=maps.edit().getObject();
                        if (map.getAttribute("objectAttributeName").getValueString().equalsIgnoreCase(xat.getName()))
                        {
                            find=true;
                            map.getAttribute("value").setValueString(xvalue);
                            if(xvalue == null)
                            {
                                map = null;
                            }
                            break;
                        }
                        if(!find)
                        {
                            map=null;
                        }
                    }
                    if ( xvalue != null && !xvalue.equals("") )
                    {
                        if (! find )
                        {
                            map=details.addNewObject("Ebo_Map");
                            map.getAttribute("value").setValueString(xvalue);
                            map.getAttribute("objectAttributeName").setValueString(xat.getName());
                            if ( xat.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                            {
                                boObject obji = relBo.getBoManager().getObjectInContext( relBo.getEboContext(), ClassUtils.convertToLong( xvalue )  );
                                if( obji != null && !obji.exists())
                                {
                                    tmpl.getUpdateQueue().add( obji , boObjectUpdateQueue.MODE_SAVE_FORCED );
                                }
                            }
                        }
                    }
                    if(map!=null) validBouis+=(","+ map.bo_boui );
                }
            }

            // Create the mapping objects of the bridges.
            Enumeration bridges = relBo.getBridges().elements();
            while ( bridges.hasMoreElements()  )
            {
                bridgeHandler bridge = (bridgeHandler)bridges.nextElement();
                if ( !bridge.getDefAttribute().getName().equalsIgnoreCase("extendAttribute")
                     && !bridge.getDefAttribute().getName().equalsIgnoreCase("DAO")
                     && !bridge.getDefAttribute().getName().equalsIgnoreCase("READLIST")
                     && !bridge.getDefAttribute().getName().equalsIgnoreCase("KEYS")
                     && !bridge.getDefAttribute().getName().equalsIgnoreCase("WORKHISTORY")
                     && !bridge.getDefAttribute().getName().equalsIgnoreCase("toRef")
                     && !bridge.getDefAttribute().getName().equalsIgnoreCase("ccRef")
                     && !bridge.getDefAttribute().getName().equalsIgnoreCase("bccRef")
                     && !bridge.getDefAttribute().getName().equalsIgnoreCase("attachedObjects")
                     && !bridge.getDefAttribute().getName().equalsIgnoreCase("binaryDocuments")
                     && !bridge.getDefAttribute().getName().equalsIgnoreCase("fwdObjects")
                    )
                {
                    bridge.beforeFirst();

                    while( bridge.next() )
                    {
                        AttributeHandler xatt = bridge.getAttribute( bridge.getDefAttribute().getName() );

                        if( xatt != null )
                        {
                            long refobj = xatt.getValueLong();
                            if(refobj!=0)
                            {
                                maps=tmpl.getBridge("mappingAttributes");
                                boolean find=false;
                                maps.beforeFirst();
                                boObject map=null;
                                while ( maps.next() )
                                {
                                    // map=maps.edit().getObject();
                                    map=maps.getObject();
                                    if (map.getAttribute("objectAttributeName").getValueString().equalsIgnoreCase(xatt.getName()))
                                    {
                                        find=true;
                                        map.getAttribute("value").setValueString(""+refobj );
                                        break;
                                    }
                                }
                                if (! find )
                                {
                                    map=details.addNewObject("Ebo_Map");
                                    map.getAttribute("value").setValueString(""+refobj);
                                    map.getAttribute("objectAttributeName").setValueString(xatt.getName());
                                    boObject obji = relBo.getBoManager().getObjectInContext( relBo.getEboContext(), refobj );
                                    if( obji != null && !obji.exists())
                                    {
                                        tmpl.getUpdateQueue().add( obji , boObjectUpdateQueue.MODE_SAVE_FORCED );
                                    }
                                }
                                if(map!=null) validBouis+=(","+ map.bo_boui );
                            }
                        }
                    }
                }
            }

         // APAGAR os MAPs que já não existem
            maps.beforeFirst();
            String xboui;
            int sv;
            while ( maps.next() )
            {
                boObject map2=maps.edit().getObject();

                xboui=""+map2.bo_boui;
                if ( validBouis.indexOf(","+xboui) == -1 && map2.getAttribute("transformation").getValueString().equals("") )
                {
                    sv = maps.getRow();
                    maps.remove();
                    maps.moveTo(sv - 1);
                }
            }

            //copy DAO && extendAttribute
            bridgeHandler bridgeExtAttr = relBo.getBridge("extendAttribute");
            bridgeHandler bridgeDAO = relBo.getBridge("DAO");

            bridgeHandler tmpl_bridgeExtAttr  = tmpl.getBridge("extendAttribute");
            bridgeHandler tmpl_bridgeDAO = tmpl.getBridge("DAO");
            validBouis="";
            if ( bridgeExtAttr != null && !bridgeExtAttr.isEmpty() )
            {
                bridgeExtAttr.beforeFirst();
                while ( bridgeExtAttr.next() )
                {
                    Enumeration xatb = bridgeExtAttr.getLineAttributes().elements();
                    if ( !tmpl_bridgeExtAttr.haveBoui( bridgeExtAttr.getValueLong() ) )
                    {
                        bridgeExtAttr.getObject().getAttribute("PARENT").setValueObject(null);

                        tmpl_bridgeExtAttr.add( bridgeExtAttr.getValueLong() );

                    }
                    tmpl_bridgeExtAttr.edit();
                    validBouis+=","+bridgeExtAttr.getValueLong();
                    while ( xatb.hasMoreElements() )
                    {
                        xat = ( AttributeHandler ) xatb.nextElement() ;
                        tmpl_bridgeExtAttr.getAttribute( xat.getDefAttribute().getName() ).setValueObject( xat.getValueObject() );
                    }
                }
            }
            tmpl_bridgeExtAttr.beforeFirst();
            while ( tmpl_bridgeExtAttr.next() && !tmpl_bridgeExtAttr.isEmpty() )
            {
                xboui=""+tmpl_bridgeExtAttr.edit().getObject().bo_boui;
                if ( validBouis.indexOf(","+xboui) == -1 )
                {
                    tmpl_bridgeExtAttr.remove();
                    tmpl_bridgeExtAttr.previous();
                }
            }
            validBouis="";
            if ( bridgeDAO != null && !bridgeDAO.isEmpty() )
            {
                bridgeDAO.beforeFirst();
                while ( bridgeDAO.next() )
                {
                    Enumeration xatb = bridgeDAO.getLineAttributes().elements();


                    if ( !tmpl_bridgeDAO.haveBoui( bridgeDAO.getValueLong() ) )
                    {
                        tmpl_bridgeDAO.add( bridgeDAO.getValueLong() );
                    }

                    tmpl_bridgeDAO.edit();
                    validBouis+=","+bridgeDAO.getValueLong();
                    while ( xatb.hasMoreElements() )
                    {
                        xat = ( AttributeHandler ) xatb.nextElement() ;
                        tmpl_bridgeDAO.getAttribute( xat.getDefAttribute().getName() ).setValueObject( xat.getValueObject() );

                        if(xat.getDefAttribute().getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE
                            &&
                            xat.getValueObject() != null
                          )
                        {
                            (( BridgeObjAttributeHandler )tmpl_bridgeDAO.getAttribute( xat.getDefAttribute().getName() )).edit();
                        }
                    }
                }
            }

            tmpl_bridgeDAO.beforeFirst();
            while ( tmpl_bridgeDAO.next() )
            {
                xboui=""+tmpl_bridgeDAO.edit().getObject().bo_boui;
                if ( validBouis.indexOf(","+xboui) == -1 )
                {
                    tmpl_bridgeDAO.remove();
                    tmpl_bridgeDAO.previous();
                }
            }
            //ordernar BRIDGE DAO
            int porder[]=new int[ tmpl_bridgeDAO.getRowCount() ];

            tmpl_bridgeDAO.beforeFirst();
            int i=1;
            int p;
            while ( tmpl_bridgeDAO.next() )
            {
                int j=ClassUtils.convertToInt(tmpl_bridgeDAO.getAttribute("code").getValueString());
                if ( j != i )
                {
                    p=tmpl_bridgeDAO.getRow();
                    tmpl_bridgeDAO.moveRowTo(tmpl_bridgeDAO.getRowCount());

                    tmpl_bridgeDAO.moveTo( p-1 );
                }
                else i++;
            }
        }
    }
    public static boolean isParentInTemplateMode(boObject parent)
    {
        boolean result = false;
        byte mode;
        try
        {
            if(parent != null)
            {
                mode = parent.getMode();
                if(boObject.MODE_EDIT_TEMPLATE == mode)
                {
                    result = true;
                }
            }
        }
        catch (Exception ex)
        {
        }
        return result;
    }
    private static boObject applyTemplate(EboContext ctx, boObject lastobj, long applyTemplate, String poolUniqueId, HttpServletRequest request) throws boRuntimeException
    {
        boObject template = (Ebo_TemplateImpl) boObject.getBoManager().loadObject(ctx, applyTemplate);
        boObject eboClsreg = template.getAttribute("masterObjectClass").getObject();
        String newName = eboClsreg.getAttribute("name").getValueString();
        String lastClass = lastobj.getName();
        long boui = ClassUtils.convertToLong(request.getParameter("BOUI"),0);

        if(!newName.equals(lastobj.getName()))
        {

            //faço o cast no object origem
            lastobj.castTo(newName);

            //save
            boObject svObj = lastobj;

            lastobj = boObject.getBoManager().loadObjectAs(ctx, lastobj.getBoui(), newName );
            lastobj.applyTemplate(lastClass, applyTemplate );

            //copia
            lastobj.setSendRedirect(svObj.getSendRedirect());
            lastobj.setMustCast(svObj.mustCast());
            lastobj.bo_name = svObj.bo_name;
            lastobj.bo_definition = svObj.bo_definition;
            lastobj.setChanged(true);
        }
        else if(boui!=0)
        {
            if(!lastobj.poolIsStateFull())
            {
                lastobj.poolSetStateFull(poolUniqueId);
            }
            lastobj.applyTemplate(lastClass,applyTemplate );
        }
        return lastobj;
    }
    private static void setTemplateMode(boObject object,boolean forceCreation) throws boRuntimeException
    {
        object.setModeEditTemplate();
        if(forceCreation)
        {
            templateCreation(object);
        }
        else
        {
            boObject template = object.getAttribute("TEMPLATE").getObject();
            if(template == null)
            {
                templateCreation(object);
            }
        }

    }
    private static void templateCreation(boObject object) throws boRuntimeException
    {
        StringBuffer cardText = object.getTextCARDID();
        boObject template = boObject.getBoManager().createObject( object.getEboContext(),"Ebo_Template");
        AttributeHandler attr = template.getAttribute("masterObjectClass");

        boObject boClsReg = boObject.getBoManager().loadObject(object.getEboContext(),"select Ebo_ClsReg where name='"+object.getName()+"'" );
        template.setParameter("relatedObjBOUI",String.valueOf(object.bo_boui));
        template.setParameter("relatedCls",String.valueOf(object.getName()));
        template.getAttribute("name").setValueString(cardText.toString());
        attr.setValueString(String.valueOf(boClsReg.bo_boui));
        attr.setDisabled();

        object.getAttribute("TEMPLATE").setValueString(String.valueOf(template.bo_boui));
        AttributeHandler attrParent=object.getAttribute("PARENT");
        object.setParameter("neverUpdate","yes");
        //JMF 8-4-2005
//        if ( attrParent != null )
//        {
//            long parent = object.getAttribute("PARENT").getValueLong();
//            if ( parent !=0 )
//            {
//                object.getAttribute("PARENT").setValueObject(null);
//            }
//        }
        //----
       // object.getDataRow().reset();
        convertRuntimeObjects(object);
    }
    private static void convertRuntimeObjects(boObject object) throws boRuntimeException
    {
        boObject attObject = null;
        AttributeHandler attHandler = null;
        AttributeHandler refObject = null;
        bridgeHandler bridge = null;
        boObject[] attBoObjects = null;

        Enumeration oEnum = object.getAttributes().elements();
        while( oEnum.hasMoreElements() )
        {
            attHandler = (AttributeHandler)oEnum.nextElement();
            if(attHandler.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
            {

                    if(attHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1)
                    {
                            attObject = attHandler.getObject();
                            if(attObject != null)
                            {
                                refObject = attObject.getAttribute("refObj");
                                if(refObject != null)
                                {
                                    //attHandler.setValueObject(refObject.getValueObject());
                                    attHandler.setValueLong( refObject.getObject().getBoui() );
                                    boObject o = attHandler.getObject();
                                 //   boObject parentdd= attHandler.getParent();
                                    int uuu=0;

                                }
                            }
                    }
                    else if(attHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_MULTI_VALUES)
                    {
                        attBoObjects = attHandler.getObjects();
                        if(attBoObjects != null)
                        {
                            for(int i = 0 ; i < attBoObjects.length ; i++)
                            {
                                attObject = attBoObjects[i];
                                refObject = attObject.getAttribute("refObj");
                                if(refObject != null)
                                {
                                    attBoObjects[i] = (boObject)refObject.getObject();

                                }
                            }
                        }
                    }
                    else
                    {
                        bridge = object.getBridge(attHandler.getName());
                        bridge.beforeFirst();
                        while(bridge.next())
                        {
                            attObject = bridge.getObject();
                            refObject = attObject.getAttribute("refObj");
                            if(refObject != null)
                            {
                                bridge.setValue(refObject.getObject().getBoui());
                            }
                        }
                    }

            }
        }
    }
    public static boolean doTemplateAction(boObject object) throws boRuntimeException
    {
        boolean result = true;
        String action = getTemplateAction(object);
        if(action != null && action.length() > 0)
        {
            result = executeTemplateAction(object,action);
        }
        return result;
    }

    private static String getTemplateAction(boObject object) throws boRuntimeException
    {
        String result = null;
        AttributeHandler templateHandler = object.getAttribute("TEMPLATE");
        if(templateHandler != null)
        {
            boObject template = (boObject)templateHandler.getObject();
            if(template != null)
            {
                if(object.getMode() == boObject.MODE_NEW)
                {
                    result = template.getAttribute("whendCreate").getValueString();
                }
                else if(object.getMode() == boObject.MODE_EDIT)
                {
                    result = template.getAttribute("whendUpdate").getValueString();
                }
                else if(object.getMode() == boObject.MODE_DESTROY)
                {
                    result = template.getAttribute("whendDelete").getValueString();
                }
            }
        }
        return result;
    }
    private static boolean executeTemplateAction(boObject object, String action)
    {
        boolean result = false;
        Object out = null;
        try
        {
            String[] path = action.split("\\.");
            String cl = action.split("." + path[path.length -1])[0];
            Class clss = Class.forName(cl);
            Class partypes[] = new Class[1];
            partypes[0] = boObject.class;
            Method ometh = clss.getMethod(path[path.length -1],partypes);

            Object args[] = new Object[1];
            args[0] = object;
            out = ometh.invoke(null,args);
            result = ((Boolean)out).booleanValue();
            System.out.println("Result : " + result);
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    private static void removeExtendAttributes(boObject object) throws boRuntimeException
    {
        bridgeHandler bridge = object.getBridge("DAO");
        if(bridge != null)
        {
            bridgeHandler parameterList = null;
            boObject parameter = null;
            bridge.beforeFirst();
            while(bridge.next())
            {
                boObject config = bridge.getAttribute("config").getObject();
                if(config != null)
                {
                    parameterList = config.getBridge("extAtt_parameter");
                    if(parameterList != null)
                    {
                        parameterList.truncate();
                    }
                }
            }
            bridge = object.getBridge("extendAttribute");
            if(bridge != null)
            {
                bridge.truncate();
            }
        }
    }

   /**
     * Devolve uma lista de Ebo_Template, com base num conjunto e palavras, ordenado pelo maior SCORE.
     * @param ctx, EboContext contexto em que o programa é iniciado.
     * @param keyWords, palavras a pesquisar no campo keyWords no Ebo_Template.
     * @return result, lista de Ebo_Template (modelos).
     */
    public static boObjectList findTemplateFromWords(EboContext ctx, String keyWords)
    {
        boObjectList result = null;
        if(keyWords != null && !"".equals(keyWords))
        {
            String words = boObjectList.arrangeFulltext(ctx, keyWords);
            StringBuffer boql = new StringBuffer("SELECT boui FROM  Ebo_Template WHERE 1=[1 and contains(keyWords, '");
            boql.append(words);
            boql.append("',1) > 0] ORDER BY [SCORE(1)] DESC");
            result = boObjectList.list(ctx,boql.toString());
        }
        return result;
    }
}