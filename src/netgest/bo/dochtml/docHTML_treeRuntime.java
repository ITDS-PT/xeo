/*Enconding=UTF-8*/
package netgest.bo.dochtml;

import netgest.bo.def.*;

import netgest.bo.ql.*;

import netgest.bo.runtime.*;

import netgest.bo.userquery.*;

import netgest.utils.*;

import oracle.xml.parser.v2.*;

import org.w3c.dom.*;

import java.io.*;

import java.sql.*;
import java.sql.Date;

import java.util.*;

import javax.servlet.jsp.*;


public class docHTML_treeRuntime {
    public static final byte ORDER_ASC = 0;
    public static final byte ORDER_DESC = 1;
    private Hashtable p_attributes;
    public docHTML_treeAttribute[] p_groups;
    public boolean[] p_groups_optionOPEN;
    public int[] p_groups_optionMAXLINES;
    public boolean[] p_groups_optionSHOWALLVALUES;
    public byte[] p_groups_order;
    public String[] p_orders = new String[3];
    public String[] p_ordersDirection = new String[3];
    public ngtXMLHandler p_treeDef;
    public String p_focusGroup;
    public docHTML_treeAttribute[] p_cols;
    public boDefHandler p_bodef;
    public String p_key;
    public Hashtable p_runtimeGroupsStatus = new Hashtable(); // tem a Key do grupo e uma String com OPEN-PAGE
    public String p_originalBOQL;
    public String p_resultBOQL;
    public String p_textFullSearch;
    public String p_filterName;
    public String p_textUserQuery;
	public String p_lasttextUserQuery;
    public StringBuffer p_extraColumns;
    public long p_bouiUserQuery = -1;
    public int p_htmlLinesPerPage = 50;
    public int p_htmlCurrentPage = 1;
    public boolean p_haveErrors = false;
	public boolean p_showUserParameters = true;
    public ArrayList p_userParameters=new ArrayList();
    public ArrayList p_userNullIgnore=new ArrayList();
    public Vector p_parameters = new Vector();
    public Hashtable p_maxCols; // contem o número máximo de colunas para cada bridge

