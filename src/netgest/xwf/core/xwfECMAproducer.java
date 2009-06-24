/*Enconding=UTF-8*/
package netgest.xwf.core;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import netgest.bo.runtime.*;
import netgest.bo.security.*;
import netgest.bo.utils.DateUtils;
import netgest.utils.*;
import netgest.bo.def.*;
import netgest.bo.*;
import netgest.bo.ql.*;
import netgest.bo.system.*;

 /**
 * 
 *  Classe responsável pela produção do Java corresondente ao XEP reconhecido pelo parser.
 *  <p>
 *  O parser vai informando-o das ocorrencias reconhecidas.
 *  <p>
 *  Guada vários estados para ir interpretando cada statement da linguagem. Quando termina uma linha esses estados
 *  são reiniciados.
 *  
 * @author Ricardo Andrade
*/
public class xwfECMAproducer 
{
  /**
   * Vector contendo as várias de linha código produzidas
   */
  private Vector code;
  /**
   * Actual linha de código produzida por vários statements 
   */
  private Vector line;
  /**
   * confirma se estamos perante um statement de declaração de variável
   */
  private boolean declar;
  /**
   * confirma se estamos do lado esquerdo de uma atribuição
   */
  private boolean left;
  /**
   * tipo do ultimo objecto referenciado 
   */
  private String objectType;
  /**
   * confirma que uma assignação foi feita anteriormente neste statemente
   */
  private boolean set;
  /**
   * confirma se na navegação de atributos estamos posicionados num atributo
   */
  private boolean inatt;
  /**
   * permite a diferenciação de casos na navegação de atributos
   */
  private boolean extra_inatt = false;
  /**
   * confirma se na navegação de atributos estamos posicionados numa bridge
   */
  private boolean bridge;
  /**
   * numero de parenteses por fechar
   */
  private int par_to_close=0;
  /**
   * numero de parenteses de metódos por fechar
   */
  private int par_meth = 0;
  /**
   * numero de parenteses dos métodos 'equals' usados na transformação do statement
   */
  private int par_equals = 0;
  /**
   * confirma se na navegação de atributos estamos posicionados numa bridge
   */
  private boolean on_bridge = false;
  /**
   * confirma a necessidade de haver uma função de transformação string->boolean
   */
  private boolean boolWarn = false;
  /**
   * Linha de código actual
   */
  private String inline_data="";
  /**
   * Expressão invocadora do método corrente
   */
  private String method_caller="";
/**
 * confirma a necessidade de ser feita referencia a funções previamente definidas no código
 */
  private boolean upper_call=false;
  /**
   * confirma a necessidade de ser feia uma transformação do tipo String->Date
   */
  private boolean dateT=false;
  /**
   * confirma se no memoento é necessário acrescentar a terminação '.getBoui()' ao statemente corrente
   */
  private boolean needBoui = false;
  /**
   * Assinala a existência da palavra null na expressão
   */
  private boolean haveNull = false;
/**
 * pré-statement da linha a analisar
 */
  private String pre_line = "";
  /**
   * Tabela com métodos reconhecidos
   */
  private Hashtable methods;
  /**
   * Vector com código de funções prévias à interpretação do código
   */
  private Vector funct_code;
  /**
   * Pilha de chamadas de métodos num statement
   */
  private Stack lastMpoint;
  /**
   * Construtor por defeito.
   */
  public xwfECMAproducer()
  {
    funct_code = new Vector();
    code = new Vector();
    line = new Vector();
    
    lastMpoint = new Stack();
    
    declar = false;
    left = true;
    objectType = null;
    set = false;
    inatt = false;
    bridge = false;
    needBoui = false;
    inicialMethods();
  }
  /**
   * Inicializa os métodos reconhecidos.
   * <P>A descrição dos métodos seguem uma sinaxe do género: RETURN_TYPE TARGET_TYPE:METH_NAME(ATTRIBUTE_TYPE ATTRIBUTE_NAME) §TRANSFORM_CODE§
   */
  private void inicialMethods()
  {
    methods = new Hashtable();
    putMethod(xwfECMAparser.ASSG_OBJ+" "+xwfECMAparser.TYPE_VOID+":boql("+xwfECMAparser.TYPE_STR+" s) §xm.loadObject(#s#)§ ");
    putMethod(xwfECMAparser.ASSG_OBJ+" "+xwfECMAparser.TYPE_VOID+":boui("+xwfECMAparser.TYPE_INT+" d) §xm.getObject(#d#)§ ");
    putMethod(xwfECMAparser.TYPE_LIST+" "+xwfECMAparser.TYPE_VOID+":boql("+xwfECMAparser.TYPE_STR+" s) §xm.listObject(#s#)§ ");
    putMethod(xwfECMAparser.TYPE_DATE+" "+xwfECMAparser.TYPE_VOID+":now() §DateUtils.getNow()§ ");
    
    putMethod(xwfECMAparser.ASSG_OBJ+" "+xwfECMAparser.ASSG_NEW+":new("+xwfECMAparser.TYPE_STR+" s) §xm.createObject(\"#s#\")§ ");
    putMethod(xwfECMAparser.ASSG_OBJ+" "+xwfECMAparser.ASSG_NEW+":new("+xwfECMAparser.TYPE_STR+" s, "+xwfECMAparser.LIT_NUMB+" d) §xm.createObject(\"#s#\", #d#)§ ");
    putMethod(xwfECMAparser.ASSG_OBJ+" "+xwfECMAparser.ASSG_NEW+":new("+xwfECMAparser.TYPE_STR+" s, "+xwfECMAparser.ASSG_OBJ+" o) §xm.createObject(\"#s#\", #o#.getBoui())§ ");
    putMethod(xwfECMAparser.ASSG_OBJ+" "+xwfECMAparser.TYPE_VOID+":getPerformer() §xm.getObject(xm.getContext().getBoSession().getPerformerBoui())§ ");
    
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.ASSG_OBJ+":save() \" xm.updateObject(##, true)\" \" \" ");
    putMethod(xwfECMAparser.TYPE_BOOL+" "+xwfECMAparser.ASSG_OBJ+":valid() §.valid()§ ");
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.ASSG_OBJ+":destroy() §.destroy()§ ");
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.ASSG_OBJ+":destroyForce() §.destroyForce()§ ");
    putMethod(xwfECMAparser.LIT_NUMB+" "+xwfECMAparser.ASSG_OBJ+":BOUI() §.getBoui()§ ");
    putMethod(xwfECMAparser.TYPE_STR+" "+xwfECMAparser.ASSG_OBJ+":getCARDID() §.getCARDIDwNoIMG().toString()§ ");
    putMethod(xwfECMAparser.TYPE_STR+" "+xwfECMAparser.ASSG_OBJ+":getControllerName() §.getEboContext().getController().getName()§ ");
    
    putMethod(xwfECMAparser.TYPE_BOOL+" "+xwfECMAparser.ATTR_BRDG+":next() §.next()§ ");
    putMethod(xwfECMAparser.TYPE_BOOL+" "+xwfECMAparser.ATTR_BRDG+":remove() §.remove()§ ");
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.ATTR_BRDG+":previous() §.previous()§ ");
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.ATTR_BRDG+":beforeFirst() §.beforeFirst()§ ");
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.ATTR_BRDG+":moveTo("+ xwfECMAparser.TYPE_INT +" d) §.moveTo(#d#)§ ");
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.ATTR_BRDG+":add("+ xwfECMAparser.ASSG_OBJ +" o) §.add(#o#.getBoui())§ ");
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.ATTR_BRDG+":find("+ xwfECMAparser.ASSG_OBJ +" o) §.find(#o#.getBoui())§ ");
    putMethod(xwfECMAparser.TYPE_BOOL+" "+xwfECMAparser.ATTR_BRDG+":haveBoui("+ xwfECMAparser.ASSG_OBJ +" o) §.haveBoui(#o#.getBoui())§ ");
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.ATTR_BRDG+":add("+ xwfECMAparser.LIT_NUMB +" d) §.add(#d#)§ ");
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.ATTR_BRDG+":find("+ xwfECMAparser.LIT_NUMB +" d) §.find(#d#)§ ");
    putMethod(xwfECMAparser.TYPE_BOOL+" "+xwfECMAparser.ATTR_BRDG+":haveBoui("+ xwfECMAparser.LIT_NUMB +" d) §.haveBoui(#d#)§ ");
    putMethod(xwfECMAparser.TYPE_DOUBLE+" "+xwfECMAparser.ATTR_BRDG+":sum("+xwfECMAparser.LIT_NUMB+" n) \" "+xwfECMAparser.ATTR_BRDG+"Sum#!#(##) \"  \"double "+xwfECMAparser.ATTR_BRDG+"Sum#!#(bridgeHandler bl)\n{ double res=0; boBridgeIterator bi = bl.iterator(); bi.beforeFirst(); while(bi.next()){ res += bi.currentRow()#n#; } return res; }\" ");
    
    putMethod(xwfECMAparser.TYPE_INT+" "+xwfECMAparser.TYPE_STR+":length() §.length()§ ");
    putMethod(xwfECMAparser.TYPE_STR+" "+xwfECMAparser.TYPE_STR+":toLowerCase() §.toLowerCase()§ ");
    putMethod(xwfECMAparser.TYPE_STR+" "+xwfECMAparser.TYPE_STR+":toUpperCase() §.toUpperCase()§ ");
    
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.TYPE_LIST+":add("+ xwfECMAparser.LIT_NUMB +" d) §.add(#d#)§ ");
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.TYPE_LIST+":add("+ xwfECMAparser.ASSG_OBJ +" o) §.add(#o#.getBoui())§ ");
    putMethod(xwfECMAparser.TYPE_BOOL+" "+xwfECMAparser.TYPE_LIST+":next() §.next()§ ");
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.TYPE_LIST+":previous() §.previous()§ ");
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.TYPE_LIST+":beforeFirst() §.beforeFirst()§ ");
    putMethod(xwfECMAparser.TYPE_VOID+" "+xwfECMAparser.TYPE_LIST+":moveTo("+ xwfECMAparser.TYPE_INT +" d) §.moveTo(#d#)§ ");
    putMethod(xwfECMAparser.LIT_NUMB+" "+xwfECMAparser.TYPE_LIST+":getCurrentBoui() §.getCurrentBoui()§ ");
    putMethod(xwfECMAparser.ASSG_OBJ+" "+xwfECMAparser.TYPE_LIST+":getCurrentObject() §.getObject()§ ");
    putMethod(xwfECMAparser.TYPE_BOOL+" "+xwfECMAparser.TYPE_LIST+":haveBoui("+ xwfECMAparser.ASSG_OBJ +" o) §.haveBoui(#o#.getBoui())§ ");
    putMethod(xwfECMAparser.TYPE_BOOL+" "+xwfECMAparser.TYPE_LIST+":haveBoui("+ xwfECMAparser.LIT_NUMB +" d) §.haveBoui(#d#)§ ");
    
    putMethod(xwfECMAparser.TYPE_DATE+" "+xwfECMAparser.TYPE_DATE+":sumUtilDays(int d) \"DateUtils.sumToUtilDate(##, 0,0, #d#,xm.getContext())\" \"{}\" ");
    putMethod(xwfECMAparser.TYPE_DATE+" "+xwfECMAparser.TYPE_DATE+":sumDays(int d) \"DateUtils.sumToDate(##, 0,0, #d#)\" \"{}\" ");
    putMethod(xwfECMAparser.TYPE_DATE+" "+xwfECMAparser.TYPE_DATE+":subUtilDays(int d) \"DateUtils.subtractToUtilDate(##, 0,0, #d#,xm.getContext())\" \"{}\" ");
    putMethod(xwfECMAparser.TYPE_DATE+" "+xwfECMAparser.TYPE_DATE+":subDays(int d) \"DateUtils.subtractToDate(##, 0, 0, #d#)\" \"{}\" ");
    putMethod(xwfECMAparser.TYPE_INT+" "+xwfECMAparser.TYPE_DATE+":diffDays(Date d2) \"DateUtils.diffInIntDays(##, #d2#)\" \"{}\" ");
    putMethod(xwfECMAparser.TYPE_INT+" "+xwfECMAparser.TYPE_DATE+":diffUtilDays(Date d2) \"DateUtils.diffInIntUtilsDays(##, #d2#, xm.getContext())\" \"{}\" ");
    putMethod(xwfECMAparser.TYPE_STR+" "+xwfECMAparser.TYPE_DATE+":toString() §.toString()§ ");
    putMethod(xwfECMAparser.TYPE_STR+" "+xwfECMAparser.TYPE_VOID+":format("+xwfECMAparser.TYPE_DATETIME+" d2,"+xwfECMAparser.TYPE_STR+" f) §new java.text.SimpleDateFormat(#f#).format(#d2#)§ ");    
  } 
  /**
     * Introudz na tabela de métodos o método passado através da sua assinatura.
     * <P>Faz o parser da assinatura de forma a decompo-lo nos vários componentes. 
     * @return <code>true</code> caso tenha sido inserido com sucesso, <code>false</code> caso contrário
     * @param sig   Assinatura do método que contém a sua descrição completa
     */
  private boolean putMethod(String sig)
  {
    xwfECMAtokenizer t = new xwfECMAtokenizer(sig);
    t.tokenizeStr();
    String ret = t.getCurrentWord();
    String who = t.getCurrentWord();
    if(!t.lookCurrentWord().equals(":"))
      return false;
    t.incConsumer();
    String name = t.getCurrentWord();
    Vector vpar = new Vector();
    vpar.add(ret);
    if(!t.getCurrentWord().equals("("))
      return false;
    String tipo, nome;
    while(!t.lookCurrentWord().equals(")"))
    {
      tipo = t.getCurrentWord();
      if(!tipo.equals(","))
      {
        nome = t.getCurrentWord();
        vpar.add(new Triple(tipo, nome, null));
      }
    }
    t.incConsumer();
    String code = t.getCurrentWord();
    String upper = null;
    if(!t.endOfText())
    {
      upper = t.getCurrentWord();
    }
   Hashtable ht = (Hashtable)methods.get(who);
    if(ht == null)
    {
      ht = new Hashtable();
      Vector vname = new Vector();
      vname.add(new Triple(vpar, code, upper));
      ht.put(name, vname);
      methods.put(who, ht);
    }
    else
    {
      Vector vname = (Vector)ht.get(name);
      if(vname == null)
      {
        vname = new Vector();
        vname.add(new Triple(vpar, code, upper));
        ht.put(name, vname);
      }
      else
      {
        vname.add(new Triple(vpar, code, upper));
      }
    }
    return true;
  }
  
