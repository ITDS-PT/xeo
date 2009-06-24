/*Enconding=UTF-8*/
package netgest.bo.controller.xwf;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.servlet.http.HttpServletRequest;
import netgest.bo.controller.basic.BasicController;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.xwf.core.xwfDefActivity;

import netgest.utils.ClassUtils;

import netgest.xwf.EngineGate;
import netgest.xwf.common.xwfActionHelper;
import netgest.xwf.common.xwfFunctions;
import netgest.xwf.common.xwfHelper;
import netgest.xwf.xwfEngineGate;

import org.apache.log4j.Logger;

/**
 * <p>Title: XwfController </p>
 * <p>Description: Classe controladora das acções do WorkFlow (XWF)</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @Company: Enlace3 </p>
 * @author Pedro Castro Campos ( pedro.campos@itds.pt )
 * @version 2.5
 */
public class XwfController extends BasicController
{       
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.controller.xwf.XwfController");

    /**
     * Ligação ao engine do core do XWF 
     */
    private EngineGate engine = null;
    /**
     * Pilha de dependencias entre as actividades 
     */    
    private Stack branch = null;
    /**
     * Actividade activa. 
     */
    private long runtimeActivityBoui = -1;
        
    private List optionalQueueObjects = new ArrayList();
    /**
     * Informação mantida durante o request para análise e construção do xwf.     
     */
    private Map requestInformation = new HashMap();
    
    /**
     * Construtor do controlador XWF, sem definir actividade
     * @param dochtml, docHTML
     */
    public XwfController(docHTML dochtml) throws boRuntimeException
    {
        super(dochtml);        
        setPresentation(new XwfPresentation(this));
        setNavigator(new XwfNavigator(this));        
        loadEngine(-1);
        getBranchInSession();               
    }
    /**
     * Construtor do controlador XWF , defenindo a actividade em questão
     * @param dochtml, docHTML
     * @param runtimeProgramBoui, Actividade a iniciar o XWF 
     */    
    public XwfController( docHTML dochtml, String runtimeProgramBoui) throws boRuntimeException
    {
        super(dochtml);
        setPresentation(new XwfPresentation(this));
        setNavigator(new XwfNavigator(this));        
        loadEngine(Long.parseLong(runtimeProgramBoui));
        getBranchInSession();               
    }    
    /**
     * Construtor do controlador XWF , defenindo a actividade em questão
     * @param dochtml, docHTML
     * @param runtimeProgramBoui, Actividade a iniciar o XWF 
     */    
    public XwfController( docHTML dochtml, long runtimeProgramBoui) throws boRuntimeException
    {
        super(dochtml);
        setPresentation(new XwfPresentation(this));
        setNavigator(new XwfNavigator(this));        
        loadEngine(runtimeProgramBoui);
        getBranchInSession();    
    }     
    public void buildRequestBoList(docHTML_controler DOCLIST, HttpServletRequest request) throws boRuntimeException
    {       

        loadPresentationType();             
        super.buildRequestBoList(DOCLIST,request);
        try
        {            
            workFlowBusiness();
        }
        catch(Exception exception)
        {
            handleRuntimeException(exception);
        }
    }
    private void handleRuntimeException(Exception exception) throws boRuntimeException
    {
        boolean handleDone = false;
        if(exception instanceof boRuntimeException)
        {            
            if ( ((boRuntimeException)exception).getErrorCode().equals("BO-3021") )
            {
                boObject srcObject = ((boRuntimeException)exception).getSrcObject();
                if(srcObject != null)
                {
                    if( (srcObject.getAttributeErrors() != null && srcObject.getAttributeErrors().size() > 0 ) ||
                        (srcObject.getObjectErrors() != null && srcObject.getObjectErrors().size() > 0 ))                
                    {
                        getEngine().getBoManager().getDocHTML().renderErrors( srcObject );
                    }
                    else 
                    {
                            getEngine().getBoManager().getDocHTML().addErrorMessage
                            (
                                "Existe um objecto inválido. ["+srcObject.getCARDIDwLink(false,"docid="+getEngine().getBoManager().getDocHTML().getDocIdx()+"&ctxParentIdx="+getEngine().getBoManager().getDocHTML().getDocIdx()+"&noUpdate=y&")+"]"
                            );
                    }
                    handleDone = true;
                }
            }                
            else if ( ((boRuntimeException)exception).getErrorCode().equals("BO-3022") )
            {
                getEngine().getBoManager().getDocHTML().addErrorMessage("Não é possível gravar porque o objecto já foi alterado por outro utilizador.");
                handleDone = true;
            }  
            else if(exception.getMessage() != null && ((boRuntimeException)exception).getErrorCode().equals("BO-3055") && exception.getMessage().indexOf("ORA-00001") != -1)
            {
                getEngine().getBoManager().getDocHTML().addErrorMessage("Não é possível gravar porque, já existe um objecto com a mesma chave única.");
                handleDone = true;
            }
            else if(getEngine().getBoManager().getDocHTML().haveErrors())
            {
                handleDone = true;
            }
        }
        if(!handleDone)
        {
            CharArrayWriter cr = new CharArrayWriter();
            PrintWriter pw = new PrintWriter( cr );
            exception.printStackTrace( pw );
            getEngine().getBoManager().getDocHTML().addErrorMessage("<span style='background-color:#FFFFFF;border:2px solid red' onclick=\"displayXeoError(  errorpre.innerHTML )\" >Erro interno no Sistema ,clique para reportar o erro</span><pre class='error' id='errorpre'> \n" + cr.toString() +"</pre>" );
            logger.error(cr.toString());            
        }
        try
        {
            if("close".equals(this.getRuntimeActivity().getStateAttribute("runningState").getValueString()))
                this.getRuntimeActivity().getStateAttribute("runningState").setValue("create");
        }catch(Exception e){}
    } 
    /**
     *  Executa trabalho especifico do workflow.      
     */
    private void workFlowBusiness() throws boRuntimeException
    {
        boObject object = null;
        String action = request.getParameter( XwfKeys.ACTION_CODE_KEY );
        if(getDocHTML().getMasterBoList() != null)
        {
            object = getDocHTML().getMasterBoList().getObject();
            if(object != null && action == null)
            {
                if("xwfDefActivity".equals(object.getName()))
                {
                    String type = object.getAttribute("type").getValueString();
                    if(type == null || "".equals(type))
                    {
                        type = request.getParameter( "deftype" );
                        object.getAttribute("type").setValueString(type);
                        String v = request.getParameter( XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY );
                        if(v != null && v.trim().length() > 0 && !"-1".equals(v))
                        {
                            object.getAttribute("fromActivity").setValueString(v);
                        }
                        object.getAttribute("program").setValueString(request.getParameter( XwfKeys.PROGRAM_RUNTIME_BOUI_KEY ));                    
                    }
                }
            }
        }
        
        if(isInMainController())
        {
            if(isEmbeddedProgram())
            {
                if("new".equals(engine.getBoManager().getDocHTML().getMethod()))
                {            
                    if(isToSend())
                    {                
                        setRuntimeActivityBoui(xwfActionHelper.createEmbeddedProgram(getEngine(),"xwfActivitySend").getBoui());
                    }
                    else
                    {
                        setRuntimeActivityBoui(xwfActionHelper.createEmbeddedProgram(getEngine(),"xwfCreateReceivedMessage").getBoui());                    
                    }
                }
            }
            else if(loadRuntimeProgram())
            {
                afterLoadProgram();
            }      
            else 
            {                                
                startRuntimeProgram();
            }  
            
            if(getRuntimeActivity() != null)
            {
                getDocHTML().setMasterBoList( getEngine().getBoManager().listObject("SELECT "+getRuntimeActivity().getName()+" WHERE 0=1"));
                getRuntimeActivity().poolSetStateFull(); 
                getDocHTML().getMasterBoList().inserRow( getRuntimeActivityBoui() );
                // Faz sentido ??
                setBranchInSession();                
            }    
            else
            {
                boObject program = getRuntimeProgram();
                if(program != null)
                {
                    getDocHTML().setMasterBoList( getEngine().getBoManager().listObject("SELECT "+program.getName()+" WHERE 0=1"));
                    program.poolSetStateFull(); 
                    getDocHTML().getMasterBoList().inserRow( program.getBoui() );                                                        
                }
            }
        }
    }    
    /**
     * Constroi a ligação com o core do XWF.
     * @param runtimeProgramBoui , programa em curso.
     */  
    private void loadEngine(long runtimeProgramBoui) throws boRuntimeException
    {
        if(getEngine() == null)
        {
            engine = new xwfEngineGate(getDocHTML(),runtimeProgramBoui);    
        }        
    }
    /**
     * Constroi uma nova ligação com o core do XWF internamente para mudar o xwf.
     * @param runtimeProgramBoui , programa em curso.
     */      
    private void loadNewEngineByForce(long runtimeProgramBoui) throws boRuntimeException
    {
        engine = new xwfEngineGate(getDocHTML(),runtimeProgramBoui);            
    }
    
