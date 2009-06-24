/*Enconding=UTF-8*/
package netgest.xwf.common;

import java.rmi.server.UID;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import netgest.bo.controller.xwf.XwfController;
import netgest.bo.def.boDefAttribute;
import netgest.bo.lovmanager.*;
import netgest.bo.runtime.*;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.security.securityOPL;
import netgest.bo.security.securityRights;
import netgest.bo.utils.DateUtils;
import netgest.utils.ClassUtils;
import netgest.utils.ngtXMLHandler;

import netgest.xwf.*;
import netgest.xwf.EngineGate;

import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XSLException;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** 
 * Métodos comuns
 * @author Pedro Castro Campos 
 * @author Ricardo Andrade
 * @version 1.0
 */
public final class xwfHelper 
{
    public final static String VALUE_OBJECT = "valueObject";
    public final static String VALUE_LIST = "valueList";
    public final static String VALUE_BOOLEAN = "valueBoolean";
    public final static String VALUE_NUMBER = "valueNumber";
    public final static String VALUE_DATETIME = "valueDateTime";
    public final static String VALUE_DATE = "valueDate";
    public final static String VALUE_CLOB = "valueClob";
    public final static String VALUE_TEXT = "valueText";
    public final static String VALUE_LOV = "valueLov";
    
    public final static String PART_STARTER_NAME = "starter";
    public final static String PART_WFADMINISTRATOR_NAME = "workFlowAdministrator";
    
    public final static String STEP_BEGIN_TIME = "beginTime";
    public final static String STEP_STOP_TIME = "stopTime";
    public final static String STEP_WAIT_TIME = "waitTime";
    public final static String STEP_WAIT_RESPONSE = "waitResponse";
    public final static String STEP_WAIT_THREAD = "waitThread";
    public final static String STEP_XEP_CODE = "xepCode";
    public final static String STEP_ACTIVITY = "activity";
    public final static String STEP_FILL = "fillVariable";
    public final static String STEP_DECISION = "decision";
    public final static String STEP_CHOICE = "choice";
    public final static String STEP_MENU = "menu";
    public final static String STEP_COPY = "copy";
    public final static String STEP_MILESTONE = "milestone";
    public final static String STEP_LABEL = "programlabel";
    public final static String STEP_SEND = "send";
    public final static String STEP_USER_CALL_PROG = "userCallProgram";
    public final static String STEP_THREAD = "thread";
    public final static String STEP_GOTO = "goto";
    public final static String STEP_WHILE = "while";
    public final static String STEP_FOR_EACH = "foreach";
    public final static String STEP_IF = "if";
    public final static String STEP_SWITCH = "switch";
    public final static String STEP_COMMENT = "comment";
    public final static String STEP_CALL_PROGRAM = "callProgram";
    public final static String STEP_CANCEL = "cancelActivity";
    public final static String STEP_SUB_PROGRAM = "subProgram";
    public final static String STEP_SUB_PROCEDURE = "subProcedure";
    public final static String STEP_EXIT = "exit";
    public final static String STEP_ADD_PROCEDURE = "addProcedures";
    public final static String STEP_REM_PROCEDURE = "removeProcedures";
    public final static String STEP_REM_ALL_PROCEDURE = "removeAllProcedures";
    public final static String STEP_CALL_PROCEDURE = "callProcedure";
    public final static String STEP_TERMINATE = "terminateProgram";
    public final static String STEP_POLL = "poll";
    public final static String STEP_CREATE_MSG = "createMessage";
    
    public final static String MODE_EMBEDDED = "embedded";
    public final static String MODE_OUTSOURCE = "outsource";
    
    public final static String PERFORMER_CLAUSE ="(performer=CTX_PERFORMER_BOUI or ( performer is null and ( CTX_XWFMYORWK or assignedQueue=CTX_PERFORMER_BOUI)))";
    public final static String PERFORMER_CLAUSE_ALL ="(performer=CTX_PERFORMER_BOUI or ( ( CTX_ALLPERFORMERGROUPS or assignedQueue=CTX_PERFORMER_BOUI)))";
    
    public final static String TAG_VALUE_DAY = "day";
    public final static String TAG_VALUE_HOUR = "hour";
    public final static String TAG_VALUE_MINUTE = "minute";
    public final static String TAG_VALUE_LINEAR = "linear";
    public final static String TAG_VALUE_UTIL = "util";
    public final static String TAG_VALUE_CREATE = "afterCreate";
    public final static String TAG_VALUE_END = "afterEnd";
    public final static String TAG_VALUE_AFTER = "afterDeadLine";
    public final static String TAG_VALUE_BEFORE = "beforeDeadLine";
    public final static String TAG_VALUE_PROGRAM = "__program";
    public final static String TAG_VALUE_TASK = "__task";
    public final static String TAG_VALUE_ALERT = "alert";
    public final static String TAG_VALUE_REASSIGN = "reassign";
    public final static String TAG_VALUE_ASSIGNED = "__assignedUser";
    
    public final static String TAG_VALUE_TRANSFER = "transfer";
    
    public final static String WF_ADMINISTRATOR = "workFlowAdministrator";
    
    public final static byte PROGRAM_EXEC_DEFAULT_MODE = 0;
    public final static byte PROGRAM_EXEC_TEST_MODE = 1;
    
  
    
