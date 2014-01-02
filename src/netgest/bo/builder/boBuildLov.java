/*Enconding=UTF-8*/
package netgest.bo.builder;

import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefLov;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.boApplication;

import netgest.utils.ngtXMLHandler;

import java.util.Hashtable;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class boBuildLov
{
    //private static String xsql = "select * from EBO_LOV ";
    private boDefLov xmllov;
    private boDefHandler xmlObj;
    private EboContext p_eboctx;
    private String xeoLovFileName;
   
    /**
     *
     * @since
     */
    public boBuildLov(EboContext ebo)
    {
        p_eboctx = ebo;
    }

    public void build(boDefLov xmlObj) throws boRuntimeException
    {
        ngtXMLHandler[] childs = xmlObj.getChilds();
        //boObjectList list = boObjectList.list(p_eboctx,"SELECT Ebo_LOV WHERE 1=1");
        for (int i = 0; i < childs.length; i++)
        {
            if (!"General".equalsIgnoreCase(childs[i].getNodeName()))
            {
                if (childs[i].getNodeName().trim().equals("Lov"))
                {
                    if (childs[i].getAttribute("name")!=null)
                    {
                        String lovName=childs[i].getAttribute("name");
                        buildLov(lovName, childs[i]); 
                    }
                    else
                    throw new boRuntimeException("boBuildLov.build - "+MessageLocalizer.getMessage("MISSING_ATTRIBUTE_NAME_IN_LOV"),
                        "", new Exception());
                }
                else
                    buildLov(childs[i].getNodeName().trim(), childs[i]);
            }
        }
    }

    private String getPTItemName(ngtXMLHandler item)
    {
        return item.getChildNode("label").getText();
    }

    private String getENItemName(ngtXMLHandler item)
    {
        return item.getChildNode("label").getText();
    }

    private String getItemValue(ngtXMLHandler item)
    {
        return item.getChildNode("value").getText();
    }

    private boolean existInArray(int[] okItem, int max, int value)
    {
        for (int k = 0; k < max; k++)
        {
            if (value == okItem[k])
            {
                return true;
            }
        }

        return false;
    }

    private void buildLov(String lovName, ngtXMLHandler lov)
        throws boRuntimeException
    {
        try
        {
            ngtXMLHandler details = lov.getChildNode("details");
            boolean retainValues = "Y".equalsIgnoreCase(lov.getAttribute(
                        "retainValues")) ||
                "YES".equalsIgnoreCase(lov.getAttribute("retainValues"));
           //////////////
            String lovLanguage = lov.getAttribute("lang");
            if (lovLanguage == null || (lovLanguage != null && lovLanguage.length() < 1))
            	lovLanguage = boApplication.getDefaultApplication().getApplicationConfig().getLanguage(); 
        ////////////
            buildLov(lovName, retainValues, details,lovLanguage);
        }
        catch (boRuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new boRuntimeException("boBuildLov.buildLov(String, ngtXMLHandler, boObjectList)",
                "", e);
        }
    }

    public void buildLov(String lovName, boolean retainValues,
        ngtXMLHandler details, String lovLanguage) throws boRuntimeException
    {
        try
        {
            Hashtable flags = new Hashtable();
            ngtXMLHandler[] items = details.getChildNodes();
            ngtXMLHandler item = null;
            String nomeItem;
            String bdLovName;
            bridgeHandler bridge = null;
            boObject n = null;
            boObject detail = null;
            boolean exist = false;
            boolean first = true;
            boolean existItem = false;
            boObject aux = null;

            //1ª posição
            boObjectList list = boObjectList.list(p_eboctx,
                    "SELECT Ebo_LOV WHERE name = '" + lovName + "'");

            if (list != null)
            {
                list.beforeFirst();

                if (list.next())
                {
                    aux = list.getObject();

                    //ja existe
                    exist = true;
                }

                //boPoolManager.destroyObject( list );
                p_eboctx.getApplication().getMemoryArchive().getPoolManager()
                        .destroyObject(list);
            }

            if (!exist)
            {
                if (items.length > 0)
                {
                    n = boObject.getBoManager().createObject(p_eboctx, "Ebo_LOV");
                    n.getAttribute("name").setValueString(lovName);
                    n.getAttribute("xeolovfile").setValueString(getXeoLovFileName());
                    if (lovLanguage!="")
                    n.getAttribute("lang").setValueString(lovLanguage);
                    bridge = n.getBridge("details");

                    for (int i = 0; i < items.length; i++)
                    {
                        item = items[i];
                        detail = bridge.addNewObject();
                        detail.getAttribute("description").setValueString(getPTItemName(
                                item));
                        detail.getAttribute("value").setValueString(getItemValue(
                                item));
                        buildFlag(item, detail, flags);
                    }

                    n.update();
                }
            }
            else
            {
                int[] okItem = new int[items.length];
                int j = 0;

                bridge = aux.getBridge("details");
                bridge.beforeFirst();
                
                //Set the correct .xeolov filename
                aux.getAttribute("xeolovfile").setValueString(getXeoLovFileName());

                boolean remove = false;

                while (remove || bridge.next())
                {
                    remove = false;
                    n = bridge.getObject();

                    if (n != null)
                    {
                        existItem = false;

                        for (int i = 0; (i < items.length) && !existItem;
                                i++)
                        {
                            item = items[i];

                            if (n.getAttribute("description").getValueString()
                                     .equalsIgnoreCase(getPTItemName(item)) ||
                                        n.getAttribute("value").getValueString()
                                         .equalsIgnoreCase(getItemValue(item)))
                            {
                                existItem = true;
                                n.getAttribute("description").setValueString(getPTItemName(
                                        item));
                                n.getAttribute("value").setValueString(getItemValue(
                                        item));

                                if (!existInArray(okItem, j, i))
                                {
                                    okItem[j] = i;
                                    j++;
                                    mergeFlags(item, n, flags);
                                }
                                else
                                { //repetidos vou remover

                                    if (!bridge.getRslt().isLast())
                                    {
                                        remove = true;
                                    }

                                    bridge.remove();
                                }
                            }
                        }

                        if (!existItem && !retainValues)
                        {
                            if (!bridge.getRslt().isLast())
                            {
                                remove = true;
                            }

                            bridge.remove();
                        }
                    }
                }

                if (j < items.length)
                {
                    //existem novos
                    for (int i = 0; i < items.length; i++)
                    {
                        existItem = false;

                        for (int k = 0; k < j; k++)
                        {
                            if (i == okItem[k])
                            {
                                existItem = true;
                            }
                        }

                        if (!existItem)
                        {
                            item = items[i];
                            detail = bridge.addNewObject();

                            String s = getPTItemName(item);
                            detail.getAttribute("description").setValueString(getPTItemName(
                                    item));
                            detail.getAttribute("value").setValueString(getItemValue(
                                    item));
                            buildFlag(item, detail, flags);
                        }
                    }
                }

                aux.update();
            }
        }
        catch (boRuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new boRuntimeException("boBuildLov.buildLov(String, ngtXMLHandler, boObjectList)",
                "", e);
        }
    }

    private void buildFlag(ngtXMLHandler item, boObject detail,
        Hashtable htFlags) throws boRuntimeException
    {
        ngtXMLHandler nodeFlag = item.getChildNode("flags");

        if (nodeFlag != null)
        {
            ngtXMLHandler[] flags = nodeFlag.getChildNodes();
            ngtXMLHandler flag = null;
            String code = null;
            boObject boFlag = null;
            bridgeHandler bridge = null;

            for (int i = 0; i < flags.length; i++)
            {
                flag = flags[i];
                code = getCode(flag);

                if (htFlags.get(code) == null)
                {
                    boFlag = boObject.getBoManager().loadObject(p_eboctx,
                            "SELECT Ebo_Flag WHERE code = '" + code + "'");

                    // Não existe
                    if (!boFlag.exists())
                    {
                        boFlag.getAttribute("code").setValueString(code);
                        boFlag.getAttribute("description").setValueString(getDescription(
                                flag));
                        htFlags.put(code, boFlag);
                    }
                }
                else
                {
                    boFlag = (boObject) htFlags.get(code);
                }

                bridge = detail.getBridge("flags");

                if (!bridge.haveBoui(boFlag.getBoui()))
                {
                    bridge.add(boFlag.getBoui());
                    bridge.edit();
                }
            }
        }
    }

    private void mergeFlags(ngtXMLHandler item, boObject detail,
        Hashtable htFlags) throws boRuntimeException
    {
        ngtXMLHandler nodeFlag = item.getChildNode("flags");

        if (nodeFlag != null)
        {
            ngtXMLHandler[] flags = nodeFlag.getChildNodes();
            boObject boFlag = null;
            String code = null;
            bridgeHandler bridge = detail.getBridge("flags");

            if (bridge != null)
            {
                bridge.beforeFirst();

                while (bridge.next())
                {
                    boFlag = bridge.getObject();
                    code = boFlag.getAttribute("code").getValueString();

                    if (nodeFlag != null)
                    {
                        boolean found = false;
                        ngtXMLHandler flag = null;

                        for (int i = 0; i < flags.length; i++)
                        {
                            if (code.equals(getCode(flags[i])))
                            {
                                found = true;
                            }
                        }

                        if (!found)
                        {
                            bridge.remove();
                            detail.update();
                        }
                    }
                }

                for (int i = 0; i < flags.length; i++)
                {
                    boolean found = false;
                    code = getCode(flags[i]);
                    bridge.beforeFirst();

                    while (bridge.next() && !found)
                    {
                        boFlag = bridge.getObject();

                        if (code.equals(boFlag.getAttribute("code")
                                                  .getValueString()))
                        {
                            found = true;
                        }
                    }

                    if (!found)
                    {
                        buildFlag(item, detail, htFlags);
                        detail.update();
                    }
                }
            }
        }
    }

    private static String getCode(ngtXMLHandler flag)
    {
        return flag.getChildNode("code").getText();
    }

    private static String getDescription(ngtXMLHandler flag)
    {
        return flag.getChildNode("description").getText();
    }

	public void setXeoLovFileName(String xeoLovFileName) {
		this.xeoLovFileName = xeoLovFileName;
	}

	public String getXeoLovFileName() {
		return xeoLovFileName;
	}
}
