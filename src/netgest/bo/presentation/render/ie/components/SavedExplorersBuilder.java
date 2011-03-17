/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.components;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.dochtml.docHTML_section;
import netgest.bo.dochtml.docHTML_sectionField;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.presentation.render.Browser;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.Explorer;
import netgest.bo.presentation.render.elements.Filter;
import netgest.bo.presentation.render.elements.SavedExplorers;
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
public class SavedExplorersBuilder {
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.presentation.render.ie.components.SavedExplorersBuilder");
    
    private static final char[] FILTER_IMG = ("<td style='padding-left:0px;' ><img class=\"buttonMenu\" src=\"" +
        Browser.getThemeDir() + "menu/views.gif\" WIDTH=\"16\" HEIGHT=\"16\" /></td>").toCharArray();
    private static final char[] FILTER_BEGIN = "<td style='padding-left:5px;' ><span style='width:170px'>".toCharArray();
    private static final char[] FILTER_END = "</span></td>".toCharArray();
    private static final char[] FILTER_EMPTY = "<td style='DISPLAY: none' ><span style='width:170px'><SPAN class=selectBox value=''><TABLE style='DISPLAY: none' cellSpacing=0 cellPadding=2><TBODY><TR><TD val='0'>&nbsp;</TD></TR></TBODY></TABLE></SPAN></span></td>".toCharArray();
    private static final String boql="select Ebo_treeDef where expName = ? and objectType = ? and ( share in ( select iXEOUser.queues where boui=CTX_PERFORMER_BOUI) or  share in ( select iXEOUser.groups where boui=CTX_PERFORMER_BOUI) or share in ( select iXEOUser.roles where boui=CTX_PERFORMER_BOUI) or share =CTX_PERFORMER_BOUI ) order by name";
    /**
     *
     * @Company Enlace3
     * @since
     */
    public SavedExplorersBuilder() {
    }

    public static void writeSavedExplorers(PrintWriter out, SavedExplorers elementSvExplorer,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        if (elementSvExplorer.getCode() != null) {
            out.write(elementSvExplorer.getCode().toCharArray());
            return;
        }

        Explorer tree = elementSvExplorer.getExplorer();        

        boObjectList userSvExplorer = null;
        try
        {
            String expName = tree.getExplorerName() == null ? "":tree.getExplorerName();
            int pos = 0;
            if((pos = expName.indexOf("--")) > 0)
            {
                expName = expName.substring(0, pos);
                String aux = tree.p_bodef.getName();
            }
            String _boql="select Ebo_treeDef where expName = '"+expName+"' and objectType = '"+tree.p_bodef.getName()+"' and ( share in ( select iXEOUser.queues where boui=CTX_PERFORMER_BOUI) or  share in ( select iXEOUser.groups where boui=CTX_PERFORMER_BOUI) or share in ( select iXEOUser.roles where boui=CTX_PERFORMER_BOUI) or share =CTX_PERFORMER_BOUI ) order by name";
            userSvExplorer = boObjectList.list(doc.getEboContext(), _boql, 1 ,100);
            userSvExplorer.beforeFirst();
        }
        catch (Exception e)
        {
            try{
                logger.finer(LoggerMessageLocalizer.getMessage("ERROR_DRAWING_USER_FILTER_QUERIES_FOR_DEFINITION")+"("+
                    tree.p_bodef.getName()+") do utilizador("+
                        doc.getEboContext().getBoSession().getPerformerBoui()+"): ", e);
            }catch(Exception _e){/*IGNORE*/}
            /*IGNORE*/
        }
        if(userSvExplorer == null || userSvExplorer.getRecordCount() == 0)
        {
            out.write(FILTER_EMPTY);
            return;
        }

        

        StringBuffer[] xInternal = new StringBuffer[userSvExplorer.getRowCount() +
            1];
        StringBuffer[] xExternal = new StringBuffer[userSvExplorer.getRowCount() +
            1];

        xInternal[0] = new StringBuffer("0");
        xExternal[0] = new StringBuffer("&nbsp;");

        int idx = 1;
        ArrayList r = new ArrayList();
        int svExp = 1;
        while (userSvExplorer.next()) {
            if(!r.contains(String.valueOf(userSvExplorer.getObject().getBoui())))
            {
                xInternal[idx] = new StringBuffer(userSvExplorer.getObject().getBoui() +
                        "");
                xExternal[idx++] = new StringBuffer(userSvExplorer.getObject()
                                                               .getAttribute("name")
                                                               .getValueString());
                r.add(String.valueOf(userSvExplorer.getObject().getBoui()));
                svExp++;
            }
        }

        if(svExp < xInternal.length)
        {
            xInternal = shrink(xInternal, svExp);
            xExternal = shrink(xExternal, svExp);
        }
        if (tree.p_svExplorer != -1) {
            //String sqlu=userquery.userQueryToSql( DOC.getEboContext() , tree.p_bouiUserQuery , true , tree.p_userParameters );
            //if ( sqlu!= null && sqlu.length()>0)
            // {
            boObject o = doc.getObject(tree.p_svExplorer);

            if (o != null) {
                tree.p_svExplorerName = o.getAttribute("name").getValueString();
            }
            // }
            else {
                tree.p_svExplorerName = "";
            }

            xInternal[0] = new StringBuffer("0");
            xExternal[0] = new StringBuffer("&nbsp;");
        } else {
            tree.p_filterName = "";
        }

        docHTML_sectionField f;
        f = docHTML_sectionField.newCombo(new StringBuffer("svExplObject_" +
                    tree.p_key), new StringBuffer("svExplObject__" + tree.p_key),
                new StringBuffer("Objecto"),
                new StringBuffer(tree.p_svExplorerName), xExternal, xInternal,
                false, false, new StringBuffer("setUserSvExplorerBoui"), null, null);

        StringBuffer toPrint = new StringBuffer();
        out.write(FILTER_IMG);
        out.write(FILTER_BEGIN);

        docHTML_section.renderHTMLObject(toPrint, f, doc, docList);
        out.write(toPrint.toString().toCharArray());
        out.write(FILTER_END);
    }
    
    private static StringBuffer[] shrink(StringBuffer[] arr, int lastPos) 
    {
        StringBuffer[] newValue = new StringBuffer[lastPos];
        System.arraycopy(arr, 0, newValue, 0, lastPos);
        return newValue;
    }
}
