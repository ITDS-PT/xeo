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

import netgest.bo.system.Logger;


public class GtCampoNFormula extends GtCampo {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.impl.document.merge.gestemp.GtCampoNFormula");

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
            newCampo.setNome(campo.getAttribute("nome").getValueString());
            newCampo.setDescricao(campo.getAttribute("descricao")
                                       .getValueString());
            newCampo.setPergunta(campo.getAttribute("pergunta").getValueString());
            newCampo.setTipo(campo.getAttribute("tipo").getValueString());
            newCampo.setFormula(campo.getAttribute("formula").getValueString());
            newCampo.setValidacao(campo.getAttribute("validacao")
                                       .getValueString());
            newCampo.setParametro(campo.getAttribute("parametro").getValueLong());
            newCampo.setTags(campo.getBridge("tags"));

            newCampo.setTipoSeleccao(campo.getAttribute("tipoSeleccao")
                                          .getValueString());

            //campos
            boBridgeIterator bit = campo.getBridge("campos").iterator();
            bit.beforeFirst();

            boObject aux = null;
            GtCampoFormula cf = null;
            GtCampoNFormula cfn = null;
            GtCampoNJava cjn = null;

            while (bit.next()) {
                aux = bit.currentRow().getObject();

                if ("GESTEMP_CampoFormula".equals(aux.getName())) {
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
            javaExec.addImport("netgest.bo");
            javaExec.addImport("netgest.bo.def");
            javaExec.addImport("netgest.utils");
            javaExec.addImport("netgest.bo.runtime");
            javaExec.addImport("netgest.bo.utils");
            javaExec.addImport("netgest.bo.impl.document.merge.gestemp");

            //variaveis
            javaExec.addTypedVariable("contexto", Contexto.class,
                new Contexto(boctx), null);
            javaExec.addTypedVariable("modelo", Modelo.class,
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
            String msg = Messages.getString("GtCampoNFormula.24") + getFormula() + "]";

            if ((query != null) && (query.getParametro() != null) &&
                    (query.getParametro().getValue() != null)) {
                Object o = query.getParametro().getValue().getValue();

                if ((o != null) && o instanceof Long) {
                    boObject paramObj = boObject.getBoManager().loadObject(boctx,
                            ((Long) o).longValue());
                    msg += (Messages.getString("GtCampoNFormula.26") + paramObj.getBoui() + "/" +
                    paramObj.getName() + Messages.getString("GtCampoNFormula.28"));
                }
            }

            logger.severe(Messages.getString("GtCampoNFormula.23") + getNome() + "] : " + msg, e);
            throw new boRuntimeException("",
                Messages.getString("GtCampoNFormula.32") + getPergunta() +
                Messages.getString("GtCampoNFormula.33"), null);
        }
    }

    public GtCampo[] getAllCampos() {
        return (GtCampo[]) campos.toArray(new GtCampo[campos.size()]);
    }
    
}
