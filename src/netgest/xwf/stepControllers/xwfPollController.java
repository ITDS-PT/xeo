/*Enconding=UTF-8*/
package netgest.xwf.stepControllers;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.xwf.StepControl;
import netgest.xwf.common.xwfHelper;
import netgest.xwf.core.*;
import oracle.xml.parser.v2.XMLElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import netgest.xwf.common.xwfBoManager;
import netgest.bo.runtime.*;
import netgest.xwf.common.xwfFunctions;
import netgest.utils.ngtXMLHandler;
import org.w3c.dom.NodeList;
import netgest.bo.xep.Xep;
/**
 * Gestor do passo de votaçao do workflow (StepController) 
 */
public class xwfPollController implements StepControl
{
    /**
     * Contém a maioria das rotinas básicas de processamento de steps
     */
    private xwfStepExec st_exe; 
    
    /**
     * Construtor por defeito, não inicia nenhum membro privado
     */
    public xwfPollController()
    {
    }
    
    /**
     * Construtor que irá iniciar a informação necessária para o processamento do passo
     * @throws netgest.bo.runtime.boRuntimeException Excepções relacionadas com a manipulação de Objectos
     * @param cf        Controlo de Fluxos que está a gerir o fluxo
     * @param ecmaeval  Evaluador de expressões iniciado com o contexto do actual step
     * @param step      Nó XML com o step que necessita de tratamento
     * @param xbm       BoManager usado para gerir os objectos deste fluxo
     */
    public xwfPollController(xwfBoManager xbm, Node step, xwfECMAevaluator ecmaeval, xwfControlFlow cf) throws boRuntimeException
    {
        st_exe = new xwfStepExec(xbm, step, ecmaeval, cf);
    }
    
      /**
   * Rotina principal que está responsável pelo lançamento do respectivo passo.
   * Lançará uma actividade para cada um dos participantes que constem na definição do XML e criará o objecto
   * que fará a gestão da votação com base na definição associada.
   * @return devovlerá sempre <code>false</code> pois trata-se de uma operação do utilizador 
   * @throws netgest.bo.runtime.boRuntimeException Excepções ao nível da execução do passo
   */
    public boolean execStep() throws boRuntimeException
    {
        String type = "xwfActivityPoll";
        ngtXMLHandler nxml = new ngtXMLHandler(st_exe.getStepNode());
        nxml = st_exe.contextReplacement(nxml);
        String name = nxml.getNodeName();
        boObject act = st_exe.createActivity(nxml, type, "create");
        String quest = nxml.getChildNodeText("question", "");
        act.getAttribute("question").setValueString(quest);
        quest = nxml.getChildNodeText("defPoll", "");
        ngtXMLHandler nparts = nxml.getChildNode("participants");
        ngtXMLHandler[] parts = nparts.getChildNodes();
        boObject prun = null;        
        if(quest.length() > 0)
        {
            prun = st_exe.getBoManager().createObject("PollRun");
            long boui_def = Long.parseLong(quest);
            prun.getAttribute("definition").setValueLong(boui_def);
            prun.getAttribute("program").setValueLong(st_exe.getBoManager().getProgBoui());
            prun.getAttribute("universe").setValueLong(parts.length);
            boObject bdef = st_exe.getBoManager().getObject(boui_def);
            bridgeHandler boption = bdef.getBridge("options");
            boption.beforeFirst();
            bridgeHandler boptrun = prun.getBridge("options");
            while(boption.next())
            {
                boptrun.add(boption.getCurrentBoui());
                act.getBridge("options").add(boption.getCurrentBoui());
                boptrun.getAttribute("votes").setValueString("0");
                boptrun.getAttribute("percent").setValueString("0");
            }
        }
        
        for(int i=0; i<parts.length; i++)
        {
            boObject nact = act.cloneObject();
            String partname = parts[i].getAttribute("name");
            if(partname != null)
            {
                st_exe.setAssignedTo(nxml, nact, partname);
                nact.getAttribute("partName").setValueString(partname);
            }
            nact.getAttribute("pollObj").setObject(prun);
            xwfAnnounceImpl.addAnnounce(nact.getAttribute("label").getValueString(), nact.getAttribute("assignedQueue").getObject(), nact, st_exe.getBoManager());
            st_exe.getBoManager().updateObject(nact);
        }
        return false;
    }
    
