/*Enconding=UTF-8*/
package netgest.xwf.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import netgest.bo.runtime.*;
import netgest.bo.security.*;
import netgest.utils.*;
import netgest.bo.def.*;
import netgest.bo.ql.*;
import netgest.bo.*;
import netgest.bo.system.*;
import java.util.regex.*;


/**
 *  Classe responsável pela gestão de tokens para o xwfEMCAparser.
 *  <p>
 *  Divide a string de texto em tokens reconheciveis e disponibiliza funções para aceder a estes
 *  <p>
 * @author Ricardo Andrade
*/
public class xwfECMAtokenizer 
{
    /**
     * vector de tokens a serem reconhecidos
     */
    private Vector words;           
    /**
     * posição na string da query dos vários tokens
     */
    private Vector words_pos;      
    /**
     * string com o texto original
     */
    private String text;           
    /**
     * indicador de posição a ser consumida
     */
    private int consumer;           
    
  /**
   * 
   * Construtor por defeito.
   */
  public xwfECMAtokenizer()
  {
    words = new Vector();
    words_pos = new Vector();
    consumer = 0;
  }
  
  /**
   * Construtor que recebe o texto representando o programa que irá ser dividido.
   * @param xeoProgram  texto represetativo do programa XEO.
   */
  public xwfECMAtokenizer(String xeoProgram)
  {
    words = new Vector();
    words_pos = new Vector();
    consumer = 0;
    text = xeoProgram;
  }
  /**
   * Faz avançar para o próximo token e verifica se o fim do texto foi atingido
   * @return <code> true </code> caso o fim do texto tenha sido alcançado;<code>false </code> caso o fim do texto não tenha sido alcançado;
   */
   public boolean incConsumer()
    {
        consumer++;
        return consumer >= words.size();
    }
    /**
   * Verifica se o final do texto foi alcançado
   * @return <code> true </code> caso o fim do texto tenha sido alcançado;<code>false </code> caso o fim do texto não tenha sido alcançado;
   */
    public boolean endOfText()
    {
        return consumer >= words.size();
    }
   /**
   * Devolve o token que está <code>i</code> posições à frente. Não faz o apontador de tokens avançar.
   * @param i   número de posições de avanço
   * @return token correspondente à posição mais à frente
   */
    public String lookahead(int i)
    {
        if(consumer+i>=words.size())
            return null;
        return (String)words.get(consumer+i);
    }
    /**
   * Permite aceder ao token corrente sem incremetar o apontador
   * @return token corrente
   */
    public String lookCurrentWord()
    {
        if(endOfText())
            return null;
        return (String)words.get(consumer);
    }
    /**
   * Permite aceder ao token corrente incrementando de seguida o apontador
   * @return token corrente
   */
    public String getCurrentWord()
    {
        String s = (String)words.get(consumer);
        incConsumer();
        return s;
    }
/**
 * Faz a divisão em tokens tendo como o base o texto e preenchendo o vector que servirá de base para a gestão de tokens.
 */
    public void tokenizeStr(){
        String tok, tok2;
        //String xxx = ".*#(.*)#.*";
        //String patterns = "[ \\.\\,\\(\\)\\[\\]=<>!\\+\\-\\*/'\\n\\?]|[a-zA-Z_$0-9]++|\\w++|\\W";
        String patterns = "\\{\\*|\\*\\}|\\|\\||&&|\\+=|-=|<=|>=|==|!=|<>|\\+\\+|--|'(\\\\)?.'|§(.*)§|(\"(?:[^\\\\\"]|\\\\.)*\")|[a-zA-Z_][a-zA-Z_0-9]*|[\\p{Punct}\\s}]|[\\w]*";
//        String patterns = "\"(?:[^\\\\\"]|\\\\.)*;$|(\"(?:[^\\\\\"]|\\\\.)*\")|[\\p{Punct}\\s}]|[\\w]*";
        Pattern p = Pattern.compile(patterns, Pattern.UNIX_LINES);
        Matcher m = p.matcher(text);
        
        int curr_pos = 0;    
        while (m.find())
        {
    //        tok = text.substring(m.start(), m.end());
            tok = m.group();
  /*          if(tok.startsWith("{*"))
            {
                for(int i = 1; i <= 3; i++)
                {
                    tok = m.group(i);
                    curr_pos+=tok.length();
                    words.add(tok.trim());   
                    words_pos.add(new Integer(curr_pos));
                }
            }
            else
            {*/
                
                curr_pos+=tok.length(); //incrementando á medida que vamos passando pela string por forma a podermos guardar as posições
                if(tok.length() > 1)    //se os tokens tiverem um tamanho maior que 1 então não existem mais verificações a fazer
                {         
                   words.add(tok.trim());   
                   words_pos.add(new Integer(curr_pos));
                }
               else                 
                  //se tiverem tamanho igual a 1...
                   if(!tok.equals("") && !tok.equals(" ") && tok.charAt(0)!=10 && tok.charAt(0)!=13 && tok.charAt(0)!=3 && tok.charAt(0)!=9)   //...e se forem diferentes de espaços e quebras de linha
                   {
                      words.add(tok.trim());   
                      words_pos.add(new Integer(curr_pos));
                   }
//            }
        }
               
  
  }
  /**
   * Produz uma String que reflete a identificação do primeiro erro dentro do texto intrdoduzido.
   * @return  texto original com o erro realçado.
   */
  public String errorSpot()
  {
    String s = text;
    String sp = "";
    if(consumer != 0)
      sp = s.substring(0, ((Integer)words_pos.get(consumer-1)).intValue()); //retirar a parte antes do erro
    String ss;
    if(endOfText())
        ss = "<b>[...] </b>";   //se o erro for derivado da query não ter terminado correctamente
    else
        ss = "<b>[" + lookCurrentWord() + "]</b> " + s.substring(((Integer)words_pos.get(consumer)).intValue());
        //realçar o erro e juntar o resto da string
    
    String res = sp + ss;
    res = Pattern.compile("[\\n]").matcher(res).replaceAll("<br>");
    return "<br>"+res+"</br>";  //resutlado com a quebra de linha 
  }
  
