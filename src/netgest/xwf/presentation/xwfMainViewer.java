/*Enconding=UTF-8*/
package netgest.xwf.presentation;

import java.util.ArrayList;
import java.util.List;

import netgest.bo.controller.ControllerFactory;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.impl.templates.boTemplateManager;
import netgest.bo.message.utils.MessageUtils;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.xwf.common.xwfHelper;
import org.w3c.dom.NodeList;
/**
 * <p>Title: xwfActivityViewer </p>
 * <p>Description: Classe responsável pelo main viewer</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public final class xwfMainViewer 
{
    public static String getProgramContextCardId(XwfController controller)  throws boRuntimeException
    {        
        String result = "&nbsp;&nbsp;";        
        String type = controller.getPresentation().getType(); 
        if(type != null && "show.attributes".equals(type))
        {
            result += "Informação do Programa";
        }       
        else
        {
            if(controller.getRuntimeActivity() != null)
            {
                result += controller.getRuntimeActivity().getCARDID().toString();   
            }             
            else
            {
                result += controller.getRuntimeProgram().getCARDID().toString();                
            }
        }
        return result;
    }    
    
    public static String buildOpenDocList(XwfController controller) throws boRuntimeException
    {
        String result = null;
        boObject activity = controller.getRuntimeActivity();   
        if(activity != null)
        {
            List openDocList = new ArrayList();
            List readOnlyList = new ArrayList();
            // Activity
            String showTask = activity.getAttribute("showTask").getValueString();
            if("1".equals(showTask))
            {
                buildActivityOpenDoc(controller,openDocList);   
            }
            if("xwfActivitySend".equals(activity.getName()) ||
               "xwfCreateReceivedMessage".equals(activity.getName()) ||
               "xwfActivityCreateMessage".equals(activity.getName()) )
            {            

                buildMessagesOpenDoc(controller,openDocList, "xwfActivitySend".equals(activity.getName()));
            }
            // Edit boObjects        
            boolean anyAttr = buildEditObjectsFromVariablesOpenDoc(controller,openDocList,readOnlyList);
            if(anyAttr || 
               "xwfActivityChoice".equals(activity.getName()) || 
               "xwfActivityPoll".equals(activity.getName()) ||
               "xwfActivityDecision".equals(activity.getName()) ||
               "xwfWaitResponse".equals(activity.getName()) ||
               "xwfUserCallProgram".equals(activity.getName()) ||
               "xwfActivityReceive".equals(activity.getName()))
            {
                // Edit Attributes
                buildEditAtributesOpenDoc(controller,openDocList);
            }            
                       
            buildOptionalQueueOpenDoc(controller,openDocList);
            
            if(readOnlyList.size() > 0)
            {
                openDocList.addAll(readOnlyList);
            }
            findTabToActivate(activity,openDocList);
            result = renderOpenDocList(openDocList);
        }
        else if(activity == null && !xwfHelper.programNotExists(controller.getEngine()) /*controller.getRuntimeProgram().exists()*/ )
        {         
            ArrayList procedures = xwfHelper.getProcedures(controller.getEngine());            
            if(procedures != null && procedures.size() > 0)
            {
                result = buildProgramOpenDoc(controller,null,"__xwfViewerProcedure.jsp","closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=false", null).toString();
                result += buildProgramOpenDoc(controller, null,"__xwfViewerProgramActivitysDone.jsp","closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true", null).toString();
                result += "activeDocByIdx(1);";
            }
            else
            {
                result = buildProgramOpenDoc(controller,null,"__xwfViewerProgramClose.jsp","closeWindowOnCloseDoc=false,showCloseIcon=false,activeOnOpen=false").toString();
                result += "activeDocByIdx(0);";
            }
           /* if(result != null)
            {
                result += "activeDocByIdx(0);";
            }*/
        }                
        return result;
    }        
    private static void findTabToActivate(boObject activity,List openDocList)
    {
        StringBuffer toPrint = new StringBuffer();        
        if("xwfActivity".equals(activity.getName()))
        {
            toPrint.append("activeDocByIdx(").append(0).append(");\n");   
        }   
        else if(openDocList.size() > 1)
        {
            toPrint.append("activeDocByIdx(").append(1).append(");\n");
        } 
        else
        {
            toPrint.append("activeDocByIdx(").append(0).append(");\n");
        }
        openDocList.add(toPrint);
    }
    private static String renderOpenDocList(List openDocList)
    {
        StringBuffer toPrint = new StringBuffer();
        for (int i = 0; i < openDocList.size(); i++) 
        {
            toPrint.append(openDocList.get(i)).append("\n");
        }        
        return toPrint.toString();
    }
    private static void buildActivityOpenDoc(XwfController controller,List openDocList) throws boRuntimeException
    {
        StringBuffer toPrint = new StringBuffer();
        boObject activity = controller.getRuntimeActivity();
        toPrint.append("winmain().openDoc('delay=true,closeWindowOnCloseDoc=false,showCloseIcon=false,activeOnOpen=false','").append(activity.getName().toLowerCase()).append("','edit',");
        toPrint.append("'docid=").append(controller.getDocHTML().getDocIdx());
        toPrint.append("&method=edit");                
        toPrint.append("&object=").append(activity.getName());
        toPrint.append("&menu=no");
        toPrint.append("&doNotRedirect=true");
        toPrint.append("&boui=").append(activity.getBoui());        
        toPrint.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());                
        toPrint.append("');");        
        openDocList.add(toPrint);
    }
    private static void buildMessagesOpenDoc(XwfController controller,List openDocList, boolean send)   throws boRuntimeException
    {
        boObject variable = null;
        boObject activity = controller.getRuntimeActivity();        
        variable = activity.getAttribute("message").getObject();
        if(variable != null)
        {
            long templateMode = variable.getAttribute("templateMode").getValueLong();
            if(templateMode < 10)
            {
                buildObjectOpenDoc(controller,variable,openDocList, send);
            }
            else
            {
                int nrTemplates = findTemplate(controller,variable);
    
                if(nrTemplates == 1)
                {
                    buildObjectOpenDoc(controller,variable,openDocList, send);
                }
                else if(nrTemplates > 1)
                {
                    buildTemplateProcessOpenDoc(controller,openDocList,variable.getBoui(),false);    
                }
                else
                {
                    buildTemplateProcessOpenDoc(controller,openDocList,variable.getBoui(),true);
                }            
            }
        }
    }    
    private static boolean buildEditObjectsFromVariablesOpenDoc(XwfController controller,List openDocList,List readOnlyList) throws boRuntimeException 
    {        
        boolean anyAttr = false;
        long templateMode = -1;
        int nrTemplates = 0;
        boObject activity = controller.getRuntimeActivity();
        boObject variable = null;
        bridgeHandler bridge = activity.getBridge("variables");
        bridge.beforeFirst();
        while(bridge.next())
        {
            variable = bridge.getObject();
            if(isForEdit(variable))
            {
                templateMode = variable.getAttribute("templateMode").getValueLong();
                if(templateMode < 10)
                {
                    long mode =  variable.getAttribute("mode").getValueLong();
                    if(mode == 0)
                    {                    
                        buildObjectPreviewOpenDoc(controller,variable,readOnlyList);
                    }
                    else
                    {
                        buildObjectOpenDoc(controller,variable,openDocList, false);
                    }
                }
                else
                {
                    nrTemplates = findTemplate(controller,variable);

                    if(nrTemplates == 1)
                    {
                        long mode =  variable.getAttribute("mode").getValueLong();
                        if(mode == 0)
                        {                    
                            buildObjectPreviewOpenDoc(controller,variable,readOnlyList);
                        }
                        else
                        {
                            buildObjectOpenDoc(controller,variable,openDocList, false);
                        }
                    }
                    else if(nrTemplates > 1)
                    {
                        buildTemplateProcessOpenDoc(controller,openDocList,variable.getBoui(),false);    
                    }                    
                    else
                    {
                        buildTemplateProcessOpenDoc(controller,openDocList,variable.getBoui(),true);
                    }
                }                
            }
            else
            {
                anyAttr = true;
            }
        }    
        return anyAttr;
    }
    private static void buildObjectOpenDoc(XwfController controller,boObject variable,List openDocList, boolean sendActivity) throws boRuntimeException
    {
        StringBuffer toPrint = new StringBuffer();
        toPrint.append("winmain().openDoc('closeWindowOnCloseDoc=false,showCloseIcon=false,activeOnOpen=false'").append(",");
        StringBuffer sb = new StringBuffer();
        boObject varValue = variable.getAttribute("value").getObject();
        String attrName = xwfHelper.getTypeName(varValue);        
        boObject boDef =  varValue.getAttribute("object").getObject();
        String objName = boDef.getAttribute("name").getValueString(); 
        toPrint.append("'").append(objName.toLowerCase()).append("'").append(",'edit',");
        
//        boObject object = varValue.getAttribute(attrName).getObject();
        boObject object = controller.getEngine().getBoManager().getValueBoObject(varValue);
        
        boolean alreadySend = false;
        if(sendActivity && object != null)
        {
            alreadySend = MessageUtils.alreadySend(object);
        }
        
        if(object == null)
        {               
            object = controller.getEngine().getBoManager().createObject(objName);
            varValue.getAttribute(attrName).setObject(object);
        }
        else if(!object.isEnabled || alreadySend)
        {
            buildObjectPreviewOpenDoc(controller, variable, openDocList);
            return;
        }
        
        
        if("xwfCreateReceivedMessage".equals(controller.getRuntimeActivity().getName()))
        {
            object.getAttribute("createdReceiveMsg").setValueString("1");
        }
                
        sb.append("'docid=").append(controller.getDocHTML().getDocIdx());
        sb.append("&method=edit");        
        sb.append("&menu=yes");        
        sb.append("&boui=").append(object.getBoui());        
        sb.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());
        sb.append("&").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeActivityBoui());                
        if(controller.getActionCode() != null)
        {
            sb.append("&").append("pathItem").append("=").append("1");    
        }
        sb.append("');");    
        toPrint.append(sb);      
        openDocList.add(toPrint);
    }
    private static void buildObjectPreviewOpenDoc(XwfController controller,boObject variable,List openDocList) throws boRuntimeException
    {
        
        StringBuffer toPrint = new StringBuffer();
        
        toPrint.append("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=false,activeOnOpen=false','__buildPreview.jsp',");
                
        
        boObject varValue = variable.getAttribute("value").getObject();
        String attrName = xwfHelper.getTypeName(varValue);        
        boObject boDef =  varValue.getAttribute("object").getObject();
        String objName = boDef.getAttribute("name").getValueString(); 
        
        
        boObject object = varValue.getAttribute(attrName).getObject();    
        
        if(object == null)
        {               
            object = controller.getEngine().getBoManager().createObject(objName);
            varValue.getAttribute(attrName).setObject(object);
        }
        
                
        toPrint.append("'?docid=").append(controller.getDocHTML().getDocIdx());
        toPrint.append("&method=edit");        
        toPrint.append("&menu=yes");        
        toPrint.append("&boui=").append(object.getBoui());
        toPrint.append("&bouiToPreview=").append(object.getBoui());
        toPrint.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());
        toPrint.append("&").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeActivityBoui());
        
        toPrint.append("','std',null,'").append(variable.getAttribute("label").getValueString()).append("');");    
              
        openDocList.add(toPrint);
    }
    
    private static void buildEditAtributesOpenDoc(XwfController controller,List openDocList) throws boRuntimeException 
    {
        StringBuffer toPrint = new StringBuffer();
        toPrint.append("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=false,activeOnOpen=false','__xwfViewerActivityGeneral.jsp',");
        toPrint.append("'?docid=").append(controller.getDocHTML().getDocIdx());
        toPrint.append("&method=edit");        
        toPrint.append("&menu=yes");
        toPrint.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());
        toPrint.append("&").append(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeProgramBoui());
        toPrint.append("&").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeActivityBoui());
        if(controller.getActionCode() != null && XwfKeys.ACTION_ASSOCIATE_KEY.equals(controller.getActionCode()))
        {
            toPrint.append("&").append(XwfKeys.ACTION_CODE_KEY).append("=").append(controller.getActionCode());
        }
        toPrint.append("','std');");        
        openDocList.add(toPrint);
    }    
    public static StringBuffer buildMessagesListOpenDoc(XwfController controller,List openDocList) throws boRuntimeException 
    {
        StringBuffer toPrint = new StringBuffer();
        toPrint.append("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__messageViewer.jsp',");
        toPrint.append("'?docid=").append(controller.getDocHTML().getDocIdx());
        toPrint.append("&method=edit");        
        toPrint.append("&menu=yes");             
        toPrint.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());
        toPrint.append("&").append(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeProgramBoui());                                        
        toPrint.append("','std');");        
        if(openDocList != null)
        {
            openDocList.add(toPrint);
        }
        return toPrint;
    }      
    
    public static StringBuffer buildViewFlowOpenDoc(XwfController controller,List openDocList) throws boRuntimeException 
    {
        StringBuffer toPrint = new StringBuffer();
        toPrint.append("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfViewerFlow.jsp',");
        toPrint.append("'?docid=").append(controller.getDocHTML().getDocIdx());
        toPrint.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());
        toPrint.append("&").append(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeProgramBoui());                                        
        toPrint.append("','std',null,'Fluxo de trabalho');");        
        if(openDocList != null)
        {
            openDocList.add(toPrint);
        }
        return toPrint;
    }
    private static boolean isForEdit(boObject variable) throws boRuntimeException
    {
        boolean result = false;
        boObject varValue = null;
        long variableType = -1;
        long showMode = -1;
        long maxoccurs =  -1;                        
        varValue = variable.getAttribute("value").getObject();
        if(varValue != null)
        {
            variableType = varValue.getAttribute("type").getValueLong(); 
            if(variableType == 0)            
            {
                maxoccurs =  varValue.getAttribute("maxoccurs").getValueLong();
                if(maxoccurs == 1)
                {                    
                    showMode = variable.getAttribute("showMode").getValueLong();
                    if(showMode == 0)
                    {
                        result = true;
                    }
                }
            }
        }
        return result;
    }
    public static StringBuffer buildProgramOpenDoc(XwfController controller,List openDocList,String jspName) throws boRuntimeException
    {
        return buildProgramOpenDoc(controller,openDocList,jspName,"closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true");
    }
    public static StringBuffer buildProgramOpenDoc(XwfController controller,List openDocList,String jspName,String options) throws boRuntimeException 
    {
        return buildProgramOpenDoc(controller,openDocList,jspName,"closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true",null);
    }    
    public static StringBuffer buildProgramOpenDoc(XwfController controller,List openDocList,String jspName,String options,String parameters) throws boRuntimeException 
    {   
        StringBuffer toPrint = new StringBuffer();
        toPrint.append("winmain().openDocUrl('");
        toPrint.append(options);
        toPrint.append("','");
        toPrint.append(jspName).append("',");
        toPrint.append("'?docid=").append(controller.getDocHTML().getDocIdx());
        toPrint.append("&method=").append("edit");        
        toPrint.append("&menu=yes");        
        toPrint.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());
        toPrint.append("&").append(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeProgramBoui());
        if(controller.getRuntimeActivityBoui() != -1)
        {
            toPrint.append("&").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeActivityBoui());
        }
        if(parameters != null)
        {
            toPrint.append(parameters);    
        }
        toPrint.append("','std');");        
        if(openDocList != null)
        {
            openDocList.add(toPrint);
        }
        return toPrint;
    }  
    public static StringBuffer buildProgramAttributesOpenDoc(XwfController controller,List openDocList) throws boRuntimeException 
    {
        StringBuffer toPrint = new StringBuffer();
        toPrint.append("winmain().openDocUrl('closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true','__xwfViewerActivityGeneral.jsp',");
        toPrint.append("'?docid=").append(controller.getDocHTML().getDocIdx());
        toPrint.append("&method=edit");        
        toPrint.append("&menu=yes");        
        toPrint.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());
        toPrint.append("&").append(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeProgramBoui());
        toPrint.append("&").append(XwfKeys.VIEWER_TYPE_KEY).append("=").append("show.attributes");        
        toPrint.append("','std');");
        toPrint.append("\n");
        if(openDocList != null)
        {
            openDocList.add(toPrint);
        }
        return toPrint;
    }
    public static void buildDefActivitysOpenDoc(XwfController controller,StringBuffer toPrint,long boui, String type, String method) throws boRuntimeException
    {
        String jspName = null;
        toPrint.append("winmain().openDoc('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','xwfdefactivity','edit','");
        if(xwfHelper.STEP_ACTIVITY.equals(type))
        {
            jspName = "xwfdefactivity_generaledit.jsp";            
        }
        else if(xwfHelper.STEP_USER_CALL_PROG.equals(type))
        {
            jspName = "xwfdefactivity_generalcallprogram_edit.jsp";
            toPrint.append("docid=").append(controller.getDocHTML().getDocIdx());
        }
        toPrint.append("&boui=").append(boui);
        toPrint.append("&method=").append(method);        
        toPrint.append("&menu=yes");
        toPrint.append("&object=xwfDefActivity");    
        toPrint.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());
        toPrint.append("&").append(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeProgramBoui());
        toPrint.append("&").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeActivityBoui());
        toPrint.append("','std','").append(jspName).append("?');");          
    }
    public static void buildDefActivitysOpenDocUrl(XwfController controller,StringBuffer toPrint,String type) throws boRuntimeException
    {
        String jspName = null;
        toPrint.append("winmain().openDocUrl('closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true',");        
        if(xwfHelper.STEP_ACTIVITY.equals(type))
        {
            toPrint.append("'xwfdefactivity_generalwizard.jsp','?");
        }
        else if(xwfHelper.STEP_USER_CALL_PROG.equals(type))
        {
            toPrint.append("'xwfdefactivity_generalcallprogram_edit.jsp','?");
            toPrint.append("docid=").append(controller.getDocHTML().getDocIdx());
        }
        toPrint.append("&method=").append("new");        
        toPrint.append("&menu=yes");
        toPrint.append("&object=xwfDefActivity");
        toPrint.append("&deftype=").append(type);        
        toPrint.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());
        toPrint.append("&").append(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeProgramBoui());
        toPrint.append("&").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeActivityBoui());
        toPrint.append("','std');");        
    }
    public static void buildDefActivitysOpenDoc(XwfController controller,StringBuffer toPrint,String type) throws boRuntimeException 
    {   
        String jspName = null;
        toPrint.append("winmain().openDoc('closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true','xwfdefactivity','edit','");
        if(xwfHelper.STEP_ACTIVITY.equals(type))
        {
            jspName = "xwfdefactivity_generalwizard.jsp";
        }
        else if(xwfHelper.STEP_USER_CALL_PROG.equals(type))
        {
            jspName = "xwfdefactivity_generalcallprogram_edit.jsp";
            toPrint.append("docid=").append(controller.getDocHTML().getDocIdx());
        }
        toPrint.append("&method=").append("new");        
        toPrint.append("&menu=yes");
        toPrint.append("&object=xwfDefActivity");
        toPrint.append("&deftype=").append(type);        
        toPrint.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());
        toPrint.append("&").append(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeProgramBoui());
        toPrint.append("&").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeActivityBoui());
        toPrint.append("','std','").append(jspName).append("?');");                      
    }
    public static void buildOptionalQueueOpenDoc(XwfController controller,List openDocList) throws boRuntimeException
    {
        StringBuffer toPrint = null;
        boObject object = null;
        String type = null;
        List queue = controller.getOptionalQueue();
        for (int i = 0; i < queue.size(); i++)
        {
            object = (boObject)queue.get(i);
            toPrint = new StringBuffer();
            if("xwfReassign".equals(object.getName()))
            {
                toPrint.append(buildReassignOpenDoc(controller,String.valueOf(object.getBoui())));               
            }
            else if("xwfActivityTransfer".equals(object.getName()))
            {
                toPrint.append(buildTransferOpenDoc(controller,String.valueOf(object.getBoui())));               
            }
            else
            {
                type = object.getAttribute("type").getValueString();
                if(type != null)
                {                    
                    buildDefActivitysOpenDoc(controller,toPrint,object.getBoui(),type,"edit");                          
                }                            
            }
            openDocList.add(toPrint);
        }        
        controller.getOptionalQueue().clear();
    }
    
    public static String buildReassignOpenDoc(XwfController controller,String boui) throws boRuntimeException 
    {   
        StringBuffer toPrint = new StringBuffer();
        String jspName = "xwfreassign_generaledit.jsp";
        toPrint.append("winmain().openDoc('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true'").append(",");                
        toPrint.append("'").append("xwfreassign").append("'").append(",'edit',");                            
        toPrint.append("'docid=").append(controller.getDocHTML().getDocIdx());                
        toPrint.append("&menu=yes");       
        toPrint.append("&object=xwfReassign");
        toPrint.append("&fromAttribute=reassigns");
        toPrint.append("&ctxParentIdx=").append(controller.getDocHTML().getDocIdx());        
        toPrint.append("&ctxParent=").append(controller.getRuntimeActivityBoui());        
        if(boui != null)
        {
            toPrint.append("&method=edit");
            toPrint.append("&boui=").append(boui);
        }
        else
        {
            toPrint.append("&method=new");
            toPrint.append("&addToCtxParentBridge=reassigns");
        }
        toPrint.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());
        toPrint.append("&").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeActivityBoui());
        toPrint.append("&").append(XwfKeys.MAIN_CONTROLLER_KEY).append("=").append("true");
        toPrint.append("&").append("pathItem").append("=").append("1");
        toPrint.append("','std','").append(jspName).append("?');");         
        return toPrint.toString();
    }
     public static String buildTransferOpenDoc(XwfController controller,String boui) throws boRuntimeException 
    {   
        StringBuffer toPrint = new StringBuffer();
        String jspName = "xwfactivitytransfer_generaleditTransfer.jsp";
        toPrint.append("winmain().openDoc('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true'").append(",");                
        toPrint.append("'").append("xwfactivitytransfer").append("'").append(",'edit',");                            
        toPrint.append("'docid=").append(controller.getDocHTML().getDocIdx());                
        toPrint.append("&menu=yes");       
        toPrint.append("&object=xwfActivityTransfer");
        toPrint.append("&fromAttribute=transfer");
        toPrint.append("&formMode=quest");
        toPrint.append("&ctxParentIdx=").append(controller.getDocHTML().getDocIdx());        
        toPrint.append("&ctxParent=").append(controller.getRuntimeActivityBoui());
        toPrint.append("&doNotRedirect=true");
        if(boui != null)
        {
            toPrint.append("&method=edit");
            toPrint.append("&boui=").append(boui);
        }
        else
        {
            toPrint.append("&method=new");
        }
        toPrint.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());
        toPrint.append("&").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeActivityBoui());
        toPrint.append("&").append(XwfKeys.MAIN_CONTROLLER_KEY).append("=").append("true");
        toPrint.append("&").append("pathItem").append("=").append("1");
        toPrint.append("','std','").append(jspName).append("?');");         
        return toPrint.toString();
    } 
    
    private static int findTemplate(XwfController controller,boObject variable) throws boRuntimeException 
    {
        int result = 0;
        boObjectList templates = null;
        boObject template = null;
        long templateBoui = variable.getAttribute("templateBoui").getValueLong();
        if(templateBoui > 0)
        {
            template = controller.getObject(templateBoui);
            processTemplate(controller,variable,template);
            result = 1;
        }
        else
        {                    
            String keyWords = variable.getAttribute("keyWords").getValueString();
            templates = boTemplateManager.findTemplateFromWords(variable.getEboContext(),keyWords);
            
            if(templates != null)
            {
                result = templates.getRowCount();   
            }
            
            if(result == 1)
            {
                templates.beforeFirst();
                if(templates.next())
                {
                    template = templates.getObject();
                    processTemplate(controller,variable,template);                    
                }                  
            }
            else
            {
                controller.addInformation(XwfKeys.IN_FINDING_TEMPLATE_KEY,Boolean.TRUE);
            }
        }
        return result;
    }
    private static void processTemplate(XwfController controller,boObject variable,boObject template) throws boRuntimeException
    {
        boObject varValue = variable.getAttribute("value").getObject();    
        boObject object = controller.getEngine().getBoManager().getValueBoObject(varValue);                    
        if(object == null)
        {
            String attrName = xwfHelper.getTypeName(varValue);
            boObject boDef =  varValue.getAttribute("object").getObject();            
            String objName = boDef.getAttribute("name").getValueString();             
        
            object = controller.getEngine().getBoManager().createObject(objName,template,controller.getRuntimeActivity());
            varValue.getAttribute(attrName).setObject(object);
        
        }  
        else
        {
            controller.getEngine().getBoManager().applyTemplate(object,template.getBoui(),controller.getRuntimeActivity());
        }
        variable.getAttribute("templateMode").setValueLong(1);
        controller.getEngine().getBoManager().updateObject(variable);        
    }
    private static void buildTemplateProcessOpenDoc(XwfController controller,List openDocList,long variable, boolean allTemplates) throws boRuntimeException 
    {
        StringBuffer toPrint = new StringBuffer();
        toPrint.append("winmain().openDocUrl('closeWindowOnCloseDoc=true,showCloseIcon=false,activeOnOpen=false','__xwfViewerActivityTemplateList.jsp',");
        toPrint.append("'?docid=").append(controller.getDocHTML().getDocIdx());
        toPrint.append("&method=edit");        
        toPrint.append("&menu=yes");
        toPrint.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());
        toPrint.append("&").append(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeProgramBoui());
        toPrint.append("&").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeActivityBoui());
        toPrint.append("&").append("variable").append("=").append(variable);
        toPrint.append("&").append("allTemplates").append("=").append(allTemplates);
        toPrint.append("','std');");        
        openDocList.add(toPrint);
    }        
}