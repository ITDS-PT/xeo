package netgest.bo.impl.document.merge.gestemp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.impl.document.merge.Tabela;
import netgest.bo.impl.document.merge.gestemp.validation.Campo;
import netgest.bo.impl.document.merge.gestemp.validation.Contexto;
import netgest.bo.impl.document.merge.gestemp.validation.JavaExecuter;
import netgest.bo.impl.document.merge.gestemp.validation.Modelo;
import netgest.bo.impl.document.merge.gestemp.validation.Query;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import org.apache.log4j.Logger;


public abstract class GtCampo {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.impl.document.merge.gestemp.GtCampo");

    //atributos do campo
    private String nome = null;
    private String pergunta = null;
    private String descricao = null;
    private String tipo = null;

    //minimo maximo
    private long min = Long.MIN_VALUE;
    private long max = Long.MAX_VALUE;
    private long objecto = -1;
    private long atributo = -1;
    private String helper = null;
    private String tipoSeleccao = "1";
    private String formula = null;
    private String validacao = null;
    private long parametro = -1;
    private boolean obrigatorio = false;
    private boolean askUser = false;
    private long textLov = -1;
    protected GtValue value = null;
    private ArrayList tags = new ArrayList();
    private GtQuery query = null;
    private GtTemplate template = null;
    private GtCampo parentObject = null;
    //campo que indica se é usado ou não no modelo
    private boolean referenceByTemplate = false;

    public GtCampo(GtTemplate template, GtQuery query) {
        this.template = template;
        this.query = query;
    }

    public GtCampo(GtCampo parentObject) {
        this.template = parentObject.getTemplate();
        this.query = parentObject.getQuery();
        this.parentObject = parentObject;
    }

    //Métodos Set's
    public void setNome(String newValue) {
        this.nome = newValue;
    }

    public void setPergunta(String newValue) {
        this.pergunta = newValue;
    }

    public void setDescricao(String newValue) {
        this.descricao = newValue;
    }

    public void setTipo(String newValue) {
        this.tipo = newValue;
    }

    public void setObjecto(long newValue) {
        this.objecto = newValue;
    }

    public void setAtributo(long newValue) {
        this.atributo = newValue;
    }

    public void setHelper(String newValue) {
        this.helper = newValue;
    }

    public void setTipoSeleccao(String newValue) {
        this.tipoSeleccao = newValue;
    }

    public void setFormula(String newValue) {
        this.formula = newValue;
    }

    public void setValidacao(String newValue) {
        this.validacao = newValue;
    }

    public void setObrigatorio(String newValue) {
        if ("1".equals(newValue) || "S".equalsIgnoreCase(newValue) ||
                "Sim".equals(newValue) || "y".equalsIgnoreCase(newValue) ||
                "Yes".equalsIgnoreCase(newValue)) {
            this.obrigatorio = true;
        }
    }

    public void setAskUser(String newValue) {
        if ("1".equals(newValue) || "S".equalsIgnoreCase(newValue) ||
                "Sim".equals(newValue) || "y".equalsIgnoreCase(newValue) ||
                "Yes".equalsIgnoreCase(newValue)) {
            this.askUser = true;
        }
    }

    public void setParametro(long newValue) {
        this.parametro = newValue;
    }

    public void setValue(GtValue newValue) {
        this.value = newValue;
    }

