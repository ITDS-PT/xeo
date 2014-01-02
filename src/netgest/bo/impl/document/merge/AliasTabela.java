/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge;

import java.util.Hashtable;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class AliasTabela
{
    private Hashtable alias = new Hashtable();
    private Hashtable values = new Hashtable();

    /**
     *
     * @Company Enlace3
     * @since
     */
    public AliasTabela()
    {
    }

    public void insertAlias(String _alias, String value)
    {
        alias.put(_alias, value);
        values.put(value, _alias);
    }

    public String verifyAlias(String _alias)
    {
        Object o = alias.get(_alias);

        return (o == null) ? _alias : (String) o;
    }

    public String verifyValue(String value)
    {
        Object o = values.get(value);

        return (o == null) ? value : (String) o;
    }
}
