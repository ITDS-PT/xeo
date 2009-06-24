/*Enconding=UTF-8*/
package netgest.xwf.core;

import bsh.Primitive;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import netgest.bo.runtime.*;
import netgest.bo.security.*;
import netgest.utils.*;
import netgest.bo.def.*;
import netgest.bo.*;
import netgest.bo.ql.*;
import netgest.bo.system.*;
import java.util.regex.*;


/**
 * 
 *  Classe responsável pelo reconhecimento da linguagem do XEP.
 *  <p>
 *  Ao longo do reconhecimento vai informando o produtor das ocorrencias encotnradas para que este possa ter informações suficientes
 *  para produzir o Java necessário.
 *  <p>
 *  Efectua a detectação de erros à medida que vai fazendo o reconhecimento.
 * @author Ricardo Andrade
*/

public class xwfECMAparser 
{

  public static final String TYPE_INT = "int";
  public static final String TYPE_LONG = "long";
  public static final String TYPE_DOUBLE = "double";
  public static final String TYPE_DATE = "Date";
  public static final String TYPE_DATETIME = "dateTime";
  public static final String TYPE_BOOL = "boolean";
  public static final String TYPE_STR = "String";
  public static final String TYPE_CLOB = "clob";
  public static final String TYPE_LIST = "List";
  public static final String TYPE_CHAR = "char";
  public static final String TYPE_VOID = "void";
  protected static final String LIT_CHAR = "CHAR";
  protected static final String ASSG_NEW = "new";
  protected static final String ASSG_OBJ = "object";
  protected static final String LIT_TRUE = "true";
  protected static final String LIT_FALSE = "false";
  protected static final String LIT_NULL = "null";
  protected static final String LIT_NUMB = "NUMBER";
  protected static final String ATTR_BRDG = "bridge";
  protected static final String BLOC_IF = "if";
  protected static final String BLOC_ELSE = "else";
  protected static final String BLOC_WHIL = "while";
  protected static final String BLOC_FOR = "for";
  protected static final String BLOC_DO = "do";
  protected static final String ESTR_BRK = "break";
  protected static final String ESTR_CONT = "continue";
  protected static final String BLOC_SWTH = "switch";
  protected static final String BLOC_CASE = "case";
  protected static final String BLOC_DEF = "default";
  protected static final String RET_RETURN = "return";
  protected static final String IMP_IMPORT = "import";
  
  
  private static final String S_ERR1 = "Unknown instruction.";
  private static final String S_ERR2 = "Expecting ;";
  private static final String VAR_ERR1 = "Variable expected.";
  private static final String ATTR_ERR1 = "Missing attribute.";
  private static final String ATTR_ERR2 = "Not a valid attribute for this object.";
  private static final String VARx_ERR1 = "Variable declaration: "; 
  private static final String ASSG_ERR1 = "Assignation expected.";
  private static final String ASSG_ERR2 = "Unknown object.";
  private static final String ASSG_ERR3 = "Uncompleted assignation.";
  private static final String LIT_ERR1 = "Literal expected.";
  private static final String TYPE_ERR1 = "Unknown type.";
  private static final String TYPE_ERR2 = "Invalid variable";
  private static final String TYPE_ERR3 = "Repeated variable definition.";
  private static final String TYPE_ERR4 = "Incorrect List definition: ex: List.objectname varname.";
  private static final String END_ERR1 = "Type Checking error. Diferent types on assigment.";
  private static final String END_ERR2 = "Missing ).";
  private static final String END_ERR3 = "Missing (.";
  private static final String OPP_ERR1 = "Unknown opperator.";
  private static final String BLOC_ERR1 = "Missing }.";
  private static final String BLOC_ERR2 = "Block has not been recognized.";
  private static final String IF_ERR1 = "Uncompleted 'if' structure.";
  private static final String ELSE_ERR1 = "Uncompleted 'else' structure.";
  private static final String WHIL_ERR1 = "Uncompleted 'while' structure.";
  private static final String FOR_ERR1 = "Uncompleted 'for' structure.";
  private static final String DO_ERR1 = "Uncompleted 'do...while' structure.";
  private static final String SWTH_ERR1 = "Uncompleted 'switch' structure.";
  private static final String CASE_ERR1 = "Uncompleted 'case' structure.";
  private static final String RET_ERR1 = "Uncompleted 'return' structure.";
  private static final String EXPR_ERR1 = "No declaration allowed here.";
  private static final String OPP_ERR2 = "Can't equal String objects with opperator.";
  private static final String METH_ERR1 = "No similar method definition exists. Type missmatch.";
  
  private static final String[] opUn = {"++", "--"};
  private static final String[] opAr = {"+", "-", "*", "/"};
  private static final String[] opCd = {"==", "<", ">", "<=", ">=", "!="};
  private static final String[] opLo = {"||", "&&"};
  /**
   * Vector com os operadores lógicos
   */
  private Vector vOpLo;     
  /**
   * Vector com os operadores únicos
   */
  private Vector vOpUn;
  /**
   * Vector com os operadores aritméticos
   */
  private Vector vOpAr;
  /**
   * Vector com os operadores Condicionais
   */
  private Vector vOpCd;
  /**
   * Pilha com os tipos de variáveis usados nas expressões
   */
  private Stack stVar;
/**
 * Gestor de tokens do código a interpretar
 */
  private xwfECMAtokenizer tok;
  /**
   * Produtor de código que vai sendo informado dos elementos reconhecidos pelo parser 
   */
  private xwfECMAproducer prod;
  /**
   * Tabela com as variáveis usadas no código
   */
  private Hashtable variables;
  /**
   * Código java produzido
   */
  private String java_code;  
  /**
   * Sub-conjunto actual dos tipos de variáveis 
   */
  private Vector varType;
  /**
   * Util para o reconhecimento de atributos com bridge. Ao passar por uma bridge memorizamos-a.
   */
  private boDefBridge lastBridge=null;
  /**
   * Atributo actualmente reconhecido
   */
  private boDefAttribute bo_att=null;
  /**
   * Util para o reconhecimento dos atributos dentros de métodos. 
   * Memorizamos o último método encontrado.
   */
  private boDefMethod lastMeth=null;
  /**
   * Define se será necessário a expressão que está a ser analisada terminar com ;
   */
  private boolean endNeed = true;
  /**
   * Define se esta é uma expressão "java injected"
   */
  private boolean java_inj = false;
  /**
   * Passará a true quando for detectada que a expressao trata de uma declaração de variáveis
   */
  private boolean notDecl = false;
  /**
   * Último dos resultados de interpretação, conterá o erro detectado na interpretação em caso de insucesso
   */
  private ResultQL finalres;
  /**
   * String de acumulação de parenteses abertos
   */
  private String parOpen = "";
  /**
   * String de acumulação de parenteses fechados
   */
  private String parClose = "";
  /**
   * número de parenteses abertos
   */
  private int par_open = 0;
  /**
   * Declaraçãoes de imports
   */
  private Vector dec_imports;
  /**
   * Armazenamento de chamadas de métodos
   */
  private Vector mcalls;
  /**
   * Construtor por defeito.
   */
  public xwfECMAparser()
  {
    variables = new Hashtable();
    prod = new xwfECMAproducer();
    varType = new Vector();
    vOpAr = new Vector();
    for(int i = 0; i < opAr.length; i++)
      vOpAr.add(opAr[i]);
    vOpCd = new Vector();
    for(int i = 0; i < opCd.length; i++)
      vOpCd.add(opCd[i]);
    vOpUn = new Vector();
    for(int i = 0; i < opUn.length; i++)
      vOpUn.add(opUn[i]);
    vOpLo = new Vector();
    for(int i = 0; i < opLo.length; i++)
      vOpLo.add(opLo[i]);
      
    stVar = new Stack();
    mcalls = new Vector();
    dec_imports = new Vector();

    
  }
  
  /**
   * Operação de transformação de código XEP em código Java.
   * @param code  código XEP.
   * @return String com o respectivo código Java
   */
  public String toJava(String code)
  {
    prod = new xwfECMAproducer(); 
    tok = new xwfECMAtokenizer(code);
    tok.tokenizeStr();
    java_code = run();
    return java_code;
  }
  
