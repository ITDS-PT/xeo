package netgest.bo.impl.document.merge.gestemp;

import bsh.BshClassManager;
import bsh.NameSpace;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.impl.document.merge.Tabela;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.bo.system.Logger;


public class GtParametro extends GtCampo {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.impl.document.merge.gestemp.GtCampoManual");
    private String queryName = null;
    private boolean destinatario = false;
    private int size = 0;
    private int width = 0;

    public GtParametro(GtTemplate template, GtQuery query) {
        super(template, query);
    }

    public void setQueryName(String newValue) {
        this.queryName = newValue;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setDestinatario(boolean value) {
        destinatario = value;
    }

    public String getHTMLFieldName() {
        return getQueryName() + "__" + getNome();
    }

    public String getHTMLFieldID() {
        return "tblLook" + getQueryName() + "__" + getNome();
    }

    public boolean destinatario() {
        return destinatario;
    }

    public static GtParametro getCampo(GtTemplate template, GtQuery query,
        boObject campo) throws boRuntimeException {
        GtParametro newCampo = null;

        if (campo != null) {
            newCampo = new GtParametro(template, query);
            newCampo.setNome(campo.getAttribute("nome").getValueString());
            newCampo.setPergunta(campo.getAttribute("pergunta").getValueString());
            newCampo.setDescricao(campo.getAttribute("descricao")
                                       .getValueString());
            newCampo.setObjecto(campo.getAttribute("objecto").getValueLong());
            newCampo.setAtributo(campo.getAttribute("atributo").getValueLong());
            newCampo.setHelper(campo.getAttribute("helper").getValueString());
            newCampo.setValidacao(campo.getAttribute("validacao")
                                       .getValueString());
            newCampo.setObrigatorio(campo.getAttribute("obrigatorio")
                                         .getValueString());
            newCampo.setFormula(campo.getAttribute("formula").getValueString());
            newCampo.setAskUser(campo.getAttribute("askUser").getValueString());
            newCampo.setDestinatario("1".equals(campo.getAttribute(
                        "destinatario").getValueString()));
                        
            newCampo.size = (int)campo.getAttribute("maximocaracteres").getValueLong();
            newCampo.width = (int)campo.getAttribute("largura").getValueLong();
            
        }

        return newCampo;
    }

    public void setValueString(String s) throws boRuntimeException {
        if ((s != null) && !"".equals(s)) {
            GtValue v = new GtValue();
            v.addValue(new Long(s));
            setValue(v);
        } else {
            setValue(null);
        }
    }

    public void setData(EboContext boctx, Tabela tab, byte templateType)
        throws boRuntimeException {
        if (value != null) {
            if (value.getValue() instanceof Long) {
                boObject aux = boObject.getBoManager().loadObject(boctx,
                        ((Long) value.getValue()).longValue());

                if (templateType == GtTemplate.CLF_TEMPALTE) {
                    tab.insert(BigDecimal.valueOf(aux.getBoui()), getNome(),
                        Helper.getSqlTypeFromGesDocTipoCampoLov(1, templateType, null));
                } else {
                    tab.insert(aux.getCARDID(false).toString(), getNome(),
                        Helper.getSqlTypeFromGesDocTipoCampoLov(1, templateType, null));
                }
            } else {
                Object returned = value.getValue(); 
                tab.insert(returned, getNome(),
                    Helper.getSqlTypeFromGesDocTipoCampoLov(Integer.parseInt(
                            getTipo()), templateType, returned));
            }
        }
    }

    public void setValues(EboContext boctx) throws boRuntimeException 
    {
        HttpServletRequest request = boctx.getRequest();
        String s = request.getParameter(getHTMLFieldName());
        if( this.askUser() || s != null )
        {
        GtValue v = new GtValue();
            if ((s != null) && !"".equals(s) && !"-1".equals(s)) 
            {
                if( getObjecto() != 0 )
                {
            v.addValue(new Long(s));
        }
                else
                {
                    v.addValue( s );
                }
            }
        setValue(v);
    }
    }

    public boObject getAnswerObject(EboContext boctx) throws boRuntimeException {
        boObject answer = null;
        GtValue v = getValue();

        if ((v != null) && (v.getValue() != null)) {
            answer = boObject.getBoManager().createObject(boctx,
                    "GESTEMP_Answer");
            answer.getAttribute("pergunta").setValueString(getPergunta());
            if( getObjecto() != 0 )
            {
            answer.getAttribute("objecto").setValueLong(((Long) v.getValue()).longValue());
        }
            else
            {
                answer.getAttribute("resposta").setValueString( (String)v.getValue() );
            }
        }

        return answer;
    }

    public void setReferencias(boObject generatedObj)   throws boRuntimeException
    {
        GtValue v = getValue();
        if(v != null && v.getValue() != null)
        {
            if( getObjecto() != 0 )
            {
            boObject o = boObject.getBoManager().loadObject(generatedObj.getEboContext(), ((Long)v.getValue()).longValue());
            if("Pessoa".equals(o.getName()) || 
                "Peritagem".equals(o.getName()) ||
                "process_sinistro".equals(o.getName()) ||
                "process_producao".equals(o.getName())
            )
            {
                if(!generatedObj.getBridge("objReferences").haveBoui(o.getBoui()))
                {
                    generatedObj.getBridge("objReferences").add(o.getBoui());
                }
                //Martelada
                if("Peritagem".equals(o.getName()))
                {
                    o = o.getAttribute("sinistro").getObject();
                    if(o != null && !generatedObj.getBridge("objReferences").haveBoui(o.getBoui()))
                    {
                        generatedObj.getBridge("objReferences").add(o.getBoui());
                    }
                }
            }
        }
    }
    }
    public void calculateAutomicFields(EboContext boctx, GtTemplate template,
        GtQuery query) throws boRuntimeException {
        try {
            NameSpace nsp = new NameSpace(new BshClassManager(),
                    "campoParamAutomatic");
            nsp.importPackage("netgest.bo");
            nsp.importPackage("netgest.bo.def");
            nsp.importPackage("netgest.utils");
            nsp.importPackage("netgest.bo.runtime");
            nsp.importPackage("netgest.bo.utils");
            nsp.importPackage("netgest.bo.impl.document.merge.gestemp");

            nsp.setTypedVariable("ctx", EboContext.class, boctx, null);
            nsp.setTypedVariable("template", GtTemplate.class, template, null);
            nsp.setTypedVariable(getNome(), GtParametro.class, this, null);

            //não passa o parametro
            bsh.Interpreter bshi = new bsh.Interpreter();
            bshi.setNameSpace(nsp);

            Object xo = bshi.eval(getFormula());

            GtValue v = new GtValue();

            if (xo != null) {
                if (xo instanceof boObject) {
                    v.addValue(new Long(((boObject) xo).getBoui()));
                } else if (xo instanceof Long) {
                    v.addValue(xo);
                } else if (xo instanceof String) {
                    v.addValue(new Long((String) xo));
                } else if (xo instanceof BigDecimal) {
                    v.addValue(new Long(((BigDecimal) xo).longValue()));
                }
            }

            setValue(v);
        } catch (Exception e) {
            throw new boRuntimeException("GtCampoFormula",
                "calculateAutomicFields", e);
        }
    }

    public void setAnswer(bridgeHandler bh) throws boRuntimeException {
        boBridgeIterator bit = bh.iterator();
        bit.beforeFirst();

        String pergunta;

        while (bit.next()) {
            pergunta = bit.currentRow().getObject().getAttribute("pergunta")
                          .getValueString();

            if (pergunta.equalsIgnoreCase(getPergunta())) {
                boObject o = bit.currentRow().getObject().getAttribute("objecto")
                                .getObject();

                if (o != null) {
                    GtValue g = new GtValue();
                    g.addValue(new Long(o.getBoui()));
                    setValue(g);

                    return;
                }
            }
        }
    }


    public int getSize()
    {
        return size;
    }


    public int getWidth()
    {
        return width;
    }
}
