/*Enconding=UTF-8*/
package netgest.xwf.core;

import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.runtime.*;
import netgest.bo.security.*;
import netgest.bo.utils.DateUtils;
import netgest.utils.*;
import netgest.bo.def.*;
import netgest.bo.dochtml.*;
import netgest.bo.*;
import netgest.xwf.StepControl;
import netgest.xwf.stepControllers.*;
import netgest.xwf.common.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
/**
 * Gestor das tarefas relacionadas com o programa em execução.
 * <p>Centraliza as operações efectuadas sobre o programa, como a sua criação e incialização, bem como outras 
 * operações realizadas sobre as tarefas.
 * <p>As ordens dadas ao motor passam primariamente por aqui.
 * 
 */
public class xwfManager 
{
/**
 * Log de registo de eventos
 */
  private static Logger logger = Logger.getLogger("netgest.xwf.core.xwfManager");
  /**
   * boManager do programa
   */
  private xwfBoManager bo_manag;
  /**
   * Instância do motor do workflow
   */
  private xwfControlFlow cf=null;
  /**
     * Construtor para executar um programa em modo de testes
     * <p>A informação passada serve essencialmente para construir o respectivo xwfBoManager
     * @throws netgest.bo.runtime.boRuntimeException
     * @param execMode  chave que define o modo de execução do programa. Em teste ou normal. (ver xwfHelepr)  
     * @param boui      boui da instnacia do programa (xwfProgramRuntime)
     * @param doc       docHTML da sessão
     */
  public xwfManager(docHTML doc, long boui, byte execMode) throws boRuntimeException
  {
    bo_manag = new xwfBoManager(doc, boui, execMode);
  }
  /**
     * Construtor mais usual do gestor
     * <p>A informação passada serve essencialmente para construir o respectivo xwfBoManager
     * @throws netgest.bo.runtime.boRuntimeException
     * @param boui  boui da instnacia do programa (xwfProgramRuntime)
     * @param doc   docHTML da sessão
     */
  public xwfManager(docHTML doc, long boui) throws boRuntimeException
  {
    bo_manag = new xwfBoManager(doc, boui);
  }
  /**
     * Construtor usual com passagem directa do objecto programa.
     * <p>A informação passada serve essencialmente para construir o respectivo xwfBoManager
     * @throws netgest.bo.runtime.boRuntimeException
     * @param program   instancia do programa (xwfProgramRuntime)
     * @param doc       docHTML da sessão
     */
  public xwfManager(docHTML doc, boObject program) throws boRuntimeException
  {
    bo_manag = new xwfBoManager(doc, program);
  }
  /**
     * Construtor que passa directamente o respectivo xwfBoManager
     * @throws netgest.bo.runtime.boRuntimeException
     * @param bo_manager    xwfBoManager incializado com o respectivo programa
     */
  public xwfManager(xwfBoManager bo_manager) throws boRuntimeException
  {
    bo_manag = bo_manager;
  }
  /**
     * Construtor para casos em que não existe sessão e respectivo docHTML.
     * <p>Comum em casos de execução através de robots.
     * <p>Ainda podem existir algumas deficiencias devido à não existência de docHTML 
     * @throws netgest.bo.runtime.boRuntimeException
     * @param program   instancia do programa (xwfProgramRuntime)
     * @param ctx       eboContext da presente sessão
     */
  public xwfManager(EboContext ctx, boObject program) throws boRuntimeException
  {
    bo_manag = new xwfBoManager(ctx, program);
    bo_manag.setXwfManager(this);
  }
/**
     * Cria uma nova instancia do program (xwfProgramRuntime) e aplica a definição do programa passado no argumento.
     * <p>Nota: Apenas faz a cópia de atributos não arrancando com o programa
     * @throws netgest.bo.runtime.boRuntimeException
     * @return devolve o número da instancia do programa (xwfProgramRuntime) que acabou de gerar. 
     * @param boui_prog_def     boui do objecto que contém a definição do programa (xwfProgramDef)
     */
  protected long createProgramWhithDef(long boui_prog_def)throws boRuntimeException
  {
    boObject def = bo_manag.getObject(boui_prog_def);
    boObject rt = bo_manag.createObject("xwfProgramRuntime");
    bo_manag.setProgram(rt.getBoui());
    rt.getAttribute("name").setValueString(def.getAttribute("name").getValueString(), AttributeHandler.INPUT_FROM_INTERNAL);
    rt.getAttribute("description").setValueString(def.getAttribute("description").getValueString());
    rt.getAttribute("flow").setValueString(def.getAttribute("flow").getValueString());
    rt.getAttribute("version").setValueString(def.getAttribute("version").getValueString());
    rt.getStateAttribute("runningState").setValue("config");
    rt.getAttribute("programDef").setValueLong(boui_prog_def);
    //vou dar permissão ao administrador
    boObject admin = def.getAttribute("administrator").getObject();
    if(XEOUserUtils.isXEOUser(admin) || 
        "workQueue".equals(admin.getName()) ||
        "workQueue".equals(admin.getBoDefinition().getBoSuperBo()) ||
        "Ebo_Group".equals(admin.getName()) ||
        "Ebo_Group".equals(admin.getBoDefinition().getBoSuperBo()) ||
        "Ebo_Role".equals(admin.getName())  || 
        "Ebo_Role".equals(admin.getBoDefinition().getBoSuperBo()))
    {
        if(!rt.getBridge("access").haveBoui(admin.getBoui()))
        {
            rt.getBridge("access").add(admin.getBoui());
        }
    }
    //vou dar permissões aos participantes
    boBridgeIterator bit = def.getBridge("participants").iterator();
    bit.beforeFirst();
    boObject varValue;
    boObject refObj = null;
    while(bit.next())
    {
        varValue = bit.currentRow().getObject();
        if(varValue != null)
        {
            refObj = varValue.getAttribute("valueObject").getObject();
            if(refObj != null)
            {
                if(XEOUserUtils.isXEOUser(refObj) || 
                    "workQueue".equals(admin.getName()) ||
                    "workQueue".equals(admin.getBoDefinition().getBoSuperBo()) ||
                    "Ebo_Group".equals(admin.getName()) ||
                    "Ebo_Group".equals(admin.getBoDefinition().getBoSuperBo()) ||
                    "Ebo_Role".equals(admin.getName())  || 
                    "Ebo_Role".equals(admin.getBoDefinition().getBoSuperBo()) )
                {
                    if(!rt.getBridge("access").haveBoui(refObj.getBoui()))
                    {
                        rt.getBridge("access").add(refObj.getBoui());
                    }
                }
            }
        }
    }
//    rt.update();
    return rt.getBoui();
  }
  /**
     * Cria e lança uma nova instancia de programa (xwfProgramRuntime) com base na definição e nos valores de input.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return devolve o número da instancia do programa (xwfProgramRuntime) que acabou de gerar.
     * @param boui_obj          boui do objecto que fez gerar o programa
     * @param vars              tabela com nome e valores de inicialização das variáveis do programa  
     * @param boui_prog_def     boui do objecto que contém a definição do programa (xwfProgramDef)
     */
  protected long createProgramWhithDef(long boui_prog_def, Hashtable vars, long boui_obj)throws boRuntimeException
  {
    long boui_prog_runt = createProgramWhithDef(boui_prog_def);
    initProgram(boui_prog_runt, vars, boui_obj);
    return boui_prog_runt;
  }
  /**
     * Localiza a versão activa do programa (xwfProgram). 
     * <p>A pesquisa será feita na bridge de versões.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return boui da versão activa do programa
     * @param prog  instancia do programa (xwfProgram)
     */
  public static long findVersion(boObject prog)throws boRuntimeException
  {
    bridgeHandler bversion = prog.getBridge("versions");
    bversion.beforeFirst();
    long boui_prog_def=-1;
    while(bversion.next())
    {
      if(bversion.getAttribute("active").getValueString().equals("1") || bversion.getRowCount()==1)
      {
        boui_prog_def = bversion.getObject().getBoui();
        break;
      }
    }
    if(boui_prog_def < 1)
      throw new boRuntimeException("xwfManager", "createProgram", new Exception("Error: There is no active program version!!!"));
    else
      return boui_prog_def;
  }
  /**
     * Cria uma nova instancia do programa (xwfProgramRuntime) com base nas definições do programa (xwfProgram) 
     * @throws netgest.bo.runtime.boRuntimeException
     * @return devolve o número da instancia do programa (xwfProgramRuntime) que acabou de gerar.
     * @param mode          modo de execução em que o programa se encontra
     * @param boui_prog     boui do objecto que aramzenas as definições do programa (xwfProgram)
     */
  public long createProgram(long boui_prog, byte mode)throws boRuntimeException
  {
    bo_manag.setMode(mode);
    boObject prog = bo_manag.getObject(boui_prog);
    long boui_prog_def = findVersion(prog);
    long boui_prog_runt = createProgramWhithDef(boui_prog_def);
    bo_manag.setProgram(boui_prog_runt);
    initProgram(boui_prog_runt, new Hashtable(), 0);
    return boui_prog_runt; 
  }
  /**
     * Cria uma nova instancia do programa (xwfProgramRuntime) inicializando-a com os valores de input passados.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return devolve o número da instancia do programa (xwfProgramRuntime) que acabou de gerar.
     * @param mode          modo de execução em que o programa se encontra
     * @param vars          tabela com nome e valores de inicialização das variáveis do programa
     * @param boui_prog     boui do objecto que aramzenas as definições do programa (xwfProgram)
     */
  public long createProgram(long boui_prog, Hashtable vars, byte mode)throws boRuntimeException
  {
    bo_manag.setMode(mode);
    boObject prog = bo_manag.getObject(boui_prog);
    long boui_prog_def = findVersion(prog);
    long boui_prog_runt = createProgramWhithDef(boui_prog_def);
    bo_manag.setProgram(boui_prog_runt);
    initProgram(boui_prog_runt, vars, 0);
    return boui_prog_runt;
  }
  /**
     * Cria uma nova instancia do programa (xwfProgramRuntime) inicializando-a com os valores de input passados.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return devolve o número da instancia do programa (xwfProgramRuntime) que acabou de gerar.
     * @param mode          modo de execução em que o programa se encontra
     * @param vars          tabela com nome e valores de inicialização das variáveis do programa
     * @param prog_name     identificação do programa. Poderá ser o nome pelo qual é conhecido ou até o seu boui
     */
  public long createProgram(String prog_name, Hashtable vars, byte mode)throws boRuntimeException
  {
    bo_manag.setMode(mode);
    long boui=0;
    try
    {
      boui = Long.parseLong(prog_name);
    }catch(Exception e){ boui = bo_manag.loadObject("select xwfProgram where name='"+prog_name+"'").getBoui();}
    if(boui > 0)
        return createProgram(boui, vars,mode);
    else
        return 0;
  }
  /**
     * Cria uma nova instancia do programa (xwfProgramRuntime) inicializando-a com os valores de input passados.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return devolve o número da instancia do programa (xwfProgramRuntime) que acabou de gerar.
     * @param mode          modo de execução em que o programa se encontra
     * @param boui_init_obj boui do objecto que fez gerar o programa
     * @param boui_prog     boui do objecto que aramzenas as definições do programa (xwfProgram)
     */
  public long createProgram(long boui_prog, long boui_init_obj, byte mode)throws boRuntimeException
  {
    bo_manag.setMode(mode);
    boObject prog = bo_manag.getObject(boui_prog);
    long boui_prog_def = findVersion(prog);
    long boui_prog_runt = createProgramWhithDef(boui_prog_def);
    bo_manag.setProgram(boui_prog_runt);
    initProgram(boui_prog_runt, new Hashtable(), boui_init_obj);
    return boui_prog_runt; 
  }
  /**
     * Aplica à actual instancia do programa (xwfProgramRuntime) o fluxo da definição e inicializando-a com os valores de input passados.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return devolve o número da instancia do programa (xwfProgramRuntime) que acabou de gerar.
     * @param mode          modo de execução em que o programa se encontra
     * @param boui_init_obj boui do objecto que fez gerar o programa
     * @param boui_prog     boui do objecto que aramzenas as definições do programa (xwfProgram)
     */
  public long createUsingProgram(long boui_prog, long boui_init_obj, byte mode)throws boRuntimeException
  {
    bo_manag.setMode(mode);
    boObject prog = bo_manag.getObject(boui_prog);
    long boui_prog_def = findVersion(prog);
    long boui_prog_runt = bo_manag.getProgBoui();
//    bo_manag.setProgram(boui_prog_runt);
    boObject rt = getBoManager().getObject(boui_prog_runt);
    boObject def = getBoManager().getObject(boui_prog_def);
    rt.getAttribute("name").setValueString(def.getAttribute("name").getValueString());
    rt.getAttribute("description").setValueString(def.getAttribute("description").getValueString());
    rt.getAttribute("flow").setValueString(def.getAttribute("flow").getValueString());
    rt.getAttribute("version").setValueString(def.getAttribute("version").getValueString());
    rt.getStateAttribute("runningState").setValue("config");
    rt.getAttribute("programDef").setValueLong(boui_prog_def);
    initProgram(boui_prog_runt, new Hashtable(), boui_init_obj);
    return boui_prog_runt; 
  }
  /**
     * Procede a incialização das variáveis e lança o programa
     * @throws netgest.bo.runtime.boRuntimeException
     * @param boui_obj      boui do objecto que fez gerar o programa
     * @param vars          tabela com nome e valores de inicialização das variáveis do programa
     * @param boui_prog_runt instancia do programa (xwfProgramRuntime) a iniciar
     */
  public void initProgram(long boui_prog_runt, Hashtable vars, long boui_obj)throws boRuntimeException
  {
    try{
      cf = new xwfControlFlow(this.bo_manag);
      cf.initVars(cf.getActualXml(), vars, boui_obj, "0", 0);
    }catch(Exception e){
        
      boObject prt = getBoManager().getProgram();
      long admin_boui = prt.getAttribute("administrator").getValueLong();
      if(admin_boui < 1)
      {
        boObject def = prt.getAttribute("programDef").getObject();
        admin_boui=def.getAttribute("administrator").getValueLong();
      }
    
      boObject xwfadmin = getBoManager().getObject(admin_boui);
      xwfActionHelper.errorHandle(getBoManager(), xwfadmin, e);
      
    }
  }
  /**
     * Lança um sub programa dentro do programa já existente.
     * <P>é executada quando o utilizador decide lançar outro fluxo antes ou paralelamente a tarefa que deve executar. 
     * @throws netgest.bo.runtime.boRuntimeException
     * @param boui_actv_def     boui da actividade em cujo o lançamento de sub programa foi efectuado
     */
  public void runSubProgram(long boui_actv_def) throws boRuntimeException
  {
    boObject actv_def = this.getBoManager().getObject(boui_actv_def);
    ngtXMLHandler new_actv_xml = xwfHelper.xmlDefinition(actv_def);
    
    boObject actv = actv_def.getAttribute("fromActivity").getObject();
    cf = this.getControlFlow();
    cf.clearEvals();
    if(actv != null)
    {
      if("false".equals(new_actv_xml.getAttribute("async")))
      {
        actv.getStateAttribute("runningState").setValue("wait");
        bo_manag.updateObject(actv);      
      }
      String unique_sid = actv.getAttribute("unique_sid").getValueString();
      cf.runSubProg(xwfHelper.STEP_SUB_PROGRAM, unique_sid, new_actv_xml);
    }else
      cf.runSubProg(xwfHelper.STEP_SUB_PROGRAM, null, new_actv_xml);
  }
  /**
     * Lança um procedimento dentro deste programa
     * @throws netgest.bo.runtime.boRuntimeException
     * @param fw_name   nome do procedimento
     * @param actv      actividade em cujo o lançamento de sub programa foi efectuado
     */
  public void runProcedure(boObject actv, String fw_name) throws boRuntimeException
  {
    cf = this.getControlFlow();
    ngtXMLHandler actv_node = null;
    String actv_usid = null;
    if(actv != null)
    {
      actv_usid = actv.getAttribute("unique_sid").getValueString();
      actv_node = cf.getNode(actv_usid);
    }
    ngtXMLHandler new_actv_xml = cf.prodNewXMLProcedure(fw_name, actv_node);
    cf.clearEvals();
    cf.runSubProg(xwfHelper.STEP_SUB_PROCEDURE, actv_usid, new_actv_xml);
  }
  /**
     * Devolve o xwfBoManager do programa. Selector de membro privado. 
     * @return xwfBomanager do programa
     */
  public xwfBoManager getBoManager()
  {
    return bo_manag;
  }
  /**
     * Devolve a classe motor do programa. Selector de membro privado.  
     * @throws netgest.bo.runtime.boRuntimeException
     * @return motor do programa
     */
  public xwfControlFlow getControlFlow() throws boRuntimeException
  {    
    if(cf == null)
      cf = new xwfControlFlow(bo_manag);  
    else
      cf.clearEvals();
    return cf;
  }
  /**
     * Executa o encerramento de uma tarefa. Opção desencada pelo utilizador.
     * @throws netgest.bo.runtime.boRuntimeException
     * @param unique_sid    código da actividade a encerrar
     */
  protected void finishedStep(String unique_sid) throws boRuntimeException
  {
    cf = this.getControlFlow();
    cf.clearEvals();
    cf.finishedStep(unique_sid);
  }
  /**
     * Executa o cancelamento de uma tarefa. Opção desencada pelo utilizador.
     * @throws netgest.bo.runtime.boRuntimeException
     * @param boui_actv    boui da actividade a cancelar
     */
  public void cancelStep(long boui_actv) throws boRuntimeException
  {
    boObject actv = bo_manag.getObject(boui_actv);
    actv.getStateAttribute("runningState").setValue("cancel");
    actv.getAttribute("done").setValueString("1");
    makeVarImage(actv);
    bo_manag.updateObject(actv);
  }