    /**
     * Devolve o nome do atributo,relacionado com o type e o maxoccurs.
     * @param varValue  boObject do tipo xwfVarValue.
     * @return result  nome do atributo.
     */
    public final static String getTypeName(boObject varValue) throws boRuntimeException 
    {        
        String result = null;                
        int attributeType = Integer.parseInt(varValue.getAttribute("type").getValueString());        
        switch(attributeType) 
        {      
            case boDefAttribute.VALUE_UNKNOWN:
                long maxoccurs =  varValue.getAttribute("maxoccurs").getValueLong();
                if(maxoccurs == 1)
                {
                    result = VALUE_OBJECT;
                }
                else
                { 
                    result = VALUE_LIST;
                }
                break;
            case boDefAttribute.VALUE_BOOLEAN: 
                result = VALUE_BOOLEAN;
                break;
            case boDefAttribute.VALUE_NUMBER: 
                result = VALUE_NUMBER;
                break;
            case boDefAttribute.VALUE_DATETIME: 
                result = VALUE_DATETIME;
                break;
            case boDefAttribute.VALUE_DATE: 
                result = VALUE_DATE;
                break;
            case boDefAttribute.VALUE_CLOB: 
                result = VALUE_CLOB;
                break;                
            case boDefAttribute.VALUE_CHAR: 
                result = VALUE_TEXT;
                break;
            case 12:
                result = VALUE_LOV;
                break;
            default: 
                throw new RuntimeException("getTypeName(boObject varValue): type not found");
        }
        return result;
    }   
    /**
     * Devolve uma hashtable com o nome interno e os seus identificadores (cardId).
     * @param controller  controlador no contexto.
     * @param defBouiList  lista de identificadores 
     * @return lista com o nome interno como chave e o identificador como valor.
     */     
    public static Hashtable getObjectIdentification(EngineGate engine,Hashtable defBouiList) throws boRuntimeException 
    { 
        Hashtable list = new Hashtable();
        String key = null;        
        Long boui = null;        
        boObject object = null;
        boObject value = null;
        boObject valueObject = null;
        long type = -1;
        String typeStr = null;
        StringBuffer identification = null;
        Enumeration keys = defBouiList.keys();
        while(keys.hasMoreElements())
        {
            identification = null;
            
            key = (String)keys.nextElement();
            boui = (Long)defBouiList.get(key);
            
            object = engine.getObject(boui.longValue());
            value = object.getAttribute("value").getObject();
            type = value.getAttribute("type").getValueLong();
            typeStr = getTypeName(value);
            if(typeStr.equals(VALUE_OBJECT))
            {
                valueObject = engine.getBoManager().getBoObject(value);
                if(valueObject != null)
                {
                    identification = valueObject.getCARDID();   
                }                
            }
            else if(typeStr.equals(VALUE_DATE) || typeStr.equals(VALUE_DATETIME) )
            {
                
                Date date = engine.getBoManager().getValueDate(value);
                if(date != null)
                {
                    SimpleDateFormat df = null; 
                    if(typeStr.equals(VALUE_DATETIME))
                    {
                        df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");   
                    }   
                    else
                    {
                        df = new SimpleDateFormat("dd/MM/yyyy");
                    }
                    identification = new StringBuffer(df.format(date));                    
                }                     
            }
            else if(typeStr.equals(VALUE_TEXT))
            {
                String valueStr = engine.getBoManager().getValueString(value);
                if(valueStr.length() > 8)
                {
                    identification = new StringBuffer(valueStr.substring(0,8));
                    identification.append("(...)");
                }                
                else
                {
                    identification = new StringBuffer(valueStr);
                }
            }   
            else if(typeStr.equals(VALUE_NUMBER))
            {
                String valueStr = engine.getBoManager().getValueString(value);  
                identification = new StringBuffer(valueStr);
            }              
            if(identification != null && identification.length() > 0)
            {
                list.put(key,identification);
            }
        }
        return list;        
    }   
    /**
     * Devolve uma hashtable com o nome interno dos participantes e os seus bouis.
     * @param xml string a pesquisar os participantes. 
     * @return lista com o nome interno dos participantes como chave e o boui como valor.
     */    
    public static Hashtable getParticipantsBouis(String xml)
    {
        ngtXMLHandler nx = new ngtXMLHandler(xml);
        return getParticipantBouis(nx);
    }
    /**
     * Devolve uma hashtable com o nome interno dos atributos e os seus bouis.
     * @param xml string a pesquisar os atributos. 
     * @return lista com o nome interno dos atributos como chave e o boui como valor.
     */     
    public static Hashtable getVariableBouis(String xml)
    {
        ngtXMLHandler nx = new ngtXMLHandler(xml);
        return getVariableBouis(nx);
    }    
    /**
     * Devolve uma hashtable com o nome interno dos participantes e os seus bouis.
     * @param xml Xml a pesquisar os participantes. 
     * @return lista com o nome interno dos participantes como chave e o boui como valor.
     */     
    public static Hashtable getParticipantBouis(ngtXMLHandler xml)
    { 
        return getDefTypeBouis(xml,"//defParticipant");
    }
    /**
     * Devolve uma hashtable com o nome interno dos atributos e os seus bouis.
     * @param xml  Xml a pesquisar os atributos. 
     * @return lista com o nome interno dos atributos como chave e o boui como valor.
     */    
    public static Hashtable getVariableBouis(ngtXMLHandler xml)
    { 
        return getDefTypeBouis(xml,"//defVariable");
    }    
    /**
     * Devolve uma hashtable em que a chave é o nome interno e o valor é o boui correspondente.
     * @param xml  Xml a pesquisar o type de nó.
     * @param defType  nó a pesquisar no xml. 
     * @return lista com o nome como chave e o boui como valor.
     */    
    public static Hashtable getDefTypeBouis(ngtXMLHandler xml,String defType)
    {     
        Hashtable list = new Hashtable();
        ngtXMLHandler nx = null;
        Node node = null;
        long lboui = -1;
        String name = null;
        String sboui = null;        
        try
        {
            NodeList var = xml.getDocument().selectNodes(defType);                
            for(int i=0; i < var.getLength(); i++)
            {
                node = var.item(i);
                nx = new ngtXMLHandler(node);
                name = nx.getAttribute("name");
                sboui = nx.getChildNodeText("VAR_ADDRESS", "");
                lboui = ClassUtils.convertToLong(sboui.trim(),-1);
                if(lboui != -1)
                {                    
                    list.put(name,new Long(lboui));
                }
            }
        }
        catch (XSLException e)
        {
            //ignorar
        }        
        return list;
    }   
    /**
     * Devolve xml correspondente a uma hashtable em que a chave é o nó no xml e o text é o valor
     * @param table  Tabela a passar para xml
     * @return xml de uma hashtable.
     */
    public static String toXML(Hashtable table)
    {
        StringBuffer result = new StringBuffer("<hashtable>\n");
        String key = null;
        StringBuffer identification = null;
        Enumeration keys = table.keys();
        while(keys.hasMoreElements())
        {
            key = (String)keys.nextElement();                        
            identification = (StringBuffer)table.get(key);
            result.append("<").append(key).append(">");
            result.append("<![CDATA[");
            result.append(identification);
            result.append("]]>");
            result.append("</").append(key).append(">\n");
        }
        result.append("</hashtable>");
        return result.toString();                
    }    
    /**
   * Rotina simples de conversão dos vários tipos de atributos para um número inteiro 
   * @param t   String identificativa do tipo
   * @return    número inteiro correspondente ao tipo
   */
  static public int typeCode(String t)
  {
    
    if(t.equals("boolean"))
      return boDefAttribute.VALUE_BOOLEAN;
    else    
      if(t.equalsIgnoreCase("Number"))
        return boDefAttribute.VALUE_NUMBER;
      else
        if(t.equals("String") || t.startsWith("char"))
          return boDefAttribute.VALUE_CHAR;
        else
            if(t.equalsIgnoreCase("Date"))
              return boDefAttribute.VALUE_DATE;
            else
              if(t.equalsIgnoreCase("DateTime"))
                return boDefAttribute.VALUE_DATETIME;
              else
                if(t.length()>7 && t.substring(0,6).equalsIgnoreCase("Object"))
                  return boDefAttribute.VALUE_UNKNOWN;
                else
                  if(t.equalsIgnoreCase("clob"))
                    return boDefAttribute.VALUE_CLOB;
                  else
                    return boDefAttribute.VALUE_UNKNOWN;
  }
  
