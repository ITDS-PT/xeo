/*Enconding=UTF-8*/
package netgest.bo.parser;

import netgest.bo.parser.symbol.*;
import netgest.bo.parser.modifiers.*;
import netgest.bo.parser.types.*;
import org.apache.log4j.Logger;
/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class Verifier 
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.parser.Verifier");
    
    private String code;
    private String node;
    /**
     * 
     * @since 
     */
    public Verifier(String s, String nodeName)
    {
        this.code = s; 
        this.node = nodeName;
    }
        
    private String getJava()
    {
        MethodDeclaration mdecl = new MethodDeclaration();
        ModifierClause mc = new ModifierClause();
        mc.setModifier(PrimitiveModifier.PUBLIC);     
        mdecl.setModifiers(mc);
        mdecl.setName(node);
        mdecl.setType(new TypeClause(PrimitiveType.BOOLEAN));
        //mdecl.setParameters();
//        XEORecognizer xeoRec = new XEORecognizer("{" + code + "}");
//        mdecl.setCode(xeoRec.getStatement());
//        return mdecl.toJAVA();
        return null;
    }

    /**
     * 
     * @param args
     * @since 
     */
    public static void main(String[] args)
    {
        
        Verifier verifier = new Verifier("if(tlinha===\"C\" ) {  returnValue={\"333\", \"a\"} } else { returnValue=null }", "valid");
        logger.debug(verifier.getJava());
        System.exit(0);
    }
}