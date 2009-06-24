package netgest.bo.presentation.render.elements;
import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import netgest.bo.def.*;
import javax.servlet.jsp.*;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.presentation.render.ie.components.TreeBuilder;
import netgest.bo.ql.*;
import netgest.bo.runtime.*;
import netgest.utils.*;

public class ClassColumn extends ColumnProvider
{
    String javaClass = null;
    
    public String       p_sql;
    public String       p_sqlGroup;
    public String       p_name;
    
    private String      p_label="";
    private String      p_color="#000000";
    public Hashtable    p_colorResult;
    public boolean      p_isAttribute;
    public boolean      p_isAttributeExternal;
    public boDefAttribute p_defatr;
    private int         p_width;   
    public byte         p_returnType;
    private String      p_showLink = null;
    private String      p_openDocument = null;
    private long         decimals;
    
    //indicador de atributo dependete de uma bridge, indica qual o index (no p_attributes) do atributo do qual este depende
    public int p_bridgeInd = -1;
    //indica quantas bridges já se imprimiu
    public int p_countBr=0;
    
    private long clfBoui = -1;
    private String clfNome = null;
    private String segmento = null;

    private long clfObjBoui;
    private String clfObjNome;
    private byte tipo;
    private String brAtt;
    private String brAttDBName;
    private long lovBoui;
    
    private String tablename = "OEBO_DOCUMENT$classification"; 
    
    public ClassColumn(boObject clf, boObject clfObject)
    {
        try
        {
            String  sSegementoCode = "";
            if( clf != null )
            {
            clfBoui = clf.getBoui();
            clfNome = clf.getAttribute("name").getValueString();
            
                
            if(clf.getAttribute("segmento").getValueString() != null &&
                clf.getAttribute("segmento").getValueString().length() > 0
            )
            {
                lovObject lovObj = LovManager.getLovObject(clf.getEboContext(), "appSegmentType");
                boolean found = false;
                if(lovObj != null)
                {
                    lovObj.beforeFirst();
                    while(!found && lovObj.next())
                    {
                        if(clf.getAttribute("segmento").getValueString().equalsIgnoreCase(lovObj.getCode()))
                        {
                            segmento = lovObj.getDescription();
                                sSegementoCode = lovObj.getCode();
                            found = true;
                        }
                    }
                }
            }
            }
            else
            {
                sSegementoCode = null;
            }
            
            if( sSegementoCode == null )
            {
                p_name="C__" + clfObject.getAttribute("internalName") ;
            } 
            else
            {
                p_name="C_" + sSegementoCode + "_" + clfObject.getAttribute("internalName") ;
            }

            clfObjBoui=clfObject.getBoui();
            clfObjNome=clfObject.getAttribute("name").getValueString();
            setTipoAtt(clfObject);
    
            String language="pt";
            p_isAttribute=false;
            p_isAttributeExternal=false;
            boDefAttribute attrdef=null;
            StringBuffer labelPrefix=new StringBuffer();
            // Label
            p_label=clfObject.getAttribute("name").getValueString();
            //showLink
            p_showLink = "";
    
            //openDocument
            p_openDocument = "";
    
            //sql
            p_sql = "[(select "+brAttDBName+" from OEBO_DOCUMENT$classification where parent$=OEEbo_Document.boui and child$="+clfObjBoui+" and rownum = 1)]";
            p_sqlGroup = "classification." + brAtt;
            
        }
        catch (boRuntimeException e)
        {
            e.printStackTrace();
        }
    }
    
    public int getWidth()
    {
        return p_width;
    }
    public String getSpecialWhereClause(EboContext ctx, String alias)
    {
        return " classification = " + clfObjBoui;
    }
        
    public String showLink()
    {
        return p_showLink;
    }
        
    public String openDocument()
    {
        return p_openDocument;
    }

    public void setWidth(int width)
    {
        p_width = width;
    }

    public boolean hasResults()
    {
        return false;
    }

    public byte getType()
    {
        return p_returnType;
    }
        
