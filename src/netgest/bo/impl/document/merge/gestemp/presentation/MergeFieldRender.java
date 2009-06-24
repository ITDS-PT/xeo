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

public class MergeFieldRender implements ICustomField 
{
    public int render(EboContext ctx, docHTML_controler doccont, docHTML doc, boObject object, PrintWriter out, AttributeHandler relatedAtt) throws boRuntimeException
    {
        int ret = ICustomField.RENDER_CONTINUE;
        if( ctx.getRequest().getParameter("actRenderObj") != null ) //$NON-NLS-1$
        {
            out.println(getButton(1, "2")); //$NON-NLS-1$
            composeScript( ctx, out, Long.parseLong( ctx.getRequest().getParameter("actRenderObj") ) ); //$NON-NLS-1$
        }
        
//        if( object.getParent() != null )
//        {
//            out.println(getButton(1, "2"));
//            composeScript( ctx, out, object.getParent().getBoui() );
//        }
        return ret;
    }

    private static String getButton(int pos, String tabIndex)
    {
        return Messages.getString("MergeFieldRender.3"); //$NON-NLS-1$
    }

    public String getRelatedAttribute() throws boRuntimeException
    {
        return null;
    }
    
    public void composeScript( EboContext ctx, PrintWriter out, long boui ) throws boRuntimeException
    {
        out.println("<script>"); //$NON-NLS-1$
        out.println("var txtAreaObj=null;"); //$NON-NLS-1$
        out.println("var txtAreaPos=null;"); //$NON-NLS-1$
        out.println("   txtAreaObj=document.getElementsByTagName('textArea')[0];"); //$NON-NLS-1$
        out.println("   txtAreaPos=txtAreaObj.value.length;"); //$NON-NLS-1$

        out.println("function checkVars()"); //$NON-NLS-1$
        out.println("{"); //$NON-NLS-1$
        //out.println("if (txtAreaObj==null){          ");
        //out.println("}");
        out.println("}"); //$NON-NLS-1$
        //out.println("    checkVars();");

        
        out.println("function saveRange()"); //$NON-NLS-1$
        out.println("{"); //$NON-NLS-1$
        out.println("    checkVars();"); //$NON-NLS-1$
        out.println("    txtAreaPos = caretPos(event.srcElement);"); //$NON-NLS-1$
        out.println("}"); //$NON-NLS-1$

        out.println("function caretPos( txtArea ){      "); //$NON-NLS-1$
        out.println("    checkVars();"); //$NON-NLS-1$
        out.println("var i=txtArea.value.length+1;          "); //$NON-NLS-1$
        out.println("if (txtArea.createTextRange){          "); //$NON-NLS-1$
        out.println("theCaret = document.selection.createRange().duplicate(); "); //$NON-NLS-1$
        out.println("while ( theCaret.parentElement() == txtArea && theCaret.move('character',1)==1 ) --i; "); //$NON-NLS-1$
        out.println("} "); //$NON-NLS-1$
        out.println("return i==txtArea.value.length+1?-1:i; "); //$NON-NLS-1$
        out.println("}"); //$NON-NLS-1$

        out.println("txtAreaObj.attachEvent( 'onkeyup',saveRange);"); //$NON-NLS-1$
        out.println("txtAreaObj.attachEvent( 'onkeydown',saveRange);"); //$NON-NLS-1$
        out.println("txtAreaObj.attachEvent( 'onclick',saveRange);"); //$NON-NLS-1$
        
        out.println("function addMergeField() {");  //$NON-NLS-1$
        out.println("    checkVars();"); //$NON-NLS-1$
        out.println("var url = '__gesTempChooseMergeField.jsp?templateObj="+boui+"&NOCACHE='+new Date();" ); //$NON-NLS-1$ //$NON-NLS-2$
        out.println("var args = [ '' ]"); //$NON-NLS-1$
        out.println("window.showModalDialog(url,args,\"dialogHeight: 500px; dialogWidth: 290px; edge: raised;center: No;  help: No; scroll: yes; resizable: yes; status: no;\");"); //$NON-NLS-1$
        out.println("if( args[0].length > 0 )"); //$NON-NLS-1$
        out.println("{"); //$NON-NLS-1$
        out.println("   var currPos = txtAreaPos - (txtAreaObj.value.split('\\n').length - (txtAreaObj.value.split('\\n').length==0?1:2));"); //$NON-NLS-1$
        //out.println("   alert(txtAreaObj.value.split('\\n').length) ");
        out.println("   var insType = args[0].substring(0,args[0].indexOf(':')); "); //$NON-NLS-1$
        out.println("   var insValue = args[0].substring(args[0].indexOf(':') + 1); ");  //$NON-NLS-1$
        out.println("   if( insType == 'FIELD' ) {"); //$NON-NLS-1$
        out.println("       txtAreaObj.value = txtAreaObj.value.substring(0,currPos  ) + '#' + insValue + '#' + txtAreaObj.value.substring(currPos);"); //$NON-NLS-1$
        out.println("   } else if ( insType == 'BOOKMARK' ) {"); //$NON-NLS-1$
        out.println("       txtAreaObj.value = txtAreaObj.value.substring(0,currPos  ) + '[' + insValue + ']['+ insValue + ']' + txtAreaObj.value.substring(currPos);"); //$NON-NLS-1$
        out.println("   } else if ( insType == 'BOOKMARKFIELD' ) {"); //$NON-NLS-1$
        out.println("       txtAreaObj.value = txtAreaObj.value.substring(0,currPos  ) + '#' + insValue.split('|')[1] + '#' + txtAreaObj.value.substring(currPos);"); //$NON-NLS-1$
        out.println("   }"); //$NON-NLS-1$
        out.println("}");  //$NON-NLS-1$
        out.println("}");  //$NON-NLS-1$
        out.println("</script>"); //$NON-NLS-1$
    }

}