  private String run()
  {
    ResultQL r = new ResultQL(1);
    while(!tok.endOfText() && r.success())
        r = IMP();
    while(!tok.endOfText() && !r.failed())
      r=S();
    finalres = r;
    if(r.failed())
    {
      clearStruct();
      return tok.errorSpot()+"<p>"+r.getMessage();
    }
    else
      if(tok.endOfText())
      {
        return prod.prodCode();
      }
      else
        return "Parsing completed but more tokens existed.";
  }
  
  public boolean success()
  {
    return finalres.success();
  }
  
  public ResultQL finalResult()
  {
    return finalres;
  }
  
  private ResultQL IMP()
  {
    String res="";
    if(tok.endOfText())
      return new ResultQL(2, TYPE_ERR1);
    String s = tok.lookCurrentWord();
    if(s.equals(IMP_IMPORT))
    {
        if(tok.incConsumer())
            return new ResultQL(2, TYPE_ERR1);
        s = tok.lookCurrentWord();
        int i = 0;
        while(!s.equals(";")) 
        {
            i++;
            res += s;
            if(tok.incConsumer())
                return new ResultQL(2, TYPE_ERR1);
            s = tok.lookCurrentWord(); 
        }
        if(i==0)
            return new ResultQL(2, TYPE_ERR1);
        else
        {
            dec_imports.add(res);
            tok.incConsumer();
            return new ResultQL(1, res);
        }
    }
    else
        return new ResultQL(0, null);
  }
  
  private ResultQL S()
  {
    if(tok.endOfText())
      return new ResultQL(0);
    int pos = tok.getConsumer();
    ResultQL resjava = JAVA();
    if(resjava.success() || (tok.getConsumer()>pos && !tok.lookahead(-1).equals("(")))
    {
        this.java_inj = true;
        this.endNeed = false;
        resjava = END(resjava.getMessage(), true);
        this.java_inj = false;
        this.endNeed = true;
        return resjava;
    }
    ResultQL res = ESTR();
    if(res.success() || (tok.getConsumer()>pos && !tok.lookahead(-1).equals("(")))
      return res;
    else
    {
        ResultQL rexp = EXPR();
        if(rexp.success() || (tok.getConsumer()>pos && !tok.lookahead(-1).equals("(")))
          return rexp;
        else
        {
          ResultQL rret = RET();
          if(rret.success() || (tok.getConsumer()>pos && !tok.lookahead(-1).equals("(")))
            return rret;    
          else
            return rexp;
        }
    }
  }
  
  private ResultQL EXPR()
  {
  
    if(tok.endOfText())
      return new ResultQL(2, S_ERR1);
    int pos = tok.getConsumer();
    ResultQL rvar = SVAR();
    if(!rvar.failed() || (tok.getConsumer()>pos && !tok.lookahead(-1).equals("(")))
      return rvar;
    else
    {
      ResultQL robj = OBJ();
      if(!robj.failed() || (tok.getConsumer()>pos && !tok.lookahead(-1).equals("(")))
      {
        if(notDecl)
        {
          return new ResultQL(2, EXPR_ERR1);
        }
        else
          return robj;
      }
      else
      {
        ResultQL rl = LIT();
        if(!rl.failed() || (tok.getConsumer()>pos && !tok.lookahead(-1).equals("(")))
        {
          ResultQL re = END(rl.getMessage());
          return rl;
        }
        else 
        {
          ResultQL rm = METH(ASSG_OBJ);
          if(!rm.failed() || (tok.getConsumer()>pos && !tok.lookahead(-1).equals("(")))
          {
            ResultQL re = END(rm.getMessage());
            return re;
          }
      }
      }
      
      return rvar;
    }
  }
  
  private ResultQL SVAR()
  {
    ResultQL rvar = VAR(false);
    if(rvar.failed())
      return rvar;
    ResultQL rend = END(rvar.getMessage());
    if(rend.failed())
      return rend;
    else
    {
      return new ResultQL(1, rvar.getMessage()+rend.getMessage());
    }
  }
  
  private ResultQL VAR(boolean definition)
  {

      String rp = PARO();
      String rn = NOT();
      String prodVAR=""+rp+rn;
      if(tok.endOfText())
        return new ResultQL(2, VAR_ERR1);
      String s = tok.lookCurrentWord();
      if(!definition)
      {
        String type = (String)variables.get(s);
        if(type!=null)
        {
          tok.incConsumer();
          
          
            
///          prod.VAR(s);
          prodVAR += prod.transfVAR(s, type);
          int pp = mcalls.size();
          ResultQL rattr=null;
          if(tok.lookCurrentWord().equals("."))
          {
//            while(tok.lookCurrentWord().equals("."))
//            {
              tok.incConsumer();
              rattr = ATTR(type);
              if(rattr.failed())
              {
                rattr = METH(type);
                if(rattr.failed())
                  return rattr;
              }
              //else
                if(!rattr.success() && varType.size()==0)
                  varType.add(type);  
                else
                  if(upperMethCall(pp))
                    prodVAR = rattr.getMessage();  
                  else
                    prodVAR += rattr.getMessage();
//            }
          }
          else
            varType.add(type);
        }
        else
          return new ResultQL(2, VAR_ERR1);
      }
      else
      {
        if(!isLegalVarname())
          return new ResultQL(2, TYPE_ERR2);
        else
          if(variables.get(s)!=null)
            return new ResultQL(2, TYPE_ERR3);  
          else
          {
            tok.incConsumer();
//            /prod.VAR(s); 
            prodVAR += prod.transfVAR(s, null);
          }
      }
      ResultQL rvarx = VARx(definition);
      String parc = PARC();
      if(rvarx.success() || !rvarx.failed())
        return new ResultQL(1,prodVAR+" "+rvarx.getMessage()+parc);
      else
        return rvarx;
  }
  
  private String  NOT()
  {
    String res ="";
    if(!tok.endOfText())
    {
      String s = tok.lookCurrentWord();
      if(s.equals("!"))
      {
        
          varType.clear();
          varType.add(TYPE_BOOL);
          stVar.push(varType.clone());
          varType.clear();


        ///prod.addToLine("!");
        tok.incConsumer();
        res += "!"+NOT();
        res += PARO();
      }
      
    }
    return res;
  }
  
  private String PARO()
  {
    String res = parOpen;

    if(!tok.endOfText())
    {
      String s = tok.lookCurrentWord();
      if(s.equals("("))
      {
//       / prod.addToLine("(");
        par_open++;
        tok.incConsumer();
        res += "(" + PARO();
        res += NOT();
      }
    }
    parOpen = res;
    parClose = "";
    return res;
  }
  
  private String PARC()
  {
    String res=parClose;
    if(!tok.endOfText())
    {
      String s = tok.lookCurrentWord();
      if(s.equals(")") && par_open>0)
      {
//        prod.PARC();
        res += ")";
        par_open--;
        tok.incConsumer();
        res += PARC();
      }
    }
    parOpen = "";
//    parClose = res;
    return res;
  }
  
  private ResultQL ATTR(String type)
  {
    if(tok.endOfText())
      return new ResultQL(2, ATTR_ERR1);
    String a = tok.lookCurrentWord();
    String attType = isAttribute(type, a);
    String sres="";
    if(type.equals(TYPE_STR))
      return METH(type);
    else
      if(attType!= null && tok.lookahead(1)!= null && !tok.lookahead(1).equals("("))
      {
        tok.incConsumer();
//        prod.ATTR(a, attType);
        sres = prod.transfATTR(a, attType, this.getLastType(0));
        if(!tok.endOfText() && tok.lookCurrentWord().equals("."))
        {
          tok.incConsumer();
          int pp = mcalls.size();
          ResultQL res = METH(attType);
//          ResultQL res = ATTR(attType);
          if(res.failed())
          {
//            res = METH(attType);
            res = ATTR(attType);
            if(res.failed())
              return res;
          } 
          if(upperMethCall(pp))
            sres = res.getMessage();
          else
            sres += res.getMessage();
          return new ResultQL(1, sres);
        }
        else
        {

          varType.add(attType);
          return new ResultQL(1, sres);
        }
      }
      else
        return new ResultQL(2, ATTR_ERR2);
  }
  
