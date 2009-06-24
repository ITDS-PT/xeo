package netgest.bo.impl.document.merge.gestemp.presentation;
import netgest.bo.runtime.EboContext;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.dochtml.docHTML;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import java.io.PrintWriter;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.dochtml.ICustomField;

public class PreviewDocumentRender implements ICustomField 
{
    public PreviewDocumentRender()
    {
    }

    public int render(EboContext ctx, docHTML_controler doccont, docHTML doc, boObject object, PrintWriter out, AttributeHandler relatedAtt) throws boRuntimeException
    {
        boolean useCache = useCache(object);
        return render(ctx, doccont, doc, object, out, relatedAtt, useCache);
    }
    public int render(EboContext ctx, docHTML_controler doccont, docHTML doc, boObject object, PrintWriter out, AttributeHandler relatedAtt, boolean useCache) throws boRuntimeException
    {
    
        String fileName = ""; //$NON-NLS-1$
        int ret = ICustomField.RENDER_CONTINUE;
        if("GESTEMP_Generated".equals(object.getName())) //$NON-NLS-1$
        {
            if(object.getAttribute("rosto").getValueLong() > 0) //$NON-NLS-1$
            {
                out.println("<table width='100%'><tr><td style='border-bottom:1px solid #DDDDDD;'>"); //$NON-NLS-1$
                out.println(Messages.getString("PreviewDocumentRender.4")); //$NON-NLS-1$
                out.println("</td></tr><tr><td>"); //$NON-NLS-1$
                //out.println("<img width='100%' src='__gesTempPreviewDocument.jsp?docid="+doc.getDocIdx()+"&method=edit&menu=yes&boui="+relatedAtt.getParent().getBoui()+"&docBoui="+ it.currentRow().getValueLong() +"' >");
                out.println("<iframe width='100%' height='600' src='__gesTempPreviewDocument.jsp?autoConvert=true&docid="+doc.getDocIdx()+"&method=edit&menu=yes&boui="+object.getBoui()+"&docBoui="+ object.getAttribute("rosto").getValueLong()+"&useCache="+(useCache?"true":"false")+"' >"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                out.println("</iframe>"); //$NON-NLS-1$
                out.println("</td></tr></table>"); //$NON-NLS-1$
            }
            if(object.getAttribute("doc").getValueLong() > 0) //$NON-NLS-1$
            {
                out.println("<table width='100%'><tr><td style='border-bottom:1px solid #DDDDDD;'>"); //$NON-NLS-1$
                out.println(Messages.getString("PreviewDocumentRender.18")); //$NON-NLS-1$
                out.println("</td></tr><tr><td>"); //$NON-NLS-1$
                //out.println("<img width='22%' src='__gesTempPreviewDocument.jsp?docid="+doc.getDocIdx()+"&method=edit&menu=yes&boui="+relatedAtt.getParent().getBoui()+"&docBoui="+ object.getAttribute("doc").getValueLong() +"' >");
                //out.println("<iframe frameborder='no' border='0' marginheight='0' marginwidth='0' width='100%' height='400' src='__gesTempPreviewDocument.jsp?docid="+doc.getDocIdx()+"&method=edit&menu=yes&boui="+object.getBoui()+"&docBoui="+ object.getAttribute("doc").getValueLong()+"&useCache="+(useCache?"true":"false") +"' >");
                
                out.println(Messages.getString("PreviewDocumentRender.3")+doc.getDocIdx()+"&method=edit&menu=yes&docBoui="+ object.getAttribute("doc").getValueLong()+"&useCache="+(useCache?"true":"false") +"' >"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                
                out.println("</iframe>"); //$NON-NLS-1$
                out.println("</td></tr></table>"); //$NON-NLS-1$
            } 
        }
        else if(!relatedAtt.isBridge())
        {
            if(object.getAttribute(relatedAtt.getName()).getValueLong() > 0)
            {
                out.println("<table width='100%'><tr><td style='border-bottom:1px solid #DDDDDD;'>"); //$NON-NLS-1$
                out.println(Messages.getString("PreviewDocumentRender.30")); //$NON-NLS-1$
                out.println("</td></tr><tr><td>"); //$NON-NLS-1$
                //out.println("<img width='100%' src='__gesTempPreviewDocument.jsp?docid="+doc.getDocIdx()+"&method=edit&menu=yes&boui="+relatedAtt.getParent().getBoui()+"&docBoui="+ it.currentRow().getValueLong() +"' >");
                out.println("<iframe width='100%' height='600' src='__gesTempPreviewDocument.jsp?autoConvert=true&docid="+doc.getDocIdx()+"&method=edit&menu=yes&boui="+relatedAtt.getParent().getBoui()+"&docBoui="+ object.getAttribute(relatedAtt.getName()).getValueLong()+"&useCache="+(useCache?"true":"false") +"' >"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                out.println("</iframe>"); //$NON-NLS-1$
                out.println("</td></tr></table>"); //$NON-NLS-1$
            }
        }
        else 
        {
            boBridgeIterator it =  object.getBridge( relatedAtt.getName() ).iterator(); 
            while( it.next() ) 
            {
                AttributeHandler atrFileName = it.currentRow().getObject().getAttribute("fileName"); //$NON-NLS-1$
                if( atrFileName != null )
                {
                    fileName = it.currentRow().getObject().getAttribute("fileName").getValueString(); //$NON-NLS-1$
                    if(!( fileName.toLowerCase().endsWith(".eml") ) ) //$NON-NLS-1$
                    {
                        out.println("<table id='previewTable' collspan='0' cellspacing='0' width='100%'>"); //$NON-NLS-1$
                        //<tr><td style='border-bottom:1px solid #DDDDDD;'>
                        //out.println("<b>Pré-Visualização do documento:</b><br>");
                        //</td></tr>
                        out.println("<tr><td>"); //$NON-NLS-1$
                        //out.println("<img width='100%' src='__gesTempPreviewDocument.jsp?docid="+doc.getDocIdx()+"&method=edit&menu=yes&boui="+relatedAtt.getParent().getBoui()+"&docBoui="+ it.currentRow().getValueLong() +"' >");
                        out.println("<iframe id='previewFrame1' scrolling='no' frameborder='no' border='0' marginheight='0' marginwidth='0' width='100%' height='600' src='__gesdocclf_previewdoc.jsp?autoConvert=true&docid="+doc.getDocIdx()+"&method=edit&menu=yes&docBoui="+ it.currentRow().getValueLong()+"&useCache="+(useCache?"true":"false") +"' >"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                        //out.println("<iframe width='100%' height='400' src='__gesTempPreviewDocument.jsp?docid="+doc.getDocIdx()+"&method=edit&menu=yes&boui="+relatedAtt.getParent().getBoui()+"&docBoui="+ it.currentRow().getValueLong()+"&useCache="+(useCache?"true":"false") +"' >");
                        out.println("</iframe>"); //$NON-NLS-1$
                        out.println("</td></tr></table>");  //$NON-NLS-1$
                    }
                }
            }
            
            //if( fileName != null && fileName.toLowerCase().endsWith(".doc") )
            //    out.println("<iframe id='coverFrame' scrolling='no' frameborder='no' border='0' marginheight='0' marginwidth='0'  style='position:relative;width:173px;height:33px;top:0;left:0;background-color:red;zOrder:1000'></iframe>");

            //out.println("<script language='JavaScript'>"); 
/*
            out.println("function resizeFrame() {");
            out.println("alert(document.getElementById('previewTable').parentElement.tagName);");
            out.println("}");
            out.println("document.getElementById('previewFrame1').attachEvent('onreadystatechange',resizeFrame);");
*/ 
            //if( fileName != null && fileName.toLowerCase().endsWith(".doc") )
            //    out.println("document.getElementById('coverFrame').style.top=-(document.body.clientHeight-95)+30;");

            //out.println("document.getElementById('previewFrame1').style.height=document.body.clientHeight-95;");

            //out.println("</script>");
            
        }
        return ret; 
    }

    public String getRelatedAttribute() throws boRuntimeException
    {
        return null;
    }
    
    private boolean useCache(boObject obj) throws boRuntimeException
    {
        //pode usar cache em todas as mensagens que usam template não editável em todas as que já estão completas
        if("message".equals(obj.getName()) || "message".equals(obj.getBoDefinition().getBoSuperBo())) //$NON-NLS-1$ //$NON-NLS-2$
        {
            if(obj.getAttribute("usedTemplate").getValueLong() > 0) //$NON-NLS-1$
            {
                if(obj.getAttribute("usedTemplate").getObject() != null) //$NON-NLS-1$
                {
                    if("0".equals(obj.getAttribute("usedTemplate").getObject().getAttribute("editavel").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    {
                        return true; 
                    }
                } 
            }
            //todas as mensagens em que já foi feito o enviar completar
            if(obj.getAttribute("dtdoc").getValueDate() != null) //$NON-NLS-1$
            {
                return true;
            }
            return false;
        }
        return true;
    }
}