  /**
   * Função que devolve o nó que contém um atributo "name" com o valor <code>name</code> no contexto do programa a que <code>xnml</code> pertence
   * @param nxml    nó do qual partimos para conhecer o outro nó mantendo-nos no mesmo contexto
   * @param name    valor do atributo "name" do nó a achar
   * @return        nó xml correspondente. null se não for possível localizar
   */   
    public static ngtXMLHandler getUnlinkContextNode(ngtXMLHandler nxml_total, ngtXMLHandler nxml_unlinked, String name)throws boRuntimeException
    {
      String usid=null;
      
      Node nb = null;
      Node nnn;
      try{
        nnn = nxml_unlinked.getDocument().selectSingleNode("//*[@unique_sid][position()=1]");
        usid = new ngtXMLHandler(nnn).getAttribute("unique_sid");
        nb = nxml_total.getDocument().selectSingleNode("//*[@unique_sid='"+usid+"']");
      }catch(Exception e){throw new boRuntimeException("xwfStepExec", "createActivity", e);}
      return getContextNode(new ngtXMLHandler(nb), name);
    }
    
/*    public static ngtXMLHandler getContextNode(ngtXMLHandler nxml, String name)throws boRuntimeException
    {
      ngtXMLHandler nx = new ngtXMLHandler(nxml);
//      try
//      {
//        nx.getDocument().print(System.out);
//      }catch(Exception e){}
//      
      while(nx.getNode() != null && !nx.getNodeName().equals("program") && nx != null)
      {
        nx.goParentNode();
      }
      String u_prog = "";
      if(nx.getNode() != null)
        u_prog = nx.getAttribute("unique_sid", "");
        
      nx.goChildNode("program");
      nx.goChildNode("code");
      XMLDocument xdoc = new XMLDocument();
      xdoc.importNode(nx.getNode(), true);
      xdoc.appendChild(nx.getNode());
//      ngtXMLHandler var = nx.getChildNode("defParticipants");
//      ngtXMLHandler[] vars = var.getChildNodes();
      NodeList nb = null;
      try{
        nb = xdoc.selectNodes("//*[@name='"+name+"'][@unique_sid]");
      }catch(Exception e){throw new boRuntimeException("xwfStepExec", "createActivity", e);}
      if(nb == null || nb.getLength() <= 0)
        return null;
      else
      {
        if(nb.getLength() == 1)
          return new ngtXMLHandler(nb.item(0));
        else
        {
          for(int i=0; i<nb.getLength(); i++)
          {
            ngtXMLHandler as = new ngtXMLHandler(nb.item(i));
            while(as.getNode() != null && !as.getNodeName().equals("callProgram") )
            {
              as = as.getParentNode();
            }
            if(as.getNode() != null)
            {
              if(u_prog.equals(as.getAttribute("unique_sid", "")))
                return new ngtXMLHandler(nb.item(i));
            }
            else
              if(u_prog.equals(""))
                return new ngtXMLHandler(nb.item(i));
          }
        }
      }
      return null;
    }
*/
  public static ngtXMLHandler getContextNode(ngtXMLHandler nxml, String name)throws boRuntimeException
  {
      return getContextNode(nxml, name, false);
  }
  public static ngtXMLHandler getContextNode(ngtXMLHandler nxml, String name, boolean procedureCtx)throws boRuntimeException
  {
    ngtXMLHandler nx = new ngtXMLHandler(nxml);
    String usid = nx.getAttribute("unique_sid", "");
    String u_prog = "";
    String nxnmae = nx.getNodeName();
    while(nx.getNode() != null && !nx.getNodeName().equals("callProgram") && (!procedureCtx || !nx.getNodeName().equals("subProcedure") ))
    {
      nx.goParentNode();
    }
    if(nx.getNode() != null)
      u_prog = nx.getAttribute("unique_sid", "");
    NodeList nb = null;
    try{
      nb = nxml.getDocument().selectNodes("//*[@name='"+name+"'][@unique_sid]");
    }catch(Exception e){throw new boRuntimeException("xwfStepExec", "createActivity", e);}
    if(nb == null || nb.getLength() <= 0)
      return null;
    else
    {
      if(nb.getLength() == 1)
        return new ngtXMLHandler(nb.item(0));
      else
      {
        for(int i=0; i<nb.getLength(); i++)
        {
          ngtXMLHandler as = new ngtXMLHandler(nb.item(i));
          while(as.getNode() != null && !as.getNodeName().equals("callProgram") && (!procedureCtx || !as.getNodeName().equals("subProcedure") ) )
          {
            as = as.getParentNode();
          }
          if(as.getNode() != null)
          {
            if(u_prog.equals(as.getAttribute("unique_sid", "")))
              return new ngtXMLHandler(nb.item(i));
          }
          else
            if(u_prog.equals(""))
              return new ngtXMLHandler(nb.item(i));
        }
        if(procedureCtx)
        {
            return getContextNode(nxml, name, false);
        }
      }
    }
    return null;
  }
  /**
   * Função que devolve o boui do participante de nome <code>name</code> no contexto do programa a que <code>xnml</code> pertence
   * @param nxml    nó do qual precisamos conhecer o participante mantendo-nos no mesmo contexto
   * @param name    nome do participante a localizar
   * @return        boui do objecto xwfVariable correspondente. 0 se não for possível localizar
   */
  public static long getContextPar(ngtXMLHandler nxml, String name)
  {
    ngtXMLHandler nx = new ngtXMLHandler(nxml);
    if(nx.getParentNode().getNode() == null)
        nx.goChildNode("program");
    while(nx.getNode() != null && !nx.getNodeName().equals("program") )
    {
      nx.goParentNode();
    }
    nx.goChildNode("program");
    nx.goChildNode("code");
    ngtXMLHandler var = nx.getChildNode("defParticipants");
    ngtXMLHandler[] vars = var.getChildNodes();
    for(int i=0; i < vars.length; i++)
    {
      if(name.equals(vars[i].getAttribute("name")))
      {
        String sboui = vars[i].getChildNodeText("VAR_ADDRESS", "");
        return Long.parseLong(sboui.trim());
      }
    }
    try{
        NodeList nl = nx.getDocument().selectNodes("//defParticipant[@name='"+name+"']");
        if(nl.getLength() > 0)
        {
            ngtXMLHandler nnl = new ngtXMLHandler(nl.item(0));
            String sboui = nnl.getChildNodeText("VAR_ADDRESS", "");
            return Long.parseLong(sboui.trim());
        }
    }catch(Exception e){}
    return 0;
  }
  
