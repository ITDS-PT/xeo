/*Enconding=UTF-8*/
package netgest.xwf.core;

import bsh.BshClassManager;
import bsh.NameSpace;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import netgest.bo.def.boDefAttribute;
import netgest.bo.parser.CodeJavaConstructor;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.utils.ngtXMLHandler;

import netgest.xwf.common.xwfBoManager;
import netgest.xwf.common.xwfHelper;

import oracle.xml.parser.v2.XMLElement;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Classe responsavel pela gestão dos cálclulos as formulas das variaveis de um programa
 * @author Ricardo Andrade
 */
public class xwfFormulaCalc 
{
/**
 * motor do fluxo de trabalho que guarda a definição XML do programa
 */
  private xwfControlFlow cf;
  /**
   * boManager do programa
   */
  private xwfBoManager xwfm;
  /**
   * patern de reconhecimento de tokens nas formulas
   */
  private String patterns = "\\|\\||&&|\\+=|-=|<=|>=|==|!=|<>|\\+\\+|--|'(\\\\)?.'|§(.*)§|(\"(?:[^\\\\\"]|\\\\.)*\")|[\\p{Punct}\\s}]|[\\w]*";
  /**
   * variaveis reconhecidas nas formulas
   */
  private Hashtable vars = new Hashtable();
  /**
   * lista de todos os nós de definição de variáveis e participantes
   */
  private NodeList nl=null;
  /**
     * Construtor por defeito
     * @param manage    bomanager do programa
     * @param control   motor do workflow que contéma definição XML do programa
     */
  public xwfFormulaCalc(xwfControlFlow control, xwfBoManager manage)
  {
    cf = control;
    xwfm = manage;
    
  }
  /**
   * Inicia o pré-processamento necessário no arranque de um program que irá definir as ligações das fórmulas nas variáveis.
   * <p>Preenche a estrutura que guarda a relação entre variaveis
   */
  protected void startPreCalc()
  {
    try{
      nl = cf.getActualXml().getDocument().selectNodes("//defVariable[@name] | //defParticipant[@name]");
    }catch(Exception e){}
    
    for(int i = 0; i < nl.getLength(); i++)
    {
      String name = new ngtXMLHandler(nl.item(i)).getAttribute("name", null);
      vars.put(name, new Vector());
    }
    for(int i = 0; i < nl.getLength(); i++)
    {
      String name = new ngtXMLHandler(nl.item(i)).getAttribute("name", null);
      String formula = new ngtXMLHandler(nl.item(i)).getChildNodeText("formula", "");
      varTree(name, formula);
    }
  }
  
  /**
   * Finaliza o pré-processamento necessário no arranque de um program que irá definir as ligações das fórmulas nas variáveis.
   * <p>Passa para o XML os dados armazenados na estrutura
   */
  protected void finalizePreCalc()
  {
    for(int i = 0; i < nl.getLength(); i++)
    {
      String name = new ngtXMLHandler(nl.item(i)).getAttribute("name", null);
      Vector vvar = (Vector)vars.get(name);
      orderVect(vvar);
      xmlFromVector(vvar, nl.item(i));
    }
  }
  /**
     * Ordena o vector de relações de variáveis para uma das variávies referenciada
     * @param v     vector de relações de variavel referenciadas
     */
  protected void orderVect(Vector v)
  {
    for(int i=0; i < v.size(); i++)
    {
      Object oi = v.get(i);
      int size_oi = ((Vector)vars.get(oi)).size();
      for(int j=0; j < i; j++)
      {
        Object oj = v.get(j);
        int size_oj = ((Vector)vars.get(oj)).size();
        if(size_oi < size_oj)
        {
          switchPlaces(v, i, j);
        }
      }
    }
  }

    /**
     * Procede a troca de duas posição de um vector 
     * @param v         vector de relações de variavel referenciadas
     * @param pos_a     primeira posição da troca
     * @param pos_b     segunda posição da troca 
     */
  private static void switchPlaces(Vector v, int pos_a, int pos_b)
  {
    Object temp = v.get(pos_a);
    v.setElementAt(v.get(pos_b), pos_a);
    v.setElementAt(temp, pos_b);
  }
  /**
     * Adiciona uma referencia a estrutura que armazena as relações das fórmulas
     * @param var_caller    variável que possui uma fórmula
     * @param formula       fórmula da variavel
     */
  protected void varTree(String var_caller, String formula)
  {
    Pattern p = Pattern.compile(patterns);
    Matcher m = p.matcher(formula);
    String t;
//        int curr_pos = 0;    
    
    while (m.find())
    {
      t = m.group();
      Vector vvar = (Vector)vars.get(t);
      if(vvar != null)
      {
        if(!vvar.contains(var_caller))
          vvar.add(var_caller);
      }
    }
//    vars.put(var_caller, ret);
  }
  
