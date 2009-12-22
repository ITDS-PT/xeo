package netgest.bo.impl.document.merge.gestemp;

import java.util.ArrayList;

import netgest.bo.impl.document.merge.Tabela;
import netgest.bo.impl.document.merge.gestemp.validation.Campo;
import netgest.bo.impl.document.merge.gestemp.validation.CampoLista;
import netgest.bo.impl.document.merge.gestemp.validation.CampoListaObj;
import netgest.bo.impl.document.merge.gestemp.validation.Contexto;
import netgest.bo.impl.document.merge.gestemp.validation.JavaExecuter;
import netgest.bo.impl.document.merge.gestemp.validation.Modelo;
import netgest.bo.impl.document.merge.gestemp.validation.Query;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.system.Logger;


public class GtCampoFormula extends GtCampo {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.impl.document.merge.gestemp.GtCampoObjecto"); //$NON-NLS-1$

    public GtCampoFormula(GtTemplate template, GtQuery query) {
        super(template, query);
    }

    public GtCampoFormula(GtCampoNFormula parentFormula) {
        super(parentFormula);
    }

    public GtCampoFormula(GtCampoNObjecto parentObject) {
        super(parentObject);
    }

    public String getHeaderName() {
        if ((getQueryName() != null) && (getParentObj() == null)) {
            return getQueryName() + "__" + getNome(); //$NON-NLS-1$
        }

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

    public static GtCampoFormula getCampo(GtCampoNFormula parentFormula,
        boObject campo) throws boRuntimeException {
        GtCampoFormula newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoFormula(parentFormula);
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    public static GtCampoFormula getCampo(GtCampoNObjecto parentObject,
        boObject campo) throws boRuntimeException {
        GtCampoFormula newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoFormula(parentObject);
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    public static GtCampoFormula getCampo(GtTemplate template, GtQuery query,
        boObject campo) throws boRuntimeException {
        GtCampoFormula newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoFormula(template, query);
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    private static void setCampoValues(GtCampoFormula newCampo, boObject campo)
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
        throws boRuntimeException 
    {
        Object currValue = value!=null?value.getValue():null;
        Object fieldValue = Helper.getReturnObject( boctx, Integer.parseInt(getTipo()), currValue, templateType, getTags() );
        String hname       = getHeaderName();
        tab.insert(
                fieldValue, 
                hname,
                Helper.getSqlTypeFromGesDocTipoCampoLov(
                    Integer.parseInt(getTipo()),
                templateType, fieldValue)
                );
    }

    public void setData(EboContext boctx, Tabela tab, byte templateType,
        int index) throws boRuntimeException {
        ArrayList valores = value.getValues();

        if ((valores != null) && (valores.size() > 0)) {
            Object returnedObj = Helper.getReturnObject(boctx,
                    Integer.parseInt(getTipo()), valores.get(index),
                    templateType, getTags()); 
            tab.insert(returnedObj, getHeaderName(),
                Helper.getSqlTypeFromGesDocTipoCampoLov(Integer.parseInt(
                        getTipo()), templateType, returnedObj));
        }
    }

    public void calculateAutomicFields(EboContext boctx, GtTemplate template)
        throws boRuntimeException {
        GtQuery query = null;
        GtCampo parentObj = null;

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

            //
            if (getParentObj() != null) {
                if (getParentObj() instanceof GtCampoNFormula) {
                    javaExec.addTypedVariable(getParentObj().getNome(),
                        CampoLista.class,
                        new CampoLista(boctx, (GtCampoNFormula) getParentObj()),
                        null);
                } else if (getParentObj() instanceof GtCampoNObjecto) {
                    javaExec.addTypedVariable(getParentObj().getNome(),
                        CampoLista.class,
                        new CampoLista(boctx, (GtCampoNObjecto) getParentObj()),
                        null);
                }
            }

            javaExec.addTypedVariable(getNome(), Campo.class,
                new Campo(boctx, this), null);

            //javaCode
            javaExec.setJavaCode(getFormula());
            javaExec.execute();
        if( !javaExec.sucess() )
        {
            String msg = "Não foi possível calcular o campox [" + getNome() + "]";
            
            if( getQuery() != null )
            {
                msg += " da Query ["+ getQuery().getNome() +"]";
            }

            if ((query != null) && (query.getParametro() != null) &&
                    (query.getParametro().getValue() != null)) {
                Object o = query.getParametro().getValue().getValue();

                if ((o != null) && o instanceof Long) {
                    boObject paramObj = boObject.getBoManager().loadObject(boctx,
                            ((Long) o).longValue());
                    msg += (" para o objecto [" + paramObj.getBoui() + "/" +
                    paramObj.getName() + "]");
                }
            }
            msg += "\n" + javaExec.getErrorMessage();
            logger.severe("Campo Formula[" + getNome() + "] : " + msg, javaExec.getException() );
            throw new boRuntimeException("",
               msg , null);

        }
    }

    public void calculateAutomicFields(EboContext boctx, GtTemplate template,
        int pos) throws boRuntimeException {
        GtQuery query = null;
        GtCampo parentObj = null;

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

            //
            if (getParentObj() instanceof GtCampoNFormula) {
                javaExec.addTypedVariable(getParentObj().getNome() + Messages.getString("GtCampoFormula.19"), //$NON-NLS-1$
                    CampoLista.class,
                    new CampoLista(boctx, (GtCampoNFormula) getParentObj()),
                    null);
                javaExec.addTypedVariable(getParentObj().getNome(),
                    CampoListaObj.class,
                    new CampoListaObj(boctx, (GtCampoNFormula) getParentObj(),
                        pos), null);
            } else if (getParentObj() instanceof GtCampoNObjecto) {
                javaExec.addTypedVariable(getParentObj().getNome() + Messages.getString("GtCampoFormula.41"), //$NON-NLS-1$
                    CampoLista.class,
                    new CampoLista(boctx, (GtCampoNObjecto) getParentObj()),
                    null);
                javaExec.addTypedVariable(getParentObj().getNome(),
                    CampoListaObj.class,
                    new CampoListaObj(boctx, (GtCampoNObjecto) getParentObj(),
                        pos), null);
            }

            javaExec.addTypedVariable(getNome(), CampoListaObj.class,
                new CampoListaObj(boctx, this, pos), null);

            //javaCode
            javaExec.setJavaCode(getFormula());
            javaExec.execute();
        } catch (Exception e) {
            String msg = Messages.getString("GtCampoFormula.42") + getFormula() + "]"; //$NON-NLS-1$ //$NON-NLS-2$

            if ((query != null) && (query.getParametro() != null) &&
                    (query.getParametro().getValue() != null)) {
                Object o = query.getParametro().getValue().getValue();

                if ((o != null) && o instanceof Long) {
                    boObject paramObj = boObject.getBoManager().loadObject(boctx,
                            ((Long) o).longValue());
                    msg += (Messages.getString("GtCampoFormula.44") + paramObj.getBoui() + "/" + //$NON-NLS-1$ //$NON-NLS-2$
                    paramObj.getName() + "]"); //$NON-NLS-1$
                }
            }

            logger.severe(Messages.getString("GtCampoFormula.47") + getNome() + "] : " + msg, e); //$NON-NLS-1$ //$NON-NLS-2$
            throw new boRuntimeException("", //$NON-NLS-1$
                Messages.getString("GtCampoFormula.50") + getPergunta() + //$NON-NLS-1$
                Messages.getString("GtCampoFormula.51"), null); //$NON-NLS-1$
        }
    }
}
