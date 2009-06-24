/*Enconding=UTF-8*/
package netgest.bo.workflow;

import netgest.bo.runtime.*;

import java.util.*;


/**
 * Esta classe destina-se a guardar as mensagens , warnings e erros
 * que o Workflow possa gerar
 * Esta Class nao guarda referencias directas aos objectos.Guarda
 * apenas os BOUI's.
 *
 *
 *
 * Para todos( ou quase todos ) os métodos do Workflow deverá ser
 * passado uma instancia desta classe
 *
 *
 * @author JMF
 * @version 1.0
 * @see WorkFlowMethods
 */
public final class WFInf
{
    public static final Hashtable WKFL_MSG_RESOURCES = new Hashtable();
    public static final int TYPE_INFORMATION = 1;
    public static final int TYPE_WARNING = 2;
    public static final int TYPE_ERROR = 3;
    public static final int OP_CANCOMPLETE = 1;
    public static final String WF_ACTIVITY_CONDITION_CANNOT_EVALUTED = "XWF-001";
    public static final String WF_ACTIVITY_ALREADY_COMPLETE = "XWF-002";
    public static final String WF_ACTIVITY_NOT_RUNNING = "XWF-003";
    public static final String WF_ACTIVITY_UNKNOWN_STATE = "XWF-004";

    static
    {
        Hashtable WKFL_MSG_RESOURCES = new Hashtable();

        WKFL_MSG_RESOURCES.put(
            WF_ACTIVITY_CONDITION_CANNOT_EVALUTED, "A condição nao pode ser executada [:1] ");

        WKFL_MSG_RESOURCES.put(WF_ACTIVITY_ALREADY_COMPLETE, "A actividade ja está completa");

        WKFL_MSG_RESOURCES.put(WF_ACTIVITY_UNKNOWN_STATE, "Actividade com estado desconhecido");
    }

    private ArrayList p_Errors;
    private ArrayList p_Warnings;
    private ArrayList p_Informations;

    public WFInf()
    {
    }

    protected void addWarning(boObject srcObject, int operation, String keyResource, String[] par)
    {
        String mess = ( String ) WKFL_MSG_RESOURCES.get(keyResource);

        if (mess != null)
        {
        }
        else
        {
            mess = "desconhecido";
        }

        p_Warnings.add(new WFInf.Entry(operation, srcObject.getBoui(), mess, TYPE_WARNING));
    }

    protected void addWarning(boObject srcObject, int operation, String keyResource)
    {
        addWarning(srcObject, operation, keyResource, null);
    }

    protected void addError(boObject srcObject, int operation, String keyResource, String[] par)
    {
        String mess = ( String ) WKFL_MSG_RESOURCES.get(keyResource);

        if (mess != null)
        {
        
        }
        else
        {
            mess = "desconhecido";
        }

        p_Errors.add(new WFInf.Entry(operation, srcObject.getBoui(), mess, TYPE_ERROR));
    }

    protected void addError(boObject srcObject, int operation, String keyResource)
    {
        addError(srcObject, operation, keyResource, null);
    }

    public String toString()
    {
        return "";
    }

    private final class Entry
    {
        int p_operation;
        long p_boui;
        String p_message;
        int p_type;

        private Entry(int operation, long boui, String message, int type)
        {
            p_operation = operation;
            p_boui = boui;
            p_message = message;
            p_type = type;
        }
    }
}