    /**
     * Sem qualquer tipo de processamento pois todo ele é realizado no método special treatment
     * @throws netgest.bo.runtime.boRuntimeException Excepções relacionadas com a manipulação de Objectos
     * @param cf        Controlo de Fluxos que está a gerir o fluxo
     * @param option    opção escolhida pelo utilizador
     * @param ngtn      Nó XML do respectivo step
     */
    public void finishedStep(ngtXMLHandler ngtn, String option, xwfControlFlow cf) throws boRuntimeException
    {
        
    }
    
   /**
     * Apesar deste ser um step de escolha esta função é chamada internamente quando se processa o avanço do fluxo.
     * @throws netgest.bo.runtime.boRuntimeException Excepções relacionadas com a manipulação de Objectos ou do XML
     * @param cf        Controlo de Fluxos que está a gerir o fluxo
     * @param ngtn      Nó XML do step a terminar
     */
  public void finishedStep(ngtXMLHandler ngtn, xwfControlFlow cf) throws boRuntimeException
  {
      if("done".equalsIgnoreCase(ngtn.getAttribute("pointer")))
      {
          Node nextpn = cf.findNextStep(ngtn.getNode(), false);
          if(nextpn == null)
          {
            cf.finishFlow();
            return;
          }
          cf.markPointer(nextpn, "start");
          cf.incCounter(nextpn);
      }
  }
  
  /**
     * Antes de dar por concluido o step é chamado este método que fará o tratamento necessario à actividade.
     * No caso da votação este método não é invocado.
     * @throws netgest.bo.runtime.boRuntimeException Excepção na manipulação dos objectos no tratamento
     * @return retorna sempre <code>true</code>  
     * @param manag Manager que está a processar o pedido de fim da actividade
     * @param actv  Actividade concluida pelo utilizador
     */
  public boolean specialTreatment(boObject actv, xwfManager manag) throws boRuntimeException
  {
      return true;
  }
  
  /**
     * Antes de dar por concluido o step é chamado este método que fará o tratamento necessario à actividade.
     * Acrescentará o actual voto e recalculará todos os cálculos gerais da votação após mais este voto.
     * Caso a votação tenha chegado a um resultado iniciará o próximo passo a se realizar.
     * @throws netgest.bo.runtime.boRuntimeException Excepção na manipulação dos objectos no tratamento
     * @return retorna <code>true</code> caso seja invocado no contexto de uma aprovação, retorna <code>false</code> 
     * em todos os outros casos    
     * @param manag Manager que está a tratar do pedido de finalização do step
     * @param option Opção escolhida pelo utilizador
     * @param actv  actividade terminada
     */
  public boolean specialTreatment(boObject actv, String option, xwfManager manag) throws boRuntimeException
  {
  
    boObject dec = null;
    bridgeHandler vars = actv.getBridge("variables");
    vars.beforeFirst();
    while(vars.next())
    {
        boObject val = vars.getObject().getAttribute("value").getObject();
        if(val.getAttribute("type").getValueLong() == 0)
        {
            boObject valO = manag.getBoManager().getValueBoObject(val);
            if(valO != null)
            {
                bridgeHandler bdec = valO.getBridge("decisions");
                if(bdec != null)
                {
                    if(dec == null)
                    {
                        dec = manag.getBoManager().createObject("xwfDecision");
                        dec.getAttribute("program").setValueLong(manag.getBoManager().getProgBoui());
                        dec.getAttribute("performer").setValueLong(manag.getBoManager().getPerformerBoui());
                        dec.getAttribute("justification").setValueString(actv.getAttribute("justification").getValueString());
                        String answer = actv.getAttribute("answer").getValueString();
                        if(answer != null && !"".equals(answer))
                        {
                            String label = null;
                            if(answer.equals(actv.getAttribute("yes").getObject().getAttribute("codeOption").getValueString()))
                            {
                                label = actv.getAttribute("yes").getObject().getAttribute("labelOption").getValueString();
                            }
                            else
                            {
                                label = actv.getAttribute("no").getObject().getAttribute("labelOption").getValueString();
                            }     
                            dec.getAttribute("answer").setValueString(label);
                        }  
                        manag.getBoManager().updateObject(dec);
                    }
                    bdec.add(dec.getBoui());
                    manag.getBoManager().updateObject(valO);
                }
            }
        }
    }  
  
    String partName = actv.getAttribute("partName").getValueString();
    String usid = actv.getAttribute("unique_sid").getValueString();
    if(usid == null || usid.equals("-1") || usid.startsWith("0_"))
        return true;
    ngtXMLHandler ngtn = manag.getControlFlow().getNode(usid);
      {
        String cv = calculateVotation(actv, option, manag); 
        if(cv != null)
        {
            manag.getControlFlow().markPointer(ngtn.getNode(), "done");
            if(xwfHelper.STEP_POLL.equalsIgnoreCase(ngtn.getNodeName()))
            {
                ngtXMLHandler[] conds_child = ngtn.getChildNode("conditionsPoll").getChildNodes();
                for(int i=0; i < conds_child.length; i++)
                {
                    if(cv.equals(conds_child[i].getAttribute("value")))
                    {
                       Node nxt = manag.getControlFlow().findNextStep(conds_child[i].getNode(),  true);
                        if(nxt != null)
                        {
                            manag.getControlFlow().markPointer(nxt, "start");
                            break;
                        }
                    }
                }
            }
            else
                if(xwfHelper.STEP_DECISION.equalsIgnoreCase(ngtn.getNodeName()))
                {
                    ngtXMLHandler[] conds_child = ngtn.getChildNode("answers").getChildNodes();
                    for(int i=0; i < conds_child.length; i++)
                    {
                        if(cv.equals(conds_child[i].getNodeName()))
                        {
                            Node nxt = manag.getControlFlow().findNextStep(conds_child[i].getNode(),  true);
                            if(nxt != null)
                            {
                                manag.getControlFlow().markPointer(nxt, "start");
                                return true;
                            }
                        }
                    }
                }
        }
      }
        
      if(xwfHelper.STEP_DECISION.equalsIgnoreCase(ngtn.getNodeName()))
        return false;
      else
        return true;
  }
  
