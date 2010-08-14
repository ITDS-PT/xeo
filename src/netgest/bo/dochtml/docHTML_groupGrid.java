/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import java.util.*;
import netgest.bo.def.*;
import javax.servlet.jsp.*;

import netgest.bo.localized.JSMessages;
import netgest.bo.localized.JSPMessages;
import netgest.bo.runtime.*;


public final class docHTML_groupGrid {
    

    private Vector  p_cols;
    private Vector  p_groups;
    private String  p_title;
    private boolean p_canSelectRows;
    private boolean p_showIcon;
    private boolean p_showState;
    private boolean p_canExpandRows;
    private boolean p_barStatus;
    private boolean p_colsFooter;
    private boolean p_barFilter;
    private docHTML p_doc;
//    private String p_template;

    
    public docHTML_groupGrid(docHTML doc) {
        p_cols=new Vector();
        p_groups=new Vector();
        p_title="";
        p_canSelectRows=true;
        p_showIcon  =true;
        p_showState =true;
        p_canExpandRows= true;


        p_barStatus=false;
        p_colsFooter=false;
        p_barFilter=false;;
        p_doc=doc;
     //   p_template=template;
        
    
    }

    public void setTitle(String xtitle){
        p_title=xtitle;

    }
    public void setTitle(){
        p_title="";

    }

    public void addCol(String atrName,int width,Hashtable attributes) {
        p_cols.add( new docHTML_groupGridCol(atrName,width,attributes) );
    }
    
