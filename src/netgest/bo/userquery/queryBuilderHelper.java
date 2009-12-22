/*Enconding=UTF-8*/
package netgest.bo.userquery;

import netgest.bo.def.*;

import netgest.bo.dochtml.*;

import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.ql.*;

import netgest.bo.runtime.*;

import netgest.utils.*;

import java.util.*;
import netgest.bo.system.Logger;


public class queryBuilderHelper
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.userquery.queryBuilderHelper");
    
    public queryBuilderHelper()
    {
    }

    public static StringBuffer getXMLAttributes(docHTML DOC, boDefHandler bodef, boolean includeAttributeObjects, boolean extendAttributes)
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

                getXMLStringAttribute(DOC, 0, attributesStr, attrDef, attrOptions, includeAttributeObjects, "", "");
            }
        }

        attributesStr.append("</attributes>");

        return attributesStr;
    }

    public static void getXMLStringAttribute(docHTML DOC, int level, StringBuffer attributesStr, boDefAttribute attr, StringBuffer options,
        boolean includeAttributeObjects, String prefix, String prefixLabel)
    {
        getXMLStringAttributeSingle(DOC, attributesStr, attr, level, options, prefix, prefixLabel);

        if (includeAttributeObjects && (attr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                (attr.getRelationType() == boDefAttribute.RELATION_1_TO_1))
        {
            boDefHandler relBo = attr.getReferencedObjectDef();
            String xname = attr.getName();

            if (!relBo.getName().equalsIgnoreCase("boObject"))
            {
                boDefAttribute[] attrs2 = relBo.getAttributesDef();

                if ((level == 0) || !relBo.getBoCanBeOrphan())
                {
                    String xprefix = (prefix.length() == 0) ? "" : (prefix + ".");
                    String xprefixLabel = (prefixLabel.length() == 0) ? "" : (prefixLabel + "::");

                    if (xprefix.length() > 0)
                    {
                        int u = 0;
                    }

                    for (int i = 0; i < attrs2.length; i++)
                    {
                        getXMLStringAttribute(DOC, level + 1, attributesStr, attrs2[i], null, includeAttributeObjects, xprefix + attr.getName(),
                            xprefixLabel + attr.getLabel());
                    }
                }
            }
        }
    }

    public static void getXMLStringAttributeSingle(docHTML DOC, StringBuffer attributesStr, boDefAttribute attr, int level, StringBuffer options,
        String prefix, String prefixLabel)
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
                lovObject lovObj;

                try
                {
                    lovObj = LovManager.getLovObject(DOC.getEboContext(), lovName);
                    if(lovObj != null)
                    {
                        lovObj.beforeFirst();
                        lov = "<lov>";
                        while (lovObj.next())
                        {
                            lov += "<item><description>";
                            if(lovObj.getDescription() != null && lovObj.getDescription().trim().length() > 0)
                                lov += lovObj.getDescription() ;
                            else
                                lov += "(sem descricao)";
                            lov += "</description>";
                            lov += "<value>";
                            lov += lovObj.getCode();
                            lov += "</value>";
                            lov += "</item>";
                        }

                        lov += "</lov>";
                    }
                }
                catch (Exception e)
                {
                    logger.warn("Loading Lov ERROR : lovname= " + lovName);
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

            if (!charType.equals(""))
            {
                attributesStr.append("<").append(prefix).append(attr.getName()).append(" ");
                attributesStr.append(decimals);
                attributesStr.append(" type='" + charType + "' >");

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
        Counter index)
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
        Arrays.sort( listAttributesArray , new  queryBuilderHelper.labelComparator() );    

        

        StringBuffer[] strScript = new StringBuffer[listAttributes.size()];
        attributesStr.append("attributes=[];\n");
        attributesStr.append("foldersTree = gFld(\"Atributos\", \"\");\n");
        attributesStr.append("foldersTree.xID='rootNode';\n");

        // boolean
        int z = 0;

        for (i = 0; i < listAttributesArray.length ; i++) 
        {
            boDefAttribute attrDef = listAttributesArray[i];
            StringBuffer attrOptions = (StringBuffer) listAttributesOptions.get(attrDef.getName());

            strScript[z++] = getJAVASCRIPTAttribute(0, attributesStr, attrDef, attrOptions, includeAttributeObjects, "foldersTree", "", index);
        }

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
        boolean includeAttributeObjects, String parentItem, String parentAttribute, Counter index)
    {
        boolean haveChildren = false;
        StringBuffer toRet = new StringBuffer();
        boDefAttribute[] attrs2 = null;

        if (includeAttributeObjects && (attr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                (attr.getRelationType() == boDefAttribute.RELATION_1_TO_1))
        {
            boDefHandler relBo = attr.getReferencedObjectDef();
            String xname = attr.getName();

            if (!relBo.getName().equalsIgnoreCase("boObject"))
            {
                attrs2 = relBo.getAttributesDef();

                if ((level == 0) || !relBo.getBoCanBeOrphan())
                {
                    /* String xprefix = (prefix.length() == 0) ? "" : (prefix + ".");
                     String xprefixLabel = (prefixLabel.length() == 0) ? "" : (prefixLabel + "::");

                     if (xprefix.length() > 0)
                     {
                         int u = 0;
                     }
                     */
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

            for (int i = 0; i < attrs2.length; i++)
            {
                strScript[z++] = getJAVASCRIPTAttribute(level + 1, attributesStr, attrs2[i], null, includeAttributeObjects, parentItem + "_" + attr.getName(),
                        atrName, index);
            }

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

            attributesStr.append("]);");
        }

        return toRet;
    }
}
