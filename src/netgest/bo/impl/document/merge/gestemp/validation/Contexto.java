package netgest.bo.impl.document.merge.gestemp.validation;

import java.sql.Connection;

import java.util.ArrayList;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;


public class Contexto {
    private EboContext ctx;
    private ArrayList erros = new ArrayList();

    public Contexto(EboContext ctx) {
        this.ctx = ctx;
    }

    public boObject getUtilizadorXEOObject() throws boRuntimeException {
        long boui = getUtilizadorBoui();

        return boObject.getBoManager().loadObject(ctx, boui);
    }

    public long getUtilizadorBoui() {
        return ctx.getBoSession().getPerformerBoui();
    }

    public String getUtilizadorLogin() throws boRuntimeException {
        return getUtilizadorXEOObject().getAttribute("username").getValueString();
    }

    public Connection getBDLigacao() {
        return ctx.getConnectionData();
    }

    public Connection getBDLigacaoDedicada() {
        return ctx.getDedicatedConnectionData();
    }

    public void addErro(String erro) {
        erros.add(erro);
    }

    public ArrayList getErros() {
        return erros;
    }

    public boObject getXEOObject(long boui) throws boRuntimeException {
        return boObject.getBoManager().loadObject(ctx, boui);
    }

    public boObjectList getXEOObjectList(String boql) throws boRuntimeException {
        return boObjectList.list(this.ctx, boql);
    }
}