  private ResultQL METH(String type)
  {
    if(tok.endOfText())
      return new ResultQL(2, ATTR_ERR1);
    String a = tok.lookCurrentWord();
    boolean sigPar = isMethod(type, a);
    String res="";
    String assgType=getLastType(0);

    if(sigPar)
    {
//      tok.incConsumer();
      boDefBridge lb = lastBridge;
      
   //   prod.METH(a, attType);
      boDefMethod lm = lastMeth;
      
      if(!tok.endOfText() && tok.lookahead(1).equals("(") )
      {
//        prod.METH(type, a);
//          res += prod.transfMETH(type, a);
//        prod.addToLine("(");
//          res += "(";
      //  PARO();
        lastBridge = null;
        tok.incConsumer();
        stVar.push(varType.clone());
        varType.clear();
        tok.incConsumer();
        Vector param = new Vector();
        xwfECMAproducer aux_prod = prod;
        while(!tok.lookCurrentWord().equals(")"))
        {
            prod = new xwfECMAproducer();
            if(tok.lookCurrentWord().equals(","))
              tok.incConsumer();
//            varType.add(sigPar[i]);
            String rend = "";
            ResultQL rv = VAR(false);
            if(rv.failed())
              rv = LIT();
            else
                if(rv.success())
                    rend = prod.transfEND(false);
            if(rv.failed())
            {
              lastBridge = lb;
              rv = ATTR(type);
              lastBridge = null;
              rend = prod.transfEND(false);
            }                
            if(rv.failed() /*|| tok.incConsumer() */|| (!tok.lookCurrentWord().equals(",") && !tok.lookCurrentWord().equals(")")))
              return new ResultQL(2, ATTR_ERR1);
            else
              param.add(new Triple(varType.lastElement(), rv.getMessage()+rend, null));
            
            if(this.typeChecking(varType, false) == -1)
              return new ResultQL(2, this.END_ERR1);
            else
              varType.clear();
            
        }
        prod = aux_prod;
        String prodMeth="";
        String retType="";
        if(tok.lookCurrentWord().equals(")"))
        {
          //PARC();
          tok.incConsumer();
//          prod.addToLine(")");
//          res += ")";
          Triple tt;
          String passType = type;
          if(!type.equals(TYPE_VOID) && typeCode(type) == 4)
            type =ASSG_OBJ;
          
        String[] args;
        if(lm != null)
        {
          args = lm.getAssinatureArgNames();
          tt = prod.transfMETH(lastMeth, param);
        }
        else
          tt = prod.transfMETH(passType, a, param, assgType);    
        if(tt == null)
          return new ResultQL(2, ATTR_ERR1);
          
          prodMeth = (String)tt.getFirst();
          retType = (String)tt.getSecond();
          mcalls.add(tt.getThird());
          
            if(prodMeth == null)
              return new ResultQL(2, METH_ERR1);
            else
              res += prodMeth;
        }
        if(!tok.lookCurrentWord().equals("."))
          if(!retType.equals("void"))
          {
            varType = (Vector)stVar.pop();
            varType.add(retType);
            
          }  
         //return new ResultQL(1);
         String attrres = "";
         if(".".equals(tok.lookCurrentWord()) && !tok.incConsumer())
         {
            ResultQL mx = ATTR(retType);
            if(mx.success())
            {
                attrres += mx.getMessage();
            }
            else
                if(mx.failed())
                {
                    mx = METH(retType);
                    if(mx.success())
                    {
                        attrres += mx.getMessage();
                    }
                }
         }
         
         
         ResultQL mx = LITx();
         if(mx.failed())
            return  mx;
         else
            return new ResultQL(1, res + " "+ attrres+" "+mx.getMessage());
         
                
      }
        
      else
      {
    //    varType.add(attType[0]);
     //   return new ResultQL(1);
//          ResultQL mx = LITx();
//          if(mx.failed())
//            return  mx;
//          else
//            return new ResultQL(1, res + " "+mx.getMessage());
        prod.setBridge(false);
          return new ResultQL(2, ATTR_ERR2);
      }
    }
    else
      return new ResultQL(2, ATTR_ERR2);
  }
  
  private ResultQL VARx(boolean definition)
  {
    if(tok.endOfText())
        return new ResultQL(2, VARx_ERR1+S_ERR2);
    int pos = tok.getConsumer();
    ResultQL r=null;

      r = ASSG();
      if(r.success() || tok.checkViolation(pos) )
        return r;
      else
        if(definition)
          return r;
    
    pos = tok.getConsumer();
    r = OPP(false);
    if(r.success() || tok.checkViolation(pos))
      return r;
    else
      return new ResultQL(0, "");
  }
  
  private ResultQL ASSG()
  {
    String rp = PARC();
    String res= "";
    if(tok.endOfText())
      return new ResultQL(2, ASSG_ERR1);
    String s = tok.lookCurrentWord();
    if(s.equals("=") || s.equals("+=")|| s.equals("-="))
    {
      tok.incConsumer();
//      prod.ASSG(s);
      res += prod.transfASSG(s);
      stVar.push(varType.clone());
      varType.clear();

      if(s.equals("=") && tok.lookCurrentWord().equals(ASSG_NEW))
      {
        if(!tok.incConsumer())
        {
//          prod.ASSG("new");
//            res += prod.transfASSG(ASSG_NEW);    
          String sobj = tok.lookCurrentWord();
          sobj = isObj(sobj);
          if(sobj != null)
          {
            
            if(!tok.incConsumer() && tok.lookCurrentWord().equals("(") && !tok.incConsumer())
            {
//              prod.OBJ(sobj, true);
                
              Vector param = PARM(sobj);
              if(param == null)
                return new ResultQL(2, END_ERR1);
              String pobj = prod.transfOBJ(sobj, true, param);
              if(pobj != null)
                res += " " + pobj;
              else
                return new ResultQL(2, METH_ERR1);
              /*if(rparm.failed())
                return rparm;
              else*/
                if(tok.lookCurrentWord().equals(")"))
                {
                    tok.incConsumer();
                    return new ResultQL(1,res + needBoui("ASSG"));
                }
                else
                  return new ResultQL(2, ASSG_ERR3);
            }
            else
              return new ResultQL(2, ASSG_ERR3);
          }
          else
            return new ResultQL(2, ASSG_ERR2);  
        }
        else
          if(vOpAr.indexOf(s)>=0)
          {
            tok.incConsumer();
            int d = 2;
            int c;
            c = 2 + 3 + 3;
            return VAR(false);
          }
          return new ResultQL(2, ASSG_ERR3);
      } 
      int pos = tok.getConsumer();
      ResultQL rvar = VAR(false);
      if(rvar.success())
        if(prod.getBoolWarn())
        {
          return new ResultQL (1, rp + res + " StringToBool("+rvar.getMessage());
        }
        else
          return new ResultQL (1, rp + res + " "+rvar.getMessage()+ needBoui("ASSG"));
      else
        if(tok.checkViolation(pos))
          return rvar;
        else
        {
          rvar = LIT();
          if(rvar.failed() || pos == tok.getConsumer())
          {
            int pp = mcalls.size();
            rvar = METH(TYPE_VOID);
            if(rvar.failed())
              return rvar;
            else
              if(upperMethCall(pp))
                return new ResultQL (1, rp + " "+rvar.getMessage());
              else
                return new ResultQL (1, rp + res + " "+rvar.getMessage());
          }
          else
            return new ResultQL (1, rp + res + " "+rvar.getMessage());
        }
    }
    else
      return new ResultQL(0);
  }
  
