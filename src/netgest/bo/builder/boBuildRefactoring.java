/*Enconding=UTF-8*/
package netgest.bo.builder;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.system.boRepository;

import netgest.utils.ngtXMLHandler;

import java.sql.CallableStatement;
import java.sql.Connection;

import javax.naming.InitialContext;
import netgest.bo.system.Logger;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class boBuildRefactoring
{

    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.builder.boBuildRefactoring");
    
    private EboContext p_eboctx;
    private boRepository p_repository;

    /**
     *
     * @since
     */
    public boBuildRefactoring(EboContext ebo)
    {
        p_eboctx = ebo;
        p_repository = ebo.getBoSession().getRepository();
    }

    public void build(ngtXMLHandler[] updates) throws boRuntimeException
    {
        Connection con = null;
        CallableStatement csm = null;
        int n;

        try
        {
            final InitialContext ic = new InitialContext();
            String update = "";
            con = p_eboctx.getDedicatedConnectionData();

            for (int i = 0; i < updates.length; i++)
            {
                try
                {
                    ngtXMLHandler[] sqls = updates[i].getChildNodes();

                    if ((sqls != null) && (sqls.length > 0))
                    {
                        for (int j = 0; j < sqls.length; j++)
                        {
                            update = sqls[j].getText();
                            csm = con.prepareCall(update);
                            n = csm.executeUpdate();
                            csm.close();
                            logger.finest(LoggerMessageLocalizer.getMessage("EXECUTED_QUERY")+" (" +
                                sqls[j].getText() + ") "+LoggerMessageLocalizer.getMessage("UPDATED")+" " + n +
                                " "+LoggerMessageLocalizer.getMessage("RECORDS"));
                        }
                    }

                    con.commit();
                }
                catch (Exception e)
                {
                    logger.finest(LoggerMessageLocalizer.getMessage("ERROR")+" (" + e.getMessage() +
                        ") "+LoggerMessageLocalizer.getMessage("EXECUTING_UPDATE")+"(" + update + ").");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (csm != null)
                {
                    csm.close();
                }
            }
            catch (Exception e)
            {
                //ignora
            }

            try
            {
                if (con != null)
                {
                    con.close();
                }
            }
            catch (Exception e)
            {
                //ignora
            }
        }
    }
}
