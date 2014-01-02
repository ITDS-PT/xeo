/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import java.io.IOException;
import java.net.URLEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import netgest.bo.boConfig;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.localized.JSPMessages;
import netgest.bo.runtime.*;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.userquery.userquery;

import netgest.utils.ClassUtils;
import netgest.utils.ngtXMLHandler;
import netgest.bo.system.Logger;


public class docHTML_treeView
    {    
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.dochtml.docHTML_treeView");
    




    

  
    private docHTML_treeView( boDefHandler objdef, ngtXMLHandler defTree )
    {
        
       
        
     
     
    }

    
    public static void render( docHTML_treeRuntime tree, PageContext page, docHTML DOC, docHTML_controler DOCLIST) throws boRuntimeException,java.io.IOException,SQLException 
    {
        long timeItotal = System.currentTimeMillis();
        JspWriter out=page.getOut();

        //long xt = System.currentTimeMillis();
        
       
        tree.p_haveErrors=true;
        boolean hParameters      = tree.haveParameters( DOC.getEboContext() );
        boolean hBlankParameters = tree.haveBlankParameters( DOC.getEboContext() );
        
        if ( !hBlankParameters && hParameters )
        {
            if ( !tree.analizeSql( DOC.getEboContext() ) )
            {
                hBlankParameters=true;
            }
        }
        
        String[] ctrlLines = new String[500];
        int line=0;
        int nrgroups=0;
        Hashtable groupCnt = new Hashtable();
        Connection cn=null;
        if ( !hBlankParameters  )
        {
            String sqlGroups=tree.getSqlGroups( DOC.getEboContext() );
           
           
            
            cn= DOC.getEboContext().getConnectionData();
            if ( sqlGroups != null )
            {
                //Connection cn= DOC.getEboContext().getConnectionData();
                PreparedStatement pr;
                ResultSet rslt;
             
                nrgroups=tree.p_groups.length;
                pr=cn.prepareStatement( sqlGroups , ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );
                for (int ip = 0; ip < tree.p_parameters.size() ; ip++) 
                {
                    pr.setObject(ip+1, (Object)tree.p_parameters.get(ip) );   
                }
                long timeI = System.currentTimeMillis();
                 rslt= pr.executeQuery();
                long timeF = System.currentTimeMillis() - timeI;
                writeQueryToLog(out, timeF, sqlGroups);
                 
                 
                
                 
                 String vg[]    = new String[ nrgroups ];
                 int vl[]      = new int[ nrgroups];
                
                 boolean haveResults = rslt.next();
                 
                 if ( haveResults )
                 {
                     for (byte i = 0; i < nrgroups ; i++) 
                     {
                            vg[i]  = tree.getGroupValue( rslt , i+1 );
                            ctrlLines[ line ++ ] = ""+i+"-:-"+vg[i];
                            
                            
                     }
                     
                     
                     do 
                     {
                        
                        for (byte i = 0; i < nrgroups ; i++) 
                        {
                            String xv=tree.getGroupValue( rslt , i+1 );
                            
                            int counter=rslt.getInt( nrgroups+1 );
                            
                            if ( !xv.equals(vg[i] ) )
                            {
                                
                                for (int z = i; z < nrgroups ; z++) 
                                {
                                    xv=tree.getGroupValue( rslt , z+1 );     
                                    ctrlLines[ line ++ ] = ""+z+"-:-"+xv;
                                    
                                    if ( line > ctrlLines.length-1 ) ctrlLines = ClassUtils.growStringArray( ctrlLines , 200 );
                                }
                                
                                
                                for (int z = i ; z < nrgroups ; z++) 
                                {
                                
                                       
                                     
                                     String xkey="";
                                     for (byte k=0 ; k<=z ;k++ )
                                     {
                                        xkey+=vg[k]+"-";
                                     }
                                     
                                 //    logger.finest( "G"+z+"-:-"+xkey+" |-->"+vl[z] );
                                     groupCnt.put( "G"+z+"-:-"+xkey , new Integer( vl[z] ) );
                                     
                                    //groupCnt.put( "G"+z+"-:-"+vg[z] , new Integer( vl[z] ) ); 
                                    vl[z]=counter;
                                }
                                
                                break;
                      
                            }
                            else
                            {
                                vl[i]+=counter;
                            }
              
                        }
                        
                        for (int i = 0; i < nrgroups ; i++) 
                        {
                            vg[i] = tree.getGroupValue( rslt , i+1 );
                        }
                        
                
                    } while ( rslt.next() );
                    
                    
                    
                     for ( int j=0 ; j< nrgroups ; j++ )
                     {
                     
                                 
                                 String xkey="";
                                     for (byte k=0 ; k<=j ;k++ )
                                     {
                                        xkey+=vg[k]+"-";
                                     }
                         
                           groupCnt.put( "G"+j+"-:-"+xkey , new Integer( vl[j] ) );          
                         
                     }
                    
                 }
                 rslt.close();
                 pr.close();
            }
        }
        
        
        // escrever HEADER
        
        
        
        
        
        
        
        
        out.print("<!--BEGIN -->\n");   
        out.print("<table cellSpacing=\"0\" cellPadding=\"0\" style=\"height:100%;width:100%;table-layout:fixed;\">\n");
        out.print("               <tr><td id='buildArea' class='toolAreaGrid'>");
        
                
       out.print("<table cellpading='0' cellspacing='0' style='width:100%'><tr><td>");
       
        out.print("<span id='0' class='colGroupInterval' >&nbsp;</span>");

        
        for (int i = 0; i < tree.p_groups.length ; i++)
        {
            out.print("<a title='Clique para mudar ordenação do grupo' relatedTree='"+tree.p_key+"' class='colGroup' href='javascript:toggleOrderGroup(\""+tree.p_key+"\",\""+i+"\")' id='");
            out.print( tree.p_groups[i].p_name );
            out.print("'>");
            out.print( tree.getImageSort( tree.p_groups_order[i] ) );
            
            out.print( tree.getGroupLabel( i) );
            out.print("</a>");
        
            
            out.print("<span id='"+(i+1)+"' relatedTree='"+tree.p_key+"' class='colGroupInterval' >&nbsp;</span>");
        }
   
            out.print("<a  relatedTree='"+tree.p_key+"' title=' Mova colunas para este espaço de modo a agrupar a informação ' class='colGroupNone' href='javascript:' id='");
            out.print( "NoNe" );
            out.print("'>");
            out.print(JSPMessages.getString("docHTML_treeView.466")); 
            out.print("</a>");
   
        out.print("</td><td align='right'>");
       
       
       StringBuffer toPrint = new StringBuffer();
       out.print("<table style='background-color:#EEEEEE;border-right:1px solid #BBBBBB;border-bottom:1px solid #BBBBBB'><tr>");
       
       Hashtable xattributes=new Hashtable();
       
       boObjectList userFilters = userquery.getUserQueries( DOC.getEboContext() , tree.p_bodef.getName() );
       
       userFilters.beforeFirst();
       
       StringBuffer[] xInternal = new StringBuffer[ userFilters.getRowCount()+1 ] ;
       StringBuffer[] xExternal = new StringBuffer[ userFilters.getRowCount()+1 ] ;
       
       xInternal[0]=new StringBuffer("0");
       xExternal[0]=new StringBuffer("&nbsp;");
       int idx=1;      
       while (userFilters.next())
       {
            
            xInternal[idx  ]=new StringBuffer( userFilters.getObject().getBoui()+"" );
            xExternal[idx++]=new StringBuffer( userFilters.getObject().getAttribute("name").getValueString() );
            
       }
       
       if ( tree.p_bouiUserQuery != -1 )
       {
            //String sqlu=userquery.userQueryToSql( DOC.getEboContext() , tree.p_bouiUserQuery , true , tree.p_userParameters );
            //if ( sqlu!= null && sqlu.length()>0)
           // {
                boObject o=DOC.getObject(tree.p_bouiUserQuery );
                if ( o!= null ) tree.p_filterName=o.getAttribute("name").getValueString();
           // }
            else tree.p_filterName="";
            
            xInternal[0]=new StringBuffer("0");
            xExternal[0]=new StringBuffer("&nbsp;"); 
       }
       else
       {
           tree.p_filterName="";
       }
       
       docHTML_sectionField f;  
       f=docHTML_sectionField.newCombo(
                new StringBuffer("qryObject_"+tree.p_key),
                new StringBuffer("qryObject__"+tree.p_key),
                new StringBuffer("Objecto"),
                new StringBuffer( tree.p_filterName ),
                xExternal,
                xInternal,
                false,false,
                new StringBuffer("setUserQueryBoui"),null,null
                );
               
       toPrint.setLength(0);
       out.print("<td style='width:130px' ><span style=''>");
       
       docHTML_section.renderHTMLObject(toPrint , f , DOC ,  DOCLIST);
       out.print(toPrint); 
       out.print("</span></td>");
       
       
      // out.print("<td style='width:50px'>Texto </td>");
       out.print("<td style='width:170px' >");
       
       toPrint.setLength(0);
       /*
       docHTML_section.renderHTMLObject(toPrint ,
               docHTML_sectionField.newText(
                new StringBuffer("TEXTSEARCH"),
                new StringBuffer("TEXTSEARCH"),
                new StringBuffer("texttofind"),
                new StringBuffer( tree.p_textFullSearch ),null,null,null),DOC,DOCLIST );*/
                
//--------------------------desenho do campo procura  

        
                   toPrint.append("<table  class='fullTextSearch' cellpadding=0 cellspacing=0 width='100%'><tr><td width='100%'>");
                   toPrint.append("<input class='fullTextSearch' value='");
                   toPrint.append( tree.p_textFullSearch);
                   toPrint.append("' id='");
                   toPrint.append( "TEXTSEARCH" );
                   toPrint.append('\'');
                   toPrint.append(" key='"+tree.p_key+"'");
                     
              //     if (  field.p_JSonChange!=null &&  field.p_JSonChange.length()>0 ){
              //         toPrint.append(" onchange='");
              //         toPrint.append(field.p_JSonChange);
              //         toPrint.append('\'');
               //    }
               
                   toPrint.append(" onchange='");
                   toPrint.append( "setFullTextGroup(\""+tree.p_key+"\",TEXTSEARCH.value )"  );
                   toPrint.append('\'');
                   toPrint.append(" name = '");
                   toPrint.append( "TEXTSEARCH" );
                   //toPrint.append("' tabindex='"+DOCLIST.tabindex+++"'/>");
                   //<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "detail", DOCLIST)%>
                   toPrint.append("' tabIndex='")
                        .append(DOC.getTabindex(DOC.SEARCH, "TEXTSEARCH" + tree.p_key, DOCLIST))
                        .append("'/>");
                   toPrint.append("</td><td><img "+" title='"+JSPMessages.getString("docHTML_treeView.1")+"' onclick='setFullTextGroup(\""+tree.p_key+"\",\"\" )' style='' border='0' src='templates/form/std/fulltextdelete.gif' width='10' height='10'></td></tr></table>");
//---------------------------------------------------------------------------------                   
                   
        
       out.print(toPrint);
       out.print("</td>");
  /*     
        Enumeration h=DOC.getEboContext().getRequest().getHeaderNames();
         while ( h.hasMoreElements() )
         {
             String name=(String )h.nextElement();
             String xxx=DOC.getEboContext().getRequest().getHeader(name);
             logger.finest(name+"-->"+xxx);
             
         }
       
    */   
         /*
       f=docHTML_sectionField.newButton(
                new StringBuffer("find"),
                new StringBuffer("Procurar"),
                new StringBuffer("this.location.href=SURL+\"&list_fulltext=\"+escape(TEXTSEARCH.value)"));
               */
       int tab = DOC.getTabindex(DOC.SEARCH, "BTNSRCH" + tree.p_key, DOCLIST);
       
       out.print("<td style='width:60px' >");
      // docHTML_section.renderHTMLObject(toPrint , f , DOC ,  DOCLIST);
        out.print("<button id='BTNSRCH"+tree.p_key+"' title='"+JSPMessages.getString("docHTML_treeView.2")+"' tabindex = '" + tab + "' onclick='setFullTextGroup(\""+tree.p_key+"\",TEXTSEARCH.value )' style='height:20px;width:60px;'>Procura</button>");
        out.print("</td>");
       
        out.print("<td style='width:62px' >");
        
        tab = DOC.getTabindex(DOC.SEARCH, "AVANCADA" +tree.p_key, DOCLIST);
        
        out.print("<button tabIndex='" + tab + "' title='"+JSPMessages.getString("docHTML_treeView.3")+"' style='height:20px;width:62px;"+
        (tree.p_textUserQuery==null? "":"color:#990000;") +
        "' onclick=\"winmain().openDocUrl(',800,580','__queryBuilder.jsp','?docid='+getDocId()+'&relatedIDX='+getIDX()+'&object="+
        tree.p_bodef.getName()+"&referenceFrame='+getReferenceFrame()+'&reference="+
        tree.p_key+
        (tree.p_textUserQuery!=null?"&xmlFilter="+ URLEncoder.encode( tree.p_textUserQuery, boConfig.getEncoding() ):"") +"','lookup')\">Avançada</button>");
        
        //'&queryBoui='+qryObject"+tree.p_key+".returnValue+
        out.print("</td>");
     //
     //  out.print(toPrint); 
       
       
       out.print("</tr></table>");
       
       
        out.print("</td></tr></table>");
        
        out.print("</td></tr>\n");

   

   
        out.print("    <tr>\n");
        out.print("	 <td style=\"height:100%;width:100%\">\n");
        out.print("    <div style=\"width:100%;height:100%;overflow-x:scroll\">\n");
		out.print("		   <table style=\"height:100%;width:100%;\" class=\"g_std\" cellSpacing=\"0\" cellPadding=\"0\" >\n");
		out.print("			  <tbody>\n");
        
        
        
        out.print("				  <tr height=\"25\">\n");
		out.print("				    <td>\n");
		out.print("					   <table id=\"g1000_body\"  cellpadding=\"2\" cellspacing=\"0\" style=\"height:25px\" class=\"gh_std\">\n");
		out.print("							<colgroup>\n");
		out.print("							<col style=\"PADDING-LEFT: 10px\" width=30 />\n");
        
        for (int i = 0; i < nrgroups ; i++) 
        {
        out.print("							<col width=13 />\n");    
        }
            
        out.print("							<col width=20 />\n");
        out.print("							<col width=2>\n");
        out.print("							<col>\n");
        
        for (int i = 0; i < tree.p_cols.length; i++) 
        {
              int w=  tree.p_cols[i].getWidth()  ;      
              out.print("							<col width=");out.print( w -2 ); out.print(">\n");
              out.print("							<col width=2>\n");
            
        }
        
		out.print("							<col width=15 />\n");
 		out.print("						    <tbody>\n");
		out.print("								<tr>\n");
        out.print("                               <td class=\"gh_std\"><img src='resources/buildgrid.gif' title='"+JSPMessages.getString("docHTML_treeView.4")+"' onclick='chooseCols(\""+tree.p_key+"\")' height=16 with=16 /> </td>\n");
        out.print("                               <td class=\"gh_std\">&nbsp</td>\n");
        //out.print("             				  <td class='gh_std' width='14'> <img title='Impressão de Listagem (S/ Opções)' onclick=\"winmain().open('__printTree.jsp?docid="+DOC.getDocIdx()+"&viewer="+tree.p_key+"','blank')\" src='templates/grid/std/ghRefresh.gif' width='13' height='13' /> </td>\n");
        for (int i = 0; i < nrgroups ; i++) 
        {
        out.print("                               <td class=\"gh_std\">&nbsp</td>\n");    
        }
        out.print(" 							  <td class=\"ghSep_std\">&nbsp;</td>\n");
		out.print("								  <td colspan=2 id=\"g$ExpanderParent\" class=\"gh_std\"><a title='"+JSPMessages.getString("docHTML_treeView.5")+" : "+tree.p_cols[0].getLabel()+"' relatedTree='"+tree.p_key+"'  class='colHeader' href='javascript:orderCol(\""+tree.p_key+"\",\""+tree.p_cols[0].p_name +"\")' id='");
        out.print( tree.p_cols[0].p_name );
        out.print("'><nobr>");
            
        out.print( tree.getImageSort( tree.p_cols[0].getName() ) + tree.p_cols[0].getLabel() );
        out.print("</nobr></a>");
        
        out.print("</td>\n");
        out.print(" 							  <td class=\"ghSep_std\">&nbsp;</td>\n");
        
        
		for (int i = 1; i < tree.p_cols.length; i++) 
        {
            
            out.print("								  <td class='gh_std' ><a title='"+JSPMessages.getString("docHTML_treeView.5")+" "+tree.p_cols[i].getLabel()+"' relatedTree='"+tree.p_key+"' class='colHeader' href='javascript:orderCol(\""+tree.p_key+"\",\""+tree.p_cols[i].p_name +"\")' id='");
            out.print( tree.p_cols[i].p_name );
            out.print("'><nobr>");
            
            out.print( tree.getImageSort( tree.p_cols[i].getName() ) + tree.p_cols[i].getLabel() );
            out.print("</nobr></a>");    
            out.print("</td>\n");
            out.print(" 							  <td class='ghSep_std'>&nbsp;</td>\n");

            
        }			
      //  out.print("             				  <td class='gh_std' width='14'><img onclick=\"winmain().openDocUrl('small','__exportList.jsp','?docid="+DOC.getDocIdx()+"&form="+tree.p_treeDef.getParentNode().getNodeName()+"&objName="+tree.p_bodef.getName()+"','lookup')\" src='templates/grid/std/ghExport.gif' width='13' height='13' /> </td>\n");
      //      out.print(" 							  <td class='ghSep_std'></td>\n");
    
    
    
    
    
        //refresh
        out.print("             				  <td class='gh_std' width='14'> <img title='Refresh' onclick='reloadGrid()' src='templates/grid/std/ghRefresh.gif' width='13' height='13' /> </td>\n");
        
        //Exportação de Listagem (S/ Opções)
//        out.print("             				  <td class='gh_std' width='14'> <img title='Exportação de Listagem (S/ Opções)' onclick=\"winmain().openDocUrl('fixed,730px,450px','__choseExportList.jsp','?docid="+DOC.getDocIdx()+"&viewer="+tree.p_key+"','lookup')\" src='templates/grid/std/ghRefresh.gif' width='13' height='13' /> </td>\n");

        //Impressão de Listagem (S/ Opções)
        //out.print("             				  <td class='gh_std' width='14'> <img title='Impressão de Listagem (S/ Opções)' onclick=\"winmain().open('__printTree.jsp?docid="+DOC.getDocIdx()+"&viewer="+tree.p_key+"','blank')\" src='templates/grid/std/ghRefresh.gif' width='13' height='13' /> </td>\n");

        //Impressão ou Exportação de Listagens (C/ Opções)
        //out.print("                         <td class='gh_std' width='14'> <img title='Impressão ou Exportação de Listagens (C/ Opções)' onclick=\"winmain().openDocUrl('medium','__chooseExportData.jsp','?docid=" + DOC.getDocIdx() + "&treeKey=" + tree.p_key + "','lookup')\" src='templates/grid/std/ghRefresh.gif' width='13' height='13' /> </td>\n");

        //Gravar Definição de Listagens
        //out.print("             				  <td class='gh_std' width='14'> <img title='Gravar Definição de Listagens' onclick=\"winmain().openDocUrl('fixed,400px,150px,noresize','__saveTree.jsp','?docid="+DOC.getDocIdx()+"&viewer="+tree.p_key+"','lookup')\" src='templates/grid/std/ghRefresh.gif' width='13' height='13' /> </td>\n");
    
    
    out.print("								</tr>\n");
		out.print("							</tbody>\n");
		out.print("						</table>\n");
		out.print("					</td>\n");
		out.print("			    </tr>\n");
        
        //parametros
        if (hParameters)
        {
//        out.print("     <tr height=\"20px\">");
//        out.print("                           <td>");
//        out.print("                           <TABLE style='background-color:#ffffff' height=\"1%\" border=0 cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\">");
//        out.print("                              <colgroup/>");
//         out.print("                              <col/>");
//         out.print("                              <TBODY>");        
//        out.print("                                 <tr height=\"20\">");
//        out.print("                                     <TD noWrap=\"1\" width=\"100%\"><span style='text-decoration:underline' onclick='excludeParam(this);' >Esconder parâmetros</span></TD>");
//        out.print("                                 </tr>");
//        out.print("                              </TBODY>");
//        out.print("                           </TABLE>");
//        out.print("                           </td>");
//        out.print("     </tr>");
        if( !hBlankParameters )
        {
            out.print("     <tr id ='tableParamToExclude' >");
        }
        else
        {
            out.print("     <tr height=\"100%\">");
        }
        out.print("                           <td>");
        out.print("                           <TABLE id='tableParamToExclude2' height=\"100%\" border=0 cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\">");
        out.print("                              <colgroup/>");
        out.print("                              <col width=\"16\"/>");
        out.print("                              <col/>");
        out.print("                              <col width=\"22\"/>");
        out.print("                              <TBODY>");
        out.print("                                 <TR>");
        out.print("                                    <TD colspan=\"3\" valign=\"top\" >");
        out.print("                                       <div width=\"100%\" height=\"100%\">");
         
        String parameters = "object=" + tree.p_bodef.getName() + "&docid=" + DOC.getDocIdx()+"&referenceFrame="+page.getPage().getClass().getName()+"&reference=" + tree.p_key;
         if ( tree.p_bouiUserQuery != -1)
         {
            parameters += "&xmlFilter=" + userquery.userQueryToXML( DOC.getEboContext() , tree.p_bouiUserQuery ,true,tree.p_userParameters ); //retorna um xml com os parametros já definidos
         }

        if(!hBlankParameters )
        {
           out.print("                                          <IFRAME id='params"+tree.p_key + "' src='__queryParams.jsp?" + parameters +"' frameBorder=0 width='100%' height='100%' onload=\"tableParamToExclude.style.height=params"+tree.p_key+".window.parameterZone.offsetHeight;this.style.height=tableParamToExclude.style.height\" scrolling=no height='100%' tabindex='125'></IFRAME>");
        }
        else
        {
            out.print("                                          <IFRAME id='params"+tree.p_key + "' src='__queryParams.jsp?" + parameters +"' frameBorder=0 width='100%' height='100%' scrolling=no height='100%' tabindex='125'></IFRAME>");
        }
        out.print("                                       </div>");
        out.print("                                    </TD>");
        out.print("                                 </TR>");
        out.print("                              </TBODY>");
        out.print("                           </TABLE>");
        out.print("                      </td>");
        out.print("                  </tr>");
    }
    //fim dos parametros
   if(!hParameters || !hBlankParameters )
    {      
		out.print("				<tr>\n");
		out.print(" 				<td>\n");
        out.print("					   <div id=\"grid\" class=\"gContainerLines_std\">\n");
//out.print("<span style='padding:20px;'><span style='padding:20px;background-color:#FFFFFF;color:#CB2226'>Atributo A maior que ahhhh e dioiui diou doioi ioudoioi doioiudoiuoiudouodiu </span></span> " );
        String[] qryG= new String[ nrgroups ];
        String[] aux;
        int lastGroupNumber=99999;
        int groupNumber;
        
        boolean groupIsOpen=true;
        
        boolean parentGroupIsOpen=true;
        
        boolean cancelRender=false;
        boolean renderTitleGroup  =true;
        int  groupControl=999999;
        
        String[] hist=new String[nrgroups];
        
        
        String focusGroup=tree.p_focusGroup;
        int currentLine=0;
        int renderLines=0;
        boolean completePage=false;
        
        if ( nrgroups > 0 )
        {
            int i=0;
            for (; i < line && ! completePage ; i++) 
            {
            
                aux=ctrlLines[ i ].split("-:-");
                String xGroup=aux[0];
                
                String xGroupValue="";
                if ( aux.length>1) xGroupValue=aux[1];
                
                
                groupNumber = ClassUtils.convertToInt(xGroup);
                hist[ groupNumber ] = xGroupValue;
                
                //if ( lastGroupNumber > groupNumber ) groupControl=999999;
                
                if ( lastGroupNumber >= groupNumber && groupNumber<= groupControl ) 
                {
                    
                       groupIsOpen = tree.groupIsOpen(   genKey ( hist,groupNumber ),groupNumber  );
                        if( ! groupIsOpen )
                        {
                            cancelRender=true;
                            renderTitleGroup=true;
                            groupControl=groupNumber;
                        }
                        else
                        {
                         
                            cancelRender=false;
                            renderTitleGroup=true;
                        }
                    
                }
                else 
                {
                    if ( !cancelRender )
                    {
                        groupIsOpen = tree.groupIsOpen(   genKey ( hist,groupNumber ),groupNumber  );
                        if( ! groupIsOpen )
                        {
                            cancelRender=true;
                            renderTitleGroup=true;
                            groupControl=groupNumber;
                        }
                        else
                        {
                         
                            cancelRender=false;
                            renderTitleGroup=true;
                        }
                    }
                    
                }
                 
              //  if ( groupNumber <= groupControl )
              //  {
                        
                        
                       
               // }
             
                if (  renderTitleGroup )
                {
                    String xcor= tree.getGroupColor( ClassUtils.convertToInt( xGroup ), xGroupValue )  ;
                    String xtitle = tree.getGroupStringToPrint( ClassUtils.convertToInt( xGroup ), xGroupValue, DOC  );
                    
                    String xkey="";
                    for (int k = 0; k <= groupNumber  ; k++) 
                    {
                        xkey+=hist[k]+"-";
                    }
                    
                    
                    //Integer counterLines= ( Integer ) groupCnt.get( "G"+ xGroup +"-:-"+ xGroupValue );
                    //if( debug ) logger.finest("G"+ xGroup +"-:-"+ xkey );
                    Integer counterLines= ( Integer ) groupCnt.get( "G"+ xGroup +"-:-"+ xkey );
                    
                    currentLine++;
					int pageCurr=(int) Math.round( (currentLine-1)/tree.p_htmlLinesPerPage+0.5 );
                    if (  pageCurr  ==  tree.p_htmlCurrentPage   )
                    {
                        
                        if ( groupIsOpen)
                        {
                            xtitle="<img title='"+JSPMessages.getString("docHTML_treeView.6")+"' style='cursor:hand' onclick=\"closeGroup('"+tree.p_key+"','"+ genKey(hist,groupNumber) +"')\" src='resources/minus.gif' height=13 width=13 hspace=3 align=absmidle />"+xtitle ;
                        }
                        else
                        {
                            xtitle="<img title='"+JSPMessages.getString("docHTML_treeView.7")+"' style='cursor:hand' onclick=\"openGroup('"+tree.p_key+"','"+ genKey(hist,groupNumber) +"')\"  src='resources/more.gif' height=13 width=13 hspace=3 align=absmidle />"+xtitle;
                        }
                        
                        if ( counterLines != null )
                        {
                            xtitle=xtitle+"<span style='font-size:9px;font-weight:normal' >&nbsp;( "+counterLines.intValue() +" )</span>";
                        }
                        
                        out.print("         <DIV class=headerGroup id='"+genKey(hist,groupNumber)+"' style='COLOR:"+xcor+";padding-left:"+(groupNumber*13)+"px'>"+ xtitle +"</DIV>\n");
                        renderLines++;
                        if ( renderLines >= tree.p_htmlLinesPerPage )
                        {
                            completePage=true;
                        }
                    }
                    
                    
                   
                    if ( groupNumber+1 == nrgroups  && ! cancelRender)
                    {
                        
                        qryG[groupNumber] = xGroupValue;
                        
                        String xsql="";
                        
                         xsql=tree.getSql( DOC.getEboContext() ,qryG );
                                  
                         PreparedStatement pr;
                         ResultSet rslt;
                         pr=cn.prepareStatement( xsql , ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );
                         for (int ip = 0; ip < tree.p_parameters.size() ; ip++) 
                         {
                                pr.setObject(ip+1, (Object)tree.p_parameters.get(ip) );   
                         }
                         long timeI = System.currentTimeMillis();
                         rslt= pr.executeQuery();
                         long timeF = System.currentTimeMillis() - timeI;
                         writeQueryToLog(out, timeF, xsql);
                        
                         int x[] = renderLines( out,tree, rslt , DOC, DOCLIST ,renderLines , currentLine );
                         renderLines=x[0];
                         currentLine=x[1];
                         if ( renderLines >= tree.p_htmlLinesPerPage )
                         {
                            completePage=true;
                         }
                                            
                         rslt.close();
                         pr.close();
                         
                         //groupControl=999999;
                        
                    }
                    else
                    {
                        qryG[groupNumber] = xGroupValue;
                      //  groupControl=999999;
                    }
                              
                }
                lastGroupNumber=groupNumber;
                if( cancelRender) renderTitleGroup=false;
                
                 
            }
            
            docHTML_treeView.renderFooter(  out , tree , i<line || completePage?true:false );
            
        }
        else
        {
            
             PreparedStatement pr;
             ResultSet rslt;

             String xsql= tree.getSql( DOC.getEboContext(), null ) ;
             pr=cn.prepareStatement( xsql , ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );
             for (int ip = 0; ip < tree.p_parameters.size() ; ip++) 
             {
                pr.setObject(ip+1, (Object)tree.p_parameters.get(ip) );   
             }

             pr.setFetchSize( tree.p_htmlLinesPerPage );
             long timeI = System.currentTimeMillis();
             rslt= pr.executeQuery();
             long timeF = System.currentTimeMillis() - timeI;
             writeQueryToLog(out, timeF, xsql);

             int x[] = renderLines( out,tree, rslt , DOC, DOCLIST ,renderLines , currentLine );
             renderLines=x[0];
             currentLine=x[1];
             completePage= ( x[2]==0?true:false);
             
             rslt.close();
             pr.close();
             docHTML_treeView.renderFooter(  out , tree , !completePage );
         
    
        }
        out.print("</div></td></tr></table></div></td></tr></table>\n");
        if ( focusGroup != null )
        {
            
            out.write("<script language='javascript'>try{window.document.getElementById('"+focusGroup+"').focus();document.getElementById('TEXTSEARCH').focus();}catch(e){}</script>" );
            
        }
        else
        {
          out.write("<script language='javascript'>window.setTimeout(\"try{document.getElementById('TEXTSEARCH').focus()}catch(e){} \",100)</script>" );          
        }
     out.print("<!--END -->\n");
    }
     tree.p_haveErrors=false;
     long timeFtotal = System.currentTimeMillis() - timeItotal;
     writeToLog(out, "Tempo total da apresentação", timeFtotal);
    }
    
    public static void renderFooter(  JspWriter out , docHTML_treeRuntime tree , boolean hasMore ) throws java.io.IOException 
    {
     if ( tree.p_htmlCurrentPage > 1 || hasMore)
        {
            out.print("<span class='nav'>");
            out.print(JSPMessages.getString("docHTML_treeView.8"));
            out.print( tree.p_htmlCurrentPage );
            out.print("&nbsp;");
            
            if ( tree.p_htmlCurrentPage > 1 )
             {
                if (tree.p_htmlCurrentPage >2 )  
                out.print("<a class='nav' accessKey='P' href='javascript:treeOperation(\""+tree.p_key+"\", \"firstPage\")'><<"+JSPMessages.getString("docHTML_treeView.9")+"</a>");
                 out.print("<a class='nav' accessKey='A' href='javascript:treeOperation(\""+tree.p_key+"\", \"previousPage\")'><"+JSPMessages.getString("docHTML_treeView.10")+"</a>");
             }
            
            if ( hasMore )
            {
                out.print("<a class='nav' accessKey='X' href='javascript:treeOperation(\""+tree.p_key+"\", \"nextPage\")'>"+JSPMessages.getString("docHTML_treeView.11")+"></a>");
            }
            out.print("</span>");
        }
       //out.print("<p/>"); 
    }
    public static String genKey( String[] hist, int nrgroup )
    {
        String toRet="";
        for (int i = 0; i <= nrgroup ; i++) 
        {
            toRet+="G--"+hist[i].replaceAll("'","").replaceAll("\"","");
        }
        return toRet;
    }
    
    
    
    public static int[] renderLines( JspWriter out ,
                                    docHTML_treeRuntime tree,ResultSet rslt ,
                                    docHTML DOC ,
                                    docHTML_controler DOCLIST ,
                                    int renderLines ,
                                    int currentLine 
                                    ) throws boRuntimeException,java.io.IOException,SQLException
    {
        
        long xt= System.currentTimeMillis();
        StringBuffer headerTable=new StringBuffer();
        boolean alreadyPrintHeader=false;
            
        headerTable.append("<table cellpadding='2' cellspacing='0' style='TABLE-LAYOUT: fixed; MARGIN-BOTTOM: 10px; WIDTH: 100%'>\n");
	    headerTable.append("<colgroup>\n");
		
        for (int i = 0; i < tree.p_groups.length ; i++) 
        {
        headerTable.append("<col width=13 />\n");    
        }
        headerTable.append("<col style='PADDING-LEFT: 10px' width=30 />\n");
        headerTable.append("<col width=20/>\n");
        headerTable.append("<col />\n");
        
        
        
        int nrcols=3;
		for (int z = 1; z < tree.p_cols.length; z++) 
        {
            headerTable.append("<col width=");headerTable.append( tree.p_cols[z].getWidth()  ); headerTable.append(">\n");
            nrcols++;
            
        }			
        
        
        
        boObject obj;
        String xatrname="";
        boDefHandler def;
        
        boolean completePage=false;
        
        
        
        
        
        
        
        
        
        String[] extraCols=tree.p_extraColumns.toString().split(",");
        HashMap extrValues=new HashMap(); 
        
        long[] bouis= new long[50];
        int indice=0;
        long boui;
        int count = 0;
        while ( !completePage && rslt.next() && renderLines <= tree.p_htmlLinesPerPage  )
        {
              currentLine++;
        
              if (  Math.round( (currentLine-1)/tree.p_htmlLinesPerPage+0.5 ) ==  tree.p_htmlCurrentPage   )
              {
                    
                    
                                
                    boui=rslt.getLong(1);
                    if( indice > bouis.length )
                    {
                       bouis = ClassUtils.growLongArray( bouis, 20 );
                    }
                    
                    bouis[indice++]=boui;
                   
                    for (int i = 0; i < extraCols.length ; i++) 
                    {
                         extrValues.put(  extraCols[i]+boui+"-"+count, tree.getExtAtrHTML( rslt ,  extraCols[i]  , DOC ) );
                    }
                    count++;
                    
                    renderLines++;
                    
                    if ( renderLines >= tree.p_htmlLinesPerPage )
                    {
                          completePage=true;
                    }
              }
              
              
        }

        bouis = ClassUtils.setsizeLongArray( bouis , indice );
        boObject.getBoManager().preLoadObjects( DOC.getEboContext(), bouis );
        
        //boleano que indica que é a primeira linha de um objecto 
        boolean firstTime = true;
        
        
        
        
        for(int ib=0 ; ib < bouis.length ; ib++)
        {
             boolean backtrack=false;
             try
             {
                     
              //  currentLine++;
              //  if (  Math.round( currentLine/tree.p_htmlLinesPerPage+0.5 ) ==  tree.p_htmlCurrentPage   )
              //  {
                     
                     if (! alreadyPrintHeader )
                     {
                         out.print( headerTable );
                         alreadyPrintHeader=true;
                     }
                     obj=DOC.getObject( bouis[ib] );
                     def=obj.getBoDefinition();
                     if ( !obj.userReadThis() )
                        out.print("<tr style='font-weight:bold' class=rowGroup boui="+obj.bo_boui+" obj='"+obj.getName()+"' >\n");
                     else
                        out.print("<tr class=rowGroup boui="+obj.bo_boui+" obj='"+obj.getName()+"' >\n");
                     
                     for (int i = 0; i < tree.p_groups.length ; i++) 
                     {
                        out.print("<td>&nbsp;</td>\n");    
                     }
                     
                     //quando já se imprimiu os dados de um objecto, não se volta a imprimir
                     if(firstTime)
                     {
                        out.print("<td><img title='");
                        out.print(def.getLabel() );
                        out.print( "' src='resources/"+def.getName()+"/ico16.gif'") ;
                        out.print(" width=16 height=16 /></td>\n");
                     
                     
                        out.print("<td>");
                     

                          out.print( obj.getICONComposedState() ) ;
                     }
                     else
                     {
                       out.print("<td>&nbsp</td><td>&nbsp");
                     }
                     
                    out.print("</td>\n");
                       
                        for (int z = 0; z < tree.p_cols.length; z++) 
                            {
                                    
                               
                                    //caso para atributos de bridges, para o qual não existe mais valores, ou para atributos normais que já foram impressos
                                    if((!firstTime && !(tree.p_cols[z].p_bridgeInd>-1)) || ((tree.p_cols[z].p_countBr==-1) && !firstTime))
                                    {
                                      out.print("<td>&nbsp</td>");
                                      continue;
                                    }
                                    //quando é a primeira linha de um objecto, a contagem das bridges volat a 0
                                    if(firstTime && (tree.p_cols[z].p_countBr==-1))
                                      tree.p_cols[z].p_countBr=0;
                                      
                                  //  boObject obj1;
                                    out.print("<td>");
                                    
                                    //quando não é a primeira linha de um objecto só se coloca a grid se ouver valores
                                    if(firstTime)
                                      out.print("<span class='gridCell' style='width:"+ (z==0 ?"100%":tree.p_cols[z].getWidth()+"px" )+"'>");
                                    
                                    xatrname=tree.p_cols[z].p_name;   
                                    boolean hasResults=(( docHTML_treeAttribute) tree.getAllAttributes().get( xatrname )).hasResults();
                             
                                    
                                    if ( !hasResults && tree.p_cols[z].p_isAttribute && !tree.p_cols[z].p_isAttributeExternal)
                                    {
                                    
                                        AttributeHandler attr= obj.getAttribute(xatrname);
                                        boDefAttribute xdef= null;
                                    
                                        if ( attr!=null)
                                        {
                                                xdef=attr.getDefAttribute();
                                        }    
                                       
                                        if ( def.hasAttribute(xatrname ))
                                        {
                                        
                                   
                                        
                                            if( xdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                                            {
                                                //obj.getAttribute(xatrname)
                                                long[] values=new long[0];
                                                if ( xdef.getRelationType()== boDefAttribute.RELATION_1_TO_1 )
                                                {
                                                    long v=attr.getValueLong();
                                                    if ( v >0 )
                                                    {
                                                        values=new long[1];
                                                        values[0]=v;
                                                    }
                                                }
                                                else if ( xdef.getRelationType()== boDefAttribute.RELATION_MULTI_VALUES )
                                                {
                                                   values=attr.getValuesLong();
                                                    
                                                }
                                                //if necessário devido a bug no get values long
                                                else if ( xdef.getRelationType()>1)
                                                {
                                                    //contem os bouis dos objectos da bridge
                                                    String bouisStr[] = attr.getValueString().split(";");
                                                    
                                                    if((bouisStr.length==0) || (bouisStr[0]==null) || (bouisStr[0].equals("")))
                                                    {
                                                      values=null;
                                                    }
                                                    else
                                                    {
                                                      long bouiBo = 0;
                                                     
                                                     //caso seja a primeira vez carregar os objectos
                                                      if(firstTime)
                                                      {
                                                        values = new long[bouisStr.length];
                                                        for (int iv = 0; iv < bouisStr.length; iv++) 
                                                        {
                                                          values[iv]=Long.parseLong(bouisStr[iv]);
                                                        }
                                                        if(values.length>0)
                                                        {
                                                          boObject.getBoManager().preLoadObjects( DOC.getEboContext(), values );
                                                          bouiBo = values[tree.p_cols[z].p_countBr];
                                                        }
                                                      }
                                                      else
                                                      {
                                                        bouiBo = Long.parseLong(bouisStr[tree.p_cols[z].p_countBr]);
                                                      }
                                                      
                                                      //testar se existem mais atributos da bridge mas não existem mais bouis deste objecto
                                                      if(tree.p_cols[z].p_countBr<(bouisStr.length-1))
                                                      {
                                                        backtrack=true;
                                                      }
                                                      
                                                      if(bouiBo>0)
                                                      {
                                                          values = new long[1];
                                                          values[0]=bouiBo;
                                                          
                                                          tree.p_cols[z].p_countBr++;
         
                                                          //testar se a bridge tem mais elementos
                                                          if( tree.p_cols[z].p_countBr == bouisStr.length)
                                                            tree.p_cols[z].p_countBr=-1;
                                                      }
                                                    }
                                                    
                                                }
                                                if ( values !=null && values.length  > 0) 
                                                {
                                                    //como não e a primeira vez e existem dados, imprime--se a grid
                                                    if(!firstTime)
                                                      out.print("<span class='gridCell' style='width:"+ (z==0 ?"100%":tree.p_cols[z].getWidth()+"px" )+"'>");
                                                    
                                                    
                                                    out.print("<div style='overflow:hidden'>");
                                                    
                                                    for ( int j=0; j < values.length ; j++ )
                                                    {
                                                        
                                                        boObject o = DOC.getObject( values[j] );
                                                        out.print("<span class='lui' onclick=\"");
                                                        out.print("winmain().openDoc('medium','");
                                                        out.print( o.getName().toLowerCase() );
                                                        out.print("','edit','method=edit&boui=");
                                                        out.print( values[j] );
                                                        out.print("')");
                                                        out.print(";event.cancelBubble=true\"");
                                                        out.print(" boui='");
                                                        out.print( values[j] );
                                                        out.print("' object='");
                                                        out.print( o.getName() );
                                                        out.print("'>");
                                                        out.print(o.getCARDID());
                                                        out.print("</span>");
                                                   }
                                                    
                                                    out.print( "</div>");
                                                }
                                                 else out.print("&nbsp");
                                                
                                            }
                                            else
                                            {
                                                String v=attr.getValueString();
                                                if( xdef.getType().equalsIgnoreCase("DATETIME") && v.length()>0 )
                                                {
                                                    v=v.replace('T',' ');
                                                    v=v.substring(0,v.length()-3);
                                                }
                                                
                                                if(!firstTime)
                                                  out.print("<span class='gridCell' style='width:"+ (z==0 ?"100%":tree.p_cols[z].getWidth()+"px" )+"'>");
                                                // mostrar descrição da lov e não o valor
                                                String lovname=xdef.getLOVName();
                                                if(lovname!=null && !lovname.equalsIgnoreCase(""))
                                                 v=boObjectUtils.getLovDescription(DOC.getEboContext(),lovname,v);
												 
												 //substitui os boleanos por Sim ou Não
                                                if(xdef.getType().equalsIgnoreCase("boolean"))
                                                {
                                                  if(v.equals("1"))
                                                    v=JSPMessages.getString("docHTML_treeView.12");
                                                  else if(v.equals("0"))
                                                      v=JSPMessages.getString("docHTML_treeView.13");
                                                  else
                                                    v="";
                                                }

                                                out.print(v);
                                            }
                                            
                                        }
                                        
                                    }
                                    else
                                    {
                                        //extension attribute
                                        String value = "";
                                        
                                        //testar se o atributo está dependente de uma bridge
                                        if(tree.p_cols[z].p_bridgeInd>-1)
                                        {
                                          for (int ic = 0; ic <tree.p_cols.length ; ic++) 
                                          {
                                            //caso esteja
                                            if((tree.p_cols[z].p_bridgeInd == tree.p_cols[ic].p_bridgeInd) && (tree.p_cols[ic].p_defatr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) && (tree.p_cols[ic].p_defatr.getRelationType()>1))
                                            {

                                                //irá conter o caminho necessário para chegar ao atributo pretendido
                                                String[] path = tree.p_cols[z].p_name.split("\\.");
                                                
                                                boObject curObj = obj;
                                                AttributeHandler finalAtt = null;
                                                
                                                //pecorrer o atributo do qual depende até chegar ao pretendido
                                                int p;
                                                for (p = 0; p < path.length-1; p++) 
                                                {
                                                  AttributeHandler attCur = curObj.getAttribute(path[p]);
                                                  
                                                  //caso o atributo actual seja uma bridge
                                                  if(attCur.getDefAttribute().getRelationType()>1)
                                                  {
                                                    bridgeHandler curBr = curObj.getBridge(path[p]);
                                                    curBr.beforeFirst();
                                                    
                                                    //testar se abridge tem objectos
                                                    if(!curBr.next())
                                                    {
                                                      value="&nbsp";
                                                      break;
                                                    }
                                                    
                                                    //percorrer a bridge até ao objecto pretendido
                                                    for (int b = 0; b < tree.p_cols[z].p_countBr; b++)
                                                      curBr.next();
                                                    
                                                    //testar se a bridge contem o atributo seguinte
                                                    if(curBr.getAttribute(path[p+1])!=null)
                                                    {
                                                      //testar se o atributo seguinte é o pretendido
                                                      if( p+1 == path.length-1)
                                                        finalAtt = curBr.getAttribute(path[p+1]);
                                                      else
                                                       curObj = curBr.getAttribute(path[p+1]).getObject(); 
                                                    }
                                                    else //caso não contenha pssa-se para o objecto seguinte
                                                      curObj=curBr.getObject();
                                                    
                                                  }
                                                  else//caso não seja uma bridge passa-se para o objecto seguinte
                                                  {
                                                    curObj=curObj.getAttribute(path[p]).getObject();
                                                  }
                                                }
                                                
                                                //se não se percorreu o ciclo todo é porque não exitem dados
                                                if(p!=path.length-1)
                                                  break;
                                                
                                                if(finalAtt==null)
                                                {
                                                  finalAtt = curObj.getAttribute(path[path.length-1]);
                                                }
                                                
                                                if(finalAtt.getDefAttribute().getAtributeType() == finalAtt.getDefAttribute().TYPE_OBJECTATTRIBUTE)
                                                {
                                                  boObject objVal = finalAtt.getObject();
                                                  if(objVal!=null)
                                                    value = objVal.getCARDID().toString();
                                                  else
                                                    value = "&nbsp";
                                                }
                                                else
                                                {
                                                  String valAux = finalAtt.getValueString();
                                                  if (value!=null)
                                                    value = valAux;
                                                  else
                                                    value = "&nbsp";
                                                }
                                                
                                                tree.p_cols[z].p_countBr++;
                                                break;
                                            }
                                          }
                                        }
                                        else
                                        {
                                          value=null;
                                        }
										
                                        if(value==null)
                                        {
                                          if(xatrname.indexOf('.')>-1) 
                                          {
                                              xatrname=xatrname.replaceAll("\\.","\\$") ;
                                          }
                                          value = extrValues.get(xatrname+bouis[ ib ]+"-"+ib).toString();
                                        }
                                        
                                        if(!firstTime && !value.equals("&nbsp"))
                                          out.print("<span class='gridCell' style='width:"+ (z==0 ?"100%":tree.p_cols[z].getWidth()+"px" )+"'>");
										String lovname=null;
                                        if(tree.p_cols[z].p_defatr!=null)
										{
                                          lovname=tree.p_cols[z].p_defatr.getLOVName();
										  //substitui os boleanos por Sim ou Não
                                          if(tree.p_cols[z].p_defatr.getType().equalsIgnoreCase("boolean"))
                                          {
                                            if(value.equals("1"))
                                              value="Sim";
                                            else if(value.equals("0"))
                                              value="Não";
                                            else
                                              value="";
                                          }
                                          
                                        }
                                        if(lovname!=null && !lovname.equalsIgnoreCase(""))
                                         value=boObjectUtils.getLovDescription(DOC.getEboContext(),lovname,value);
                                        out.print( value );
                                        //out.print(tree.getExtAtrHTML( rslt , xatrname, DOC ));
                                    }
                                out.print("</span></td>");
                               
                            }
                            
                     out.print("</tr>\n");
                     
                     //se o proximo não objecto é o igual ao actual, ou não acabou a listagem
                     if(ib==bouis.length-1)
                     {
                       if(backtrack)
                         ib--;
                       firstTime=false;
                     }
                     else if(bouis[ib]!=bouis[ib+1])
                     {
                       if(backtrack)
                       {
                         ib--;
                        firstTime=false;
                       }
                       else
                        firstTime=true;
                     }
                     else
                     {
                      firstTime=false;
                     }


                     //marcar atributos dependentes de bridges que já não tem mais dados
                      for (int ic = 0; ic < tree.p_cols.length; ic++) 
                      {
                        if(firstTime)
                        {
                          tree.p_cols[ic].p_countBr = 0;   
                        }
                        else if(tree.p_cols[ic].p_countBr == -1)
                        {
                          for (int jc = 0; jc < tree.p_cols.length; jc++)
                          {
                            if(tree.p_cols[jc].p_bridgeInd == tree.p_cols[ic].p_bridgeInd)
                            {
                              tree.p_cols[jc].p_countBr = -1;
                            }
                          }
                        }
                        
                      }
                      
                      
                      //devido a um bug do boql, que coloca o numero de objectos conforme o numero da soma de elementos das várias bridges
                      //é preciso saltar os os objectos que já foram impressos, e para o qual não exitem mais bridges para imprimir
                      boolean invCol = true;
                      for (int ic = 0; ic < tree.p_cols.length && !firstTime; ic++) 
                      {
                        if((tree.p_cols[ic].p_bridgeInd>-1) && (tree.p_cols[ic].p_countBr==-1))
                          invCol&=true;
                        else if(tree.p_cols[ic].p_bridgeInd>-1)
                          invCol&=false;
                      }
                      if(invCol && !firstTime)
                      {
                         while(((ib+1)<bouis.length) && (bouis[ib]==bouis[ib+1]))
                          ib++;
                          firstTime=true;
                      }
                      //---
                
             }
             catch (Exception e)
             {
                     if (! alreadyPrintHeader )
                     {
                         out.print( headerTable );
                         alreadyPrintHeader=true;
                     }
                     out.print("<tr>");
                     out.print("<td class='none' colspan="+nrcols+" >"+e.getMessage()+"</td>\n");
                     out.print("</tr>\n"); 
                 
             }
            
        }
        if (alreadyPrintHeader )
        {
            out.print("</table>\n");    
        }
        
     
    int toRet[] = new int[3];
    toRet[0]=renderLines;
    toRet[1]=currentLine;
    toRet[2]=rslt.next()?1:0;
    logger.finer( String.valueOf(System.currentTimeMillis() -xt));
    return toRet;    
    }
    private static void writeQueryToLog(JspWriter out, long time, String query)throws IOException {
        String t = (float)(Math.round((float)(time)/100f))/10f +"s";
        out.print("<!--Tempo da query(" + t +"): " + query+" -->");

    }
    private static void writeToLog(JspWriter out, String s, long time)throws IOException {
        String t = (float)(Math.round((float)(time)/100f))/10f +"s";
        out.print("<!--" +s +": "+ t +" -->");

    }
 }