/*Enconding=UTF-8*/
package netgest.xwf.stepControllers;
import java.lang.reflect.Constructor;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.xwf.StepControl;
import netgest.xwf.core.*;
import org.w3c.dom.Node;
import netgest.xwf.common.xwfBoManager;
import netgest.xwf.common.xwfHelper;
import netgest.bo.runtime.*;
import netgest.xwf.common.xwfFunctions;
import netgest.utils.ngtXMLHandler;

public class xwfBasicStepController implements StepControl
{
    private xwfStepExec st_exe; 
    
    public xwfBasicStepController() throws boRuntimeException
    {
        
    }
    
    public xwfBasicStepController(xwfBoManager xbm, Node step, xwfECMAevaluator ecmaeval, xwfControlFlow cf) throws boRuntimeException
    {
        st_exe = new xwfStepExec(xbm, step, ecmaeval, cf);
    }
    
    public boolean execStep() throws boRuntimeException
    {
        return st_exe.execStep();
    }
    
    public void finishedStep(ngtXMLHandler npn, String option, xwfControlFlow cf) throws boRuntimeException
    {
        if(xwfHelper.STEP_CHOICE.equals(npn.getNodeName()))
        {
          int pos = Integer.parseInt(option);
          ngtXMLHandler asw = npn.getChildNode("answers");
          ngtXMLHandler[] choices = asw.getChildNodes();
          ngtXMLHandler noption = choices[pos];
          Node nextpn = cf.findNextStep(noption.getNode(), true);
          if(nextpn == null)
            return;
          if(cf.markPointer(nextpn, "start"))
          {
            cf.incCounter(nextpn);
          }
          cf.markPointer(noption.getNode(), "done");
          cf.incCounter(noption.getNode());
          
        }
        else
          if(xwfHelper.STEP_DECISION.equals(npn.getNodeName()))
          {
    //        int pos = Integer.parseInt(option);
              if("done".equalsIgnoreCase(npn.getAttribute("pointer")))
                 return;
            ngtXMLHandler asw = npn.getChildNode("answers");
            
            ngtXMLHandler noption = asw.getChildNode(option);
            Node nextpn = cf.findNextStep(noption.getNode(), true);
            if(nextpn == null)
              return;
            if(cf.markPointer(nextpn, "start"))
              cf.incCounter(nextpn);
            cf.markPointer(noption.getNode(), "done");
            cf.incCounter(noption.getNode());
          }
          else
            if(xwfHelper.STEP_MENU.equals(npn.getNodeName()))
            {
              int pos = Integer.parseInt(option);
              ngtXMLHandler asw = npn.getChildNode("answers");
              ngtXMLHandler[] choices = asw.getChildNodes();
              ngtXMLHandler noption = choices[pos];
              Node nextpn = cf.findNextStep(noption.getNode(), true);
              if(nextpn == null)
                return;
              if(cf.markPointer(nextpn, "start"))
              {
                cf.incCounter(nextpn);
              }
              cf.markPointer(noption.getNode(), "done");
              cf.incCounter(noption.getNode());
            }
          cf.markPointer(npn.getNode(), "done");
          cf.incCounter(npn.getNode());
    }
  
  /**
   * Rotina que permite indicar o fim de determinado passo no WF.
   * <p>Para além de finalizar o corrente passo irá mover o apontador para o próximo passo.
   * 
   * @param pn  Nó XML representante do passo a terminar
   * @throws java.lang.boRuntimeException  Excepções ao nível da escrita no XML.
   */
    public void finishedStep(ngtXMLHandler ngtn, xwfControlFlow cf) throws boRuntimeException
    {
    //    evals.remove(this.getXwfEvalKey(ngtn));
        
        String ass_val = ngtn.getAttribute("async");
        if(ass_val == null || (ass_val != null && !ass_val.equals("true")))
        {    
          Node nextpn = cf.findNextStep(ngtn.getNode(), false);
          cf.markPointer(ngtn.getNode(), "done");
          if(nextpn == null)
          {
            cf.finishFlow();
            return;
          }
          cf.markPointer(nextpn, "start");
          cf.incCounter(nextpn);
        }
        else
        {
          cf.markPointer(ngtn.getNode(), "done");
          String asy_name = ngtn.getAttribute("name");
          if(asy_name != null)
            cf.finishThread(asy_name);
        }
  }
    
