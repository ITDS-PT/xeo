/*Enconding=UTF-8*/
package netgest.bo.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Enumeration;
import java.util.Vector;

import netgest.bo.data.DataRow;
import netgest.bo.data.DataSet;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boAttributesArray;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectUpdateQueue;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;


/**
 *
 * @Company Enlace3
 * @author Luís Eduardo Moscoso Barreira
 * @version 1.0
 * @since
 */
public class boVersioning
{
    //vector que irá conter os objectos a alterar
    private Vector rollbackObjects = new Vector();
    //vector que irá conter os objectos a apagar
    private Vector destroyObjects = new Vector();

    public boVersioning()
    {
    }

    /**
    *  Retorna os logs relativos a um certo atributo
    *
    * @param objBrHd bridge para os logs
    * @param name nome do atributo
    * @return objectos de logs relativos ao atributo
    * @throws netgest.bo.runtime.boRuntimeException
    */
    private boObject[] getLog(bridgeHandler objBrHd, String name) throws boRuntimeException
    {
        return getLog(objBrHd, name, -1);
    }

    /**
    *  Retorna os logs relativos a um certo atributo
    *
    * @param objBrHd bridge para os logs
    * @param name name nome do atributo
    * @param line linha a que os atributos pertemcem(só válido para atributos de bridge), -1 para ignorar linha
    * @return objectos de logs relativos ao atributo
    * @throws netgest.bo.runtime.boRuntimeException
    */
    private boObject[] getLog(bridgeHandler objBrHd, String name, int line)
        throws boRuntimeException
    {
        Vector ret = new Vector();

        objBrHd.beforeFirst();

        while (objBrHd.next())
        {
            boObject obj = objBrHd.getObject();

            if (obj.getAttribute("attribute").getValueString().equalsIgnoreCase(name) &&
                    (((line != -1) && (obj.getAttribute("line").getValueLong() == line)) ||
                    (line == -1)))
            {
                ret.add(obj);
            }
        }

        return (boObject[]) ret.toArray(new boObject[ret.size()]);
    }

    /**
    * Retorna os logs relativos aos atributos de uma certa linha
    *
    * @param objBrHd bridge para os logs
    * @param line linha a que os atributos pertemcem(só válido para atributos de bridge)
    * @return objectos de logs relativos ao atributo
    * @throws netgest.bo.runtime.boRuntimeException
    */
    private bridgeHandler removeLineLog(bridgeHandler objBrHd, int line) throws boRuntimeException
    {
        objBrHd.beforeFirst();

        while (objBrHd.next())
        {
            boObject obj = objBrHd.getObject();

            if ((obj.getAttribute("line").getValueLong() == line) &&
                    obj.getAttribute("action").getValueString().equalsIgnoreCase("NSERT"))
            {
                objBrHd.remove();
            }
        }

        return objBrHd;
    }

