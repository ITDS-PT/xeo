/*Enconding=UTF-8*/
package netgest.xwf.core;

import bsh.BshClassManager;
import bsh.EvalError;
import bsh.NameSpace;
import netgest.bo.utils.*;
import netgest.xwf.common.*;
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
import java.text.DateFormat;
import netgest.bo.dochtml.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Classe responsável pela avaliação de código XEP.
 * Tem como principal função <code>eval</code> que retornará o objecto que resulta da avalização do código introduzido.
 */
public class xwfECMAevaluator 
{

  private xwfECMAparser parser;
//  private bsh.NameSpace nsp = bsh.NameSpace.JAVACODE;
  private NameSpace nsp = new NameSpace(new BshClassManager(),"xwfEval");
  private boolean unchanged = true;
  private Vector obj_boui;
  private bsh.Interpreter bshi;
  /**
   * Construtor por defeito.
   */
  public xwfECMAevaluator()
  {  
    parser = new xwfECMAparser();
    
    nsp.importPackage("netgest.bo");
    nsp.importPackage("netgest.bo.def");
    nsp.importPackage("netgest.utils");
    nsp.importPackage("netgest.bo.runtime");
    nsp.importPackage("netgest.bo.utils");
  }
  
  
  
  /**
   * Construtor que inicializa o evaluador partindo de um objecto Programa e de um contexto.
   * <br>Pega nas variáveis do programa e inicializa o evaluador com elas.
   * 
   * @param xm    boManager do programa
   * @param env   nó que representa o contexto de execução da tarefa
   * @throws java.lang.Exception  Excepções ao nível da avalização
   */
  public xwfECMAevaluator(xwfBoManager xm, Node env) throws boRuntimeException
  {
    parser = new xwfECMAparser();
//    xwfBoManager xm = new xwfBoManager(doc, prog);
    obj_boui = new Vector();
    nsp.importPackage("netgest.bo");
    nsp.importPackage("netgest.bo.def");
    nsp.importPackage("netgest.utils");
    nsp.importPackage("netgest.bo.runtime");
    nsp.importPackage("netgest.bo.utils");
 
    /*bridgeHandler b = xm.getProgram().getBridge("variables");
    b.beforeFirst();
    while(b.next())*/
    long[] vars_boui = xwfHelper.getContextVarsAndPars(new ngtXMLHandler(env));
    for(int o = 0; o < vars_boui.length; o++)
    {
      if(vars_boui[o] < 1)
        continue;
      boObject var = xm.getObject(vars_boui[o]);
      boObject varVal = var.getAttribute("value").getObject();
//      varVal = xm.getObject(varVal.getBoui());
      String name = var.getAttribute("name").getValueString();
      String stype = varVal.getAttribute("type").getValueString();
      String slink = varVal.getAttribute("linkVar").getValueString();
      if(slink != null && slink.length() > 0)
        continue;
      int type = new Double(stype).intValue();
      varChange(xm, varVal, name);
    }
    nsp.nameSpaceChanged();
  }
  
   public xwfECMAevaluator(xwfBoManager xm, Vector names, Vector bouis) throws boRuntimeException
  {
    parser = new xwfECMAparser();
//    xwfBoManager xm = new xwfBoManager(doc, prog);
    
    nsp.importPackage("netgest.bo");
    nsp.importPackage("netgest.bo.def");
    nsp.importPackage("netgest.utils");
    nsp.importPackage("netgest.bo.runtime");
    nsp.importPackage("netgest.bo.utils");
    
  }
  /**
     * Adiciona um package ao ambiente de avaliação 
     * @param packag    caminho (nome) do package a adicionar
     */
  public void addImportPackage(String packag)
  {
      nsp.importPackage(packag);
  }
  /**
     * Adiciona uma classe ao ambiente de avaliação 
     * @param clas  caminho (nome) da classe a adicionar
     */
  public void addImportClass(String clas)
  {
      nsp.importClass(clas);
  }
  
