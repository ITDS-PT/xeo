/*Enconding=UTF-8*/
package netgest.xwf.common;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.FileOutputStream;

import java.io.IOException;
import java.math.BigDecimal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import netgest.bo.ql.QLParser;
import netgest.bo.utils.UserUtils;
import netgest.utils.StringUtils;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.XMLParseException;
import netgest.bo.system.Logger;
import org.w3c.dom.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import netgest.bo.controller.Controller;
import netgest.bo.controller.ControllerFactory;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.def.boDefAttribute;
import netgest.bo.dochtml.docHTML;
import netgest.bo.impl.Ebo_TemplateImpl;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeMasterAttribute;
import netgest.bo.runtime.boConvertUtils;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectUpdateQueue;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.utils.objectSerialization;
import netgest.bo.system.*;


import netgest.utils.ngtXMLHandler;

import netgest.xwf.core.*;
import netgest.xwf.xwfEngineGate;


import oracle.xml.parser.v2.XMLDocument;
import netgest.xwf.EngineGate;
import org.xml.sax.SAXException;
/**
 * Classe que gere os objectos no contexto de um WorkFlow. Trabalha fundamentalmente com um docHTML e o boui do
 * programRuntime correspondente.
 */
public class xwfBoManager
{
  private docHTML bodoc;
  private long prog_boui=-1;
  private byte executionMode = xwfHelper.PROGRAM_EXEC_DEFAULT_MODE;
  private final static String[] NOT_SERIALIZABLE_OBJECTS = {"xwfActivity",
                                                          "xwfActivityChoice",
                                                          "xwfActivityCreateMessage",
                                                          "xwfActivityDecision",
                                                          "xwfActivityFill",
                                                          "xwfActivityReceive",
                                                          "xwfActivitySend",
                                                          "xwfAnnounce",
                                                          "xwfAnnounceDetails",
                                                          "xwfApprovalRule",
                                                          "xwfCreateReceivedMessage",
                                                          "xwfDecision",
                                                          "xwfDefActivity",
                                                          "xwfOption",
                                                          "xwfParticipant",
                                                          "xwfPerformerConfig",
                                                          "xwfProgram",
                                                          "xwfProgramDef",
                                                          "xwfProgramRuntime",
                                                          "xwfReassign",
                                                          "xwfSerialObject",
                                                          "xwfStarterConfig",
                                                          "xwfUserCallProgram",
                                                          "xwfVariable",
                                                          "xwfVarValue",
                                                          "xwfWait",
                                                          "xwfWaitResponse",
                                                          "xwfActivityTransfer"
                                                          };
//  private boolean inTest=false;
  private EboContext ec;  //casos extremos
  private xwfManager xman;  //casos extremos
//  private boObject p_program;  //casos extremos

    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.xwf.common.xwfBoManagerllo");

  public xwfBoManager(docHTML doc, long boui, byte execMode) throws boRuntimeException
  {
    bodoc = doc;
    prog_boui = boui;
    executionMode = execMode;
  }

  public xwfBoManager(docHTML doc, long boui) throws boRuntimeException
  {
    bodoc = doc;
    prog_boui = boui;
  }

  public xwfBoManager(docHTML doc, boObject program) throws boRuntimeException
  {
    bodoc = doc;
    if(program != null)
      prog_boui = program.getBoui();
  }

  public xwfBoManager(EboContext contx, boObject program) throws boRuntimeException
  {
    bodoc = null;
    ec = contx;
    if(program != null)
      prog_boui = program.getBoui();
  }

  public void setMode(byte mode)
  {
    executionMode = mode;
  }

  public byte getMode()
  {
    return executionMode;
  }

  protected boolean isInTest()
  {
    if(executionMode == xwfHelper.PROGRAM_EXEC_TEST_MODE)
        return true;
    else
        return false;
  }

  /**
   * Define o boui do programRuntime em questão
   * @param boui
   */
  public void setProgram(long boui)
  {
    prog_boui = boui;
  }

  /**
   * Selector do membro privado que contém o docHTML
   * @return docHTML utilizado
   */
  public docHTML getDocHTML()
  {
    return bodoc;
  }