  private ResultQL LIT()
  {
    String res = "" + PARO();
    if(tok.endOfText())
      return new ResultQL(2, LIT_ERR1);
    ResultQL resjava = JAVA();
    if(resjava.success())
    {
        return new ResultQL(1, resjava.getMessage() + " "+LITx().getMessage() );
    }
    String s = isNumber();
    if(s != null)
    {
      
//      prod.LIT(s, LIT_NUMB);
        res += prod.transfLIT(s, LIT_NUMB);
      //LITx();
      return new ResultQL(1, res + " "+LITx().getMessage() );
    }
    else
    {
      s = isString();
      if(s!=null)
      {
        varType.add(TYPE_STR);
//        prod.LIT(s, TYPE_STR);
        
        res += prod.transfLIT(s, TYPE_STR);
        //LITx();
        return new ResultQL(1, res + " "+LITx().getMessage() );
      }
      else
      {
        s = isChar();
        if(s!=null)
        {
          varType.add(LIT_CHAR);
//          prod.LIT(s, LIT_CHAR);
          
          res += prod.transfLIT(s, LIT_CHAR);
          //LITx();
          return new ResultQL(1, res + " "+LITx().getMessage() );
        }
        else
        {
          s = tok.lookCurrentWord();
          if(s.equals(LIT_TRUE) || s.equals(LIT_FALSE) || s.equals(LIT_NULL))
          {
            if(s.equals(LIT_NULL))
            {
              varType.add(LIT_NULL);
//              prod.LIT(s, LIT_NULL);
              res += prod.transfLIT(s, LIT_NULL);
            }
            else
            {
              varType.add(TYPE_BOOL);
//              prod.LIT(s, TYPE_BOOL);
              res += prod.transfLIT(s, TYPE_BOOL);
            }
            
            tok.incConsumer();
            
            //LITx();
            return new ResultQL(1, res + " "+LITx().getMessage() );
          }
          else
            return new ResultQL(2, LIT_ERR1);
        }
      }
    } 
  }
  
  private ResultQL LITx()
  {
    ResultQL res = OPP(false);
    if(res.success())
      return res;
    else
      return new ResultQL(0, res.getMessage());
  }
  private ResultQL OPP(boolean last_unique)
  {
    boolean oppc = false;
    String sres = PARC();
    String last = null;
    if(varType.size() > 0)
      last = (String)varType.lastElement();
    ResultQL res = OPPA();
    if(res.failed())
    {
//      stVar.push(varType.clone());
//      varType.clear();

      res = OPPC();
      if(res.success())
      {
//          prod.argMeth((String)varType.lastElement());
          oppc = true;
          
      }
      else
        if(res.failed())
        {
          if(!last_unique)
            res = OPPU();
          if(res.success())
          {
//            prod.OPP(res.getMessage());
            varType.add(LIT_NUMB);
            int pos = tok.getConsumer();
            ResultQL ropp = OPP(true);
            if(ropp.success() || pos < tok.getConsumer())
              return ropp;
            else
              return res;
          }
          else
          {
            res = OPPL();
            if(res.success())
            {
              if(typeChecking(varType, false)>-1)
              {
                varType.clear();
                varType.add(TYPE_BOOL);
                stVar.push(varType.clone());
                varType.clear();
              }
              else
                return new ResultQL(2, END_ERR1);
            }
            else
              return new ResultQL(2, sres);
          }
        }
        else
          return new ResultQL(2, OPP_ERR1);


    }
      
      sres += prod.transfOPP(res.getMessage(), last, tok.lookCurrentWord());
//    prod.OPP(res.getMessage());
    
    res = VAR(false);
    String ss="";
    if(res.failed())
    {
      res = LIT();
      if(res.failed())
      {
        res = METH(TYPE_VOID);
        if(res.failed())
          return res;
      }
    }
    ss = res.getMessage();
      if(varType.size() > 0)
        ss += prod.varTransform((String)varType.lastElement());

      else
      {
        varType = (Vector)stVar.pop();
        ss += prod.varTransform((String)varType.lastElement());
      }
    
    if(oppc)
      if(typeChecking(varType, false)>-1)
      {
       
        varType.clear();
        varType.add(TYPE_BOOL);
        stVar.push(varType.clone());
        varType.clear();
      }
      else
        return new ResultQL(2, END_ERR1);
  
    return new ResultQL (1, sres + " " +ss);
  }
  
  private ResultQL OPPA()
  {
    if(tok.endOfText())
      return new ResultQL(2, ASSG_ERR1);
    String s = tok.lookCurrentWord();
    if(vOpAr.indexOf(s)>=0)
    {
      tok.incConsumer();
      return new ResultQL(1, s);
    }
    else
      return new ResultQL(2, OPP_ERR1);
  }
  
  private ResultQL OPPC()
  {
    if(tok.endOfText())
      return new ResultQL(2, ASSG_ERR1);
    String s = tok.lookCurrentWord();
    if(vOpCd.indexOf(s)>=0)
    {
//FC: retirei estas dua linhas pois não estava a permitir a comparação Str com str
//      if(varType.size() > 0 && ((String)varType.lastElement()).equals(TYPE_STR))
//        return  new ResultQL(2, OPP_ERR2);
      tok.incConsumer();
      
      return new ResultQL(1, s);
    }
    else
      return new ResultQL(2, OPP_ERR1);
  }
  
  private ResultQL OPPU()
  {
    if(tok.endOfText())
      return new ResultQL(2, ASSG_ERR1);
    String s = tok.lookCurrentWord();
    if(vOpUn.indexOf(s)>=0)
    {
      tok.incConsumer();
      
      return new ResultQL(1, s);
    }
    else
      return new ResultQL(2, OPP_ERR1);
  }
  
  private ResultQL OPPL()
  {
    if(tok.endOfText())
      return new ResultQL(2, ASSG_ERR1);
    String s = tok.lookCurrentWord();
    if(vOpLo.indexOf(s)>=0)
    {
      tok.incConsumer();
      
      return new ResultQL(1, s);
    }
    else
      return new ResultQL(2, OPP_ERR1);
  }
  
  private ResultQL OBJ()
  {
    String res="";
    if(tok.endOfText())
      return new ResultQL(2, TYPE_ERR1);
    ResultQL rtype = TYPE();
    String stype;
    if(rtype.success())
    {
      stype = rtype.getMessage();
//      prod.OBJ(stype, false); 
      res = prod.transfOBJ(stype, false, new Vector());
    }
    else
    {
      stype = isObj(tok.lookCurrentWord());
      if(stype != null)
      {
        //stype = tok.lookCurrentWord();
//        prod.OBJ(stype, true);
        res = prod.transfOBJ(stype, true, new Vector());
      }
      else
        return new ResultQL(2, TYPE_ERR1);
    }
    
    
    
    if(tok.incConsumer())
      return new ResultQL(2, TYPE_ERR1);
    else
    {
      String v = tok.lookCurrentWord();
      varType.add(stype);
      ResultQL rvar = VAR(true);
      

      
      if(rvar.failed())
        return rvar;
      variables.put(v, stype);
      ResultQL rend = END(res + " " + rvar.getMessage());
      if(rend.failed())
        return rend;
      else
      {

        return new ResultQL(1, res);
      }
    }
  }
  
  private Vector PARM(String sobj)
  {
    boDefBridge lb = lastBridge;
    boDefMethod lm = lastMeth;
    
    lastBridge = null;
    stVar.push(varType.clone());
    varType.clear();
    Vector param = new Vector();
    while(!tok.lookCurrentWord().equals(")"))
    {
        varType.add(null);
        if(tok.lookCurrentWord().equals(","))
          tok.incConsumer();
//            varType.add(sigPar[i]);
        String rend = "";
        ResultQL rv = VAR(false);
        if(rv.failed())
          rv = LIT();
        if(rv.failed())
        {
          lastBridge = lb;
          rv = ATTR(sobj);
          lastBridge = null;
          rend = prod.transfEND(false);
        }
        if(rv.failed() /*|| tok.incConsumer() */|| (!tok.lookCurrentWord().equals(",") && !tok.lookCurrentWord().equals(")")))
          return null;
        else
          param.add(new Triple(varType.lastElement(), rv.getMessage()+rend, null));
        if(varType.get(0) == null)
          varType.remove(0);
        if(this.typeChecking(varType, false) == -1)
          return null;
        else
          varType.clear();
        
    }
    
    lastBridge = lb;
    lastMeth = lm;
  
//    if(!tok.lookCurrentWord().equals(")"))
//      tok.incConsumer();
    return param;
  }
  