    public String getLabel()
    {
        return p_label;
    }
       
    public String getName()
    {
        return p_name;
    }
    
    public String getValueResult(String value , EboContext boctx) throws boRuntimeException
    {
        return getValueResult(value, boctx, true);
    }
    
    public String getValueResult(long boui, String value , EboContext boctx) throws boRuntimeException
    {
        return getValueResult(boui,value, boctx, true);
    }
    
    public String getValueResult(String value , EboContext boctx, boolean showLink) throws boRuntimeException
    {
        String toRet = value;
        if(value != null && value.length() > 0)
        {
            if(tipo == 1)//boObject
            {
                boObject o = boObject.getBoManager().loadObject(boctx, Long.parseLong(value));
                StringBuffer x=new StringBuffer();
                x.append("<span style='white-space: nowrap;overflow-y:hidden;background-color:transparent;border:0' class='lu ro'>");
                if(showLink)
                {
                    x.append("<span class='lui' onclick=\"");
                    x.append("winmain().openDoc('medium','");
                    x.append( o.getName().toLowerCase() );
                    x.append("','edit','method=edit&boui=");
                    x.append( value );
                    x.append("')");
                    x.append(";event.cancelBubble=true\"");
                    x.append(" boui='");
                    x.append(value);
                    x.append("' object='");
                    x.append(o.getName() );
                    x.append("'>");
                    x.append(o.getCARDID(false).toString());
                    x.append("</span></span>" );
                    toRet=x.toString();
                }
                else
                {
                    x.append(o.getCARDID(false).toString());
                    x.append("</span>");
                    toRet=x.toString();
                }
                
            }
            else if(tipo == 2)
            {
                //texto
            }
            else if(tipo == 3)//data sem horas
            {
                toRet = value!=null&&value.length() > 10?value.substring(0,10):value;
            }
            else if(tipo == 4)//lov
            {
                if(toRet != null && !"".equals(toRet))
                {
                    lovObject oLov = LovManager.getLovObject( boctx, lovBoui );
                    oLov.beforeFirst();
                    while( oLov.next() )
                    {
                        if(toRet.equalsIgnoreCase( oLov.getCode() ) )
                        {
                            toRet = oLov.getDescription();
                            break;
                        }
                    }
                
                    /*
                    
                    boObject lov = boObject.getBoManager().loadObject(boctx, lovBoui);
                    if(lov.exists())
                    {
                        boBridgeIterator lovit= lov.getBridge("details").iterator();
                        lovit.beforeFirst();
                        boObject det;
                        boolean found = false;
                        while(lovit.next() && !found)
                        {
                            det = lovit.currentRow().getObject();
                            if(toRet.equalsIgnoreCase(det.getAttribute("value").getValueString()))
                            {
                                toRet = det.getAttribute("description").getValueString();
                                found = true;
                            }
                        }
                    }
                    */
                }
            }
            else if(tipo == 5)//currency
            {
                
            }
            else if(tipo == 6)//data com horas
            {

            }
            else if(tipo == 7)//numérico
            {

            }
        }
        return toRet;
    }
    
    public String getValueResult(long boui, String value , EboContext boctx, boolean showLink) throws boRuntimeException
    {
        Connection cn = null;
        String toRet = "";
        PreparedStatement pst = null;
        ResultSet rs = null;

        try
        {
            if(value != null && !"".equals(value))
            {
                cn = boctx.getConnectionData();
//                pst =  cn.prepareStatement("select "+brAttDBName+" from OEBO_DOCUMENT$classification where parent$ = ? and "+brAttDBName+" is not null and valueClassification$ = ? and child$ = ?");
                pst =  cn.prepareStatement("select "+brAttDBName+" from OEBO_DOCUMENT$classification where parent$ = ? and "+brAttDBName+" is not null and child$ = ?");
                pst.setLong(1, boui);
//                pst.setLong(2, clfBoui);
                pst.setLong(2, clfObjBoui);
                rs = pst.executeQuery();
                if(rs.next())
                {
                    toRet = getValue(boctx, rs, tipo, lovBoui, decimals, showLink);
                }
                else
                {
                    
                }
            }
        }
        catch (Exception e)
        {
            String[] args = { "" };
            throw new boRuntimeException("pt.lusitania.events.boMessage.beforeSave","BO-3121",e,args);
        }
        finally
        {
            try{if(rs != null) rs.close();}catch(Exception e){/*IGNORE*/}
            try{if(pst != null) pst.close();}catch(Exception e){/*IGNORE*/}
        }
        return toRet;
    }
    