    /**
     * Limpa a cache do controlador.
     */     
    public void cleanCache()
    {
//        setRuntimeActivityBoui(-1);
        this.requestInformation.clear();
        getEngine().clearErrorMessages();
    }
    /**
     * Inicia o programa com base em parametros do request.
     */
    private boolean startRuntimeProgram() throws boRuntimeException
    {
        boolean start = false;
        String defProgramBoui = request.getParameter(XwfKeys.PROGRAM_DEFINITION_BOUI_KEY );
        String inputObjectBoui = request.getParameter(XwfKeys.PROGRAM_INPUT_OBJECT_BOUI_KEY);
        String programMode = request.getParameter(XwfKeys.PROGRAM_MODE);
        byte mode = 0;
        if(programMode != null && !"".equals(programMode))
        {
            mode = Byte.parseByte(programMode);   
        }        
        if(defProgramBoui != null && !"".equals(defProgramBoui))
        {
            if(inputObjectBoui != null && !"".equals(inputObjectBoui))
            {                                
                getEngine().startProgramRuntime(Long.parseLong(defProgramBoui),Long.parseLong(inputObjectBoui),mode);   
                start = true;
            }
            else
            {
                getEngine().startProgramRuntime(Long.parseLong(defProgramBoui),null,mode);   
                start = true;                
            }
        }
        return start;
    }    
    private void loadPresentationType()
    {
        getPresentation().setType(request.getParameter( XwfKeys.VIEWER_TYPE_KEY ));
    }
    private boolean loadRuntimeProgram() throws boRuntimeException
    {        
        boolean load = false;
        String runtimeProgramBoui = request.getParameter( XwfKeys.PROGRAM_RUNTIME_BOUI_KEY );
        if(runtimeProgramBoui != null && !"".equals(runtimeProgramBoui))
        {    
            getEngine().getBoManager().getProgram();
            load = true;
        }                  
        return load;
    }    
    
