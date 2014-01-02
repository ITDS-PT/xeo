package netgest.bo.impl.document.merge.gestemp.presentation;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;

public class GesDocLov extends GesDocObj
{
    private long lovBoui = -1;
    public GesDocLov(GesDocViewer clfViewer, long gesDocboui, String internalName, String name, long lovBoui, boolean required, String validation)
    {
        this.name = name;
        this.lovBoui = lovBoui;
        this.gesDocBoui = gesDocboui;
        this.required = required;
        this.validation = validation;
        this.clfViewer = clfViewer;
        this.internalName = internalName;
    }

    public void setValue(EboContext boctx)  throws boRuntimeException
    {
        HttpServletRequest request = boctx.getRequest();
        Enumeration oEnum = request.getParameterNames();
        String valueDesc = request.getParameter(getHTMLFieldName());
        value=request.getParameter(getHTMLFieldName() + "_lovValue");
    }

    public long getLovBoui()
    {
        return this.lovBoui;
    }
}