  /**
   * função que devolve todos os bouis das xwfVariables de determinado programa
   * @param nxml    nó pertencente ao programa do qual queremos as variáveis
   * @return        array de bouis dos objectos xwfVariables do programa
   */
  public static long[] getContextVarsAndPars(ngtXMLHandler nxml)
  {
    ngtXMLHandler nx = new ngtXMLHandler(nxml);
    if(nx.getParentNode().getNode() == null)
        nx.goChildNode("program");
    while(nx.getNode() != null && !nx.getNodeName().equals("program") )
    {
      nx.goParentNode();
    }
    nx.goChildNode("program");
    nx.goChildNode("code");
    ngtXMLHandler var = nx.getChildNode("defVariables");
    ngtXMLHandler[] vars = var.getChildNodes();
    ngtXMLHandler par = nx.getChildNode("defParticipants");
    ngtXMLHandler[] pars = par.getChildNodes();
    long[] ret = new long[vars.length+pars.length];
    int i;
    for(i=0; i < vars.length; i++)
    {
      String sboui = vars[i].getChildNodeText("VAR_ADDRESS", null);
      if(sboui == null)
        ret[i] = 0;
      else
        ret[i] = Long.parseLong(sboui.trim());
    }
    for(int j=0; j < pars.length; j++)
    {
      String sboui = pars[j].getChildNodeText("VAR_ADDRESS", null);
      if(sboui == null)
        ret[i] = 0;
      else
        ret[i] = Long.parseLong(sboui.trim());
      i++;
    }
    return ret;
  }
  
  public static long[] getContextVars(ngtXMLHandler nxml)
  {
    ngtXMLHandler nx = new ngtXMLHandler(nxml);
    if(nx.getParentNode().getNode() == null)
        nx.goChildNode("program");
    while(nx.getNode() != null && !nx.getNodeName().equals("program") )
    {
      nx.goParentNode();
    }
    nx.goChildNode("program");
    nx.goChildNode("code");
    ngtXMLHandler var = nx.getChildNode("defVariables");
    ngtXMLHandler[] vars = var.getChildNodes();
    long[] ret = new long[vars.length];
    int i;
    for(i=0; i < vars.length; i++)
    {
      String sboui = vars[i].getChildNodeText("VAR_ADDRESS", null);
      if(sboui == null)
        ret[i] = 0;
      else
        ret[i] = Long.parseLong(sboui.trim());
    }
    return ret;
  }
    