  private ResultQL TYPE()
  {
    if(tok.endOfText())
      return new ResultQL(2, TYPE_ERR1);
    String s = tok.lookCurrentWord();
    if(s.equals(TYPE_INT)||s.equals(TYPE_DOUBLE)||s.equals(TYPE_BOOL)||s.equals(TYPE_STR)||s.equals(TYPE_CHAR)||s.equals(TYPE_DATE))
    {
      if(s.equals(TYPE_DATE))
        prod.setDateSignal();
      varType.add(s);
      return new ResultQL(1, s);
    }
    else
      if(s.equals(TYPE_LIST) && !tok.incConsumer())
      {
        s = tok.lookCurrentWord();
        if(s.equals(".") && !tok.incConsumer())
        {
          String stype = isObj(tok.lookCurrentWord());
          if(stype != null)
          {
            //stype = tok.lookCurrentWord();
            //prod.OBJ(stype, true);
            return new ResultQL(1, TYPE_LIST+"."+stype);
          }
            return new ResultQL(2, TYPE_ERR4);
        }
        else
          return new ResultQL(2, TYPE_ERR4);
      }
      else
      return new ResultQL(2, TYPE_ERR1);
  }
  
  private ResultQL ESTR()
  {
    if(tok.endOfText())
      return new ResultQL(2, TYPE_ERR1);
    String s = tok.lookCurrentWord();
    if(s.equals(ESTR_BRK) || s.equals(ESTR_CONT))
    {
//      prod.addToLine(s);
      tok.incConsumer();
      return END(s);
    }
    int pos = tok.getConsumer();
    ResultQL rtype = IF();
    if(rtype.failed() && pos == tok.getConsumer())
    {
      rtype = WHIL();
      if(rtype.failed() && pos == tok.getConsumer())
      {
        rtype = FOR();
        if(rtype.failed() && pos == tok.getConsumer())
        {
          rtype = DOWH();
          if(rtype.failed() && pos == tok.getConsumer())
            rtype = SWTH();
        }
      } 
    }
    return rtype;
  }
  
  private ResultQL BLOC()
  {
    if(tok.endOfText())
      return new ResultQL(2, TYPE_ERR1);
    String s = tok.lookCurrentWord();
    if(s.equals("{") && !tok.incConsumer())
    {
      ResultQL res;
      prod.CHV("{");
      
      do
      {
        res = S();
        if(tok.endOfText())
          return new ResultQL(2, BLOC_ERR1);
        else
          if(res.failed())
            return res;
        s = tok.lookCurrentWord();
      }while(!s.equals("}"));
      tok.incConsumer();
      prod.CHV("}");

      
      return new ResultQL(1);
    }
    else
      return new ResultQL(2, BLOC_ERR2);
  }
  
  private ResultQL IF()
  {
    if(tok.endOfText())
      return new ResultQL(2, TYPE_ERR1);
    String s = tok.lookCurrentWord();
    String sres="";
    ResultQL res=null;
    if(s.equals(BLOC_IF))
    {
      if(tok.incConsumer() || !tok.lookCurrentWord().equals("("))
        return new ResultQL(2, IF_ERR1);
//      prod.addToLine(BLOC_IF);  
      sres += BLOC_IF;
      varType.add(TYPE_BOOL);
      stVar.push(varType.clone());
      varType.clear();
      
//      prod.addToLine("(");
      sres += "( ";
      tok.incConsumer();

      endNeed = false;
      notDecl = true;
      res = EXPR();
      endNeed = true;
      notDecl = false;

      if(res.failed())
        return res;
      else
      {
        s = tok.lookCurrentWord();
        if(s.equals(")"))
        {
            tok.incConsumer();
            sres += res.getMessage() + " ) ";
//            prod.addToLine(")");
            prod.addToLine(sres + "\n");
            int pos = tok.getConsumer();
            res = BLOC();
            if(!res.failed() || tok.getConsumer()>pos)
              ;
            else
            {
              notDecl = true;
              res = S();
              notDecl = false;
              if(res.failed())
                return res;
            }
            res = ELSE();
            if(res.success())
              return res;
            else
              return new ResultQL(1);
            
        }
        else
          return new ResultQL(2, IF_ERR1);
      }
    }
    else
      return new ResultQL(2, IF_ERR1);
  }
  
  private ResultQL ELSE()
  {
    if(tok.endOfText())
      return new ResultQL(2, TYPE_ERR1);
    String s = tok.lookCurrentWord();
    if(s.equals(BLOC_ELSE))
    {
      prod.addToLine(BLOC_ELSE+" "); 
      if(tok.incConsumer())
        return new ResultQL(2, ELSE_ERR1);
      int pos = tok.getConsumer();
      ResultQL res = BLOC();
      if(!res.failed() || tok.getConsumer()>pos)
        return res;
      else
      {
        notDecl = true;
        res = S();
        notDecl = false;
        if(res.failed())
          return res;
      }
      return res;
    }
    else 
      return new ResultQL(0);
  }
  
  private ResultQL WHIL()
  {
    if(tok.endOfText())
      return new ResultQL(2, TYPE_ERR1);
    String s = tok.lookCurrentWord();
    ResultQL res=null;
    String sres="";
    if(s.equals(BLOC_WHIL))
    {
      if(tok.incConsumer() || !tok.lookCurrentWord().equals("("))
        return new ResultQL(2, WHIL_ERR1);
//      prod.addToLine(BLOC_WHIL);  

      sres += BLOC_WHIL;
      varType.add(TYPE_BOOL);
      stVar.push(varType.clone());
      varType.clear();
      
//      prod.addToLine("(");
      sres += "(";
      tok.incConsumer();
      
      notDecl = true;
      endNeed = false;
      res = EXPR();
      endNeed = true;
      notDecl = false;
      
      sres += " "+res.getMessage();
      if(res.failed())
        return res;
      else
      {
        s = tok.lookCurrentWord();
        if(s.equals(")"))
        {
          tok.incConsumer();
//          prod.addToLine(")");
            sres += ")";
            prod.addToLine(sres);
            int pos = tok.getConsumer();
            res = BLOC();
            if(!res.failed() || tok.getConsumer()>pos)
              return res;
            else
            {
              notDecl = true;
              res = S();
              notDecl = false;
              if(res.failed())
                return res;
            }
            return res;
            
        }
        else
          return new ResultQL(2, WHIL_ERR1);
      }
    }
    else
      return new ResultQL(2, TYPE_ERR1);
  }
  
  private ResultQL FOR()
  {
    if(tok.endOfText())
      return new ResultQL(2, TYPE_ERR1);
    String s = tok.lookCurrentWord();
    ResultQL res=null;
    String sres="";
    if(s.equals(BLOC_FOR))
    {
      if(tok.incConsumer() || !tok.lookCurrentWord().equals("("))
        return new ResultQL(2, FOR_ERR1);
//      prod.addToLine(BLOC_FOR+"(");  
      sres += BLOC_FOR+"(";
      
      tok.incConsumer();
      endNeed = false;
      if(!tok.lookCurrentWord().equals(";") )
      {
        
        res = EXPR();
        if(res.failed())
          return res;
        else
          sres += " "+res.getMessage();
      }
      /*else
      {
        tok.incConsumer();
//        prod.addToLine(";");
      }*/
      if(tok.lookCurrentWord().equals(";") && !tok.incConsumer())
      {
//        prod.addToLine(";");
        sres += ";";
        if(!tok.lookCurrentWord().equals(";"))
        {
          varType.add(TYPE_BOOL);
          stVar.push(varType.clone());
          varType.clear();
        
          notDecl = true;
          res = EXPR();
          notDecl = false;
          if(res.failed())
            return res;    
          else
            sres += " "+res.getMessage();
        }
      }
      
      
      if(tok.lookCurrentWord().equals(";") && !tok.incConsumer())
      {
//        prod.addToLine(";");
        sres += ";";
        //par_open++;
        if(!tok.lookCurrentWord().equals(")"))
        {
          res = EXPR();
          if(res.failed())
              return res;    
          else
            sres += " "+res.getMessage();
        }
      }
      endNeed = true;

      s = tok.lookCurrentWord();
      if(res!=null && res.failed()&&!(s.equals(")") && tok.lookahead(-1).equals(";")))
        return res;
      else
      {
        if(s.equals(")") && par_open == 0)
        {
//          prod.addToLine(")");
          sres += ")";
          tok.incConsumer();
          prod.addToLine(sres);
        }
        if(tok.lookahead(-1).equals(")"))
        {
            int pos = tok.getConsumer();
            res = BLOC();
            if(!res.failed() || tok.getConsumer()>pos)
              return res;
            else
            {
              res = S();
              if(res.failed())
                return res;
            }
            return res;
            
        }
        else
          return new ResultQL(2, FOR_ERR1);
      }
    }
    else
      return new ResultQL(2, TYPE_ERR1);
  }
  
