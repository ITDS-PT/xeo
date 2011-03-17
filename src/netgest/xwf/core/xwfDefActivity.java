/*Enconding=UTF-8*/
package netgest.xwf.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.naming.InitialContext;
import netgest.bo.controller.ControllerFactory;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.message.GarbageController;
import netgest.bo.message.MessageServer;
import netgest.bo.system.*;
import netgest.xwf.*;
import netgest.xwf.common.*;
import java.util.*;
import netgest.bo.runtime.*;
import netgest.bo.security.*;
import netgest.utils.*;
import netgest.bo.def.*;
import netgest.bo.dochtml.*;
import netgest.bo.utils.*;
import netgest.bo.system.Logger;


public class xwfDefActivity extends Thread 
{
    private boObject p_defActivity = null;
    private boApplication p_app = null;
    private int p_sleepTime = 0;
    
    private static final int FIRST_CODE = 97440432;
    private static final int SECOND_CODE = -906279820;
    private static final int THIRD_CODE = 110331239;
    private static final int FOURTH_CODE = -1268684262;
    private static final int LAST_CODE = 3314326;
    
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.runtime.robots.xwfDefActivityThread");

    public xwfDefActivity( ThreadGroup group, boObject defActivity, boApplication app, int sleepTime)
    { 
        super(group, "Definição de Actividades") ;
        p_defActivity = defActivity;
        p_app = app;
        p_sleepTime = sleepTime;
    }
    
    private static void createProgram(EboContext ctx, boObject defActivity) throws boRuntimeException
    {
        boObject program = boObject.getBoManager().createObject(ctx, "xwfProgramRuntime");        
        String label = defActivity.getAttribute("label").getValueString();
        if(label != null && label.length() >= 200)
        {
            label = label.substring(0, 190) + "(...)"; 
        }
        program.getAttribute("label").setValueString(label, AttributeHandler.INPUT_FROM_INTERNAL);        
        if(defActivity.getAttribute("SYS_DTCREATE").getValueDate() != null)
        {
            program.getAttribute("beginDate").setValueDate(defActivity.getAttribute("SYS_DTCREATE").getValueDate(), AttributeHandler.INPUT_FROM_INTERNAL);
        }
        else
        {
            program.getAttribute("beginDate").setValueDate(new Date(), AttributeHandler.INPUT_FROM_INTERNAL);
        }
        //tenho que juntar ao programa os responsáveis
        bridgeHandler bh = program.getBridge("access");
        if(bh != null)
        {
            if(!bh.haveBoui(defActivity.getAttribute("assignedQueue").getValueLong()))
            {
                bh.add(defActivity.getAttribute("assignedQueue").getValueLong());
            }
            if(!bh.haveBoui(defActivity.getAttribute("to").getValueLong()))
            {
                bh.add(defActivity.getAttribute("to").getValueLong());
            }
        }
        
        
        defActivity.getAttribute("program").setObject(program);
    }

    public void run()
    {
        EboContext ctx = null;
        long iniTime = System.currentTimeMillis();
        boolean ok = false;
        String error = null;
        boolean deactivate = false;
        try
        {
            logger.finest(MessageLocalizer.getMessage("STARTING_AGENT_FOR_DEF_ACTIVITY")+" "+ p_defActivity.getAttribute("label").getValueString() );
            
            InitialContext ic = new InitialContext();
            
            boLogin login = ((boLoginHome)ic.lookup("boLogin")).create();
//            boSession session =  login.boLogin( p_app, "SYSTEM", boLoginBean.getSystemKey(), p_app.getDefaultRepositoryName() );
            boSession session = p_app.boLogin( "SYSTEM", boLoginBean.getSystemKey(),p_app.getDefaultRepositoryName() );
            
            ctx = session.createRequestContext( null,null,null );
            
            try
            {
                xwfEngineGate engine = null;
                if(p_defActivity.getAttribute("program").getValueObject() == null)
                {
                    createProgram(p_defActivity.getEboContext(), p_defActivity);
                }
                engine = new xwfEngineGate(p_defActivity.getEboContext(),
                            boObject.getBoManager().loadObject(
                                p_defActivity.getEboContext(), 
                                p_defActivity.getAttribute("program").getValueLong()
                            )
                        );
                ok = verifier(engine, p_defActivity, p_sleepTime);
            }
            finally
            {
                if( ctx != null )
                {
                    ctx.close();                    
                }
                if(session != null)
                {
                    session.closeSession();
                }
                if(ic != null)
                {
                    ic.close();
                }
            }
            // Release the objects
        }
        catch (Exception e)
        {
            deactivate = true;
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
        }
        boSession session = null;
        try
        {
            session = p_app.boLogin( "SYSTEM", boLoginBean.getSystemKey(),p_app.getDefaultRepositoryName() );            
            ctx = session.createRequestContext( null,null,null );
            
            Date now = new Date();
            boObject reloadedsched = boObject.getBoManager().loadObject( ctx, p_defActivity.getBoui() );
            
            //last runtime
            reloadedsched.getAttribute("lastruntime").setValueDate(reloadedsched.getAttribute("nextruntime").getValueDate());
            // Calc the next runtime            
            reloadedsched.getAttribute("nextruntime").setValueDate(calcNextRun( reloadedsched, null, p_sleepTime ));

            // Set the schedule attributes
            if("1".equals(reloadedsched.getAttribute("createdAll").getValueString()))
            {
                reloadedsched.getStateAttribute( "activeStatus" ).setValue( "3" );
            }
            else
            {
                reloadedsched.getStateAttribute( "activeStatus" ).setValue( "0" );
            }
            reloadedsched.getAttribute("executiontime").setValueLong( System.currentTimeMillis() - iniTime );
            reloadedsched.getAttribute("lastresultcode").setValueString( ok?"OK":"NOK" );
            
            if( error != null )
            {
                reloadedsched.getAttribute("errormessage").setValueString( error );
            }
            if( deactivate )
            {
                cancel(reloadedsched);
            }
            else
            {
                reloadedsched.update();
            }
        }
        catch (boRuntimeException e)
        {
            logger.warn(MessageLocalizer.getMessage("ERROR_SETTING_OBJECT_TO_STATE_READY"), e);
        }
        catch (Exception e)
        {
            logger.warn(MessageLocalizer.getMessage("ERROR_SETTING_OBJECT_TO_STATE_READY"), e);
        }
        finally
        {
            if( ctx != null )
            {
                ctx.close();                    
            }
            if(session != null)
            {
                session.closeSession();
            }
        }
    }

    public static boolean verifier(xwfEngineGate engine,boObject actvDef, int sleepInMills) throws boRuntimeException
    {
        boolean result = false;
        if(!"1".equals(actvDef.getAttribute("type_recursive").getValueString()))
        {
            result = simples(engine,actvDef, sleepInMills);
        }
        else
        {
            result = periodica(engine,actvDef, sleepInMills);
        }

        return result;
    }
    
    private static boolean simples(xwfEngineGate engine,boObject actvDef, int sleepInMills) throws boRuntimeException
    {
        boolean result = true;
        //simples vou verificar a hora de criação
        if(create(actvDef.getAttribute("beginDate").getValueDate(),sleepInMills)
            && !"1".equals(actvDef.getAttribute("createdAll").getValueString())
        )
        {
            boObject actv = engine.getBoManager().createObject("xwfActivity");
            setActvFields(actv, actvDef, sleepInMills);
//            engine.getBoManager().updateObject(actv);
            actvDef.getBridge("defActivityDepends").add(actv.getBoui());
            actvDef.getAttribute("createdAll").setValueString("1");
            actvDef.getAttribute("n_occur").setValueString("1");
            engine.getBoManager().updateObject(actvDef);
            engine.getProgramRuntime().getStateAttribute("runningState").setValue("open");
            engine.getBoManager().updateObject(engine.getProgramRuntime());
            result = true;
        }
        
        return result;
    }
    
