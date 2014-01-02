/*Enconding=UTF-8*/
package netgest.xwf;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import java.util.List;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.dochtml.docHTML;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.utils.DestroyBusinessObject;
import netgest.utils.ClassUtils;
import netgest.xwf.EngineGate;
import netgest.xwf.core.*;
import netgest.xwf.common.*;

/**
 * Ligação ao engine do WorkFlow do XWF
 * @author  Pedro Castro Campos ( pedro.campos@itds.pt )
 * @version 1.0
 */
public class xwfEngineGate implements EngineGate
{
    /**
     * Ligação ao engine do Workflow XWF 
     */
    private xwfManager manager = null;   
    private List errorMessages = new ArrayList();    
  /**
   * Classe construtora que inicia a ligação ao motor do XWF. 
   * @param context Contexto.   
   */       
    public xwfEngineGate(EboContext context) throws boRuntimeException
    {        
        manager = new xwfManager(context,null);
    }
  /**
   * Classe construtora que inicia a ligação ao motor do XWF. 
   * @param context Contexto.
   * @param program <code>xwfProgramRuntime</code> do Contexto.
   */       
    public xwfEngineGate(EboContext context,boObject program) throws boRuntimeException
    {        
        manager = new xwfManager(context,program);
    }
  /**
   * Classe construtora que inicia a ligação ao motor do XWF. 
   * @param boManager <code>xwfBoManager</code>.
   */       
    public xwfEngineGate(xwfBoManager boManager) throws boRuntimeException
    {        
        manager = new xwfManager(boManager);
    }          
  /**
   * Classe construtora que inicia a ligação ao motor do XWF. 
   * @param doc docHTML do contexto.
   * @param runtimeProgramBoui o fluxo de trabalho para o motor iniciar ou < 0 caso ainda não exista.
   */       
    public xwfEngineGate(docHTML doc, long runtimeProgramBoui) throws boRuntimeException
    {        
        if(runtimeProgramBoui > 0)
        {
            manager = new xwfManager(doc,runtimeProgramBoui);
        }
        else
        {
            manager = new xwfManager(doc,null);   
        }        
    }
  /**
   * Inicia um processo do workflow com base na definição de um programa e de uma variável. 
   * @param defProgramBoui boui de definição de um programa.
   * @param variable boui de uma variavel para iniciar o processo, < 0 caso contrário.   
   * @param mode forma de execução do programa.
   * @throws netgest.bo.runtime.boRuntimeException
   */       
    public void startProgramRuntime(long defProgramBoui,long variable, byte mode) throws boRuntimeException
    {
        if(defProgramBoui != 0)
        {  
            if(variable > 0)
            {
                manager.createProgram(defProgramBoui,variable,mode);    
            }
            else
            {
                manager.createProgram(defProgramBoui,mode);
            }
        }        
    }    
  /**
   * Inicia um processo do workflow com base na definição de um programa e de uma lista de variáveis. 
   * @param defProgramBoui boui de definição de um programa.
   * @param variables lista de  variaveis para iniciar o processo, null caso contrário.   
   * @param mode forma de execução do programa.
   * @throws netgest.bo.runtime.boRuntimeException
   */
    public void startProgramRuntime(long defProgramBoui,Hashtable variables, byte mode) throws boRuntimeException
    {                        
        if(defProgramBoui != 0)
        {              
            if(variables == null)
            {
                manager.createProgram(defProgramBoui,mode);    
            }
            else
            {
                manager.createProgram(defProgramBoui,variables,mode);
            }
        }
    }
    /**
     * Devolve o programa a decorrer neste contexto.
     * @return <code>xwfProgramRuntime</code> no engine do xwf. 
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public boObject getProgramRuntime() throws boRuntimeException
    {
        return this.getBoManager().getProgram();
    }       
    /**
     * Devolve o estado em que o workflow se encontra.
     * @return estado em execução.
     */    
    public byte getExecutionMode()
    {
        return getBoManager().getMode();
    }
  /**
   * Devolve um <code>boObject</code> no contexto do workflow. 
   * @param boui identificador do objecto a devolver.
   * @return <code>boObject</code> no contexto do engine.   
   * @throws netgest.bo.runtime.boRuntimeException
   */     
    public boObject getObject(long boui) throws boRuntimeException
    {
        return manager.getBoManager().getObject(boui);
    }
  /**
   * Devolve a actividade corrente, caso não exista tenta encontrar a próxima. 
   * @param controller XwfController do contexto a decorrer.
   * @return runtimeActivity actividade a decorrer, null caso não encontre nenhuma.    
   */       
    public boObject getRuntimeActivity(XwfController controller) throws boRuntimeException
    {       
        boObject runtimeActivity = null;
        if(controller.getBranch().isEmpty())
        {
            runtimeActivity = getNextActivity();                
        }
        else
        {
            while(runtimeActivity == null && !controller.getBranch().isEmpty())
            {
                Object sid = controller.getBranch().lastElement();  
                if(sid == null)
                {
                    runtimeActivity = getNextActivity();   
                }
                else
                {
                    runtimeActivity = getNextActivity((String)sid);
                }
                if(runtimeActivity == null && !controller.getBranch().isEmpty())
                {
                    controller.getBranch().pop();
                }
            }                
        }        
        controller.setBranchId(runtimeActivity);           
        return runtimeActivity;
    }    
  /**
   * Altera o estado e os dados de uma actividade, com base no seu estado anterior. 
   * @param activityBoui boui da actividade a alterar.
   * @param state novo estado da actividade.    
   * @throws netgest.bo.runtime.boRuntimeException
   */      
    public void setActivityState(long activityBoui, String state) throws boRuntimeException
    {
        boObject activity = manager.getBoManager().getObject(activityBoui);
        xwfFunctions.setActivityData(activity,state);
        if(XwfKeys.ACTION_OPEN_KEY.equals(state))
        {
            getManager().recalcFormulas();
            getBoManager().updateObject(activity);                        
        }
        else
        {
            manager.finishedStep(activityBoui);
        }
        
    }
  /**
   * Termina uma actividade com um valor. 
   * @param activityBoui boui da actividade a alterar.
   * @param value valor da actividade.    
   */      
    public void setActivityValue(long activityBoui, String value) throws boRuntimeException
    {        
        manager.finishedStep(activityBoui,value);   
    }
  /**
   * Devolve a próxima actividade do fluxo de trabalho. 
   * @return boObject próxima actividade do fluxo de trabalho.    
   * @throws netgest.bo.runtime.boRuntimeException
   */          
    public boObject getNextActivity() throws boRuntimeException
    {
        return getNextActivity(null);
    }
  /**
   * Devolve a próxima actividade com base num ramo do fluxo de trabalho. 
   * @param branch ramo do fluxo de trabalho, null caso contrário.
   * @return currentActivity próxima actividade do fluxo de trabalho.    
   */      
    public boObject getNextActivity(String branch) throws boRuntimeException
    {        
        boObject currentActivity = null;
        boolean required = false;
        String aux = null;
        String branchAux = null;
        boObjectList availableActivityList = getAvailableActivityList();
        if(availableActivityList != null)
        {
            availableActivityList.beforeFirst();
            while(availableActivityList.next() && !required) 
            {
                currentActivity = availableActivityList.getObject();
                branchAux = currentActivity.getAttribute("branch").getValueString();
                aux = currentActivity.getAttribute("optional").getValueString();
                if("0".equals(aux))
                {
                    if(branch != null)
                    {
                        if(branch.equals(branchAux))
                        {
                            required = true;    
                        }
                    }
                    else
                    {
                        required = true;   
                    }
                }
            }                      
            if(currentActivity == null && !required)
            {      
                availableActivityList.beforeFirst();
                if(availableActivityList.next())
                {
                    currentActivity = availableActivityList.getObject();  
                }
            }
        }
        return currentActivity;
    }
  /**
   * Devolve a lista de actividades disponíveis para serem executadas. 
   * @return <code>boObjectList</code> lista de actividades do fluxo de trabalho disponíveis.  
   * @throws netgest.bo.runtime.boRuntimeException
   */ 
    private boObjectList getAvailableActivityList() throws boRuntimeException
    {
        StringBuffer boql = new StringBuffer("SELECT xwfActivity WHERE program = " );
        boql.append(manager.getBoManager().getProgBoui());
        boql.append(" AND runningState <= 1 AND ");
        boql.append(xwfHelper.PERFORMER_CLAUSE);
        boql.append(" ORDER BY SYS_DTCREATE,BOUI");
        boObjectList list = manager.getBoManager().listObject(boql.toString(),true,false);
        if(getExecutionMode() == xwfHelper.PROGRAM_EXEC_TEST_MODE)
        {
            list.beforeFirst();
            while(list.next())
            {
                boObject objl = list.getObject();
               if(objl.getAttribute("program").getValueLong() != manager.getBoManager().getProgBoui()
                || (!objl.getStateAttribute("runningState").getValueString().equalsIgnoreCase("open")
                && !objl.getStateAttribute("runningState").getValueString().equalsIgnoreCase("create"))
                )
                   list.removeCurrent(); 
            }
        }
        return list;
//        return manager.getBoManager().listObject(boql.toString(),true,false);        
    } 
  /**
   * Devolve a lista de actividades pendentes para serem executadas, e visiveis para utilizador.
   * @return <code>boObjectList</code> lista de actividades pendentes do workflow.   
   * @throws netgest.bo.runtime.boRuntimeException
   */     
    public boObjectList getPendingActivityList() throws boRuntimeException
    {
        StringBuffer boql = new StringBuffer("SELECT /*NO_SECURITY*/ xwfActivity STATE open OR wait OR reopen WHERE program = ");    
        boql.append(manager.getBoManager().getProgBoui());
        if(!xwfHelper.isWorkFlowAdministrator(this))
        {
            boql.append(" AND ");
            boql.append(xwfHelper.PERFORMER_CLAUSE_ALL);
        }        
        boql.append(" ORDER BY SYS_DTCREATE,BOUI");                
        boObjectList list = manager.getBoManager().listObject(boql.toString(),true,false);
        if(getExecutionMode() == xwfHelper.PROGRAM_EXEC_TEST_MODE)
        {
            list.beforeFirst();
            while(list.next())
            {
                boObject objl = list.getObject();
                if(objl.getAttribute("program").getValueLong() != manager.getBoManager().getProgBoui()
                || (!objl.getStateAttribute("runningState").getValueString().equalsIgnoreCase("open")
                && !objl.getStateAttribute("runningState").getValueString().equalsIgnoreCase("wait"))
                )
                   list.removeCurrent(); 
            }
        }
        return list;
//        return manager.getBoManager().listObject("SELECT xwfActivity STATE open OR wait WHERE program = " + manager.getBoManager().getProgBoui() + " AND "+ xwfHelper.PERFORMER_CLAUSE +" ORDER BY SYS_DTCREATE,BOUI");        
    }
    