  private ResultQL DOWH()
  {
    if(tok.endOfText())
      return new ResultQL(2, TYPE_ERR1);
    String s = tok.lookCurrentWord();
    String sres="";
    ResultQL res=null;
    if(s.equals(BLOC_DO))
    {
      if(tok.incConsumer())
        return new ResultQL(2, TYPE_ERR1);
//      prod.addToLine(BLOC_DO);  
      sres += BLOC_DO;
      prod.addToLine(sres);
      int pos = tok.getConsumer();
      res = BLOC();
      if(!res.failed() || tok.getConsumer()>pos)
        ;
      else
      {
        if(tok.getConsumer() == pos)
        {
          notDecl = true;
          res = S();
          notDecl = false;
          if(res.failed())
            return res;
        }
      }
      sres="";
      if(tok.lookCurrentWord().equals(BLOC_WHIL) && !tok.incConsumer() && tok.lookCurrentWord().equals("("))
      {
//        prod.addToLine(BLOC_WHIL);
        sres = BLOC_WHIL;
        varType.add(TYPE_BOOL);
        stVar.push(varType.clone());
        varType.clear();
        
        tok.incConsumer();
//        prod.addToLine("(");
        sres += "(";
        notDecl = true;
        endNeed = false;
        res = EXPR();
        endNeed = true;
        notDecl = false;
        if(res.failed())
          return res;
        if(tok.lookCurrentWord().equals(")"))   
        {
          tok.incConsumer();
//          prod.addToLine(")");
          sres+= res.getMessage() + ")";
          return END(sres);
        }
        else
          return new ResultQL(2, DO_ERR1);
      }
      else
        return new ResultQL(2, DO_ERR1);
    }
    else
      return new ResultQL(2, DO_ERR1);
  }
  
  private ResultQL SWTH()
  {
    if(tok.endOfText())
      return new ResultQL(2, TYPE_ERR1);
    String s = tok.lookCurrentWord();
    ResultQL res=null;
    String sres="";
    if(s.equals(BLOC_SWTH))
    {
      if(tok.incConsumer() || !tok.lookCurrentWord().equals("("))
        return new ResultQL(2, SWTH_ERR1);
//      prod.addToLine(BLOC_SWTH);  
        sres += BLOC_SWTH;
      tok.incConsumer();
//      prod.addToLine("(");
      sres += "( ";
      endNeed = false;
      res = EXPR();
      sres += res.getMessage();
      
      
      if(res.failed())
        return res;
      else
      {
        s = tok.lookCurrentWord();
        if(s.equals(")"))
        {
//          prod.argMeth((String)varType.lastElement());
          sres += ")";
          tok.incConsumer();
//          prod.addToLine(")");
        }
        else
          return new ResultQL(2, SWTH_ERR1);
        s = tok.lookCurrentWord();
        if(s.equals("{") && !tok.incConsumer())
        {
          //prod.addToLine("{\n");
          prod.addToLine(sres);
          prod.CHV("{");
          
          
          endNeed = true;          
          do
          {
            res = CASE(BLOC_CASE);
          }while(!res.failed());
          
          CASE(BLOC_DEF);
          if(tok.lookCurrentWord().equals("}"))
          {
            //prod.addToLine("}\n");
            prod.CHV("}");
            tok.incConsumer();
          }
          else
            return new ResultQL(2, SWTH_ERR1);
          return new ResultQL(1, sres);
        }
        else
          return new ResultQL(2, SWTH_ERR1);
      }
    }
    else
      return new ResultQL(2, TYPE_ERR1);
  }
  
  private ResultQL CASE(String match)
  {
    if(tok.endOfText())
      return new ResultQL(2, TYPE_ERR1);
    String s = tok.lookCurrentWord();
    ResultQL res=null;
    String sres="";
    if(s.equals(match) && !tok.incConsumer())
    {
//      prod.addToLine(match);
      sres+= match;
      if(match.equals(BLOC_CASE))
      {
        res = LIT();
        if(res.failed())
          return res;
        else
          sres += " " + res.getMessage();
      }
      s = tok.lookCurrentWord();
      if(s.equals(":") && !tok.incConsumer())
      {
//        prod.addToLine(":");
        sres += ":";
        prod.addToLine(sres);
        int pos = tok.getConsumer();
        res = BLOC();
        if(!res.failed() || tok.getConsumer()>pos)
          return res;
        else
          res = S();
        return res;
      }
      else
        return new ResultQL(2, CASE_ERR1);
    }
    else
      return new ResultQL(2, CASE_ERR1);
      
  }
  
  private ResultQL RET()
  {
    String res="";
    if(tok.endOfText())
      return new ResultQL(2, TYPE_ERR1);
    String s = tok.lookCurrentWord();
    if(s.equals(RET_RETURN))
    {
//      prod.addToLine(RET_RETURN);
      res += (RET_RETURN);
      if(tok.incConsumer())
        return new ResultQL(2, RET_ERR1);
      else
      {
        ResultQL rend = END("");
        if(rend.failed())
        {
          prod.addToLine(res);
          ResultQL rexp = EXPR();
          return new ResultQL(rexp.getCode(), res + " " +rexp.getMessage());
        }
        else
          return rend;
      }
    }
    else
      return new ResultQL(2, S_ERR1);
  }
  
  private ResultQL JAVA()
  {
    String res="" + PARO();
    if(tok.endOfText())
      return new ResultQL(2, TYPE_ERR1);
    String s = tok.lookCurrentWord();
    if(s.equals("{*"))
    {
        if(tok.incConsumer())
            return new ResultQL(2, RET_ERR1);
        s = tok.lookCurrentWord(); 
        if(s.equals("{*"))
            return new ResultQL(2, RET_ERR1);
        else
        {
          do
          {
            res += s;
            if(!s.equals("."))
                res += " ";
            if(tok.incConsumer())
              return new ResultQL(2, BLOC_ERR1);
            s = tok.lookCurrentWord();
          }while(!s.equals("*}"));
            
            if(s.equals("*}"))
            {
                tok.incConsumer();
                return new ResultQL(1, res);
            }
            else
                return new ResultQL(2, RET_ERR1);
        }
    }
    else
        return new ResultQL(0, res);
  }
  
  private ResultQL END(String prodStr)
  {
    return END(prodStr, false);
  }
  
  private ResultQL END(String prodStr, boolean java_inj)
  {
    lastBridge = null;

    if(varType.size() > 0)
      stVar.push(varType);
   /* if(!typeChecking())
      return new ResultQL(2,END_ERR1);*/
    typeChecking();
    varType.clear();
    if(par_open>0)
      return new ResultQL(2,END_ERR2);
    else
      if(par_open<0)
        return new ResultQL(2,END_ERR3);  
    if(endNeed && !tok.lookCurrentWord().equals(";"))
      return new ResultQL(2, S_ERR1);
    else
    {
      if(endNeed)
      {
          prod.addToLine(prodStr);
          tok.incConsumer();
      }
      else
        if(java_inj)
        {
          prod.addToLine(prodStr);
          if(!tok.endOfText() && tok.lookCurrentWord().equals(";"))
          {
            tok.incConsumer();
            endNeed = true;
          }
        }  
        
      String retTransf = null;
      if(java_inj)
        retTransf = prod.transfEND(endNeed, java_inj);
      else
        retTransf = prod.transfEND(endNeed);
      if(endNeed)
        clearStruct();
      return new ResultQL(1, retTransf);
    }
  }
  
  private void clearStruct()
  {
    this.varType.clear();
    this.stVar.clear();
  }
  