  /**
   * Selector básico do boui do programa a correr
   * @return boui do programa
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public long getProgBoui() throws boRuntimeException
  {
    return prog_boui;
  }

  /**
   * Rotina que permite fazer o load de um objecto mediante o seu boui
   * @param boui  boui do objecto a obter
   * @return      objecto obtido
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public boObject getObject(long boui) throws boRuntimeException
  {
    boObject res=null;
    if(prog_boui > 0)
    {
      if(bodoc != null)
        res = bodoc.getObject(boui);
      else
        res = boObject.getBoManager().loadObject(ec , boui);
    }
    else
      if(bodoc != null)
        res = bodoc.getObject(boui);
      else
        res = boObject.getBoManager().loadObject(ec , boui);
    String name = res.getName();

    if((name.startsWith("xwfActivity") || res.getBoDefinition().getBoPhisicalMasterTable().equalsIgnoreCase("activity"))
    && !res.wasSerialChecked)
    {
      String actvname = res.getAttribute("label").getValueString();
      undoSerialization(res, false);
      res.wasSerialChecked = true;
      return res;
    }
    else
      if(res.getName().equals("xwfSerialObject"))
        return this.undoSerial(res);
      else
        return res;
  }

  /**
   * Permite obter o ProgramRuntime actual num objecto do tipo boObject
   * @return boObject do programRuntime
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public boObject getProgram()  throws boRuntimeException
  {
    boObject program = null;
    if(prog_boui > 0)
    {
      program = getObject(prog_boui);
    }
    return program;
  }

  /**
   * Rotina que permite fazer o load de um objecto mediante um boql
   * @param boql  query boql correspondente
   * @return      objecto resultante da pesquisa
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public boObject loadObject(String boql) throws boRuntimeException
  {
    boObject reto = null;
    if(bodoc != null)
    {
        reto = boObject.getBoManager().loadObject(bodoc.getEboContext(), boql);
    }
    else
      if(prog_boui > 0 || getContext() != null)
        reto = boObject.getBoManager().loadObject(getContext(), boql);
      else
        reto =  null;
 /*   if(inTest && reto = null)
    {
        boObjectList bol = this.listObject(boql);
        reto = bol.
    }*/
    return reto;
  }

  private void addPoolObjects(boObjectList list, String boql) throws boRuntimeException
  {
        boPoolManager poolm = getContext().getApplication().getMemoryArchive().getPoolManager();
        ArrayList enobj = (ArrayList)poolm.OwnedObjects.get(getContext().getPreferredPoolObjectOwner());
        QLParser qlp = list.getQLParser();
//        qlp.toSql(list.getMboql, getContext());
        String selectobj = qlp.getObjectName();
        String whereC = qlp.getWhereClause(boql, getContext());
        for(int i=0; i < enobj.size(); i++)
        {
            Object key = enobj.get(i);
            boPoolable o = poolm.getObjectById( key.toString());
            if ( o instanceof boObject )
            {
                boObject obj = (boObject)o;
                if(obj.getName().equalsIgnoreCase(selectobj) ||
                (selectobj.equalsIgnoreCase(obj.getBoDefinition().getBoSuperBo())))
                    list.inserRow(obj.getBoui());
            }
        }
  }
  /**
   * Rotina qye permite fazer load de uma lista de objectos mediante um boql
   * @param boql  query boql correspondente
   * @param securit indica a necessidade de serem usadas seguranças durante a pesquisa
   * @param cache   indica a necessidade de ser usada a cache durante a pesquisa
   * @return lista de objectos seleccionados
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public boObjectList listObject(String boql, boolean securit, boolean cache) throws boRuntimeException
  {
    boObjectList retbol = null;
    if(bodoc != null)
    {
        retbol= boObjectList.list(bodoc.getEboContext(), boql,1,99999,securit,cache);
    }
    else
    {
       if(prog_boui > 0)
        retbol= boObjectList.list(getContext(), boql,1,99999,securit,cache);
      else
        retbol= null;
    }
    if(isInTest())
    {
        addPoolObjects(retbol, boql);
    }
    return retbol;
  }

  /**
   * Rotina qye permite fazer load de uma lista de objectos mediante um boql
   * @param boql  query boql correspondente
   * @return lista de objectos seleccionados
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public boObjectList listObject(String boql) throws boRuntimeException
  {
    boObjectList retbol = null;
    if(bodoc != null)
    {
        retbol= boObjectList.list(bodoc.getEboContext(), boql, 1, 9999999);

    }
    else
       if(getContext() != null)
        retbol= boObjectList.list(getContext(), boql, 1, 9999999);
      else
        retbol= null;
    if(isInTest())
    {
        addPoolObjects(retbol, boql);
    }
    return retbol;
  }

  /**
   * Rotina qye permite fazer load de uma lista de objectos mediante um boql
   * @param boql  query boql correspondente
   * @param securit indica a necessidade de serem usadas seguranças durante a pesquisa
   * @return lista de objectos seleccionados
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public boObjectList listObject(String boql, boolean securit) throws boRuntimeException
  {
    boObjectList retbol = null;
    if(bodoc != null)
    {
        retbol= boObjectList.list(bodoc.getEboContext(), boql, 1, 9999999, securit);
    }
    else
     if(prog_boui > 0)
        retbol= boObjectList.list(getContext(), boql,  1, 9999999, securit);
      else
        retbol= null;
    if(isInTest())
    {
        addPoolObjects(retbol, boql);
    }
    return retbol;
  }

  /**
   * Rotina qye permite fazer load de uma lista de objectos mediante um array de bouis
   * @param type tipo de objectos a seleccionar
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public boObjectList listObject(long[] bouis, String type) throws boRuntimeException
  {
    if(bodoc != null)
    {
        return boObjectList.list(bodoc.getEboContext(), type, bouis );
    }
    else
      if(prog_boui > 0)
        return boObjectList.list(getContext(), type, bouis );
      else
        return null;
  }

  /**
   * Permite a criação de um novo objecto no contexto do workflow
   * @param objectType  tipo de objecto a ser criado
   * @return novo objecto criado
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public boObject createObject(String objectType) throws boRuntimeException
  {
    return boObject.getBoManager().createObject(getContext(), objectType);
  }

  /**
   * Permite a criação de um novo objecto no contexto do workflow
   * @param objectType  tipo de objecto a ser criado
   * @param boui_template template a aplicar no novo objecto
   * @return novo objecto criado
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public boObject createObject(String objectType, long boui_template) throws boRuntimeException
  {
    if(bodoc != null)
    {
        boObject o = boObject.getBoManager().createObject(bodoc.getEboContext(), objectType);
        boObject templ = getObject(boui_template);
        ((Ebo_TemplateImpl)templ).loadTemplate(o);
//        o.setTemplate(boui_template);
        return o;
    }
    else
      if(prog_boui > 0)
      {
        boObject o = boObject.getBoManager().createObject(getContext(), objectType);
        boObject templ = getObject(boui_template);
        ((Ebo_TemplateImpl)templ).loadTemplate(o);

//        o.setTemplate(boui_template);
        return o;
      }
      else
        return null;
  }

  public boObject createObject(String objectType, long boui_template, xwfECMAevaluator p) throws boRuntimeException
  {
    boObject retO = createObject(objectType, boui_template);
    processRefTemplate(retO, boui_template, p);
    return retO;
  }

  public boObject createObject(String objectType, boObject template, boObject actv) throws boRuntimeException
  {
    String usid = actv.getAttribute("unique_sid").getValueString();
    return createObject(objectType, template, usid);
  }

  private boObject createObject(String objectType, boObject template, String usid) throws boRuntimeException
  {
    boObject retO = createObject(objectType, template.getBoui());
    xwfECMAevaluator p = new xwfECMAevaluator(this, null);
    processRefTemplate(retO, template.getBoui(), p);
    return retO;
  }

  public void applyTemplate(boObject obj, long boui_template, boObject actv)throws boRuntimeException
  {
    boObject templ = getObject(boui_template);
    ((Ebo_TemplateImpl)templ).loadTemplate(obj);
    String usid = actv.getAttribute("unique_sid").getValueString();
    processRefTemplate(obj, boui_template, usid);
  }

  private void processRefTemplate(boObject obj, long boui_template, String usid)throws boRuntimeException
  {
    String prog_xml = getProgram().getAttribute("flow").getValueString();
    ngtXMLHandler n_flow = new ngtXMLHandler(prog_xml);
    Node actv_node=null;
    try
    {
      actv_node=n_flow.getDocument().selectSingleNode("//*[@unique_sid='"+usid+"']");
    } catch (Exception ex)
    {
      return;
    }
    xwfECMAevaluator p = new xwfECMAevaluator(this, actv_node);
    processRefTemplate(obj, boui_template, p);
  }

  public void processRefTemplate(boObject obj, long boui_template, xwfECMAevaluator p) throws boRuntimeException
  {
    boObjectList att_list = listObject("select Ebo_Template.mappingAttributes where boui = "+boui_template+" and mappingAttributes.value like '%#%'");
    String patterns = "#(\\D.*?)#";
    Pattern pat = Pattern.compile(patterns);
    att_list.beforeFirst();
    for(int i=0; i < att_list.getRowCount(); i++)
    {
      String att_name = att_list.getObject().getAttribute("objectAttributeName").getValueString();
      String att_value = obj.getAttribute(att_name).getValueString();
      Matcher m = pat.matcher(att_value);
      String transf=null;
      while (m.find())
      {
        String res = m.group(1);
        transf = res;
        try{
          transf = (String)p.eval(this, res+";");
        }catch(Exception e){
          transf=null;
          continue;};
//        att_value = att_value.replaceFirst(m.group(), transf);
        att_value = StringUtils.replacestr(att_value, m.group(), transf);
      }
      if(transf != null)
        obj.getAttribute(att_name).setValueString(att_value);

      att_list.next();
    }
  }
  /**
   * Permite a criação de um novo objecto no contexto do workflow
   * @param objectType  tipo de objecto a ser criado
   * @param boui_template template a aplicar no novo objecto
   * @return novo objecto criado
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public boObject createObject(String objectType, double boui_template) throws boRuntimeException
  {
    return createObject(objectType, doubleToLong(boui_template));
  }

  /**
   * Selector do contexto presente
   * @return contexto actual
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public EboContext getContext() throws boRuntimeException
  {
    if(bodoc != null)
    {
        return bodoc.getEboContext();
    }
    else
      if(prog_boui > 0)
        return getProgram().getEboContext();
      else
        return ec;
  }

  /**
   * Muito util função de transformar um double num long
   * @param x valor double
   * @return valor long correspondente
   */
  private long doubleToLong(double x)
  {
    return new Double(x).longValue();
  }
   /**
   * Permite gravar o objecto de forma permanente
   * @param boobj objecto a ser gravado
   * @throws netgest.bo.runtime.boRuntimeException
   */
   public void updateObject(boObject boobj) throws boRuntimeException
  {
    updateObject(boobj, false);
  }
  /**
   * Permite gravar o objecto de forma permanente
   * @param boobj objecto a ser gravado
   * @throws netgest.bo.runtime.boRuntimeException
   */
   public void updateObject(boObject boobj, boolean directUpdate) throws boRuntimeException
  {
    String y = boobj.getAttribute("label") != null ? boobj.getAttribute("label").getValueString():null;


    if(!isInTest())
    {
      try{
        boobj.update();
      }catch(Exception e)
      {
        try
        {
            this.serialization(this.getProgram(), false);
            if(!directUpdate)
                boobj.update();
        }catch(Exception e2)
        {
          this.serialization(boobj, true);
          if(!directUpdate)
                boobj.update();
        }
      }
    }
/*    else
    {
      XMLDocument x = objectSerialization.boObjectToXML(boobj);
      try{
        FileOutputStream fos = new FileOutputStream("c:\\xmdoc.xml");
        x.print(fos);
      }catch(Exception e){}
      boObject xparsed = objectSerialization.xmlToBoObject(x, getContext());
    }*/
  }

  /**
   * Selector do boui do actual utilizador
   * @return boui do actual utilizador
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public long getPerformerBoui() throws boRuntimeException
  {
    if(bodoc != null)
    {
      return bodoc.getEboContext().getBoSession().getPerformerBoui();
    }
    else
      if(prog_boui > 0)
        return getContext().getBoSession().getPerformerBoui();
      else
        return -1;
  }

  /**
   * Rotina que desencadeará o processo de serialização de objectos
   * @param obj   objecto por onde começar a análise de serialização
   * @param recursive booleano que define a necessidade de efectuar serialização nos objectos filhos de <code>obj</code>
   * @throws netgest.bo.runtime.boRuntimeException
   */
  private void serialization(boObject obj, boolean recursive) throws boRuntimeException
  {
    Hashtable ht = new Hashtable();
    doSerialWork(true,obj, ht, recursive);
  }
  /**
   * Rotina que desencadeará o processo de inverso à serialização de objectos
   * @param obj   objecto por onde começar a análise de serialização
   * @param recursive booleano que define a necessidade de efectuar serialização nos objectos filhos de <code>obj</code>
   * @throws netgest.bo.runtime.boRuntimeException
   */
  private void undoSerialization(boObject obj, boolean recursive) throws boRuntimeException
  {
    Hashtable ht = new Hashtable();
    doSerialWork(false,obj, ht, recursive);
  }
  /**
   * Função especifica de serialização
   * @param serialOp  tipo de operação a efectuar: <code>true</code> para a serialização, <code>false</code> para o processo inverso
   * @param obj       objecto a analisar
   * @param htref     hashtable de objectos analisados
   * @param recursive booleano que define a necessidade de efectuar serialização nos objectos filhos de <code>obj</code>
   * @throws netgest.bo.runtime.boRuntimeException
   */
  private void doSerialWork(boolean serialOp, boObject obj, Hashtable htref, boolean recursive) throws boRuntimeException
  {
        boolean flag=false;
        if(serialOp && !obj.isChanged())
          return;

        long[][] xobjqueue = obj.getUpdateQueue().getObjects();
        for (int i = 0; i < xobjqueue.length; i++)
        {
            if( xobjqueue[i][1] == boObjectUpdateQueue.MODE_SAVE_FORCED )
            {
              if(serialOp)
                decideSerial( new BigDecimal(Long.toString((xobjqueue[i][0]))) , htref , recursive);
              else
                decideUndoSerial( new BigDecimal(Long.toString((xobjqueue[i][0]))) , htref , recursive);
            }
            else if ( xobjqueue[i][1] == boObjectUpdateQueue.MODE_DESTROY_FORCED )
            {
              if(serialOp)
                decideSerial( new BigDecimal(Long.toString((xobjqueue[i][0]))) , htref , recursive);
              else
                decideUndoSerial( new BigDecimal(Long.toString((xobjqueue[i][0]))) , htref , recursive);
            }
        }

        Enumeration oEnum = obj.getAttributes().elements();
        BigDecimal refboui;
        while(oEnum.hasMoreElements())
        {
            AttributeHandler att = (AttributeHandler)oEnum.nextElement();
            if( att.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && !(att instanceof boBridgeMasterAttribute) )
            {
                if( att.getDefAttribute().getDbIsBinding() )
                {
                    if( att.getDefAttribute().getDbIsTabled())
                    {
                        BigDecimal[] refbouis = (BigDecimal[])att.getValueObject();
                        if( refbouis != null )
                        {
                            for (int i = 0; i < refbouis.length; i++)
                            {
                              long resS = 0;
                              if(serialOp)
                                resS = decideSerial( refbouis[i], htref, recursive );
                              else
                                resS = decideUndoSerial( refbouis[i], htref, recursive );
                              if(resS > 0)
                              {
                                att.setValueLong(resS);
                                if(serialOp)
                                  flag = true;
                              }
                            }
                        }
                    }
                    else
                    {
                        long resS = 0;
                        if(serialOp)
                          resS = decideSerial( (BigDecimal)att.getValueObject(), htref , recursive );
                        else
                          resS = decideUndoSerial( (BigDecimal)att.getValueObject(), htref , recursive );
                        if(resS > 0)
                        {
                          att.setValueLong(resS);
                          if(serialOp)
                                  flag = true;
                        }
                    }
                }
            }
        }

        boDefAttribute[] allatts = obj.getBoDefinition().getAttributesDef();

        for (int i = 0; i < allatts.length; i++)
        {
            if( allatts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && allatts[i].getMaxOccurs() > 1 && !allatts[i].getDbIsTabled() )
            {
                bridgeHandler bridge = obj.getBridge( allatts[i].getName() );
                if( bridge != null )
                {
                    boDefAttribute[] bridgeatts = bridge.getDefAttribute().getBridge().getBoAttributes();
                    bridge.beforeFirst();
                    while(bridge.next())
                    {
                        refboui = BigDecimal.valueOf(bridge.getObject().getBoui()) ;
                        if(serialOp)
                          decideSerial( refboui, htref , recursive );
                        else
                          decideUndoSerial( refboui, htref , recursive );
                        for (int z = 0; z < bridgeatts.length ; z++)
                        {
                            if( bridgeatts[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                            {
                                refboui = bridge.getAttribute( bridgeatts[z].getName() ).getObject() == null ? null : BigDecimal.valueOf(bridge.getAttribute( bridgeatts[z].getName() ).getObject().getBoui());
                                long resS = 0;
                                if(serialOp)
                                  resS = decideSerial( refboui , htref , recursive);
                                else
                                  resS = decideUndoSerial( refboui , htref , recursive);
                                if(resS > 0)
                                {
                                  bridge.setValue(resS);
                                  if(serialOp)
                                    flag = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if(flag)
          obj.update();
    }

  /**
   * Tomada de decisão sobre a necessidade de serializar o objecto
   * @param boui  boui do objecto a serializar
   * @param ht    hashtable de objectos analisados
   * @param recursive booleano que define a necessidade de efectuar serialização nos objectos filhos de <code>obj</code>
   * @return boui do objecto serializado. -1 em caso de falha
   * @throws netgest.bo.runtime.boRuntimeException
   */
    private final long decideSerial( BigDecimal boui, Hashtable ht , boolean recursive ) throws boRuntimeException
    {
        long serObj = -1;
        if( boui != null && ht.get(boui)==null )
        {
          boObject refobj = null;
          if(this.getDocHTML() != null)
          {
            try
            {
                refobj = this.getDocHTML().getObject( boui.longValue() );
            }catch(Exception e)
            {
                logger.finer("",e);
                refobj = null;
            }
          }
          else
            refobj = boObject.getBoManager().loadObject(getContext(), boui.longValue() );
          if(refobj != null && isSerializable(refobj.getName()) && !refobj.valid())
          {
              ht.put(boui, new Boolean(true));
              serObj = createSerial(refobj);
          }
          else
            ht.put(boui, new Boolean(false));
          if(refobj != null && (recursive || refobj.getName().equals("xwfVariable")))
            doSerialWork(true,refobj, ht, recursive);
        }

        return serObj;
    }

    private boolean isSerializable(String bodefName)
    {
        for (int i = 0; i < NOT_SERIALIZABLE_OBJECTS.length; i++)
        {
            if(NOT_SERIALIZABLE_OBJECTS[i].equalsIgnoreCase(bodefName))
            {
                return false;
            }
        }
        return true;
    }

    /**
   * criação do objecto serializado com base num objecto
   * @param boobj objecto a serializar
   * @return  boui do objecto serializado
   * @throws netgest.bo.runtime.boRuntimeException
   */
    private long createSerial(boObject boobj) throws boRuntimeException
    {
      XMLDocument x = objectSerialization.boObjectToXML(boobj);
      String strx = ngtXMLUtils.getXML( x );
//      ByteArrayOutputStream barrOS = new ByteArrayOutputStream();
//      try{
//        x.print(barrOS,"UTF-8");
//      }catch(Exception e){throw new boRuntimeException("xwfBoManager","createSerail", e);}

      boObject oldser = loadObject("select xwfSerialObject where uid="+boobj.getBoui()+"");

      boObject ser = createObject("xwfSerialObject");
      ser.getAttribute("program").setValueLong(this.getProgBoui());
      ser.getAttribute("uid").setValueLong(boobj.getBoui());
      ser.getAttribute("xmlSerial").setValueString( strx );
      if(oldser != null)
      {
          oldser.destroy();
      }
      return ser.getBoui();
    }

    /**
   * Tomada de decisão sobre a necessidade de realizar o processo inverso à serialização o objecto
   * @param boui  boui do objecto a serializar
   * @param ht    hashtable de objectos analisados
   * @param recursive booleano que define a necessidade de efectuar serialização nos objectos filhos de <code>obj</code>
   * @return boui do objecto serializado. -1 em caso de falha
   * @throws netgest.bo.runtime.boRuntimeException
   */
    private final long decideUndoSerial( BigDecimal boui, Hashtable ht , boolean recursive ) throws boRuntimeException
    {
        long serObj = -1;
        if( boui != null && ht.get(boui)==null && boui.longValue() > 0)
        {

          boObject refobj = null;
//          if(this.getDocHTML() != null)
//          {
//            try
//            {
//                refobj = this.getDocHTML().getObject( boui.longValue() );
//            }
//            catch (Exception e)
//            {
//                return -1;
//            }
//          }
//          else
//          {
            try
            {
                refobj = boObject.getBoManager().loadObject(getContext(), boui.longValue() );
            }
            catch (Exception e)
            {
                return -1;
            }
//          }
            if(refobj != null && refobj.getName().equals("xwfSerialObject"))
            {
              serObj = undoSerial(refobj).getBoui();
              ht.put(boui, new Boolean(true));
            }
            else
              ht.put(boui, new Boolean(false));

          if(recursive || refobj.getName().equals("xwfVariable") || refobj.getName().equals("xwfVarValue"))
            doSerialWork(false,refobj, ht, recursive);
        }

        return serObj;
    }

    /**
   * criação do objectocom base num objecto serializado
   * @param boobj objecto a serializado
   * @return  objecto criado com base na serialização
   * @throws netgest.bo.runtime.boRuntimeException
   */
    private boObject undoSerial(boObject boobj) throws boRuntimeException
    {

        CharArrayReader cr = new CharArrayReader(boobj.getAttribute("xmlSerial").getValueString().toCharArray());

        DOMParser dp = new DOMParser();
        dp.setPreserveWhitespace(false);

        try
        {
            dp.parse( cr );
        }
        catch (IOException e)
        {

        }
        catch (XMLParseException e)
        {

        }
        catch (SAXException e)
        {

        }
        XMLDocument xmldoc = dp.getDocument();

        ngtXMLHandler xhand = new ngtXMLHandler( xmldoc );
        boObject bo = objectSerialization.xmlToBoObject(xhand.getDocument(), getContext());
        return bo;
    }
  /**
   * Criação de um objecto do tipo xwfVariable com base num xwfParticipant
   * @param par_boui  boui do objecto xwfParticipant
   * @return boui da nova xwfVariable
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public long createVarFromPar(long par_boui) throws boRuntimeException
  {
    boObject par = getObject(par_boui);
    boObject var = createObject("xwfVariable");

    var.getAttribute("name").setValueString(par.getAttribute("name").getValueString());
    var.getAttribute("label").setValueString(par.getAttribute("label").getValueString());
    var.getAttribute("description").setValueString(par.getAttribute("description").getValueString());
    var.getAttribute("mode").setValueLong(par.getAttribute("mode").getValueLong());
    var.getAttribute("showMode").setValueLong(par.getAttribute("showMode").getValueLong());
    var.getAttribute("required").setValueString(par.getAttribute("required").getValueString());
    var.getAttribute("objectFilterQuery").setValueString(par.getAttribute("objectFilterQuery").getValueString());
    var.getAttribute("value").setObject(par.getAttribute("value").getObject());
    var.getAttribute("isParticipant").setValueString("1");
    var.getAttribute("isClone").setValueString("1");

    return var.getBoui();
  }
 /* public String getValueString(boObject object) throws boRuntimeException
  {
      String result = "";
      Object value = getValueObject(object);
      if(value != null)
      {
          result = value.toString();
      }
      return result;
  }*/
  /**
   * função que permite obter o boObjecto guardado pelo objecto xwfVarValue passado
   * @param object  xwfVarValue a extrair o valor
   * @return boObject presente no objecto xwfVarValue <code>object</code>
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public boObject getBoObject(boObject object)throws boRuntimeException
  {
    Object res = getValueObject(object);
    String boui = null;
    if(res != null)
    {
      boui = res.toString();
    }
    if(boui != null && boui.length() > 1)
    {
      return getObject(Long.parseLong(boui));
    }
    else
      return null;
  }

  /**
   * função que permite obter o valor guardado pelo objecto xwfVarValue passado, seja qual for o tipo de dados guardado
   * @param object  xwfVarValue a extrair o valor
   * @return valor guardado, no caso de ser um boObject será devolvido um BigDecimal com o boui do objecto
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public Object getValueObject(boObject object) throws boRuntimeException
  {
    Object result = null;
    AttributeHandler attr = null;
    String linkVar = object.getAttribute("linkVar").getValueString();
    String linkAtt = object.getAttribute("linkAttribute").getValueString();
    if(linkVar != null && linkVar.length()>1)
    {
      boObject rec = getVarObject(linkVar, object.getAttribute("unique_sid").getValueString());
      if(rec != null)
      {
          attr = rec.getAttribute(linkAtt);
          if(attr != null && attr.isObject())
          {
            result = attr.getValueObject();
          }
          else
          {
              result = rec.getBridge(linkAtt);
          }
      }
    }
    else
    {
        attr = getAttributeInContentManager(object);
        if(attr != null && attr.isObject())
        {
            result = attr.getValueObject();
        }
        else
        {
            result = getBridgeInContentManager(object);
        }
    }
    return result;
  }

  /**
   * Função que permite obter o valor guardado pelo objecto xwfVarValue passado, seja qual for o tipo de dados guardado,
   * sob a forma de uma String
   * @param object  xwfVarValue a extrair o valor
   * @return valor guardado, no caso de ser um boObject será devolvido uma string com o seu boui
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public String getValueString(boObject object) throws boRuntimeException
  {
    String linkVar = object.getAttribute("linkVar").getValueString();
    String linkAtt = object.getAttribute("linkAttribute").getValueString();
    if(linkVar != null && linkVar.length()>1)
    {
      boObject rec = getVarObject(linkVar, object.getAttribute("unique_sid").getValueString());
      if(rec != null)
        return rec.getAttribute(linkAtt).getValueString();
      else
        return null;
    }
    else
      return getAttributeInContentManager(object).getValueString();
  }

  /**
   * Função que permite aplicar a função getValueLong ao valor armazenado no xwfVarValue
   * @param object  xwfVarValue a extrair o valor
   * @return valor guardado
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public long getValueLong(boObject object) throws boRuntimeException
  {
    String linkVar = object.getAttribute("linkVar").getValueString();
    String linkAtt = object.getAttribute("linkAttribute").getValueString();
    if(linkVar != null && linkVar.length()>1)
    {
      boObject rec = getVarObject(linkVar, object.getAttribute("unique_sid").getValueString());
      if(rec != null)
        return rec.getAttribute(linkAtt).getValueLong();
      else
        return 0;
    }
    else
      return getAttributeInContentManager(object).getValueLong();
  }

  /**
   * Função que permite aplicar a função getValueDouble ao valor armazenado no xwfVarValue
   * @param object  xwfVarValue a extrair o valor
   * @return valor guardado
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public double getValueDouble(boObject object) throws boRuntimeException
  {
    String linkVar = object.getAttribute("linkVar").getValueString();
    String linkAtt = object.getAttribute("linkAttribute").getValueString();
    if(linkVar != null && linkVar.length()>1)
    {
      boObject rec = getVarObject(linkVar, object.getAttribute("unique_sid").getValueString());
      if(rec != null)
        return rec.getAttribute(linkAtt).getValueDouble();
      else
        return 0;
    }
    else
      return getAttributeInContentManager(object).getValueDouble();
  }

  /**
   * Função que permite aplicar a função getValueDate ao valor armazenado no xwfVarValue
   * @param object  xwfVarValue a extrair o valor
   * @return valor guardado
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public Date getValueDate(boObject object) throws boRuntimeException
  {
    String linkVar = object.getAttribute("linkVar").getValueString();
    String linkAtt = object.getAttribute("linkAttribute").getValueString();
    if(linkVar != null && linkVar.length()>1)
    {
      boObject rec = getVarObject(linkVar, object.getAttribute("unique_sid").getValueString());
      if(rec != null)
        return rec.getAttribute(linkAtt).getValueDate();
      else
        return null;
    }
    else
      return getAttributeInContentManager(object).getValueDate();
  }

  /**
   * Função que permite aplicar a função getObject ao valor armazenado no xwfVarValue
   * @param object  xwfVarValue a extrair o valor
   * @return valor guardado
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public boObject getValueBoObject(boObject object) throws boRuntimeException
  {
    if(object == null)
        return null;
    String linkVar = object.getAttribute("linkVar").getValueString();
    String linkAtt = object.getAttribute("linkAttribute").getValueString();
    boObject rec = null;
    if(linkVar != null && linkVar.length()>1)
    {
      rec = getVarObject(linkVar, object.getAttribute("unique_sid").getValueString());
      if(rec != null)
        return rec.getAttribute(linkAtt).getObject();
      else
        return null;
    }
    else
      rec = getAttributeInContentManager(object).getObject();
    if(rec != null && "xwfSerialObject".equals(rec.getName()))
    {
      rec = undoSerial(rec);
      setValueObjectInContentManager(object, rec.toString(), AttributeHandler.INPUT_FROM_USER);
    }
    return rec;
  }

  /**
   * Devolve o AttributeHandler do atributo que armazena o valor do xwfVarValue object
   * @param object  xwfVarValue a extrair o valor
   * @return atributo que contém o valor
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public AttributeHandler getAttributeInContentManager(boObject object) throws boRuntimeException
  {
      return object.getAttribute(xwfHelper.getTypeName(object));
  }
  /**
   * Devolve o bridgeHandler do objecto que armazena o valor do xwfVarValue object
   * @param object  xwfVarValue a extrair o valor
   * @return bridge que contém o valor
   * @throws netgest.bo.runtime.boRuntimeException
   */
  private bridgeHandler getBridgeInContentManager(boObject object) throws boRuntimeException
  {
      return object.getBridge(xwfHelper.VALUE_LIST);
  }
  /**
   * Permite definir o valor a ser armazenado  pelo objecto passando apenas a string representativa desse valor
   * @param object  xwfVarValue que irá guardar o valor
   * @param value   string representativa do valor a guardar
   * @param type    tipo de inserção realizada (ex: AttributeHandler.INPUT_FROM_INTERNAL)
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public void setValueString(boObject object, String value, byte type) throws boRuntimeException
  {
    setValueString(object, value, true, type);
  }

  /**
   * Permite definir o valor a ser armazenado  pelo objecto passando apenas a string representativa desse valor
   * @param object  xwfVarValue que irá guardar o valor
   * @param value   string representativa do valor a guardar
   * @param formula  indica a necessidade de serem aplicadas formulas após a atribuição ter sido feita
   * @param type    tipo de inserção realizada (ex: AttributeHandler.INPUT_FROM_INTERNAL)
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public void setValueString(boObject object, String value, boolean formula, byte type) throws boRuntimeException
  {
      setValueObjectInContentManager(object,value, type);
      setValueInXwf(object,value, formula, type);
  }

  /**
   * Permite definir o valor a ser armazenado  pelo objecto passando apenas o objecto representativo do seu valor
   * @param object  xwfVarValue que irá guardar o valor
   * @param value   objecto representativo do valor a guardar
   * @param type    tipo de inserção realizada (ex: AttributeHandler.INPUT_FROM_INTERNAL)
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public void setVarValueObject(boObject object, Object value, byte type) throws boRuntimeException
  {
    setVarValueObject(object, value, true, type);
  }
  /**
   * Permite definir o valor a ser armazenado  pelo objecto passando apenas o objecto representativo do seu valor
   * @param object  xwfVarValue que irá guardar o valor
   * @param value   objecto representativo do valor a guardar
   * @param formula  indica a necessidade de serem aplicadas formulas após a atribuição ter sido feita
   * @param type    tipo de inserção realizada (ex: AttributeHandler.INPUT_FROM_INTERNAL)
   * @throws netgest.bo.runtime.boRuntimeException
   */
  public void setVarValueObject(boObject object, Object value, boolean formulas, byte type) throws boRuntimeException
  {
    if(value == null)
    {
      setValueString(object, null, formulas, type);
      return;
    }
    int tc = new Long(object.getAttribute("type").getValueLong()).intValue();
    switch(tc)
      {
        case boDefAttribute.VALUE_BOOLEAN:
          {
            Boolean bo = (Boolean)value;
            if(bo.booleanValue())
              setValueString(object, "1", formulas, type);
            else
              setValueString(object, "0", formulas, type);
            break;
          }
        case boDefAttribute.VALUE_NUMBER:
          {
            setValueString(object, value.toString(), formulas, type);
            break;
          }
        case boDefAttribute.VALUE_CHAR:
          {
            setValueString(object, (String)value, formulas, type);
            break;
          }
        case boDefAttribute.VALUE_CLOB:
          {
            setValueString(object, (String)value, formulas, type);
            break;
          }
        case boDefAttribute.VALUE_DATE:
          {
            setValueString(object, new SimpleDateFormat("dd-MM-yyyy").format((Date)value), formulas, type);
            break;
          }
        case boDefAttribute.VALUE_DATETIME:
          {
            setValueString(object, new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss").format((Date)value), formulas, type);
            break;
          }
        case boDefAttribute.VALUE_UNKNOWN:
          {
              String card = object.getAttribute("maxoccurs").getValueString();
              if("1".equals(card))
              {
//                boObject xval = (boObject)value;
                if(value == null)
                  setValueString(object, null, formulas, type);
                else
                  setValueString(object, value.toString(), formulas, type);
              }
              else
              {
                setValueString(object, ((boObjectList)value).getValueString(), formulas, type);
              }
            break;
          }
      }
  }

  private void setValueInXwf(boObject object, String value, boolean formula, byte type) throws boRuntimeException
  {
    setValueObjectInXwf(object,value,formula,type);
  }
  private void setValueObjectInXwf(boObject object, String value, boolean formula, byte type) throws boRuntimeException
  {
      String usid = null;
      if(object.getName().equals("xwfVarValue"))
      {
        String linkVar = object.getAttribute("linkVar").getValueString();
        String linkAtt = object.getAttribute("linkAttribute").getValueString();
        usid = object.getAttribute("unique_sid").getValueString();
        if(linkVar != null && linkVar.length()>1)
        {
          boObject go = getVarObject(linkVar, usid);
          if(go == null)
          {
            go = createRefObject(linkVar, usid, type);
          }
          go.getAttribute(linkAtt).setValueString(value, type);
        }
      }
      if(formula)
      {
        usid = object.getAttribute("unique_sid").getValueString();
        if(usid != null && usid.length() > 0)
        {
            xwfControlFlow cf = null;
            if(bodoc != null)
    //            cf = ((XwfController)(bodoc.getController())).getEngine().getManager().getControlFlow();
                cf = getXwfManager().getControlFlow();
            else
                cf = this.xman.getControlFlow();
            xwfECMAevaluator e = cf.getXwfEval(cf.getNode(usid));
            String name = cf.getNode(usid).getAttribute("name", null);
            String linkVar = object.getAttribute("linkVar").getValueString();
            if(name != null && linkVar == null)
              e.varChange(this, object, name);

            if(bodoc != null)
    //            cf = ((XwfController)(bodoc.getController())).getEngine().getManager().getControlFlow();
                cf = getXwfManager().getControlFlow();
            else
                cf = this.xman.getControlFlow();
            xwfFormulaCalc formc = new xwfFormulaCalc(cf, this);
            formc.afterSetApplyFormula(object);
        }
      }
  }
  private void setValueObjectInContentManager(boObject object, String value, byte type) throws boRuntimeException
  {
      object.getAttribute(xwfHelper.getTypeName(object)).setValueString(value, type);
  }

  public boObject getVarObject(String name, String usid) throws boRuntimeException
  {
    boObject var = this.getXwfManager().getVariableWithUsid(name, usid);
    if(var == null) return null;
    boObject value = var.getAttribute("value").getObject();
    if(value == null) return null;
    String link = value.getAttribute("linkVar").getValueString();
    String linkAtt = value.getAttribute("linkAttribute").getValueString();
    boObject ret = null;
    if(link != null && link.length()>1)
    {
      value = getVarObject(link, usid);
      if(value == null)
        return null;
      else
      {
        ret = value.getAttribute(linkAtt).getObject();
      }
    }
    else
      ret = value.getAttribute("valueObject").getObject();
    if(ret!=null && ret.getName().equals("xwfSerialObject"))
    {
      boObject sobj = getObject(ret.getBoui());
      value.getAttribute("valueObject").setObject(sobj);
      return sobj;
    }
    else
      return ret;
  }

  private boObject createRefObject(String name, String usid, byte type) throws boRuntimeException
  {
    boObject var = this.getXwfManager().getVariableWithUsid(name, usid);
    boObject value = var.getAttribute("value").getObject();
    String objType = value.getAttribute("object").getObject().getAttribute("name").getValueString();
    boObject ret = createObject(objType);
    value.getAttribute("valueObject").setObject(ret);
    String link = value.getAttribute("linkVar").getValueString();
    String linkAtt = value.getAttribute("linkAttribute").getValueString();
    if(link != null && link.length()>1)
    {
      boObject new_value = getVarObject(link, usid);
      if(new_value == null)
      {
        new_value = createRefObject(link, usid, type);
      }
      new_value.getAttribute(linkAtt).setObject(ret);
    }
    return ret;
  }



  public void setXwfManager(xwfManager manag)
  {
    xman = manag;
  }

  public xwfManager getXwfManager()
  {
    if(xman != null)
      return xman;
    else
    {
        XwfController controller =  null;
        if(!XwfKeys.CONTROLLER_NAME_KEY.equals(bodoc.getController().getName()))
        {
            Controller auxController = ControllerFactory.getControllerByForce(bodoc,XwfKeys.CONTROLLER_NAME_KEY);
            if(auxController != null)
            {
                controller = (XwfController)auxController;
            }
        }
        else
        {
            controller = (XwfController)bodoc.getController();
        }
        return ((xwfEngineGate)controller.getEngine()).getManager();
    }
  }
    public void destroyObject(boObject object) throws boRuntimeException
    {
        object.destroy();
    }
    public boObject getLastActivityCreated(String activityName) throws boRuntimeException
    {
        boObject result = null;
        boObjectList list = this.listObject("SELECT xwfActivity WHERE program = " + getProgBoui() + " AND name = '" + activityName +"' ORDER BY SYS_DTCREATE DESC");
        list.beforeFirst();
        if(list.next())
        {
            result = list.getObject();
        }
        return result;
    }
}