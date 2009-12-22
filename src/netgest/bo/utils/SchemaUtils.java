/*Enconding=UTF-8*/
package netgest.bo.utils;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;

import netgest.bo.builder.boBuilder;
import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.boRepository;
import netgest.bo.system.boSession;
import netgest.bo.system.Logger;

/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class SchemaUtils
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.utils.SchemaUtils");

    //objectos semi-privados
    private static final String[] XEO_SEMI_PRIVATE_OBJ = 
    {
        "Ebo_ClsReg", "Ebo_Registry", "Ebo_Attribute", "Ebo_Method",
        "Ebo_Package", "Ebo_Policy", "Ebo_PolicyRule", "Ebo_Template"
    };

    //objectos privados
    private static final String[] XEO_PRIVATE_OBJ = {  };

    /**
     *
     * @Company Enlace3
     * @since
     */
    public SchemaUtils()
    {
    }

    public static final String[] getSchemas(boSession session)
    {
        String[] toRet = null;
        ArrayList r = new ArrayList();
        EboContext eboctx = null;

        try
        {
            long boui = session.getUser().getBoui();

            if (boui == 0)
            {
                return allSchemas(session);
            }

            boolean existDefault = false;
            String aux;
            eboctx = session.createRequestContext(null, null, null);

            boObject user = boObject.getBoManager().loadObject(eboctx,
                    boui);
            bridgeHandler bh = user.getBridge("repositories");

            if (bh != null)
            {
                bh.beforeFirst();

                while (bh.next())
                {
                    aux = bh.getObject().getAttribute("name").getValueString();

                    if ("default".equalsIgnoreCase(aux))
                    {
                        existDefault = true;

                        if (!r.contains(aux))
                        {
                            r.add(aux);
                        }
                    }
                    else if (!r.contains(aux) &&
                            boRepository.existsRepository(
                                session.getApplication(), aux))
                    {
                        r.add(aux);
                    }
                }

                if (existDefault)
                {
                    toRet = new String[r.size()];
                }
                else
                {
                    toRet = new String[r.size() + 1];
                }

                for (int i = 0; i < r.size(); i++)
                {
                    toRet[i] = (String) r.get(i);
                }

                if (!existDefault)
                {
                    toRet[r.size()] = "default";
                }

                //ordenar
                Arrays.sort(toRet);

                if (!"default".equalsIgnoreCase(toRet[0]))
                {
                    toRet = moveDefaultUp(toRet);
                }
            }
        }
        catch (boRuntimeException e)
        {
        }
        finally
        {
            if ( eboctx!= null) eboctx.close();
        }

        if (toRet == null)
        {
            toRet = new String[1];
            toRet[0] = "default";
        }

        return toRet;
    }

    private static final String[] allSchemas(boSession session)
    {
        String[] toRet = null;
        ArrayList r = new ArrayList();
        EboContext eboctx = null;

        boRepository[] all = boRepository.getAllRepositories(session.getApplication());
        toRet = new String[all.length];

        for (int i = 0; i < all.length; i++)
        {
            toRet[i] = all[i].getName();
        }

        Arrays.sort(toRet);
        toRet = moveDefaultUp(toRet);

        if ((toRet == null) || (toRet.length == 0))
        {
            toRet = new String[1];
            toRet[0] = "default";
        }

        return toRet;
    }

    public static final boolean existSchema(EboContext ctx, String schemaName)
        throws boRuntimeException
    {
        boObject repository = boObject.getBoManager().loadObject(ctx,
                "Ebo_Repository", "SCHEMANAME='" + schemaName + "'");

        if ((repository != null) &&
                schemaName.equalsIgnoreCase(repository.getAttribute("name")
                                                          .getValueString()))
        {
            return true;
        }

        return false;
    }

    private static final String[] moveDefaultUp(String[] values)
    {
        String[] toRet = new String[values.length];
        toRet[0] = "default";

        int pos = 1;

        for (int i = 0; i < values.length; i++)
        {
            if (!"default".equalsIgnoreCase(values[i]))
            {
                toRet[pos] = values[i];
                pos++;
            }
        }

        return toRet;
    }

    public static final void createSchema(boObject objController)
        throws boRuntimeException
    {
        OracleDBM dbm = null;
        EboContext ctx = objController.getEboContext();

        try
        {
            String schemaName = objController.getName() + "_" +
                objController.getBoui();
            logger.finer("criar o schema: " + schemaName);
            dbm = new OracleDBM();
            dbm.setEnvironment(ctx);

            if (!dbm.existsSchema(schemaName))
            {
                dbm.createSchema(schemaName, schemaName,
                    objController.getName(), objController.getBoui());
                //boBuilder.buildAll(ctx, schemaName);
            }

            //criação objecto Ebo_Repository
            boObject repository = boObject.getBoManager().loadObject(ctx,
                    "Ebo_Repository", "NAME='" + schemaName + "'");

            if (repository != null)
            {
                repository.getAttribute("name").setValueString(schemaName);
                repository.getAttribute("schemaName").setValueString(schemaName);
                repository.update();
            }
        }
        catch (SQLException e)
        {
            throw new boRuntimeException("", e.getMessage(), null);
        }
        finally
        {
            if (dbm != null)
            {
                dbm.close();
            }
        }
    }

    public static boolean definedAsSemiPrivate(String objName)
    {
        return existIn(objName, XEO_SEMI_PRIVATE_OBJ);
    }

    public static boolean definedAsPrivate(String objName)
    {
        return existIn(objName, XEO_PRIVATE_OBJ);
    }

    private static boolean existIn(String objName, String[] objsType)
    {
        for (int i = 0; i < objsType.length; i++)
        {
            if (objsType[i].equals(objName))
            {
                return true;
            }
        }

        return false;
    }
}
