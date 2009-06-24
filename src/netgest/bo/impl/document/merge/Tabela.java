/*Enconding=UTF-8*/
// Copyright (c) 2001 IIES
package netgest.bo.impl.document.merge;

//imports
import java.util.ArrayList;
import netgest.bo.runtime.*;


/**
 * Este objecto representa uma linha da tabela MATRIZ_TABELA.
 * <P>
 * @author Francisco Câmara
 */
public class Tabela
{
    private ArrayList header = new ArrayList();
    private ArrayList sqlTypes = new ArrayList();
    private Object[] line;
    private ArrayList tabela = new ArrayList();
    private boolean editingLine = false;
    private int apt = -1;
    private boolean emptyLine = true;
    private ArrayList prefix = new ArrayList();
    private AliasTabela aliasTable = new AliasTabela();
    private boolean startMarking = false;
    private ArrayList markedHeaders = new ArrayList();
    private boObject obj;

    public Tabela(){}
    
    public Tabela(boObject obj)
    {
        this.obj = obj;
    }

    public void startEditingLine()
    { 
        editingLine = true;
        line = new Object[header.size()];
    }

    public void endEditingline()
    {
        if (!emptyLine)
        {
            if (line.length > header.size())
            {
                Object[] lineAux = line;
                line = new Object[header.size()];
                System.arraycopy(lineAux, 0, line, 0, header.size());
            }

            tabela.add(line);
        }

        editingLine = false;
        emptyLine = true;
    }

    public ArrayList getSqlTypes()
    {
        return sqlTypes;
    }

    public ArrayList getHeader()
    {
        return header;
    }

    public void setPrefix(String s)
    {
        prefix.add(s);
    }

    public void removePrefix()
    {
        prefix.remove(prefix.size() - 1);
    }

    public void insert(Object value, String headerName, int sqlType)
    {
        String aux;

        if ((getPrefix() != null) && (getPrefix().length() > 0))
        {
            aux = getPrefix() + "." + headerName;
        }
        else
        {
            aux = headerName;
        }

        if (startMarking && (markedHeaders.indexOf(aux) == -1))
        {
            markedHeaders.add(aux);
        }

        if (header.indexOf(aux) == -1)
        {
            header.add(aux);
            sqlTypes.add(new Integer(sqlType));
        }

        if (line.length <= header.indexOf(aux))
        {
            Object[] lineAux = line;
            line = new Object[header.size() + 10];
            System.arraycopy(lineAux, 0, line, 0, lineAux.length);
        }

        line[header.indexOf(aux)] = value;
        emptyLine = false;
    }

    public Object getValue(String name)
    {
        int col = header.indexOf(name);

        if (col > -1)
        {
            Object[] o = (Object[]) tabela.get(apt);

            if (col < o.length)
            {
                return o[col];
            }
        }

        return null;
    }

    public boolean next()
    {
        apt++;

        if (apt < tabela.size())
        {
            return true;
        }

        return false;
    }

    public void insertAlias(String k, String v)
    {
        aliasTable.insertAlias(k, v);
    }

    public String verifyAlias(String k)
    {
        String[] words = k.split("\\.");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < words.length; i++)
        {
            if (i > 0)
            {
                sb.append(".");
            }

            sb.append(aliasTable.verifyAlias(words[i]));
        }

        return sb.toString();
    }

    public String verifyValue(String v)
    {
        return aliasTable.verifyValue(v);
    }

    public void copyLastLine()
    {
        Object[] lineAux = (Object[]) tabela.get(tabela.size() - 1);
        line = new Object[lineAux.length];

        for (int i = 0; i < lineAux.length; i++)
        {
            if (markedHeaders.indexOf(((String) header.get(i))) == -1)
            {
                line[i] = lineAux[i];
            }
            else
            {
                line[i] = null;
            }
        }
    }

    public void startMarking()
    {
        startMarking = true;
    }

    public void stopMarking()
    {
        startMarking = false;
        markedHeaders = new ArrayList();
    }

    private String getPrefix()
    {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < prefix.size(); i++)
        {
            if (i != 0)
            {
                if (!((String) prefix.get(i)).startsWith(".") &&
                        !sb.toString().endsWith("."))
                {
                    sb.append(".");
                }
            }

            sb.append(prefix.get(i));
        }

        return sb.toString();
    }
    
    public void beforeFirst()
    {
        apt = -1;
    }
}
