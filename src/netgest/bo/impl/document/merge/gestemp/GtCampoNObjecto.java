package netgest.bo.impl.document.merge.gestemp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.math.BigDecimal;
import java.sql.Types;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.impl.document.merge.Tabela;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.io.iFile;
import netgest.io.iFileServer;

import netgest.bo.system.Logger;


public class GtCampoNObjecto extends GtCampo {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.impl.document.merge.gestemp.GtCampoNObjecto");

    //Campos Formula ou Objecto
    ArrayList camposformulas;
    ArrayList camposObjecto;
    ArrayList campos;
    private String queryName = null;

    public GtCampoNObjecto(GtTemplate template, GtQuery query) {
        super(template, query);
        camposformulas = new ArrayList();
        camposObjecto = new ArrayList();
        campos = new ArrayList();
    }

    public GtCampoNObjecto(GtCampo parentObj) {
        super(parentObj);
        camposformulas = new ArrayList();
        camposObjecto = new ArrayList();
        campos = new ArrayList();
    }

    public String getHeaderName() {
        return getNome();
    }

    public String getHTMLFieldName() {
        return getQueryName() + "__" + getNome();
    }

    public String getHTMLFieldID() {
        if (getQueryName() != null) {
            return "tblLook" + getQueryName() + "__" + getNome();
        }

        return "tblLook" + getNome();
    }

    public void addFormula(GtCampoFormula newValue) {
        camposformulas.add(newValue);
    }

    public void addObjecto(GtCampoObjecto newValue) {
        camposObjecto.add(newValue);
    }

    public void addCampo(GtCampo newValue) {
        campos.add(newValue);
    }

