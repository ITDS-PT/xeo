/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.components;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.presentation.render.HTMLCommon;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.ClassColumn;
import netgest.bo.presentation.render.elements.ColumnProvider;
import netgest.bo.presentation.render.elements.Explorer;
import netgest.bo.presentation.render.elements.Tree;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectUtils;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.security.securityOPL;
import netgest.bo.security.securityRights;

import netgest.utils.ClassUtils;

import netgest.bo.system.Logger;
import xeo.client.business.helper.RegistryHelper;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class TreeBuilder {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.presentation.render.ie.components.TreeBuilder");
    private static final char[] TREE_BEGIN = "<!--BEGIN TREE-->\n".toCharArray();
    private static final char[] TREE_FRAME_HELPER_1 = "<iframe style='display:none;height:50px' id='treeHelper".toCharArray();
    private static final char[] TREE_FRAME_HELPER_2 ="' src=\"__treeHelper.jsp?docid=".toCharArray();
    private static final char[] TREE_FRAME_HELPER_3 = "&explorer_key=".toCharArray();
    private static final char[] TREE_FRAME_HELPER_4 = "\"></iframe>".toCharArray();
    
    private static final char[] TREE_TABLE = "<table cellSpacing=\"0\" cellPadding=\"0\" class=\"layout\">\n".toCharArray();
    private static final char[] TREE_TABLE_1 = "<tr height=\"22\">\n".toCharArray();
    private static final char[] TREE_TABLE_2 = "<td><div id=\"head\" style=\"width:100%;height:100%;overflow:hidden\">".toCharArray();
    private static final char[] TREE_TABLE_3 = "<table  cellSpacing=\"0\" cellPadding=\"0\">".toCharArray();
	private static final char[] TREE_TABLE_4 = "<tbody><tr><td valign=\"top\">".toCharArray();
	private static final char[] TREE_TABLE_5 = "<table cellpadding=\"0\" cellspacing=\"0\" style=\"height:22px\" class=\"gh_std\">".toCharArray();
	private static final char[] TREE_TABLE_6 = "<colgroup>".toCharArray();
	private static final char[] TREE_TABLE_7 = "<col style=\"PADDING-LEFT: 10px\" width=\"30\" />".toCharArray();
	private static final char[] TREE_TABLE_8 = "<col width=\"13\"/>".toCharArray();
	private static final char[] TREE_TABLE_9 = "<col width=\"20\"/>".toCharArray();
	private static final char[] TREE_TABLE_10 = "<col width=\"2\"/>".toCharArray();
	private static final char[] TREE_TABLE_11 = "<col width=\"100%\"/>".toCharArray();
    private static final char[] TREE_TABLE_12 = "<col width=".toCharArray();
    private static final char[] TREE_TABLE_13 = ">\n".toCharArray();
    private static final char[] TREE_TABLE_14 = "<col width=2>\n".toCharArray();
    private static final char[] TREE_TABLE_15 = "<col width=15 />\n".toCharArray();
    
    private static final char[] TREE_TABLE_16 = "<tbody>\n".toCharArray();
    private static final char[] TREE_TABLE_17 = "<tr>\n".toCharArray();
    private static final char[] TREE_TABLE_18 = "<td class=\"gh_std\"><img src='resources/buildgrid.gif' title='Clique aqui para definir acrescentar ou remover colunas' onclick='explorerChooseCols(\"".toCharArray();
    private static final char[] TREE_TABLE_19 = "\")' height=16 with=16 /> </td>\n".toCharArray();
    // Sem a possibilidade de escolher colunas
    private static final char[] TREE_TABLE_18_1 = "<td class=\"gh_std\"></td>\n".toCharArray();

    
    private static final char[] TREE_TABLE_20 = "<td class=\"gh_std\">&nbsp</td>\n".toCharArray();

    //    private static final char[] TREE_TABLE_21 = "                               <td class=\"gh_std\">&nbsp</td>\n".toCharArray();
    private static final char[] TREE_TABLE_22 = "<td class=\"ghSep_std\">&nbsp;</td>\n".toCharArray();
    
    private static final char[] TREE_TABLE_23 = "<td colspan=2 id=\"g$ExpanderParent\" class=\"gh_std\"><a title='Clique para ordenar por : ".toCharArray();
    private static final char[] TREE_TABLE_24 = "' relatedTree='".toCharArray();
    private static final char[] TREE_TABLE_25 = "'  class='colHeader' href='javascript:orderExplorerCol(\"".toCharArray();
    private static final char[] TREE_TABLE_26 = "\",\"".toCharArray();
    private static final char[] TREE_TABLE_27 = "\")' id='".toCharArray();
    private static final char[] TREE_TABLE_28 = "'><nobr>".toCharArray();
    private static final char[] TREE_TABLE_23_1 = "<td colspan=2 id=\"g$ExpanderParent\" class=\"gh_std\">".toCharArray();
    
    private static final char[] TREE_TABLE_29 = "</nobr></a>".toCharArray();
    private static final char[] TREE_TABLE_30 = "</td>\n".toCharArray();
    private static final char[] TREE_TABLE_31 = "<td class=\"ghSep_std\">&nbsp;</td>\n".toCharArray();
    private static final char[] TREE_TABLE_32 = "<td class='gh_std' ><a title='Clique para ordenar por : ".toCharArray();
    private static final char[] TREE_TABLE_32_1 = "<td class='gh_std' >".toCharArray();

    private static final char[] TREE_TABLE_33 = "<td class='ghSep_std'>&nbsp;</td>\n".toCharArray();
    private static final char[] TREE_TABLE_34 = "<td class='gh_std' width='14'> <img title='Refresh' onclick='refreshExplorer()' src='templates/grid/std/ghRefresh.gif' width='13' height='13' /> </td>\n".toCharArray();
    private static final char[] TREE_TABLE_CLOSE_HEADER ="</tr></tbody></table></td></tr></tbody></table></div></td></tr>".toCharArray();
     					
    private static final char[] TREE_TABLE_35 =
    "<tr><td class=\"layout\"><div onscroll=\"document.getElementById('head').scrollLeft=this.scrollLeft\" style=\"width:100%;height:100%;overflow:scroll\"><table style=\"height:100%;width:100%\" cellSpacing=\"0\" cellPadding=\"0\"><tbody><tr><td valign=\"top\" style=\"background-Color:#ffffff;height:100%;width:100%;\">".toCharArray(); 
    
 /*   
     <tr>
		 <td style="height:100%;width:100%">		 
		 	     <div onscroll="document.getElementById('xxx').scrollLeft=this.scrollLeft" style="width:100%;height:100%;overflow:scroll">
					<table style="height:100%;width:100%" cellSpacing="0" cellPadding="0">
						<tbody>
							<tr>
 							<td valign="top" style="background-Color:#ffffff;height:100%;width:100%;">
                            
    */
    
 
    private static final char[] TREE_PAGEC_1 = "<div class='footerGroup' style='padding-left:".toCharArray();
    private static final char[] TREE_PAGEC_2 = "px'>".toCharArray();
    private static final char[] TREE_PAGEC_3 = "</div>".toCharArray();
    
    private static final char[] TREE_PAGE_1 = "<span class='navGroup' onclick=\"openPageExplorerGroup('".toCharArray();
    private static final char[] TREE_PAGE_2 = "','".toCharArray();
    private static final char[] TREE_PAGE_3 = "')\">Próximos registos</span>".toCharArray();
    
    
    private static final char[] TREE_PAGE_4 = "')\">Registos anteriores</span>".toCharArray();
    
    
    private static final char[] TREE_TABLE_36 = "<img title='Clique para fechar grupo' style='cursor:hand' onclick=\"closeExplorerGroup('".toCharArray();
    private static final char[] TREE_TABLE_37 = "','".toCharArray();
    private static final char[] TREE_TABLE_38 = "')\" src='resources/minus.gif' height=13 width=13 hspace=3 align=absmidle />".toCharArray();
    private static final char[] TREE_TABLE_39 = "<img title='Clique para expandir grupo' style='cursor:hand' onclick=\"openExplorerGroup('".toCharArray();
    private static final char[] TREE_TABLE_40 = "')\"  src='resources/more.gif' height=13 width=13 hspace=3 align=absmidle />".toCharArray();
    private static final char[] TREE_TABLE_41 = "<span style='font-size:9px;font-weight:normal' >&nbsp;( ".toCharArray();
    private static final char[] TREE_TABLE_42 = " )</span>".toCharArray();
    private static final char[] TREE_TABLE_43 = "         <DIV class=headerGroup id='".toCharArray();
    private static final char[] TREE_TABLE_44 = "' style='COLOR:".toCharArray();
    private static final char[] TREE_TABLE_45 = ";padding-left:".toCharArray();
    private static final char[] TREE_TABLE_46 = "px'>".toCharArray();
    private static final char[] TREE_TABLE_47 = "</DIV>\n".toCharArray();
    
    //private static final char[] TREE_TABLE_48 = "</div></td></tr></table></div></td></tr></table>\n".toCharArray();
    
    private static final char[] TREE_TABLE_48 = "</td></tr></tbody></table></div><!-- end div grid x --></td>	</tr></table>".toCharArray();

    private static final char[] TREE_TABLE_49 = "<script language='javascript'>try{window.document.getElementById('".toCharArray();
    private static final char[] TREE_TABLE_50 = "').focus();document.getElementById('TEXTSEARCH').focus();}catch(e){}</script>".toCharArray();
    private static final char[] TREE_TABLE_51 = "<script language='javascript'>window.setTimeout(\"try{document.getElementById('TEXTSEARCH').focus()}catch(e){} \",100)</script>".toCharArray();
    private static final char[] TREE_TABLE_52 = "Tempo total da apresentação".toCharArray();
    private static final char[] TREE_TABLE_53 = "<!--".toCharArray();
    private static final char[] TREE_TABLE_54 = ": ".toCharArray();
    private static final char[] TREE_TABLE_55 = " -->".toCharArray();
    private static final char[] TREE_FOOTER_1 = "<span class='nav'>".toCharArray();
    private static final char[] TREE_FOOTER_2 = " Página  ".toCharArray();
    private static final char[] TREE_FOOTER_3 = "<a class='nav' accessKey='P' href='javascript:explorerOperation(\"".toCharArray();
    private static final char[] TREE_FOOTER_4 = "\", \"firstPage\")'><u>P</u>rimeira</a>".toCharArray();
    private static final char[] TREE_FOOTER_5 = "<a class='nav' accessKey='A' href='javascript:explorerOperation(\"".toCharArray();
    private static final char[] TREE_FOOTER_6 = "\", \"previousPage\")'><< <u>A</u>nterior</a>".toCharArray();
    private static final char[] TREE_FOOTER_7 = "<a class='nav' accessKey='X' href='javascript:explorerOperation(\"".toCharArray();
    private static final char[] TREE_FOOTER_8 = "\", \"nextPage\")'>Pró<u>x</u>ima >></a>".toCharArray();
    private static final String TREE_RPL_1 = "G--";
    private static final String TREE_RPL_2 = "'";
    private static final String TREE_RPL_3 = "";
    private static final String TREE_RPL_4 = "\"";
    private static final String NBSP = "&nbsp";
    private static final String TREE_LINES_1 = "<table blocktype='lines' cellpadding='2' cellspacing='0' style='TABLE-LAYOUT: fixed; MARGIN-BOTTOM: 10px; WIDTH: 100%'>\n";
    private static final String TREE_LINES_2 = "<colgroup>\n";
    private static final String TREE_LINES_3 = "<col width=13 />\n";
    private static final String TREE_LINES_4 = "<col style='PADDING-LEFT: 10px' width=30 />\n";
    private static final String TREE_LINES_5 = "<col width=20/>\n";
    private static final String TREE_LINES_6 = "<col width=100%/>\n";
    private static final String TREE_LINES_7 = "<col width=";

    private static final char[] TREE_LINES_8 = "<tr class='rowgroup' style='font-weight:bold' onclick=\"showOnPreview(".toCharArray();
    private static final char[] TREE_LINES_8_1 = ");\" ".toCharArray();
//    private static final char[] TREE_LINES_8_2 = "ondblclick=\"doubleClick(".toCharArray();
    private static final char[] TREE_LINES_8_2 = "ondblclick=\"".toCharArray();
    private static final char[] TREE_LINES_8_3 = "\"".toCharArray();
    private static final char[] TREE_LINES_8_4 = " boui=".toCharArray();
    private static final char[] TREE_LINES_9 = " obj='".toCharArray();
    private static final char[] TREE_LINES_10 = "' >\n".toCharArray();

    private static final char[] TREE_LINES_11 = "<tr class='rowgroup' onclick=\"showOnPreview(".toCharArray();
    private static final char[] TREE_LINES_11_1 = ");\" ".toCharArray();
//    private static final char[] TREE_LINES_11_2 = "ondblclick=\"doubleClick(".toCharArray();
    private static final char[] TREE_LINES_11_2 = "ondblclick=\"".toCharArray();
    private static final char[] TREE_LINES_11_3 = "\"".toCharArray();
    private static final char[] TREE_LINES_11_4 = " boui=".toCharArray();
    
    //    private static final char[] TREE_LINES_12 = " obj='".toCharArray();
    private static final char[] TREE_LINES_13 = "<td>&nbsp;</td>\n".toCharArray();
    private static final char[] TREE_LINES_14_1 = "<input type=\"checkbox\" ".toCharArray();
    private static final char[] TREE_LINES_14_2 = " onclick=\"".toCharArray();
    private static final char[] TREE_LINES_14_3 = "\">".toCharArray(); 
    private static final char[] TREE_LINES_14 = "<td><img title='".toCharArray();
    private static final char[] TREE_LINES_15 = "' src='".toCharArray();
    private static final char[] TREE_LINES_16 = "' width=16 height=16 /></td>\n".toCharArray();

    //    private static final char[] TREE_LINES_17 = " ".toCharArray();
    private static final char[] TREE_LINES_18 = "<td>&nbsp</td><td>&nbsp".toCharArray();
    private static final char[] TREE_LINES_19 = "<span class='gridCell' style='width:".toCharArray();
    private static final char[] TREE_LINES_20 = "100%".toCharArray();
    private static final char[] TREE_LINES_21 = "px".toCharArray();
    private static final char[] TREE_LINES_22 = "'>".toCharArray();
    private static final char[] TREE_LINES_23 = "<div style='overflow:hidden'>".toCharArray();
    private static final char[] TREE_LINES_24 = "<span class='lui' onclick=\"".toCharArray();
    private static final char[] TREE_LINES_25 = "winmain().openDoc('medium','".toCharArray();
    private static final char[] TREE_LINES_26 = "','edit','method=edit&boui=".toCharArray();
    private static final char[] TREE_LINES_27 = "')".toCharArray();
    private static final char[] TREE_LINES_28 = ";event.cancelBubble=true\"".toCharArray();
    private static final char[] TREE_LINES_29 = " boui='".toCharArray();
    private static final char[] TREE_LINES_30 = "' object='".toCharArray();
    private static final String TREE_LINES_31 = "DATETIME";
    private static final char[] TREE_LINES_32 = "<td class='none' colspan=".toCharArray();
    private static final char[] TREE_ID_PARAM = "id ='tableParamToExclude' ".toCharArray();
    private static final char[] TREE_HEIGHT_100 = "height =\"100%\" ".toCharArray();
    private static final char[] TREE_FIRST_COL = "<TD colspan=\"2\">".toCharArray();
    private static final char TIME_T = 'T';
    private static final char EMPTY = ' ';
    private static final String BOOLEAN = "boolean";
    private static final String ONE = "1";
    private static final String SIM = "Sim";
    private static final String NAO = "Não";
    private static final String EMTY_STR = "";
    private static final String POINT = "\\.";
    private static final String CIFRAO = "\\$";
    private static final String TREE_SIGN_1 = "-:-";
    private static final String TREE_LETTER_G = "G";
    private static final String TREE_MINUS = "-";
    private static final String TREE_QRY_OBJ_ = "qryObject_";
    private static final String TREE_QRY_OBJ__ = "qryObject__";
    private static final char[] TREE_END = "<!--END -->\n".toCharArray();
    private static final String TREE_ZERO = "0";

    /**
     *
     * @Company Enlace3
     * @since
     */
    public TreeBuilder() {
    }

    public static void writeTree(PrintWriter out, Tree elementTree,
        docHTML doc, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException, SQLException {
        
        long timeItotal = System.currentTimeMillis();

        if (elementTree.getCode() != null) {
            out.write(elementTree.getCode().toCharArray());

            return;
        }

        Explorer tree = elementTree.getExplorer();
        tree.p_haveErrors = true;

        boolean hParameters = tree.haveParameters(doc.getEboContext());
        boolean hBlankParameters = tree.haveBlankParameters(doc.getEboContext());

        if (!hBlankParameters && hParameters) {
            if (!tree.analizeSql(doc.getEboContext())) {
                hBlankParameters = true;
            }
        }

        tree.ctrlLines = new String[500];
        int line = 0;
        int nrgroups = 0;
        tree.groupCnt = new Hashtable();
        Connection cn = null;

        if (!hBlankParameters) {
            
            String sqlGroups = tree.getSqlGroups(doc.getEboContext());

            cn = doc.getEboContext().getConnectionData();

            if (sqlGroups != null) {
                //Connection cn= doc.getEboContext().getConnectionData();
                PreparedStatement pr = null;
                ResultSet rslt = null;
                
                try
                {
                    nrgroups = tree.getGroupProvider().groupSize();
                    pr = cn.prepareStatement(sqlGroups,
                            ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    
                    for (int ip = 0; tree.p_parameters != null && ip < tree.p_parameters.size(); ip++) {
                        pr.setObject(ip + 1, (Object) tree.p_parameters.get(ip));
                    }
    
                    long timeI = System.currentTimeMillis();
                    rslt = pr.executeQuery();
                    long timeF = System.currentTimeMillis() - timeI;
                    writeQueryToLog(out, timeF, sqlGroups);
    
                    String[] vg = new String[nrgroups];
                    int[] vl = new int[nrgroups];
    
                    boolean haveResults = rslt.next();
    
                    if (haveResults)
                    {
                    
                        for (byte i = 0; i < nrgroups; i++) {
                            vg[i] = tree.getGroupValue(doc.getEboContext(), rslt, i + 1);
                            tree.ctrlLines[line++] = i + TREE_SIGN_1 + vg[i];
                             if (line > ( tree.ctrlLines.length - 1)) {
                                            tree.ctrlLines = ClassUtils.growStringArray(tree.ctrlLines,
                                                    200);
                                        }
                        }
    
                        do {
                            for (byte i = 0; i < nrgroups; i++) {
                                String xv = tree.getGroupValue(doc.getEboContext(), rslt, i + 1);
    
                                int counter = rslt.getInt(nrgroups + 1);
    
                                if (!xv.equals(vg[i])) {
                                    for (int z = i; z < nrgroups; z++) {
                                        xv = tree.getGroupValue(doc.getEboContext(), rslt, z + 1);
                                        tree.ctrlLines[line++] = z + TREE_SIGN_1 + xv;
    
                                        if (line > ( tree.ctrlLines.length - 1)) {
                                            tree.ctrlLines = ClassUtils.growStringArray(tree.ctrlLines,
                                                    200);
                                        }
                                    }
    
                                    for (int z = i; z < nrgroups; z++) {
                                        String xkey = "";
    
                                        for (byte k = 0; k <= z; k++) {
                                            xkey += (vg[k] + TREE_MINUS);
                                        }
    
                                        //    logger.finest( TREE_LETTER_G+z+TREE_SIGN_1+xkey+" |-->"+vl[z] );
                                        tree.groupCnt.put(TREE_LETTER_G + z +
                                            TREE_SIGN_1 + xkey, new Integer(vl[z]));
    
                                        //groupCnt.put( TREE_LETTER_G+z+TREE_SIGN_1+vg[z] , new Integer( vl[z] ) ); 
                                        vl[z] = counter;
                                    }
    
                                    break;
                                } else {
                                    vl[i] += counter;
                                }
                            }
    
                            for (int i = 0; i < nrgroups; i++)
                            {
                                vg[i] = tree.getGroupValue(doc.getEboContext(), rslt, i + 1);
                            }
                        } while (rslt.next());
    
                        for (int j = 0; j < nrgroups; j++)
                        {
                            String xkey = "";
    
                            for (byte k = 0; k <= j; k++) {
                                xkey += (vg[k] + TREE_MINUS);
                            }
    
                            tree.groupCnt.put(TREE_LETTER_G + j + TREE_SIGN_1 + xkey,
                                new Integer(vl[j]));
                        }
                    }
                    tree.ctrlLine = line;   
                }
                catch(Exception e)
                {
                    logger.severe(e);
                    
                    logger.severe(LoggerMessageLocalizer.getMessage("ERROR_IN_EXPLORER")+": " + tree.getKey() + " - " + tree.getExplorerName() + " "+LoggerMessageLocalizer.getMessage("USER")+": " + tree.getUser() + " SQL: " + sqlGroups);
                    throw new boRuntimeException("TreeBuilder", "writeTree", e);
                }
                finally
                {
                    try{if(rslt != null){rslt.close();}}catch(Exception _e){}
                    try{if(pr != null){pr.close();}}catch(Exception _e){}
                }
            }            
        }

        // escrever HEADER
        
        out.write(TREE_BEGIN);

        out.write( TREE_FRAME_HELPER_1  );
        out.write( tree.getKey().toCharArray() );
        out.write( TREE_FRAME_HELPER_2  );
        out.write(  String.valueOf(doc.getDocIdx()).toCharArray() );
        out.write( TREE_FRAME_HELPER_3  );
        out.write( tree.getKey().toCharArray() );
        out.write( TREE_FRAME_HELPER_4  );
        
        out.write(TREE_TABLE);
        out.write(TREE_TABLE_1);
        out.write(TREE_TABLE_2);
        out.write(TREE_TABLE_3);
        out.write(TREE_TABLE_4);
        out.write(TREE_TABLE_5);
        out.write(TREE_TABLE_6);
        out.write(TREE_TABLE_7);
       
        for (int i = 0; i < nrgroups; i++) 
        {
            out.write(TREE_TABLE_8);
        }
        

        out.write(TREE_TABLE_9);
        out.write(TREE_TABLE_10);
        out.write(TREE_TABLE_11);


        for (int i = 0; i < tree.getColumnsProvider().columnsSize(); i++) {
            int w = tree.getColumnsProvider().getColumn(i).getWidth();
            out.write(TREE_TABLE_12);
            out.write(String.valueOf((w - 2)).toCharArray());
            out.write(TREE_TABLE_13);
            out.write(TREE_TABLE_14);
        }

        out.write(TREE_TABLE_15);
        
        out.write(TREE_TABLE_16);
        out.write(TREE_TABLE_17);
        
        if( tree.getExplorerOptions().isChooseColumnsOptionVisible() ) {
        out.write(TREE_TABLE_18);
        out.write(tree.p_key.toCharArray());
        out.write(TREE_TABLE_19);
        }
        else {
        	out.write(TREE_TABLE_18_1);
        }
        out.write(TREE_TABLE_20);

        //out.print("             				  <td class='gh_std' width='14'> <img title='Impressão de Listagem (S/ Opções)' onclick=\"winmain().open('__printTree.jsp?docid="+doc.getDocIdx()+"&viewer="+tree.p_key+"','blank')\" src='templates/grid/std/ghRefresh.gif' width='13' height='13' /> </td>\n");
        for (int i = 0; i < nrgroups; i++) 
        {
            out.write(TREE_TABLE_20);
        }

        out.write(TREE_TABLE_22);
        
        if( tree.getExplorerOptions().isSortColumnsOptionVisible() ) {
        out.write(TREE_TABLE_23);
        out.write(tree.getColumnsProvider().getColumn(0).getLabel().toCharArray());
        out.write(TREE_TABLE_24);
        out.write(tree.p_key.toCharArray());
        out.write(TREE_TABLE_25);
        out.write(tree.p_key.toCharArray());
        out.write(TREE_TABLE_26);
        out.write(tree.getColumnsProvider().getColumn(0).getName().toCharArray());
        out.write(TREE_TABLE_27);
        out.write(tree.getColumnsProvider().getColumn(0).getName().toCharArray());
        out.write(TREE_TABLE_28);
        }
        else {
        	out.write( TREE_TABLE_23_1 );
        }

        out.write(tree.getImageSort(tree.getColumnsProvider().getColumn(0).getName()).toCharArray());
        out.write(tree.getColumnsProvider().getColumn(0).getLabel().toCharArray());
        out.write(TREE_TABLE_29);

        out.write(TREE_TABLE_30);
        out.write(TREE_TABLE_31);

        for (int i = 1; i < tree.getColumnsProvider().columnsSize(); i++) {
        	if( tree.getExplorerOptions().isSortColumnsOptionVisible() ) {
            out.write(TREE_TABLE_32);
            out.write(tree.getColumnsProvider().getColumn(i).getLabel().toCharArray());
            out.write(TREE_TABLE_24);
            out.write(tree.p_key.toCharArray());
            out.write(TREE_TABLE_25);
            out.write(tree.p_key.toCharArray());
            out.write(TREE_TABLE_26);
            out.write(tree.getColumnsProvider().getColumn(i).getName().toCharArray());
            out.write(TREE_TABLE_27);
            out.write(tree.getColumnsProvider().getColumn(i).getName().toCharArray());
            out.write(TREE_TABLE_28);
        	}
        	else {
	            out.write(TREE_TABLE_32_1);
        	}

            out.write(tree.getImageSort(tree.getColumnsProvider().getColumn(i).getName()).toCharArray());
            out.write(tree.getColumnsProvider().getColumn(i).getLabel().toCharArray());
            out.write(TREE_TABLE_29);
            out.write(TREE_TABLE_30);
            out.write(TREE_TABLE_33);
        }

        //refresh
        out.write(TREE_TABLE_34);
        out.write(TREE_TABLE_CLOSE_HEADER);
        
        if (!hParameters || !hBlankParameters) {
           
            out.write(TREE_TABLE_35);

            String[] qryG = new String[nrgroups];
            String[] aux;
            int lastGroupNumber = 99999;
            int groupNumber;

            boolean groupIsOpen = true;

            

            boolean cancelRender = false;
            boolean renderTitleGroup = true;
            int groupControl = 999999;

            String[] hist = new String[nrgroups];

            String focusGroup = tree.p_focusGroup;
            int currentLine = 0;
            int renderLines = 0;
            boolean completePage = false;

            if (nrgroups > 0) {
                int i = 0;

                for (; (i < line) && !completePage; i++) {
                    aux = tree.ctrlLines[i].split(TREE_SIGN_1);

                    String xGroup = aux[0];

                    String xGroupValue = "";

                    if (aux.length > 1) {
                        xGroupValue = aux[1];
                    }

                    groupNumber = ClassUtils.convertToInt(xGroup);
                    hist[groupNumber] = xGroupValue;

                    //if ( lastGroupNumber > groupNumber ) groupControl=999999;
                    if ((lastGroupNumber >= groupNumber) &&
                            (groupNumber <= groupControl)) {
                        groupIsOpen = tree.groupIsOpen(genKey(hist, groupNumber));

                        if (!groupIsOpen) {
                            cancelRender = true;
                            renderTitleGroup = true;
                            groupControl = groupNumber;
                        } else {
                            cancelRender = false;
                            renderTitleGroup = true;
                        }
                    } else {
                        if (!cancelRender) {
                            groupIsOpen = tree.groupIsOpen(genKey(hist,
                                        groupNumber));

                            if (!groupIsOpen) {
                                cancelRender = true;
                                renderTitleGroup = true;
                                groupControl = groupNumber;
                            } else {
                                cancelRender = false;
                                renderTitleGroup = true;
                            }
                        }
                    }

                    //  if ( groupNumber <= groupControl )
                    //  {
                    // }
                    if (renderTitleGroup) {
                        String xcor = tree.getGroupColor(ClassUtils.convertToInt(
                                    xGroup), xGroupValue);
                        String xtitle = tree.getGroupStringToPrint(ClassUtils.convertToInt(
                                    xGroup), xGroupValue, doc);

                        String xkey = "";

                        for (int k = 0; k <= groupNumber; k++) {
                            xkey += (hist[k] + TREE_MINUS);
                        }

                        //Integer counterLines= ( Integer ) groupCnt.get( TREE_LETTER_G+ xGroup +TREE_SIGN_1+ xGroupValue );
                        //if( debug ) logger.finest(TREE_LETTER_G+ xGroup +TREE_SIGN_1+ xkey );
                        Integer counterLines = (Integer) tree.groupCnt.get(TREE_LETTER_G +
                                xGroup + TREE_SIGN_1 + xkey);

                        currentLine++;

                        int pageCurr = (int) Math.round(((currentLine - 1) / tree.p_htmlLinesPerPage) +
                                0.5);
                        char[] genAux = genKey(hist, groupNumber).toCharArray();

                        if (pageCurr == tree.p_htmlCurrentPage) {
                            out.write(TREE_TABLE_43);
                            out.write(genAux);
                            out.write(TREE_TABLE_44);
                            out.write(xcor.toCharArray());
                            out.write(TREE_TABLE_45);
                            out.write(String.valueOf(groupNumber * 13).toCharArray());
                            out.write(TREE_TABLE_46);

                            if (groupIsOpen) {
                                out.write(TREE_TABLE_36);
                                out.write(tree.p_key.toCharArray());
                                out.write(TREE_TABLE_37);
                                out.write(genAux);
                                out.write(TREE_TABLE_37);
                                out.write( (""+i).toCharArray() );                                
                                out.write(TREE_TABLE_38);
                                out.write(xtitle.toCharArray());
                            } else {
                                out.write(TREE_TABLE_39);
                                out.write(tree.p_key.toCharArray());
                                out.write(TREE_TABLE_37);
                                out.write(genAux);
                                out.write(TREE_TABLE_37);
                                out.write( (""+i).toCharArray() );
                                out.write(TREE_TABLE_40);
                                out.write(xtitle.toCharArray());
                            }

                            if (counterLines != null) {
                                out.write(TREE_TABLE_41);
                                out.write(String.valueOf(counterLines.intValue()).toCharArray());
                                out.write(TREE_TABLE_42);
                            }

                            out.write(TREE_TABLE_47);

                            renderLines++;

                            if (renderLines >= tree.p_htmlLinesPerPage) {
                                completePage = true;
                            }
                        }

                        if (((groupNumber + 1) == nrgroups) && !cancelRender) {
                            qryG[groupNumber] = xGroupValue;

                            int page = elementTree.getExplorer().getGroupPageOpen(focusGroup);
                            openGroupTree(out, elementTree, i, focusGroup, page, doc, docList);
                            //groupControl=999999;
                        } else {
                            qryG[groupNumber] = xGroupValue;

                            //  groupControl=999999;
                        }
                    }

                    lastGroupNumber = groupNumber;

                    if (cancelRender) {
                        renderTitleGroup = false;
                    }
                }

                renderFooter(out, tree,
                    ((i < line) || completePage) ? true : false);
            } else {
                PreparedStatement pr = null;
                ResultSet rslt = null;
                String xsql = null;
                try
                {
                    if(!tree.hasBoqlInitJSP() || tree.getMenu().isBoqlInitSet())
                    {
                        xsql = tree.getSql(doc.getEboContext(), null);
                        if(tree.getMenu().isBoqlInitSet() && 
                           tree.getBoqlInitJSPClassificationBoql() != null &&
                           tree.getBoqlInitJSPClassificationBoql().length() > 0)
                        {
                            int posi;
                            if((posi = xsql.toUpperCase().indexOf(" GROUP ")) == -1)
                            {
                                posi = xsql.toUpperCase().indexOf(" ORDER ");
                            }
                            if(posi > -1)
                            {
                                String firstPart = xsql.substring(0, posi);
                                String secondPart = xsql.substring(posi);
                                xsql = firstPart + " AND (" + tree.getBoqlInitJSPClassificationBoql() + ") " + secondPart; 
                            }
                            else
                            {
                                xsql += "AND (" + tree.getBoqlInitJSPClassificationBoql() + ")";
                            }
                        }
                        pr = cn.prepareStatement(xsql, ResultSet.TYPE_FORWARD_ONLY,
                                ResultSet.CONCUR_READ_ONLY);
        
                        for (int ip = 0; tree.p_parameters != null && ip < tree.p_parameters.size(); ip++) {
                            pr.setObject(ip + 1, (Object) tree.p_parameters.get(ip));
                        }
        
                        pr.setFetchSize(tree.p_htmlLinesPerPage);
        
                        long timeI = System.currentTimeMillis();
                        rslt = pr.executeQuery();
                        long timeF = System.currentTimeMillis() - timeI;
                        writeQueryToLog(out, timeF, xsql);
                        
                        StringBuffer sbH = new StringBuffer("");
                        int[] x = renderLines(sbH, tree, rslt, doc, docList,
                                renderLines, currentLine);
                        renderLines = x[0];
                        currentLine = x[1];
                        completePage = ((x[2] == 0) ? true : false);
                        out.write(sbH.toString().toCharArray());
                        
                        renderFooter(out, tree, !completePage);
                        if(tree.hasBoqlInitJSP())
                        {
//                            logger.finer("Executed SQL [" +xsql+ "]");
                        }
                    }
                }
                catch(Exception e)
                {
                    logger.severe(e);
                    
                    logger.severe(LoggerMessageLocalizer.getMessage("ERROR_IN_EXPLORER")+": " + tree.getKey() + " - " + tree.getExplorerName() + " "+LoggerMessageLocalizer.getMessage("USER")+": " + tree.getUser() + " SQL: " + xsql);
                    throw new boRuntimeException("TreeBuilder", "writeTree", e);
                }
                finally
                {
                    try{if(rslt != null){rslt.close();}}catch(Exception _e){}
                    try{if(pr != null){pr.close();}}catch(Exception _e){}
                }

            }

            out.write(TREE_TABLE_48);

            if (focusGroup != null) {
                out.write(TREE_TABLE_49);
                out.write(focusGroup.toCharArray());
                out.write(TREE_TABLE_50);
            } else {
                out.write(TREE_TABLE_51);
            }

            out.write(TREE_END);
        }

        tree.p_haveErrors = false;

        long timeFtotal = System.currentTimeMillis() - timeItotal;
        writeToLog(out, TREE_TABLE_52, timeFtotal);
    }

    public static void renderFooter(PrintWriter out, Explorer tree,
        boolean hasMore) throws java.io.IOException {
        if ((tree.p_htmlCurrentPage > 1) || hasMore) {
            out.write(TREE_FOOTER_1);
            out.write(TREE_FOOTER_2);
            out.write(String.valueOf(tree.p_htmlCurrentPage).toCharArray());
            out.write(HTMLCommon.UTIL_WHITE_SPACE);

            if (tree.p_htmlCurrentPage > 1) {
                if (tree.p_htmlCurrentPage > 2) 
                {
                    out.write(TREE_FOOTER_3);
                    out.write(tree.p_key.toCharArray());
                    out.write(TREE_FOOTER_4);
                }
                out.write(TREE_FOOTER_5);
                out.write(tree.p_key.toCharArray());
                out.write(TREE_FOOTER_6);
            }

            if (hasMore) {
                out.write(TREE_FOOTER_7);
                out.write(tree.p_key.toCharArray());
                out.write(TREE_FOOTER_8);
            }

            out.write(HTMLCommon.HTML_SPAN_END);
        }
    }
 public static void closeGroupTree(Tree elementTree, String groupKey){
        elementTree.getExplorer().closeGroup(groupKey);
}
 public static void openGroupTree(PrintWriter out, Tree elementTree,int lineControl,
        String groupKey , int Page,docHTML doc, docHTML_controler docList)
        throws IOException, boRuntimeException, SQLException {
        
        
        long timeItotal = System.currentTimeMillis();
        if(Page<1) Page=1;
        elementTree.getExplorer().openGroup(groupKey, Page);
        
        Explorer tree = elementTree.getExplorer();
        
        if ( tree.ctrlLines == null )
        {
            logger.warn(LoggerMessageLocalizer.getMessage("GROUPS_ARE_NULL_ERROR_RECOVERING"));
            writeTree( new PrintWriter(new ByteArrayOutputStream()), elementTree, doc, docList, null );
        }

        
        int line = tree.ctrlLine;
        
        int nrgroups = tree.getGroupProvider().groupSize();
        
        
        Connection cn = null;
        cn = doc.getEboContext().getConnectionData();            
        String[] qryG = new String[nrgroups];
        String[] aux;
        StringBuffer outRender = null;
//        PrintWriter outRender = null;
        //int lastGroupNumber = 99999;
        int groupNumber;

        //boolean cancelRender = false;
        boolean renderTitleGroup = true;
    //    int groupControl = 999999;

        String[] hist = new String[nrgroups];

        String focusGroup = tree.p_focusGroup;
        int currentLine = 0;
        int renderLines = 0;
        boolean completePage = false;
        
        
        aux = tree.ctrlLines[ lineControl ].split(TREE_SIGN_1);
        String xGroup = aux[0];
        String xGroupValue = "";
    
        if (aux.length > 1) {
            xGroupValue = aux[1];
        }

       // tree.openGroup( groupKey );
        int openGroupNumber = ClassUtils.convertToInt(  xGroup );
        hist[ openGroupNumber ] = xGroupValue;
        qryG[ openGroupNumber ] = xGroupValue;
        
        //montar a hitória para trás do grup actual
        if ( openGroupNumber > 0 )
        {
            int curr=openGroupNumber;
            for (int j = lineControl-1 ; j >=0  ; j--) 
            {
                
                aux = tree.ctrlLines[ j ].split(TREE_SIGN_1);
                int xGroupNumber= ClassUtils.convertToInt(aux[0]) ;
                if ( xGroupNumber < curr )
                {
                    if ( aux.length > 1)
                    {
                    xGroupValue = aux[1];
                    }
                    else
                    {
                    xGroupValue = "";    
                    }
                    hist[ xGroupNumber ] = xGroupValue;
                    qryG[ xGroupNumber ] = xGroupValue;
                    curr = xGroupNumber;
                    if ( xGroupNumber == 0 )
                    {
                        break;
                    }
                }
            }
        }
        
        char[] genAux = genKey(hist, openGroupNumber).toCharArray();
        
        boolean haveMore = false;
        
                
        if (nrgroups > 0) 
        {
            int i =  lineControl;
            
            if ( openGroupNumber == nrgroups-1 )
            {
                // abrir linhas
                    String xsql = "";
                    xsql = tree.getSql(doc.getEboContext(), qryG);

                    PreparedStatement pr = null;
                    ResultSet rslt=null;
                    try
                    {
                        pr = cn.prepareStatement(xsql,
                                ResultSet.TYPE_FORWARD_ONLY,
                                ResultSet.CONCUR_READ_ONLY);
    
                        for (int ip = 0; tree.p_parameters != null && ip < tree.p_parameters.size();
                                ip++) 
                        {
                            pr.setObject(ip + 1,
                                (Object) tree.p_parameters.get(ip));
                        }
    //atenção podem intefrior no DHTML
    //                    long timeI = System.currentTimeMillis();
                        rslt = pr.executeQuery();
    //
    //                    long timeF = System.currentTimeMillis() - timeI;
    //                    writeQueryToLog(out, timeF, xsql);
                        
                        outRender = new StringBuffer();
                        int[] x = renderLines(outRender, tree, rslt, doc,
                                docList, renderLines, currentLine, Page);

                        if ( x[2] !=0 )
                        {
                            haveMore = true;
                        }
                    }
                    catch(Exception e)
                    {
                        logger.severe( e );
                        
                        logger.severe(LoggerMessageLocalizer.getMessage("ERROR_IN_EXPLORER")+": " + tree.getKey() + " - " + tree.getExplorerName() + " "+LoggerMessageLocalizer.getMessage("USER")+": " + tree.getUser() + " SQL: " + xsql);
                        throw new boRuntimeException("TreeBuilder", "writeTree", e);
                    }
                    finally
                    {
                        try{if(rslt != null){rslt.close();}}catch(Exception _e){}
                        try{if(pr != null){pr.close();}}catch(Exception _e){}
                    }
            }
            else
            {
                //abrir subGroups
                i++;
                //enquanto group==openGroup+1;
                
                  outRender = new StringBuffer();                
                for ( ;i < line && renderLines <= tree.p_htmlLinesPerPage ; i++) 
                {
            
                    aux = tree.ctrlLines[i].split(TREE_SIGN_1);
    
                    xGroup = aux[0];
    
                    xGroupValue = "";
    
                    if (aux.length > 1) {
                        xGroupValue = aux[1];
                    }
    
                    groupNumber = ClassUtils.convertToInt(xGroup);
                    
                    hist[groupNumber] = xGroupValue;
                    
                    if( groupNumber == openGroupNumber+1 )
                    {
                        renderTitleGroup=true;
                        
                        if (renderTitleGroup) {
                            String xcor = tree.getGroupColor(ClassUtils.convertToInt(
                                        xGroup), xGroupValue);
                            String xtitle = tree.getGroupStringToPrint(ClassUtils.convertToInt(
                                        xGroup), xGroupValue, doc);
        
                            String xkey = "";
        
                            for (int k = 0; k <= groupNumber; k++) {
                                xkey += (hist[k] + TREE_MINUS);
                            }
        
                            Integer counterLines = (Integer) tree.groupCnt.get(TREE_LETTER_G +
                                    xGroup + TREE_SIGN_1 + xkey);
        
                            currentLine++;
        
                            int pageCurr = (int) Math.round(((currentLine - 1) / tree.p_htmlLinesPerPage) +
                                    0.5);
                            char[] genAux1= genKey(hist, groupNumber).toCharArray();
        
                            if (pageCurr == Page ) 
                            {
                                outRender.append(TREE_TABLE_43);
                                outRender.append(genAux1);
                                outRender.append(TREE_TABLE_44);
                                outRender.append(xcor.toCharArray());
                                outRender.append(TREE_TABLE_45);
                                outRender.append(groupNumber * 13);
                                outRender.append(TREE_TABLE_46);
                                
                                //group is closed
                                outRender.append(TREE_TABLE_39);
                                outRender.append(tree.p_key.toCharArray());
                                outRender.append(TREE_TABLE_37);
                                outRender.append(genAux1);
                                outRender.append(TREE_TABLE_37);
                                outRender.append( (""+i).toCharArray() );
                                outRender.append(TREE_TABLE_40);
                                outRender.append(xtitle.toCharArray());
                              
        
                                if (counterLines != null) {
                                    outRender.append(TREE_TABLE_41);
                                    outRender.append(counterLines.intValue());
                                    outRender.append(TREE_TABLE_42);
                                }
        
                                outRender.append(TREE_TABLE_47);
        
                                renderLines++;
        
                               
                            }
                            qryG[groupNumber] = xGroupValue;
             
                        }
                    }
                    else if(groupNumber <= openGroupNumber)
                    {
                        break;
                    }
                    
                    
    
                   // lastGroupNumber = groupNumber;
                
                
                
                 }
                 haveMore =  i < line && renderLines >= tree.p_htmlLinesPerPage;
                 

            }
            
            
//            PrintWriter outFooter = new PrintWriter(byteOutFooter);
            StringBuffer outFooter = new StringBuffer();
            
            boolean nextLink = false;
            boolean previousLink = false;
            
            
            if ( haveMore ) //existem mais 
            {
                nextLink=true;
                if ( Page > 1 ) previousLink = true;
                
            }
            else
            {
                if ( Page > 1 ) previousLink=true;
            }
            if ( previousLink || nextLink )
            {
                
                
                
                outFooter.append(TREE_PAGEC_1);
                outFooter.append( String.valueOf( (openGroupNumber)*13 ).toCharArray());
                outFooter.append(TREE_PAGEC_2);
                
                if ( previousLink )
                {
                    //BEGIN PRIOR LINK
                outFooter.append(TREE_PAGE_1);
                outFooter.append(tree.p_key.toCharArray());
                outFooter.append(TREE_PAGE_2);
                outFooter.append(genAux);
                outFooter.append(TREE_PAGE_2);
                outFooter.append( (""+lineControl).toCharArray() );
                outFooter.append(TREE_PAGE_2);
                outFooter.append( (""+(Page-1)).toCharArray() );
                outFooter.append(TREE_PAGE_4);
                //---END PRIOR LINK
                
                }
                //BEGIN NEXT LINK
                
                if ( nextLink )
                {
                    outFooter.append(TREE_PAGE_1);
                    outFooter.append(tree.p_key.toCharArray());
                    outFooter.append(TREE_PAGE_2);
                    outFooter.append(genAux);
                    outFooter.append(TREE_PAGE_2);
                    outFooter.append( (""+lineControl).toCharArray() );
                    outFooter.append(TREE_PAGE_2);
                    outFooter.append( (""+(Page+1)).toCharArray() );
                    outFooter.append(TREE_PAGE_3);
                    //---END NEXT LINK
                }
                
                outFooter.append(TREE_PAGEC_3);
            } 
            
            out.write( outFooter.toString().toCharArray() );
            out.write( outRender.toString().toCharArray() );
            out.write( outFooter.toString().toCharArray() );

   
        } 

        long timeFtotal = System.currentTimeMillis() - timeItotal;
        writeToLog(out, TREE_TABLE_52, timeFtotal);
    }



public static int[] renderLines(StringBuffer out, Explorer tree,
        ResultSet rslt, docHTML DOC, docHTML_controler DOCLIST,
        int renderLines, int currentLine) throws boRuntimeException, java.io.IOException, SQLException
        {
           return renderLines(out, tree,rslt, DOC, DOCLIST,renderLines,currentLine, tree.p_htmlCurrentPage);
            
        }
    private static int[] renderLines(StringBuffer out, Explorer tree,
        ResultSet rslt, docHTML DOC, docHTML_controler DOCLIST,
        int renderLines, int currentLine, int page)
        throws boRuntimeException, java.io.IOException, SQLException {
        long xt = System.currentTimeMillis();
        StringBuffer headerTable = new StringBuffer();
        boolean alreadyPrintHeader = false;

        headerTable.append(TREE_LINES_1);
        
        
        headerTable.append(TREE_LINES_2);

        for (int i = 0; i < tree.getGroupProvider().groupSize(); i++) {
            headerTable.append(TREE_LINES_3);
        }

        headerTable.append(TREE_LINES_4);
        headerTable.append(TREE_LINES_5);
        headerTable.append(TREE_LINES_6);

        int nrcols = 3;

        for (int z = 0; z < tree.getColumnsProvider().columnsSize(); z++) {
            headerTable.append(TREE_LINES_7);
            headerTable.append(String.valueOf(tree.getColumnsProvider().getColumn(z).getWidth()));
            new String(headerTable.append(new String(TREE_TABLE_13)));
            nrcols++;
        }

        boObject obj;
        String xatrname = "";
        boDefHandler def;

        boolean completePage = false;

        String[] extraCols = new String[0];
        if(tree.p_extraColumns != null)
        {
            extraCols = tree.p_extraColumns.toString().split(",");
        }
        HashMap extrValues = new HashMap();

        long[] bouis = new long[50];
        int indice = 0;
        long boui;
        int count = 0;

        long ti = System.currentTimeMillis();
        while (!completePage && rslt.next() &&
                (renderLines <= tree.p_htmlLinesPerPage)) {
            currentLine++;

            if (Math.round(((currentLine - 1) / tree.p_htmlLinesPerPage) + 0.5) == page ) {
                boui = rslt.getLong(1);

                if (indice > bouis.length) {
                    bouis = ClassUtils.growLongArray(bouis, 20);
                }

                bouis[indice++] = boui;

                for (int i = 0; i < extraCols.length; i++) {
                    extrValues.put(extraCols[i] + boui + TREE_MINUS + count,
                        tree.getExtAtrHTML(rslt, extraCols[i], DOC));
                }

                count++;

                renderLines++;

                if (renderLines >= tree.p_htmlLinesPerPage) {
                    completePage = true;
                }
            }
        }
        writeToLog(out, ("1 "+MessageLocalizer.getMessage("CYCLE")).toCharArray(), System.currentTimeMillis()-ti);
        ti = System.currentTimeMillis();
        bouis = ClassUtils.setsizeLongArray(bouis, indice);
        try
        {
            boObject.getBoManager().preLoadObjects(DOC.getEboContext(), bouis);
        }
        catch(Exception e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR_IN_PRELOADOBJECTS"), e);
        }
        writeToLog(out, "preload".toCharArray(), System.currentTimeMillis()-ti);

        //guardar posições e bouis
        tree.getTree().addShowingBouis(bouis);

        //boleano que indica que é a primeira linha de um objecto 
        boolean firstTime = true;

        ti = System.currentTimeMillis();
        for (int ib = 0; ib < bouis.length; ib++) {
            boolean backtrack = false;

            try {
                //  currentLine++;
                //  if (  Math.round( currentLine/tree.p_htmlLinesPerPage+0.5 ) ==  tree.p_htmlCurrentPage   )
                //  {
                if (!alreadyPrintHeader) {
                    out.append(headerTable.toString().toCharArray());
                    alreadyPrintHeader = true;
                }

                obj = DOC.getObject(bouis[ib]);
                def = obj.getBoDefinition();

                if (!obj.userReadThis()) {
                    out.append(TREE_LINES_8);
                    out.append(String.valueOf(obj.bo_boui).toCharArray());
                    out.append(HTMLCommon.SYMBOL_SLASH);
                    out.append(HTMLCommon.SYMBOL_PLICA);
                    out.append(tree.getKey().toCharArray());
                    out.append(HTMLCommon.SYMBOL_PLICA);
                    out.append(HTMLCommon.SYMBOL_SLASH);
                    out.append(HTMLCommon.SYMBOL_PLICA);
                    out.append(!tree.getExplorerOptions().isSelectActionDisable());
                    out.append(HTMLCommon.SYMBOL_PLICA);
                    out.append(TREE_LINES_8_1);
                    if(!tree.getExplorerOptions().isDoubleClickActionDisable())
                    {
                        out.append(TREE_LINES_8_2);
                        if(tree.getExplorerOptions().getDoubleClickActionCode() == null)
                        {
                            out.append(obj.getViewerUtils().getOpenObjectScript());
                        }
                        else
                        {
                            out.append(replaceSpecialCode(DOC.getEboContext(), tree.getExplorerOptions().getDoubleClickActionCode(), tree, obj.bo_boui, ib));
                        }
//                        out.append(HTMLCommon.SYMBOL_PLICA);
//                        out.append(obj.getName().toCharArray());
//                        out.append(HTMLCommon.SYMBOL_PLICA);
//                        out.append(HTMLCommon.SYMBOL_SLASH);
//                        out.append(String.valueOf(obj.bo_boui).toCharArray());
                        out.append(TREE_LINES_8_3);
                    }
                    out.append(TREE_LINES_8_4);
                    out.append(String.valueOf(obj.bo_boui).toCharArray());
                    out.append(TREE_LINES_9);
                    out.append(obj.getName().toCharArray());
                    out.append(TREE_LINES_10);
                } else {
                    out.append(TREE_LINES_11);
                    out.append(String.valueOf(obj.bo_boui).toCharArray());
                    out.append(HTMLCommon.SYMBOL_SLASH);
                    out.append(HTMLCommon.SYMBOL_PLICA);
                    out.append(tree.getKey().toCharArray());
                    out.append(HTMLCommon.SYMBOL_PLICA);
                    out.append(HTMLCommon.SYMBOL_SLASH);
                    out.append(HTMLCommon.SYMBOL_PLICA);
                    out.append(!tree.getExplorerOptions().isSelectActionDisable());
                    out.append(HTMLCommon.SYMBOL_PLICA);
                    out.append(TREE_LINES_11_1);
                    if(!tree.getExplorerOptions().isDoubleClickActionDisable())
                    {            
                        out.append(TREE_LINES_11_2);
                        if(tree.getExplorerOptions().getDoubleClickActionCode() == null)
                        {
                            out.append(obj.getViewerUtils().getOpenObjectScript());
                        }
                        else
                        {
                            out.append(replaceSpecialCode(DOC.getEboContext(),tree.getExplorerOptions().getDoubleClickActionCode(), tree, obj.bo_boui, ib));
                        }
//                        out.append(HTMLCommon.SYMBOL_PLICA);
//                        out.append(obj.getName().toCharArray());
//                        out.append(HTMLCommon.SYMBOL_PLICA);
//                        out.append(HTMLCommon.SYMBOL_SLASH);
//                        out.append(String.valueOf(obj.bo_boui).toCharArray());
                        out.append(TREE_LINES_11_3);
                    }
                    out.append(TREE_LINES_11_4);
                    out.append(String.valueOf(obj.bo_boui).toCharArray());
                    out.append(TREE_LINES_9);
                    out.append(obj.getName().toCharArray());
                    out.append(TREE_LINES_10);
                }

                for (int i = 0; i < tree.getGroupProvider().groupSize(); i++) {
                    out.append(TREE_LINES_13);
                }

                //quando já se imprimiu os dados de um objecto, não se volta a imprimir
                if (firstTime) {
                    boolean hasCheck = false;
                    if(tree.getCheckOnclickEvent() != null && !"".equals(tree.getCheckOnclickEvent()))
                    {
                        hasCheck = true;
                        out.append(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);
                        out.append(TREE_LINES_14_1);
                        if(tree.isChecked(obj.bo_boui))
                        {
                            out.append("checked".toCharArray());
                        }
                        out.append(TREE_LINES_14_2);
                        out.append(replaceSpecialCode(DOC.getEboContext(), tree.getCheckOnclickEvent(), tree, obj.bo_boui, ib));
                        out.append(TREE_LINES_14_3);
                    }
                    out.append(TREE_LINES_14);
                    out.append(obj.getLabel().toCharArray());
                    out.append(TREE_LINES_15);
                    out.append(obj.getSrcForIcon16().toCharArray());
                    out.append(TREE_LINES_16);
                    if(!hasCheck)
                    {
                        out.append(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);
                        out.append(obj.getICONComposedState().toCharArray());
                    }
                } else {
                    out.append(TREE_LINES_18);
                }

                out.append(HTMLCommon.HTML_TABLE_COLUMN_END);
                out.append(HTMLCommon.UTIL_NEW_LINE);

                for (int z = 0; z < tree.getColumnsProvider().columnsSize(); z++) {
                    //caso para atributos de bridges, para o qual não existe mais valores, ou para atributos normais que já foram impressos
                    if ((!firstTime && !(tree.getColumnsProvider().getColumn(z).bridgeInd() > -1)) ||
                            ((tree.getColumnsProvider().getColumn(z).count_Br() == -1) && !firstTime)) {
                        if(z==0)
                        {
                            out.append(TREE_FIRST_COL);
                            out.append(NBSP.toCharArray());
                            out.append(HTMLCommon.HTML_COL_END);
                        }
                        else
                        {
                            out.append(TREE_LINES_13);
                        }

                        continue;
                    }

                    //quando é a primeira linha de um objecto, a contagem das bridges volat a 0
                    if (firstTime && (tree.getColumnsProvider().getColumn(z).count_Br() == -1)) {
                        tree.getColumnsProvider().getColumn(z).setCount_Br(0);
                    }

                    //  boObject obj1;
                    if ( z==0 ) out.append( TREE_FIRST_COL );
                    else
                    out.append(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);

                    //quando não é a primeira linha de um objecto só se coloca a grid se ouver valores
                    if (firstTime) {
                        out.append(TREE_LINES_19);

                        if (z == 0) {
                            out.append(TREE_LINES_20);
                        } else {
                            out.append(tree.getColumnsProvider().getColumn(z).getWidth());
                            out.append(TREE_LINES_21);
                        }

                        out.append(TREE_LINES_22);
                    }

                    xatrname = tree.getColumnsProvider().getColumn(z).getName();

                    boolean hasResults = ((ColumnProvider) tree.getAllAttributes()
                                                                      .get(xatrname)).hasResults();

                    if (!hasResults && tree.getColumnsProvider().getColumn(z).isAttribute() &&
                            !tree.getColumnsProvider().getColumn(z).isExternalAttribute()) {
                        AttributeHandler attr = obj.getAttribute(xatrname);
                        boDefAttribute xdef = null;

                        if (attr != null) {
                            xdef = attr.getDefAttribute();
                        }

                        if (def.hasAttribute(xatrname)) {
                            if (xdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                                //obj.getAttribute(xatrname)
                                long[] values = new long[0];

                                if (xdef.getRelationType() == boDefAttribute.RELATION_1_TO_1) {
                                    long v = attr.getValueLong();

                                    if (v > 0) {
                                        values = new long[1];
                                        values[0] = v;
                                    }
                                } else if (xdef.getRelationType() == boDefAttribute.RELATION_MULTI_VALUES) {
                                    values = attr.getValuesLong();
                                }
                                //if necessário devido a bug no get values long
                                else if (xdef.getRelationType() > 1) {
                                    //contem os bouis dos objectos da bridge
                                    String[] bouisStr = attr.getValueString()
                                                            .split(";");

                                    if ((bouisStr.length == 0) ||
                                            (bouisStr[0] == null) ||
                                            (bouisStr[0].equals(""))) {
                                        values = null;
                                    } else {
                                        long bouiBo = 0;

                                        //caso seja a primeira vez carregar os objectos
                                        if (firstTime) {
                                            values = new long[bouisStr.length];

                                            for (int iv = 0;
                                                    iv < bouisStr.length;
                                                    iv++) {
                                                values[iv] = Long.parseLong(bouisStr[iv]);
                                            }

                                            if (values.length > 0) {
                                                boObject.getBoManager()
                                                        .preLoadObjects(DOC.getEboContext(),
                                                    values);
                                                bouiBo = values[tree.getColumnsProvider().getColumn(z).count_Br()];
                                            }
                                        } else {
                                            bouiBo = Long.parseLong(bouisStr[tree.getColumnsProvider().getColumn(z).count_Br()]);
                                        }

                                        //testar se existem mais atributos da bridge mas não existem mais bouis deste objecto
                                        if (tree.getColumnsProvider().getColumn(z).count_Br() < (bouisStr.length -
                                                1)) {
                                            backtrack = true;
                                        }

                                        if (bouiBo > 0) {
                                            values = new long[1];
                                            values[0] = bouiBo;

                                            tree.getColumnsProvider().getColumn(z).setCount_Br(tree.getColumnsProvider().getColumn(z).count_Br()+ 1);

                                            //testar se a bridge tem mais elementos
                                            if (tree.getColumnsProvider().getColumn(z).count_Br() == bouisStr.length) {
                                                tree.getColumnsProvider().getColumn(z).setCount_Br(-1);
                                            }
                                        }
                                    }
                                }

                                if ((values != null) && (values.length > 0)) {
                                    //como não e a primeira vez e existem dados, imprime--se a grid
                                    if (!firstTime) {
                                        out.append(TREE_LINES_19);

                                        if (z == 0) {
                                            out.append(TREE_LINES_20);
                                        } else {
                                            out.append(String.valueOf(tree.getColumnsProvider().getColumn(z).getWidth()).toCharArray());
                                            out.append(TREE_LINES_21);
                                        }

                                        out.append(TREE_LINES_22);
                                    }

                                    out.append(TREE_LINES_23);

                                    for (int j = 0; j < values.length; j++) {
                                        boObject o = boObject.getBoManager().loadObject( DOC.getEboContext(),values[j] );
                                        if(showLink(tree, tree.getColumnsProvider().getColumn(z)) 
                                        		&& securityRights.canRead( DOC.getEboContext(), o.getName() )
                                        		&& securityOPL.canRead( o )
                                        	)
                                        {
                                            out.append(TREE_LINES_24);
                                            out.append(TREE_LINES_25);
                                            out.append(o.getName().toLowerCase()
                                                       .toCharArray());
                                            out.append(TREE_LINES_26);
                                            out.append(String.valueOf(values[j]).toCharArray());
                                            out.append(TREE_LINES_27);
                                            out.append(TREE_LINES_28);
                                            out.append(TREE_LINES_29);
                                            out.append(String.valueOf(values[j]).toCharArray());
                                            out.append(TREE_LINES_30);
                                            out.append(o.getName().toCharArray());
                                            out.append(TREE_LINES_22);
                                            if(openDocument(tree, tree.getColumnsProvider().getColumn(z)))
                                            {
                                                if(!isDocumentValidForClientRender(DOC.getEboContext(), o.getName(), o.getBoui()))
                                                {
                                                    out.append(o.getCARDID(false).toString()
                                                               .toCharArray());
                                                }
                                                else
                                                {
                                                    String s = openDocumentinClient(obj, attr, String.valueOf(o.getBoui()), new StringBuffer(""+o.getBoui()), new StringBuffer(""),1,  DOC, false, true, "edit","document", "title", null, tree.p_key);
                                                    out.append(s.toCharArray());
                                                }
                                            }
                                            else
                                            {
                                                out.append(o.getCARDID(false).toString()
                                                               .toCharArray());
                                            }
                                            out.append(HTMLCommon.HTML_SPAN_END);
                                        }
                                        else
                                        {
                                            if(!isDocumentValidForClientRender(DOC.getEboContext(), o.getName(), o.getBoui()))
                                            {
                                                out.append(o.getCARDID(false).toString()
                                                           .toCharArray());
                                            }
                                            else
                                            {
                                                String s = openDocumentinClient(obj, attr, String.valueOf(o.getBoui()), new StringBuffer(""+o.getBoui()), new StringBuffer(""),1,  DOC, false, true, "edit","document", "title", null, tree.p_key);
                                                out.append(s.toCharArray());
                                            }
                                        }
                                    }

                                    out.append(HTMLCommon.HTML_DIV_END);
                                } else {
                                    out.append(HTMLCommon.UTIL_WHITE_SPACE);
                                }
                            } else {
                                String v = getStringValue(attr);

                                

                                if (!firstTime) {
                                    out.append(TREE_LINES_19);

                                    if (z == 0) {
                                        out.append(TREE_LINES_20);
                                    } else {
                                        out.append(String.valueOf(tree.getColumnsProvider().getColumn(z).getWidth()).toCharArray());
                                        out.append(TREE_LINES_21);
                                    }

                                    out.append(TREE_LINES_22);
                                }

                                out.append(v.toCharArray());
                            }
                        }
                    }else if(tree.getColumnsProvider().getColumn(z).hasSpecialClauses())//Classcolumn
                    {
                        ClassColumn classCol = (ClassColumn)tree.getColumnsProvider().getColumn(z);
                        String values[] = null;
                        String bouiBo = "";
                        //contem os bouis dos objectos da bridge
                        String[] bouisStr = classCol.getAllClassifs(DOC.getEboContext(), obj.getBoui());
                        if ((bouisStr.length == 0) ||
                                (bouisStr[0] == null) ||
                                (bouisStr[0].equals(""))) {
                            values = null;
                        } 
                        else 
                        {

                            //caso seja a primeira vez carregar os objectos
                            if (firstTime) {
                                values = new String[bouisStr.length];

                                for (int iv = 0;
                                        iv < bouisStr.length;
                                        iv++) {
                                    values[iv] = bouisStr[iv];
                                }

                                if (values.length > 0) {
//                                    boObject.getBoManager()
//                                            .preLoadObjects(DOC.getEboContext(),
//                                        values);
                                    bouiBo = values[tree.getColumnsProvider().getColumn(z).count_Br()];
                                }
                            } else {
                                bouiBo = bouisStr[tree.getColumnsProvider().getColumn(z).count_Br()];
                            }

//                            testar se existem mais atributos da bridge mas não existem mais bouis deste objecto
                            if (tree.getColumnsProvider().getColumn(z).count_Br() < (bouisStr.length -
                                    1)) {
                                backtrack = true;
                            }


                            if (bouiBo.length() > 0) 
                            {
                                values = new String[1];
                                values[0] = bouiBo;

                                tree.getColumnsProvider().getColumn(z).setCount_Br(tree.getColumnsProvider().getColumn(z).count_Br()+ 1);

                                //testar se a bridge tem mais elementos
                                if (tree.getColumnsProvider().getColumn(z).count_Br() == bouisStr.length) {
                                    tree.getColumnsProvider().getColumn(z).setCount_Br(-1);
                                }
                            }
                        }
                        if ((values != null) && (values.length > 0)) {
                            //como não e a primeira vez e existem dados, imprime--se a grid
                            if (!firstTime) {
                                out.append(TREE_LINES_19);

                                if (z == 0) {
                                    out.append(TREE_LINES_20);
                                } else {
                                    out.append(String.valueOf(tree.getColumnsProvider().getColumn(z).getWidth()).toCharArray());
                                    out.append(TREE_LINES_21);
                                }

                                out.append(TREE_LINES_22);
                            }

                            out.append(TREE_LINES_23);

                            for (int j = 0; j < values.length; j++) 
                            {
                                String toPrint = tree.getColumnsProvider().getColumn(z).getValueResult(values[j], DOC.getEboContext(), showLink(tree, tree.getColumnsProvider().getColumn(z)));
                                out.append(toPrint.toCharArray());
                            }

                            out.append(HTMLCommon.HTML_DIV_END);
                        } else {
                            out.append(HTMLCommon.UTIL_WHITE_SPACE);
                        }
                        
                    }else {
                        //extension attribute
                        String value = "";

                        //testar se o atributo está dependente de uma bridge
                        if (tree.getColumnsProvider().getColumn(z).bridgeInd() > -1) {
                            for (int ic = 0; ic < tree.getColumnsProvider().columnsSize(); ic++) {
                                //caso esteja
                                if ((tree.getColumnsProvider().getColumn(z).bridgeInd() == tree.getColumnsProvider().getColumn(ic).bridgeInd()) &&
                                        (tree.getColumnsProvider().getColumn(ic).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                        (tree.getColumnsProvider().getColumn(ic).getDefAttribute().getRelationType() > 1)) {
                                    //irá conter o caminho necessário para chegar ao atributo pretendido
                                    String[] path = tree.getColumnsProvider().getColumn(z).getName().split(POINT);

                                    boObject curObj = obj;
                                    AttributeHandler finalAtt = null;

                                    //pecorrer o atributo do qual depende até chegar ao pretendido
                                    int p;

                                    for (p = 0; p < (path.length - 1); p++) {
                                        AttributeHandler attCur = curObj.getAttribute(path[p]);

                                        //caso o atributo actual seja uma bridge
                                        if (attCur.getDefAttribute()
                                                      .getRelationType() > 1) {
                                            bridgeHandler curBr = curObj.getBridge(path[p]);
                                            curBr.beforeFirst();

                                            //testar se abridge tem objectos
                                            if (!curBr.next()) {
                                                value = NBSP;

                                                break;
                                            }

                                            //percorrer a bridge até ao objecto pretendido
                                            for (int b = 0;
                                                    b < tree.getColumnsProvider().getColumn(z).count_Br();
                                                    b++)
                                                curBr.next();

                                            //testar se a bridge contem o atributo seguinte
                                            if (curBr.getAttribute(path[p + 1]) != null) {
                                                //testar se o atributo seguinte é o pretendido
                                                if ((p + 1) == (path.length -
                                                        1)) {
                                                    finalAtt = curBr.getAttribute(path[p +
                                                            1]);
                                                } else {
                                                    curObj = curBr.getAttribute(path[p +
                                                            1]).getObject();
                                                }
                                            } else { //caso não contenha pssa-se para o objecto seguinte
                                                curObj = curBr.getObject();
                                            }
                                        } else //caso não seja uma bridge passa-se para o objecto seguinte
                                         {
                                            curObj = curObj.getAttribute(path[p])
                                                           .getObject();
                                        }
                                    }

                                    //se não se percorreu o ciclo todo é porque não exitem dados
                                    if (p != (path.length - 1)) {
                                        break;
                                    }

                                    if (finalAtt == null) {
                                        finalAtt = curObj.getAttribute(path[path.length -
                                                1]);
                                    }

                                    if (finalAtt.getDefAttribute()
                                                    .getAtributeType() == finalAtt.getDefAttribute().TYPE_OBJECTATTRIBUTE) {
                                        boObject objVal = finalAtt.getObject();

                                        if (objVal != null) {
                                            value = objVal.getCARDID().toString();
                                        } else {
                                            value = NBSP;
                                        }
                                    } else {
                                        String valAux = finalAtt.getValueString();

                                        if (value != null) {
                                            value = valAux;
                                        } else {
                                            value = NBSP;
                                        }
                                    }

                                    tree.getColumnsProvider().getColumn(z).setCount_Br(tree.getColumnsProvider().getColumn(z).count_Br()+1);

                                    break;
                                }
                            }
                        } else {
                            value = null;
                        }

                        if (value == null) {
                            if (xatrname.indexOf('.') > -1) {
                                xatrname = xatrname.replaceAll(POINT, CIFRAO);
                            }

                            value = extrValues.get(xatrname + bouis[ib] +
                                    TREE_MINUS + ib).toString();
                        }

                        if (!firstTime && !value.equals(NBSP)) {
                            out.append(TREE_LINES_19);

                            if (z == 0) {
                                out.append(TREE_LINES_20);
                            } else {
                                out.append(String.valueOf(tree.getColumnsProvider().getColumn(z).getWidth()).toCharArray());
                                out.append(TREE_LINES_21);
                            }

                            out.append(TREE_LINES_22);
                        }

                        String lovname = null;

                        if (tree.getColumnsProvider().getColumn(z).getDefAttribute() != null) {
                            lovname = tree.getColumnsProvider().getColumn(z).getDefAttribute().getLOVName();

                            //substitui os boleanos por Sim ou Não
                            if (tree.getColumnsProvider().getColumn(z).getDefAttribute().getType()
                                                           .equalsIgnoreCase("boolean")) {
                                if (value.equals("1")) {
                                    value = "Sim";
                                } else if (value.equals("0")) {
                                    value = "Não";
                                } else {
                                    value = "";
                                }
                            }
                        }

                        if ((lovname != null) && !lovname.equalsIgnoreCase("")) {
                            value = boObjectUtils.getLovDescription(DOC.getEboContext(),
                                    lovname, value);
                        }
                        

                        out.append(value.toCharArray());

                        //out.print(tree.getExtAtrHTML( rslt , xatrname, DOC ));
                    }

                    out.append(HTMLCommon.HTML_SPAN_END);
                    out.append(HTMLCommon.HTML_TABLE_COLUMN_END);
                }

                out.append(HTMLCommon.HTML_TABLE_LINE_END);
                out.append(HTMLCommon.UTIL_NEW_LINE);

                //se o proximo não objecto é o igual ao actual, ou não acabou a listagem
                if (ib == (bouis.length - 1)) {
                    if (backtrack) {
                        ib--;
                    }

                    firstTime = false;
                } else if (bouis[ib] != bouis[ib + 1]) {
                    if (backtrack) {
                        ib--;
                        firstTime = false;
                    } else {
                        firstTime = true;
                    }
                } else {
                    firstTime = false;
                }

                //marcar atributos dependentes de bridges que já não tem mais dados
                for (int ic = 0; ic < tree.getColumnsProvider().columnsSize(); ic++) {
                    if (firstTime) {
                        tree.getColumnsProvider().getColumn(ic).setCount_Br(0);
                    } else if (tree.getColumnsProvider().getColumn(ic).count_Br() == -1) {
                        for (int jc = 0; jc < tree.getColumnsProvider().columnsSize(); jc++) {
                            if (tree.getColumnsProvider().getColumn(jc).bridgeInd() == tree.getColumnsProvider().getColumn(ic).bridgeInd()) {
                                tree.getColumnsProvider().getColumn(jc).setCount_Br(-1);
                            }
                        }
                    }
                }

                //devido a um bug do boql, que coloca o numero de objectos conforme o numero da soma de elementos das várias bridges
                //é preciso saltar os os objectos que já foram impressos, e para o qual não exitem mais bridges para imprimir
                boolean invCol = true;

                for (int ic = 0; (ic < tree.getColumnsProvider().columnsSize()) && !firstTime;
                        ic++) {
                    if ((tree.getColumnsProvider().getColumn(ic).bridgeInd() > -1) &&
                            (tree.getColumnsProvider().getColumn(ic).count_Br() == -1)) {
                        invCol &= true;
                    } else if (tree.getColumnsProvider().getColumn(ic).bridgeInd() > -1) {
                        invCol &= false;
                    }
                }

                if (invCol && !firstTime) {
                    while (((ib + 1) < bouis.length) &&
                            (bouis[ib] == bouis[ib + 1]))
                        ib++;

                    firstTime = true;
                }

                //---
            } catch (Exception e) {
                if (!alreadyPrintHeader) {
                    out.append(headerTable.toString().toCharArray());
                    alreadyPrintHeader = true;
                }

                out.append(HTMLCommon.HTML_TABLE_LINE_BEGIN);
                out.append(TREE_LINES_32);
                out.append(String.valueOf(nrcols).toCharArray());
                out.append(HTMLCommon.SYMBOL_GT);
                if ( e.getMessage()!= null)            out.append(e.getMessage().toCharArray());
                
                out.append(HTMLCommon.HTML_TABLE_COLUMN_END);
                out.append(HTMLCommon.UTIL_NEW_LINE);
                out.append(HTMLCommon.HTML_TABLE_LINE_END);
                out.append(HTMLCommon.UTIL_NEW_LINE);
                logger.severe(e);
                
                String sTmp = "";
                for(int z=0; z < e.getStackTrace().length; z++)
                {
                    sTmp += e.getStackTrace()[z].getClassName() + "." + e.getStackTrace()[z].getMethodName() + "(" + 
                            e.getStackTrace()[z].getFileName() + ":" + String.valueOf(e.getStackTrace()[z].getLineNumber()) + ")";
                }
                logger.severe(sTmp, e);
            }
        }
        writeToLog(out, ("2 "+MessageLocalizer.getMessage("CYCLE")).toCharArray(), System.currentTimeMillis()-ti);
        if (alreadyPrintHeader) {
            out.append(HTMLCommon.HTML_TABLE_END);
            out.append(HTMLCommon.UTIL_NEW_LINE);
        }

        int[] toRet = new int[3];
        toRet[0] = renderLines;
        toRet[1] = currentLine;
        toRet[2] = rslt.next() ? 1 : 0;
        writeToLog(out, MessageLocalizer.getMessage("RENDER_LINE_TIME").toCharArray(), System.currentTimeMillis() - xt);
        return toRet;
    }

    private static void writeQueryToLog(PrintWriter out, long time,
        String query) throws IOException {
        String t = ((float) (Math.round((float) (time) / 100f)) / 10f) + "s";
        out.write(("<!--"+MessageLocalizer.getMessage("QUERY_TIME")+"(" + t + "): " + query + " -->").toCharArray());
    }

    private static void writeToLog(StringBuffer out, char[] s, long time)
        throws IOException {
        String t = ((float) (Math.round((float) (time) / 100f)) / 10f) + "s";
        out.append(TREE_TABLE_53);
        out.append(s);
        out.append(TREE_TABLE_54);
        out.append(t.toCharArray());
        out.append(TREE_TABLE_55);
    }
    private static void writeToLog(PrintWriter out, char[] s, long time)
        throws IOException {
        String t = ((float) (Math.round((float) (time) / 100f)) / 10f) + "s";
        out.write(TREE_TABLE_53);
        out.write(s);
        out.write(TREE_TABLE_54);
        out.write(t.toCharArray());
        out.write(TREE_TABLE_55);
    }

    public static String genKey(String[] hist, int nrgroup) {
        String toRet = "";

        for (int i = 0; i <= nrgroup; i++) {
            toRet += (TREE_RPL_1 +
            hist[i].replaceAll(TREE_RPL_2, TREE_RPL_3).replaceAll(TREE_RPL_4,
                TREE_RPL_3).replaceAll("\n", "").replaceAll("\r", ""));
        }

        return toRet;
    }
    
    public static String getStringValue( AttributeHandler att ) throws boRuntimeException, SQLException
    {
        EboContext ctx = att.getEboContext();
        boDefAttribute attDef = att.getDefAttribute();
         if (attDef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
         {
             long b=att.getValueLong();
             if ( b > 0 )
             {
                 boObject o =  boObject.getBoManager().loadObject( ctx, b );
                 return o.getCARDID().toString();
             }
             else return "";
         }
         else if("boolean".equalsIgnoreCase(attDef.getType()))
         {
            String value = att.getValueString();
            if("0".equals(value))
            {
                //falta verificar a lingua
                return MessageLocalizer.getMessage("NO");
            }
            else if("1".equals(value))
            {
                return MessageLocalizer.getMessage("YES");
            }
            return value;
         }             
         else if(attDef.getLOVName() != null &&  
            !"".equals(attDef.getLOVName()))
         {
            String xlov = attDef.getLOVName(); 
            String value = att.getValueString();
            if(value != null && !"".equals(value))
            {
                lovObject lovObj = LovManager.getLovObject(ctx, xlov);
                if(lovObj != null)
                {
                    lovObj.beforeFirst();
                    while(lovObj.next())
                    {
                        if(value.equalsIgnoreCase(lovObj.getCode()))
                        {
                            return lovObj.getDescription();
                        }
                    }
                }
            }
            return "";
         }
         else if("dateTime".equalsIgnoreCase(attDef.getType())) 
         {
            Date d = att.getValueDate();           
            if(d != null)
            {
                 SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss");
                 return formatter.format(d);
            }
            return "";
         }
         else if("date".equalsIgnoreCase(attDef.getType()))
         {
            Date d = att.getValueDate();           
            if(d != null)
            {
                 SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy");
                 return formatter.format(d);
            }
            return "";
         }
         else
         {
            NumberFormat currencyFormatter = NumberFormat.getInstance();
            if(attDef.getDecimals() != 0)
            {
                //currency
                currencyFormatter.setParseIntegerOnly(false);
                if("Y".equalsIgnoreCase(attDef.getGrouping()))
                {
                    currencyFormatter.setGroupingUsed(true);
                }
                currencyFormatter.setMaximumFractionDigits(attDef.getDecimals());
                currencyFormatter.setMinimumFractionDigits(attDef.getMinDecimals());
                currencyFormatter.setMinimumIntegerDigits(1);
                return currencyFormatter.format(att.getValueDouble());
            }
            else if("Y".equalsIgnoreCase(attDef.getGrouping()))
            {
                currencyFormatter.setParseIntegerOnly(false);
                currencyFormatter.setMinimumIntegerDigits(1);
                currencyFormatter.setGroupingUsed(true);
                return currencyFormatter.format(att.getValueDouble());
            }
            return att.getValueString();
         }
    }
    
    public static boolean isDocumentValidForClientRender(EboContext ctx, String objClassName ,long documentBoui)throws boRuntimeException
    {
        boolean result = false;
        String  XeoWin32Client_address = ctx.getXeoWin32Client_adress();
        if(DocumentHelper.isDocument(objClassName) &&                
           XeoWin32Client_address != null &&
           RegistryHelper.isClientConnected(XeoWin32Client_address) &&
           DocumentHelper.canClientOpenExtension(ctx,documentBoui))  
        {
            result = true;
        }
        else if ( DocumentHelper.isDocument(objClassName) && isXeoControlActive( ctx )  ) 
        {
            result= true;
        }
        return result;
  }
      
      private static final boolean isXeoControlActive( EboContext ctx )
      {
          boolean result = false;
          if( ctx.getRequest() != null )
          {
            if( ctx.getRequest().getCookies() != null )
            {
                for (int i = 0; i < ctx.getRequest().getCookies().length; i++) 
                {
                    if( "XeoControl".equals(ctx.getRequest().getCookies()[i].getName() ) )
                    {
                        result = true;
                        break;
                    }
                }
            }
          }
          return result;
      }
      
      public static final boolean showLink(Explorer explorer,  ColumnProvider expAttr)
      {
        if(explorer.getExplorerOptions().showLinks())
        {
            if("false".equals(expAttr.showLink()))
            {
                return false;
            }
            return true;
        }
        else
        {
            if("true".equals(expAttr.showLink()))
            {
                return true;
            }
            return false;
        }
      }
      
      public static final boolean openDocument(Explorer explorer,  ColumnProvider expAttr)
      {
        if(explorer.getExplorerOptions().openDocuments())
        {
            if("false".equals(expAttr.openDocument()))
            {
                return false;
            }
            return true;
        }
        else
        {
            if("true".equals(expAttr.openDocument()))
            {
                return true;
            }
            return false;
        }
      }
      
      public static String openDocumentinClient(
            boObject objParent,
            AttributeHandler atrParent,
            String docBoui,
            StringBuffer value,
            StringBuffer name,
            int tabIndex,
            docHTML doc,
            boolean isDisabled ,
            boolean isVisible ,            
            String options,
            String options2,
            String title,
            String query,
            String p_key
            )throws boRuntimeException
    {
        StringBuffer toPrint = new StringBuffer("");
        toPrint.append("<span style='cursor:hand;text-decoration:underline' ");
        toPrint.append(" title= 'Clique para vêr o documento' ");       
        if( isXeoControlActive( doc.getEboContext() ) )
        {
            toPrint.append(" onclick=\"javascript:window.top.XEOControl.documentManager.OpenWordDocument("+docBoui+");\">");
        }
        else
        {
            toPrint.append(" onclick=\" executeStaticMeth('"+p_key+"', 'openDocFromExplorer',['"+objParent.getBoui()+"','"+ docBoui +"']); \">");
        }
        toPrint.append(atrParent.getObject().getCARDID().toString());
        toPrint.append("</span>");
        return toPrint.toString(); 
    }
    
     public static String replaceSpecialCode(EboContext boctx, String code, Explorer tree, long boui, int treeListIndex)
     {
        try
        {
            String toRet = code;
            String sessionId = boctx.getBoSession().getId();
            String explorerKey = tree.getKey();
            toRet = toRet.replaceAll("sessionId", sessionId);
            toRet = toRet.replaceAll("explorerKey", explorerKey);
            toRet = toRet.replaceAll("objBoui", String.valueOf(boui));
            toRet = toRet.replaceAll("treeListIndex", String.valueOf(treeListIndex));
            
            return toRet;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return code;
     }
}
