/*Enconding=UTF-8*/
package netgest.xwf.core;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import netgest.bo.controller.xwf.*;
import netgest.bo.data.DataManager;
import netgest.bo.data.DataResultSet;
import netgest.bo.data.DataSet;
import netgest.bo.impl.Ebo_TemplateImpl;
import netgest.xwf.StepControl;
import netgest.xwf.stepControllers.xwfPollController;
import netgest.xwf.common.*;
import java.util.*;
import netgest.bo.runtime.*;
import netgest.bo.security.*;
import netgest.utils.*;
import netgest.bo.def.*;
import netgest.bo.dochtml.*;
import netgest.bo.utils.*;

import netgest.utils.IOUtils;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;
import netgest.io.*;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XSLException;

import netgest.bo.system.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.regex.*;

/**
 * Classe responsável pelo tratamento dos passos que não estão directamente relacionados com o controlo de fluxos.
 * <p>Lançará as <code>activities</code> necessárias e executará os xepcodes necessários
 * @author Ricardo Andrade
 */
public class xwfStepExec
{
/**
 * Log de registo de eventos
 */
  private static Logger logger = Logger.getLogger("netgest.xwf.core.xwfStepExec");
/**
 * Nó a interpretar e gerir.
 */
  private Node step_node;
  /**
   * Avaliador de expressoes
   */
  private xwfECMAevaluator p;
  /**
   * Instancia do motor que invocou o gestão do step
   */
  private xwfControlFlow controlf;
  /**
   * boManager do programa
   */
  private xwfBoManager xwfm;
  
  /**
   * Construtor. Necessita de várias informações referentes ao contexto, programa, passo.
   * <br>Este construtor é usalmente chamado.
   * 
   * @param xbm         boManaer do programa
   * @param step        passo que irá ser executado
   * @param ecmaeval    objecto do tipos <code>xwfECMAevaluator</code> que tem sido usado para efectual avaliações
   * @param cf          objecto do tipos <code>xwfECMAControlFlow</code> que tem sido usado para controlar o fluxo
   * @throws java.lang.Exception  Excepções provocadas por falhas na leitura de objectos
   */
  public xwfStepExec(xwfBoManager xbm, Node step, xwfECMAevaluator ecmaeval, xwfControlFlow cf) throws boRuntimeException
  {

    step_node = step;
    p = ecmaeval;
    controlf = cf;
    xwfm = xbm;
/*    if(cf != null)
      xwfm.setInTest(true);
    else
      xwfm.setInTest(false);
*/
  }
  
  /**
   * Construtor. Necessita de várias informações referentes ao contexto, programa, passo.
   * <br>Este construtor é chamado quando não é necessário a referencia ao motor
   * 
   * @param xbm         boManager do programa
   * @param step        passo que irá ser executado
   * @param ecmaeval    objecto do tipos <code>xwfECMAevaluator</code> que tem sido usado para efectual avaliações
   * @throws java.lang.Exception  Excepções provocadas por falhas na leitura de objectos
   **/
  public xwfStepExec(xwfBoManager xbm, Node step, xwfECMAevaluator ecmaeval) throws boRuntimeException
  {

    step_node = step;
    p = ecmaeval;
    controlf = null;
    xwfm = xbm;
  }
  
  
  
  /**
   * Construtor. As informações necessárias são passadas no boManager para além do step e do motor
   * 
   * @param xbm         instancia do motor que invocou a gestão deste método
   * @param step        passo que irá ser executado
   * @throws java.lang.Exception  Excepções provocadas por falhas na leitura de objectos
   **/
  public xwfStepExec(xwfBoManager xbm, Node step) throws boRuntimeException
  {

    step_node = step;
    xwfm = xbm;
//    p = new xwfECMAevaluator(prog, bodoc);
    p = new xwfECMAevaluator(xwfm, step);    
  }
  
  /**
   * Rotina principal que está responsável pela execução do respectivo passo
   * @return <code>true</code> se a execução estiver sido realizada em modo sincrono ou <code>false</code> caso tenha sido realizada em modo assincrono
   * @throws java.lang.Exception Excepções ao nível da execução do passo
   */
  public boolean execStep() throws boRuntimeException
  {
    switch(stepType(step_node))
    {
      case 0://beginTime
      {
        ngtXMLHandler nxml = new ngtXMLHandler(step_node);
        String cond = nxml.getChildNodeText("condition", null);
        if(checkCondition(cond))
        {
          boObject tm = this.createWait();
          xwfm.updateObject(tm);
        } 
      break;
      }
      case 1://stopTime
      {
        ngtXMLHandler nxml = new ngtXMLHandler(step_node);
        String cond = nxml.getChildNodeText("condition", null);
        if(checkCondition(cond))
        {
          String name = nxml.getChildNodeText("timerVariable", "");
          String boql_wait = "select xwfWait where program = "+xwfm.getProgBoui()+" and name = '"+name+"' and done = '0'"; 
          boObject tm = xwfm.loadObject(boql_wait);
          if(tm != null && tm.exists())
          {
            tm.getAttribute("endTime").setValueDate(DateUtils.getNowMinutes());
            tm.getAttribute("done").setValueString("1");
            xwfm.updateObject(tm);
          }
          else
            if(xwfm.getMode() == xwfHelper.PROGRAM_EXEC_TEST_MODE)
            {
                boObjectList ll = xwfm.listObject(boql_wait);
                ll.beforeFirst();
                while(ll.next())
                {
                   boObject objl = ll.getObject();
                   if(objl.getAttribute("program").getValueLong() != xwfm.getProgBoui()
                    || !objl.getAttribute("name").getValueString().equalsIgnoreCase("name")
                    || !objl.getStateAttribute("done").getValueString().equalsIgnoreCase("0")
                    )
                    
                    {
                        tm = objl;
                        tm.getAttribute("endTime").setValueDate(DateUtils.getNowMinutes());
                        tm.getAttribute("done").setValueString("1");
                        xwfm.updateObject(tm);
                        break;
                    }
                }               
            }
          
        } 
      break;
      }
      case 2://waitTime
      {
        ngtXMLHandler nxml = new ngtXMLHandler(step_node);
        String cond = nxml.getChildNodeText("condition", null);
        if(checkCondition(cond))
        {
          boObject tm = this.createWait();
          xwfm.updateObject(tm);
        } 
        return false;
      }
      case 4://waitThread
      {
        return makeWaitThread();
      }
      case 6://activity
      {
        this.createActivity("create");
        return false;     
      }
      case 7://fillVariable
      {
        this.createActivity("create");
        return false;     
      }
      case 8://Decision
      {
        this.createActivity("create");
        return false;     
      }
      case 9://choice
      {
        this.createActivity("create");
        return false;     
      }
      case 15://menu
      {
        this.createActivity("create");
        return false;     
      }
      case 5://xepCode
      {
        ngtXMLHandler nxml = new ngtXMLHandler(step_node);
        String cond = nxml.getChildNodeText("condition", null);
        if(checkCondition(cond))
        {
        
          String code = nxml.getChildNodeText("codetorun", null);
//          xwfECMAparser p = new xwfECMAparser(prog, boctx);
          //p.toJava(code);
          this.getBoManager().updateObject(getBoManager().getProgram());
          p.eval(xwfm, code);
//          this.getBoManager().updateObject(getBoManager().getProgram());
          p.getVariables(xwfm, step_node, AttributeHandler.INPUT_FROM_USER);
        }
        break;
      }
      case 10://copy
      {
        ngtXMLHandler nxml = new ngtXMLHandler(step_node);
        String cond = nxml.getChildNodeText("condition", null);
        if(checkCondition(cond))
        {
          String varf = nxml.getChildNodeText("fromVariable", null);
          String vart = nxml.getChildNodeText("toVariable", null);
          
          String code = vart + " = " +varf + ";";
          p.eval(xwfm, code);
          p.getVariables(xwfm, step_node, AttributeHandler.INPUT_FROM_USER);
        }
        break;
      }
      case 11://milestone
      {
        //informar controlador de tempos sobre a passagem num milestone
        break;
      }
      case 12://programlabel
      {
        //informar controlador de tempos sobre a passagem numa label
        break;
      }
      case 13://send
      {
        ngtXMLHandler nxml = new ngtXMLHandler(step_node);
        String cond = nxml.getChildNodeText("condition", null);
        if(checkCondition(cond))
        {
          boObject tm = createActivity("create");
          return false;
        }
        break;
      }
      case 17://create msg
      {
        ngtXMLHandler nxml = new ngtXMLHandler(step_node);
        String cond = nxml.getChildNodeText("condition", null);
        if(checkCondition(cond))
        {
          boObject tm = createActivity("create");
          return false;
        }
        break;
      }
      case 3://waitResponse
      {
        ngtXMLHandler nxml = new ngtXMLHandler(step_node);
        String cond = nxml.getChildNodeText("condition", null);
        if(checkCondition(cond))
        {
          boObject tm = createActivity("create");
          return false;
        }
        break;
      }
      case 14://usercallProgram
      {
        ngtXMLHandler nxml = new ngtXMLHandler(step_node);
        String cond = nxml.getChildNodeText("condition", null);
        if(cond==null || checkCondition(cond))
        {
          boObject tm = createActivity("create");
          return false;
        }
        break;
      }
        
    }
    return true;
  }
  
