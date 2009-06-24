package netgest.bo.impl.document.merge.gestemp;

import netgest.bo.dochtml.docHTML;

import netgest.bo.presentation.render.elements.cache.Cache;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectUpdateQueue;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.xwf.EngineGate;

import netgest.xwf.common.xwfHelper;

import netgest.xwf.core.xwfMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;


public class Aprovacao {
    //    private static Cache aprovacoes = new Cache("Aprovacoes", 60, 100, 30);
    //
    //    private long actAprovBoui = -1;
    //    public static Aprovacao getAprovacao(docHTML doc,  long boui)
    //    {
    //        if(aprovacoes.get(String.valueOf(doc.getDocIdx())) == null)
    //        {
    //            aprovacoes.put(String.valueOf(doc.getDocIdx()), 
    //            new Aprovacao(doc.getEboContext(), boui), doc.getEboContext().getBoSession().getPerformerBoui());
    //        }
    //        
    //        return (Aprovacao)aprovacoes.get(String.valueOf(doc.getDocIdx()));
    //    }
    //    
    //    public Aprovacao(EboContext boctx, long boui)
    //    {
    //        actAprovBoui = boui;
    //    }
    public static boolean showAproveSign(EboContext boctx, long actvBoui)
        throws boRuntimeException {
        boObject owfAprov = boObject.getBoManager().loadObject(boctx, actvBoui);
        boObject protocolo = owfAprov.getAttribute("protocolo").getObject(); //$NON-NLS-1$
        boObject aprovacao = owfAprov.getAttribute("aprovacao").getObject(); //$NON-NLS-1$
        boObject sendActv = owfAprov.getAttribute("actvEnvio").getObject(); //$NON-NLS-1$
        boObject variable = sendActv.getAttribute("message").getObject(); //$NON-NLS-1$
        boObject value = variable.getAttribute("value").getObject(); //$NON-NLS-1$
        boObject msg = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();

        if ("messageLetter".equals(msg.getName()) || //$NON-NLS-1$
                "messageFax".equals(msg.getName())) { //$NON-NLS-1$
            String lastSeq = getLastSeq(msg);
            boolean alreadySign = alreadySigned(lastSeq, msg);

            if (!alreadySign) {
                return canSign(boctx, protocolo, aprovacao);
            }
        }

        return false;
    }

    private static boolean canSign(EboContext boctx, long actvBoui)
        throws boRuntimeException {
        boObject owfAprov = boObject.getBoManager().loadObject(boctx, actvBoui);
        boObject protocolo = owfAprov.getAttribute("protocolo").getObject(); //$NON-NLS-1$
        boObject aprovacao = owfAprov.getAttribute("aprovacao").getObject(); //$NON-NLS-1$

        return (protocolo != null) ? canSign(boctx, protocolo, aprovacao) : false;
    }

    private static boolean canSign(EboContext boctx, boObject protocolo,
        boObject aprovacao) throws boRuntimeException {
        if (protocolo != null) {
            boBridgeIterator bit = protocolo.getBridge("aprovacoes").iterator(); //$NON-NLS-1$
            boObject aux;
            long lastBoui = -1;

            while (bit.next()) {
                aux = bit.currentRow().getObject();

                if (aux.getBoui() == aprovacao.getBoui()) {
                    return "1".equals(aux.getAttribute("assinar") //$NON-NLS-1$ //$NON-NLS-2$
                                         .getValueString());
                }
            }
        }

        return false;
    }

    public static boolean showSign(EboContext boctx, long actvBoui)
        throws boRuntimeException {
        boObject owfAprov = boObject.getBoManager().loadObject(boctx, actvBoui);
        boObject sendActv = owfAprov.getAttribute("actvEnvio").getObject(); //$NON-NLS-1$
        boObject variable = sendActv.getAttribute("message").getObject(); //$NON-NLS-1$
        boObject value = variable.getAttribute("value").getObject(); //$NON-NLS-1$
        boObject msg = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();
        String lastSeq = getLastSeq(msg);

        return alreadySigned(lastSeq, msg);
    }

