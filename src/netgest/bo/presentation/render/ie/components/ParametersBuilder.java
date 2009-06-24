/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.components;

import java.io.IOException;
import java.io.PrintWriter;

import java.net.URLEncoder;

import netgest.bo.boConfig;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.localized.JSPMessages;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.Explorer;
import netgest.bo.presentation.render.elements.Parameters;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.userquery.userquery;

/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class ParametersBuilder {
//    private static final char[] PARAM_1 = "\n     <tr class='param' id ='tableParamToExclude' >".toCharArray();
//    private static final char[] PARAM_2 = "\n     <tr class='param' id ='tableParamToExclude' height=\"100%\">".toCharArray();
    private static final char[] PARAM_3 = "\n                           <TABLE  style='table-layout:fixed;height:100%;width:100%' id='tableParamToExclude2' border=0 cellSpacing=\"0\" cellPadding=\"0\" >".toCharArray();
    private static final char[] PARAM_4 = "\n                              <colgroup/>".toCharArray();
    //private static final char[] PARAM_5 = "\n                              <col width=\"16\"/>".toCharArray();
    private static final char[] PARAM_6 = "\n                              <col/>".toCharArray();
//    private static final char[] PARAM_7 = "\n                              <col width=\"22\"/>".toCharArray();
    private static final char[] PARAM_8 = "\n                              <TBODY>".toCharArray();
    private static final char[] PARAM_9 = "\n                                 <TR style='height:100%'>".toCharArray();
    private static final char[] PARAM_10 = "\n                                    <TD  valign=\"top\" >".toCharArray();
    private static final char[] PARAM_11 = "\n                                       <div width=\"100%\" height=\"100%\">".toCharArray();
    private static final String PARAM_12 = "object=";
    private static final String PARAM_13 = "&docid=";
    private static final String PARAM_14 = "&referenceFrame=";
    private static final String PARAM_15 = "&reference=";
    private static final String PARAM_16 = "&xmlFilter=";
    private static final char[] PARAM_17 = "\n                                          <IFRAME  id='params".toCharArray();
    private static final char[] PARAM_18 = "' src='__queryParams.jsp?explorer=true&".toCharArray();
    private static final char[] PARAM_19 = "' frameBorder=0 width='100%' scrolling=no height='100%'></IFRAME>".toCharArray();
//    private static final char[] PARAM_20 = "  ></IFRAME>".toCharArray();
   // private static final char[] PARAM_21 = "' frameBorder=0 scrolling='no' ></IFRAME>".toCharArray();
    private static final char[] PARAM_22 = "\n                                       </div>".toCharArray();
    private static final char[] PARAM_23 = "\n                                    </TD>".toCharArray();
    private static final char[] PARAM_24 = "\n                                 </TR>".toCharArray();
    private static final char[] PARAM_25 = "\n                              </TBODY>".toCharArray();
    private static final char[] PARAM_26 = "\n                           </TABLE>".toCharArray();
    private static final String PARAM_NO_PARAMstr="<p>"+JSPMessages.getString("ParametersBuilder1")+"</p>";
    private static final char[] PARAM_NO_PARAM = PARAM_NO_PARAMstr.toCharArray();

//    private static final char[] PARAM_27 = "\n                      </td>".toCharArray();
//    private static final char[] PARAM_28 = "\n                  </tr>".toCharArray();

    /**
     *
     * @Company Enlace3
     * @since
     */
    public ParametersBuilder() {
    }

    public static void writeParameters(PrintWriter out,
        Parameters elementParam, docHTML doc, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        if (elementParam.getCode() != null) {
            out.write(elementParam.getCode().toCharArray());

            return;
        }

        Explorer explorer = elementParam.getTreeRuntime();
        String pageName = elementParam.getPageName();
        explorer.p_haveErrors = true;

        boolean hParameters = explorer.haveParameters(doc.getEboContext());
        boolean hBlankParameters = explorer.haveBlankParameters(doc.getEboContext());

        if (!hBlankParameters && hParameters) {
            if (!explorer.analizeSql(doc.getEboContext())) {
                hBlankParameters = true;
            }
        }

        if (hParameters) {
//            if (!hBlankParameters) {
//                out.write(PARAM_1);
//            } else {
//                out.write(PARAM_2);
//            }

//            out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);
            out.write(PARAM_3);
            out.write(PARAM_4);
    //        out.write(PARAM_5);
            out.write(PARAM_6);
      //      out.write(PARAM_7);
            out.write(PARAM_8);
            out.write(PARAM_9);
            out.write(PARAM_10);
            out.write(PARAM_11);

            String parameters = PARAM_12 + explorer.p_bodef.getName() + PARAM_13 +
                doc.getDocIdx() + PARAM_14 +
                pageName + PARAM_15 + explorer.p_key;

            if (explorer.p_bouiUserQuery != -1) {
                String aux = userquery.userQueryToXML(doc.getEboContext(),
                    explorer.p_bouiUserQuery, true, explorer.p_userParameters);
                aux = URLEncoder.encode( aux, boConfig.getEncoding() );
                parameters += (PARAM_16 + aux); //retorna um xml com os parametros já definidos
            }

//            if (!hBlankParameters) {
                out.write(PARAM_17);
                out.write(explorer.p_key.toCharArray());
                out.write(PARAM_18);
                out.write(parameters.toCharArray());
                out.write(PARAM_19);
//                out.write(explorer.p_key.toCharArray());
//                out.write(PARAM_20);
//            } else {
//                //            out.write("                                          <IFRAME id='params"+tree.p_key + "' src='__queryParams.jsp?" + parameters +"' frameBorder=0 width='100%' height='100%' scrolling=no height='100%' tabindex='125'></IFRAME>");
//                out.write(PARAM_17);
//                out.write(explorer.p_key.toCharArray());
//                out.write(PARAM_18);
//                out.write(parameters.toCharArray());
//                out.write(PARAM_21);
//            }

            out.write(PARAM_22);
            out.write(PARAM_23);
            out.write(PARAM_24);
            out.write(PARAM_25);
            out.write(PARAM_26);
//            out.write(PARAM_27);
//            out.write(PARAM_28);
        }
        else
        {
            out.write(PARAM_NO_PARAM);
        }
    }
}
