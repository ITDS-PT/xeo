package netgest.bo.runtime;
import bsh.BshClassManager;
import bsh.EvalError;
import bsh.NameSpace;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import netgest.bo.def.boDefXeoCode;
import netgest.bo.system.boApplication;
import netgest.bo.xep.Xep;
import netgest.xwf.core.xwfECMAparser;
import netgest.bo.system.Logger;

public class boXEPEval 
{
	private static Logger logger = Logger.getLogger("netgest.bo.runtime.boObject");
    
	private String          code;
    private int             language;
	private Object          evalReturn;
    
	private boObject        thisObject       = null;
	private HashMap         attributeValues  = null;
	private HashMap         contextVariables = null;
	private Vector          packageImports   = new Vector();
	private boolean         throwException   = false;
    private EboContext      context          = null;
    private Xep             xep              = null;
    private bsh.Interpreter bshi             = null;
    
	public boXEPEval( boDefXeoCode code, EboContext ctx )
	{
		this.code       = code.getSource();
        this.language   = code.getLanguage();
        this.context    = ctx;
	}

	public boXEPEval( String code, int language, EboContext ctx )
	{
		this.code       = code;
        this.language   = language;
        this.context    = ctx;
	}

    
	public void setThrowException( boolean throwException )
	{
		this.throwException = throwException;
	}
    
	public void addThisObject( boObject object )
	{
		this.thisObject = object;
	}
    
	public void addImport( String packageName )
	{
		packageImports.add( packageName );
	}
    
	public void addObjectAttributeValue( String name, AttributeHandler att )
	{
		if( attributeValues == null ) attributeValues = new HashMap();
		attributeValues.put( name, att );
	}
    
    public void addVariableString( String name, String value )
    {
        if( contextVariables == null ) contextVariables = new HashMap();
        contextVariables.put( name, value );
    }
    
    public Object getVariable( String name )
    {
        try
        {
            if( bshi != null ) return bshi.get( name ); 
            else if ( xep != null ) return xep.getVariable( name );
        }
        catch (EvalError e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

	public Object eval() throws boRuntimeException
	{
		if( language == boDefXeoCode.LANG_JAVA )
		{
			return evalJava();
		}
		else if( language == boDefXeoCode.LANG_XEP )
		{
			return evalXep();
		}
		else
		{
			throw new RuntimeException("Cannot evaluate code, Language not implemented in boXEPEval.");
		}
	}

	public Object evalJava()
	{
		NameSpace nsp = new NameSpace( new BshClassManager(), "executeJavaCode" );
		bshi = new bsh.Interpreter();
		nsp.importPackage("netgest.bo");
		nsp.importPackage("netgest.bo.def");
		nsp.importPackage("netgest.utils");
		nsp.importPackage("netgest.bo.runtime");
		nsp.importPackage("netgest.bo.utils");

		for (int i = 0; i < packageImports.size(); i++) 
		{
			nsp.importPackage( packageImports.get( i ).toString() );
		}
		bshi.setNameSpace(nsp);
        
		String source = code;
		try
		{
			if(source.indexOf("this") != -1)
			{
				source = source.replaceAll("this","object");
				nsp.setTypedVariable("object", boObject.class, thisObject, false);                        
			}                               
            
            if( attributeValues != null )            
            {
                Iterator itKeys = attributeValues.keySet().iterator();
                while( itKeys.hasNext() )
                {
                    String key = itKeys.next().toString();
                    Object[] value = prepareAttributeValue( ((AttributeHandler)attributeValues.get( key )) );
                    nsp.setTypedVariable( key , (Class)value[1], value[0], false);
                }
            }
            
            if( contextVariables != null )
            {
                Iterator itKeys = attributeValues.keySet().iterator();
                while( itKeys.hasNext() )
                {
                    String key = itKeys.next().toString();
                    nsp.setTypedVariable( key, String.class,contextVariables.get( key ), false  );
                }
            }
			evalReturn = bshi.eval(source);
		}
		catch (Exception e)
		{
			logger.severe("Error evaluating Java Code:\n"+source,e);
			if( throwException )  
			{
				throw new RuntimeException(e);
			}
		}                         
        return evalReturn;
	}
    
	public Object evalXep() throws boRuntimeException
	{
        String source = code;
        try 
        {
            xep = new Xep();
            source = source.replaceAll( "this", "THIS" );
            xep.addCode( source );
            if( thisObject != null )  
            {
                xep.addBoObjectVariable("THIS", thisObject );
            }
            if( attributeValues != null )            
            {
                Iterator itKeys = attributeValues.keySet().iterator();
                while( itKeys.hasNext() )
                {
                    String key = itKeys.next().toString();
                    Object[] value = prepareAttributeValue( ((AttributeHandler)attributeValues.get( key )) );
                    xep.addVariable( key, (Class)value[1], value[0], (String)value[2] );
                }
            }
            if( contextVariables != null )
            {
                Iterator itKeys = contextVariables.keySet().iterator();
                while( itKeys.hasNext() )
                {
                    String key = itKeys.next().toString();
                    xep.addVariable( key, String.class, contextVariables.get( key ),  xwfECMAparser.TYPE_STR );
                }
            }
            
            evalReturn = xep.eval( context );
        } 
        catch (Exception ex) 
        {
			logger.severe("Error evaluating Xep Code:\n"+source,ex);
			if( throwException )  
			{
				throw new RuntimeException(ex);
			}
        }
		return evalReturn;
	}

	public boolean getReturnBoolean()  
	{
		if( evalReturn != null && evalReturn instanceof Boolean )
		{
			return ((Boolean)evalReturn).booleanValue();
		}
		return false;
	}
    
	public String   getReturnString()
	{
		if( evalReturn != null && evalReturn instanceof String )
		{
			return (String)evalReturn;
		}
		return null;
	}
    
	private static Object[] prepareAttributeValue( AttributeHandler att ) throws boRuntimeException
	{
		Object ret[] = null;
		Object value = att.getValueObject();
		if( value instanceof BigDecimal )
		{
			value = new Double( ((BigDecimal)value).doubleValue() );
			ret = new Object[] {value, Double.class, xwfECMAparser.TYPE_DOUBLE };
		}
		else if ( value instanceof java.util.Date )
		{
			Class cls = value!=null?value.getClass():Object.class;
			ret = new Object[] {value, cls, xwfECMAparser.TYPE_DATETIME };
		}
        else
        {
			Class cls = value!=null?value.getClass():Object.class;
			ret = new Object[] {value, cls, xwfECMAparser.TYPE_STR };
        }
		return ret;
	}    
//            xep.addVariable( "value", cls, value, xwfECMAparser.TYPE_CHAR );

//	public void     addVariableString( String name, Object value ) throws boRuntimeException
//	{
//		xep.addVariable( name, String.class, value, xwfECMAparser.TYPE_STR );
//	}


}