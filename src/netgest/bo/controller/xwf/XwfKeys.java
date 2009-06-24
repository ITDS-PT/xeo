/*Enconding=UTF-8*/
package netgest.bo.controller.xwf;

/**
 * <p>Title: XwfKeys </p>
 * <p>Description: Contêm as chaves dos parâmetros relacionados com o control XWF.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public final class XwfKeys 
{   
    // Chave nome do controlador
    public final static String CONTROLLER_NAME_KEY = "XwfController";
    
    public final static String MAIN_CONTROLLER_KEY = "inMainController";

    // Chaves do programa
    public final static String PROGRAM_DEFINITION_BOUI_KEY = "defProgramBoui";
    public final static String PROGRAM_RUNTIME_BOUI_KEY = "runtimeProgramBoui";
    public final static String PROGRAM_INPUT_OBJECT_BOUI_KEY = "inputObjectBoui";
    public final static String PROGRAM_EMBEDDED_KEY = "embeddedProgram";
    public final static String PROGRAM_MODE = "programMode";
    
    // Chaves da actividade
    public final static String ACTIVITY_RUNTIME_BOUI_KEY = "runtimeActivityBoui";    
    public final static String ACTIVITY_VALUE_KEY = "xwfActivityValue";                
    public final static String ACTIVITY_STATE_BOUI_KEY = "stateActivityBoui";    
    
    // Chave para identificar o tipo de viewer 
    public final static String VIEWER_TYPE_KEY = "xwfViewerType";

    // Chave da Acção dentro do xwf
    public final static String ACTION_CODE_KEY = "actionCode";    
    // Chaves para Acções que podem ser executadas no XWF
    public final static String ACTION_CLEAR_CACHE_KEY = "clearCache";
    public final static String ACTION_SHOW_KEY = "show";
    public final static String ACTION_LAUNCH_KEY = "launch";
    public final static String ACTION_PROCEDURE = "procedure";
    public final static String ACTION_SAVE_KEY = "save";
    public final static String ACTION_OPEN_KEY = "open";
    public final static String ACTION_REOPEN_KEY = "reopen";
    public final static String ACTION_CLOSE_KEY = "close";
    public final static String ACTION_CANCEL_KEY = "cancel";
    public final static String ACTION_SEND_KEY = "send";    
    public final static String ACTION_REPLY_KEY = "reply";
    public final static String ACTION_REPLY_ALL_KEY = "replyAll";
    public final static String ACTION_FORWARD_KEY = "forward";
    public final static String ACTION_ASSOCIATE_KEY = "associate";
    public final static String ACTION_REASSIGN_KEY = "reassign";
    public final static String ACTION_PROCESS_TEMPLATE_KEY = "processTemplate";
    public final static String ACTION_RE_PROCESSAR_KEY = "reProcessar";
    public final static String ACTION_SEND_READ_RECEIPT_KEY = "sendReadReceipt";
    public final static String ACTION_RELEASE_MSG_KEY = "releaseMsg";
    
    // Chaves para Acções sobre o fluxo de trabalho
    public final static String ACTION_CREATE_PROGRAM_KEY = "createProgram";
    public final static String ACTION_REMOVE_PROGRAM_KEY = "removeProgram";
    public final static String ACTION_CANCEL_PROGRAM_KEY = "cancelProgram";
    public final static String ACTION_MOVE_PROGRAM_KEY = "moveProgram";
            // Transfer Action Keys
    public final static String ACTION_TRANSFER_PROGRAM_KEY = "transfer";
    
    
    // Chaves para acções sobre tarefas
    public final static String ACTION_MOVE_ACTIVITY_KEY = "moveActivity";
    public final static String ACTION_MOVE_ACTIVITIES_KEY = "moveActivities";
    public final static String ACTION_REMOVE_ACTIVITY_KEY = "removeActivity";
    
    
    public final static String ACTION_OBJECT_METHOD_KEY = "objectMethod";    
    // Information Keys
    public final static String IN_FINDING_TEMPLATE_KEY = "inFindingTemplate";


}