/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge.gestemp.presentation;

import netgest.bo.def.*;

import netgest.bo.dochtml.*;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.ql.*;

import netgest.bo.runtime.*;

import netgest.utils.*;

import java.util.*;
import netgest.bo.system.Logger;


public class AttributesTreeHelper
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.userquery.queryBuilderHelper");
    private final static String[] NOT_INCLUDE = {"TEMPLATE", "PARENTCTX", "SYS_ORIGIN"
                                                          };
    
    public AttributesTreeHelper()
    {
    }

    private static Long getAttributeValue(EboContext boctx, String objectName, String attName) throws boRuntimeException
    {
        boObject clsReg = null;
        boObjectList list = boObjectList.list(boctx, "Select Ebo_ClsReg.attributes where name = ? and attributes.name=?",new Object[] { objectName, attName }, 1 );
        list.beforeFirst();
        if( list.next() )
        {
            return new Long( list.getCurrentBoui() );
        }
        /*list.beforeFirst();
        if(list.next())
        {
            clsReg = list.getObject();
        }
    
        if(clsReg != null) 
        {
            boBridgeIterator bit = clsReg.getBridge("attributes").iterator();
            bit.beforeFirst();
            while(bit.next())
            {
                if(attName.equals(bit.currentRow().getObject().getAttribute("name").getValueString()))
                {
                    return new Long(bit.currentRow().getValueLong());
                }
            }
        }*/
        return new Long(-1);
    }

    public static StringBuffer getXMLAttributes(docHTML DOC, boDefHandler bodef, boolean includeAttributeObjects, boolean extendAttributes, boolean onlyBridge) throws boRuntimeException
    {
        StringBuffer attributesStr = new StringBuffer("");
        attributesStr.append("<attributes>");

        if (bodef != null)
        {
            boDefHandler[] subClasses = null;

            if (extendAttributes)
            {
                subClasses = bodef.getTreeSubClasses();
            }

            boDefAttribute[] attr = new boDefAttribute[] {  };
            attr = bodef.getAttributesDef();
            
            
            

            Hashtable listAttributes = new Hashtable();
            Hashtable listAttributesOptions = new Hashtable();
            ArrayList valueList = new ArrayList();

            for (int i = 0; i < attr.length; i++)
            {
                listAttributes.put(attr[i].getName(), attr[i]);
                listAttributesOptions.put(attr[i].getName(), new StringBuffer("all"));
            }

            if (extendAttributes)
            {
                for (int i = 0; i < subClasses.length; i++)
                {
                    boDefHandler bo = subClasses[i];
                    boDefAttribute[] subAttr = bo.getAttributesDef();

                    for (int j = 0; j < subAttr.length; j++)
                    {
                        if (listAttributes.containsKey(subAttr[j].getName()))
                        {
                            StringBuffer options = (StringBuffer) listAttributesOptions.get(subAttr[j].getName());

                            if (!options.toString().equals("all"))
                            {
                                options.append(';').append(bo.getName());
                            }
                        }
                        else
                        {
                            listAttributes.put(subAttr[j].getName(), subAttr[j]);
                            listAttributesOptions.put(subAttr[j].getName(), new StringBuffer(bo.getName()));
                        }
                    }
                }
            }

            Enumeration attrs = listAttributes.elements();
            
            while (attrs.hasMoreElements())
            {
                boDefAttribute attrDef = (boDefAttribute) attrs.nextElement();
                StringBuffer attrOptions = (StringBuffer) listAttributesOptions.get(attrDef.getName());

                if(validAttribute(attrDef.getName()))
                {
                    getXMLStringAttribute(DOC, 0, attributesStr, attrDef, attrOptions, includeAttributeObjects, "", "", onlyBridge);
                }
            }
        }

        attributesStr.append("</attributes>");

        return attributesStr;
    }

    public static void getXMLStringAttribute(docHTML DOC, int level, StringBuffer attributesStr, boDefAttribute attr, StringBuffer options,
        boolean includeAttributeObjects, String prefix, String prefixLabel, boolean onlyBridge) throws boRuntimeException
    {
        boolean exclude = true;
        if(!onlyBridge)
        {
            if(attr.getAtributeType() != boDefAttribute.TYPE_OBJECTATTRIBUTE || 
                (   attr.getRelationType() != boDefAttribute.RELATION_1_TO_N && 
                    attr.getRelationType() != boDefAttribute.RELATION_1_TO_N_WBRIDGE && 
                    attr.getRelationType() != boDefAttribute.RELATION_MULTI_VALUES)
            )
            {
                exclude = false;
                getXMLStringAttributeSingle(DOC, attributesStr, attr, level, options, prefix, prefixLabel);
            }         
        }
        else
        {
            if(attr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && 
                (   attr.getRelationType() == boDefAttribute.RELATION_1_TO_N || 
                    attr.getRelationType() == boDefAttribute.RELATION_1_TO_N_WBRIDGE || 
                    attr.getRelationType() == boDefAttribute.RELATION_MULTI_VALUES)
            )
            {
                exclude = false;
                getXMLStringAttributeSingle(DOC, attributesStr, attr, level, options, prefix, prefixLabel);
            }
        }

        if (!exclude && includeAttributeObjects && (attr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                (attr.getRelationType() == boDefAttribute.RELATION_1_TO_1))
        {
            boDefHandler relBo = attr.getReferencedObjectDef();
            String xname = attr.getName();

            if (!relBo.getName().equalsIgnoreCase("boObject"))
            {
                boDefAttribute[] attrs2 = relBo.getAttributesDef();

                if ((level < 3) || !relBo.getBoCanBeOrphan())
                {
                    String xprefix = (prefix.length() == 0) ? "" : (prefix + ".");
                    String xprefixLabel = (prefixLabel.length() == 0) ? "" : (prefixLabel + "::");

                    if (xprefix.length() > 0)
                    {
                        int u = 0;
                    }

                    for (int i = 0; i < attrs2.length; i++)
                    {
                        if(validAttribute(attrs2[i].getName()))
                        {
                            getXMLStringAttribute(DOC, level + 1, attributesStr, attrs2[i], null, includeAttributeObjects, xprefix + attr.getName(),
                                xprefixLabel + attr.getLabel(), onlyBridge);
                        }
                    }
                }
            }
        }
    }

    public static void getXMLStringAttributeSingle(docHTML DOC, StringBuffer attributesStr, boDefAttribute attr, int level, StringBuffer options,
        String prefix, String prefixLabel) throws boRuntimeException
    {
        String lovName;
        byte atype;
        boolean lovRequired;
        byte typeRelation;
        byte type;
        String lov = "";
        String nameAttr = attr.getName();
        atype = attr.getAtributeType();
        lovName = attr.getLOVName();
        String objName = attr.getBoDefHandler().getName();

        if ((prefix.length() > 0) && !prefix.endsWith("."))
        {
            prefix = prefix + ".";
        }

        if ((prefixLabel.length() > 0) && !prefixLabel.endsWith("::"))
        {
            prefixLabel = prefixLabel + "::";
        }

        if (lovName != null && !"".equalsIgnoreCase(lovName))
        {
            //lovRequired = attr.isLovEditable();

            //if (!lovRequired)
            //{
                boObject lovobject;

                try
                {
                    lovobject = boObject.getBoManager().loadObject(DOC.getEboContext(), "Ebo_LOV", "name='" + lovName + "'");

                    if (lovobject.exists())
                    {
                        bridgeHandler lovdetails = lovobject.getBridge("details");
                        lov = "<lov>";
                        lovdetails.beforeFirst();

                        while (lovdetails.next())
                        {
                            lov += "<item><description>";
                            lov += lovdetails.getObject().getAttribute("description").getValueString().replaceAll("\n","");
                            lov += "</description>";
                            lov += "<value>";
                            lov += lovdetails.getObject().getAttribute("value").getValueString();
                            lov += "</value>";
                            lov += "</item>";
                        }

                        lov += "</lov>";
                    }
                }
                catch (Exception e)
                {
                    logger.warn(LoggerMessageLocalizer.getMessage("ERROR_LOADING_LOV_LOVNAME")+"= " + lovName);
                }
            //}
        }

        if (atype == boDefAttribute.TYPE_ATTRIBUTE)
        {
            type = attr.getValueType();

            String charType = "";
            String decimals = "";

            if (type == boDefAttribute.VALUE_CHAR)
            {
                charType = "char";
            }
            else if (type == boDefAttribute.VALUE_BOOLEAN)
            {
                charType = "char";
                lov = "<lov><item><description>Sim</description><value>1</value></item><item><description>Não</description><value>0</value></item></lov>";
            }
            else if (type == boDefAttribute.VALUE_DATE)
            {
                charType = "date";
            }
            else if (type == boDefAttribute.VALUE_DATETIME)
            {
                charType = "datetime";
            }
            else if (type == boDefAttribute.VALUE_NUMBER)
            {
                charType = "number";

                String xtype = attr.getType();
                decimals = "0";

                if (xtype.indexOf(',') > -1)
                {
                    decimals = xtype.substring(xtype.indexOf(','));
                }

                decimals = " decimals='" + decimals + "' ";
            }
            else if (type == boDefAttribute.VALUE_CLOB)
            {
                charType = "char";
            }
            else if (type == boDefAttribute.VALUE_IFILELINK)
            {
                charType = "iFile";
            }

            if (!charType.equals(""))
            {
                attributesStr.append("<").append(prefix).append(attr.getName()).append(" ");
                attributesStr.append(decimals);
                attributesStr.append(" type='" + charType + "' >");
                
                attributesStr.append("<value>");
                attributesStr.append(getAttributeValue(DOC.getEboContext(), objName, attr.getName()));
                attributesStr.append("</value>");
                
                attributesStr.append("<label>");
                attributesStr.append(prefixLabel + attr.getLabel());
                attributesStr.append("</label>");

                attributesStr.append("<level>");
                attributesStr.append(level);
                attributesStr.append("</level>");

                attributesStr.append("<prefixlabel>");
                attributesStr.append(prefixLabel);
                attributesStr.append("</prefixlabel>");

                if (!lov.equals(""))
                {
                    attributesStr.append(lov);
                }

                attributesStr.append("</").append(prefix).append(attr.getName()).append(">");
            }
        }
        else if (atype == boDefAttribute.TYPE_OBJECTATTRIBUTE)
        {
            typeRelation = attr.getRelationType();

            char charTypeRel = ' ';

            if (typeRelation == boDefAttribute.RELATION_1_TO_1)
            {
                charTypeRel = '1';
            }
            else if (typeRelation == boDefAttribute.RELATION_1_TO_N)
            {
                charTypeRel = 'N';
            }
            else if (typeRelation == boDefAttribute.RELATION_MULTI_VALUES)
            {
                charTypeRel = 'N';
            }
            else if (typeRelation == boDefAttribute.RELATION_1_TO_N_WBRIDGE)
            {
                charTypeRel = 'N';
            }

            String charType = attr.getType();
            boDefHandler bodefRef = attr.getReferencedObjectDef();

            if (bodefRef != null)
            {
                attributesStr.append("<").append(prefix).append(attr.getName()).append(" ");
                attributesStr.append(" type='" + charType + "' ");

                attributesStr.append(" relation='" + charTypeRel + "' >");

                attributesStr.append("<value>");
                attributesStr.append(getAttributeValue(DOC.getEboContext(), objName, attr.getName()));
                attributesStr.append("</value>");
                
                attributesStr.append("<label>");
                attributesStr.append(prefixLabel + attr.getLabel());
                attributesStr.append("</label>");

                attributesStr.append("<level>");
                attributesStr.append(level);
                attributesStr.append("</level>");

                attributesStr.append("<prefixlabel>");
                attributesStr.append(prefixLabel);
                attributesStr.append("</prefixlabel>");

                StringBuffer sobjects = new StringBuffer();

                if (bodefRef.getName().equalsIgnoreCase("boObject"))
                {
                    boDefHandler[] xdfs = attr.getObjects();

                    if (xdfs != null)
                    {
                        for (int j = 0; j < xdfs.length; j++)
                        {
                            sobjects.append(xdfs[j].getName());

                            if ((j + 1) < xdfs.length)
                            {
                                sobjects.append(';');
                            }
                        }
                    }
                    else
                    {
                        sobjects.append("");
                    }

                    attributesStr.append("<objects>");
                    attributesStr.append(sobjects);
                    attributesStr.append("</objects>");
                    attributesStr.append("<objectLabel>");
                    attributesStr.append("boObject");
                    attributesStr.append("</objectLabel>");
                    attributesStr.append("<objectName>");
                    attributesStr.append("boObject");
                    attributesStr.append("</objectName>");
                }
                else
                {
                    attributesStr.append("<objectLabel>");
                    attributesStr.append(bodefRef.getLabel());
                    attributesStr.append("</objectLabel>");
                    attributesStr.append("<objectName>");
                    attributesStr.append(bodefRef.getName());
                    attributesStr.append("</objectName>");
                }

                /*
                var nodeObjects=nodeAttr.selectSingleNode( "objects" ).text; //lista de objectos separados por ;
                var cardObjects=nodeAttr.selectSingleNode( "card" ).text.firstChild.nodeValue; //acesso ao CDATA
                var objectLabel=nodeAttr.selectSingleNode( "objectLabel" ).text;
                var objectName=nodeAttr.selectSingleNode( "objectName" ).text;
                */
                if (!lov.equals(""))
                {
                    attributesStr.append(lov);
                }

                attributesStr.append("</").append(prefix).append(attr.getName()).append(">");

                if ((charTypeRel == '1') && !bodefRef.getName().equalsIgnoreCase("boObject"))
                {
                    //  attr[i].getBoDefHandler()
                }
            }
        }
        else if (atype == boDefAttribute.TYPE_STATEATTRIBUTE)
        {
            boDefClsState xsta = (boDefClsState) attr;
            String xname = xsta.getName();
            String xlabel = xsta.getLabel();

            lov = "<lov>";

            boDefClsState[] xstaChilds = xsta.getChildStates();

            for (int j = 0; j < xstaChilds.length; j++)
            {
                lov += "<item><description>";
                lov += xstaChilds[j].getLabel();
                lov += "</description>";
                lov += "<value>";
                lov += xstaChilds[j].getNumericForm();
                lov += "</value>";
                lov += "</item>";
            }

            lov += "</lov>";

            attributesStr.append("<").append(prefix).append(xname).append(" ");
            attributesStr.append(" type='number' >");

            attributesStr.append("<value>");
            attributesStr.append(getAttributeValue(DOC.getEboContext(), objName, attr.getName()));
            attributesStr.append("</value>");
            
            attributesStr.append("<label>");
            attributesStr.append(prefixLabel + xlabel);
            attributesStr.append("</label>");

            attributesStr.append("<level>");
            attributesStr.append(level);
            attributesStr.append("</level>");

            attributesStr.append("<prefixlabel>");
            attributesStr.append(prefixLabel);
            attributesStr.append("</prefixlabel>");

            if (!lov.equals(""))
            {
                attributesStr.append(lov);
            }

            attributesStr.append("</").append(prefix).append(xname).append('>');
        }
    }

    //--------------------------JAVASCRIPT TREE
    public static StringBuffer getJavaScriptTree(boDefHandler bodef, String onlyObjects, boolean includeAttributeObjects, boolean extendAttributes,
        Counter index, boolean onlyBridge)
    {
        StringBuffer attributesStr = new StringBuffer("");

        boDefHandler[] subClasses = null;

        if (extendAttributes)
        {
            subClasses = bodef.getTreeSubClasses();
        }

        boDefAttribute[] attr = new boDefAttribute[] {  };
        attr = bodef.getAttributesDef();

        Hashtable listAttributes = new Hashtable();
        Hashtable listAttributesOptions = new Hashtable();

        for (int i = 0; i < attr.length; i++)
        {
            listAttributes.put(attr[i].getName(), attr[i]);
            listAttributesOptions.put(attr[i].getName(), new StringBuffer("all"));
        }

        if (extendAttributes)
        {
            for (int i = 0; i < subClasses.length; i++)
            {
                boDefHandler bo = subClasses[i];

                if (onlyObjects.indexOf(bo.getName() + ";") > -1 || onlyObjects.length()==0)
                {
                    boDefAttribute[] subAttr = bo.getAttributesDef();

                    for (int j = 0; j < subAttr.length; j++)
                    {
                        if (listAttributes.containsKey(subAttr[j].getName()))
                        {
                            StringBuffer options = (StringBuffer) listAttributesOptions.get(subAttr[j].getName());

                            if (!options.toString().equals("all"))
                            {
                                options.append(';').append(bo.getName());
                            }
                        }
                        else
                        {
                            listAttributes.put(subAttr[j].getName(), subAttr[j]);
                            listAttributesOptions.put(subAttr[j].getName(), new StringBuffer(bo.getName()));
                        }
                    }
                }
            }
        }
        
        boDefAttribute[] listAttributesArray=new boDefAttribute[ listAttributes.size() ];
        Enumeration attrs = listAttributes.elements();
        int i=0;
        while (attrs.hasMoreElements())
        {
            boDefAttribute attrDef = (boDefAttribute) attrs.nextElement();
            listAttributesArray[i++] = attrDef;
        }
        Arrays.sort( listAttributesArray , new  AttributesTreeHelper.labelComparator() );    

        

        StringBuffer[] strScript = new StringBuffer[listAttributes.size()];
        attributesStr.append("attributes=[];\n");
        attributesStr.append("foldersTree = gFld(\"Atributos\", \"\");\n");
        attributesStr.append("foldersTree.xID='rootNode';\n");

        // boolean
        int z = 0;
        StringBuffer aux = null;
        for (i = 0; i < listAttributesArray.length ; i++) 
        {
            boDefAttribute attrDef = listAttributesArray[i];
            StringBuffer attrOptions = (StringBuffer) listAttributesOptions.get(attrDef.getName());

            aux = getJAVASCRIPTAttribute(0, attributesStr, attrDef, attrOptions, includeAttributeObjects, "foldersTree", "", index, onlyBridge);
            if(aux != null)
            {
                strScript[z++] = aux;
            }
        }

        strScript = shrink(strScript, z);
        attributesStr.append("foldersTree.xID = \"tree\";\n");
        attributesStr.append("foldersTree.addChildren([");

        for ( i = 0; i < strScript.length; i++)
        {
            attributesStr.append(strScript[i]);

            if ((i + 1) < strScript.length)
            {
                attributesStr.append(',');
            }
        }

        attributesStr.append("]);");

        //   foldersTree.addChildren([aux2000,aux2001,aux2002])
        //   foldersTree.treeID = "L1" 
        //attributesStr.append("</attributes>");
        return attributesStr;
    }
    private static class labelComparator implements Comparator 
    { 
    
    public final int compare ( Object a, Object b) 
    { 
    return ( (String)((boDefAttribute)a).getLabel() ).compareTo( (String)((boDefAttribute)b).getLabel() ); 
    }  
    }  
    public static StringBuffer getJAVASCRIPTAttribute(int level, StringBuffer attributesStr, boDefAttribute attr, StringBuffer options,
        boolean includeAttributeObjects, String parentItem, String parentAttribute, Counter index, boolean onlyBridge)
    {
        boolean haveChildren = false;
        StringBuffer toRet = new StringBuffer();
        boDefAttribute[] attrs2 = null;
        
        boolean exclude = true;
        if(!onlyBridge)
        {
            if(attr.getAtributeType() != boDefAttribute.TYPE_OBJECTATTRIBUTE || 
                (   attr.getRelationType() != boDefAttribute.RELATION_1_TO_N && 
                    attr.getRelationType() != boDefAttribute.RELATION_1_TO_N_WBRIDGE && 
                    attr.getRelationType() != boDefAttribute.RELATION_MULTI_VALUES)
            )
            {
                exclude = false;
            }         
        }
        else
        {
            if(attr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && 
                (   attr.getRelationType() == boDefAttribute.RELATION_1_TO_N || 
                    attr.getRelationType() == boDefAttribute.RELATION_1_TO_N_WBRIDGE || 
                    attr.getRelationType() == boDefAttribute.RELATION_MULTI_VALUES)
            )
            {
                exclude = false;
            }
        }
        if(!exclude)
        {
            if (includeAttributeObjects && (attr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                    (attr.getRelationType() == boDefAttribute.RELATION_1_TO_1))
            {
                boDefHandler relBo = attr.getReferencedObjectDef();
                String xname = attr.getName();
    
                if (!relBo.getName().equalsIgnoreCase("boObject"))
                {
                    attrs2 = relBo.getAttributesDef();
    
                    if ((level < 3) || !relBo.getBoCanBeOrphan())
                    {
                        if (attrs2.length > 0)
                        {
                            haveChildren = true;
                        }
                    }
                }
            }
    
            String atrVar = parentItem + "_" + attr.getName();
    
            if (!haveChildren)
            {
                String atrName = "";
    
                if (parentAttribute.length() > 0)
                {
                    atrName = parentAttribute + "." + attr.getName();
                }
                else
                {
                    atrName = attr.getName();
                }
    
                attributesStr.append("attributes[").append(index.getNumber()).append("]=\"").append(atrName).append("\";\n");
    
                attributesStr.append(atrVar);
                attributesStr.append("=[\"");
                attributesStr.append(attr.getLabel());
                attributesStr.append("\",\"");
                attributesStr.append("javascript:selAttribute(").append(index.getNumber()).append(" )");
                attributesStr.append("\"] ;\n");
    
                attributesStr.append(atrVar).append(".xID='x").append(index.getNumber()).append("';\n");
    
                toRet.append(atrVar);
                index.increment();
            }
            else
            {
                String atrName = "";
    
                if (parentAttribute.length() > 0)
                {
                    atrName = parentAttribute + "." + attr.getName();
                }
                else
                {
                    atrName = attr.getName();
                }
    
                attributesStr.append("attributes[").append(index.getNumber()).append("]=\"").append(atrName).append("\";\n");
    
                StringBuffer[] strScript = new StringBuffer[attrs2.length];
    
                attributesStr.append(atrVar).append(" = gFld(\"").append(attr.getLabel()).append("\",\"javascript:selAttribute(");
                attributesStr.append(index.getNumber());
                attributesStr.append(")\");\n");
                attributesStr.append(atrVar).append(".xID='x").append(index.getNumber()).append("';\n");
                index.increment();
    
                //       attributesStr.append("rootTree.xID=rootNode;\n");
                int z = 0;
    
                StringBuffer aux = null;
                for (int i = 0; i < attrs2.length; i++)
                {
                    aux = getJAVASCRIPTAttribute(level + 1, attributesStr, attrs2[i], null, includeAttributeObjects, parentItem + "_" + attr.getName(),
                            atrName, index, onlyBridge);
                    if(aux != null)
                    {
                        strScript[z++] = aux;
                    }
                }
    
                strScript = shrink(strScript, z);
                toRet.append(atrVar);
    
                attributesStr.append(atrVar).append(".addChildren([");
    
                for (int i = 0; i < strScript.length; i++)
                {
                    attributesStr.append(strScript[i]);
    
                    if ((i + 1) < strScript.length)
                    {
                        attributesStr.append(',');
                    }
                }
    
                attributesStr.append("]);\n");
            }
            return toRet;
        }
        return null;
    }
    
    private static StringBuffer[] shrink(StringBuffer[] arr, int lastPos) 
    {
        StringBuffer[] newValue = new StringBuffer[lastPos];
        System.arraycopy(arr, 0, newValue, 0, lastPos);
        return newValue;
    }
    
    private static boolean validAttribute(String name)
    {
        for (int i = 0; i < NOT_INCLUDE.length; i++) 
        {
            if(NOT_INCLUDE[i].equalsIgnoreCase(name))
            {
                return false;
            }
        }
        return true;
    }
}
