package netgest.bo.impl.document.merge.gestemp.presentation;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import netgest.bo.dochtml.ICustomField;
import netgest.bo.dochtml.docHTML_renderFields;
import netgest.bo.dochtml.docHTML_section;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.bridgeHandler;

public class MapClfAtribRender implements ICustomField
{
    
    private String relatedAttName;
    private static int a = 0;
    
    public int render(EboContext ctx, docHTML_controler doccont, docHTML doc , boObject object, PrintWriter out, AttributeHandler relatedAtt) throws boRuntimeException
    {
    
        int ret = ICustomField.RENDER_CONTINUE;
        
        boObject o = object.getParent() == null ? null : object.getParent(); 

        boObject clf = object.getAttribute("classificacao").getObject();
        StringBuffer values[] = new StringBuffer[0];
        StringBuffer labels[] = new StringBuffer[0];
        if(clf != null)
        {
            boObject group = clf.getAttribute("grupo").getObject();
            values = new StringBuffer[(int)group.getBridge("classificacao").getRecordCount()];
            labels = new StringBuffer[(int)group.getBridge("classificacao").getRecordCount()];
            boBridgeIterator bit = group.getBridge("classificacao").iterator();
            bit.beforeFirst();
            boObject aux = null;
            int i = 0;
            while(bit.next())
            {
                aux = bit.currentRow().getObject();
                values[i] = new StringBuffer(String.valueOf(aux.getBoui()));
                labels[i] = new StringBuffer(aux.getAttribute("name").getValueString());
                i++;
            }
        }
        
        
        StringBuffer toPrint  = new StringBuffer();
        StringBuffer v        = new StringBuffer( relatedAtt.getValueString() == null ? "":relatedAtt.getValueString() );
        StringBuffer nameH    = new StringBuffer();
        StringBuffer id       = new StringBuffer();
        StringBuffer onChange = new StringBuffer();
        int fieldNumber       = doccont.countFields++;
        int tabNumber         = doc.getTabindex(doc.FIELD, object.getName(), String.valueOf(object.getBoui()), relatedAtt.getName(), doccont);
        boolean isDisabled    =   relatedAtt.isDisabled();                      
        boolean isVisible     =   relatedAtt.isVisible();
        boolean inTemplate    =   object.getMode() == boObject.MODE_EDIT_TEMPLATE; 
        boolean isRequired    =   relatedAtt.required();
        boolean isRecommend   =   relatedAtt.getRecommend();
        
        nameH.append( object.getName() ).append( "__" ).append( object.bo_boui ).append("__").append( relatedAtt.getName() );
        id.append( object.getName() ).append( "__" ).append( object.bo_boui ).append("__").append( relatedAtt.getName() ).append(fieldNumber);
        
               
        docHTML_renderFields.writeHTML_forCombo(
            toPrint,
            v,
            nameH ,
            id,
            1,
            labels,
            values,
            false,
            isDisabled,
            true,
            false,
            new StringBuffer(),
            false,
            true,
            new Hashtable()
        );
        
        out.println(toPrint);
                                        
        
        
        return ret;
    }
    
    public String getRelatedAttribute()
    {
        return relatedAttName;
    }

}