    public boObject getRuntimeActivity() throws boRuntimeException
    {
        boObject runtimeActivity = null;
        if(getRuntimeActivityBoui() == -1)
        {    
            runtimeActivity = getEngine().getRuntimeActivity(this);
            if(runtimeActivity != null)
            {
                setRuntimeActivityBoui(runtimeActivity.getBoui());                
            }             
        }
        else
        {
            runtimeActivity = getObject(getRuntimeActivityBoui());
        }
        return runtimeActivity;
    }
    public long getRuntimeProgramBoui() throws boRuntimeException
    {
        return  getEngine().getBoManager().getProgBoui();
    }
    public boObject getRuntimeProgram() throws boRuntimeException
    {
        return getEngine().getBoManager().getProgram();
    }    
    public long getRuntimeActivityBoui()
    {    
        return runtimeActivityBoui;
    }
    private void setRuntimeActivityBoui(long boui)
    {    
        runtimeActivityBoui = boui;
    }
    public void setOverrideRuntimeActivity(long boui)
    {    
        runtimeActivityBoui = boui;
    }        
    public String getName()
    {
       return XwfKeys.CONTROLLER_NAME_KEY;
    }   
    public List getOptionalQueue()
    {
       return this.optionalQueueObjects;
    } 
    public Stack getBranch()
    {
       return this.branch;
    }     
    public EngineGate getEngine()
    {
        return this.engine;
    }
    public boObject getObject(long boui) throws boRuntimeException
    {        
        return getEngine().getBoManager().getObject(boui);
    }      
    public String getProgramState() throws boRuntimeException
    {
        return getRuntimeProgram().getStateAttribute("runningState").getValueString();
    }    
    private boolean loadRuntimeActivity() throws boRuntimeException
    {
        boolean load = false;
        String runtimeActivityBoui = request.getParameter( XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY );        
        if(runtimeActivityBoui != null && !"".equals(runtimeActivityBoui))
        {          
            this.setRuntimeActivityBoui(Long.parseLong(runtimeActivityBoui));
            load = true;
        }  
        return load;    
    }      
    private boolean loadStateActivity() throws boRuntimeException
    {
        boolean load = false;
        String runtimeActivityBoui = request.getParameter( XwfKeys.ACTIVITY_STATE_BOUI_KEY );        
        if(runtimeActivityBoui != null && !"".equals(runtimeActivityBoui))
        {          
            this.setRuntimeActivityBoui(Long.parseLong(runtimeActivityBoui));
            load = true;
        }  
        return load;
    }
    public String getActionCode() throws boRuntimeException
    {
        String actionCode = null;
        if(request != null)
        {
            actionCode = request.getParameter( XwfKeys.ACTION_CODE_KEY ); 
        }
        return actionCode;
    }     
    private boolean doWorkflowAction(String actionCode) throws boRuntimeException
    {
        boolean result = false;  
        boObject activity = null;
//        String actionCode = getActionCode();
        if(actionCode != null && !"".equals(actionCode))
        {    
            String[] actions = actionCode.split(";");
            for (int i = 0; i < actions.length; i++) 
            {
                actionCode = actions[i];
            
                if(isSendReadReceipt(actionCode))
                {
                    result = sendReadReceipt(actionCode);
                }
                else if(isReleaseMsg(actionCode))
                {
                    result = releaseMsg(actionCode);
                }            
                else if(!isMessageViewer(actionCode))
                {
                    String runtimeActivityBoui = request.getParameter( XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY );
                    if(runtimeActivityBoui != null && !"".equals(runtimeActivityBoui))
                    {   
                        setRuntimeActivityBoui(Long.parseLong(runtimeActivityBoui));
                        activity = getRuntimeActivity();
    
                        if(XwfValidator.validate(getDocHTML(),activity,actionCode))
                        {            
                            if(XwfKeys.ACTION_SAVE_KEY.equals(actionCode))
                            {
                                result = getEngine().doAction(XwfKeys.ACTION_SAVE_KEY,activity);
                            }
                            else if(XwfKeys.ACTION_REOPEN_KEY.equals(actionCode))
                            {
                                result = getEngine().doAction(XwfKeys.ACTION_REOPEN_KEY,activity);
                            }
                            else if(XwfKeys.ACTION_CLEAR_CACHE_KEY.equals(actionCode))
                            {
                                setRuntimeActivityBoui(-1);
                                this.cleanCache();
                                result = true;
                            }
                            else if(XwfKeys.ACTION_SEND_KEY.equals(actionCode) && "xwfActivitySend".equals(activity.getName()))
                            {
                                result = xwfActionHelper.sendActivity(this,activity);
                            }
                            else if(actionCode.startsWith(XwfKeys.ACTION_SHOW_KEY))
                            {
                                String showActivity = actionCode.substring(actionCode.indexOf("[") + 1, actionCode.indexOf("]"));
                                if(showActivity != null && !"".equals(showActivity))
                                {
                                    long boui = ClassUtils.convertToLong(showActivity,-1);
                                    if(boui != -1)
                                    {
                                        this.setRuntimeActivityBoui(boui);
                                        setBranchId(this.getRuntimeActivity());
                                    }
                                }
                            }  
                            else if(XwfKeys.ACTION_REMOVE_PROGRAM_KEY.equals(actionCode))
                            {
                                if(getEngine().doAction(XwfKeys.ACTION_REMOVE_PROGRAM_KEY,this.getRuntimeProgram()))
                                {
                                    setRuntimeActivityBoui(-1);
                                    result = true;                                
                                }
                                /*
                                if(xwfActionHelper.removeProgram(getEngine().getBoManager(),getRuntimeProgramBoui()))
                                {
                                    setRuntimeActivityBoui(-1);
                                    result = true;
                                }
                                else
                                {
                                    getDocHTML().addErrorMessage("Não foi possível remover este fluxo de trabalho");
                                } 
                                */
                            }  
                            else if(XwfKeys.ACTION_CANCEL_PROGRAM_KEY.equals(actionCode))
                            {                            
                                if(getEngine().doAction(XwfKeys.ACTION_CANCEL_PROGRAM_KEY,this.getRuntimeProgram()))
                                {
                                    setRuntimeActivityBoui(-1);
                                    result = true;
                                }
    /*                        
                                if(getEngine().getManager().cancelProgram())
                                {
                                    setRuntimeActivityBoui(-1);
                                    result = true;
                                }
                                else
                                {
                                    getDocHTML().addErrorMessage("Não foi possível cancelar este fluxo de trabalho");
                                }               
    */                            
                            }
                            else if(actionCode.startsWith(XwfKeys.ACTION_PROCEDURE))
                            {
                                String procedureName = actionCode.substring(actionCode.indexOf("[") + 1, actionCode.indexOf("]"));
                                if(procedureName != null && !"".equals(procedureName))
                                {
//                                    getEngine().getManager().runProcedure(activity,procedureName);
                                    getEngine().doAction(actionCode,activity);
                                    if(activity != null)
                                    {
                                        String branch = activity.getAttribute("unique_sid").getValueString();
                                        activity =  getEngine().getNextActivity(branch); 
                                        setBranchId(activity);
                                        if(activity != null)
                                        {
                                            setRuntimeActivityBoui(activity.getBoui());                
                                        }                                                 
                                    }
                                    result = true;                                                                
                                }                            
                            }
                            else if(actionCode.startsWith(XwfKeys.ACTION_LAUNCH_KEY))
                            {
                                boObject defActivity = null; 
                                String defActivityBoui = actionCode.substring(actionCode.indexOf("[") + 1, actionCode.indexOf("]"));
                                if(defActivityBoui != null && !"".equals(defActivityBoui))
                                {
                                    /* antigo defActivity
                                    defActivity = getObject(Long.parseLong(defActivityBoui));
                                    if("xwfDefActivity".equals(defActivity.getName()) && XwfValidator.validate(getDocHTML(),defActivity,XwfKeys.ACTION_LAUNCH_KEY))
                                    {
                                        ((xwfEngineGate)getEngine()).getManager().runSubProgram(defActivity.getBoui());
                                        setRuntimeActivityBoui(-1);
                                        result = true;
                                    }
                                    else
                                    {
                                        optionalQueueObjects.add(defActivity);
                                    }
                                    */
                                    defActivity = getObject(Long.parseLong(defActivityBoui));
                                    if("xwfDefActivity".equals(defActivity.getName()) && XwfValidator.validate(getDocHTML(),defActivity,XwfKeys.ACTION_LAUNCH_KEY))
                                    {
                                        return xwfActionHelper.defActivity(defActivity);
                                    }
                                    else
                                    {
                                        optionalQueueObjects.add(defActivity);
                                    }
                                    
                                }
                                else
                                {
                                    String type = actionCode.substring(actionCode.indexOf("][",0) + 2, actionCode.lastIndexOf("]"));
                                    if(type != null && !"".equals(type))
                                    {
                                        defActivity = getEngine().getBoManager().createObject("xwfDefActivity");
                                        defActivity.getAttribute("type").setValueString(type);
                                        defActivity.getAttribute("fromActivity").setValueLong(getRuntimeActivityBoui());
                                        defActivity.getAttribute("program").setValueLong(getRuntimeProgramBoui());
                                        defActivity.getAttribute("assignedQueue").setValueLong(getDocHTML().getEboContext().getBoSession().getPerformerBoui());
                                        if(xwfHelper.STEP_ACTIVITY.equals(type))
                                        {
                                            defActivity.getAttribute("label").setValueString("Nova Tarefa");
                                        }
                                        else if(xwfHelper.STEP_USER_CALL_PROG.equals(type))
                                        {
                                            defActivity.getAttribute("label").setValueString("Novo Fluxo de Trabalho");
                                        }         
                                        
                                        ((xwfEngineGate)getEngine()).getManager().runSubProgram(defActivity.getBoui());
                                        setRuntimeActivityBoui(-1);
                                        result = true;                                    
                                    }                                
                                }
                            }  
                            else if(actionCode.startsWith(XwfKeys.ACTION_CREATE_PROGRAM_KEY))
                            {
                                String parameter = actionCode.substring(actionCode.indexOf("[") + 1, actionCode.indexOf("]"));
                                if(parameter != null && !"".equals(parameter))
                                {                                               
                                    long programBoui = ClassUtils.convertToLong(parameter,-1);
                                    if(programBoui != -1)
                                    {
                                        boObject object = xwfHelper.getMasterObject(getEngine(),activity);                                    
                                        if(object != null && XwfValidator.valid(getDocHTML(),object))
                                        {
                                            getEngine().getBoManager().updateObject(activity);
                                            boObject p = getEngine().getBoManager().getProgram();
                                            if(p != null && p.getAttribute("flow").getValueString().equals(""))
                                                ((xwfEngineGate)getEngine()).getManager().createUsingProgram(programBoui,object.getBoui(),xwfHelper.PROGRAM_EXEC_DEFAULT_MODE);
                                            else
                                            {
                                                long prt_boui = ((xwfEngineGate)getEngine()).getManager().createProgram(programBoui,object.getBoui(),xwfHelper.PROGRAM_EXEC_DEFAULT_MODE);
                                                if(object.getName().startsWith("message"))
                                                {
                                                    boObject act_rec = ((xwfEngineGate)getEngine()).getManager().createMessageActivity("xwfActivityReceive", 
                                                    object.getAttribute("name").getValueString(), object, 
                                                    object.getBoDefinition().getName(), prt_boui, getEngine().getBoManager().getPerformerBoui());
                                                    getEngine().getBoManager().updateObject(act_rec);
                                                    bridgeHandler bmsg = getEngine().getBoManager().getProgram().getBridge("message");
                                                    if(!bmsg.haveBoui(object.getBoui()))
                                                        bmsg.add(object.getBoui());
                                                }
                                            }
                                            setRuntimeActivityBoui(-1);
                                            result = true;                                                                            
                                        }                                    
                                    }
                                }
                            }
                            else if(actionCode.startsWith("move"))
                            {
                                // Validar actividade e ver se from e to não são iguais
                                String programRuntime = actionCode.substring(actionCode.indexOf("[") + 1, actionCode.indexOf("]"));
                                if(programRuntime != null && !"".equals(programRuntime))
                                {                                               
                                    long programBoui = ClassUtils.convertToLong(programRuntime,-1);
                                    if(programBoui != -1)
                                    {
                                        boObject toProgram = getObject(programBoui);
                                        if(getRuntimeProgramBoui() != toProgram.getBoui())
                                        {
                                            boolean move = false;
                                            if(actionCode.startsWith(XwfKeys.ACTION_MOVE_ACTIVITY_KEY))
                                            {
                                                getEngine().getBoManager().updateObject(activity);
                                                move = xwfActionHelper.moveActivity(getEngine(),getRuntimeProgram(),getObject(programBoui),activity);
                                            }
                                            else
                                            if(actionCode.startsWith(XwfKeys.ACTION_MOVE_ACTIVITIES_KEY))
                                            {
                                                int indSec = actionCode.indexOf("[", XwfKeys.ACTION_MOVE_ACTIVITIES_KEY.length()+programRuntime.length());
                                                int indSecEnd = actionCode.indexOf("]", indSec);
                                                String moveActBouis = actionCode.substring(indSec + 1, indSecEnd);
                                                getEngine().getBoManager().updateObject(activity);
                                                move = xwfActionHelper.moveActivitiesToProgram(getEngine(),getRuntimeProgram(),getObject(programBoui),moveActBouis,null);
                                            }
                                            else
                                            {
                                                engine.getBoManager().updateObject(activity);
                                                move = xwfActionHelper.moveProgram(getEngine(),getRuntimeProgram(),getObject(programBoui));
                                            }
                                            
                                            if(move)
                                            {
                                                setRuntimeActivityBoui(-1);
                                                if(!"close".equals(activity.getStateAttribute("runningState").getValueString()))
                                                {
                                                    if("close".equals(toProgram.getStateAttribute("runningState").getValueString()))
                                                    {
                                                        toProgram.getStateAttribute("runningState").setValueString("open");   
                                                    }                                          
                                                }
                                                loadNewEngineByForce(toProgram.getBoui());
                                                result = true;
                                            }
                                            else
                                            {
                                                StringBuffer message = new StringBuffer("Não foi possível associar esta tarefa ");
                                                message.append(activity.getCARDID());
                                                message.append(" ou fluxo de trabalho ");
                                                message.append(toProgram.getCARDID().toString());
                                                getDocHTML().addErrorMessage(message.toString());
                                            }
                                        }
                                        else
                                        {
                                            getDocHTML().addErrorMessage("Não é possível associar ao mesmo fluxo de trabalho");                                        
                                        }
                                    }
                                }
                            }                        
                            else if(actionCode.startsWith(XwfKeys.ACTION_PROCESS_TEMPLATE_KEY))
                            {
                                String[] actionDef = actionCode.substring(actionCode.indexOf("[") + 1, actionCode.indexOf("]")).split(",");
                                String templ = actionDef[0];
                                String var = actionDef[1];
                                boObject variable = getObject(Long.parseLong(var));
                                if(templ != null && !"".equals(templ))
                                {                                
                                    if(var != null && !"".equals(var))
                                    {                                                                    
                                        boObject object = variable.getAttribute("value").getObject().getAttribute(xwfHelper.VALUE_OBJECT).getObject();
                                        getEngine().getBoManager().applyTemplate(object,Long.parseLong(templ),activity);
                                        variable.getAttribute("templateMode").setValueLong(1);
                                        getEngine().getBoManager().updateObject(variable);
                                        result = true;                                                                    
                                    }
                                }
                                else
                                {
                                    variable.getAttribute("templateMode").setValueLong(1);
                                    getEngine().getBoManager().updateObject(variable);
                                    result = true;                                
                                }                            
                            }                          
                            else if(actionCode.startsWith(XwfKeys.ACTION_REASSIGN_KEY))
                            {
                                String reassignBoui = actionCode.substring(actionCode.indexOf("[") + 1, actionCode.indexOf("]"));
                                if(reassignBoui != null && !"".equals(reassignBoui))
                                {
                                    boObject reassign = getObject(Long.parseLong(reassignBoui));
                                    if("xwfReassign".equals(reassign.getName()) && XwfValidator.validate(getDocHTML(),reassign,XwfKeys.ACTION_REASSIGN_KEY))
                                    {                                    
                                        if(xwfActionHelper.reassignActivity(activity,reassign))
                                        {
                                            getEngine().getBoManager().updateObject(activity);
                                            setRuntimeActivityBoui(-1);
                                            result = true;
                                        }
                                    }
                                    if(!result)
                                    {
                                        optionalQueueObjects.add(reassign);
                                    }
                                }
                            }
                            else if(actionCode.startsWith(XwfKeys.ACTION_TRANSFER_PROGRAM_KEY))
                            {
                                String transferBoui = actionCode.substring(actionCode.indexOf("[") + 1, actionCode.indexOf("]"));
                                if(transferBoui != null && !"".equals(transferBoui))
                                {
                                    boObject transfer = getObject(Long.parseLong(transferBoui));
                                    if("xwfActivityTransfer".equals(transfer.getName()) && XwfValidator.validate(getDocHTML(),transfer,XwfKeys.ACTION_TRANSFER_PROGRAM_KEY))
                                    {                                    
                                        if(xwfActionHelper.transferProcess(transfer))
                                        {
                                            getEngine().getBoManager().updateObject(activity);
                                            setRuntimeActivityBoui(-1);
                                            result = true;
                                        }
                                    }
                                    if(!result)
                                    {
                                        optionalQueueObjects.add(transfer);
                                    }
                                }
                            }
                            else if("xwfActivityReceive".equals(activity.getName()))// tem de ser por reply etc
                            {
                                result = treatMessage(activity,actionCode);
                                if(XwfKeys.ACTION_CLOSE_KEY.equals(actionCode))//é para sair
                                {
    //                                setAdhocLabels();                                                    
                                    xwfActionHelper.setRead(activity, getDocHTML().getEboContext().getBoSession().getPerformerBoui());
                                    getEngine().setActivityState(Long.parseLong(runtimeActivityBoui),actionCode);
                                    setRuntimeActivityBoui(-1);
                                    result = true;                            
                                }      
                                else if(XwfKeys.ACTION_OPEN_KEY.equals(actionCode)) //é para sair
                                {
    //                                setAdhocLabels();
                                    getEngine().setActivityState(Long.parseLong(runtimeActivityBoui),actionCode);
                                    setRuntimeActivityBoui(-1);
                                    result = true;                                   
                                }
                                else if(actionCode.startsWith(XwfKeys.ACTION_OBJECT_METHOD_KEY))
                                {   
                                    try
                                    {
                                        String method = actionCode.substring(actionCode.indexOf("[") + 1, actionCode.indexOf("]"));
                                        String boui = actionCode.substring(actionCode.indexOf("][",0) + 2, actionCode.lastIndexOf("]"));                                    
                                        boObject object = engine.getObject(Long.parseLong(boui));                                        
//                                        boObject object = xwfHelper.getMasterObject(engine,activity);
                                        getDocHTML().executeMethod(object,method);
                                        result = true;    
                                    }
                                    catch ( Throwable e )
                                    {
                                        CharArrayWriter cr = new CharArrayWriter();
                                        PrintWriter pw = new PrintWriter( cr );
                                        e.printStackTrace( pw );
                                        getDocHTML().addErrorMessage( "<span style='background-color:#FFFFFF;border:2px solid red' onclick=\"displayXeoError(  errorpre.innerHTML )\" >Erro interno no Sistema ,clique para reportar o erro</span><pre class='error' id='errorpre'> \n" + cr.toString() +"</pre>" );
                                    }                              
                                }                                   
                                xwfFunctions.setIntelligentLabel(engine,activity);
                            }
                            else if(actionCode.startsWith(XwfKeys.ACTION_OBJECT_METHOD_KEY))
                            {
                                try
                                {
                                    String method = actionCode.substring(actionCode.indexOf("[") + 1, actionCode.indexOf("]"));
                                    String boui = actionCode.substring(actionCode.indexOf("][",0) + 2, actionCode.lastIndexOf("]"));
                                    boObject object = engine.getObject(Long.parseLong(boui));
//                                    boObject object = xwfHelper.getMasterObject(engine,activity);
                                    getDocHTML().executeMethod(object,method);
                                }
                                catch ( Throwable e )
                                {
                                    CharArrayWriter cr = new CharArrayWriter();
                                    PrintWriter pw = new PrintWriter( cr );
                                    e.printStackTrace( pw );
                                    getDocHTML().addErrorMessage( "<span style='background-color:#FFFFFF;border:2px solid red' onclick=\"displayXeoError(  errorpre.innerHTML )\" >Erro interno no Sistema ,clique para reportar o erro</span><pre class='error' id='errorpre'> \n" + cr.toString() +"</pre>" );
                                }                              
                            }
                            else if(XwfKeys.ACTION_RE_PROCESSAR_KEY.equals(actionCode))
                            {
                                boObject object = xwfHelper.getMasterObject(engine,activity);                                    
                                if(object != null)
                                {
                                    if(xwfActionHelper.mergeMessage(this.getEngine(),activity))
                                    {
                                        getEngine().getBoManager().updateObject(activity);
                                        result = true;
                                    }
                                }
                            }
                            else if(actionCode.equals(XwfKeys.ACTION_CLOSE_KEY))
                            {
                                if(activity.getAttribute("defActivity").getObject() != null &&
                                    !"reopen".equals(activity.getStateAttribute("runningState").getValueString())
                                )
                                {
                                    boObject defActivity = activity.getAttribute("defActivity").getObject(); 
                                    if("1".equals(activity.getAttribute("justificationRequired").getValueString())
                                        && (activity.getAttribute("justification").getValueString() == null
                                        || activity.getAttribute("justification").getValueString().trim().length()==0)
                                    )
                                    {
                                        getDocHTML().addErrorMessage("Foi requisitado o preenchimento do campo Resumo.");
                                        return false;
                                    }
                                    long rtActvBoui = -1;
                                    if("xwfActivityFill".equalsIgnoreCase(activity.getName()))
                                    {
                                        xwfDefActivity.notify((xwfEngineGate)getEngine(), activity);
                                        if(xwfDefActivity.isInChain(defActivity))
                                        {
                                            xwfDefActivity.inChain(defActivity);
                                        }
                                    }
                                    else if("xwfActivity".equalsIgnoreCase(activity.getName()))
                                    {
                                        rtActvBoui = xwfDefActivity.endingActivity((xwfEngineGate)getEngine(), activity);
                                    }
                                    getEngine().doAction(actionCode,activity);
                                    setRuntimeActivityBoui(rtActvBoui);
                                    result = true;
                                }
                                else
                                {
                                    getEngine().doAction(actionCode,activity);
                                    setRuntimeActivityBoui(-1);
                                    result = true;
                                }
                            }
                            else
                            {                         
    //                            getEngine().setActivityState(Long.parseLong(runtimeActivityBoui),actionCode);
    //                            xwfFunctions.setIntelligentLabel(engine,activity);
                                getEngine().doAction(actionCode,activity);
                                setRuntimeActivityBoui(-1);
                                result = true;
                            }                            
                        }                
                    }
                }
            }
        }        
        return result;
    }           
    private boolean setActivityValue() throws boRuntimeException
    {
        boolean set = false;
        String runtimeActivityBoui = request.getParameter( XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY );        
        if(runtimeActivityBoui != null && !"".equals(runtimeActivityBoui))
        {                                
            String xwfActivityValue = request.getParameter( XwfKeys.ACTIVITY_VALUE_KEY );
            if(xwfActivityValue != null && !"".equals(xwfActivityValue))
            {   
                setRuntimeActivityBoui(Long.parseLong(runtimeActivityBoui));
                Date date = new Date();
                if("create".equals(getRuntimeActivity().getStateAttribute("runningState").getValueString()))
                {                    
                    getRuntimeActivity().getAttribute("beginDate").setValueDate(date);
                    getRuntimeActivity().getAttribute("performer").setValueLong(getDocHTML().getEboContext().getBoSession().getPerformerBoui());                
                }
                
                getRuntimeActivity().getAttribute("percentComplete").setValueString("100");
                getRuntimeActivity().getAttribute("endDate").setValueDate(date);        
                
                getEngine().setActivityValue(Long.parseLong(runtimeActivityBoui),xwfActivityValue);  
                setRuntimeActivityBoui(-1);
                set = true;
            }
        }
        return set; 
    }          
    private void afterLoadProgram() throws boRuntimeException
    {    
        if(!getDocHTML().haveErrors())
        {               
            doBusinessAction();
        }
        else
        {
            loadRuntimeActivity();
        }        
    }

