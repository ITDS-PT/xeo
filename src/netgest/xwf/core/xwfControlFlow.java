/*Enconding=UTF-8*/
package netgest.xwf.core;

import java.lang.reflect.Constructor;
import netgest.bo.controller.Controller;
import netgest.bo.controller.xwf.XwfController;
import netgest.xwf.StepControl;
import netgest.xwf.common.*;
import netgest.xwf.stepControllers.*;
import bsh.Primitive;
import java.io.ByteArrayOutputStream;
import java.util.*;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.*;
import netgest.bo.security.*;
import netgest.bo.utils.DateUtils;
import netgest.utils.*;
import netgest.bo.def.*;
import netgest.bo.dochtml.*;

import netgest.utils.IOUtils;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;
import netgest.io.*;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XSLException;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.rmi.server.UID;
import netgest.bo.system.Logger;

/**
 * Classe responsavel pelo controlo de fluxos de um programa XWF.
 * <p>Mediante o XML que representa o programa irá iniciliazar o objecto <code>xwfProgramRuntime</code> com as suas variáveis e participantes
 * <p>Lança os passos consecutivamente segundo a sua ordem de precedencias.
 * @author Ricardo Andrade
 */


public class xwfControlFlow 
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.xwf.core.xwfControlFlow");
/**
 * Vector que armazena os apontares para tarefas activas em acada momento
 */
  private Vector pointers;
  /**
   * Estrutura XML que contém a definiçao do programa em execução
   */
  private ngtXMLHandler actual_xml;
/**
 * Tabela para "cache" dos evaluadores de expressões. Acabou por ser abandonada devido a dúvidas existentes acerca
 * da seu beneficio vs possibildiade de erros
 */
  private Hashtable evaluators = new Hashtable();
  /**
   * Tabela que tendo como chave o nome do step a controlar armazena a classe responsável por gerir o lançamento do passo
   */
  private Hashtable step_ctrls = null;
  /**
   * xwfBoManager do programa
   */
  private xwfBoManager xwfm;
  
  /**
   * Construtor do controlo de fluxos do xwf. Inicializa apenas os membros privados necessários ao funcionamento.
   * 
   * @param doc         docHTML que conterá o contexto necessário a execução
   * @param boui_prog   BOUI do programa a controlar
   * @throws java.lang.boRuntimeException  Excepção lançada a quando da falha no load do prgrama através do seu boui
   */
  public xwfControlFlow(docHTML doc, long boui_prog) throws boRuntimeException
  {
    xwfm = new xwfBoManager(doc, boui_prog);
    boObject prog = xwfm.getProgram();
    
    String xml = prog.getAttribute("flow").getValueString();
    if(xml == null || xml.length() < 1)
      return;
    actual_xml = new ngtXMLHandler(xml);
    try{
      ngtXMLHandler n = new ngtXMLHandler( actual_xml.getDocument().selectSingleNode("/program"));
      String execVal = n.getAttribute("exec");

    }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "<init>", e);}
  }
  
  /**
   * Construtor do controlo de fluxos do xwf. Inicializa apenas os membros privados necessários ao funcionamento.
   * Recebe logo o boManager para não haver necessidade de ser criado outro
   * 
   * @param xbm         xwfBoManager que controlará as operações dos objectos no contexto do XWF
   * @throws java.lang.boRuntimeException  Excepção lançada a quando da falha no load do prgrama através do seu boui
   */
  public xwfControlFlow(xwfBoManager xbm) throws boRuntimeException
  {
    xwfm = xbm;
    boObject prog = xwfm.getProgram();
    
    String xml = prog.getAttribute("flow").getValueString();
    if(xml == null || xml.length() < 1)
      return;
    actual_xml = new ngtXMLHandler(xml);
    try{
      ngtXMLHandler n = new ngtXMLHandler( actual_xml.getDocument().selectSingleNode("/program"));
      String execVal = n.getAttribute("exec");
    }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "<init>", e);}
  }
  
  /**
   * Construtor do controlo de fluxos do xwf. Inicializa apenas os membros privados necessários ao funcionamento.
   * Utilizado principalmente quando é ncessário iniciar o controlador de fluxos sem se estar dentro de uma página e portanto não se tem o 
   * docHTML necessário para criar o xwfBoManager.
   * 
   * @param doc         docHTML que conterá o contexto necessário a execução
   * @param prog        Object xwfProgramruntime que o controlador deve controlar
   * @throws java.lang.boRuntimeException  Excepção lançada a quando da falha no load do prgrama através do seu boui
   */
  public xwfControlFlow(docHTML doc, boObject prog) throws boRuntimeException
  {
    xwfm = new xwfBoManager(doc, prog);
    String xml = xwfm.getProgram().getAttribute("flow").getValueString();
    if(xml == null)
      return;
    actual_xml = new ngtXMLHandler(xml);
    try{
      ngtXMLHandler n = new ngtXMLHandler( actual_xml.getDocument().selectSingleNode("/program"));
      String execVal = n.getAttribute("exec");
    }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "<init>", e);}
  }
  
  /**
   * Rotina que desencadeia todo o processo de controlo de fluxos do xwf
   * @throws java.lang.boRuntimeException Excepções lançadas ao nível da leitura do XML ou do load de objectos 
   */
  public void ControlFlow() throws boRuntimeException
  {
    String status=null;
    long stnumb=-1;

      status = xwfm.getProgram().getStateAttribute("runningState").getValueString();
      if(status == null)
        return;      
      if(status.equals("open") || 
         status.equals("reopen"))
      { 
        advanceFlow(); 
      }
      closeControlFlow();
  }
  
  /**
     * Função que procederá a pesquisa da variável estática já inicializada e existente. 
     * @throws netgest.bo.runtime.boRuntimeException Excepções ao nivel da manipulação de objectos
     * @return  <code>true</code> se a variavel for encontrada e ligada com sucesso, <false> caso ainda não exista nenhuma 
     * variavel estatica para esta definição e seja necessário criar
     * @param name  nome da variavel a ligar
     * @param var   nó com a definição da variavel
     */
  private boolean staticMapVarFound(ngtXMLHandler var, String name) throws boRuntimeException
  {
    boObjectList bol = null;
    
    if(var.getNodeName().equals("defVariable"))
    {
        bol = xwfm.listObject("select xwfVariable where staticKey = '"+xwfHelper.produceStaticKey(xwfm, name)+"'");
    }
    else
    {
        bol = xwfm.listObject("select xwfParticipant where staticKey = '"+xwfHelper.produceStaticKey(xwfm, name)+"'");
    }
    bol.beforeFirst();
    if(bol.getRecordCount() >= 1 && bol.next())
    {   
        Node nvar = var.getDocument().createElement("VAR_ADDRESS");
        Node nvarCD = var.getDocument().createCDATASection(""+bol.getCurrentBoui());
        nvar.appendChild(nvarCD);
        var.getNode().appendChild(nvar);
        if(var.getNodeName().equals("defVariable"))
            xwfm.getProgram().getBridge("variables").add(bol.getCurrentBoui());
        else
            xwfm.getProgram().getBridge("participants").add(bol.getCurrentBoui());
        return true;
    } 
    else
    {
        return false;
    }
  }
  
  /**
   * Rotina que faz o mapeamento do XML que define a variável para o objecto que fará o suporte aos dados
   * @param var     nó xml da variável
   * @param vo      objecto xwfVariable que suportará os dados
   * @param ovalue  valor passado como valor para a variável
   * @param start   objecto que deu inicio ao programa 
   * @param boui_def boui da definição do programa que declarou a variavel. Apenas para diferenciar variáveis declaradas através de sub_programas
   * @return        <code>true</code> caso a variável seja de input e <code>false</code> caso contrário
   * @throws netgest.bo.runtime.boRuntimeException
   */
  private boolean mapVar(ngtXMLHandler var, boObject vo, Object ovalue, boObject start, long boui_def) throws boRuntimeException
  {
    String name, val, type, input, label, desc, lov, valid, formula, hidden, render, filtQ, filtC;
    boolean res = false;
    name = var.getAttribute("name");
    input = var.getAttribute("input");
    val = var.getChildNodeText("defaultValue", "");
    type = var.getChildNodeText("type","");
    label = var.getChildNodeText("label","");
    desc = var.getChildNodeText("description","");
    //Preenchimento do xwfVariable (vo)
    vo.getAttribute("name").setValueString(name);
    vo.getAttribute("label").setValueString(label);
    if(vo.getName().equals("xwfVariable"))
    {
      vo.getAttribute("isClone").setValueString("0");
      if(input != null)
          if("true".equals(input))
            vo.getAttribute("input").setValueString("1");
          else
            vo.getAttribute("input").setValueString("0");
    }
    
    lov = var.getChildNodeText("lov", null);
    if(lov != null)
      vo.getAttribute("lov").setValueString(lov);
    lov = var.getChildNodeText("valid", null);
    if(lov != null)
      vo.getAttribute("valid").setValueString(lov);
    lov = var.getChildNodeText("formula", null);
    if(lov != null)
      vo.getAttribute("formula").setValueString(lov);
    lov = var.getChildNodeText("hiddenWhen", null);
    if(lov != null)
      vo.getAttribute("hiddenWhen").setValueString(lov);
    
    lov = var.getChildNodeText("question", null);
    if(lov != null)    
      vo.getAttribute("question").setValueString(lov);
    lov = var.getChildNodeText("mode", null);
      if("read".equalsIgnoreCase(lov))
        vo.getAttribute("mode").setValueLong(0);
      else
        vo.getAttribute("mode").setValueLong(1);
    lov = var.getChildNodeText("showMode", null);
    if(lov != null)    
      if("viewObject".equalsIgnoreCase(lov))
        vo.getAttribute("showMode").setValueLong(0);
      else
        if("viewAsLov".equalsIgnoreCase(lov))
          vo.getAttribute("showMode").setValueLong(2);
        else
          vo.getAttribute("showMode").setValueLong(1);
    if(boui_def > 0)
    {
      vo.getAttribute("subProgDef").setValueLong(boui_def);      
    }
  
          
    lov = var.getChildNodeText("validDB", null);
    if(lov != null)    
      if("Y".equalsIgnoreCase(lov))
        vo.getAttribute("validDB").setValueString("1");
      else
        vo.getAttribute("validDB").setValueString("0");
    lov = var.getChildNodeText("validBusiness", null);
    if(lov != null)    
      if("Y".equalsIgnoreCase(lov))
        vo.getAttribute("validBusiness").setValueString("1");
      else
        vo.getAttribute("validBusiness").setValueString("0");
    lov = var.getChildNodeText("required", "");
    
      if("Y".equalsIgnoreCase(lov))
        vo.getAttribute("required").setValueString("1");
      else
        vo.getAttribute("required").setValueString("0");
        
    ngtXMLHandler of = var.getChildNode("objectFilter");
    if(of != null)
    {
      lov = of.getChildNodeText("xeo_ql", null);
      if(lov != null)    
        vo.getAttribute("objectFilterQuery").setValueString(lov);
    }
    
    //Descobrir se será necessário criar um xwfVarValue ou é possível usar o existente
    boObject varVal = null;
    boolean passedVal = false;
    if(start != null && start.getName().equals("xwfVarValue"))
    {//se o objecto q deu ínicio for do tipo varValue é porque se está a partilhar este objecto com esta variável
      varVal = start;
      passedVal = true;
    }
    else
    {
      if(ovalue != null && ovalue instanceof boObject)
      {
        boObject ovalo = null;
        ovalo = (boObject)ovalue;
        if(ovalo.getName().equalsIgnoreCase("xwfVarValue"))
        {//se o valor passado for do tipo varValue é porque se está a partilhar este objecto com esta variável
          varVal = ovalo;
          res = false;
          passedVal = true;
        }
        else
          varVal = xwfm.createObject("xwfVarValue");
      }
      else
        varVal = xwfm.createObject("xwfVarValue");
        
      varVal.getAttribute("program").setValueLong(xwfm.getProgBoui());
    }

    String link = var.getChildNodeText("linkVar", null);
    //Preenchimento do xwfVarValue
    if(link != null)
      varVal.getAttribute("linkVar").setValueString(link);
    link = var.getChildNodeText("linkAttribute", null);
    if(link != null)
      varVal.getAttribute("linkAttribute").setValueString(link);
      
    if("false".equals(var.getAttribute("static", "false")) || link != null)
    {
        String code_usid = xwfHelper.markUSID(var.getNode());
        varVal.getAttribute("unique_sid").setValueString(code_usid);
    }
    int tc = xwfHelper.typeCode(type);
    varVal.getAttribute("type").setValueLong(tc);
    String subt=null;
    if(tc == 0)
    {
      long boui_cls=0;
      try
      {
        boui_cls = Long.parseLong(type);
      }
      catch(Exception e){
        subt = type.substring(7);
        boui_cls = xwfm.loadObject("select Ebo_ClsReg where name = '"+ subt +"'").getBoui();
      }
      varVal.getAttribute("object").setValueLong(boui_cls);
      String max = var.getChildNodeText("maxoccurs", "1");
      if("N".equals(max))
        max = "9999";
      varVal.getAttribute("maxoccurs").setValueString(max);
      String min = var.getChildNodeText("minoccurs", "");
      varVal.getAttribute("minoccurs").setValueString(min);
      
    
      of = var.getChildNode("availableMethods");
      if(of != null && boui_cls > 0)
      {
         XMLDocument xdoc = new XMLDocument();
        xdoc.appendChild(xdoc.importNode(of.getNode(), true));
         ByteArrayOutputStream xmlOS = new ByteArrayOutputStream();
         try{
          xdoc.print(xmlOS,"UTF-8");
          vo.getAttribute("availableMethods").setValueString(xmlOS.toString("UTF-8"));
         }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "mapVars", e);}         
      }
      
      of = var.getChildNode("hiddenMethods");
      if(of != null && boui_cls > 0)
      {
        XMLDocument xdoc = new XMLDocument();
         xdoc.appendChild(xdoc.importNode(of.getNode(), true));
         ByteArrayOutputStream xmlOS = new ByteArrayOutputStream();
         try{
          xdoc.print(xmlOS,"UTF-8");
          vo.getAttribute("hiddenMethods").setValueString(xmlOS.toString("UTF-8"));
         }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "mapVars", e);}         
      }
      
      of = var.getChildNode("requireMethods");
      if(of != null && boui_cls > 0)
      {
        XMLDocument xdoc = new XMLDocument();
         xdoc.appendChild(xdoc.importNode(of.getNode(), true));
         ByteArrayOutputStream xmlOS = new ByteArrayOutputStream();
         try{
          xdoc.print(xmlOS,"UTF-8");
          vo.getAttribute("requireMethods").setValueString(xmlOS.toString("UTF-8"));
         }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "mapVars", e);}         
      }
      


    }
    if(!passedVal)//caso tenha sido criado um novo xwfVarValue há a necessidade de preencher com o respectivo valor
      switch(tc)
      {
        case boDefAttribute.VALUE_BOOLEAN:
          {
            if(ovalue != null)
            {
              xwfm.setVarValueObject(varVal, ovalue, false, AttributeHandler.INPUT_FROM_DEFAULT);
              res = false;
            }
            else
              if(!val.equals(""))
              {
                if(val.equalsIgnoreCase("false"))
                  xwfm.setValueString(varVal, "0", false, AttributeHandler.INPUT_FROM_DEFAULT);
                else
                  xwfm.setValueString(varVal, "1", false, AttributeHandler.INPUT_FROM_DEFAULT);
                res = false;
              }
              else
                res = true;
            break;
          }
        case boDefAttribute.VALUE_NUMBER:
          {
            if(ovalue != null)
            {
              try{
                xwfm.setValueString(varVal, ((Primitive)ovalue).numberValue().toString(), false, AttributeHandler.INPUT_FROM_DEFAULT);
              }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "mapVars", e);}
              res = false;
            }
            else
              if(!val.equals(""))
              {
                xwfm.setValueString(varVal, val, false, AttributeHandler.INPUT_FROM_DEFAULT);
                res = false;
              }
              else
                res = true;
            break;
          }
        case boDefAttribute.VALUE_CHAR:
          {
            if(ovalue != null)
            {
              xwfm.setVarValueObject(varVal, ovalue, false, AttributeHandler.INPUT_FROM_DEFAULT);
              res = false;
            }
            else
              if(!val.equals(""))
              {
            
                xwfm.setValueString(varVal, val, false, AttributeHandler.INPUT_FROM_DEFAULT);
                res = false;
              }
              else
                res = true;
            break;
          }
        case boDefAttribute.VALUE_CLOB:
          {
            if(ovalue != null)
            {
              xwfm.setVarValueObject(varVal, ovalue, false, AttributeHandler.INPUT_FROM_DEFAULT);
              res = false;
            }
            else
              if(!val.equals(""))
              {
                xwfm.setValueString(varVal, val, false, AttributeHandler.INPUT_FROM_DEFAULT);
                res = false;
              }
              else
                res = true;
            break;
          }
        case boDefAttribute.VALUE_DATE:
          {
            if(ovalue != null)
            {
              xwfm.setVarValueObject(varVal, ovalue, false, AttributeHandler.INPUT_FROM_DEFAULT);
              res = false;
            }
            else
              if(!val.equals(""))
              {
                xwfm.setValueString(varVal, val, false, AttributeHandler.INPUT_FROM_DEFAULT);
                res = false;
              }
              else
                res = true;
            break;
          }
        case boDefAttribute.VALUE_DATETIME:
          {
            if(ovalue != null)
            {
              xwfm.setVarValueObject(varVal, ovalue, false, AttributeHandler.INPUT_FROM_DEFAULT);
              res = false;
            }
            else
              if(!val.equals(""))
              {
                xwfm.setValueString(varVal, val, false, AttributeHandler.INPUT_FROM_DEFAULT);
                res = false;
              }
              else
                res = true;
            break;
          }
        case boDefAttribute.VALUE_UNKNOWN:
          {
            if(ovalue != null)
            {
              String card = varVal.getAttribute("maxoccurs").getValueString();
              if("1".equals(card))
              {
                xwfm.setVarValueObject(varVal, ovalue, false, AttributeHandler.INPUT_FROM_DEFAULT);
              }
              else
              {
                xwfm.setVarValueObject(varVal, ovalue, false, AttributeHandler.INPUT_FROM_DEFAULT);
              }
              res = false;
            }
            else
            {
              boolean matched=false;
              if(start != null && subt != null && input!=null && input.equalsIgnoreCase("true"))
              {
                if(start.getBoDefinition().getBoName().equalsIgnoreCase(boDefHandler.getBoDefinition(subt).getBoName()))
                {
                  res = false;
                  xwfm.setVarValueObject(varVal, start, false, AttributeHandler.INPUT_FROM_DEFAULT);
                  matched = true;
                }
                else
                {
                  boDefHandler[] sub_cls = boDefHandler.getBoDefinition(subt).getTreeSubClasses();
                  
                  for(int j=0; j < sub_cls.length; j++)
                  {
                    if(sub_cls[j].getBoName().equalsIgnoreCase(start.getBoDefinition().getBoName()))
                    {
                      xwfm.setVarValueObject(varVal, start, false, AttributeHandler.INPUT_FROM_DEFAULT);
                      matched = true;
                      break;
                    }
                  }
                  if(matched)
                    res = false;
                  else
                    res = true;
                }
              }
              if(!matched)
                if(!val.equals("") )
                {
                  if(val.substring(0,4).equals("boui"))
                  {
                    val = val.substring(5, val.length()-1);
                    xwfm.setValueString(varVal, val, false, AttributeHandler.INPUT_FROM_DEFAULT);
                  }
                  else
                  {
                    if(val.substring(0,4).equals("boql"))
                    {
                      val = val.substring(5);
                      xwfm.setVarValueObject(varVal, xwfm.loadObject(val), false, AttributeHandler.INPUT_FROM_DEFAULT);
                    }
                    else
                    {
                      xwfm.setValueString(varVal, val, false, AttributeHandler.INPUT_FROM_DEFAULT);
                    }
                  }
                }
                else
                {//casos especias de preenchimento automático: starter e adiministrator
                  if(xwfHelper.PART_STARTER_NAME.equals(name))
                  { 
                    long set_boui = xwfm.getPerformerBoui();
                    if(set_boui <= 0)
                    {
                        set_boui = getProgramAdmin(xwfm.getProgram());
                    }
                    xwfm.setVarValueObject(varVal, xwfm.getObject(set_boui), false, AttributeHandler.INPUT_FROM_DEFAULT);    
                  }                  
                  else
                    if(xwfHelper.PART_WFADMINISTRATOR_NAME.equals(name))
                    {
                      boObject prt = xwfm.getProgram();
                      long admin_boui = prt.getAttribute("administrator").getValueLong();
                      if(admin_boui < 1)
                      {
                        boObject def = prt.getAttribute("programDef").getObject();
                        admin_boui=def.getAttribute("administrator").getValueLong();
                        if(admin_boui > 0)
                          xwfm.setVarValueObject(varVal, xwfm.getObject(admin_boui), false, AttributeHandler.INPUT_FROM_DEFAULT);
                      } 
                      else
                        xwfm.setVarValueObject(varVal, xwfm.getObject(admin_boui), false, AttributeHandler.INPUT_FROM_DEFAULT);
                    }
                    else
                      res = true;
                }
            }
            break;
          }
      }
    
      if((input!=null && input.equalsIgnoreCase("true")) )
      {
        res = res && true;
      }
      else
        res = false;
      
      vo.getAttribute("value").setObject(varVal);
    //escrever no XML a referência para os objectos variáveis
      Long lboui = new Long(vo.getBoui());
      Node nvar = var.getDocument().createElement("VAR_ADDRESS");
      Node nvarCD = var.getDocument().createCDATASection(lboui.toString());
      nvar.appendChild(nvarCD);
      var.getNode().appendChild(nvar);
      return res;
  }
   
