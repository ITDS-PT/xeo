/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import bsh.BshClassManager;
import bsh.NameSpace;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefObjectFilter;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.parser.CodeJavaConstructor;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boConvertUtils;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.security.securityOPL;
import netgest.bo.security.securityRights;
//import netgest.bo.system.*; att:JP



public final class docHTML_section  {
    private Vector p_rows;
    private String p_title;
    private String p_template;
    public boolean p_showLabel;
    private int p_space_before=10;
    public String p_height;
    public String p_width;
    public String p_styleSection;
    public Hashtable p_values;
    public String p_name=null;
    public String p_id=null;
    private String p_format="120,70";
    
    public docHTML_section(String template, Hashtable p_values) {
        p_rows=new Vector();
        p_template=template;
        p_showLabel=true;
        this.p_values = p_values; 
        
        
    }
    public docHTML_section(String template, boolean showLabel , int space_before , Hashtable p_values ) {
        p_rows=new Vector();
        p_template=template;
        p_showLabel=showLabel;
        p_space_before=space_before;
        this.p_values = p_values;
    }

    public docHTML_section( SectionProperties secprop ) {
        p_rows=new Vector();
        p_template      =secprop.p_template;
        p_showLabel     =secprop.p_showLabel;
        p_space_before  =secprop.p_space_before;
        p_styleSection  =secprop.p_styleSection;
        
    }
    public void setTitle(String xtitle){
        p_title=xtitle;

    }
    
    public void setShowTile( boolean showtitle )
    {
        p_showLabel = showtitle;
    }

    public void setFormat(String format)
    {
        p_format=format;

    }
   public void setTitle(){
        p_title="";
   }

     public docHTML_sectionRow addRow() {
       docHTML_sectionRow r;
       r=new docHTML_sectionRow();
       p_rows.add(r) ;
       return r;
    }

public static final class SectionProperties
    {
        public String  title="";
        public String p_template="";
        public boolean p_showLabel=true;
        public int p_space_before=10;
        public String p_styleSection=null;
    }

