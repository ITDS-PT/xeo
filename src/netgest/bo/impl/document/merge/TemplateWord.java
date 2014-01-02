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
public class TemplateWord
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
    private String label;
    private ArrayList dsRequired = null;

    public TemplateWord(String name, String label)
    {
        this.name = name;
        this.label = label;
    }

    public TemplateWord(String name, String label, ArrayList dsRequired)
    {
        this.name = name;
        this.label = label;
        this.dsRequired = dsRequired;
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }

    public void addRequiredDS(DS ds)
    {
        if (dsRequired == null)
        {
            dsRequired = new ArrayList();
        }

        dsRequired.add(ds);
    }

    public ArrayList getRequiredDS()
    {
        return dsRequired;
    }
}