  private boolean makeWaitThread() throws boRuntimeException
  {
        boolean flag = true;
        ngtXMLHandler nxml = new ngtXMLHandler(step_node);
        nxml = this.contextReplacement(nxml);
        String cond = nxml.getChildNodeText("condition", null);
        if(checkCondition(cond))
        {
          boObject tm = this.createWait();
          String ths="";
          String lop = nxml.getChildNodeText("logicalOperator", "AND");
          if("OR".equalsIgnoreCase(lop))
              flag = false; 
          ngtXMLHandler[] names_th = nxml.getChildNode("threads").getChildNodes();
          for(int i=0; i < names_th.length; i++)
          {
            try
            {              
              String nameth=names_th[i].getAttribute("name");
              if(nameth != null)
              {
                
                ngtXMLHandler nodeth = xwfHelper.getContextNode(new ngtXMLHandler(step_node), nameth);//new ngtXMLHandler(nxml.getDocument().selectSingleNode("//*[@name='"+nameth+"']"));
                if(nodeth != null)
                {
                  ths += nameth + "#";
                  if(!"done".equalsIgnoreCase(nodeth.getAttribute("pointer")))              
                  {
                    if("AND".equalsIgnoreCase(lop))
                      flag = false; 
                  }
                  else
                    if("OR".equalsIgnoreCase(lop))
                      flag = true; 
                }
              }
            }catch(Exception e){throw new boRuntimeException("xwfStepExec", "execStep", e);}
          }
          tm.getAttribute("waitFor").setValueString(ths);
          if(!flag)
          {
            tm.getAttribute("done").setValueString("0");
          }
          else
            tm.getAttribute("done").setValueString("1");
          
          xwfm.updateObject(tm);
        } 
        return flag;
  }

  private void makeSend(ngtXMLHandler nxml, boObject actv) throws boRuntimeException
  {
    
//    ngtXMLHandler nxml = new ngtXMLHandler(step_node);
    String value_xml = nxml.getChildNodeText("channel","");
    String rtsid = nxml.getChildNode("message").getAttribute("refered_sid");
    boObject msg = null;
    if(rtsid != null && rtsid.length() > 0)
    {
        String usid=null;
        try{
            NodeList nlsid = this.controlf.getActualXml().getDocument().selectNodes("//*[@runtime_sid='"+rtsid+"']");
            if(nlsid.getLength() != 1)
            {
                nlsid = this.controlf.getActualXml().getDocument().selectNodes("//*[@sid='"+rtsid+"']");
            }
            if(nlsid.getLength() == 1)
            {
                ngtXMLHandler nsid = new ngtXMLHandler(nlsid.item(0));
                usid = nsid.getAttribute("unique_sid");
                while(usid == null || usid.length() == 0)
                {
                    nsid = nsid.getParentNode();
                    usid = nsid.getAttribute("unique_sid");
                }
            }  
        }catch(Exception e){}
        if(usid != null)
        {
            boObject boMact = xwfFunctions.getActivityByUsid("xwfActivity", usid, xwfm);
            if(!"xwfActivity".equalsIgnoreCase(boMact.getAttribute("CLASSNAME").getValueString()))
            {
                boMact = xwfFunctions.getActivityByUsid(boMact.getAttribute("CLASSNAME").getValueString(), 
                            usid, xwfm);
            }
            boObject boMvar = boMact.getAttribute("message").getObject();
            msg = xwfm.getValueBoObject(boMvar.getAttribute("value").getObject());
            boObject var = atachMessageActivity(actv, msg);
            return;
        }
    }
   
        msg = xwfm.createObject("message");
    
        if(
          "Prefered".equalsIgnoreCase(value_xml) || 
           "".equals(value_xml) ||
           "Mail".equalsIgnoreCase(value_xml)           
        )
        {
            value_xml = "E-Mail";            
        }
            
        msg.getAttribute("preferedMedia").setValueString(value_xml);
        boObject var = atachMessageActivity(actv, msg);
        
        ngtXMLHandler msg_node = nxml.getChildNode("message");
        Node defn = null;
        if(msg_node != null)
        {
          String body_name = msg_node.getAttribute("name", null);
          var.getAttribute("name").setValueString(body_name); 
          templateApplication(var, msg_node);
        }
    

  
    String req_bool = nxml.getChildNodeText("requireDeliveryReceipt","false");
    if(req_bool.equals("true"))
      msg.getAttribute("requestDeliveryReceipt").setValueString("1");
    else
      msg.getAttribute("requestDeliveryReceipt").setValueString("0");
    req_bool = nxml.getChildNodeText("requireReadReceipt","false");
    if(req_bool.equals("true"))
      msg.getAttribute("requestReadReceipt").setValueString("1");
    else
      msg.getAttribute("requestReadReceipt").setValueString("0");
      
    value_xml = nxml.getChildNodeText("priority","1");
    msg.getAttribute("priority").setValueString(value_xml);

    
    
    value_xml = nxml.getChildNode("from").getAttribute("name");
    if(value_xml != null)
    {
      boObject par = xwfm.getObject(xwfHelper.getContextPar(new ngtXMLHandler(step_node),value_xml));
      boObject valO = par.getAttribute("value").getObject().getAttribute("valueObject").getObject();
      if(valO == null)
      {
        par = xwfm.getObject(xwfHelper.getContextPar(new ngtXMLHandler(step_node), xwfHelper.WF_ADMINISTRATOR));
        valO = par.getAttribute("value").getObject().getAttribute("valueObject").getObject();
      }
      msg.getAttribute("from").setValueLong(valO.getBoui());
    }
    
    fillToFileds("to", msg, nxml);
    fillToFileds("cc", msg, nxml);
    fillToFileds("bcc", msg, nxml);
    
    try{
        ngtXMLHandler natach = nxml.getChildNode("attachVars");
        ngtXMLHandler[] n_attachs = natach.getChildNodes();
        for(int i=0; i < n_attachs.length; i++)
        {
            long boui_var = xwfHelper.getContextVar(nxml, n_attachs[i].getAttribute("name"));
            boObject var_var = xwfm.getObject(boui_var);
            if(var_var != null)
            {
                var_var = var_var.getAttribute("value").getObject();
                if(var_var.getAttribute("type").getValueString().equals("0") || var_var.getAttribute("type").getValueString().equals("-1"))
                {
                    long max = var_var.getAttribute("maxoccurs").getValueLong();
                    if(max <= 1)
                    {
                        addAttach(msg, xwfm.getValueBoObject(var_var));
                    }
                    else
                    {
                        boObjectList bol = (boObjectList)xwfm.getValueObject(var_var);
                        bol.beforeFirst();
                        while(bol.next())
                        {
                            addAttach(msg, bol.getObject());
                        }
                    }
                }
            }
        }
    }catch(Exception e){logger.severe(e.getMessage());}
    
    
  }
  