 public void render(PageContext page,boObjectList bolist,docHTML_controler DOCLIST,docHTML doc) throws boRuntimeException,java.io.IOException {
        JspWriter out=page.getOut();
        StringBuffer toPrint=new StringBuffer();

        
        
        toPrint.append("<TABLE class='section' ");
        if( p_name != null && !"".equals(p_name) )
        {
            toPrint.append(" name='"+p_name+"'");
        }
        if(p_id != null && !"".equals(p_name))
        {
            toPrint.append(" id='"+p_id+"'");
        }
        toPrint.append(" style=\"");
        if( p_height !=null) {
            toPrint.append("height:");
            toPrint.append(p_height);
            toPrint.append(';');
        }
        if( p_width !=null) {
            toPrint.append("width:");
            toPrint.append(p_width);
            toPrint.append(';');
        }
        
        if (p_styleSection != null) toPrint.append( p_styleSection );
        
        toPrint.append("\" cellSpacing='0' cellPadding='3'  >");
        toPrint.append("<COLGROUP/>");
        
        String[] formats= p_format.split(",");
        
        toPrint.append("<COL width='").append(formats[0]).append("' />");
        toPrint.append("<COL />");
        toPrint.append("<COL style=\"PADDING-LEFT: 5px\" width='").append(formats[1]).append("' />");
        toPrint.append("<COL />");
        
        toPrint.append("<TBODY>");
        int p_space = p_space_before;
        if( !p_showLabel ) p_space=0;
        
        toPrint.append("<TR height='"+ p_space +"'><TD colspan='4'></TD></TR>");
        if(p_showLabel){
            toPrint.append("<TR><TD class='sec bar' colSpan='4'>");
            toPrint.append(p_title);
            toPrint.append("</TD></TR>");
            toPrint.append("<TR height='5'><TD></TD></TR>");
        }

        int nrRows=p_rows.size();
        docHTML_sectionRow r;
        docHTML_sectionCell c1;
        docHTML_sectionCell c2;

        String v;
        String keyShow;
        String reserveLabelSpace;
        boolean showLabel;
        
        
        boObject obj=bolist.getObject();
        AttributeHandler attr;
        StringBuffer nameH=new StringBuffer();
        toSubmit(p_values, obj, DOCLIST, doc);         
        
        int fieldNumber;
        boolean designLine;
        
        for (int i = 0; i < nrRows; i++)  {
            designLine = true;
            r=(docHTML_sectionRow)p_rows.get(i);

            c1=(docHTML_sectionCell)r.cells.get(0);
            if(r.cells.size()>1) c2=(docHTML_sectionCell)r.cells.get(1);
            else c2=null;
            
            if(c1 != null || c2 != null){
                //caso em que a primeira coluna tem um atributo
                if(c1 != null && c1.p_atr != null)
                {
                    //caso em que a primeira coluna está escondida
                    if(!obj.getAttribute( c1.p_atr.getName() ).isVisible())
                    {
                        //caso em que a segunda coluna tem um atributo ou coluna vazia
                        if(c2 != null)
                        {
                            //caso em que a segunda coluna tem um atributo
                            if(c2.p_atr != null)
                            {
                                //caso em que a 2ª coluna encontra-se escondida
                                if(!obj.getAttribute( c1.p_atr.getName() ).isVisible())
                                {
                                    //não vou desenhar a linha
                                    designLine = false;
                                }
                                //caso em que a 2ª coluna não se encontra escondida
                                else
                                {
                                    //vou desenhar o atributo da 2ª coluna na 1ª coluna 
                                    //e desenha a 2ª coluna vazia
                                    c1 = c2;
                                    c2.p_atr = null;
                                }
                            }
                            //caso em que a segunda coluna está vazia
                            else
                            {
                                //não vou desenhar a linha
                                designLine = false;
                            }
                        }
                        else
                        {
                            //não vou desenhar a linha
                            designLine = false;
                        }
                    }
                    else
                    {
                        //caso em que a segunda coluna tem um atributo ou coluna vazia
                        if(c2 != null)
                        {
                            //caso em que a segunda coluna tem um atributo
                            if(c2.p_atr != null)
                            {
                                //caso em que a 2ª coluna encontra-se escondida
                                if(!obj.getAttribute( c1.p_atr.getName() ).isVisible())
                                {
                                    //não vou desenhar o atributo da 2ª coluna
                                    c2.p_atr = null;
                                }
                            }
                        }
                    }
                }
                else
                {
                    //a primeira coluna está vazia
                    //caso em que a segunda coluna tem um atributo ou coluna vazia
                    if(c2 != null)
                    {
                        //caso em que a segunda coluna tem um atributo
                        if(c2.p_atr != null)
                        {
                            //caso em que a 2ª coluna encontra-se escondida
                            
                            
                                if(!obj.getAttribute( c2.p_atr.getName() ).isVisible())
                                {
                                    //não vou desenhar a linha
                                    designLine = false;
                                }
                            
                        }
                    }
                }
            
                if(designLine)
                {
                    toPrint.append("<tr>");
                
                    if(c1!=null){
                        if(c1.p_atr==null){
                            toPrint.append( Sectxt.text[Sectxt.EMPTY_TD]);
                            toPrint.append( Sectxt.text[Sectxt.EMPTY_TD]);
                        }
                        else{
                            fieldNumber=DOCLIST.countFields++;        
                            attr=obj.getAttribute( c1.p_atr.getName() );
                            nameH.setLength(0);
                            nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( attr.getName() );
                            keyShow = (String) c1.p_attributes.get("showlabel");
                            reserveLabelSpace = (String) c1.p_attributes.get("reserveLabelSpace");
                            if ( reserveLabelSpace==null ) reserveLabelSpace="no";
                            
                            showLabel=true;
                            
                            if ( keyShow!=null && keyShow.equalsIgnoreCase("no")) {
                                showLabel=false;
                            }
                            String xh=(String) c1.p_attributes.get("height");
                            boolean renderAsCheck = false;
                            if(  attr.getDefAttribute().getValueType()== boDefAttribute.VALUE_BOOLEAN 
                             && attr.getDefAttribute().renderAsCheckBox() )
                             {
                                 renderAsCheck=true;
                             }
                            
                            
                            if ( ( showLabel && !renderAsCheck ) || reserveLabelSpace.equalsIgnoreCase("yes") )
                            {
                                
                               
                                if ( xh == null )
                                {
                                    toPrint.append( Sectxt.text[Sectxt.OPEN_TD] );    
                                }
                                else
                                {
                                    toPrint.append( "<TD height=" );
                                    toPrint.append( xh );
                                    toPrint.append(">");
                                }
                                
                                
                               if ( showLabel )
                               {
                                   toPrint.append("<label ");
                                   if ( !attr.isVisible() ){
                                        toPrint.append(" style='display:none' ");                                
                                   }
                                   if ( attr.disableWhen() ){
                                        toPrint.append(" disabled ");                   
                                   }
                                   boolean req = attr.required();
                                   if ( req )
                                   {
                                        toPrint.append(" class=req ");  
                                   }
                                   else if (  attr.getRecommend() )
                                   {
                                        toPrint.append(" class=rcm ");  
                                   }
                                   
                                   toPrint.append(" for='");
                                   toPrint.append(nameH);
//                                   toPrint.append( fieldNumber );
                                   toPrint.append("'>");
                                   
                                   toPrint.append( c1.p_atr.getLabel());
                                   
                                   toPrint.append("</label>");
                               }
                               
                               toPrint.append( Sectxt.text[Sectxt.CLOSE_TD]);
                                
                               if (c2==null)toPrint.append("<td colspan='3'>");
                               else toPrint.append("<td>");
                            
                            }
                            else{
                                if (c2==null){
                                    
                                    if ( xh == null )
                                    {
                                      toPrint.append("<td colspan='4'>");    
                                    }
                                    else
                                    {
                                        toPrint.append( "<TD colspan=4 height=" );
                                        toPrint.append( xh );
                                        toPrint.append(">");
                                    }
                                    
                                }
                                else{
                                
                                    if ( xh == null )
                                    {
                                      toPrint.append("<td colspan='2'>");    
                                    }
                                    else
                                    {
                                        toPrint.append( "<TD colspan=2 height=" );
                                        toPrint.append( xh );
                                        toPrint.append(">");
                                    }
                                    
                                }
                            }
                            
                            if( c1.p_customRender != null )
                            {
                                renderCustomTag( toPrint ,fieldNumber , attr , obj , DOCLIST, doc , c1.p_customRender , c1.p_attributes, p_values  );   
                            }
                            else if ( c1.p_text == null )
                            {
                                renderHTMLObject( toPrint ,fieldNumber , attr , obj , DOCLIST, doc , c1.p_attributes, p_values);
                            }
                            else
                            {
//                                toPrint.append("<table id=\"messageZone\" class=\"messageZone\"><tr><td>");
                                renderHTMLtextObject( toPrint ,fieldNumber , attr , obj , DOCLIST, doc , c1.p_text , c1.p_attributes, p_values);
//                                toPrint.append("</td></tr></table>");
                            }
                               
                            toPrint.append("</td>");
                            
                        }
                        
                    }
        
                    if(c2!=null){
                    
                        if(c2.p_atr==null)  toPrint.append("<td colspan='2'>&nbsp;</td>");                        
                        else{
                           fieldNumber=DOCLIST.countFields++;
                           keyShow = (String) c2.p_attributes.get("showlabel");
                           showLabel=true;
                           reserveLabelSpace = (String) c2.p_attributes.get("reserveLabelSpace");
                           if ( reserveLabelSpace==null ) reserveLabelSpace="no";
                            
                           if ( keyShow!=null && keyShow.equalsIgnoreCase("no") ) showLabel=false;
                           String xh=(String) c2.p_attributes.get("height");
                           attr=obj.getAttribute( c2.p_atr.getName() );
                           
                           boolean renderAsCheck = false;
                           if(  attr.getDefAttribute().getValueType()== boDefAttribute.VALUE_BOOLEAN 
                             && attr.getDefAttribute().renderAsCheckBox() )
                             {
                                 renderAsCheck=true;
                             }
                                                        
                           nameH.setLength(0);
                           nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( attr.getName() );
                                                 
                           if ( ( showLabel && !renderAsCheck ) || reserveLabelSpace.equalsIgnoreCase("yes") )
                           {
//                           if ( showLabel || reserveLabelSpace.equalsIgnoreCase("yes") ) {                       
                               toPrint.append( Sectxt.text[Sectxt.OPEN_TD_ALIGNRIGHT] );
                               
                                
                               
                               if ( showLabel )
                               {
                                   toPrint.append("<label ");
                                   if ( !attr.isVisible() ){
                                        toPrint.append(" style='display:none' ");                                
                                   }
                                   if ( attr.disableWhen() ){
                                        toPrint.append(" disabled ");                            
                                   }
                                   boolean req = attr.required();
                                   if ( req )
                                   {
                                        toPrint.append(" class=req ");  
                                   }
                                   else if (  attr.getRecommend() )
                                   {
                                        toPrint.append(" class=rcm ");  
                                   }
                                   
                                   toPrint.append(" for='");
                                   toPrint.append(nameH);
//                                   toPrint.append( fieldNumber );
                                   toPrint.append("'>");
                                   
                                   toPrint.append( c2.p_atr.getLabel());
                                   
                                   toPrint.append("</label>");
                               }
                               
                               toPrint.append( Sectxt.text[Sectxt.CLOSE_TD]);
                               toPrint.append( Sectxt.text[Sectxt.OPEN_TD] );
                           }
                           else
                           {
                                if (c2==null){
                                    
                                    if ( xh == null )
                                    {
                                      toPrint.append("<td colspan='4'>");    
                                    }
                                    else
                                    {
                                        toPrint.append( "<TD colspan=4 height=" );
                                        toPrint.append( xh );
                                        toPrint.append(">");
                                    }
                                    
                                }
                                else{
                                
                                    if ( xh == null )
                                    {
                                      toPrint.append("<td colspan='2'>");    
                                    }
                                    else
                                    {
                                        toPrint.append( "<TD colspan=2 height=" );
                                        toPrint.append( xh );
                                        toPrint.append(">");
                                    }
                                    
                                }
                            }                           
//                           else {
//                                toPrint.append("<td colspan='2'>");                     
//                           }
                           
                           
                            if( c2.p_customRender != null )
                            {
                                renderCustomTag( toPrint ,fieldNumber , attr , obj , DOCLIST, doc , c2.p_customRender , c2.p_attributes, p_values  );   
                            }
                            else if ( c2.p_text == null )
                            {
                                renderHTMLObject(toPrint, fieldNumber , attr , obj , DOCLIST , doc , c2.p_attributes, p_values);
                            }
                            else
                            {
                                renderHTMLtextObject( toPrint ,fieldNumber , attr , obj , DOCLIST, doc , c2.p_text , c1.p_attributes, p_values );
                            }
                         
                           toPrint.append( Sectxt.text[Sectxt.CLOSE_TD]);
                            
                        }
                        
                    }
                }
                toPrint.append("</tr>");
            }
         
         
         }  //END FOR nrROWS
             
             
        
        toPrint.append("</TBODY>");
        toPrint.append("</TABLE>");

        out.print(toPrint);
        
    }

/**
   É executado quando a seccão não está binding a um objecto
     *   
     * @param page
     * @param doc
     * @param DOCLIST
     * @throws boRuntimeException
     * @throws IOException
     */
    public void render(PageContext page , docHTML doc , docHTML_controler DOCLIST) throws boRuntimeException,java.io.IOException {
    
        JspWriter out=page.getOut();
        StringBuffer toPrint=new StringBuffer();
//        toPrint.append("<TABLE style=\"TABLE-LAYOUT: fixed\" cellSpacing='0' cellPadding='3'  width='100%'>");
        toPrint.append("<TABLE class='section' style=\"");
        if( p_height !=null) {
            toPrint.append("height:");
            toPrint.append(p_height);
            toPrint.append(';');
        }
        if( p_width !=null) {
            toPrint.append("width:");
            toPrint.append(p_width);
            toPrint.append(';');
        }
        if (p_styleSection != null) toPrint.append( p_styleSection );
        toPrint.append("\" cellSpacing='0' cellPadding='3'  >");

        
        toPrint.append("<COLGROUP/>");
        toPrint.append("<COL width='120' />");
        toPrint.append("<COL />");
        toPrint.append("<COL style=\"PADDING-LEFT: 20px\" width='135' />");
        toPrint.append("<COL />");
        toPrint.append("<TBODY>");
        toPrint.append("<TR height='"+ p_space_before +"'><TD colspan='4'></TD></TR>");
        if(p_showLabel){
            toPrint.append("<TR><TD class='sec bar' colSpan='4'>");
            toPrint.append(p_title);
            toPrint.append("</TD></TR>");
            toPrint.append("<TR height='5'><TD></TD></TR>");
        }

        int nrRows=p_rows.size();
        docHTML_sectionRow r;
        docHTML_sectionCell c1;
        docHTML_sectionCell c2;

        String v;
        
        
             docHTML_sectionField xfield;
             for (int i = 0; i < nrRows; i++)  {
                r=(docHTML_sectionRow)p_rows.get(i);
                c1=(docHTML_sectionCell)r.cells.get(0);
                if(r.cells.size()>1) c2=(docHTML_sectionCell)r.cells.get(1);
                else c2=null;
                if( c1 != null || c2 != null){
                    toPrint.append("<tr>");
                    if(c1!=null){
                        if(c1.p_field==null && !c1.p_multiFields ){
                            toPrint.append( Sectxt.text[Sectxt.EMPTY_TD]);
                        }
                        else{
                            if ( !c1.p_multiFields ) 
                            {
                                toPrint.append( Sectxt.text[Sectxt.OPEN_TD] );
                                xfield=c1.p_field;    
                                if(xfield.p_label!=null) {
                                      toPrint.append("<label ");
                                      toPrint.append(" for='");
                                      toPrint.append( xfield.p_id );
                                      toPrint.append("'>");
                                      toPrint.append( xfield.p_label);
                                      toPrint.append("</label>"); 
                                }
                                
                                toPrint.append( Sectxt.text[Sectxt.CLOSE_TD]);
            
                                if (c2==null)toPrint.append("<td colspan='3'>");
                                else toPrint.append("<td>");
                                
                                renderHTMLObject( toPrint, xfield ,doc , DOCLIST);
                                toPrint.append("</td>");
                            }
                            else
                            {
                                xfield=(docHTML_sectionField) c1.p_fields.get(0);
                                if ( xfield.p_label==null )
                                {
                                    String xextras=(String) c1.p_attributes.get("attr_td");
                                    if( xextras == null){
                                        if (c2==null)toPrint.append("<td colspan='4'>");
                                        else toPrint.append("<td>");
                                    }
                                    else
                                    {
                                        if (c2==null)toPrint.append("<td colspan='4' "+xextras+" >");
                                        else toPrint.append("<td "+xextras+" >");
                                    }
                                }
                                else 
                                {
                                      toPrint.append( Sectxt.text[Sectxt.OPEN_TD] );         
                                      
                                      
                                      toPrint.append("<label ");
                                      toPrint.append(" for='");
                                      toPrint.append( xfield.p_id );
                                      toPrint.append("'>");
                                      toPrint.append( xfield.p_label);
                                      toPrint.append("</label>");
                                      
                                      toPrint.append( Sectxt.text[Sectxt.CLOSE_TD] );
                                      
                                      if (c2==null)toPrint.append("<td colspan='3'>");
                                      else toPrint.append("<td>");
                                }
                                
                                    
                                
                                ///toPrint.append("<TABLE style='table-layout:fixed' cellpadding=0 cellspacing=0><TR>");
                                //toPrint.append("<COLGROUP />");
                                toPrint.append("<TABLE cellpadding=0 cellspacing=0><TR>");
                                String xsize;
                                /*
                                for ( int z = 0 ; z < c1.p_fields.size() ; z++ )
                                {
                                    toPrint.append("<COL ");
                                    if ( z > 0) toPrint.append(" style='padding-left:4px' ");
                                    xsize=(String) c1.p_fieldsSize.get(z);
                                    if ( xsize != null && !xsize.equalsIgnoreCase("") )
                                    {
                                        toPrint.append("width=");
                                        toPrint.append( xsize );
                                    }
                                    toPrint.append("/>");
                                }
                                */
                                for ( int z = 0 ; z < c1.p_fields.size() ; z++ ) 
                                {
                                    
                                    xfield=(docHTML_sectionField) c1.p_fields.get(z);
                                    xsize=(String) c1.p_fieldsSize.get(z);
                                    if ( xsize != null && !xsize.equalsIgnoreCase("") )
                                    {
                                        toPrint.append("<TD style='width=");
                                        toPrint.append(xsize);
                                        if ( z > 0 ) toPrint.append(";padding-left:4px");
                                        toPrint.append("'>");
                                    }
                                    else {
                                        if( z >0 ) 
                                        {
                                        toPrint.append("<TD style='padding-left:4px'>");    
                                        }
                                        else{
                                            toPrint.append("<TD>");
                                        }
                                    }
                                    
                                    if(xfield.p_label!=null && z > 0 ) {
                                          toPrint.append("<label ");
                                          toPrint.append(" for='");
                                          toPrint.append( xfield.p_id );
                                          toPrint.append("'>");
                                          toPrint.append( xfield.p_label);
                                          toPrint.append("</label>"); 
                                    }
    
                                    renderHTMLObject( toPrint, xfield ,doc , DOCLIST);
                                    toPrint.append("</TD>");
                                }
                                toPrint.append("</TR></TABLE>");
                                
                                toPrint.append("</td>");
                            }
                               
                            
                        }
                        
                    }
    
                    if(c2!=null){
                        if(c2.p_field==null && !c2.p_multiFields){
                            toPrint.append("<td>&nbsp;</td>");                        
                        }
                        else{
                            //atr.getName().
                            if ( !c2.p_multiFields ) 
                            {
                                   toPrint.append("<td>");
                                   if(c2.p_field.p_label!=null) {
                                   
                                   
                                      toPrint.append("<label ");
                                      toPrint.append(" for='");
                                      toPrint.append( c2.p_field.p_id );
                                      toPrint.append("'>");
                                      toPrint.append( c2.p_field.p_label);
                                      toPrint.append("</label>");
                                      
                                   }
                                   toPrint.append("</td>");
            
                                   
                                   toPrint.append("<td>");
                                   renderHTMLObject( toPrint , c2.p_field , doc , DOCLIST );
                                   toPrint.append("</td>");
                                   
                            }
                            else
                            {
                                toPrint.append( Sectxt.text[Sectxt.OPEN_TD] );
                                
                                toPrint.append("<TABLE cellpadding=0 cellspacing=0><TR>");
                                //toPrint.append("<COLGROUP />");
                                String xsize;
                           /*     for ( int z = 0 ; z < c2.p_fields.size() ; z++ )
                                {
                                    toPrint.append("<COL ");
                                    xsize=(String) c2.p_fieldsSize.get(z);
                                    if ( xsize != null && !xsize.equalsIgnoreCase("") )
                                    {
                                        toPrint.append("width=");
                                        toPrint.append( xsize );
                                    }
                                    toPrint.append("/>");
                                }*/
                                
                                for ( int z = 0 ; z < c2.p_fields.size() ; z++ ) 
                                {
                                    xsize=(String) c2.p_fieldsSize.get(z); 
                                    xfield=(docHTML_sectionField) c2.p_fields.get(z);
                                    if ( xsize != null && !xsize.equalsIgnoreCase("") )
                                    {
                                        toPrint.append("<TD style='width=");
                                        toPrint.append(xsize);
                                        if ( z > 0 ) toPrint.append(";padding-left:4px");
                                        toPrint.append("'>");
                                    }
                                    else {
                                        if( z >0 ) 
                                        {
                                        toPrint.append("<TD style='padding-left:4px'>");    
                                        }
                                        else{
                                            toPrint.append("<TD>");
                                        }
                                    }
                                    if(xfield.p_label!=null ) {
                                          toPrint.append("<label ");
                                          toPrint.append(" for='");
                                          toPrint.append( xfield.p_id );
                                          toPrint.append("'>");
                                          toPrint.append( xfield.p_label);
                                          toPrint.append("</label>"); 
                                    }
    
                                    renderHTMLObject( toPrint, xfield ,doc , DOCLIST);
                                    toPrint.append("</TD>");
                                }
                                toPrint.append("</TR></TABLE>");
                                
                                toPrint.append( Sectxt.text[Sectxt.CLOSE_TD] );
                            }
                               
                            
                        
                       
                            
                        }
                        
                      }
                      toPrint.append("</tr>");
                  }
             }
             
             
            toPrint.append("</TBODY>");
        toPrint.append("</TABLE>");
   

        out.print(toPrint);
        
    }

