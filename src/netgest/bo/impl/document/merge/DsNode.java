/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge;

import java.util.ArrayList;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class DsNode
{
    /**
     *
     * @Company Enlace3
     * @since
     */
    private String name;

    /**
     *
     * @Company Enlace3
     * @since
     */
    private String dsType;
    private ArrayList groups = null;

    public DsNode(String name, String dsType)
    {
        this.name = name;
        this.dsType = dsType;
    }

    public DsNode(String name, String dsType, ArrayList groups)
    {
        this.name = name;
        this.dsType = dsType;
        this.groups = groups;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return dsType;
    }

    public void addGroup(DsGroup g)
    {
        if (groups == null)
        {
            groups = new ArrayList();
        }

        groups.add(g);
    }

    public ArrayList getGroups()
    {
        return groups;
    }
}
