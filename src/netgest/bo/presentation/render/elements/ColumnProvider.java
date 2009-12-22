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
public abstract class ColumnProvider 
{
    ExplorerAttribute explorerAtt;
    
   
    public int getWidth()
        {
            return explorerAtt.getWidth();
        }
        
        public String showLink()
        {
            return explorerAtt.showLink();
        }
        
        public String openDocument()
        {
            return explorerAtt.openDocument();
        }

        public void setWidth(int width)
        {
            explorerAtt.setWidth(width);
        }

        public boolean hasResults()
        {
            return explorerAtt.hasResults();
        }
        public byte getType()
        {
            return explorerAtt.getType();
        }
        
        public String getLabel()
        {
            return explorerAtt.getLabel();
        }
        
        public String getColumnChooseLabel()
        {
            return getLabel();
        }
       
        public String getName()
        {
            return explorerAtt.getName();
        }
        
        public String getValueResult(long boui, String value , EboContext boctx) throws boRuntimeException
        {
            return explorerAtt.getValueResult(value, boctx);
        }
        
        public String getValueResult( String value , EboContext boctx) throws boRuntimeException
        {
            return explorerAtt.getValueResult(value, boctx);
        }
        
        public String getValueResult(long boui, String value , EboContext boctx, boolean showLink) throws boRuntimeException
        {
           
           return explorerAtt.getValueResult(value, boctx, showLink);
        }
        
        public String getValueResult( String value , EboContext boctx, boolean showLink) throws boRuntimeException
        {
           
           return explorerAtt.getValueResult(value, boctx, showLink);
        }
        
        public String getColorResult( String value )
        {
           return explorerAtt.getColorResult(value);
        }
        
        public boDefAttribute getDefAttribute()
        {
            return explorerAtt.p_defatr;
        }
        
        public boolean isAttribute()
        {
            return explorerAtt.p_isAttribute;
        }
        
        public String getSQL()
        {
            return explorerAtt.p_sql;
        }
        
        public String getSQLGroup()
        {
            return explorerAtt.p_sql;
        }
        
        public boolean isExternalAttribute()
        {
            return explorerAtt.p_isAttributeExternal;
        }
        
        public void setCount_Br(int value)
        {
            explorerAtt.p_countBr = value;
        }
        
        public void setBridgeInd(int value)
        {
            explorerAtt.p_bridgeInd = value;
        }
        
        public int count_Br()
        {
            return explorerAtt.p_countBr;
        }
        
        public int bridgeInd()
        {
            return explorerAtt.p_bridgeInd;
        }
        
        public boolean hasSpecialClauses()
        {
            return false;
        }

        public String getSpecialWhereClause(EboContext boctx)
        {
            return "";
        }
        
        public String getSqlGroupcolumn(int groupPos)
        {
            return "";
        }
        
        public String getFromGroupcolumn(int groupPos)
        {
            return "";
        }
        public String getWhereGroupcolumn(int groupPos)
        {
            return "";
        }
        
         public String getWhereGroupGridcolumn(int groupPos, String[] qryG, boolean setValue, Vector p_parameters)
        {
            return "";
        }
}