    public static void renderHTMLObject(StringBuffer toPrint , docHTML_sectionField field ,docHTML doc , docHTML_controler DOCLIST) 
    throws boRuntimeException{

           
           boolean cont=true;
           int tabNumber = doc.getTabindex(doc.FIELD, field.p_id.toString(), DOCLIST);
        
           if ( field.p_htmlRenderType == docHTML_sectionField.HTML_SELECT ){
                        
                    cont=false;
                    docHTML_renderFields.writeHTML_forCombo(
                        toPrint,
                        field.p_value ,
                        field.p_name ,
                        field.p_id ,
                        tabNumber,
                        field.p_selectDisplayValues ,
                        field.p_selectInternalValues ,
                        field.p_selectAllowValueEdit,
                        field.p_disabled,field.p_visible,false,field.p_JSonChange,
                        field.p_required,
                        field.p_recommend,
                        null
                        );
            
                           
           }
           
           if(cont){
               if(field.p_htmlRenderType == docHTML_sectionField.HTML_DATE ){
                      docHTML_renderFields.writeHTML_forDate(
                        toPrint,
                        field.p_value ,
                        field.p_name ,
                        field.p_id ,
                        tabNumber,
                        field.p_disabled,field.p_visible,false,field.p_JSonChange,
                        field.p_required,
                        field.p_recommend,
                        null
                        );
               }
               else if(field.p_htmlRenderType == docHTML_sectionField.HTML_DATETIME ){
                      docHTML_renderFields.writeHTML_forDateTime(
                        toPrint,
                        field.p_value,
                        field.p_name,
                        field.p_id,
                        tabNumber,
                        true,
                        field.p_disabled,field.p_visible,false,field.p_JSonChange,
                        field.p_required,
                        field.p_recommend,
                        null
                        );
                        
               }
               else if(field.p_htmlRenderType == docHTML_sectionField.HTML_BOOLEAN ){
                      docHTML_renderFields.writeHTML_forBoolean(
                        toPrint,
                        field.p_value ,
                        field.p_name ,
                        field.p_id ,
                        tabNumber,
                        field.p_disabled,field.p_visible,false,field.p_JSonChange,
                        field.p_required,
                        field.p_recommend,
                        null
                        );
               }
               else if(field.p_htmlRenderType == docHTML_sectionField.HTML_NUMBER ){
                
                docHTML_renderFields.writeHTML_forHTMLEDITOR(
                            toPrint,
                            null,
                            null,
                            null,
                            field.p_value,
                            field.p_name,
                            field.p_id,
                            tabNumber,
                            field.p_disabled,field.p_visible,false,field.p_JSonChange,
                            field.p_required,
                            field.p_recommend,
                            null
                            ,"html");
                            
               }
               else if(field.p_htmlRenderType == docHTML_sectionField.HTML_NUMBER ){
                 boolean grouping = field.p_grouping;
                 int decimals = field.p_decimals;
                 int minDecimals = field.p_minDecimals;
                 String min=field.p_min;
                 String max=field.p_max;                     
                         
                 docHTML_renderFields.writeHTML_forNumber(
                    toPrint,
                    field.p_value,
                    field.p_name,
                    field.p_id,
                    tabNumber,
                    new StringBuffer(""),
                    new StringBuffer(String.valueOf(decimals)),
                    new StringBuffer(String.valueOf(minDecimals)),
                    grouping,
                    new StringBuffer(max),
                    new StringBuffer(min),
                    field.p_disabled,field.p_visible,false,field.p_JSonChange,
                    field.p_required,
                    field.p_recommend,
                    null
                    );

            
               }
               else if(field.p_htmlRenderType == docHTML_sectionField.HTML_BUTTON ){

                   toPrint.append("<button ");
                   toPrint.append("id='");
                   toPrint.append( field.p_id );
                   toPrint.append("'");
                   if(field.p_JSonClick.length() >0 ) {
                        toPrint.append("onclick='");
                        toPrint.append( field.p_JSonClick );
                        toPrint.append('\'');
                   }
                   toPrint.append(" tabindex='"+DOCLIST.tabindex+++"'> ");
                   //toPrint.append('>');
                    
                   toPrint.append( field.p_value);
                   toPrint.append("</button>");

            
               }
               else if(field.p_htmlRenderType == docHTML_sectionField.HTML_FILE ){

                   toPrint.append("<input type='file' class='text' value='");
                   toPrint.append( field.p_value );
                   toPrint.append("' id='");
                   toPrint.append( field.p_id );
                   toPrint.append('\'');
                                      
                   if (  field.p_JSonChange!=null &&  field.p_JSonChange.length()>0 ){
                       toPrint.append(" onchange='");
                       toPrint.append(field.p_JSonChange);
                       toPrint.append('\'');
                   }
                   toPrint.append(" name = '");
                   toPrint.append( field.p_name );
                   toPrint.append("' tabindex='"+DOCLIST.tabindex+++"'>");
               }
               else{

                   
                   toPrint.append("<input class='text' value='");
                   toPrint.append( field.p_value );
                   toPrint.append("' id='");
                   toPrint.append( field.p_id );
                   toPrint.append('\'');
                                      
                   if (  field.p_JSonChange!=null &&  field.p_JSonChange.length()>0 ){
                       toPrint.append(" onchange='");
                       toPrint.append(field.p_JSonChange);
                       toPrint.append('\'');
                   }
                   if (  field.p_JSonClick!=null &&  field.p_JSonClick.length()>0 ){
                       toPrint.append(" onKeyDown='");
                       toPrint.append(field.p_JSonClick);
                       toPrint.append('\'');
                   }
                   toPrint.append(" name = '");
                   toPrint.append( field.p_name );
                   toPrint.append("' tabindex='"+DOCLIST.tabindex+++"'>");
                   

               }
           }


        
    }

