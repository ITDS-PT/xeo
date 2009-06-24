package netgest.bo.impl.document.merge.gestemp.validation;

import netgest.bo.impl.document.merge.gestemp.presentation.GesDocObj;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;


public class ClfAtributo {
    private GesDocObj gesDocObj = null;

    public ClfAtributo(EboContext boctx, GesDocObj docObj) {
        this.gesDocObj = docObj;
    }

    public void setValor(String value) throws boRuntimeException {
        gesDocObj.setValue(value);
    }

    public String getValor() throws boRuntimeException {
        return gesDocObj.getValue();
    }
}
