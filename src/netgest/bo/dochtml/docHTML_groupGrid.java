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
        p_title=""; //$NON-NLS-1$
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
        p_title=""; //$NON-NLS-1$

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
        if ( columnsTitles[0] ==null) columnsTitles[0]="";  //$NON-NLS-1$
        
        
     out.print("<!--BEGIN -->\n");    //$NON-NLS-1$
        out.print("<table cellSpacing=\"0\" cellPadding=\"0\" style=\"height:100%;width:100%;table-layout:fixed;\">\n"); //$NON-NLS-1$
        out.print("               <tr><td id='buildArea' class='toolAreaGrid'>"); //$NON-NLS-1$
        
         docHTML_section sec = DOC.createSection("lookupGG","",false,5); //$NON-NLS-1$ //$NON-NLS-2$
            //bodef=boDefHandler.getBoDefinition(look_object);
        docHTML_sectionRow row; 
        row=sec.addRow();

        Hashtable xattributes=new Hashtable();
        docHTML_sectionCell xcell=row.addCellMultiField();
        xcell.addField( docHTML_sectionField.newText(
                new StringBuffer("TEXTSEARCH"), //$NON-NLS-1$
                new StringBuffer("TEXTSEARCH"), //$NON-NLS-1$
                new StringBuffer(JSPMessages.getString("docHTML_groupGrid.10")), //$NON-NLS-1$
                new StringBuffer(),null,null,null),"100%",xattributes) ; //$NON-NLS-1$

    xcell.addField( docHTML_sectionField.newButton(
                new StringBuffer("find"), //$NON-NLS-1$
                new StringBuffer(JSPMessages.getString("docHTML_groupGrid.13")), //$NON-NLS-1$
                new StringBuffer("window.location.reload();")) //$NON-NLS-1$
                
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
    sec.p_height="50px"; //$NON-NLS-1$
      sec.render( page ,DOC, DOCLIST);
        
        out.print("</td></tr>\n"); //$NON-NLS-1$
        out.print("    <tr>\n"); //$NON-NLS-1$
        out.print("	 <td style=\"height:100%;width:100%\">\n"); //$NON-NLS-1$
		out.print("    <div style=\"width:100%;height:100%;overflow-x:auto\">\n"); //$NON-NLS-1$
		out.print("		   <table style=\"height:100%;width:100%;\" class=\"g_std\" cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\">\n"); //$NON-NLS-1$
		out.print("			  <tbody>\n"); //$NON-NLS-1$
        out.print("				  <tr height=\"25\">\n"); //$NON-NLS-1$
		out.print("				    <td>\n"); //$NON-NLS-1$
		out.print("					   <table id=\"g1000_body\"  cellpadding=\"2\" cellspacing=\"0\" style=\"height:25px\" class=\"gh_std\">\n"); //$NON-NLS-1$
		out.print("							<colgroup>\n"); //$NON-NLS-1$
		out.print("							<col style=\"PADDING-LEFT: 10px\" width=30 />\n"); //$NON-NLS-1$
        out.print("							<col width=20 />\n"); //$NON-NLS-1$
        out.print("							<col>\n"); //$NON-NLS-1$
        boolean hasSep=false;
        for (int i = 0; i < columnsWidth.length; i++) 
        {
            if ( columnsTitles[i] != null )
            {
        out.print("							<col width=");out.print( columnsWidth[i].intValue() -2 ); out.print(">\n"); //$NON-NLS-1$ //$NON-NLS-2$
        out.print("							<col width=2>\n"); //$NON-NLS-1$
        hasSep=true;
            }
        }
        if ( ! hasSep ){
		out.print("							<col width=2 />\n"); //$NON-NLS-1$
        }
		out.print("							<col width=15 />\n"); //$NON-NLS-1$
 		out.print("						    <tbody>\n"); //$NON-NLS-1$
		out.print("								<tr>\n"); //$NON-NLS-1$
        out.print("                               <td class=\"gh_std\"><img src='resources/buildgrid.gif'onclick='toggleBuildArea(buildArea)' height=16 with=16 /> </td>\n"); //$NON-NLS-1$
        out.print("                               <td class=\"gh_std\">&nbsp</td>\n"); //$NON-NLS-1$
		out.print("								  <td colspan=2 id=\"g$ExpanderParent\" class=\"gh_std\">");out.print( columnsTitles[0] ); out.print("</td>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        out.print(" 							  <td class=\"ghSep_std\">&nbsp;</td>\n"); //$NON-NLS-1$
        hasSep=false;
        
		for (int i = 1; i < columnsWidth.length; i++) 
        {
            if ( columnsTitles[i] != null )
            {
        out.print("								  <td class='gh_std' >");out.print( columnsTitles[i] ); out.print("</td>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        out.print(" 							  <td class='ghSep_std'>&nbsp;</td>\n"); //$NON-NLS-1$
        hasSep=true;
            }
        }			
        if ( !hasSep )
        {
        out.print(" 							  <td class=\"ghSep_std\">&nbsp;</td>\n");     //$NON-NLS-1$
        }
        out.print("             				  <td class='gh_std' width='14'><img onclick='window.location.reload()' src='templates/grid/std/ghRefresh.gif' width='13' height='13' /> </td>\n"); //$NON-NLS-1$
		out.print("								</tr>\n"); //$NON-NLS-1$
		out.print("							</tbody>\n"); //$NON-NLS-1$
		out.print("						</table>\n"); //$NON-NLS-1$
		out.print("					</td>\n"); //$NON-NLS-1$
		out.print("			    </tr>\n"); //$NON-NLS-1$
		out.print("				<tr>\n"); //$NON-NLS-1$
		out.print(" 				<td>\n"); //$NON-NLS-1$
		out.print("					   <div id=\"grid\" class=\"gContainerLines_std\">\n"); //$NON-NLS-1$
        
        for (int i = 0; i < bolist.length ; i++)
        {        
             String xcor=(String) attributes[i].get("color"); //$NON-NLS-1$
             if ( xcor != null )
             {
       out.print("         <DIV class=headerGroup  style='COLOR:"+xcor+"; BORDER-BOTTOM:"+xcor+" 1px solid'>"+titles[i]+"</DIV>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
             }
             else
             {
       out.print("          <DIV class=headerGroup >"+titles[i]+"</DIV>\n");  //$NON-NLS-1$ //$NON-NLS-2$
             }
		     
             boObjectList list=bolist[i];
             list.beforeFirst();
             
        out.print("      <table cellpadding='2' cellspacing='0' style='TABLE-LAYOUT: fixed; MARGIN-BOTTOM: 10px; WIDTH: 100%'>\n"); //$NON-NLS-1$
	    out.print("		     <colgroup>\n"); //$NON-NLS-1$
		out.print("			 <col style='PADDING-LEFT: 10px' width=30 />\n"); //$NON-NLS-1$
        out.print("			 <col width=20 />\n"); //$NON-NLS-1$
        out.print("			 <col />\n"); //$NON-NLS-1$
        int nrcols=3;
		for (int z = 1; z < columnsWidth.length; z++) 
        {
            if ( columnsTitles[z] != null )
            {
        out.print("		 	 <col width=");out.print( columnsWidth[z] ); out.print(">\n"); //$NON-NLS-1$ //$NON-NLS-2$
                nrcols++;
            }
        }			
        
        boObject obj;
        String xatrname=""; //$NON-NLS-1$
        boDefHandler def;
        while ( list.next() )
        {
             obj=list.getObject();
             def=obj.getBoDefinition();
             if ( obj.exists() )
             {
                 out.print("<tr class=rowGroup boui="+obj.bo_boui+" obj='"+obj.getName()+"' >\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 out.print("<td><img src='"); //$NON-NLS-1$
                 out.print( "resources/"+def.getName()+"/ico16.gif'") ; //$NON-NLS-1$ //$NON-NLS-2$
                 out.print(" width=16 height=16 /></td>\n"); //$NON-NLS-1$
                 
                 
                 out.print("<td><img src='"); //$NON-NLS-1$
                 out.print( "resources/"+obj.getStringComposedState()) ; //$NON-NLS-1$
                 out.print(".gif' width=16 height=16 /></td>\n"); //$NON-NLS-1$
                   
                    for (int z = 0; z < columnsWidth.length; z++) 
                        {
                            if ( columnsTitles[z] != null )
                            {
                            out.print("<td>"); //$NON-NLS-1$
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
                                        
                                            out.print("<div style='white-space: nowrap;overflow-y:hidden;background-color:transparent;border:0' class='lu ro'><span class='lui' onclick=\""); //$NON-NLS-1$
                                            out.print("winmain().openDoc('medium','"); //$NON-NLS-1$
                                            
                                            boObject o = DOC.getObject( v );
                                            out.print( o.getName().toLowerCase() );
                                            out.print("','edit','method=edit&boui="); //$NON-NLS-1$
                                            out.print( v);
                                            out.print("')"); //$NON-NLS-1$
                                          //  out.print(DOC.getDocIdx());
                                         //   out.print("','");
                                            //toPrint.append();
                                         //   out.print("','");
                                            //toPrint.append();
                                            out.print(";event.cancelBubble=true\""); //$NON-NLS-1$
                                            out.print(" boui='"); //$NON-NLS-1$
                                            out.print(v);
                                            out.print("' object='"); //$NON-NLS-1$
                                            out.print( o.getName() );
                                            out.print("'>"); //$NON-NLS-1$

                                        
                                            out.print(o.getCARDID());
                                            out.print("</span></div>" ); //$NON-NLS-1$
                                        }
                                         else out.print("&nbsp"); //$NON-NLS-1$
                                        
                                    }
                                    else
                                    {
                                        String v=attr.getValueString();
                                        if( attr.getDefAttribute().getType().equalsIgnoreCase("DATETIME") && v.length()>0 ) //$NON-NLS-1$
                                        {
                                            v=v.replace('T',' ');
                                            v=v.substring(0,v.length()-3);
                                        }
                                        
                                        out.print("<nobr>"+v+"</nobr>"); //$NON-NLS-1$ //$NON-NLS-2$
                                    }
                                    
                                    
                                }
                                else
                                {
                                    out.print("&nbsp;"); //$NON-NLS-1$
                                }
                            out.print("</td>"); //$NON-NLS-1$
                            }
                        }			
                                    
                 out.print("</tr>\n"); //$NON-NLS-1$
                 
             }
             else
             {
                 out.print("<tr>"); //$NON-NLS-1$
                 out.print("<td class='none' colspan="+nrcols+JSPMessages.getString("docHTML_groupGrid.9")); //$NON-NLS-1$ //$NON-NLS-2$
                 out.print("</tr>\n");  //$NON-NLS-1$
             }
            
        }
             
        out.print("      </table>\n"); //$NON-NLS-1$
          
             
        }
        
        out.print("</div></td></tr></table></div></td></tr></table>\n"); //$NON-NLS-1$
     out.print("<!--END -->\n"); //$NON-NLS-1$
    }

  
}