    private static boolean alreadySigned(String lastSeq, boObject msg)
        throws boRuntimeException {
        boBridgeIterator bit = msg.getBridge("aprovacoes").iterator(); //$NON-NLS-1$

        while (bit.next()) {
            if (lastSeq.equals(bit.currentRow().getObject()
                                      .getAttribute("groupSeq").getValueString())) { //$NON-NLS-1$
                if ("1".equals(bit.currentRow().getObject() //$NON-NLS-1$
                                      .getAttribute("assinou").getValueString())) { //$NON-NLS-1$
                    return true;
                }
            }
        }

        return false;
    }

    private static boObject getNextAprover(EboContext boctx,
        boObject protocolo, boObject aprovacao) throws boRuntimeException {
        boBridgeIterator bit = protocolo.getBridge("aprovacoes").iterator(); //$NON-NLS-1$
        boObject aux;
        long lastBoui = -1;

        while (bit.next()) {
            aux = bit.currentRow().getObject();

            if (aprovacao == null) {
                return null;
            }

            if (aux.getBoui() == aprovacao.getBoui()) {
                if (bit.next()) {
                    return bit.currentRow().getObject();
                }

                return null;
            }
        }

        return null;
    }

    public static boolean aproveSign(EboContext boctx, long actvBoui)
        throws Exception 
    {
        boObject owfAprov = boObject.getBoManager().loadObject(boctx, actvBoui);
        boObject protocolo = owfAprov.getAttribute("protocolo").getObject(); //$NON-NLS-1$
        boObject aprovacao = owfAprov.getAttribute("aprovacao").getObject(); //$NON-NLS-1$
        boObject sendActv = owfAprov.getAttribute("actvEnvio").getObject(); //$NON-NLS-1$
        boObject variable = sendActv.getAttribute("message").getObject(); //$NON-NLS-1$
        boObject value = variable.getAttribute("value").getObject(); //$NON-NLS-1$
        boObject msg = value.getAttribute("valueObject").getObject(); //$NON-NLS-1$
        Date actualDate = new Date();

        //gerar novamente com assinatura
        GtTemplate.regenerateWsignature(sendActv,
            boctx.getBoSession().getPerformerBoui(), actualDate);

        //vou marcar o resultado desta aprovação
        boObject nextAprov = getNextAprover(boctx, protocolo, aprovacao);
        boObject aprovRslt = setMessageAprovResult(msg, aprovacao,
                boctx.getBoSession().getPerformerBoui(), true, true, null,
                nextAprov == null, actualDate);
        setIntelligentLabel(msg.getEboContext(), sendActv);

        //verificação se existe uma próxima aprovação
        boObject aprovAct = null;

        if (nextAprov != null) {
            aprovAct = getNextAprovActv(boctx, false, sendActv, msg, protocolo,
                    nextAprov);
        }

        owfAprov.getAttribute("aprovState").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
        owfAprov.getAttribute("runningState").setValueString("90"); //$NON-NLS-1$ //$NON-NLS-2$
        owfAprov.getAttribute("endDate").setValueDate(actualDate); //$NON-NLS-1$
        owfAprov.getAttribute("performer").setValueLong(boctx.getBoSession() //$NON-NLS-1$
                                                             .getPerformerBoui());

        if (aprovRslt != null) {
            owfAprov.getUpdateQueue().add(aprovRslt,
                boObjectUpdateQueue.MODE_SAVE_FORCED);
        }

        owfAprov.getUpdateQueue().add(msg, boObjectUpdateQueue.MODE_SAVE_FORCED);
        owfAprov.getUpdateQueue().add(sendActv,
            boObjectUpdateQueue.MODE_SAVE_FORCED);

        if (aprovAct != null) {
            owfAprov.getUpdateQueue().add(aprovAct,
                boObjectUpdateQueue.MODE_SAVE_FORCED);
        }

        owfAprov.update();

        return true;
    }

