/*Enconding=UTF-8*/
package netgest.bo.utils;

import netgest.bo.*;

import netgest.bo.data.DataSet;

import netgest.bo.def.*;

import netgest.bo.impl.*;
import netgest.bo.localizations.MessageLocalizer;

import netgest.bo.runtime.*;

import netgest.utils.*;

import java.io.IOException;

import java.math.BigDecimal;

import java.nio.*;

import java.sql.*;

import java.util.*;


/**
 *
 * @Company Enlace3
 * @author Luís Eduardo Moscoso Barreira
 * @version 1.0
 * @since
 */
public class boRequest {
    public boRequest() {
    }

    /**
    * Metodo que aceita um pedido de alteração
    *
    * @param obj Pedido de Alteração
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public static void acceptRequest(boObject obj) throws boRuntimeException {
        String action = obj.getAttribute("action").getValueString();
        boObject changedObject = getChangedObject(obj); //obj.getAttribute("changedObject").getObject();

        if ("CHANGE".equals(action)) {
            changedObject.update();
        } else if ("CREATE".equals(action)) {
            if (changedObject.getStateAttribute("stateControl") != null) {
                changedObject.getStateAttribute("stateControl").setValue("active");
            }

            changedObject.update();
        } else if ("DESTROY".equals(action)) {
            if (changedObject.getStateAttribute("stateControl") != null) {
                changedObject.getStateAttribute("stateControl").setValue("inactive");
                changedObject.update();
            } else {
                changedObject.destroy();
            }
        }
    }

    /**
    * Metodo que recusa um pedido de alteração
    *
    * @param obj Pedido de Alteração
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public static void refuseRequest(boObject obj) throws boRuntimeException {
        String action = obj.getAttribute("action").getValueString();
        boObject changedObject = obj.getAttribute("changedObject").getObject(); //getChangedObject(obj);

        if ("CREATE".equals(action)) {
            /* obj.getAttribute("changedObject").setValueObject(null);
             obj.update();
             changedObject.destroy();
             */
        }
    }

    /**
    * Metodo que cancela um pedido de alteração
    *
    * @param obj Pedido de Alteração
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public static void cancelRequest(boObject obj) throws boRuntimeException {
        //devido a não se conseguir apagar objectos
        //ao cancelar o pedido, recusa-se o pedido
        bridgeHandler bh = obj.getBridge("DAO");
        bh.beforeFirst();

        while (bh.next()) {
            boObject objDao = bh.getObject();
            cancelRequest(objDao);
        }

        obj.getStateAttribute("requestState").setValue("deniedAproval");
        ((netgest.bo.impl.states.actionObjectImpl) obj.getStateManager()).activity_CompleteSoft();
        obj.update();

        /*
                bridgeHandler bh= obj.getBridge("RO");
                bh.beforeFirst();
                while(bh.next())
                  bh.remove();

                bh= obj.getBridge("documents");
                bh.beforeFirst();
                while(bh.next())
                  bh.remove();

                bh= obj.getBridge("log");
                bh.beforeFirst();
                while(bh.next()){
                  boObject objLog = bh.getObject();
                  bh.remove();
                  objLog.destroy();
                }


                bh= obj.getBridge("DAO");
                bh.beforeFirst();
                while(bh.next()){
                  boObject objDao = bh.getObject();
                  bh.remove();
                  cancelRequest(objDao);
                }

                obj.destroy();
          */
    }

    /**
    * Mostar as diferenças entre o objecto actual e o refrido no pedido de alteração
    *
    * @param obj Pedido de Alteração
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public static void showDiff(boObject obj) throws boRuntimeException {
        obj.isOkToSave = false;

        boObject changedObject = getChangedObject(obj);

        try {
            String docid = obj.getEboContext().getRequest().getParameter("docid");
            obj.getEboContext().getResponse().sendRedirect("__showDifferences.jsp?Boui=" +
                changedObject.getBoui() + "&boFormSubmitMode=10&docid=" +
                docid);
        } catch (IOException e) {
        }
    }

    /**
    * Retorna o objecto com as alterações indicadas no pedido de alteração
    *
    * @param obj Pedido de alteração
    * @return objecto com as alterações indicadas no pedido de alteração
    * @throws netgest.bo.runtime.boRuntimeException
    */
    public static boObject getChangedObject(boObject obj)
        throws boRuntimeException {
        if ((obj.getAttribute("changedObject") == null) ||
                (obj.getAttribute("changedObject").getValueLong() == 0)) {
            return obj;
        }

        EboContext ctx = obj.getEboContext();
        boObject changedObject = boObject.getBoManager().loadObject(ctx,
                obj.getAttribute("changedObject").getValueLong());

        if ((obj.getStateAttribute("requestState") != null) &&
                (obj.getStateAttribute("requestState").getCurrentState() != null) &&
                (obj.getStateAttribute("requestState").getCurrentState()
                        .getNumericForm() > 1)) {
            return changedObject;
        }

        if (!"CHANGE".equals(obj.getAttribute("action").getValueString())) {
            return changedObject;
        }

        Vector updateQueue = new Vector();

        bridgeHandler bh = obj.getBridge("log");
        int[] ignoreLogs = new int[bh.getRowCount()];

        //apagar bridges de objectos
        bh.beforeFirst();

        while (bh.next()) {
            boObject log = bh.getObject();

            String name = log.getAttribute("attribute").getValueString();
            String action = log.getAttribute("action").getValueString();

            StringTokenizer st = new StringTokenizer(name, ".");

            if (st.countTokens() == 1) {
                continue;
            }

            name = st.nextToken();

            String nameBr = st.nextToken();

            if (!name.equals(nameBr)) {
                continue;
            }

            if (!log.getAttribute("action").getValueString().equalsIgnoreCase("DELETE")) {
                continue;
            }

            long boui = log.getAttribute("objectReference").getValueLong();
            long line = log.getAttribute("line").getValueLong();

            boObject curObj = boObject.getBoManager().loadObject(ctx, boui);
            bridgeHandler curBr = curObj.getBridge(name);
            curBr.moveTo((int) line);
            curBr.remove();

            //remover todos os atributos dessa bridge
            int row = bh.getRow();
            bh.beforeFirst();

            while (bh.next()) {
                log = bh.getObject();

                if ((log.getAttribute("attribute").getValueString().startsWith(name)) &&
                        (log.getAttribute("line").getValueLong() == line) &&
                        (log.getAttribute("objectReference").getValueLong() == boui)) {
                    ignoreLogs[bh.getRow() - 1] = 1;
                }
            }

            bh.moveTo(row);

            if (!changedObject.equals(curObj) && !updateQueue.contains(curObj)) {
                updateQueue.add(curObj);
            }
        }

        //inserir bridges em objectos
        bh.beforeFirst();

        while (bh.next()) {
            boObject log = bh.getObject();

            String name = log.getAttribute("attribute").getValueString();
            String action = log.getAttribute("action").getValueString();

            StringTokenizer st = new StringTokenizer(name, ".");

            if (st.countTokens() == 1) {
                continue;
            }

            name = st.nextToken();

            String nameBr = st.nextToken();

            if (!name.equals(nameBr)) {
                continue;
            }

            if (!log.getAttribute("action").getValueString().equalsIgnoreCase("INSERT")) {
                continue;
            }

            long boui = log.getAttribute("objectReference").getValueLong();
            long line = log.getAttribute("line").getValueLong();

            boObject curObj = boObject.getBoManager().loadObject(ctx, boui);
            bridgeHandler bhBr = curObj.getBridge(name);
            
            long bouiBr = log.getAttribute("value_Long").getValueLong();
            if(bhBr.haveBoui(bouiBr))
              continue;
            
            bhBr.add(log.getAttribute("value_Long").getValueLong());
            bhBr.moveRowTo((int) line);

            ignoreLogs[bh.getRow() - 1] = 1;

            if (!changedObject.equals(curObj) && !updateQueue.contains(curObj)) {
                updateQueue.add(curObj);
            }
        }

        long boui = 0;
        boObject curObj = null;

        //alterar atributos
        bh.beforeFirst();

        while (bh.next()) {
            if (ignoreLogs[bh.getRow() - 1] == 1) {
                continue;
            }

            boObject log = bh.getObject();

            long b = log.getAttribute("objectReference").getValueLong();

            if (b != boui) {
                if ((curObj != null) && !changedObject.equals(curObj) &&
                        !updateQueue.contains(curObj)) {
                    updateQueue.add(curObj);
                }

                boui = b;
                curObj = boObject.getBoManager().loadObject(ctx, boui);
            }

            String name = log.getAttribute("attribute").getValueString();
            String action = log.getAttribute("action").getValueString();
            int line = (int) log.getAttribute("line").getValueLong();
            String type = log.getAttribute("type").getValueString();

            StringTokenizer st = new StringTokenizer(name, ".");

            AttributeHandler att = null;

            if (st.countTokens() > 1) {
                name = st.nextToken();

                String nameBr = st.nextToken();
                bridgeHandler curBr = curObj.getBridge(name);
                curBr.moveTo(line);
                att = curBr.getAttribute(nameBr);
            } else {
                att = curObj.getAttribute(name);
            }

            Object value = null;

            if (!"DELETE".equals(action)) {
                if (type.equalsIgnoreCase("BOOLEAN") ||
                        type.equalsIgnoreCase("CHAR")) {
                    value = log.getAttribute("value_String").getValueObject();
                } else if (type.equalsIgnoreCase("CLOB")) {
                    value = log.getAttribute("value_CLOB").getValueObject();
                } else if (type.equalsIgnoreCase("CURRENCY") ||
                        type.equalsIgnoreCase("NUMBER")) {
                    value = log.getAttribute("value_Long").getValueObject();
                } else if (type.equalsIgnoreCase("DATE") ||
                        type.equalsIgnoreCase("DATETIME") ||
                        type.equalsIgnoreCase("DURATION")) {
                    value = log.getAttribute("value_Date").getValueObject();
                }
            }

            if (att.getDefAttribute().getDbIsTabled()) {
                Vector valVec = new Vector();
                Object[] values = (Object[]) value;

                if (value != null) {
                    for (int i = 0; i < values.length; i++) {
                        valVec.add(values[i]);
                    }
                }

                value = null;

                if (type.equalsIgnoreCase("BOOLEAN") ||
                        type.equalsIgnoreCase("CHAR")) {
                    value = log.getAttribute("value_String").getValueObject();
                } else if (type.equalsIgnoreCase("CLOB")) {
                    value = log.getAttribute("value_CLOB").getValueObject();
                } else if (type.equalsIgnoreCase("CURRENCY") ||
                        type.equalsIgnoreCase("NUMBER")) {
                    value = log.getAttribute("value_Long").getValueObject();
                } else if (type.equalsIgnoreCase("DATE") ||
                        type.equalsIgnoreCase("DATETIME") ||
                        type.equalsIgnoreCase("DURATION")) {
                    value = log.getAttribute("value_Date").getValueObject();
                }

                //apaga-se o valor novo
                if (action.equalsIgnoreCase("INSERT")) {
                    valVec.remove((int) line - 1);
                }
                //quando é um caso de update insere-se o antigo e apaga-se o novo
                else if (action.equalsIgnoreCase("UPDATE")) {
                    valVec.add((int) line - 1, value);
                    valVec.remove((int) line);
                }
                //quando é um caso de delete insere-se o antigo
                else if (action.equalsIgnoreCase("DELETE")) {
                    valVec.add((int) line - 1, value);
                }

                //adicionar o novo array
                if (valVec.size() > 0) {
                    att.setValueObject(valVec.toArray(new Object[valVec.size()]));
                } else {
                    att.setValueObject(null);
                }
            } else {
                att.setValueObject(value);
            }
        }

        if ((curObj != null) && !changedObject.equals(curObj) &&
                !updateQueue.contains(curObj)) {
            updateQueue.add(curObj);
        }

        for (int i = 0; i < updateQueue.size(); i++) {
            changedObject.getUpdateQueue().add((boObject) updateQueue.elementAt(
                    i), boObjectUpdateQueue.MODE_SAVE);
        }

        return changedObject;
    }

    /**
     * Indica os objectos para qual é necessário um pedido de alteração
     *
     * @param obj Objecto a alterar
     * @return Objectos sobre o qual é preciso um pedido de alteração
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public static boObject[] getRequestChangedObjects(boObject obj)
        throws boRuntimeException {
        Vector ret = new Vector();

        Enumeration atts = obj.getAllAttributes().elements();

        while (atts.hasMoreElements()) {
            AttributeHandler attr = (AttributeHandler) atts.nextElement();
            String name = attr.getName();

            if (name.equalsIgnoreCase("PARENT")) {
                continue;
            }

            if ((attr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                    (attr.getDefAttribute().getMaxOccurs() > 1) &&
                    (name.indexOf(".") < 0)) {
                continue;
            } else if (attr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                boObject objValue = attr.getObject();

                if (objValue == null) {
                    continue;
                }

                if (!objValue.getBoDefinition().getBoCanBeOrphan()) {
                    boObject[] changedObj = getRequestChangedObjects(objValue);

                    for (int j = 0; j < changedObj.length; j++) {
                        ret.add(changedObj[j]);
                    }
                }
            }
        }

        if (obj.getBoDefinition().getModifyProtocol() != null) {
            ret.add(obj);
        }

        return (boObject[]) ret.toArray(new boObject[ret.size()]);
    }

    /**
     * Retorna logs de alterações referentes a um pedido de alteração
     * @param ctx Contexto do objecto a alterar
     * @param obj Objecto a alterar
     * @return logs de alteração
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public static boObject[] createRequestData(EboContext ctx, boObject obj)
        throws boRuntimeException {
        Vector ret = new Vector();

        boObject[] logs = boVersioning.createVersionData(ctx, obj, false, false);

        Enumeration atts = obj.getAllAttributes().elements();

        while (atts.hasMoreElements()) {
            AttributeHandler attr = (AttributeHandler) atts.nextElement();
            String name = attr.getName();

            if (name.equalsIgnoreCase("PARENT")) {
                continue;
            }

            if ((attr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                    (attr.getDefAttribute().getMaxOccurs() > 1) &&
                    (name.indexOf(".") < 0)) {
                continue;
            } else if (attr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                boObject objValue = attr.getObject();

                if (objValue == null) {
                    continue;
                }

                if (!objValue.getBoDefinition().getBoCanBeOrphan() &&
                        (objValue.getBoDefinition().getModifyProtocol() == null)) {
                    boObject[] changedObj = createRequestData(ctx, objValue);

                    for (int j = 0; j < changedObj.length; j++) {
                        ret.add(changedObj[j]);
                    }
                }
            }
        }

        for (int i = 0; i < logs.length; i++) {
            logs[i].getAttribute("objectReference").setValueLong(obj.getBoui());
            ret.add(logs[i]);
        }

        return (boObject[]) ret.toArray(new boObject[ret.size()]);
    }

    /**
     * Efectua um pedido de alteração
     * @param bobj Objecto alterado
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public void makeRequest(boObject bobj) throws boRuntimeException {
        Vector saveObjects = new Vector();

        bobj.isOkToSave = false;

        if (bobj.getParameter("requestObjects") == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(bobj.getParameter(
                    "requestObjects"), "-");
        boObject[] requests = new boObject[st.countTokens()];

        for (int i = 0; st.hasMoreTokens(); i++) {
            requests[i] = bobj.getBoManager().loadObject(bobj.getEboContext(),
                    Long.parseLong(st.nextToken()));
        }
        
        for (int i = 0; i < requests.length; i++) 
        {
            if(!requests[i].valid())
            {
               requests[i].p_forceCheck=true;
               bobj.addErrorMessage(MessageLocalizer.getMessage("NON_FILLED_REQUEST"));
               return;
            }
        }
        
        bobj.setUpdateMode(boObject.MODE_VIEW);
        
        Vector protocol = new Vector();

        for (int i = 0; i < requests.length; i++) {
            boObject changedObject = requests[i].getAttribute("changedObject")
                                                .getObject();

            requests[i].getUpdateQueue().remove(changedObject.getBoui());

            if (requests[i].getAttribute("action").getValueString().equals("CREATE")) {
                boObject objReq = requests[i].getAttribute("changedObject")
                                             .getObject();

                if (objReq.getParent() != null) {
                    objReq.getParent().isOkToSave = false;
                }

                objReq.setCheckSecurity(false);
                objReq.isOkToSave = true;

                if (objReq.getStateAttribute("stateControl") != null) {
                    objReq.getStateAttribute("stateControl").setValue("inactive");
                }

                objReq.update();
            }

            if (requests[i].getAttribute("action").getValueString().equals("CHANGE")) {
                bridgeHandler bh = requests[i].getBridge("log");
                bh.beforeFirst();

                while (bh.next()) {
                    boObject log = bh.getObject();

                    if (log.getAttribute("attribute").getValueString().equals("BOUI")) {
                        saveObjects.add(boObject.getBoManager().loadObject(bobj.getEboContext(),
                                log.getAttribute("objectReference")
                                   .getValueLong()));
                    }
                }
            }

            requests[i].getBridge("solicitor").add(bobj.getEboContext()
                                                       .getBoSession()
                                                       .getPerformerBoui());

            requests[i].getStateAttribute("requestState").setValue("needAproval");
            protocol.add(requests[i]);
        }

        boObject principalSolicit = (boObject) protocol.elementAt(0);

        for (int i = 1; i < protocol.size(); i++) {
            boObject protocolObject = (boObject) protocol.elementAt(i);
            principalSolicit.getBridge("DAO").add(protocolObject.getBoui());
            principalSolicit.getUpdateQueue().add(protocolObject,
                boObjectUpdateQueue.MODE_SAVE);
        }

        for (int i = 0; i < saveObjects.size(); i++) {
            ((boObject) saveObjects.elementAt(i)).update();
        }

        principalSolicit.update();
    }
}