  /**
   * Função que devolve o boui da variavel de nome <code>name</code> no contexto do programa a que <code>xnml</code> pertence
   * @param nxml    nó do qual precisamos conhecer a variável mantendo-nos no mesmo contexto
   * @param name    nome da variavl a localizar
   * @return        boui do objecto xwfVariable correspondente. 0 se não for possível localizar
   */
  public static long getContextVar(ngtXMLHandler nxml, String name)
  {
    ngtXMLHandler nx = new ngtXMLHandler(nxml);
    ngtXMLHandler var = null;
    if(nx.getParentNode().getNode() == null)
        nx.goChildNode("program");
    if(!nx.getNodeName().equals("defVariable"))
    {
      while(!nx.getNodeName().equals("program") && nx != null)
      {
        nx.goParentNode();
      }
      nx.goChildNode("program");
      nx.goChildNode("code");
      var = nx.getChildNode("defVariables");
    }
    else
    {
      if(nxml.getAttribute("name").equals("name"))
      {
        String sboui = nxml.getChildNodeText("VAR_ADDRESS", "");
        return Long.parseLong(sboui.trim());
      }
      else
        var = nx.getParentNode();
    }
    ngtXMLHandler[] vars = var.getChildNodes();
    for(int i=0; i < vars.length; i++)
    {
      if(name.equals(vars[i].getAttribute("name")))
      {
        String sboui = vars[i].getChildNodeText("VAR_ADDRESS", "");
        return Long.parseLong(sboui.trim());
      }
    }
    try{
        NodeList nl = nx.getDocument().selectNodes("//defVariable[@name='"+name+"']");
        if(nl.getLength() > 0)
        {
            ngtXMLHandler nnl = new ngtXMLHandler(nl.item(0));
            String sboui = nnl.getChildNodeText("VAR_ADDRESS", "");
            return Long.parseLong(sboui.trim());
        }
    }catch(Exception e){}
    return 0;    
  }
  
  public static ngtXMLHandler getContextProcedure(ngtXMLHandler nxml, String name)
  {
    ngtXMLHandler nx = new ngtXMLHandler(nxml);
    ngtXMLHandler var = null;
    if(nx.getParentNode().getNode() == null)
        nx.goChildNode("program");
    if(!nx.getNodeName().equals("defProcedure"))
    {
      while(!nx.getNodeName().equals("program") && nx != null)
      {
        nx.goParentNode();
      }
      nx.goChildNode("program");
      nx.goChildNode("code");
      var = nx.getChildNode("defProcedures");
    }
    else
    {
      if(nxml.getAttribute("name").equals("name"))
      {
        return nxml;
      }
      else
        var = nx.getParentNode();
    }
    ngtXMLHandler[] vars = var.getChildNodes();
    for(int i=0; i < vars.length; i++)
    {
      if(name.equals(vars[i].getAttribute("name")))
      {
        return vars[i];
      }
    }
    return null;    
  }
  
  public static ngtXMLHandler xmlDefinition(boObject defActv)throws boRuntimeException
  {
    StringBuffer sb = new StringBuffer();
    StringBuffer common_sb = new StringBuffer();
    String type = defActv.getAttribute("type").getValueString();
    String name = defActv.getAttribute("name").getValueString();
    if(name == null)
      name = "";
    String async = defActv.getAttribute("async").getValueString();
    if(async.equals("1"))
      async = "true";
    else
      async = "false";
    String optional = defActv.getAttribute("optional").getValueString();
    if(optional.equals("1"))
      optional = "true";
    else
      optional = "false";
    long pboui = defActv.getAttribute("assignedQueue").getValueLong();
    common_sb.append("<participant boui='").append(defActv.getAttribute("assignedQueue").getValueString()).append("'/>");
    common_sb.append("<creator><![CDATA[").append(defActv.getEboContext().getBoSession().getPerformerBoui()).append("]]></creator>");
    common_sb.append("<condition><![CDATA[]]></condition>");
    common_sb.append("<label><![CDATA[").append(defActv.getAttribute("label").getValueString()).append("]]></label>");
    common_sb.append("<description><![CDATA[").append(defActv.getAttribute("description").getValueString()).append("]]></description>");
    common_sb.append("<priority>").append(defActv.getAttribute("priority").getValueString()).append("</priority>");
    String lim = defActv.getAttribute("deadLineDate").getValueString();
    if(lim != null && !lim.equals(""))
      lim = "\""+lim+"\"";
    else
      lim = "";
    common_sb.append("<deadLineDate>").append(lim).append("</deadLineDate>");
    common_sb.append("<canAddSubActivities/><canDelegate/>");
    common_sb.append("<variables/>");
    common_sb.append("<subprogram/>");  
    
    if(STEP_ACTIVITY.equals(type))
    {
      sb.append("<").append(STEP_ACTIVITY).append(" name='").append(name).append("' async=\'").append(async).append("' optional='").
      append(optional).append("' >");
      sb.append(common_sb);
      sb.append("</"+STEP_ACTIVITY+">");
    }
    else
      if(STEP_USER_CALL_PROG.equals(type))
      {
        String mode = defActv.getAttribute("mode").getValueString();
        sb.append("<").append(STEP_USER_CALL_PROG).append(" name='").append(name).append("' async=\'").append(async).append("' optional='").
        append(optional).append("' mode='").append(mode).append("' >");
        sb.append(common_sb);
        sb.append("<programFilter><xeoql><![CDATA[").append(defActv.getAttribute("filter").getValueString()).append("]]></xeoql></programFilter>");
        sb.append("</"+STEP_USER_CALL_PROG+">");
      }
    return new ngtXMLHandler(sb.toString()).getFirstChild();
  }
  
  /**
   * Diz se o utilizador é administrador do workflow. 
   * @param engine ligação ao motor do workflow.
   * @return true caso seja administrador, false caso contrário.
   */    
    public static boolean isWorkFlowAdministrator(EngineGate engine) throws boRuntimeException
    {
        boolean result = false;
        boObject workflowAdministrator = getWorkFlowAdministrator(engine);
        if(workflowAdministrator != null && workflowAdministrator.getBoui() == engine.getBoManager().getPerformerBoui())
        {
            result = true;
        }
        return result;
    }
    