    public static boObject getNextAprovActv(EboContext boctx,
        boolean startNewSeq, boObject xwfActivitySend, boObject msg,
        boObject protocolo, boObject aprovacao) throws boRuntimeException {
        boObject aprovActv = boObject.getBoManager().createObject(boctx,
                "OWFactivityAprovTemp"); //$NON-NLS-1$
        aprovActv.getAttribute("actvEnvio").setObject(xwfActivitySend); //$NON-NLS-1$

        //protocolo
        aprovActv.getAttribute("protocolo").setObject(protocolo); //$NON-NLS-1$

        //aprovacao
        aprovActv.getAttribute("aprovacao").setObject(aprovacao); //$NON-NLS-1$

        //aprovState
        aprovActv.getAttribute("aprovState").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$

        //assignar a 
        aprovActv.getAttribute("assignedQueue").setObject(aprovacao.getAttribute( //$NON-NLS-1$
                "assignedQueue").getObject()); //$NON-NLS-1$

        //name
        aprovActv.getAttribute("name").setValueString(Messages.getString("Aprovacao.41")); //$NON-NLS-1$ //$NON-NLS-2$

        //        aprovActv.getStateAttribute("runningState").setValueString("0");
        xwfActivitySend.getUpdateQueue().add(aprovActv,
            boObjectUpdateQueue.MODE_SAVE_FORCED);

        //AprovacaoResult
        boObject aprovResult = boObject.getBoManager().createObject(xwfActivitySend.getEboContext(),
                "AprovacaoResult"); //$NON-NLS-1$
        String seq = null;

        if (startNewSeq) {
            seq = getNewSeq(msg);
        } else {
            seq = getLastSeq(msg);
        }

        aprovResult.getAttribute("groupSeq").setValueString(seq); //$NON-NLS-1$
        aprovResult.getAttribute("assignedQueue").setObject(aprovacao.getAttribute( //$NON-NLS-1$
                "assignedQueue").getObject()); //$NON-NLS-1$
        aprovResult.getAttribute("envioAprov").setValueDate(new Date()); //$NON-NLS-1$
        aprovResult.getAttribute("aprovState").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
        msg.getBridge("aprovacoes").add(aprovResult.getBoui()); //$NON-NLS-1$

        //tenho que dar permissão a quem vai aprovar para vêr a mensagem e actividade
        giveAprovadorPermission(msg,
            aprovacao.getAttribute("assignedQueue").getValueLong()); //$NON-NLS-1$
        giveAprovadorPermission(xwfActivitySend,
            aprovacao.getAttribute("assignedQueue").getValueLong()); //$NON-NLS-1$

        return aprovActv;
    }

    public static boolean aprove(EboContext boctx, long actvBoui)
        throws boRuntimeException {
        boObject owfAprov = boObject.getBoManager().loadObject(boctx, actvBoui);
        boObject protocolo = owfAprov.getAttribute("protocolo").getObject(); //$NON-NLS-1$
        boObject aprovacao = owfAprov.getAttribute("aprovacao").getObject(); //$NON-NLS-1$
        boObject sendActv = owfAprov.getAttribute("actvEnvio").getObject(); //$NON-NLS-1$
        boObject variable = sendActv.getAttribute("message").getObject(); //$NON-NLS-1$
        boObject value = variable.getAttribute("value").getObject(); //$NON-NLS-1$
        boObject msg = value.getAttribute("valueObject").getObject(); //$NON-NLS-1$
        Date actualDate = new Date();

        //vou marcar o resultado desta aprovação
        boObject nextAprov = (protocolo != null)
            ? getNextAprover(boctx, protocolo, aprovacao) : null;
        boObject aprovRslt = setMessageAprovResult(msg, aprovacao,
                boctx.getBoSession().getPerformerBoui(), true, false, null,
                nextAprov == null, actualDate);
        setIntelligentLabel(msg.getEboContext(), sendActv);

        //verificação se existe uma próxima aprovação
        boObject aprovAct = null;

        if (nextAprov != null) {
            aprovAct = getNextAprovActv(boctx, false, sendActv, msg, protocolo,
                    nextAprov);
        }

        owfAprov.getAttribute("aprovState").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
        owfAprov.getAttribute("runningState").setValueString("90"); //$NON-NLS-1$ //$NON-NLS-2$
        owfAprov.getAttribute("endDate").setValueDate(actualDate); //$NON-NLS-1$
        owfAprov.getAttribute("performer").setValueLong(boctx.getBoSession() //$NON-NLS-1$
                                                             .getPerformerBoui());

        if (aprovRslt != null) {
            owfAprov.getUpdateQueue().add(aprovRslt,
                boObjectUpdateQueue.MODE_SAVE_FORCED);
        }

        owfAprov.getUpdateQueue().add(msg, boObjectUpdateQueue.MODE_SAVE_FORCED);
        owfAprov.getUpdateQueue().add(sendActv,
            boObjectUpdateQueue.MODE_SAVE_FORCED);

        if (aprovAct != null) {
            owfAprov.getUpdateQueue().add(aprovAct,
                boObjectUpdateQueue.MODE_SAVE_FORCED);
        }

        owfAprov.update();

        return true;
    }

