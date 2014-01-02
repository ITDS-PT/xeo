package netgest.bo.impl.document.merge.gestemp;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.bo.system.Logger;


public class GtCampoManual extends GtCampo {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.impl.document.merge.gestemp.GtCampoManual");
    private GtCampoNManual parentManual;

    private GtCampoManual(GtTemplate template, GtQuery query) {
        super(template, null);
    }

    private GtCampoManual(GtCampoNManual parentManual) {
        super(parentManual);
    }

    public static GtCampoManual getCampo(GtCampoNManual parentManual,
        boObject campo) throws boRuntimeException {
        GtCampoManual newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoManual(parentManual);
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    public static GtCampoManual getCampo(GtTemplate template, GtQuery query,
        boObject campo) throws boRuntimeException {
        GtCampoManual newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoManual(template, query);
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    private static void setCampoValues(GtCampoManual newCampo, boObject campo)
        throws boRuntimeException {
        if (campo != null) {
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

            newCampo.setDecimals(campo.getAttribute("numeroCasasDecimais").getValueObject());
            
            if (campo.getAttribute("min").getValueObject() != null) {
                newCampo.setMin(campo.getAttribute("min").getValueLong());
            }

            if (campo.getAttribute("max").getValueObject() != null) {
                newCampo.setMax(campo.getAttribute("max").getValueLong());
            }

            newCampo.setTags(campo.getBridge("tags"));
            //é usado no modelo
            if(newCampo.getParentObj() != null)
            {//esta numa bridge
                newCampo.setReferenceByTemplate(Helper.referencedByTemplate(newCampo.getParentObj(), campo, newCampo.getTemplate().getTemplateBookmarks(), true));
            }
            else
            {
                newCampo.setReferenceByTemplate(Helper.referencedByTemplate((GtQuery)null, campo, newCampo.getTemplate().getTemplateFields(), false));
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

        if ("telefone".equalsIgnoreCase(getNome()) ||
                "fax".equalsIgnoreCase(getNome())) {
            s = redifineValue(s);
        }

        //        logger.finest(getHTMLFieldName() + "->" + s);
        if (Integer.parseInt(getTipo()) == 4) {
            String time = request.getParameter("_ignore_" + getHTMLFieldName());

            if ((time != null) && !"".equals(time)) {
                s += (" " + time);
            }
        }
        else if(Integer.parseInt(getTipo()) == 2)
        {
            if(getDecimals()!=null)
              s = formatCurrency(s, ((BigDecimal)getDecimals()).intValue());
            else
            s = formatCurrency(s);
        }


        super.setValueString(s);
    }

    public static String formatCurrency(String value, int fractionDigits)
    {
        if(value == null || value.length() == 0) return value;
        String valor = value.replaceAll("\\.", "").replaceAll(",", ".");
        BigDecimal bg = new BigDecimal(valor);
        NumberFormat currencyFormatter = NumberFormat.getInstance();
        currencyFormatter.setParseIntegerOnly(false);
        currencyFormatter.setGroupingUsed(true);
        currencyFormatter.setMaximumFractionDigits(fractionDigits);
        currencyFormatter.setMinimumFractionDigits(fractionDigits);
        currencyFormatter.setMinimumIntegerDigits(1);
        return currencyFormatter.format(bg);
    }

    public static String formatCurrency(String value)
    {
        return formatCurrency(value, 2);
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

        if ((v != null) && (v.getValue() != null)) {
            answer = boObject.getBoManager().createObject(boctx,
                    "GESTEMP_Answer");
            answer.getAttribute("pergunta").setValueString(getPergunta());
            answer.getAttribute("resposta").setValueString(String.valueOf(
                    v.getValue()));
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
                    GtValue g = new GtValue();
                    g.addValue(v);
                    setValue(g);

                    return;
                }
            }
        }
    }
}