    public void setTextos(long newValue) {
        this.textLov = newValue;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public void addTag(GtTag tag) {
        boolean found = false;

        for (int i = 0; i < tags.size(); i++) {
            if (((GtTag) tags.get(i)).getName().equals(tag.getName())) {
                found = true;
            }
        }

        if (!found) {
            tags.add(tag);
        }
    }

    //Métodos Get's
    public GtQuery getQuery() {
        return query;
    }

    public GtTemplate getTemplate() {
        return template;
    }

    public GtCampo getParentObj() {
        return parentObject;
    }

    public String getQueryName() {
        if (getQuery() != null) {
            return getQuery().getNome();
        }

        return null;
    }

    public String getNome() {
        return this.nome;
    }

    public String getPergunta() {
        return this.pergunta;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public String getTipo() {
        if ((this.tipo == null) || "".equals(tipo)) {
            return "1";
        }

        return this.tipo;
    }

    public long getObjecto() {
        return this.objecto;
    }

    public long getAtributo() {
        return this.atributo;
    }

    public String getHelper() {
        return this.helper;
    }

    public String getTipoSeleccao() {
        return this.tipoSeleccao;
    }

    public String getFormula() {
        return this.formula;
    }

    public String getValidacao() {
        return this.validacao;
    }

    public boolean getObrigatorio() {
        return this.obrigatorio;
    }

    public boolean askUser() {
        return this.askUser;
    }

    public long getParametro() {
        return this.parametro;
    }

    public GtValue getValue() {
        return value;
    }

    public ArrayList getTags() {
        return tags;
    }

    public long getTextos() {
        return this.textLov;
    }

    public long getMin() {
        return this.min;
    }

    public long getMax() {
        return this.max;
    }

    public void setData(EboContext boctx, Tabela tab, byte templateType)
        throws boRuntimeException {
        Object returnedObject = null;
        if ((Integer.parseInt(getTipo()) == 8) && (value != null) &&
                (value.getValue() != null)) {
            byte[] b = null;
            File f = new File((String) value.getValue());

            if (f.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(f);
                    b = inputStreamToBytes(fis);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                returnedObject = Helper.getReturnObject(boctx,
                        Integer.parseInt(getTipo()), b, templateType, getTags()); 
                tab.insert(returnedObject,
                    getHeaderName(),
                    Helper.getSqlTypeFromGesDocTipoCampoLov(Integer.parseInt(
                            getTipo()), templateType, returnedObject));
            } else {
                returnedObject = Helper.getReturnObject(boctx,
                        Integer.parseInt(getTipo()), null, templateType,
                        getTags());
                tab.insert(returnedObject, getHeaderName(),
                    Helper.getSqlTypeFromGesDocTipoCampoLov(Integer.parseInt(
                            getTipo()), templateType, returnedObject));
            }
        } else {
            returnedObject = Helper.getReturnObject(boctx,
                    Integer.parseInt(getTipo()),
                    (value != null) ? value.getValue() : value, templateType,
                    getTags()); 
            tab.insert(returnedObject, getHeaderName(),
                Helper.getSqlTypeFromGesDocTipoCampoLov(Integer.parseInt(
                        getTipo()), templateType, returnedObject));
        }
    }

    public void setValues(EboContext boctx) throws boRuntimeException {
        HttpServletRequest request = boctx.getRequest();
        String s = request.getParameter(getHTMLFieldName());
        logger.debug(getHTMLFieldName() + "->" + s);

        if (Integer.parseInt(getTipo()) == 4) {
            String time = request.getParameter("_ignore_" + getHTMLFieldName());

            if ((time != null) && !"".equals(time)) {
                s += (" " + time);
            }

            if ((s != null) && !"".equals(s)) {
                s = s.replaceAll("////", "-");
            }
        }

        setValueString(s);
    }

    public void setValueString(String s) throws boRuntimeException {
        if ((s != null) && !"".equals(s)) {
            GtValue v = new GtValue();
            v.addValue(s);
            setValue(v);
        } else {
            setValue(null);
        }
    }

    public void calculateAutomicFields(EboContext boctx, GtTemplate template)
        throws boRuntimeException {
    }

    public String getHeaderName() {
        return getNome();
    }

    public String getHTMLFieldName() {
        return getNome();
    }

    public String getHTMLFieldID() {
        return getNome();
    }

    public void validate(EboContext boctx, ArrayList erros)
        throws boRuntimeException {
        if (getObrigatorio()) {
            GtValue v = getValue();

            if ((v == null) ||
                    ((v.getValue() == null) &&
                    ((v.getValues() == null) || (v.getValues().size() == 0)))) {
                StringBuffer aux = new StringBuffer("Preencha o campo obrigatório [");
                if(getQuery() != null)
                {
                    aux.append(getQuery().getPergunta()).append(" - ").append(getPergunta()).append("]");
                }
                else
                {
                    aux.append(getPergunta()).append("]");
                }
                
                if (erros.indexOf(aux.toString()) == -1) {
                    erros.add(aux.toString());
                }
            } 
            else {
                if ((validacao != null) && (validacao.length() > 0)) {
                    javaValidation(boctx, erros);
                }
            }
        }
        else if ((validacao != null) && (validacao.length() > 0))
        {
            javaValidation(boctx, erros);
        }
    }

    public boolean javaValidation(EboContext boctx, ArrayList erros)
        throws boRuntimeException {
        JavaExecuter javaExec = new JavaExecuter(getNome());

        //imports
        javaExec.addImport("netgest.bo");
        javaExec.addImport("netgest.bo.def");
        javaExec.addImport("netgest.utils");
        javaExec.addImport("netgest.bo.runtime");
        javaExec.addImport("netgest.bo.utils");
        javaExec.addImport("netgest.bo.impl.document.merge.gestemp");

        //variaveis
        Contexto contexto = new Contexto(boctx);
        javaExec.addTypedVariable("contexto", Contexto.class, contexto, null);
        javaExec.addTypedVariable(getNome(), Campo.class,
            new Campo(boctx, this), null);

        if (query != null) {
            javaExec.addTypedVariable("query", Query.class,
                new Query(boctx, query), null);
        } else {
            javaExec.addTypedVariable("query", Query.class, null, null);
        }

        javaExec.addTypedVariable("modelo", Modelo.class,
            new Modelo(boctx, template), null);

        //javaCode
        javaExec.setJavaCode(validacao);

        Object result = javaExec.execute();
        if( javaExec.sucess() )
        {
        if ((result != null) && result instanceof Boolean) {
            if (!((Boolean) result).booleanValue()) {
                for (int i = 0; i < contexto.getErros().size(); i++) {
                    erros.add(contexto.getErros().get(i));
                }

                return false;
            }
            }
        }
        else
        {
            String sErrorMessage = "Erro a validar campo [" + this.getNome() + "].\n";
            if( this.getQuery() != null )
            {
                sErrorMessage += " da Query [" + this.getQuery().getNome() + "]";
            }
            sErrorMessage += "\n" + javaExec.getErrorMessage();
            contexto.addErro( sErrorMessage );
            return false;
        }

        return true;
    }

    public void setTags(bridgeHandler bh) throws boRuntimeException {
        if (bh != null) {
            boBridgeIterator bit = bh.iterator();
            bit.beforeFirst();

            boObject obj;
            GtTag tag;

            while (bit.next()) {
                obj = bit.currentRow().getObject();
                tag = new GtTag(obj.getAttribute("nome").getValueString(),
                        "1".equals(obj.getAttribute("applyWord").getValueString()),
                        "1".equals(obj.getAttribute("applyText").getValueString()),
                        "1".equals(obj.getAttribute("wordTag").getValueString()),
                        obj.getAttribute("javaCode").getValueString());
                addTag(tag);
            }
        }
    }

    private byte[] inputStreamToBytes(InputStream in) throws IOException {
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
    public void setReferenceByTemplate(boolean value)
    {
//        System.out.println((getQuery() == null ? "":(getQuery().getNome() + "__" ))+ getNome() + " - " + (value ? "Sim":"Não"));
        this.referenceByTemplate = value;
    }
    
    public boolean referenceByTemplate()
    {
        return referenceByTemplate;
    }
}