  /**
   * Devolve a posição que representa o apontador para o token corrente.
   * @return posição actual do vector de tokens.
   */
  public int getConsumer()
  {
    return consumer;
  }
  /**
     * Verifica se o consumo de tokens atingiu um ponto irreversivel q seja considerada uma violação  
     * @return <code> true </code> caso se confirme um avanço superior ao permitido <code>false </code> caso contrário  
     * @param pos   posição de referencia a qual se espera poder voltar se não se verificar violação 
     */
  protected boolean checkViolation(int pos)
  {
    
//      return (pos < consumer) && (!lookahead(-1).equals("(")) && (!lookahead(-1).equals(")"));
    if(consumer > 0)
      if(pos < consumer)
      {
        for(int i=pos - consumer; i < 0; i++)
        {
          if(!lookahead(i).equals("(") && !lookahead(i).equals(")"))
          {
            return true;
          }
        }
        return false;
      }
      else
        return false;
    else
      return false;
  }
  
  /**
     * Extrai todo o texto de um código HTML.
     * <P>Função especifamente desenvolvida para um pequeno propósito. Ficou aqui perdida.
     * @return texto extraído do código HTML
     * @param html  código HTML
     */
    /*
  public String htmlToText(String html)
  {
    String tok1, tok2, tok3, res="";
         

        
        String patterns = "\n";
        Pattern p = Pattern.compile(patterns);
        Matcher m = p.matcher(html);
        res = m.replaceAll("");
        
        patterns = "<p>|<h.>|<table.*>|<tr>|<br>";
        p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
        m = p.matcher(res);
        res= m.replaceAll("\n");
        
        patterns = "<td>";
        p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
        m = p.matcher(res);
        res= m.replaceAll("\t");
        
        patterns = "<[^>]*>";
        p = Pattern.compile(patterns);
        m = p.matcher(res);
        res= m.replaceAll("");
        

    System.out.println(res);
    return res;
    
  }
*/
}