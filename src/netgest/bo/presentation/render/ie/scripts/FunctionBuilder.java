/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.scripts;

import java.io.IOException;
import java.io.PrintWriter;

import netgest.bo.presentation.render.HTMLCommon;
import netgest.bo.presentation.render.PageController;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class FunctionBuilder {
    public static final int FUNC_CALCULATOP = 0;
    private static final char[] FUNC_CALCULATOP_B = "function calculatop(x_ele){var x_ret=0;if (x_ele.tagName=='BODY') return 0;else  x_ret=x_ele.offsetTop-x_ele.scrollTop+calculatop(x_ele.parentElement);return x_ret}".toCharArray();
    public static final int FUNC_CALCULALEFT = 1;
    private static final char[] FUNC_CALCULALEFT_B = "function calculaleft(x_ele){var x_ret=0;if (x_ele.tagName=='BODY') return 0;else x_ret=x_ele.offsetLeft-x_ele.scrollLeft+calculaleft(x_ele.parentElement);return x_ret;}".toCharArray();
    public static final int FUNC_SETPREVIEWDOWN = 3;
    private static final char[] FUNC_SETPREVIEWDOWN_B = ("function setPreviewDown(treeName, menuName){if(document.getElementById('previewBottom').style.display != 'none'){return;}document.getElementById('tdResizeRight')"+
        ".style.display='none';document.getElementById('previewRight').style.display='none';document.getElementById('trResizeBottom').style.display='';document.getElementById('previewBottom').style.display='';document.getElementById"+
        "(\"menuframe\").contentWindow.bindMenu(treeName, menuName, 'preview', 'down');showDown.scripts = showRight.scripts;showDown.document.body.innerHTML = showRight.document.body.innerHTML}").toCharArray();
    public static final int FUNC_SETPREVIEWRIGHT = 4;
    private static final char[] FUNC_SETPREVIEWRIGHT_B = ("function setPreviewRight(treeName, menuName){if(document.getElementById('previewRight').style.display != 'none'){return;}if(document.getElementById('previewBottom')."+
        "style.display != 'none'){showRight.scripts = showDown.scripts;showRight.document.body.innerHTML = showDown.document.body.innerHTML;}document.getElementById('trResizeBottom').style.display='none';document.getElementById"+
        "('previewBottom').style.display='none';document.getElementById('tdResizeRight').style.display='';document.getElementById('previewRight').style.display='';document.getElementById(\"menuframe\").contentWindow.bindMenu(treeName, menuName, 'preview', 'right');}").toCharArray();
    public static final int FUNC_SETGROUP = 5;
    private static final char[] FUNC_SETGROUP_B = "function setGroup(treeName, menuName){var value = false;if(document.getElementById('groupbar').style.display == 'none') {document.getElementById('groupbar').style.display='';value = true;}else document.getElementById('groupbar').style.display='none';document.getElementById(\"menuframe\").contentWindow.bindMenu(treeName, menuName, 'group', value);}".toCharArray();
    public static final int FUNC_SETPARAM = 6;
    private static final char[] FUNC_SETPARAM_B = "function setParam(treeName, menuName){var value = false;if(document.getElementById('parambar').style.display == 'none') {document.getElementById('parambar').style.display='';value = true;}else document.getElementById('parambar').style.display='none';document.getElementById(\"menuframe\").contentWindow.bindMenu(treeName, menuName, 'parameters', value);}".toCharArray();
    public static final int FUNC_SETPREVIEWOFF = 7;
    private static final char[] FUNC_SETPREVIEWOFF_B = "function setPreviewOff(treeName, menuName){document.getElementById('trResizeBottom').style.display='none';document.getElementById('previewBottom').style.display='none';document.getElementById('tdResizeRight').style.display='none';document.getElementById('previewRight').style.display='none';document.getElementById(\"menuframe\").contentWindow.bindMenu(treeName, menuName, 'preview', 'off');}".toCharArray();
    public static final int FUNC_GETRUNTIME_STYLE = 8;
    private static final char[] FUNC_GETRUNTIME_STYLE_B = "function getRuntimeStyle(o){if(o.runtimeStyle) return o.runtimeStyle;else return o.style;}".toCharArray();
    public static final int FUNC_SO = 9;
    private static final char[] FUNC_SO_B = "function so(id){if(!_hsos[id]){_hsos[id]=new Object();hsos[id].hb=document.getElementById(id+'_body'); _hsos[id].id=id;}_hso=_hsos[id];}".toCharArray();
    public static final int FUNC_GET_TO_ELEMENT = 10;
    private static final char[] FUNC_GET_TO_ELEMENT_B = "function getToElement(e){if(e&&e.target) return e.relatedTarget;return window.event.toElement;}".toCharArray();
    public static final int FUNC_GET_ELEMENT = 11;
    private static final char[] FUNC_GET_ELEMENT_B = "function getElement(e){if(e&&e.target) return e.target;return window.event.srcElement;}".toCharArray();
    public static final int ON_SELECT_START = 12;
    private static final char[] ON_SELECT_START_B = "document.onselectstart=function(){var s = event.srcElement.tagName;if (s != \"INPUT\" && s != \"TEXTAREA\") event.returnValue = false;}".toCharArray();
    public static final int TREE_VARS = 13;
    private static final char[] TREE_VARS_B = "var _hsos=[];var _hso=null;var ii=0;var lastActive=new Date('01/01/1970');".toCharArray();
    public static final int FUNC_ACTIVETHIS = 14;
    private static final char[] FUNC_ACTIVETHIS_B = "function activethis(){if( new Date() - lastActive > 1000) {var xwin=winmain();if(xwin){xwin.status=\"ok...\"+(ii++);if(xwin.activeDocByIdx) xwin.activeDocByIdx(getIDX())}}lastActive=new Date();}".toCharArray();
    public static final int ON_MOUSE_DOWN = 15;
    private static final char[] ON_MOUSE_DOWN_B = "document.onmousedown=activethis;".toCharArray();
    public static final int FUNC_CALL_PARENT_REFRESH_FRAME = 16;
    private static final char[] FUNC_CALL_PARENT_REFRESH_FRAME_B_1 = "function callParentRefreshFrame(clientIDX, frmName){var windowToUpdate=clientIDX;var w=parent.ndl[windowToUpdate];if(w){var ifrm=w.htm.getElementsByTagName('IFRAME'); for(var i=0; i < ifrm.length ; i++){if ( ifrm[i].id == frmName ){var wDocfrms=ifrm[i].contentWindow.document.getElementsByTagName('IFRAME');".toCharArray();
    private static final char[] FUNC_CALL_PARENT_REFRESH_FRAME_B_2 = "for(var z=0;  z < wDocfrms.length ; z++){if (  wDocfrms[z].id == \"refreshframe\" ){wDocfrms[z].contentWindow.refreshValues();}}}}}}".toCharArray();
    public static final int FUNC_UPDATE_FRAME = 17;
    private static final char[] FUNC_UPDATE_FRAME_B = "function updateFrame(wFrm, incName){wDoc=wFrm.contentWindow;var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');for(var z=0;  z < wDocfrms.length ; z++){if (  wDocfrms[z].id == incNameH ){wDocfrms[z].contentWindow.submitGrid();return;}else{updateFrame(wDocfrms[z]);}}}".toCharArray();
    public static final int FUNC_UPDATE_LOOK_ATRIB = 18;
    private static final char[] FUNC_UPDATE_LOOK_ATRIB_B = "function updateLookupAttribute(clientIDX, frmName){var windowToUpdate=clientIDX;var w=parent.ndl[windowToUpdate];if(w){var ifrm=w.htm.getElementsByTagName('IFRAME');var xw;for(var i=0; i < ifrm.length ; i++){if ( ifrm[i].id == frmName ){updateFrame(ifrm[i]);}}}}".toCharArray();
    public static final int FUNC_UPDATE_FRAME_2 = 19;
    private static final char[] FUNC_UPDATE_FRAME_2_B = "function updateFrame(wFrm, tblLookName){wDoc=wFrm.contentWindow;var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');toRet=false;for(var z=0;  z < wDocfrms.length ; z++){var xok=false;xw=wDocfrms[z].contentWindow.document.all;var toR=[];for(var j=0; j< xw.length; j++){if ( xw[j].id == tblLookName ){toR[toR.length]=xw[j];}}for(var y=0; y < toR.length; y++){toR[y].outerHTML=toRender.innerHTML;}updateFrame(wDocfrms[z]);}}".toCharArray();
    public static final int FUNC_UPDATE_IN_FRAMES = 20;
    private static final char[] FUNC_UPDATE_IN_FRAMES_B = "function updateInFrames(clientIDX){var windowToUpdate=clientIDX;var w=parent.ndl[windowToUpdate];if(w){var ifrm=w.htm.getElementsByTagName('IFRAME');var xw;for(var i=0; i < ifrm.length ; i++){var xok=updateFrame(ifrm[i]);if ( xok ) break;}}}".toCharArray();
    public static final int FUNC_UPDATE_LOOK_ATRIB_2 = 21;
    private static final char[] FUNC_UPDATE_LOOK_ATRIB_2_B_1 = "function updateLookupAttribute(clientIDX, tblLookName, name){var windowToUpdate=clientIDX;var xok=false;var w=parent.ndl[windowToUpdate];if(w){var ifrm=w.htm.getElementsByTagName('IFRAME');var xw;for(var i=0; i < ifrm.length ; i++){xw=ifrm[i].contentWindow.document.all;var toR=[];for(var z=0; z< xw.length; z++){if ( xw[z].id == tblLookName ){toR[toR.length]=xw[z];}}for(var y=0; y < toR.length; y++)".toCharArray();
    private static final char[] FUNC_UPDATE_LOOK_ATRIB_2_B_2 = "{toR[y].outerHTML=toRender.innerHTML;var xele=ifrm[i].contentWindow.document.getElementsByName(name);for( var z=0; z<xele.length;z++)xele[z].original=xele[0].value;xok=true;}}if ( !xok ) updateInFrames();}}".toCharArray();
    public static final int FUNC_UPDATE_OBJ = 22;
    private static final char[] FUNC_UPDATE_OBJ_B = "function updateObj(searchClientIDX, boui){var w=parent.ndl[searchClientIDX];  if( w ){var ifrm=w.htm.getElementsByTagName('IFRAME');var xw;ifrm[0].contentWindow.findframe.submitSelectOne2(boui);}}".toCharArray();
    public static final int FUNC_START_FILTER = 23;
    private static final char[] FUNC_START_FILTER_B = "function startInitialFilter(jspName, expKey){winmain().openDocUrl(',800,580',jspName,'?explorerKey='+expKey+'&docid='+getDocId()+'&relatedIDX='+getIDX()+'&referenceFrame='+getReferenceFrame(),'lookup');}".toCharArray();
    /**
     *
     * @Company Enlace3
     * @since
     */
    private FunctionBuilder() {
    }

    public static boolean canWrite(int[] functionCode, PageController control) {
        if (functionCode != null) {
            for (int i = 0; i < functionCode.length; i++) {
                if (canWrite(functionCode[i], control)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean canWrite(int functionCode, PageController control) {
        if (control.canWriteFunction(functionCode)) {
            switch (functionCode) {
            case FUNC_CALCULALEFT:
            case FUNC_CALCULATOP:
            case FUNC_SETPREVIEWDOWN:
            case FUNC_SETPREVIEWRIGHT:
            case FUNC_SETGROUP:
            case FUNC_SETPARAM:
            case FUNC_SETPREVIEWOFF:
            case FUNC_GETRUNTIME_STYLE:
            case FUNC_SO:
            case FUNC_GET_TO_ELEMENT:
            case FUNC_GET_ELEMENT:
            case ON_SELECT_START:
            case TREE_VARS:
            case FUNC_ACTIVETHIS:
            case ON_MOUSE_DOWN:
            case FUNC_CALL_PARENT_REFRESH_FRAME:
            case FUNC_UPDATE_FRAME:
            case FUNC_UPDATE_LOOK_ATRIB:
            case FUNC_UPDATE_FRAME_2:
            case FUNC_UPDATE_IN_FRAMES:
            case FUNC_UPDATE_LOOK_ATRIB_2:
            case FUNC_UPDATE_OBJ:
            case FUNC_START_FILTER:
                return true;

            default:
                return false;
            }
        }

        return false;
    }

    public static boolean writeFunction(PrintWriter out, int[] functionCode,
        PageController control) throws IOException {
        boolean toRet = false;

        if (functionCode != null) {
            for (int i = 0; i < functionCode.length; i++) {
                if (writeFunction(out, functionCode[i], control)) {
                    out.write(HTMLCommon.UTIL_NEW_LINE);
                    toRet = true;
                }
            }
        }

        return toRet;
    }

    public static boolean writeFunction(PrintWriter out, int functionCode,
        PageController control) throws IOException {
        if (control.canWriteFunction(functionCode)) {
            switch (functionCode) {
            case FUNC_CALCULALEFT:
                out.write(FUNC_CALCULALEFT_B);
                control.markWriteFunction(FUNC_CALCULALEFT);

                break;

            case FUNC_CALCULATOP:
                out.write(FUNC_CALCULATOP_B);
                control.markWriteFunction(FUNC_CALCULATOP);

                break;

            case FUNC_SETPREVIEWDOWN:
                out.write(FUNC_SETPREVIEWDOWN_B);
                control.markWriteFunction(FUNC_SETPREVIEWDOWN);

                break;

            case FUNC_SETPREVIEWRIGHT:
                out.write(FUNC_SETPREVIEWRIGHT_B);
                control.markWriteFunction(FUNC_SETPREVIEWRIGHT);

                break;

            case FUNC_SETPARAM:
                out.write(FUNC_SETPARAM_B);
                control.markWriteFunction(FUNC_SETPARAM);

                break;

            case FUNC_SETGROUP:
                out.write(FUNC_SETGROUP_B);
                control.markWriteFunction(FUNC_SETGROUP);

                break;

            case FUNC_SETPREVIEWOFF:
                out.write(FUNC_SETPREVIEWOFF_B);
                control.markWriteFunction(FUNC_SETPREVIEWOFF);

                break;

            case FUNC_GETRUNTIME_STYLE:
                out.write(FUNC_GETRUNTIME_STYLE_B);
                control.markWriteFunction(FUNC_GETRUNTIME_STYLE);

                break;

            case FUNC_SO:
                out.write(FUNC_SO_B);
                control.markWriteFunction(FUNC_SO);

                break;

            case FUNC_GET_TO_ELEMENT:
                out.write(FUNC_GET_TO_ELEMENT_B);
                control.markWriteFunction(FUNC_GET_TO_ELEMENT);

                break;

            case FUNC_GET_ELEMENT:
                out.write(FUNC_GET_ELEMENT_B);
                control.markWriteFunction(FUNC_GET_ELEMENT);

                break;

            case ON_SELECT_START:
                out.write(ON_SELECT_START_B);
                control.markWriteFunction(ON_SELECT_START);

                break;

            case TREE_VARS:
                out.write(TREE_VARS_B);
                control.markWriteFunction(TREE_VARS);

                break;

            case FUNC_ACTIVETHIS:
                out.write(FUNC_ACTIVETHIS_B);
                control.markWriteFunction(FUNC_ACTIVETHIS);

                break;

            case ON_MOUSE_DOWN:
                out.write(ON_MOUSE_DOWN_B);
                control.markWriteFunction(ON_MOUSE_DOWN);

                break;

            case FUNC_CALL_PARENT_REFRESH_FRAME:
                out.write(FUNC_CALL_PARENT_REFRESH_FRAME_B_1);
                out.write(FUNC_CALL_PARENT_REFRESH_FRAME_B_2);
                control.markWriteFunction(FUNC_CALL_PARENT_REFRESH_FRAME);

                break;

            case FUNC_UPDATE_FRAME:
                out.write(FUNC_UPDATE_FRAME_B);
                control.markWriteFunction(FUNC_UPDATE_FRAME);

                break;

            case FUNC_UPDATE_LOOK_ATRIB:
                out.write(FUNC_UPDATE_LOOK_ATRIB_B);
                control.markWriteFunction(FUNC_UPDATE_LOOK_ATRIB);

                break;

            case FUNC_UPDATE_FRAME_2:
                out.write(FUNC_UPDATE_FRAME_2_B);
                control.markWriteFunction(ON_MOUSE_DOWN);

                break;

            case FUNC_UPDATE_IN_FRAMES:
                out.write(FUNC_UPDATE_IN_FRAMES_B);
                control.markWriteFunction(FUNC_UPDATE_IN_FRAMES);

                break;

            case FUNC_UPDATE_LOOK_ATRIB_2:
                out.write(FUNC_UPDATE_LOOK_ATRIB_2_B_1);
                out.write(FUNC_UPDATE_LOOK_ATRIB_2_B_2);
                control.markWriteFunction(FUNC_UPDATE_LOOK_ATRIB_2);

                break;

            case FUNC_UPDATE_OBJ:
                out.write(FUNC_UPDATE_OBJ_B);
                control.markWriteFunction(FUNC_UPDATE_OBJ);

                break;
            
            case FUNC_START_FILTER:
                out.write(FUNC_START_FILTER_B);
                control.markWriteFunction(FUNC_START_FILTER);

                break;

            default:
                return false;
            }

            return true;
        }

        return false;
    }
}