    /**
    * Retorna um certo objecto a uma versão anterior
    *
    * @param version objecto de versão para o qual se deve retornar o onjecto
    * @param rollBackChilds verdadeiro se se deve fazer o rollback dos filhos do objecto
    * @return verdadeiro se operação bem sucedida
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public boolean rollbackVersion(boObject version, boolean rollBackChilds)
        throws boRuntimeException
    {
        EboContext ctx = version.getEboContext();

        //boui do objecto a alterar
        long boui = version.getAttribute("changedObject").getValueLong();

        //versão actual do objecto        
        long actVer = version.getAttribute("version").getValueLong();

        //buscar o objecto a fazer o rollback
        boObject obj = null;

        try
        {
            obj = boObject.getBoManager().loadObject(ctx, boui);
        }
        catch (Exception e)
        {
        }

        //seleciona todas as versões do objecto posteriores a versão para qual se pretende alterar
        boObjectList boList = boObjectList.list(ctx,
                "SELECT Ebo_Versioning WHERE Ebo_Versioning.changedObject=" + boui +
                " AND Ebo_Versioning.version>=" + actVer + " ORDER BY version DESC");

        //boList.setOrderBy("version", false);
        //fazer o backtraking
        boList.beforeFirst();
        for (int l = 0; l < boList.getPages(); l++,  boList.nextPage()) 
        {        
        while (boList.next())
        {
            boObject[] log = null;
            boObject currentVersion = boList.getObject();

            bridgeHandler objBrHd = currentVersion.getBridge("log");

            //quando se chega a uma versão em que o objecto foi criado, não se faz nada
            objBrHd.beforeFirst();

            while (objBrHd.next())
            {
                if (objBrHd.getObject().getAttribute("attribute").getValueString().equalsIgnoreCase("BOUI") &&
                        objBrHd.getObject().getAttribute("action").getValueString()
                                   .equalsIgnoreCase("INSERT"))
                {
                    continue;
                }
            }

            //
            objBrHd.beforeFirst();

            //se o obj está a nulo então ele foi realmente apagado do sistema, 
            //logo a que criar um novo, para tal é necessário saber o classname, 
            //que está no primeiro objecto versionig da lista
            if (obj == null)
            {
                log = getLog(objBrHd, "CLASSNAME");

                //quando se sabe o classname já se pode criar um objecto novo
                obj = boObject.getBoManager().createObject(ctx,
                        log[0].getAttribute("value_String").getValueString(), boui);
            }

            boDefAttribute[] atts = obj.getBoDefinition().getBoAttributes();

            // Pecorrer todos os atributos do objecto
            for (int x = 0; x < atts.length; x++)
            {
                String name = atts[x].getName();

                log = getLog(objBrHd, name);

                String action = null;

                //atributo tabled
                if (obj.getAttribute(name).getDefAttribute().getDbIsTabled())
                {
                    //testar se foi alterado o conteudo da bridge
                    log = getLog(objBrHd, name);

                    if (log.length == 0)
                    {
                        continue;
                    }

                    // Buscar as rows do atributo
                    Object[] values = (Object[]) obj.getAttribute(name).getValueObject();
                    Vector valVec = new Vector();

                    if (values != null)
                    {
                        for (int i = 0; i < values.length; i++)
                        {
                            valVec.add(values[i]);
                        }
                    }

                    for (int i = 0; i < log.length; i++)
                    {
                        action = log[i].getAttribute("action").getValueString();

                        long line = log[i].getAttribute("line").getValueLong();
                        String type = log[i].getAttribute("type").getValueString();
                        Object value = null;

                        if (type.equalsIgnoreCase("BOOLEAN") || type.equalsIgnoreCase("CHAR") ||
                                type.equalsIgnoreCase("CLOB"))
                        {
                            value = log[i].getAttribute("value_String").getValueObject();
                        }
                        else if (type.equalsIgnoreCase("CURRENCY") ||
                                type.equalsIgnoreCase("NUMBER"))
                        {
                            value = log[i].getAttribute("value_Long").getValueObject();
                        }
                        else if (type.equalsIgnoreCase("DATE") ||
                                type.equalsIgnoreCase("DATETIME") ||
                                type.equalsIgnoreCase("DURATION"))
                        {
                            value = log[i].getAttribute("value_Date").getValueObject();
                        }

                        //apaga-se o valor novo
                        if (action.equalsIgnoreCase("INSERT"))
                        {
                            valVec.remove((int) line - 1);
                        }

                        //quando é um caso de update insere-se o antigo e apaga-se o novo
                        else if (action.equalsIgnoreCase("UPDATE"))
                        {
                            valVec.add((int) line - 1, value);
                            valVec.remove((int) line);
                        }

                        //quando é um caso de delete insere-se o antigo
                        else if (action.equalsIgnoreCase("DELETE"))
                        {
                            valVec.add((int) line - 1, value);
                        }
                    }

                    //adicionar o novo array
                    if (valVec.size() > 0)
                    {
                        obj.getAttribute(name).setValueObject(valVec.toArray(
                                new Object[valVec.size()]));
                    }
                    else
                    {
                        obj.getAttribute(name).setValueObject(null);
                    }
                }

                //atributo bridge
                else if (((obj.getAttribute(name).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                        (obj.getAttribute(name).getDefAttribute().getMaxOccurs() > 1)))
                {
                    bridgeHandler brHd = null;

                    //testar se foi alterado o conteudo da bridge
                    log = getLog(objBrHd, name + "." + name);

                    for (int i = 0; i < log.length; i++)
                    {
                        action = log[i].getAttribute("action").getValueString();

                        long line = log[i].getAttribute("line").getValueLong();
                        long bouiBr = log[i].getAttribute("value_Long").getValueLong();
                        brHd = obj.getBridge(name);

                        //é necessário testar se a bridge foi apagada
                        if (action.equalsIgnoreCase("DELETE"))
                        {
                            //verifica se o objecto existe
                            boObject objBr = null;

                            try
                            {
                                objBr = boObject.getBoManager().loadObject(ctx, bouiBr);
                            }
                            catch (Exception e)
                            {
                            }

                            //se não existe é preciso fazer o rollback dele
                            if ((objBr == null) && rollBackChilds)
                            {
                                boolean sucess = rollbackVersion(ctx, bouiBr, rollBackChilds);

                                if (!sucess)
                                {
                                    return sucess;
                                }

                                objBr = boObject.getBoManager().loadObject(ctx, bouiBr);
                            }
                            else if (objBr == null)
                            {
                                return false;
                            }

                            //adicionar o objecto a bridge e move-lo para a row certa
                            brHd.add(bouiBr);
                            brHd.moveRowTo((int) line);
                        }

                        //caso contrário foi inserida
                        else
                        {
                            //procurar a bridge
                            brHd.beforeFirst();

                            while (brHd.next())
                            {
                                boObject objBr = brHd.getObject();

                                if (objBr.getBoui() == bouiBr)
                                {
                                    //quando encontra a bridge remove o objecto
                                    brHd.remove();
                                }
                            }

                            //remover todos as entradas no log dos atributos desta bridge
                            removeLineLog(objBrHd, (int) line);
                        }
                    }

                    brHd = obj.getBridge(name);

                    Enumeration emBr = brHd.getAllAttributes().elements();

                    // Pecorrer todos os atributos da bridge.
                    while (emBr.hasMoreElements())
                    {
                        String nameBr = ((AttributeHandler) emBr.nextElement()).getDefAttribute()
                                         .getName();

                        if (name.equalsIgnoreCase(nameBr))
                        {
                            continue;
                        }

                        log = getLog(objBrHd, name + "." + nameBr);

                        //testar se o atributo foi alterado
                        for (int i = 0; i < log.length; i++)
                        {
                            action = log[i].getAttribute("action").getValueString();

                            long line = log[i].getAttribute("line").getValueLong();
                            String type = log[i].getAttribute("type").getValueString();
                            long versionValue = log[i].getAttribute("version").getValueLong();

                            brHd.moveTo((int) line);

                            //para um caso de insert a que apagar o atributo
                            if (action.equalsIgnoreCase("INSERT"))
                            {
                                //se é do tipo object pode ser necessário apagar o objecto da lista de objecto a gravar
                                if (obj.getAttribute(name).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                                {
                                    long bouiObj = log[i].getAttribute("value_Long").getValueLong();
                                    removeObject(ctx, bouiObj);
                                }

                                //quando encontra a bridge apaga o atributo
                                brHd.getAttribute(nameBr).setValueObject(null);
                            }

                            //quando é um filho não orfão que é alterado
                            else if (action.equalsIgnoreCase("UPDATE_CHILD"))
                            {
                                return rollbackVersion(ctx,
                                    log[i].getAttribute("value_Long").getValueLong(), versionValue,
                                    true);
                            }

                            //quando é um caso de update ou delete altera-se o valor
                            else
                            {
                                //testar se é do tipo objecto e se este novo existe
                                if (obj.getAttribute(name).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                                {
                                    long bouiObj = log[i].getAttribute("value_Long").getValueLong();

                                    //verifica se o objecto existe
                                    boObject objBr = null;

                                    try
                                    {
                                        objBr = boObject.getBoManager().loadObject(ctx, bouiObj);
                                    }
                                    catch (Exception e)
                                    {
                                    }

                                    //testar se o objecto implementa versioning, e se é orfão
                                    String classname = boVersioning.getObjectClassname(ctx, bouiObj);
                                    boolean orphan = true;

                                    if (classname != null)
                                    {
                                        orphan = boDefHandler.getBoDefinition(classname)
                                                             .getBoCanBeOrphan();
                                    }

                                    //se não existe é preciso fazer o rollback dele
                                    if ((objBr == null) && rollBackChilds)
                                    {
                                        boolean sucess = false;

                                        if (!orphan)
                                        {
                                            sucess = rollbackVersion(ctx, bouiObj, versionValue,
                                                    rollBackChilds);
                                        }
                                        else
                                        {
                                            sucess = rollbackVersion(ctx, bouiObj, rollBackChilds);
                                        }

                                        if (!sucess)
                                        {
                                            return sucess;
                                        }

                                        objBr = boObject.getBoManager().loadObject(ctx, bouiObj);
                                    }
                                    else if (objBr == null)
                                    {
                                        return false;
                                    }

                                    brHd.getAttribute(nameBr).setValueLong(log[i].getAttribute(
                                            "value_Long").getValueLong());
                                }
                                else if (type.equalsIgnoreCase("BOOLEAN") ||
                                        type.equalsIgnoreCase("CHAR") ||
                                        type.equalsIgnoreCase("CLOB"))
                                {
                                    brHd.getAttribute(nameBr).setValueString(log[i].getAttribute(
                                            "value_String").getValueString());
                                }
                                else if (type.equalsIgnoreCase("CURRENCY") ||
                                        type.equalsIgnoreCase("NUMBER"))
                                {
                                    brHd.getAttribute(nameBr).setValueLong(log[i].getAttribute(
                                            "value_Long").getValueLong());
                                }
                                else if (type.equalsIgnoreCase("DATE") ||
                                        type.equalsIgnoreCase("DATETIME") ||
                                        type.equalsIgnoreCase("DURATION"))
                                {
                                    brHd.getAttribute(nameBr).setValueDate(log[i].getAttribute(
                                            "value_Date").getValueDate());
                                }
                            }
                        }
                    }
                }

                //fim dos atributos dop tipo bridge
                //atributo state
                else if (obj.getStateAttribute(name) != null)
                {
                    if (log.length > 0)
                    {
                        action = log[0].getAttribute("action").getValueString();

                        String type = log[0].getAttribute("type").getValueString();

                        if (!action.equalsIgnoreCase("INSERT"))
                        {
                            obj.getStateAttribute(name).setValue(log[0].getAttribute("value_Long")
                                                                       .getValueString());
                        }
                    }
                }

                //atributo normal
                else
                {
                    if (log.length > 0)
                    {
                        action = log[0].getAttribute("action").getValueString();

                        String type = log[0].getAttribute("type").getValueString();

                        if (action.equalsIgnoreCase("INSERT"))
                        {
                            //se é do tipo object pode ser necessário apagar o objecto da lista de objecto a gravar
                            if (obj.getAttribute(name).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                            {
                                long bouiObj = log[0].getAttribute("value_Long").getValueLong();
                                removeObject(ctx, bouiObj);
                            }

                            //simplesmente apaga o valor do atributo independentemente do tipo
                            obj.getAttribute(name).setValueObject(null);
                        }

                        //quando é um filho não orfão que é alterado
                        else if (action.equalsIgnoreCase("UPDATE_CHILD"))
                        {
                            return rollbackVersion(ctx,
                                log[0].getAttribute("value_Long").getValueLong(),
                                log[0].getAttribute("version").getValueLong(), true);
                        }

                        else
                        {
                            //testar se é do tipo objecto e se este novo existe
                            if (obj.getAttribute(name).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                            {
                                long bouiObj = log[0].getAttribute("value_Long").getValueLong();
                                long versionValue = log[0].getAttribute("version").getValueLong();

                                //verifica se o objecto existe
                                boObject objBr = null;

                                try
                                {
                                    objBr = boObject.getBoManager().loadObject(ctx, bouiObj);
                                }
                                catch (Exception e)
                                {
                                }

                                //testar se o objecto implementa versioning, e se é orfão
                                String classname = boVersioning.getObjectClassname(ctx, bouiObj);
                                boolean orphan = true;

                                if (classname != null)
                                {
                                    orphan = boDefHandler.getBoDefinition(classname)
                                                         .getBoCanBeOrphan();
                                }

                                //se não existe é preciso fazer o rollback dele
                                if ((objBr == null) && rollBackChilds)
                                {
                                    boolean sucess = false;

                                    if (!orphan)
                                    {
                                        sucess = rollbackVersion(ctx, bouiObj, versionValue,
                                                rollBackChilds);
                                    }
                                    else
                                    {
                                        sucess = rollbackVersion(ctx, bouiObj, rollBackChilds);
                                    }

                                    if (!sucess)
                                    {
                                        return sucess;
                                    }

                                    objBr = boObject.getBoManager().loadObject(ctx, bouiObj);
                                }
                                else if (objBr == null)
                                {
                                    return false;
                                }

                                obj.getAttribute(name).setValueLong(log[0].getAttribute(
                                        "value_Long").getValueLong());
                            }

                            //simplesmente altera o valor do atributo conforme o tipo
                            else if (type.equalsIgnoreCase("BOOLEAN") ||
                                    type.equalsIgnoreCase("CHAR"))
                            {
                                obj.getAttribute(name).setValueString(log[0].getAttribute(
                                        "value_String").getValueString());
                            }
                            else if (type.equalsIgnoreCase("CURRENCY") ||
                                    type.equalsIgnoreCase("NUMBER"))
                            {
                                obj.getAttribute(name).setValueLong(log[0].getAttribute(
                                        "value_Long").getValueLong());
                            }
                            else if (type.equalsIgnoreCase("DATE") ||
                                    type.equalsIgnoreCase("DATETIME") ||
                                    type.equalsIgnoreCase("DURATION"))
                            {
                                obj.getAttribute(name).setValueDate(log[0].getAttribute(
                                        "value_Date").getValueDate());
                            }
                            else if (type.equalsIgnoreCase("CLOB"))
                            {
                                obj.getAttribute(name).setValueString(log[0].getAttribute(
                                        "value_CLOB").getValueString());
                            }
                        }
                    }
                }

                //fim dos atributos normais
            }

            //fim da pesquisa dos atributos do objecto
        }
        }

        //fim da pesquisa dos objectos de versão
        //mudar o estado para activo, irrelevante se ele já estava activo
        if (obj.getStateAttribute("stateControl") != null)
        {
            obj.getStateAttribute("stateControl").setValue("active");
        }

        //adicionar ao vector de objectos a gravar
        rollbackObjects.add(obj);

        return true;
    }

    /**
    *  Retorna um certo objecto a uma versão anterior
    *
    * @param ctx contexto do objceto a retornar
    * @param boui boui do objecto a retornar
    * @param version versão para o qual se quer retornar
    * @param rollBackChilds verdadeiro se se deve fazer o rollback dos filhos do objecto
    * @return verdadeiro se operação bem sucedida
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public boolean rollbackVersion(EboContext ctx, long boui, long version, boolean rollBackChilds)
        throws boRuntimeException
    {
        //Select the latest object form the list
        boObjectList boList = boObjectList.list(ctx,
                "SELECT Ebo_Versioning WHERE Ebo_Versioning.changedObject=" + boui +
                " AND Ebo_Versioning.version=" + version + "");

        //Retrieve the lastest from the list (current version)
        boList.beforeFirst();

        if (boList.next())
        {
            return rollbackVersion(boList.getObject(), rollBackChilds);
        }

        return false;
    }

    /**
    *  Retorna um certo objecto à versão em que se encontrou
    *
    * @param ctx contexto do objceto a retornar
    * @param boui boui do objecto a retornar
    * @param rollBackChilds verdadeiro se se deve fazer o rollback dos filhos do objecto
    * @return verdadeiro se operação bem sucedida
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public boolean rollbackVersion(EboContext ctx, long boui, boolean rollBackChilds)
        throws boRuntimeException
    {
        //seleciona o ùltimo objecto alteração referente ao boui
        boObjectList boList = boObjectList.list(ctx,
                "SELECT Ebo_Versioning WHERE Ebo_Versioning.changedObject=" + boui +
                " ORDER BY version DESC");

        //retirar o primeiro objecto da lista pois este é a versão mais actual
        boList.beforeFirst();

        if (boList.next())
        {
            return rollbackVersion(boList.getObject(), rollBackChilds);
        }

        return false;
    }

    /**
    * Retorna todos os objectos que foram alterados devido a um rollback
    *
    * @return boObjects que foram alterados
    */
    public boObject[] getRollbackObjects()
    {
        //remover entradas repetidas
        for (int i = 0; i < rollbackObjects.size(); i++)
        {
            for (int j = i + 1; j < rollbackObjects.size(); j++)
            {
                if (((boObject) rollbackObjects.elementAt(i)).getBoui() == ((boObject) rollbackObjects.elementAt(
                            j)).getBoui())
                {
                    rollbackObjects.remove(j);
                }
            }
        }

        return (boObject[]) rollbackObjects.toArray(new boObject[rollbackObjects.size()]);
    }