  private void addAttach(boObject msg, boObject att) throws boRuntimeException
  {
      if("Ebo_Document".equals(att.getName()))
      {
          bridgeHandler bdoc = msg.getBridge("documents");
          if(bdoc != null && !bdoc.haveBoui(att.getBoui()))
            bdoc.add(att.getBoui());
      }
      else 
      {
          bridgeHandler bdoc = msg.getBridge("attachedObjects");
          if(bdoc != null && !bdoc.haveBoui(att.getBoui()))
            bdoc.add(att.getBoui());
      }
  }
  
  private void makeWaitResp(ngtXMLHandler nxml, boObject actv) throws boRuntimeException
  {
//    ngtXMLHandler nxml = new ngtXMLHandler(step_node);
    String value_xml = nxml.getChildNode("from").getAttribute("name");
/*    if(value_xml != null)
    {
      boObject par = xwfm.getObject(xwfHelper.getContextPar(new ngtXMLHandler(step_node),value_xml));
      boObject valO = xwfm.getValueBoObject(par.getAttribute("value").getObject());
//      long cont = valO.getAttribute("refObj").getValueLong();
      actv.getAttribute("waitFrom").setValueLong(valO.getBoui());
    }*/
    
    value_xml = nxml.getChildNodeText("sendID", null);
    if(value_xml != null)
    {
      ngtXMLHandler sendA = xwfHelper.getContextNode(new ngtXMLHandler(step_node), value_xml, true);
      String usid = sendA.getAttribute("unique_sid", null);
      if(usid != null)
      {
        String boql_usid ="select xwfActivitySend where unique_sid = '"+ usid+ "'";
//        boObject sendAct = xwfm.loadObject(boql_usid);
        boObject sendAct = xwfFunctions.getActivityByUsid("xwfActivitySend", usid, xwfm);
        if(sendAct != null)
        {
            actv.getAttribute("sendActivity").setObject(sendAct);
            boObject msg = xwfm.getValueBoObject(sendAct.getAttribute("message").getObject().getAttribute("value").getObject());
            bridgeHandler bref = msg.getBridge("toRef");
            bref.beforeFirst();
            if(bref.next())
            {
                actv.getAttribute("waitFrom").setValueLong(bref.getCurrentBoui());
            }
        }
      }
      else
        throw new boRuntimeException("xwfStepExec", "makeWaitResp", new Exception("Error: send message not found!!!"));
    }
    
    value_xml = nxml.getChildNodeText("responseMessageVariable", null);
    if(value_xml != null)
    {
      actv.getAttribute("saveOn").setValueLong(xwfHelper.getContextVar(new ngtXMLHandler(step_node), value_xml));
    }
    if(actv != null)
    {
        xwfFunctions.setIntelligentLabel(((XwfController)actv.getEboContext().getController()).getEngine(),actv);
    }
  }
  
  private void fillToFileds(String field, boObject message, ngtXMLHandler nxml)throws boRuntimeException
  {
    ngtXMLHandler[] tos = nxml.getChildNode(field).getChildNodes();
    for(int i=0; i < tos.length; i++)
    {
      String toname = tos[i].getAttribute("name", "");
      boObject par = xwfm.getObject(xwfHelper.getContextPar(new ngtXMLHandler(step_node), toname));
      boObject parval = par.getAttribute("value").getObject();
      String card = parval.getAttribute("maxoccurs").getValueString();
      if("1".equals(card))
      {
        boObject toPart = xwfm.getValueBoObject(parval);
        if(toPart != null)
            message.getBridge(field).add(toPart.getBoui());
      }
      else
      {
        bridgeHandler b = parval.getBridge("valueList");
        b.beforeFirst();
        while(b.next())
        {
          message.getBridge(field).add(b.getObject().getBoui());
        }
      }
    }
  }
  /**
   * Rotina que permite verificar se determina condição para execução de qualquer passo é verdadeira ou falsa.
   * @param cond  String represemtante da condição em XEP code
   * @return      <code>true</code> caso a condição seja verdadeira, <code>false</code> caso contrário
   * @throws java.lang.Exception  Excepções ao nível da avaliação das expressões.
   */
  private boolean checkCondition(String cond) throws boRuntimeException
  {
    if(cond != null)
    {
      Object reto = p.eval(xwfm, cond+";");
      return ((Boolean)reto).booleanValue();
    }
    else
      return true;
  }
  
  /**
   * Rotina simples que mediante o step dado classifica o seu tipo forncendo um número
   * @param n   Nó a analizar
   * @return    número inteiro que representa o seu tipo
   */
  public static int stepType(Node n)
  {
    String name = n.getNodeName();
    if(xwfHelper.STEP_BEGIN_TIME.equals(name))
      return 0;
    else
      if(xwfHelper.STEP_STOP_TIME.equals(name) )
        return 1;
      else
        if(xwfHelper.STEP_WAIT_TIME.equals(name))
          return 2;
        else
          if(xwfHelper.STEP_WAIT_RESPONSE.equals(name) )
            return 3;
          else
            if(xwfHelper.STEP_WAIT_THREAD.equals(name) )
              return 4;
            else
              if(xwfHelper.STEP_XEP_CODE.equals(name) )
                return 5;
              else
                if(xwfHelper.STEP_ACTIVITY.equals(name) )
                  return 6;
                else
                  if(xwfHelper.STEP_FILL.equals(name) )
                    return 7;
                  else
                    if(xwfHelper.STEP_DECISION.equals(name) )
                      return 8;
                    else
                      if(xwfHelper.STEP_CHOICE.equals(name) )
                        return 9;
                      else
                        if(xwfHelper.STEP_COPY.equals(name) )
                          return 10;
                        else
                          if(xwfHelper.STEP_MILESTONE.equals(name) )
                            return 11;
                          else
                            if(xwfHelper.STEP_LABEL.equals(name) )
                              return 12;
                            else
                              if(xwfHelper.STEP_SEND.equals(name) )
                                return 13;
                              else
                                if(xwfHelper.STEP_USER_CALL_PROG.equals(name) )
                                  return 14;
                                else  
                                  if(xwfHelper.STEP_MENU.equals(name) )
                                    return 15;
                                  else  
                             /*         if(xwfHelper.STEP_POLL.equals(name) )
                                        return 16;
                                      else  */
                                         if(xwfHelper.STEP_CREATE_MSG.equals(name) )
                                            return 17;
                                          else 
                                            return -1;
  }
  