  /**
   * Devolve o administrador do workflow. 
   * @param engine ligação ao motor do workflow.
   * @return workflowAdministrator, boObject caso encontre, null caso contrário.
   */   
    public static boObject getWorkFlowAdministrator(EngineGate engine) throws boRuntimeException
    {
        boObject workflowAdministrator = null;              
        boObject performer = engine.getObject(engine.getBoManager().getPerformerBoui());
        if(performer != null && "SYSUSER".equals(performer.getAttribute("id").getValueString()))
        {
            workflowAdministrator = performer;
        }
        else
        {
            boObject admin = engine.getBoManager().getProgram().getAttribute("administrator").getObject();        
            if(admin == null)
            {
                boObject pdef = engine.getBoManager().getProgram().getAttribute("programDef").getObject();
                if(pdef != null)
                {
                    admin = pdef.getAttribute("administrator").getObject();
                }
                else
                {
                    admin = performer;
                }
            }
            if(admin != null)
            {           
                if(admin.getBoui() == performer.getBoui())
                {
                    workflowAdministrator = admin;
                }
            }
        }
        return workflowAdministrator;
    } 
  
  /**
   * Devolve a lista de procedimentos que podem ser executados, no contexto. 
   * @param engine ligação ao motor do workflow.
   * @return NodeList de procedimentos e null caso contrário.
   */     
    public static ArrayList getProcedures(EngineGate engine) throws boRuntimeException
    {
        ArrayList toReturn = new ArrayList();
        boObject program = engine.getBoManager().getProgram();
        long perfBoui = engine.getBoManager().getContext().getBoSession().getPerformerBoui();
        String procedures = program.getAttribute("procedures").getValueString();
        if(procedures != null && !"".equals(procedures))
        {
            try
            {
                ngtXMLHandler xml = new ngtXMLHandler(procedures);
                ngtXMLHandler nx = null;
                NodeList result = xml.getDocument().selectNodes("//procedure");
                Node node = null;
                String name = null;
                if(result != null)
                {                
                    for(int i=0; i < result.getLength(); i++)
                    {
                        node = result.item(i);
                        nx = new ngtXMLHandler(node);
                        name = nx.getAttribute("name");
                        if(((xwfEngineGate)engine).getManager().getControlFlow().hasAccessToProcedure(name, perfBoui,
                            ((XwfController)engine.getBoManager().getContext().getController()).getRuntimeActivity()))
//                        engine.getRuntimeActivity((XwfController)engine.getBoManager().getContext().getController())))
                        {
                            toReturn.add(nx);
                        }
                    }
                }
            }
            catch (XSLException e)
            {
                //ignorar
            }              
        }
        return toReturn;
    }
  /**
   * Devolve se um boObject foi usado como variavel de input num fluxo de trabalho ou não. 
   * @param object boObject a verificar se é usado num fluxo de trabalho.
   * @return  TRUE caso tenha sido usado, FALSE caso contrário.
   */       
    public static boolean isInputVariableInProgram(boObject object)
    {
        boolean result = false;
        StringBuffer sb = new StringBuffer("SELECT xwfVariable WHERE value in (SELECT xwfVarValue WHERE  (valueObject = ");
        sb.append(object.getBoui()).append(" or valueList = ").append(object.getBoui()).append("))");
//TODO a rever, muito importante        
//        sb.append(" and input = '1'");    
        boObjectList variables = boObjectList.list(object.getEboContext(), sb.toString(),false,false);
        if(variables.getRecordCount() > 0)
        {
            result = true;
        }
        return result;
    }  
    
