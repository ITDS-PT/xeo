/*Enconding=UTF-8*/
package netgest.bo.ql.V2;
import java.util.*;
import netgest.utils.*;
import java.io.PrintStream;
import java.io.PrintWriter;
import netgest.bo.*;

public class boqlParserException extends boException {
    
    public boqlParserException(String src,String code,Exception base) {
        super(src,code,base);
    }
    public boqlParserException(String src,String code,Exception base,String[] args) {
         super(src,code,base,args);
    }
    public boqlParserException(String src,String code,Exception base,String args) {
          super(src,code,base,args);
    }

    private void initErrorCodes() {
       if(p_errors==null) {
            p_errors = new Hashtable();

            // Erros Mais genéricos
            p_errors.put("BOQL",":: ");
            p_errors.put("BOQL-1000","Erro a fazer o parser da Query :1 \n :baserror");
            p_errors.put("BOQL-1001","Erro a fazer o parser da Query :1 \n Erro na expressão :2 \n :baserror  ");
            p_errors.put("BOQL-1002","Erro a fazer o parser da Query :1 \n Numero máximo de Ligações (profundidade) entre objectos execedida \n :baserror  ");

            // Erros boBuildDB
            p_errors.put("BO-1301","Erro a adicionar no NGTDIC a tabéla [:1] \n :baserror");
            p_errors.put("BO-1302","Erro a adicionar no NGTDIC o campo [:1] \n :baserror");
            p_errors.put("BO-1303","Erro a adicionar no NGTDIC o ForeignKey [:1] \n :baserror");
            p_errors.put("BO-1304","Erro a gerar Scripts para a o object [:1] \n :baserror");
            p_errors.put("BO-1305","Tipo de dados não suportado [:1] \n :baserror");
            p_errors.put("BO-1306","Erro de sintaxe no [:1] \n :baserror");

            // 
            p_errors.put("BO-1401","Current Attribute is not a object [:1]");
            p_errors.put("BO-1402","Only Orphan Objects are supported in bridge attributes [:1]");
            p_errors.put("BO-1403","Attribute without type defined [:1]");


            // Errors Class Generator and compiler
            p_errors.put("BO-1501","Error copying xml to deployment dir og business object [:1]");

            // Error of class compiler
            p_errors.put("BO-1601","Error compiling class generated for [:1]");

            // Error Runtime Class Errors
            p_errors.put("BO-2101","Error Loading class for object [:1]");

        }    
    }

}