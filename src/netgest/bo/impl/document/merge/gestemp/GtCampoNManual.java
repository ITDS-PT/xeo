package netgest.bo.impl.document.merge.gestemp;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.impl.document.merge.Tabela;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.bo.system.Logger;


public class GtCampoNManual extends GtCampo {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.impl.document.merge.gestemp.GtCampoNManual");

    private GtCampoNManual(GtTemplate template, GtQuery query) {
        super(template, null);
    }

    public static GtCampoNManual getCampo(GtTemplate template, GtQuery query,
        boObject campo) throws boRuntimeException {
        GtCampoNManual newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoNManual(template, query);
            newCampo.setNome(campo.getAttribute("nome").getValueString());
            newCampo.setPergunta(campo.getAttribute("pergunta").getValueString());
            newCampo.setDescricao(campo.getAttribute("descricao")
                                       .getValueString());
            newCampo.setTipo(campo.getAttribute("tipo").getValueString());
            newCampo.setValidacao(campo.getAttribute("validacao")
                                       .getValueString());
            newCampo.setObrigatorio(campo.getAttribute("obrigatorio")
                                         .getValueString());
            newCampo.setTextos(campo.getAttribute("textLov").getValueLong());

            if (campo.getAttribute("min").getValueObject() != null) {
                newCampo.setMin(campo.getAttribute("min").getValueLong());
            }

            if (campo.getAttribute("max").getValueObject() != null) {
                newCampo.setMax(campo.getAttribute("max").getValueLong());
            }

            newCampo.setTags(campo.getBridge("tags"));

            newCampo.setTipoSeleccao(campo.getAttribute("tipoSeleccao")
                                          .getValueString());
            //é usado no modelo
            newCampo.setReferenceByTemplate(Helper.referencedByTemplate(query, campo, template.getTemplateBookmarks(), true));
        }

        return newCampo;
    }

    public void setData(EboContext boctx, Tabela tab, byte templateType)
        throws boRuntimeException {
        if (value != null) {
            ArrayList valores = value.getValues();

            if ((valores != null) && (valores.size() > 0)) {
                for (int i = 0; i < valores.size(); i++) {
                    tab.startEditingLine();

                    Object returnedObj = null;
                    
                    if ( valores.get(i) instanceof Long )
                    {
                    String valor = ((Long) valores.get(i)).toString();
                         returnedObj = Helper.getReturnObject(boctx,
                            Integer.parseInt(getTipo()), valor, templateType,
                            getTags());
                    }
                    else
                    {
                        returnedObj = valores.get(i);
                    }
                    
                    tab.insert(returnedObj, getHeaderName(),
                        Helper.getSqlTypeFromGesDocTipoCampoLov(
                            Integer.parseInt(getTipo()), templateType, returnedObj));
                    tab.endEditingline();
                }
            }
        }
    }

    public String getHTMLFieldName() {
        return "manualField__" + getNome();
    }

    public String getHTMLFieldID() {
        return "manualField__" + getNome();
    }

    public static String redifineValue(String s) {
        String result = null;
        int pos1;
        int pos2;

        if ((s != null) && (s.length() >= 9) && (s.indexOf(" ") == -1)) {
            int ciclo = 0;

            for (int i = s.length(); i > 0; i++) {
                pos2 = i;
                pos1 = i - 3;

                if (result == null) {
                    result = s.substring(pos1, pos2);
                } else {
                    result = s.substring(pos1, pos2) + " " + result;
                }

                if (ciclo == 2) {
                    if (pos1 != 0) {
                        result = (s.substring(0, pos1) + " " + result);
                    }

                    i = -2;
                } else {
                    i = (pos1 - 1);
                    ciclo++;
                }
            }

            return result;
        }

        return s;
    }

    public void setValues(EboContext boctx) throws boRuntimeException {
        HttpServletRequest request = boctx.getRequest();
        Enumeration oEnum = request.getParameterNames();

        //        show(request);
        String s = request.getParameter(getHTMLFieldName());

        if ((s != null) && (s.length() > 0)) {
            GtValue gv = getValue();
            String[] vs = s.split(";");
            ArrayList values = null;

            if ((gv == null) || (gv.getValues() == null)) {
                gv = (gv == null) ? new GtValue() : gv;
                values = new ArrayList(vs.length);
                gv.addValues(values);
                setValue(gv);
            } else {
                values = gv.getValues();
                values.clear();
            }

            for (int i = 0; i < vs.length; i++) {
                values.add(new Long(vs[i]));
            }
        } else {
            setValue(null);
        }
    }

    private static void show(HttpServletRequest req) {
        Enumeration oEnum = req.getParameterNames();

        while (oEnum.hasMoreElements()) {
            String o = (String) oEnum.nextElement();
            logger.finest(o + " -> " + req.getParameter(o));
        }
    }

    public boObject getAnswerObject(EboContext boctx) throws boRuntimeException {
        boObject answer = null;
        GtValue v = getValue();

        if ((v != null) && (v.getValues() != null)) {
            ArrayList r = v.getValues();
            String vStr = "";

            for (int i = 0; i < r.size(); i++) {
                if (i > 0) {
                    vStr += ";";
                }
                if( r.get(i) instanceof Long )
                {
                vStr += ((Long) r.get(i)).toString();
                }
            }

            if (vStr.length() > 0) {
                answer = boObject.getBoManager().createObject(boctx,
                        "GESTEMP_Answer");
                answer.getAttribute("pergunta").setValueString(getPergunta());
                answer.getAttribute("resposta").setValueString(vStr);
            }
        }

        return answer;
    }

    public void setAnswer(bridgeHandler bh) throws boRuntimeException {
        boBridgeIterator bit = bh.iterator();
        bit.beforeFirst();

        String pergunta;

        while (bit.next()) {
            pergunta = bit.currentRow().getObject().getAttribute("pergunta")
                          .getValueString();

            if (pergunta.equalsIgnoreCase(getPergunta())) {
                String v = bit.currentRow().getObject().getAttribute("resposta")
                              .getValueString();

                if ((v != null) && !"".equals(v)) {
                    String[] valores = v.split(";");
                    ArrayList r = null;
                    Long auxL = null;

                    for (int i = 0; i < valores.length; i++) {
                        try {
                            auxL = new Long(valores[i]);

                            if (r == null) {
                                r = new ArrayList();
                            }

                            r.add(auxL);
                        } catch (Exception e) {
                            //ignore
                        }
                    }

                    if (r != null) {
                        GtValue g = new GtValue();
                        g.addValues(r);
                        setValue(g);
                    } else {
                        setValue(null);
                    }
                }
            }
        }
    }
}