/**
   * Função que devolve o boui do administrador do actual programa
   * 
   * @param prt     objecto do tipo xwfProgramRuntime a extrair o administrador
   * @throws java.lang.boRuntimeException Erros de manipulação de objectos
   */
   protected long getProgramAdmin(boObject prt) throws boRuntimeException
   {
      long admin_boui = prt.getAttribute("administrator").getValueLong();
      if(admin_boui < 1)
      {
        boObject def = prt.getAttribute("programDef").getObject();
        admin_boui=def.getAttribute("administrator").getValueLong();
      }
      return admin_boui;   
   }
   
   
   /**
   * Rotina de iniciação das variáveis de uma instancia de um programa.
   * <p>Criará os objectos que armazenarão as variaveis e participantes declarados no XML.
   * <p>Irá também escrever no documento XML os BOUIs destes objectos criados por forma a manter uma referência
   * <p>É também usada a quando da chamada de sub-programas para efectuar a inicialização das novas variáveis
   * 
   * @param doc       raiz do documento XML que contém o novo programa a iniciar
   * @param htvars    tabela de dispersão contendo valores de incialização das variáveis (name, value)
   * @param boui      boui do objecto que invocou o programa, sendo um dos possiveis valores de input
   * @param init_usid em caso de estar a ser inicializado um sub-programa é necessário referir o identificador da 
   *                  respectiva instrução callProgram. É passado o valor "0" caso seja a primeira inicialização
   * @throws java.lang.boRuntimeException Erros de escrita no XML ou de criação de novos objectos4
   */
  public void initVars(ngtXMLHandler doc, Hashtable htvars, long boui, String init_usid, long boui_def ) throws boRuntimeException
  {
    
    boolean flag = true;
    doc.goChildNode("program");
    String prog_name = doc.getAttribute("name", null);
    if(!xwfm.getProgram().getStateAttribute("runningState").getValue().equals("open"))
    {
      if(prog_name != null)
        xwfm.getProgram().getAttribute("name").setValueString(prog_name);
      prog_name = doc.getChildNodeText("label", null);
      if(prog_name != null)
        xwfm.getProgram().getAttribute("label").setValueString(prog_name, AttributeHandler.INPUT_FROM_INTERNAL);
      prog_name = doc.getChildNodeText("labelFormula", null);
      if(prog_name != null)
        xwfm.getProgram().getAttribute("labelFormula").setValueString(prog_name);
    }  
    doc.goChildNode("code");
    ngtXMLHandler var = doc.getChildNode("defVariables");
    ngtXMLHandler[] vars = var.getChildNodes();
    
    //PARTICIPANTS
    var = doc.getChildNode("defParticipants");
    ngtXMLHandler[] pars = var.getChildNodes();
    
    ngtXMLHandler[] allvar = new ngtXMLHandler[vars.length+pars.length];
    System.arraycopy(vars, 0, allvar, 0, vars.length);
    System.arraycopy(pars, 0, allvar, vars.length, pars.length);
    boObject vo;
    boObject actv=null;
    boObject prog = xwfm.getProgram();
    boObject starter = null;
    //Pré tratamento de fórmulas
    xwfFormulaCalc xf = new xwfFormulaCalc(this, xwfm);
    xf.startPreCalc();
    xf.finalizePreCalc();
    
    if(boui > 0)
      starter = xwfm.getObject(boui);
    for(int i=0; i < allvar.length; i++)
    { 
      String name = allvar[i].getAttribute("name");
       boolean inputv = false;
      if(allvar[i].getNodeName().equals("defMessage"))
      {
        xwfHelper.markUSID(allvar[i].getNode());
        continue;
      }
      else
      {
       String vv_link = allvar[i].getChildNodeText("linkVar", null);
        if(allvar[i].getAttribute("static", "false").equals("true") && vv_link == null)
        {
            if (staticMapVarFound(allvar[i],name))
            {
                continue;
            }
            else
            {
                if(allvar[i].getNodeName().equals("defVariable"))
                  vo = xwfm.createObject("xwfVariable");
                else
                  vo = xwfm.createObject("xwfParticipant");
              
                inputv = this.mapVar(allvar[i], vo, htvars.get(name), starter, boui_def );
                vo.getAttribute("staticKey").setValueString(xwfHelper.produceStaticKey(xwfm, name));
            }
        }
        else
        {
        
            if(allvar[i].getNodeName().equals("defVariable"))
              vo = xwfm.createObject("xwfVariable");
            else
              vo = xwfm.createObject("xwfParticipant");
          
            inputv = this.mapVar(allvar[i], vo, htvars.get(name), starter, boui_def );
        }
    }   
      if(inputv)
      {//Se for uma variável de input que necessite ser preenchida para o programa arrancar
        flag = false;
        vo.getAttribute("input").setValueString("1");
        vo.getAttribute("required").setValueString("1");
        if(actv == null)
        {//Criação da actividade de preenchimento das variáveis de input não incializadas
          actv = xwfm.createObject("xwfActivityFill"); 
          actv.getStateAttribute("runningState").setValue("create");
          actv.getAttribute("program").setValueLong(prog.getBoui());
          actv.getAttribute("sid").setValueString(init_usid);
          actv.getAttribute("unique_sid").setValueString("0_"+init_usid);
          actv.getAttribute("optional").setValueString("0");
          actv.getAttribute("done").setValueString("0");
          actv.getAttribute("name").setValueString("Input.");
          actv.getAttribute("label").setValueString("Introdução de variáveis de Input.");
          actv.getAttribute("assignedQueue").setValueLong(xwfm.getPerformerBoui());
        }
        if(allvar[i].getNodeName().equals("defVariable"))
          actv.getBridge("variables").add(vo.getBoui());
        else
          actv.getBridge("variables").add(xwfm.createVarFromPar(vo.getBoui()));
      }
      else
        if("".equals(vo.getAttribute("input").getValueString()))
            vo.getAttribute("input").setValueString("0");
      
      //adicionar os objectos variaveis á bridge das variaveis
      if(allvar[i].getNodeName().equals("defVariable"))
        prog.getBridge("variables").add(vo.getBoui());
      else
        prog.getBridge("participants").add(vo.getBoui());
    }
    //aplicação das fórmulas nas variáveis
    bridgeHandler bvar = xwfm.getProgram().getBridge("variables");
    xf.applyFormulas(bvar);
    //aplicação das fórmulas nos participantes
    bvar = xwfm.getProgram().getBridge("participants");
    xf.applyFormulas(bvar);
    xf.setProgLabelFormula(actual_xml);

    if("0".equals(init_usid))
    {
      markProgramStart(doc);   
      if(flag)
      {
        xwfActionHelper.openProgram(xwfm, prog);
        ControlFlow(); 
      }
      else
      {
        prog.getStateAttribute("runningState").setValue("create");
        if(actv != null)
          xwfm.updateObject(actv); 
        this.closeControlFlow();
      }
    }
    else
      if(flag)
      {
        markProgramStart(doc);
        ControlFlow(); 
      }
      else
      {
        if(actv != null)
          xwfm.updateObject(actv); 
        this.closeControlFlow();
      }
    
    doc.goDocumentElement();
  }
  
  /**
   * Procedimento que irá pesquisar pela primeira instrução do programa e marcar o apontador para ela de forma a que seja 
   * processada seguidamente
   * @param doc   handler XML do programa a iniciar. Colocado principal tag code.
   */
  private void markProgramStart(ngtXMLHandler doc)//code tag
  {
    ngtXMLHandler[] childs =  doc.getChildNodes();    
    for(int i=0; i < childs.length; i++)
    {
      if(!childs[i].getNodeName().equalsIgnoreCase("defVariables") && !childs[i].getNodeName().equalsIgnoreCase("defParticipants")
      && !childs[i].getNodeName().equalsIgnoreCase("activityAlerts") && !childs[i].getNodeName().equalsIgnoreCase("defProcedures"))
      {
        Attr att = childs[i].getDocument().createAttribute("pointer");
        att.setValue("start");
        ((XMLElement)childs[i].getNode()).setAttributeNode(att);
        incCounter(childs[i].getNode());
        break;
      }
    }       
  }
  
  

  /**
   * Rotina privada que vai constantemente buscar o próximo passo e executa-o.
   * <p>Só irá parar quando não for possível executar mais nenhum passo devido às suas dependencias ou o WF tiver chegado ao fim
   * 
   * @throws java.lang.boRuntimeException Excepções de tratamento do XML
   */
  
  private void advanceFlow() throws boRuntimeException
  {
    NodeList ln = null;  
    Node pn = null;
    int post=0;
    try{
      ln = getPointers();
      post = ln.getLength()-1;

      pn = ln.item(post);
      while(pn != null)
      {
        xwfHelper.markUSID(pn);     //regista o usid na actividade
        markPointer(pn, "open");
        if(execStep(pn))
        {
          try{
            ln = actual_xml.getDocument().selectNodes("//*[@pointer='start']"); //getPointers
            post = ln.getLength()-1;
          }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "advanceFlow", e);}
          pn = ln.item(post);
        }
        else
          if(post >= 1)
          {
            post--;
            pn = ln.item(post);
          }
          else
          {
            try{
              ln = actual_xml.getDocument().selectNodes("//*[@pointer='start']"); //getPointers
              post = ln.getLength()-1;
            }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "advanceFlow", e);}
            if(ln.getLength() == 0)
              pn = null;
            else
              pn = ln.item(post);
          }
            
      }
    }
    catch(Exception e){
      boObject xwfadmin = xwfm.getObject(xwfHelper.getContextPar(new ngtXMLHandler(pn), xwfHelper.WF_ADMINISTRATOR));
      xwfadmin = xwfm.getValueBoObject(xwfadmin.getAttribute("value").getObject());
      xwfActionHelper.errorHandle(xwfm, xwfadmin, e);
      if(pn!=null)
        markPointer(pn, "error");
    }
  }
  
  
  
  /**
   * Rotina que permite indicar o fim passos de decisão ou de escolha do WF.
   * <p>Para além de finalizar o corrente passo registando a opção escolhida irá mover o apontador para o próximo passo.
   * 
   * @param pn  Nó XML representante do passo a terminar
   * @param option  String contendo o código da opção escolhida
   * @throws java.lang.boRuntimeException  Excepções ao nível da escrita no XML.
   */
    protected void finishedStep(Node pn, String option) throws boRuntimeException
  {
      ngtXMLHandler ngtn = new ngtXMLHandler(pn);
      removeEvaluator(this.getXwfEvalKey(ngtn));
      StepControl se = this.getStepControl(pn);
      se.finishedStep(ngtn, option, this);
  }
 
  /**
   * Rotina que permite indicar o fim de determinado passo no WF.
   * <p>Para além de finalizar o corrente passo irá mover o apontador para o próximo passo.
   * 
   * @param unique_sid  String com o número identificativo do step a terminar
   * @throws java.lang.boRuntimeException  Excepções ao nível da escrita no XML.
   */
  public void finishedStep(String unique_sid) throws boRuntimeException
  {//Se o usid for -1 ou começar por 0 então são casos especiais que devem ser tratados
    if(unique_sid.startsWith("-1") || "".equals(unique_sid))
    {//se for -1 não necessita de ser o feito o tratamento q vai à procura da próxima tarefa
     //tenho que ir buscar o última acção
        unique_sid = getLastUniqueSid();
        if(unique_sid == null || "-1".equalsIgnoreCase(unique_sid) || "".equals(unique_sid))
        {
            finishFlow();
            return;
        }
    }
    if(unique_sid.startsWith("0_"))
    {//casos da actividade de preenchimento das variaveis de input
      if(unique_sid.equals("0_0"))
      {
        boObjectList bol = xwfm.listObject("select xwfActivity where program="+xwfm.getProgBoui()+" and sid='0' and done='0'", false);
        if(bol == null || bol.getRowCount() == 0 )
            xwfActionHelper.openProgram(xwfm, xwfm.getProgram());
        else            
            if(getBoManager().getMode() == xwfHelper.PROGRAM_EXEC_TEST_MODE)
            {
                int c = 0;
                bol.beforeFirst();
                while(bol.next())
                {
                    if(bol.getObject().getAttribute("done").equals("0") && bol.getObject().getAttribute("sid").equals("0"))
                        c++;
                }
                if(c == 0)
                    xwfActionHelper.openProgram(xwfm, xwfm.getProgram());
            }
      }
      else//caso em q existira mais do que uma activida de preenchimento das variaveis de input.
      {//julgo que já não é possível acontecer
        String usid = unique_sid.substring(2);
        Node newcode = null;
        try{
          Node pn = this.getNode(usid).getNode();
          if(pn == null)
          {
              finishFlow();
              return;
          }
          
        newcode = findNextStep(pn, true);
        }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "finishedStep", e);}
        if(newcode != null)
        {
          ngtXMLHandler ngt_code = new ngtXMLHandler(newcode).getChildNode("code");
          markProgramStart(ngt_code); 
        }
        else
          throw new boRuntimeException("xwfControlFlow", "finishedStep", new Exception("Error: "+MessageLocalizer.getMessage("NOT_A_VALID_PROGRAM_DEFINITION_IN_SUB_PROGRAM_CALL")));
      }
    }
    else
    {//casos comuns de actividades lançadas pelo motor que após o seu término lança outras que dependem delas
      try{
        Node pn = actual_xml.getDocument().selectSingleNode("//*[@unique_sid='"+unique_sid+"']");
        if(pn == null)
        {//se não encontra o unique sid na estrutura XML, então temos um problema!!
          try
          {
            actual_xml.getDocument().print(System.out);
          }catch(Exception e){}
        }
        else
          if(pn!=null)
          {
            //antes de fechar verificar se existem alertas para esta actividade
            boObjectList bol = xwfm.listObject("select xwfWait where unique_sid='"+unique_sid+"' and done='0'", false);
            bol.beforeFirst();
            while(bol.next())
            {
              bol.getObject().destroy();
            }
          }
        
        finishedStep(pn);
      }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "finishedStep", e);}
    }
    ControlFlow(); 
  }
  /**
     * Método que delegará na respectiva classe de controlo de steps o término da tarefa
     * @throws netgest.bo.runtime.boRuntimeException    erros de XML 
     * @param pn        nó XML que represanta a tarefa a encerrar
     */
  protected void finishedStep(Node pn) throws boRuntimeException
  {
      ngtXMLHandler ngtn = new ngtXMLHandler(pn);
      removeEvaluator(this.getXwfEvalKey(ngtn));
      StepControl se = this.getStepControl(pn);
      se.finishedStep(ngtn, this);
  }
  
  /**
   * Rotina que permite indicar o fim passos de decisão ou de escolha do WF.
   * <p>Para além de finalizar o corrente passo registando a opção escolhida irá mover o apontador para o próximo passo.
   * 
   * @param unique_sid  String com o número identificativo do step a terminar
   * @param option      String contendo o código da opção escolhida
   * @param boui_actv   boui da actividade a encerrar
   * @throws java.lang.boRuntimeException  Excepções ao nível da escrita no XML.
   */
  public void finishedStep(String unique_sid, String option, long boui_actv) throws boRuntimeException
  {
    if(unique_sid.startsWith("-1"))
    {//se for -1 não necessita de ser o feito o tratamento q vai à procura da próxima tarefa
        finishFlow();
        return;
    }
    else
    {
        try{
          Node pn = actual_xml.getDocument().selectSingleNode("//*[@unique_sid='"+unique_sid+"']");
          if(pn!=null)
          {
            boObjectList bol = xwfm.listObject("select xwfWait where unique_sid='"+unique_sid+"' and done='0'", false);
            bol.beforeFirst();
            while(bol.next())
            {
              bol.getObject().destroy();
            }
    
              ngtXMLHandler npn = new ngtXMLHandler(pn);
              removeEvaluator(this.getXwfEvalKey(npn));
              StepControl se = this.getStepControl(unique_sid);
              if(se != null)
                se.finishedStep(npn, option, this);
          } 
        }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "finishedStep", e);}
        ControlFlow();
    }
  }
  
  
  /**
   * Rotina que irá permitir executar sub-programas antes desta tarefa.
   * @param unique_sid String com o número identificativo do step em que o subprograma é executado
   * @throws netgest.bo.runtime.boRuntimeException Excepções ao nível da escrita e da leitura no XML.
   */
  public void runSubProg(String tagName, String unique_sid, ngtXMLHandler content) throws boRuntimeException
  {
    this.addCodeTag(tagName, unique_sid, content ,true);
    ngtXMLHandler from = this.getNode(unique_sid);
    if(from != null && from.getNode() != null && "false".equals(content.getAttribute("async")))
    {
      this.markPointer(from.getNode(), "wait");
    }
    if(xwfm.getProgram().getStateAttribute("runningState").getValue().equals("close"))
    {
        xwfActionHelper.openProgram(xwfm, xwfm.getProgram());
    }
    this.ControlFlow();
  }
  
  /**
   * Procedimento a desencadear quando determinado passo de espera expirou o seu tempo limite de espera
   * @param unique_sid   String com o número identificativo do step em que o subprograma é executado
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public void timeoutStep(String unique_sid, String name) throws boRuntimeException
  {
    Node pn= null;
    try{
      pn = actual_xml.getDocument().selectSingleNode("//*[@unique_sid='"+unique_sid+"']");
    }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "finishedStep", e);}
    if(pn == null)
      return;
    switch(xwfStepExec.stepType(pn))
    {
      case 0://beginTime
      {
        processAlert(new ngtXMLHandler(pn), unique_sid, name);
        break;
      }
      case 2://waitTime
      {
        this.finishedStep(pn);
        break;
      }
      case 4://waitTime
      {
        this.finishedStep(pn);
        break;
      }
      default:
      {
        processAlert(new ngtXMLHandler(pn), unique_sid, name);
        return;
      }
    }
    ControlFlow();    
  }
  
  /**
   * Rotina privada que faz a gestão da terminação de threads ou tarefas assincronas.
   * @param name  nome identificativo da thread que terminou o seu fluxo
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public void finishThread(String name) throws boRuntimeException
  {
    String query = "select xwfWait where done = '0' and waitFor like '%"+name+"#%' and program = "+xwfm.getProgBoui();
    boObjectList bol = xwfm.listObject(query);
    bol.beforeFirst();
    while(bol.next())
    {
      String ths = bol.getObject().getAttribute("waitFor").getValueString();
      StringTokenizer stok = new StringTokenizer(ths, "#");
      boolean flag=true;
      String lop =  bol.getObject().getAttribute("operator").getValueString();
      if("OR".equalsIgnoreCase(lop))
          flag = false; 
      while(stok.hasMoreTokens())
      {
        String th_name = stok.nextToken();
        ngtXMLHandler nodeth = null;
        try{
          nodeth = xwfHelper.getContextNode(actual_xml, th_name);// new ngtXMLHandler(this.actual_xml.getDocument().selectSingleNode("//*[@name='"+th_name+"']"));
        }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "finishThread", e);}
        if(nodeth != null)
        {
          if(!"done".equals(nodeth.getAttribute("pointer")))
          {
            if("AND".equalsIgnoreCase(lop))
              flag = false; 
          }
          else
            if("OR".equalsIgnoreCase(lop))
              flag = true; 
        }
      }
      if(flag)
      {
        bol.getObject().getAttribute("done").setValueString("1");
        xwfm.updateObject(bol.getObject());
        this.finishedStep(bol.getObject().getAttribute("unique_sid").getValueString());
      }
    }
  }
  
  /**
   * Rotina privada que mediante um nó irá pesquisar pelo nó que lhe precede.
   * <p>Pode ser chamada a quando da terminaçao de um passo ou aquando do começo de um passo
   * <p>Vital em todo o processo de controlo de fluxo
   * 
   * @param n         Nó XML que contém a representação do nó de referencia
   * @param starting  Valor booleano que indica se estamos a terminar o nó ou a começar a sua execução
   * @return          Próximo passo representado através do seu respectivo nó XML
   * @throws java.lang.boRuntimeException Excepções ao nível da escrita no XML.
   */
  public Node findNextStep(Node n, boolean starting)throws boRuntimeException
  {
    ngtXMLHandler ngtn = new ngtXMLHandler(n);
    if(starting && stepType(n)!=0)//se estiver a começar este step vai interessar o código a executar
    {
      
      ngtXMLHandler ngtncode = ngtn.getChildNode("code").getFirstChild();
      if(ngtncode != null)
        return ngtncode.getNode();
    }
    //se estiver a acabar e quisermos saber o que se segue vamos procurar o próximo
    Node nxt = n.getNextSibling();
    if(nxt != null)
    {
      String nxtname = nxt.getNodeName();
      if(nxtname.startsWith("#"))
        nxt = nxt.getNextSibling();
    }
    if(nxt == null || stepType(nxt)==-1)//se não se seguir mais nada no seu ramo há que ver o pai
    {
      Node par = n.getParentNode();
      if(par.getNodeName().equalsIgnoreCase("code"))//se o pai for do tipo code então queremos subir mais um nível
      {
        par = par.getParentNode();
        if(par.getNodeName().equalsIgnoreCase("program") || par.getNodeName().equalsIgnoreCase("procedure"))//se o pai for program já subimos tudo e não existem mais nós a analisar
        {
          Node grandpar = par.getParentNode();  //a menos que tenha sido chamada através de uma instrução CALLPROGRAM
          if(grandpar == null || grandpar.getNodeName().startsWith("#"))
          {
            this.finishFlow();
            return null;
          }
          else
          {
            Node grandgrand = grandpar.getParentNode();
            if(par.getNodeName().equalsIgnoreCase("procedure") && 
              (grandgrand != null && !xwfHelper.STEP_CALL_PROCEDURE.equals(grandgrand.getNodeName())))
              return null;
            else
              return findNextStep(par, false);
          }
        }
        else
        {
          int st = stepType(par);
          if(st == 0)
            st = xwfStepExec.stepType(par)*10; //para diferenciar os tipos 
          if(st == 5 || st == 6 || st == 150 )//se for um while, um foreach ou um menu não queremos acabar o step 
            return par;
          else
            if(st == 3) //se for uma thread, acabou aqui o seu fio de execução não interessa verificar mais
            {
              //informar o controlador de tempos do fim desta thread
              this.markPointer(par, "done");
              this.finishThread(new ngtXMLHandler(par).getAttribute("name",""));
//              if(this.endOfProg)
                this.finishFlow();
              return null;
            }
            else
              if(st == 11) //se for um callProgram, acabou aqui o seu fio de execução não interessa verificar mais
              {
                this.markPointer(par, "done");
//                this.finishFlow();
                return findNextStep(par, false);
              }
              else
              {
                return findNextStep(par, false); //caso contrário queremos andar para a frente.
              }
        }
      }
      else //se afinal o pai não era code 
        if(stepType(par)==-1)//mas alguma tag que não representa um step
        {
          
          Node grd = par.getParentNode();
          ngtXMLHandler ngrd = new ngtXMLHandler(grd);
          String pgrd = ngrd.getAttribute("pointer");
          if(pgrd == null || (pgrd != null && !pgrd.equals("wait")))//se não estiver há espera
          {
            if(!par.getNodeName().equals(xwfHelper.STEP_SUB_PROGRAM))
            {
              Node grdgrd = par.getParentNode(); 
              if(xwfHelper.STEP_MENU.equals(grdgrd.getNodeName()))//se for do tipo menu vamos querer que seja repetido
                return grdgrd;
              else
                finishedStep(grdgrd);//vamos acabar com o pai que certamente será uma tag conhecida
                                                 //isto já fará avançar o apontador pelo que podemos retornar null
            }
          }                                
          
          else//se estiver à espera vamos voltar a marcá-lo como open
            if(xwfStepExec.stepType(n) != 14 || ("done".equals(ngtn.getAttribute("pointer", "open"))))
              this.markPointer(grd, "open");
//          return findNextStep(par.getParentNode(), false); //vamos ao avô ver qual é o próximo
          return null;
        }
        else
          return par;//se representa então podemos devolver
    }
    else
      return nxt;
  }
  /**
   * Devolve uma lista de nós que representam os passos que estão no estado <code>start</code>
   * @return Lista de nós abertos
   * @throws java.lang.boRuntimeException Excepçõa ao nível de selecção de nós XML
   */
  private NodeList getPointers() throws boRuntimeException
  {
    if(actual_xml == null || actual_xml.getNode() == null)
        return null;
    try{
      return actual_xml.getDocument().selectNodes("//*[@pointer='start']");
    }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "getPointers", e);}
  }
  
  /**
     * Verifica a existência de apontadores no XML que indicam que uma actividade foi lançada e não concluída 
     * @throws netgest.bo.runtime.boRuntimeException
     * @return <code>true</code> caso existam apontadores com o valor <code>start</code>, <code>false</code> caso contrário
     */
  private boolean existsOpenPointers() throws boRuntimeException
  {
      NodeList nl = getPointers();
      if(nl == null || nl.getLength()<=0)
        return false;
      else
        return true;
  }
  /**
     * Verifica se existem procedimentos obrigatórios que ainda não foram executados.
     * <p>Actualmente basta que um dos procedimentos obrigatórios tenha sido lançado para a função retornar <ocde>true</code>
     * @throws netgest.bo.runtime.boRuntimeException
     * @return <code>true</code> caso existam procediemntos obrigatórios não executados, <code>false</code> caso contrário 
     */
   private boolean existsRequiredProcNotExecuted() throws boRuntimeException
  {
    boolean ret=false;
    if(actual_xml == null || actual_xml.getNode() == null)
        return ret;
    try{
      String sproc = xwfm.getProgram().getAttribute("procedures").getValueString();
      if("".equals(sproc))
        return false;  
      ngtXMLHandler xmlProc = new ngtXMLHandler(sproc);
      NodeList nl = xmlProc.getDocument().selectNodes("//procedure[@required='yes']");
      for(int i=0; i < nl.getLength(); i++)
      {
          ngtXMLHandler nproc = new ngtXMLHandler(nl.item(i));
          String count_val = nproc.getAttribute("count", "0");
          if(count_val.equals("0"))
            ret = true;
          else
          {
            ret = false;
            break;
          }
      }
    }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "existsRequiredMethodsExecuted", e);}
    return ret;
  }
  
  /**
   * Selector básico que devolve o handler xml para o programa actual
   * @return handler xml para o programa actual
   */
  public ngtXMLHandler getActualXml()
  {
    return actual_xml;
  }
  
  /**
   * Selector básico que devolve o boManager para o programa actual
   * @return boManager para o programa actual
   */
  public xwfBoManager getBoManager()
  {
    return xwfm;
  }
  /**
     * Modificador da estrutura XML guardada em memória 
     * @param new_xml   nova estrutura XML a guardar
     */
  private void setActualXml(ngtXMLHandler new_xml)
  {
    actual_xml = new_xml;
  }
  /**
   * Rotina que centraliza a execução dos steps do WF.
   * <p>Os vários tipos de steps são classificados e executados mediante o seu tipo
   * @param n   Nó XML que representa o step a ser executado
   * @return    <code>true</code> caso a execução tenha sido sincrona e <code>false</code> caso tenha sido feito assincronamente
   * @throws java.lang.boRuntimeException  Excepções sobre a execução dos steps ou de consulta do XML
   */
  private boolean execStep(Node n)throws boRuntimeException
  {
    switch(stepType(n))
    {
      case 0:
      {
        return makeParticipantStep(n);      
      }
      case 3://thread
      {
        makeThread(n);
        return true;
      }
      case 4: //goto
      {
        makeGoto(n);
        return true;
      }
      case 5://while
      {
        makeWhile(n);
        return true;
      }
      case 6://foreach
      {
        makeForeach(n);
        return true;
      }
      case 8: //if
      {
        makeIf(n);
        return true;
      }
      case 9: //switch
      {
        makeSwitch(n);
        return true;
      }
      case 11: //callProgram
      {        
        return makeCallProgram(n);
      }
      case 12: //cancel
      {        
        makeCancel(n);
        return true;
      }
      case 13: //exit
      {        
        makeExit(n);
        return true;
      }
      case 14: //addProcedure
      {        
        makeAddProc(n);
        return true;
      }
      case 15: //removeProcedure
      {        
        makeRemProc(n);
        return true;
      }
      case 16: //removeAllProcedures
      {        
        xwfm.getProgram().getAttribute("procedures").setValueString("");
        finishedStep(n);
        return true;
      }
      case 17: //callProcedure
      {        
        makeCallProcedure(n);
        return true;
      }
      case 18: //terminateProgram
      {        
        xwfActionHelper.interruptProgram("close", xwfm, this);
//        finishedStep(n);
        return true;
      }
      case -1:
      {
        break;
      }
      case -2:
      {
        return makeParticipantStep(n);   
        //break;
      }
      default://comment
      {
        finishedStep(n);
      }
    }
    return true;
  }
  /**
   * Lança o processo para a classe <code>xwfStepExec</code> encarregando-a da execução do passo.
   * 
   * @param pointer     Nó XML que representa o passo a ser executado
   * @return            <code>true</code> caso a execução tenha sido sincrona e <code>false</code> caso tenha sido feito assincronamente
   * @throws java.lang.boRuntimeException 
   */
  private boolean sendExecution(Node pointer, boolean wait) throws boRuntimeException
  {
    StepControl se = null;
    boolean res = false;
    if(!wait)
    {   
      se = this.getStepControl(pointer);//encontrar a classe q gere o step
      if(se != null)
        res = se.execStep();
      else
        return false;
      return res;
    }
    else
      return false;
  }
  
  /**
   * Verifica se existe sub-programa a ser lançado e assinala o seu apontador em caso afirmativo
   * @param n   nó XML que contém a descrição do sub-programa a executar
   * @return  <code>true</code> caso exista subprograma para ser executado, <code>false</code> caso contrário
   * @throws netgest.bo.runtime.boRuntimeException
   */
  private boolean startSubProgram(ngtXMLHandler n)throws boRuntimeException
  {
    ngtXMLHandler subn = n.getChildNode("subprogram");
    if(subn != null)
    {
      ngtXMLHandler subnxtn = subn.getFirstChild();
      if(subnxtn != null)
      {
        markPointer(subnxtn.getNode(), "start");
        return true;
      }
      else
        return false;
    }
    else
      return false;
  }
  
  /**
   * Rotina responsável pela criação de todas as actividades que necessitem da interacção dos utilziadores + step do tipo xepcode
   * @param n     nó que representa o step a executar
   * @return      devolve <code>true</code> caso o procedimento do step esteja concluído e <code>false</code> caso necessite 
   *              da resposta do utilizador
   * @throws netgest.bo.runtime.boRuntimeException
   */
  private boolean makeParticipantStep(Node n)throws boRuntimeException
  {
    ngtXMLHandler ngtn = new ngtXMLHandler(n);
    
    String ass_val = ngtn.getAttribute("async");
    String allEnded = ngtn.getAttribute("allEnded");
    
    if(ass_val != null && ass_val.equals("true"))
    {
      Node nextpn = findNextStep(n, false);
      if(nextpn != null)
      {
        if(markPointer(nextpn, "start"))
          incCounter(nextpn);
      }
    }

    boolean exs;
    if(allEnded != null && "true".equals(allEnded) &&
       waitForCloseAllActv())
    {
      this.markPointer(n, "wait");
      exs = true;
    }
    else
    {
        boolean sub_val = this.startSubProgram(ngtn);        
        if(sub_val)
        {
          this.markPointer(n, "wait");
          exs = true;
        }
        else
        {
          exs = sendExecution(n, false);      
          if(exs)
            finishedStep(n);
        }
    }
    return exs;    
  }
  
  private boolean waitForCloseAllActv() throws boRuntimeException
  {
      boObjectList list = xwfm.listObject("select xwfDefActivity where STATE=1 AND (ACTIVESTATUS=0 OR ACTIVESTATUS=1) and program = " + xwfm.getProgBoui(), false, false);
      list.beforeFirst();
      boObject def = null;
      if(list.next())
      {
        return true;
      }
      
      list = xwfm.listObject("select xwfActivity where (runningState<>20 AND  runningState<>90) and program = " + xwfm.getProgBoui(), false, false);
      list.beforeFirst();
      if(list.next())
      {
        return true;
      }
      return false;
  }
  
  /**
   * Rotina encarregue do processamento dos steps do tipo <code>thread<code>
   * @param n   Nó XML que representa o passo a ser executado
   * @throws java.lang.boRuntimeException  Excepções sobre a execução dos steps ou de consulta do XML
   */
  private void makeThread(Node n)throws boRuntimeException
  {
    Node nextpn = findNextStep(n, true);
    if(nextpn != null)
    {
      if(markPointer(nextpn, "start"))
        incCounter(nextpn);
    }
//    finishedStep(n);
    nextpn = findNextStep(n, false);
    if(nextpn != null)
    {
      if(markPointer(nextpn, "start"))
        incCounter(nextpn);
    }
  }
  
  /**
   * Rotina encarregue do processamento dos steps do tipo <code>while<code>
   * @param n   Nó XML que representa o passo a ser executado
   * @throws java.lang.boRuntimeException  Excepções sobre a execução dos steps ou de consulta do XML
   */
  private void makeWhile(Node n) throws boRuntimeException
  {
    ngtXMLHandler nxml = new ngtXMLHandler(n);
    String cond = nxml.getChildNodeText("condition", null);
    if(checkCondition(cond, n))
    {
      markPointer(n, "done");
      Node nextpn = findNextStep(n, true);
      if(nextpn != null)
      {
        if(markPointer(nextpn, "start"))
          incCounter(nextpn);
      }
    }
    else
    {
      finishedStep(n);
    }
  }
  
  /**
   * Rotina encarregue do processamento dos steps do tipo <code>foreach<code>
   * @param n   Nó XML que representa o passo a ser executado
   * @throws java.lang.boRuntimeException  Excepções sobre a execução dos steps ou de consulta do XML
   */
  private void makeForeach(Node n) throws boRuntimeException
  {
    ngtXMLHandler nxml = new ngtXMLHandler(n);
    String countn = nxml.getAttribute("count");
    Integer i = new Integer(countn);
    String varname = nxml.getChildNodeText("foreachvariable", null);
    if(varname != null)
    {
      
      long sboui = xwfHelper.getContextVar(new ngtXMLHandler(n), varname);
      boObject bovar = xwfm.getObject(sboui);
      long bovarT = bovar.getAttribute("value").getObject().getAttribute("type").getValueLong();
      long bovarC = bovar.getAttribute("value").getObject().getAttribute("maxoccurs").getValueLong();
      if(bovarT == 0 && bovarC > 1)
      {
        markPointer(n, "done");
        bridgeHandler b = bovar.getAttribute("value").getObject().getBridge("valueList");
        
        if(i.intValue() <= b.getRowCount())
        {
      
         getXwfEval(new ngtXMLHandler(n)).eval(xwfm, varname + ".moveTo("+ i.toString()+");");
            
          String cond = nxml.getChildNodeText("condition", null);
          if(checkCondition(cond, n))
          {
            Node nextpn = findNextStep(n, true);
            if(nextpn != null)
            {
              if(markPointer(nextpn, "start"))
                incCounter(nextpn);
            }
          }
        }
        else
          finishedStep(n);
      }
      else
        throw new boRuntimeException("xwfControlFlow", "makeForeach", new Exception("Error: "+MessageLocalizer.getMessage("WRONG_VARIABLE_TYPE_IN_FOREACH")));
    }
    else
      throw new boRuntimeException("xwfControlFlow", "makeForeach", new Exception("Error: "+MessageLocalizer.getMessage("VARIABLE_NAME_EMPTY_IN_FOREACH")));
  }
  
  /**
   * Rotina encarregue do processamento dos steps do tipo <code>if<code>
   * @param n   Nó XML que representa o passo a ser executado
   * @throws java.lang.boRuntimeException  Excepções sobre a execução dos steps ou de consulta do XML
   */
  private void makeIf(Node n) throws boRuntimeException
  {
    ngtXMLHandler nxml = new ngtXMLHandler(n);
    String cond = nxml.getChildNodeText("condition", null);
    ngtXMLHandler ntrue = null;
    if(checkCondition(cond, n))
    {
      ngtXMLHandler naws = nxml.getChildNode("answers");
      ntrue = naws.getChildNode("TRUE");
    }
    else
    {
      ngtXMLHandler naws = nxml.getChildNode("answers");
      ntrue = naws.getChildNode("FALSE");
    }
    if(ntrue == null)
    {
      finishedStep(n);
    }
    else
    {
      Node nextpn = findNextStep(ntrue.getNode(), true);
      if(nextpn != null)
      {
        if(markPointer(nextpn, "start"))
          incCounter(nextpn);
      }
    }
  }
  
  /**
   * Rotina encarregue do processamento dos steps do tipo <code>goto<code>
   * @param n   Nó XML que representa o passo a ser executado
   * @throws java.lang.boRuntimeException  Excepções sobre a execução dos steps ou de consulta do XML
   */
  private void makeGoto(Node n) throws boRuntimeException
  {
    ngtXMLHandler nxml = new ngtXMLHandler(n);
    String cond = nxml.getChildNodeText("condition", null);
    if(checkCondition(cond, n))
    {
      String label = nxml.getAttribute("label");
      if(label != null)
      {
        ngtXMLHandler nxmlgoto = xwfHelper.getContextNode(nxml, label);
        if(nxmlgoto != null)
        {
          if(markPointer(nxmlgoto.getNode(), "start"))
            incCounter(nxmlgoto.getNode());
        }
      }
      markPointer(n, "done");
      String unique_sid = nxml.getAttribute("unique_sid");
      NodeList nlgt = null;
      NodeList nllb = null;
      //verificar se a instrução goto e a label estão no mesmo fluxo
      try{
        nlgt = actual_xml.getDocument().selectNodes("//*[@unique_sid='"+unique_sid+"']/ancestor::thread");
        nllb = actual_xml.getDocument().selectNodes("//programlabel[@name='"+label+"']/ancestor::thread | //milestone[@name='"+label+"']/ancestor::thread");
      }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "makeGoto", e);}
      if((nlgt.getLength() == 0 && nllb.getLength() != 0) || (nlgt.getLength() != 0 && nllb.getLength() == 0) )
        throw new boRuntimeException("xwfControlFlow", "makeGoto", new Exception("Error: goto -> "+MessageLocalizer.getMessage("LABEL_ON_DIFFERENT_FLOWS")));
      else
        if(nlgt.getLength()!=0 && nllb.getLength() != 0)
          if(!nlgt.item(nlgt.getLength()-1).equals(nllb.item(nllb.getLength()-1)))
            throw new boRuntimeException("xwfControlFlow", "makeGoto", new Exception("Error: goto -> "+MessageLocalizer.getMessage("LABEL_ON_DIFFERENT_FLOWS")));
    
    }
    else
      finishedStep(n);
  }
  
  /**
   * Rotina encarregue do processamento dos steps do tipo <code>cancelActivities<code>
   * @param n   Nó XML que representa o passo a ser executado
   * @throws java.lang.boRuntimeException  Excepções sobre a execução dos steps ou de consulta do XML
   */
  private void makeCancel(Node n) throws boRuntimeException
  {
    ngtXMLHandler nxml = new ngtXMLHandler(n);
    String cond = nxml.getChildNodeText("condition", null);
    if(checkCondition(cond, n))
    {
      String addQL = " and (";
      ngtXMLHandler naws = nxml.getChildNode("activities");
      ngtXMLHandler[] ncases = naws.getChildNodes();
      for(int i=0; i<ncases.length; i++)
      {   
        nxml = ncases[i];
        String name = nxml.getAttribute("name", null);
        ngtXMLHandler nextpn = xwfHelper.getContextNode(nxml, name);
        
        if(nextpn != null && nextpn.getNode() != null)
        {
          String usid = nextpn.getAttribute("unique_sid", "");
          markPointer(nextpn.getNode(), "cancel");
          if(i != 0)
            addQL += " OR ";
          addQL += "unique_sid = '"+usid+"' ";
        }
      }
      if(ncases.length != 0)
      {
        addQL += ")";
        this.cancelOptionalActivity(addQL, false);
      }
    }
    finishedStep(n);
  }
  
  /**
   * Rotina encarregue do processamento dos steps do tipo <code>switch<code>
   * @param n   Nó XML que representa o passo a ser executado
   * @throws java.lang.boRuntimeException  Excepções sobre a execução dos steps ou de consulta do XML
   */
  private void makeSwitch(Node n) throws boRuntimeException
  {
    ngtXMLHandler nxml = new ngtXMLHandler(n);
    ngtXMLHandler naws = nxml.getChildNode("answers");
    ngtXMLHandler[] ncases = naws.getChildNodes();
    for(int i=0; i<ncases.length; i++)
    {   
      nxml = ncases[i];
      String cond = nxml.getChildNodeText("condition", null);
      if(checkCondition(cond, n))
      {
        Node nextpn = findNextStep(nxml.getNode(), true);
        if(nextpn != null)
        {
          if(markPointer(nextpn, "start"))
            incCounter(nextpn);
        }
        break;
      }
    }
  }
  
  /**
   * Rotina encarregue do processamento dos steps do tipo <code>exit<code>
   * @param n   Nó XML que representa o passo a ser executado
   * @throws java.lang.boRuntimeException  Excepções sobre a execução dos steps ou de consulta do XML
   */
  private void makeExit(Node n) throws boRuntimeException
  {
    markPointer(n, "done");
    Node nxt = n;
    do
    {
      nxt = nxt.getParentNode();
    }while(!(xwfHelper.STEP_SWITCH.equals(nxt.getNodeName()) || xwfHelper.STEP_CHOICE.equals(nxt.getNodeName()) || 
            xwfHelper.STEP_DECISION.equals(nxt.getNodeName()) || xwfHelper.STEP_FOR_EACH.equals(nxt.getNodeName()) ||
            xwfHelper.STEP_IF.equals(nxt.getNodeName()) || xwfHelper.STEP_THREAD.equals(nxt.getNodeName()) ||
            xwfHelper.STEP_WHILE.equals(nxt.getNodeName()) || xwfHelper.STEP_MENU.equals(nxt.getNodeName()) || "program".equals(nxt.getNodeName())));

    if(!"program".equals(nxt.getNodeName()))
      finishedStep(nxt);
    else
      finishFlow();
  }
  /**
   * Rotina encarregue do processamento dos steps do tipo <code>addProcedure<code>
   * @param n   Nó XML que representa o passo a ser executado
   * @throws java.lang.boRuntimeException  Excepções sobre a execução dos steps ou de consulta do XML
   */
  private void makeAddProc(Node n) throws boRuntimeException
  {
    
    ngtXMLHandler nxml = new ngtXMLHandler(n);
    ngtXMLHandler procs = nxml.getChildNode("procedures");
    if(procs != null)
    {
      String xmlProcs = xwfm.getProgram().getAttribute("procedures").getValueString();
      ngtXMLHandler nprocsRT =  null;
      if(xmlProcs != "")
        nprocsRT = new ngtXMLHandler(xmlProcs);
      else
        nprocsRT = new ngtXMLHandler("<procedures></procedures>");
    
      ngtXMLHandler[] vars = procs.getChildNodes();
      int i = 0;
      for(i=0; i<vars.length; i++)
      {
        String vname = vars[i].getAttribute("name");
        String vreq = vars[i].getAttribute("required", "no");
        Node foundN = null;
        try
        {
          foundN = nprocsRT.getDocument().selectSingleNode("//*[@name='"+vname+"']");
        }catch(Exception e){}
        if(foundN != null)
        {
         ((XMLElement)foundN).setAttribute("required", vreq);
        }
        else
        {
          Element procE = nprocsRT.getDocument().createElement("procedure");
          procE.setAttribute("name",vname);
          if(vreq != null)
          {
            procE.setAttribute("required",vreq);
          }
          
          ngtXMLHandler procDef = xwfHelper.getContextProcedure(vars[i], vname);
         
          procE.setAttribute("count", procDef.getAttribute("count", "0"));
          Node labelProc = procDef.getChildNode("label").getNode();
          labelProc = procE.getOwnerDocument().importNode(labelProc, true);
          procE.appendChild(labelProc);
          Node partProc = procDef.getChildNode("participants").getNode();
          partProc = procE.getOwnerDocument().importNode(partProc, true);
          procE.appendChild(partProc);
          
          nprocsRT.getFirstChild().getNode().appendChild(procE);
        }
        
      }
      
      if(i > 0)
      {
        String swrite = ngtXMLUtils.getXML(nprocsRT.getDocument());
        xwfm.getProgram().getAttribute("procedures").setValueString(swrite);
      }
    }
    
    finishedStep(n);
  }
  /**
   * Rotina encarregue do processamento dos steps do tipo <code>removeProcedures<code>
   * @param n   Nó XML que representa o passo a ser executado
   * @throws java.lang.boRuntimeException  Excepções sobre a execução dos steps ou de consulta do XML
   */
  private void makeRemProc(Node n) throws boRuntimeException
  {
    ngtXMLHandler nxml = new ngtXMLHandler(n);
    ngtXMLHandler procs = nxml.getChildNode("procedures");
    if(procs != null)
    {
      String xmlProcs = xwfm.getProgram().getAttribute("procedures").getValueString();
      ngtXMLHandler nprocsRT =  null;
      if(xmlProcs != "")
      {
        nprocsRT = new ngtXMLHandler(xmlProcs);
      
        ngtXMLHandler[] vars = procs.getChildNodes();
        int i = 0;
        for(i=0; i<vars.length; i++)
        {
          String vname = vars[i].getAttribute("name");
          Node foundN = null;
          try
          {
            foundN = nprocsRT.getDocument().selectSingleNode("//*[@name='"+vname+"']");
          }catch(Exception e){}
          if(foundN != null)
          {
            foundN.getParentNode().removeChild(foundN);
          }
        }
        
        if(i > 0)
        {
          nprocsRT.goDocumentElement();
          if(nprocsRT.getFirstChild() != null && nprocsRT.getFirstChild().getNode() != null)
          {
            String swrite = ngtXMLUtils.getXML(nprocsRT.getDocument());
            xwfm.getProgram().getAttribute("procedures").setValueString(swrite);
          }
          else
            xwfm.getProgram().getAttribute("procedures").setValueString("");
        }
      }
    }
    
    finishedStep(n);
  }
  /**
   * Rotina encarregue do processamento dos steps do tipo <code>callProgram<code>
   * @param n   Nó XML que representa o passo a ser executado
   * @throws java.lang.boRuntimeException  Excepções sobre a execução dos steps ou de consulta do XML
   */
  private boolean makeCallProgram(Node n) throws boRuntimeException
  {
    ngtXMLHandler nxml = new ngtXMLHandler(n);
    String mode = nxml.getAttribute("mode", xwfHelper.MODE_EMBEDDED);
    String pname = nxml.getAttribute("programName", null);
    long boui_def = 0;
    if(pname == null)
    {
      pname = nxml.getAttribute("programBoui", null);
      boui_def = Long.parseLong(pname);
    }
    boolean embebed;
    if(mode.equals(xwfHelper.MODE_EMBEDDED))
      embebed = true;
    else
      embebed = false;
    
    Hashtable t = new Hashtable();
    ngtXMLHandler mapVarsNode = nxml.getChildNode("mappingVariables");
    ngtXMLHandler[] tuplos = mapVarsNode.getChildNodes();
    for(int i=0; i < tuplos.length; i++)
    {
      String varm_name = tuplos[i].getAttribute("mainVar");
      String varp_name = tuplos[i].getAttribute("progVar");
            
      long sboui = xwfHelper.getContextVar(new ngtXMLHandler(n), varm_name);
      boObject bovar = xwfm.getObject(sboui);
      String linkType = tuplos[i].getAttribute("hardLink", "N");
      if(!embebed && "N".equals(linkType))
      {
        Object value = getVarObject(bovar);
        if(value !=null)
          t.put(varp_name, value);
      }
      else
      {
        t.put(varp_name, bovar.getAttribute("value").getObject());
      }
    }
    mapVarsNode = nxml.getChildNode("mappingParticipants");
    tuplos = mapVarsNode.getChildNodes();
    for(int i=0; i < tuplos.length; i++)
    {
      String varm_name = tuplos[i].getAttribute("mainPar");
      String varp_name = tuplos[i].getAttribute("progPar");
      
      long sboui = xwfHelper.getContextPar(new ngtXMLHandler(n), varm_name);
      boObject bovar = xwfm.getObject(sboui);
      String linkType = tuplos[i].getAttribute("hardLink", "N");
      if(!embebed && "N".equals(linkType))
      {
        Object value = getVarObject(bovar);
        if(value !=null)
          t.put(varp_name, value);
      }
      else
      {
        t.put(varp_name, bovar.getAttribute("value").getObject());//varValue
      }
    }
    
    if(!embebed)//outsource
    {
      if(pname!=null)
      {

        xwfManager launch = new xwfManager(xwfm.getDocHTML(), null);
        launch.getBoManager().setXwfManager(launch);
        long rt_boui = launch.createProgram(pname, t,xwfHelper.PROGRAM_EXEC_DEFAULT_MODE);
        launch.getBoManager().getProgram().getAttribute("returnToSid").setValueString(nxml.getAttribute("unique_sid", ""));
        launch.getBoManager().getProgram().getAttribute("returnToProg").setValueLong(xwfm.getProgBoui());
        launch.getBoManager().updateObject(launch.getBoManager().getProgram());
        launch.getBoManager().setXwfManager(null);
        launch.initProgram(rt_boui, t, 0);
      }
      return false;
    }
    else//mode embebed
    { 
      xwfManager launch = null;
      if(xwfm.getDocHTML() != null)
      {
        launch = new xwfManager(xwfm.getDocHTML(), null);
      }
      else
      {
        launch = new xwfManager(xwfm.getContext(), null);
      }
      String sub_flow = launch.getVersionFlow(pname);
      
      ngtXMLHandler docxml = new ngtXMLHandler(sub_flow);
      Node coden = nxml.getChildNode("code").getNode();
      Node root = docxml.getDocument().getDocumentElement();
      
      root = coden.getOwnerDocument().importNode(root, true);
      coden.appendChild(root);

      if(boui_def == 0)
      {
        boObject p = xwfm.loadObject("select xwfProgram where name='"+pname+"'");
        boui_def = xwfManager.findVersion(p);
      }
      else
        boui_def = xwfManager.findVersion(xwfm.getObject(boui_def));
        
      initVars(new ngtXMLHandler(root), t, 0, nxml.getAttribute("unique_sid"), boui_def);
      return false;
    }
  }
  /**
   * Rotina encarregue do processamento dos steps do tipo <code>callProcedure<code>
   * @param n   Nó XML que representa o passo a ser executado
   * @throws java.lang.boRuntimeException  Excepções sobre a execução dos steps ou de consulta do XML
   */
  private void makeCallProcedure(Node n) throws boRuntimeException
  {
    
    ngtXMLHandler nxml = new ngtXMLHandler(n);
    ngtXMLHandler proc = nxml.getChildNode("procedure");
    if(proc != null)
    {
        String vname = proc.getAttribute("name", null);
        ngtXMLHandler nprocsRT = prodNewXMLProcedure(vname, proc);
        this.runSubProg(xwfHelper.STEP_SUB_PROCEDURE, nxml.getAttribute("unique_sid", null), nprocsRT);
    }
  }
  
  /**
     * Constroi o nó XML com o procedimento invocado e incrementa a contagem das invocações dos métodos 
     * @return nó XML a adicionar ao fluxo
     * @param contextNode contexto de onde o procedimento foi invocado, <code>null</code> caso não tenha contexto
     * @param vname nome do método invocado
     */
  public ngtXMLHandler prodNewXMLProcedure(String vname, ngtXMLHandler contextNode)
  {
    ngtXMLHandler procDef =  null;
    if(contextNode == null || contextNode.getNode() == null)
      procDef = xwfHelper.getContextProcedure(this.getActualXml(), vname);
    else
      procDef = xwfHelper.getContextProcedure(contextNode, vname);
      
    if(procDef == null)
    {
        try
        {
            NodeList nl = getActualXml().getDocument().selectNodes("//*[@name='"+vname+"']");
            if(nl.getLength() > 0)
                procDef = new ngtXMLHandler(nl.item(0));
        }catch(Exception e){}
    }
    
  
    ngtXMLHandler nprocsRT = new ngtXMLHandler("<procedure></procedure>");
    
    nprocsRT.goDocumentElement();
    Node nproc_root = nprocsRT.getNode();
    this.incCounter(procDef.getNode());
    Node codeProc = procDef.getChildNode("code").getNode();
      codeProc = nproc_root.getOwnerDocument().importNode(codeProc, true);
    nproc_root.appendChild(codeProc);
      
      try
      {
        String xmlProcs = xwfm.getProgram().getAttribute("procedures").getValueString();
        ngtXMLHandler tempRt = new ngtXMLHandler(xmlProcs);
        Node rtNode = tempRt.getDocument().selectSingleNode("//*[@name='"+vname+"']");
        if(rtNode != null)
          incCounter(rtNode);
        String swrite = ngtXMLUtils.getXML(tempRt.getDocument());
        xwfm.getProgram().getAttribute("procedures").setValueString(swrite);
      }catch(Exception e){
        e = e;
      }

    return nprocsRT;
  }
  
  /**
   * Rotina encarregue de finalizar o controlo de fluxos, procedendo às actualizações e escritas na base de dados
   * @throws java.lang.boRuntimeException Excepções sobre a escrita na base de dados
   */
  private void closeControlFlow() throws boRuntimeException
  {
    boObject p = xwfm.getProgram();
    p.getAttribute("flow").setValueString(ngtXMLUtils.getXML(actual_xml.getDocument()));
    xwfm.updateObject(p);
  }
  
  /**
   * Tomará as medidas necessárias caso verifique que o programa chegou ao seu término.
   * <p>Cancelará as actividades optionais não inicializadas existentes.
   * 
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public void finishFlow()throws boRuntimeException
  {
    String boql = "select xwfActivity where program = "+Long.toString(xwfm.getProgram().getBoui())+" and runningState <> 90 and (optional=0 or (optional =1 and (runningState >=1 or sid = '-1')))";
    boObjectList obl = xwfm.listObject(boql, false );    
    if(xwfm.getMode() == xwfHelper.PROGRAM_EXEC_TEST_MODE)
    {
        obl.beforeFirst();
        while(obl.next())
        {
            boObject objl = obl.getObject();
           if(objl.getAttribute("program").getValueLong() != xwfm.getProgBoui()
            || objl.getStateAttribute("runningState").getValueString().equalsIgnoreCase("close")
            || "-1".equalsIgnoreCase(objl.getAttribute("unique_sid").getValueString())
            )
               obl.removeCurrent(); 
        }
    }
    if(obl == null || obl.getRowCount() == 0)
    {
      obl = xwfm.listObject("select xwfWait where program = "+Long.toString(xwfm.getProgram().getBoui())+" and done='0'", false );
      if((obl == null || obl.getRowCount() == 0) && !existsOpenPointers() && !existsRequiredProcNotExecuted())
      {
        xwfm.getProgram().getStateAttribute("runningState").setValueString("close");
        xwfm.getProgram().getAttribute("endDate").setValueDate(DateUtils.getNow());
        
        long retProgBoui = xwfm.getProgram().getAttribute("returnToProg").getValueLong();
        String retSid = xwfm.getProgram().getAttribute("returnToSid").getValueString();
        if(retProgBoui > 1 && retSid != null)
        {
          xwfManager launch = new xwfManager(xwfm.getDocHTML(), retProgBoui);
          launch.finishedStep(retSid);
        }
        xwfHelper.objectStateSet(xwfm, "1");
        cancelOptionalActivity(null, true);
      }
    }
  }
  
  
  
  /**
   * Função que procede ao cancelamento das actividades opcionais não inicializadas
   * @param addQL     xeo.ql adicinoal com os usids dos objectos a encerrar. Ex: and (unique_sid = xxx_xxx or unique_sid = yy_yyy)
   * @param xmlMark   define se será necessário marcar também no xml o cancelamento. <code>true</code> escreve, <code>false</code> não escreverá no xml
   * @throws netgest.bo.runtime.boRuntimeException
   */
  protected void cancelOptionalActivity(String addQL, boolean xmlMark)throws boRuntimeException
  {
    if(addQL == null)
      addQL = "";
    String cancel_boql = "select xwfActivity where program = "+Long.toString(xwfm.getProgram().getBoui())+
                          " and (optional =1 and runningState = 0 and sid != '-1' and CLASSNAME <> 'xwfActivityReceive' and CLASSNAME <> 'xwfCreateReceivedMessage' ) "+addQL;
    cancelActivity(cancel_boql, xmlMark);
  }
  
