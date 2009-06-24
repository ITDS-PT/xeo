package netgest.bo.impl.document.merge.gestemp;

import java.util.ArrayList;

import netgest.bo.impl.document.merge.Tabela;
import netgest.bo.impl.document.merge.gestemp.validation.CampoLista;
import netgest.bo.impl.document.merge.gestemp.validation.Contexto;
import netgest.bo.impl.document.merge.gestemp.validation.JavaExecuter;
import netgest.bo.impl.document.merge.gestemp.validation.Modelo;
import netgest.bo.impl.document.merge.gestemp.validation.Query;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

import org.apache.log4j.Logger;


public class GtCampoNFormula extends GtCampo {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.impl.document.merge.gestemp.GtCampoNFormula"); //$NON-NLS-1$

    //Campos Formula ou Objecto
    ArrayList campos;

    public GtCampoNFormula(GtTemplate template, GtQuery query) {
        super(template, query);
        campos = new ArrayList();
    }

    public GtCampoNFormula(GtCampo parent) {
        super(parent);
        campos = new ArrayList();
    }

    public String getHeaderName() {
        return getNome();
    }

    public String getHTMLFieldName() {
        if (getQueryName() != null) {
            return getQueryName() + "__" + getNome(); //$NON-NLS-1$
        }

        return getNome();
    }

    public String getHTMLFieldID() {
        if (getQueryName() != null) {
            return "tblLook" + getQueryName() + "__" + getNome(); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return "tblLook" + getNome(); //$NON-NLS-1$
    }

    public void addCampo(GtCampo newValue) {
        campos.add(newValue);
    }

    public static GtCampoNFormula getCampo(GtCampo parent, boObject campo)
        throws boRuntimeException {
        GtCampoNFormula newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoNFormula(parent);
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    public static GtCampoNFormula getCampo(GtTemplate template, GtQuery query,
        boObject campo) throws boRuntimeException {
        GtCampoNFormula newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoNFormula(template, query);
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    private static void setCampoValues(GtCampoNFormula newCampo, boObject campo)
        throws boRuntimeException {
        if (campo != null) {
            newCampo.setNome(campo.getAttribute("nome").getValueString()); //$NON-NLS-1$
            newCampo.setDescricao(campo.getAttribute("descricao") //$NON-NLS-1$
                                       .getValueString());
            newCampo.setPergunta(campo.getAttribute("pergunta").getValueString()); //$NON-NLS-1$
            newCampo.setTipo(campo.getAttribute("tipo").getValueString()); //$NON-NLS-1$
            newCampo.setFormula(campo.getAttribute("formula").getValueString()); //$NON-NLS-1$
            newCampo.setValidacao(campo.getAttribute("validacao") //$NON-NLS-1$
                                       .getValueString());
            newCampo.setParametro(campo.getAttribute("parametro").getValueLong()); //$NON-NLS-1$
            newCampo.setTags(campo.getBridge("tags")); //$NON-NLS-1$

            newCampo.setTipoSeleccao(campo.getAttribute("tipoSeleccao") //$NON-NLS-1$
                                          .getValueString());

            //campos
            boBridgeIterator bit = campo.getBridge("campos").iterator(); //$NON-NLS-1$
            bit.beforeFirst();

            boObject aux = null;
            GtCampoFormula cf = null;
            GtCampoNFormula cfn = null;
            GtCampoNJava cjn = null;

            while (bit.next()) {
                aux = bit.currentRow().getObject();

                if ("GESTEMP_CampoFormula".equals(aux.getName())) { //$NON-NLS-1$
                    cf = GtCampoFormula.getCampo(newCampo, aux);
                    newCampo.addCampo(cf);
                }
            }
            
            //é usado no modelo
            newCampo.setReferenceByTemplate(Helper.referencedByTemplate(newCampo.getQuery(), campo, newCampo.getTemplate().getTemplateBookmarks(), true));
        }
    }

    public void setData(EboContext boctx, Tabela tab, byte templateType)
        throws boRuntimeException {
        Object returnedObj = Helper.getReturnObject(boctx, Integer.parseInt(getTipo()),
                value.getValue(), templateType, getTags()); 
        tab.insert(returnedObj, getHeaderName(),
            Helper.getSqlTypeFromGesDocTipoCampoLov(Integer.parseInt(getTipo()),
                templateType, returnedObj));
    }

    public void calculateAutomicFields(EboContext boctx, GtTemplate template)
        throws boRuntimeException {
        GtQuery query = null;

        try {
            boolean paramIsNull = true;
            boolean parentObjIsNull = true;
            JavaExecuter javaExec = new JavaExecuter(getNome());

            //imports
            javaExec.addImport("netgest.bo"); //$NON-NLS-1$
            javaExec.addImport("netgest.bo.def"); //$NON-NLS-1$
            javaExec.addImport("netgest.utils"); //$NON-NLS-1$
            javaExec.addImport("netgest.bo.runtime"); //$NON-NLS-1$
            javaExec.addImport("netgest.bo.utils"); //$NON-NLS-1$
            javaExec.addImport("netgest.bo.impl.document.merge.gestemp"); //$NON-NLS-1$

            //variaveis
            javaExec.addTypedVariable("contexto", Contexto.class, //$NON-NLS-1$
                new Contexto(boctx), null);
            javaExec.addTypedVariable("modelo", Modelo.class, //$NON-NLS-1$
                new Modelo(boctx, template), null);
            javaExec.addTypedVariable(getQuery().getNome(), Query.class,
                new Query(boctx, getQuery()), null);
            javaExec.addTypedVariable(getNome(), CampoLista.class,
                new CampoLista(boctx, this), null);

            //javaCode
            javaExec.setJavaCode(getFormula());
            javaExec.execute();

            if ((getValue() != null) && (getValue().getValues() != null)) {
                for (int i = 0; i < campos.size(); i++) {
                    ArrayList r = getValue().getValues();

                    for (int j = 0; j < r.size(); j++) {
                        ((GtCampoFormula) campos.get(i)).calculateAutomicFields(boctx,
                            template, j);
                    }
                }
            }
        } catch (Exception e) {
            String msg = Messages.getString("GtCampoNFormula.24") + getFormula() + "]"; //$NON-NLS-1$ //$NON-NLS-2$

            if ((query != null) && (query.getParametro() != null) &&
                    (query.getParametro().getValue() != null)) {
                Object o = query.getParametro().getValue().getValue();

                if ((o != null) && o instanceof Long) {
                    boObject paramObj = boObject.getBoManager().loadObject(boctx,
                            ((Long) o).longValue());
                    msg += (Messages.getString("GtCampoNFormula.26") + paramObj.getBoui() + "/" + //$NON-NLS-1$ //$NON-NLS-2$
                    paramObj.getName() + Messages.getString("GtCampoNFormula.28")); //$NON-NLS-1$
                }
            }

            logger.error(Messages.getString("GtCampoNFormula.23") + getNome() + "] : " + msg, e); //$NON-NLS-1$ //$NON-NLS-2$
            throw new boRuntimeException("", //$NON-NLS-1$
                Messages.getString("GtCampoNFormula.32") + getPergunta() + //$NON-NLS-1$
                Messages.getString("GtCampoNFormula.33"), null); //$NON-NLS-1$
        }
    }

    public GtCampo[] getAllCampos() {
        return (GtCampo[]) campos.toArray(new GtCampo[campos.size()]);
    }
    
}
