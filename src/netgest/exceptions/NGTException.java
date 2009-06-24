/*Enconding=UTF-8*/
package netgest.exceptions;

import netgest.utils.*;

public class NGTException extends Exception
{
  private String p_errorCode = null;
  private String p_param1 = null;
  private String p_param2 = null;
  private Exception p_javaException = null;
  private String[] p_errdesc;
  public NGTException(String errorCode,String param1)
  {
       super(errorCode);
       this.p_param1 = param1;
       this.p_errorCode = errorCode;
  }
  public String getMessage() {
       String xret ="";
       int i=0; // Default messages for Errors
       p_errdesc = new String[23];

       // Errors handled by updates bean
       p_errdesc[i++] = "NGT-01003: O campo ? esta repetido";
   	   p_errdesc[i++] = "NGT-01001: Numero com formato invalido no campo ?";
   	   p_errdesc[i++] = "NGT-01002: Data invalida no campo ?";
   	   p_errdesc[i++] = "NGT-01004: Não foi possivel inserir ? num CLOB";
   	   p_errdesc[i++] = "NGT-01005: Não foi encontrada secção master no documento ?";
   	   p_errdesc[i++] = "NGT-01006: O campo da chave primaria ? tem que estar preenchido";
   	   p_errdesc[i++] = "NGT-01007: O campo da chave primaria ? tem que fazer parte da virtual table";
   	   p_errdesc[i++] = "NGT-01008: O(s) campo(s) da chave primaria ? tem que estar preenchido(s)";
   	   p_errdesc[i++] = "NGT-01009: Nao é possivel gravar secções filhas da MASTER ?, pois esta tem multiplas linhas";
   	   p_errdesc[i++] = "NGT-01010: Não tem previlegios Suficientes na tabela ? ";
   	   p_errdesc[i++] = "NGT-01011: A tabela ? nao tem Chaves Primária Defenidas";
   	   p_errdesc[i++] = "NGT-01012: A Virtual Table ? não existe";
   	   p_errdesc[i++] = "NGT-01013: Registo ja foi alterado por outro utilizador";
   	   p_errdesc[i++] = "NGT-01014: Não pode gravar um documento com a seccao master vazia";
   	   p_errdesc[i++] = "NGT-01015: O documento esta vazio";
   	   p_errdesc[i++] = "NGT-01016: Modo invalido para gravacao";


       // Errors handled by SQL server
   	   p_errdesc[i++] = "NGT-01100: Servidor muito ocupado\nSQL:?";
   	   p_errdesc[i++] = "NGT-01101: Chave ja existente\nSQL:?";
   	   p_errdesc[i++] = "NGT-01102: Campo obrigatorio vazio\nSQL:?";
   	   p_errdesc[i++] = "NGT-01103: Erro SQL:\n?";
   	   p_errdesc[i++] = "NGT-01104: Erro XSU:\n?";

       // Server side Execution erros
   	   p_errdesc[i++] = "NGT-01200: Esse EJB não existe";
   	   p_errdesc[i++] = "NGT-01201: O metodo desse EJB não existe";


       for (i=0;i<p_errdesc.length;i++) {
           if (p_errdesc[i].indexOf(this.p_errorCode)>-1) {
                xret = p_errdesc[i];
                break;
           }
       }
       if (i >= p_errdesc.length) xret = p_param1;
       if (xret.indexOf("?")>-1 && p_param1 != null) {
            xret = StringUtils.replacestr(xret,"?",p_param1);
       }
       return xret;
  }

}