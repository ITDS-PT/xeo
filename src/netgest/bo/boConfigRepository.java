/*Enconding=UTF-8*/
package netgest.bo;

import java.io.Serializable;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class boConfigRepository implements Serializable
{
    Node p_node;
    String p_constrdata;
    String p_constrdef;
    String p_constrdatadbm;
    String p_constrdefdbm;
    String p_name;

    public boConfigRepository(String name, Node xml)
    {
        p_name = name;

        NodeList nodes = xml.getChildNodes();

        for (byte i = 0; i < nodes.getLength(); i++)
        {
            Node cnode = nodes.item(i);

            if (cnode.getNodeName().equalsIgnoreCase("data-sources"))
            {
                NodeList dsnodes = cnode.getChildNodes();

                for (byte z = 0; z < dsnodes.getLength(); z++)
                {
                    Node dsnode = dsnodes.item(z);
                    String dsnoden = dsnode.getNodeName();

                    if (dsnoden.equalsIgnoreCase("data"))
                    {
                        p_constrdata = dsnode.getFirstChild().getNodeValue();
                    }
                    else if (dsnoden.equalsIgnoreCase("definitions"))
                    {
                        p_constrdef = dsnode.getFirstChild().getNodeValue();
                    }
                    else if (dsnoden.equalsIgnoreCase("datadbm"))
                    {
                        p_constrdatadbm = dsnode.getFirstChild().getNodeValue();
                    }
                    else if (dsnoden.equalsIgnoreCase("definitionsdbm"))
                    {
                        p_constrdefdbm = dsnode.getFirstChild().getNodeValue();
                    }
                }
            }
        }
    }

    public String getConnectionStringData()
    {
        return p_constrdata;
    }

    public String getConnectionStringDef()
    {
        return p_constrdef;
    }

    public String getConnectionStringDataDbm()
    {
        return p_constrdatadbm;
    }

    public String getConnectionStringDefDbm()
    {
        return p_constrdefdbm;
    }

    public String getName()
    {
        return p_name;
    }
}