  /**
   * Principal rotina que irá fazer a avalização do código fornecido
   * 
   * @param xm      boManager do programa
   * @param code    código a ser avaliado
   * @return        objecto resultante da avaliação do código
   * @throws java.lang.Exception  Excepções ao nível da avalização
   */
  public Object eval(xwfBoManager xm, String code) throws boRuntimeException
  {
      String java_code="";
      if(code != null)
        java_code = parser.toJava(code);
      
      if(code == null || !parser.success())
      {
        throw new boRuntimeException("xwfECMAevaluator", "eval", new Exception(java_code));
      }
      
//      xwfBoManager xm = new xwfBoManager(doc, null);
      try{
        setImports();
        nsp.setTypedVariable("xm", Class.forName("netgest.xwf.common.xwfBoManager"), xm, null ) ;
      }catch(Exception e){throw new boRuntimeException("xwfECMAevaluator", "eval", e);}
      nsp.nameSpaceChanged();
      
      bshi = new bsh.Interpreter();
      bshi.setNameSpace(nsp);
      try{
        Object xo = bshi.eval(java_code);  
        unchanged = false;
        return xo;
      }catch(Exception e){throw new boRuntimeException("xwfECMAevaluator", "eval", e);}
  }
  
  
  
  /**
   * Função que permite actualizar as variáveis do programa após a execução de código que pode ter executado alterações nas variáveis do programa
   * 
   * @param xm          boManager do programa
   * @param env         nó de contexto da execução
   * @param input_type  tipo de set a ser feito nas variaveis 
   * @throws java.lang.Exception Excepções ao nível da avalização
   */
  public void getVariables(xwfBoManager xm, Node env, byte input_type) throws boRuntimeException
  {
    this.obj_boui = new Vector();
    long[] vars_boui = xwfHelper.getContextVarsAndPars(new ngtXMLHandler(env));
    for(int o = 0; o < vars_boui.length; o++)
    {
      if(vars_boui[o] == 0)
        continue;
        
      boObject var = xm.getObject(vars_boui[o]);
      boObject varVal = var.getAttribute("value").getObject();
      String name = var.getAttribute("name").getValueString();
      String stype = varVal.getAttribute("type").getValueString();
      String slink = varVal.getAttribute("linkVar").getValueString();
      String sstatic = var.getAttribute("staticKey").getValueString();
      if((slink != null && slink.length() > 0) || (sstatic != null && sstatic.length() > 0) || !checkUpdateNeed(varVal, name))
        continue;
      int type = new Double(stype).intValue();
//      varChange(xm, varVal, name);
      try{
        switch(type)
        {
          case boDefAttribute.VALUE_BOOLEAN:
          {
            Primitive pval = (Primitive)nsp.getVariable(name);
            xm.setVarValueObject(varVal, pval.getValue(), input_type);
            break;
          }
          case boDefAttribute.VALUE_CHAR:
          {
            xm.setVarValueObject(varVal, nsp.getVariable(name), input_type);
            break;
          }
          case boDefAttribute.VALUE_CLOB:
          {
            xm.setVarValueObject(varVal, nsp.getVariable(name), input_type);
            break;
          }
          case boDefAttribute.VALUE_NUMBER:
          {
            Primitive pval = (Primitive)nsp.getVariable(name);
            xm.setVarValueObject(varVal, pval.getValue(), input_type);
            break;
          }
          case boDefAttribute.VALUE_DATE:
          {
            try{
              Date val= (Date)nsp.getVariable(name);
              xm.setVarValueObject(varVal, val, input_type);
            }catch(Exception e)
            {
              xm.setVarValueObject(varVal, null, input_type);
            }
            break;
          }
          case boDefAttribute.VALUE_DATETIME:
          {
            try{
              Date val= (Date)nsp.getVariable(name);
              xm.setVarValueObject(varVal, val, input_type);
            }catch(Exception e)
            {
              xm.setVarValueObject(varVal, null, input_type);
            }
            break;
          }
          case boDefAttribute.VALUE_UNKNOWN:
          {
            String card = varVal.getAttribute("maxoccurs").getValueString();
            if("1".equals(card))
            {           
                Object ovalue = nsp.getVariable(name);
                if(ovalue instanceof boObject)
                {
                  xm.setVarValueObject(varVal, ovalue, input_type);
                  this.obj_boui.add(new Triple(name, new Long(((boObject)ovalue).getBoui()), null)); 
                }
                else
                  xm.setVarValueObject(varVal, null, input_type);
                break;
            }
            else
            {
                Object ovalue = nsp.getVariable(name);
                if(ovalue instanceof boObjectList)
                  xm.setVarValueObject(varVal, ovalue, input_type);
                else
                  xm.setVarValueObject(varVal, null, input_type);
                break;
            }
          }
        }
      }catch(Exception e){throw new boRuntimeException("xwfECMAevaluator", "getVariables", e);}
    }
    unchanged = true;
  }
  
