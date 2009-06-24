/*Enconding=UTF-8*/
package netgest.bo.runtime.templates;
import bsh.*;
import java.math.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import netgest.bo.def.*;
import netgest.bo.runtime.*;
import netgest.utils.*;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XSLException;
import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;

public class boExpressionEval  {
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.templates.boExpressionEval");
    
    public boExpressionEval() {
    
    }
    private Hashtable p_parentdep = new Hashtable();
    private Hashtable p_childdep  = new Hashtable();
    
    
    private boObject p_parent;
    private boObject p_child;
    private boObject p_template;
    
    private String currdestatt;
    
    public static void main(String[] args) {
        //transformToJava("1000Days");
    }

//    public boTemplateManager(boObject templatedef,boObject parent,boObject child) {
//        p_parent = parent;
//        p_child = child;
//        p_template = templatedef;
//        
//    }

    public static void setExpressionAttribute(Hashtable relobj, org.w3c.dom.Node xmlexpression, AttributeHandler att)
    {
        try {
            Object value = evalExpression( relobj , xmlexpression );
            try
            {
                if( value == null )
                {
                    att.setValueObject(null);
                }
                else
                {
                    String sval = value.toString();
                    if ( att.getDefAttribute().getAtributeType()==boDefAttribute.TYPE_ATTRIBUTE )
                    {
                        String type = boDefDataTypeMapping.getDataType(att.getDefAttribute().getType());
                        if( type.equals("Timestamp") )
                        {
                            att.setValueDate( new Date(Long.parseLong( sval ) ) );
                        }
                        else
                        {
                            att.setValueString( sval );   
                        }
                    }
                    else
                    {
                        att.setValueString( sval );
                    }
                    
                }
            }
            catch ( boRuntimeException e )
            {
                logger.error("Error: ", e);
            }
        } 
        catch (boExpressionException e) 
        {
            //Ignore the exception
            throw new boRuntimeException2(" boExpressionEval.setExpressionAttribute\n"+e.getMessage());
        }
    }


    public static Object evalExpression( Hashtable relobj, org.w3c.dom.Node xmlexpression ) throws boExpressionException
    {
        String code = processExpression( relobj , xmlexpression );
        Object ret  = null;
        if( code.length() > 0 ) 
        {
            Interpreter jeval = new Interpreter();
  
            try
            {
                ret = jeval.eval(code);
            }
            catch (EvalError e) 
            {
                logger.error(e);
            }
        }
        return ret;
    }

