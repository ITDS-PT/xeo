package netgest.bo.impl.document.merge.gestemp.validation;

import netgest.bo.impl.document.merge.gestemp.GtCampo;
import netgest.bo.impl.document.merge.gestemp.GtCampoNFormula;
import netgest.bo.impl.document.merge.gestemp.GtCampoNJava;
import netgest.bo.impl.document.merge.gestemp.GtCampoNObjecto;
import netgest.bo.impl.document.merge.gestemp.GtQuery;
import netgest.bo.impl.document.merge.gestemp.GtValue;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.system.Logger;


public class Query {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.impl.document.merge.gestemp.Query");
    private static EboContext boctx = null;
    private GtQuery query;

    public Query(EboContext boctx, GtQuery query) {
        this.boctx = boctx;
        this.query = query;
    }

    //Campos Manuais
    public int numeroCampos() {
        return (query.getAllCampos() == null) ? 0 : query.getAllCampos().length;
    }

    public Campo getCampo(int pos) {
        GtCampo c = query.getAllCampos()[pos];

        if (c instanceof GtCampoNFormula) {
            return new CampoLista(boctx, (GtCampoNFormula) c);
        } else if (c instanceof GtCampoNJava) {
            return new CampoLista(boctx, (GtCampoNJava) c);
        } else if (c instanceof GtCampoNObjecto) {
            return new CampoLista(boctx, (GtCampoNObjecto) c);
        }

        return new Campo(boctx, c);
    }

    public Campo getCampoLista(int pos) {
        GtCampo c = query.getAllCampos()[pos];

        if (c instanceof GtCampoNFormula) {
            return new CampoLista(boctx, (GtCampoNFormula) c);
        } else if (c instanceof GtCampoNJava) {
            return new CampoLista(boctx, (GtCampoNJava) c);
        } else if (c instanceof GtCampoNObjecto) {
            return new CampoLista(boctx, (GtCampoNObjecto) c);
        }

        return null;
    }

    public Campo getCampo(String nome) throws boRuntimeException {
        GtCampo c = null;

        if (query.getAllCampos() != null) {
            for (int i = 0; i < query.getAllCampos().length; i++) {
                c = query.getAllCampos()[i];

                if (c.getNome().equals(nome)) {
                    if (c instanceof GtCampoNFormula) {
                        return new CampoLista(boctx, (GtCampoNFormula) c);
                    } else if (c instanceof GtCampoNJava) {
                        return new CampoLista(boctx, (GtCampoNJava) c);
                    } else if (c instanceof GtCampoNObjecto) {
                        return new CampoLista(boctx, (GtCampoNObjecto) c);
                    }

                    return new Campo(boctx, c);
                }
            }
        }

        throw new boRuntimeException("Query.getCampo",MessageLocalizer.getMessage("THE_FIELD")+
            " [" + nome + "] "+MessageLocalizer.getMessage("IS_UNKNOWN"), null);
    }

    public CampoLista getCampoLista(String nome) {
        GtCampo c = null;

        if (query.getAllCampos() != null) {
            for (int i = 0; i < query.getAllCampos().length; i++) {
                c = query.getAllCampos()[i];

                if (c.getNome().equals(nome)) {
                    if (c instanceof GtCampoNFormula) {
                        return new CampoLista(boctx, (GtCampoNFormula) c);
                    } else if (c instanceof GtCampoNJava) {
                        return new CampoLista(boctx, (GtCampoNJava) c);
                    } else if (c instanceof GtCampoNObjecto) {
                        return new CampoLista(boctx, (GtCampoNObjecto) c);
                    }

                    return null;
                }
            }
        }

        return null;
    }

    public String getValorString() {
        GtValue valor = query.getParametro().getValue();

        if ((valor != null) && (valor.getValue() != null)) {
            return String.valueOf( valor.getValue() );
        }

        return null;
    }

    public long getValor() {
        GtValue valor = query.getParametro().getValue();

        if ((valor != null) && (valor.getValue() != null)) {
            return ((Long) valor.getValue()).longValue();
        }

        return -1;
    }

    public boObject getValorXEOObject() throws boRuntimeException {
        GtValue value = query.getParametro().getValue();

        if (value != null) {
            Long valor = (Long) value.getValue();

            if (valor != null) {
                return boObject.getBoManager().loadObject(boctx,
                    valor.longValue());
            }
            logger.severe(LoggerMessageLocalizer.getMessage("VALUE_NULL_OBTAINING_XEO_VALUE_CHECK_THIS_SITUATION"));
        }

        return null;
    }

    public void setValor(String value) throws boRuntimeException {
        query.getParametro().setValueString(value);
    }

    public void setValor(long value) throws boRuntimeException {
        query.getParametro().setValueString(String.valueOf(value));
    }

    public void setCampo(int pos, Object value) throws boRuntimeException {
        Campo c = getCampo(pos);

        if (c != null) {
            c.setValor(value);
        }
    }

    public void setCampo(String nome, Object value) throws boRuntimeException {
        Campo c = getCampo(nome);

        if (c != null) {
            c.setValor(value);
        }
    }
    
    public boolean emUsoNoModelo()
    {
        return query.referenceByTemplate();
    }
}
