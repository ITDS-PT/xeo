package netgest.bo.impl.document.merge.gestemp;
import bsh.BshClassManager;
import bsh.NameSpace;
import java.util.ArrayList;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
public class GtTag
{
    private String name;
    private boolean wordTag;
    private boolean applyWord;
    private boolean applyText;
    private String javaCode;
    
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.impl.document.merge.gestemp.GtTag");

    public GtTag(String name, boolean applyWord, boolean applyText, boolean isWordTag, String javaCode)
    {
        this.name = name;
        this.applyWord = applyWord;
        this.applyText = applyText;
        this.javaCode = javaCode;
        this.wordTag = isWordTag;
    }  
    
    public boolean applyToWord()
    {
        return applyWord;
    }
    
    public boolean applyToText()
    {
        return applyText;
    }
    
    public boolean isWordTag()
    {
        return wordTag;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Object apply(EboContext boctx, Object value)  throws boRuntimeException
    {
        try
        {
            long ti = System.currentTimeMillis();
            boolean paramIsNull = true;
            NameSpace nsp = new NameSpace(new BshClassManager(), "GtQuery");
            nsp.importPackage("netgest.bo");
            nsp.importPackage("netgest.bo.def");
            nsp.importPackage("netgest.utils");
            nsp.importPackage("netgest.bo.runtime");
            nsp.importPackage("netgest.bo.utils");
            nsp.importPackage("netgest.bo.impl.document.merge.gestemp");
            
            nsp.setTypedVariable( "ctx", EboContext.class, boctx, null);
//            nsp.setTypedVariable( "template", GtTemplate.class, template, null);
//            nsp.setTypedVariable( "query", GtQuery.class, this, null);

            nsp.setTypedVariable("valor", Object.class, value, null);
            bsh.Interpreter bshi = new bsh.Interpreter();
            bshi.setNameSpace(nsp);
            //não é suposto devolver nada
            return bshi.eval(javaCode);
        }
        catch (Exception e)
        {
            String msg = "Não foi possível calcular a tag [" +getName() +"]";
            logger.severe("Query[" + getName()+"] : " + msg, e);
            throw new boRuntimeException("", msg, null);
        }
    }
}