    private void doBusinessAction() throws boRuntimeException
    {
        if(setActivityValue())
        {
            getRuntimeActivity();    
        }
        else if(doWorkflowAction(getActionCode()))
        {
            loadStateActivity();
        }
        else if(getRuntimeActivityBoui() == -1 && !loadRuntimeActivity())
        {
            String boui = request.getParameter("boui");  
            if(boui != null && !"".equals(boui))
            {
                boObject aux = getEngine().getBoManager().getObject(Long.parseLong(boui));
                if("xwfProgramRuntime".equals(aux.getName()))
                {
                    getRuntimeActivity();
                }                
                else
                {
                    setRuntimeActivityBoui(Long.parseLong(boui));
                }                
            }                                                
        } 
    }
   
    private void setBranchInSession()
    {
        getDocHTML().getEboContext().getBoSession().setProperty("XWF_BRANCH[" + getDocHTML().getDocIdx() + "]",getBranch()) ;
    }
    private void getBranchInSession()
    {
        setBranch((Stack)getDocHTML().getEboContext().getBoSession().getProperty("XWF_BRANCH[" + getDocHTML().getDocIdx() + "]"));        
    }  
    private void setBranch(Stack branch)
    {
        if(branch != null)
        {
            this.branch = branch;   
        }
        else
        {
            this.branch = new Stack();
        }
    }
    public void setBranchId(boObject runtimeActivity) throws boRuntimeException
    {
        if(runtimeActivity != null)
        {
            String branchAux = runtimeActivity.getAttribute("unique_sid").getValueString();
            if(branchAux != null && !"".equals(branchAux) && !"0".equals(branchAux))
            {
                if(branch != null && !branch.isEmpty())
                {
                    String lastSid = (String)branch.lastElement();
                    if(!branchAux.equals(lastSid))
                    {
                        branch.add(branchAux);
                    }
                }
                else
                {
                    branch.add(branchAux);
                }
            }
        }
    }
    private boolean isInMainController()
    {
        return "true".equals((String)request.getAttribute(XwfKeys.MAIN_CONTROLLER_KEY));        
    }    
    private boolean isEmbeddedProgram()
    {
        return "true".equals(request.getParameter(XwfKeys.PROGRAM_EMBEDDED_KEY));        
    }
    private boolean isToSend()
    {
        return "true".equals(request.getParameter("toSend"));
    }
    private boolean treatMessage(boObject activity,String actionCode) throws boRuntimeException
    {
        boolean result = true;
        boObject activityCreated = null;
        boolean replyAll = actionCode.startsWith(XwfKeys.ACTION_REPLY_ALL_KEY);
        boolean fwd = actionCode.startsWith(XwfKeys.ACTION_FORWARD_KEY);
        if(actionCode.endsWith("Conversation"))
        {
            if(fwd)
            {
                activityCreated = xwfActionHelper.forwardMessage(this.getEngine(),activity, "messageConversation");
            }
            else
            {
                activityCreated = xwfActionHelper.replyMessage(this.getEngine(), activity, replyAll, "messageConversation");
            }
            if(activityCreated == null)
            {
                boObject variable = activity.getAttribute("message").getObject();
                boObject value = variable.getAttribute("value").getObject();
                boObject message = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();
                getDocHTML().renderErrors(message);
                result = false;
            }
        }
        else if(actionCode.endsWith("Fax"))
        {
            if(fwd)
            {
                activityCreated = xwfActionHelper.forwardMessage(this.getEngine(),activity, "messageFax");
            }
            else
            {
                activityCreated = xwfActionHelper.replyMessage(this.getEngine(), activity, replyAll, "messageFax");
            }
            if(activityCreated == null)
            {
                boObject variable = activity.getAttribute("message").getObject();
                boObject value = variable.getAttribute("value").getObject();
                boObject message = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();
                getDocHTML().renderErrors(message);
                result = false;
            }
        }
        else if(actionCode.endsWith("Letter"))
        {
            if(fwd)
            {
                activityCreated = xwfActionHelper.forwardMessage(this.getEngine(),activity, "messageLetter");
            }
            else
            {
                activityCreated = xwfActionHelper.replyMessage(this.getEngine(), activity, replyAll, "messageLetter");
            }      
            if(activityCreated == null)
            {
                boObject variable = activity.getAttribute("message").getObject();
                boObject value = variable.getAttribute("value").getObject();
                boObject message = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();
                getDocHTML().renderErrors(message);
                result = false;
            }
        }
        else if(actionCode.endsWith("Mail"))
        {
            if(fwd)
            {
                activityCreated = xwfActionHelper.forwardMessage(this.getEngine(),activity, "messageMail");
            }
            else
            {
                activityCreated = xwfActionHelper.replyMessage(this.getEngine(), activity, replyAll, "messageMail");
            }      
            if(activityCreated == null)
            {
                boObject variable = activity.getAttribute("message").getObject();
                boObject value = variable.getAttribute("value").getObject();
                boObject message = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();
                getDocHTML().renderErrors(message);
                result = false;
            }
        }
        else if(actionCode.endsWith("Phone"))
        {
            if(fwd)
            {
                activityCreated = xwfActionHelper.forwardMessage(this.getEngine(),activity, "messagePhone");
            }
            else
            {
                activityCreated = xwfActionHelper.replyMessage(this.getEngine(), activity, replyAll, "messagePhone");
            }      
            if(activityCreated == null)
            {
                boObject variable = activity.getAttribute("message").getObject();
                boObject value = variable.getAttribute("value").getObject();
                boObject message = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();
                getDocHTML().renderErrors(message);
                result = false;
            }
        }
        else if(actionCode.endsWith("Sgis"))
        {
            if(fwd)
            {
                activityCreated = xwfActionHelper.forwardMessage(this.getEngine(),activity, "messageSgis");
            }
            else
            {
                activityCreated = xwfActionHelper.replyMessage(this.getEngine(), activity, replyAll, "messageSgis");
            }
            if(activityCreated == null)
            {
                boObject variable = activity.getAttribute("message").getObject();
                boObject value = variable.getAttribute("value").getObject();
                boObject message = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();
                getDocHTML().renderErrors(message);
                result = false;
            }
        }
        else if("reply".equals(actionCode))
        {
            activityCreated = xwfActionHelper.replyMessage(this.getEngine(), activity, false, null);      
            if(activityCreated == null)
            {
                boObject variable = activity.getAttribute("message").getObject();
                boObject value = variable.getAttribute("value").getObject();
                boObject message = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();
                getDocHTML().renderErrors(message);
                result = false;
            }
        }
        else if(XwfKeys.ACTION_REPLY_ALL_KEY.equals(actionCode))
        {
            activityCreated = xwfActionHelper.replyMessage(this.getEngine(), activity, true, null);
            if(activityCreated == null)
            {
                boObject variable = activity.getAttribute("message").getObject();
                boObject value = variable.getAttribute("value").getObject();
                boObject message = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();
                getDocHTML().renderErrors(message);
                result = false;
            }
        }
        else if(XwfKeys.ACTION_FORWARD_KEY.equals(actionCode))
        {
            activityCreated = xwfActionHelper.forwardMessage(this.getEngine(),activity, null);
            if(activityCreated == null)
            {
                boObject variable = activity.getAttribute("message").getObject();
                boObject value = variable.getAttribute("value").getObject();
                boObject message = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();
                getDocHTML().renderErrors(message);
                result = false;
            }
        }
        if(activityCreated != null)
        {
            setRuntimeActivityBoui(activityCreated.getBoui());            
        }
        return result;
    }
    