  /**
     * Procede a passagem das relações guardadas na estrutura de dados para a representação XML
     * @param v       relações da variavel
     * @param n       nó XML representativo da variavel em questão
     */
  private void xmlFromVector(Vector v, Node n)
  {
    Element vd_elem = cf.getActualXml().getDocument().createElement("VAR_DEPEND");
    for(int i = 0; i < v.size(); i++)
    {
      String vname = (String)v.get(i);
      Element var_elem = cf.getActualXml().getDocument().createElement(vname);
      ((XMLElement)vd_elem).appendChild(var_elem);  
    }
    ((XMLElement)n).appendChild(vd_elem);
  }
    /**
     * Percorre uma bridge de variáveis ou participantes e aplica as fórmulas que sejam necessárias
     * @param bvar    bridge de variáveis
     */
  public void applyFormulas(bridgeHandler bvar)throws boRuntimeException
  {
//    bridgeHandler bvar = xwfm.getProgram().getBridge("variables");
    boObject variable = null;
    String sstatic = null;
    boObject object = null;
    String formula = null;
    bvar.beforeFirst();
    while(bvar.next())
    {
      variable = bvar.getObject();
      if(variable != null)
      {
          sstatic = variable.getAttribute("staticKey").getValueString();
          if(sstatic == null || sstatic.length() <= 0)
          {
            formula = variable.getAttribute("formula").getValueString();
            if(formula != null && formula.startsWith("CODE_JAVA(") && formula.endsWith(")"))
            {
                setFormula(variable,null);
            }
            else
            {
                object = variable.getAttribute("value").getObject();
                if(object != null)
                {
                    afterSetApplyFormula(object);
                }   
            }
          }
      }
    }
  }
    /**
     * Para cada uma das variáveis de uma bridge irá tentar aplicar as fórmulas caso existam 
     * @param varValue  objecto do tipo xwfVarValue que contém o valor da variável em si mesmo
     */
    public void afterSetApplyFormula(boObject varValue)throws boRuntimeException
    {
        if(varValue != null)
        {
            String name = null; 
            long boui = -1;
            if(xwfm.getValueObject(varValue) != null)
            {
                String usid = varValue.getAttribute("unique_sid").getValueString();
                if(usid == null || usid.length() < 1 || usid.equals("-1"))
                  return;
                  
                ngtXMLHandler n = cf.getNode(usid);
                if(n != null && n.getNode()!=null)
                {
                    ngtXMLHandler var_dep = n.getChildNode("VAR_DEPEND");
                    if(var_dep == null)
                        return;
                    
                    ngtXMLHandler[] var_names = var_dep.getChildNodes();
                    for(int i=0; i < var_names.length; i++)
                    {
                        name = var_names[i].getNodeName();
                        boui = xwfHelper.getContextVar(var_names[i], name);
                        if(boui < 1)
                        {
                            boui = xwfHelper.getContextPar(var_names[i], name);
                        }
                        if(boui > 1)
                        {
                            setFormula(xwfm.getObject(boui), var_names[i]);
                        }
                    }
                }
            }
        }
    }
  /**
     * Procede ao calculo da formula do label do programa
     * @param doc  estrutura XML do programa
     */
  public void setProgLabelFormula(ngtXMLHandler doc)throws boRuntimeException
  {
    String att_name = "labelFormula";
    boObject prog = xwfm.getProgram();
    String f = prog.getAttribute(att_name).getValueString();
    boolean applyable = (prog.getAttribute("label").getInputType() != AttributeHandler.INPUT_FROM_USER) ||
    (prog.getAttribute("label").getInputType() == AttributeHandler.INPUT_FROM_UNITIALIZED);
    try{
      if(applyable && f != null && f.length() > 0)
      {
        f +=";";
        xwfECMAevaluator e = cf.getXwfEval(doc);
        if(!e.isUnchanged())
          e = new xwfECMAevaluator(xwfm, doc.getNode());
        Object oeval = e.eval(xwfm, f);
        String seval = (String)oeval;
        prog.getAttribute("label").setValueString(seval, AttributeHandler.INPUT_FROM_INTERNAL);
      }
    }catch(Exception e){}
  }
  