  /**
   * Devolve a lista de actividades criadas, e visiveis para utilizador.
   * @return <code>boObjectList</code> lista de actividades criadas do workflow.
   * @throws netgest.bo.runtime.boRuntimeException
   */     
    public boObjectList getCreateActivityList() throws boRuntimeException
    {
        StringBuffer boql = new StringBuffer("SELECT /*NO_SECURITY*/ xwfActivity STATE create WHERE program = ");
        boql.append(manager.getBoManager().getProgBoui());
        if(!xwfHelper.isWorkFlowAdministrator(this))
        {
            boql.append(" AND ");
            boql.append(xwfHelper.PERFORMER_CLAUSE_ALL);
        }        
        boql.append(" ORDER BY SYS_DTCREATE,BOUI");                
        boObjectList list = manager.getBoManager().listObject(boql.toString(),true,false);
        if(getExecutionMode() == xwfHelper.PROGRAM_EXEC_TEST_MODE)
        {
            list.beforeFirst();
            while(list.next())
            {
                boObject objl = list.getObject();
                if(objl.getAttribute("program").getValueLong() != manager.getBoManager().getProgBoui()
                || !objl.getStateAttribute("runningState").getValueString().equalsIgnoreCase("create")
                )
                   list.removeCurrent(); 
            }
        }
        return list;  
//        return manager.getBoManager().listObject("SELECT xwfActivity STATE create WHERE program = " + manager.getBoManager().getProgBoui() + " AND "+ xwfHelper.PERFORMER_CLAUSE +" ORDER BY SYS_DTCREATE,BOUI");        
    }      
  /**
   * Devolve a lista de actividades fechadas, e visiveis para utilizador. 
   * @return <code>boObjectList</code> lista de actividades fechadas do workflow. 
   * @throws netgest.bo.runtime.boRuntimeException
   */      
    public boObjectList getCloseActivityList() throws boRuntimeException
    {        
        StringBuffer boql = new StringBuffer("SELECT /*NO_SECURITY*/ xwfActivity STATE close WHERE program = ");
        boql.append(manager.getBoManager().getProgBoui());
        if(!xwfHelper.isWorkFlowAdministrator(this))
        {
            boql.append(" AND ");
            boql.append(xwfHelper.PERFORMER_CLAUSE_ALL);
        }        
        boql.append(" ORDER BY SYS_DTCREATE,BOUI");                
        boObjectList list = manager.getBoManager().listObject(boql.toString(),true,false);
        if(getExecutionMode() == xwfHelper.PROGRAM_EXEC_TEST_MODE)
        {
            list.beforeFirst();
            while(list.next())
            {
                boObject objl = list.getObject();
                if(objl.getAttribute("program").getValueLong() != manager.getBoManager().getProgBoui()
                || !objl.getStateAttribute("runningState").getValueString().equalsIgnoreCase("close")
                )
                   list.removeCurrent(); 
            }
        }
//        try{
//            Class[] param = {Class.forName("netgest.bo.runtime.boObject"), Class.forName("netgest.bo.runtime.boObject")};  
//            list.orderList(Class.forName("netgest.xwf.common.xwfFunctions").getMethod("conpareActivtiyDates", param));
//        }catch(Exception e){}
        return list;        
//        return manager.getBoManager().listObject("SELECT xwfActivity STATE close WHERE program = " + manager.getBoManager().getProgBoui() + " AND "+ xwfHelper.PERFORMER_CLAUSE +" ORDER BY SYS_DTCREATE,BOUI");
    }     
  /**
   * Devolve o gestor de objectos do workflow XWF. 
   * @return <code>xwfBoManager</code> gestor de objectos.    
   */      
    public xwfBoManager getBoManager()
    {
        return manager.getBoManager();
    }
  /**
   * Devolve o gestor de negócio do workflow XWF. 
   * @return xwfBoManager gestor de negócio.    
   */         
    public xwfManager getManager()
    {
        return manager;
    } 
    /**
     * Adiciona mensagem de erro ao docHtml caso exista, cc guarda internamente.
     * @param message mensagem de erro a adicionar.
     */
    public void addErrorMessage(String message)
    {
        if(getBoManager() != null && getBoManager().getDocHTML() != null)
        {
           getBoManager().getDocHTML().addErrorMessage(message);
        }
        else
        {
            this.errorMessages.add(message);   
        }               
    }
    public List getErrorMessages()
    {                      
        return this.errorMessages;
    }
    /**
     * Remove as mensagem de erro internas     
     */
    public void clearErrorMessages()
    {
        this.errorMessages.clear();
    }
    /**
     * <p>Chama o core do workflow para executar as acções pretendidas.</p>
     * <p>Acções(<code>XwfKeys</code>):</p>
     * <ul>
     *     <li> ACTION_OPEN_KEY
     *     <li> ACTION_CLOSE_KEY
     *     <li> ACTION_CANCEL_KEY
     *     <li> ACTION_SAVE_KEY
     *     <li> ACTION_REOPEN_KEY
     *     <li> ACTION_REMOVE_PROGRAM_KEY
     *     <li> ACTION_CANCEL_PROGRAM_KEY
     *     <li> ACTION_PROCEDURE
     *     <li> ACTION_MOVE_ACTIVITY_KEY
     *     <li> ACTION_REASSIGN_KEY
     *     <li> ACTION_REMOVE_PROGRAM_KEY
     * </ul>     
     * @param action Acção a executar pelo workflow.
     * @param object Objecto sobre o qual recai a acção.
     * @return True se tiver sucesso, False caso contrário.
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public boolean doAction(String action, boObject object) throws boRuntimeException
    {
        boolean result = false;  
        if(action != null && !"".equals(action))
        {                    
            boObject activity = null; 
            if(object != null && !"xwfProgramRuntime".equals(object.getName()))
            {
                activity = object;
            }
            
            if(action.equals(XwfKeys.ACTION_SAVE_KEY))
            {
                xwfFunctions.setIntelligentLabel(this,object);
                this.getBoManager().updateObject(object);
                this.getManager().recalcFormulas();  
                result = true;                  
            }
            else if(action.equals(XwfKeys.ACTION_REOPEN_KEY))
            {
                if(object != null && !"xwfProgramRuntime".equals(object.getName()))
                {
                    xwfActionHelper.reopenActivity(this.getBoManager(),this.getProgramRuntime(),object);
                    this.getBoManager().updateObject(activity);
                    this.getBoManager().updateObject(this.getProgramRuntime());
                    result = true;
                }
                else
                {
                    xwfActionHelper.reopenProgram(this.getBoManager(),this.getProgramRuntime());
                    this.getBoManager().updateObject(this.getProgramRuntime());
                    result = true;                    
                }
            }
            else if(action.startsWith(XwfKeys.ACTION_PROCEDURE))
            {
                String procedureName = action.substring(action.indexOf("[") + 1, action.indexOf("]"));                
                if(procedureName != null && !"".equals(procedureName))
                {
                    this.getManager().runProcedure(activity,procedureName);
                    result = true;                                                                
                }                            
            }          
            else if(action.startsWith("move"))
            {
                String programRuntime = action.substring(action.indexOf("[") + 1, action.indexOf("]"));
                if(programRuntime != null && !"".equals(programRuntime))
                {                                               
                    long programBoui = ClassUtils.convertToLong(programRuntime,-1);
                    if(programBoui != -1)
                    {
                        boObject toProgram = getObject(programBoui);
                        boolean move = false;
                        if(action.startsWith(XwfKeys.ACTION_MOVE_ACTIVITY_KEY))
                        {
                            this.getBoManager().updateObject(activity);
                            move = xwfActionHelper.moveActivity(this,getProgramRuntime(),getObject(programBoui),activity);
                        }
                        else
                        {
                            this.getBoManager().updateObject(activity);
                            move = xwfActionHelper.moveProgram(this,getProgramRuntime(),getObject(programBoui));
                        }
                        
                        if(move)
                        {
                            if(!"close".equals(activity.getStateAttribute("runningState").getValueString()))
                            {
                                if("close".equals(toProgram.getStateAttribute("runningState").getValueString()))
                                {
                                    toProgram.getStateAttribute("runningState").setValueString("open");   
                                }                                          
                            }
                            result = true;
                        }
                    }
                }
            }
            else if(action.startsWith(XwfKeys.ACTION_REASSIGN_KEY))
            {
                if(!"xwfProgramRuntime".equals(object))
                {
                    String reassignBoui = action.substring(action.indexOf("[") + 1, action.indexOf("]"));
                    if(reassignBoui != null && !"".equals(reassignBoui))
                    {
                        boObject reassign = getObject(Long.parseLong(reassignBoui));
                        if("xwfReassign".equals(reassign.getName()))
                        {                                    
                            if(xwfActionHelper.reassignActivity(activity,reassign))
                            {
                                activity.getEboContext().getBoSession().setProperty("settingReassign", Boolean.TRUE);
                                this.getBoManager().updateObject(activity);
                                activity.getEboContext().getBoSession().removeProperty("settingReassign");
                                result = true;
                            }
                        }
                    }
                }
            }    
            else if(action.startsWith(XwfKeys.ACTION_TRANSFER_PROGRAM_KEY))
            {
                if(!"xwfProgramRuntime".equals(object))
                {
                    String transferBoui = action.substring(action.indexOf("[") + 1, action.indexOf("]"));
                    if(transferBoui != null && !"".equals(transferBoui))
                    {
                        boObject transfer = getObject(Long.parseLong(transferBoui));
                        if("xwfReassign".equals(transfer.getName()))
                        {                                    
                            if(xwfActionHelper.reassignActivity(activity,transfer))
                            {
                                activity.getEboContext().getBoSession().setProperty("settingReassign", Boolean.TRUE);
                                this.getBoManager().updateObject(transfer);
                                activity.getEboContext().getBoSession().removeProperty("settingReassign");
                                result = true;
                            }
                        }
                    }
                }
            }
            else if(action.equals(XwfKeys.ACTION_REMOVE_PROGRAM_KEY))
            {
//                if(xwfActionHelper.removeProgram(this.getBoManager(),object.getBoui()))
//                {                    
//                    result = true;
//                }
//                else
//                {
//                    this.addErrorMessage("Não foi possível remover este fluxo de trabalho");
//                }           
                result = DestroyBusinessObject.destroy(object);
                if(!result)
                {
                    this.addErrorMessage(MessageLocalizer.getMessage("COULD_NOT_REMOVE_THIS_WORKFLOW"));
                }
            }   
            else if(action.equals(XwfKeys.ACTION_CANCEL_PROGRAM_KEY))
            {                            
                if(this.getManager().cancelProgram())
                {
                    result = true;
                }
                else
                {
                    this.addErrorMessage(MessageLocalizer.getMessage("COULD_NOT_CANCEL_THIS_WORKFLOW"));
                }                                                                                
            }            
            else
            {
                if(activity != null)
                {
                    this.setActivityState(activity.getBoui(),action);
                    xwfFunctions.setIntelligentLabel(this,activity);
                    result = true;
                }
            }
        }
        return result;
    }
}