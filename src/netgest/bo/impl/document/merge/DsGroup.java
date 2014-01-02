/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefBridge;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefPrinterDefinitions;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boAttributesArray;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import java.sql.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import netgest.utils.ClassUtils;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class DsGroup
{
    /**
     *
     * @Company Enlace3
     * @since
     */
    private String alias;

    /**
     *
     * @Company Enlace3
     * @since
     */
    private String wildcard;

    /**
     *
     * @Company Enlace3
     * @since
     */
    private String relatedDS;
    private ArrayList fields = null;

    public DsGroup(String alias, String wildcard, String relatedDS)
    {
        this.alias = alias;
        this.wildcard = wildcard;
        this.relatedDS = relatedDS;
        this.fields = new ArrayList();
    }

    public DsGroup(String alias, String wildcard, String relatedDS,
        ArrayList fields)
    {
        this.alias = alias;
        this.wildcard = wildcard;
        this.relatedDS = relatedDS;
        this.fields = fields;
    }

    public String getAlias()
    {
        return alias;
    }

    public String getWildcard()
    {
        return wildcard;
    }

    public String getRelatedDS()
    {
        return relatedDS;
    }

    public void addField(DsField f)
    {
        if (fields == null)
        {
            fields = new ArrayList();
        }

        fields.add(f);
    }

    public ArrayList getFields()
    {
        return fields;
    }

    public void getData(boObject obj, Tabela tabela) throws boRuntimeException
    {
        if ((wildcard != null) && (wildcard.length() > 0))
        {
            if ("*".equals(wildcard))
            {
                addSimpleData(obj.getAttributes(), tabela);

                //não liga ao Alias
                //fields
                for (int i = 0; i < fields.size(); i++)
                {
                    ((DsField) fields.get(i)).getData(obj, tabela);
                }
            }
            else
            {
                String[] path = wildcard.split("\\.");
                AttributeHandler attHandler;
                boObject aptObj = obj;

                for (int i = 0; (aptObj != null) && (i < path.length); i++)
                {
                    if (!"*".equals(path[i]))
                    {
                        if ((i + 1) == path.length)
                        {
                            if (aptObj.getAttribute(path[i]) != null)
                            {
                                boAttributesArray boAtt = new boAttributesArray();
                                boAtt.add(aptObj.getAttribute(path[i]));
                                addSimpleData(boAtt, tabela);

                                //fields
                                for (int k = 0; k < fields.size(); k++)
                                {
                                    ((DsField) fields.get(k)).getData(aptObj,
                                        tabela);
                                }

                                if (getAlias() != null)
                                {
                                    tabela.insertAlias(getAlias(), wildcard);
                                }
                            }
                        }
                        else
                        {
                            if (aptObj.getAttribute(path[i]) != null)
                            {
                                aptObj = aptObj.getAttribute(path[i]).getObject();
                            }
                            else
                            {
                                aptObj = null;
                            }
                        }
                    }
                }

                if ((aptObj != null) && "*".equals(path[path.length - 1]))
                {
                    addSimpleData(aptObj.getAttributes(), tabela);

                    //fields
                    for (int i = 0; i < fields.size(); i++)
                    {
                        ((DsField) fields.get(i)).getData(aptObj, tabela);
                    }

                    if (getAlias() != null)
                    {
                        String sub = wildcard.substring(0,
                                wildcard.indexOf(".*"));
                        tabela.insertAlias(getAlias(), sub);
                    }
                }
            }
        }
        else
        {
            String relatedDs = getRelatedDS();

            if ((relatedDs != null) && (relatedDs.length() > 0))
            {
                String[] strs = relatedDs.split("\\.");

                if (strs.length == 2)
                {
                    boObject sonObj = obj.getAttribute(strs[0]).getObject();
                    String refObjName = obj.getAttribute(strs[0])
                                           .getDefAttribute()
                                           .getReferencedObjectName();

                    if (sonObj != null)
                    {
                        boDefPrinterDefinitions printerDef = boDefPrinterDefinitions.loadPrinterDefinitions(refObjName);
                        DS refDS = printerDef.getDS(strs[1]);
                        tabela.setPrefix(strs[0]);
                        refDS.getData(sonObj, tabela, false);
                        tabela.removePrefix();
                    }
                }
            }
        }
    }

    private void addSimpleData(boAttributesArray attArr, Tabela tab)
        throws boRuntimeException
    {
        Enumeration xenum = attArr.elements();
        String labelName;

        while (xenum.hasMoreElements())
        {
            AttributeHandler att = (AttributeHandler) xenum.nextElement();

            if (!att.isBridge())
            {
                labelName = (wildcard == null) ? att.getName()
                                               : wildcard.replaceAll("\\*",
                        att.getName());
                tab.insert(getValue(att), labelName, getSqlType(att));
            }
        }
    }

    public void getBridgeData(bridgeHandler bh, Tabela tabela)
        throws boRuntimeException
    {
        if ((wildcard != null) && (wildcard.length() > 0))
        {
            if ("*".equals(wildcard))
            {
                if (getAlias() != null)
                {
                    tabela.insertAlias(getAlias(), bh.getName());
                }

                addSimpleData(bh.getAllAttributes(), tabela);

                //fields
                for (int i = 0; i < fields.size(); i++)
                {
                    ((DsField) fields.get(i)).getData(bh, tabela);
                }

                if (getAlias() != null)
                {
                    tabela.insertAlias(getAlias(), bh.getName());
                }
            }
            else
            {
                String[] path = wildcard.split("\\.");
                AttributeHandler attHandler;
                boObject aptObj = null;

                for (int i = 0;
                        ((i == 0) || (aptObj != null)) && (i < path.length);
                        i++)
                {
                    if (!"*".equals(path[i]))
                    {
                        if ((i + 1) == path.length)
                        {
                            if (i == 0)
                            {
                                if (bh.getAttribute(path[i]) != null)
                                {
                                    boAttributesArray boAtt = new boAttributesArray();
                                    boAtt.add(bh.getAttribute(path[i]));
                                    addSimpleData(boAtt, tabela);

                                    if (getAlias() != null)
                                    {
                                        tabela.insertAlias(getAlias(),
                                            bh.getName() + "." + path[0]);
                                    }

                                    //fields
                                    for (int k = 0; k < fields.size(); k++)
                                    {
                                        ((DsField) fields.get(k)).getData(bh,
                                            tabela);
                                    }
                                }
                            }
                            else if (aptObj.getAttribute(path[i]) != null)
                            {
                                boAttributesArray boAtt = new boAttributesArray();
                                boAtt.add(aptObj.getAttribute(path[i]));
                                addSimpleData(boAtt, tabela);

                                if (getAlias() != null)
                                {
                                    tabela.insertAlias(getAlias(),
                                        bh.getName() + "." + wildcard);
                                }

                                //fields
                                for (int k = 0; k < fields.size(); k++)
                                {
                                    ((DsField) fields.get(k)).getData(aptObj,
                                        tabela);
                                }
                            }
                        }
                        else
                        {
                            if ("child".equalsIgnoreCase(path[i]))
                            {
                                aptObj = bh.getObject();
                            }
                            else if (i == 0)
                            {
                                if (bh.getAttribute(path[i]) != null)
                                {
                                    aptObj = bh.getAttribute(path[i]).getObject();
                                }
                                else
                                {
                                    aptObj = null;
                                }
                            }
                            else
                            {
                                if (aptObj.getAttribute(path[i]) != null)
                                {
                                    aptObj = aptObj.getAttribute(path[i])
                                                   .getObject();
                                }
                                else
                                {
                                    aptObj = null;
                                }
                            }
                        }
                    }
                }

                if ((aptObj != null) && "*".equals(path[path.length - 1]))
                {
                    if (getAlias() != null)
                    {
                        if (path.length == 1)
                        {
                            tabela.insertAlias(getAlias(), bh.getName());
                        }
                        else
                        {
                            String sub = wildcard.substring(0,
                                    wildcard.indexOf(".*"));
                            tabela.insertAlias(getAlias(),
                                bh.getName() + "." + sub);
                        }
                    }

                    tabela.setPrefix(bh.getName());
                    addSimpleData(aptObj.getAttributes(), tabela);

                    //fields
                    for (int i = 0; i < fields.size(); i++)
                    {
                        ((DsField) fields.get(i)).getData(aptObj, tabela);
                    }

                    tabela.removePrefix();
                }
            }
        }
        else
        {
            String relatedDs = getRelatedDS();

            if ((relatedDs != null) && (relatedDs.length() > 0))
            {
                if (getAlias() != null)
                {
                    String sub = relatedDs.substring(0,
                            relatedDs.lastIndexOf("."));
                    tabela.insertAlias(getAlias(), bh.getName() + "." + sub);
                }

                String[] strs = relatedDs.split("\\.");
                boObject obj;

                if (strs.length == 2)
                {
                    String refObjName;

                    if ("child".equalsIgnoreCase(strs[0]))
                    {
                        obj = bh.getObject();
                        refObjName = obj.getBoDefinition().getName();
                        tabela.setPrefix(bh.getName() + "." + "child");
                    }
                    else
                    {
                        obj = bh.getAttribute(strs[0]).getObject();
                        refObjName = bh.getAttribute(strs[0]).getDefAttribute()
                                       .getReferencedObjectName();
                        tabela.setPrefix(bh.getName());
                    }

                    if (obj != null)
                    {
                        boDefPrinterDefinitions printerDef = boDefPrinterDefinitions.loadPrinterDefinitions(refObjName);
                        DS refDS = printerDef.getDS(strs[1]);
                        refDS.getData(obj, tabela, false);
                    }

                    tabela.removePrefix();
                }
            }
        }
    }

    private Object getValue(AttributeHandler attr) throws boRuntimeException
    {
        if (attr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
        {
            long b = -1;
            try{
                b = attr.getValueLong();
            }catch(Exception e){return "";}
            if (b > 0 && attr.getValueObject() != null)
            {
                boObject o = attr.getObject();

                return o.getCARDIDwNoIMG().toString();
            }
            else
            {
                return "";
            }
        }
        else if ("boolean".equalsIgnoreCase(attr.getDefAttribute().getType()))
        {
            String value = attr.getValueString();

            if ("0".equals(value))
            {
                //falta verificar a lingua
                return "Não";
            }
            else if ("1".equals(value))
            {
                return "Sim";
            }

            return value;
        }
        else if ((attr.getDefAttribute().getLOVName() != null) &&
                !"".equals(attr.getDefAttribute().getLOVName()))
        {
            String xlov = attr.getDefAttribute().getLOVName();
            String value = attr.getValueString();

            if ((value != null) && !"".equals(value))
            {
                boObject lov;
                boObject obj = attr.getParent();
                lov = obj.getBoManager().loadObject(obj.getEboContext(),
                        "Ebo_LOV", "name='" + xlov + "'");

                if (lov.exists())
                {
                    bridgeHandler lovdetails = lov.getBridge("details");
                    lovdetails.beforeFirst();

                    boObject det;

                    while (lovdetails.next())
                    {
                        det = lovdetails.getObject();

                        if (value.equalsIgnoreCase(det.getAttribute("value")
                                                          .getValueString()))
                        {
                            return det.getAttribute("description")
                                      .getValueString();
                        }
                    }
                }
            }

            return attr.getValueString();
        }
        else if("clob".equalsIgnoreCase(attr.getDefAttribute().getType()))
        {
            if(attr.getValueString() != null)
            {
                return ClassUtils.htmlToText(attr.getValueString(), true);
            }
            return attr.getValueObject();
        }
        else
        {
            return attr.getValueObject();
        }
    }

    private int getSqlType(AttributeHandler att)
    {
        String t = att.getDefAttribute().getType().toUpperCase();

        if ((t.indexOf("CHAR") != -1) ||
                ((att.getDefAttribute().getLOVName() != null) &&
                !"".equals(att.getDefAttribute().getLOVName())))
        {
            return Types.VARCHAR;
        }

        if (t.indexOf("NUMBER") != -1)
        {
            return Types.NUMERIC;
        }

        if (t.indexOf("BOOLEAN") != -1)
        {
            return Types.VARCHAR;
        }

        if (t.indexOf("OBJECT") != -1)
        {
            return Types.VARCHAR;
        }

        if (t.indexOf("CLOB") != -1)
        {
            return Types.VARCHAR;
        }

        if (t.indexOf("DATE") != -1)
        {
            return Types.DATE;
        }

        return Types.VARCHAR;
    }

    //------------------- HEADERS
    public ArrayList getHeader(boDefHandler objDef, String prefixo,
        String repeatBlockName) throws boRuntimeException
    {
        return getHeader(objDef, prefixo, repeatBlockName, getWildcard());
    }

    public ArrayList getHeader(boObject obj, String prefixo,
        String repeatBlockName) throws boRuntimeException
    {
        return getHeader(obj.getBoDefinition(), prefixo, repeatBlockName,
            getWildcard());
    }

    public ArrayList getHeader(boDefHandler objDef, String prefixo,
        String repeatBlockName, String wildCard) throws boRuntimeException
    {
        ArrayList r = new ArrayList();

        if ((wildCard != null) && (wildCard.length() > 0))
        {
            if ("*".equals(wildCard))
            {
                if (repeatBlockName == null)
                {
                    addSimpleAttToHeaders(prefixo, objDef.getAttributesDef(), r);

                    //fields
                    for (int i = 0; i < fields.size(); i++)
                    {
                        addSimpleFieldToHeaders(prefixo,
                            ((DsField) fields.get(i)).getAlias(), r);
                    }
                }
                else
                {
                    addBridgeAttToHeaders(prefixo, objDef, repeatBlockName, r);
                }
            }
            else
            {
                String[] path = wildCard.split("\\.");

                if (repeatBlockName == null)
                {
                    AttributeHandler attHandler;
                    boDefHandler aptObj = objDef;

                    for (int i = 0; i < path.length; i++)
                    {
                        if (!"*".equals(path[i]))
                        {
                            if ((i + 1) == path.length)
                            {
                                addSimpleFieldToHeaders(prefixo, wildcard, r);

                                //fields
                                for (int k = 0; k < fields.size(); k++)
                                {
                                    addSimpleFieldToHeaders(prefixo,
                                        ((DsField) fields.get(k)).getAlias(), r);
                                }
                            }
                            else
                            {
                                if ((aptObj.getAttributeRef(path[i]).getObjects() == null) ||
                                        (aptObj.getAttributeRef(path[i])
                                                   .getObjects().length < 2))
                                {
                                    aptObj = aptObj.getAttributeRef(path[i])
                                                   .getReferencedObjectDef();
                                }
                                else
                                {
                                    boDefHandler[] objs = aptObj.getAttributeRef(path[i])
                                                                .getObjects();

                                    for (int j = 0; j < objs.length; j++)
                                    {
                                        ArrayList rAux = getHeader(objs[i],
                                                null, joinPath(path, i));
                                        joinHeaders(rAux, r);
                                    }
                                }
                            }
                        }
                    }

                    if ("*".equals(path[path.length - 1]))
                    {
                        String prefix = wildCard.substring(0,
                                wildCard.indexOf(".*"));
                        prefix = (getAlias() != null) ? getAlias() : prefix;

                        if ((prefixo != null) && (prefixo.length() > 0))
                        {
                            prefix = prefixo + "." + prefix;
                        }

                        addSimpleAttToHeaders(prefix,
                            aptObj.getAttributesDef(), r);

                        //fields
                        for (int i = 0; i < fields.size(); i++)
                        {
                            addSimpleFieldToHeaders(prefixo,
                                ((DsField) fields.get(i)).getAlias(), r);
                        }
                    }
                }
                else
                {
                    AttributeHandler attHandler;
                    boDefHandler aptObj = objDef;
                    boDefAttribute attDef = objDef.getAttributeRef(repeatBlockName);

                    if ((attDef.getObjects() == null) ||
                            (attDef.getObjects().length < 2))
                    {
                        for (int i = 0; i < path.length; i++)
                        {
                            if (!"*".equals(path[i]))
                            {
                                if ((i + 1) == path.length)
                                {
                                    addSimpleFieldToHeaders(prefixo,
                                        repeatBlockName + "." + wildCard, r);

                                    //fields
                                    for (int k = 0; k < fields.size(); k++)
                                    {
                                        addSimpleFieldToHeaders(prefixo,
                                            ((DsField) fields.get(k)).getAlias(),
                                            r);
                                    }
                                }
                                else if ("child".equalsIgnoreCase(path[i]))
                                {
                                    aptObj = attDef.getReferencedObjectDef();
                                }
                                else
                                {
                                    if (i == 0)
                                    {
                                        boDefAttribute[] bAtt = attDef.getBridge()
                                                                      .getBoAttributes();

                                        for (int j = 0; j < bAtt.length; i++)
                                        {
                                            if (path[i].equalsIgnoreCase(
                                                        bAtt[j].getName()))
                                            {
                                                aptObj = bAtt[j].getReferencedObjectDef();
                                            }
                                        }
                                    }
                                    else
                                    {
                                        aptObj = aptObj.getAttributeRef(path[i])
                                                       .getReferencedObjectDef();
                                    }
                                }
                            }
                        }

                        if ("*".equals(path[path.length - 1]))
                        {
                            String prf = wildCard.substring(0,
                                    wildCard.lastIndexOf(".*"));
                            prf = (getAlias() != null) ? getAlias() : prf;

                            if ((prefixo != null) && (prefixo.length() > 0))
                            {
                                prf = prefixo + "." + prf;
                            }
                            else
                            {
                                prf = repeatBlockName + "." + prf;
                            }

                            addSimpleAttToHeaders(prf,
                                aptObj.getAttributesDef(), r);

                            //fields
                            for (int i = 0; i < fields.size(); i++)
                            {
                                addSimpleFieldToHeaders(prefixo,
                                    ((DsField) fields.get(i)).getAlias(), r);
                            }
                        }
                    }
                    else
                    {
                        boDefHandler[] obList = attDef.getObjects();
                        ArrayList auxR;

                        for (int i = 0; i < obList.length; i++)
                        {
                            String pathAux = joinPath(path, 1);
                            auxR = getHeader(obList[i],
                                    repeatBlockName + "." + path[0],
                                    repeatBlockName, pathAux);
                            joinHeaders(auxR, r);
                        }
                    }
                }
            }
        }
        else
        {
            //block
            if (repeatBlockName == null)
            {
                String relatedDs = getRelatedDS();

                if ((relatedDs != null) && (relatedDs.length() > 0))
                {
                    String[] strs = relatedDs.split("\\.");

                    if (strs.length == 2)
                    {
                        if (objDef.getAttributeRef(strs[0]).getType().indexOf("boObject") != -1)
                        {
                            boDefHandler[] obList = objDef.getAttributeRef(strs[0])
                                                          .getObjects();

                            for (int i = 0; i < obList.length; i++)
                            {
                                String refObjName = obList[i].getName();
                                boDefPrinterDefinitions printerDef = boDefPrinterDefinitions.loadPrinterDefinitions(refObjName);
                                DS refDS = printerDef.getDS(strs[1]);
                                String pref = strs[0];

                                if ((prefixo != null) && !"".equals(prefixo))
                                {
                                    pref = prefixo + "." + strs[0];
                                }

                                Object[] oo = refDS.getHeader(objDef.getAttributeRef(
                                            strs[0]).getReferencedObjectDef(),
                                        pref);
                                joinHeaders(Arrays.asList(oo), r);
                            }
                        }
                        else
                        {
                            String refObjName = objDef.getAttributeRef(strs[0])
                                                      .getReferencedObjectName();
                            boDefPrinterDefinitions printerDef = boDefPrinterDefinitions.loadPrinterDefinitions(refObjName);
                            DS refDS = printerDef.getDS(strs[1]);
                            String pref = strs[0];

                            if ((prefixo != null) && !"".equals(prefixo))
                            {
                                pref = prefixo + "." + strs[0];
                            }

                            Object[] oo = refDS.getHeader(objDef.getAttributeRef(
                                        strs[0]).getReferencedObjectDef(), pref);
                            joinHeaders(Arrays.asList(oo), r);
                        }
                    }
                }
            }
            else
            {
                //bridge
                String relatedDs = getRelatedDS();

                if ((relatedDs != null) && (relatedDs.length() > 0))
                {
                    String[] strs = relatedDs.split("\\.");
                    boObject obj;

                    if (strs.length == 2)
                    {
                        String refObjName;

                        if ("child".equalsIgnoreCase(strs[0]))
                        {
                            boDefAttribute bhDef = objDef.getAttributeRef(repeatBlockName);

                            if (bhDef.getType().indexOf("boObject") != -1)
                            {
                                boDefHandler[] obList = bhDef.getObjects();

                                for (int i = 0; i < obList.length; i++)
                                {
                                    refObjName = obList[i].getName();

                                    boDefPrinterDefinitions printerDef = boDefPrinterDefinitions.loadPrinterDefinitions(refObjName);
                                    DS refDS = printerDef.getDS(strs[1]);
                                    String pref = repeatBlockName + "." +
                                        strs[0];

                                    if ((prefixo != null) &&
                                            !"".equals(prefixo))
                                    {
                                        pref = prefixo + "." + repeatBlockName +
                                            "." + strs[0];
                                    }

                                    Object[] oo = refDS.getHeader(objDef.getAttributeRef(
                                                strs[0]).getReferencedObjectDef(),
                                            pref);
                                    joinHeaders(Arrays.asList(oo), r);
                                }
                            }
                            else
                            {
                                refObjName = bhDef.getReferencedObjectName();

                                boDefPrinterDefinitions printerDef = boDefPrinterDefinitions.loadPrinterDefinitions(refObjName);
                                DS refDS = printerDef.getDS(strs[1]);
                                String pref = repeatBlockName + "." + strs[0];

                                if ((prefixo != null) && !"".equals(prefixo))
                                {
                                    pref = prefixo + "." + repeatBlockName +
                                        "." + strs[0];
                                }

                                Object[] oo = refDS.getHeader(boDefHandler.getBoDefinition(
                                            refObjName), pref);
                                joinHeaders(Arrays.asList(oo), r);
                            }
                        }
                        else
                        {
                            boDefAttribute bhDef = objDef.getAttributeRef(repeatBlockName);
                            boDefBridge brigeDef = bhDef.getBridge();

                            if (brigeDef.getAttributeRef(strs[0]).getType()
                                            .indexOf("boObject") != -1)
                            {
                                boDefHandler[] obList = brigeDef.getAttributeRef(strs[0])
                                                                .getObjects();

                                for (int i = 0; i < obList.length; i++)
                                {
                                    refObjName = obList[i].getName();

                                    boDefPrinterDefinitions printerDef = boDefPrinterDefinitions.loadPrinterDefinitions(refObjName);
                                    DS refDS = printerDef.getDS(strs[1]);
                                    String pref = repeatBlockName + "." +
                                        strs[0];

                                    if ((prefixo != null) &&
                                            !"".equals(prefixo))
                                    {
                                        pref = prefixo + "." + repeatBlockName +
                                            "." + strs[0];
                                    }

                                    Object[] oo = refDS.getHeader(objDef.getAttributeRef(
                                                strs[0]).getReferencedObjectDef(),
                                            pref);
                                    joinHeaders(Arrays.asList(oo), r);
                                }
                            }
                            else
                            {
                                refObjName = brigeDef.getAttributeRef(strs[0])
                                                     .getReferencedObjectName();

                                boDefPrinterDefinitions printerDef = boDefPrinterDefinitions.loadPrinterDefinitions(refObjName);
                                DS refDS = printerDef.getDS(strs[1]);
                                String pref = repeatBlockName + "." + strs[0];

                                if ((prefixo != null) && !"".equals(prefixo))
                                {
                                    pref = prefixo + "." + repeatBlockName +
                                        "." + strs[0];
                                }

                                Object[] oo = refDS.getHeader(objDef.getAttributeRef(
                                            strs[0]).getReferencedObjectDef(),
                                        repeatBlockName + "." + strs[0]);
                                joinHeaders(Arrays.asList(oo), r);
                            }
                        }
                    }
                }
            }
        }

        return r;
    }

    private void addSimpleAttToHeaders(String prefix, boDefAttribute[] att,
        ArrayList r)
    {
        for (int i = 0; i < att.length; i++)
        {
            if (!r.contains(att[i].getName()) &&
                    !((att[i].getMaxOccurs() > 1) &&
                    (att[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)))
            {
                if ((prefix != null) && !"".equals(prefix))
                {
                    r.add(prefix + "." + att[i].getName());
                }
                else
                {
                    r.add(att[i].getName());
                }
            }
        }
    }

    private void addSimpleFieldToHeaders(String prefix, String att, ArrayList r)
    {
        if ((prefix != null) && !"".equals(prefix))
        {
            r.add(prefix + "." + att);
        }
        else
        {
            r.add(att);
        }
    }

    private void addBridgeAttToHeaders(String prefixo, boDefHandler objDef,
        String repeatBlockName, ArrayList r)
    {
        boDefAttribute att = objDef.getAttributeRef(repeatBlockName);

        if ((att.getMaxOccurs() > 1) &&
                (att.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE))
        {
            boDefBridge brdg = att.getBridge();

            if (brdg.haveBridgeAttributes())
            {
                boDefAttribute[] atts = brdg.getBoAttributes();
                String alias = (getAlias() == null) ? repeatBlockName : getAlias();
                String auxN;

                for (int i = 0; i < atts.length; i++)
                {
                    if ((prefixo != null) && !"".equals(prefixo))
                    {
                        auxN = prefixo + "." + alias + "." + atts[i].getName();
                    }
                    else
                    {
                        auxN = alias + "." + atts[i].getName();
                    }

                    if (!r.contains(auxN))
                    {
                        r.add(auxN);
                    }
                }
            }
        }
    }

    private void joinHeaders(List lfrom, List rto)
    {
        for (int i = 0; i < lfrom.size(); i++)
        {
            if (!rto.contains(lfrom.get(i)))
            {
                rto.add(lfrom.get(i));
            }
        }
    }

    private String joinPath(String[] path, int from)
    {
        StringBuffer sb = new StringBuffer();

        for (int i = from + 1; i < path.length; i++)
        {
            sb.append(path[i]);
        }

        return sb.toString();
    }
}
