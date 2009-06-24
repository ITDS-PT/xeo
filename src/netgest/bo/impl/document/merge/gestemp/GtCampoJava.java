package netgest.bo.impl.document.merge.gestemp;

import netgest.bo.impl.document.merge.Tabela;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;


public class GtCampoJava extends GtCampo {
    private GtCampoNJava parentJava;

    public GtCampoJava(GtTemplate template, GtQuery query) {
        super(template, query);
    }

    public GtCampoJava(GtCampoNJava parentJava) {
        super(parentJava);
    }

    public String getHeaderName() {
        if ((getQueryName() != null) && (getParentObj() == null)) {
            return getQueryName() + "__" + getNome();
        }

        return getNome();
    }

    public String getHTMLFieldName() {
        if (getQueryName() != null) {
            return getQueryName() + "__" + getNome();
        }

        return getNome();
    }

    public String getHTMLFieldID() {
        if (getQueryName() != null) {
            return "tblLook" + getQueryName() + "__" + getNome();
        }

        return "tblLook" + getNome();
    }

    public static GtCampoJava getCampo(GtCampoNJava parentJava, boObject campo)
        throws boRuntimeException {
        GtCampoJava newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoJava(parentJava.getTemplate(),
                    parentJava.getQuery());
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    public static GtCampoJava getCampo(GtTemplate template, GtQuery query,
        boObject campo) throws boRuntimeException {
        GtCampoJava newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoJava(template, query);
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    public static void setCampoValues(GtCampoJava newCampo, boObject campo)
        throws boRuntimeException {
        if (campo != null) {
            newCampo.setNome(campo.getAttribute("nome").getValueString());
            newCampo.setDescricao(campo.getAttribute("descricao")
                                       .getValueString());
            newCampo.setFormula(campo.getAttribute("formula").getValueString());
            newCampo.setParametro(campo.getAttribute("parametro").getValueLong());
            newCampo.setTipo(campo.getAttribute("tipo").getValueString());
            newCampo.setTags(campo.getBridge("tags"));
            //é usado no modelo
            if(newCampo.getParentObj() != null)
            {//esta numa bridge
                newCampo.setReferenceByTemplate(Helper.referencedByTemplate(newCampo.getParentObj(), campo, newCampo.getTemplate().getTemplateBookmarks(), true));
            }
            else
            {
                newCampo.setReferenceByTemplate(Helper.referencedByTemplate(newCampo.getQuery(), campo, newCampo.getTemplate().getTemplateFields(), false));
            }
        }
    }

    public void setData(EboContext boctx, Tabela tab, byte templateType)
        throws boRuntimeException {
        Object returnedObj = Helper.getReturnObject(boctx, Integer.parseInt(getTipo()),
                (value == null) ? null : value.getValue(), templateType,
                getTags()); 
        tab.insert(returnedObj, getHeaderName(),
            Helper.getSqlTypeFromGesDocTipoCampoLov(Integer.parseInt(getTipo()),
                templateType, returnedObj));
    }
}