    public static String produceStaticKey(xwfBoManager xbm, String name)throws boRuntimeException
    {
        return xbm.getProgram().getAttribute("programDef").getValueLong() +"#"+name;
    }
  /**
   * Devolve objecto principal numa actividade se possivel. 
   * @param activity xwfActivity para retirar o objecto principal.
   * @return object boObject caso encontre, null cc.
   */        
    public static boObject getMasterObject(EngineGate engine,boObject activity) throws boRuntimeException
    {
        boObject object = null;
        if(activity != null)
        {
            boObject variable = null;
            boObject value = null;        
            AttributeHandler attr = activity.getAttribute("message");
            if(attr != null)
            {
                variable = attr.getObject();
                if(variable != null)
                {
                    attr = variable.getAttribute("value");
                    if(attr != null)
                    {
                        value = attr.getObject();
                        if(value != null)
                        {
                            object = engine.getBoManager().getValueBoObject(value);                            
                        }                        
                    }
                }
            }        
//            if("xwfActivityReceive".equals(activity.getName()) ||
//               "xwfCreateReceivedMessage".equals(activity.getName()) ||
//               "xwfActivitySend".equals(activity.getName())
//            )
//            {
//                object = engine.getBoManager().getValueBoObject(activity.getAttribute("message").getObject().getAttribute("value").getObject());//.getAttribute(xwfHelper.VALUE_OBJECT).getObject();                                        
//            }
            else
            {
                boObjectList variables =  activity.getBridge("variables");
                variables.beforeFirst();
                if(variables.next())
                {
                    variable = variables.getObject();
                    if(variable != null)
                    {
                        value = variable.getAttribute("value").getObject();
                        if(value != null)
                        {
                            object = engine.getBoManager().getValueBoObject(value);    
                        }                        
                    }                    
                }                                        
            }
        }
        return object;
    }
  /**
   * Devolve a informação relativa a um boObject do xwf. 
   * @param object o boObject que compilar a informação contextual.
   * @return lista de Object caso encontre alguma informação e null caso contrário.
   */    
    public static Object[] getRelatedInformation(boObject object) throws boRuntimeException
    {
        Object[] result = null;
        Stack info = new Stack();
        if(object != null)
        {            
            boObject aux = null;
            boObject variable = null;
            boObject value = null;
            if("xwfWaitResponse".equals(object))
            {
                result = getRelatedInformation(object.getAttribute("sendActivity").getObject());
                if(result == null)
                {
                    for (int i = 0; i < result.length; i++) 
                    {
                        info.add(result[i]);
                    }
                }
                result = getRelatedInformation(object.getAttribute("receiveActivity").getObject());
                if(result == null)
                {                
                    for (int i = 0; i < result.length; i++) 
                    {
                        info.add(result[i]);
                    }
                }
                result = null;
            }
            AttributeHandler attr = object.getAttribute("message");
            if(attr != null)
            {
                variable = attr.getObject();
                if(variable != null)
                {
                    attr = variable.getAttribute("value");
                    if(attr != null)
                    {
                        value = attr.getObject();
                        if(value != null)
                        {
                            attr = value.getAttribute(xwfHelper.VALUE_OBJECT);
                            if(attr != null)
                            {                        
                                aux = attr.getObject();
                                if(aux != null)
                                {
                                    info.add(aux);
                                }
                            }
                        }
                    }
                }
            }
                        
            bridgeHandler bridge = null;
            String typeName = null;
            boObjectList variables =  object.getBridge("variables");
            variables.beforeFirst();
            while(variables.next())
            {
                variable = variables.getObject();
                value = variable.getAttribute("value").getObject();
                typeName = getTypeName(value);
                if(VALUE_OBJECT.equals(typeName))
                {
                    aux = value.getAttribute(typeName).getObject();
                    if(aux != null)
                    {
                        info.add(aux);   
                    }                    
                }
                else if(VALUE_LIST.equals(typeName))
                {
                    bridge = value.getBridge(VALUE_LIST);
                    bridge.beforeFirst();
                    while(bridge.next())
                    {
                        aux = bridge.getObject();
                        if(aux != null)
                        {
                            info.add(aux);                            
                        }
                    }
                }
                else if(VALUE_LOV.equals(typeName))
                {                    
                    long lovBoui = variable.getAttribute("lov").getValueLong();
                    boObject lov = null;
                    if(lovBoui != 0)
                    {
                        lov = boObject.getBoManager().loadObject(object.getEboContext(),lovBoui);
                    }
                                        
                    lovObject lovHandler=null;
                    if(lov != null)
                    {
                        lovHandler = LovManager.getLovObject( object.getEboContext(), lov.getAttribute("name").getValueString() );                    
                        if ( lovHandler != null)
                        {
                            lovHandler.beforeFirst();
                            while(lovHandler.next())
                            {
                                if (lovHandler.getCode().equals( value.getAttribute(VALUE_LOV).getValueString()))
                                {
                                    info.add(lovHandler.getDescription());
                                }
                            }                                        
                        }
                    }
                }
                else
                {
                    info.add(value.getAttribute(typeName).getValueString());   
                }                
                
            }
        }
        if(info.size() > 0)
        {
            result = new Object[info.size()];
            info.copyInto(result);            
        }
        return result;
    }
    
    public static String markUSID(Node pn)
    {
        String value = generateUID();
        markAtt(pn, "unique_sid", value);
        return value;
    }
    
    public static void markAtt(Node pn, String attName, String value)
    {
        Attr att = pn.getOwnerDocument().createAttribute(attName);
        att.setValue(value);
        ((XMLElement)pn).setAttributeNode(att);
    }
    
    public static String generateUID()
    {
        UID step_id = new UID();
        return step_id.toString().replace(':','_'); 
    }
    
  public static void objectStateSet(xwfBoManager xwfm, String stateValue)throws boRuntimeException
  {
    bridgeHandler bvars = xwfm.getProgram().getBridge("variables");
    bvars.beforeFirst();
    while(bvars.next())
    {
        String sstatic = bvars.getObject().getAttribute("staticKey").getValueString();
        if(sstatic != null && sstatic.length() > 0)
           continue;

        boObject var_obj = xwfm.getValueBoObject(bvars.getObject().getAttribute("value").getObject());
        if(var_obj != null && var_obj.exists())
        {
            boObjectList bl_pr = xwfm.listObject("SELECT xwfProgramRuntime WHERE boui <> "+xwfm.getProgBoui()+
                                            " and runningState <> 90 and variables.value.valueObject = "+var_obj.getBoui());
            if(bl_pr.getRowCount() == 0)
            {
                AttributeHandler att = var_obj.getAttribute("XWF_STATE");
                if(att != null)
                {
                    att.setValueString(stateValue);
                    att = var_obj.getAttribute("enddate");
                    if(att != null)
                    {
                        att.setValueDate(DateUtils.getNow());
                    }
                }
            }
        }
    }
    
  }
  
//  public static boolean programNotExists(XwfController controller) throws boRuntimeException
  public static boolean programNotExists(EngineGate engine) throws boRuntimeException
  {
      return !engine.getBoManager().getProgram().exists() && !engine.getBoManager().isInTest();
  }
  
  public static boolean needPressMenuQuestion(XwfController cont) throws boRuntimeException
  {
      boolean resp = false;
      boObject act = cont.getRuntimeActivity();
      if(act != null)
        if("xwfActivityReceive".equalsIgnoreCase(act.getName()) && !"close".equals(act.getStateAttribute("runningState").getValueString()))
            resp = true;
      return resp;
  }
  
  public static boolean getUserMenuAnswer(EboContext ctx)
  {
      return false;
  }
  
