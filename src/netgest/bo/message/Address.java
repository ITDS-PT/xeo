/*Enconding=UTF-8*/
package netgest.bo.message;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public interface Address
{
    public String getFullAddress();

    public String getName();

    public String getAddress();

    public String getPostalCode();

    public String getLocation();

    public String getCountry();

    public String getCity();

    public String getXEOID();
}