    public void addGroup(String boql,String title, Hashtable attributes) {
        p_groups.add( new docHTML_groupGridDefGroup( boql , title , attributes ) );
    }
    public void render(PageContext page,docHTML DOC,docHTML_controler DOCLIST) throws boRuntimeException,java.io.IOException {
        JspWriter out=page.getOut();
        boObjectList[] bolist=new boObjectList[p_groups.size()];
        String[] titles=new String[p_groups.size()];
        Hashtable[] attributes= new Hashtable[ p_groups.size() ]; 
        for (int i = 0; i < p_groups.size(); i++) 
        {
            String boql=(( docHTML_groupGridDefGroup )p_groups.get(i)).p_boql;
            //boql=boql.replaceAll("\\&lt;","<");
            
            String title=(( docHTML_groupGridDefGroup )p_groups.get(i)).p_title;
            
            
            bolist[i] = boObjectList.list( DOC.getEboContext(),boql);
            titles[i] = title;
            attributes[i] = (( docHTML_groupGridDefGroup )p_groups.get(i)).p_attributes;
        }
        
        String[] columnsTitles = new String[ p_cols.size() ];
        Integer[] columnsWidth   = new Integer[ p_cols.size() ];
        String[] columnsAtr    = new String[ p_cols.size() ];
        
        for (int i = 0; i < p_cols.size() ; i++) 
        {
           String attributeName=(( docHTML_groupGridCol ) p_cols.get(i)).p_atr;
           columnsAtr[i]=attributeName;
           columnsWidth[i]=  new Integer(  (( docHTML_groupGridCol ) p_cols.get(i)).p_width );
           columnsTitles[i]=null;
           for( int z=0 ; z < bolist.length ; z++)
           {
              
               AttributeHandler attr=bolist[z].getObject().getAttribute( attributeName );
               if ( attr != null )
               {
                   columnsTitles[i]=attr.getDefAttribute().getLabel();
                   break;
               }
               
           }
           
        }
        if ( columnsTitles[0] ==null) columnsTitles[0]=""; 
        
        
     out.print("<!--BEGIN -->\n");   
        out.print("<table cellSpacing=\"0\" cellPadding=\"0\" style=\"height:100%;width:100%;table-layout:fixed;\">\n");
        out.print("               <tr><td id='buildArea' class='toolAreaGrid'>");
        
         docHTML_section sec = DOC.createSection("lookupGG","",false,5);
            //bodef=boDefHandler.getBoDefinition(look_object);
        docHTML_sectionRow row; 
        row=sec.addRow();

        Hashtable xattributes=new Hashtable();
        docHTML_sectionCell xcell=row.addCellMultiField();
        xcell.addField( docHTML_sectionField.newText(
                new StringBuffer("TEXTSEARCH"),
                new StringBuffer("TEXTSEARCH"),
                new StringBuffer(JSPMessages.getString("docHTML_groupGrid.10")),
                new StringBuffer(),null,null,null),"100%",xattributes) ;

    xcell.addField( docHTML_sectionField.newButton(
                new StringBuffer("find"),
                new StringBuffer(JSPMessages.getString("docHTML_groupGrid.13")),
                new StringBuffer("window.location.reload();"))
                
                ,null,null
                ) ;
        //new StringBuffer("this.location.href=SURL+\"&list_fulltext=\"+escape(TEXTSEARCH.value)"))
        /*
        row=sec.addRow();
        row.addCell( docHTML_sectionField.newButton(
                new StringBuffer("find2"),
                new StringBuffer("Procura"),
                new StringBuffer("findframe.location.href=SURL+\"&list_fulltext=\"+escape(TEXTSEARCH.value)"))
                ,null  ) ;
          */      
    sec.p_height="50px";
      sec.render( page ,DOC, DOCLIST);
        
        out.print("</td></tr>\n");
        out.print("    <tr>\n");
        out.print("	 <td style=\"height:100%;width:100%\">\n");
		out.print("    <div style=\"width:100%;height:100%;overflow-x:auto\">\n");
		out.print("		   <table style=\"height:100%;width:100%;\" class=\"g_std\" cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\">\n");
		out.print("			  <tbody>\n");
        out.print("				  <tr height=\"25\">\n");
		out.print("				    <td>\n");
		out.print("					   <table id=\"g1000_body\"  cellpadding=\"2\" cellspacing=\"0\" style=\"height:25px\" class=\"gh_std\">\n");
		out.print("							<colgroup>\n");
		out.print("							<col style=\"PADDING-LEFT: 10px\" width=30 />\n");
        out.print("							<col width=20 />\n");
        out.print("							<col>\n");
        boolean hasSep=false;
        for (int i = 0; i < columnsWidth.length; i++) 
        {
            if ( columnsTitles[i] != null )
            {
        out.print("							<col width=");out.print( columnsWidth[i].intValue() -2 ); out.print(">\n");
        out.print("							<col width=2>\n");
        hasSep=true;
            }
        }
        if ( ! hasSep ){
		out.print("							<col width=2 />\n");
        }
		out.print("							<col width=15 />\n");
 		out.print("						    <tbody>\n");
		out.print("								<tr>\n");
        out.print("                               <td class=\"gh_std\"><img src='resources/buildgrid.gif'onclick='toggleBuildArea(buildArea)' height=16 with=16 /> </td>\n");
        out.print("                               <td class=\"gh_std\">&nbsp</td>\n");
		out.print("								  <td colspan=2 id=\"g$ExpanderParent\" class=\"gh_std\">");out.print( columnsTitles[0] ); out.print("</td>\n");
        out.print(" 							  <td class=\"ghSep_std\">&nbsp;</td>\n");
        hasSep=false;
        
		for (int i = 1; i < columnsWidth.length; i++) 
        {
            if ( columnsTitles[i] != null )
            {
        out.print("								  <td class='gh_std' >");out.print( columnsTitles[i] ); out.print("</td>\n");
        out.print(" 							  <td class='ghSep_std'>&nbsp;</td>\n");
        hasSep=true;
            }
        }			
        if ( !hasSep )
        {
        out.print(" 							  <td class=\"ghSep_std\">&nbsp;</td>\n");    
        }
        out.print("             				  <td class='gh_std' width='14'><img onclick='window.location.reload()' src='templates/grid/std/ghRefresh.gif' width='13' height='13' /> </td>\n");
		out.print("								</tr>\n");
		out.print("							</tbody>\n");
		out.print("						</table>\n");
		out.print("					</td>\n");
		out.print("			    </tr>\n");
		out.print("				<tr>\n");
		out.print(" 				<td>\n");
		out.print("					   <div id=\"grid\" class=\"gContainerLines_std\">\n");
        
        for (int i = 0; i < bolist.length ; i++)
        {        
             String xcor=(String) attributes[i].get("color");
             if ( xcor != null )
             {
       out.print("         <DIV class=headerGroup  style='COLOR:"+xcor+"; BORDER-BOTTOM:"+xcor+" 1px solid'>"+titles[i]+"</DIV>\n");
             }
             else
             {
       out.print("          <DIV class=headerGroup >"+titles[i]+"</DIV>\n"); 
             }
		     
             boObjectList list=bolist[i];
             list.beforeFirst();
             
        out.print("      <table cellpadding='2' cellspacing='0' style='TABLE-LAYOUT: fixed; MARGIN-BOTTOM: 10px; WIDTH: 100%'>\n");
	    out.print("		     <colgroup>\n");
		out.print("			 <col style='PADDING-LEFT: 10px' width=30 />\n");
        out.print("			 <col width=20 />\n");
        out.print("			 <col />\n");
        int nrcols=3;
		for (int z = 1; z < columnsWidth.length; z++) 
        {
            if ( columnsTitles[z] != null )
            {
        out.print("		 	 <col width=");out.print( columnsWidth[z] ); out.print(">\n");
                nrcols++;
            }
        }			
        
        boObject obj;
        String xatrname="";
        boDefHandler def;
        while ( list.next() )
        {
             obj=list.getObject();
             def=obj.getBoDefinition();
             if ( obj.exists() )
             {
                 out.print("<tr class=rowGroup boui="+obj.bo_boui+" obj='"+obj.getName()+"' >\n");
                 out.print("<td><img src='");
                 out.print( "resources/"+def.getName()+"/ico16.gif'") ;
                 out.print(" width=16 height=16 /></td>\n");
                 
                 
                 out.print("<td><img src='");
                 out.print( "resources/"+obj.getStringComposedState()) ;
                 out.print(".gif' width=16 height=16 /></td>\n");
                   
                    for (int z = 0; z < columnsWidth.length; z++) 
                        {
                            if ( columnsTitles[z] != null )
                            {
                            out.print("<td>");
                                xatrname=columnsAtr[z];    
                                if ( def.hasAttribute( xatrname ) )
                                {
                                   AttributeHandler attr= obj.getAttribute(xatrname);
                                   
                                    if( def.getAttributeType(xatrname) == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                                    {
                                        //obj.getAttribute(xatrname)
                                        long v=attr.getValueLong();
                                        if ( v  > 0) 
                                        {
                                        
                                            out.print("<div style='white-space: nowrap;overflow-y:hidden;background-color:transparent;border:0' class='lu ro'><span class='lui' onclick=\"");
                                            out.print("winmain().openDoc('medium','");
                                            
                                            boObject o = DOC.getObject( v );
                                            out.print( o.getName().toLowerCase() );
                                            out.print("','edit','method=edit&boui=");
                                            out.print( v);
                                            out.print("')");
                                          //  out.print(DOC.getDocIdx());
                                         //   out.print("','");
                                            //toPrint.append();
                                         //   out.print("','");
                                            //toPrint.append();
                                            out.print(";event.cancelBubble=true\"");
                                            out.print(" boui='");
                                            out.print(v);
                                            out.print("' object='");
                                            out.print( o.getName() );
                                            out.print("'>");

                                        
                                            out.print(o.getCARDID());
                                            out.print("</span></div>" );
                                        }
                                         else out.print("&nbsp");
                                        
                                    }
                                    else
                                    {
                                        String v=attr.getValueString();
                                        if( attr.getDefAttribute().getType().equalsIgnoreCase("DATETIME") && v.length()>0 )
                                        {
                                            v=v.replace('T',' ');
                                            v=v.substring(0,v.length()-3);
                                        }
                                        
                                        out.print("<nobr>"+v+"</nobr>");
                                    }
                                    
                                    
                                }
                                else
                                {
                                    out.print("&nbsp;");
                                }
                            out.print("</td>");
                            }
                        }			
                                    
                 out.print("</tr>\n");
                 
             }
             else
             {
                 out.print("<tr>");
                 out.print("<td class='none' colspan="+nrcols+JSPMessages.getString("docHTML_groupGrid.9"));
                 out.print("</tr>\n"); 
             }
            
        }
             
        out.print("      </table>\n");
          
             
        }
        
        out.print("</div></td></tr></table></div></td></tr></table>\n");
     out.print("<!--END -->\n");
    }

  
}