   /**
     * Executa o encerramento de uma tarefa em que foi tomada uma decisão. Opção desencada pelo utilizador.
     * @throws netgest.bo.runtime.boRuntimeException
     * @param option        opção escolhida pelo utilizador
     * @param unique_sid    código da actividade a encerrar
     */
  private void finishedStep(String unique_sid, String option) throws boRuntimeException
  {
    cf = this.getControlFlow();
    cf.clearEvals();
    cf.finishedStep(unique_sid, option, 0);
  }
  /**
     * Efectua um tratamento especial à tarefa antes desta acabar.
     * <P>Em muitos casos, quando a tarefa é terminada existem uma data de implicações para alem da passagem para o próximo passo,
     * como cálculos e atribuições de objectos, que se sucedem ao término da tarefa.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return <code>true</code> caso este tratamento não forçe uma interrupção do fecho da tarefa, <code>false</code> caso contrário
     * @param actv  actividade cujo processo de encerramento está a ser executado
     */
  private boolean specialTreatment(boObject actv)throws boRuntimeException
  {
    String usid = actv.getAttribute("unique_sid").getValueString();
    if(usid != null && !usid.equals("-1") && !usid.startsWith("0_"))
    {
        StepControl sc = getControlFlow().getStepControl(usid);
        if(sc != null)
            return sc.specialTreatment(actv, this);
    }
    return new xwfBasicStepController().specialTreatment(actv, this);
  }
    /**
     * Efectua um tratamento especial à tarefa de decisão antes desta acabar.
     * <P>Em muitos casos, quando a tarefa é terminada existem uma data de implicações para alem da passagem para o próximo passo,
     * como cálculos e atribuições de objectos, que se sucedem ao término da tarefa.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return <code>true</code> caso este tratamento não forçe uma interrupção do fecho da tarefa, <code>false</code> caso contrário
     * @param actv      actividade cujo processo de encerramento está a ser executado
     * @param option    opção escolhida pelo utilizador
     */
  private boolean specialTreatment(boObject actv, String option)throws boRuntimeException
  {
    String usid = actv.getAttribute("unique_sid").getValueString();
    if(usid != null && !usid.equals("-1") && !usid.startsWith("0_"))
    {
        StepControl sc = getControlFlow().getStepControl(usid);
        if(sc != null)
            return sc.specialTreatment(actv, option, this);
    }
    return new xwfBasicStepController().specialTreatment(actv, option, this);
  }
  /**
     * Executa o encerramento de uma tarefa. Opção desencada pelo utilizador.
     * @throws netgest.bo.runtime.boRuntimeException
     * @param boui_actv boui da actividade a encerrar
     */
  public void finishedStep(long boui_actv) throws boRuntimeException
  {
    finishedStep(boui_actv, true);
  }
  