  /**
   * Rotina que permite a alteração de uma variável no contexto do avaliador
   * @param name  nome da variável
   * @param ctype tipo da variável
   * @param val   valor a atribuir a variável
   * @throws java.lang.Exception  Excepção ao nível da definição da variável
   */
  public void varChange(String name, Class ctype, Object val) throws boRuntimeException
  {
    try{
      if(ctype == null)
        nsp.setVariable(name, val, false);
      else
        nsp.setTypedVariable( name, ctype, val, null);
    }catch(Exception e){throw new boRuntimeException("xwfECMAevaluator", "varChange", e);}
    nsp.nameSpaceChanged();
  }
  /**
     * Rotina que permite a alteração de uma variável no contexto do avaliador
     * @throws netgest.bo.runtime.boRuntimeException
     * @param name      nome da variável
     * @param var_val   
     * @param xm
     */
  public void varChange(xwfBoManager xm, boObject var_val, String name) throws boRuntimeException
  {
    
      String stype = var_val.getAttribute("type").getValueString();
      int type = new Double(stype).intValue();
      try{
        switch(type)
        {
          case boDefAttribute.VALUE_BOOLEAN:
          {
            parser.setVariable(name, parser.TYPE_BOOL);
//            String sval = varVal.getAttribute("valueBoolean").getValueString();
            String sval = xm.getValueString(var_val);
            Primitive vp=null;
            if(sval.equals("1"))
              vp = new Primitive(true);
            else
              vp = new Primitive(false);
            nsp.setVariable(name, vp, false);
            break;
          }
          case boDefAttribute.VALUE_CHAR:
          {
            parser.setVariable(name, parser.TYPE_STR);
//            String val = varVal.getAttribute("valueText").getValueString();
            String val = xm.getValueString(var_val);
            nsp.setTypedVariable(name, Class.forName("java.lang.String"), val, null ) ;
            break;
          }
          case boDefAttribute.VALUE_CLOB:
          {
            parser.setVariable(name, parser.TYPE_STR);
//            String val = varVal.getAttribute("valueText").getValueString();
            String val = xm.getValueString(var_val);
            nsp.setTypedVariable(name, Class.forName("java.lang.String"), val, null ) ;
            break;
          }
          case boDefAttribute.VALUE_NUMBER:
          {
            parser.setVariable(name, parser.TYPE_DOUBLE);
//            double val = varVal.getAttribute("valueNumber").getValueDouble();
            double val = xm.getValueDouble(var_val);
            Primitive vp = new Primitive(val);
            nsp.setVariable(name, vp, false);
            break;
          }
          case boDefAttribute.VALUE_DATE:
          {
            parser.setVariable(name, parser.TYPE_DATE);
//            Date val = varVal.getAttribute("valueDate").getValueDate();
            Date val = xm.getValueDate(var_val);
            nsp.setTypedVariable(name, Class.forName("java.util.Date"), val, null );
            break;
          }
          case boDefAttribute.VALUE_DATETIME:
          {
            parser.setVariable(name, parser.TYPE_DATE);
//            Date val = varVal.getAttribute("valueDateTime").getValueDate();
            Date val = xm.getValueDate(var_val);
            nsp.setTypedVariable(name, Class.forName("java.util.Date"), val, null );
            break;
          }
          case boDefAttribute.VALUE_UNKNOWN:
          {
            String card = var_val.getAttribute("maxoccurs").getValueString();
            if("1".equals(card))
            {
//              boObject obVal = varVal.getAttribute("valueObject").getObject();
              boObject obVal = (boObject)xm.getValueBoObject(var_val);
              String otype = null;
              if(obVal == null)
              {
                boObject bcls = var_val.getAttribute("object").getObject();
                otype = bcls.getAttribute("name").getValueString();
              }
              else
              {
                otype = obVal.getName();
                
              }
              parser.setVariable(name, otype);
              nsp.setTypedVariable(name, Class.forName("netgest.bo.runtime.boObject"), obVal, null );
              
            }
            else
            {
//              long[] list_bouis = varVal.getAttribute("valueList").getValuesLong();
              long[] list_bouis = var_val.getAttribute("valueList").getValuesLong();
              bridgeHandler blist = var_val.getBridge("valueList");
              String otype = null;
              
              boObject bcls = var_val.getAttribute("object").getObject();
              String clsname =  bcls.getAttribute("name").getValueString();
              otype = "List."+ clsname;
              parser.setVariable(name, otype);
              boObjectList bol = xm.listObject(list_bouis, clsname);

              nsp.setTypedVariable(name, Class.forName("netgest.bo.runtime.boObjectList"), bol, null );
            }
            
            break;
          }
        }
      }catch(Exception e){throw new boRuntimeException("xwfECMAevaluator", "varChange", e);}
  }
  