/**
   * Função que procede ao cancelamento de actividades
   * @param boql     boql q selecciona as actividades a apagar
   * @param xmlMark   define se será necessário marcar também no xml o cancelamento. <code>true</code> escreve, <code>false</code> não escreverá no xml
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public void cancelActivity(String boql, boolean xmlMark)throws boRuntimeException
  {    
    if(xwfm.getMode() != xwfHelper.PROGRAM_EXEC_TEST_MODE)
        interruptActivity("cancel", boql, xmlMark);
  }
  
  /**
   * Função que executa todo o trabalho de interromper uma actividade lançada, seja para cancelar ou não
   * @param status   estado em que a actividade irá ficar
   * @param boql     boql q selecciona as actividades a apagar
   * @param xmlMark   define se será necessário marcar também no xml o cancelamento. <code>true</code> escreve, <code>false</code> não escreverá no xml
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public void interruptActivity(String status, String boql, boolean xmlMark)throws boRuntimeException
  {
    boObjectList obl  = xwfm.listObject(boql, false );
    obl.beforeFirst();
    while(obl.next())
    {
      boObject actv=obl.getObject();
      actv.getStateAttribute("runningState").setValueString(status);
      String usid = actv.getAttribute("unique_sid").getValueString();
      if(xmlMark)
      {
        Node pn = null;
        try{
          if(actual_xml != null)
          {
            pn = actual_xml.getDocument().selectSingleNode("//*[@unique_sid='"+usid+"']");
          }
        }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "cancelActivity", e);}
        if(pn != null)
          if("close".equalsIgnoreCase(status))
            this.markPointer(pn, "done");
          else
            this.markPointer(pn, status);
      }
      boObjectList bol = xwfm.listObject("select xwfWait where unique_sid = '"+usid+"' and done='0'", false);
      bol.beforeFirst();
      while(bol.next())
      {
        bol.getObject().destroy();
      }
      xwfAnnounceImpl.removeAnnouncer(actv);
      xwfm.updateObject(actv);
    }
  }
  
  /**
   * Rotina simples que mediante o step dado classifica o seu tipo forncendo um número
   * @param n   Nó a analizar
   * @return    número inteiro que representa o seu tipo
   */
  private int stepType(Node n)
  {
    String name = n.getNodeName();
    if(xwfHelper.STEP_BEGIN_TIME.equals(name) || xwfHelper.STEP_STOP_TIME.equals(name) || xwfHelper.STEP_WAIT_TIME.equals(name) ||
       xwfHelper.STEP_ACTIVITY.equals(name) || xwfHelper.STEP_COPY.equals(name) || xwfHelper.STEP_FILL.equals(name) ||
       xwfHelper.STEP_XEP_CODE.equals(name) || xwfHelper.STEP_MILESTONE.equals(name) || xwfHelper.STEP_SEND.equals(name) ||
       xwfHelper.STEP_WAIT_RESPONSE.equals(name) || xwfHelper.STEP_DECISION.equals(name) || xwfHelper.STEP_CHOICE.equals(name) ||
       xwfHelper.STEP_WAIT_THREAD.equals(name) || xwfHelper.STEP_LABEL.equals(name) || xwfHelper.STEP_USER_CALL_PROG.equals(name) ||
       xwfHelper.STEP_MENU.equals(name) /*|| xwfHelper.STEP_POLL.equals(name)*/ || xwfHelper.STEP_CREATE_MSG.equals(name) )
      return 0;
    else
      if(xwfHelper.STEP_THREAD.equals(name) )
        return 3;
      else
        if(xwfHelper.STEP_GOTO.equals(name) )
          return 4;
        else
          if(xwfHelper.STEP_WHILE.equals(name) )
            return 5;
          else
            if(xwfHelper.STEP_FOR_EACH.equals(name))
              return 6;
            else
                if(xwfHelper.STEP_IF.equals(name))
                  return 8;
                else
                  if(xwfHelper.STEP_SWITCH.equals(name))
                    return 9;
                  else
                    if(xwfHelper.STEP_COMMENT.equals(name))
                      return 10;
                    else
                      if(xwfHelper.STEP_CALL_PROGRAM.equals(name))
                        return 11;
                      else
                        if(xwfHelper.STEP_CANCEL.equals(name))
                          return 12;
                        else
                          if(xwfHelper.STEP_EXIT.equals(name))
                            return 13;
                          else
                            if(xwfHelper.STEP_ADD_PROCEDURE.equals(name))
                              return 14;
                            else
                              if(xwfHelper.STEP_REM_PROCEDURE.equals(name))
                                return 15;
                              else
                              if(xwfHelper.STEP_REM_ALL_PROCEDURE.equals(name))
                                return 16;
                              else
                                if(xwfHelper.STEP_CALL_PROCEDURE.equals(name))
                                  return 17;
                                else
                                  if(xwfHelper.STEP_TERMINATE.equals(name))
                                    return 18;
                                  else
                                    if(getStepControlClass(name) != null)
                                        return -2;
                                    else
                                        return -1;
  }
  
  
  /**
   * Rotina privada que permite a edição do atributo XML <code>pointer</code> para um dos seus valores <code>start, open</code> ou <code>done</code>
   *
   * @param n   nó XML a referenciar
   * @param value valor a preencher (<code>start, open</code> ou <code>done</code>)
   * @throws java.lang.boRuntimeException  Excepções recorrentes da escrita no ficheiro XML
   */
    public boolean markPointer(Node n, String value) throws boRuntimeException
    {
        Attr att = actual_xml.getDocument().createAttribute("pointer");
        boolean errorflag = true;
        ngtXMLHandler ngtn = new ngtXMLHandler(n);
        String pval = ngtn.getAttribute("pointer");
    
        if(pval != null && value.equals("start") && pval.equals("open"))
        {
//            xwfm.getDocHTML().addErrorMessage("Error: Trying to Start an Open step!!!");  
            errorflag = false;
        }
        else if(pval != null && value.equals("open") && pval.equals("wait"))
        {
            //ir à base de dados mudar o estado da tarefa 
            boObjectList waitl = xwfm.listObject("select xwfActivity STATE wait WHERE unique_sid = '"+ngtn.getAttribute("unique_sid", "") +
                                              "' AND program = "+xwfm.getProgBoui(), false);
            if(!waitl.isEmpty())
            {
                waitl.beforeFirst();
                waitl.next();
                boObject a_wait = xwfm.getObject(waitl.getCurrentBoui());
                if(a_wait != null)
                {
                    if(a_wait.getAttribute("performer").getValueLong() > 0)
                    {
                        a_wait.getStateAttribute("runningState").setValue("open");
                    }   
                    else
                    {
                        a_wait.getStateAttribute("runningState").setValue("create");
                    }
                    xwfm.updateObject(a_wait);
                }
                else
                {
                    xwfm.getDocHTML().addErrorMessage("ERROR: Waiting activity not found!!!");  
                    errorflag = false;
                }  
            }
            else
            {
                xwfm.getDocHTML().addErrorMessage("ERROR: Waiting activity not found!!!");  
                errorflag = false;
            }
        }
          
        if(errorflag)
        {
            att.setValue(value);
            ((XMLElement)n).setAttributeNode(att);
        }
        return errorflag;
    }
  
  /**
   * Rotina privada que incrementa o atributo <code>count</code> do nó <code>n</code>
   * @param n  Nó XML a alterar
   */
  public void incCounter(Node n)
  {
    Node attc = n.getAttributes().getNamedItem("count");
    Attr att = n.getOwnerDocument().createAttribute("count");
    if(attc == null)
      att.setValue("1");
    else
    {
      String sval = attc.getNodeValue();
      Integer ival = new Integer(sval);
      ival = new Integer(ival.intValue()+1);
      att.setValue(ival.toString());
    }
    ((XMLElement)n).setAttributeNode(att);
  }
  
  /**
   * Rotina que permite verificar se determina condição para execução de qualquer passo é verdadeira ou falsa.
   * @param cond  String represemtante da condição em XEP code
   * @return      <code>true</code> caso a condição seja verdadeira, <code>false</code> caso contrário
   * @throws java.lang.boRuntimeException  Excepções ao nível da avaliação das expressões.
   */
  private boolean checkCondition(String cond, Node pointer) throws boRuntimeException
  {
    if(cond != null)
    {
//      xwfECMAparser p = new xwfECMAparser(prog,  boctx);
      Object reto = getXwfEval(new ngtXMLHandler(pointer)).eval(xwfm, cond+";");
      return ((Boolean)reto).booleanValue();
    }
    else
      return true;
  }
  
  /**
   * Através de um boObjecto do tipo xwfVariable devolverá o valor que contém numa objecto da classe Object
   * @param var   xwfVariable a analizar
   * @return      objecto da classe Object que contém o valor da variável.
   * @throws netgest.bo.runtime.boRuntimeException
   */
  private Object getVarObject(boObject var)throws boRuntimeException
  {
    boObject varVal = var.getAttribute("value").getObject();
    String stype = varVal.getAttribute("type").getValueString();
    int type = new Double(stype).intValue();
    switch(type)
    {
      case boDefAttribute.VALUE_BOOLEAN:
      {
//        String sval = varVal.getAttribute("valueBoolean").getValueString();
        String sval = xwfm.getValueString(varVal);
      /*  bsh.Primitive vp=null;
        if(sval.equals("1"))
          vp = new bsh.Primitive(true);
        else
          vp = new bsh.Primitive(false);
        return vp;*/
        return sval;
      }
      case boDefAttribute.VALUE_CHAR:
      {
//        String val = varVal.getAttribute("valueText").getValueString();
        String val = xwfm.getValueString(varVal);
        return val;
      }
      case boDefAttribute.VALUE_CLOB:
      {
//        String val = varVal.getAttribute("valueText").getValueString();
        String val = xwfm.getValueString(varVal);
        return val;
      }
      case boDefAttribute.VALUE_NUMBER:
      {
//        double val = varVal.getAttribute("valueNumber").getValueDouble();
        double val = ((Double)xwfm.getValueObject(varVal)).doubleValue();
        bsh.Primitive vp = new bsh.Primitive(val);
        return vp;
      }
      case boDefAttribute.VALUE_DATE:
      {
//        Date val = varVal.getAttribute("valueDate").getValueDate();
        Date val = ((Date)xwfm.getValueObject(varVal));
        return val;
      }
      case boDefAttribute.VALUE_DATETIME:
      {
//        Date val = varVal.getAttribute("valueDateTime").getValueDate();
        Date val = ((Date)xwfm.getValueObject(varVal));
        return val;
      }
      case boDefAttribute.VALUE_UNKNOWN:
      {
      /*  String card = varVal.getAttribute("maxoccurs").getValueString();
        if("1".equals(card))
        {
          boObject obVal = varVal.getAttribute("valueObject").getObject();         
          return obVal;
        }
        else
        {
          long[] list_bouis = varVal.getAttribute("valueList").getValuesLong();
          bridgeHandler blist = varVal.getBridge("valueList");
          String otype = null;
          
          boObject bcls = varVal.getAttribute("object").getObject();
          String clsname =  bcls.getAttribute("name").getValueString();
          otype = "List."+ clsname;
          boObjectList bol = xwfm.listObject(list_bouis, clsname);
          return bol;
        }*/
        return xwfm.getValueObject(varVal);
      }
    }
    return null;
  }
  