  public static boolean verifyParticipant(boObject performer, boObject partObject)
        throws boRuntimeException
    {
        bridgeHandler bh;

        if (performer.getBoui() == partObject.getBoui())
        {
            return true;
        }

        if (partObject.getAttribute("administrator") != null)
        {
            bh = partObject.getBridge("administrator");

            if (bridgeContainsBoui(bh, performer.getBoui()))
            {
                return true;
            }
        }

        if (performer.getAttribute("queues") != null)
        {
            bh = performer.getBridge("queues");

            if (bridgeContainsBoui(bh, partObject.getBoui()))
            {
                return true;
            }
        }

        if (performer.getAttribute("roles") != null)
        {
            bh = performer.getBridge("roles");

            if (bridgeContainsBoui(bh, partObject.getBoui()))
            {
                return true;
            }
        }

        if (performer.getAttribute("groups") != null)
        {
            bh = performer.getBridge("groups");

            if (bridgeContainsBoui(bh, partObject.getBoui()))
            {
                return true;
            }
        }

        return false;
    }
    
    private static boolean bridgeContainsBoui(bridgeHandler bh, long boui)
        throws boRuntimeException
    {
        boolean toRet = false;
        boBridgeIterator bit = bh.iterator();
        bit.beforeFirst();        
        while (bit.next() && !toRet)
        {
            if (bit.currentRow().getObject().getBoui() == boui)
            {
                toRet = true;
            }
        }
        return toRet;
    }
    
    public static String implementsInterface(boObject activity, String intfName) throws boRuntimeException
    {
        boObject program = activity.getAttribute("program").getObject();
        if(program == null)
        {
            String value = activity.getAttribute("implements_" + intfName).getValueString();
            
            if(value == null || "".equals(value))
                return "N";
            else
                return value;
        }
        boBridgeIterator varit = program.getBridge("variables").iterator();
        varit.beforeFirst();
        boObject xwfVar = null, xwfVarValue = null;
        ArrayList toRet = new ArrayList();
        while(varit.next())
        {
            xwfVar = varit.currentRow().getObject();
            xwfVarValue = xwfVar.getAttribute("value").getObject();
            if(xwfVarValue != null && 
                "0".equals(xwfVarValue.getAttribute("type").getValueString()))
            {
                if(xwfVarValue.getAttribute("valueObject").getObject() != null)
                {
                    toRet.add(xwfVarValue.getAttribute("valueObject").getObject().getName());
                }
            }
        }
        if("iESP_SMCE".equalsIgnoreCase(intfName))
        {
            if(toRet.indexOf("ESP_PedidoInterv") != -1 || toRet.indexOf("ESP_Solicitation")!=-1 )
            {
                return "S";
            }
        }
        else if("iGP_Proj".equalsIgnoreCase(intfName))
        {
            if(toRet.indexOf("GP_Projecto") != -1)
            {
                return "S";
            }
        }
        return "N";

    }
    
    public static String getProgActvFromMessage(EboContext boctx, long msgBoui)
    {
        return getProgActvFromMessage( boctx,  msgBoui, true );
    }
    
    public static String getProgActvFromMessage(EboContext boctx, long msgBoui, boolean checksecurity)
    {
            Connection cn = null;
            PreparedStatement pstm = null;
            ResultSet rslt = null;
            String toRet = null;
            try 
            {
                long actvBoui = -1, progBoui = -1;
                
                
                boObject obj = null;
                cn = boctx.getConnectionData();
                final String boql ="select a.program$, a.boui from activity a, xwfvariable var, xwfvarvalue valu where a.message$ = var.boui and var.value$ = valu.boui and valu.valueObject$ = ?";
                pstm = cn.prepareStatement( boql );
                pstm.setLong(1, msgBoui );
                rslt = pstm.executeQuery();
                while(rslt.next())
                {   
                    progBoui = rslt.getLong( 1 );
                    actvBoui = rslt.getLong( 2 );
                    boObject act = boObject.getBoManager().loadObject( boctx, actvBoui );
                    if ( !checksecurity || securityRights.hasRights( act , act.getName() ,boctx.getBoSession().getPerformerBoui()) && securityOPL.canRead(act))
                    {
                        toRet = progBoui + ":" + actvBoui; 
                        break;
                    }
                }                                                                
            }
            catch (Exception ex) 
            {
            } 
            finally 
            {
              try 
                {
                    if(rslt != null) rslt.close();
                    if(pstm != null) pstm.close();                            
                } 
                catch (Exception ex) 
                {
                }         
            }
            return toRet;
    }
    
    public static String getProgActvFromDoc(EboContext boctx, long docBoui)
    {
            Connection cn = null;
            PreparedStatement pstm = null;
            ResultSet rslt = null;
            String toRet = ":"+docBoui;
            try 
            {
                long actvBoui = -1, progBoui = -1;
                
                boObject docObj = boObject.getBoManager().loadObject(boctx, docBoui);
                if(docObj.getAttribute("msg").getValueLong() > 0)
                {
                    return getProgActvFromMessage(boctx, docObj.getAttribute("msg").getValueLong());
                }
                
                boObject obj = null;
                cn = boctx.getConnectionData();
                final String boql ="select a.program$, a.boui from activity a, xwfvariable var, xwfvarvalue valu where a.appxDoc$ = var.boui and var.value$ = valu.boui and valu.valueObject$ = ?";
                pstm = cn.prepareStatement( boql );
                pstm.setLong(1, docBoui );
                rslt = pstm.executeQuery();
                if(rslt.next())
                {   
                    progBoui = rslt.getLong( 1 );
                    actvBoui = rslt.getLong( 2 );
                    toRet = progBoui + ":" + actvBoui; 
                }                                                                
            }
            catch (Exception ex) 
            {
            } 
            finally 
            {
              try 
                {
                    if(rslt != null) rslt.close();
                    if(pstm != null) pstm.close();                            
                } 
                catch (Exception ex) 
                {
                }         
            }
            return toRet;
    }
}