    public static void renderHTMLObject(StringBuffer toPrint,  int fieldNumber ,AttributeHandler attr, boObject obj,docHTML_controler DOCLIST,docHTML doc, Hashtable xattributes, Hashtable p_values)throws boRuntimeException  
    {
        renderHTMLObject(toPrint,  fieldNumber ,attr, obj, DOCLIST,doc, xattributes, p_values, false, -1);
    }

    public static void renderHTMLObject(StringBuffer toPrint,  int fieldNumber ,AttributeHandler attr, boObject obj,docHTML_controler DOCLIST,docHTML doc, Hashtable xattributes, Hashtable p_values, 
        boolean submit, int tabIndex) throws boRuntimeException  {

           StringBuffer v = new StringBuffer( attr.getValueString() == null ? "":attr.getValueString() );  
           
            //obj.getMode() == boObject.MODE_EDIT_TEMPLATE
            
           boDefAttribute attrDef   =   attr.getDefAttribute();
           String attrType          =   attrDef.getType().toUpperCase();
           byte attrTypeObj         =   attrDef.getAtributeType();
           StringBuffer nameH       =   new StringBuffer();
           StringBuffer id          =   new StringBuffer();
           StringBuffer onChange    =   new StringBuffer();
           StringBuffer onFocus    =   new StringBuffer();
           boolean isDisabled       =   attr.isDisabled();                      
           boolean isVisible        =   attr.isVisible();
           boolean inTemplate       =   obj.getMode() == boObject.MODE_EDIT_TEMPLATE; 
           boolean isRequired       =   attr.required();
           boolean isRecommend      =   attr.getRecommend();
           
           
           String disable="";
           Boolean showLinkValue = null;
           String keyShowLink = null;
           if (xattributes!=null)
           {
            disable = (String)xattributes.get("disable");
            keyShowLink = (String) xattributes.get("showlink");
            if ( "no".equalsIgnoreCase(keyShowLink)) 
            {
               showLinkValue=Boolean.FALSE;
            }
            else if("yes".equalsIgnoreCase(keyShowLink))
            {
               showLinkValue=Boolean.TRUE;
            }
           }
           
           isDisabled = isDisabled ||  "YES".equalsIgnoreCase(disable) ? true:false;
           if(isDisabled)
           {//se estiver disabled para já vou retirar o link mas deverá abrir um preview
                if("ifenabled".equalsIgnoreCase(keyShowLink))
                {
                    showLinkValue = Boolean.FALSE;
                }
                else if("ifenabled".equalsIgnoreCase(keyShowLink))
                {
                    showLinkValue = Boolean.TRUE;
                }
           }
           else
           {
                if("ifenabled".equalsIgnoreCase(keyShowLink))
                {
                    showLinkValue = Boolean.TRUE;
                }
                else if("ifenabled".equalsIgnoreCase(keyShowLink))
                {
                    showLinkValue = Boolean.FALSE;
                }
           }
           
           boolean showLink = (showLinkValue == null || showLinkValue.booleanValue()) ? true:false;
           
           String xlov=attrDef.getLOVName();
           
           boolean xlovreq   = attr.required();
//           boolean xlovreq = (attrDef.getRequired().toUpperCase().equals("Y")||
//                                attrDef.getRequired().toUpperCase().equals("YES")) ? true:false;
           
           nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( attr.getName() );
           id.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( attr.getName() ).append(fieldNumber);
           
           if(obj.onChangeSubmit(attr.getName()) || submit)
           {
               onChange.append(" document.getElementById(\"refreshframe\").contentWindow.BindToValidate(\"" + nameH +"\"); ");
           }

           if( showLink ) {
	    	   boObject o = attr.getObject(); 
	           if( attr.getObject() != null ) {
	        	   if( !( securityRights.canRead(attr.getEboContext(),o.getName()) && securityOPL.canRead( o ) ) ) {
	        		   showLink = false;
	        	   }
	           }
	           else if ( !securityRights.canRead( attr.getEboContext(), attr.getDefAttribute().getReferencedObjectName() ) ) {
	        	   showLink = false;
	           }
          }
           
           boolean cont=true;
           int tabNumber = doc.getTabindex(doc.FIELD, obj.getName(), String.valueOf(obj.getBoui()), attr.getName(), DOCLIST);
           if ( attrTypeObj==boDefAttribute.TYPE_OBJECTATTRIBUTE){
                cont=false;
                id.setLength(0);                
                
                if ( attrDef.getRelationType() != boDefAttribute.RELATION_1_TO_1 && attrDef.getRuntimeMaxOccurs() > 1)
                {
                refreshValue(p_values, obj, nameH, v, attr.getName(), isDisabled, isRequired, !isVisible, fieldNumber, tabNumber, false);
                id.append("tblLook").append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( attr.getName() );
                
                docHTML_renderFields.writeHTML_lookupN(
                    toPrint,
                    obj,
                    obj.getBridge(attr.getName()),
                    attr,
                    v,
                    nameH,
                    id,
                    tabNumber,
                    doc,
                    isDisabled,
                    isVisible,
                    inTemplate,
                    isRequired,
                    isRecommend,
                    showLink,
                    xattributes
                    );    
                }
                else
                {
                    if(attr.getDefAttribute().renderAsLov())
                    {
                        String type =  attr.getDefAttribute().getType();
                        
                        String            renderAsLovQuery  = null;
                        
                        
                        //boDefObjectFilter renderAsLovFilter = attr.getDefAttribute().getObjectFilter( "" );
                        renderAsLovQuery = attr.getFilterBOQL_query( "" );
                        
                        if( renderAsLovQuery == null || renderAsLovQuery.trim().length() == 0 )
                        {
                            if(type.startsWith("object."))
                            {
                                type = type.substring(7);
                            }
                            renderAsLovQuery = "select " + type;                            
                        }
                        /*
                        if( renderAsLovFilter == null || renderAsLovFilter.getXeoQL() == null )
                        {
                            if(type.startsWith("object."))
                            {
                                type = type.substring(7);
                            }
                            renderAsLovQuery = "select " + type;                            
                        }
						else
						{
							renderAsLovQuery = renderAsLovFilter.getXeoQL();
						}
                        */
                        
                        boObjectList list = boObjectList.list( attr.getEboContext(), renderAsLovQuery );
                        
                        
                        boolean bCurrentValueExistsOnList = false;
                        
                        ArrayList oApresentationStr = new ArrayList();
                        ArrayList oValuesStr = new ArrayList();
                        
                        boObject auxObj;
                        int i = 0;
                        while(list.next())
                        {
                            auxObj = list.getObject();
                            oApresentationStr.add( auxObj.getCARDIDwNoIMG() );
                            oValuesStr.add( 
                                            new StringBuffer( String.valueOf( auxObj.getBoui() ) ) 
                                        );
                            
                            if ( attr.getValueLong() == auxObj.getBoui() )
                            {
                                bCurrentValueExistsOnList = true;
                            }
                            i++;
                        }
                        
                        // Se o valor actual não existir na lista de selecção e
                        // e o atributo estiver disabled, obriga o carregamento do valor actual
                        
                        if( attr.getValueLong() != 0 && isDisabled )
                        {
                            boObject oCurrentObject = attr.getObject();
                            if( oCurrentObject != null )
                            {
                                oApresentationStr.add( oCurrentObject.getCARDIDwNoIMG() );
                                oValuesStr.add( 
                                            new StringBuffer( String.valueOf( oCurrentObject.getBoui() ) )
                                        );
                            }
                        }
                        
                        
                        if( oValuesStr.size() == 0 )
                        {
                            oApresentationStr.add( new StringBuffer() );
                            oValuesStr.add( new StringBuffer() );
                        }
                        
                        StringBuffer[] apresentationStr = (StringBuffer[])oApresentationStr.toArray(
                                                    new StringBuffer[ oApresentationStr.size() ]
                                                );

                        StringBuffer[] valuesStr = (StringBuffer[])oValuesStr.toArray(
                                                    new StringBuffer[ oValuesStr.size() ]
                                                );
                        
                        id.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( attr.getName() ).append(fieldNumber);
                        refreshValue(p_values, obj, id, v, attr.getName(), isDisabled, isRequired, !isVisible, fieldNumber, tabNumber, false);
                        docHTML_renderFields.writeHTML_forCombo(
                            toPrint,
                            v ,
                            nameH ,
                            id,
                            tabNumber,
                            apresentationStr,
                            valuesStr,
                            false,
                            isDisabled,
                            isVisible,
                            inTemplate,
                            onChange,
                            isRequired,
                            isRecommend,
                            xattributes
                        );
                        
                    }
                    else
                    {
                        refreshValue(p_values, obj, nameH, v, attr.getName(), isDisabled, isRequired, !isVisible, fieldNumber, tabNumber, false);
                        id.append("tblLook").append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( attr.getName() );
                        docHTML_renderFields.writeHTML_lookup(
                            toPrint,
                            obj,
                            attr,
                            v,
                            nameH,
                            id,
                            tabNumber,
                            doc,
                            isDisabled,
                            isVisible,
                            inTemplate,
                            isRequired,
                            isRecommend,
                            showLink,
                            xattributes
                            );
                    }
                }
           }
           else{
            if("boolean".equalsIgnoreCase(attr.getDefAttribute().getType()))
            {
                refreshValue(p_values, obj, nameH, v, attr.getName(), isDisabled, isRequired, !isVisible, fieldNumber, tabNumber, !attrDef.renderAsCheckBox());
            }
            else if("date".equalsIgnoreCase(attr.getDefAttribute().getType()) || 
                "datetime".equalsIgnoreCase(attr.getDefAttribute().getType()))
            {
                StringBuffer vAux = new StringBuffer(boConvertUtils.convertToStringYFirst(attr.getValueDate(), attr));
                refreshValue(p_values, obj, id, vAux, attr.getName(), isDisabled, isRequired, !isVisible, fieldNumber, tabNumber, false);
            }
            else
            {
                refreshValue(p_values, obj, id, v, attr.getName(), isDisabled, isRequired, !isVisible, fieldNumber, tabNumber, false);
            }
            
            
           if ( !xlov.equals("") ){

                lovObject lov_object = LovManager.getLovObject( obj.getEboContext() , xlov , attr.condition() );
//                boObject lov;
//                lov = obj.getBoManager().loadObject(obj.getEboContext(),"Ebo_LOV","name='"+xlov+"'");
                
                if( lov_object!=null ){
                    //bridgeHandler lovdetails= lov.getBridge("details");
                    cont=false;
                    
                    docHTML_renderFields.writeHTML_forCombo(
                        toPrint,
                        v ,
                        nameH ,
                        id,
                        tabNumber,
                        lov_object ,
                        attr.canChangeLov(),
                        isDisabled,
                        isVisible,
                        inTemplate,
                        onChange,
                        isRequired,
                        isRecommend,
                        xattributes
                        );
                }
                           
           }
           }
           if(cont){
               if(attrType.equals("BOOLEAN")){    
               
                    if ( attrDef.renderAsCheckBox() )
                    {
                       docHTML_renderFields.writeHTML_forBooleanAsCheck(
                        attrDef.getLabel(),
                        toPrint,
                        v ,
                        nameH,
                        id,
                        tabNumber,
                        isDisabled,
                        isVisible,
                        inTemplate,
                        onChange,
                        isRequired,
                        isRecommend,
                        xattributes
                        );
                        
                    }
                    else
                    {
                      docHTML_renderFields.writeHTML_forBoolean(
                        toPrint,
                        v ,
                        nameH,
                        id,
                        tabNumber,
                        isDisabled,
                        isVisible,
                        inTemplate,
                        onChange,
                        isRequired,
                        isRecommend,
                        xattributes
                        );
                    }  
               }
               else if(attrType.equals("DATE")){
                      docHTML_renderFields.writeHTML_forDate(
                        toPrint,
                        v ,
                        nameH,
                        id,
                        tabNumber,
                        isDisabled,
                        isVisible,
                        inTemplate,
                        onChange,
                        isRequired,
                        isRecommend,
                        xattributes
                        );
               }

               else if(attrType.equals("DATETIME") ){
                      docHTML_renderFields.writeHTML_forDateTime(
                        toPrint,
                        v,
                        nameH,
                        id,
                        tabNumber,
                        true,
                        isDisabled,
                        isVisible,
                        inTemplate,
                        onChange,
                        isRequired,
                        isRecommend,
                        xattributes
                        );
               }
               else if(attrType.equals("DURATION") ){
                      docHTML_renderFields.writeHTML_forDuration(
                        toPrint,
                        v,
                        nameH,
                        id,
                        tabNumber,
                        true,
                        isDisabled,
                        isVisible,
                        inTemplate,
                        onChange,
                        isRequired,
                        isRecommend,
                        attrDef.getClock() && obj.getMode() != boObject.MODE_EDIT_TEMPLATE && xattributes.get("noClock")==null,
                        xattributes
                        );
               }
               else if(attrType.equals("CLOB") )
               {
                        String editor=attr.getDefAttribute().getEditorType();
                        id = new StringBuffer(
                            DOCLIST.getVuiClob(doc.getDocIdx(), 
                            obj.getBoui(), nameH.toString(), id.toString()));
                            if ( editor.equalsIgnoreCase("TEXT")|| editor.equalsIgnoreCase("CODE"))
                            {
                                 docHTML_renderFields.writeHTML_text(
                                        toPrint,
                                        v,
                                        nameH,id,tabNumber,
                                        isDisabled,
                                        isVisible,
                                        inTemplate,
                                        onChange,
                                        isRequired,
                                        isRecommend,
                                        -1,
                                        xattributes
                                );
                        
                            }
                            else
                            {
                                  docHTML_renderFields.writeHTML_forHTMLEDITOR(
                                    toPrint,
                                     obj,
                                    attr,
                                    doc,
                                    v,
                                    nameH,id,tabNumber,
                                    isDisabled,
                                    isVisible,
                                    inTemplate,
                                    onChange,
                                    isRequired,
                                    isRecommend,
                                    xattributes,editor
                                    );
                            }
               }
               else if (attrType.equals("IFILE")) 
               {
                    id.setLength(0);
                    id.append("tblLook").append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( attr.getName() );
                    docHTML_renderFields.writeHTML_iFile(
                        toPrint,
                        obj,
                        attr,
                        v,
                        nameH,
                        id,
                        tabNumber,
                        doc,
                        isDisabled,
                        isVisible,
                        inTemplate,
                        isRequired,
                        isRecommend,
                        xattributes
                    );
               }
               else if(attrType.startsWith("NUMBER") || attrType.startsWith("SEQUENCE")  ) 
               {
                 boolean beh=false;
                    if (  attrDef.getBeahvior_Img()!=null) 
                    {
                        beh=true;
                        toPrint.append("<table style='width:100%' cellpadding='0' cellspacing='0'><tr><td>");
                    }
                    String s = attrDef.getGrouping();
                    boolean grouping = "Y".equalsIgnoreCase(attrDef.getGrouping()) || "yes".equalsIgnoreCase(attrDef.getGrouping());
                    int decimals = attrDef.getDecimals();
                    int minDecimals = attrDef.getMinDecimals();
                    String min=attrDef.getMin();
                    String max=attrDef.getMax();
                    
                     docHTML_renderFields.writeHTML_forNumber(
                        toPrint,
                        v,
                        nameH,
                        id,
                        tabNumber,
                        new StringBuffer(""),
                        new StringBuffer(String.valueOf(decimals)),
                        new StringBuffer(String.valueOf(minDecimals)),
                        grouping,
                        new StringBuffer(max),
                        new StringBuffer(min),
                        isDisabled,
                        isVisible,
                        inTemplate,
                        onChange,
                        isRequired,
                        isRecommend,
                        xattributes
                        );    
                        if ( beh )
                        {
                        
                            toPrint.append("</td><td width=80% align=left >&nbsp;<IMG ");
                            toPrint.append(" id='beh_");
                            toPrint.append(id);
                            toPrint.append("' relatedID='");
                            toPrint.append(id);
                            //if ( isDisabled )   toReturn.append("' disable=true src='resources/clockrun.gif' width=21 height=19 /></td></tr></table>");
                            //else
                            toPrint.append("' src='"+ attrDef.getBeahvior_Img() + "' onclick=\"runBeh('"+attrDef.getBeahvior_Script()+"',"+id+".value)\" width=21 height=19 /></td></tr></table>");
                         
                        }
               }
               else{
                   boolean beh=false;
                    if (  attrDef.getBeahvior_Img()!=null) 
                    {
                        beh=true;
                        toPrint.append("<table style='width:100%' cellpadding='0' cellspacing='0'><tr><td>");
                    }
                   docHTML_renderFields.writeHTML_text(
                        toPrint,
                        v,
                        nameH,id,tabNumber,
                        isDisabled,
                        isVisible,
                        inTemplate,
                        onChange,
                        isRequired,
                        isRecommend,
                        attrDef.getLen(),
                        xattributes
                        );
                        
                        if ( beh )
                        {
                        
                            toPrint.append("</td><td width=25 align=right><IMG ");
                            toPrint.append(" id='beh_");
                            toPrint.append(id);
                            toPrint.append("' relatedID='");
                            toPrint.append(id);
                            //if ( isDisabled )   toReturn.append("' disable=true src='resources/clockrun.gif' width=21 height=19 /></td></tr></table>");
                            //else
                            toPrint.append("' src='"+ attrDef.getBeahvior_Img() + "' onclick=\"runBeh('"+attrDef.getBeahvior_Script()+"',"+id+".value)\" width=21 height=19 /></td></tr></table>");
                         
                        }
                        
                        
               }
           }
         if( obj.getMode() == boObject.MODE_EDIT_TEMPLATE  && xattributes.get("noRenderTemplate")==null){
            toPrint.append("<img onclick=\"openAttributeMap('");
            toPrint.append( nameH );
            toPrint.append( "','" );
            toPrint.append( doc.getDocIdx() );
            toPrint.append( "','" );
            toPrint.append( obj.getAttribute("TEMPLATE").getValueString() );
            toPrint.append("')\" src='templates/form/std/iconformula_on.gif' class='imgonoff' />");
          }
        
    }
    
    
    public static void renderCustomTag(StringBuffer toPrint,  int fieldNumber ,AttributeHandler attr, boObject obj,docHTML_controler DOCLIST,docHTML doc, ICustomField customRender , Hashtable xattributes, Hashtable p_values)throws boRuntimeException  
    {
        CharArrayWriter cw = new CharArrayWriter();
        PrintWriter     pw = new PrintWriter( cw );
        
        boolean bDataBinding = false;
        Class[] oFieldInterfaces = customRender.getClass().getInterfaces();                                    
        for (int z = 0; z < oFieldInterfaces.length; z++) 
        {
            if( oFieldInterfaces[ z ].getName().equals( 
                        ICustomFieldDataBinding.class.getName() 
                    ) 
              )
            {
                ICustomFieldDataBinding oPreviousDataField = doc.getDataBindingField( attr, (ICustomFieldDataBinding)customRender );
                if( oPreviousDataField != null )
                {
                    customRender = (ICustomField)oPreviousDataField;
                }
                else
                {
                    doc.registerDataBindingField( attr, (ICustomFieldDataBinding)customRender );
                }
                bDataBinding = true;
            } 
        }
        customRender.render( obj.getEboContext(),DOCLIST, doc , obj, pw, attr );
        StringBuffer nameH = new StringBuffer();
        StringBuffer v = new StringBuffer( attr.getValueString() == null ? "":attr.getValueString() );
        nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( attr.getName() );
        refreshValue(p_values, obj, nameH, v, customRender.getRelatedAttribute(), 
                    attr.isDisabled(), attr.required(), !attr.isVisible(), fieldNumber, 
                    /*tabNumber*/123, !attr.getDefAttribute().renderAsCheckBox());
        
        pw.close();
        cw.close();
        toPrint.append( cw.toCharArray() );
    }
    