    public String[] getAllClassifs(EboContext boctx, long docBoui) throws boRuntimeException
    {
        Connection cn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        ArrayList r = new ArrayList();
        try
        {
            cn = boctx.getConnectionData();
//                pst =  cn.prepareStatement("select "+brAttDBName+" from OEBO_DOCUMENT$classification where parent$ = ? and "+brAttDBName+" is not null and valueClassification$ = ? and child$ = ?");
            pst =  cn.prepareStatement("select "+brAttDBName+" from OEBO_DOCUMENT$classification where parent$ = ? and "+brAttDBName+" is not null and child$ = ?");
            pst.setLong(1, docBoui);
//                pst.setLong(2, clfBoui);
            pst.setLong(2, clfObjBoui);
            rs = pst.executeQuery();
            while(rs.next())
            {
                if( tipo == 4 )
                {
                    String value = rs.getString(1);
                    boObject lov = boObject.getBoManager().loadObject(boctx, lovBoui);
                    if(lov.exists())
                    {
                        lovObject lovit= LovManager.getLovObject( boctx, lov.getAttribute("name").getValueString() );
                        lovit.beforeFirst();
                        boObject det;
                        boolean found = false;
                        while(lovit.next() && !found)
                        {
                            if(value.equalsIgnoreCase( lovit.getCode() ) )
                            {
                                r.add( lovit.getDescription() );
                                found = true;
                                break;
                            }
                        }
                    }
                }
                else
                {
                    r.add(rs.getString(1));
                }
            }
        }
        catch (Exception e)
        {
            String[] args = { "" };
            throw new boRuntimeException("pt.lusitania.events.boMessage.beforeSave","BO-3121",e,args);
        }
        finally
        {
            try{if(rs != null) rs.close();}catch(Exception e){/*IGNORE*/}
            try{if(pst != null) pst.close();}catch(Exception e){/*IGNORE*/}
        }
        if(r == null || r.size() == 0)
        {
            return new String[0];
        }
        return (String[])r.toArray(new String[r.size()]);
    }
    
    public String getColorResult( String value )
    {
       if ( p_colorResult == null ) return null;
      return ( String) p_colorResult.get( value );
    }
    
    public boDefAttribute getDefAttribute()
    {
        return p_defatr;
    }
    
    public boolean isAttribute()
    {
        return p_isAttribute;
    }
    
    public String getSQL()
    {
        return p_sql;
    }
    
    public String getSQLNoParRect()
    {
        return p_sql.substring(1, p_sql.length() -1);
    }
    
    public String getSQLGroup()
    {
        return p_sqlGroup;
    }
    
    public boolean isExternalAttribute()
    {
        return p_isAttributeExternal;
    }
    
    public void setCount_Br(int value)
    {
        p_countBr = value;
    }
    
    public void setBridgeInd(int value)
    {
        p_bridgeInd = value;
    }
    
    public int count_Br()
    {
        return p_countBr;
    }
    
    public int bridgeInd()
    {
        return p_bridgeInd;
    }

