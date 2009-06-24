/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class TemplateHTML
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

    /**
     *
     * @Company Enlace3
     * @since
     */
    private String viewer;

    public TemplateHTML(String name, String label, String viewer)
    {
        this.name = name;
        this.label = label;
        this.viewer = viewer;
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }

    public String getViewer()
    {
        return viewer;
    }
}