  /**
     * Função que adiciona um voto e recalcula os valores já apurados conferindo se a votação chegou ao seu fim 
     * @throws netgest.bo.runtime.boRuntimeException Excepção na manipulação dos objectos no tratamento
     * @return <code>null</code> caso a votação ainda n tenha terminado, ou o código da condição vencedora
     * @param manag Manager que está a tratar do pedido de finalização do step
     * @param option Opção escolhida pelo utilizador
     * @param actv  actividade terminada
     */
  private String calculateVotation(boObject actv, String option, xwfManager manag) throws boRuntimeException
  {
    int i_votes = 0;
    int i_votantes = 0;
    double complete = 0;
    boolean ret=false;
    boObject pollr = actv.getAttribute("pollObj").getObject();
    bridgeHandler bopt = pollr.getBridge("options");
    boObject curr_opt = null; 
    bopt.beforeFirst();
    while(bopt.next())
    {
        curr_opt = bopt.getObject();
        if(option.equals(curr_opt.getAttribute("codeOption").getValueString()))
        {
            long nvotes = bopt.getAttribute("votes").getValueLong();
            bopt.getAttribute("votes").setValueLong(nvotes+1);
            break;
        }
    }
    bridgeHandler bvoters = pollr.getBridge("voters");
    bvoters.add(actv.getAttribute("assignedQueue").getValueLong());
    bvoters.getAttribute("option").setObject(curr_opt); 
    bvoters.getAttribute("nameV").setValueString(actv.getAttribute("performer").getObject().getCARDIDwNoIMG().toString());
    recalcPercentage(pollr);
    long cont = pollr.getAttribute("universe").getValueLong();
    boolean completed = cont == bvoters.getRowCount();
    return checkVoteCondition(pollr, completed);
  }
  
  /**
     * Rotina especifica para o recalculo das percentagens 
     * @throws netgest.bo.runtime.boRuntimeException Excepções devido à manipulação de objectos
     * @param obj_poll  objecto que gere a votação
     */
  private static void recalcPercentage(boObject obj_poll) throws boRuntimeException
  {
    long cont = 0;
    cont = obj_poll.getAttribute("universe").getValueLong();
    bridgeHandler bopt = obj_poll.getBridge("options");
    bopt.beforeFirst();
    while(bopt.next())
    {
        double vot = bopt.getAttribute("votes").getValueDouble();
        double perv = vot/cont * 100;
        bopt.getAttribute("percent").setValueDouble( perv );
    }
  }
  