  public boolean isUnchanged()
  {
    return true;
  }
  
  public void setUnchanged(boolean val)
  {
    unchanged = val;
  }
  
  /**
   * Rotina que permite a alteração de uma variável no contexto do avaliador
   * @param name  nome da variável
   * @param ctype tipo da variável
   * @param val   valor da variável
   * @throws java.lang.Exception  Excepção ao nível da definição da variável
   */
  public void setVariable(String name, Class ctype, Object val, String type) throws boRuntimeException
  {
    varChange(name, ctype, val);
    this.parser.setVariable(name, type);
  }
  
  public Object getVariable( String name )
  {
      try
      {
          return bshi.get( name );
      }
      catch (EvalError e)
      {
          throw new RuntimeException(e);
      }
  }
  
  public void refreshObjects(xwfBoManager xm)throws boRuntimeException
  {
      if(obj_boui != null)
      {    
          int i = 0;
          for(i=0; i < obj_boui.size(); i++)
          {
            Triple t = (Triple)obj_boui.get(i);
            String sname = (String)t.getFirst();
            Long lboui = (Long)t.getSecond();
              try{
                nsp.setTypedVariable(sname, Class.forName("netgest.bo.runtime.boObject"), xm.getObject(lboui.longValue()), null ) ;
              }catch(Exception e){throw new boRuntimeException("xwfECMAevaluator", "eval", e);}
          }
          if(i > 0)
            nsp.nameSpaceChanged();
      }
  }
  
  private void setImports()
  {
      Object[] imps = parser.getImports();
      for(int i=0; i < imps.length; i++)
      {
        String actual = (String)imps[i];
          if(actual.indexOf('*') >= 0)
          {
              addImportPackage(actual.substring(0, actual.length()-2));
          }
          else
            addImportClass(actual);
      }
  }
  
  private boolean checkUpdateNeed(boObject varvalue, String name) 
  {
      boolean ret=false;
      try
      {
          if(varvalue.isChanged())
            ret = true;
          else
         {
            String stype = varvalue.getAttribute("type").getValueString();
            int type = new Double(stype).intValue();
            if(boDefAttribute.VALUE_UNKNOWN == type)
            {
                if(varvalue.getAttribute("valueObject").getObject() == null)
                {
                    if(nsp.getVariable(name) != null)
                        ret = true;
                }
                else if (nsp.getVariable(name) == null)
                {
                    ret = true;
                }
                else
                {
                    Object o1 = nsp.getVariable(name);
                    Object o2 = varvalue.getAttribute("valueObject").getObject();
                    if(!Calculate.compare(o1, o2, Calculate.EQUAL))
                    {
                        ret = true;
                    }
                }
                
            }
         }
      }
      catch (Exception e)
      {
          return ret;
      }
      return ret;
  }
}