/**
   * Função que encontra um nó XML na estrutura do programa passando o respectivo usid 
   * @param usid    unique sid do passo a encontrar
   * @return    nó encontrado
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public ngtXMLHandler getNode(String usid)throws boRuntimeException
  {
    Node nb = null;
    try{
      if(usid != null && usid.trim().length() > 0)
      {
          nb = actual_xml.getDocument().selectSingleNode("//*[@unique_sid='"+usid+"']");
          if(nb == null)
          {
            ngtXMLHandler new_xml = refreshActualXml();
            nb = new_xml.getDocument().selectSingleNode("//*[@unique_sid='"+usid+"']");
            if(nb == null)
            {
              //actual_xml.getDocument().print(System.out);
              nb = nb;
            }
            else
              setActualXml(new_xml);
          }
      }
      else
      {
        return new ngtXMLHandler(nb);
      }
    }catch(Exception e){throw new boRuntimeException("xwfStepExec", "createActivity", e);}

    return new ngtXMLHandler(nb);
  }
  
  /**
   * Para cada programa existe um avaliador. Mediante um nó identifica o respectivo avaliador. 
   * Cache de evaluadores de expressões. Foi abandonada devido a relação benificio - possibilidade de erros
   * @param nxml  nó em que nos localizamos
   * @return      avaliador xwf respectivo
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public xwfECMAevaluator getXwfEval(ngtXMLHandler nxml) throws boRuntimeException
  {
    return new xwfECMAevaluator(xwfm, nxml.getNode());
/*
    if(nxml == null)
//      return (xwfECMAevaluator)evals.get("0");
      return (xwfECMAevaluator)getEvaluator("0");
    else
    {
      ngtXMLHandler nx = null;
      nx = new ngtXMLHandler(nxml);
      if(nx.getParentNode().getNode() == null)
        nx.goChildNode("program");
      while(nx.getNode() != null && !nx.getNodeName().equals("program"))
      {
        nx.goParentNode();
      }
      xwfECMAevaluator e = null;
      ngtXMLHandler nxpar = nx.getParentNode().getParentNode();
      if(nxpar == null || nxpar.getNode() == null)
      {
//        e = (xwfECMAevaluator)evals.get("0");
        e = (xwfECMAevaluator)getEvaluator("0");
        if(e == null)
        {
          e = new xwfECMAevaluator(xwfm, nxml.getNode());
//          evals.put("0", e);
          setEvaluator("0", e);
          return e;
        }
        else
        {
          e.refreshObjects(xwfm);
          return e;
        }
      }
      else
      {
        if(nxpar.getNodeName().equals("callProgram"))
        {
          String usid = nxpar.getAttribute("unique_sid", "");
//          e = (xwfECMAevaluator)evals.get(usid);
          e = (xwfECMAevaluator)getEvaluator(usid);
          if(e == null)
          {
            e = new xwfECMAevaluator(xwfm, nxml.getNode());
//            evals.put(usid, e);
            setEvaluator(usid, e);
            return e;
          }
          else
            e.refreshObjects(xwfm);
            return e;
        }
        else
          return null;
      }
    }*/
  }
  
  /**
   * Método que procede a limpeza da cache de evaluadores. 
   */
  protected void clearEvals()
  {
//    evals.clear();
    this.evaluators.clear();
  }
  
  /**
   * Devolve a chave da tabela de dispersão do avaliador respectivo do nó <code>nxml</code>
   * Foi abandonada devido a relação benificio - possibilidade de erros
   * @param nxml  nó em que nos localizamos
   * @return      String com o código de hash do avaliador
   * @throws netgest.bo.runtime.boRuntimeException
   */
  private String getXwfEvalKey(ngtXMLHandler nxml) throws boRuntimeException
  {
    return "1111";
  /*
    ngtXMLHandler nx = null;
    nx = new ngtXMLHandler(nxml);
    if(nxml == null)
      return null;
    else
    {
      while(!nx.getNodeName().equals("program") && nx != null)
      {
        nx.goParentNode();
      }
      xwfECMAevaluator e = null;
      ngtXMLHandler nxpar = nx.getParentNode().getParentNode();
      if(nxpar == null || nxpar.getNode() == null)
      {
        return "0";
      }
      else
      {
        if(nxpar.getNodeName().equals("callProgram"))
        {
          String usid = nxpar.getAttribute("unique_sid", "");
          return usid;
        }
        else
          return null;
      }
    }*/
  }
  
  /**
   * Procede a uma troca de sids para que não ocorram situações ambiguas quando existem referencias.
   * @param contentXML  estrutura XML a pesquisar e proceder a troca de sids   
   * @throws netgest.bo.runtime.boRuntimeException
   */
  private void sidSubst(ngtXMLHandler contentXML)
  {
    try{
      NodeList nl = contentXML.getDocument().selectNodes("//*[@refered_sid]");
      for(int i=0; i < nl.getLength(); i++)
      {
          String sid = nl.item(i).getAttributes().getNamedItem("refered_sid").getNodeValue();
          NodeList nl_sid = contentXML.getDocument().selectNodes("//*[@sid='"+sid+"'] | *[@runtime_sid='"+sid+"']");
          if(nl_sid.getLength() > 0)
          {
            String usid = xwfHelper.generateUID();
            for(int j=0; j < nl_sid.getLength(); j++)
            {
                xwfHelper.markAtt(nl_sid.item(j), "runtime_sid", usid);
            }
            nl.item(i).getAttributes().getNamedItem("refered_sid").setNodeValue(usid);
          }
      }
    }catch(Exception e){}
  }
  
 /**
   * Adiciona uma nova tag ao nó com o códgio <code>usid</code>
   * @param tagname     nome da nova tag
   * @param usid        unique_sid do nó pai, no qual se irá adicionar a nova tag
   * @param content     string com o xml a adicionar
   * @param mark        necessidade de marcar o novo nó com o apontador do tipo start. 
   * @return o nó correspondente ao usid passado     
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public ngtXMLHandler addCodeTag(String tagname, String usid, String content, boolean mark)throws boRuntimeException
  {
    return addCodeTag(tagname, usid, new ngtXMLHandler(content), mark);
  }
 /**
   * Adiciona uma nova tag ao nó com o códgio <code>usid</code>
   * @param tagname     nome da nova tag
   * @param usid        unique_sid do nó pai, no qual se irá adicionar a nova tag
   * @param contentXML  estrutura xml a adicionar
   * @param mark        necessidade de marcar o novo nó com o apontador do tipo start. 
   * @return o nó correspondente ao usid passado     
   * @throws netgest.bo.runtime.boRuntimeException
   */ 
  public ngtXMLHandler addCodeTag(String tagname, String usid, ngtXMLHandler contentXML, boolean mark)throws boRuntimeException
  {
//    ngtXMLHandler nxml = null;
    sidSubst(contentXML);
    /*
    if(usid == null || usid.equals("-1") || usid.equals("0"))
    {//caso não seja passado nenhum usid 
      String ct_sid = null;
      ngtXMLHandler ct_child = contentXML;
      while(ct_sid == null && ct_child != null && ct_child.getNode() != null)
      { 
        ct_sid = ct_child.getAttribute("sid");
        ct_child.goFirstChild();
      }
      //vamos tentar a definição do procedimento que tenha o sid do que foi passado. Comum na chamada de procedimentos
      if(ct_sid != null)
      {
        try{
            Node n_nxml = getActualXml().getDocument().selectSingleNode("//defProcedure/descendant::*[@sid='"+ct_sid+"']");
            if(n_nxml == null)
                nxml = new ngtXMLHandler(getActualXml().getDocument().getDocumentElement());
            else
            {
                nxml = new ngtXMLHandler(n_nxml);
                while(nxml != null && nxml.getNode() != null && !nxml.getNodeName().equals("program"))
                {
                    nxml.goParentNode(); 
                }
            }
        }catch(Exception e){nxml = new ngtXMLHandler(getActualXml().getDocument().getDocumentElement());}
      }
      else
        nxml = new ngtXMLHandler(getActualXml().getDocument().getDocumentElement());
      
      nxml.goChildNode("program");
      nxml.goChildNode("code");
    }
    else
      nxml = getNode(usid);
    */  
    ngtXMLHandler nxml = getCodePosition(usid,contentXML);
    // E se nxml vem a null ?
    XMLDocument doc = nxml.getDocument();
    Node ncode = doc.createElement(tagname);
    Node ncontent = contentXML.getDocument().getDocumentElement();
    Node ncont = ncode.getOwnerDocument().importNode(ncontent, true);
    ncode.appendChild(ncont);
    xwfHelper.markUSID(ncode);
    nxml.getNode().appendChild(ncode);

    
    if(mark)
    {
      ngtXMLHandler ngt_cont = new ngtXMLHandler(ncont);
      while(stepType(ngt_cont.getNode()) == -1)
        ngt_cont.goFirstChild();
      this.markPointer(ngt_cont.getNode(), "start");
    }

    return nxml;
  }
    /**
     * Para if(nxml == null) getCodePosition(null,contentXML);
     * @throws netgest.bo.runtime.boRuntimeException
     * @return 
     * @param contentXML
     * @param usid
     */
    private ngtXMLHandler getCodePosition( String usid, ngtXMLHandler contentXML) throws boRuntimeException
    {
        ngtXMLHandler nxml = null;
        if(usid == null || usid.equals("-1") || usid.equals("0"))
        {//caso não seja passado nenhum usid 
          String ct_sid = null;
          ngtXMLHandler ct_child = contentXML;
          while(ct_sid == null && ct_child != null && ct_child.getNode() != null)
          { 
            ct_sid = ct_child.getAttribute("sid");
            ct_child.goFirstChild();
          }
          //vamos tentar a definição do procedimento que tenha o sid do que foi passado. Comum na chamada de procedimentos
          if(ct_sid != null)
          {
            try{
                Node n_nxml = getActualXml().getDocument().selectSingleNode("//defProcedure/descendant::*[@sid='"+ct_sid+"']");
                if(n_nxml == null)
                {
                    nxml = new ngtXMLHandler(getActualXml().getDocument().getDocumentElement());
                }
                else
                {
                    nxml = new ngtXMLHandler(n_nxml);
                    while(nxml != null && nxml.getNode() != null && !nxml.getNodeName().equals("program"))
                    {
                        nxml.goParentNode(); 
                    }
                }
            }catch(Exception e){nxml = new ngtXMLHandler(getActualXml().getDocument().getDocumentElement());}
          }
          else
          {
            nxml = new ngtXMLHandler(getActualXml().getDocument().getDocumentElement());
          }
          
          nxml.goChildNode("program");
          nxml.goChildNode("code");      
        }
        else
        {
            nxml = getNode(usid);    
            if(nxml == null)
            {
                nxml = getCodePosition(null,contentXML);
            }
        }     
        return nxml;
    }  
  /**
   * Volta a ir buscar a estrutura XML ao objecto xwfProgramRuntime
   * @return estrutura XML com o fluxo actualizado
   */
  private ngtXMLHandler refreshActualXml()throws boRuntimeException
  {
    String xml = xwfm.getProgram().getAttribute("flow").getValueString();
    return new ngtXMLHandler(xml);           
  }
