package netgest.bo.dochtml.viewerImpl;
import java.io.PrintWriter;
import java.util.Vector;
import netgest.bo.dochtml.ICustomField;
import netgest.bo.dochtml.docHTML_renderFields;
import netgest.bo.dochtml.docHTML_section;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.def.boDefAttribute;

public class AttributesFieldRenderPassword
implements ICustomField
{
    
    private String relatedAttName;
    private static int a = 0;
    
    public int render(EboContext ctx, docHTML_controler doccont, docHTML doc , boObject obj, PrintWriter out, AttributeHandler relatedAtt) throws boRuntimeException
    {
    
        int ret = ICustomField.RENDER_CONTINUE;
        
        AttributeHandler attr = obj.getAttribute("password");
        String pass = attr.getValueString();   
        StringBuffer toPrint = new StringBuffer();
        StringBuffer nameH       =   new StringBuffer();
        StringBuffer id       =   new StringBuffer();
        boolean isDisabled       =   attr.isDisabled();                      
        boolean isVisible        =   attr.isVisible();
        boolean inTemplate       =   obj.getMode() == boObject.MODE_EDIT_TEMPLATE; 
        boolean isRequired       =   attr.required();
        boolean isRecommend      =   attr.getRecommend();
        int fieldNumber       = doccont.countFields++;
        int tabNumber         = doc.getTabindex(doc.FIELD, obj.getName(), String.valueOf(obj.getBoui()), relatedAtt.getName(), doccont);
        StringBuffer onChange    =   new StringBuffer();
         boDefAttribute attrDef   =   attr.getDefAttribute();
        
        nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( attr.getName() );
        id.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( attr.getName() ).append(fieldNumber);
        

               
         docHTML_renderFields.writeHTML_text(
                        toPrint,
                        new StringBuffer(pass),
                        nameH,id,tabNumber,
                        isDisabled,
                        isVisible,
                        inTemplate,
                        onChange,
                        isRequired,
                        isRecommend,
                        attrDef.getLen(),
                        null
                        );     
      
        String stoPrint = toPrint.toString().replaceAll("<input class","<input type='password' class");
        out.println(stoPrint);                               
        
        return ret;
    }
    
    public String getRelatedAttribute()
    {
        return relatedAttName;
    }

}