    public static void renderHTMLtextObject(StringBuffer toPrint,  int fieldNumber ,AttributeHandler attr, boObject obj,docHTML_controler DOCLIST,docHTML doc,String text, Hashtable xattributes, Hashtable p_values)throws boRuntimeException  {
           
           StringBuffer v = new StringBuffer( attr.getValueString() == null ? "":attr.getValueString() );
           
           boDefAttribute attrDef   =   attr.getDefAttribute();
           String attrType          =   attrDef.getType().toUpperCase();
           byte attrTypeObj         =   attrDef.getAtributeType();
           StringBuffer nameH       =   new StringBuffer();
           StringBuffer id          =   new StringBuffer();
           StringBuffer onChange    =   new StringBuffer();
           StringBuffer onFocus    =   new StringBuffer();
           boolean isDisabled       =   attr.disableWhen();
           boolean isVisible        =   attr.isVisible();
           boolean inTemplate       =   obj.getMode() == boObject.MODE_EDIT_TEMPLATE; 
           boolean isRequired       =   attr.required();
           boolean isRecommend      =   attr.getRecommend();

           
           if ( text.equalsIgnoreCase("label") )
           {
               toPrint.append("<table id=\"messageZone\" class=\"messageZone\"><tr><td>");
               toPrint.append( attrDef.getLabel()  );
               toPrint.append("</td></tr></table>");
           }
           else if ( text.equalsIgnoreCase("description") )
           {
               toPrint.append("<table id=\"messageZone\" class=\"messageZone\"><tr><td>");
               toPrint.append( attrDef.getDescription()  );  
               toPrint.append("</td></tr></table>");
           }
           else if ( text.equalsIgnoreCase("empty") )
           {
               toPrint.append("<table id=\"messageZone\" class=\"messageZone\"><tr><td>");
               toPrint.append("</td></tr></table>");
           }
           else if ( text.equalsIgnoreCase("tooltip") )
           {
               toPrint.append("<table id=\"messageZone\" class=\"messageZone\"><tr><td>");
               toPrint.append( attrDef.getTooltip()  );
               toPrint.append("</td></tr></table>");
           }
           else if ( text.equalsIgnoreCase("object.label") )
           {
               toPrint.append("<table id=\"messageZone\" class=\"messageZone\"><tr><td>");
               toPrint.append( obj.getBoDefinition().getLabel() );
               toPrint.append("</td></tr></table>");
           }
           else if ( text.equalsIgnoreCase("object.description") )
           {
               toPrint.append("<table id=\"messageZone\" class=\"messageZone\"><tr><td>");
               toPrint.append( obj.getBoDefinition().getDescription() );
               toPrint.append("</td></tr></table>");
           }
           else if ( text.startsWith("object.") )
           {
                   toPrint.append("<table id=\"messageZone\" class=\"messageZone\"><tr><td>");
                   String Method=text.split("\\.")[1];
                   try
                   {
                       if("getExplainProperties".equals(Method))
                       {
                             toPrint.append(obj.getExplainProperties(doc));
                       }
                       else
                       {
                           Method ometh = obj.getClass().getMethod(Method,new Class[0]);
                            if(ometh!=null) 
                            {
                                toPrint.append( ometh.invoke(obj,new Object[0]) );
                            }
                       }
                   }
                   catch (InvocationTargetException e)
                   {
                       toPrint.append("InvocationTargetException : "+Method );
                   }
                   catch (NoSuchMethodException e)
                   {
                       toPrint.append("NoSuchMethodException: "+Method );
                   }
                   catch (IllegalAccessException e)
                   {
                       toPrint.append("IllegalAccessException: "+Method );
                   }
               
                   toPrint.append("</td></tr></table>");
           }
           else if ( text.startsWith("atr.") )
           {
                toPrint.append("<table id=\"messageZone\" class=\"messageZone\"><tr><td>");
                String Method=text.split("\\.")[1]; 
               try
                   {
                      
                       Method ometh = attr.getClass().getMethod(Method,new Class[0]);
                        if(ometh!=null) 
                        {
                            toPrint.append( ometh.invoke(attr,new Object[0]) );
                        }
                   }
                   catch (InvocationTargetException e)
                   {
                       toPrint.append("InvocationTargetException : "+Method );
                   }
                   catch (NoSuchMethodException e)
                   {
                       toPrint.append("NoSuchMethodException: "+Method );
                   }
                   catch (IllegalAccessException e)
                   {
                       toPrint.append("IllegalAccessException: "+Method );
                   }
                toPrint.append("</td></tr></table>");
           }
           else if(text.startsWith("CODE_JAVA(") && text.endsWith(")"))
           {
                NameSpace nsp = new NameSpace(new BshClassManager(),"executeJavaCode");
                bsh.Interpreter bshi = new bsh.Interpreter();
                nsp.importPackage("netgest.bo");
                nsp.importPackage("netgest.bo.def");
                nsp.importPackage("netgest.utils");
                nsp.importPackage("netgest.bo.runtime");
                nsp.importPackage("netgest.bo.utils");                
                bshi.setNameSpace(nsp);
                
                try
                {                    
                    CodeJavaConstructor cjc = new CodeJavaConstructor();
                    String code = cjc.treatCodeJava(text);
                    if(code.indexOf("this") != -1)
                    {
                        code = code.replaceAll("this","object");
                        nsp.setTypedVariable("object", boObject.class, obj, null);                        
                    }                               
                    Object xo = bshi.eval(code);
                    toPrint.append(xo);
                }
                catch (Exception e)
                {
                    throw new boRuntimeException(text, "executeJavaCode", e);
                }                                                              
           }     
           else
           {
               toPrint.append( (text==null||"null".equals(text))?"":text );
           }
        
    }
    