  /**
     * Aplica directamente a fórmula que atribui o valor a variável
     * @param var  objecto do tipo xwfVariable/xwfParticipant que contém a definição e valor
     * @param doc  estrutura XML do programa
     */  
    private void setFormula(boObject var, ngtXMLHandler doc)throws boRuntimeException
    {
        xwfECMAevaluator evaluator = null;
        Object objectEvaluated = null;
        String att_name = "formula";
        
        if(var.getName().equals("xwfProgramRuntime"))
        {
            att_name = "labelFormula";
        }
        
        String formula = var.getAttribute(att_name).getValueString();
    
        if(formula != null && formula.length() > 0 /*&& !var.isChanged()*/)
        {
//            String n = var.getAttribute("name").getValueString(); //debugger?
            boObject varValue = var.getAttribute("value").getObject();
            int tc = Integer.parseInt(varValue.getAttribute("type").getValueString());
            boolean applyable = (xwfm.getAttributeInContentManager(varValue).getInputType() != AttributeHandler.INPUT_FROM_USER) ||
            (xwfm.getAttributeInContentManager(varValue).getInputType() == AttributeHandler.INPUT_FROM_UNITIALIZED) ||
            (xwfm.getValueObject(varValue) == null);
            
            if(applyable)
            {
                try
                {
                    if(formula.startsWith("CODE_JAVA(") && formula.endsWith(")"))
                    {
                        objectEvaluated = executeJavaCode(formula);
                    }
                    else
                    {
                        evaluator = cf.getXwfEval(doc);
                        if(!evaluator.isUnchanged())
                        {
                            evaluator = new xwfECMAevaluator(xwfm, doc.getNode());
                        }
                        objectEvaluated = evaluator.eval(xwfm, formula + ";");
                    }
                            
                    switch(tc)
                    {
                        case boDefAttribute.VALUE_BOOLEAN:
                        {
                            Boolean beval = (Boolean)objectEvaluated;
                            xwfm.setVarValueObject(varValue, beval, AttributeHandler.INPUT_FROM_INTERNAL);
                            break;
                        }
                        case boDefAttribute.VALUE_NUMBER:
                        {
                            Double deval = (Double)objectEvaluated;
                            xwfm.setVarValueObject(varValue, deval, AttributeHandler.INPUT_FROM_INTERNAL);
                            break;
                        }
                        case boDefAttribute.VALUE_CHAR:
                        {
                            String seval = (String)objectEvaluated;
                            xwfm.setVarValueObject(varValue, seval, AttributeHandler.INPUT_FROM_INTERNAL);
                            break;
                        }
                        case boDefAttribute.VALUE_CLOB:
                        {
                            String seval = (String)objectEvaluated;
                            xwfm.setVarValueObject(varValue, seval, AttributeHandler.INPUT_FROM_INTERNAL);
                            break;
                        }
                        case boDefAttribute.VALUE_DATE:
                        {
                            Date deval = (Date)objectEvaluated;
                            xwfm.setVarValueObject(varValue, deval, AttributeHandler.INPUT_FROM_INTERNAL);
                            break;
                        }
                        case boDefAttribute.VALUE_DATETIME:
                        { 
                            Date deval = (Date)objectEvaluated;
                            xwfm.setVarValueObject(varValue, deval, AttributeHandler.INPUT_FROM_INTERNAL);
                            break;
                        }
                        case boDefAttribute.VALUE_UNKNOWN:
                        {
                            String card = varValue.getAttribute("maxoccurs").getValueString();
                            if("1".equals(card))
                            { 
                                boObject objeval = (boObject)objectEvaluated;
                                xwfm.setVarValueObject(varValue, objeval, AttributeHandler.INPUT_FROM_INTERNAL);
                            }
                            else
                            {
                                boObjectList objleval = (boObjectList)objectEvaluated;
                                xwfm.setVarValueObject(varValue, objleval, AttributeHandler.INPUT_FROM_INTERNAL);
                            }
                            break;
                        }
                    }
                }   
                catch(Exception e)
                {
                    //ignore?
                }  
            }
        }  
    }
    private Object executeJavaCode(String code)
    {
        Object result = null;
        NameSpace nsp = new NameSpace(new BshClassManager(),"executeJavaCode");
        bsh.Interpreter bshi = new bsh.Interpreter();
        nsp.importPackage("netgest.bo");
        nsp.importPackage("netgest.bo.def");
        nsp.importPackage("netgest.utils");
        nsp.importPackage("netgest.bo.runtime");
        nsp.importPackage("netgest.bo.utils");                
        bshi.setNameSpace(nsp);
        
        try
        {                    
            CodeJavaConstructor cjc = new CodeJavaConstructor();
            code = cjc.treatCodeJava(code);
            result = bshi.eval(code);
        }
        catch (Exception e)
        {
            //ignore
        } 
        return result;
    }
}