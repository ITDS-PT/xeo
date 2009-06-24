/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge;

import netgest.bo.def.boDefHandler;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import java.util.ArrayList;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class DS
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
    private String dstype;
    private ArrayList groups = null;

    public DS(String name, String dstype)
    {
        this.name = name;
        this.dstype = dstype;
    }

    public DS(String name, String dstype, ArrayList groups)
    {
        this.name = name;
        this.dstype = dstype;
        this.groups = groups;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return dstype;
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

    public Object[] getHeader(boObject obj) throws boRuntimeException
    {
        ArrayList r = new ArrayList();
        ArrayList aux;

        for (int i = 0; i < groups.size(); i++)
        {
            if ("block".equalsIgnoreCase(getType()))
            {
                aux = ((DsGroup) groups.get(i)).getHeader(obj, "", null);
            }
            else
            {
                aux = ((DsGroup) groups.get(i)).getHeader(obj, "", getName());
            }

            if (aux != null)
            {
                addList(aux, r);
            }
        }

        return r.toArray();
    }

    public Object[] getHeader(boDefHandler objDef, String prefixo)
        throws boRuntimeException
    {
        ArrayList r = new ArrayList();
        ArrayList aux;

        for (int i = 0; i < groups.size(); i++)
        {
            if ("block".equalsIgnoreCase(getType()))
            {
                aux = ((DsGroup) groups.get(i)).getHeader(objDef, prefixo, null);
            }
            else
            {
                aux = ((DsGroup) groups.get(i)).getHeader(objDef, prefixo,
                        getName());
            }

            if (aux != null)
            {
                addList(aux, r);
            }
        }

        return r.toArray();
    }

    public Tabela getData(boObject obj) throws boRuntimeException
    {
        return getData(obj, new Tabela(), true);
    }

    public Tabela getData(boObject obj, Tabela tab, boolean startLine)
        throws boRuntimeException
    {
        if ("block".equalsIgnoreCase(getType()))
        {
            if (startLine)
            {
                tab.startEditingLine();
            }

            for (int i = 0; i < groups.size(); i++)
            {
                ((DsGroup) groups.get(i)).getData(obj, tab);
            }

            if (startLine)
            {
                tab.endEditingline();
            }
        }
        else if ("repeatBlock".equalsIgnoreCase(getType()))
        {
            bridgeHandler bh = obj.getBridge(getName());
            bh.beforeFirst();

            boolean first = true;

            while (bh.next())
            {
                if (startLine)
                {
                    tab.startEditingLine();
                }
                else
                {
                    //estou numa bridge dentro de outra
                    //por cada linha desta bridge tenho que duplicar excepto a primeira
                    if (!first)
                    {
                        tab.endEditingline();
                        tab.startEditingLine();
                        tab.copyLastLine();
                    }
                    else
                    {
                        //começa a marcar os headers pertencentes a bridge
                        //de forma que ao copiar última linha estes valores 
                        //não sejam copiados
                        tab.startMarking();
                    }
                }

                for (int i = 0; i < groups.size(); i++)
                {
                    ((DsGroup) groups.get(i)).getBridgeData(bh, tab);
                }

                if (startLine)
                {
                    tab.endEditingline();
                }

                first = false;
            }

            tab.stopMarking();
        }

        return tab;
    }

    private void addList(ArrayList from, ArrayList to)
    {
        for (int i = 0; i < from.size(); i++)
        {
            if (!to.contains(from.get(i)))
            {
                to.add(from.get(i));
            }
        }
    }
}