  /**
     * Executa o encerramento de uma tarefa. Opção desencada pelo utilizador.
     * @throws netgest.bo.runtime.boRuntimeException
     * @param specialT      <code>true</code> caso seja necessário executar a rotina de tratamento posterior (specialTreatment),
     * <code>false</code> caso contrário
     * @param boui_actv     boui da actividade a encerrar
     */
  public void finishedStep(long boui_actv, boolean specialT) throws boRuntimeException
  {
    boObject actv = bo_manag.getObject(boui_actv);
    if(specialT)
      if( !specialTreatment(actv))
        return;
    actv.getStateAttribute("runningState").setValue("close");
    actv.getAttribute("done").setValueString("1");
    cf = this.getControlFlow();
    cf.clearEvals();
    makeVarImage(actv);
    bo_manag.updateObject(actv);
    cf.finishedStep(actv.getAttribute("unique_sid").getValueString());
  }
  /**
     * Executa o encerramento de uma tarefa em que foi tomada uma decisão. Opção desencada pelo utilizador. 
     * @throws netgest.bo.runtime.boRuntimeException
     * @param option        opção escolhida pelo utilizador
     * @param boui_actv     boui da actividade a encerrar
     */
  public void finishedStep(long boui_actv, String option) throws boRuntimeException
  {
    boObject actv = bo_manag.getObject(boui_actv);
    actv.getStateAttribute("runningState").setValue("close");
    actv.getAttribute("done").setValueString("1");
    actv.getAttribute("answer").setValueString(option);
    if( !specialTreatment(actv, option))
    {
        makeVarImage(actv);
        bo_manag.updateObject(actv);
        return;
    }
    cf = this.getControlFlow();
    cf.clearEvals();
    makeVarImage(actv);
    bo_manag.updateObject(actv);
    String usid = actv.getAttribute("unique_sid").getValueString();
    String partName = actv.getAttribute("partName").getValueString();
    cf.finishedStep(usid, option, boui_actv);
  }
  /**
     * Realiza a duplicação de variáveis de forma a que a actividade possua um histórico do estado das actividades
     * @throws netgest.bo.runtime.boRuntimeException
     * @param actv  actividade cuja a acção se desenrola 
     */
  private void makeVarImage(boObject actv)throws boRuntimeException
  {
    recalcFormulas();
    bridgeHandler vars = actv.getBridge("variables");
    String cloneBouis="";
    long[] brdg_boui = actv.getAttribute("variables").getValuesLong();
    int i=0;
    vars.beforeFirst();
    while(vars.next())
    {
      String clone_val = vars.getObject().getAttribute("isClone").getValueString();
      boObject cloneBo = null;
      if(!(clone_val != null && !clone_val.equals("0")))
        cloneBo = vars.getObject().cloneObject();
      else
        cloneBo = vars.getObject();
      cloneBo.getAttribute("value").setObject(vars.getObject().getAttribute("value").getObject().cloneObject());
      vars.setValue(cloneBo.getBoui());
      i++;
    }
  }
  /**
     * Devolve a estrutura XML que contém o fluxo de trabalho da versão activa do program
     * @throws netgest.bo.runtime.boRuntimeException
     * @return estrutura XML do fluxo de trabaho do programa
     * @param program_name  identificação do programa que poderá ser o seu nome ou o seu boui
     */
  public String getVersionFlow(String program_name)throws boRuntimeException
  {
    String ret = null;
    boObject p = null;
    try
    {
      long pboui = Long.parseLong(program_name);
      p = getBoManager().getObject(pboui);
    }catch(Exception e){
      p = bo_manag.loadObject("select xwfProgram where name='"+program_name+"'");
    }
    if(p != null && p.exists())
    {
      long boui_prog_def = this.findVersion(p);
      if(boui_prog_def > 0)
      {
        boObject ver = bo_manag.getObject(boui_prog_def);
        if(ver != null && ver.exists())
        {
          ret = ver.getAttribute("flow").getValueString();
        }
      }
    }
    return ret;
  }
  /**
     * Avalia uma expressão XEP no ambito do programa actual tem por base a actividade passada
     * @throws netgest.bo.runtime.boRuntimeException
     * @return objecto resultante da avaliação
     * @param actv_boui actividade na qual a avaliação é pedida
     * @param expres    expressão a avaliar
     */
  public Object expressionEval(String expres, long actv_boui) throws boRuntimeException
  {
    cf = this.getControlFlow();
    boObject actv = bo_manag.getObject(actv_boui);
    String usid = actv.getAttribute("unique_sid").getValueString();
    ngtXMLHandler nxml = cf.getNode(usid);
    xwfECMAevaluator e = cf.getXwfEval(nxml);
    return e.eval(bo_manag, expres);
  }
  /**
     * Obtém a variável (xwfVariable) do programa mediante o contexto em que a actividade é executada
     * @throws netgest.bo.runtime.boRuntimeException
     * @return devolve a variavel indicada pelo nome dentro do contexto da actividade
     * @param actv_boui boui da actividade em que é pedida a variável
     * @param name      nome da variável a obter
     */
  public boObject getVariableInContext(String name, long actv_boui) throws boRuntimeException
  {
    if(cf == null)
      cf = new xwfControlFlow(bo_manag);
    boObject actv = bo_manag.getObject(actv_boui);
    String usid = actv.getAttribute("unique_sid").getValueString();
    ngtXMLHandler nxml = cf.getNode(usid);
    return bo_manag.getObject(xwfHelper.getContextVar(nxml, name));
  }
  /**
     * Obtém a variável (xwfVariable) do programa mediante o contexto em que usid está enquadrado
     * @throws netgest.bo.runtime.boRuntimeException
     * @return devolve a variavel indicada pelo nome dentro do contexto do usid
     * @param usid  identificação de um passo do contexto de avaliação
     * @param name  nome da variável a obter
     */
  public boObject getVariableWithUsid(String name, String usid) throws boRuntimeException
  {
    if(cf == null)
      cf = new xwfControlFlow(bo_manag);
    ngtXMLHandler nxml = cf.getNode(usid);
    return bo_manag.getObject(xwfHelper.getContextVar(nxml, name));
  }
  /**
     * Cria uma actividade de manipulação de mensagem neste programa para o actual utlizador.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return instancia do objecto do tipo actividade de mensagem que acabou de criar
     * @param type      nome do objecto da mensagem
     * @param message   objecto do tipo mensagem a anexar a actividade
     * @param label     assunto da tarefa a criar
     * @param actv_name nome do objecto do tipo actividade a criar
     */
  public boObject createMessageActivity(String actv_name, String label, boObject message, String type) throws boRuntimeException
  {
    return createMessageActivity(actv_name, label, message, type, getBoManager().getProgBoui(), getBoManager().getPerformerBoui());
  }
  /**
     * Cria uma actividade de manipulação de mensagem num programa para um utlizador.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return instancia do objecto do tipo actividade de mensagem que acabou de criar
     * @param prog_boui program onde a tarefa deve ser incluida
     * @param recv_boui boui para quem a tarefa deve ser assignada 
     * @param type      nome do objecto da mensagem
     * @param message   objecto do tipo mensagem a anexar a actividade
     * @param label     assunto da tarefa a criar
     * @param actv_name nome do objecto do tipo actividade a criar
     */
  public boObject createMessageActivity(String actv_name, String label, boObject message, String type, long prog_boui, long recv_boui) throws boRuntimeException
  {
        return createMessageActivity(actv_name, label, message, type, prog_boui, recv_boui, true, true, true, true);
  }
     
