package netgest.bo.impl.document.merge.gestemp.presentation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import netgest.bo.def.boDefHandler;
import netgest.bo.dochtml.*;
import netgest.bo.runtime.*;
import java.io.PrintWriter;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.workflow.*;
import netgest.utils.Counter;
import netgest.utils.ngtXMLHandler;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class MapTempAtribRender implements ICustomField
{
    private String relatedAttName;
    private String id;
    private String name;
    
    private String idLabel;
    private String nameLabel;
    
    private String idShowLabel;
    private String nameShowLabel;
    private ngtXMLHandler xml;
    
    public int render(EboContext ctx, docHTML_controler doccont, docHTML doc , boObject object, PrintWriter out, AttributeHandler relatedAtt) throws boRuntimeException
    {
        int ret = ICustomField.RENDER_CONTINUE;
        
        relatedAttName = relatedAtt.getName();

        int fieldNumber =doccont.countFields++;
        StringBuffer aux = new StringBuffer();
        aux.append( object.getName() ).append( "__" ).append( object.bo_boui ).append("__").append( relatedAtt.getName() ); //$NON-NLS-1$ //$NON-NLS-2$
        name = aux.toString();
        aux.append(fieldNumber);
        id = aux.toString();
        
        aux.setLength(0);
        aux.append( object.getName() ).append( "__" ).append( object.bo_boui ).append("__").append( "helper" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        nameLabel = aux.toString();
        aux.append(fieldNumber);
        idLabel = aux.toString();
        
        aux.setLength(0);
        aux.append( object.getName() ).append( "__" ).append( object.bo_boui ).append("__").append( "show" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        nameShowLabel = aux.toString();
        aux.append(fieldNumber);
        idShowLabel = aux.toString();
        
        out.println(getScripts(ctx, doc, object, out, id, idShowLabel, idLabel));
        
        StringBuffer v = new StringBuffer();
        StringBuffer v2 = new StringBuffer();
        String label = null;
        
        boObject o = object.getParent() == null ? null : object.getParent(); 
        if(o != null)
        {
            String relAttBoui = relatedAtt.getValueString();
            String helperValue = object.getAttribute("helper").getValueString(); //$NON-NLS-1$
            
            if(relAttBoui != null && !"".equals(relAttBoui) &&  //$NON-NLS-1$
               validAttribute(ctx, o, Long.parseLong(relAttBoui), helperValue))
            {
                v.append( relAttBoui == null ? "":relAttBoui ); //$NON-NLS-1$
                v2.append(object.getAttribute("helper").getValueString() == null ? "":object.getAttribute("helper").getValueString() ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ngtXMLHandler c = xml.getChildNode("campos").getChildNode(v2.toString()); //$NON-NLS-1$
                label = c.getChildNodeText("label", ""); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                relatedAtt.setObject(null);
                object.getAttribute("helper").setValueString(null); //$NON-NLS-1$
            }
        }
        else
        {
            relatedAtt.setObject(null);
            object.getAttribute("helper").setValueString(null); //$NON-NLS-1$
        }
        
         
        
        
        out.println("<table cellspacing='0' cellpaging='0' style='width:100%'>"); //$NON-NLS-1$
        out.println("<tr>"); //$NON-NLS-1$
            out.println(Messages.getString("MapTempAtribRender.0")); //$NON-NLS-1$
            out.println("<td width='100%'>"); //$NON-NLS-1$
            out.println(getTextField(label, fieldNumber));
            out.println(getHiddenField(v.toString(), v2.toString(), fieldNumber));
            out.println("</td>"); //$NON-NLS-1$
            out.println("<td>"); //$NON-NLS-1$
            out.println(getButton(1, "2")); //$NON-NLS-1$
            out.println("</td>"); //$NON-NLS-1$
        out.println("</tr>"); //$NON-NLS-1$
        out.println("</table>"); //$NON-NLS-1$
        
        
        return ret;
    }
    
    private static boolean validAttribute(EboContext boctx, boObject eboClsReg, long relAttBoui, String helperValue)
    throws boRuntimeException
    {
        if(helperValue != null && relAttBoui > 0)
        {
            return true;
        }
        return false;
    }

    public String getRelatedAttribute()
    {
        return relatedAttName;
    }
    private String getHiddenField(String value1, String value2,int tabIndex)
    { 
        StringBuffer sb = new StringBuffer();
        sb.append("<input type='hidden' value='") //$NON-NLS-1$
        .append((value1 == null ? "":value1)) //$NON-NLS-1$
        .append("' id ='") //$NON-NLS-1$
        .append(id)
        .append("' name='") //$NON-NLS-1$
        .append(name).append("'>"); //$NON-NLS-1$
        sb.append("<input type='hidden' value='") //$NON-NLS-1$
        .append((value2 == null ? "":value2)) //$NON-NLS-1$
        .append("' id ='") //$NON-NLS-1$
        .append(idLabel)
        .append("' name='") //$NON-NLS-1$
        .append(nameLabel).append("'>"); //$NON-NLS-1$
        return sb.toString();
    }
    
    private String getTextField(String value, int tabIndex)
    { 
        StringBuffer sb = new StringBuffer();
        sb.append("<input class='text' value='") //$NON-NLS-1$
        .append((value == null ? "":value)) //$NON-NLS-1$
        .append("' id ='") //$NON-NLS-1$
        .append(idShowLabel)
        .append("' onchange='changeAttribute(\""+idShowLabel+"\");' name='") //$NON-NLS-1$ //$NON-NLS-2$
        .append(nameShowLabel).append("' tabindex='") //$NON-NLS-1$
        .append(tabIndex)
        .append("'>"); //$NON-NLS-1$
        return sb.toString();
    }
    
    private static String getButton(int pos, String tabIndex)
    {
        return "&nbsp;<button style='width:25px;height:20px' onclick='showTreeAttr();'>....</button>"; //$NON-NLS-1$
    }
    
    private String getScripts(EboContext ctx, docHTML doc ,boObject masterObject, PrintWriter out, String fieldId, String fieldShow, String fieldNode) throws boRuntimeException
    {
        
        String objName = ""; //$NON-NLS-1$
        StringBuffer attributesStr = null;
        long tempBoui = -1;
        
        if(masterObject != null && masterObject.getParent() != null )
        {
            tempBoui = masterObject.getParent().getBoui();
            attributesStr = MapTempAtribTreeHelper.getXMLAttributes(ctx, tempBoui);
            if(attributesStr != null && attributesStr.length() > 0)
            {
                xml = new ngtXMLHandler(attributesStr.toString());
            }
        }
        else
        {
            attributesStr = new StringBuffer("<campos></campos>"); //$NON-NLS-1$
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n<script>") //$NON-NLS-1$
            .append("\n var objectName='"+objName+"';") //$NON-NLS-1$ //$NON-NLS-2$
            .append("\n var _attributesXml =\""+attributesStr+"\";") //$NON-NLS-1$ //$NON-NLS-2$
            
            .append("\nfunction changeAttribute( newValue )") //$NON-NLS-1$
            .append("\n{") //$NON-NLS-1$
            .append("\n	qryNode = document.getElementById('"+fieldNode+"');") //$NON-NLS-1$ //$NON-NLS-2$
            .append("\n	qryValue = document.getElementById('"+fieldId+"');") //$NON-NLS-1$ //$NON-NLS-2$
            .append("\n	qryShow = document.getElementById('"+fieldShow+"');") //$NON-NLS-1$ //$NON-NLS-2$
            
            .append("\n	attrDoc		= new ActiveXObject(\"Microsoft.XMLDOM\");") //$NON-NLS-1$
            .append("\n	attrDoc.async	= false;") //$NON-NLS-1$
            .append("\n	attrDoc.loadXML(_attributesXml);") //$NON-NLS-1$
            .append("\n	attrXML = attrDoc.selectSingleNode(\"*\");") //$NON-NLS-1$
            
            .append("\n	var nodeAttr=this.attrXML.selectSingleNode( newValue );") //$NON-NLS-1$
            .append("\n	if ( nodeAttr )") //$NON-NLS-1$
            .append("\n	{") //$NON-NLS-1$
            
            .append("\n		qryNode.value=newValue;") //$NON-NLS-1$
            .append("\n		qryValue.value=nodeAttr.selectSingleNode(\"value\").text;") //$NON-NLS-1$
            .append("\n		qryShow.value=nodeAttr.selectSingleNode(\"label\").text;") //$NON-NLS-1$
            .append("\n	}") //$NON-NLS-1$
            .append("\n	this.resetBorder();") //$NON-NLS-1$
            .append("\n}") //$NON-NLS-1$
            
            
            .append("\nfunction resetBorder( )\n")  //$NON-NLS-1$
            .append("{\n") //$NON-NLS-1$
                .append("\n	qry = document.getElementById('"+fieldShow+"');") //$NON-NLS-1$ //$NON-NLS-2$
                .append("\n	qry.runtimeStyle.border=\"1px solid #6B8C9C\";") //$NON-NLS-1$
            .append("}\n")     //$NON-NLS-1$
            
            .append("\nfunction showTreeAttr( )\n") //$NON-NLS-1$
                .append("{\n") //$NON-NLS-1$
                .append("\n	qry = document.getElementById('"+fieldNode+"');") //$NON-NLS-1$ //$NON-NLS-2$
                .append("\n	var value=qry.value;") //$NON-NLS-1$
                .append("	if ( value == \"\") attribXML=\"_x_\"; else attribXML=value;") //$NON-NLS-1$
                .append("\n  if(qry.disabled) return;") //$NON-NLS-1$
                
                .append("\n	attrDoc		= new ActiveXObject(\"Microsoft.XMLDOM\");") //$NON-NLS-1$
                .append("\n	attrDoc.async	= false;") //$NON-NLS-1$
                .append("\n	attrDoc.loadXML(_attributesXml);") //$NON-NLS-1$
                .append("\n	attrXML = attrDoc.selectSingleNode(\"*\");") //$NON-NLS-1$
                
                .append("\n	var nodeAttr = attrXML.selectSingleNode( attribXML );") //$NON-NLS-1$
                
                .append("\n  qry.runtimeStyle.border=\"1px solid red\";") //$NON-NLS-1$
                
                .append("\n	var url=\"__chooseTempAtt.jsp?tempBoui="+tempBoui+"\";") //$NON-NLS-1$ //$NON-NLS-2$
                .append("\n	var topper=event.screenY+15;") //$NON-NLS-1$
                .append("\n	var lefter=event.screenX-290;") //$NON-NLS-1$
                .append("\n	var attchoosed=[];") //$NON-NLS-1$
                .append("\n	attchoosed[0]=new Object();") //$NON-NLS-1$
                .append("\n	window.showModalDialog(url,attchoosed,\"dialogHeight: 500px; dialogWidth: 290px; dialogTop: \"+(topper)+\"; dialogLeft: \"+( lefter)+\"; edge: raised;center: No;  help: No; scroll: yes; resizable: yes; status: no;\");") //$NON-NLS-1$
                .append("\n	if(attchoosed[0].value != null){changeAttribute(attchoosed[0].value);}") //$NON-NLS-1$
                .append("\n	resetBorder();") //$NON-NLS-1$
                .append("\n}") //$NON-NLS-1$
            .append("\n </script>\n\n\n"); //$NON-NLS-1$
        return sb.toString();
    }
}