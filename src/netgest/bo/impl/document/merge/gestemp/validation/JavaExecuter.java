package netgest.bo.impl.document.merge.gestemp.validation;

import bsh.BshClassManager;
import bsh.EvalError;
import bsh.Modifiers;
import bsh.NameSpace;

import bsh.TargetError;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;


public class JavaExecuter 
{
    NameSpace nsp = null;
    BshClassManager bshManager = null;
    
    Throwable errorException = null;
    String    errorMessage   = null;
    
    String name = null;
    String javaCode = null;

    private static Logger logger = Logger.getLogger(JavaExecuter.class);

    public JavaExecuter(String name) {
        this.name = name;
        bshManager = new BshClassManager();
        nsp = new NameSpace(new BshClassManager(), name);
    }

    public void addImport(String importStr) throws boRuntimeException {
        try {
            nsp.importPackage(importStr);
        } catch (Exception e) {
            throw new boRuntimeException("JavaExecuter.addImport",
                "Erro a parametrizar a execução do Namespace [" + name + "]", e);
        }
    }

    public void addTypedVariable(String varName, Class varClass,
        Object varValue, Modifiers modifiers) throws boRuntimeException {
        try {
            nsp.setTypedVariable(varName, varClass, varValue, modifiers);
        } catch (Exception e) {
            throw new boRuntimeException("JavaExecuter.addType",
                "Erro a parametrizar a execução do Namespace [" + name + "]", e);
        }
    }

    public void setJavaCode(String javaCode) {
        this.javaCode = javaCode;
    }

    public Throwable getException() 
    {
        return this.errorException;
    }
    
    public String getErrorMessage()
    {
        return this.errorMessage;
    }

    public boolean sucess()
    {
        return this.errorException == null;
    }

    public Object execute() 
    {
        bsh.Interpreter bshi = new bsh.Interpreter();
        try {
            bshi.setNameSpace(nsp);

            return bshi.eval(javaCode);
        } 
        catch (EvalError e) 
        {
            this.errorException = e;
            try
            {
                logger.severe( "Error executing java code line#:" + e.getErrorLineNumber() + "\n" + this.javaCode );
            }
            catch (Exception ex)
            {
                logger.severe( "Erro dumping error.", e );
            }
            
            String lineNumber = "<uknown>";
            String errorText  = "";
            try 
            {
                lineNumber = String.valueOf( e.getErrorLineNumber() );
                errorText = e.getErrorText();

                this.errorMessage = 
                        "Linha " + lineNumber + " : " +
                        errorText + "]";

            } catch (Exception ex) 
            {
            } 
            
            
            if( e instanceof TargetError )
            {
                Throwable exTargetError = ((TargetError)e).getTarget(); 
                if ( exTargetError != null )
                {
                    this.errorMessage += "\n" + exTargetError.getMessage();
                }
            }
            else
            {
                this.errorMessage += "\n" + e.getMessage();
            }
            
            return null;
        }
        catch ( Throwable e )
        {
            this.errorException = e;
            try
            {
                logger.severe( e );
            }
            catch (Exception ex)
            {
                logger.severe( "Erro dumping error.", e );
            }
            
            this.errorMessage = 
                    "Erro na execução " + e.getMessage() ;
            return null;
        }
    }
}