    public static String processExpression( Hashtable relobj , org.w3c.dom.Node xmlexpression ) throws boExpressionException
    {
        
        LinkedList  tkns         = XMLExpressionTokenizer.getTokens(xmlexpression);
        ArrayList   executeStack = new ArrayList();      
        
        int ptr;
        for( ptr=0 ; ptr<tkns.size() ; ptr++ ) {
            XMLExpressionTokenizer.Token tk =  (XMLExpressionTokenizer.Token)tkns.get(ptr);
            switch (tk.getType()) {
                case XMLExpressionTokenizer.VALUE_NUMBER:
                    ptr = processNUMBER(ptr,tkns,executeStack);
                    break;
                case XMLExpressionTokenizer.VALUE_ATTRIBUTE:
                    ptr = processATTRIBUTE( ptr , tkns , executeStack , relobj );
                    break;
                case XMLExpressionTokenizer.VALUE_OPERATOR:
                case XMLExpressionTokenizer.VALUE_PARENTESES:
                    ptr = processOPERATOR(ptr,tkns,executeStack);
                    break;
                case XMLExpressionTokenizer.VALUE_DATELITERAL:
                    ptr = processDATELITERAL(ptr,tkns,executeStack);
                    break;
                case XMLExpressionTokenizer.VALUE_TEXT:
                    ptr = processTEXT(ptr,tkns,executeStack);
                    break;
                default:
            }
            if(ptr < 0) 
            {
                break;
            }
        }
        StringBuffer result = new StringBuffer();
        
        // if ptr == -1 
        //    Cannot process expression because there are no suficient Objects.
        if( ptr != -1) 
        { 
            ListIterator iter =  executeStack.listIterator();
    
            while ( iter.hasNext() ) 
            {
                result.append( iter.next() );
            }
        } 
        else
        {
            throw new boExpressionException("Attribute expression refers a object witch is no in the context table.");
        }
        return result.toString();
    }
    
    
    private static int processDATELITERAL( int ptr , LinkedList tkn , ArrayList executeStack ) {

        XMLExpressionTokenizer.Token ntkn = ( XMLExpressionTokenizer.Token )tkn.get( ptr );
        
        String expr = ntkn.getString().toLowerCase();
        CodeTokenizer ct = new CodeTokenizer(expr);
        LinkedList wtkns = ct.getTokens();
        
        Date auxdate;
        
        if(wtkns.size()==2 && 
            ((CodeTokenizer.Token)wtkns.get(0)).getType() == CodeTokenizer.NUMBER &&
            ((CodeTokenizer.Token)wtkns.get(1)).getType() == CodeTokenizer.VARIABLE
            )  
        {
            String toapp = ( ( CodeTokenizer.Token )wtkns.get(0) ).getString();
            String word  = ( ( CodeTokenizer.Token )wtkns.get(1) ).getString();
            
            word = uniformUnity(word);
            
            if(word.endsWith("Days")) {
                executeStack.add("("+toapp+"L*86400000L)" );
            } else if(word.equalsIgnoreCase("Weeks")) {
                executeStack.add("("+toapp+"L*86400000L*7L)" );
            } else if(word.equalsIgnoreCase("Months")) {
                executeStack.add("("+toapp+"L*86400000L*30L)" );
            } else if(word.equalsIgnoreCase("Hours")) {
                executeStack.add("("+toapp+"L*86400000L/24L)" );
            } else if(word.equalsIgnoreCase("Minutes")) {
                executeStack.add("("+toapp+"L*86400000L/1440L)" );
            }
        } 
        else if ( (auxdate=ClassUtils.convertToDate(expr))!=null ) 
        {
            executeStack.add(""+auxdate.getTime());
        }
        else
        {
            try {
                executeStack.add(""+Long.parseLong(expr));
            }
            catch (NumberFormatException e)
            {
                throw new boRuntimeException2("Cannor parse date value ["+expr+"] ");                    
            }
        }
        return ptr;
    }
    
    private static int processTEXT( int ptr , LinkedList tkn , ArrayList executeStack ) 
    {
        XMLExpressionTokenizer.Token ntkn = ( XMLExpressionTokenizer.Token )tkn.get( ptr );
        String word = ntkn.getString();
        word = word.replaceAll("\'","\\\"");
        word = word.replaceAll("\'","\\\'");
        executeStack.add( "\"" + word + "\"" );
        return ptr;
    }
    
    private static int processOPERATOR( int ptr , LinkedList tkn , ArrayList executeStack ) 
    {
        executeStack.add( ( ( XMLExpressionTokenizer.Token )tkn.get( ptr ) ).getString() );
        return ptr;
    }

    private static int processNUMBER( int ptr , LinkedList tkn , ArrayList executeStack ) 
    {
        CodeTokenizer ct = new CodeTokenizer( ( ( XMLExpressionTokenizer.Token )tkn.get( ptr ) ).getString() );
        LinkedList wtkns = ct.getTokens();
    
        executeStack.add( ( (CodeTokenizer.Token)wtkns.get(0) ).getString() );
        return ptr;
    }

    private static int processATTRIBUTE( int ptr , LinkedList tkn , ArrayList executeStack , Hashtable ctxobjs )
    {
        String att = ((XMLExpressionTokenizer.Token)tkn.get(ptr)).getString();
        boObject ctxobj;
        
        String[] xatt = att.split( "\\." );
        boolean fromparent=false;
        if(xatt[0].equalsIgnoreCase( "#PARENT#" ) )
        {
            ctxobj = ( boObject )ctxobjs.get( "parent" );
            fromparent = true;
        }
        else
        {
            ctxobj = ( boObject )ctxobjs.get("ctxobj");
        }

        att = att.substring( att.indexOf('.') + 1 );

        if(ctxobj != null &&  ctxobj.getBoDefinition().hasAttribute(att)) {

            AttributeHandler objatt = ctxobj.getAttribute( att );
            
            Object value = null;
    
            try
            {
                value = objatt.getValueObject();
            }
            catch (boRuntimeException e)
            {
                logger.error(e);
            }
            
            String toadd = "";
            
            if( value == null ) {
            
                if(objatt.getDefAttribute().getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE) 
                {
                    toadd = "";
                } 
                else 
                {
                    String dttype = boDefDataTypeMapping.getDataType(objatt.getDefAttribute().getType());
                    if(dttype.equalsIgnoreCase("Timestamp") || dttype.equalsIgnoreCase("BigDecimal"))
                    {
                        if( fromparent )
                        {
                            ptr = -1;
                        }
                        toadd = "0";
                    }
                    else
                    {
                        toadd = "\"\"";
                    }
                }
            }
            else if( value instanceof BigDecimal ) 
            {
                toadd = value.toString()+"D";
            }
            else if ( value instanceof String )
            {
                
                toadd = (String)value;
                toadd = toadd.replaceAll("\'","\\\"");
                toadd = toadd.replaceAll("\'","\\\'");
                toadd = "\""+toadd+"\"";
    
            }
            else if ( value instanceof Timestamp )
            {
                toadd = ""+((Timestamp)value).getTime()+"L";
            }
            
            executeStack.add(toadd);
        }
        else
        {
            ptr = -1;
        }
        return ptr;
    }
    

