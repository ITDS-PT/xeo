/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import bsh.BshClassManager;
import bsh.NameSpace;
import bsh.UtilEvalError;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import netgest.bo.def.*;
import netgest.bo.runtime.*;
import netgest.utils.*;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class docHtml_wizard 
{
    private ngtXMLHandler wizardView = null;
    private ArrayList sectionNames = new ArrayList();
    private ArrayList sectionID = new ArrayList();
    private ArrayList sectionCondition = new ArrayList();
    private ArrayList sectionEnd = new ArrayList();
    private ArrayList sectionLast = new ArrayList();
    private ArrayList sectionValid = new ArrayList();
    
    private Stack p_path = new Stack();
    
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public docHtml_wizard(boObject lastobj, boDefHandler bodef)
    {
        boDefViewer viewer[] = bodef.getBoViewers();
        ngtXMLHandler forms[] = viewer[0].getForms();
        boolean found = false;
        for (int i = 0; !found && i < forms.length; i++) 
        {
            if("wizard".equalsIgnoreCase(forms[i].getNodeName()))
            {
                wizardView = forms[i];
                found = true;
            }
        }
        for (int i = 0; i < wizardView.getChildNodes().length; i++) 
        {
            fill(wizardView.getChildNodes()[i]);
        }
    }
    
    private void fill(ngtXMLHandler form)
    {
        String xTagName = form.getNodeName();
        if("section".equalsIgnoreCase(xTagName))
        {
            sectionNames.add(form.getAttribute("name", ""));
            sectionID.add(form.getAttribute("id", ""));
            if(form.getChildNode("showCondition") != null)
            {
                sectionCondition.add(form.getChildNode("showCondition").getText());
            }
            else
            {
                sectionCondition.add("");
            }
            if(form.getChildNode("endCondition") != null)
            {
                sectionEnd.add(form.getChildNode("endCondition").getText());
            }
            else
            {
                sectionEnd.add("");
            }
            if(form.getChildNode("lastSection") != null)
            {
                sectionLast.add(form.getChildNode("lastSection").getText());
            }
            else
            {
                sectionLast.add("");
            }
            if(form.getChildNode("validCondition") != null)
            {
                sectionValid.add(form.getChildNode("validCondition").getText());
            }
            else
            {
                sectionValid.add("");
            }
        }
        else
        {
            for (int i = 0; i < form.getChildNodes().length; i++) 
            {
                fill(form.getChildNodes()[i]);
            }
        }
    }
    
    
    public boolean isShowing(String sectionName, boObject obj) throws boRuntimeException
    {
        int index = sectionNames.indexOf(sectionName);
        String javacode = (String)sectionCondition.get(index);
        if(javacode == null || "".equals(javacode))
        {
            return true;
        }
        Object xo = executeJavaCode(obj.getEboContext(), obj, javacode);
        if (xo instanceof Boolean)
        {
            return ((Boolean) xo).booleanValue();
        }
        else
        {
            throw new boRuntimeException("docHtml_wizard",
                "executeJavaCode",
                new Exception("Wrong type of data returned;"));
        }
    }
    
    public boolean isEnd(String sectionName, boObject obj) throws boRuntimeException
    {
        int index = sectionNames.indexOf(sectionName);
        String javacode = (String)sectionEnd.get(index);
        if(javacode == null || "".equals(javacode))
        {
            return true;
        }
        Object xo = executeJavaCode(obj.getEboContext(), obj, javacode);
        if (xo instanceof Boolean)
        {
            return !((Boolean) xo).booleanValue();
        }
        else
        {
            throw new boRuntimeException("docHtml_wizard",
                "executeJavaCode",
                new Exception("Wrong type of data returned;"));
        }
    }
    
    public boolean isLastSection(String sectionName, boObject obj) throws boRuntimeException
    {
        int index = sectionNames.indexOf(sectionName);
        String javacode = (String)sectionLast.get(index);
        if(javacode == null || "".equals(javacode))
        {
            return false;
        }
        Object xo = executeJavaCode(obj.getEboContext(), obj, javacode);
        if (xo instanceof Boolean)
        {
            return ((Boolean) xo).booleanValue();
        }
        else
        {
            throw new boRuntimeException("docHtml_wizard",
                "executeJavaCode",
                new Exception("Wrong type of data returned;"));
        }
    }
    
    public ArrayList getSectionNames() throws boRuntimeException
    {
        return sectionNames;
    }
    
    public void putOnStack(String sectionName)
    {
        p_path.push(sectionName);
    }
    
    public boolean hasPrevious()
    {
        return p_path.size() > 1;
    }
    
    public String popFromStack()
    {
        try
        {
            return (String)p_path.pop();
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    public boolean teste(boObject obj) throws boRuntimeException
    {
        if(obj.getAttribute("label").getValueString() != null &&
            obj.getAttribute("label").getValueString().trim().length()>0 &&
            obj.getAttribute("assignedQueue").getValueObject() != null &&
            obj.getAttribute("to").getValueObject() != null)
        {
          return true;
        }
        else
        {
          return false;//"Preencha os campos obrigatórios";
        }
    }
    
    public String isValid(String sectionName, boObject obj) throws boRuntimeException
    {
        int index = sectionNames.indexOf(sectionName);
        String javacode = (String)sectionValid.get(index);
        teste(obj);
        if(javacode == null || "".equals(javacode))
        {
            return null;
        }
        Object xo = executeJavaCode(obj.getEboContext(), obj, javacode);
        if(xo == null) return null;
        if (xo instanceof String)
        {
            return (String) xo;
        }
        else
        {
            throw new boRuntimeException("docHtml_wizard",
                "executeJavaCode",
                new Exception("Wrong type of data returned;"));
        }
    }
    
    
    /**
    * Chamada para executar o código em JAVA.
    * @param ctx , EboContext, contexto para a execução.
    * @return result, true caso tenha sucesso, false cc.
    */
    private Object executeJavaCode(EboContext ctx, boObject obj, String javaCode)
        throws boRuntimeException
    {
        NameSpace nsp = new NameSpace(new BshClassManager(),"wizJava");

        nsp.importPackage("netgest.bo");
        nsp.importPackage("netgest.bo.def");
        nsp.importPackage("netgest.utils");
        nsp.importPackage("netgest.bo.runtime");
        nsp.importPackage("netgest.bo.utils");
        
        String vv = obj.getAttribute("type_recursive").getValueString();

        String ctxVarName = "ctx";
        javaCode = javaCode.replaceAll("this.", "thisObject.");

        if (ctxVarName != null)
        {
            try
            {
                nsp.setTypedVariable(ctxVarName, EboContext.class, ctx, null);
            }
             catch (UtilEvalError e)
            {
            }
        }
        try
        {
            nsp.setTypedVariable("thisObject", boObject.class, obj, null);
        }
         catch (UtilEvalError e)
        {
        }

        bsh.Interpreter bshi = new bsh.Interpreter();
        bshi.setNameSpace(nsp);

        try
        {
            return bshi.eval(javaCode);
        }
         catch (Exception e)
        {
            throw new boRuntimeException("docHtml_wizard", "executeJavaCode", e);
        }
    }
}