/** 
 * Responsável pelo lançamento de alertas das tarefas
 * @param ngt_node      nó com a definição do step no qual o alerta foi definido
 * @param usid          usid da actividade criada
 * @param alert_name    nome do alerta a criar
 */
  private void processAlert(ngtXMLHandler ngt_node, String usid, String alert_name)throws boRuntimeException
  {
    boObject user = null;
    boObject actv = null;
    Node nalert = null;
    boolean falseActv = false;
    try{
      nalert = getActualXml().getDocument().selectSingleNode("//alert[@name='"+alert_name+"']");
    }catch(Exception e){return;}
    if(nalert != null)
    {
      ngtXMLHandler ngt_alert = new ngtXMLHandler(nalert);
      String alert_desc = ngt_alert.getText();
      StringTokenizer st = new StringTokenizer(alert_desc, ";");      
      String time = st.nextToken();
      String unit = st.nextToken();
      String linear = st.nextToken();
      String after = st.nextToken();
      String what = st.nextToken();
      String forPar = st.nextToken();
      String procedure = st.nextToken();
      String msg = (String)st.nextToken();
      if(usid != null || usid.length() > 0)
      {
        String boql_usid = "select xwfActivity where unique_sid = '"+usid+"'";
        actv = xwfFunctions.getActivityByUsid("xwfActivity", usid, xwfm);
        if(actv.exists())
        {
          actv = xwfm.getObject(actv.getBoui());
          if(forPar.equals(xwfHelper.TAG_VALUE_ASSIGNED))
            user = actv.getAttribute("assignedQueue").getObject();
          else
          {
            boObject par = xwfm.getObject(xwfHelper.getContextPar(ngt_node, forPar));
            user = xwfm.getValueBoObject(par.getAttribute("value").getObject());
          }
        }
        else
        {
          boObject par = xwfm.getObject(xwfHelper.getContextPar(ngt_node, forPar));
          user = xwfm.getValueBoObject(par.getAttribute("value").getObject());
          actv = xwfm.getProgram();
          falseActv = true;
        }
      }
      else
      {
          boObject par = xwfm.getObject(xwfHelper.getContextPar(ngt_node, forPar));
          user = xwfm.getValueBoObject(par.getAttribute("value").getObject());
          actv = xwfm.getProgram();
          falseActv = true;
      }
      
      if(user != null && actv != null)
      {
        if(what.equals(xwfHelper.TAG_VALUE_ALERT))
        {
          xwfAlertManager.sendAlert(msg, user, actv, xwfm);
            
//          xwfm.updateObject(actv);
        }
        else if(what.equals(xwfHelper.TAG_VALUE_REASSIGN) && (usid != null || usid.length() > 0))
        {
            boObject reass = xwfm.createObject("xwfReassign");
            reass.getAttribute("from").setObject(actv.getAttribute("assignedQueue").getObject());
            reass.getAttribute("to").setObject(user);
            reass.getAttribute("description").setValueString(msg);
            xwfAlertManager.reassignTask(reass, actv);
            actv.getEboContext().getBoSession().setProperty("settingReassign", Boolean.TRUE);
            xwfm.updateObject(reass);
            xwfm.updateObject(actv);
            actv.getEboContext().getBoSession().removeProperty("settingReassign");
            xwfAlertManager.sendAlert(msg, user, actv, xwfm);
        }
        else if(what.equals(xwfHelper.TAG_VALUE_TRANSFER) && (usid != null || usid.length() > 0))
        {
            boObject transf = xwfm.createObject("xwfActivityTransfer");
            transf.getAttribute("assignedQueue").setObject(actv.getAttribute("assignedQueue").getObject());
            xwfActionHelper.transferProcess(transf);
            xwfm.updateObject(transf);
            xwfm.updateObject(actv);
            xwfAlertManager.sendAlert(msg, user, transf, xwfm);
        }
        if(procedure != null && procedure.length() > 0)
        {
            ngtXMLHandler nprocsRT = prodNewXMLProcedure(procedure, getNode(usid));
            this.runSubProg(xwfHelper.STEP_SUB_PROCEDURE, usid, nprocsRT);
        }
      }
    }
    Node node = ngt_node.getNode();
    int i;
    ngtXMLHandler[] ngt_alerts = ngt_node.getChildNode("alerts").getChildNodes();
    for(i=0; i < ngt_alerts.length; i++)
    {
      if(ngt_alerts[i].getText().equals(alert_name))
        break;
    }
    ngtXMLHandler ngt_alert=null;
    if(i+1 < ngt_alerts.length)
     ngt_alert = ngt_alerts[i+1];
    
    if(ngt_alert != null && ngt_alert.getNode() != null)
    {
      if(actv == null)
      {
//        actv = xwfm.loadObject("Select xwfActivity where unique_sid = '"+usid+"'");
        actv = xwfFunctions.getActivityByUsid("xwfActivity", usid, xwfm);
        actv = xwfm.getObject(actv.getBoui());
      }
      xwfStepExec se = new xwfStepExec(xwfm, node, null, this);
      if(falseActv)
      {
        String durs = new ngtXMLHandler(node).getChildNodeText("deadLineDate", null);
        Date deadLine = se.calculateDeadLine(durs, user);
        se.regAlert(ngt_alert.getText(), deadLine, user, usid );
      }
      else
      {
          
          se.regAlert(ngt_alert.getText(), actv.getAttribute("deadLineDate").getValueDate(), 
                      actv.getAttribute("assignedQueue").getObject(), usid );
      }
    }
  }
  
  
  /**
     * Indicará qual a posição que o executante da tarefa ocupa na hierarqui de aprovação 
     * @return posção na hierarquia de aprovação
     * @param ass_q pessoa ou grupo a procurar
     * @param bh    bridge que contém a hierarquia
     * @return posição do executante da tarefa na bridge
     */
  protected int locateApprovalRole(bridgeHandler bh, boObject ass_q)
  {
      bridgeHandler bh_roles = ass_q.getBridge("roles");
      bridgeHandler bh_queues = ass_q.getBridge("queues");
      bridgeHandler bh_groups = ass_q.getBridge("groups");

      int i = bh.getRowCount();
      long rol_boui = -1;
      long perf = ass_q.getBoui();
      for(;i > 0; i--)
      {
          bh.moveTo(i);
          rol_boui = bh.getCurrentBoui();
          if(perf == rol_boui || bh_roles.haveBoui(rol_boui) || bh_queues.haveBoui(rol_boui)  
            || bh_groups.haveBoui(rol_boui)  )
          {
              break; 
          }     
      }
      return i;
  }
  
  /**
     * Procederá ao tratamento de uma aprovação quando esta for requerida  
     * @throws netgest.bo.runtime.boRuntimeException    Excepções ao nível do tratamento de obejctos
     * @param option    ultima opção escolhida
     * @param pn        nó da decisão que ira está a ser aprovada
     * @param perf      boui do ultimo utilizador a tomar uma posição perante a aprovação
     * @param approval_boui boui da definição da aprovação que rege este caso
     */
  public void makeApproval(long approval_boui, long perf, Node pn, String option) throws boRuntimeException
  {
      boObject app_bo = xwfm.getObject(approval_boui);
      boObject ass_q = xwfm.getObject(perf);
      bridgeHandler bh = app_bo.getBridge("roles");
      
      int i = locateApprovalRole(bh, ass_q);
      if(i >= 0)
      {
        ngtXMLHandler npn = new ngtXMLHandler(pn);
        ngtXMLHandler anw = npn.getChildNode("answers");
        ngtXMLHandler noption = null;
        try
        {
            int pos = Integer.parseInt(option);
            ngtXMLHandler[] options = anw.getChildNodes();
            noption = options[pos];
        }catch(Exception e)
        {
            noption = anw.getChildNode(option);
        }
        Node clo_pn = pn.cloneNode(true);
        if(bh.moveTo(i+1))
        {
            markPointer(pn, "done");
            markPointer(noption.getNode(), "done");
            incCounter(noption.getNode());           
            setNewNodeApproval(noption.getNode(), clo_pn, String.valueOf(bh.getCurrentBoui()));
        }
        else
            finishedStep(pn, option);
      }
      else
        markPointer(pn, "done");
  }
  /**
     * Acupla o nó clonado ao nó original por forma para marcar a hierarquia num sistema de aprovações hierarquia
     * @param perf_boui boui do participante que irá receber a tarefa 
     * @param clone     novo nó XML, réplica do original
     * @param origin    nó XML que da última decisão tomada nesta aprovação 
     */
  private void setNewNodeApproval(Node origin, Node clone, String perf_boui)
  {
        ngtXMLHandler norigin = new ngtXMLHandler(origin);
        ngtXMLHandler n_code = norigin.getChildNode("code");
        ngtXMLHandler[] opt_child = n_code.getChildNodes();
        int code_pos = -1;
        for(int j = 0; j < opt_child.length; j++)
        {
            n_code.getNode().removeChild(opt_child[j].getNode());
        }
      ngtXMLHandler nclone = new ngtXMLHandler(clone);
      clone.getAttributes().getNamedItem("pointer").setNodeValue("start");
      Node ac_usid = clone.getAttributes().removeNamedItem("unique_sid");
      ac_usid = clone.getAttributes().removeNamedItem("count");
      Node part = nclone.getChildNode("participant").getNode();
      part.getAttributes().getNamedItem("name").setNodeValue("");;
      Attr boui = part.getOwnerDocument().createAttribute("boui");
      boui.setValue(perf_boui);
      ((XMLElement)part).setAttributeNode(boui);
      this.incCounter(clone);
      n_code.getNode().appendChild(clone);
      
  }