  /**
   * Rotina genéria que pegando em determinado nó preenche o respectivo atributo no objecto xwfVariable correspondente
   * @param node  nó XML que contém o atributo a mapear
   * @param var   objecto xwfVariable que conterá o atributo mapeado
   * @throws netgest.bo.runtime.boRuntimeException
   */
  private void setAttVar(ngtXMLHandler node, boObject var)throws boRuntimeException
  {
    String name = node.getNodeName();
    String val = node.getText();
    if(val != null)
    {
      if("mode".equalsIgnoreCase(name))
        if("read".equalsIgnoreCase(val))
          var.getAttribute("mode").setValueLong(0);
        else
          var.getAttribute("mode").setValueLong(1);
      else
        if("showMode".equalsIgnoreCase(name))
          if("viewObject".equalsIgnoreCase(val))
            var.getAttribute("showMode").setValueLong(0);
          else
            if("viewAsLov".equalsIgnoreCase(val))
              var.getAttribute("showMode").setValueLong(2);
            else
              var.getAttribute("showMode").setValueLong(1);
        else
          if("validDB".equalsIgnoreCase(name) || "validBusiness".equalsIgnoreCase(name) || "required".equalsIgnoreCase(name))
            if("Y".equalsIgnoreCase(val))
              var.getAttribute(name).setValueString("1");
            else
              var.getAttribute(name).setValueString("0");
          else
            var.getAttribute(name).setValueString(val);
    }
    else
      if(name.indexOf("Methods") > 0)
      {
         XMLDocument xdoc = new XMLDocument();
//         xdoc.importNode()
//         xdoc.adoptNode(node.getNode());
//         xdoc.appendChild(node.getNode());
         xdoc.appendChild(xdoc.importNode(node.getNode(), true));
         ByteArrayOutputStream xmlOS = new ByteArrayOutputStream();
         try{
          xdoc.print(xmlOS,"UTF-8");
          var.getAttribute(name).setValueString(xmlOS.toString("UTF-8"));
         }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "mapVars", e);}         
      }
      else
        if("objectfilter".equalsIgnoreCase(name))
        {
            val = node.getChildNodeText("xeoql", null);
            if(val != null)    
              var.getAttribute("objectFilterQuery").setValueString(val);
        }
  }
  

  
  /**
   * Rotina nuclear de criação de uma actividade com base no passo a ser executado.
   * 
   * @param type    tipo de objecto a ser criado (xwfActivity, xwfActivityFill, xwfActivityChoice, xwfActivityDecision)
   * @param state   estado com que será inicializada (create ou wait)
   * @return        objecto correspondente a actividade criada
   * @throws netgest.bo.runtime.boRuntimeException
   */
    public boObject createActivity(ngtXMLHandler nxml, String type, String state) throws boRuntimeException
  {
    boObject b = xwfm.createObject(type);
    b.getStateAttribute("runningState").setValue(state);
    b.getAttribute("program").setValueLong(this.xwfm.getProgBoui());
    b.getAttribute("done").setValueString("0");
       
//    ngtXMLHandler nxml = new ngtXMLHandler(step_node);
    
    String name = nxml.getChildNodeText("label", nxml.getNodeName());
    b.getAttribute("label").setValueString(name,AttributeHandler.INPUT_FROM_INTERNAL);
    name = nxml.getAttribute("name", nxml.getNodeName());
    b.getAttribute("name").setValueString(name);
    name = nxml.getChildNodeText("description", "");
    b.getAttribute("description").setValueString(name);
    name = nxml.getChildNodeText("priority", "1");
    b.getAttribute("priority").setValueString(name);
    
    name = nxml.getChildNodeText("process", null);
    if(name != null)
      b.getAttribute("process").setValueString(name);
    
    String nsid = nxml.getAttribute("sid","-1");
    b.getAttribute("sid").setValueString(nsid);
    
    nsid = nxml.getAttribute("unique_sid","-1");
    b.getAttribute("unique_sid").setValueString(nsid);
    try{
      NodeList nbl = nxml.getDocument().selectNodes("//*[@unique_sid='"+nsid+"']/ancestor::*[@unique_sid]");
      if(nbl.getLength() > 0)
      {
        ngtXMLHandler nbranch = new ngtXMLHandler(nbl.item(nbl.getLength()-1));
        String ssid = nbranch.getAttribute("sid",null);
        b.getAttribute("branch").setValueString(nbranch.getAttribute("unique_sid",ssid));
      }
    }catch(Exception e){throw new boRuntimeException("xwfStepExec", "createActivity", e);}    
    
    
    String nopt = nxml.getAttribute("optional","0");
    if("true".equals(nopt))
      b.getAttribute("optional").setValueString("1");
    else
      b.getAttribute("optional").setValueString("0");
      
    nopt = nxml.getChildNodeText("oneShotActivity","0");
    if("true".equals(nopt))
      b.getAttribute("oneShotActivity").setValueString("1");
    else
      b.getAttribute("oneShotActivity").setValueString("0");
      
    nopt = nxml.getChildNodeText("showTask","true");
    if("true".equals(nopt))
      b.getAttribute("showTask").setValueString("1");
    else
      b.getAttribute("showTask").setValueString("0");
    
    nopt = nxml.getChildNodeText("showReassign","true");
    if("true".equals(nopt))
      b.getAttribute("showReassign").setValueString("1");
    else
      b.getAttribute("showReassign").setValueString("0");
    
    nopt = nxml.getChildNodeText("showWorkFlowArea","true");
    if("true".equals(nopt))
      b.getAttribute("showWorkFlowArea").setValueString("1");
    else
      b.getAttribute("showWorkFlowArea").setValueString("0");
    
    String partname = nxml.getChildNode("participant").getAttribute("name",null);
    long lpart = setAssignedTo(nxml, b, partname);
    
    if(nxml.getChildNode("executante") != null)
    {
        String executername = nxml.getChildNode("executante").getAttribute("name",null);
        if(executername != null)
        {
            setPerformer(nxml, b, executername);
        }
    }
    
    String durs = nxml.getChildNodeText("forecastWorkDuration", null);
    if(durs != null && !durs.startsWith(";"))
    {
      double dur = Double.parseDouble(durs);
      b.getAttribute("forecastWorkDuration").setValueDouble(dur);
    }
     
    durs = nxml.getChildNodeText("deadLineDate", null);
    Date deadLine = null;
    if(durs != null && durs.startsWith("\""))
    {
      durs = durs.substring(1, durs.length()-1); 
      deadLine = boConvertUtils.convertToDate(durs, b.getAttribute("deadLineDate"));
      if(deadLine != null) 
        b.getAttribute("deadLineDate").setValueDate(deadLine);
    }  
    else
      if(durs != null && !durs.startsWith(";"))
      {
        deadLine = calculateDeadLine(durs, xwfm.getObject(lpart));
        b.getAttribute("deadLineDate").setValueDate(deadLine);
      }
    
    
    
    ngtXMLHandler alerts = nxml.getChildNode("alerts");
    if(alerts != null && alerts.getNode() != null)
    {
      ngtXMLHandler first_alert = alerts.getFirstChild();
      if(first_alert != null && first_alert.getNode() != null)
      {
        String alert_name = first_alert.getText();
        regAlert(alert_name, deadLine, xwfm.getObject(lpart), nsid);
      }
    }
    String varnode = "variables";
    ngtXMLHandler variables = nxml.getChildNode(varnode);
    if(variables != null)
    {
      ngtXMLHandler[] vars = variables.getChildNodes();
      for(int i=0; i<vars.length; i++)
      {
        String vname = vars[i].getAttribute("name");
        
        long sboui = 0;
        if(vars[i].getNodeName().equals("variable"))
          sboui = xwfHelper.getContextVar(new ngtXMLHandler(this.step_node), vname);
        else//particpant
        {
          sboui = xwfHelper.getContextPar(new ngtXMLHandler(this.step_node), vname);
          sboui = xwfm.createVarFromPar(sboui);
        }
        if(sboui != 0)
        {
          ngtXMLHandler[] varChilds = vars[i].getChildNodes();
          if(varChilds == null || varChilds.length == 0)
          {
            b.getBridge("variables").add(sboui);
            givePrivileges(lpart, xwfm.getObject(sboui));
          }
          else
          {
            boObject clone = xwfm.getObject(sboui).cloneObject();
            clone.getAttribute("isClone").setValueString("1");
            for(int j=0; j < varChilds.length; j++)
            {
              setAttVar(varChilds[j], clone);
            }
            sboui = clone.getBoui();
            b.getBridge("variables").add(sboui);
            givePrivileges(lpart, clone);
          }
        }
        vname = vars[i].getAttribute("processTemplate", "false");
        if("true".equalsIgnoreCase(vname))
        {
          boObject varO = xwfm.getObject(sboui);
          this.templateApplication(varO, vars[i]);
          xwfm.updateObject(varO);
        }
      }
    }
        
    

    return b;
  }
  /**
     * Mediante a definição do modo de acesso a variavel da definição serão dados privilégios adicionais ao utilizador
     * que deverá manipular esse objecto.
     * <P> Tem um filosofia incremental, ou seja, não retira privilégios já adquiridos, apenas acrescenta caso não tenha 
     * os privilégios necessários à execução da tarefa
     * @throws netgest.bo.runtime.boRuntimeException
     * @param var       variável sobre o qual será necessário operar
     * @param particip  boui utilizador que executará a operação
     */
  public static void givePrivileges(long particip, boObject var)throws boRuntimeException
  {
    boObject obj = var.getAttribute("value").getObject().getAttribute("valueObject").getObject();
    if(obj != null && obj.getBoDefinition().implementsSecurityRowObjects())
    {
      bridgeHandler sec = obj.getBridge("KEYS");
      boBridgeRow row = sec.getRow(particip);
      long new_code = var.getAttribute("mode").getValueLong();
      new_code++;
      long curr_code=0;
      if(row != null)
        curr_code = row.getAttribute("securityCode").getValueLong();
      
      sec = obj.getBridge("KEYS_PERMISSIONS");
      row = sec.getRow(particip);
      if(row != null)
        curr_code = row.getAttribute("securityCode").getValueLong();
      else
      {
        if(curr_code == 0 && new_code > curr_code)
        {
          sec.add(particip);
          row = sec.getRow(particip);
        }
      }
      if(new_code > curr_code)
      {
      //RICARDO coloquei o TESTE != null dava erro aseguir ao assignar projecto
           if( row!=null )
           {
               row.getAttribute("securityCode").setValueLong(new_code);
           }
      }
    }
  }
  
  /**
   * Função genérica para a criação da actividade correspondente ao passo que queremos executar
   * 
   * @param state   estado com que será inicializada (create ou wait)
   * @return        objecto correspondente a actividade criada
   * @throws netgest.bo.runtime.boRuntimeException
   */
  private boObject createActivity(String state)throws boRuntimeException
  {
    ngtXMLHandler nxml = new ngtXMLHandler(step_node);
    nxml = contextReplacement(nxml);
    String name = nxml.getNodeName();
    String type = null;
    boObject act = null;
    switch(stepType(step_node))
    { 
      case 6:
      {
        type = "xwfActivity";
        act = createActivity(nxml, type, state);
        break;
      }
      case 7:
      {
        type = "xwfActivityFill";
        act = createActivity(nxml, type, state);
        
        break;
      }
      case 8:
      {
        type = "xwfActivityDecision";
        act = createActivity(nxml, type, state);
        String quest = nxml.getChildNodeText("question", "");
        act.getAttribute("question").setValueString(quest);
        ngtXMLHandler aws = nxml.getChildNode("answers");
        ngtXMLHandler atrue = aws.getChildNode("TRUE");
        
        boObject boopt1 = xwfm.createObject("xwfOption");
        boopt1.getAttribute("labelOption").setValueString(atrue.getChildNodeText("label",""));
        boopt1.getAttribute("codeOption").setValueString("TRUE");
        act.getAttribute("yes").setValueLong(boopt1.getBoui());
        
        boObject boopt2 = xwfm.createObject("xwfOption");
        ngtXMLHandler afalse = aws.getChildNode("FALSE");
        boopt2.getAttribute("labelOption").setValueString(afalse.getChildNodeText("label",""));
        boopt2.getAttribute("codeOption").setValueString("FALSE");
        act.getAttribute("no").setValueLong(boopt2.getBoui());
        
          ngtXMLHandler n_app = nxml.getChildNode("approval");
          if(n_app != null && n_app.getNode() != null)
          {
            String s_app_boui = n_app.getAttribute("boui");
            String s_pname = nxml.getChildNode("participant").getAttribute("name");
            if(s_app_boui != null && s_app_boui.length() > 0 && this.controlf!=null && s_pname != null && s_pname.length() > 0)
            {
                boObject app_bo = xwfm.getObject(Long.parseLong(s_app_boui));
                String mode_app = app_bo.getAttribute("mode").getValueString();
                if("hierar".equalsIgnoreCase(mode_app))
                {
                    bridgeHandler bh = app_bo.getBridge("roles");
                    AttributeHandler att = act.getAttribute("assignedQueue");
                    int i = controlf.locateApprovalRole(bh, att.getObject());
                    if(bh.moveTo(i+1))
                    {
                        att.setValueLong(bh.getCurrentBoui());
                    }
                    else
                    {
                        String s_va = n_app.getAttribute("validAnswer");
                        if(s_va != null && s_va.length() > 0)
                            controlf.finishedStep(nxml.getNode(), s_va);
                        
                        return act;
                    }
                }
                else
                    if("parallel".equalsIgnoreCase(mode_app))
                    {
                        boObject prun = this.getBoManager().createObject("PollRun");
                        boObject defPoll = app_bo.getAttribute("pollDefinition").getObject();
                        if(defPoll == null)
                            defPoll = getBoManager().loadObject("Select PollDef where code = 'APP_PARAL'");
                        long boui_def = defPoll.getBoui();
                        prun.getAttribute("definition").setValueLong(boui_def);
                        prun.getAttribute("program").setValueLong(getBoManager().getProgBoui());
                        bridgeHandler broles = app_bo.getBridge("roles");
                        prun.getAttribute("universe").setValueLong(broles.getRowCount());
                        boObject bdef = this.getBoManager().getObject(boui_def);
                        bridgeHandler boption = bdef.getBridge("options");
                        boption.beforeFirst();
                        bridgeHandler boptrun = prun.getBridge("options");
                        while(boption.next())
                        {
                            boptrun.add(boption.getCurrentBoui());
                            boptrun.getAttribute("votes").setValueString("0");
                            boptrun.getAttribute("percent").setValueString("0");
                        }
                        
                        
                        AttributeHandler att = act.getAttribute("assignedQueue");
                       // int starterAproval = controlf.locateApprovalRole(broles, att.getObject());
                        broles.beforeFirst();
                       /* if(starterAproval != 1)
                        {*/
                            if(broles.next() )
                            {
                                String partname = broles.getObject().getAttribute("name").getValueString();
                                if(partname != null)
                                {
                                    act.getAttribute("assignedQueue").setValueLong(broles.getCurrentBoui());
                                    act.getAttribute("partName").setValueString(partname);
                                }
                                act.getAttribute("pollObj").setObject(prun);
                                xwfAnnounceImpl.addAnnounce(act.getAttribute("label").getValueString(), act.getAttribute("assignedQueue").getObject(), act, xwfm);
                                getBoManager().updateObject(act);
                            }
                       /* }
                        else
                        {
                            if(broles.getRowCount() == 1)
                            {
                                controlf.finishedStep(this.step_node, "TRUE");
                                act.destroy();
                                return null;
                            }
                        }
                        int i = 2;*/
                        while(broles.next())
                        {
                            boObject nact = null;
                           /* if(starterAproval == 1 && i == 2)
                                nact = act;
                            else
                            {
                                if(i == starterAproval)
                                {
                                    String sc = xwfPollController.approvalStarterVotation(broles.getObject(), prun, "TRUE");
                                    if(sc != null)
                                    {
                                        controlf.finishedStep(this.step_node, sc);
                                        act.destroy();
                                        return null;
                                    }
                                    i++;
                                    continue;
                                }
                                else*/
                                    nact = act.cloneObject();
                            //}
                            String partname = broles.getObject().getAttribute("name").getValueString();
                            if(partname != null)
                            {
//                                this.setAssignedTo(nxml, nact, partname);
                                nact.getAttribute("assignedQueue").setValueLong(broles.getCurrentBoui());
                                nact.getAttribute("partName").setValueString(partname);
                            }
                            nact.getAttribute("pollObj").setObject(prun);
                            xwfAnnounceImpl.addAnnounce(nact.getAttribute("label").getValueString(), nact.getAttribute("assignedQueue").getObject(), nact, xwfm);
                            getBoManager().updateObject(nact);
                           // i++;
                        }
                        return act;
                    }
            }
          }

        break;
      }
      case 9:
      {
        type = "xwfActivityChoice";
        act = createActivity(nxml, type, state);
        String quest = nxml.getChildNodeText("question", "");
        act.getAttribute("question").setValueString(quest);
        
        ngtXMLHandler aws = nxml.getChildNode("answers");
        ngtXMLHandler[] choices = aws.getChildNodes();
        
        for(int i=0; i<choices.length; i++)
        {
          boObject boopt1 = xwfm.createObject("xwfOption");
          boopt1.getAttribute("labelOption").setValueString(choices[i].getChildNodeText("label",""));
          boopt1.getAttribute("codeOption").setValueString(Integer.toString(i));
// Ricardo         boopt1.getAttribute("availableWhen").setValueString(choices[i].getChildNodeText("availableWhen",null));
//          boopt1.getAttribute("disableWhen").setValueString(choices[i].getChildNodeText("disableWhen",null));
          act.getBridge("options").add(boopt1.getBoui());
        }
        
        break;
      }
      case 15:
      {
        type = "xwfActivityChoice";
        act = createActivity(nxml, type, state);
        String quest = nxml.getChildNodeText("question", "");
        act.getAttribute("question").setValueString(quest);
        
        ngtXMLHandler aws = nxml.getChildNode("answers");
        ngtXMLHandler[] choices = aws.getChildNodes();
        
        for(int i=0; i<choices.length; i++)
        {
          boObject boopt1 = xwfm.createObject("xwfOption");
          boopt1.getAttribute("labelOption").setValueString(choices[i].getChildNodeText("label",""));
          boopt1.getAttribute("codeOption").setValueString(Integer.toString(i));
          act.getBridge("options").add(boopt1.getBoui());
        }
        break;
      }
      
      case 13:
      {
        type = "xwfActivitySend";
        act = createActivity(nxml, type, state);
        makeSend(nxml, act);
        break;
      }
      case 17:
      {
        type = "xwfActivityCreateMessage";
        act = createActivity(nxml, type, state);
        makeSend(nxml, act);
        break;
      }
      case 14:
      {
        type = "xwfUserCallProgram";
        act = createActivity(nxml, type, state);
        String xml_value = nxml.getAttribute("mode", "embebed");
        act.getAttribute("mode").setValueString(xml_value);
        ngtXMLHandler pf = nxml.getChildNode("programFilter");
        xml_value = pf.getChildNodeText("xeoql", null);
        if(xml_value != null)
          act.getAttribute("filter").setValueString(xml_value);
        break;
      }
      case 3:
      {
        type = "xwfWaitResponse";
        act = createActivity(nxml, type, state);
        makeWaitResp(nxml, act);
      }
      
    }
    if(getBoManager().getProgram() != null)
    {
        xwfActionHelper.givePrivilegesToProgram(act,getBoManager().getProgBoui());
    }
    else if(getBoManager().getProgBoui() > 0)
    {
        xwfActionHelper.givePrivilegesToProgram(act,getBoManager().getProgBoui());
    }
    xwfFunctions.setIntelligentLabel(xwfm, xwfm.getProgram(), act );
    xwfAnnounceImpl.addAnnounce(act.getAttribute("label").getValueString(), act.getAttribute("assignedQueue").getObject(), act, xwfm);
    xwfm.updateObject(act);
    return act;
  }
  
  
  
  /**
   * Cria um objecto xwfWait com base no currente passo a ser executado
   * @return  objecto xwfWait criado
   * @throws netgest.bo.runtime.boRuntimeException
   */
  private boObject createWait() throws boRuntimeException
  {
  
    ngtXMLHandler nxml = new ngtXMLHandler(step_node);
    boObject tim = null;
    
    tim = xwfm.createObject("xwfWait");
    String name = nxml.getChildNodeText("timerVariable", nxml.getNodeName());
    tim.getAttribute("name").setValueString(name);
    name = nxml.getChildNodeText("label",  name);
    tim.getAttribute("label").setValueString(name,AttributeHandler.INPUT_FROM_INTERNAL);
    name = nxml.getChildNodeText("description",  nxml.getText());
    tim.getAttribute("description").setValueString(name);
    String nsid = nxml.getAttribute("unique_sid", null);
    tim.getAttribute("unique_sid").setValueString(nsid);
    name = nxml.getChildNodeText("logicalOperator", null);
    tim.getAttribute("operator").setValueString(name);
    tim.getAttribute("program").setValueLong(xwfm.getProgBoui());
    tim.getAttribute("done").setValueString("0");

    String durs = nxml.getChildNodeText("deadLineDate", null);
    Date deadLine = null;

    DataSet drs;
    EboContext c = xwfm.getContext();
    drs = DataManager.executeNativeQuery(c,"DATA", "select sysdate from dual", 1, 1, null);
    Date now = drs.rows(1).getDate(1);
    
    tim.getAttribute("beginTime").setValueDate(now);
    
      if(durs != null && !durs.startsWith(";"))
      {
        deadLine = calculateDeadLine(durs, xwfm.getObject(this.controlf.getProgramAdmin(xwfm.getProgram())));
        tim.getAttribute("endTime").setValueDate(deadLine);
      }
    
    
    ngtXMLHandler alerts = nxml.getChildNode("alerts");
    if(alerts != null && alerts.getNode() != null)
    {
      ngtXMLHandler first_alert = alerts.getFirstChild();
      if(first_alert != null && first_alert.getNode() != null)
      {
        String alert_name = first_alert.getText();
        regAlert(alert_name, deadLine, xwfm.getObject(this.controlf.getProgramAdmin(xwfm.getProgram())), nsid);
      }
    }

//    String strTime = nxml.getChildNodeText("time", null);
//    DataSet drs;
//    EboContext c = xwfm.getContext();
//    drs = DataManager.executeNativeQuery(c,"DATA", "select sysdate from dual", 1, 1, null);
//    Date now = drs.rows(1).getDate(1);
//    if(strTime != null)
//    {
//        double dur = Double.parseDouble(nxml.getChildNodeText("time", "0"));
//        
//        tim.getAttribute("maxDuration").setValueDouble(dur);
//        long min = (long)dur;
//        long sec = (long)((dur - min) *  10);
//        sec = sec * 60 / 10;
//        tim.getAttribute("endTime").setValueDate(DateUtils.sumToDate(now, 0, 0, 0, 0, min, sec));
//    }   
    
    return  tim;
  }
  
  private boObject atachMessageActivity(boObject actv, boObject message) throws boRuntimeException
  {
      
    boObject varValue = xwfm.createObject("xwfVarValue");
    varValue.getAttribute("program").setValueLong(xwfm.getProgBoui());
    varValue.getAttribute("type").setValueLong(0); 
    long boui_cls = xwfm.loadObject("select Ebo_ClsReg where name = 'message'").getBoui();
    varValue.getAttribute("object").setValueLong(boui_cls);
    varValue.getAttribute("minoccurs").setValueLong(0);
    varValue.getAttribute("maxoccurs").setValueLong(1);
    varValue.getAttribute("unique_sid").setValueString(actv.getAttribute("unique_sid").getValueString());
    if(message != null)
      xwfm.setVarValueObject(varValue, message, false, AttributeHandler.INPUT_FROM_INTERNAL);
    
    boObject vo = xwfm.createObject("xwfVariable");
    vo.getAttribute("name").setValueString("message");
    vo.getAttribute("label").setValueString("Mensagem");
    vo.getAttribute("isClone").setValueString("1");
    vo.getAttribute("mode").setValueLong(1);  
    vo.getAttribute("showMode").setValueLong(0);    
    vo.getAttribute("required").setValueString("1");
    vo.getAttribute("value").setValueLong(varValue.getBoui());
    
    actv.getAttribute("message").setValueLong(vo.getBoui());
    return vo;
  }
  /**
     * Procede a substituição das fórmulas presentes no XML do passo.
     * <P> Estas fórmulas podem estar em qualquer parte da definição do passo e são ladeadas pelo o caracter #
     * <P> Após a identificação das mesmas serão avaliadas as expressões cujos valores substituirão as presentes formas.
     * <P> Esta substituição não ficará guardada no XML do programa para não desvirtuar a definição, é apenas gerado um novo
     * objecto de estrutura XML com as novas alterações
     * @throws netgest.bo.runtime.boRuntimeException
     * @return estrutura XML com a interpretação as fórmulas
     * @param nxml  nó XML do passo a interpretar
     */
  public ngtXMLHandler contextReplacement(ngtXMLHandler nxml)throws boRuntimeException
  {
    String sdocxml = nxml.getText();

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try{
      ((XMLElement)nxml.getNode()).print(bos,"UTF-8");
      sdocxml = bos.toString("UTF-8");  
    }catch(Exception e){return null;}    
    
    String patterns = "#(.*?)#";
    Pattern pat = Pattern.compile(patterns);
    Matcher m = pat.matcher(sdocxml);
    String transf=null;
    while (m.find())
    {
      String res = m.group(1);
      transf = res;
      try{
        transf = (String)p.eval(xwfm, res+";");
      }catch(Exception e){};
      sdocxml = StringUtils.replacestr(sdocxml, m.group(), transf);
    }
    if(transf == null)
      return nxml;
    else
    {
      transf = sdocxml;
      return new ngtXMLHandler(transf).getFirstChild();
    }
  }
  /**
     * Calcula a data de conclusão da tarefa mediante a definição do passo
     * @throws netgest.bo.runtime.boRuntimeException
     * @return data máxima para a conclusão da tarefa
     * @param par   utilizador para quem está assignada a tarefa
     * @param durs  tag que contém a definição do tempo que a tarefa tem para ser executada
     */
  protected Date calculateDeadLine(String durs, boObject par)throws boRuntimeException
  {
    xwfECMAtokenizer tok = new xwfECMAtokenizer(durs);
    tok.tokenizeStr();
    String time = tok.getCurrentWord();tok.incConsumer();
    String unit = tok.getCurrentWord();tok.incConsumer();
    String linear = tok.getCurrentWord();tok.incConsumer();
    String after = tok.getCurrentWord();tok.incConsumer();
    String what = tok.getCurrentWord();
    int ltime = Integer.parseInt(time);
    Date from_t = null;
    Date to_set = null;
    if(what.equals(xwfHelper.TAG_VALUE_TASK))
    {
      from_t = WorkPlaceDateUtils.getNowMinutes(par);
    }
    else
      if(what.equals(xwfHelper.TAG_VALUE_PROGRAM))
      {
        from_t = xwfm.getProgram().getAttribute("SYS_DTCREATE").getValueDate();
        if(from_t == null)
        {
          from_t = WorkPlaceDateUtils.getNowMinutes(par);
        }
      }
      else
      {
        Node nb=null;
        try{
         nb = this.controlf.getActualXml().getDocument().selectSingleNode("//*[@name='"+what+"']");
        }catch(Exception e){}
        ngtXMLHandler nact=null;
        if(nb != null)
        {
          nact = new ngtXMLHandler(nb);  
          String usid_act = nact.getAttribute("unique_sid");
          if(usid_act != null)
          {
            String boql_usid = "select xwfActivity where unique_sid = '"+usid_act+"'";
//            boObject boAct = xwfm.loadObject(boql_usid);
            boObject boAct = xwfFunctions.getActivityByUsid("xwfActivity", usid_act, xwfm);
            if(xwfm.getMode() == xwfHelper.PROGRAM_EXEC_TEST_MODE)
            {
                boObjectList ll = xwfm.listObject(boql_usid);
                ll.beforeFirst();
                while(ll.next())
                {
                    if(usid_act.equals(ll.getObject().getAttribute("unique_sid").getValueString()))
                    {
                        boAct = ll.getObject();
                        break;
                    }
                }
            }
            if(boAct != null && boAct.exists())
            {
              from_t = boAct.getAttribute("endDate").getValueDate();
            }
          }
          else
            from_t = WorkPlaceDateUtils.getNowMinutes(par);
        }
        else
          from_t = WorkPlaceDateUtils.getNowMinutes(par);
      }
    int days, hours, mins;
    days = hours = mins = 0;
    if(unit.equals(xwfHelper.TAG_VALUE_DAY))
      days = ltime;
    else
      if(unit.equals(xwfHelper.TAG_VALUE_HOUR))
        hours = ltime;
      else
        mins = ltime;
      
    if(linear.equals(xwfHelper.TAG_VALUE_LINEAR))
      to_set = WorkPlaceDateUtils.sumToDate(par,from_t, 0, 0, days, hours, mins, 0);
    else
    {
      WorkPlaceDateUtils wdt = new WorkPlaceDateUtils(par);
      to_set = wdt.sumToUtilDate(par, from_t, 0, 0, days, hours, mins, 0);
    }
    return to_set;
  }
  /**
     * Regista um alerta para que alguma acção seja desencadeada quando a tarefa estiver perto do limite de execução.
     * Esse alerta ficará em modo stand by até que chegue a hora de despertar, caso a tarefa seja concluída antes disso
     * ele será apagado por ouro método.
     * @throws netgest.bo.runtime.boRuntimeException
     * @param usid          unique sid da tarefa a monitorar       
     * @param par           utilizador que tera que realizar a tarefa antes que seja activado o alerta
     * @param dead_line     data limite da execução
     * @param alert_name    nome da definição de alerta
     */
  protected void regAlert(String alert_name, Date dead_line, boObject par, String usid)throws boRuntimeException
  {
    if(dead_line == null)
      return;
    Node nalert = null;
    try{
      nalert = this.controlf.getActualXml().getDocument().selectSingleNode("//alert[@name='"+alert_name+"']");
    }catch(Exception e){return;}
    ngtXMLHandler ngtalert = new ngtXMLHandler(nalert);
    String alert_desc = ngtalert.getText();
    xwfECMAtokenizer tok = new xwfECMAtokenizer(alert_desc);
    tok.tokenizeStr();
    String time = tok.getCurrentWord();tok.incConsumer();
    String unit = tok.getCurrentWord();tok.incConsumer();
    String linear = tok.getCurrentWord();tok.incConsumer();
    String after = tok.getCurrentWord();tok.incConsumer();
    String what = tok.getCurrentWord();tok.incConsumer();
    String forPar = tok.getCurrentWord();tok.incConsumer();
    
    int ltime = Integer.parseInt(time);
    Date from_t = null;
    Date to_set = null;
    
    
    int days, hours, mins;
    days = hours = mins = 0;
    if(unit.equals(xwfHelper.TAG_VALUE_DAY))
      days = ltime;
    else
      if(unit.equals(xwfHelper.TAG_VALUE_HOUR))
        hours = ltime;
      else
        mins = ltime;
      
    if(linear.equals(xwfHelper.TAG_VALUE_LINEAR))
      if(after.equals(xwfHelper.TAG_VALUE_BEFORE))
        to_set = WorkPlaceDateUtils.sumToDate(par,dead_line, 0, 0, -days, -hours, -mins, 0);
      else
        to_set = WorkPlaceDateUtils.sumToDate(par,dead_line, 0, 0, days, hours, mins, 0);
    else
    {
      WorkPlaceDateUtils wdt = new WorkPlaceDateUtils(par);
      if(after.equals(xwfHelper.TAG_VALUE_BEFORE))
        to_set = wdt.sumToUtilDate(par, dead_line, 0, 0, -days, -hours, -mins, 0);
      else
        to_set = wdt.sumToUtilDate(par, dead_line, 0, 0, days, hours, mins, 0);
    }
    
    boObject tim = xwfm.createObject("xwfWait");
    
    tim.getAttribute("name").setValueString(alert_name);
    
    tim.getAttribute("label").setValueString("Alert: "+alert_name);

    tim.getAttribute("unique_sid").setValueString(usid);
//    tim.getAttribute("operator").setValueString(name);
    tim.getAttribute("program").setValueLong(xwfm.getProgBoui());
    tim.getAttribute("done").setValueString("0");
    
   
    DataSet drs;
    EboContext c = xwfm.getContext();
    drs = DataManager.executeNativeQuery(c,"DATA", "select sysdate from dual", 1, 1, null);
    Date now_bd = drs.rows(1).getDate(1);
    Date now_wp = WorkPlaceDateUtils.getNow(par);
    long to_add =  now_bd.getTime() - now_wp.getTime();
    Calendar cpar = Calendar.getInstance();
    cpar.setTime(to_set);
    cpar.add(Calendar.MILLISECOND, (int)to_add);
    to_set = cpar.getTime();
    tim.getAttribute("endTime").setValueDate(to_set);
    tim.getAttribute("beginTime").setValueDate(now_bd);
    xwfm.updateObject(tim);
//    tim.update();
  }
  
  protected boolean templateApplication(boObject var, ngtXMLHandler varCall)throws boRuntimeException
  {
    String var_name = varCall.getAttribute("name");
//    ngtXMLHandler nvardef = xwfHelper.getContextNode(varCall, var_name);
  
    ngtXMLHandler nvardef = xwfHelper.getUnlinkContextNode(controlf.getActualXml(), varCall, var_name);
    
    ngtXMLHandler ot = nvardef.getChildNode("objTemplate");
    if(ot != null && ot.getNode() != null)
    { 
      String ot_boui = ot.getAttribute("boui", null);
      if(ot_boui != null)
      {
        boObject value = xwfm.getValueBoObject(var.getAttribute("value").getObject());
        if(value != null /*&& value.exists()*/)
        {
          boObject templ = xwfm.getObject(Long.parseLong(ot_boui));
          if(templ != null && templ.exists())
          {
            ((Ebo_TemplateImpl)templ).loadTemplate(value);
            xwfm.processRefTemplate(value, templ.getBoui(), p);
            var.getAttribute("templateMode").setValueLong(1);
          }
          else
            return false;
        }
        else
        {
          var.getAttribute("templateBoui").setValueLong(Long.parseLong(ot_boui));
          var.getAttribute("templateMode").setValueLong(10);
        }
      }
      else
      {
        String words = "";
        ngtXMLHandler[] n_kws = ot.getChildNode("keyWords").getChildNodes();
        int i=0;
        for(i=0; i < n_kws.length; i++)
        {
          String sd = n_kws[i].getText();
          try{
            words += p.eval(xwfm, sd + ";").toString() + " ";
          }catch(Exception e){}
        }
        if(words != null && i > 0 )
        {
            var.getAttribute("keyWords").setValueString(words);
            var.getAttribute("templateMode").setValueLong(10);
        }
        else
        {
            boObject val_msg = xwfm.getValueBoObject(var.getAttribute("value").getObject());
            if(val_msg != null && val_msg.getName().startsWith("message"))
            {
                nvardef = contextReplacement(nvardef);
                String subject_s = nvardef.getChildNodeText("subject", null);
                String body_s = nvardef.getChildNodeText("message", null);
                val_msg.getAttribute("name").setValueString(subject_s);
                val_msg.getAttribute("description").setValueString(body_s);
            //    xwfm.updateObject(val_msg);
            }
            else
                return false;
        }
      }
    }
    else
        return false;
    
    return true;
  }
  /**
     * Atribui o utilizador que deverá realizar a tarefa mediante a definição do passo.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return devolve o boui do utilizador que atribuiu ao campo assignedQueue
     * @param partname  nome do participante a achar
     * @param b         actividade a assignar
     * @param nxml      XML com a definição do passo 
     */
  public void setPerformer(ngtXMLHandler nxml, boObject b, String partname) throws boRuntimeException
  {
    try
    {
        long lpart = 0;
        if(partname != null && partname.length() > 0)
        {
          try{
            long sboui = xwfHelper.getContextPar(new ngtXMLHandler(this.step_node), partname);
            if(sboui != 0)
            {
              boObject xwfpart = xwfm.getObject(sboui);
              long xpart = xwfm.getValueLong(xwfpart.getAttribute("value").getObject());
              if(xpart > 0)
                lpart = xpart;
            }
          }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "createActivity", e);}
        }
        else
        {
          partname = nxml.getChildNode("executante").getAttribute("boui", null);
          lpart = Long.parseLong(partname);
        }
        if(lpart != 0)
          b.getAttribute("performer").setValueLong(lpart);
    }
    catch (Exception e)
    {
        /*Ignora no caso de não conseguir o executante fica com o grupo*/
    }
    
  }
  
  /**
     * Atribui o utilizador que deverá realizar a tarefa mediante a definição do passo.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return devolve o boui do utilizador que atribuiu ao campo assignedQueue
     * @param partname  nome do participante a achar
     * @param b         actividade a assignar
     * @param nxml      XML com a definição do passo 
     */
  public long setAssignedTo(ngtXMLHandler nxml, boObject b, String partname) throws boRuntimeException
  {
    
    String creatorboui = nxml.getChildNodeText("creator", null);
    long admin_boui = 0;
    long lpart = 0;
    if(creatorboui == null)
    {
      admin_boui = xwfHelper.getContextPar(new ngtXMLHandler(this.step_node), xwfHelper.PART_WFADMINISTRATOR_NAME);
      boObject xwfpart = xwfm.getObject(admin_boui);
      lpart = xwfm.getValueLong(xwfpart.getAttribute("value").getObject());
    }
    else
      lpart = Long.parseLong(creatorboui);
    
    if(lpart != 0)
      b.getAttribute("CREATOR").setValueLong(lpart);
    else
      throw new boRuntimeException("xwfControlFlow", "createActivity", new Exception("Error: Not a valid Administrator defined!!!"));
    if(partname != null && partname.length() > 0)
    {
      try{
        long sboui = xwfHelper.getContextPar(new ngtXMLHandler(this.step_node), partname);
        if(sboui != 0)
        {
          boObject xwfpart = xwfm.getObject(sboui);
          long xpart = xwfm.getValueLong(xwfpart.getAttribute("value").getObject());
          if(xpart > 0)
            lpart = xpart;
        }
      }catch(Exception e){throw new boRuntimeException("xwfControlFlow", "createActivity", e);}
    }
    else
    {
      partname = nxml.getChildNode("participant").getAttribute("boui", null);
      lpart = Long.parseLong(partname);
    }
    if(lpart != 0)
      b.getAttribute("assignedQueue").setValueLong(lpart);
    else
      throw new boRuntimeException("xwfControlFlow", "createActivity", new Exception("Error: Not a valid Administrator defined!!!"));
    
    return lpart;
  }
  /**
     * select do nó actual
     * @return nó actual que se está a fazer a gestão
     */
  public Node getStepNode()
  {
      return this.step_node;
  }
  /**
     * Selector do boManager do programa
     * @return boManager do programa
     */
  public xwfBoManager getBoManager()
  {
      return this.xwfm;
  }
  
      
}