/*Enconding=UTF-8*/
package netgest.bo.def;

import java.io.File;

import java.util.ArrayList;
import java.util.Hashtable;

import netgest.bo.boConfig;
import netgest.bo.impl.document.merge.DS;
import netgest.bo.impl.document.merge.DsField;
import netgest.bo.impl.document.merge.DsGroup;
import netgest.bo.impl.document.merge.TemplateHTML;
import netgest.bo.impl.document.merge.TemplateWord;

import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;

import org.w3c.dom.Node;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class boDefPrinterDefinitions extends ngtXMLHandler
{
    private static Hashtable p_cachedef = new Hashtable();
    public boDefHandler p_bodef;
    private ngtXMLHandler[] p_xmlnodes;
    private Node p_node;
    private ArrayList p_wsd;
    private ArrayList p_templateHtml;
    private ArrayList p_templateWord;

    /**
     *
     * @Company Enlace3
     * @since
     */
    public boDefPrinterDefinitions(boDefHandler p_bodef, Node node)
    {
        super(node);
        goDocumentElement();
        this.p_node = node;
        this.p_bodef = p_bodef;
        refresh();
    }

    public String getDefaultTemplate()
    {
        if ((p_templateWord != null) && (p_templateWord.size() > 0))
        {
            return ((TemplateWord) p_templateWord.get(0)).getName();
        }

        return null;
    }

    public static boDefPrinterDefinitions loadPrinterDefinitions(String object)
    {
        boDefPrinterDefinitions ret = null;

        try
        {
            ret = (boDefPrinterDefinitions) p_cachedef.get(object);

            if (ret == null)
            {
                boConfig boconf = new boConfig();
                String fName = boconf.getWordTemplateConfig().getProperty("path") +
                                File.separator + object + netgest.bo.builder.boBuilder.TYPE_WSD;
                if(new File(fName).exists())
                {
                
                    XMLDocument doc = ngtXMLUtils.loadXMLFile(boconf.getWordTemplateConfig()
                                                                    .getProperty("path") +
                            File.separator + object + netgest.bo.builder.boBuilder.TYPE_WSD);
                    ret = new boDefPrinterDefinitions(boDefHandler.getBoDefinition(object), doc);
                    p_cachedef.put(object, ret);
                }
            }

            return ret;
        }
        catch (Exception e)
        {
            return ret;
        }
    }

    public void refresh()
    {
        ngtXMLHandler xnode;
        ngtXMLHandler[] xnodes;
        xnode = super.getChildNode("wds");

        if (xnode != null)
        {
            xnodes = xnode.getChildNodes();

            DS dsObj;

            for (int i = 0; i < xnodes.length; i++)
            {
                if ("ds".equalsIgnoreCase(xnodes[i].getNodeName()))
                {
                    dsObj = getDS(xnodes[i]);

                    if (p_wsd == null)
                    {
                        p_wsd = new ArrayList();
                    }

                    p_wsd.add(dsObj);
                }
            }
        }

        //template
        xnode = super.getChildNode("templates");

        if (xnode != null)
        {
            TemplateHTML tHtml;
            TemplateWord tWord;
            xnodes = xnode.getChildNodes();

            for (int i = 0; i < xnodes.length; i++)
            {
                if ("templateHTML".equalsIgnoreCase(xnodes[i].getNodeName()))
                {
                    tHtml = getTemplateHtml(xnodes[i]);

                    if (tHtml != null)
                    {
                        if (p_templateHtml == null)
                        {
                            p_templateHtml = new ArrayList();
                        }

                        p_templateHtml.add(tHtml);
                    }
                }
                else if ("templateWord".equalsIgnoreCase(
                            xnodes[i].getNodeName()))
                {
                    tWord = getTemplateWord(xnodes[i]);

                    if (tWord != null)
                    {
                        if (p_templateWord == null)
                        {
                            p_templateWord = new ArrayList();
                        }

                        p_templateWord.add(tWord);
                    }
                }
            }
        }
    }

    public DS getDS(String name)
    {
        for (int i = 0; i < p_wsd.size(); i++)
        {
            if (name.equalsIgnoreCase(((DS) p_wsd.get(i)).getName()))
            {
                return (DS) p_wsd.get(i);
            }
        }

        return null;
    }

    public boolean isWordTemplate(String name)
    {
        for (int i = 0; i < p_templateWord.size(); i++)
        {
            if (name.equalsIgnoreCase(
                        ((TemplateWord) p_templateWord.get(i)).getName()))
            {
                return true;
            }
        }

        return false;
    }

    public boolean existWordTemplates()
    {
        return ((p_templateWord == null) || (p_templateWord.size() == 0))
        ? false : true;
    }
    
    public int getNumberOfWordTemplates()
    {
        return ((p_templateWord == null) || (p_templateWord.size() == 0)) ? 0: p_templateWord.size();
    }

    public boolean isHTMLTemplate(String name)
    {
        for (int i = 0; i < p_templateHtml.size(); i++)
        {
            if (name.equalsIgnoreCase(
                        ((TemplateHTML) p_templateHtml.get(i)).getName()))
            {
                return true;
            }
        }

        return false;
    }

    public TemplateWord getTemplateWord(String name)
    {
        for (int i = 0; i < p_templateWord.size(); i++)
        {
            if (name.equalsIgnoreCase(
                        ((TemplateWord) p_templateWord.get(i)).getName()))
            {
                return (TemplateWord) p_templateWord.get(i);
            }
        }

        return null;
    }
    
    public ArrayList getTemplateWord()
    {
        return p_templateWord;
    }

    public TemplateHTML getTemplateHTML(String name)
    {
        for (int i = 0; i < p_templateHtml.size(); i++)
        {
            if (name.equalsIgnoreCase(
                        ((TemplateHTML) p_templateHtml.get(i)).getName()))
            {
                return (TemplateHTML) p_templateHtml.get(i);
            }
        }

        return null;
    }

    private TemplateHTML getTemplateHtml(ngtXMLHandler tempHtmlnode)
    {
        TemplateHTML tempHtml = new TemplateHTML(tempHtmlnode.getAttribute(
                    "name"), tempHtmlnode.getAttribute("label"),
                tempHtmlnode.getAttribute("viewer"));

        return tempHtml;
    }

    private TemplateWord getTemplateWord(ngtXMLHandler tempWordnode)
    {
        TemplateWord tempWordObj = new TemplateWord(tempWordnode.getAttribute(
                    "name"), tempWordnode.getAttribute("label"));
        ngtXMLHandler reqNode = tempWordnode.getChildNode("dsrequired");

        if (reqNode != null)
        {
            DS aux;
            ngtXMLHandler[] requNodes = reqNode.getChildNodes();

            for (int i = 0; i < requNodes.length; i++)
            {
                aux = getDS(requNodes[i].getNodeName());

                if (aux != null)
                {
                    tempWordObj.addRequiredDS(aux);
                }
            }
        }

        return tempWordObj;
    }

    private DS getDS(ngtXMLHandler dsnode)
    {
        DS dsObj = new DS(dsnode.getAttribute("name", ""),
                dsnode.getAttribute("dstype", "block"));
        ngtXMLHandler[] groupsNode = dsnode.getChildNodes();
        DsGroup group;

        for (int i = 0; i < groupsNode.length; i++)
        {
            if ("group".equalsIgnoreCase(groupsNode[i].getNodeName()))
            {
                group = getGroup(groupsNode[i]);
                dsObj.addGroup(group);
            }
        }

        return dsObj;
    }

    private DsGroup getGroup(ngtXMLHandler groupnode)
    {
        DsGroup groupObj = new DsGroup(groupnode.getAttribute("alias"),
                groupnode.getAttribute("wildcard"),
                groupnode.getAttribute("relatedDS"));
        ngtXMLHandler fieldsNode = groupnode.getChildNode("fields");

        if (fieldsNode != null)
        {
            ngtXMLHandler[] fieldNode = fieldsNode.getChildNodes();
            DsField field;

            for (int i = 0; i < fieldNode.length; i++)
            {
                if ("field".equalsIgnoreCase(fieldNode[i].getNodeName()))
                {
                    field = getField(fieldNode[i]);
                    groupObj.addField(field);
                }
            }
        }

        return groupObj;
    }

    private DsField getField(ngtXMLHandler fieldnode)
    {
        DsField fieldObj = new DsField(fieldnode.getAttribute("alias"),
                fieldnode.getAttribute("format"),
                fieldnode.getAttribute("type"),
                fieldnode.getChildNode("value").getText(),
                fieldnode.getAttribute("repeatBlock"),
                fieldnode.getAttribute("initValue"));

        return fieldObj;
    }

    public static boolean existsWordTemplatesFor(
        netgest.bo.runtime.boObject obj)
    {
        return existsWordTemplatesFor(obj.getName());
    }

    public static boolean existsWordTemplatesFor(String objName)
    {
        String path = getWsdFile(objName);
        File f = new File(path);

        if (f.exists())
        {
            boDefPrinterDefinitions objBoDefPrinter = loadPrinterDefinitions(objName);

            return objBoDefPrinter.existWordTemplates();
        }

        return false;
    }

    public static String getWsdFile(netgest.bo.runtime.boObject obj)
    {
        return getWsdFile(obj.getName());
    }

    public static String getWsdFile(String objName)
    {
        return boConfig.getWordTemplateConfig().getProperty("path") +
        File.separator + objName + netgest.bo.builder.boBuilder.TYPE_WSD;
    }
}