  public boObject createMessageActivity(String actv_name, String label, boObject message, String type, long prog_boui, 
                                        long recv_boui, boolean showTask, boolean showReassign, boolean showProcess, boolean showWorkFlowArea) throws boRuntimeException
  {
        boObject actv = bo_manag.createObject(actv_name);
        String name = label;
        if(name != null && name.length() >= 200)
        {
            name = name.substring(0, 190) + "(...)";
        }
        actv.getAttribute("name").setValueString(name);
        if(label != null && label.length() >= 3000)
        {
            label = label.substring(0, 2990) + "(...)";
        }
        actv.getAttribute("label").setValueString(label, AttributeHandler.INPUT_FROM_INTERNAL);
    
        if(prog_boui > 0)
        {
            actv.getAttribute("program").setValueLong(prog_boui);
        }
        actv.getStateAttribute("runningState").setValue("create");
        actv.getAttribute("sid").setValueString("-1");
        actv.getAttribute("unique_sid").setValueString("-1");
        actv.getAttribute("optional").setValueString("1");
        actv.getAttribute("done").setValueString("0");
        actv.getAttribute("assignedQueue").setValueLong(recv_boui);
        actv.getAttribute("showTask").setValueString(showTask ? "1":"0");
        actv.getAttribute("showReassign").setValueString(showReassign ? "1":"0");
        actv.getAttribute("showWorkFlowArea").setValueString(showWorkFlowArea ? "1":"0");
        actv.getAttribute("showProcess").setValueString(showProcess ? "1":"0");
        
        boObject varValue = bo_manag.createObject("xwfVarValue");
        varValue.getAttribute("program").setValueLong(bo_manag.getProgBoui());
        varValue.getAttribute("type").setValueLong(0); 
        
        boObjectList list = boObjectList.list(getBoManager().getContext(), "select Ebo_ClsReg where name = '"+ type +"'");
        list.beforeFirst();
        long boui_cls = 0;
        if(list.next())
        {
            boui_cls = list.getObject().getBoui();
        }
    //        long boui_cls = getBoManager().loadObject("select Ebo_ClsReg where name = '"+ type +"'").getBoui();
        varValue.getAttribute("object").setValueLong(boui_cls);
        varValue.getAttribute("minoccurs").setValueLong(0);
        varValue.getAttribute("maxoccurs").setValueLong(1);
        if(message != null)
          varValue.getAttribute("valueObject").setValueLong(message.getBoui());
        
        boObject vo = bo_manag.createObject("xwfVariable");
        vo.getAttribute("name").setValueString("message");
        vo.getAttribute("label").setValueString("Mensagem", AttributeHandler.INPUT_FROM_INTERNAL);
        vo.getAttribute("isClone").setValueString("1");
        vo.getAttribute("mode").setValueLong(1);  
        vo.getAttribute("showMode").setValueLong(0);    
        vo.getAttribute("required").setValueString("1");
        vo.getAttribute("value").setValueLong(varValue.getBoui());
        /*if(actv_name.equals("xwfCreateReceivedMessage"))
        {
            vo.getAttribute("templateMode").setValueLong(10);
            vo.getAttribute("keyWords").setValueString("CreateReceivedMessage");
            if(message == null)
                varValue.getAttribute("valueObject").setObject(getBoManager().createObject(type));
        }*/
        
        actv.getAttribute("message").setValueLong(vo.getBoui());
        if("xwfActivityReceive".equals(actv_name) || "xwfActivityReceive".equals(actv_name))
        {
            actv.getAttribute("CREATOR").setValueObject(null, AttributeHandler.INPUT_FROM_USER);
            actv.getAttribute("CREATOR").setInputType(AttributeHandler.INPUT_FROM_USER);
        }
        xwfActionHelper.givePrivilegesToProgram(actv,prog_boui);
//        xwfAnnounceImpl.addAnnounce(actv.getAttribute("label").getValueString(), actv.getAttribute("assignedQueue").getObject(), actv, bo_manag);        
        return actv;    
  }
  