    private boolean isSendReadReceipt(String actionCode) throws boRuntimeException
    {
        if(actionCode.indexOf(XwfKeys.ACTION_SEND_READ_RECEIPT_KEY) != -1)
        {
            return true;
        }
        return false;
    }
    
    private boolean sendReadReceipt(String actionCode) throws boRuntimeException
    {
        boolean result = false;
        if(actionCode.indexOf("[") != -1)
        {        
            if(actionCode.indexOf(XwfKeys.ACTION_SEND_READ_RECEIPT_KEY) != -1)
            {
                String aux = actionCode;
                aux = aux.substring(aux.indexOf("[") + 1, aux.indexOf("]"));
                long performerBoui = Long.parseLong(aux);
                boObject performer = boObject.getBoManager().loadObject(getDocHTML().getEboContext(), performerBoui);
                long runtimeActivityBoui = Long.parseLong(request.getParameter( XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY ));
                result = xwfActionHelper.sendReadReceipt(this.getEngine(), this.getObject(runtimeActivityBoui), performer);                
            }
        }
        return result;
    }
    
    private boolean isMessageViewer(String actionCode) throws boRuntimeException
    {
        boolean result = false;
        boObject activityCreated = null;
        if("sendMessage".equals(actionCode))
        {
            activityCreated = xwfActionHelper.sendMessage(this.getEngine());            
            result = true;
        }
        else if(actionCode.indexOf("[") != -1)
        {        
            if(actionCode.indexOf(XwfKeys.ACTION_REPLY_ALL_KEY) != -1)
            {
                String aux = actionCode;
                aux = aux.substring(aux.indexOf("[") + 1, aux.indexOf("]"));
                long actvBoui = Long.parseLong(aux);
                activityCreated = xwfActionHelper.replyMessage(this.getEngine(), this.getObject(actvBoui), true, null);                
                result = true;
            }
            else if(actionCode.indexOf(XwfKeys.ACTION_REPLY_KEY) != -1)
            {
                String aux = actionCode;
                aux = aux.substring(aux.indexOf("[") + 1, aux.indexOf("]"));
                long actvBoui = Long.parseLong(aux);
                activityCreated = xwfActionHelper.replyMessage(this.getEngine(), this.getObject(actvBoui), false, null);
                result = true;
            }            
            else if(actionCode.indexOf(XwfKeys.ACTION_FORWARD_KEY) != -1 )
            {
                String aux = actionCode;
                aux = aux.substring(aux.indexOf("[") + 1, aux.indexOf("]"));
                long actvBoui = Long.parseLong(aux);
                activityCreated = xwfActionHelper.forwardMessage(this.getEngine(), this.getObject(actvBoui), null);
                result = true;
            }
        }
        if(result == true && activityCreated != null)
        {
            setRuntimeActivityBoui(activityCreated.getBoui());
        }
        return result;
    }
    public void addInformation(Object key,Object value)
    {
        requestInformation.put(key,value);
    }
    public Object getInformation(Object key)
    {
        return requestInformation.get(key);
    }
    private boolean isReleaseMsg(String actionCode) throws boRuntimeException
    {
        if(actionCode.indexOf(XwfKeys.ACTION_RELEASE_MSG_KEY) != -1)
        {
            return true;
        }
        return false;
    }
    
    private boolean releaseMsg(String actionCode) throws boRuntimeException
    {
        boolean result = false;
        if(actionCode.indexOf(XwfKeys.ACTION_RELEASE_MSG_KEY) != -1)
        {
            String aux = actionCode;
            long runtimeActivityBoui = Long.parseLong(request.getParameter( XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY ));
            
            //TODO:Implement Interface LUSITANIA
            //result = xwfActionHelper.releaseMsg(this.getEngine(), this.getObject(runtimeActivityBoui));                
        }
        return result;
    }
}