  /**
   * Verifica se o actual token é considerado um número. Este pode tanto ser um inteiro como um real.
   * @return número reconhecida; <code> null </code> em caso de falha no reconhecimento.
   */
  private String isNumber()
  {
    long i = 0;
    String min = "";
    if(tok.lookCurrentWord().equals("-"))
        if(tok.incConsumer())//se a query terminar a seguir ao sinal menos retornar null para ser detectado o erro na classe de chamada
            return null;
        else
            min="-";            //memorizar o sinal -
    String s = tok.lookCurrentWord();
        i = ClassUtils.convertToLong( s , Long.MIN_VALUE ); //no limite pode dar problemas ...
        if ( i == Long.MIN_VALUE )
        {
            return null;
        }


    if(s.equals( Long.toString( i ) ))    //para além de ter sido convertido em Integer é necessário confirmar se após a conversão estamos a falar do mesmo número
        if(!tok.incConsumer() && tok.lookCurrentWord().equals("."))   //se a query não terminar e tivermos um ponto a seguir
        {
            if(tok.incConsumer())//avançar mais um token, se terminar devolver null
                return null;
            String s1 = tok.lookCurrentWord();
            i = ClassUtils.convertToLong( s1 , Long.MIN_VALUE ); //no limite pode dar problemas ...
            if ( i == Long.MIN_VALUE )
            {
                return null;
            }

            if(s1.equals( Long.toString( i ) ) )//voltar a confirmar se depois da trasnformação estamos a falar do mesmo número
            {
                tok.incConsumer();
                varType.add(TYPE_DOUBLE);
                return min+s+"."+s1;            //devovler a parte inteira e a parte decimal
            }
            else
                return null;
        }
        else    //se não for um ponto ou a query terminar podemos devolver o resultado
        {
            varType.add(TYPE_INT);
            return min+s;
        }
    else
        return null;
  }
  /**
   * Verifica se o actual token é considerado uma String.
   * @return String reconhecida; <code> null </code> em caso de falha no reconhecimento.
   */
  private String isString()
  {
    String res;
    String t;
    t = tok.lookCurrentWord();
  //  if(t.equals("\"") || t.equals("\'"))
    if(t.charAt(0) == '\"' && t.charAt(t.length()-1) == '\"')
    {
      res = t;
      tok.incConsumer();
      return res;
    }

    return null;
  }
  
  private String isChar()
  {
    String res;
    String t;
    t = tok.lookCurrentWord();
  //  if(t.equals("\"") || t.equals("\'"))
    if(t.length()>=3 && t.charAt(0) == '\'' && t.charAt(t.length()-1) == '\'')
    {
      res = t;
      tok.incConsumer();
      return res;
    }

    return null;
  }
  /**
   * Permite verificar se a String <code> W </code> corresponde a ao nome de um objecto XEO.
   * @param W nome do possível objecto XEO.
   * @return verdadeiro nome do tipo de objecto identificado. <code> null </code> caso não seja reconhecido como um objecto XEO
   */
  private String isObj(String W) 
  {
    if ( W.indexOf('.') > 0)
    {   
        String[] xw=W.split("\\.");
        W=xw[0];
        
    }
    boDefHandler boObj = boDefHandler.getBoDefinition(W);
    if (boObj==null) return null;
    else
    {
        String realObj = boObj.getName();
        return realObj;
    }
  }
  /**
   * Verifica se o token actual é considerado um nome de variável válido na sua estrutura.
   * @return <code> true </code> caso esteja construído segundo as normas; <code> false </code> no caso contrário.
   */
  private boolean isLegalVarname()
  {
    String s = tok.lookCurrentWord();
    String patterns = "[a-zA-Z_][a-zA-Z_0-9]*";
    Pattern p = Pattern.compile(patterns);
    Matcher m = p.matcher(s);
    return m.matches() && !s.equals(this.ASSG_NEW) && !s.equals(this.ATTR_BRDG) && !s.equals(this.BLOC_ELSE) && 
            !s.equals(this.BLOC_IF) && !s.equals(this.BLOC_WHIL) && !s.equals(this.LIT_FALSE) && !s.equals(this.LIT_NULL)
            && !s.equals(this.LIT_NUMB) && !s.equals(this.LIT_TRUE) && !s.equals(this.TYPE_DOUBLE) && !s.equals(this.TYPE_BOOL)
            && !s.equals(this.TYPE_INT) && !s.equals(this.TYPE_STR) && !s.equals(this.BLOC_FOR) && !s.equals(this.ESTR_BRK) 
            && !s.equals(this.ESTR_CONT) && !s.equals(this.BLOC_SWTH) && !s.equals(this.BLOC_CASE) && !s.equals(this.BLOC_DEF)
            && !s.equals(this.ASSG_OBJ) && !s.equals(this.BLOC_DO) && !s.equals(this.RET_RETURN) && !s.equals(this.TYPE_LIST)
            && !s.equals(this.TYPE_VOID);    
            
          
  }
  
