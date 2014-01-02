package netgest.bo.impl.document.merge.gestemp.validation;

import java.math.BigDecimal;

import java.util.ArrayList;

import netgest.bo.impl.document.merge.gestemp.GtCampoFormula;
import netgest.bo.impl.document.merge.gestemp.GtCampoNFormula;
import netgest.bo.impl.document.merge.gestemp.GtCampoNJava;
import netgest.bo.impl.document.merge.gestemp.GtCampoNObjecto;
import netgest.bo.impl.document.merge.gestemp.GtValue;
import netgest.bo.impl.document.merge.gestemp.Helper;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;


public class CampoListaObj extends Campo {
    int pos = -1;

    public CampoListaObj(EboContext boctx, GtCampoFormula campo, int pos) {
        super(boctx, campo);
        this.pos = pos;
    }

    public CampoListaObj(EboContext boctx, GtCampoNFormula campo, int pos) {
        super(boctx, campo);
        this.pos = pos;
    }

    public CampoListaObj(EboContext boctx, GtCampoNJava campo, int pos) {
        super(boctx, campo);
        this.pos = pos;
    }

    public CampoListaObj(EboContext boctx, GtCampoNObjecto campo, int pos) {
        super(boctx, campo);
        this.pos = pos;
    }

    public Object getValue() {
        if (campo.getValue() != null) {
            ArrayList r = campo.getValue().getValues();

            if ((r != null) && (r.size() > pos)) {
                return r.get(pos);
            }
        }

        return null;
    }

    public boObject getValorXEOObject() throws boRuntimeException {
        if (campo.getValue() != null) {
            ArrayList r = campo.getValue().getValues();

            if ((r != null) && (r.size() > pos)) {
                return boObject.getBoManager().loadObject(boctx,
                    ((Long) r.get(pos)).longValue());
            }
        }

        return null;
    }

    public void setValor(Object fieldValue) throws boRuntimeException {
        ArrayList r = null;

        if ((campo.getValue() == null) ||
                (campo.getValue().getValues() == null)) {
            r = new ArrayList();
        } else {
            r = campo.getValue().getValues();
        }

        if (fieldValue instanceof boObject) {
            if (r.size() > pos) {
                r.set(pos, new Long(((boObject) fieldValue).getBoui()));
            } else {
                r.add(new Long(((boObject) fieldValue).getBoui()));
            }
        } else if (fieldValue instanceof Long) {
            if (r.size() > pos) {
                r.set(pos, fieldValue);
            } else {
                r.add(fieldValue);
            }
        } else if (fieldValue instanceof BigDecimal) {
            if (r.size() > pos) {
                r.set(pos, new Long(((BigDecimal) fieldValue).longValue()));
            } else {
                r.add(new Long(((BigDecimal) fieldValue).longValue()));
            }
        } else {
            GtValue val = null;

            if ("8".equals(campo.getTipo())) {
                try {
                    val = Helper.setImg(fieldValue);

                    if (r.size() > pos) {
                        r.set(pos, val.getValue());
                    } else {
                        r.add(val.getValue());
                    }
                } catch (Exception e) {
                } finally {
                    if (r.size() > pos) {
                        r.set(pos, null);
                    } else {
                        r.add(null);
                    }
                }
            } else {
                if (r.size() > pos) {
                    r.set(pos, fieldValue);
                } else {
                    r.add(fieldValue);
                }
            }
        }

        GtValue v = new GtValue();
        v.addValues(r);
        campo.setValue(v);
    }
}
