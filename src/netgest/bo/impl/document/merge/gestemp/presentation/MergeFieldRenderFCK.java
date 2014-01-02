package netgest.bo.impl.document.merge.gestemp.presentation;
import netgest.bo.runtime.EboContext;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.dochtml.docHTML;
import netgest.bo.runtime.boObject;
import java.io.PrintWriter;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.dochtml.ICustomField;

public class MergeFieldRenderFCK implements ICustomField 
{
    public int render(EboContext ctx, docHTML_controler doccont, docHTML doc, boObject object, PrintWriter out, AttributeHandler relatedAtt) throws boRuntimeException
    {
        int ret = ICustomField.RENDER_CONTINUE;
        
        if( ctx.getRequest().getParameter("actRenderObj") != null )
        {
            out.println(getButton(1, "2"));
            String fckEditor = "GESTEMP_EMAILTexto__"+object.getBoui()+"__texto";
            composeScript( ctx, out, Long.parseLong( ctx.getRequest().getParameter("actRenderObj") ), fckEditor );
        }
        
//        boObject xobj = object.getParent();
//        
//        if( xobj != null )
//        {
//            out.println(getButton(1, "2"));
//            String fckEditor = "GESTEMP_EMAILTexto__"+object.getBoui()+"__texto";
//            composeScript( ctx, out, xobj.getBoui(), fckEditor );
//        }
        return ret;
    }

    private static String getButton(int pos, String tabIndex)
    {
        return Messages.getString("MergeFieldRenderFCK.6");
    }

    public String getRelatedAttribute() throws boRuntimeException
    {
        return null;
    }
    
    public void composeScript( EboContext ctx, PrintWriter out, long boui, String fckEditor ) throws boRuntimeException
    {
        out.println("<script>");
        out.println("var xid=    document.getElementsByName('"+fckEditor+"')[0].id;");
        
        out.println("function addMergeField() {"); 
        out.println("var url = '__gesTempChooseMergeField.jsp?templateObj="+boui+"&NOCACHE='+new Date();" );
        out.println("var args = [ '' ]");
        out.println("window.showModalDialog(url,args,\"dialogHeight: 500px; dialogWidth: 290px; edge: raised;center: No;  help: No; scroll: yes; resizable: yes; status: no;\");");
        out.println("if( args[0].length > 0 )");
        out.println("{");
        out.println("   var fckObj=FCKeditorAPI.GetInstance(xid);");
        out.println("   var insType = args[0].substring(0,args[0].indexOf(':')); ");
        out.println("   var insValue = args[0].substring(args[0].indexOf(':') + 1); "); 
        out.println("   if( insType == 'FIELD' ) {");
        out.println("       fckObj.InsertHtml('#' + insValue + '#');");
        out.println("   } else if ( insType == 'BOOKMARK' ) {");
        out.println("       fckObj.InsertHtml('[' + insValue + ']['+ insValue + ']');");
        out.println("   } else if ( insType == 'BOOKMARKFIELD' ) {");
        out.println("       fckObj.InsertHtml('#' + insValue.split('|')[1] + '#');");
        out.println("   }");
        out.println("}"); 


//        out.println("if( args[0].length > 0 )");
//        out.println("{");
////        out.println("    var fckObj=FCKeditorAPI.GetInstance(xid);");
////        out.println("    fckObj.InsertHtml('#' + args[0] + '#');");
//        out.println("}");
        out.println("}"); 
        out.println("</script>");
    }

}