    public static final void computeProcessStack( Stack stack , String attname , Hashtable atts ) 
    {
        XMLNode xmlnode = ( XMLNode )atts.get( attname );

        if( xmlnode != null ) 
        {
            if( stack.indexOf( attname ) == -1 ) 
            {
                stack.push( attname );
                try
                {
                    NodeList nodes = xmlnode.selectNodes( "//atr" );
                    for (short i = 0 ; i < nodes.getLength() ; i++ ) 
                    {
                        
                        String nextatt = ((XMLNode)nodes.item( i )).getText();
                        computeProcessStack( stack , nextatt , atts );
                        
                    }
                }
                catch ( XSLException e )
                {
                  throw new boRuntimeException2("boExpressionEval.computeProcessStack() "+ e.getMessage() );   
                }
            } 
            else 
            {
                stack.remove( attname );
                stack.push( attname );
            }
        }
    }


    public static final String uniformUnity(String unit) 
    {
        unit = unit.toLowerCase();
        if(unit.startsWith("d"))
            return "Days";
        if(unit.startsWith("me") || unit.startsWith("mo") )
            return "Months";
        if(unit.startsWith("s") || unit.startsWith("w") )
            return "Weeks";
        if(unit.startsWith("h"))
            return "Hours";
        
        return "";

    }
    
    
/*    public void processTemplate() throws boRuntimeException {
        Interpreter xx = new Interpreter();
        try {
            xx.set("parent",p_parent);
            xx.set("child",p_child);
        }
        catch (EvalError e) {
            throw new RuntimeException(e.getMessage());
        }
        p_template.getBridge("mappingAttributes").beforeFirst();
        while(p_template.getBridge("mappingAttributes").next()) {
            String attname   = "";//((templateDetails)p_template.mappingAttributes.getObject()).getAttributename();
            String transform = "";//((templateDetails)p_template.mappingAttributes.getObject()).getAttributetransformation();
            String value     = "";//((templateDetails)p_template.mappingAttributes.getObject()).getAttributevalue();
            try {
                currdestatt = attname;
                if(transform!=null) {
                    String javacode = transformToJava(transform);
                    Object rslt = xx.eval(javacode);
                    logger.debug(transform +" ---> "+ rslt);
                }
            }
            catch (EvalError e) {
                e.printStackTrace();
            }
            
        }
    }

    private static int processNUMBER(int ptr,LinkedList tkn,StringBuffer toapp) {
        
        CodeTokenizer.Token ntkn = (CodeTokenizer.Token)tkn.get(ptr+1);
        if(ntkn.getType()==CodeTokenizer.VARIABLE) {
            String word = ntkn.getString();
            if(word.equalsIgnoreCase("Days")) {
                replaceStringBuffer(toapp,"("+toapp+"*86400000)" );
                ptr++;
            } else if(word.equalsIgnoreCase("Weeks")) {
                replaceStringBuffer(toapp,"("+toapp+"*86400000*7)" );
                ptr++;
            } else if(word.equalsIgnoreCase("Months")) {
                replaceStringBuffer(toapp,"("+toapp+"*86400000*30)" );
                ptr++;
            } else if(word.equalsIgnoreCase("Hours")) {
                replaceStringBuffer(toapp,"("+toapp+"*86400000/24)" );
                ptr++;
            } else if(word.equalsIgnoreCase("Minutes")) {
                replaceStringBuffer(toapp,"("+toapp+"*86400000/1440)" );
                ptr++;
            }
        }
        return ptr;
    }
    private static int processVARIABLE(int ptr,LinkedList tkn,StringBuffer toapp) {
        CodeTokenizer.Token nexttk=null;
        byte xtype=-1;
        if(ptr<tkn.size()-1) {
            nexttk = (CodeTokenizer.Token)tkn.get(ptr+1);
            xtype = nexttk.getType();
        }
        String oper;
        switch (xtype) {
            case CodeTokenizer.OPERATOR:
                // Next to variable is a operator
                oper = nexttk.getString();
                if(oper.equals(".") && toapp.toString().equals("parent")) {
                    CodeTokenizer.Token ntk = (CodeTokenizer.Token)tkn.get(ptr+2);
                    if(ntk.getType()==CodeTokenizer.VARIABLE) { // parent.attribute;
                        if(p_parent.getBoDefinition().hasAttribute(ntk.getString())) {
                            boDefAttribute att = p_parent.getBoDefinition().getAttributeRef(ntk.getString());
                            replaceStringBuffer(toapp,transformAttributeToJava("parent",att.getValueType(),att.getName()));
                            p_parentdep.put(currdestatt,att.getName());
                            ptr+=2;
                        }
                    }
                }
                break;
            default:
                // Try to find the attribute in child object
                oper = ((CodeTokenizer.Token)tkn.get(ptr)).getString();
                if(p_child.getBoDefinition().hasAttribute(oper)) {
                    boDefAttribute att = p_parent.getBoDefinition().getAttributeRef(oper);
                    replaceStringBuffer(toapp,transformAttributeToJava("child",att.getValueType(),att.getName()));
                    p_childdep.put(currdestatt,att.getName());
                    ptr++;
                }
            break;
        }
        return ptr;
    }
    private static final void replaceStringBuffer(StringBuffer sb,String newString) {
        sb.delete(0,sb.length());
        sb.append(newString);
    }
    
    private static final String transformAttributeToJava(String objectref,byte type,String attname) {
        String ret="";
        switch (type) {
            case boDefAttribute.VALUE_NUMBER:
            case boDefAttribute.VALUE_CURRENCY:
            case boDefAttribute.VALUE_DURATION:
            case boDefAttribute.VALUE_CLOB:
            case boDefAttribute.VALUE_BOOLEAN:
            case boDefAttribute.VALUE_CHAR:
                ret = objectref+"."+attname+".getValue()";
                break;
            case boDefAttribute.VALUE_DATE:
            case boDefAttribute.VALUE_DATETIME:
                ret = objectref+"."+attname+".getValue().getTime()";
                break;
        }
        return ret;
    }
    
    public static final void computeProcessStack( Stack stack , String attname , Hashtable atts ) 
    {
        XMLNode xmlnode = ( XMLNode )atts.get( attname );

        if( xmlnode != null ) 
        {
            if( stack.indexOf( attname ) == -1 ) 
            {
                stack.push( attname );
                try
                {
                    NodeList nodes = xmlnode.selectNodes( "//atr" );
                    for (short i = 0 ; i < nodes.getLength() ; i++ ) 
                    {
                        
                        String nextatt = ((XMLNode)nodes.item( i )).getText();
                        computeProcessStack( stack , nextatt , atts );
                        
                    }
                }
                catch ( XSLException e )
                {
                    
                }
            } 
            else 
            {
                stack.remove( attname );
                stack.push( attname );
            }
        }
    }
    
    */
/*    public String  transformToJava(String transformation) {
        StringBuffer result = new StringBuffer();
        XMLExpressionTokenizer.getTokens();
        LinkedList tkns = tkn.getTokens();
        for(int ptr=0;ptr<tkns.size();ptr++) {
            CodeTokenizer.Token tk =  (CodeTokenizer.Token)tkns.get(ptr);
            StringBuffer toapp = new StringBuffer(tk.getString());
            switch (tk.getType()) {
                case CodeTokenizer.NUMBER:
                    ptr = processNUMBER(ptr,tkns,toapp);
                    break;
                case CodeTokenizer.VARIABLE:
                    ptr = processVARIABLE(ptr,tkns,toapp);
                    break;
                case CodeTokenizer.OPERATOR:
                case CodeTokenizer.CHARACTER_LITERAL:
                case CodeTokenizer.STRING_LITERAL_QUOTES:
                case CodeTokenizer.STRING_LITERAL:
                case CodeTokenizer.NEXT_LINE:
                default:
            }
            result.append(toapp);
        }
        return result.toString();
    }*/
    
}