    public static GtCampoNObjecto getCampo(GtCampo parentObj, boObject campo)
        throws boRuntimeException {
        GtCampoNObjecto newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoNObjecto(parentObj);
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    public static GtCampoNObjecto getCampo(GtTemplate template, GtQuery query,
        boObject campo) throws boRuntimeException {
        GtCampoNObjecto newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoNObjecto(template, query);
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    private static void setCampoValues(GtCampoNObjecto newCampo, boObject campo)
        throws boRuntimeException {
        if (campo != null) {
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
            newCampo.setParametro(campo.getAttribute("parametro").getValueLong());
            newCampo.setTags(campo.getBridge("tags"));

            newCampo.setTipoSeleccao(campo.getAttribute("tipoSeleccao")
                                          .getValueString());

            //Campos
            boBridgeIterator bit = campo.getBridge("campos").iterator();
            bit.beforeFirst();

            boObject aux = null;
            GtCampoObjecto co = null;
            GtCampoFormula cf = null;
            GtCampoNObjecto con = null;
            GtCampoNFormula cfn = null;
            GtCampoNJava cjn = null;

            while (bit.next()) {
                aux = bit.currentRow().getObject();

                if (Helper.isMarkedForTemplate(newCampo, aux)) {
                    if ("GESTEMP_CampoObjecto".equals(aux.getName())) {
                        co = GtCampoObjecto.getCampo(newCampo, aux);
                        newCampo.addObjecto(co);
                        newCampo.addCampo(co);
                    } else if ("GESTEMP_CampoFormula".equals(aux.getName())) {
                        cf = GtCampoFormula.getCampo(newCampo, aux);
                        newCampo.addFormula(cf);
                        newCampo.addCampo(cf);
                    }
                }
            }
            //é usado no modelo
            newCampo.setReferenceByTemplate(Helper.referencedByTemplate(newCampo.getQuery(), campo, newCampo.getTemplate().getTemplateBookmarks(), true));
        }
    }

    public void setData(EboContext boctx, Tabela tab, byte templateType)
        throws boRuntimeException {
        long clsregBoui = getObjecto();
        long attBoui = getAtributo();
        String helper = getHelper();
        int sqlType = -1;

        long ti = System.currentTimeMillis();

        //vou carregar os objectos XEO
        boObject clsRegObj = boObject.getBoManager().loadObject(boctx,
                clsregBoui);
        boObject attObj = boObject.getBoManager().loadObject(boctx, attBoui);
        boDefAttribute attDef = null;

        //verificar se o atributo é atributo do objecto
        if (attObj.getAttribute("clsReg").getValueLong() == clsRegObj.getBoui()) {
            boDefHandler bodef = boDefHandler.getBoDefinition(clsRegObj.getAttribute(
                        "name").getValueString());
            attDef = bodef.getAttributeRef(attObj.getAttribute("name")
                                                 .getValueString());
            sqlType = Helper.getSqlType(attDef, templateType);
        } else {
            boDefHandler def = boDefHandler.getBoDefinition(clsRegObj.getAttribute(
                        "name").getValueString());
            String[] helpers = helper.split("\\.");

            for (int i = 0; i < helpers.length; i++) {
                if ((i + 1) < helpers.length) {
                    def = def.getAttributeRef(helpers[i])
                             .getReferencedObjectDef();
                } else {
                    attDef = def.getAttributeRef(helpers[i]);
                    sqlType = Helper.getSqlType(attDef, templateType);
                }
            }
        }

        if (sqlType == Types.BLOB) {
            if (GtTemplate.TEXT_TEMPLATE == templateType) {
                tab.insert("", getHeaderName(), sqlType);
            } else if (GtTemplate.CLF_TEMPALTE == templateType) {
                tab.insert("", getHeaderName(), sqlType);
            } else {
                tab.insert(toBytes(value), getHeaderName(), sqlType);
            }
        } else {
            if ((value != null) && (value.getValues() != null)) {
                ArrayList valores = value.getValues();

                if ((valores != null) && (valores.size() > 0)) {
                    for (int i = 0; i < valores.size(); i++) {
                        String t = attDef.getType().toUpperCase();
                        tab.startEditingLine();
                        if (GtTemplate.CLF_TEMPALTE == templateType && t.indexOf("OBJECT") != -1)
                        {
                            tab.insert(new BigDecimal(((Long)valores.get(i)).toString()), getHeaderName(),
                            Helper.getSqlTypeFromGesDocTipoCampoLov(
                                Integer.parseInt(getTipo()), templateType, null));
                        }
                        else
                        {
                            Object returnedObj = Helper.getReturnObject(boctx,
                                    Integer.parseInt(getTipo()), valores.get(i),
                                    templateType, getTags()); 
                            tab.insert(returnedObj, getHeaderName(),
                                Helper.getSqlTypeFromGesDocTipoCampoLov(
                                    Integer.parseInt(getTipo()), templateType,returnedObj));
                        }

                        for (int j = 0; j < camposObjecto.size(); j++) {
                            ((GtCampoObjecto) camposObjecto.get(j)).setData(boctx,
                                tab, templateType, i);
                        }

                        for (int j = 0; j < camposformulas.size(); j++) {
                            ((GtCampoFormula) camposformulas.get(j)).setData(boctx,
                                tab, templateType, i);
                        }

                        tab.endEditingline();
                    }
                }
            }
        }

        long tf = System.currentTimeMillis();
        logger.finer(Messages.getString("GtCampoNObjecto.27") +
            ((float) (Math.round((float) (tf - ti) / 100f)) / 10f) +
            Messages.getString("GtCampoNObjecto.28") + getNome());
    }

    public void calculateAutomicFields(EboContext boctx, GtTemplate template)
        throws boRuntimeException {
        GtValue g = getQuery().getParametro().getValue();

        try {
            if ("3".equals(getTipoSeleccao())) {
                if (g != null) {
                    if ((g.getValue() != null) && g.getValue() instanceof Long) {
                        boObject paramObj = boObject.getBoManager().loadObject(boctx,
                                ((Long) g.getValue()).longValue());
                        Object xo = getValue(boctx, paramObj, getHelper());
                        GtValue v = new GtValue();

                        if (xo != null) {
                            v.addValues((ArrayList) xo);
                        }

                        setValue(v);
                    }
                }
            }

            for (int i = 0; i < camposObjecto.size(); i++) {
                ((GtCampoObjecto) camposObjecto.get(i)).calculateAutomicFields(boctx,
                    template);
            }

            if ((getValue() != null) && (getValue().getValues() != null)) {
                for (int i = 0; i < camposformulas.size(); i++) {
                    ArrayList r = getValue().getValues();

                    for (int j = 0; j < r.size(); j++) {
                        ((GtCampoFormula) camposformulas.get(i)).calculateAutomicFields(boctx,
                            template, j);
                    }
                }
            }
        } catch (Exception e) {
            String msg = Messages.getString("GtCampoNObjecto.30") + getHelper() + "]";

            if ((g != null) && (g.getValue() != null)) {
                if (g.getValue() instanceof boObject) {
                    msg += (Messages.getString("GtCampoNObjecto.32") +
                    ((boObject) g.getValue()).getBoui() + "/" +
                    ((boObject) g.getValue()).getName() + "]");
                }
            }

            logger.severe(Messages.getString("GtCampoNObjecto.35") + getNome() + "] : " + msg, e);
            throw new boRuntimeException("",
                Messages.getString("GtCampoNObjecto.38") + getPergunta() +
                Messages.getString("GtCampoNObjecto.39"), null);
        }
    }

    private static Object getValue(EboContext boctx, boObject obj,
        String helperValue) throws boRuntimeException {
        if (helperValue != null) {
            String[] helpers = helperValue.split("\\.");

            if (helpers.length == 1) {
                boBridgeIterator bit = obj.getBridge(helpers[0]).iterator();
                bit.beforeFirst();

                boObject aux;
                ArrayList values = null;

                while (bit.next()) {
                    aux = bit.currentRow().getObject();

                    if (values == null) {
                        values = new ArrayList();
                    }

                    values.add(new Long(aux.getBoui()));
                }

                return values;
            } else {
                String helperAux = "";

                for (int i = 1; i < helpers.length; i++) {
                    helperAux += helpers[i];

                    if ((i + 1) < helpers.length) {
                        helperAux += ".";
                    }
                }

                return getValue(boctx,
                    obj.getAttribute(helpers[0]).getObject(), helperAux);
            }
        }

        return null;
    }

    private static byte[] toBytes(GtValue value) {
        byte[] b = null;

        if (value == null) {
            return null;
        }

        iFileServer fs = new iFileServer();
        fs.mount();

        iFile f = fs.getFile((String) value.getValue());

        if (f.exists()) {
            try {
                InputStream fis = f.getInputStream();
                b = inputStreamToBytes(fis);
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return b;
    }

    private static byte[] inputStreamToBytes(InputStream in)
        throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;
        boolean empty = true;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
            empty = false;
        }

        out.close();

        if (empty) {
            return null;
        }

        return out.toByteArray();
    }

    public void setValues(EboContext boctx) throws boRuntimeException {
        HttpServletRequest request = boctx.getRequest();
        GtValue v = new GtValue();
        String s = request.getParameter(getHTMLFieldName());

        if ((s != null) && !"".equals(s)) {
            String[] bouis = s.split(";");
            ArrayList r = null;

            for (int i = 0; i < bouis.length; i++) {
                if (r == null) {
                    r = new ArrayList();
                }

                r.add(new Long(Long.parseLong(bouis[i])));
            }

            v.addValues(r);
        }

        setValue(v);
    }

    //    select process_sinistro.intervenientes where boui=1
    public String getBoqlFilter(EboContext boctx) throws boRuntimeException {
        GtQuery q = getQuery();

        if (q != null) {
            GtParametro p = q.getParametro();

            if (p != null) {
                if (p.getValue() != null) {
                    if (p.getValue().getValue() != null) {
                        String boui = ((Long) p.getValue().getValue()).toString();
                        long clsreg = p.getObjecto();
                        boObject clsRegObj = boObject.getBoManager().loadObject(boctx,
                                clsreg);
                        String parentName = clsRegObj.getAttribute("name")
                                                     .getValueString();
 
                        return "select /*TEXINDEXONRETURNOBJECT*/ " + parentName + "." + getHelper() +
                        " where boui = " + boui;
                    }
                }
            }
        }

        long clsreg = getObjecto();
        boObject clsRegObj = boObject.getBoManager().loadObject(boctx, clsreg);
        String parentName = clsRegObj.getAttribute("name").getValueString();

        return "select " + clsRegObj + " where boui = 0";
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

                vStr += ((Long) r.get(i)).toString();
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

    //    public void setClassification(boObject generatedObj)   throws boRuntimeException
    //    {
    //        GtValue v = getValue();
    //        if(v != null && v.getValue() != null)
    //        {
    //            boObject o = boObject.getBoManager().loadObject(generatedObj.getEboContext(), ((Long)v.getValue()).longValue());
    //            if("Pessoa".equals(o.getName()) || 
    //                "Peritagem".equals(o.getName()) ||
    //                "process_sinistro".equals(o.getName())
    //            )
    //            {
    //                if(!generatedObj.getBridge("objReferences").haveBoui(o.getBoui()))
    //                {
    //                    generatedObj.getBridge("objReferences").add(o.getBoui());
    //                }
    //                //Martelada
    //                if("Peritagem".equals(o.getName()))
    //                {
    //                    o = o.getAttribute("sinistro").getObject();
    //                    if(o != null && !generatedObj.getBridge("objReferences").haveBoui(o.getBoui()))
    //                    {
    //                        generatedObj.getBridge("objReferences").add(o.getBoui());
    //                    }
    //                }
    //            }
    //        }
    //    }
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

    public GtCampo[] getAllCampos() {
        return (GtCampo[]) campos.toArray(new GtCampo[campos.size()]);
    }
    
    public void setReferencias(boObject generatedObj)   throws boRuntimeException
    {
        GtValue v = getValue();
        if(v != null && v.getValue() != null)
        {
            ArrayList valores = (ArrayList)v.getValues();
            Long auxL;
            for (int i = 0; i < valores.size(); i++) 
            {
                auxL = (Long)valores.get(i);
                boObject o = boObject.getBoManager().loadObject(generatedObj.getEboContext(), auxL.longValue());
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