    public String changeSQL(String sql)
    {
        String aux = null;
        int iFrom = sql.toUpperCase().indexOf(" FROM ");
        int iWhere = sql.toUpperCase().indexOf(" WHERE ");
        int iOrder = 0;
        if(iWhere > 0)
        {
            aux = sql.substring(0, iWhere);
            aux = aux +  ",OEBO_DOCUMENT$CLASSIFICATION";
            aux = aux + sql.substring(iWhere);
        }
        else
        {
            iOrder = sql.toUpperCase().indexOf(" ORDER ");
            if(iOrder > 0)
            {
                aux = sql.substring(0, iOrder);
                aux = aux +  ",OEBO_DOCUMENT$CLASSIFICATION";
                aux = aux + sql.substring(iOrder);                
            }
            else
            {
                aux = sql;
                aux = aux +  ",OEBO_DOCUMENT$CLASSIFICATION";
                aux = aux + sql.substring(iOrder);
            }
        }
        iOrder = aux.toUpperCase().indexOf(" ORDER ");
        if(iOrder > 0)
        {
            String aux2 = aux.substring(0, iOrder);
            if(iWhere > 0)
            {
                aux2 = aux2 + " and (oebo_document$classification.PARENT$(+)=oeebo_document.BOUI)";
            }
            else
            {
                aux2 = aux2 + " WHERE (oebo_document$classification.PARENT$(+)=oeebo_document.BOUI)";
            }
            aux = aux2;
        }
        else
        {
            aux = aux + " and (oebo_document$classification.PARENT$(+)=oeebo_document.BOUI)";
        }
        return aux;
    }
    
    private void setTipoAtt(boObject gesdocObj) throws boRuntimeException
    {
        boDefHandler boDoc = boDefHandler.getBoDefinition("Ebo_Document");
        boDefBridge bridgeDef = boDoc.getAttributeRef("classification").getBridge();
        if(gesdocObj != null)
        {
            if("GESDocClfObject".equals(gesdocObj.getName()))
            {
                tipo = 1;
                brAtt = "valueObject";
                brAttDBName = "valueObject$";
                p_defatr = bridgeDef.getAttributeRef("valueObject");
                p_returnType=p_defatr.getValueType();
                p_width = 150;
            }
            else if("GESDocClfText".equals(gesdocObj.getName()))
            {
                tipo =  2;
                brAtt = "valueText";
                brAttDBName = "valueText";
                p_defatr = bridgeDef.getAttributeRef("valueText");
                p_returnType=p_defatr.getValueType();
                p_width = 110;
            }
            else if("GESDocClfDate".equals(gesdocObj.getName()))
            {
                tipo =  (byte)("1".equals(gesdocObj.getAttribute("dtFormat").getValueString()) ? 3:6);
                brAtt = "valueDate";
                brAttDBName = "valueDate";
                p_defatr = bridgeDef.getAttributeRef("valueDate");
                p_returnType=p_defatr.getValueType();
                p_width = 120;
            }
            else if("GESDocClfLov".equals(gesdocObj.getName()))
            {
                tipo =  4;
                brAtt = "valueText";
                brAttDBName = "valueText";
                p_defatr = bridgeDef.getAttributeRef("valueText");
                p_returnType=p_defatr.getValueType();
                p_width = 180;
                lovBoui = gesdocObj.getAttribute("lov").getValueLong();
            }
            else if("GESDocClfNumber".equals(gesdocObj.getName()))
            {
                tipo =  (byte)("1".equals(gesdocObj.getAttribute("currency").getValueString()) ? 5:7);
                decimals = gesdocObj.getAttribute("decimals").getValueLong();
                brAtt = "valueNumber";
                brAttDBName = "valueNumber";
                p_defatr = bridgeDef.getAttributeRef("valueNumber");
                p_returnType=p_defatr.getValueType();
                p_width = 110;
            }
        }
        else
        {
            tipo = -1;
            brAtt = "";
            brAttDBName = "";
            p_defatr = null;
        }
    }
    