    /**
    * Grava as alterações dos objectos que forma alterados durante um rollback
    *
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public void updateObjects() throws boRuntimeException
    {
        for (int i = 0; i < rollbackObjects.size(); i++)
        {
            ((boObject) rollbackObjects.elementAt(i)).update();
        }

        for (int i = 0; i < destroyObjects.size(); i++)
        {
            ((boObject) destroyObjects.elementAt(i)).update();
        }
    }

    /**
    * Adiciona um objecto a lista de objectos a apagar
    *
    * @param ctx contexto do objecto
    * @param boui boui do objecto a remover
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public void removeObject(EboContext ctx, long boui) throws boRuntimeException
    {
        for (int i = 0; i < rollbackObjects.size(); i++)
        {
            if (((boObject) rollbackObjects.elementAt(i)).getBoui() == boui)
            {
                //se ele não existe na base de dados é só remover do array
                if (boObject.getBoManager().loadObject(ctx, boui) != null)
                {
                    rollbackObjects.remove(i);
                }

                //caso contrário passa-se o objecto para destroy
                else
                {
                    destroyObjects.add(rollbackObjects.elementAt(i));
                }
            }
        }
    }

    /**
    * Retorna s versão actual de um objecto
    *
    * @param ctx contexto do objecto
    * @param boui boui do objecto
    * @return número da versão
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public static long getObjectVersion(EboContext ctx, long boui) throws boRuntimeException
    {
        PreparedStatement pstm = null;
        ResultSet rslt = null;
        Connection cn = null;
        long maxVer = 0;

        try
        {
            cn = ctx.getConnectionData();
            pstm = cn.prepareStatement(
                    "SELECT MAX(version) MAXVERSION FROM oebo_versioning WHERE changedobject=" +
                    boui);
            rslt = pstm.executeQuery();

            if (rslt.next())
            {
                maxVer = rslt.getLong("MAXVERSION");
            }
        }
        catch (SQLException e)
        {
            throw new boRuntimeException(MessageLocalizer.getMessage("SQL_EXCEPTION_READING_VERSION_FROM_DATABASE"), "", e);
        }
        finally
        {
            try
            {
                if (rslt != null)
                {
                    rslt.close();
                }
            }
            catch (Exception e)
            {
            }

            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
            }
            catch (Exception e)
            {
            }
        }

        return maxVer;
    }

    /**
    * Retorna o nome da classe de um objecto que foi apagado
    *
    * @param ctx contexto da versão do objecto
    * @param boui boui que o objecto tinha quando apagado
    * @return nome da classe do objecto
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public static String getObjectClassname(EboContext ctx, long boui) throws boRuntimeException
    {
        String classname = null;

        //seleciona o primeiro objecto alteração referente ao boui
        boObjectList boList = boObjectList.list(ctx,
                "SELECT Ebo_Versioning WHERE Ebo_Versioning.changedObject=" + boui +
                " ORDER BY version DESC");

        //retirar o primeiro objecto da lista pois este é a ultima versão
        boList.beforeFirst();

        if (boList.next())
        {
            bridgeHandler bh = boList.getObject().getBridge("log");

            bh.beforeFirst();

            while (bh.next())
            {
                boObject log = bh.getObject();

                if (log.getAttribute("attribute").getValueString().equals("CLASSNAME"))
                {
                    classname = log.getAttribute("value_String").getValueString();
                }
            }
        }

        return classname;
    }

    /**
    *  Cria um objecto de log relativo à alteração de um atributo
    *
    * @param ctx contexto do objecto
    * @param attribute nome do atributo
    * @param value valor do atributo a guardar
    * @param type tipo de atributo
    * @param action acção tomada
    * @param line linha a que pertence (só para atributos de bridge, caso contrário 0)
    * @param version versão do objecto a que o atributo se refere (só para atributos do tipo object)
    * @return retorna o objecto de log
    * @throws netgest.bo.runtime.boRuntimeException
    */
    private static boObject createVersionLog(EboContext ctx, String attribute, String name, Object value,
        byte type, String action, long line, long version) throws boRuntimeException
    {
        boObject log = boObject.getBoManager().createObject(ctx, "Ebo_Log");

        log.getAttribute("attribute").setValueString(attribute);
        log.getAttribute("name").setValueString(name);

        switch (type)
        {
            case boDefAttribute.VALUE_BOOLEAN:
                log.getAttribute("type").setValueString("BOOLEAN");
                log.getAttribute("value_String").setValueObject(value);

                break;

            case boDefAttribute.VALUE_CHAR:
                log.getAttribute("type").setValueString("CHAR");
                log.getAttribute("value_String").setValueObject(value);

                break;

            case boDefAttribute.VALUE_CLOB:
                log.getAttribute("type").setValueString("CLOB");
                log.getAttribute("value_CLOB").setValueObject(value.toString());

                break;

            case boDefAttribute.VALUE_CURRENCY:
                log.getAttribute("type").setValueString("CURRENCY");
                log.getAttribute("value_Long").setValueObject(value);

                break;

            case boDefAttribute.VALUE_NUMBER:
                log.getAttribute("type").setValueString("NUMBER");
                log.getAttribute("value_Long").setValueObject(value);

                break;

            case boDefAttribute.VALUE_DATE:
                log.getAttribute("type").setValueString("DATE");
                log.getAttribute("value_Date").setValueObject(value);

                break;

            case boDefAttribute.VALUE_DATETIME:
                log.getAttribute("type").setValueString("DATETIME");
                log.getAttribute("value_Date").setValueObject(value);

                break;

            case boDefAttribute.VALUE_DURATION:
                log.getAttribute("type").setValueString("DURATION");
                log.getAttribute("value_Date").setValueObject(value);

                break;
            case boDefAttribute.VALUE_IFILELINK:
                log.getAttribute("type").setValueString("CHAR");
                log.getAttribute("value_String").setValueObject(value);
                break;
            default:
                throw new RuntimeException(MessageLocalizer.getMessage("DATA_TYPE_UNABLE_TO_CREATE_HISTORY"));
        }

        log.getAttribute("action").setValueString(action);
        log.getAttribute("line").setValueLong(line);

        if (version > 0)
        {
            log.getAttribute("version").setValueLong(version);
        }

        return log;
    }

