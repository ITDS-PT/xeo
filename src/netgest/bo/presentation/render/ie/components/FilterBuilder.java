/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.components;

import java.io.IOException;
import java.io.PrintWriter;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.dochtml.docHTML_section;
import netgest.bo.dochtml.docHTML_sectionField;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.presentation.render.Browser;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.Explorer;
import netgest.bo.presentation.render.elements.Filter;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.userquery.userquery;


import netgest.bo.system.Logger;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class FilterBuilder {
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.presentation.render.ie.components.FilterBuilder");
    
    private static final char[] FILTER_IMG = ("<td style='padding-left:5px;' ><img class=\"buttonMenu\" src=\"" +
        Browser.getThemeDir() + "menu/filter.gif\" WIDTH=\"16\" HEIGHT=\"16\" /></td>").toCharArray();
    private static final char[] FILTER_BEGIN = "<td style='padding-left:5px;' ><span style='width:170px'>".toCharArray();
    private static final char[] FILTER_END = "</span></td>".toCharArray();
    private static final char[] FILTER_EMPTY = "<td style='DISPLAY: none' ><span style='width:170px'><SPAN class=selectBox value=''><TABLE style='DISPLAY: none' cellSpacing=0 cellPadding=2><TBODY><TR><TD val='0'>&nbsp;</TD></TR></TBODY></TABLE></SPAN></span></td>".toCharArray();

    /**
     *
     * @Company Enlace3
     * @since
     */
    public FilterBuilder() {
    }

    public static void writeFilter(PrintWriter out, Filter elementFilter,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        if (elementFilter.getCode() != null) {
            out.write(elementFilter.getCode().toCharArray());

            return;
        }

        Explorer tree = elementFilter.getExplorer();        

        boObjectList userFilters = null;
        try
        {
            userFilters = userquery.getUserQueries(doc.getEboContext(),
                    tree.p_bodef.getName());
            userFilters.beforeFirst();
        }
        catch (Exception e)
        {
            try{
                logger.finer(LoggerMessageLocalizer.getMessage("ERROR_DRAWING_USER_FILTER_QUERIES_FOR_DEFINITION")+"("+
                    tree.p_bodef.getName()+") "+LoggerMessageLocalizer.getMessage("FROM_USER")+"("+
                        doc.getEboContext().getBoSession().getPerformerBoui()+"): ", e);
            }catch(Exception _e){/*IGNORE*/}
            /*IGNORE*/
        }
        if(userFilters == null || userFilters.getRecordCount() == 0)
        {
            out.write(FILTER_EMPTY);
            return;
        }

        

        StringBuffer[] xInternal = new StringBuffer[userFilters.getRowCount() +
            1];
        StringBuffer[] xExternal = new StringBuffer[userFilters.getRowCount() +
            1];

        xInternal[0] = new StringBuffer("0");
        xExternal[0] = new StringBuffer("&nbsp;");

        int idx = 1;

        while (userFilters.next()) {
            xInternal[idx] = new StringBuffer(userFilters.getObject().getBoui() +
                    "");
            xExternal[idx++] = new StringBuffer(userFilters.getObject()
                                                           .getAttribute("name")
                                                           .getValueString());
        }

        if (tree.p_bouiUserQuery != -1) {
            //String sqlu=userquery.userQueryToSql( DOC.getEboContext() , tree.p_bouiUserQuery , true , tree.p_userParameters );
            //if ( sqlu!= null && sqlu.length()>0)
            // {
            boObject o = doc.getObject(tree.p_bouiUserQuery);

            if (o != null) {
                tree.p_filterName = o.getAttribute("name").getValueString();
            }
            // }
            else {
                tree.p_filterName = "";
            }

            xInternal[0] = new StringBuffer("0");
            xExternal[0] = new StringBuffer("&nbsp;");
        } else {
            tree.p_filterName = "";
        }

        docHTML_sectionField f;
        f = docHTML_sectionField.newCombo(new StringBuffer("qryObject_" +
                    tree.p_key), new StringBuffer("qryObject__" + tree.p_key),
                new StringBuffer("Objecto"),
                new StringBuffer(tree.p_filterName), xExternal, xInternal,
                false, false, new StringBuffer("setUserExplorerQueryBoui"), null, null);

        StringBuffer toPrint = new StringBuffer();
        out.write(FILTER_IMG);
        out.write(FILTER_BEGIN);

        docHTML_section.renderHTMLObject(toPrint, f, doc, docList);
        out.write(toPrint.toString().toCharArray());
        out.write(FILTER_END);
    }
}