    /**
     * Cria uma actividade espera de resposta a envio de mensagem num programa para um utlizador.
     * @throws netgest.bo.runtime.boRuntimeException
     * @return instancia do objecto do tipo actividade de mensagem que acabou de criar
     * @param sendActv_boui boui da actividade de envio pela qual esperamos resposta
     * @param prog_boui program onde a tarefa deve ser incluida
     * @param recv_boui boui para quem a tarefa deve ser assignada 
     * @param type      nome do objecto da mensagem
     * @param message   objecto do tipo mensagem a anexar a actividade
     * @param label     assunto da tarefa a criar
     */
  public boObject createWaitRespActivity(String label, boObject message, String type, long recv_boui, long prog_boui, long sendActv_boui) throws boRuntimeException
  {
        boObject actv = bo_manag.createObject("xwfWaitResponse");
        actv.getAttribute("name").setValueString(label);
        actv.getAttribute("label").setValueString(label,AttributeHandler.INPUT_FROM_INTERNAL);
    
        if(prog_boui > 0)
        {
            actv.getAttribute("program").setValueLong(prog_boui);
        }
        actv.getStateAttribute("runningState").setValue("create");
        actv.getAttribute("sid").setValueString("-1");
        actv.getAttribute("unique_sid").setValueString("-1");
        actv.getAttribute("optional").setValueString("1");
        actv.getAttribute("done").setValueString("0");
        long o = message.getAttribute("from").getObject().getAttribute("refObj").getValueLong();
        actv.getAttribute("assignedQueue").setValueLong(o);
        actv.getAttribute("showTask").setValueString("1");

        actv.getAttribute("sendActivity").setValueLong(sendActv_boui);
        actv.getAttribute("waitFrom").setValueLong(recv_boui);
        xwfActionHelper.givePrivilegesToProgram(actv,prog_boui);

        return actv;
  }
  /**
     * Recalcula os valores das variáveis e participantes do programa mediante as fórmulas que estejam definidas 
     * @throws netgest.bo.runtime.boRuntimeException
     */
  public void recalcFormulas() throws boRuntimeException
  {
    xwfControlFlow cf = this.getControlFlow();
    cf.clearEvals();
    xwfFormulaCalc xf = new xwfFormulaCalc(cf, this.bo_manag);
    xf.applyFormulas(bo_manag.getProgram().getBridge("variables"));
    xf.applyFormulas(bo_manag.getProgram().getBridge("participants"));
    ngtXMLHandler xml = cf.getActualXml();
    if(xml != null && xml.getNode()!=null)
      xf.setProgLabelFormula(new ngtXMLHandler(cf.getActualXml().getDocument().getDocumentElement()));
  }
  /**
     * Cancela o programa em execução. 
     * @throws netgest.bo.runtime.boRuntimeException
     * @return <code>true</code> em caso de sucesso, <code>false</code> em caso contrário
     */
  public boolean cancelProgram() throws boRuntimeException
  {
    return xwfActionHelper.interruptProgram("cancel", getBoManager(), getControlFlow());
  }
  

}