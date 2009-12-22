/*Enconding=UTF-8*/
package netgest.xwf.presentation;

import java.io.IOException;

import java.lang.StringBuffer;

import java.net.URLEncoder;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Hashtable;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import netgest.bo.controller.Controller;
import netgest.bo.controller.ControllerFactory;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.controller.xwf.XwfValidator;
import netgest.bo.def.boDefAttribute;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_renderFields;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.presentation.render.elements.AdHocElement;
import netgest.bo.presentation.render.elements.Element;
import netgest.bo.presentation.render.elements.Preview;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectStateHandler;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.utils.ClassUtils;

import netgest.xwf.common.xwfBoManager;
import netgest.xwf.common.xwfHelper;
import netgest.xwf.core.xwfECMAevaluator;

import netgest.bo.system.Logger;

import xeo.client.business.helper.RegistryHelper;

/**
 * <p>Title: xwfActivityViewer </p>
 * <p>Description: Classe responsável por apresentar as variaveis ou decisões</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public class xwfActivityViewer 
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.xwf.presentation.xwfActivityViewer"); //$NON-NLS-1$
    
    public static void renderActivity(XwfController controller,PageContext pageContext,int idx) throws IOException, boRuntimeException
    {
        JspWriter out = pageContext.getOut();    
        docHTML doc = controller.getDocHTML();
        StringBuffer toPrint = new StringBuffer();
        boObject activity = controller.getRuntimeActivity();        
//        int renderMode = getRenderMode(activity.getBridge("variables"));
//        renderTableStart(toPrint,renderMode);
        renderTableStart(toPrint);
        if("xwfActivity".equals(activity.getName())) //$NON-NLS-1$
        {
            renderActivity(toPrint,controller,doc,idx);
        }
        else if("xwfActivityFill".equals(activity.getName())) //$NON-NLS-1$
        {
            renderActivityFill(toPrint,controller,doc,idx,false);
        }        
        else if("xwfActivityChoice".equals(activity.getName())|| //$NON-NLS-1$
                "xwfActivityPoll".equals(activity.getName())) //$NON-NLS-1$
        {
            renderActivity(toPrint,controller,doc,idx);            
            renderActivityChoice(toPrint,controller);
        }
        else if("xwfActivityDecision".equals(activity.getName())) //$NON-NLS-1$
        {            
            renderActivity(toPrint,controller,doc,idx);       
            renderActivityDecision(toPrint,controller);
        }       
        else
        {
            renderActivity(toPrint,controller,doc,idx);
        }
        renderTableEnd(toPrint);
        out.print(toPrint);
    }  
    public static void renderVariables(XwfController controller,PageContext pageContext,int idx) throws IOException, boRuntimeException
    {
        JspWriter out = pageContext.getOut();
        docHTML doc = controller.getDocHTML();
        StringBuffer toPrint = new StringBuffer();
        Hashtable ht_variables = new Hashtable(); 
        renderTableStart(toPrint);
        
        // Tempos
        renderSeparatorLabel(toPrint,Messages.getString("xwfActivityViewer.6")); //$NON-NLS-1$
        toPrint.append("<TR><TD colspan=4>"); //$NON-NLS-1$
        toPrint.append(Messages.getString("xwfActivityViewer.8")); //$NON-NLS-1$
        Date date = controller.getRuntimeProgram().getAttribute("beginDate").getValueDate(); //$NON-NLS-1$
        if(date != null)
        {
            SimpleDateFormat df = new SimpleDateFormat("EEEEE, dd 'de' MMMMM 'de' yyyy HH:mm:ss");             //$NON-NLS-1$
            toPrint.append(df.format(date));
        }                                    
        toPrint.append("</TD><TR>"); //$NON-NLS-1$
        
        toPrint.append("<TR><TD colspan=4>"); //$NON-NLS-1$
        toPrint.append(Messages.getString("xwfActivityViewer.5")); //$NON-NLS-1$
        date = controller.getRuntimeProgram().getAttribute("endDate").getValueDate(); //$NON-NLS-1$
        if(date != null)
        {
            SimpleDateFormat df = new SimpleDateFormat("EEEEE, dd 'de' MMMMM 'de' yyyy HH:mm:ss");             //$NON-NLS-1$
            toPrint.append(df.format(date));
        }                                    
        toPrint.append("</TD><TR>");       //$NON-NLS-1$
        
        toPrint.append("<TR><TD colspan=4>"); //$NON-NLS-1$
        toPrint.append(Messages.getString("xwfActivityViewer.18")); //$NON-NLS-1$
        date = controller.getRuntimeProgram().getAttribute("deadLineDate").getValueDate(); //$NON-NLS-1$
        if(date != null)
        {
            SimpleDateFormat df = new SimpleDateFormat("EEEEE, dd 'de' MMMMM 'de' yyyy HH:mm:ss");             //$NON-NLS-1$
            toPrint.append(df.format(date));
        }                                    
        toPrint.append("</TD><TR>");          //$NON-NLS-1$
        toPrint.append("<TR><TD colspan=4>&nbsp;</TD><TR>"); //$NON-NLS-1$
        
        // participants
        renderParticipants(controller,toPrint);
        
        // variables
        renderSeparatorLabel(toPrint,Messages.getString("xwfActivityViewer.4")); //$NON-NLS-1$
        toPrint.append("<TR><TD colspan=4>&nbsp;</TD><TR>"); //$NON-NLS-1$
        boObject variable = null;
        boObject activity = null;
        String var_name = null; 
        boObject program = controller.getRuntimeProgram();    
        bridgeHandler bridge = program.getBridge("variables"); //$NON-NLS-1$
        bridge.beforeFirst();
        while(bridge.next())
        {            
            variable = bridge.getObject();
            var_name = variable.getAttribute("name").getValueString(); //$NON-NLS-1$
            Object value = null;
            if(variable.getAttribute("value").getObject() != null) //$NON-NLS-1$
            {
                value = controller.getEngine().getBoManager().getValueObject(
                                        variable.getAttribute("value").getObject()); //$NON-NLS-1$
            }
            if(ht_variables.get(var_name) != null)  //ver se existe na tabela uma entrada com o nome da variável
            {   //se sim
                String svalue = ht_variables.get(var_name).toString();
                if(value != null && svalue != null && !value.toString().equals(svalue))
                {//ver se os valores são diferentes
                    if(ht_variables.get(value.toString()) != null )
                    {
                        if(!ht_variables.get(value.toString()).toString().equals(var_name))
                        {//se os valores são diferentes ver se existe uma entrada para o boui dado
                            renderObjects(toPrint,activity,variable,doc,idx,true,true);
                            ht_variables.put(value.toString(), var_name);
                        }
                    }
                    else
                    {
                        renderObjects(toPrint,activity,variable,doc,idx,true,true);
                        ht_variables.put(value.toString(), var_name);   
                    }
                }
            }
            else
            {   //senão
                renderObjects(toPrint,activity,variable,doc,idx,true,true);
                if(value != null)
                {
                    ht_variables.put(var_name, value);
                }
            }
        }        
        
        // justifications
        renderAllJustifications(controller,toPrint);
        
        renderTableEnd(toPrint);
        out.print(toPrint);
    }      
    private static void renderParticipants(XwfController controller,StringBuffer toPrint)  throws boRuntimeException
    {
        renderSeparatorLabel(toPrint,Messages.getString("xwfActivityViewer.29")); //$NON-NLS-1$
        toPrint.append("<TR><TD colspan=4>&nbsp;</TD><TR>"); //$NON-NLS-1$
        Hashtable ht_prticipants = new Hashtable();
        boObject participant = null;
        boObject value = null;
        boObject valueObject = null;
        boObject program = controller.getRuntimeProgram();    
        bridgeHandler bridge = program.getBridge("participants"); //$NON-NLS-1$
        bridge.beforeFirst();
        boolean toRender = false;
        while(bridge.next())
        {   
            participant = bridge.getObject();
            value = participant.getAttribute("value").getObject(); //$NON-NLS-1$
            valueObject = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();

            String par_name = participant.getAttribute("name").getValueString(); //$NON-NLS-1$
            if(ht_prticipants.get(par_name) != null)  //ver se existe na tabela uma entrada com o nome da variável
            {   //se sim
                String svalue = ht_prticipants.get(par_name) != null ? ht_prticipants.get(par_name).toString():null;
                if(valueObject != null && svalue != null && !valueObject.toString().equals(svalue))
                {//ver se os valores são diferentes
                    if(ht_prticipants.get(valueObject.toString()) != null )
                    {
                        if(!ht_prticipants.get(valueObject.toString()).toString().equals(par_name))
                        {//se os valores são diferentes ver se existe uma entrada para o boui dado
                            toRender = true;
                            ht_prticipants.put(valueObject.toString(), par_name);
                        }
                    }
                    else
                    {
                        toRender = true;
                        ht_prticipants.put(valueObject.toString(), par_name);
                    }
                }
            }
            else
            {   //senão
                toRender = true;
                if(valueObject != null)
                {
                    ht_prticipants.put(par_name, valueObject);
                }
            }
            if(toRender)
            {
                
                if(valueObject != null)
                {
                    toPrint.append("<TR><TD colspan=4>"); //$NON-NLS-1$
                    toPrint.append(participant.getAttribute("label").getValueString()).append(", "); //$NON-NLS-1$ //$NON-NLS-2$
                    toPrint.append(valueObject.getCARDID());
                    toPrint.append("</TD><TR>"); //$NON-NLS-1$
                }
                toRender = false;
            }
        }                
        toPrint.append("<TR><TD colspan=4>&nbsp;</TD><TR>"); //$NON-NLS-1$
    }
    private static void renderAllJustifications(XwfController controller,StringBuffer toPrint)  throws boRuntimeException
    {
        boObject activity = null;
        StringBuffer boql = new StringBuffer("SELECT xwfActivityChoice WHERE program="); //$NON-NLS-1$
        boql.append(controller.getRuntimeProgramBoui());
       // boql.append(" AND ").append(xwfHelper.PERFORMER_CLAUSE);
        boql.append(" ORDER BY SYS_DTCREATE"); //$NON-NLS-1$
        boObjectList list = controller.getEngine().getBoManager().listObject(boql.toString(), false);
        list.beforeFirst();
        while(list.next())
        {
            toPrint.append("<TR><TD colspan=4>&nbsp;</TD><TR>"); //$NON-NLS-1$
            activity = list.getObject();
            renderChoiceJustification(activity,toPrint);
        }        
        boql.replace(7,24,"xwfActivityDecision"); //$NON-NLS-1$
        list = controller.getEngine().getBoManager().listObject(boql.toString());
        list.beforeFirst();
        while(list.next())
        {
            toPrint.append("<TR><TD colspan=4>&nbsp;</TD><TR>"); //$NON-NLS-1$
            activity = list.getObject();
            renderDecisionJustification(activity,toPrint);
        }        
    }
    private static void renderChoiceJustification(boObject activity,StringBuffer toPrint) throws boRuntimeException
    {
        boObject performer = activity.getAttribute("performer").getObject(); //$NON-NLS-1$
        if(performer != null)
        {
            StringBuffer header = new StringBuffer(Messages.getString("xwfActivityViewer.45")); //$NON-NLS-1$
            header.append("<a style=\"COLOR:#0000ff\">").append(activity.getAttribute("question").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            header.append(Messages.getString("xwfActivityViewer.3")); //$NON-NLS-1$
            header.append(performer.getCARDID());        
            Date date = activity.getAttribute("endDate").getValueDate(); //$NON-NLS-1$
            if(date != null)
            {
                header.append(Messages.getString("xwfActivityViewer.2")); //$NON-NLS-1$
                SimpleDateFormat df = new SimpleDateFormat("EEEEE, dd 'de' MMMMM 'de' yyyy HH:mm:ss");                 //$NON-NLS-1$
                header.append(df.format(date));
                header.append(". "); //$NON-NLS-1$
            }                         
            header.append(Messages.getString("xwfActivityViewer.53")); //$NON-NLS-1$
            boolean found = false;
            boObject objAux = null;
            bridgeHandler bridge = activity.getBridge("options"); //$NON-NLS-1$
            bridge.beforeFirst();
            while(bridge.next() && !found)
            {
                objAux = bridge.getObject();
                String answer = activity.getAttribute("answer").getValueString(); //$NON-NLS-1$
                if(answer.equals(objAux.getAttribute("codeOption").getValueString())) //$NON-NLS-1$
                {
                        header.append("<a style=\"COLOR:#0000ff\">").append(objAux.getAttribute("labelOption").getValueString()).append("</a>."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        found = true;
                }  
            }                 
            renderSeparatorLabel(toPrint,header.toString());
            toPrint.append("<TR><TD colspan=4>&nbsp;</TD><TR>"); //$NON-NLS-1$
            toPrint.append("<TR><TD colspan=4 'padding-left:30px;padding-right:30px;'>"); //$NON-NLS-1$
            toPrint.append(activity.getAttribute("justification").getValueString()); //$NON-NLS-1$
            toPrint.append("</TD><TR>"); //$NON-NLS-1$
        }
    }
    private static void renderDecisionJustification(boObject activity,StringBuffer toPrint) throws boRuntimeException
    {
       boObject performer = activity.getAttribute("performer").getObject(); //$NON-NLS-1$
        if(performer != null)
        {
            StringBuffer header = new StringBuffer(Messages.getString("xwfActivityViewer.65")); //$NON-NLS-1$
            header.append("<a style=\"COLOR:#0000ff\">").append(activity.getAttribute("question").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            header.append(Messages.getString("xwfActivityViewer.68")); //$NON-NLS-1$
            header.append(performer.getCARDID());        
            Date date = activity.getAttribute("endDate").getValueDate(); //$NON-NLS-1$
            if(date != null)
            {
                header.append("  em "); //$NON-NLS-1$
                SimpleDateFormat df = new SimpleDateFormat("EEEEE, dd 'de' MMMMM 'de' yyyy HH:mm:ss");                 //$NON-NLS-1$
                header.append(df.format(date));
                header.append(". "); //$NON-NLS-1$
            }          
            header.append(Messages.getString("xwfActivityViewer.73")); //$NON-NLS-1$
            String answer = activity.getAttribute("answer").getValueString(); //$NON-NLS-1$
            if(answer != null && !"".equals(answer)) //$NON-NLS-1$
            {
                String label = null;
                if(answer.equals(activity.getAttribute("yes").getObject().getAttribute("codeOption").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    label = activity.getAttribute("yes").getObject().getAttribute("labelOption").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
                }
                else
                {
                    label = activity.getAttribute("no").getObject().getAttribute("labelOption").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
                }    
                header.append("<a style=\"COLOR:#0000ff\">").append(label).append("</a>.");               //$NON-NLS-1$ //$NON-NLS-2$
            }             
            renderSeparatorLabel(toPrint,header.toString());
            toPrint.append("<TR><TD colspan=4>&nbsp;</TD><TR>"); //$NON-NLS-1$
            toPrint.append("<TR><TD colspan=4 'padding-left:30px;padding-right:30px;'>"); //$NON-NLS-1$
            toPrint.append(activity.getAttribute("justification").getValueString()); //$NON-NLS-1$
            toPrint.append("</TD><TR>"); //$NON-NLS-1$
        }
    }
  /*  
    private static int getRenderMode(bridgeHandler bridge)  throws boRuntimeException
    {
        int renderMode = 0;
        boObject variable = null;
        boObject varvalue = null;
        long variableType = -1;
        long showMode = -1;
        long maxoccurs =  -1;
        int count = 0;
        bridge.beforeFirst();                
        while ( bridge.next()) 
        {                   
            variable = bridge.getObject();
            varvalue = variable.getAttribute("value").getObject();
            showMode = variable.getAttribute("showMode").getValueLong();
            variableType = varvalue.getAttribute("type").getValueLong();            
            maxoccurs =  varvalue.getAttribute("maxoccurs").getValueLong();
            if (variableType == 0 && showMode == 0 && maxoccurs ==0)
            {
                renderMode = 1;                              
            }            
            count ++;
        }
        if(count > 1)
        {
            renderMode = 0;
        }                        
        return renderMode;
    }*/
    private static boolean isAnyAttributeToRender(bridgeHandler bridge)  throws boRuntimeException
    {
        boolean result = false;
        boObject variable = null;
        boObject varvalue = null;
        long showMode = -1;        
        bridge.beforeFirst();                
        while ( bridge.next() && !result) 
        {                   
            variable = bridge.getObject();
            varvalue = variable.getAttribute("value").getObject(); //$NON-NLS-1$
            showMode = variable.getAttribute("showMode").getValueLong(); //$NON-NLS-1$
            if (showMode != 0)
            {
                result = true;                            
            }                                
        }                        
        return result;
    }    
    private static void renderActivity(StringBuffer toPrint,XwfController controller, docHTML doc, int idx)  throws boRuntimeException,IOException
    {
        bridgeHandler bridge = controller.getRuntimeActivity().getBridge("variables"); //$NON-NLS-1$
//        if(!bridge.isEmpty())
        if(isAnyAttributeToRender(bridge))
        {
            renderSeparatorLabel(toPrint,Messages.getString("xwfActivityViewer.91")); //$NON-NLS-1$
        }
        renderActivityFill(toPrint,controller,doc,idx,true);
        if(!bridge.isEmpty())
        {
            renderTableSpace(toPrint);
        }        
    }
    private static void renderActivityDecision(StringBuffer toPrint,XwfController controller)  throws boRuntimeException,IOException
    {
        boObject activity = controller.getRuntimeActivity();                
                        
        boObjectStateHandler pstate = activity.getStateAttribute( "runningState" ); //$NON-NLS-1$
        boolean close = false;        
        renderJustification(controller.getDocHTML() ,toPrint,activity,close);
        toPrint.append("<TR><TD colspan=4>&nbsp;</TD><TR>"); //$NON-NLS-1$
        renderSeparatorLabel(toPrint,activity.getAttribute("question").getValueString()); //$NON-NLS-1$
        toPrint.append("<tr>");  //$NON-NLS-1$
        if (pstate != null && "close".equals(pstate.getValueString())) //$NON-NLS-1$
        {
            close = true;
            String answer = activity.getAttribute("answer").getValueString(); //$NON-NLS-1$
            if(answer != null && !"".equals(answer)) //$NON-NLS-1$
            {
                String label = null;
                if(answer.equals(activity.getAttribute("yes").getObject().getAttribute("codeOption").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    label = activity.getAttribute("yes").getObject().getAttribute("labelOption").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
                }
                else
                {
                    label = activity.getAttribute("no").getObject().getAttribute("labelOption").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
                }                
                toPrint.append("<td colspan=4>A decisão foi, <b>" + label + "</b>.</td>");               //$NON-NLS-1$ //$NON-NLS-2$
            } 
            else
            {
                toPrint.append("<td colspan=4>Foi cancelada.</td>");                 //$NON-NLS-1$
            }
        }
        else
        {
            toPrint.append("<td colspan='4'><button onclick=\"setActivityValue('"); //$NON-NLS-1$
            toPrint.append(activity.getAttribute("yes").getObject().getAttribute("codeOption").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            toPrint.append("');savePressed(true);wait();\">"); //$NON-NLS-1$
            toPrint.append(activity.getAttribute("yes").getObject().getAttribute("labelOption").getValueString());         //$NON-NLS-1$ //$NON-NLS-2$
            toPrint.append("</button>&nbsp;&nbsp;"); //$NON-NLS-1$

            toPrint.append("<button onclick=\"setActivityValue('"); //$NON-NLS-1$
            toPrint.append(activity.getAttribute("no").getObject().getAttribute("codeOption").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            toPrint.append("');savePressed(true);wait();\">"); //$NON-NLS-1$
            toPrint.append(activity.getAttribute("no").getObject().getAttribute("labelOption").getValueString());         //$NON-NLS-1$ //$NON-NLS-2$
            toPrint.append("</button></td>"); //$NON-NLS-1$
        }        
        toPrint.append("</tr>"); //$NON-NLS-1$

    }
    private static void renderActivityChoice(StringBuffer toPrint,XwfController controller)  throws boRuntimeException,IOException
    {
        boObject activity = controller.getRuntimeActivity();
        renderSeparatorLabel(toPrint,activity.getAttribute("question").getValueString());         //$NON-NLS-1$
        boObjectStateHandler pstate = activity.getStateAttribute( "runningState" ); //$NON-NLS-1$
        boObject objAux = null;
        boolean close = false;
        bridgeHandler bridge = activity.getBridge("options"); //$NON-NLS-1$
        bridge.beforeFirst();                
        toPrint.append("<tr><td colspan=4 style='padding-left:30px' >");                     //$NON-NLS-1$
        
//        if (pstate != null && "close".equals(pstate.getValueString()))
//        {
//            String answer = activity.getAttribute("answer").getValueString();
//            
//            if(answer != null && !"".equals(answer))
//            {
//                String label = null;
//                boolean found = false;
//                while(bridge.next() && !found)
//                {
//                    objAux = bridge.getObject();
//                    if(answer.equals(objAux.getAttribute("codeOption").getValueString()))
//                    {
//                        label = objAux.getAttribute("labelOption").getValueString();
//                        found = true;
//                    }
//                }                
//                toPrint.append("A opção escolhida foi, <b>" + label + "</b>.");              
//            }       
//            else
//            {
//                toPrint.append("Foi cancelada.");                
//            }            
//        }
//        else
//        {
            toPrint.append("<TABLE cellSpacing=0 cellPadding=0 width=\"300px\" align=left border=0>"); //$NON-NLS-1$
            toPrint.append("<TR><TD colspan=2>&nbsp;</TD><TR>"); //$NON-NLS-1$
            while(bridge.next())
            {
                objAux = bridge.getObject();
                boolean toShow = true;
                String avWhen = objAux.getAttribute("availableWhen").getValueString(); //$NON-NLS-1$
                if(avWhen!=null && avWhen.length()>0){
                    xwfBoManager xm = controller.getEngine().getBoManager();
                    xwfECMAevaluator ev = new xwfECMAevaluator();
                    Object val = ev.eval(xm, avWhen);
                    if(val != null && val instanceof Boolean){
                      if(((Boolean)val).booleanValue() == false)
                        toShow = false;
                    }
                }
                
                if(toShow){
                  toPrint.append("<TR >"); //$NON-NLS-1$
                  toPrint.append("<TD style='border-bottom:1px solid #CCCCCC;'>"); //$NON-NLS-1$
                  toPrint.append("&nbsp;");                 //$NON-NLS-1$
                  toPrint.append(objAux.getAttribute("labelOption").getValueString()); //$NON-NLS-1$
                  toPrint.append("</TD>"); //$NON-NLS-1$
                  toPrint.append("<TD style='border-bottom:1px solid #CCCCCC' >"); //$NON-NLS-1$
                  toPrint.append("<INPUT id=").append(objAux.getAttribute("codeOption").getValueString());        //$NON-NLS-1$ //$NON-NLS-2$
                  if (pstate != null && "close".equals(pstate.getValueString())) //$NON-NLS-1$
                  {
                      close = true;
                      String answer = activity.getAttribute("answer").getValueString(); //$NON-NLS-1$
                      if(answer.equals(objAux.getAttribute("codeOption").getValueString())) //$NON-NLS-1$
                      {
                          toPrint.append(" checked "); //$NON-NLS-1$
                      }  
                      toPrint.append(" disabled "); //$NON-NLS-1$
                  }
                  toPrint.append(" type=radio name=xwfChoice style='border:0;'>");                 //$NON-NLS-1$
                  toPrint.append("</TD>"); //$NON-NLS-1$
                  toPrint.append("</TR>"); //$NON-NLS-1$
                }
            }     
            toPrint.append("</TABLE>"); //$NON-NLS-1$
//        }                 
        toPrint.append("</tr>"); //$NON-NLS-1$
        
        toPrint.append("<TR><TD>&nbsp;</TD><TR>"); //$NON-NLS-1$
        
        renderJustification(controller.getDocHTML() ,toPrint,activity,close);

        if (!close)
        {            
            toPrint.append("<TR><TD>&nbsp;</TD><TR>");                //$NON-NLS-1$
            toPrint.append("<TR>"); //$NON-NLS-1$
            toPrint.append("<TD align=left colspan=4 style='padding-left:30px;padding-right:30px;border-top:2 groove'> "); //$NON-NLS-1$
            toPrint.append("<button onclick=\"setActivityChoiceValue();\">").append(Messages.getString("xwfActivityViewer.1")).append("</button>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            toPrint.append("</TD>");     //$NON-NLS-1$
            toPrint.append("</TR>"); //$NON-NLS-1$
        }        
        
    } 
    /*
    private static void renderActivityChoice(StringBuffer toPrint,XwfController controller)  throws boRuntimeException,IOException
    {
        boObject activity = controller.getRuntimeActivity();
        renderSeparatorLabel(toPrint,activity.getAttribute("question").getValueString());        
        boObjectStateHandler pstate = activity.getStateAttribute( "runningState" );
        boObject objAux = null;
        bridgeHandler bridge = activity.getBridge("options");
        bridge.beforeFirst();                
        toPrint.append("<tr><td colspan=4>");
        if (pstate != null && "close".equals(pstate.getValueString()))
        {
            String answer = activity.getAttribute("answer").getValueString();
            
            if(answer != null && !"".equals(answer))
            {
                String label = null;
                boolean found = false;
                while(bridge.next() && !found)
                {
                    objAux = bridge.getObject();
                    if(answer.equals(objAux.getAttribute("codeOption").getValueString()))
                    {
                        label = objAux.getAttribute("labelOption").getValueString();
                        found = true;
                    }
                }                
                toPrint.append("A opção escolhida foi, <b>" + label + "</b>.");              
            }       
            else
            {
                toPrint.append("Foi cancelada.");                
            }            
        }
        else
        {
            while(bridge.next())
            {
                objAux = bridge.getObject();
                toPrint.append("&nbsp;");
                
                toPrint.append("<button onclick=\"setActivityValue('");
                toPrint.append(objAux.getAttribute("codeOption").getValueString());
                toPrint.append("');savePressed(true);\">");
                toPrint.append(objAux.getAttribute("labelOption").getValueString());        
                toPrint.append("</button>");            
            }            
            
        }         
        toPrint.append("</tr>");    
    } */
    private static void renderActivityFill(StringBuffer toPrint,XwfController controller, docHTML doc, int idx,boolean forceDisabled)  throws boRuntimeException,IOException    
    {                    
        boObject variable = null;
        boObject activity = controller.getRuntimeActivity();
        bridgeHandler bridge = activity.getBridge("variables");         //$NON-NLS-1$
        bridge.beforeFirst();                
        while ( bridge.next() ) 
        {               
            variable = bridge.getObject();
            renderObjects(toPrint,activity,variable,doc,idx,forceDisabled,false);
        }        
    }   
    private static void renderObjects(StringBuffer toPrint,boObject activity,boObject variable, docHTML doc, int idx,boolean forceDisabled, boolean showAttributes)  throws boRuntimeException,IOException
    {
        XwfController controller =  (XwfController)doc.getController();
        boObject valueObject = null;
        boObject varvalue = variable.getAttribute("value").getObject(); //$NON-NLS-1$
        if(varvalue != null)
        {
            long variableType = varvalue.getAttribute("type").getValueLong();         //$NON-NLS-1$
            long maxoccurs =  -1;
            long showMode = -1;
            if(variableType == 0)
            {
                showMode = variable.getAttribute("showMode").getValueLong(); //$NON-NLS-1$
                maxoccurs =  varvalue.getAttribute("maxoccurs").getValueLong(); //$NON-NLS-1$
                if(maxoccurs == 1 && showMode == 1)
                {                              
                    valueObject = varvalue.getAttribute("valueObject").getObject();                 //$NON-NLS-1$
                    if(valueObject != null)
                    {
                        if(!"xwfSerialObject".equals(valueObject.getName())) //$NON-NLS-1$
                        {
//                            toPrint.append("<tr>");     
                            renderCommon(toPrint,activity,variable,varvalue,doc,true,forceDisabled);                    
//                            toPrint.append("</tr>");
                        }
                    }
                    else
                    {
//                        toPrint.append("<tr>");     
                        renderCommon(toPrint,activity,variable,varvalue,doc,true,forceDisabled);                    
//                        toPrint.append("</tr>");                    
                    }
                }
                else if(maxoccurs == 1 && showMode == 0 && showAttributes)
                {
//                    toPrint.append("<tr>");     
                    renderCommon(toPrint,activity,variable,varvalue,doc,true,forceDisabled);                    
//                    toPrint.append("</tr>");                
                }
                else if(maxoccurs > 1)
                {   
                    renderSeparatorLabel(toPrint,variable.getAttribute("label").getValueString()); //$NON-NLS-1$
                    toPrint.append("<tr><td colspan=4 height=190px>"); //$NON-NLS-1$
                    toPrint.append(renderList(doc.getController(),activity,variable,idx,forceDisabled));                    
                    toPrint.append("</td></tr>"); //$NON-NLS-1$
                }                
            }
            else 
            {
//                toPrint.append("<tr>");
                renderCommon(toPrint,activity,variable,varvalue,doc,true,forceDisabled);                       
//                toPrint.append("</tr>");
            }
        }
        else 
        {
            logger.severe("Variable["+variable.getBoui()+"] with VarValue NULL");          //$NON-NLS-1$ //$NON-NLS-2$
        }            
    }
    private static String renderList(Controller controller,boObject activity,boObject variable, int idx, boolean forceDisabled)  throws boRuntimeException 
    {
        boObject varValue = variable.getAttribute("value").getObject(); //$NON-NLS-1$
        String attrName = xwfHelper.getTypeName(varValue);
        boObject boDef =  varValue.getAttribute("object").getObject();                         //$NON-NLS-1$
        String objName = boDef.getAttribute("name").getValueString(); //$NON-NLS-1$
        
        String linkVar = varValue.getAttribute("linkVar").getValueString(); //$NON-NLS-1$
        String linkAtt = varValue.getAttribute("linkAttribute").getValueString(); //$NON-NLS-1$
        if(linkVar != null && linkVar.length()>1)
        {
          xwfBoManager xbm = ((XwfController)controller).getEngine().getBoManager();
          varValue = xbm.getVarObject(linkVar, varValue.getAttribute("unique_sid").getValueString()); //$NON-NLS-1$
          attrName = linkAtt;
        }

        StringBuffer id = new StringBuffer();        
        id.append("inc_").append( varValue.getName() ).append( "__" ).append( varValue.bo_boui ).append("__").append( attrName ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
        String jspName = objName + "_generallist.jsp"; //$NON-NLS-1$
        StringBuffer src = new StringBuffer(jspName.toLowerCase());
        src.append("?"); //$NON-NLS-1$
        src.append("docid=").append(idx); //$NON-NLS-1$
        src.append("&method=list"); //$NON-NLS-1$
        src.append("&parent_attribute=").append(attrName); //$NON-NLS-1$
        src.append("&parent_boui=").append(varValue.getBoui()); //$NON-NLS-1$
        src.append("&object=").append(objName); //$NON-NLS-1$
        src.append("&look_object=").append(objName); //$NON-NLS-1$
        src.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName());         //$NON-NLS-1$ //$NON-NLS-2$
        
        if(isDisabled(activity,variable,forceDisabled))
        {
            src.append("&menu=no");         //$NON-NLS-1$
        }
        return "<div class=extendList><IFRAME id='" + id.toString() + "' src='"+ src.toString() +"' frameBorder='0' width='100%'  scrolling=no height='180px'  ></IFRAME></div>";                                                                                             //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    private static void renderCommon(StringBuffer toPrint,boObject activity ,boObject variable,boObject varvalue, docHTML doc, boolean renderLabel,boolean forceDisabled)  throws boRuntimeException
    {              
        toPrint.append("<tr>"); //$NON-NLS-1$
        XwfController controller =  null;
        if(!XwfKeys.CONTROLLER_NAME_KEY.equals(doc.getController().getName()))
        {
            Controller auxController = ControllerFactory.getControllerByForce(doc,XwfKeys.CONTROLLER_NAME_KEY);
            if(auxController != null)
            {
                controller = (XwfController)auxController;
            }
        }
        else
        {
            controller = (XwfController)doc.getController();
        }
        
        String attrName = xwfHelper.getTypeName(varvalue);
        AttributeHandler attrHandler = varvalue.getAttribute(attrName);        
        Hashtable attributes = new Hashtable();            
        
        boObject boDef = varvalue.getAttribute("object").getObject(); //$NON-NLS-1$
        String valueObject = controller.getEngine().getBoManager().getValueString(varvalue);
        if(valueObject == null)
        {
            valueObject = ""; //$NON-NLS-1$
        }
        StringBuffer nameH = new StringBuffer();
        StringBuffer id = new StringBuffer();
        nameH.append( varvalue.getName() ).append( "__" ).append( varvalue.bo_boui ).append("__").append( attrName ); //$NON-NLS-1$ //$NON-NLS-2$
        id.append("tblLook").append( varvalue.getName() ).append( "__" ).append( varvalue.bo_boui ).append("__").append( attrName ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
        boolean isDisabled = isDisabled(activity,variable,forceDisabled);
        boolean isVisible = true;        
        boolean isRequired = XwfValidator.isRequired(variable);
        boolean isRecommend = false;                   
        
        long type = varvalue.getAttribute("type").getValueLong(); //$NON-NLS-1$
        
        if ( renderLabel && type != boDefAttribute.VALUE_CLOB)
        {
          toPrint.append("<TD>"); //$NON-NLS-1$
          toPrint.append("<label ");                 //$NON-NLS-1$
          if(isDisabled)
          {
              toPrint.append(" disabled "); //$NON-NLS-1$
          }
          if(isRequired)
          {
              toPrint.append(" class=req "); //$NON-NLS-1$
          }
          /*else if( require == 2)
          {
              toPrint.append(" class=req STYLE ='color:green' ");    
          }*/
          toPrint.append(" for='"+ nameH +"'>");        //$NON-NLS-1$ //$NON-NLS-2$
          toPrint.append(variable.getAttribute("label").getValueString()); //$NON-NLS-1$
          toPrint.append("</label></TD>");              //$NON-NLS-1$
          toPrint.append("<td colspan='3'>");            //$NON-NLS-1$
          
        }
                
        if(type == boDefAttribute.VALUE_UNKNOWN)
        {
                                
          writeHTML_lookup(
            new StringBuffer( boDef.getAttribute("name").getValueString()), //$NON-NLS-1$
            toPrint,
            varvalue,
            varvalue.getAttribute(attrName),
            new StringBuffer(valueObject),
            nameH,
            id,
            1,
            doc,
            isDisabled,
            isVisible,            
            isRequired,
            isRecommend,                        
            attributes,
            variable,
            activity
          );
        }                
        else if(type == boDefAttribute.VALUE_BOOLEAN)
        {
            docHTML_renderFields.writeHTML_forBoolean(toPrint,
                                            new StringBuffer(valueObject),
                                            nameH,
                                            id,
                                            1,
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""), //$NON-NLS-1$
                                            isRequired,
                                            isRecommend,
                                            attributes
                                            );                                                                 
        }
        else if(type == boDefAttribute.VALUE_NUMBER)
        {
        
//         String decimals = variable.getAttribute("decimals").getValueString();         
//         if(decimals == null || "".equals(decimals))
//         {
             String decimals = "0"; //$NON-NLS-1$
//         }
//         String minDecimals =  variable.getAttribute("minDecimals").getValueString();
//         if(minDecimals == null || "".equals(minDecimals))
//         {
             String minDecimals = "-99999999"; //$NON-NLS-1$
//         }
//         String  maxNumber = variable.getAttribute("maxNumber").getValueString();
//         if(maxNumber == null || "".equals(maxNumber))
//         {
             String maxNumber = "99999999"; //$NON-NLS-1$
//         }         
//         String  minNumber = variable.getAttribute("minNumber").getValueString();   
//         if(minNumber == null || "".equals(minNumber))
//         {
             String minNumber = "-99999999"; //$NON-NLS-1$
//         }           
//         String grouping = variable.getAttribute("grouping").getValueString();
//         if(grouping == null || "".equals(grouping))
//         {
             String grouping = "0"; //$NON-NLS-1$
//         }         
         docHTML_renderFields.writeHTML_forNumber(toPrint,
                                            new StringBuffer(valueObject),
                                            nameH,
                                            id,
                                            1,
                                            new StringBuffer(attrHandler.getDefAttribute().getType().toString()),
                                            new StringBuffer(decimals),
                                            new StringBuffer(minDecimals),
                                            ("0".equals(grouping)) ?  false :  true,     //$NON-NLS-1$
                                            new StringBuffer(maxNumber),
                                            new StringBuffer(minNumber),
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""), //$NON-NLS-1$
                                            isRequired,
                                            isRecommend,
                                            attributes
                                            );                                                                                                                                                 
                                            
        }
        else if(type == boDefAttribute.VALUE_DATETIME)
        {
            docHTML_renderFields.writeHTML_forDateTime(toPrint,
                                            new StringBuffer(valueObject),
                                            nameH,
                                            id,
                                            1,
                                            isDisabled,
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""), //$NON-NLS-1$
                                            isRequired,
                                            isRecommend,
                                            attributes
                                            );                                                                 
        }       
        else if(type == boDefAttribute.VALUE_DATE)
        {
            docHTML_renderFields.writeHTML_forDate(toPrint,
                                            new StringBuffer(valueObject),
                                            nameH,
                                            id,
                                            1,
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""), //$NON-NLS-1$
                                            isRequired,
                                            isRecommend,
                                            attributes
                                            );                                                                 
        }    
        else if(type == boDefAttribute.VALUE_CLOB)
        {
            if (renderLabel)
            {
                toPrint.append("<TD>"); //$NON-NLS-1$
                toPrint.append("<label ");                 //$NON-NLS-1$
                if(isDisabled)
                {
                    toPrint.append(" disabled "); //$NON-NLS-1$
                }
                if(isRequired)
                {
                    toPrint.append(" class=req "); //$NON-NLS-1$
                }
              toPrint.append(" for='"+ nameH +"'>");        //$NON-NLS-1$ //$NON-NLS-2$
              toPrint.append(variable.getAttribute("label").getValueString()); //$NON-NLS-1$
              toPrint.append("</label></TD>");  //$NON-NLS-1$
              toPrint.append("</tr>"); //$NON-NLS-1$
              toPrint.append("<tr>"); //$NON-NLS-1$
              toPrint.append("<td colspan='4'>"); //$NON-NLS-1$
            }
          attributes.put("height","250px"); //$NON-NLS-1$ //$NON-NLS-2$
          docHTML_renderFields.writeHTML_forHTMLEDITOR(toPrint,
                                                     varvalue,
                                                     varvalue.getAttribute(attrName),
                                                     doc,
                                                     new StringBuffer(valueObject),
                                                     nameH,
                                                     id,
                                                     1,
                                                     isDisabled,
                                                     isVisible,
                                                     false,
                                                     new StringBuffer(""), //$NON-NLS-1$
                                                     isRequired,
                                                     isRecommend,
                                                     attributes,
                                                    "html" //$NON-NLS-1$
                                                     );  
            if (renderLabel)
            {
                toPrint.append("</td>"); //$NON-NLS-1$
            }
            
        } 
        else if(type == boDefAttribute.VALUE_CHAR)
        { 
            docHTML_renderFields.writeHTML_text(toPrint,
                                            new StringBuffer(valueObject),
                                            nameH,
                                            id,
                                            1,
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""), //$NON-NLS-1$
                                            isRequired,
                                            isRecommend,
//                                            attrHandler.getDefAttribute().getLen(),
                                            3999,
                                            attributes
                                            );
        }        
        else if(type == 12)
        {
            String[] values = null;  
            long lovBoui = varvalue.getAttribute("lov").getValueLong(); //$NON-NLS-1$
            boObject lov = null;
            if(lovBoui != 0)
            {
                lov = boObject.getBoManager().loadObject(activity.getEboContext(),lovBoui);
            }
            
            //bridgeHandler lovHandler = null;
            lovObject lovHandler=null;
            if(lov != null)
            {
                 lovHandler = LovManager.getLovObject( activity.getEboContext(), lov.getAttribute("name").getValueString() ); //$NON-NLS-1$
            }
            if ( lovHandler!=null)
            {
            docHTML_renderFields.writeHTML_forCombo(
                                            toPrint,
                                            new StringBuffer(valueObject),
                                            nameH,
                                            id,
                                            1,
                                            lovHandler,
                                            false,
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""), //$NON-NLS-1$
                                            isRequired,
                                            isRecommend,
                                            attributes

                                            );
            }
        }           
        if(renderLabel && type != boDefAttribute.VALUE_CLOB)
        {
            toPrint.append("</td>\n");    //$NON-NLS-1$
        }   
        toPrint.append("</tr>");         //$NON-NLS-1$
    }
    public static void writeHTML_lookup(        
        StringBuffer toPrint,        
        AttributeHandler atrParent,
        long variableBoui,
        long activityBoui,
        docHTML doc
        )
        
        throws boRuntimeException{
        
            boObject varvalue = atrParent.getParent();
            boObject variable = boObject.getBoManager().loadObject(varvalue.getEboContext(),variableBoui);
            boObject activity = boObject.getBoManager().loadObject(variable.getEboContext(),activityBoui);
            renderCommon(toPrint,activity,variable,varvalue,doc,false,false);
        }    
    private static void writeHTML_lookup(
        StringBuffer clss,
        StringBuffer toPrint,
        boObject objParent,
        AttributeHandler atrParent,
        StringBuffer value,
        StringBuffer name,
        StringBuffer id,
        int tabIndex,
        docHTML doc,
        boolean isDisabled ,
        boolean isVisible ,
        boolean isRequired,
        boolean isRecommend,        
        Hashtable xattributes,
        boObject variable,
        boObject activity
        )
        
        throws boRuntimeException{


        long xRefBoui=ClassUtils.convertToLong(value,-1);
        boObject xref=null;
        if(xRefBoui>0)
        {
            xref=objParent.getBoManager().loadObject(objParent.getEboContext(),xRefBoui );
            clss.delete(0, clss.length());
            clss.append(xref.getName());
        } 
                
        toPrint.append("<table id='"); //$NON-NLS-1$
        toPrint.append("ext").append(id); //$NON-NLS-1$
        toPrint.append("' style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'><tbody><tr><td>"); //$NON-NLS-1$
                        
        if(xRefBoui!=-1 && xRefBoui!=0)
        {
            toPrint.append("<div class='lu ro lui' "); //$NON-NLS-1$
        }
        else
        {
            toPrint.append("<div style='overflow:hidden' class='lu ro lui' "); //$NON-NLS-1$
        }
        
        toPrint.append("valido='");                  //$NON-NLS-1$
        toPrint.append( clss );                 
        toPrint.append("' >"); //$NON-NLS-1$
        

        String  XeoWin32Client_address = objParent.getEboContext().getXeoWin32Client_adress();
        if(!DocumentHelper.isDocument(clss.toString()) || 
            !DocumentHelper.isMSWordFile( xRefBoui==-1||xRefBoui==0?null:doc.getObject( xRefBoui ) ) ||
             XeoWin32Client_address==null ||
            !RegistryHelper.isClientConnected(XeoWin32Client_address))           
        {          
        
            docHTML_renderFields.writeHTML_lookupObject(toPrint,
                                                       objParent,
                                                       atrParent,
                                                       value,
                                                       tabIndex,
                                                       doc,
                                                       isDisabled,
                                                       isVisible,
                                                       true,
                                                       xRefBoui,
                                                       false);
        }
        else
        {                   
            docHTML_renderFields.writeHTML_lookupDocument(toPrint,
                                                          objParent,  
                                                          atrParent,
                                                          value,
                                                          name,
                                                          tabIndex,
                                                          doc,
                                                          isDisabled,
                                                          isVisible,
                                                          xRefBoui,
                                                          clss.toString(),
                                                          false);
        }

        
		toPrint.append("</div>"); //$NON-NLS-1$
        toPrint.append("</td>"); //$NON-NLS-1$
        toPrint.append("<td style='TEXT-ALIGN: right' width='25'><img class='lu' id style='CURSOR: default' tabIndex='"); //$NON-NLS-1$
        toPrint.append(tabIndex);
        
        if ( isDisabled ) {
                toPrint.append("' disabled src='templates/form/std/btn_dis_lookup.gif' lookupstyle='single' "); //$NON-NLS-1$
        }
        else {
            if(atrParent.getDefAttribute().getRelationType()==boDefAttribute.RELATION_1_TO_1){
                toPrint.append("' src='templates/form/std/btn_off_lookup.gif' lookupstyle='single' "); //$NON-NLS-1$
            }
            else{
                toPrint.append("' src='templates/form/std/btn_off_lookup.gif' lookupstyle='multi' "); //$NON-NLS-1$
            }
            
            
            if ( !isVisible ) {
                    toPrint.append(" style='display:none' "); //$NON-NLS-1$
            }
        }
        if ( variable != null && activity != null ) 
        {
            toPrint.append(" options='forWorkFlowActivity.").append(variable.getBoui()); //$NON-NLS-1$
            toPrint.append("."); //$NON-NLS-1$
            toPrint.append(activity.getBoui()).append("'"); //$NON-NLS-1$
        }
        toPrint.append(" shownew='"); //$NON-NLS-1$
        toPrint.append("1'"); //$NON-NLS-1$
        toPrint.append(" parentBoui='"); //$NON-NLS-1$
        toPrint.append(objParent.bo_boui);
        toPrint.append("' parentObj='"); //$NON-NLS-1$
        toPrint.append(objParent.getName());
        toPrint.append("' parentAttribute='"); //$NON-NLS-1$
        toPrint.append(atrParent.getName() );
        toPrint.append("' object='"); //$NON-NLS-1$
        toPrint.append(clss);
        toPrint.append("'  docid='"); //$NON-NLS-1$
        toPrint.append(doc.getDocIdx());
        toPrint.append("' width='21' height='19'><input type='hidden' value='"); //$NON-NLS-1$
        toPrint.append(value);
        toPrint.append("' name='"); //$NON-NLS-1$
        toPrint.append(name);
        toPrint.append("' object='"); //$NON-NLS-1$
        toPrint.append(atrParent.getDefAttribute().getReferencedObjectName());
        toPrint.append("' req='"); //$NON-NLS-1$
        boolean req = atrParent.required();        
        if ( req ) toPrint.append(1);
        else  toPrint.append(0);
        toPrint.append("' boType='lu'>"); //$NON-NLS-1$
        
        
        toPrint.append("</td></tr></tbody></table>"); //$NON-NLS-1$
              
    }    
    private static void renderSeparatorLabel(StringBuffer toPrint, String label)  throws boRuntimeException
    {
        toPrint.append("<TR><TD colspan=4 nowrap style=\"border-bottom:2 groove\" ><B>"); //$NON-NLS-1$
        toPrint.append(label);        
        toPrint.append("</B></TD></TR>");         //$NON-NLS-1$
    }
    private static void renderJustification(docHTML doc, StringBuffer toPrint,boObject activity, boolean disabled) throws boRuntimeException
    {
        Hashtable attributes = new Hashtable();
        AttributeHandler attrHandler = activity.getAttribute("justification"); //$NON-NLS-1$
        StringBuffer nameH = new StringBuffer();
        StringBuffer id = new StringBuffer();
        nameH.append( activity.getName() ).append( "__" ).append( activity.bo_boui ).append("__justification"); //$NON-NLS-1$ //$NON-NLS-2$
        id.append("tblLook").append( activity.getName() ).append( "__" ).append( activity.bo_boui ).append("__justification"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
        renderSeparatorLabel(toPrint,attrHandler.getDefAttribute().getLabel());
        
        toPrint.append("<TR><TD colspan=4>&nbsp;</TD></TR>"); //$NON-NLS-1$
        
        
        toPrint.append("<TR>"); //$NON-NLS-1$
        toPrint.append("<td colspan='4' style='padding-left:30px;padding-right:30px'>");                    //$NON-NLS-1$

        attributes.put("height","250px"); //$NON-NLS-1$ //$NON-NLS-2$
        docHTML_renderFields.writeHTML_forHTMLEDITOR(toPrint,
                                                     activity,
                                                     attrHandler,
                                                     doc,
                                                     new StringBuffer(attrHandler.getValueString()),
                                                     nameH,
                                                     id,
                                                     1,
                                                     disabled,
                                                     true,
                                                     false,
                                                     new StringBuffer(""), //$NON-NLS-1$
                                                     false,
                                                     true,
                                                     attributes,
                                                    "html" //$NON-NLS-1$
                                                     );  
        toPrint.append("</td>");      //$NON-NLS-1$
        toPrint.append("</TR>"); //$NON-NLS-1$
    }
    private static boolean isDisabled(boObject activity, boObject variable,boolean forceDisable) throws boRuntimeException
    {
        boolean result = false;
        if(forceDisable)
        {
            result = true;
        }
        else
        {
            long mode = variable.getAttribute("mode").getValueLong();    //$NON-NLS-1$
            if(mode == 0)
            {
                result = true;
            }
    
            String input = variable.getAttribute("input").getValueString();    //$NON-NLS-1$
            if("1".equals(input)) //$NON-NLS-1$
            {
                result = false;
            }
            if(activity != null)
            {
                boObjectStateHandler pstate = activity.getStateAttribute( "runningState" ); //$NON-NLS-1$
                if (pstate != null && pstate.getValueString().equals("close")) //$NON-NLS-1$
                {        
                    result = true;
                }                    
            }
        }
        return result;
    }
//    private static void renderTableStart(StringBuffer toPrint,int renderMode)
    private static void renderTableStart(StringBuffer toPrint)
    {
        toPrint.append("<br>"); //$NON-NLS-1$
        toPrint.append("<table valign=top class='section' "); //$NON-NLS-1$
        toPrint.append(" cellSpacing='0' cellPadding='3' width='100%' "); //$NON-NLS-1$
//        if(renderMode == 1)
//        {
//            toPrint.append("height='100%'");
//        }
        toPrint.append("><COLGROUP/><COL width='120' /><COL /><COL style=\"PADDING-LEFT: 5px\" width='70' /><COL /><tbody>"); //$NON-NLS-1$
        
    }
    private static void renderTableEnd(StringBuffer toPrint)
    {
        toPrint.append("</tbdoy></table>"); //$NON-NLS-1$
    }
    private static void renderTableSpace(StringBuffer toPrint)
    {
        toPrint.append("<tr><td colspan='4'><br></td></tr>"); //$NON-NLS-1$
    }

    public static Element getWaitElement(XwfController controller, int idx) throws boRuntimeException
    {        
        StringBuffer sb = new StringBuffer();
        sb.append("<IFRAME id='waitViewer' src='__xwfViewerActivityWaitList.jsp?"); //$NON-NLS-1$
        sb.append("docid=").append(idx); //$NON-NLS-1$
        sb.append("&method=edit");          //$NON-NLS-1$
        sb.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("&").append(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeProgramBoui());                 //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("&").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeActivityBoui());         //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("' frameBorder='0' width='100%' height='100%' scrolling=no ></IFRAME>"); //$NON-NLS-1$
        Element wait = new AdHocElement(sb.toString());
        return wait;
    }      

    public static Element getAssociateWaitElement(XwfController controller, String program,int idx) throws boRuntimeException
    {        
        StringBuffer sb = new StringBuffer();
        boObject valueObject = controller.getRuntimeActivity().getAttribute("message").getObject().getAttribute("value").getObject().getAttribute("valueObject").getObject();         //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        sb.append("<IFRAME ");         //$NON-NLS-1$
        sb.append("id=").append("'inc_xwfActivityReceive__").append(controller.getRuntimeActivityBoui()).append("__waitingResponse' ");         //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        sb.append("src='xwfwaitresponse_generalcardid_list.jsp?"); //$NON-NLS-1$
        sb.append("docid=").append(idx); //$NON-NLS-1$
        sb.append("&method=list");     //$NON-NLS-1$
        sb.append("&").append("menu").append("=").append("no"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        sb.append("&").append("parent_attribute").append("=").append("waitingResponse"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        sb.append("&").append("parent_boui").append("=").append(controller.getRuntimeActivityBoui());                 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        sb.append("' frameBorder='0' width='100%' height='100%' scrolling=no ></IFRAME>"); //$NON-NLS-1$
        Element wait = new AdHocElement(sb.toString());
        return wait;
    }      
    public static Element getProgramElement(XwfController controller,int idx) throws boRuntimeException
    {        
        StringBuffer sb = new StringBuffer();
        sb.append("<IFRAME src='__xwfViewerActivityProgramList.jsp?"); //$NON-NLS-1$
        sb.append("docid=").append(idx);         //$NON-NLS-1$
        sb.append("&method=edit");          //$NON-NLS-1$
        sb.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("&").append(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeProgramBoui()); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("&").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeActivityBoui());         //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("' frameBorder='0' width='100%' height='100%' scrolling=no ></IFRAME>"); //$NON-NLS-1$
        Element program = new AdHocElement(sb.toString());
        return program;
    }    
    public static Element getProgramReverseBridgeElement(XwfController controller,int idx) throws boRuntimeException
    {        
        StringBuffer sb = new StringBuffer();        
        boObject activity = controller.getRuntimeActivity();
        boObject variable = activity.getAttribute("message").getObject(); //$NON-NLS-1$
        boObject value = variable.getAttribute("value").getObject(); //$NON-NLS-1$
        boObject message = value.getAttribute("valueObject").getObject(); //$NON-NLS-1$
        
        sb.append("<IFRAME ");         //$NON-NLS-1$
        sb.append("id=").append("'inc_message__").append(message.getBoui()).append("__dynbrige_xwfProgramRuntime' ");         //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        sb.append("src='xwfprogramruntime_generalcardid_list.jsp?");         //$NON-NLS-1$
        sb.append("docid=").append(idx);             //$NON-NLS-1$
        sb.append("&method=list"); //$NON-NLS-1$
        sb.append("&").append("menu").append("=").append("no"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        sb.append("&").append("list_frommethod").append("=").append("referencedBy%28xwfProgramRuntime%2Cmessage%29"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        sb.append("&").append("parent_attribute").append("=").append("dynbrige_xwfProgramRuntime"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        sb.append("&").append("parent_boui").append("=").append(message.getBoui()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//        String onclick = URLEncoder.encode(URLEncoder.encode("winmain().openDoc('medium','xwfprogramruntime','edit','method=edit&" + ControllerFactory.CONTROLLER_NAME_KEY + "=XwfController&masterdoc=true&boui=OBJECT_LIST_BOUI');"));
        StringBuffer onclick = new StringBuffer();        
        onclick.append("winmain().openDocUrl('closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','");         //$NON-NLS-1$
        onclick.append("?method=edit");         //$NON-NLS-1$
        onclick.append("&masterdoc=true"); //$NON-NLS-1$
        onclick.append("&runtimeProgramBoui=OBJECT_LIST_BOUI')"); //$NON-NLS-1$
        
        sb.append("&").append("onclick").append("=").append(URLEncoder.encode(URLEncoder.encode(onclick.toString())));         //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        sb.append("' frameBorder='0' width='100%' height='100%' scrolling=no ></IFRAME>"); //$NON-NLS-1$
        Element program = new AdHocElement(sb.toString());
        return program;
    }        
    public static Element getPreviewReceiveElement(XwfController controller) throws boRuntimeException
    {
        boObject msg = null, xwfVar = null, valueObject = null;
        if((msg = controller.getRuntimeActivity().getAttribute("message").getObject()) != null) //$NON-NLS-1$
        {
            if((xwfVar = msg.getAttribute("value").getObject()) != null) //$NON-NLS-1$
            {
                if((valueObject = xwfVar.getAttribute("valueObject").getObject()) != null) //$NON-NLS-1$
                {
                    Element preview = new Preview(valueObject,"preview",null); //$NON-NLS-1$
                    return preview;
                }
            }
        }
        Element preview = new Preview(null,"preview",null); //$NON-NLS-1$
        return preview;
    }
    public static Element getPreviewSendElement(XwfController controller) throws boRuntimeException
    {
        boObject actvSend = null, msg = null, xwfVar = null, valueObject = null; 
        if((actvSend = controller.getRuntimeActivity().getAttribute("sendActivity").getObject()) != null) //$NON-NLS-1$
        {
            if((msg = actvSend.getAttribute("message").getObject()) != null) //$NON-NLS-1$
            {
                if((xwfVar = msg.getAttribute("value").getObject()) != null) //$NON-NLS-1$
                {
                    if((valueObject = xwfVar.getAttribute("valueObject").getObject()) != null) //$NON-NLS-1$
                    {
                        Element preview = new Preview(valueObject,"previeSendActivity",null); //$NON-NLS-1$
                        return preview;
                    }
                }
            }
        }
        Element preview = new Preview(null,"previeSendActivity",null); //$NON-NLS-1$
        return preview;
    }  
    public static Element getProgramMessagesElement(XwfController controller,int idx) throws boRuntimeException
    {        
        StringBuffer sb = new StringBuffer();
        sb.append("<IFRAME ");         //$NON-NLS-1$
        sb.append("id=").append("'inc_saveOn__").append(controller.getRuntimeActivityBoui()).append("__xwfWaitResponse' ");                 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        sb.append("src='__xwfViewerProgramMessageList.jsp?"); //$NON-NLS-1$
        sb.append("docid=").append(idx);         //$NON-NLS-1$
        sb.append("&method=edit");          //$NON-NLS-1$
        sb.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("&").append(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeProgramBoui()); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("&").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeActivityBoui()); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("' frameBorder='0' width='100%' height='100%' scrolling=no ></IFRAME>"); //$NON-NLS-1$
        Element program = new AdHocElement(sb.toString());
        return program;
    }  
    public static Element getEmptyPreview() throws boRuntimeException
    {
        Element preview = new Preview(null,"preview",null); //$NON-NLS-1$
        return preview;
    }
    public static Element getCallProgramElement(XwfController controller,int idx) throws boRuntimeException
    {        
        StringBuffer sb = new StringBuffer();
        sb.append("<IFRAME ");         //$NON-NLS-1$
        sb.append("id=").append("'callProgram'");                 //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("src='__xwfViewerActivityCallProgram.jsp?"); //$NON-NLS-1$
        sb.append("docid=").append(idx);         //$NON-NLS-1$
        sb.append("&method=edit");          //$NON-NLS-1$
        sb.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append(controller.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("&").append(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeProgramBoui()); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("&").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("=").append(controller.getRuntimeActivityBoui());         //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("' frameBorder='0' width='100%' height='100%' scrolling=no ></IFRAME>"); //$NON-NLS-1$
        Element program = new AdHocElement(sb.toString());
        return program;
    }         
}