/**
     * Devolve a terminação '.getBoui()' caso seja necessário 
     * @return terminação '.getBoui()' caso seja necessário
     */
  public String getObjT()
  {
    if(needBoui)
    {
      needBoui = false;
      return ".getBoui()";
    }
    else
      return "";
  }
  /**
     * procede ao correcto tratamento das chavetas
     * @param signal    string com o caracter chaveta, seja a abrir ou a fechar
     */
  public void CHV(String signal)
  {
    if(line.size() > 0)
    {
      code.add(line.clone());
      line.clear();
    }
    line.add(signal);
    code.add(line.clone());
    line.clear();
    if(signal.equals("{"))
      pre_line += "   ";
    else  
      pre_line = pre_line.substring(3);
  }
  /**
     * Constroi uma única string com todo o código interpretado.
     * 
     * @return String com todo o código traduzido 
     */
  public String prodCode()
  {
    String res="";
    String s;
    
    for(int i=0; i<funct_code.size(); i++)
      res += (String)funct_code.get(i)+"\n";
    
    for(int i=0; i<code.size(); i++)
    {
      Vector l = (Vector)code.get(i);
      
      for(int j=0; j<l.size(); j++)
      {
        s = (String)l.get(j);
        res += s;
        if(j+2<l.size() && !(((String)l.get(j+1)).charAt(0) == '.'))
          res += " ";
      }
//      res+="<br>";
        res+="\n";
    }
    return res;
  }
  /**
     * Assinala que estamos posicionados numa bridge na corrente navegação de atributos
     * @param val
     */
  public void setBridge(boolean val)
  {
    bridge = val;
//    inatt = !val;
  }
  /**
     * Assinala que entramos numa bridge na corrente navegação de atributos
     * @param inattVal
     * @param bridval
     */
  public void setOnBridge(boolean bridval, boolean inattVal)
  {
    on_bridge = bridval;
    
    if(inattVal && inatt)
      extra_inatt = true;
      
    inatt = inattVal;
  }
  /**
     * Adiciona o statement a linha de código actual
     * @param s     statement a adicionar
     */
  public void addToLine(String s)
  {
    line.add(s);
    inline_data += s;
  }
  /**
     * Procede à transformação necessaria para o fim de uma linha de código.
     * @return      linha ou statement traduzido
     * @param need  <code>true</code> caso seja necessário um ; no final da linha, <code>false</code> em caso contrário
     */
  public String transfEND(boolean need)
  {
      return transfEND(need, false);
  }
  /**
     * Procede à transformação necessaria para o fim de uma linha de código.
     * @return          linha ou statement traduzido
     * @param java_inj  <code>true</code> caso estejemos perante um stetement de java puro, <code>false</code> em caso contrário 
     * @param need      <code>true</code> caso seja necessário um ; no final da linha, <code>false</code> em caso contrário      
     */
  public String transfEND(boolean need, boolean java_inj)
  {
    String res="";
    upper_call = false;
    if(objectType != null)
    {
      if(objectType.equals("Object"))
        res+=(".get"+objectType+"()");
      else if (objectType.equals("Boolean"))
        res+=(".getValueString()");
      else
        res+=(".getValue"+objectType+"()");
    }
    while(par_to_close > 0)
    {
      res +=(")");
      par_to_close--;
    }
    while(par_equals > 0)
    {
      res +=(")");
      par_equals--;
    }
    if(set)
      res +=(")");
    
    if(need)
    {
      res +=(";");
    
      line.insertElementAt(pre_line, 0);
      line.add(res);
      code.add(line.clone());
      line.clear();
      inline_data = "";
    }
    else
    {
      inline_data += res;
      if(java_inj)
      {
        line.insertElementAt(pre_line, 0);
        line.add(res);
        code.add(line.clone());
        line.clear();
        inline_data = "";  
      }
    }
    left = true;
    declar = false;
    objectType = null;
    inatt = false;
    par_to_close = 0;
    on_bridge=false;
    dateT = false;
    set = false;
    boolWarn = false;
    haveNull = false;
    extra_inatt = false;
    return res;
  }
  /**
     * Procede a transformação do statement durante a avaliação da função OBJ do parser
     * @return statement traduzido
     * @param param     vector com os parametros de inciação do objecto
     * @param object    <code>true</code> caso seja a declaração de objecto XEO, <code>false</code> em caso do tipo base
     * @param type      tipo de objecto invocado 
     */
  public String transfOBJ(String type, boolean object, Vector param)
  {
    upper_call = false;
    if(left)
    {
      declar = true;
      if(!object)
      {
        if(type.startsWith(xwfECMAparser.TYPE_LIST+"."))
          return "boObjectList";
        else
          return type;
      }
      else
      {
        return "boObject";
      }
    }
    else
    {
      if(object)
      {
        if(!set)
        {
            Triple tt = new Triple(xwfECMAparser.TYPE_STR, type, null);
            param.insertElementAt(tt, 0);
            isMethod(xwfECMAparser.ASSG_NEW, xwfECMAparser.ASSG_NEW);
            tt = transfMETH(xwfECMAparser.ASSG_NEW, xwfECMAparser.ASSG_NEW, param, null);
            if(tt != null) 
              return (String)tt.getFirst();
            else
              return null;
        }
        else
        {
            Triple tt = new Triple(xwfECMAparser.TYPE_STR, type, null);
            param.insertElementAt(tt, 0);
            isMethod(xwfECMAparser.ASSG_NEW, xwfECMAparser.ASSG_NEW);
            tt = transfMETH(xwfECMAparser.ASSG_NEW, xwfECMAparser.ASSG_NEW, param, null);
            if(tt != null) 
              return (String)tt.getFirst();
            else
              return null;
          
        }
      }
      else
          return type;
    }
  }
  /**
     * Procede a transformação do statement durante a avaliação da função VAR do parser
     * @return statement traduzido
     * @param varname   nome da variavel invocada
     * @param type      tipo da variavel 
     */
  public String transfVAR(String varname, String type)
  {
    if(type != null && boolWarn && type.equals(xwfECMAparser.TYPE_BOOL))
    {
      String upperBool = "String boolToString(boolean b){ if(b) return \"1\"; else return \"0\";}";
      String res = "boolToString("+varname;
      par_to_close++;
      objectType = null;
      boolWarn = false;
      inline_data += res;  
      if(!funct_code.contains(upperBool))
        funct_code.add(upperBool);
      return (res);
    }
    else
    {
      if(type!= null && type.equals(xwfECMAparser.TYPE_DATE))
        this.setDateSignal();
      objectType = null;
      inline_data += varname;  
      return (varname);
    }
  }
  /**
     * Procede a transformação do statement durante a avaliação da função ATTR do parser
     * @return statement traduzido
     * @param att       nome do attributo invocada
     * @param aType     tipo do atributo
     * @param lastVar   tipo da última variavel com a qual se pode estar a comparar ou atribuir
     */
  public String transfATTR(String att, String aType, String lastVar)
  {
    String sType="";
    String res;
    if(aType.equalsIgnoreCase(xwfECMAparser.TYPE_INT) || aType.equalsIgnoreCase(xwfECMAparser.TYPE_LONG))
    {
        if(!this.left && xwfECMAparser.TYPE_STR.equalsIgnoreCase(lastVar))
            sType = "String";
        else
            if(!this.left && xwfECMAparser.TYPE_DOUBLE.equalsIgnoreCase(lastVar))
                sType = "Double";
            else
                sType = "Long";
      
    }
    else
      if(xwfECMAparser.TYPE_DOUBLE.equalsIgnoreCase(aType)||xwfECMAparser.LIT_NUMB.equalsIgnoreCase(aType))
        if(!this.left && xwfECMAparser.TYPE_STR.equalsIgnoreCase(lastVar))
            sType = "String";
        else
            if(!this.left && (xwfECMAparser.TYPE_INT.equalsIgnoreCase(lastVar) || xwfECMAparser.TYPE_LONG.equalsIgnoreCase(lastVar)))
                sType = "Long";
            else
                sType = "Double";
      else
        if(aType.substring(0,4).equalsIgnoreCase("char") || aType.equalsIgnoreCase("clob"))
          sType = "String";  
        else
          if(xwfECMAparser.TYPE_DATE.equalsIgnoreCase(aType) || xwfECMAparser.TYPE_DATETIME.equalsIgnoreCase(aType))
          {
              if(!this.left && xwfECMAparser.TYPE_STR.equalsIgnoreCase(lastVar))
                sType = "String";
              else
                sType = "Date";
          }
          else
          if(xwfECMAparser.TYPE_BOOL.equalsIgnoreCase(aType))
          {
            sType= "Boolean";
            boolWarn = true;
          }
          else
          {
            sType = "Object";
          }
      if(inatt)
        if(!bridge)
        {
          res = (".getObject().getAttribute(\""+att+"\")");
          objectType = sType;
        } 
        else
        {
          res =  (".getObject().getBridge(\""+att+"\")");
          bridge = false;
          objectType = null;
          if(extra_inatt)
          {
            res += ".getObject()";
            extra_inatt = false;
          }
        }
      else
      {
        if(bridge)
          if(on_bridge)
          {
            res =  (".getBridge(\""+att+"\").getObject()");
            on_bridge = false;
            bridge = false;
            objectType = null;
          }
          else
          {
            res =  (".getBridge(\""+att+"\")");
            bridge = false;
            objectType = sType;
          }
        else
          if(on_bridge)
          {
            res =  (".getAttribute(\""+att+"\")");
            on_bridge = false; 
            objectType = sType;
          }
          else
          {
            res =  (".getAttribute(\""+att+"\")");
            objectType = sType;
          }
        inatt = true;
      }
     inline_data += res;
    return res;
  }
  
  /**
     * Procede a transformação do statement durante a avaliação da função ASSG do parser
     * @return statement traduzido
     * @param op      operador de atribuição usado
     */
  public String transfASSG(String op)
  {
    String res="";
    left = false;
    inatt = false;
    inline_data = "";
    upper_call = false;
    if(objectType == null)
    {
      res += (op);
    }
    else
    {
      String sub = res;
      if(objectType.equals("Object"))
      {
        res += (".setObject(");
//        needBoui = true;
      }
      else if(objectType.equals("Boolean"))
      {
        res += (".setValueObject(");
//        needBoui = true;
      }
      else
        res += (".setValue"+objectType+"(");
      if(op.equals("+=")||op.equals("-="))
      {
        if(objectType.equals("Object"))
          sub+=(".get"+objectType+"()");
        else if (objectType.equals("Boolean"))
          res+=(".getValueString()");
        else
          sub+=(".getValue"+objectType+"()");
        sub+=(op.substring(0,1));
        res += (sub);
      }
      set = true;
    }
    objectType = null;
    return res;
  }
   /**
     * Procede a transformação do statement durante a avaliação da função METH do parser
     * @return statement traduzido
     * @param type     tipo do objecto sobre o qual foi invocado o método
     * @param m        nome do método invocado
     * @param param    vector com os argumentos passados no método  
     * @param assgTo   tipo da variavel a comparar ou a assignar o método
     */
  public Triple transfMETH(String type, String m, Vector param, String assgTo)
  {
    
    String res="";
    upper_call = false;

    Vector vname = (Vector)lastMpoint.pop();
    Hashtable values = new Hashtable();
    String retType="";
    String preres="";
    if(objectType != null && !this.bridge)
    {
        if(objectType.equals("Object"))
            preres += ".get"+objectType+"()";
        else if (objectType.equals("Boolean"))
            res+=(".getValueString()");
        else
            preres += ".getValue"+objectType+"()";
        objectType = null;
    }
    else
      if(this.on_bridge && this.extra_inatt)
      {
        preres += ".getObject()";
        this.extra_inatt = false;
        objectType = null;
      }
    boolean flag=true;
    for(int i=0; i < vname.size(); i++)
    {
      Triple t_sigMeth = (Triple)vname.get(i);
      Vector sigMeth = (Vector)t_sigMeth.getFirst();
      retType = (String)sigMeth.get(0);
      Vector v = new Vector();
      v.add(assgTo);
      v.add(retType);
      if(type.startsWith("List") && retType.equalsIgnoreCase("object"))
        retType = type.substring(5);
      if(param.size() == sigMeth.size()-1 && (assgTo == null || (xwfECMAparser.typeChecking(v, false) >= 0)))
      {
        String upper = (String)t_sigMeth.getThird();
        res = (String)t_sigMeth.getSecond();
          res = res.substring(1, res.length()-1);
        if(upper != null)
        {
          upper_call = true;
          upper = upper.substring(1, upper.length()-1);
          
        }
        res = StringUtils.replacestr(res, "##", method_caller);
        String codeS = method_caller;
         
        
        for(int j=0; j < param.size(); j++)
        {
          Triple tp = (Triple)param.get(j);
          Triple ts = (Triple)sigMeth.get(j+1);
          String type_tp = (String)tp.getFirst();
          String type_ts = (String)ts.getFirst();

          if(xwfECMAparser.typeCode(type_tp) != xwfECMAparser.typeCode(type_ts))
          {
            flag = true;
            break;
          }
          else
          {
            flag = false;
            codeS +=(String)tp.getSecond();
            res = StringUtils.replacestr(res, "#"+(String)ts.getSecond()+"#", (String)tp.getSecond());
            if(upper_call)
              upper = StringUtils.replacestr(upper, "#"+(String)ts.getSecond()+"#", (String)tp.getSecond());
          }
        }
       
        if(!flag)
        {
          String code =  new Integer(Math.abs(codeS.hashCode())).toString();
          res = StringUtils.replacestr(res, "#!#", code);
          if(upper_call)
          {
            upper = StringUtils.replacestr(upper, "#!#", code);
            if(!funct_code.contains(upper) && !upper.equals("{}"))
              funct_code.add(upper);
          }
          break;
        }
        else
          if(param.size()==0)
            flag=false;
        
      }
    }
    
    objectType = null;
    inatt = false;
    method_caller ="";
    bridge = false;
    if(flag)
      return null;
    else
      return new Triple(preres + res, retType, new Boolean(upper_call));

  }
  
 /**
 * Procede a transformação do statement durante a avaliação da função METH do parser
 * @return statement traduzido
 * @param bom      definição do metodo
 * @param param    vector com os argumentos passados no método  
 */
  public Triple transfMETH(boDefMethod bom, Vector param)
  {
    
    String res="";
    upper_call = false;

    int nxml=1;
    
    if(bom.getParentAttribute() != null)
      nxml = bom.getParentAttribute().getMaxOccurs();
      
    if(nxml != 1)
    {
      if(nxml > 1 )
      {
        int ipos = inline_data.lastIndexOf("getBridge");
        if(ipos >= 0)
        {
          String lastatt = inline_data.substring(ipos+9);
          res = ".getAttribute"+lastatt ;
        }
      }
    }
    else
    {
      if(inatt)
        res = ".getObject()";
    }
      

    res += "."+ bom.getName() + "(";
    String[] args = bom.getAssinatureClassNames();
    String retType=bom.getReturnType();
    StringBuffer sb = new StringBuffer();
    if(args.length != param.size())
      return null;
      
    for(int i=0; i < args.length; i++)
    {
      Triple parT = (Triple)param.get(i);
      String p = (String)parT.getFirst();
      if(!args[i].equals(p))
        return null;
      else
        if(i != 0)
          sb.append("," + (String)parT.getSecond());
        else
          sb.append((String)parT.getSecond());
    }
    res += sb.toString() + ")";
    objectType = null;
    inatt = false;
    method_caller ="";
    bridge = false;

    return new Triple(res, retType, new Boolean(upper_call));

  }
  
   /**
     * Procede a transformação do statement durante a avaliação da função OPP do parser
     * @return statement traduzido
     * @param opp       operador usado
     * @param lastType  tipo da última variavel com a qual se esta a comparar
     * @param next      token que se segue ao operador 
     */
  public String transfOPP(String opp, String lastType, String next)
  {
    String res = "";
    if(objectType != null)
    {
      if(objectType.equals("Object"))
        res+=(".get"+objectType+"()");
      else if (objectType.equals("Boolean"))
        res+=(".getValueString()");
      else
        res+=(".getValue"+objectType+"()");
    objectType = null;
    inatt = false;
    }
      if(lastType != null && !next.equals(xwfECMAparser.LIT_NULL))
        res += varTransform(lastType);
    
    while(par_to_close > 0)
    {
      res += (")");
      par_to_close--;
    }
    
    if("==".equals(opp) && xwfECMAparser.typeCode(lastType) == 2)
    {
      res += ".equals(";
      while(par_equals > 0)
      {
        res += (")");
        par_equals--;
      }
      par_equals++;
    } 
    else
      res += (opp);
    inline_data += "";
    return res;
  }
  
   /**
     * Procede a transformação do statement durante a avaliação da função LIT do parser
     * @return statement traduzido
     * @param lit       literal usado
     * @param type      tipo do literal usado
     */
  public String transfLIT(String lit, String type)
  {
    String res= "";
//    upper_call = false;
    if(!set)
      res += (lit);
    else
      if(lit.equals(xwfECMAparser.LIT_TRUE))
      {
        res += ("1");
      }
      else
        if(lit.equals(xwfECMAparser.LIT_FALSE))
        {
          res += ("0");
        }
        else
            res += (lit);
    if(dateT && type.equals(xwfECMAparser.TYPE_STR))
      res = "boConvertUtils.convertToDate("+lit+", null)";

    inline_data += res;
    return res;
  }
  
   /**
     * Função que verifica se existe o método pedido.
     * <P>Durante o seu funcionamento, caso seja identificado o método pretendido, ele é armazenado para futura utilização.  
     * @return <code>true</code> caso o método seja  <code>false</code> caso contrário
     * @param obj       objecto sobre o qual foi invocado o método
     * @param method    nome do método invocado
     */
  public boolean isMethod(String obj, String method)
  {
    Hashtable ht = (Hashtable)methods.get(obj);
    Vector lastMpointer = null;
    if(ht == null)
      return false;
    else
    {
      lastMpointer = (Vector)ht.get(method);
      if(lastMpointer == null)
      {
        return false;
      }
      else
      {
        lastMpoint.push(lastMpointer);
        method_caller = inline_data;
        return true;
      }
    }
  }
  
     /**
     * Função que verifica se actualmente é necessário finalisar o statement com alguma adição de código.   
     * @return statement adição ao statement actual
     * @param type      tipo do último atributo ou variavel usada
     */
  public String varTransform(String type)
  {
    String res="";

    while(par_meth > 0)
    {
      res = ")";
      par_meth--;
    }
    if(type.equals("null"))
    {
      haveNull = true;
    }
    else
      if(!haveNull && (type.equals("object")||xwfECMAparser.typeCode(type)==4))
      {
        if(objectType != null && "Object".equals(objectType))
        {
          res = (".getObject().getBoui()");
        }
        else
          res = (".getBoui()");
          
        objectType = null;
        inatt = false;
      }
    return res;
  }
  /**
   * Adiciona uma função de transformação de string para boolean às funções prévias do código produzido
   * @return devolve se já foi detectada a necessidade de transformação string->boolean
   */
  public boolean getBoolWarn()
  {
    if(boolWarn)
    {
      String upperBool = "String StringToBool(String s){ if(s.equals(\"1\")) return \"1\"; else return \"0\";}";
      if(!funct_code.contains(upperBool))
        funct_code.add(upperBool);
      par_to_close++;
    }
    return boolWarn;
  }
  /**
   * Modificador do membro privado dateT
   */
  public void setDateSignal()
  {
    dateT = true;
  }
}