    private static class Sectxt {
      private static final char text[][]=new char[4][];
      public static final byte OPEN_TD = 0;
      public static final byte OPEN_TD_ALIGNRIGHT = 3;
      public static final byte CLOSE_TD = 1;
      public static final byte EMPTY_TD = 2;
      static {
        text[0] = "<TD>".toCharArray();
        text[1] = "</TD>".toCharArray();
        text[2] = "<TD>&nbsp;</TD>".toCharArray();
        text[3] = "<TD align='right'>".toCharArray();
      }
    }
    
    private static void refreshValue(Hashtable p_values, boObject obj, StringBuffer id, StringBuffer value, 
        String bdName, boolean disable, boolean req, boolean hidden, int fieldNumber, int tab, boolean bool)
    {
        HtmlField htmlAux =  new HtmlField(value.toString(), id.toString(), bdName, disable, req, hidden, fieldNumber, tab);
        if(bool)
            htmlAux.setBool();
        p_values.put(obj.getName() + "_" + obj.getBoui() + "_" +bdName, htmlAux);
    }
    
    private static void toSubmit(Hashtable p_values, boObject obj, docHTML_controler DOCLIST,
        docHTML doc)
    {
        try
        {
            String[] dep = obj.dependencesFields();
            if(dep == null) return;
            String parentName;
            String attName;
            String fieldName;
            String attF, key;
            HtmlField auxField, htmlAux;
            AttributeHandler attH;
            StringBuffer htmlCode;
            for(int i = 0; i < dep.length; i++)
            {
                if(dep[i].startsWith("parent_"))
                {
                    parentName = dep[i].substring(7, dep[i].indexOf("."));
                    if(obj.getParent() != null && parentName.equals(obj.getParent().getName()))
                    {
                        attName = dep[i].substring(dep[i].indexOf(".") + 1);
                        
                        fieldName = "submit_" + parentName + "_" + attName;
                        attF = parentName + "__" + obj.getParent().getBoui() + "__" + attName;
                        key = parentName + "_" + obj.getParent().getBoui() + "_" + attName;
                        htmlCode = new StringBuffer();
                        attH = obj.getParent().getAttribute(attName);
                        auxField = (HtmlField)p_values.get(key);
                        renderHTMLObject(htmlCode,  auxField.getFieldId() , attH, obj.getParent(), DOCLIST, doc, null, p_values, true, auxField.getTab());
                        
                                                
                        htmlAux =  new HtmlField(attF, fieldName, htmlCode.toString(), false, false, false, 0, 0);
                        p_values.put(fieldName, htmlAux);
                    }
                }
            }
        }
        catch (boRuntimeException e)
        {
            //ignore
        }
    }
}