    public boolean specialTreatment(boObject actv, xwfManager manag) throws boRuntimeException
    {
        if("xwfCreateReceivedMessage".equals(actv.getName()))
        {
            boObject msg = manag.getBoManager().getValueBoObject(actv.getAttribute("message").getObject()
                    .getAttribute("value").getObject());
            xwfMessage.receiveMessage(manag, msg, manag.getBoManager().getProgram());
        }
        else
            if("xwfActivitySend".equals(actv.getName()))
            {
                if("-1".equals(actv.getAttribute("sid").getValueString()))
                {
                    boObject msg = manag.getBoManager().getValueBoObject(actv.getAttribute("message").getObject()
                            .getAttribute("value").getObject());
                    
                    if(msg != null)
                    {
                        boObject fromRef = msg.getAttribute("fromRef").getObject();
                        if(fromRef != null)
                            actv.getAttribute("assignedQueue").setObject(fromRef);
                    }
                }   
            }
        else
          if("xwfActivityReceive".equals(actv.getName()))
          {
            bridgeHandler b_wait = actv.getBridge("waitingResponse");
            b_wait.beforeFirst();
            while(b_wait.next())
            {
              manag.finishedStep(b_wait.getCurrentBoui(), false);
            }
            b_wait = actv.getBridge("relatedProgram");
            b_wait.beforeFirst();
            while(b_wait.next())
            {
              if(b_wait.getCurrentBoui() != manag.getBoManager().getProgBoui())
                xwfMessage.createRecActv(manag, manag.getBoManager().getPerformerBoui(), 
                actv.getAttribute("message").getObject().getAttribute("value").getObject().getAttribute("valueObject").getObject(),
                b_wait.getCurrentBoui());
            }
            manag.getBoManager().updateObject(actv.getAttribute("message").getObject().getAttribute("value").getObject().getAttribute("valueObject").getObject());
          }
          else
          {
            if("xwfWaitResponse".equals(actv.getName()))
            {                
              long recv_boui = actv.getAttribute("receiveActivity").getValueLong();
              if(recv_boui > 0)
              {
                boObject recvActivity = manag.getBoManager().getObject(recv_boui);
                xwfFunctions.setActivityData(recvActivity,XwfKeys.ACTION_CLOSE_KEY);                
                manag.finishedStep(recv_boui, false);   
                xwfFunctions.setIntelligentLabel(((XwfController)manag.getBoManager().getContext().getController()).getEngine(),recvActivity);
              }            
            }
            else
              if("xwfUserCallProgram".equals(actv.getName()))
              {
                String xml = actv.getAttribute("xmlStep").getValueString();
                String usid = actv.getAttribute("unique_sid").getValueString();
                manag.getControlFlow().addCodeTag("code", usid, xml, true);
              }
          }
          return true;
    }
    
    public boolean specialTreatment(boObject actv, String option, xwfManager manag) throws boRuntimeException
    {
        String usid = actv.getAttribute("unique_sid").getValueString();
        if(usid == null || usid.equals("-1") || usid.startsWith("0_"))
            return true;
        AttributeHandler att_poll = actv.getAttribute("pollObj");
        if(att_poll != null)
        {
            boObject poll = att_poll.getObject();
            if(poll != null)
            {
                Class<?> cpoll = manag.getControlFlow().getStepControlClass("poll");
                if(cpoll != null)
                {
                    try{
                        Constructor stc_const = cpoll.getConstructor();
                        StepControl se = (StepControl)stc_const.newInstance();
                        return se.specialTreatment(actv, option, manag);
                    }catch(Exception e){}
                }
            }
        }
        ngtXMLHandler npn = manag.getControlFlow().getNode(usid);
        ngtXMLHandler n_app = npn.getChildNode("approval");
          if(n_app != null && n_app.getNode() != null)
          {
            String s_app_boui = n_app.getAttribute("boui");
            if(s_app_boui != null && s_app_boui.length() > 0)
            {
                String s_app_val = n_app.getAttribute("validAnswer");
                if(s_app_boui != null && s_app_boui.length() > 0 && option.equals(s_app_val))
                {
                    manag.getControlFlow().makeApproval(Long.parseLong(s_app_boui), actv.getAttribute("performer").getValueLong(), npn.getNode(), option);
                }
            }
          }

        if("xwfActivityDecision".equals(actv.getName()) || "xwfActivityChoice".equals(actv.getName())
                || "xwfActivityPoll".equals(actv.getName()))
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
        }

        return true;
    }
}