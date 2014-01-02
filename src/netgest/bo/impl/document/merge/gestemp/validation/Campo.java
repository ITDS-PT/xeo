package netgest.bo.impl.document.merge.gestemp.validation;

import java.util.ArrayList;

import netgest.bo.impl.document.merge.gestemp.GtCampo;
import netgest.bo.impl.document.merge.gestemp.GtValue;
import netgest.bo.impl.document.merge.gestemp.Helper;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.system.Logger;


public class Campo {
    private static Logger logger = Logger.getLogger(
            "netgest.bo.impl.document.merge.gestemp.Campo");
    GtCampo campo = null;
    EboContext boctx;

    public Campo(EboContext boctx, GtCampo campo) {
        this.boctx = boctx;
        this.campo = campo;
    }

    public String getNome() {
        return campo.getNome();
    }

    public Object getValor() {
        return (campo.getValue() == null) ? null : campo.getValue().getValue();
    }

    public ArrayList getValores() {
        return (campo.getValue() == null) ? null : campo.getValue().getValues();
    }

    public void setValor(Object fieldValue) throws boRuntimeException {
        GtValue val = null;

        if ("8".equals(campo.getTipo())) {
            try {
                val = Helper.setImg(fieldValue);
            } catch (Exception e) {
                logger.severe("", e);
            } finally {
                if (val == null) {
                    val = new GtValue();
                }
            }
        } else {
            val = new GtValue();
            val.addValue(fieldValue);
        }

        campo.setValue(val);
    }

    public void setValores(ArrayList fieldValues) throws boRuntimeException {
        GtValue val = new GtValue();
        val.addValues(fieldValues);
        campo.setValue(val);
    }
    
    public boolean emUsoNoModelo()
    {
        return campo.referenceByTemplate();
    }
}
