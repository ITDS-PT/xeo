/*Enconding=UTF-8*/
package netgest.bo.data;

import java.util.Hashtable;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.*;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XSLException;

import org.w3c.dom.NodeList;
import netgest.bo.system.Logger;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class DriverManager
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.data.DriverManager");

    /**
     *
     * @since
     */
    private Hashtable p_drivers;
    private boApplication p_app;

    public DriverManager(boApplication app)
    {
        p_app = app;
        refreshDrivers();
    }

    public void refreshDrivers()
    {
        try
        {
            p_drivers = new Hashtable();

            XMLElement nds = (XMLElement) p_app.getApplicationConfig().getDataSourcesNode();
            NodeList ldrivers = nds.selectNodes("DataSource");
            int nd = ldrivers.getLength();

            for (int i = 0; i < nd; i++)
            {
                XMLElement xnode = (XMLElement) ldrivers.item(i);
                String name = xnode.getAttribute("name");
                String driver = xnode.selectSingleNode("Driver").getFirstChild()
                                     .getNodeValue();
                String ddlds = xnode.selectSingleNode("DDL").getFirstChild()
                                    .getNodeValue();
                String dmlds = xnode.selectSingleNode("DML").getFirstChild()
                                    .getNodeValue();

                try
                {
                    Driver xd = (Driver) Class.forName(driver).newInstance();
                    xd.initializeDriver(name, dmlds, ddlds);
                    p_drivers.put(name, xd);
                }
                catch (InstantiationException e)
                {
                    logger.finest(
                        "netgest.bo.data.DriverManager: InstantiationException when loading Driver Class [" +
                        driver + "]", e);
                }
                catch (IllegalAccessException e)
                {
                    logger.finest(
                        "netgest.bo.data.DriverManager: IllegalAccessException when loading Driver Class [" +
                        driver + "]", e);
                }
                catch (ClassNotFoundException e)
                {
                    logger.finest(
                        "netgest.bo.data.DriverManager: ClassNotFoundException when loading Driver Class [" +
                        driver + "]", e);
                }
            }
        }
        catch (XSLException e)
        {
            logger.severe("Error: ",e);
        }
    }

    public ReaderAdapter createReaderAdapter(EboContext ctx, String source)
    {
        Driver xd = (Driver) p_drivers.get(source);
        ReaderAdapter ra = null;

        if (xd != null)
        {
            ra = xd.createReaderAdapter(ctx);
        }
        else
        {
            throw new RuntimeException(
                "netgest.bo.data.DriverManager: DataSource not found (" +
                source + ")");
        }

        return ra;
    }

    public WriterAdapter createWriterAdapter(EboContext ctx, String source)
    {
        Driver xd = (Driver) p_drivers.get(source);
        WriterAdapter ra = null;
        ra = xd.createWriterAdapter(ctx);

        return ra;
    }

    public DriverUtils getDriverUtils(String source)
    {
        Driver xd = (Driver) p_drivers.get(source);
        DriverUtils ra = null;

        if (xd != null)
        {
            ra = xd.getDriverUtils();
        }

        return ra;
    }

    public Driver getDriver(String dataSourceName)
    {
        Driver xd = (Driver) p_drivers.get(dataSourceName);

        if (xd == null)
        {
            throw new RuntimeException("DataSource [" + dataSourceName +
                "] not found in configuration. check please in boconfig.xml file the tag DataSources.");
        }

        return xd;
    }
}