    private static void setActvFields(boObject actv, boObject actvDef, int sleepTime) throws boRuntimeException
    {
        boolean recursive = "1".equals(actvDef.getAttribute("type_recursive").getValueString());
        actv.getAttribute("label").setValueObject(actvDef.getAttribute("label").getValueObject());
        actv.getAttribute("program").setValueObject(actvDef.getAttribute("program").getValueObject());
        actv.getAttribute("description").setValueObject(actvDef.getAttribute("description").getValueObject());
        actv.getAttribute("assignedQueue").setValueObject(actvDef.getAttribute("to").getValueObject());
        actv.getAttribute("performer").setValueObject(actvDef.getAttribute("performer").getValueObject());
        actv.getAttribute("defActivity").setObject(actvDef);
        //anexos
        boBridgeIterator docit = actvDef.getBridge("documents").iterator();
        docit.beforeFirst();
        long docBoui;
        while(docit.next()) 
        {
            actv.getBridge("documents").add(docBoui = docit.currentRow().getObject().getBoui());
        }
        
        if(!"1".equals(actvDef.getAttribute("type_recursive").getValueString()))
        {//simples
            actv.getAttribute("prvBeginDate").setValueObject(actvDef.getAttribute("beginDate").getValueObject());
            if("1".equals(actvDef.getAttribute("setDuration").getValueString()))
            {
                Date bg = actvDef.getAttribute("beginDate").getValueDate();
                String durValue = actvDef.getAttribute("duration").getValueString();
                Date deadlinedate = DateUtils.sumDurationToDate(durValue, bg);
                actv.getAttribute("prv_Duration").setValueString(durValue);
                actv.getAttribute("deadLineDate").setValueDate(deadlinedate);
            }
            else if("1".equals(actvDef.getAttribute("setUtilDays").getValueString()))
            {
                Date bg = actvDef.getAttribute("beginDate").getValueDate();
                long days = actvDef.getAttribute("utilDays").getValueLong();
                Date deadlinedate = DateUtils.sumToUtilDate(bg,0,0,days, actv.getEboContext());
                actv.getAttribute("utilDays").setValueLong(days);
                actv.getAttribute("deadLineDate").setValueDate(deadlinedate);
            }
            else if("1".equals(actvDef.getAttribute("setEndDate").getValueString()))
            {
                actv.getAttribute("deadLineDate").setValueObject(actvDef.getAttribute("endDate").getValueObject());
            }
        }
        else
        {//recursivo
            Date vigBegin = actvDef.getAttribute("nextruntime").getValueDate();
            vigBegin = add(actvDef.getAttribute("nextruntime").getValueDate(), sleepTime);
            if(vigBegin == null) vigBegin = actvDef.getAttribute("vig_beginDate").getValueDate(); 
            actv.getAttribute("prvBeginDate").setValueDate(vigBegin);
            if("1".equals(actvDef.getAttribute("vig_setDuration").getValueString()))
            {
                String durValue = actvDef.getAttribute("vig_duration").getValueString();
                Date deadlinedate = DateUtils.sumDurationToDate(durValue, vigBegin);
                actv.getAttribute("prv_Duration").setValueString(durValue);
                actv.getAttribute("deadLineDate").setValueDate(deadlinedate);
            }
            else if("1".equals(actvDef.getAttribute("vig_setUtilDays").getValueString()))
            {
                long days = actvDef.getAttribute("vig_utilDays").getValueLong();
                Date deadlinedate = DateUtils.sumToUtilDate(vigBegin,0,0,days, actv.getEboContext());
                actv.getAttribute("utilDays").setValueLong(days);
                actv.getAttribute("deadLineDate").setValueDate(deadlinedate);
            }
            else if("1".equals(actvDef.getAttribute("vig_setEndDate").getValueString()))
            {
                actv.getAttribute("deadLineDate").setValueObject(actvDef.getAttribute("vig_endDate").getValueObject());
            }
        }

        actv.getAttribute("priority").setValueObject(actvDef.getAttribute("priority").getValueObject());
        //retirei o creator pq senão aparecia nas tarefas do criador o que não é o objectivo
        actv.getAttribute("controlBy").setValueObject(actvDef.getAttribute("assignedQueue").getValueObject());
        //retirei o creator pq senão aparecia nas tarefas do criador o que não é o objectivo        
        actv.getAttribute("CREATOR").setValueObject(actvDef.getAttribute("CREATOR").getValueObject());
        if((!recursive && "resume".equals(actvDef.getAttribute("toReturn").getValueString()))
            || (recursive && "resume".equals(actvDef.getAttribute("taskReturn").getValueString()))
        )
        {
            actv.getAttribute("justificationRequired").setValueString("1");
        }
        
        actv.getAttribute("sid").setValueString("-1");
        actv.getAttribute("unique_sid").setValueString("-1");
        actv.getAttribute("optional").setValueString("0");
        actv.getAttribute("done").setValueString("0");
        actv.getAttribute("showTask").setValueString("1");
        actv.getAttribute("showReassign").setValueString("1");
        actv.getStateAttribute( "runningState" ).setValue( "0" );
    }
    
    private static boolean periodica(xwfEngineGate engine,boObject actvDef, int sleepInMills) throws boRuntimeException
    {
        boolean result = true;
        //simples vou verificar a hora de criação
        if(!"1".equals(actvDef.getAttribute("createdAll").getValueString()))
        {
            boObject actv = engine.getBoManager().createObject("xwfActivity");
            setActvFields(actv, actvDef, sleepInMills);
//            engine.getBoManager().updateObject(actv);
            actvDef.getBridge("defActivityDepends").add(actv.getBoui());
            long nOccur = actvDef.getAttribute("n_occur").getValueLong(); 
            actvDef.getAttribute("n_occur").setValueLong(nOccur+1);
            engine.getBoManager().updateObject(actvDef);
            engine.getProgramRuntime().getStateAttribute("runningState").setValue("open");
            engine.getBoManager().updateObject(engine.getProgramRuntime());
            result = true;
        }
        return result;
    }
    
    private static boolean create(Date beginDate, int sleepInMills)
    {
        Date now = new Date();
        Calendar cBeginDate = Calendar.getInstance();
        cBeginDate.setTime(beginDate);
        Calendar cNow = Calendar.getInstance();
        if(cNow.equals(cBeginDate) || cNow.after(cBeginDate))
        {
            return true;
        }
        else
        {
            cNow.add(Calendar.MILLISECOND, sleepInMills);
            return !cNow.before(cBeginDate);
        }
    }
    
    private static Date add(Date beginDate, int sleepInMills)
    {
        if(beginDate == null) return null;
        Date now = new Date();
        Calendar cBeginDate = Calendar.getInstance();
        cBeginDate.setTime(beginDate);
        cBeginDate.add(Calendar.MILLISECOND, sleepInMills);
        return cBeginDate.getTime();
    }
    
    public static Date calcNextRun(boObject actvDef, Date ended, int sleepInMills) throws boRuntimeException
    {
        if("1".equals(actvDef.getAttribute("createdAll").getValueString()))
        {
            return null;
        }
        if(!"1".equals(actvDef.getAttribute("type_recursive").getValueString()))
        {
            Calendar c = Calendar.getInstance();
            c.setTime(actvDef.getAttribute("beginDate").getValueDate());
            c.add(Calendar.MILLISECOND, -sleepInMills);
            return c.getTime();
        }
        else
        {
            //horário
            if("hour".equals(actvDef.getAttribute("period").getValueString()))
            {
                return hourCalc(actvDef, ended, sleepInMills);
            }
            //diario
            else if("daily".equals(actvDef.getAttribute("period").getValueString()))
            {
                return dailyCalc(actvDef, ended, sleepInMills);
            }
            //semanal
            else if("weekly".equals(actvDef.getAttribute("period").getValueString()))
            {
                return weeklyCalc(actvDef, ended, sleepInMills);
            }
            //mensal
            else if("monthly".equals(actvDef.getAttribute("period").getValueString()))
            {
                return monthlyCalc(actvDef, ended, sleepInMills);
            }
            //anual
            else
            {
                return yearlyCalc(actvDef, ended, sleepInMills);
            }
        }
//        return null;
    }
    
    private static boolean keepRuning(boObject actvDef, Calendar nextRuntime) throws boRuntimeException
    {
        if("1".equals(actvDef.getAttribute("vig_setOccur").getValueString())) //definido o nº de ocorrencias
        {
            long occur = actvDef.getAttribute("vig_occur").getValueLong();
            long criadas = actvDef.getAttribute("n_occur").getValueLong();
            return criadas < occur;
        }
        else if("1".equals(actvDef.getAttribute("vig_setEndDate").getValueString())) //definida uma data de fim
        {
            Date dFim = actvDef.getAttribute("vig_endDate").getValueDate();
            Calendar c = Calendar.getInstance();
            c.setTime(dFim);
            return !nextRuntime.after(c);
        }
         return true;
    }
    