/**
     * Cache de evaluadores abandonada. Permite a introdução na cache de mais um evaluador  
     * @param value     xwfECMAevaluator a adicionar
     * @param key       chave da tabela
     */
  private void setEvaluator(Object key,Object value)
  {
      this.evaluators.put(key,value);
  }
  /**
     * Cache de evaluadores abandonada. Permite a obtenção de um evaluador previamente guardado
     * @return      xwfECMAevaluator guardado
     * @param key   chave da tabela
     */
  private Object getEvaluator(Object key)
  {
      return this.evaluators.get(key);
  }  
  /**
     * Cache de evaluadores abandonada. Remove um evaluador previamente guardado
     * @param key   chave da tabela
     */
  private void removeEvaluator(Object key)
  {
      this.evaluators.remove(key);
  }
  //Gestão dos StepControllers 
  /**
     * Devolve a class que gere o step com o nome <code>name</code>
     * @return      Class que gere o step dado
     * @param name  nome do step
     */
  public Class getStepControlClass(String name)
  {
      if(step_ctrls == null)
        populateStepCtrls();
      return (Class)step_ctrls.get(name);
  }
  /**
     * Devolve o StepControl respectivo para gerir o step com unique_sid fornecido
     * @throws netgest.bo.runtime.boRuntimeException Excepções relacionadas com a manipulação do XML
     * @return      StepControl respectivo
     * @param usid  unique_sid do step a gerir
     */
  public StepControl getStepControl(String usid) throws boRuntimeException
  {
    if(usid == null || usid.equalsIgnoreCase("-1") || usid.startsWith("0_"))
        return null;
    ngtXMLHandler ngtn = getNode(usid);
    if(ngtn.getNode() != null)
        return getStepControl(ngtn.getNode());
    else
        return null;
  }
  /**
     * Devolve o StepControl respectivo para gerir o step fornecido
     * @throws netgest.bo.runtime.boRuntimeException    Excepções relacionadas com a manipulação do XML
     * @return      StepControl respectivo
     * @param n     nó XML do step a gerir
     */
  public StepControl getStepControl(Node n) throws boRuntimeException
  {
    Class stc_class = getStepControlClass(n.getNodeName());
    StepControl se = null;
    if(stc_class != null)
    {
        try{
            Class[] args_class = {Class.forName("netgest.xwf.common.xwfBoManager"),
                            Class.forName("org.w3c.dom.Node"), 
                            Class.forName("netgest.xwf.core.xwfECMAevaluator"),
                            Class.forName("netgest.xwf.core.xwfControlFlow")
                            };
            Constructor stc_const = stc_class.getConstructor(args_class);
            Object[] args_obj = {xwfm, n, getXwfEval(new ngtXMLHandler(n)), this};
            se = (StepControl)stc_const.newInstance(args_obj); 
        }catch(Exception e){}
    }
    else
        if(stepType(n) >= 0)
        {
            se = new xwfBasicStepController(xwfm, n, getXwfEval(new ngtXMLHandler(n)), this);
        }
    return se;
  }
  
  /**
   * Rotina de inciação da correpondencia entre os tipos de steps e as classes que os gerem
   */
  private void populateStepCtrls()
  {
      step_ctrls = new Hashtable();
      try
      {
        step_ctrls.put(xwfHelper.STEP_POLL, Class.forName("netgest.xwf.stepControllers.xwfPollController"));
      }catch(Exception e){}
  }
  /**
     * Verifica se o utilizador tem direitos de aceder ao procedimento passado como argumento 
     * @return <code>true</code> caso o utilizador tenha acesso ao procedimento, <code>false</code> em caso contrário 
     * @param activity  actividade corrente
     * @param perfBoui  boui do utilizador corrente
     * @param procName  nome do procedimento a verificar acesso
     */
  public boolean hasAccessToProcedure(String procName, long perfBoui, boObject activity)
  {
        try
        {
            
            String usid = activity != null ? activity.getAttribute("unique_sid").getValueString():null;
            ngtXMLHandler procDef = null;
            if(usid != null && !"-1".equals(usid) && !usid.startsWith("0"))
            {
                try
                {
                    procDef = xwfHelper.getContextProcedure(getNode(usid), procName);
                }
                catch (Exception e)
                {
                    //ignore
                }
            }
            else
            {
                try
                {
                    procDef = xwfHelper.getContextProcedure(this.getActualXml(), procName);
                }
                catch (Exception e)
                {
                    //ignore
                }
            }
            if(procDef == null)
            {
                try
                {
                    NodeList nlist = this.getActualXml().getDocument().selectNodes("//defProcedure[@name='"+procName+"']");
                    if(nlist.getLength() > 0)
                    {
                        procDef = new ngtXMLHandler(nlist.item(0));
                    }
                }
                catch (Exception e)
                {
                    //ignore
                }
            }
            
            if(procDef == null) return false;
            
            ngtXMLHandler partProc = procDef.getChildNode("participants");
            ngtXMLHandler[] nList = partProc.getChildNodes();
            String partName = null;
            long parBoui;
            for (int i = 0; i < nList.length; i++) 
            {
                partName = nList[i].getAttribute("name");
                if(partName != null)
                {
                    parBoui = xwfHelper.getContextPar(this.getActualXml(), partName);
                    boObject varPar = getBoManager().getObject(parBoui).getAttribute("value").getObject();
                    if(varPar != null && getBoManager().getValueBoObject(varPar) != null)
                    {
                        if(xwfHelper.verifyParticipant(getBoManager().getObject(perfBoui),getBoManager().getValueBoObject(varPar)))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.finer(LoggerMessageLocalizer.getMessage("TREATED_ERROR")+":",e);
            return false;
        }
        return false;
  }
  
  private String getLastUniqueSid() throws boRuntimeException
  {
    boObject prog = xwfm.getProgram();
    
    String xml = prog.getAttribute("flow").getValueString();
    if(xml == null || xml.length() < 1)
    {
        //senão tiver flow não há nada a fazer
        return null;
    }
    else
    {
        boObjectList l = xwfm.listObject("select xwfActivity where program = " + xwfm.getProgBoui() + " and endDate is not null and unique_sid is not null and unique_sid <> '-1' order by endDate desc", false, false);
        l.beforeFirst();
        if(l.next())
        {
            return l.getObject().getAttribute("unique_sid").getValueString();
        }
    }
    return null;
  }
}