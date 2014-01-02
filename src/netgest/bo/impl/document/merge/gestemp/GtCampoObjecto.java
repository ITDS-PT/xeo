package netgest.bo.impl.document.merge.gestemp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.sql.Types;

import java.util.ArrayList;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.impl.document.merge.Tabela;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

import netgest.io.iFile;
import netgest.io.iFileServer;

import netgest.bo.system.Logger;


public class GtCampoObjecto extends GtCampo {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.impl.document.merge.gestemp.GtCampoObjecto");

    public GtCampoObjecto(GtTemplate template, GtQuery query) {
        super(template, query);
    }

    public GtCampoObjecto(GtCampoNObjecto parentObject) {
        super(parentObject);
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

    public static GtCampoObjecto getCampo(GtCampoNObjecto parentObjecto,
        boObject campo) throws boRuntimeException {
        GtCampoObjecto newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoObjecto(parentObjecto);
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    public static GtCampoObjecto getCampo(GtTemplate template, GtQuery query,
        boObject campo) throws boRuntimeException {
        GtCampoObjecto newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoObjecto(template, query);
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    private static void setCampoValues(GtCampoObjecto newCampo, boObject campo)
        throws boRuntimeException {
        if (campo != null) {
            newCampo.setNome(campo.getAttribute("nome").getValueString());
            newCampo.setDescricao(campo.getAttribute("descricao")
                                       .getValueString());
            newCampo.setPergunta(campo.getAttribute("pergunta")
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

        if (sqlType == Types.BLOB) 
        {
            if (GtTemplate.TEXT_TEMPLATE == templateType) {
                tab.insert("", getHeaderName(), sqlType);
            } else if (GtTemplate.CLF_TEMPALTE == templateType) {
                tab.insert("", getHeaderName(), sqlType);
            } else {
                tab.insert(toBytes(value), getHeaderName(), sqlType);
            }
        } 
        else 
        {
            if( value == null )
            {
                logger.severe(LoggerMessageLocalizer.getMessage("FIELD")+"  ["+attDef.getBoDefHandler().getName()+"."+attDef.getName()+
                        "] "+LoggerMessageLocalizer.getMessage("IS_NULL_IN_MODEL")+" " +
                        " "+LoggerMessageLocalizer.getMessage("HEADER")+":["+this.getHeaderName()+"]"+
                        " "+LoggerMessageLocalizer.getMessage("NAME")+":["+this.getNome()+"]"+
                        " "+LoggerMessageLocalizer.getMessage("QUERY")+":["+this.getQueryName()+"] " +
                        " "+LoggerMessageLocalizer.getMessage("MODEL")+":["+this.getTemplate().getNome()+"] " +
                        " "+LoggerMessageLocalizer.getMessage("USER")+":["+boctx.getSysUser().getUserName()+"] ",
                        new Throwable()
                );
                
            }
            // Esta validação de verificar se está a null deve ser verificada. O log
            // anterior serve para tentar detectar em que casos o valor chega a null.
            tab.insert(Helper.getReturnObject(boctx, attDef, value==null?"":value.getValue(),
                    templateType, getTags()), getHeaderName(), sqlType);
        }

        long tf = System.currentTimeMillis();
        logger.finer(LoggerMessageLocalizer.getMessage("TOTAL_TIME")+" (" +
            ((float) (Math.round((float) (tf - ti) / 100f)) / 10f) +
            "s) "+LoggerMessageLocalizer.getMessage("FIELDOBJECT")+":" + getNome());
    }

    public void setData(EboContext boctx, Tabela tab, byte templateType,
        int index) throws boRuntimeException {
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
                GtValue aux = new GtValue();

                if (value != null) {
                    ArrayList vls = value.getValues();
                    aux.addValue(vls.get(index));
                }

                tab.insert(aux, getHeaderName(), sqlType);
            }
        } else {
            if (value != null) {
                ArrayList vls = value.getValues();
                tab.insert(Helper.getReturnObject(boctx, attDef, vls.get(index),
                        templateType, getTags()), getHeaderName(), sqlType);
            }
        }

        long tf = System.currentTimeMillis();
        logger.finer(LoggerMessageLocalizer.getMessage("TOTAL_TIME")+" (" +
            ((float) (Math.round((float) (tf - ti) / 100f)) / 10f) +
            "s) "+LoggerMessageLocalizer.getMessage("FIELDOBJECT")+":" + getNome());
    }

    public void calculateAutomicFields(EboContext boctx, GtTemplate template)
        throws boRuntimeException {
        GtValue g = null;

        try {
            if (getParentObj() != null) {
                g = getParentObj().getValue();

                if (g != null) {
                    ArrayList values = g.getValues();

                    if (values != null) {
                        ArrayList campoValues = null;

                        for (int i = 0; i < values.size(); i++) {
                            if (campoValues == null) {
                                campoValues = new ArrayList();
                            }

                            Object r = calculate(boctx, ((Long) values.get(i)));
                            campoValues.add(r);
                        }

                        GtValue v = new GtValue();
                        v.addValues(campoValues);
                        setValue(v);
                    }
                }
            } else {
                g = getQuery().getParametro().getValue();

                if (g != null) {
                    if ((g.getValue() != null) && g.getValue() instanceof Long) {
                        GtValue v = new GtValue();
                        Object r = calculate(boctx, (Long) g.getValue());

                        if (r != null) {
                            v.addValue(r);
                        }

                        setValue(v);
                    }
                }
            }
        } catch (Exception e) {
            String msg = LoggerMessageLocalizer.getMessage("UNABLE_TO_CALCUTE")+" [" + getHelper() + "] ";

            if ((g != null) && (g.getValue() != null)) {
                if (g.getValue() instanceof boObject) {
                    msg += (LoggerMessageLocalizer.getMessage("FOR_OBJECT")+" [" +
                    ((boObject) g.getValue()).getBoui() + "/" +
                    ((boObject) g.getValue()).getName() + "]");
                }
            }

            logger.severe(LoggerMessageLocalizer.getMessage("OBJECT_FIELD")+"[" + getNome() + "] : " + msg, e);
            throw new boRuntimeException("",MessageLocalizer.getMessage("UNABLE_TO_CALCULATE")+
                " [" + getPergunta() +
                "]."+MessageLocalizer.getMessage("CHECK_IF_THE_INTRODUCED_VALUE_IS_CORRECT"), null);
        }
    }

    private Object calculate(EboContext boctx, Long valorPai)
        throws boRuntimeException {
        boObject paramObj = boObject.getBoManager().loadObject(boctx,
                valorPai.longValue());
        Object xo = getValue(boctx, paramObj, getHelper());

        return xo;
    }

    private static Object getValue(EboContext boctx, boObject obj,
        String helperValue) throws boRuntimeException {
        if (helperValue != null) {
            String[] helpers = helperValue.split("\\.");

            if (helpers.length == 1) {
                return obj.getAttribute(helpers[0]).getValueObject();
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

        return obj;
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
}