    public docHTML_treeRuntime(String Key, docHTML DOC, boDefHandler bodef,
        ngtXMLHandler treeDef, ngtXMLHandler treeUserdef) {
        p_bodef = bodef;
        p_key = Key;

        p_textFullSearch = "";
        p_filterName = "";
        p_attributes = new Hashtable();

        p_treeDef = treeDef;

        String boql = treeDef.getChildNode("boql").getText();

        QLParser ql = new QLParser();
        String sql = ql.getFromAndWhereClause(boql, DOC.getEboContext());
        p_bodef = ql.getObjectDef();

        p_originalBOQL = boql.substring(boql.toUpperCase().indexOf("WHERE") +
                5);

        ngtXMLHandler[] attributes = treeDef.getChildNode("attributes")
                                            .getChildNodes();
        String scope = treeDef.getChildNode("attributes").getAttribute("scope",
                "restricted");

        if (scope.equalsIgnoreCase("restricted")) {
            for (int i = 0; i < attributes.length; i++) {
                p_attributes.put(attributes[i].getNodeName(),
                    new docHTML_treeAttribute(attributes[i], p_bodef, DOC));
            }
        } else if (scope.equalsIgnoreCase("all")) {
            for (int i = 0; i < attributes.length; i++) {
                p_attributes.put(attributes[i].getNodeName(),
                    new docHTML_treeAttribute(attributes[i], p_bodef, DOC));
            }

            //            boDefHandler[] subClasses = null;
            //            subClasses = bodef.getTreeSubClasses();
            getAttributesComposed(p_bodef, DOC, "", "", true, false, null);

            //            for (int i = 0; i < subClasses.length; i++)
            //            {
            //                boDefHandler bo = subClasses[i];
            //                getAttributeComposed( bo , DOC,"","",true,false );
            //            }
        }

        docHTML_treeAttribute[] attrs = (docHTML_treeAttribute[]) p_attributes.values()
                                                                              .toArray(new docHTML_treeAttribute[p_attributes.size()]);

        //testar se existem atributos dependentes de bridge
        for (int i = 0; i < attrs.length; i++) {
            if ((attrs[i].p_defatr != null) &&
                    (attrs[i].p_defatr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                    !(attrs[i].p_defatr.getRelationType() == boDefAttribute.RELATION_1_TO_1) &&
                    !attrs[i].p_defatr.getDbIsTabled()) {
                for (int j = 0; j < attrs.length; j++) {
                    if (attrs[j].p_name.indexOf(attrs[i].p_name) != -1) {
                        attrs[j].p_bridgeInd = i;
                    }
                }
            }
        }

        ngtXMLHandler[] groups = treeUserdef.getChildNode("groups")
                                            .getChildNodes();

        p_groups = new docHTML_treeAttribute[groups.length];
        p_groups_optionMAXLINES = new int[groups.length];
        p_groups_optionOPEN = new boolean[groups.length];
        p_groups_optionSHOWALLVALUES = new boolean[groups.length];
        p_groups_order = new byte[groups.length];

        for (int i = 0; i < groups.length; i++) {
            String xatr = groups[i].getChildNode("attribute").getText();

            p_groups[i] = (docHTML_treeAttribute) p_attributes.get(xatr);
            p_groups_optionMAXLINES[i] = 30; //ClassUtils.convertToInt( groups[i].getAttribute( "maxlines","30" ) ,30);
            p_groups_optionOPEN[i] = false; //groups[i].getAttribute("open","NO").toUpperCase().equalsIgnoreCase("NO")? false:true;
            p_groups_optionSHOWALLVALUES[i] = false; //groups[i].getAttribute("showAllVAlues","NO").toUpperCase().equalsIgnoreCase("NO")? false:true;

            if (groups[i].getAttribute("order", "asc").equalsIgnoreCase("asc")) {
                p_groups_order[i] = this.ORDER_ASC; //ASC
            } else {
                p_groups_order[i] = this.ORDER_DESC;
            }
        }

        ngtXMLHandler[] cols = treeUserdef.getChildNode("cols").getChildNodes();
        p_cols = new docHTML_treeAttribute[cols.length];

        for (int i = 0; i < cols.length; i++) {
            String xatr = cols[i].getChildNode("attribute").getText();
            p_cols[i] = (docHTML_treeAttribute) p_attributes.get(xatr);
        }

        ngtXMLHandler order = treeUserdef.getChildNode("order");

        if (order != null) {
            ngtXMLHandler[] orders = order.getChildNodes();

            for (int i = 0; i < orders.length; i++) {
                p_orders[i] = orders[i].getText();
                p_ordersDirection[i] = orders[i].getAttribute("direction", "asc");
            }
        }
    }

	public void setBouiUserQuery( EboContext ctx , long bouiUserquery ) throws boRuntimeException
    {
        if ( p_bouiUserQuery != bouiUserquery )
        {
            p_userParameters = new ArrayList();
            p_userNullIgnore = new ArrayList();
            p_bouiUserQuery = bouiUserquery;
            userquery.readParameters( ctx , bouiUserquery,p_userParameters, p_userNullIgnore);
        }

    }

    public void setTextUserQuery( EboContext ctx , String textUserquery )
    {
        if ( p_textUserQuery!= textUserquery )
        {
            p_textUserQuery = textUserquery;
        }
    }
    public boolean haveParameters( EboContext ctx ) throws boRuntimeException
    {
        if ( p_userParameters == null || p_userParameters.size() == 0 ) return false;
        return true;
    }

    public boolean haveBlankParameters( EboContext ctx ) throws boRuntimeException
    {
        boolean haveParams=false;
        if ( p_userParameters == null || p_userParameters.size() == 0 ) return false;
        for (int i = 0; i <  p_userParameters.size() ; i++)
        {
            if ( p_userParameters.get(i) == null && "0".equals(p_userNullIgnore.get(i)))
            {
                return true;
            }
        }
        return false;

    }

    public void setParametersQuery( String parametersQuery )
    {
        ngtXMLHandler xml= new ngtXMLHandler( parametersQuery );
        ngtXMLHandler[] xmlParameters=xml.getFirstChild().getChildNodes();
        p_userParameters = new ArrayList();
        p_userNullIgnore = new ArrayList();
        for (int i = 0; i < xmlParameters.length ; i++)
        {
            if(xmlParameters[i].getChildNode("value") != null)
            {
                String value = xmlParameters[i].getChildNode("value").getText();
                if( value!=null && value.length() == 0 ) p_userParameters.add(null);
                else p_userParameters.add( value );
                p_userNullIgnore.add(xmlParameters[i].getChildNode("nullIgnore").getText());

            }
        }


    }

    private void getAttributesComposed(boDefHandler bodef, docHTML DOC,
        String prefix, String prefixLabel, boolean firstTime, boolean isBridge,
        boDefAttribute bridgedef) {
        if (!isBridge) {
            boDefAttribute[] attributes = bodef.getAttributesDef(firstTime);

            for (int i = 0; i < attributes.length; i++) {
                _getAttributeComposed(attributes[i].getName(), attributes[i],
                    bodef, DOC, prefix, prefixLabel, firstTime, false);
            }
        } else {
            boDefAttribute[] attributes = bridgedef.getBridge().getBoAttributes();

            for (int i = 0; i < attributes.length; i++) {
                _getAttributeComposed(attributes[i].getName(), attributes[i],
                    bodef, DOC, prefix, prefixLabel, firstTime, true);
            }
        }
    }

    private void _getAttributeComposed(String name, boDefAttribute attr,
        boDefHandler bodef, docHTML DOC, String prefix, String prefixLabel,
        boolean firstTime, boolean isBridge) {
        byte atype = attr.getAtributeType();
        name = prefix.equals("") ? name : (prefix + "." + name);

        String label = prefixLabel.equals("") ? attr.getLabel()
                                              : (prefixLabel + ">" +
            attr.getLabel());

        if (!p_attributes.containsKey(name)) {
            p_attributes.put(name,
                new docHTML_treeAttribute(name, label, attr, bodef, DOC));

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
                        getAttributesComposed(bodefRef, DOC, name, label,
                            false, false, null);
                    }
                } else if ((bodefRef != null) && (charTypeRel == 'B')) {
                    // boDefAttribute[] subAttr = attr.getBridge().getBoAttributes();
                    // for (int i = 0; i < subAttr.length ; i++)
                    // {
                    getAttributesComposed(bodef, DOC, name, label, false, true,
                        attr);

                    // }
                }
            }
        }
    }

    public ngtXMLHandler buildUserXML() {
        XMLDocument xBody;
        Element xnode;
        xBody = new XMLDocument();

        xnode = xBody.createElement("treeView");
        xBody.appendChild(xnode);

        Element xCols = xBody.createElement("cols");
        xnode.appendChild(xCols);

        Element xGroups = xBody.createElement("groups");
        xnode.appendChild(xGroups);

        Element xOrders = xBody.createElement("order");
        xnode.appendChild(xOrders);

        for (int i = 0; i < p_cols.length; i++) {
            Element xcol = xBody.createElement("col");
            Element xatr = xBody.createElement("attribute");
            xatr.appendChild(xBody.createTextNode(p_cols[i].getName()));
            xcol.appendChild(xatr);
            xCols.appendChild(xcol);
        }

        for (int i = 0; i < p_groups.length; i++) {
            Element xcol = xBody.createElement("group");
            Element xatr = xBody.createElement("attribute");
            xatr.appendChild(xBody.createTextNode(p_groups[i].getName()));
            xcol.appendChild(xatr);
            xatr.setAttribute("order",
                (p_groups_order[i] == this.ORDER_ASC) ? "asc" : "desc");
            xGroups.appendChild(xcol);
        }

        for (int i = 0; i < p_orders.length; i++) {
            if (p_orders[i] != null) {
                Element xatr = xBody.createElement("attribute");
                xatr.setAttribute("direction", p_ordersDirection[i]);
                xatr.appendChild(xBody.createTextNode(p_orders[i]));
                xOrders.appendChild(xatr);
            }
        }

        return new ngtXMLHandler(xBody);
    }

    public String getOrderByString() {
        String toRet = null;

        for (int i = 0; i < p_orders.length; i++) {
            if (p_orders[i] != null) {
                for (int z = 0; z < p_cols.length; z++) {
                    if (p_cols[z].getName().equalsIgnoreCase(p_orders[i])) {
                        if (toRet == null) {
                            if (p_cols[z].p_isAttribute) {
                                toRet = "order by " + p_cols[z].getName() +
                                    " " + p_ordersDirection[i];
                            } else {
                                toRet = "order by " + p_cols[z].p_sql + " " +
                                    p_ordersDirection[i];
                            }
                        } else {
                            if (p_cols[z].p_isAttribute) {
                                toRet += ("," + p_cols[z].getName() + " " +
                                p_ordersDirection[i]);
                            } else {
                                toRet += ("," + p_cols[z].p_sql + " " +
                                p_ordersDirection[i]);
                            }
                        }
                    }
                }
            }
        }

        if (toRet == null) {
            toRet = "";
        }

        return toRet;
    }

    public String getImageSort(String xcol) {
        if ((p_orders[0] != null) && xcol.equalsIgnoreCase(p_orders[0])) {
            String img = "templates/grid/std/" +
                p_ordersDirection[0].toLowerCase() + ".gif";

            return "<img style='border:0' border'0' class=direction src='" +
            img + "' hspace=3 height=10 width=10 />";
        }

        return "";
    }

    public String getImageSort(byte order) {
        String img = "templates/grid/std/" +
            ((order == this.ORDER_ASC) ? "asc" : "desc") + ".gif";

        return "<img style='border:0' border'0' class=direction src=\"" + img +
        "\" hspace=3 height=10 width=10 />";
    }

    public void setOrderCol(String xatr) {
        if ((p_orders[0] != null) && p_orders[0].equalsIgnoreCase(xatr)) {
            if (p_ordersDirection[0].equalsIgnoreCase("asc")) {
                p_ordersDirection[0] = "desc";
            } else {
                p_ordersDirection[0] = "asc";
            }
        } else {
            for (int i = 1; i < p_orders.length; i++) {
                p_orders[i] = p_orders[i - 1];
                p_ordersDirection[i] = p_ordersDirection[i - 1];
            }

            p_orders[0] = xatr;
            p_ordersDirection[0] = "desc";
        }
    }

    //    public String getExtensionAttributes()
    //    {
    //        Enumeration oEnum=p_attributes.elements();
    //        String toRet=null;
    //        while ( oEnum.hasMoreElements() )
    //        {
    //            docHTML_treeAttribute atr=( docHTML_treeAttribute ) oEnum.nextElement();
    //            if ( atr.p_valuesResult != null )
    //            {
    //                if ( toRet==null ) toRet=atr.p_sql+" "+atr.getName();
    //                else
    //                {
    //                    toRet=toRet+","+atr.p_sql+" "+atr.getName();
    //                }
    //            }
    //
    //        }
    //        if( toRet!=null) toRet=","+toRet;
    //        else toRet="";
    //        return toRet;
    //    }
    //
    public docHTML_treeAttribute[] getAttributes() {
        docHTML_treeAttribute[] listAttributesArray = new docHTML_treeAttribute[p_attributes.size()];
        Enumeration attrs = p_attributes.elements();
        int j = 0;

        while (attrs.hasMoreElements()) {
            docHTML_treeAttribute listAttribute = (docHTML_treeAttribute) attrs.nextElement();
            listAttributesArray[j++] = listAttribute;
        }

        Arrays.sort(listAttributesArray,
            new docHTML_treeRuntime.labelComparator());

        return listAttributesArray;
    }

    public docHTML_treeAttribute[] getCols() {
        return p_cols;
    }

    public docHTML_treeAttribute[] getGroups() {
        return p_groups;
    }

    public String getGroupLabel(int groupnumber) {
        return p_groups[groupnumber].getLabel();
    }

    public String getGroupValue(ResultSet rslt, int groupnumber)
        throws SQLException {
        byte x = p_groups[groupnumber - 1].getType();
        String toRet = "";

        if (x == boDefAttribute.VALUE_BOOLEAN) {
            if (rslt.getBoolean(groupnumber)) {
                toRet = "1";
            } else {
                toRet = "0";
            }
        } else if (x == boDefAttribute.VALUE_CHAR) {
            toRet = rslt.getString(groupnumber);

            if (toRet == null) {
                toRet = "";
            }
        } else if ((x == boDefAttribute.VALUE_NUMBER) ||
                (x == boDefAttribute.VALUE_DURATION)) {
            toRet = new Long(rslt.getLong(groupnumber)).toString();

            //toRet=toRet.trim();
        } else if (x == boDefAttribute.VALUE_DATETIME) {
            Date xx = rslt.getDate(groupnumber);

            if (xx != null) {
                toRet = xx.toString();
            }
        } else if (x == boDefAttribute.VALUE_DATE) {
            Date xx = rslt.getDate(groupnumber);

            if (xx != null) {
                toRet = xx.toString();
            }

            //java.util.Date xdate=new  java.util.Date();
        }

        if (rslt.wasNull()) {
            toRet = "";
        }

        return toRet;
    }

    public Hashtable getAllAttributes() {
        return p_attributes;
    }

    public String getExtAtrHTML(ResultSet rslt, String atrname, docHTML DOC)
        throws SQLException, boRuntimeException {
        if (atrname.indexOf('$') > -1) {
            atrname = atrname.replaceAll("\\$", "\\.");
        }

        docHTML_treeAttribute atrext = (docHTML_treeAttribute) p_attributes.get(atrname);

        if (atrname.indexOf('.') > -1) {
            atrname = atrname.replaceAll("\\.", "\\$");
        }

        if (atrext == null) {
            return "&nbsp;";
        }

        byte x = atrext.getType();

        String toRet = "";

        if (x == boDefAttribute.VALUE_BOOLEAN) {
            if (rslt.getBoolean(atrname)) {
                toRet = "1";
            } else {
                toRet = "0";
            }
        } else if (x == boDefAttribute.VALUE_CHAR) {
            toRet = rslt.getString(atrname);

            if (toRet == null) {
                toRet = "";
            }
        } else if ((x == boDefAttribute.VALUE_NUMBER) ||
                (x == boDefAttribute.VALUE_DURATION)) {
            toRet = new Long(rslt.getLong(atrname)).toString();

            //toRet=toRet.trim();
        } else if (x == boDefAttribute.VALUE_DATETIME) {
            Date xx = rslt.getDate(atrname);

            if (xx != null) {
                toRet = xx.toString();
            }
        } else if (x == boDefAttribute.VALUE_DATE) {
            Date xx = rslt.getDate(atrname);
            toRet = xx.toString();

            //java.util.Date xdate=new  java.util.Date();
        }

        String v = atrext.getValueResult(toRet, DOC);
        v = "<font color='" + atrext.getColorResult(toRet) + "' >" + v +
            "</font>";

        return v;
    }

    public void setCols(String[] cols) {
        p_cols = new docHTML_treeAttribute[cols.length];

        for (int i = 0; i < cols.length; i++) {
            if ((cols[i] != null) && !cols[i].equals("")) {
                String xatr = cols[i];
                p_cols[i] = (docHTML_treeAttribute) p_attributes.get(xatr);
            }
        }
    }

    public boolean groupIsOpen(String key, int groupNumber) {
        boolean toRet = false;
        Boolean isOpen = (Boolean) p_runtimeGroupsStatus.get(key);

        if (isOpen == null) {
            toRet = false; // p_groups_optionOPEN[ groupNumber ];
        } else {
            toRet = isOpen.booleanValue();
        }

        return toRet;
    }

    public void openGroup(String key) {
        //( Boolean) p_runtimeGroupsStatus.get(key);
        //p_runtimeGroupsStatus.remove( key );
        p_runtimeGroupsStatus.put(key, new Boolean(true));
        p_focusGroup = key;
    }

    public void closeGroup(String key) {
        //p_runtimeGroupsStatus.put( key , new Boolean( false ) );
        p_runtimeGroupsStatus.remove(key);
        p_focusGroup = key;
    }

    public String groupSQL(int groupNumber, String value) {
        String toRet = "";

        if (p_groups[groupNumber].p_isAttribute) {
            toRet = p_groups[groupNumber].getName();
        } else {
            toRet = p_groups[groupNumber].p_sql;
        }

        byte x = p_groups[groupNumber].getType();

        if (x == boDefAttribute.VALUE_BOOLEAN) {
            if ((value == null) || value.equals("")) {
                //toRet= toRet+" is null or "+toRet+" = '"+value+"'";
                toRet = toRet + " is null";
            }
            else {
                if (value.equals("0")) {
                    toRet += " = '0'";
                } else {
                    toRet += " = '1'";
                }
            }
        } else if (x == boDefAttribute.VALUE_CHAR) {
            if ((value == null) || value.equals("")) {
                //toRet= toRet+" is null or "+toRet+" = '"+value+"'";
                //toRet= toRet+" is null or "+toRet+" = ?";
                toRet = toRet + " is null ";

                //p_parameters.add( value );
            }
            else {
                //toRet+=" = '"+value+"'";
                toRet += " = ?";
                p_parameters.add(value);
            }
        } else if ((x == boDefAttribute.VALUE_NUMBER) ||
                (x == boDefAttribute.VALUE_DURATION)) {
            if ((value == null) || value.equals("")) {
                toRet = toRet + " is null";
            }
            else {
                //  toRet+=" = "+value+"";
                toRet += " = ?";
                p_parameters.add(value);

                //p_parametersType.add("N");
            }
        } else if ((x == boDefAttribute.VALUE_DATETIME) ||
                (x == boDefAttribute.VALUE_DATE)) {
            if ((value == null) || value.equals("")) {
                //toRet=toRet+" is null or to_Char("+toRet+",'YYYY-MM-DD') = '"+value+"'";
                toRet = toRet + " is null or to_Char(" + toRet +
                    ",'YYYY-MM-DD') = ? ";
                p_parameters.add(value);

                //p_parametersType.add("S");
            } else {
                //toRet="to_Char("+toRet+",'YYYY-MM-DD') = '"+value+"'";
                toRet = "to_Char(" + toRet + ",'YYYY-MM-DD') = ? ";
                p_parameters.add(value);

                // p_parametersType.add("S");
            }
        }

        return toRet;
    }

    public String getGroupColor(int groupNumber, String value) {
        String toRet = p_groups[groupNumber].getColorResult(value);

        if (toRet == null) {
            toRet = "#030303";
        }

        return toRet;
    }

    public String getGroupStringToPrint(int groupNumber, String value,
        docHTML DOC) throws boRuntimeException {
        String toRet = p_groups[groupNumber].getValueResult(value, DOC);

        if ((toRet != null) && !toRet.equals("") &&
                (p_groups[groupNumber].p_defatr != null) &&
                p_groups[groupNumber].p_defatr.getType().equalsIgnoreCase("boolean")) {
            toRet = toRet.equals("0") ? "Não" : "Sim";
        } else if ((toRet != null) && !toRet.equals("") &&
                (p_groups[groupNumber].p_defatr != null) &&
                (p_groups[groupNumber].p_defatr.getLOVName() != null) &&
                !p_groups[groupNumber].p_defatr.getLOVName().equals("")) {
            toRet = boObjectUtils.getLovDescription(DOC.getEboContext(),
                    p_groups[groupNumber].p_defatr.getLOVName(), toRet);
        }

        return toRet;
    }

    public String getSqlGroups(EboContext ctx) throws boRuntimeException {
        if (p_groups.length == 0) {
            return null;
        }

        StringBuffer s = new StringBuffer();

        s.append(" select ");

        for (int i = 0; i < p_groups.length; i++) {
            if (p_groups[i].p_isAttribute) {
                s.append(p_groups[i].getName());
            } else {
                s.append(p_groups[i].p_sql);
            }

            s.append(" grp");
            s.append(i);
            s.append(',');
        }

        //            i++;
        s.append(" [count(*)] counter ");

        s.append(" from  ");
        s.append(p_bodef.getName());
        s.append(" ext where ( ");
        s.append(p_originalBOQL);
        s.append(" ) ");

        if ((p_textUserQuery != null) || (p_bouiUserQuery != -1)) {
            String boql_user1 = null;
            String boql_user2 = null;

            if (p_textUserQuery != null) {
                boql_user1 = userquery.userQueryToBoql_ClauseWhere(ctx,
                        p_textUserQuery);
            }

            if (p_bouiUserQuery != -1) {
                boql_user2=userquery.userQueryToBoql_ClauseWhere( ctx ,
						p_bouiUserQuery , p_userParameters ) ;
            }

            if ((boql_user1 != null) && (boql_user1.length() > 0)) {
                s.append(" and ( ");
                s.append(boql_user1);
                s.append(" ) ");
            }

            if ((boql_user2 != null) && (boql_user2.length() > 0)) {
                s.append(" and ( ");
                s.append(boql_user2);
                s.append(" ) ");
            }
        }

        if (!p_textFullSearch.equals("")) {
            s.append(" and  contains '");

            //s.append( p_textFullSearch );
            s.append(boObjectList.arrangeFulltext(ctx, p_textFullSearch));
            s.append("'");
        }

        s.append(" group by ");

        for (int i = 0; i < p_groups.length; i++) {
            //s.append( p_groups[i].p_sql );
            s.append(" grp");
            s.append(i);

            if ((i + 1) < p_groups.length) {
                s.append(',');
            }

            // i++;
        }

        for (int i = 0; i < p_groups.length; i++) {
            if (i == 0) {
                s.append(" order by ");
            }

            s.append(" grp");
            s.append(i);

            // s.append( p_groups[i].p_sql );
            s.append((p_groups_order[i] == this.ORDER_ASC) ? " asc" : " desc");

            if ((i + 1) < p_groups.length) {
                s.append(',');
            }
        }

        QLParser ql = new QLParser(true);
        p_parameters.clear();

        return ql.toSql(s.toString(), ctx, p_parameters);
    }

    public String getSql(EboContext ctx, String[] qryG)
        throws boRuntimeException {
        return getSql(ctx, qryG, true);
    }

    public String getSql(EboContext ctx, String[] qryG, boolean attAnalysis)
        throws boRuntimeException {
        StringBuffer s = new StringBuffer();

        p_extraColumns = new StringBuffer();
        s.append("select boui");

        for (int i = 0; (i < p_cols.length) && attAnalysis; i++) {
            if (!p_cols[i].p_isAttribute) {
                s.append(',');
                s.append(p_cols[i].p_sql);
                s.append(" ");
                s.append(p_cols[i].getName());

                if (p_extraColumns.length() != 0) {
                    p_extraColumns.append(',');
                }

                p_extraColumns.append(p_cols[i].getName());
            } else if (p_cols[i].p_isAttributeExternal) {
                //if (i!=0) s.append(',');
                //s.append( p_cols[i].getName() );
                s.append(',');
                s.append(p_cols[i].getName());
                s.append(" ");
                s.append(p_cols[i].getName().replaceAll("\\.", "\\$"));

                if (p_extraColumns.length() != 0) {
                    p_extraColumns.append(',');
                }

                p_extraColumns.append(p_cols[i].getName().replaceAll("\\.",
                        "\\$"));
            }
        }

        //            i++;
        s.append(" from  ");
        s.append(p_bodef.getName());
        s.append(" ext where ( ");
        s.append(p_originalBOQL);
        s.append(" ) ");

        p_parameters.clear();

        if ((qryG != null) && (p_groups.length > 0)) {
            s.append(" and ( ");

            for (int j = 0; j < p_groups.length; j++) {
                s.append('(');
                s.append(groupSQL(j, qryG[j]));

                if ((j + 1) < p_groups.length) {
                    s.append(") and  ");
                } else {
                    s.append(')');
                }
            }

            s.append(" ) ");
        }

        if ((p_textUserQuery != null) || (p_bouiUserQuery != -1)) {
            String boql_user1 = null;
            String boql_user2 = null;

            if (p_textUserQuery != null) {
                boql_user1 = userquery.userQueryToBoql_ClauseWhere(ctx,
                        p_textUserQuery);
            }

            if (p_bouiUserQuery != -1) {
				boql_user2=userquery.userQueryToBoql_ClauseWhere( ctx ,
						p_bouiUserQuery,p_userParameters ) ;
            }

            if ((boql_user1 != null) && (boql_user1.length() > 0)) {
                s.append(" and ( ");
                s.append(boql_user1);
                s.append(" ) ");
            }

            if ((boql_user2 != null) && (boql_user2.length() > 0)) {
                s.append(" and ( ");
                s.append(boql_user2);
                s.append(" ) ");
            }
        }

        if (!p_textFullSearch.equals("")) {
            p_parameters.add(boObjectList.arrangeFulltext(ctx,p_textFullSearch));

            //   p_parametersType.add("S");
            s.append(" and  contains ? ");

            //s.append(" and  contains '");
            //s.append( p_textFullSearch );
            //s.append("'");
        }

        s.append(" ");
        s.append(this.getOrderByString());

        QLParser ql = new QLParser(true);

        String sql = ql.toSql(s.toString(), ctx, p_parameters);

        return sql;
    }

    public String[] getSqlForExportWBridges(EboContext ctx, String[] qryG)
        throws boRuntimeException {

        Vector qryBr = new Vector();
        StringBuffer s = new StringBuffer();
        s.append("select CLASSNAME, BOUI BPR");

        //percorrer todos os atributos da tree
        for (int i = 0; i < p_cols.length; i++) {

            //testar se o atributo é do tipo bridge, ou pasaa por uma
            String[] attPath = p_cols[i].p_name.split("\\.");
            boolean hasBr = false;

            //defenição do objecto a que o atributo pertence
            boDefAttribute pathBoDefAtt = null;

            //defenição do atributo
            boDefHandler pathBoDef = p_bodef;

            //percorrer todos os atributos até chegar ao pretendido
            for (int j = 0; j < attPath.length; j++) {
                pathBoDefAtt = pathBoDef.getAttributeRef(attPath[j]);

                if (pathBoDefAtt == null) //significa que o atributo é de um objecto filho
                {
                    boDefHandler[] subDefs = pathBoDef.getTreeSubClasses();

                    for (int x = 0; x < subDefs.length; x++)
                    {
                        if ((pathBoDefAtt = subDefs[x].getAttributeRef(
                                        attPath[j])) != null) {
                            break;
                        }
                    }
                }

                if(pathBoDefAtt==null)
                  break;

                //testar se o atributo currente é do tipo bridge
                if( (pathBoDefAtt.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                    (pathBoDefAtt.getRelationType() == boDefAttribute.RELATION_MULTI_VALUES ||
                     pathBoDefAtt.getRelationType() == boDefAttribute.RELATION_1_TO_N ||
                     pathBoDefAtt.getRelationType() == boDefAttribute.RELATION_1_TO_N_WBRIDGE))
                {
                      hasBr = true;
                      break;
                }
                else if( j<attPath.length-1)
                {
                    pathBoDef = pathBoDefAtt.getReferencedObjectDef();
                }
            }


            if(hasBr)
            {
                qryBr.add(p_cols[i].getName());
            }
            else
            {
                if(p_cols[i].p_defatr==null)
                  s.append(", "+p_cols[i].p_sql);
                else
                  s.append(", "+p_cols[i].getName() );
            }
        }

        StringBuffer s2 = new StringBuffer();
        s2.append(" from "+p_bodef.getName() + " where ("+p_originalBOQL+")");

        p_parameters.clear();

        if ((qryG != null) && (p_groups.length > 0)) {
            s2.append(" and ( ");

            for (int j = 0; j < p_groups.length; j++) {
                s2.append('(');
                s2.append(groupSQL(j, qryG[j]));

                if ((j + 1) < p_groups.length) {
                    s2.append(") and  ");
                } else {
                    s2.append(')');
                }
            }

            s2.append(" ) ");
        }

        if ((p_textUserQuery != null) || (p_bouiUserQuery != -1)) {
            String boql_user1 = null;
            String boql_user2 = null;

            if (p_textUserQuery != null) {
                boql_user1 = userquery.userQueryToBoql_ClauseWhere(ctx,
                        p_textUserQuery);
            }

            if (p_bouiUserQuery != -1) {
                boql_user2=userquery.userQueryToBoql_ClauseWhere( ctx ,
						p_bouiUserQuery ,p_userParameters ) ;
            }

            if ((boql_user1 != null) && (boql_user1.length() > 0)) {
                s2.append(" and ( ");
                s2.append(boql_user1);
                s2.append(" ) ");
            }

            if ((boql_user2 != null) && (boql_user2.length() > 0)) {
                s2.append(" and ( ");
                s2.append(boql_user2);
                s2.append(" ) ");
            }
        }

        if (!p_textFullSearch.equals("")) {
            p_parameters.add(boObjectList.arrangeFulltext(ctx,p_textFullSearch));

            s2.append(" and  contains ? ");
        }

        s2.append(" ");
        s2.append(this.getOrderByString());

        s.append(s2);

        QLParser ql = new QLParser(true);

        String[] qlSql = new String[(qryBr.size()*2)+1];
        qlSql[0] =  ql.toSql(s.toString(), ctx, p_parameters).replaceAll("OE"+p_bodef.getName(),"O"+p_bodef.getName());

        //construir a query das bridges
        for (int i = 0; i < qryBr.size(); i++)
        {
          String name = (String)qryBr.get(i);
          StringBuffer s3 = new StringBuffer();
          s3.append("select BOUI, ");
          s3.append(name);
          s3.append(s2);

          qlSql[(i*2)+1] = name;
          qlSql[(i*2)+2] = ql.toSql(s3.toString(), ctx, p_parameters).replaceAll("OE"+p_bodef.getName(),"O"+p_bodef.getName());
        }

        return qlSql;
    }


	public boolean analizeSql( EboContext ctx ) throws boRuntimeException
    {

        StringBuffer s=new StringBuffer();
        boolean toRet=false;
        p_extraColumns = new StringBuffer();
        s.append("select boui");

        for (int i = 0; i < p_cols.length ; i++)
        {

           if ( !p_cols[i].p_isAttribute  )
           {
                s.append(',');
                s.append( p_cols[i].p_sql );
                s.append(" ");
                s.append( p_cols[i].getName() );
                if( p_extraColumns.length() !=0)
                {
                    p_extraColumns.append(',');
                }
                p_extraColumns.append( p_cols[i].getName() );

           }
           else if ( p_cols[i].p_isAttributeExternal )
           {
               //if (i!=0) s.append(',');
               //s.append( p_cols[i].getName() );

                s.append(',');
                s.append( p_cols[i].getName());
                s.append(" ");
                s.append( p_cols[i].getName().replaceAll("\\.","\\$") );

                if( p_extraColumns.length() !=0)
                {
                    p_extraColumns.append(',');
                }
                p_extraColumns.append( p_cols[i].getName().replaceAll("\\.","\\$") );

           }


        }
//            i++;


        s.append(" from  ");
        s.append( p_bodef.getName() );


        s.append(" ext where ( ");
        s.append( p_originalBOQL );
        s.append(" ) ");

        p_parameters.clear();




        if ( p_textUserQuery!= null   || p_bouiUserQuery!=-1)
        {
            String boql_user1=null;
            String boql_user2=null;
            if ( p_textUserQuery != null )
            {
                 boql_user1=userquery.userQueryToBoql_ClauseWhere( ctx , p_textUserQuery  );
            }
            if ( p_bouiUserQuery != -1 )
            {
                 boql_user2=userquery.userQueryToBoql_ClauseWhere( ctx , p_bouiUserQuery ,p_userParameters ) ;
            }

            if ( boql_user1!=null && boql_user1.length() >0 )
            {
                s.append(" and ( ");
                s.append( boql_user1 );
                s.append( " ) ");
            }

            if ( boql_user2!=null && boql_user2.length() >0 )
            {
                s.append(" and ( ");
                s.append( boql_user2 );
                s.append( " ) ");
            }


        }



        if ( !p_textFullSearch.equals("") )
             {
             p_parameters.add( boObjectList.arrangeFulltext(ctx,p_textFullSearch) );
          //   p_parametersType.add("S");
             s.append(" and  contains ? ");
             //s.append(" and  contains '");
             //s.append( p_textFullSearch );
             //s.append("'");

             }

        s.append(" ");
        s.append( this.getOrderByString() );

        QLParser ql =   new QLParser( true );
        String sql=null;

         try
         {
             sql = ql.toSql( s.toString() , ctx , p_parameters );
             toRet=true;
         }
         catch (Exception e)
         {



         }
        return toRet;
    }

    public String getSqlForExport(EboContext ctx, String[] qryG)
        throws boRuntimeException {
        StringBuffer s = new StringBuffer();
        s.append("select CLASSNAME, BOUI BPR");

        //percorrer todos os atributos da tree
        for (int i = 0; i < p_cols.length; i++) {
            if(p_cols[i].p_defatr==null)
              s.append(", "+p_cols[i].p_sql);
            else
              s.append(", "+p_cols[i].getName() );
        }

        s.append(" from "+p_bodef.getName() + " where ("+p_originalBOQL+")");

        p_parameters.clear();

        if ((qryG != null) && (p_groups.length > 0)) {
            s.append(" and ( ");

            for (int j = 0; j < p_groups.length; j++) {
                s.append('(');
                s.append(groupSQL(j, qryG[j]));

                if ((j + 1) < p_groups.length) {
                    s.append(") and  ");
                } else {
                    s.append(')');
                }
            }

            s.append(" ) ");
        }

        if ((p_textUserQuery != null) || (p_bouiUserQuery != -1)) {
            String boql_user1 = null;
            String boql_user2 = null;

            if (p_textUserQuery != null) {
                boql_user1 = userquery.userQueryToBoql_ClauseWhere(ctx,
                        p_textUserQuery);
            }

            if (p_bouiUserQuery != -1) {
                boql_user2=userquery.userQueryToBoql_ClauseWhere( ctx ,
						p_bouiUserQuery ,p_userParameters ) ;
            }

            if ((boql_user1 != null) && (boql_user1.length() > 0)) {
                s.append(" and ( ");
                s.append(boql_user1);
                s.append(" ) ");
            }

            if ((boql_user2 != null) && (boql_user2.length() > 0)) {
                s.append(" and ( ");
                s.append(boql_user2);
                s.append(" ) ");
            }
        }

        if (!p_textFullSearch.equals("")) {
            p_parameters.add(boObjectList.arrangeFulltext(ctx,p_textFullSearch));

            s.append(" and  contains ? ");
        }

        s.append(" ");
        s.append(this.getOrderByString());

        QLParser ql = new QLParser(true);

        String qlSQL = ql.toSql(s.toString(), ctx, p_parameters);

        qlSQL=qlSQL.replaceAll("OE"+p_bodef.getName(),"O"+p_bodef.getName());

        return qlSQL;
    }

    /**
     *
     *
     * @param srcAtr Atributo que é para mover pode estar nos Grupos ou nas colunas
     * @param destAtr Atributo de referencias para inserir o srcAtr á esquerda
     */
    public void moveAttributeToColHeader(String srcAtr, String destAtr) {
        boolean removeFromGroup = false;
        boolean removeFromOtherCol = false;

        if (srcAtr.equalsIgnoreCase(destAtr)) {
            return;
        }

        for (int i = 0; i < p_cols.length; i++) {
            if (p_cols[i].p_name.equalsIgnoreCase(srcAtr)) {
                p_cols[i] = null;
                removeFromOtherCol = true;

                break;
            }
        }

        if (!removeFromOtherCol) {
            for (int i = 0; i < p_groups.length; i++) {
                if (p_groups[i].p_name.equalsIgnoreCase(srcAtr)) {
                    p_groups[i] = null;
                    removeFromGroup = true;

                    break;
                }
            }
        }

        if (removeFromGroup) {
            this.p_htmlCurrentPage = 1;

            docHTML_treeAttribute[] x_groups = new docHTML_treeAttribute[p_groups.length -
                1];
            boolean[] x_groups_optionOPEN = new boolean[p_groups.length - 1];
            int[] x_groups_optionMAXLINES = new int[p_groups.length - 1];
            boolean[] x_groups_optionSHOWALLVALUES = new boolean[p_groups.length -
                1];
            int z = 0;

            for (int i = 0; i < p_groups.length; i++) {
                if (p_groups[i] != null) {
                    x_groups[z] = p_groups[i];
                    x_groups_optionMAXLINES[z] = p_groups_optionMAXLINES[i];
                    x_groups_optionOPEN[z] = p_groups_optionOPEN[i];
                    x_groups_optionSHOWALLVALUES[z++] = p_groups_optionSHOWALLVALUES[i];
                }
            }

            p_groups = x_groups;
            p_groups_optionMAXLINES = x_groups_optionMAXLINES;
            p_groups_optionOPEN = x_groups_optionOPEN;
            p_groups_optionSHOWALLVALUES = x_groups_optionSHOWALLVALUES;
        } else if (removeFromOtherCol) {
            docHTML_treeAttribute[] x_cols = new docHTML_treeAttribute[p_cols.length -
                1];
            String[] x_colsWidth = new String[p_cols.length - 1];
            int z = 0;

            for (int i = 0; i < p_cols.length; i++) {
                if (p_cols[i] != null) {
                    x_cols[z++] = p_cols[i];
                }
            }

            p_cols = x_cols;
        }

        if (removeFromGroup || removeFromOtherCol) {
            docHTML_treeAttribute[] x_cols = new docHTML_treeAttribute[p_cols.length +
                1];

            int z = 0;
            boolean alreadydo = false;

            for (int i = 0; i < p_cols.length; i++) {
                if (p_cols[i].p_name.equalsIgnoreCase(destAtr) && !alreadydo) {
                    x_cols[z++] = (docHTML_treeAttribute) p_attributes.get(srcAtr);
                    alreadydo = true;
                }

                x_cols[z++] = p_cols[i];
            }

            p_cols = x_cols;
        }
    }

    /**
     *
     *
     * @param srcAtr Atributo que é para mover pode estar nos Grupos ou nas colunas
     * @param destAtr Atributo de referencias para inserir o srcAtr á esquerda
     */
    public void moveAttributeToColGroup(String srcAtr, String destAtr) {
        boolean removeFromGroup = false;
        boolean removeFromOtherCol = false;

        if (srcAtr.equalsIgnoreCase(destAtr)) {
            return;
        }

        this.p_htmlCurrentPage = 1;

        for (int i = 0; i < p_groups.length; i++) {
            if (p_groups[i].p_name.equalsIgnoreCase(srcAtr)) {
                p_groups[i] = null;
                removeFromGroup = true;

                break;
            }
        }

        if (!removeFromGroup) {
            for (int i = 0; i < p_cols.length; i++) {
                if (p_cols[i].p_name.equalsIgnoreCase(srcAtr)) {
                    if (p_cols.length == 1) {
                        return;
                    }

                    p_cols[i] = null;
                    removeFromOtherCol = true;

                    break;
                }
            }
        }

        if (removeFromGroup) {
            docHTML_treeAttribute[] x_groups = new docHTML_treeAttribute[p_groups.length -
                1];
            boolean[] x_groups_optionOPEN = new boolean[p_groups.length - 1];
            int[] x_groups_optionMAXLINES = new int[p_groups.length - 1];
            boolean[] x_groups_optionSHOWALLVALUES = new boolean[p_groups.length -
                1];
            byte[] x_groups_order = new byte[p_groups.length];
            int z = 0;

            for (int i = 0; i < p_groups.length; i++) {
                if (p_groups[i] != null) {
                    x_groups[z] = p_groups[i];
                    x_groups_optionMAXLINES[z] = p_groups_optionMAXLINES[i];
                    x_groups_optionOPEN[z] = p_groups_optionOPEN[i];
                    x_groups_order[z] = p_groups_order[i];
                    x_groups_optionSHOWALLVALUES[z++] = p_groups_optionSHOWALLVALUES[i];
                }
            }

            p_groups = x_groups;
            p_groups_optionMAXLINES = x_groups_optionMAXLINES;
            p_groups_optionOPEN = x_groups_optionOPEN;
            p_groups_optionSHOWALLVALUES = x_groups_optionSHOWALLVALUES;
            p_groups_order = x_groups_order;
        } else if (removeFromOtherCol) {
            docHTML_treeAttribute[] x_cols = new docHTML_treeAttribute[p_cols.length -
                1];
            String[] x_colsWidth = new String[p_cols.length - 1];
            int z = 0;

            for (int i = 0; i < p_cols.length; i++) {
                if (p_cols[i] != null) {
                    x_cols[z++] = p_cols[i];
                }
            }

            p_cols = x_cols;
        }

        if (removeFromGroup || removeFromOtherCol) {
            docHTML_treeAttribute[] x_groups = new docHTML_treeAttribute[p_groups.length +
                1];
            boolean[] x_groups_optionOPEN = new boolean[p_groups.length + 1];
            int[] x_groups_optionMAXLINES = new int[p_groups.length + 1];
            boolean[] x_groups_optionSHOWALLVALUES = new boolean[p_groups.length +
                1];
            byte[] x_groups_order = new byte[p_groups.length + 1];
            int z = 0;
            boolean alreadydo = false;

            for (int i = 0; i < p_groups.length; i++) {
                if (p_groups[i].p_name.equalsIgnoreCase(destAtr) && !alreadydo) {
                    x_groups[z] = (docHTML_treeAttribute) p_attributes.get(srcAtr);
                    x_groups_optionMAXLINES[z] = 30;
                    x_groups_optionOPEN[z] = true;
                    x_groups_order[z] = this.ORDER_ASC;
                    x_groups_optionSHOWALLVALUES[z++] = false;

                    alreadydo = true;
                }

                x_groups[z] = p_groups[i];
                x_groups_optionMAXLINES[z] = p_groups_optionMAXLINES[i];
                x_groups_optionOPEN[z] = p_groups_optionOPEN[i];
                x_groups_order[z] = p_groups_order[i];
                x_groups_optionSHOWALLVALUES[z++] = p_groups_optionSHOWALLVALUES[i];
            }

            if (destAtr.equals("NoNe")) {
                x_groups[z] = (docHTML_treeAttribute) p_attributes.get(srcAtr);
                x_groups_optionMAXLINES[z] = 30;
                x_groups_optionOPEN[z] = true;
                x_groups_order[z] = this.ORDER_ASC;
                x_groups_optionSHOWALLVALUES[z++] = false;
            }

            p_groups = x_groups;
            p_groups_optionMAXLINES = x_groups_optionMAXLINES;
            p_groups_optionOPEN = x_groups_optionOPEN;
            p_groups_optionSHOWALLVALUES = x_groups_optionSHOWALLVALUES;
            p_groups_order = x_groups_order;
        }
    }

    private static class labelComparator implements Comparator {
        public final int compare(Object a, Object b) {
            return ((String) ((docHTML_treeAttribute) a).getLabel()).compareTo((String) ((docHTML_treeAttribute) b).getLabel());
        }
    }
}