    public static boolean notAproved(EboContext boctx, long actvBoui,
        String reason) throws boRuntimeException {
        boObject owfAprov = boObject.getBoManager().loadObject(boctx, actvBoui);
        boObject protocolo = owfAprov.getAttribute("protocolo").getObject(); //$NON-NLS-1$
        boObject aprovacao = owfAprov.getAttribute("aprovacao").getObject(); //$NON-NLS-1$
        boObject sendActv = owfAprov.getAttribute("actvEnvio").getObject(); //$NON-NLS-1$
        boObject variable = sendActv.getAttribute("message").getObject(); //$NON-NLS-1$
        boObject value = variable.getAttribute("value").getObject(); //$NON-NLS-1$
        boObject msg = value.getAttribute("valueObject").getObject(); //$NON-NLS-1$
        Date actualDate = new Date();

        //vou marcar o resultado desta aprovação
        boObject nextAprov = (protocolo != null)
            ? getNextAprover(boctx, protocolo, aprovacao) : null;
        boObject aprovRslt = setMessageAprovResult(msg, aprovacao,
                boctx.getBoSession().getPerformerBoui(), false, false, reason,
                nextAprov == null, actualDate);
        setIntelligentLabel(msg.getEboContext(), sendActv);

        owfAprov.getAttribute("aprovState").setValueString("2"); //$NON-NLS-1$ //$NON-NLS-2$
        owfAprov.getAttribute("runningState").setValueString("90"); //$NON-NLS-1$ //$NON-NLS-2$
        owfAprov.getAttribute("endDate").setValueDate(actualDate); //$NON-NLS-1$
        owfAprov.getAttribute("performer").setValueLong(boctx.getBoSession() //$NON-NLS-1$
                                                             .getPerformerBoui());
        owfAprov.getAttribute("justification").setValueString(reason); //$NON-NLS-1$

        if (aprovRslt != null) {
            owfAprov.getUpdateQueue().add(aprovRslt,
                boObjectUpdateQueue.MODE_SAVE_FORCED);
        }

        owfAprov.getUpdateQueue().add(msg, boObjectUpdateQueue.MODE_SAVE_FORCED);
        owfAprov.getUpdateQueue().add(sendActv,
            boObjectUpdateQueue.MODE_SAVE_FORCED);
        owfAprov.update();

        return true;
    }