  /**
     * Verifica se alguma condição venceu a votação. No caso afirmativo devolve o código dessa mesma condição 
     * @throws netgest.bo.runtime.boRuntimeException Excepções devido à manipulação de objectos
     * @return código da condição vencedora, <code>null</code> caso nenhuma seja apurada
     * @param completed <code>true</code> representa que todos os participantes já votaram e que é necessário apurar
     * uma condição vencedora, <code>false</code> no caso contrário
     * @param obj_poll  objecto que gere a votação
     */
  private static String checkVoteCondition(boObject obj_poll, boolean completed) throws boRuntimeException
  {
    boObject pdef = obj_poll.getAttribute("definition").getObject();
    bridgeHandler bcond = pdef.getBridge("conditions");
    bridgeHandler bopts = obj_poll.getBridge("options");
    bcond.beforeFirst();
    while(bcond.next())
    {
        boObject cond = bcond.getObject();
        if(completed || "1".equals(cond.getAttribute("instantEval").getValueString()))
        {
            String expre = cond.getAttribute("expression").getValueString();
            String field = cond.getAttribute("field").getValueString();
            String criteria = cond.getAttribute("criteria").getValueString();
            boObject opt_vot = null;
            Xep xepC = new Xep();
            if(field.equalsIgnoreCase("voters"))
            {
                criteria = "option";
                opt_vot = cond.getAttribute("voter").getObject();
                xepC.addBoObjectVariable("vote", cond.getAttribute("option").getObject());
                expre = " == vote";
            }
            else
                opt_vot = cond.getAttribute("option").getObject();
            
            xepC.addBoObjectVariable("poll", obj_poll);
            xepC.addBoObjectVariable("option", opt_vot);
            xepC.addCode("if(poll."+field+".haveBoui(option)) if(poll."+field+"."+criteria+" "+expre+") return true; else return false; else return false;");
            Object oeval = xepC.eval(obj_poll.getEboContext());
            Boolean bval = (Boolean)oeval;
            if(bval.booleanValue())
                return cond.getAttribute("code").getValueString();
        }
             
    }
    if(completed)
    {
        boObject defcond = pdef.getAttribute("defaultCond").getObject();
        if(defcond != null)
            return defcond.getAttribute("code").getValueString();
        else
            return null;
    }
    else
        return null;
  }
  
  /**
     * Método invocado apenas pela votação em paralelo por forma a proceder ao primeiro voto da pessoa que lançou a votação.
     * @throws netgest.bo.runtime.boRuntimeException Excepções devido à manipulação de objectos
     * @return código da condição vencedora, <code>null</code> caso nenhuma seja apurada
     * @param option option Opção escolhida pelo utilizador
     * @param pollr     objecto que gere a votação
     * @param starter   participante que lançou a votação
     */
  public static String approvalStarterVotation(boObject starter, boObject pollr, String option) throws boRuntimeException
  {
    int i_votes = 0;
    int i_votantes = 0;
    double complete = 0;
    boolean ret=false;
//    boObject pollr = actv.getAttribute("pollObj").getObject();
    bridgeHandler bopt = pollr.getBridge("options");
    boObject curr_opt = null; 
    bopt.beforeFirst();
    while(bopt.next())
    {
        curr_opt = bopt.getObject();
        if(option.equals(curr_opt.getAttribute("codeOption").getValueString()))
        {
            long nvotes = bopt.getAttribute("votes").getValueLong();
            bopt.getAttribute("votes").setValueLong(nvotes+1);
            break;
        }
    }
    bridgeHandler bvoters = pollr.getBridge("voters");
    bvoters.add(starter.getBoui());
    bvoters.getAttribute("option").setObject(curr_opt);    
    recalcPercentage(pollr);
    long cont = pollr.getAttribute("universe").getValueLong();
    boolean completed = cont == bvoters.getRowCount();
    return checkVoteCondition(pollr, completed);
  }
  
}