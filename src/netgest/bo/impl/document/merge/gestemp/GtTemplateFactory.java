package netgest.bo.impl.document.merge.gestemp;
import java.util.Hashtable;
import netgest.bo.dochtml.docHTML;
import netgest.bo.presentation.render.elements.cache.Cache;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;

public class GtTemplateFactory 
{
    private static Cache templates = new Cache("Templates", 60, 500, 50);
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.impl.document.merge.gestemp.GtTemplateFactory");
    
    public static void clearCache() {
        templates.clear();
    }
    
    public static int cacheSize() {
        return templates.getSize();
    }
    
    public static int cacheShrinktime() {
        return templates.getShrinkTimes();
    }
    
    public static String getCacheName() {
        return templates.getName();
    }
    
    public static String getState() {
        return templates.getState();
    }

    public GtTemplateFactory()
    {
    }
    
    
    //métodos estáticos
    public static GtTemplate getTemplateByBoui(docHTML doc,  long boui) throws boRuntimeException
    {
        if(templates.get(String.valueOf(doc.getDocIdx())) == null)
        {
            templates.put(String.valueOf(doc.getDocIdx()), 
            GtTemplate.getTemplate(doc.getEboContext(), boui), doc.getEboContext().getBoSession().getPerformerBoui());
        }
        
        return (GtTemplate)templates.get(String.valueOf(doc.getDocIdx()));
    }
    
    public static GtTemplate getTemplateByBoui(docHTML doc,  long boui, long actvBoui) throws boRuntimeException
    {
        if(templates.get(String.valueOf(doc.getDocIdx())) == null)
        {
            GtTemplate t = GtTemplate.getTemplate(doc.getEboContext(), boui);
            boObject actvSend = 
                boObject.getBoManager().loadObject(doc.getEboContext(), actvBoui);
            boObject message = null;
            if(actvSend != null)
            {
                if(!actvSend.getName().startsWith("GESTEMP_Generated"))
                {
                    boObject xwfVar = actvSend.getAttribute("message").getObject();
                    if(xwfVar != null)
                    {
                        boObject varValue = xwfVar.getAttribute("value").getObject();
                        if(varValue != null)
                        {
                            message = varValue.getAttribute("valueObject").getObject();
                        }
                    }
                }
                else
                {
                    message = actvSend; 
                }
            }
            
            t.setAnswer(message);
            templates.put(String.valueOf(doc.getDocIdx()), t, doc.getEboContext().getBoSession().getPerformerBoui());
        }
        
        return (GtTemplate)templates.get(String.valueOf(doc.getDocIdx()));
    }
    
    public static void releaseAll(EboContext boctx, int docIDX)
    {
         if(templates != null)
        {
            templates.remove(String.valueOf(docIDX));
        }
    }
    
}