    private static String getValue(EboContext boctx, ResultSet rs, byte tipo, long lovBoui, long decimals, boolean showLink) throws boRuntimeException, SQLException
    {
        String toRet = rs.getString(1);
        if(tipo == 1)//boObject
        {
            boObject o = boObject.getBoManager().loadObject(boctx, rs.getLong(1));
            if(showLink)
            {
                return (o.getCARDIDwLink()).toString();
            }
            else
            {
                return (o.getCARDID(false)).toString();
            }
        }
        else if(tipo == 2)
        {
            //texto
        }
        else if(tipo == 3)//data sem horas
        {
            SimpleDateFormat sdf = null;
            sdf = new SimpleDateFormat("dd/MM/yyyy");
            toRet = sdf.format(rs.getDate(1));
        }
        else if(tipo == 4)//lov
        {
            if(toRet != null && !"".equals(toRet))
            {
                boObject lov = boObject.getBoManager().loadObject(boctx, lovBoui);
                if(lov.exists())
                {
                    lovObject lovit= LovManager.getLovObject( boctx, lov.getAttribute("name").getValueString() );
                    lovit.beforeFirst();
                    boObject det;
                    boolean found = false;
                    while(lovit.next() && !found)
                    {
                        if(toRet.equalsIgnoreCase(lovit.getCode() ) )
                        {
                            toRet = lovit.getDescription();
                            found = true;
                        }
                    }
                }
            }
        }
        else if(tipo == 5)//currency
        {
            if(toRet != null && toRet.length() > 0)
            {
                NumberFormat nf = NumberFormat.getInstance();
                double d = rs.getDouble(1);
                //currency
                nf.setParseIntegerOnly(false);
                nf.setGroupingUsed(true);
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                nf.setMinimumIntegerDigits(1);
                toRet =  nf.format(d);
            }
        }
        else if(tipo == 6)//data com horas
        {
            SimpleDateFormat sdf = null;
            sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            toRet = sdf.format(rs.getTimestamp(1));
        }
        else if(tipo == 7)//numérico
        {
            if(toRet != null && toRet.length() > 0)
            {
                NumberFormat nf = NumberFormat.getInstance();
                double d = rs.getDouble(1);
                decimals = decimals < 0 ? 0:decimals;
                nf.setParseIntegerOnly(false);
                nf.setGroupingUsed(false);
                nf.setMaximumFractionDigits((int)decimals);
                nf.setMinimumFractionDigits((int)decimals);
                nf.setMinimumIntegerDigits(1);
                toRet =  nf.format(d);
            }
        }
        return toRet;
    }
    
    public boolean hasSpecialClauses()
    {
        return true;
    }
    
    public String getSqlGroupcolumn(int groupPos)
    {
        return "g"+groupPos+"."+brAttDBName+" \"grp"+groupPos+"\"";
    }
    
    public String getFromGroupcolumn(int groupPos)
    {
        return tablename + " "+" g"+groupPos;
    }
    
    public String getWhereGroupcolumn(int groupPos)
    {       
        return " g" + groupPos +".PARENT$(+)=OEEbo_Document.BOUI AND g" + groupPos +".\"CHILD$\" = " + clfObjBoui;
    }
    
    public String getWhereGroupGridcolumn(int groupPos, String[] qryG, boolean setValue, Vector p_parameters)
    {
        if(setValue)
        {
            return " g" + groupPos +".PARENT$(+)=OEEbo_Document.BOUI AND g" + groupPos +".\"CHILD$\" = " + clfObjBoui + " AND g" + groupPos +".\""+brAttDBName.toUpperCase()+"\" = " + qryG[groupPos];
        }
        else
        {
            p_parameters.add(new Long(clfObjBoui));
            p_parameters.add(new Long(qryG[groupPos]));
            return " g" + groupPos +".PARENT$(+)=OEEbo_Document.BOUI AND g" + groupPos +".\"CHILD$\" = ? AND g" + groupPos +".\""+brAttDBName.toUpperCase()+"\" = ?";
        }
    }
    
    public String getBridegAttDBName()
    {
        return brAttDBName;
    }
    
    public String getBridegAttName()
    {
        return brAtt;
    }
    
    public String getColumnChooseLabel()
    {
        return (segmento == null ? getLabel():(segmento + " -> " + getLabel()));
    }
}