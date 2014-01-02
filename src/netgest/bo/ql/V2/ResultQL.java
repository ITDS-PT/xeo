/*Enconding=UTF-8*/


package netgest.bo.ql.V2;
import java.util.*;
import netgest.bo.runtime.*;
import netgest.utils.*;
import netgest.bo.def.*;
import netgest.bo.*;
/**
 * Esta classe é usada no parser e é por assim dizer o objecto de retorno da maioria das funções privadas do Parser.<p>
 * É composta por dois campos, o código (0=empty , 1=Success , 2 = ERROR) e uma mensagem que pode ser uma mensagem
 * de erro ou a expressão correspondente quando traduzida com sucesso
 * 
 */
public class ResultQL  {

    private int code; //0=empty , 1=Success , 2 = ERROR
    private String err_msg; //Error / expression
    /**Construtor por defeito*/
    public ResultQL()
    {}
    /**Construtor que define o código do resultado
     * @param code  código da operação realizada*/
    public ResultQL(int code)
    {
        this.code = code;
        this.err_msg = null;
    }
    /**Construtor que define o código do resultado e a mensagem a ser guardada
     * @param code      código da operação realizada*
     * @param err_msg   mensagem de erro ou tradução SQL da instrução processada*/
    public ResultQL(int code, String err_msg)
    {
        this.code = code;
        this.err_msg = err_msg;
    }
    /**Define o código do resultado
     * @param code  código da operação realizada*/
    public void setCode(int code)
    {
        this.code = code;
    }
    /**Define a mensagem de erro ou tradução SQL da instrução processada
     * @param msg   mensagem de erro ou tradução SQL da instrução processada*/
    public void setMessage(String msg)
    {
        this.err_msg = msg;
    }
    
    /**Devolve a mensagem de erro ou tradução SQL da instrução processada
     * @return mensagem de erro ou tradução SQL da instrução processada*/
    public String getMessage()
    {
        return err_msg;
    }
    /**Devovle o código do resultado
     * @return  código da operação realizada*/
    public int getCode()
    {
        return code;
    }
    /**Verifica se o resultado expressa que a instrução foi processada com sucesso
     * @return  <tt>true</tt> se a instrução foi processada com sucesso, <tt>false</tt> caso contrário*/
    public boolean success()
    {
        return code==1;
    }
    /**Verifica se o resultado da instrução foi vazio
     * @return  <tt>true</tt> se o resultado da instrução for vazio, <tt>false</tt> caso contrário*/
    public boolean empty()
    {
        return code==0;
    }
    /**Verifica se o resultado expressa que a instrução foi um erro
     * @return  <tt>true</tt> se a instrução forem encontrados erros na instrução, <tt>false</tt> caso contrário*/
    public boolean failed()
    {
        return code==2;
    }
}
