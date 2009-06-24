/*Enconding=UTF-8*/
package netgest.xwf.presentation;

import java.io.IOException;

import java.io.PrintWriter;
import java.util.ArrayList;

import java.util.List;
import javax.servlet.jsp.PageContext;

import netgest.bo.controller.Navigator;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.def.boDefMethod;
import netgest.bo.impl.document.*;
import netgest.bo.impl.document.print.PrintHelper;
import netgest.bo.message.utils.MessageUtils;
import netgest.bo.presentation.render.Browser;
import netgest.bo.presentation.render.HTMLBuilder;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.Menu;
import netgest.bo.presentation.render.elements.MenuItem;
import netgest.bo.runtime.*;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.security.securityOPL;
import netgest.bo.security.securityRights;
import netgest.xwf.EngineGate;
import netgest.xwf.common.xwfHelper;
import netgest.utils.ngtXMLHandler;
import xeo.client.business.helper.RegistryHelper;

/**
 * <p>Title: xwfMenuViewer </p>
 * <p>Description: Classe resposável por apresentar o menu correspondente a actividade</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public class xwfMenuViewer 
{
    private final static String WKF_PATH = "ieThemes/0/wkfl/"; //$NON-NLS-1$
    private final static String WKF_CORE_NORMAL_ICON = "bullet_ball_glass_green.gif"; //$NON-NLS-1$
    private final static String WKF_CORE_REQUIRED_ICON = "bullet_ball_glass_red.gif"; //$NON-NLS-1$
    private final static String WKF_OBJECT_NORMAL_ICON = "bullet_ball_glass_blue.gif"; //$NON-NLS-1$
    private final static String WKF_OBJECT_SPECIFIC_ICON = "bullet_ball_glass_yellow.gif";     //$NON-NLS-1$
    
    public static void render(XwfController controller, PageContext page) throws java.io.IOException,boRuntimeException
    {
        PrintWriter out = new PrintWriter(page.getOut());
        out.print("\n<TR  height='27px'  >");                     //$NON-NLS-1$
        out.print("\n<TD valign=top align=left colspan=\"3\">\n"); //$NON-NLS-1$
        renderToolBarMenu(controller,out);
        out.print("\n</TD>"); //$NON-NLS-1$
        out.print("\n</TR>");                          //$NON-NLS-1$
    }
    private static void renderToolBarMenu(XwfController controller, PrintWriter out) throws boRuntimeException,IOException
    {        

        ArrayList toRet = new ArrayList();
        EngineGate engine = controller.getEngine();
        boObject runtimeActivity = controller.getRuntimeActivity();
        if(runtimeActivity != null && haveSecurityRights(runtimeActivity))
        {
//            renderNavigationMenu(controller,out,toRet);        
            renderCommonMenu(engine,runtimeActivity,out,toRet);
            toRet.add(MenuItem.getVerticalLineSeparator());                          
            if(isValidForRender(controller))
            {
                renderOptionsActions(controller,runtimeActivity,toRet);                    
            }        
            renderCoreActions(controller,runtimeActivity,toRet);
            renderSpecificActions(controller,runtimeActivity,toRet);
            renderObjectActions(engine,runtimeActivity,toRet);
        }            
        PageController pageController = new PageController();
        Menu m = new Menu(toRet);
        m.writeHTML(out,controller.getDocHTML(),null,pageController);                            
    }
    private static void renderNavigationMenu(XwfController controller, PrintWriter out,ArrayList toRet)  throws boRuntimeException,IOException
    {
        Navigator navigator = controller.getNavigator();
        MenuItem svItem = null;
        svItem = new MenuItem();
        svItem.setId(Messages.getString("xwfMenuViewer.9"));         //$NON-NLS-1$
        svItem.setImgURL(navigator.getImageLinkForPriorPage());        
        svItem.setOnClickCode(navigator.renderLinkForPriorPage().toString());
        svItem.setVisible(true);
        svItem.setDisabled(false);
        svItem.setAccessKey('B');
        svItem.setTitle(Messages.getString("xwfMenuViewer.10")); //$NON-NLS-1$
        toRet.add(svItem);      
        
        svItem = new MenuItem();
        svItem.setId("next");         //$NON-NLS-1$
        svItem.setImgURL(navigator.getImageLinkForNextPage());           
        svItem.setOnClickCode(navigator.renderLinkForNextPage().toString());
        svItem.setVisible(true);
        svItem.setDisabled(false);
        svItem.setAccessKey('F');     
        svItem.setTitle(Messages.getString("xwfMenuViewer.12")); //$NON-NLS-1$
        toRet.add(svItem);                                        

        svItem = new MenuItem();
        svItem.setId("home");         //$NON-NLS-1$
        svItem.setImgURL(navigator.getImageLinkForHomePage());        
        svItem.setOnClickCode(navigator.renderLinkForHomePage().toString());
        svItem.setVisible(true);
        svItem.setDisabled(false);
        svItem.setAccessKey('H');    
        svItem.setTitle(Messages.getString("xwfMenuViewer.14")); //$NON-NLS-1$
        toRet.add(svItem);                                                
    }
    
//    private static void renderCommonMenu(XwfController controller, JspWriter out,ArrayList toRet)  throws boRuntimeException,IOException
    private static void renderCommonMenu(EngineGate engine,boObject runtimeActivity, PrintWriter out,ArrayList toRet)  throws boRuntimeException,IOException
    {
//        boObject runtimeActivity = controller.getRuntimeActivity();
        MenuItem svItem = null;
        if(runtimeActivity != null && 
            ("xwfActivitySend".equals(runtimeActivity.getName()) ||  //$NON-NLS-1$
             "xwfActivityReceive".equals(runtimeActivity.getName()) || //$NON-NLS-1$
             "xwfCreateReceivedMessage".equals(runtimeActivity.getName())) //$NON-NLS-1$
        )
        {
            svItem = new MenuItem();
            svItem.setId("print");         //$NON-NLS-1$
            svItem.setImgURL(Browser.getThemeDir() + "menu/16_print.gif");         //$NON-NLS-1$
            svItem.setOnClickCode("boForm.Print();"); //$NON-NLS-1$
            svItem.setVisible(true);
            svItem.setDisabled(false);
            svItem.setAccessKey('I');     
            toRet.add(svItem);            
                        
            boObject object = xwfHelper.getMasterObject(engine,runtimeActivity);     
            if(object != null)
            {
                
//            if(printObjects.size() > 0)
//            {
                String  XeoWin32Client_address = runtimeActivity.getEboContext().getXeoWin32Client_adress();
                if((XeoWin32Client_address!=null && RegistryHelper.isClientConnected(XeoWin32Client_address)) ||
                    (PrintHelper.isXeoControlActive(runtimeActivity.getEboContext()))
                )
                {
                    if(!"messageLetter".equals(object.getName()) ||  //$NON-NLS-1$
                    object.getAttribute("impCentral") == null || //$NON-NLS-1$
                    !"1".equals(object.getAttribute("impCentral").getValueString()) //$NON-NLS-1$ //$NON-NLS-2$
                    )
                    {
                        List printObjects = DocumentHelper.getAllObjectDocuments(object);
                        String list = ""; //$NON-NLS-1$
                        for (int i = 0; i < printObjects.size(); i++) 
                        {
                            object = (boObject)printObjects.get(i);
                            list += object.getBoui() + ";"; //$NON-NLS-1$
                        }
                        
                        MenuItem svItem2 = null;
                        svItem2 = new MenuItem();
                        svItem2.setId("printDocs"); //$NON-NLS-1$
                        svItem2.setLabel(Messages.getString("xwfMenuViewer.8")); //$NON-NLS-1$
                        svItem2.setImgURL(Browser.getThemeDir() + "menu/16_print.gif");                 //$NON-NLS-1$
    //                    svItem2.setOnClickCode("boForm.executeStaticMeth('netgest.bo.impl.document.print.PrintHelper.printAllDocuments',['this']);");
                        svItem2.setOnClickCode("window.showModalDialog('__printDocuments.jsp?selectedObjects="+list+"&docid="+engine.getBoManager().getDocHTML().getDocIdx()+"',window,'dialogHeight:400px;dialogWidth;400px;scroll=yes;status=no;resizable=yes;help=no;unadorned=yes');"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        svItem2.setVisible(true);
                        svItem2.setDisabled(false);
                        svItem2.setAccessKey('I');
                        svItem.addSubMenuItem(svItem2);
                    }
                }
//            }
            }
        }
        else
        {
            svItem = new MenuItem();
            svItem.setId("print");         //$NON-NLS-1$
            svItem.setImgURL(Browser.getThemeDir() + "menu/16_print.gif");         //$NON-NLS-1$
            svItem.setOnClickCode("boForm.Print();"); //$NON-NLS-1$
            svItem.setVisible(true);
            svItem.setDisabled(false);
            svItem.setAccessKey('I');     
            toRet.add(svItem);        
        }                  
    }
    private static void renderActivityActions(XwfController controller, boObject runtimeActivity,MenuItem svItemBase,boolean flow) throws boRuntimeException
    {
            MenuItem svItemActivity = new MenuItem();
            svItemActivity.setId(XwfKeys.ACTION_SAVE_KEY);                 
            svItemActivity.setImgURL("resources/" + runtimeActivity.getName() + "/ico16.gif"); //$NON-NLS-1$ //$NON-NLS-2$
            svItemActivity.setLabel("<u>T</u>arefa");  //$NON-NLS-1$
            svItemActivity.setTitle(Messages.getString("xwfMenuViewer.39")); //$NON-NLS-1$
            svItemActivity.setVisible(true);
            svItemActivity.setDisabled(false);
            svItemActivity.setAccessKey('T');         
            
            MenuItem svItem = null;
            
            // Novo
            svItem = new MenuItem();
            svItem.setId(XwfKeys.ACTION_SAVE_KEY);            
            svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
            svItem.setLabel("<u>N</u>ova");                             //$NON-NLS-1$
            svItem.setVisible(true);
            svItem.setDisabled(false);
            svItem.setAccessKey('N');    
            svItem.setTitle(Messages.getString("xwfMenuViewer.7")); //$NON-NLS-1$
            svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_LAUNCH_KEY+"[]["+xwfHelper.STEP_ACTIVITY+"]');boForm.BindValues();");             //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            svItemActivity.addSubMenuItem(svItem);
            
            //Atribuir
            StringBuffer newTaskOnClick = new StringBuffer();
            xwfMainViewer.buildDefActivitysOpenDoc(controller,newTaskOnClick,xwfHelper.STEP_ACTIVITY);           
            svItem = new MenuItem();
            svItem.setId(XwfKeys.ACTION_SAVE_KEY);            
            svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
            svItem.setLabel(Messages.getString("xwfMenuViewer.45"));                             //$NON-NLS-1$
            svItem.setVisible(true);
            svItem.setDisabled(false);
            svItem.setAccessKey('A');    
            svItem.setTitle(Messages.getString("xwfMenuViewer.46")); //$NON-NLS-1$
            svItem.setOnClickCode(newTaskOnClick.toString());            
            svItemActivity.addSubMenuItem(svItem);                        
            
            //Remover
            if(controller.getRuntimeActivity().exists())
            {
                svItem = new MenuItem();
                svItem.setId(XwfKeys.ACTION_REMOVE_ACTIVITY_KEY);            
                svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
                svItem.setLabel(Messages.getString("xwfMenuViewer.47"));                             //$NON-NLS-1$
                svItem.setVisible(true);
                svItem.setDisabled(false);
                svItem.setAccessKey('R');    
                svItem.setTitle(Messages.getString("xwfMenuViewer.48"));                    		  //$NON-NLS-1$
                svItem.setOnClickCode("window.showModalDialog('__explorerDeleteObjects.jsp?selectedObjects="+controller.getRuntimeActivityBoui()+"&docid="+controller.getDocHTML().getDocIdx()+"',window,'dialogHeight:400px;dialogWidth;400px;scroll=yes;status=no;resizable=yes;help=no;unadorned=yes');"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                svItemActivity.addSubMenuItem(svItem);
            }

            // Mover
//            if(!flow)
//            {            
//                String flowStr = controller.getRuntimeProgram().getAttribute("flow").getValueString();   
//                String uniqueSid = runtimeActivity.getAttribute("unique_sid").getValueString();
//                if("".equals(flowStr) || "-1".equals(uniqueSid))
//                {                
                    StringBuffer connect = xwfMainViewer.buildProgramOpenDoc(controller, null,"__xwfViewerProgramToAssociate.jsp","closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true","&moveWhat=activity"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    svItem = new MenuItem();
                    svItem.setId(XwfKeys.ACTION_SAVE_KEY);            
                    svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
                    svItem.setLabel(Messages.getString("xwfMenuViewer.55"));                             //$NON-NLS-1$
                    svItem.setVisible(true);
                    svItem.setDisabled(false);
                    svItem.setAccessKey('z');    
                    svItem.setTitle(Messages.getString("xwfMenuViewer.56")); //$NON-NLS-1$
                    svItem.setOnClickCode(connect.toString());
                    svItemActivity.addSubMenuItem(svItem);                         
//                }                                
//            }
            
            svItemBase.addSubMenuItem(svItemActivity);
            
    }
    private static void renderProgramActions(XwfController controller, boObject runtimeActivity,MenuItem svItemBase,boolean flow) throws boRuntimeException
    {
            MenuItem svItemProgram = new MenuItem();
            svItemProgram.setId(XwfKeys.ACTION_SAVE_KEY);            
            svItemProgram.setImgURL("resources/" + controller.getRuntimeProgram().getName() + "/ico16.gif"); //$NON-NLS-1$ //$NON-NLS-2$
            svItemProgram.setLabel(Messages.getString("xwfMenuViewer.6"));  //$NON-NLS-1$
            svItemProgram.setTitle(Messages.getString("xwfMenuViewer.60")); //$NON-NLS-1$
            svItemProgram.setVisible(true);
            svItemProgram.setDisabled(false);
            svItemProgram.setAccessKey('F');         
            
            MenuItem svItem = null;
            
            // Novo
            boolean workflowAdministrator = xwfHelper.isWorkFlowAdministrator(controller.getEngine());
            if(workflowAdministrator) 
            { 
                svItem = new MenuItem();
                svItem.setId(XwfKeys.ACTION_SAVE_KEY);            
                svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
                svItem.setLabel(Messages.getString("xwfMenuViewer.61"));                             //$NON-NLS-1$
                svItem.setVisible(true);
                svItem.setDisabled(false);
                svItem.setAccessKey('N');    
                svItem.setTitle(Messages.getString("xwfMenuViewer.62")); //$NON-NLS-1$
                svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_LAUNCH_KEY+"[]["+xwfHelper.STEP_USER_CALL_PROG+"]');boForm.BindValues();"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                svItemProgram.addSubMenuItem(svItem);
            }            
            
            //Atribuir
            if(workflowAdministrator) 
            {
                StringBuffer newWorkFlowOnClick = new StringBuffer();
                xwfMainViewer.buildDefActivitysOpenDoc(controller,newWorkFlowOnClick,xwfHelper.STEP_USER_CALL_PROG);
            
                svItem = new MenuItem();
                svItem.setId(XwfKeys.ACTION_SAVE_KEY);            
                svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
                svItem.setLabel(Messages.getString("xwfMenuViewer.45"));                             //$NON-NLS-1$
                svItem.setVisible(true);
                svItem.setDisabled(false);
                svItem.setAccessKey('A');    
                svItem.setTitle(Messages.getString("xwfMenuViewer.67")); //$NON-NLS-1$
                svItem.setOnClickCode(newWorkFlowOnClick.toString());
                svItemProgram.addSubMenuItem(svItem);
            }
            
            // Cancelar
            if(controller.getRuntimeProgram().exists())
            {                    
                svItem = new MenuItem();
                svItem.setId(XwfKeys.ACTION_CANCEL_PROGRAM_KEY);            
                svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
                svItem.setLabel(Messages.getString("xwfMenuViewer.68"));                             //$NON-NLS-1$
                svItem.setVisible(true);
                svItem.setDisabled(false);
                svItem.setAccessKey('C');    
                svItem.setTitle(Messages.getString("xwfMenuViewer.69")); //$NON-NLS-1$
                svItem.setOnClickCode(Messages.getString("xwfMenuViewer.70")+XwfKeys.ACTION_CANCEL_PROGRAM_KEY+"');boForm.BindValues();wait();}"); //$NON-NLS-1$ //$NON-NLS-2$
                svItemProgram.addSubMenuItem(svItem);                                    
            }    

            // Mover
//            if(!flow)
//            {            
//                String flowStr = controller.getRuntimeProgram().getAttribute("flow").getValueString();                
//                if(flowStr == null || "".equals(flowStr))
//                {                
                    StringBuffer connect = xwfMainViewer.buildProgramOpenDoc(controller, null,"__xwfViewerProgramToAssociate.jsp","closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true","&moveWhat=program"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    svItem = new MenuItem();
                    svItem.setId(XwfKeys.ACTION_SAVE_KEY);            
                    svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
                    svItem.setLabel(Messages.getString("xwfMenuViewer.75"));                             //$NON-NLS-1$
                    svItem.setVisible(true);
                    svItem.setDisabled(false);
                    svItem.setAccessKey('M');    
                    svItem.setTitle(Messages.getString("xwfMenuViewer.76")); //$NON-NLS-1$
                    svItem.setOnClickCode(connect.toString());
                    svItemProgram.addSubMenuItem(svItem);                         
//                }                                
//            }
            
            svItemBase.addSubMenuItem(svItemProgram);
            
            //Transferência
           
            svItem = new MenuItem();
            svItem.setId(XwfKeys.ACTION_TRANSFER_PROGRAM_KEY);
            svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
            svItem.setLabel(Messages.getString("xwfMenuViewer.77")); //$NON-NLS-1$
            svItem.setOnClickCode(xwfMainViewer.buildTransferOpenDoc(controller,null));
            svItem.setVisible(true);
            svItem.setDisabled(false);
            svItem.setAccessKey('T');     
            svItem.setTitle(Messages.getString("xwfMenuViewer.78")); //$NON-NLS-1$
            svItemProgram.addSubMenuItem(svItem);
    }
    private static void renderOptionsActions(XwfController controller, boObject runtimeActivity,ArrayList toRet) throws boRuntimeException
    {
        String runningState = runtimeActivity.getStateAttribute("runningState").getValueString(); //$NON-NLS-1$
        String oneShotActivity = runtimeActivity.getAttribute("oneShotActivity").getValueString(); //$NON-NLS-1$
        if("0".equals(oneShotActivity)) //$NON-NLS-1$
        {                   
           StringBuffer connect = null;
            boObjectList wflow = null;  
            boolean flow = true;
            String exec_flow = null;            

            boObject object = xwfHelper.getMasterObject(controller.getEngine(), controller.getRuntimeActivity());
            if(object != null)
            {
                exec_flow = "select xwfProgram where versions in (select xwfProgramRuntime.programDef where variables.value.valueObject = "+ object.getBoui()+  //$NON-NLS-1$
                " or variables.value.valueList = "+ object.getBoui()+") and boui="; //$NON-NLS-1$ //$NON-NLS-2$
                wflow = boObjectList.list(runtimeActivity.getEboContext(),"SELECT xwfProgram WHERE versions.input.name = '" + object.getName() + "' ORDER BY BOUI DESC"); //$NON-NLS-1$ //$NON-NLS-2$
                wflow.beforeFirst();
                if(wflow.getRecordCount() > 0)
                {
                    flow = false;
                }
            }        
        
            MenuItem svItemBase = new MenuItem();
            svItemBase.setId(XwfKeys.ACTION_SAVE_KEY);            
            svItemBase.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
            svItemBase.setLabel(Messages.getString("xwfMenuViewer.5"));                             //$NON-NLS-1$
            svItemBase.setVisible(true);
            svItemBase.setDisabled(false);
            svItemBase.setAccessKey('O');    
            svItemBase.setTitle(Messages.getString("xwfMenuViewer.88")); //$NON-NLS-1$
            
            renderActivityActions(controller,runtimeActivity,svItemBase,flow);
            renderProgramActions(controller,runtimeActivity,svItemBase,flow);
                                             
            MenuItem svItem = null;
            String flowStr = controller.getRuntimeProgram().getAttribute("flow").getValueString(); //$NON-NLS-1$
            if(flowStr != null && !"".equals(flowStr)) //$NON-NLS-1$
            {        
                if(wflow != null && wflow.getRecordCount() > 0)
                {
                    MenuItem svItemWorkFlow = new MenuItem();
                    svItemWorkFlow.setId(XwfKeys.ACTION_SAVE_KEY);            
                    svItemWorkFlow.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
                    svItemWorkFlow.setLabel(Messages.getString("xwfMenuViewer.4"));                             //$NON-NLS-1$
                    svItemWorkFlow.setVisible(true);
                    svItemWorkFlow.setDisabled(false);
                    svItemWorkFlow.setAccessKey('z');    
                    svItemWorkFlow.setTitle(Messages.getString("xwfMenuViewer.4")); //$NON-NLS-1$

                    boObject program = null;
                    long programBoui = 0;
                    long p_executed = 0;
                    String label = null;
                    while(wflow != null && wflow.next())
                    {
                        program = wflow.getObject();
                        label = program.getAttribute("name").getValueString(); //$NON-NLS-1$
                        programBoui = program.getBoui(); 
                        if(exec_flow != null)
                        {
                            //muito lento
                            //boObjectList lexec = boObjectList.list(controller.getDocHTML().getEboContext(), exec_flow+programBoui);
                            //p_executed = lexec.getRowCount();
                            p_executed=0;
                        }                                                   
                        svItem = new MenuItem();
                        svItem.setId(XwfKeys.ACTION_CREATE_PROGRAM_KEY);            
                        svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
                        if(p_executed > 0)
                        {                         
                            svItem.setLabel(label +"<span style='color:red'>(<b>"+Long.toString(p_executed)+"</b>)</span>"); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        else
                        {
                            svItem.setLabel(label);
                        }
                        svItem.setVisible(true);
                        svItem.setDisabled(false);
                        svItem.setAccessKey('z');    
                        svItem.setTitle(label);                    		 
                        svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_CREATE_PROGRAM_KEY+"["+programBoui+"]');boForm.BindValues();wait();"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        svItemWorkFlow.addSubMenuItem(svItem);                                                                                                            
                    }                    
                    svItemBase.addSubMenuItem(svItemWorkFlow);
                }
            }
            
            toRet.add(svItemBase);              
            toRet.add(MenuItem.getVerticalLineSeparator());
        }
        
    }
    /**
     * Constroi as opções especificas do controlo das actividades.
     * @param runtimeActivity, actividade a decorrer.
     * @param toRet, lista de opções para esta actividade.
     */      
    private static void renderCoreActions(XwfController controller, boObject runtimeActivity,ArrayList toRet) throws boRuntimeException    
    {
        String runningState = runtimeActivity.getStateAttribute("runningState").getValueString(); //$NON-NLS-1$
        String oneShotActivity = runtimeActivity.getAttribute("oneShotActivity").getValueString(); //$NON-NLS-1$
        MenuItem svItem = null;
        
        // Actualizar
        boolean hideActualizar = false;
        String showLabel = Messages.getString("xwfMenuViewer.101"); //$NON-NLS-1$
        String title = Messages.getString("xwfMenuViewer.102"); //$NON-NLS-1$
        char access = 'v';
        if("xwfActivitySend".equals(runtimeActivity.getName()) && "close".equals(runningState)) //$NON-NLS-1$ //$NON-NLS-2$
        {
            showLabel = "Actuali<u>z</u>ar";
            title = "Actualizar";
            access = 'c';
        }
        svItem = new MenuItem();
        svItem.setId(XwfKeys.ACTION_SAVE_KEY);            
        svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
        svItem.setLabel(showLabel);                
        svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_SAVE_KEY+"');boForm.BindValues();wait();");
        svItem.setVisible(true);
        svItem.setDisabled(false);
        svItem.setAccessKey(access);    
        svItem.setTitle(title);
        toRet.add(svItem);                                

        // Reabrir
        if("close".equals(runningState) && !"xwfActivitySend".equals(runtimeActivity.getName())) //$NON-NLS-1$ //$NON-NLS-2$
        {
            svItem = new MenuItem();
            svItem.setId(XwfKeys.ACTION_REOPEN_KEY);            
            svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
            svItem.setLabel(Messages.getString("xwfMenuViewer.118"));                             //$NON-NLS-1$
            svItem.setVisible(true);
            svItem.setDisabled(false);
            svItem.setAccessKey('N');    
            svItem.setTitle("Clique aqui para reabrir a tarefa"); //$NON-NLS-1$
            svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_REOPEN_KEY+"');boForm.BindValues();wait();");                     //$NON-NLS-1$ //$NON-NLS-2$
            toRet.add(svItem);                
        }  
        
        if(isValidForRender(controller))
        {
            // Open        
            if("create".equals(runningState) && "0".equals(oneShotActivity)) //$NON-NLS-1$ //$NON-NLS-2$
            {
                svItem = new MenuItem();
                svItem.setId(XwfKeys.ACTION_OPEN_KEY);
                svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
                svItem.setLabel(Messages.getString("xwfMenuViewer.3"));                 //$NON-NLS-1$
                svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_OPEN_KEY+"');setStateActivity('"+runtimeActivity.getBoui()+"');boForm.BindValues();wait();"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                svItem.setVisible(true);
                svItem.setDisabled(false);
                svItem.setAccessKey('i');    
                svItem.setTitle(Messages.getString("xwfMenuViewer.128")); //$NON-NLS-1$
                toRet.add(svItem);                        
            }
            
            if(!XwfKeys.ACTION_CLOSE_KEY.equals(runningState) &&
               !XwfKeys.ACTION_CANCEL_KEY.equals(runningState) &&
               !"xwfActivityDecision".equals(runtimeActivity.getName()) &&  //$NON-NLS-1$
               !"xwfActivityChoice".equals(runtimeActivity.getName())   && //$NON-NLS-1$
               !"xwfActivityPoll".equals(runtimeActivity.getName())) //$NON-NLS-1$
            {            
                if("xwfActivitySend".equals(runtimeActivity.getName()) || //$NON-NLS-1$
                   "xwfActivityCreateMessage".equals(runtimeActivity.getName()) //$NON-NLS-1$
                )
                {
    //                boObject msg = runtimeActivity.getAttribute("message").getObject().getAttribute("value").getObject().getAttribute("valueObject").getObject();
                    boObject msg = xwfHelper.getMasterObject(controller.getEngine(),runtimeActivity);
                    if(msg != null)
                    {
                        ArrayList r = MessageUtils.getChannelsMessage(msg);
                        String showProcess = runtimeActivity.getAttribute("showProcess").getValueString();
                        if("1".equals(showProcess) && r != null && (r.contains(MessageUtils.LETTER) || r.contains(MessageUtils.FAX)))
                        {
                            svItem = new MenuItem();            
                            svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);            
                            svItem.setVisible(true);
                            svItem.setDisabled(false);    
                            svItem.setId(XwfKeys.ACTION_RE_PROCESSAR_KEY);
                            svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_RE_PROCESSAR_KEY+"');setStateActivity('');boForm.BindValues();wait();");
                            svItem.setLabel("<u>P</u>rocessar");
                            svItem.setAccessKey('P');     
                            svItem.setTitle("(Re)Processar");
                            toRet.add(svItem);
                        }
                    }
                }
            }
            
            //Close
            if(!XwfKeys.ACTION_CLOSE_KEY.equals(runningState) &&
               !XwfKeys.ACTION_CANCEL_KEY.equals(runningState) &&
               !"xwfActivityDecision".equals(runtimeActivity.getName()) &&  //$NON-NLS-1$
               !"xwfActivityChoice".equals(runtimeActivity.getName())   && //$NON-NLS-1$
               !"xwfActivityPoll".equals(runtimeActivity.getName())) //$NON-NLS-1$
            {
                svItem = new MenuItem();            
                svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);            
                svItem.setVisible(true);
                svItem.setDisabled(false);            
                if("xwfActivitySend".equals(runtimeActivity.getName())) //$NON-NLS-1$
                {
                    svItem.setId(XwfKeys.ACTION_SEND_KEY);
                    svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_SEND_KEY+"');setStateActivity('');boForm.BindValues();wait();");
                    svItem.setLabel("<u>E</u>nviar/Completar");
                    svItem.setAccessKey('E');     
                    svItem.setTitle("Enviar/Completar mensagem");
                    toRet.add(svItem);
                }
                else if("xwfCreateReceivedMessage".equals(runtimeActivity.getName())) //$NON-NLS-1$
                {            
                    svItem.setId(XwfKeys.ACTION_SEND_KEY);                            
                    svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_SEND_KEY+"');setStateActivity('');boForm.BindValues();wait();"); //$NON-NLS-1$ //$NON-NLS-2$
                    svItem.setLabel(Messages.getString("xwfMenuViewer.156")); //$NON-NLS-1$
                    svItem.setAccessKey('c');     
                    svItem.setTitle(Messages.getString("xwfMenuViewer.157")); //$NON-NLS-1$
                    toRet.add(svItem);
                }
                else if("xwfWaitResponse".equals(runtimeActivity.getName())) //$NON-NLS-1$
                {                    
                    svItem.setId(XwfKeys.ACTION_CLOSE_KEY);
                    String ifrm = "document.getElementById('frm$1').contentWindow.document.getElementById('inc_saveOn__"; //$NON-NLS-1$
                    ifrm += runtimeActivity.getBoui();
                    ifrm += "__xwfWaitResponse').contentWindow.document.boFormSubmit.boFormSubmitXml.value"; //$NON-NLS-1$
                    //document.getElementById("frm$1").contentWindow.document.getElementById("inc_saveOn__173808__xwfWaitResponse").contentWindow.document.boFormSubmit.boFormSubmitXml.value
                    svItem.setOnClickCode("if(!"+ifrm+"){if(confirm('Não foi seleccionada nenhuma resposta, quer continuar ?')){setActionCode('"+XwfKeys.ACTION_CLOSE_KEY+"');setStateActivity('');boForm.BindValues();wait();}}else{setActionCode('"+XwfKeys.ACTION_CLOSE_KEY+"');setStateActivity('');boForm.BindValues();wait();}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    svItem.setLabel(Messages.getString("xwfMenuViewer.2")); //$NON-NLS-1$
                    svItem.setAccessKey('c');     
                    svItem.setTitle(Messages.getString("xwfMenuViewer.166")); //$NON-NLS-1$
                    toRet.add(svItem);
                }
                else
                {
                    svItem.setId(XwfKeys.ACTION_CLOSE_KEY);
                    svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_CLOSE_KEY+"');setStateActivity('');boForm.BindValues();wait();"); //$NON-NLS-1$ //$NON-NLS-2$
                    svItem.setLabel(Messages.getString("xwfMenuViewer.1")); //$NON-NLS-1$
                    svItem.setAccessKey('c');     
                    svItem.setTitle(Messages.getString("xwfMenuViewer.170")); //$NON-NLS-1$
                    toRet.add(svItem);                    
                }
            }
            
            // Cancel
            if("xwfActivitySend".equals(runtimeActivity.getName())) //$NON-NLS-1$
            {
                String aux = runtimeActivity.getAttribute("optional").getValueString();
                if(!"0".equals(aux) && "0".equals(oneShotActivity) && runtimeActivity.exists())
                {
                    svItem = new MenuItem();
                    svItem.setId(XwfKeys.ACTION_CANCEL_KEY);
                    svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
                    svItem.setLabel("C<u>a</u>ncelar");
                    svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_CANCEL_KEY+"');setStateActivity('');boForm.BindValues();wait();");
                    svItem.setVisible(true);
                    svItem.setDisabled(false);
                    svItem.setAccessKey('a');     
                    svItem.setTitle("Cancelar Actividade");
                    toRet.add(svItem);                                
                }    
            }
            
            //Reassign
            String showReassign = runtimeActivity.getAttribute("showReassign").getValueString(); //$NON-NLS-1$
            if(!XwfKeys.ACTION_CLOSE_KEY.equals(runningState) &&
               !XwfKeys.ACTION_CANCEL_KEY.equals(runningState) && 
               "1".equals(showReassign)) //$NON-NLS-1$
            {
                svItem = new MenuItem();
                svItem.setId(XwfKeys.ACTION_REASSIGN_KEY);
                svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
                svItem.setLabel(Messages.getString("xwfMenuViewer.0")); //$NON-NLS-1$
                svItem.setOnClickCode(xwfMainViewer.buildReassignOpenDoc(controller,null));
                svItem.setVisible(true);
                svItem.setDisabled(false);
                svItem.setAccessKey('r');     
                svItem.setTitle(Messages.getString("xwfMenuViewer.177")); //$NON-NLS-1$
                toRet.add(svItem);             
            }
        }
    }
    private static void renderSpecificActions(XwfController controller ,boObject runtimeActivity,ArrayList toRet) throws boRuntimeException
    {   
        String runningState = runtimeActivity.getStateAttribute("runningState").getValueString(); //$NON-NLS-1$
        MenuItem svItem = null;

        if("xwfActivityReceive".equals(runtimeActivity.getName()))             //$NON-NLS-1$
        {
            boObject variable = runtimeActivity.getAttribute("message").getObject(); //$NON-NLS-1$
            boObject valueObject = variable == null ? null : variable.getAttribute("value").getObject(); //$NON-NLS-1$
            boObject msg = valueObject == null ? null : valueObject.getAttribute("valueObject").getObject(); //$NON-NLS-1$
            if(msg == null || !"messageSystem".equals(msg.getBoDefinition().getName()) && //$NON-NLS-1$
            !"messageSystem".equals(msg.getBoDefinition().getBoSuperBo())) //$NON-NLS-1$
            {
                //Responder Reencaminhar
                svItem = new MenuItem();
                svItem.setId("responder"); //$NON-NLS-1$
                svItem.setImgURL(WKF_PATH + WKF_OBJECT_NORMAL_ICON);            
                svItem.setVisible(true);
                svItem.setDisabled(false);
                svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_REPLY_KEY+"');setStateActivity('');boForm.BindValues();wait();"); //$NON-NLS-1$ //$NON-NLS-2$
                svItem.setLabel(Messages.getString("xwfMenuViewer.188")); //$NON-NLS-1$
                svItem.setAccessKey('R');     
                svItem.setTitle(Messages.getString("xwfMenuViewer.189")); //$NON-NLS-1$
                toRet.add(svItem);
                addReplySubMenuItem(svItem, "reply");
                
                svItem = new MenuItem();
                svItem.setImgURL(WKF_PATH + WKF_OBJECT_NORMAL_ICON);            
                svItem.setVisible(true);
                svItem.setDisabled(false);
                svItem.setId("responderTodos"); //$NON-NLS-1$
                svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_REPLY_ALL_KEY+"');setStateActivity('');boForm.BindValues();wait();"); //$NON-NLS-1$ //$NON-NLS-2$
                svItem.setLabel(Messages.getString("xwfMenuViewer.195")); //$NON-NLS-1$
                svItem.setAccessKey('T');     
                svItem.setTitle(Messages.getString("xwfMenuViewer.196")); //$NON-NLS-1$
                toRet.add(svItem);
                addReplySubMenuItem(svItem, "replyAll");
            }

            svItem = new MenuItem();
            svItem.setImgURL(WKF_PATH + WKF_OBJECT_NORMAL_ICON);            
            svItem.setVisible(true);
            svItem.setDisabled(false);
            svItem.setId("reencaminhar"); //$NON-NLS-1$
            svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_FORWARD_KEY+"');setStateActivity('');boForm.BindValues();wait();"); //$NON-NLS-1$ //$NON-NLS-2$
            svItem.setLabel(Messages.getString("xwfMenuViewer.338")); //$NON-NLS-1$
            svItem.setAccessKey('E');      
            svItem.setTitle(Messages.getString("xwfMenuViewer.339")); //$NON-NLS-1$
            toRet.add(svItem);
            addReplySubMenuItem(svItem, XwfKeys.ACTION_FORWARD_KEY);
            
            if(!XwfKeys.ACTION_ASSOCIATE_KEY.equals(controller.getActionCode()))
            {
                svItem = new MenuItem();
                svItem.setImgURL(WKF_PATH + WKF_OBJECT_NORMAL_ICON);            
                svItem.setVisible(true);
                svItem.setDisabled(false);
                svItem.setId("associate");
                svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_ASSOCIATE_KEY+"');setStateActivity('"+runtimeActivity.getBoui()+"');boForm.BindValues();wait();");
                svItem.setLabel("<u>A</u>ssociar");
                svItem.setAccessKey('A');     
                svItem.setTitle("Associar");
                toRet.add(svItem);                            
            }
        }
    }    
    private static void renderObjectActions(EngineGate engine, boObject runtimeActivity,ArrayList toRet) throws boRuntimeException
    {
        boObject variable = null;
        boObject valueObject = null;
        boolean found = false;
        if(!"xwfActivityReceive".equals(runtimeActivity.getName())) //$NON-NLS-1$
        {            
            bridgeHandler bridge = runtimeActivity.getBridge("variables"); //$NON-NLS-1$
            bridge.beforeFirst();                
            while ( bridge.next() && !found) 
            {               
                variable = bridge.getObject();    
                valueObject = getValueObject(engine,variable);                
                if(variable != null)
                {
                    long showMode = variable.getAttribute("showMode").getValueLong(); //$NON-NLS-1$
                    if(showMode == 0)
                    {
                        renderObjectToolBarActions(variable,valueObject,toRet);   
                        found = true;
                    }                               
                }
            }
        }
//        else
//        {
            if( runtimeActivity.getAttribute("message") != null )
            {
                variable = runtimeActivity.getAttribute("message").getObject();
                if(variable != null)
                {
                    valueObject = engine.getBoManager().getValueBoObject(variable.getAttribute("value").getObject()); //$NON-NLS-1$
                    if(valueObject != null)
                    {
                        renderObjectToolBarActions(variable,valueObject,toRet);   
                    }                
                }
            }
//        }
    }
    private static void renderObjectToolBarActions(boObject variable, boObject valueObject,ArrayList toRet) throws boRuntimeException
    {
        MenuItem svItem = null;
        StringBuffer sb = null;
        if(valueObject != null)
        {
            boDefMethod[] userMethods= valueObject.getToolbarMethods();
            
            for (int i=0; i< userMethods.length ; i++ ) 
            {        
                if (securityRights.hasRightsToMethod(valueObject,valueObject.getName(),userMethods[i].getName(),valueObject.getEboContext().getBoSession().getPerformerBoui())) 
                {                          
                    if(!valueObject.methodIsHidden(userMethods[i].getName()))
                    {                                  
                        svItem = new MenuItem();
                        svItem.setLabel(userMethods[i].getLabel().replaceAll(" ","&nbsp;")); //$NON-NLS-1$ //$NON-NLS-2$
                        svItem.setImgURL(getWkfIcon(variable,userMethods[i]));
                        sb = new StringBuffer();
                        sb.append("setActionCode('").append(XwfKeys.ACTION_OBJECT_METHOD_KEY);
                        sb.append("[").append(userMethods[i].getName()).append("]");
                        sb.append("[").append(valueObject.getBoui()).append("]");
                        sb.append("');boForm.BindValues();wait();");
                        svItem.setOnClickCode(sb.toString());
//                        svItem.setOnClickCode("setActionCode('"+XwfKeys.ACTION_OBJECT_METHOD_KEY+"["+ userMethods[i].getName()+"]');boForm.BindValues();wait();");
                        toRet.add(svItem);
                    }
                }
            }
        }
    }
    private static String getWkfIcon(boObject variable,boDefMethod methodDef)  throws boRuntimeException
    {
        String icon = null;         
        String name = methodDef.getName();
        String listOfMethods = variable.getAttribute("requireMethods").getValueString(); //$NON-NLS-1$
        if(listOfMethods != null && !"".equals(listOfMethods)) //$NON-NLS-1$
        {
            ngtXMLHandler source = new ngtXMLHandler(listOfMethods).getChildNode("requireMethods"); //$NON-NLS-1$
            if(source != null && !"".equals(source)) //$NON-NLS-1$
            {
                ngtXMLHandler[] methods = source.getChildNodes();
                for (int i = 0; i < methods.length; i++) 
                {            
                    if(name.equals(methods[i].getAttribute("name"))) //$NON-NLS-1$
                    {
                        icon = WKF_PATH + WKF_CORE_REQUIRED_ICON;
                    }        
                }
            }
        }         
        if(icon == null)
        {
            icon = WKF_PATH + WKF_OBJECT_SPECIFIC_ICON;
        }
        return icon;
    }
    private static void renderObjectMenuActions(boObject valueObject,ArrayList toRet) throws boRuntimeException
    {
        MenuItem svItem = null;
        boDefMethod[] userMethods= valueObject.getMenuMethods();       
        for (int i=0; i< userMethods.length ; i++ ) 
        {    
            if (securityRights.hasRightsToMethod(valueObject,valueObject.getName(),userMethods[i].getName(),valueObject.getEboContext().getBoSession().getPerformerBoui()))
            {  
                if(!valueObject.methodIsHidden(userMethods[i].getName()))
                {
                    svItem = new MenuItem();
                    svItem.setLabel(userMethods[i].getLabel().replaceAll(" ","&nbsp;")); //$NON-NLS-1$ //$NON-NLS-2$
                    svItem.setOnClickCode("runBeforeMethodExec('" + userMethods[i].getName() + "'); setLastMethodToRun('" + userMethods[i].getName() + "'); boForm.executeMethod('"+userMethods[i].getName()+"');wait();"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    toRet.add(svItem);
                }
            }            
        }        
    }
    private static boolean isValidForRender(XwfController controller) throws boRuntimeException
    {        
        boolean result = false;
        String activityName = controller.getRuntimeActivity().getName();
        if(controller.getInformation(XwfKeys.IN_FINDING_TEMPLATE_KEY) != null)
        {            
            result = !((Boolean)controller.getInformation(XwfKeys.IN_FINDING_TEMPLATE_KEY)).booleanValue();
        }        
        else if("xwfActivityReceive".equals(activityName) || //$NON-NLS-1$
                "xwfActivitySend".equals(activityName) || //$NON-NLS-1$
                "xwfWaitResponse".equals(activityName)) //$NON-NLS-1$
        {
             result = true;         
        }
        else if(!"close".equals(controller.getProgramState()) &&  //$NON-NLS-1$
                controller.getRuntimeActivity() != null)
        {
            result = true;
        }        
        return result;
    }
    private static boObject getValueObject(EngineGate engine, boObject variable) throws boRuntimeException
    {
        boObject valueObject = null;
        boObject varValue = variable.getAttribute("value").getObject();   //$NON-NLS-1$
        long variableType = -1;
        long maxoccurs =  -1;
        if(varValue != null)
        {
            variableType = varValue.getAttribute("type").getValueLong(); //$NON-NLS-1$
            if(variableType == 0)
            {
                maxoccurs =  varValue.getAttribute("maxoccurs").getValueLong(); //$NON-NLS-1$
                if(maxoccurs == 1)
                {
                   valueObject = engine.getBoManager().getBoObject(varValue);
                }
            }
        }
        return valueObject;
    }
    private static void addReplySubMenuItem(MenuItem mi, String prefix)
    {
        MenuItem replyItem = null;
//        replyItem = new MenuItem();
//        replyItem.setId("replyConversation");
//        replyItem.setImgURL(WKF_PATH + WKF_OBJECT_NORMAL_ICON);            
//        replyItem.setVisible(true);
//        replyItem.setDisabled(false);
//        replyItem.setOnClickCode("setActionCode('" + prefix + "Conversation');setStateActivity('');boForm.BindValues();");
//        replyItem.setLabel("Mensagem <u>C</u>onversa");
//        replyItem.setAccessKey('C');     
//        replyItem.setTitle("Mensagem Conversa");
//        mi.addSubMenuItem(replyItem);
        
//        replyItem = new MenuItem();
//        replyItem.setId("replyPhone");
//        replyItem.setImgURL(WKF_PATH + WKF_OBJECT_NORMAL_ICON);            
//        replyItem.setVisible(true);
//        replyItem.setDisabled(false);
//        replyItem.setOnClickCode("setActionCode('" + prefix + "Phone');setStateActivity('');boForm.BindValues();");
//        replyItem.setLabel("Mensagem <u>T</u>elefone");
//        replyItem.setAccessKey('T');     
//        replyItem.setTitle("Mensagem Telefone");
//        mi.addSubMenuItem(replyItem);
        
        replyItem = new MenuItem();
        replyItem.setId("replyFax");
        replyItem.setImgURL(WKF_PATH + WKF_OBJECT_NORMAL_ICON);            
        replyItem.setVisible(true);
        replyItem.setDisabled(false);
        replyItem.setOnClickCode("setActionCode('" + prefix + "Fax');setStateActivity('');boForm.BindValues();wait();");
        replyItem.setLabel("Mensagem <u>F</u>ax");
        replyItem.setAccessKey('F');     
        replyItem.setTitle("Mensagem Fax");
        mi.addSubMenuItem(replyItem);
        
        replyItem = new MenuItem();
        replyItem.setId("replyLetter");
        replyItem.setImgURL(WKF_PATH + WKF_OBJECT_NORMAL_ICON);            
        replyItem.setVisible(true);
        replyItem.setDisabled(false);
        replyItem.setOnClickCode("setActionCode('" + prefix + "Letter');setStateActivity('');boForm.BindValues();wait();");
        replyItem.setLabel("Mensagem C<u>a</u>rta");
        replyItem.setAccessKey('a');     
        replyItem.setTitle("Mensagem Carta");
        mi.addSubMenuItem(replyItem);
        
        replyItem = new MenuItem();
        replyItem.setId("replyMail"); //$NON-NLS-1$
        replyItem.setImgURL(WKF_PATH + WKF_OBJECT_NORMAL_ICON);            
        replyItem.setVisible(true);
        replyItem.setDisabled(false);
        replyItem.setOnClickCode("setActionCode('" + prefix + "Mail');setStateActivity('');boForm.BindValues();wait();"); //$NON-NLS-1$ //$NON-NLS-2$
        replyItem.setLabel(Messages.getString("xwfMenuViewer.284")); //$NON-NLS-1$
        replyItem.setAccessKey('E');     
        replyItem.setTitle(Messages.getString("xwfMenuViewer.285")); //$NON-NLS-1$
        mi.addSubMenuItem(replyItem);
        
        replyItem = new MenuItem();
        replyItem.setId("replySgis");
        replyItem.setImgURL(WKF_PATH + WKF_OBJECT_NORMAL_ICON);            
        replyItem.setVisible(true);
        replyItem.setDisabled(false);
        replyItem.setOnClickCode("setActionCode('" + prefix + "Sgis');setStateActivity('');boForm.BindValues();wait();");
        replyItem.setLabel("Mensagem <u>S</u>gis");
        replyItem.setAccessKey('S');     
        replyItem.setTitle("Mensagem Sgis");
        mi.addSubMenuItem(replyItem);
    }
    public static void renderDefActivity(XwfController controller, long defActivityBoui ,PageContext page) throws java.io.IOException,boRuntimeException
    {
        PrintWriter out = new PrintWriter(page.getOut());
        out.print("\n<TR  height='35px'  >");                     //$NON-NLS-1$
        out.print("\n<TD valign=top align=left >\n"); //$NON-NLS-1$
        
        ArrayList toRet = new ArrayList();
        MenuItem svItem = new MenuItem();
        svItem.setId(XwfKeys.ACTION_LAUNCH_KEY);            
        svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
        svItem.setLabel(Messages.getString("xwfMenuViewer.312"));                 //$NON-NLS-1$
        svItem.setOnClickCode("parent.setActionCode('"+XwfKeys.ACTION_LAUNCH_KEY+"["+defActivityBoui+"]');parent.boForm.BindValues();"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        svItem.setVisible(true);
        svItem.setDisabled(false);
        svItem.setAccessKey('L');    
        svItem.setTitle(Messages.getString("xwfMenuViewer.316")); //$NON-NLS-1$
        toRet.add(svItem);
        PageController pageController = new PageController();
        Menu m = new Menu(toRet);
        m.writeHTML(out,controller.getDocHTML(),null,pageController);       
        
        out.print("\n</TD>"); //$NON-NLS-1$
        out.print("\n</TR>");           //$NON-NLS-1$
    }
    public static void renderReassignActivity(XwfController controller, long reassignBoui ,PageContext page) throws java.io.IOException,boRuntimeException
    {
        PrintWriter out = new PrintWriter(page.getOut());
        out.print("\n<TR  height='35px'  >");                     //$NON-NLS-1$
        out.print("\n<TD valign=top align=left >\n"); //$NON-NLS-1$
        
        ArrayList toRet = new ArrayList();
        MenuItem svItem = new MenuItem();
        svItem.setId(XwfKeys.ACTION_REASSIGN_KEY);            
        svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
        svItem.setLabel(Messages.getString("xwfMenuViewer.321"));                 //$NON-NLS-1$
        svItem.setOnClickCode("parent.setActionCode('"+XwfKeys.ACTION_REASSIGN_KEY+"["+reassignBoui+"]');parent.boForm.BindValues();"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        svItem.setVisible(true);
        svItem.setDisabled(false);
        svItem.setAccessKey('L');    
        svItem.setTitle(Messages.getString("xwfMenuViewer.325")); //$NON-NLS-1$
        toRet.add(svItem);
        PageController pageController = new PageController();
        Menu m = new Menu(toRet);
        m.writeHTML(out,controller.getDocHTML(),null,pageController);       
        
        out.print("\n</TD>"); //$NON-NLS-1$
        out.print("\n</TR>");           //$NON-NLS-1$
    } 
    public static void renderTransferProcess(XwfController controller, long transferBoui ,PageContext page) throws java.io.IOException,boRuntimeException
    {
        PrintWriter out = new PrintWriter(page.getOut());
        out.print("\n<TR  height='35px'  >");                     //$NON-NLS-1$
        out.print("\n<TD valign=top align=left >\n"); //$NON-NLS-1$
        
        ArrayList toRet = new ArrayList();
        MenuItem svItem = new MenuItem();
        svItem.setId(XwfKeys.ACTION_TRANSFER_PROGRAM_KEY);            
        svItem.setImgURL(WKF_PATH + WKF_CORE_NORMAL_ICON);
        svItem.setLabel(Messages.getString("xwfMenuViewer.330"));                 //$NON-NLS-1$
        svItem.setOnClickCode("parent.setActionCode('"+XwfKeys.ACTION_TRANSFER_PROGRAM_KEY+"["+transferBoui+"]');parent.boForm.BindValues();"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        svItem.setVisible(true);
        svItem.setDisabled(false);
        svItem.setAccessKey('L');    
        svItem.setTitle(Messages.getString("xwfMenuViewer.334")); //$NON-NLS-1$
        toRet.add(svItem);
        PageController pageController = new PageController();
        Menu m = new Menu(toRet);
        m.writeHTML(out,controller.getDocHTML(),null,pageController);       
        
        out.print("\n</TD>"); //$NON-NLS-1$
        out.print("\n</TR>");           //$NON-NLS-1$
    }
    private static boolean haveSecurityRights(boObject object)
    {
        boolean result = true;        
        try
        {
            result = securityOPL.canWrite(object);
        }
        catch (boRuntimeException e)
        {
            //ignore for now
        }
        return result;
    }
}