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
        aux.append( object.getName() ).append( "__" ).append( object.bo_boui ).append("__").append( relatedAtt.getName() );
        name = aux.toString();
        aux.append(fieldNumber);
        id = aux.toString();
        
        aux.setLength(0);
        aux.append( object.getName() ).append( "__" ).append( object.bo_boui ).append("__").append( "helper" );
        nameLabel = aux.toString();
        aux.append(fieldNumber);
        idLabel = aux.toString();
        
        aux.setLength(0);
        aux.append( object.getName() ).append( "__" ).append( object.bo_boui ).append("__").append( "show" );
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
            String helperValue = object.getAttribute("helper").getValueString();
            
            if(relAttBoui != null && !"".equals(relAttBoui) && 
               validAttribute(ctx, o, Long.parseLong(relAttBoui), helperValue))
            {
                v.append( relAttBoui == null ? "":relAttBoui );
                v2.append(object.getAttribute("helper").getValueString() == null ? "":object.getAttribute("helper").getValueString() );
                ngtXMLHandler c = xml.getChildNode("campos").getChildNode(v2.toString());
                label = c.getChildNodeText("label", "");
            }
            else
            {
                relatedAtt.setObject(null);
                object.getAttribute("helper").setValueString(null);
            }
        }
        else
        {
            relatedAtt.setObject(null);
            object.getAttribute("helper").setValueString(null);
        }
        
         
        
        
        out.println("<table cellspacing='0' cellpaging='0' style='width:100%'>");
        out.println("<tr>");
            out.println(Messages.getString("MapTempAtribRender.0"));
            out.println("<td width='100%'>");
            out.println(getTextField(label, fieldNumber));
            out.println(getHiddenField(v.toString(), v2.toString(), fieldNumber));
            out.println("</td>");
            out.println("<td>");
            out.println(getButton(1, "2"));
            out.println("</td>");
        out.println("</tr>");
        out.println("</table>");
        
        
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
        sb.append("<input type='hidden' value='")
        .append((value1 == null ? "":value1))
        .append("' id ='")
        .append(id)
        .append("' name='")
        .append(name).append("'>");
        sb.append("<input type='hidden' value='")
        .append((value2 == null ? "":value2))
        .append("' id ='")
        .append(idLabel)
        .append("' name='")
        .append(nameLabel).append("'>");
        return sb.toString();
    }
    
    private String getTextField(String value, int tabIndex)
    { 
        StringBuffer sb = new StringBuffer();
        sb.append("<input class='text' value='")
        .append((value == null ? "":value))
        .append("' id ='")
        .append(idShowLabel)
        .append("' onchange='changeAttribute(\""+idShowLabel+"\");' name='")
        .append(nameShowLabel).append("' tabindex='")
        .append(tabIndex)
        .append("'>");
        return sb.toString();
    }
    
    private static String getButton(int pos, String tabIndex)
    {
        return "&nbsp;<button style='width:25px;height:20px' onclick='showTreeAttr();'>....</button>";
    }
    
    private String getScripts(EboContext ctx, docHTML doc ,boObject masterObject, PrintWriter out, String fieldId, String fieldShow, String fieldNode) throws boRuntimeException
    {
        
        String objName = "";
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
            attributesStr = new StringBuffer("<campos></campos>");
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n<script>")
            .append("\n var objectName='"+objName+"';")
            .append("\n var _attributesXml =\""+attributesStr+"\";")
            
            .append("\nfunction changeAttribute( newValue )")
            .append("\n{")
            .append("\n	qryNode = document.getElementById('"+fieldNode+"');")
            .append("\n	qryValue = document.getElementById('"+fieldId+"');")
            .append("\n	qryShow = document.getElementById('"+fieldShow+"');")
            
            .append("\n	attrDoc		= new ActiveXObject(\"Microsoft.XMLDOM\");")
            .append("\n	attrDoc.async	= false;")
            .append("\n	attrDoc.loadXML(_attributesXml);")
            .append("\n	attrXML = attrDoc.selectSingleNode(\"*\");")
            
            .append("\n	var nodeAttr=this.attrXML.selectSingleNode( newValue );")
            .append("\n	if ( nodeAttr )")
            .append("\n	{")
            
            .append("\n		qryNode.value=newValue;")
            .append("\n		qryValue.value=nodeAttr.selectSingleNode(\"value\").text;")
            .append("\n		qryShow.value=nodeAttr.selectSingleNode(\"label\").text;")
            .append("\n	}")
            .append("\n	this.resetBorder();")
            .append("\n}")
            
            
            .append("\nfunction resetBorder( )\n") 
            .append("{\n")
                .append("\n	qry = document.getElementById('"+fieldShow+"');")
                .append("\n	qry.runtimeStyle.border=\"1px solid #6B8C9C\";")
            .append("}\n")    
            
            .append("\nfunction showTreeAttr( )\n")
                .append("{\n")
                .append("\n	qry = document.getElementById('"+fieldNode+"');")
                .append("\n	var value=qry.value;")
                .append("	if ( value == \"\") attribXML=\"_x_\"; else attribXML=value;")
                .append("\n  if(qry.disabled) return;")
                
                .append("\n	attrDoc		= new ActiveXObject(\"Microsoft.XMLDOM\");")
                .append("\n	attrDoc.async	= false;")
                .append("\n	attrDoc.loadXML(_attributesXml);")
                .append("\n	attrXML = attrDoc.selectSingleNode(\"*\");")
                
                .append("\n	var nodeAttr = attrXML.selectSingleNode( attribXML );")
                
                .append("\n  qry.runtimeStyle.border=\"1px solid red\";")
                
                .append("\n	var url=\"__chooseTempAtt.jsp?tempBoui="+tempBoui+"\";")
                .append("\n	var topper=event.screenY+15;")
                .append("\n	var lefter=event.screenX-290;")
                .append("\n	var attchoosed=[];")
                .append("\n	attchoosed[0]=new Object();")
                .append("\n	window.showModalDialog(url,attchoosed,\"dialogHeight: 500px; dialogWidth: 290px; dialogTop: \"+(topper)+\"; dialogLeft: \"+( lefter)+\"; edge: raised;center: No;  help: No; scroll: yes; resizable: yes; status: no;\");")
                .append("\n	if(attchoosed[0].value != null){changeAttribute(attchoosed[0].value);}")
                .append("\n	resetBorder();")
                .append("\n}")
            .append("\n </script>\n\n\n");
        return sb.toString();
    }
}