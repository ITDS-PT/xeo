/*Enconding=UTF-8*/
package netgest.bo;

import java.io.File;

import java.util.ArrayList;

import netgest.bo.boException;

import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XMLNode;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class boDataSource
{
    /**
     *
     * @Company Enlace3
     * @since
     */
    private String name;
    private String username;
    private String password;
    private String dataSource;
    private String dataSourceDef;
    private String schema;
    private String parent;
    private boolean builtRuntime;

    public boDataSource(String name, String schema, String username,
        String password, String dataSource, String dataSourceDef, 
        String parent)
    {
        this.name = name;
        this.username = username;
        this.password = password;
        this.dataSource = dataSource;
        this.dataSourceDef = dataSourceDef;
        this.schema = schema;
        this.parent = parent;
        if(parent != null)
            builtRuntime = true;
    }

    public String getName()
    {
        return name;
    }

    public String getUserName()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getSchema()
    {
        return schema;
    }

    public String getDataSouce()
    {
        return dataSource;
    }

    public String getDataSouceDef()
    {
        return dataSourceDef;
    }

    public String getParent()
    {
        return parent;
    }

    public static boDataSource getDataSource(String name)
    {
        if (name != null)
        {
            String p_ngthome = System.getProperty("netgest.home");
            String cfgfile = p_ngthome + "boconfig.xml";
            XMLDocument xmldoc;

            try
            {
                xmldoc = ngtXMLUtils.loadXMLFile_FileReader(cfgfile);

                NodeList list = xmldoc.getChildNodes();
                Node aux;
                XMLNode auxNode;
                String auxS;

                for (int i = 0; i < list.getLength(); i++)
                {
                    aux = list.item(i);
                    auxS = getValue(aux, "Name");

                    if (name.equals(auxS))
                    {
                        String username = getValue(aux, "username");
                        String password = getValue(aux, "password");
                        String schema = getValue(aux, "schema");
                        String dataSource = getValue(aux, "DataSource");
                        String dataSourceDef = getValue(aux, "DataSourceDef");
                        String parent = getValue(aux, "parent");
                        String _name = getAttributeValue(aux, "name");                        
                        return new boDataSource(_name, username, password,
                            dataSource, dataSourceDef, schema, parent);
                    }
                }
            }
            catch (RuntimeException e)
            {
                String[] emsg = { cfgfile };
                throw new boException("netgest.bo.boDataSource.getDataSource()",
                    "BO-1201", e, emsg);
            }
            catch (Exception e)
            {
                throw new boException("netgest.bo.boDataSource.getDataSource()",
                    "BO-1201", e, e.getMessage());
            }
        }

        return null;
    }

    public static ArrayList getAllDataSource()
    {
        ArrayList toRet = new ArrayList();
        String p_ngthome = System.getProperty("netgest.home");
        String cfgfile = p_ngthome + "boconfig.xml";
        XMLDocument xmldoc;

        try
        {
            xmldoc = ngtXMLUtils.loadXMLFile_FileReader(cfgfile);

            NodeList list = xmldoc.getChildNodes();
            Node aux;
            String dataSource, dataSourceDef, name;
            String username, password, schema, parent;

            for (int i = 0; i < list.getLength(); i++)
            {
                 aux = list.item(i);
                 username = getValue(aux, "username");
                 password = getValue(aux, "password");
                 schema = getValue(aux, "schema");
                 dataSource = getValue(aux, "DataSource");
                 dataSourceDef = getValue(aux, "DataSourceDef");
                 parent = getValue(aux, "parent");
                 name = getAttributeValue(aux, "name");   
                 toRet.add(new boDataSource(name, username, password,
                            dataSource, dataSourceDef, schema, parent));
            }
        }
        catch (RuntimeException e)
        {
            String[] emsg = { cfgfile };
            throw new boException("netgest.bo.boDataSource.getDataSource()",
                "BO-1201", e, emsg);
        }
        catch (Exception e)
        {
            throw new boException("netgest.bo.boDataSource.getDataSource()",
                "BO-1201", e, e.getMessage());
        }

        return (toRet.size() == 0) ? null : toRet;
    }

    private static String getValue(Node n, String att)
    {
        NodeList l = n.getChildNodes();

        for (int i = 0; i < l.getLength(); i++)
        {
            if (att.equalsIgnoreCase(l.item(i).getNodeName()))
            {
                return l.item(i).getFirstChild().getNodeValue();
            }
        }

        return null;
    }

    private static String getAttributeValue(Node n, String attName)
    {
        NamedNodeMap att = n.getAttributes();
        if(att!=null) {
            Node x = att.getNamedItem(attName);
            return x==null?null:x.getNodeValue();
        }
        return null;
    }

    public void writeTo()
    {
        String p_ngthome = System.getProperty("netgest.home");
        String cfgfile = p_ngthome + "boconfig.xml";
        XMLDocument xmldoc;

        try
        {
            xmldoc = ngtXMLUtils.loadXMLFile_FileReader(cfgfile);
        }
        catch (RuntimeException e)
        {
            String[] emsg = { cfgfile };
            throw new boException("netgest.bo.boDataSource.writeTo", "BO-1201",
                e, emsg);
        }

        File f = new File(cfgfile);
        writeTo(xmldoc, f, this);
        boConfig.getApplicationConfig().refresh();
    }

    private synchronized static void writeTo(XMLDocument doc, File f,
        boDataSource source)
    {
        XMLElement elem = new XMLElement("Repository");
        XMLElement xdatasourceName = new XMLElement("Name");
        XMLElement xusername = new XMLElement("UserName");
        XMLElement xpassword = new XMLElement("Password");
        XMLElement xschema = new XMLElement("Schema");
        XMLElement xdtSource = new XMLElement("DataSource");
        XMLElement xdtSourceDef = new XMLElement("DataSourceDef");
        XMLElement xparent = new XMLElement("Parent");
        xdatasourceName.addText(source.name);
        xusername.addText(source.username);
        xpassword.addText(source.password);
        xschema.addText(source.schema);
        xdtSource.addText(source.dataSource);
        xdtSourceDef.addText(source.dataSourceDef);
        xparent.addText(source.parent);

        elem.appendChild(xdatasourceName);
        elem.appendChild(xusername);
        elem.appendChild(xpassword);
        elem.appendChild(xdtSource);
        elem.appendChild(xdtSourceDef);
        elem.appendChild(xschema);        
        elem.appendChild(xparent);

        Node root = getRoot(doc, "bo-config");
        Node datSourc = getRepositories(root);
        datSourc.appendChild(elem);
        ngtXMLUtils.saveXML(doc, f);
    }

    private static Node getRoot(XMLDocument doc, String boname)
    {
        NodeList n = doc.getChildNodes();
        Node aux;

        for (int i = 0; i < n.getLength(); i++)
        {
            aux = n.item(i);

            if (aux.getNodeName().equalsIgnoreCase(boname))
            {
                return aux;
            }
        }
        return null;
    }

    private static Node getRepositories(Node doc)
    {
        NodeList n= doc.getChildNodes();
        Node aux;
        for(int i = 0; i < n.getLength(); i++)
        {
            aux = n.item(i);
            String s = aux.getNodeName(); 
            if(aux.getNodeName().equalsIgnoreCase("Repositories"))
            {                
                return aux;
            }
        }
        return null;
    }     
}