    /**
    * Cria logs de alterações relativos a um objecto modificado
    *
    * @param ctx contexto do objecto
    * @param bobj objecto alterado
    * @param notifyParent verdaeiro se se deve notificar os pais no caso do objecto ser não orfão
    * @return retorna os logs relativos as alterações detectadas
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public static boObject[] createVersionData(EboContext ctx, boObject bobj, boolean notifyParent)
        throws boRuntimeException
    {
        return createVersionData(ctx, bobj, notifyParent, true);
    }

    /**
    * Cria logs de alterações relativos a um objecto modificado
    *
    * @param ctx contexto do objecto
    * @param bobj objecto alterado
    * @param notifyParent verdaeiro se se deve notificar os pais no caso do objecto ser não orfão
    * @param oldData verdaeiro se se deve guardar os dados antigos no caso de updates, falso para guardar os novos valores
    * @return retorna os logs relativos as alterações detectadas
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public static boObject[] createVersionData(EboContext ctx, boObject bobj, boolean notifyParent,
        boolean oldData) throws boRuntimeException
    {
        Vector logs = new Vector();
        boDefAttribute[] atts = bobj.getBoDefinition().getBoAttributes();

        for (int i = 0; i < atts.length; i++)
        {
            String name = atts[i].getName();

            if (atts[i].getDbIsBinding())
            {
                if (atts[i].getDbIsTabled() ||
                        ((atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                        (atts[i].getMaxOccurs() > 1)))
                {
                    // Buscar as rows da bridge
                    DataSet rslt = bobj.getDataRow().getChildDataSet(ctx, atts[i].getName());

                    //percorrer todas as rows da bridge
                    // Por cada row da bridge fazer comparação. Se for um atributo do tipo Tabled não existem atributos, apenas um campo com o nome obtido através do getDbTableChildFieldName()
                    for (int j = 1; j <= rslt.getRowCount(); j++)
                    {
                        //caso em que a bridge não é nova, mas os atributos podem ser
                        if (!rslt.rows(j).isNew() && (rslt.rows(j).getFlashBackRow() != null))
                        {
                            if (atts[i].getDbIsTabled())
                            {
                                if (oldData)
                                {
                                    logs.add(createVersionLog(ctx, name, atts[i].getDescription(),
                                            rslt.rows(j).getFlashBackRow().getObject(atts[i].getDbTableChildFieldName()),
                                            atts[i].getValueType(), "UPDATE", j, 0));
                                }
                                else
                                {
                                    logs.add(createVersionLog(ctx, name, atts[i].getDescription(),
                                            rslt.rows(j).getObject(atts[i].getDbTableChildFieldName()),
                                            atts[i].getValueType(), "UPDATE", j, 0));
                                }

                                continue;
                            }

                            boAttributesArray attsBr = bobj.getBridge(atts[i].getName())
                                                           .getAllAttributes();

                            // Pecorrer todos os atributos da bridge.
                            Enumeration elem = attsBr.elements();

                            //percorre todos os atributos da bridge
                            while (elem.hasMoreElements())
                            {
                                AttributeHandler attBrHd = (AttributeHandler) elem.nextElement();

                                //como apenas se quer os nomes dos atributos, descarta-se os repetidos
                                if (!attBrHd.getName().endsWith("1"))
                                {
                                    continue;
                                }

                                boDefAttribute attBrDef = attBrHd.getDefAttribute();

                                //caso em que a row foi alterada
                                if ((rslt.rows(j).getFlashBackRow() != null) &&
                                        attBrDef.getDbIsBinding() && !attBrDef.getDbIsTabled() &&
                                        !attBrDef.getDbName().equalsIgnoreCase("LIN"))
                                {
                                    long line = rslt.rows(j).getFlashBackRow().getLong("LIN");

                                    Object cmpN = null;
                                    Object cmpO = null;

                                    //caso em que é um atributo que indica o boui do objecto
                                    if (attBrDef.getDbName().equalsIgnoreCase(name + "$"))
                                    {
                                        cmpN = rslt.rows(j).getObject(attBrDef.getBridge()
                                                                              .getChildFieldName());
                                        cmpO = rslt.rows(j).getFlashBackRow().getObject(attBrDef.getBridge()
                                                                                                .getChildFieldName());
                                    }
                                    else
                                    {
                                        cmpN = rslt.rows(j).getObject(attBrDef.getDbName());
                                        cmpO = rslt.rows(j).getFlashBackRow().getObject(attBrDef.getDbName());
                                    }

                                    //testar se o atributo está ou esteve preenchido
                                    if (!((cmpN == null) && (cmpO == null)))
                                    {
                                        long ver = 0;

                                        //se é um atributo do tipo object, este pode ser orfão
                                        //log é preciso guardar o número da versão actual
                                        if ((attBrDef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                                !name.equalsIgnoreCase(attBrDef.getName()))
                                        {
                                            long objBoui = 0;

                                            if (cmpO == null)
                                            {
                                                objBoui = Long.parseLong(cmpN.toString());
                                            }
                                            else
                                            {
                                                objBoui = Long.parseLong(cmpO.toString());
                                            }

                                            ver = boVersioning.getObjectVersion(ctx, objBoui);

                                            //se o objecto for estiver para ser apagado então
                                            //é necessário incrementar o seu numero de versão
                                            long[][] toupdate = bobj.getUpdateQueue().getObjects();

                                            for (short x = 0; x < toupdate.length; x++)
                                            {
                                                if ((toupdate[x][0] == objBoui) &&
                                                        (toupdate[x][1] == boObjectUpdateQueue.MODE_DESTROY) &&
                                                        bobj.getObject(objBoui).getBoDefinition()
                                                                .getVersioning())
                                                {
                                                    ver++;

                                                    break;
                                                }
                                            }

                                            //testar se o objecto está em espera para ser criado, 
                                            //se estiver incrementar o seu número de versão
                                            if (boObject.getBoManager().loadObject(ctx, objBoui)
                                                            .getMode() == boObject.MODE_NEW)
                                            {
                                                ver++;
                                            }
                                        }

                                        // Novo valor é nulo, anteriormente estava preenchido 
                                        if (cmpN == null)
                                        {
                                            logs.add(createVersionLog(ctx,
                                                    name + "." + attBrDef.getName(),attBrDef.getDescription(), cmpO,
                                                    attBrDef.getValueType(), "DELETE", line, ver));
                                        }

                                        //Antigo valor é nulo, passa a estar preenchido 
                                        else if (cmpO == null)
                                        {
                                            logs.add(createVersionLog(ctx,
                                                    name + "." + attBrDef.getName(),attBrDef.getDescription(), cmpN,
                                                    attBrDef.getValueType(), "INSERT", line, ver));
                                        }

                                        // Valores diferentes
                                        else if (!cmpN.equals(cmpO))
                                        {
                                            if (oldData)
                                            {
                                                logs.add(createVersionLog(ctx,
                                                        name + "." + attBrDef.getName(),attBrDef.getDescription(), cmpO,
                                                        attBrDef.getValueType(), "UPDATE", line, ver));
                                            }
                                            else
                                            {
                                                logs.add(createVersionLog(ctx,
                                                        name + "." + attBrDef.getName(),attBrDef.getDescription(), cmpN,
                                                        attBrDef.getValueType(), "UPDATE", line, ver));
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        //caso para um nova bridge
                        else if (rslt.rows(j).isNew())
                        {
                            if (atts[i].getDbIsTabled())
                            {
                                logs.add(createVersionLog(ctx, name,atts[i].getDescription(),
                                        rslt.rows(j).getObject(atts[i].getDbTableChildFieldName()),
                                        atts[i].getValueType(), "INSERT", j, 0));

                                continue;
                            }

                            boAttributesArray attsBr = bobj.getBridge(atts[i].getName())
                                                           .getAllAttributes();
                            Enumeration elem = attsBr.elements();

                            // Pecorrer todos os atributos da bridge.
                            while (elem.hasMoreElements())
                            {
                                long line = rslt.rows(j).getLong("LIN");

                                AttributeHandler attBrHd = (AttributeHandler) elem.nextElement();

                                if (!attBrHd.getName().endsWith("1"))
                                {
                                    continue;
                                }

                                boDefAttribute attBrDef = attBrHd.getDefAttribute();

                                if (attBrDef.getDbIsBinding() && !attBrDef.getDbIsTabled() &&
                                        !attBrDef.getDbName().equalsIgnoreCase("LIN"))
                                {
                                    Object cmpN = null;

                                    if (attBrDef.getDbName().equalsIgnoreCase(name + "$"))
                                    {
                                        cmpN = rslt.rows(j).getObject(attBrDef.getBridge()
                                                                              .getChildFieldName());
                                    }
                                    else
                                    {
                                        cmpN = rslt.rows(j).getObject(attBrDef.getDbName());
                                    }

                                    if (!(cmpN == null))
                                    {
                                        long ver = 0;

                                        //se é um atributo do tipo object, este pode ser não orfão
                                        //log é preciso guardar o número da versão actual
                                        if ((attBrDef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                                !name.equalsIgnoreCase(attBrDef.getName()))
                                        {
                                            ver = boVersioning.getObjectVersion(ctx,
                                                    Long.parseLong(cmpN.toString()));

                                            //testar se o objecto está em espera para ser criado, 
                                            //se estiver incrementar o seu número de versão
                                            if (boObject.getBoManager()
                                                            .loadObject(ctx,
                                                        Long.parseLong(cmpN.toString())).getMode() == boObject.MODE_NEW)
                                            {
                                                ver++;
                                            }
                                        }

                                        //Antigo valor é nulo, passa a estar preenchido 
                                        logs.add(createVersionLog(ctx,
                                                name + "." + attBrDef.getName(),attBrDef.getDescription(), cmpN,
                                                attBrDef.getValueType(), "INSERT", line, ver));
                                    }
                                }
                            }
                        }

                        //objecto marcado para apagar
                        else if (bobj.getMode() == boObject.MODE_DESTROY)
                        {
                            if (atts[i].getDbIsTabled())
                            {
                                logs.add(createVersionLog(ctx, name, atts[i].getDescription(),
                                        rslt.rows(j).getObject(atts[i].getDbTableChildFieldName()),
                                        atts[i].getValueType(), "DELETE", j, 0));

                                continue;
                            }

                            boAttributesArray attsBr = bobj.getBridge(atts[i].getName())
                                                           .getAllAttributes();
                            Enumeration elem = attsBr.elements();

                            // Pecorrer todos os atributos da bridge.
                            while (elem.hasMoreElements())
                            {
                                long line = rslt.rows(j).getLong("LIN");

                                AttributeHandler attBrHd = (AttributeHandler) elem.nextElement();

                                if (!attBrHd.getName().endsWith("1"))
                                {
                                    continue;
                                }

                                boDefAttribute attBrDef = attBrHd.getDefAttribute();

                                if (attBrDef.getDbIsBinding() && !attBrDef.getDbIsTabled() &&
                                        !attBrDef.getDbName().equalsIgnoreCase("LIN"))
                                {
                                    Object cmpO = null;

                                    if (attBrDef.getDbName().equalsIgnoreCase(name + "$"))
                                    {
                                        cmpO = rslt.rows(j).getObject(attBrDef.getBridge()
                                                                              .getChildFieldName());
                                    }
                                    else
                                    {
                                        cmpO = rslt.rows(j).getObject(attBrDef.getDbName());
                                    }

                                    if (!(cmpO == null))
                                    {
                                        long ver = 0;

                                        //se é um atributo do tipo object, este pode ser orfão
                                        //log é preciso guardar o número da versão actual
                                        if ((attBrDef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                                !name.equalsIgnoreCase(attBrDef.getName()))
                                        {
                                            long objBoui = 0;

                                            objBoui = Long.parseLong(cmpO.toString());

                                            ver = boVersioning.getObjectVersion(ctx, objBoui);

                                            //se o objecto for estiver para ser apagado então
                                            //é necessário incrementar o seu numero de versão
                                            long[][] toupdate = bobj.getUpdateQueue().getObjects();

                                            for (short x = 0; x < toupdate.length; x++)
                                            {
                                                if ((toupdate[x][0] == objBoui) &&
                                                        (toupdate[x][1] == boObjectUpdateQueue.MODE_DESTROY) &&
                                                        bobj.getObject(objBoui).getBoDefinition()
                                                                .getVersioning())
                                                {
                                                    ver++;

                                                    break;
                                                }
                                            }
                                        }

                                        //Antigo valor é nulo, passa a estar preenchido 
                                        logs.add(createVersionLog(ctx,
                                                name + "." + attBrDef.getName(),attBrDef.getDescription(), cmpO,
                                                attBrDef.getValueType(), "DELETE", line, ver));
                                    }
                                }
                            }
                        }

                        //para quando os atributos do tipo object são alterados devido ao filho ter sido alterado
                        else
                        {
                            if (atts[i].getDbIsTabled())
                            {
                                continue;
                            }

                            boAttributesArray attsBr = bobj.getBridge(atts[i].getName())
                                                           .getAllAttributes();

                            // Pecorrer todos os atributos da bridge.
                            Enumeration elem = attsBr.elements();

                            //percorre todos os atributos da bridge
                            while (elem.hasMoreElements())
                            {
                                AttributeHandler attBrHd = (AttributeHandler) elem.nextElement();

                                //como apenas se quer os nomes dos atributos, descarta-se os repetidos
                                if (!attBrHd.getName().endsWith("1"))
                                {
                                    continue;
                                }

                                boDefAttribute attBrDef = attBrHd.getDefAttribute();

                                //os atributos de bridge que são do tipo objecto não orfão são sempre alterados automáticamente
                                //logo a row não foi alterada
                                if ((attBrDef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                        !name.equalsIgnoreCase(attBrDef.getName()))
                                {
                                    if (rslt.rows(j).getObject(attBrDef.getDbName()) != null)
                                    {
                                        Object cmpN = rslt.rows(j).getObject(attBrDef.getDbName());

                                        if (cmpN == null)
                                        {
                                            break;
                                        }

                                        boObject boTemp = boObject.getBoManager().loadObject(ctx,
                                                Long.parseLong(cmpN.toString()));

                                        if (boTemp == null)
                                        {
                                            break;
                                        }

                                        if (boTemp.getParameter("changed") == null)
                                        {
                                            break;
                                        }

                                        if (!boTemp.getParameter("changed").equalsIgnoreCase("true"))
                                        {
                                            break;
                                        }

                                        logs.add(createVersionLog(ctx,
                                                name + "." + attBrDef.getName(),attBrDef.getDescription(), cmpN,
                                                attBrDef.getValueType(), "UPDATE_CHILD", j,
                                                boVersioning.getObjectVersion(ctx, boTemp.getBoui()) +
                                                1));
                                    }
                                }
                            }
                        }
                    }

                    //para bridges que foram apagadas
                    for (int j = 1; j <= rslt.getDeletedRowsCount(); j++)
                    {
                        if (atts[i].getDbIsTabled())
                        {
                            logs.add(createVersionLog(ctx, name, atts[i].getDescription(),
                                    rslt.deletedRows(j).getFlashBackRow().getObject(atts[i].getDbTableChildFieldName()),
                                    atts[i].getValueType(), "DELETE", j, 0));

                            continue;
                        }

                        boAttributesArray attsBr = bobj.getBridge(atts[i].getName())
                                                       .getAllAttributes();
                        Enumeration elem = attsBr.elements();

                        // Pecorrer todos os atributos da bridge.
                        while (elem.hasMoreElements())
                        {
                            DataRow objRow = null;
                            long    line   = 0;
                            
                            if( rslt.deletedRows(j).getFlashBackRow() == null )
                                objRow = rslt.deletedRows(j);
                            else
                                objRow = rslt.deletedRows(j).getFlashBackRow();
                            
                            line = objRow.getLong("LIN");
                            
                            AttributeHandler attBrHd = (AttributeHandler) elem.nextElement();

                            if (!attBrHd.getName().endsWith("1"))
                            {
                                continue;
                            }

                            boDefAttribute attBrDef = attBrHd.getDefAttribute();

                            if (attBrDef.getDbIsBinding() && !attBrDef.getDbIsTabled() &&
                                    !attBrDef.getDbName().equalsIgnoreCase("LIN"))
                            {
                                Object cmpO = null;

                                if (attBrDef.getDbName().equalsIgnoreCase(name + "$"))
                                {
                                    cmpO = objRow.getObject(attBrDef.getBridge()
                                                                                                   .getChildFieldName());
                                }
                                else
                                {
                                    cmpO = objRow.getObject(attBrDef.getDbName());
                                }

                                if (!(cmpO == null))
                                {
                                    long ver = 0;

                                    //se é um atributo do tipo object, este pode ser orfão
                                    //log é preciso guardar o número da versão actual
                                    if ((attBrDef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                            !name.equalsIgnoreCase(attBrDef.getName()))
                                    {
                                        long objBoui = 0;
                                        objBoui = Long.parseLong(cmpO.toString());

                                        ver = boVersioning.getObjectVersion(ctx, objBoui);

                                        //se o objecto for estiver para ser apagado então
                                        //é necessário incrementar o seu numero de versão
                                        long[][] toupdate = bobj.getUpdateQueue().getObjects();

                                        for (short x = 0; x < toupdate.length; x++)
                                        {
                                            if ((toupdate[j][0] == objBoui) &&
                                                    (toupdate[x][1] == boObjectUpdateQueue.MODE_DESTROY) &&
                                                    bobj.getObject(objBoui).getBoDefinition()
                                                            .getVersioning())
                                            {
                                                ver++;

                                                break;
                                            }
                                        }
                                    }

                                    logs.add(createVersionLog(ctx, name + "." + attBrDef.getName(), attBrDef.getDescription(),
                                            cmpO, attBrDef.getValueType(), "DELETE", line, ver));
                                }
                            }
                        }
                    }
                }

                //atributo normal
                else if (bobj.getDataRow().getFlashBackRow() != null)
                {
                    Object cmpN = bobj.getDataRow().getObject(atts[i].getDbName());
                    Object cmpO = bobj.getDataRow().getFlashBackRow().getObject(atts[i].getDbName());

                    if (!((cmpN == null) && (cmpO == null)))
                    {
                        long ver = 0;

                        //se é um atributo do tipo object, este pode ser orfão
                        //log é preciso guardar o número da versão actual
                        if ((atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                !name.equalsIgnoreCase("PARENT"))
                        {
                            long objBoui = 0;

                            if (cmpO == null)
                            {
                                objBoui = Long.parseLong(cmpN.toString());
                            }
                            else
                            {
                                objBoui = Long.parseLong(cmpO.toString());
                            }

                            ver = boVersioning.getObjectVersion(ctx, objBoui);

                            //se o objecto for estiver para ser apagado então
                            //é necessário incrementar o seu numero de versão
                            long[][] toupdate = bobj.getUpdateQueue().getObjects();

                            for (short j = 0; j < toupdate.length; j++)
                            {
                                if ((toupdate[j][0] == objBoui) &&
                                        (toupdate[j][1] == boObjectUpdateQueue.MODE_DESTROY) &&
                                        bobj.getObject(objBoui).getBoDefinition().getVersioning())
                                {
                                    ver++;

                                    break;
                                }
                            }

                            //testar se o objecto está em espera para ser criado, 
                            //se estiver incrementar o seu número de versão
                            if (boObject.getBoManager().loadObject(ctx, objBoui).getMode() == boObject.MODE_NEW)
                            {
                                ver++;
                            }
                        }

                        if (cmpN == null)
                        {
                            // Novo valor é nulo, anteriormente estava preenchido 
                            logs.add(createVersionLog(ctx, name,atts[i].getDescription(), cmpO, atts[i].getValueType(),
                                    "DELETE", 0, ver));
                        }
                        else if (cmpO == null)
                        {
                            //testar se o atributo é o BOUI, se for não se notifica o pai
                            if (name.equalsIgnoreCase("BOUI"))
                            {
                                notifyParent = false;
                            }

                            //Antigo valor é nulo, passa a estar preenchido 
                            logs.add(createVersionLog(ctx, name,atts[i].getDescription(), cmpN, atts[i].getValueType(),
                                    "INSERT", 0, ver));
                        }
                        else if (!cmpN.equals(cmpO))
                        {
                            // Valores diferentes
                            if (oldData)
                            {
                                logs.add(createVersionLog(ctx, name,atts[i].getDescription(), cmpO, atts[i].getValueType(),
                                        "UPDATE", 0, ver));
                            }
                            else
                            {
                                logs.add(createVersionLog(ctx, name,atts[i].getDescription(), cmpN, atts[i].getValueType(),
                                        "UPDATE", 0, ver));
                            }
                        }

                        //o atributo é do tipo object não orfão e este foi alterado
                        else if (atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                        {
                            if (bobj.getAttribute(name).getObject().getParameter("changed") != null)
                            {
                                if (bobj.getAttribute(name).getObject().getParameter("changed")
                                            .equalsIgnoreCase("true"))
                                {
                                    logs.add(createVersionLog(ctx, name,atts[i].getDescription(), cmpN,
                                            atts[i].getValueType(), "UPDATE_CHILD", 0, ver + 1));
                                }
                            }
                        }
                    }
                }

                //objecto marcado para apagar
                else if (bobj.getMode() == boObject.MODE_DESTROY)
                {
                    notifyParent = false;

                    // Attributo Normal
                    Object cmpN = bobj.getDataRow().getObject(atts[i].getDbName());

                    if (cmpN != null)
                    {
                        // Novo valor é nulo, anteriormente estava preenchido 
                        logs.add(createVersionLog(ctx, name,atts[i].getDescription(), cmpN, atts[i].getValueType(),
                                "DELETE", 1, 0));
                    }
                }
            }
        }

        //é um objecto não orfão, logo á que intruduzir uma referencia no pai.
        if (!bobj.getBoDefinition().getBoCanBeOrphan() && notifyParent)
        {
            bobj.setParameter("changed", "true");

            //é preciso alterar os pais que ainda não estão marcados para alterar
            boObject[] parents = bobj.getParents();

            boObject par = bobj.getParent();

            for (int i = 0; i < parents.length; i++)
            {
                if (!parents[i].isChanged())
                {
                    parents[i].setChanged(true);
                    parents[i].update();
                }
            }
        }

        //para o caso em que paenas se encotra uma referêmncia para uma alteração do pai
        //este caso não é necessário considerar
        if (logs.size() == 1)
        {
            if (((boObject) logs.elementAt(0)).getAttribute("attribute").getValueString()
                     .equalsIgnoreCase("PARENT"))
            {
                logs.remove(0);
            }
        }

        return (boObject[]) logs.toArray(new boObject[logs.size()]);
    }

    /**
    * Cria um objecto de versão segundo um conjunto de logs
    *
    * @param ctx contexto do objecto de versão a criar
    * @param logs logs de alteração do objecto
    * @param boui boui do objecto alterado
    * @return  objecto de versão criado
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public static boObject createVersionObject(EboContext ctx, boObject[] logs, long boui)
        throws boRuntimeException
    {
        //criar objecto de versioning
        if (logs.length == 0)
        {
            return null;
        }

        boObject version = boObject.getBoManager().createObject(ctx, "Ebo_Versioning");

        version.getAttribute("changedObject").setValueLong(boui);

        long maxVer = boVersioning.getObjectVersion(ctx, boui) + 1;

        version.getAttribute("version").setValueLong(maxVer);

        for (int i = 0; i < logs.length; i++)
        {
            version.getBridge("log").add(((boObject) logs[i]).getBoui());
            version.getUpdateQueue().add((boObject) logs[i], boObjectUpdateQueue.MODE_SAVE);
        }

        return version;
    }
}