  private String typeUnfold(String type)
  {
      if(type.equalsIgnoreCase(xwfECMAparser.LIT_NUMB))
      {
          if(bo_att != null && bo_att.getDecimals() > 0)
            return xwfECMAparser.TYPE_DOUBLE;
          else
            return xwfECMAparser.TYPE_LONG;
      }
      else
        return type;
  }
  /**
   * Permite verificar se um atributo pertence a um objecto devolvendo o seu tipo.
   * @param obj objecto a que o atributo pertence
   * @param att atributo a identificar
   * @return tipo do atributo consultado; <code> null </code> caso o atributo não pertença ao objecto.
   */
  private String isAttribute(String obj, String att)
  {
    boolean listObj = false;
    if(obj.startsWith("List."))
    {
      listObj = true;
      obj = obj.substring(5);
    }
    boDefHandler bo_obj = boDefHandler.getBoDefinition(obj);
    bo_att=null;
    String res;
    if(bo_obj!=null)    //se o obejcto existir
    {
        bo_att = bo_obj.getAttributeRef(att);
        if(bo_att!=null) //e o atributo for reconhecido 
        {
          String realname = bo_att.getName();
          if(!realname.equals(att))
            return null;
          if(bo_att.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) //se ele for do tipo attribute
          {
            boolean bexists = lastBridge != null;
            if((lastBridge = bo_att.getBridge()) != null)//se houver mais chamadas e ele for uma bridge
            {
              prod.setBridge(true);//marcamos que nos encontramos dentro da bridge
              if(!tok.lookahead(1).equals("."))
                if(bexists)
                  prod.setOnBridge(true, true);
                else
                  prod.setOnBridge(true, false);
              else
                if(listObj)
                  prod.setOnBridge(true, true);  
            }
            else
              if(listObj)
                prod.setOnBridge(true, true);
            return bo_att.getType().substring(7);
          }
          else//se o attribtuto existir e for de um tipo base
          {
//            prod.setBridge(false);
            lastBridge = null;  //limpamos a memória da última bridge
            if(listObj)
              prod.setOnBridge(true, true);
            if(bo_obj.getBoClsState()!=null && bo_obj.getBoClsState().getChildStateAttributes(att)!=null)
                 return "char(20)";
            else
                return typeUnfold(bo_att.getType());
               
          }
        }
        else//se o atributo não existe
          if(lastBridge != null)//e estamos numa bridge
          {
            bo_att = lastBridge.getAttributeRef(att);//vamos tentar achar o attributo da bridge
            if(bo_att!=null)//se existir
              if(bo_att.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)//e for do tipo objecto
              {
//                if((lastBridge = bo_att.getBridge()) != null)
//                  prod.setBridge(true);
                  prod.setOnBridge(true, false);
                  
                return bo_att.getType().substring(7);
              }
              else//se for do tipo base
              {
                prod.setOnBridge(true, false);//marcamos que nos encontramos dentro da bridge
                lastBridge = null;//limpamos a memória da última bridge
                return typeUnfold(bo_att.getType());
              }
            else
              if(att.equals(ATTR_BRDG)  && tok.lookahead(-1).equals(".")) 
              {   //caso de expressar claramente que queremos o atributo da bridge
                  if(tok.incConsumer())
                      return null;
                  String s2 = tok.lookCurrentWord();
                  if(s2.equalsIgnoreCase("."))
                  {
                      tok.incConsumer();
                      prod.setBridge(true);
 /*                     ResultQL r = ATTR(obj);   //novo atributo
                      if (r.failed())
                          return null;
                      else
                          return r.getMessage();*/
                      return isAttribute(obj, tok.lookCurrentWord());
                  }
                  else
                  {
      
                      return null;//"You must define a bridge atribute (bridge_name.bridge.bridge_attribute)"
                  }
              }
              else
                return null;
          }
          else
            return null;
    }
    else
        return null;
  }
  /**
   * Verifica se o método pertence ao objecto. Permite também verificar se é algum outro método conhecido.
   * @param obj   objecto que invoca o método
   * @param meth  nome do método a ser invocado
   * @return array de Strings que constituem a signature do método invocado; <code>null</code> no caso de o método não ser reconhecido
   */
  private boolean isMethod(String obj, String meth)
  {
    boDefHandler bo_obj = boDefHandler.getBoDefinition(obj);
//    boDefAttribute bo_att=null;
    String res;
    lastMeth = null;
    
      
      if(bo_obj != null)//se existir o objecto que nos foi passado
      {
        lastMeth = bo_obj.getBoMethod(meth);//tentamos achar o método
        if(lastMeth != null)//se exitir memorizamos o método e passamos true
          return true;
      }
      if(lastBridge != null)//se não for reconhecido e estivermos numa bridge
      {
        
        boolean brdgM = (prod.isMethod(this.ATTR_BRDG, meth));//vamos ao prod ver se é um método base da bridge
        if(brdgM)//se for 
        {
          prod.setBridge(true); //reforçamos a ideia que estamos numa bridge
          return true;
        }
  /*      else
        {
          prod.setBridge(false);//reforçamos que não estamos numa bridge
          prod.setOnBridge(true, false); //e que estamos num objecto da bridge
        }*/
        
      }
      
      
        if(bo_att!=null && (obj.equals(bo_att.getType()) || bo_att.getType().equals("object."+obj)))
        {
          boDefMethod[] meths = bo_att.getMethods();
          if(meths != null)
          {
            for(int i=0; i < meths.length; i++)
              if(meths[i].getName().equals(meth))
              {
                lastMeth = meths[i];
                break;
              }
            if(lastMeth != null)
              return true;
          }
        }

        if(meth.equals(ATTR_BRDG)  && tok.lookahead(-1).equals(".")) 
        {   //caso de expressar claramente que queremos o atributo da bridge
            if(tok.incConsumer())
                return false;
            String s2 = tok.lookCurrentWord();
            if(s2.equalsIgnoreCase("."))
            {
                tok.incConsumer();
                prod.setBridge(true);
  /*                     ResultQL r = ATTR(obj);   //novo atributo
                if (r.failed())
                    return null;
                else
                    return r.getMessage();*/
                return isMethod(obj, tok.lookCurrentWord());
            }
            else
            {
                return false;//"You must define a bridge atribute (bridge_name.bridge.bridge_attribute)"
            }
        }
        
        
          if(!obj.equals(TYPE_VOID) &&  typeCode(obj) == 4)
          {
            boolean retm = prod.isMethod(this.ASSG_OBJ, meth);
            if(retm && lastBridge!=null)
            {
              prod.setOnBridge(true, true);
            }
            return retm;
          }
          else
            if(typeCode(obj) == 5)
              return prod.isMethod(this.TYPE_LIST, meth);
            else
                if(typeCode(obj) == 2)
                  return prod.isMethod(this.TYPE_STR, meth);
                else
                  return prod.isMethod(obj, meth);
                  
      
    
  }
  /**
   * Procede à verificação da consistência de tipos numa expressão representada por um vector.
   * @param vect Vector com os tipos que compoem a expressão
   * @return código do tipo base da expressão analizada; caso a consistencia de tipos não seja verificada é devolvido <code>-1</code>
   */
  static protected int typeChecking(Vector vect, boolean rigid)
  {
    int max_code = 0;
    int cc=0;
    if(vect.size() == 0)
      return 4;
    if(vect.size() <= 1)
      return typeCode((String)vect.get(0));
    else
    {
      int code = typeCode((String)vect.get(0));
      max_code = code;
      if(code == 2)
        return 2;
      for(int i=1; i < vect.size(); i++)
      {
        if(code <= 1)
        {
          cc = typeCode((String)vect.get(i));
          if(rigid)
          {
            if(cc > code)
              return -1;
          }
          else              
            if(cc >1)
              return -1;
              
          if(max_code < cc)
            max_code = cc;
        }
        else
          if(code != typeCode((String)vect.get(i)))
            return -1;
          else
            if(max_code < cc)
              max_code = cc;
      }
      return max_code;
    }
  }
  /**
   * Verificação final da consistencia de tipos já traduzidos para códigos.
   * @param vect    Vector com os códigos produzidos por anteriores análises de sub-expressoes.
   * @param length  tamanha do vector <code>vect</code.
   * @return <code>true</code> se a consistencia for verificada; <code>false</code> caso contrário.
   */
  private boolean lastTypeChecking(int vect[], int length)
  {
    
    if(length <= 1)
      return true;
    else
    {
      int code = vect[length-1];
      for(int i=length-2; i >= 0 ; i--)
      {
        if(code <= 1)
        {
          if(code < vect[i])
            return false;
        }
        else
          if(code != vect[i])
            return false;
      }
      return true;
    }
  }
  /**
   * Verificação principal da consistencia de tipos de uma expressão que ocorre no final de cada sentença. 
   * Será processada a pilha de expressoes que con´em vectore que serão analisados e codificados.
   * @return <code>true</code> se a consistencia for verificada; <code>false</code> caso contrário.
   */
  private boolean typeChecking()
  {
   
   
    Vector v, newv;
    int pos=0;
    int vcode[] = new int[20];
    int code;
    if(stVar.size() < 1)
      return true;
    else
    {
      v = (Vector)stVar.pop();
      code = typeChecking(v, false);
      vcode[pos] = code;
      pos++;
      if(code < 0)
        return false;
      while(!stVar.empty())
      {
        v = (Vector)stVar.pop();
        if(v.size() > 0)
        {
          code = typeChecking(v, false);
          if(code > -1)
          {
            vcode[pos] = code;
            pos++;
          }
          else
            return false;
        }
      }
      
      return lastTypeChecking(vcode, pos);
    }
  }
  /**
   * Codifica um tipo de dados no seu código correspondente.
   * @param t tipo de dados a codificar
   * @return código correspondente ao tipo de dados <code>t</code>
   */
  static protected int typeCode(String t)
  {
    if(t.equals(TYPE_INT) || t.equals(TYPE_LONG))
      return 0;
    else    
      if(t.equalsIgnoreCase(LIT_NUMB) || t.equals(TYPE_DOUBLE))
        return 1;
      else
        if(t.equals(TYPE_STR) || (t.length() > 4 && t.substring(0,4).equals(TYPE_CHAR)) || t.equals(TYPE_DATE) || t.equalsIgnoreCase(TYPE_DATETIME) || t.equalsIgnoreCase(TYPE_CLOB))
          return 2;
        else
          if(t.equals(TYPE_BOOL))
            return 2;
          else
            if(t.equals(LIT_CHAR))
              return 3;
            else
              if(t.startsWith(TYPE_LIST))
                return 5;
              else
                return 4;
  }
  
  private String needBoui(String inst)
  {
    String lt = getLastType(0);
    String blt = getLastType(1);
    if(inst.equals("ASSG"))
      if(xwfECMAparser.typeCode(lt) ==4 && xwfECMAparser.typeCode(blt) == 4)
        return prod.getObjT();
      else
        return "";
    else
      return "";
  }
  
  private String getLastType(int pos)
  {
    if(varType.size() > pos)
      return (String)varType.lastElement();
    else
      for(;;)
        if(stVar.size() > 0)
        {
          Vector v = (Vector)stVar.peek();
          if(v.size() > 0)
            return (String)v.lastElement();
          else
            stVar.pop();
        }
        else
          return null;
  }
  
  private boolean upperMethCall(int pos)
  {
    if(pos>=mcalls.size())
      return false;
    else
      return ((Boolean)mcalls.get(pos)).booleanValue();
  }
  
  public void setVariable(String name, String type)
  {
    variables.put(name, type);
  }
  
  public Object[] getImports()
  {
      return dec_imports.toArray();
  }
  
  
  private void test()
  {
//    String str = "work" ; boObject d = boObject.getBoManager().loadObject(boctx, 2217226 ) ; d bridgeSum1011448520(d.getBridge("student")) ; bridgeSum1011448520(d.getBridge("student")) ; 
  
  }
  
  
}