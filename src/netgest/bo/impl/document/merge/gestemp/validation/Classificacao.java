package netgest.bo.impl.document.merge.gestemp.validation;

import java.text.SimpleDateFormat;

import java.util.Date;

import netgest.bo.impl.document.merge.gestemp.presentation.GesDocObj;
import netgest.bo.impl.document.merge.gestemp.presentation.GesDocViewer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;


public class Classificacao {
    private GesDocViewer classificacao = null;
    private EboContext boctx;

    public Classificacao(EboContext boctx, GesDocViewer viewer) {
        classificacao = viewer;
        this.boctx = boctx;
    }

    public ClfAtributo getAtributo(int pos) {
        return new ClfAtributo(boctx,
            (GesDocObj) classificacao.getGroupClassification().get(pos - 1));
    }

    public ClfAtributo getAtributo(String nome) {
        for (int i = 0; i < classificacao.getGroupClassification().size();
                i++) {
            if (nome.equals(
                        ((GesDocObj) classificacao.getGroupClassification().get(i)).getInternalName())) {
                return new ClfAtributo(boctx,
                    (GesDocObj) classificacao.getGroupClassification().get(i));
            }
        }

        return null;
    }

    public void setValor(String nomeAtributo, String value)
        throws boRuntimeException {
        getAtributo(nomeAtributo).setValor(value);
    }

    public void setValor(String nomeAtributo, Date value)
        throws boRuntimeException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        getAtributo(nomeAtributo).setValor(sdf.format(value));
    }

    public String getValor(String nomeAtributo) throws boRuntimeException {
        return getAtributo(nomeAtributo).getValor();
    }
}