    private static boObject setMessageAprovResult(boObject msg,
        boObject aprovacao, long performer, boolean aproved, boolean assinou,
        String reason, boolean lastOne, Date actualDate)
        throws boRuntimeException {
        String lastSeq = getLastSeq(msg);
        boBridgeIterator bit = msg.getBridge("aprovacoes").iterator(); //$NON-NLS-1$
        bit.beforeFirst();

        boObject aux = null;
        boolean found = false;
        boObject toRet = null;

        while (bit.next() && !found) {
            aux = bit.currentRow().getObject();

            if (lastSeq.equals(aux.getAttribute("groupSeq").getValueString()) && //$NON-NLS-1$
                    ((aprovacao == null) ||
                    (aprovacao.getAttribute("assignedQueue").getValueLong() == aux.getAttribute( //$NON-NLS-1$
                        Messages.getString("Aprovacao.80")).getValueLong()))) { //$NON-NLS-1$
                found = true;

                if (!aproved) {
                    aux.getAttribute("aprovState").setValueString("2"); //$NON-NLS-1$ //$NON-NLS-2$
                    aux.getAttribute("justification").setValueString(reason); //$NON-NLS-1$
                    aux.getAttribute("assinou").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    aux.getAttribute("aprovState").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$

                    if (assinou) {
                        aux.getAttribute("assinou").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        aux.getAttribute("assinou").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }

                aux.getAttribute("user").setValueLong(performer); //$NON-NLS-1$
                aux.getAttribute("aprovDate").setValueDate(actualDate); //$NON-NLS-1$
                toRet = aux;
            }
        }

        if (!aproved) {
            msg.getAttribute("aprovState").setValueString("3"); //$NON-NLS-1$ //$NON-NLS-2$
        } else if (lastOne) {
            msg.getAttribute("aprovState").setValueString("2"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return toRet;
    }

    private static void giveAprovadorPermission(boObject obj, long aprovBoui)
        throws boRuntimeException {
        if ((obj != null) && (aprovBoui > -1)) {
            bridgeHandler bh = obj.getBridge("aprovadores"); //$NON-NLS-1$
            bh.beforeFirst();

            if (!bh.haveBoui(aprovBoui)) {
                bh.add(aprovBoui);
            }
        }
    }

    /*
     * SEQUENCIA
     */
    public static String getNewSeq(boObject message) throws boRuntimeException {
        EboContext boctx = message.getEboContext();
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            Connection con = boctx.getConnectionData();
            pst = con.prepareStatement(
                    "Select max(to_number(groupSeq)) from aprovacaoResult a, message$aprovacoes m where m.parent$ = ? and a.boui = m.child$"); //$NON-NLS-1$
            pst.setLong(1, message.getBoui());
            rs = pst.executeQuery();

            int key = 1;

            if (rs.next()) {
                key = rs.getInt(1) + 1;
            }

            return padding(2, key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new boRuntimeException("", //$NON-NLS-1$
                Messages.getString("Aprovacao.101"), e); //$NON-NLS-1$
        } finally {
        }
    }

    public static String getLastSeq(boObject message) throws boRuntimeException {
        EboContext boctx = message.getEboContext();
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            Connection con = boctx.getConnectionData();
            pst = con.prepareStatement(
                    "Select max(to_number(groupSeq)) from aprovacaoResult a, message$aprovacoes m where m.parent$ = ? and a.boui = m.child$"); //$NON-NLS-1$
            pst.setLong(1, message.getBoui());
            rs = pst.executeQuery();

            int key = 1;

            if (rs.next()) {
                key = rs.getInt(1);
            }

            return padding(2, key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new boRuntimeException("", //$NON-NLS-1$
                Messages.getString("Aprovacao.104"), e); //$NON-NLS-1$
        } finally {
        }
    }

    private static String padding(int n, long data) {
        String s = new String();
        int l = String.valueOf(data).length();

        if (n >= l) {
            for (int i = 0; i < (n - l); i++) {
                s += "0"; //$NON-NLS-1$
            }
        }

        s += String.valueOf(data);

        return s;
    }

    public static void setIntelligentLabel(EboContext boctx, boObject activity)
        throws boRuntimeException {
        if (activity != null) {
            StringBuffer label = new StringBuffer();
            AttributeHandler attrLabel = activity.getAttribute("label"); //$NON-NLS-1$
            String activityName = activity.getName();
            String close = null;
            Date dtdoc = null;

            if ("xwfActivitySend".equals(activityName) && //$NON-NLS-1$
                    (attrLabel.getInputType() != AttributeHandler.INPUT_FROM_USER)) {
                boObject attr = activity.getAttribute("message").getObject(); //$NON-NLS-1$
                boObject value = attr.getAttribute("value").getObject(); //$NON-NLS-1$
                boObject message = value.getAttribute("valueObject").getObject(); //$NON-NLS-1$

                if (message != null) {
                    String aprovState = message.getAttribute("aprovState") //$NON-NLS-1$
                                               .getValueString();
                    label.append(xwfMessage.getPreferedMediaLabel(message));
                    close = activity.getStateAttribute("runningState") //$NON-NLS-1$
                                    .getValueString();

                    String preferedMedia = message.getAttribute("preferedMedia") //$NON-NLS-1$
                                                  .getValueString();

                    if ("1".equals(aprovState)) { //$NON-NLS-1$
                        label.append(Messages.getString("Aprovacao.115")); //$NON-NLS-1$
                    } else if ("2".equals(aprovState)) { //$NON-NLS-1$
                        if ("Letter".equals(preferedMedia) || //$NON-NLS-1$
                                "Prefered".equals(preferedMedia)) { //$NON-NLS-1$
                            label.append(Messages.getString("Aprovacao.119")); //$NON-NLS-1$
                        } else {
                            label.append(Messages.getString("Aprovacao.120")); //$NON-NLS-1$
                        }
                    } else if ("3".equals(aprovState)) { //$NON-NLS-1$
                        if ("Letter".equals(preferedMedia) || //$NON-NLS-1$
                                "Prefered".equals(preferedMedia)) { //$NON-NLS-1$
                            label.append(Messages.getString("Aprovacao.124")); //$NON-NLS-1$
                        } else {
                            label.append(Messages.getString("Aprovacao.125")); //$NON-NLS-1$
                        }
                    } else {
                        label.append(Messages.getString("Aprovacao.126")); //$NON-NLS-1$
                    }

                    label.append(" [ ") //$NON-NLS-1$
                         .append(message.getAttribute("name").getValueString()) //$NON-NLS-1$
                         .append(" ] "); //$NON-NLS-1$

                    bridgeHandler to = message.getBridge("to"); //$NON-NLS-1$
                    to.beforeFirst();

                    if (to.getRecordCount() > 0) {
                        label.append(Messages.getString("Aprovacao.131")); //$NON-NLS-1$

                        boObject toObject = null;
                        int count = 0;

                        while (to.next() && (count < 2)) {
                            if (count != 0) {
                                label.append(" , "); //$NON-NLS-1$
                            }

                            toObject = to.getObject();
                            label.append(toObject.getAttribute("name") //$NON-NLS-1$
                                                 .getValueString());
                            count++;
                        }

                        if (to.next()) {
                            label.append(" ..."); //$NON-NLS-1$
                        }
                    }

                    String motivo = null;
                    dtdoc = message.getAttribute("dtdoc").getValueDate(); //$NON-NLS-1$

                    if (dtdoc != null) {
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
                        label.append(Messages.getString("Aprovacao.137")); //$NON-NLS-1$
                        label.append(df.format(dtdoc));
                    }

                    activity.getAttribute("label").setValueString(label.toString(), //$NON-NLS-1$
                        AttributeHandler.INPUT_FROM_INTERNAL);
                }
            }
        }
    }

    public static boolean hasProtocoloAprovacao(EngineGate engine,
        boObject runtimeActivity) throws boRuntimeException {
        boObject msg = xwfHelper.getMasterObject(engine, runtimeActivity);

        if (msg.getAttribute("usedTemplate").getValueLong() > 0) { //$NON-NLS-1$
            boObject temp = msg.getAttribute("usedTemplate").getObject(); //$NON-NLS-1$

            if (temp.getAttribute("protocolo").getValueLong() > 0) { //$NON-NLS-1$
                return true;
            }
        }

        return false;
    }

    public static boolean userAproverChoice(EngineGate engine,
        boObject runtimeActivity) throws boRuntimeException {
        boObject msg = xwfHelper.getMasterObject(engine, runtimeActivity);

        if (msg.getAttribute("usedTemplate").getValueLong() > 0) { //$NON-NLS-1$
            boObject template = msg.getAttribute("usedTemplate").getObject(); //$NON-NLS-1$

            return (template.getAttribute("protocolo").getValueLong() <= 0) && //$NON-NLS-1$
            "1".equals(template.getAttribute("protocoloUtilizador") //$NON-NLS-1$ //$NON-NLS-2$
                               .getValueString());
        }

        return false;
    }

    public static boObject getAprovActvBy(EboContext boctx,
        boObject xwfActivitySend, boObject msg, String aprovedBy)
        throws boRuntimeException {
        long aprovBoui = Long.parseLong(aprovedBy);

        if (aprovBoui > 0) {
            boObject aproverObj = boObject.getBoManager().loadObject(boctx,
                    aprovBoui);
            boObject aprovActv = boObject.getBoManager().createObject(boctx,
                    "OWFactivityAprovTemp"); //$NON-NLS-1$
            aprovActv.getAttribute("actvEnvio").setObject(xwfActivitySend); //$NON-NLS-1$

            //aprovState
            aprovActv.getAttribute("aprovState").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$

            //assignar a 
            aprovActv.getAttribute("assignedQueue").setObject(aproverObj); //$NON-NLS-1$

            //name
            aprovActv.getAttribute("name").setValueString(Messages.getString("Aprovacao.153")); //$NON-NLS-1$ //$NON-NLS-2$

            xwfActivitySend.getUpdateQueue().add(aprovActv,
                boObjectUpdateQueue.MODE_SAVE_FORCED);

            //AprovacaoResult
            boObject aprovResult = boObject.getBoManager().createObject(xwfActivitySend.getEboContext(),
                    "AprovacaoResult"); //$NON-NLS-1$
            String seq = getNewSeq(msg);
            aprovResult.getAttribute("groupSeq").setValueString(seq); //$NON-NLS-1$
            aprovResult.getAttribute("assignedQueue").setObject(aproverObj); //$NON-NLS-1$
            aprovResult.getAttribute("envioAprov").setValueDate(new Date()); //$NON-NLS-1$
            aprovResult.getAttribute("aprovState").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
            msg.getBridge("aprovacoes").add(aprovResult.getBoui()); //$NON-NLS-1$

            //tenho que dar permissão a quem vai aprovar para vêr a mensagem e actividade
            giveAprovadorPermission(msg, aproverObj.getBoui());
            giveAprovadorPermission(xwfActivitySend, aproverObj.getBoui());

            return aprovActv;
        }

        return null;
    }

    public static boolean onBeforeSaveAprovacao(boObject aprovacao)
        throws boRuntimeException {
        if ("1".equals(aprovacao.getAttribute("assinar").getValueString())) { //$NON-NLS-1$ //$NON-NLS-2$
            boObject aprover = aprovacao.getAttribute("assignedQueue") //$NON-NLS-1$
                                        .getObject();

            if ((aprover != null) &&
                    ("Ebo_Perf".equals(aprover.getName()) || //$NON-NLS-1$
                    "Ebo_Perf".equals(aprover.getBoDefinition().getBoSuperBo()) || //$NON-NLS-1$
                    "dmUser".equals(aprover.getBoDefinition().getBoSuperBo()) //$NON-NLS-1$
                    )
                ) {
                if (!Assinaturas.hasAssinatura(aprovacao.getEboContext(),
                            aprover.getBoui())) {
                    aprovacao.addErrorMessage(aprovacao.getAttribute(
                            "assignedQueue"), //$NON-NLS-1$
                        Messages.getString("Aprovacao.168")); //$NON-NLS-1$

                    return false;
                }
            }
        }

        return true;
    }

    public static boolean onBeforeSaveProtocolo(boObject protocolo)
        throws boRuntimeException {
        boBridgeIterator bit = protocolo.getBridge("aprovacoes").iterator(); //$NON-NLS-1$
        bit.beforeFirst();

        boObject aprovacao;
        ArrayList aprovers = new ArrayList();
        String aux;
        int signers = 0;

        while (bit.next()) {
            aprovacao = bit.currentRow().getObject();
            aux = aprovacao.getAttribute("assignedQueue").getValueString(); //$NON-NLS-1$
            signers = "1".equals(aprovacao.getAttribute("assinar") //$NON-NLS-1$ //$NON-NLS-2$
                                          .getValueString()) ? (signers + 1)
                                                             : signers;

            if (aprovers.contains(aux)) {
                protocolo.addErrorMessage(
                    Messages.getString("Aprovacao.173")); //$NON-NLS-1$

                return false;
            } else {
                aprovers.add(aux);
            }
        }

        if (signers > 1) {
            protocolo.addErrorMessage(
                Messages.getString("Aprovacao.174")); //$NON-NLS-1$

            return false;
        }

        return true;
    }
    public static boolean aprovedMsg(EngineGate engine,boObject runtimeActivity) throws boRuntimeException
    {
        boObject msg = xwfHelper.getMasterObject(engine,runtimeActivity);
        return "2".equals(msg.getAttribute("aprovState").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public static boolean inAproveMsg(EngineGate engine,boObject runtimeActivity) throws boRuntimeException
    {
        boObject msg = xwfHelper.getMasterObject(engine,runtimeActivity);
        return "1".equals(msg.getAttribute("aprovState").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
    }
    public static boolean notAproveMsg(EngineGate engine,boObject runtimeActivity) throws boRuntimeException
    {
        boObject msg = xwfHelper.getMasterObject(engine,runtimeActivity);
        return "3".equals(msg.getAttribute("aprovState").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