    private static Date hourCalc(boObject actvDef, Date ended, int sleepInMills) throws boRuntimeException
    {
        if("1".equals(actvDef.getAttribute("hour_setNext").getValueString()))
        {
            //após o término da tarefa criada deve ser criada outra X horas após
            //se não tenho a data do término da anterior ou é pq é a primeira vez ou antão acabei de criar uma nova
            // e por isso não posso calcular a hora da próxima
            if(ended == null)
            {
                if(actvDef.getAttribute("n_occur").getValueLong() <= 0)
                {
                    //ainda não correu
                    Date dBegin = actvDef.getAttribute("vig_beginDate").getValueDate();
                    Calendar c = Calendar.getInstance();
                    c.setTime(dBegin);
                    if(keepRuning(actvDef, c))
                    {
                        c.add(Calendar.MILLISECOND, -sleepInMills);
                        return c.getTime();
                    }
                    else//terminou
                    {
                        actvDef.getAttribute("createdAll").setValueString("1");
                    }
                    return null;
                }
                return null;
            }
            else
            {
                //já correu pelo menos uma vez e acabou de terminar a última
                long periodo = actvDef.getAttribute("hour_next").getValueLong();
                Calendar c = Calendar.getInstance();
                c.setTime(ended);
                c.add(Calendar.HOUR, (int)periodo);
                if(keepRuning(actvDef, c))
                {
                    c.add(Calendar.MILLISECOND, -sleepInMills);
                    return c.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
        }
        else
        {
            //criar uma tarefa de x em x horas
            if(actvDef.getAttribute("n_occur").getValueLong() > 0)
            {
                //já correu pelo menos uma vez
                long periodo = actvDef.getAttribute("hour_period").getValueLong();
                long occur = actvDef.getAttribute("n_occur").getValueLong();
                periodo = periodo < 0 ? 1:periodo;
                //obter a hora do 1º
                Date dBegin = actvDef.getAttribute("vig_beginDate").getValueDate();
                Calendar c = Calendar.getInstance();
                c.setTime(dBegin);
                c.add(Calendar.HOUR, (int)(periodo*occur));
                
                if(keepRuning(actvDef, c))
                {
                    c.add(Calendar.MILLISECOND, -sleepInMills);
                    return c.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }

                return null;
            }
            else
            {
                //ainda não correu
                Date dBegin = actvDef.getAttribute("vig_beginDate").getValueDate();
                Calendar c = Calendar.getInstance();
                c.setTime(dBegin);
                if(keepRuning(actvDef, c))
                {
                    c.add(Calendar.MILLISECOND, -sleepInMills);
                    return c.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
        }
    }
    
    private static Date dailyCalc(boObject actvDef, Date ended, int sleepInMills) throws boRuntimeException
    {
        if("1".equals(actvDef.getAttribute("daily_setNext").getValueString()))
        {
            //após o término da tarefa criada deve ser criada outra X dias após
            //se não tenho a data do término da anterior ou é pq é a primeira vez ou então acabei de criar uma nova
            // e por isso não posso calcular a hora da próxima
            if(ended == null)
            {
                if(actvDef.getAttribute("n_occur").getValueLong() <= 0)
                {
                    //ainda não correu
                    Date dBegin = actvDef.getAttribute("vig_beginDate").getValueDate();
                    Calendar c = Calendar.getInstance();
                    c.setTime(dBegin);
                    if(keepRuning(actvDef, c))
                    {
                        c.add(Calendar.MILLISECOND, -sleepInMills);
                        return c.getTime();
                    }
                    else//terminou
                    {
                        actvDef.getAttribute("createdAll").setValueString("1");
                    }
                    return null;
                }
                return null;
            }
            else
            {
                //já correu pelo menos uma vez e acabou de terminar a última
                long periodo = actvDef.getAttribute("daily_next").getValueLong();
                Calendar c = Calendar.getInstance();
                c.setTime(ended);
                c.add(Calendar.DATE, (int)periodo);
                if(keepRuning(actvDef, c))
                {
                    c.add(Calendar.MILLISECOND, -sleepInMills);
                    return c.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
        }
        else
        {
            //criar uma tarefa de x em x dias
            if(actvDef.getAttribute("n_occur").getValueLong() > 0)
            {
                //já correu pelo menos uma vez
                long periodo = actvDef.getAttribute("daily_period").getValueLong();
                long occur = actvDef.getAttribute("n_occur").getValueLong();
                periodo = periodo <= 0 ? 1:periodo;
                //obter a hora do 1º
                Date dBegin = actvDef.getAttribute("vig_beginDate").getValueDate();
                Calendar c = Calendar.getInstance();
                c.setTime(dBegin);
                c.add(Calendar.DATE, (int)(periodo * occur));
                
                if(keepRuning(actvDef, c))
                {
                    c.add(Calendar.MILLISECOND, -sleepInMills);
                    return c.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }

                return null;
            }
            else
            {
                //ainda não correu
                Date dBegin = actvDef.getAttribute("vig_beginDate").getValueDate();
                Calendar c = Calendar.getInstance();
                c.setTime(dBegin);
                if(keepRuning(actvDef, c))
                {
                    c.add(Calendar.MILLISECOND, -sleepInMills);
                    return c.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
        }
    }

//---------------------------------------- WEEKLY CALC -----------------------------------------------------------------------
    
    private static Date weeklyCalc(boObject actvDef, Date ended, int sleepInMills) throws boRuntimeException
    {
        if("1".equals(actvDef.getAttribute("weekly_setNext").getValueString()))
        {
            //após o término da tarefa criada deve ser criada outra X semanas após
            //se não tenho a data do término da anterior ou é pq é a primeira vez ou então acabei de criar uma nova
            // e por isso não posso calcular a hora da próxima
            if(ended == null)
            {
                if(actvDef.getAttribute("n_occur").getValueLong() <= 0)
                {
                    //ainda não correu
                    Date dBegin = actvDef.getAttribute("vig_beginDate").getValueDate();
                    Calendar c = Calendar.getInstance();
                    c.setTime(dBegin);
                    if(keepRuning(actvDef, c))
                    {
                        c.add(Calendar.MILLISECOND, -sleepInMills);
                        return c.getTime();
                    }
                    else//terminou
                    {
                        actvDef.getAttribute("createdAll").setValueString("1");
                    }
                    return null;
                }
                return null;
            }
            else
            {
                //já correu pelo menos uma vez e acabou de terminar a última
                long periodo = actvDef.getAttribute("weekly_next").getValueLong();
                Calendar c = Calendar.getInstance();
                c.setTime(ended);
                c.add(Calendar.DATE, (int)periodo*7);
                if(keepRuning(actvDef, c))
                {
                    c.add(Calendar.MILLISECOND, -sleepInMills);
                    return c.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
        }
        else
        {
            //criar uma tarefa de x em x semanas
            if(actvDef.getAttribute("n_occur").getValueLong() > 0)
            {
                //já correu pelo menos uma vez
                long periodo = actvDef.getAttribute("daily_period").getValueLong();
                long occur = actvDef.getAttribute("n_occur").getValueLong();
                periodo = periodo <= 0 ? 1:periodo;
                
                //tenho que verificar se não tenho que criar nuenhum ainda na mesma semana
                Date lastCreated = actvDef.getAttribute("lastruntime").getValueDate();
                Calendar cLastCreated = Calendar.getInstance();
                cLastCreated.setTime(lastCreated);
                Calendar validDay = null;
                while((validDay = getNextValidWeekDay(actvDef, cLastCreated)) == null)
                {
                    cLastCreated.add(Calendar.DATE, (int)(periodo*7));
                    gotToMonday(cLastCreated);
                }                
                if(keepRuning(actvDef, validDay))
                {
                    validDay.add(Calendar.MILLISECOND, -sleepInMills);
                    return validDay.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
            else
            {
                //ainda não correu
                long periodo = actvDef.getAttribute("daily_period").getValueLong();
                Date dBegin = actvDef.getAttribute("vig_beginDate").getValueDate();
                Calendar c = Calendar.getInstance();
                c.setTime(dBegin);
                Calendar validDay = null; 
                //vou verificar se na semana currente existe um dia válido
                while((validDay = getNextValidWeekDay(actvDef, c)) == null)
                {
                    c.add(Calendar.DATE, 7);
                    gotToMonday(c);
                }

                if(keepRuning(actvDef, validDay))
                {
                    c.add(Calendar.MILLISECOND, -sleepInMills);
                    return c.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
        }
    }
    
    private static boolean weekValid(boObject actvDef, Calendar c) throws boRuntimeException
    {
        switch(c.get(Calendar.DAY_OF_WEEK))
        {
            case Calendar.MONDAY:
                            return "1".equals(actvDef.getAttribute("weekly_monday").getValueString());
            case Calendar.TUESDAY:
                            return "1".equals(actvDef.getAttribute("weekly_tuesday").getValueString());
            case Calendar.WEDNESDAY:
                            return "1".equals(actvDef.getAttribute("weekly_wednesday").getValueString());
            case Calendar.THURSDAY:
                            return "1".equals(actvDef.getAttribute("weekly_thursday").getValueString());
            case Calendar.FRIDAY:
                            return "1".equals(actvDef.getAttribute("weekly_friday").getValueString());
            case Calendar.SATURDAY:
                            return "1".equals(actvDef.getAttribute("weekly_saturday").getValueString());
            case Calendar.SUNDAY:
                            return "1".equals(actvDef.getAttribute("weekly_friday").getValueString());
            default:
                return false;
        }        
    }
    
    private static Calendar getNextValidWeekDay(boObject actvDef, Calendar c) throws boRuntimeException
    {
        //apartir de c encontrar um dia válido na semana
        Calendar toRet = Calendar.getInstance();
        toRet.setTimeInMillis(c.getTimeInMillis());
        boolean endWeek = false;
        while(!weekValid(actvDef, toRet) && !endWeek)
        {            
            toRet.add(Calendar.DATE, -1);
            if(toRet.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
            {
                endWeek = true;
            }
        }
        return endWeek ? null:toRet;
    }
    
    
    private static void gotToMonday(Calendar c)
    {
        while(c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
        {
            c.add(Calendar.DATE, -1);
        }
    }
    
    private static boolean sameDay(Calendar c, Calendar c1)
    {
        return c.get(Calendar.YEAR) == c1.get(Calendar.YEAR) &&
            c.get(Calendar.MONTH) == c1.get(Calendar.MONTH) &&
            c.get(Calendar.DATE) == c1.get(Calendar.DATE);
    }

//-------------------------------------------------------------------------------------------------
    
    private static Date monthlyCalc(boObject actvDef, Date ended, int sleepInMills) throws boRuntimeException
    {
        if("1".equals(actvDef.getAttribute("monthly_setNext").getValueString()))
        {
            //após o término da tarefa criada deve ser criada outra X semanas após
            //se não tenho a data do término da anterior ou é pq é a primeira vez ou então acabei de criar uma nova
            // e por isso não posso calcular a hora da próxima
            if(ended == null)
            {
                if(actvDef.getAttribute("n_occur").getValueLong() <= 0)
                {
                    //ainda não correu
                    Date dBegin = actvDef.getAttribute("vig_beginDate").getValueDate();
                    Calendar c = Calendar.getInstance();
                    c.setTime(dBegin);
                    if(keepRuning(actvDef, c))
                    {
                        c.add(Calendar.MILLISECOND, -sleepInMills);
                        return c.getTime();
                    }
                    else//terminou
                    {
                        actvDef.getAttribute("createdAll").setValueString("1");
                    }
                    return null;
                }
                return null;
            }
            else
            {
                //já correu pelo menos uma vez e acabou de terminar a última
                long periodo = actvDef.getAttribute("monthly_next").getValueLong();
                Calendar c = Calendar.getInstance();
                c.setTime(ended);
                c.add(Calendar.MONTH, (int)periodo);
                if(keepRuning(actvDef, c))
                {
                    c.add(Calendar.MILLISECOND, -sleepInMills);
                    return c.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
        }
        else if("1".equals(actvDef.getAttribute("monthly_setPeriod").getValueString()))
        {
            //criar uma tarefa de x em x meses
            if(actvDef.getAttribute("n_occur").getValueLong() > 0)
            {
                //já correu pelo menos uma vez
                long month_day = actvDef.getAttribute("monthly_day").getValueLong();
                long periodo = actvDef.getAttribute("monthly_period_day").getValueLong();

                Date lastCreated = actvDef.getAttribute("lastruntime").getValueDate();
                Calendar cLastCreated = Calendar.getInstance();
                cLastCreated.setTime(lastCreated);
                cLastCreated.add(Calendar.MONTH, (int)periodo);
                
                if(keepRuning(actvDef, cLastCreated))
                {
                    return cLastCreated.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
            else
            {
                //ainda não correu
                long month_day = actvDef.getAttribute("monthly_day").getValueLong();
                long periodo = actvDef.getAttribute("monthly_period_day").getValueLong();
                Date dBegin = actvDef.getAttribute("vig_beginDate").getValueDate();
                Calendar c = Calendar.getInstance();
                c.setTime(dBegin);
                c.clear(Calendar.HOUR);c.clear(Calendar.MINUTE);c.clear(Calendar.SECOND);c.clear(Calendar.MILLISECOND);
                
                Calendar validDay = Calendar.getInstance();
                validDay.setTimeInMillis(c.getTimeInMillis());
                validDay.set(Calendar.DATE, (int)month_day);
                
                if(validDay.before(c))
                {
                    //como não pode ser neste mês é no próximo
                    validDay.add(Calendar.MONTH, 1);
                }

                if(keepRuning(actvDef, validDay))
                {
                    c.add(Calendar.MILLISECOND, -sleepInMills);
                    return c.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
        }
        else //valores pre-definidos
        {
            if(actvDef.getAttribute("n_occur").getValueLong() > 0)
            {
                //já correu pelo menos uma vez
                long periodo = actvDef.getAttribute("monthly_period").getValueLong();
                String each = actvDef.getAttribute("monthly_each").getValueString();
                String day = actvDef.getAttribute("monthly_l_day").getValueString();

                Date lastCreated = actvDef.getAttribute("lastruntime").getValueDate();
                Calendar cLastCreated = Calendar.getInstance();
                cLastCreated.setTime(lastCreated);
                cLastCreated.add(Calendar.MONTH, (int)periodo);
                Calendar cAux = getDataPreDefine(actvDef.getEboContext(), each, day, cLastCreated);
                if(keepRuning(actvDef, cAux))
                {
                    return cAux.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
            else
            {//ainda não correu
                long periodo = actvDef.getAttribute("monthly_period").getValueLong();
                String each = actvDef.getAttribute("monthly_each").getValueString();
                String day = actvDef.getAttribute("monthly_l_day").getValueString();
                Date bgD = actvDef.getAttribute("vig_beginDate").getValueDate();
                Calendar cBeginCreated = Calendar.getInstance();
                cBeginCreated.setTime(bgD);
                Calendar cAux = getDataPreDefine(actvDef.getEboContext(), each, day, cBeginCreated);
                if(cAux.before(cBeginCreated))
                {
                    cAux.add(Calendar.MONTH, 1);
                    cAux = getDataPreDefine(actvDef.getEboContext(), each, day, cBeginCreated);
                }
                if(keepRuning(actvDef, cAux))
                {
                    return cAux.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
        }
    }
    
    private static Calendar getDataPreDefine(EboContext ctx, String each, String day, Calendar c) throws boRuntimeException
    {
        //each:
        //first-primeiro(a); second-segundo(a); third-terceiro(a); fourth-quarto(a); last-último(a)
        
        //day:
        //day-;utilDay-;weekendDay-;monday-;tuesday...
        
        if("day".equals(day)) return day(each, c);
        if("utilDay".equals(day)) return utilDay(ctx, each, c);
        if("weekendDay".equals(day)) return weekendDay(each, c);
        if("monday".equals(day)) return workday(each, c, Calendar.MONDAY);
        if("tuesday".equals(day)) return workday(each, c, Calendar.TUESDAY);
        if("wednesday".equals(day)) return workday(each, c, Calendar.WEDNESDAY);
        if("thursday".equals(day)) return workday(each, c, Calendar.THURSDAY);
        if("friday".equals(day)) return workday(each, c, Calendar.FRIDAY);
        if("saturday".equals(day)) return workday(each, c, Calendar.SATURDAY);
        if("sunday".equals(day)) return workday(each, c, Calendar.SUNDAY);
        
        return null;
    }
    private static Calendar day(String each, Calendar c)
    {
        Calendar aux = null;
        aux = Calendar.getInstance();
        aux.setTimeInMillis(c.getTimeInMillis());
        switch(each.hashCode())
        {
            case FIRST_CODE:aux.set(Calendar.DATE, 1);
                            break;
            case SECOND_CODE:aux.set(Calendar.DATE, 2);
                            break;
            case THIRD_CODE:aux.set(Calendar.DATE, 3);
                            break;
            case FOURTH_CODE:aux.set(Calendar.DATE, 4);
                            break;
            case LAST_CODE:aux.set(Calendar.DATE, 1);
                           aux.add(Calendar.MONTH, 1);
                           aux.add(Calendar.DATE, -1);
                           break;
        }
        return aux;
    }
    
    private static Calendar utilDay(EboContext ctx,String each, Calendar c) throws boRuntimeException
    {
        Calendar aux = null;
        int count = 0;
        aux = Calendar.getInstance();
        aux.setTimeInMillis(c.getTimeInMillis());
        switch(each.hashCode())
        {
            case FIRST_CODE:aux.set(Calendar.DATE, 1);
                            if(!DateUtils.isWorkingDay(aux, ctx))
                            {
                                while(!DateUtils.isWorkingDay(c, ctx))
                                {
                                    aux.add(Calendar.DATE, 1);
                                }
                            }
                            return aux;
            case SECOND_CODE:aux.set(Calendar.DATE, 1);
                            while(count < 2)
                            {
                                if(DateUtils.isWorkingDay(aux, ctx))
                                {
                                    count++;
                                    if(count < 2)
                                    {
                                        aux.add(Calendar.DATE, 1);
                                    }
                                }
                            }                           
                            return aux;
            case THIRD_CODE:aux.set(Calendar.DATE, 1);
                            while(count < 3)
                            {
                                if(DateUtils.isWorkingDay(aux, ctx))
                                {
                                    count++;
                                    if(count < 3)
                                    {
                                        aux.add(Calendar.DATE, 1);
                                    }
                                }
                            }                           
                            return aux;
            case FOURTH_CODE:aux.set(Calendar.DATE, 1);
                            while(count < 4)
                            {
                                if(DateUtils.isWorkingDay(aux, ctx))
                                {
                                    count++;
                                    if(count < 4)
                                    {
                                        aux.add(Calendar.DATE, 1);
                                    }
                                }
                            }                           
                            return aux;
            case LAST_CODE:aux.set(Calendar.DATE, 1);
                           aux.add(Calendar.MONTH, 1);
                           aux.add(Calendar.DATE, -1);
                           while(!DateUtils.isWorkingDay(aux, ctx))
                           {
                            aux.add(Calendar.DATE, -1);
                           }
                           return aux;
        }       
        return aux;
    }
    
    private static Calendar weekendDay(String each, Calendar c)
    {
        Calendar aux = null;
        aux = Calendar.getInstance();
        aux.setTimeInMillis(c.getTimeInMillis());
        switch(each.hashCode())
        {
            case FIRST_CODE:aux.set(Calendar.DATE, 1);
                            while(aux.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                               aux.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
                            )
                            {
                                aux.add(Calendar.DATE, 1);
                            }
                            break;
            case SECOND_CODE:aux.set(Calendar.DATE, 1);
                            while(true)
                            {
                                if(aux.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY &&
                                   aux.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )
                                {
                                   if(aux.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                                   {
                                     aux.add(Calendar.DATE, 7);
                                     return aux;
                                   }
                                   else
                                   {
                                     aux.add(Calendar.DATE, 6);
                                     return aux;
                                   }
                                }
                                else
                                {
                                    aux.add(Calendar.DATE, 1);
                                }
                            }
            case THIRD_CODE:aux.set(Calendar.DATE, 1);
                            while(true)
                            {
                                if(aux.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY &&
                                   aux.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )
                                {
                                   if(aux.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                                   {
                                     aux.add(Calendar.DATE, 14);
                                     return aux;
                                   }
                                   else
                                   {
                                     aux.add(Calendar.DATE, 13);
                                     return aux;
                                   }
                                }
                                else
                                {
                                    aux.add(Calendar.DATE, 1);
                                }
                            }
            case FOURTH_CODE:aux.set(Calendar.DATE, 1);
                            while(true)
                            {
                                if(aux.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY &&
                                   aux.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )
                                {
                                   if(aux.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                                   {
                                     aux.add(Calendar.DATE, 21);
                                     return aux;
                                   }
                                   else
                                   {
                                     aux.add(Calendar.DATE, 20);
                                     return aux;
                                   }
                                }
                                else
                                {
                                    aux.add(Calendar.DATE, 1);
                                }
                            }
            case LAST_CODE:aux.set(Calendar.DATE, 1);
                           aux.add(Calendar.MONTH, 1);
                           aux.add(Calendar.DATE, -1);
                           while(aux.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
                           {
                               aux.add(Calendar.DATE, -1);
                           }
                           break;
        }
        return aux;
    }
    
    
    private static Calendar workday(String each, Calendar c, int dayOfWeek)
    {
        Calendar aux = null;
        aux = Calendar.getInstance();
        aux.setTimeInMillis(c.getTimeInMillis());
        switch(each.hashCode())
        {
            case FIRST_CODE:aux.set(Calendar.DATE, 1);
                            while(aux.get(Calendar.DAY_OF_WEEK) != dayOfWeek)
                            {
                                aux.add(Calendar.DATE, 1);
                            }
                            break;
            case SECOND_CODE:aux.set(Calendar.DATE, 1);
                            while(aux.get(Calendar.DAY_OF_WEEK) != dayOfWeek)
                            {
                                aux.add(Calendar.DATE, 7);
                            }
                            break;
            case THIRD_CODE:aux.set(Calendar.DATE, 1);
                            while(aux.get(Calendar.DAY_OF_WEEK) != dayOfWeek)
                            {
                                aux.add(Calendar.DATE, 14);
                            }
                            break;
            case FOURTH_CODE:aux.set(Calendar.DATE, 4);
                            while(aux.get(Calendar.DAY_OF_WEEK) != dayOfWeek)
                            {
                                aux.add(Calendar.DATE, 21);
                            }
                            break;
            case LAST_CODE:aux.set(Calendar.DATE, 1);
                           aux.add(Calendar.MONTH, 1);
                           aux.add(Calendar.DATE, -1);
                           while(aux.get(Calendar.DAY_OF_WEEK) != dayOfWeek)
                           {
                                aux.add(Calendar.DATE, -1);
                           }
                           break;
        }
        return aux;
    }
//---------------------------------------------------------------------------------------------
//----------------------------------------Ano--------------------------------------------------
    private static Date yearlyCalc(boObject actvDef, Date ended, int sleepInMills) throws boRuntimeException
    {
        if("1".equals(actvDef.getAttribute("yearly_setNext").getValueString()))
        {
            //após o término da tarefa criada deve ser criada outra X semanas após
            //se não tenho a data do término da anterior ou é pq é a primeira vez ou então acabei de criar uma nova
            // e por isso não posso calcular a hora da próxima
            if(ended == null)
            {
                if(actvDef.getAttribute("n_occur").getValueLong() <= 0)
                {
                    //ainda não correu
                    Date dBegin = actvDef.getAttribute("vig_beginDate").getValueDate();
                    Calendar c = Calendar.getInstance();
                    c.setTime(dBegin);
                    if(keepRuning(actvDef, c))
                    {
                        c.add(Calendar.MILLISECOND, -sleepInMills);
                        return c.getTime();
                    }
                    else//terminou
                    {
                        actvDef.getAttribute("createdAll").setValueString("1");
                    }
                    return null;
                }
                return null;
            }
            else
            {
                //já correu pelo menos uma vez e acabou de terminar a última
                long periodo = actvDef.getAttribute("yearly_next").getValueLong();
                Calendar c = Calendar.getInstance();
                c.setTime(ended);
                c.add(Calendar.MONTH, (int)periodo);
                if(keepRuning(actvDef, c))
                {
                    c.add(Calendar.MILLISECOND, -sleepInMills);
                    return c.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
        }
        else if("1".equals(actvDef.getAttribute("monthly_setPeriod").getValueString()))
        {
            //criar uma tarefa de x em x meses
            if(actvDef.getAttribute("n_occur").getValueLong() > 0)
            {
                //já correu pelo menos uma vez
                long month_day = actvDef.getAttribute("monthly_day").getValueLong();
                long periodo = actvDef.getAttribute("monthly_period_day").getValueLong();

                Date lastCreated = actvDef.getAttribute("lastruntime").getValueDate();
                Calendar cLastCreated = Calendar.getInstance();
                cLastCreated.setTime(lastCreated);
                cLastCreated.add(Calendar.MONTH, (int)periodo);
                
                if(keepRuning(actvDef, cLastCreated))
                {
                    return cLastCreated.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
            else
            {
                //ainda não correu
                long month_day = actvDef.getAttribute("monthly_day").getValueLong();
                long periodo = actvDef.getAttribute("monthly_period_day").getValueLong();
                Date dBegin = actvDef.getAttribute("vig_beginDate").getValueDate();
                Calendar c = Calendar.getInstance();
                c.setTime(dBegin);
                c.clear(Calendar.HOUR);c.clear(Calendar.MINUTE);c.clear(Calendar.SECOND);c.clear(Calendar.MILLISECOND);
                
                Calendar validDay = Calendar.getInstance();
                validDay.setTimeInMillis(c.getTimeInMillis());
                validDay.set(Calendar.DATE, (int)month_day);
                
                if(validDay.before(c))
                {
                    //como não pode ser neste mês é no próximo
                    validDay.add(Calendar.MONTH, 1);
                }

                if(keepRuning(actvDef, validDay))
                {
                    c.add(Calendar.MILLISECOND, -sleepInMills);
                    return c.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
        }
        else //valores pre-definidos
        {
            if(actvDef.getAttribute("n_occur").getValueLong() > 0)
            {
                //já correu pelo menos uma vez
                long periodo = actvDef.getAttribute("monthly_period").getValueLong();
                String each = actvDef.getAttribute("monthly_each").getValueString();
                String day = actvDef.getAttribute("monthly_l_day").getValueString();

                Date lastCreated = actvDef.getAttribute("lastruntime").getValueDate();
                Calendar cLastCreated = Calendar.getInstance();
                cLastCreated.setTime(lastCreated);
                cLastCreated.add(Calendar.MONTH, (int)periodo);
                Calendar cAux = getDataPreDefine(actvDef.getEboContext(), each, day, cLastCreated);
                if(keepRuning(actvDef, cAux))
                {
                    return cAux.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
            else
            {//ainda não correu
                long periodo = actvDef.getAttribute("monthly_period").getValueLong();
                String each = actvDef.getAttribute("monthly_each").getValueString();
                String day = actvDef.getAttribute("monthly_l_day").getValueString();
                Date bgD = actvDef.getAttribute("vig_beginDate").getValueDate();
                Calendar cBeginCreated = Calendar.getInstance();
                cBeginCreated.setTime(bgD);
                Calendar cAux = getDataPreDefine(actvDef.getEboContext(), each, day, cBeginCreated);
                if(cAux.before(cBeginCreated))
                {
                    cAux.add(Calendar.MONTH, 1);
                    cAux = getDataPreDefine(actvDef.getEboContext(), each, day, cBeginCreated);
                }
                if(keepRuning(actvDef, cAux))
                {
                    return cAux.getTime();
                }
                else//terminou
                {
                    actvDef.getAttribute("createdAll").setValueString("1");
                }
                return null;
            }
        }
    }
    
    public static long endingActivity(xwfEngineGate engine, boObject activity) throws boRuntimeException
    {
        boObject programRuntime = engine.getProgramRuntime();
        boObject defActivity = activity.getAttribute("defActivity").getObject();
        long toRet = -1;
        if(!"1".equals(defActivity.getAttribute("type_recursive").getValueString()))
        {
            if("resume".equals(defActivity.getAttribute("toReturn").getValueString()))
            {
                defActivity.getAttribute("justification").setValueString(
                    (activity.getAttribute("justification").getValueString()));
                notify(engine, activity, true);
                engine.getBoManager().updateObject(defActivity);
            }
            else if("relatorio".equals(defActivity.getAttribute("toReturn").getValueString()))
            {
                //criar uma variável
                //tipo recursivo
                //se fôr um resumo tenho que criar um fill activity para o atributo justification
                //no xwfDefActivity
                xwfBoManager bo_manag = engine.getBoManager();

                bridgeHandler variables = engine.getProgramRuntime().getBridge("variables");
                String reportName = getReportName(variables, "_report");

                boObject varValue = bo_manag.createObject("xwfVarValue");
                varValue.getAttribute("program").setValueLong(bo_manag.getProgBoui());
                varValue.getAttribute("type").setValueLong(0);

                long boui_cls = defActivity.getAttribute("reportType").getValueLong();
                varValue.getAttribute("object").setValueLong(boui_cls);
                varValue.getAttribute("minoccurs").setValueLong(1);
                varValue.getAttribute("maxoccurs").setValueLong(1);
    
                boObject vo = bo_manag.createObject("xwfVariable");
                vo.getAttribute("name").setValueString(reportName);
                vo.getAttribute("label").setValueString("Relatório");
                vo.getAttribute("isClone").setValueString("1");
                vo.getAttribute("mode").setValueLong(1);  
                vo.getAttribute("showMode").setValueLong(0);
                vo.getAttribute("required").setValueString("1");
                vo.getAttribute("value").setValueLong(varValue.getBoui());
                
                boObject fillActv =
                            createActivity(bo_manag, defActivity,"xwfActivityFill", "0", "makeFinalReport", 
                                MessageLocalizer.getMessage("MAKE_FINAL_REPORT"), "Elaborar Relatório Final", "1", 
                               null, "-1", "-1", "false", "false", "true", "true",
                               "true", defActivity.getAttribute("to").getObject(), 
                               null,getDeadLineDate(defActivity),vo,
                               defActivity.getAttribute("performer").getObject()
                            );
                engine.getBoManager().updateObject(fillActv);
                toRet = fillActv.getBoui();
                engine.getProgramRuntime().getStateAttribute("runningState").setValue("open");
            }
            else//end
            {
                notify(engine, activity, true);
            }
        }
        else
        {
            //tipo recursivo
            //se fôr um resumo tenho que criar um fill activity para o atributo justification
            //no xwfDefActivity
            //se fôr um relatório teho que criar um fill para o relatório;
            boolean lastOne = false;
            if("1".equalsIgnoreCase(defActivity.getAttribute("createdAll").getValueString()))
            {
                bridgeHandler brCrtdActv = 
                                defActivity.getBridge("defActivityDepends");
                boBridgeIterator it = brCrtdActv.iterator();
                it.beforeFirst();
                boObject aux;
                String state;
                lastOne = true;
                while(it.next() && lastOne)
                {
                    aux = it.currentRow().getObject();
                    state = aux.getStateAttribute("runningState").getValueString();
                    if(!"cancel".equalsIgnoreCase(state) && !"close".equalsIgnoreCase(state)
                        && activity.getBoui() != aux.getBoui()
                    )
                    {
                        lastOne = false;
                    }
                }
            }
            if("relatorio".equals(defActivity.getAttribute("taskReturn").getValueString()))
            {
                xwfBoManager bo_manag = engine.getBoManager();

                bridgeHandler variables = engine.getProgramRuntime().getBridge("variables");
                String reportName = getReportName(variables, "_report");

                boObject varValue = bo_manag.createObject("xwfVarValue");
                varValue.getAttribute("program").setValueLong(bo_manag.getProgBoui());
                varValue.getAttribute("type").setValueLong(0);

                long boui_cls = defActivity.getAttribute("taskReportType").getValueLong();
                varValue.getAttribute("object").setValueLong(boui_cls);
                varValue.getAttribute("minoccurs").setValueLong(1);
                varValue.getAttribute("maxoccurs").setValueLong(1);
    
                boObject vo = bo_manag.createObject("xwfVariable");
                vo.getAttribute("name").setValueString(reportName);
                vo.getAttribute("label").setValueString("Relatório");
                vo.getAttribute("isClone").setValueString("1");
                vo.getAttribute("mode").setValueLong(1);  
                vo.getAttribute("showMode").setValueLong(0);
                vo.getAttribute("required").setValueString("1");
                vo.getAttribute("value").setValueLong(varValue.getBoui());
                
                boObject fillActv =
                            createActivity(bo_manag, defActivity,"xwfActivityFill", "0", "makeReport", 
                                MessageLocalizer.getMessage("MAKE_REPORT"), "Elaborar Relatório", "1", 
                               null, "-1", "-1", "false", "false", "true", "true",
                               "true", defActivity.getAttribute("to").getObject(), 
                               null,getDeadLineDate(defActivity),vo,
                               defActivity.getAttribute("performer").getObject()
                            );
                engine.getBoManager().updateObject(fillActv);
                toRet = fillActv.getBoui();
            }
            else//end and resume
            {
                notify(engine, activity, false);
                //se o defActivity fôr do tipo cadeia tenho que calcular o próximo
                if(isInChain(defActivity))
                {
                    inChain(defActivity);
                    if("1".equalsIgnoreCase(defActivity.getAttribute("createdAll").getValueString()))
                    {
                        lastOne = true;
                    }
                }
            }
            if(lastOne)
            {
                if("resume".equals(defActivity.getAttribute("toReturn").getValueString()))
                {
                    xwfBoManager bo_manag = engine.getBoManager();
                    
                    bridgeHandler variables = engine.getProgramRuntime().getBridge("variables");
                    String resumeName = getReportName(variables, "_resume");

                    boObject varValue = bo_manag.createObject("xwfVarValue");
                    varValue.getAttribute("program").setValueLong(bo_manag.getProgBoui());
                    varValue.getAttribute("type").setValueLong(7);
                    varValue.getAttribute("minoccurs").setValueLong(1);
                    varValue.getAttribute("maxoccurs").setValueLong(1);
        
                    boObject vo = bo_manag.createObject("xwfVariable");
                    vo.getAttribute("name").setValueString(resumeName);
                    vo.getAttribute("label").setValueString("Resumo das Acções [" + defActivity.getAttribute("label").getValueString() +"]");
                    vo.getAttribute("isClone").setValueString("1");
                    vo.getAttribute("mode").setValueLong(1);  
                    vo.getAttribute("showMode").setValueLong(1);
                    vo.getAttribute("required").setValueString("1");
                    vo.getAttribute("value").setValueLong(varValue.getBoui());
                    
                    boObject fillActv =
                                createActivity(bo_manag, defActivity,"xwfActivityFill", "0", "makeFinalResume", 
                                    MessageLocalizer.getMessage("MAKE_FINAL_RESUME"), "Elaborar Resumo Final", "1", 
                                   null, "-1", "-1", "false", "false", "true", "true",
                                   "true", defActivity.getAttribute("to").getObject(), 
                                   null,getDeadLineDate(defActivity), vo,
                                   defActivity.getAttribute("performer").getObject() 
                                );
                    
                    engine.getBoManager().updateObject(fillActv);
                    toRet = fillActv.getBoui();
                }
                else if("relatorio".equals(defActivity.getAttribute("toReturn").getValueString()))
                {
                    xwfBoManager bo_manag = engine.getBoManager();

                    bridgeHandler variables = engine.getProgramRuntime().getBridge("variables");
                    String reportName = getReportName(variables, "_report");
    
                    boObject varValue = bo_manag.createObject("xwfVarValue");
                    varValue.getAttribute("program").setValueLong(bo_manag.getProgBoui());
                    varValue.getAttribute("type").setValueLong(0);
    
                    long boui_cls = defActivity.getAttribute("reportType").getValueLong();
                    varValue.getAttribute("object").setValueLong(boui_cls);
                    varValue.getAttribute("minoccurs").setValueLong(1);
                    varValue.getAttribute("maxoccurs").setValueLong(1);
        
                    boObject vo = bo_manag.createObject("xwfVariable");
                    vo.getAttribute("name").setValueString(reportName);
                    vo.getAttribute("label").setValueString("Relatório");
                    vo.getAttribute("isClone").setValueString("1");
                    vo.getAttribute("mode").setValueLong(1);  
                    vo.getAttribute("showMode").setValueLong(0);
                    vo.getAttribute("required").setValueString("1");
                    vo.getAttribute("value").setValueLong(varValue.getBoui());
                    
                    boObject fillActv =
                                createActivity(bo_manag, defActivity,"xwfActivityFill", "0", "makeFinalReport", 
                                    MessageLocalizer.getMessage("MAKE_FINAL_REPORT"), "Elaborar Relatório Final", "1", 
                                   null, "-1", "-1", "false", "false", "true", "true",
                                   "true", defActivity.getAttribute("to").getObject(), 
                                   null,getDeadLineDate(defActivity),vo,
                                    defActivity.getAttribute("performer").getObject()
                                );
                    engine.getBoManager().updateObject(fillActv);
                    toRet = fillActv.getBoui();
                }
            }            
        }
        engine.getBoManager().updateObject(engine.getProgramRuntime());
        return toRet;
    }
    public static void notify(xwfEngineGate engine, boObject activity) throws boRuntimeException
    {
        notify(engine, activity, false);
    }
    public static void notify(xwfEngineGate engine, boObject activity, boolean lastOne) throws boRuntimeException
    {
        boObject defActivity = activity.getAttribute("defActivity").getObject();
        boolean recursive = "1".equals(defActivity.getAttribute("type_recursive").getValueString());
        boolean notifyTask = "1".equals(defActivity.getAttribute("taskNotified").getValueString());
        boolean notify = "1".equals(defActivity.getAttribute("notified").getValueString());
        String toReturn = defActivity.getAttribute("toReturn").getValueString();
        String taskReturn = defActivity.getAttribute("taskReturn").getValueString();

        if("xwfActivityFill".equalsIgnoreCase(activity.getName()))
        {
            String name = activity.getAttribute("name").getValueString();
            if("makeFinalReport".equalsIgnoreCase(name) && notify)
            {
                boObject objMessage = getReportObject(activity);
                xwfAnnounceImpl.addAnnounce(MessageLocalizer.getMessage("TASK_COMPLETED_AND_MADE_REQUESTED_FINAL_REPORT") , 
                                                defActivity.getAttribute("assignedQueue").getObject() , 
                                                engine.getProgramRuntime(), 
                                                engine.getBoManager(),true,objMessage);
            }
            else if("makeReport".equalsIgnoreCase(name) && notifyTask)
            {
                boObject objMessage = getReportObject(activity);
                xwfAnnounceImpl.addAnnounce(MessageLocalizer.getMessage("TASK_COMPLETED_AND_MADE_REQUESTED_REPORT") , 
                                                defActivity.getAttribute("assignedQueue").getObject() , 
                                                engine.getProgramRuntime(), 
                                                engine.getBoManager(),true,objMessage);
            }
            else if("makeFinalResume".equalsIgnoreCase(name) && notifyTask)
            {
                String objMessage = getResume(activity);
                xwfAnnounceImpl.addAnnounce(MessageLocalizer.getMessage("TASK_COMPLETED_AND_MADE_REQUESTED_FINAL_RESUME") + objMessage , 
                                                defActivity.getAttribute("assignedQueue").getObject() , 
                                                engine.getProgramRuntime(), 
                                                engine.getBoManager(),true,defActivity);
            }
        }
        else
        {
            if(!recursive)
            {
                if(notify && "end".equalsIgnoreCase(toReturn))
                {
                    xwfAnnounceImpl.addAnnounce(MessageLocalizer.getMessage("TASK_COMPLETED") ,
                                        defActivity.getAttribute("assignedQueue").getObject(), 
                                        engine.getProgramRuntime(), 
                                        engine.getBoManager(),true,activity);
                }
                else if(notify && "resume".equalsIgnoreCase(toReturn))
                {
                    xwfAnnounceImpl.addAnnounce(MessageLocalizer.getMessage("TASK_COMPLETED_AND_MADE_REQUESTED_RESUME"), 
                                                defActivity.getAttribute("assignedQueue").getObject() , 
                                                engine.getProgramRuntime(), 
                                                engine.getBoManager(),true,activity);
                }
            }
            else
            {
                if(lastOne)
                {
                    if("end".equalsIgnoreCase(taskReturn))
                    {
                        xwfAnnounceImpl.addAnnounce(MessageLocalizer.getMessage("TASKS_COMPLETED") , 
                                                defActivity.getAttribute("assignedQueue").getObject(), 
                                                engine.getProgramRuntime(),
                                                engine.getBoManager(),true,activity);
                    }
                }
                else if(notifyTask)
                {
                    if("resume".equalsIgnoreCase(taskReturn))
                    {
                        xwfAnnounceImpl.addAnnounce(MessageLocalizer.getMessage("TASK_COMPLETED_AND_MADE_REQUESTED_RESUME") , 
                                                defActivity.getAttribute("assignedQueue").getObject() , 
                                                engine.getProgramRuntime(), 
                                                engine.getBoManager(),true,activity);
                    }
                    else if("end".equalsIgnoreCase(taskReturn))
                    {
                        xwfAnnounceImpl.addAnnounce(MessageLocalizer.getMessage("TASK_COMPLETED") , 
                                                defActivity.getAttribute("assignedQueue").getObject() , 
                                                engine.getProgramRuntime(), 
                                                engine.getBoManager(),true,activity);
                    }
                }
            }
        }
    }
    
    private static boObject getReportObject(boObject activity) throws boRuntimeException
    {
        boObject objMessage = null;
        boBridgeIterator it = activity.getBridge("variables").iterator();
        it.beforeFirst();
        String varName = null;
        
        while(it.next() && objMessage == null)
        {
            varName = it.currentRow().getObject().getAttribute("name").getValueString();
            if(varName != null && varName.startsWith("_report"))
            {
                boObject varValue = it.currentRow().getObject().getAttribute("value").getObject();
                objMessage = varValue.getAttribute("valueObject").getObject();
            }
        }
        return objMessage;
    }
    
    private static String getResume(boObject activity) throws boRuntimeException
    {
        String objMessage = null;
        boBridgeIterator it = activity.getBridge("variables").iterator();
        it.beforeFirst();
        String varName = null;
        
        while(it.next() && objMessage == null)
        {
            varName = it.currentRow().getObject().getAttribute("name").getValueString();
            if(varName != null && varName.startsWith("_resume"))
            {
                boObject varValue = it.currentRow().getObject().getAttribute("value").getObject();
                objMessage = varValue.getAttribute("valueClob").getValueString();
            }
        }
        return objMessage;
    }
    
    private static String getReportName(bridgeHandler bh, String prefix) throws boRuntimeException
    {
        boBridgeIterator it = bh.iterator();
        it.beforeFirst();
        boObject auxObj;
        String rpNum;
        int toRet = -1, auxRet = -1;
        while(it.next())
        {
            auxObj = it.currentRow().getObject();
            if(auxObj.getAttribute("name").getValueString().startsWith(prefix))
            {
                rpNum = auxObj.getAttribute("name").getValueString().substring(prefix.length());
                try{
                    auxRet = Integer.parseInt(rpNum);
                }catch(Exception e){/*IGNORE*/}
                if(toRet < auxRet)
                {
                    toRet = auxRet;
                }
            }
        }
        if(toRet == -1)
        {
            toRet = 0;
        }
        return prefix + toRet;
    }
    
  public static boObject createActivity(xwfBoManager xwfm, boObject defActv, String type, String state,
    String name, String label, String description, String priority, 
    String process, String nsid, String uniqueSid, String optional,
    String oneShotActivity, String showTask, String showReassign,
    String showWorkFlowArea, boObject assignedTo, String forecastWorkDuration,
    Date deadlinedate, boObject xwfVar, boObject performer
  ) throws boRuntimeException
  {
    boObject b = xwfm.createObject(type);
    xwfm.getProgram().getStateAttribute("runningState").setValue("open");
    b.getStateAttribute("runningState").setValue(state);
    b.getAttribute("program").setValueLong(xwfm.getProgBoui());
    b.getAttribute("done").setValueString("0");
    b.getAttribute("defActivity").setObject(defActv);
    b.getAttribute("controlBy").setValueObject(defActv.getAttribute("assignedQueue").getValueObject());
    b.getAttribute("CREATOR").setValueObject(defActv.getAttribute("CREATOR").getValueObject());
    b.getAttribute("assignedQueue").setObject(assignedTo);
    b.getAttribute("performer").setObject(performer);
       
//    ngtXMLHandler nxml = new ngtXMLHandler(step_node);
    
    b.getAttribute("label").setValueString(label,AttributeHandler.INPUT_FROM_INTERNAL);
    b.getAttribute("name").setValueString(name);
    b.getAttribute("description").setValueString(description);
    b.getAttribute("priority").setValueString(priority);
    
    if(process != null)
      b.getAttribute("process").setValueString(process);

    b.getAttribute("sid").setValueString("-1");

    b.getAttribute("unique_sid").setValueString("-1");
    
    if("true".equals(optional))
      b.getAttribute("optional").setValueString("1");
    else
      b.getAttribute("optional").setValueString("0");
      
    if("true".equals(oneShotActivity))
      b.getAttribute("oneShotActivity").setValueString("1");
    else
      b.getAttribute("oneShotActivity").setValueString("0");
      
    if("true".equals(showTask))
      b.getAttribute("showTask").setValueString("1");
    else
      b.getAttribute("showTask").setValueString("0");
    
    if("true".equals(showReassign))
      b.getAttribute("showReassign").setValueString("1");
    else
      b.getAttribute("showReassign").setValueString("0");
    
    if("true".equals(showWorkFlowArea))
      b.getAttribute("showWorkFlowArea").setValueString("1");
    else
      b.getAttribute("showWorkFlowArea").setValueString("0");
    
    if(forecastWorkDuration != null && !forecastWorkDuration.startsWith(";"))
    {
      double dur = Double.parseDouble(forecastWorkDuration);
      b.getAttribute("forecastWorkDuration").setValueDouble(dur);
    }
     
    if(deadlinedate != null)
    {
        b.getAttribute("deadLineDate").setValueDate(deadlinedate);
    }  
    
//    ngtXMLHandler alerts = nxml.getChildNode("alerts");
//    if(alerts != null && alerts.getNode() != null)
//    {
//      ngtXMLHandler first_alert = alerts.getFirstChild();
//      if(first_alert != null && first_alert.getNode() != null)
//      {
//        String alert_name = first_alert.getText();
//        regAlert(alert_name, deadLine, xwfm.getObject(lpart), nsid);
//      }
//    }

    if(xwfVar != null)
    {
        b.getBridge("variables").add(xwfVar.getBoui());
        xwfm.getProgram().getBridge("variables").add(xwfVar.getBoui());
        xwfStepExec.givePrivileges(assignedTo.getBoui(), xwfVar);
    }
    return b;
  }
  
  private static Date getDeadLineDate(boObject actvDef) throws boRuntimeException
  {
        if(!"1".equals(actvDef.getAttribute("type_recursive").getValueString()))
        {//simples
            if("1".equals(actvDef.getAttribute("setDuration").getValueString()))
            {
                Date bg = actvDef.getAttribute("beginDate").getValueDate();
                String durValue = actvDef.getAttribute("duration").getValueString();
                return DateUtils.sumDurationToDate(durValue, bg);
            }
            else if("1".equals(actvDef.getAttribute("setUtilDays").getValueString()))
            {
                Date bg = actvDef.getAttribute("beginDate").getValueDate();
                long days = actvDef.getAttribute("utilDays").getValueLong();
                return DateUtils.sumToUtilDate(bg,0,0,days, actvDef.getEboContext());
            }
            else if("1".equals(actvDef.getAttribute("setEndDate").getValueString()))
            {
                return actvDef.getAttribute("endDate").getValueDate();
            }
        }
        else
        {//recursivo
            Date vigBegin = actvDef.getAttribute("nextruntime").getValueDate();
            if(vigBegin == null) vigBegin = actvDef.getAttribute("vig_beginDate").getValueDate(); 
            if("1".equals(actvDef.getAttribute("setDuration").getValueString()))
            {
                String durValue = actvDef.getAttribute("duration").getValueString();
                return DateUtils.sumDurationToDate(durValue, vigBegin);
            }
            else if("1".equals(actvDef.getAttribute("setUtilDays").getValueString()))
            {
                long days = actvDef.getAttribute("utilDays").getValueLong();
                return DateUtils.sumToUtilDate(vigBegin,0,0,days, actvDef.getEboContext());
            }
            else if("1".equals(actvDef.getAttribute("setEndDate").getValueString()))
            {
                return actvDef.getAttribute("endDate").getValueDate();
            }
        }
        return null;
  }
  
  public static boolean cancelHiddenWhen(boObject obj)
  {
        try
        {
            if("inactive".equals(obj.getStateAttribute("state").getValueString()) ||
            "finished".equals(obj.getStateAttribute("state").getValueString())
            )
            {
                return true;
            }
        }
        catch (Exception e)
        {
            /*IGNORE*/
        }
        return false;
  } 
 
  public static boolean cancel(boObject obj) throws boRuntimeException
  {
        
        obj.getStateAttribute("state").setValue("inactive");
        obj.update();
        try
        {
            long creatorBoui = obj.getAttribute("CREATOR").getValueLong();
            long performerBoui = obj.getEboContext().getBoSession().getPerformerBoui();
            boObject program = obj.getAttribute("program").getObject();
            xwfBoManager xm = new xwfBoManager(obj.getEboContext(), program);
            if(creatorBoui != performerBoui && performerBoui > 0)
            {
                
                StringBuffer sb = new StringBuffer("O seu Planeamento [");
                sb.append(obj.getAttribute("label").getValueString());
                if(program != null)
                {
                    sb.append(" - ").append(program.getAttribute("label").getValueString());
                }
                sb.append("] foi cancelado pelo utilizador ");
                boObject performer = boObject.getBoManager().loadObject(
                                        obj.getEboContext(), performerBoui);
                sb.append(performer.getAttribute("name").getValueString())
                  .append(" - ").append(performer.getAttribute("id").getValueString())
                  .append(" na data ");
                SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss");
                sb.append(formatter.format(new Date()));
                sb.append(".");
                
                boObject creator = boObject.getBoManager().loadObject(obj.getEboContext(), creatorBoui);

                xwfAnnounceImpl.addAnnounce(sb.toString(), creator, obj, xm, true);
            }
            String xml = program.getAttribute("flow").getValueString();
            if(xml != null && xml.length() > 0)
            {
                //se tiver flow há coisas a fazer
                xwfControlFlow controlFlow = new xwfControlFlow(xm);
                controlFlow.finishedStep("-1");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
  }
  
  public static boolean isInChain(boObject actvDef)
  {
        try
        {
            if(actvDef != null && 
                "1".equals(actvDef.getAttribute("type_recursive").getValueString()))
            {
                if("1".equals(actvDef.getAttribute("hour_setNext").getValueString()) ||
                   "1".equals(actvDef.getAttribute("daily_setNext").getValueString()) ||
                    "1".equals(actvDef.getAttribute("weekly_setNext").getValueString()) ||
                    "1".equals(actvDef.getAttribute("monthly_setNext").getValueString()) ||
                    "1".equals(actvDef.getAttribute("yearly_setNext").getValueString())
                )
                {
                    return true;
                }
            }
        }
        catch (boRuntimeException e)
        {
            /*IGNORE*/
        }
        return false;
  }
  
  public static void inChain(boObject defActivity) throws boRuntimeException
  {
    
    // Set the schedule attributes
    defActivity.getAttribute("nextruntime").setValueDate(calcNextRun( defActivity, new Date(), 30000 ));
    if("1".equals(defActivity.getAttribute("createdAll").getValueString()))
    {
        defActivity.getStateAttribute( "activeStatus" ).setValue( "3" );
    }
    else
    {
        defActivity.getStateAttribute( "activeStatus" ).setValue( "0" );
    }
  }
  
  public static String getProgramLabel(boObject activity) throws boRuntimeException
  {
    if(activity.getAttribute("implements_iESP_SMCE") != null)
    {
        if(!"xwfActivity".equals(activity.getName()) 
            && !"xwfDefActivity".equals(activity.getName()) 
            && !"ESP_Atendimento".equals(activity.getName())
        )
        {
            return activity.getAttribute("label").getValueString();
        }
        boObject program = activity.getAttribute("program").getObject();
        if(program == null) return "";
        boObject interventation = activity.getAttribute("intervention").getObject();
        if(interventation == null)
        {
            boBridgeIterator varit = program.getBridge("variables").iterator();
            varit.beforeFirst();
            boObject xwfVar = null, xwfVarValue = null;
            ArrayList toRet = new ArrayList();
            while(varit.next())
            {
                xwfVar = varit.currentRow().getObject();
                xwfVarValue = xwfVar.getAttribute("value").getObject();
                if(xwfVarValue != null && 
                    "0".equals(xwfVarValue.getAttribute("type").getValueString()))
                {
                    if(xwfVarValue.getAttribute("valueObject").getObject() != null)
                    {
                        if("ESP_PedidoInterv".equalsIgnoreCase(
                            xwfVarValue.getAttribute("valueObject").getObject().getName()))
                        {
                            interventation = xwfVarValue.getAttribute("valueObject").getObject();
                        }
                    }
                }
            }
        }
        
        if(interventation != null)
        {
            if("1".equals(interventation.getAttribute("userCreated").getValueString()))
            {
                return interventation.getAttribute("name").getValueString();
            }
            else
            {
                return interventation.getAttribute("subject").getObject().getAttribute("name").getValueString();
            }
        }
    }
    return "";
  }

  public static String getProgramAssignedQueue(boObject activity) throws boRuntimeException
  {
        if(activity.getAttribute("implements_iESP_SMCE") != null)
        {
            if(!"xwfActivity".equals(activity.getName()) 
                && !"xwfDefActivity".equals(activity.getName()) 
                && !"ESP_Atendimento".equals(activity.getName())
            )
            {
                return activity.getAttribute("label").getValueString();
            }
            boObject program = activity.getAttribute("program").getObject();
            if(program == null) return "";
            boObject interventation = activity.getAttribute("intervention").getObject();
            if(interventation == null)
            {
                boBridgeIterator varit = program.getBridge("variables").iterator();
                varit.beforeFirst();
                boObject xwfVar = null, xwfVarValue = null;
                ArrayList toRet = new ArrayList();
                while(varit.next())
                {
                    xwfVar = varit.currentRow().getObject();
                    xwfVarValue = xwfVar.getAttribute("value").getObject();
                    if(xwfVarValue != null && 
                        "0".equals(xwfVarValue.getAttribute("type").getValueString()))
                    {
                        if(xwfVarValue.getAttribute("valueObject").getObject() != null)
                        {
                            if("ESP_PedidoInterv".equalsIgnoreCase(
                                xwfVarValue.getAttribute("valueObject").getObject().getName()))
                            {
                                boObject interv = xwfVarValue.getAttribute("valueObject").getObject();
                            }
                        }
                    }
                }
            }
            if(interventation != null)
            {
                return interventation.getAttribute("assignedQueue").getValueString();
            }
        }
        return "";
  }
     
}