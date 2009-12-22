package netgest.bo.presentation.render.elements;
import java.io.IOException;
import java.io.PrintWriter;

import java.lang.reflect.Constructor;
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

public class ColumnsProvider 
{
    public ColumnProvider[] p_cols;
    private Hashtable p_attributes;
    
    public ColumnsProvider()
    {
        p_attributes = new Hashtable();
    }
    
    public void readDefinition(ngtXMLHandler treeDef)
    {
    
    }
    
    public void readAttributes(ngtXMLHandler treeDef, boDefHandler p_bodef, EboContext boctx)
    {
        ngtXMLHandler[] attributes = treeDef.getChildNode("attributes").getChildNodes();
        String scope = treeDef.getChildNode("attributes").getAttribute("scope",
				"restricted");


        for (int i = 0; i < attributes.length; i++) 
        {
            // psantos ini
            if( attributes[i].getText() != null &&  attributes[i].getText().trim().length()>0 )
            {
                if(attributes[i].getAttribute("columnsProvider") != null && 
                    attributes[i].getAttribute("columnsProvider").trim().length()>0)
                {
                    ColumnsProvInt c= getClassObject(attributes[i], p_bodef, boctx);
                    if(c != null)
                    {
                        c.setColumns(boctx, p_attributes);
                    }
                }
                else
                {
                    p_attributes.put(attributes[i].getText(), new ObjectColumn(attributes[i], p_bodef, boctx));
                }
            }
            else
            {
                if(attributes[i].getAttribute("columnsProvider") != null && 
                    attributes[i].getAttribute("columnsProvider").trim().length()>0)
                {
                    ColumnsProvInt c= getClassObject(attributes[i], p_bodef, boctx);
                    if(c!= null)
                    {
                        c.setColumns(boctx, p_attributes);
                    }
                }
                else
                {
                    p_attributes.put(attributes[i].getNodeName(), new ObjectColumn(attributes[i], p_bodef, boctx));
                }
            }
            // psantos fim
        }
        if (scope.equalsIgnoreCase("all")) 
        {
            getAttributesComposed(p_bodef, boctx, "", "", true, false, null);
        }
        ColumnProvider[] attrs = (ColumnProvider[]) p_attributes.values()
                                                                              .toArray(new ColumnProvider[p_attributes.size()]);
                                                                              
        //testar se existem atributos dependentes de bridge
        for (int i = 0; i < attrs.length; i++) 
        {
            if (attrs[i].hasSpecialClauses() ||((attrs[i].getDefAttribute() != null) &&
                    (attrs[i].getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                    !(attrs[i].getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1) &&
                    !attrs[i].getDefAttribute().getDbIsTabled())) {
                for (int j = 0; j < attrs.length; j++) {
                    if (attrs[j].getName().indexOf(attrs[i].getName()) != -1) {
                        attrs[j].setBridgeInd(i);
                    }
                }
            }
        }
    }
    
    private void getAttributesComposed(boDefHandler bodef, EboContext boctx,
        String prefix, String prefixLabel, boolean firstTime, boolean isBridge,
        boDefAttribute bridgedef) {
        if (!isBridge) {
            boDefAttribute[] attributes = bodef.getAttributesDef(firstTime);

            for (int i = 0; i < attributes.length; i++) {
                _getAttributeComposed(attributes[i].getName(), attributes[i],
                    bodef, boctx, prefix, prefixLabel, firstTime, false);
            }
        } else {
            boDefAttribute[] attributes = bridgedef.getBridge().getBoAttributes();

            for (int i = 0; i < attributes.length; i++) {
                _getAttributeComposed(attributes[i].getName(), attributes[i],
                    bodef, boctx, prefix, prefixLabel, firstTime, true);
            }
        }
    }

    private void _getAttributeComposed(String name, boDefAttribute attr,
        boDefHandler bodef, EboContext boctx, String prefix, String prefixLabel,
        boolean firstTime, boolean isBridge) {
        byte atype = attr.getAtributeType();
        name = prefix.equals("") ? name : (prefix + "." + name);

        String label = prefixLabel.equals("") ? attr.getLabel()
                                              : (prefixLabel + ">" +
            attr.getLabel());

        if (!p_attributes.containsKey(name)) {
            p_attributes.put(name,
                new ComposedColumn(name, label, attr, bodef, boctx));

            boDefHandler bodefRef = null;
            boolean orphan = false;
            char charTypeRel = ' ';

            if (atype == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                byte typeRelation = attr.getRelationType();

                if (typeRelation == boDefAttribute.RELATION_1_TO_1) {
                    charTypeRel = '1';
                } else if (typeRelation == boDefAttribute.RELATION_1_TO_N) {
                    charTypeRel = 'N';
                } else if (typeRelation == boDefAttribute.RELATION_MULTI_VALUES) {
                    charTypeRel = 'N';
                } else if (typeRelation == boDefAttribute.RELATION_1_TO_N_WBRIDGE) {
                    charTypeRel = 'B';
                }

                String charType = attr.getType();
                bodefRef = attr.getReferencedObjectDef();
                orphan = attr.getChildIsOrphan();
            }

            if ((atype == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                    (firstTime || !orphan)) {
                if ((bodefRef != null) &&
                        !bodefRef.getName().equalsIgnoreCase("boObject") &&
                        (charTypeRel == '1')) {
                    boDefAttribute[] subAttr = bodefRef.getAttributesDef();

                    for (int i = 0; i < subAttr.length; i++) {
                        getAttributesComposed(bodefRef, boctx, name, label,
                            false, false, null);
                    }
                } else if ((bodefRef != null) && (charTypeRel == 'B')) {
                    // boDefAttribute[] subAttr = attr.getBridge().getBoAttributes();
                    // for (int i = 0; i < subAttr.length ; i++) 
                    // {
                    getAttributesComposed(bodef, boctx, name, label, false, true,
                        attr);

                    // }
                }
            }
        }
    }
    
    public void readCols(ngtXMLHandler treeUserdef, boDefHandler p_bodef, EboContext boctx)
    {
        ngtXMLHandler[] cols = treeUserdef.getChildNode("cols").getChildNodes();
        p_cols = new ColumnProvider[cols.length];

        for (int i = 0; i < cols.length; i++) {
            String xatr = cols[i].getChildNode("attribute").getText();
            p_cols[i] = (ColumnProvider) p_attributes.get(xatr);
        }
    }

    public void readOrders(ngtXMLHandler treeUserdef, boDefHandler p_bodef, EboContext boctx, String[] p_orders, String[] p_ordersDirection)
    {
        ngtXMLHandler order = treeUserdef.getChildNode("order");

        if (order != null) {
            ngtXMLHandler[] orders = order.getChildNodes();

            for (int i = 0; i < orders.length; i++) {
                p_orders[i] = orders[i].getText();
                p_ordersDirection[i] = orders[i].getAttribute("direction", "asc");
            }
        }
    }
    
    public ColumnProvider getAttribute(String attKey)
    {
        return (ColumnProvider) p_attributes.get(attKey);
    }
    
    public Hashtable getAllAttributes()
    {
        return p_attributes;
    }
    
    public ColumnProvider[] getAttributes() 
    {
        ColumnProvider[] listAttributesArray = new ColumnProvider[p_attributes.size()];
        Enumeration attrs = p_attributes.elements();
        int j = 0;

        ColumnProvider listAttribute = null;
        while (attrs.hasMoreElements()) {
            listAttribute = (ColumnProvider) attrs.nextElement();
            listAttributesArray[j++] = listAttribute;
        }

        Arrays.sort(listAttributesArray,
            new ColumnsProvider.LabelComparator());

        return listAttributesArray;
    }
    
    private static class LabelComparator implements Comparator {
        public final int compare(Object a, Object b) {
            return ((String) ((ColumnProvider) a).getLabel()).compareTo((String) ((ColumnProvider) b).getLabel());
        }
    }
    
     public void setCols(String[] cols) 
     {
        p_cols = new ColumnProvider[cols.length];

        for (int i = 0; i < cols.length; i++) {
            if ((cols[i] != null) && !cols[i].equals("")) {
                String xatr = cols[i];
                p_cols[i] = (ColumnProvider)getAttribute(xatr);
            }
        }
     }
     
     public int columnsSize()
     {
        return p_cols.length;
     }
     
     public ColumnProvider getColumn(int pos)
     {
        return p_cols[pos];
     }
     
     public ColumnProvider getColumn(String columnName)
     {
        columnName = columnName.replaceAll("\"", "");
        for (int i = 0; i < p_cols.length; i++) 
        {
            if(columnName.equalsIgnoreCase(p_cols[i].getName()))
            {
                return p_cols[i];
            }
        }
        return null;
     }
     
     public void setColumns(ColumnProvider[] values)
     {
        p_cols = values;
     }
     
     public ColumnProvider[] getColumns()
     {
        return p_cols;
     }
     
     public void setColumn(int pos, ColumnProvider value)
     {
        p_cols[pos] = value;
     }
     
     private ColumnsProvInt getClassObject( ngtXMLHandler attr, boDefHandler bodef, EboContext boctx)
    {
        try
        {
            String javaClass = attr.getAttribute("columnsProvider");
            if(javaClass == null || javaClass.trim().length() == 0)
            {
                javaClass = attr.getNodeName();
            }
            if ((javaClass != null) && !"".equals(javaClass.trim()))
            {
                Constructor providerConst = Class.forName( javaClass ).getConstructor( new Class[] { EboContext.class } );
                ColumnsProvInt toRet = (ColumnsProvInt)providerConst.newInstance(new Object[] { boctx });   
                return toRet;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public String getSpecialWhereClause(EboContext boctx)
    {
        StringBuffer sb = new StringBuffer("");
        String aux;
        boolean first = true;
        for (int i = 0; i < p_cols.length; i++) 
        {
            aux = p_cols[i].getSpecialWhereClause(boctx);
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
}