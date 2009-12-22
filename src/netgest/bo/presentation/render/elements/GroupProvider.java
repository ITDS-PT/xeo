package netgest.bo.presentation.render.elements;
import java.io.IOException;
import java.io.PrintWriter;

import java.math.BigDecimal;
import java.net.URLDecoder;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import netgest.bo.boConfig;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.presentation.render.HTMLBuilder;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.ObjectCardReport;
import netgest.bo.presentation.render.ie.components.TreeBuilder;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectUtils;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.userquery.userquery;

import netgest.utils.ngtXMLHandler;

import oracle.xml.parser.v2.XMLCDATA;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;

import netgest.bo.system.Logger;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class GroupProvider 
{
    public ColumnProvider[] p_groups;
    public boolean[] p_groups_optionOPEN;
    public int[] p_groups_optionMAXLINES;
    public boolean[] p_groups_optionSHOWALLVALUES;
    public byte[] p_groups_order;

    public GroupProvider()
    {
    }
    
    public void readGroups(ngtXMLHandler treeUserdef, EboContext boctx, ColumnsProvider colProv)
    {
        ngtXMLHandler[] groups = new ngtXMLHandler[0];
        if(treeUserdef.getChildNode("groups") != null)
        {
            groups = treeUserdef.getChildNode("groups").getChildNodes();
        }
        treeUserdef.getChildNode("cols").getChildNodes();

        p_groups = new ColumnProvider[groups.length];
        p_groups_optionMAXLINES = new int[groups.length];
        p_groups_optionOPEN = new boolean[groups.length];
        p_groups_optionSHOWALLVALUES = new boolean[groups.length];
        p_groups_order = new byte[groups.length];

        for (int i = 0; i < groups.length; i++) {
            String xatr = groups[i].getChildNode("attribute").getText().trim();

            p_groups[i] = colProv.getAttribute(xatr);
            p_groups_optionMAXLINES[i] = 30; //ClassUtils.convertToInt( groups[i].getAttribute( "maxlines","30" ) ,30);
            p_groups_optionOPEN[i] = false; //groups[i].getAttribute("open","NO").toUpperCase().equalsIgnoreCase("NO")? false:true;
            p_groups_optionSHOWALLVALUES[i] = false; //groups[i].getAttribute("showAllVAlues","NO").toUpperCase().equalsIgnoreCase("NO")? false:true;

            if (groups[i].getAttribute("order", "asc").equalsIgnoreCase("asc")) {
                p_groups_order[i] = Explorer.ORDER_ASC; //ASC
            } else {
                p_groups_order[i] = Explorer.ORDER_DESC;
            }
        }
    }
    
    public int groupSize()
    {
        return p_groups.length;
    }
    
    public ColumnProvider getGroup(int pos)
    {
        return p_groups[pos];
    }
    
    public ColumnProvider[] getGroups()
    {
        return p_groups;
    }
    
    public byte getGroupOrder(int pos)
    {
        return p_groups_order[pos];
    }
    
    public void setGroup(int pos, ColumnProvider value)
    {
        p_groups[pos] = value;
    }
    
    public int getGroupOptionMaxLines(int pos)
    {
        return p_groups_optionMAXLINES[pos];
    }
    
    public boolean getGroupOptionOpen(int pos)
    {
        return p_groups_optionOPEN[pos];
    }
    
    public boolean getGroupOptionShowAllValues(int pos)
    {
        return p_groups_optionSHOWALLVALUES[pos];
    }
    
    public void setGroups(ColumnProvider[] values)
    {
        p_groups = values;
        if(p_groups != null)
        {
            p_groups_optionMAXLINES = new int[p_groups.length];
            p_groups_optionOPEN = new boolean[p_groups.length];
            p_groups_optionSHOWALLVALUES = new boolean[p_groups.length];
            p_groups_order = new byte[p_groups.length];
            for (int i = 0; i < p_groups.length; i++) {
                p_groups_optionMAXLINES[i] = 30; //ClassUtils.convertToInt( groups[i].getAttribute( "maxlines","30" ) ,30);
                p_groups_optionOPEN[i] = false; //groups[i].getAttribute("open","NO").toUpperCase().equalsIgnoreCase("NO")? false:true;
                p_groups_optionSHOWALLVALUES[i] = false; //groups[i].getAttribute("showAllVAlues","NO").toUpperCase().equalsIgnoreCase("NO")? false:true;
                p_groups_order[i] = Explorer.ORDER_ASC;
            }
        }
        else
        {
            p_groups_optionMAXLINES = new int[0];
            p_groups_optionOPEN = new boolean[0];
            p_groups_optionSHOWALLVALUES = new boolean[0];
            p_groups_order = new byte[0];
        }
    }
    
    public void setGroupOptionMaxLines(int[] values)
    {
        p_groups_optionMAXLINES = values;
    }
    
    public void setGroupOptionOpen(boolean[] values)
    {
        p_groups_optionOPEN = values;
    }
    
    public void setGroupOptionShowAllValues(boolean[] values)
    {
        p_groups_optionSHOWALLVALUES = values;
    }
    
    public void setGroupOrder(byte[] values)
    {
        p_groups_order = values;
    }
    
    public void setGroupOrder(int pos, byte value)
    {
        p_groups_order[pos] = value;
    }
    
    public int groupOrderSize()
    {
        return p_groups_order.length;
    }
    
    public String getSpecialWhereClause(EboContext boctx)
    {
        StringBuffer sb = new StringBuffer("");
        String aux;
        boolean first = true;
        for (int i = 0; i < p_groups.length; i++) 
        {
            aux = p_groups[i].getSpecialWhereClause(boctx);
            if(aux != null && aux.length() > 0)
            {
                if(!first)
                {
                    sb.append(" OR ");
                }
                else
                {
                    sb.append(" (");
                    first = false;
                }
                sb.append(aux);
            }
        }
        if(sb.length() > 0)
        {
            sb.append(") ");
        }
        return sb.toString();
    }
    
    public boolean hasClassColumns()
    {
        for (int i = 0; i < p_groups.length; i++) 
        {
            if(p_groups[i].hasSpecialClauses())
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean columnInGroup(ColumnProvider cp)
    {
        for (int i = 0; i < p_groups.length; i++) 
        {
            if(p_groups[i] == cp)
            {
                return true;
            }
        }
        return false;
    }
}