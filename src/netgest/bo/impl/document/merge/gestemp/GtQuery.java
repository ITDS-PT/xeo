package netgest.bo.impl.document.merge.gestemp;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.impl.document.merge.Tabela;
import netgest.bo.impl.document.merge.gestemp.GtParametro;
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


public class GtQuery {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.impl.document.merge.gestemp.GtCampoManual");

    //atributos do campo
    private String nome = null;
    private String descricao = null;
    private String pergunta = null;
    private String tipoSeleccao = null;
    private String javaCode = null;
    public byte templateType;

    //parametros GtCampoObjecto
    GtParametro parametro;

    //Campos Formula ou Objecto
    ArrayList camposformulas;
    ArrayList camposObjecto;
    ArrayList camposJava;

    //Campos N Formula ou Objecto
    ArrayList camposNformulas;
    ArrayList camposNObjecto;
    ArrayList camposNJava;

    //Campos em ordem
    ArrayList campos;
    private GtTemplate template;
    private String validacao;
    
    //campo que indica se é usado ou não no modelo
    private boolean referenceByTemplate = false;

    public GtQuery(GtTemplate template) {
        camposformulas = new ArrayList();
        camposObjecto = new ArrayList();
        camposJava = new ArrayList();

        camposNformulas = new ArrayList();
        camposNObjecto = new ArrayList();
        camposNJava = new ArrayList();

        campos = new ArrayList();
        this.template = template;
    }

    //Métodos Set's
    public void setNome(String newValue) {
        this.nome = newValue;
    }

    public void setDescricao(String newValue) {
        this.descricao = newValue;
    }

    public void setPergunta(String newValue) {
        this.pergunta = newValue;
    }

    public void setJavaCode(String newValue) {
        this.javaCode = newValue;
    }

    public void setParametro(GtParametro newValue) {
        this.parametro = newValue;
    }

    public void addObjecto(GtCampoObjecto newValue) {
        camposObjecto.add(newValue);
    }

    public void addFormula(GtCampoFormula newValue) {
        camposformulas.add(newValue);
    }

    public void addJava(GtCampoJava newValue) {
        camposJava.add(newValue);
    }

    public void addNObjecto(GtCampoNObjecto newValue) {
        camposNObjecto.add(newValue);
    }

    public void addNFormula(GtCampoNFormula newValue) {
        camposNformulas.add(newValue);
    }

    public void addNJava(GtCampoNJava newValue) {
        camposNJava.add(newValue);
    }

    public void addCampo(GtCampo newValue) {
        campos.add(newValue);
    }

    public void setValidacao(String validacao) {
        this.validacao = validacao;
    }

    //Métodos Get's
    public String getNome() {
        return this.nome;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public String getPergunta() {
        return this.pergunta;
    }

    public String getJavaCode() {
        return this.javaCode;
    }

    public String getValidacao() {
        return this.validacao;
    }

    public GtParametro getParametro() {
        return parametro;
    }

    public GtCampoFormula[] getFormulas() {
        return (GtCampoFormula[]) camposformulas.toArray(new GtCampoFormula[camposformulas.size()]);
    }

    public GtCampoObjecto[] getObjectos() {
        return (GtCampoObjecto[]) camposObjecto.toArray(new GtCampoObjecto[camposObjecto.size()]);
    }

    public GtCampo[] getAllCampos() {
        return (GtCampo[]) campos.toArray(new GtCampo[campos.size()]);
    }

    public static GtQuery getQuery(GtTemplate template, boObject query)
        throws boRuntimeException {
        GtQuery newQuery = null;
        ArrayList fields = template.getTemplateFields();
        ArrayList bookmarks = template.getTemplateBookmarks();
        byte templateType = template.getChannel();

        if (query != null) {
            newQuery = new GtQuery(template);
            newQuery.setNome(query.getAttribute("nome").getValueString());
            newQuery.setDescricao(query.getAttribute("descricao")
                                       .getValueString());
            newQuery.setPergunta(query.getAttribute("pergunta").getValueString());
            newQuery.setValidacao(query.getAttribute("validacao")
                                       .getValueString());

            if (query.getAttribute("javaCode") != null) {
                newQuery.setJavaCode(query.getAttribute("javaCode")
                                          .getValueString());
            }

            //parametro
            boObject aux = null;
            aux = query.getAttribute("parametro").getObject();

//            if (Helper.isMarkedForTemplate(newQuery, aux, template)) {
                GtParametro gp = GtParametro.getCampo(template, newQuery, aux);
                gp.setQueryName(newQuery.getNome());
                newQuery.setParametro(gp);
//            }
            newQuery.setReferenceByTemplate(Helper.referencedByTemplate(newQuery, aux, template.getTemplateFields(), false));

            //campos
            boBridgeIterator bit = query.getBridge("campos").iterator();
            bit.beforeFirst();
            aux = null;

            GtCampoFormula cf = null;
            GtCampoObjecto co = null;
            GtCampoJava cj = null;
            GtCampoNFormula cfn = null;
            GtCampoNObjecto con = null;
            GtCampoNJava cjn = null;

            while (bit.next()) {
                aux = bit.currentRow().getObject();
                if ("GESTEMP_CampoJava".equals(aux.getName())) 
                {
                        cj = GtCampoJava.getCampo(template, newQuery, aux);
                        newQuery.addJava(cj);
                        newQuery.addCampo(cj);
                }
                else if ("GESTEMP_CampoNJava".equals(aux.getName())) {
                        cjn = GtCampoNJava.getCampo(template, newQuery, aux);
                        newQuery.addNJava(cjn);
                        newQuery.addCampo(cjn);
                }
                else if (Helper.isMarkedForTemplate(newQuery, aux, template)) {
                    if ("GESTEMP_CampoFormula".equals(aux.getName())) {
                        cf = GtCampoFormula.getCampo(template, newQuery, aux);
                        newQuery.addFormula(cf);
                        newQuery.addCampo(cf);
                    } else if ("GESTEMP_CampoObjecto".equals(aux.getName())) {
                        co = GtCampoObjecto.getCampo(template, newQuery, aux);
                        newQuery.addObjecto(co);
                        newQuery.addCampo(co);
                    } else if ("GESTEMP_CampoNFormula".equals(aux.getName())) {
                        cfn = GtCampoNFormula.getCampo(template, newQuery, aux);
                        newQuery.addNFormula(cfn);
                        newQuery.addCampo(cfn);
                    } else if ("GESTEMP_CampoNObjecto".equals(aux.getName())) {
                        con = GtCampoNObjecto.getCampo(template, newQuery, aux);
                        newQuery.addNObjecto(con);
                        newQuery.addCampo(con);
                    } 
                }
            }
        }

        return newQuery;
    }

    public void setData(EboContext boctx, Tabela tab, byte templateType)
        throws boRuntimeException {
        //parâmetro
        parametro.setData(boctx, tab, templateType);

        for (int i = 0; i < camposObjecto.size(); i++) {
            ((GtCampoObjecto) camposObjecto.get(i)).setData(boctx, tab,
                templateType);
        }

        for (int i = 0; i < camposformulas.size(); i++) {
            ((GtCampoFormula) camposformulas.get(i)).setData(boctx, tab,
                templateType);
        }

        for (int i = 0; i < camposJava.size(); i++) {
            ((GtCampoJava) camposJava.get(i)).setData(boctx, tab, templateType);
        }
    }

    public void setData(EboContext boctx, String bookmark, Tabela tab,
        byte templateType) throws boRuntimeException 
    {
        String listName = bookmark.split("__")[1];
        boolean found = false;

        //N
        for (int i = 0; i < camposNObjecto.size(); i++) {
            if (listName.equals(
                        ((GtCampoNObjecto) camposNObjecto.get(i)).getNome())) {
                ((GtCampoNObjecto) camposNObjecto.get(i)).setData(boctx, tab,
                    templateType);
                found = true;
            }
        }

        for (int i = 0; !found && (i < camposNformulas.size()); i++) {
            if (listName.equals(
                        ((GtCampoNFormula) camposNformulas.get(i)).getNome())) {
                ((GtCampoNFormula) camposNformulas.get(i)).setData(boctx, tab,
                    templateType);
                found = true;
            }
        }

        for (int i = 0; !found && (i < camposNJava.size()); i++) {
            if (listName.equals(((GtCampoNJava) camposNJava.get(i)).getNome())) {
                ((GtCampoNJava) camposNJava.get(i)).setData(boctx, tab,
                    templateType);
                found = true;
            }
        }
    }

    public void setValues(EboContext boctx) throws boRuntimeException {
        HttpServletRequest request = boctx.getRequest();

        //apenas o parâmetro depende do utilizador
        parametro.setValues(boctx);
    }

    public void setListValues(EboContext boctx) throws boRuntimeException {
        HttpServletRequest request = boctx.getRequest();

        //apenas o parâmetro depende do utilizador
        ArrayList dependents = getListDependents();

        for (int i = 0; i < dependents.size(); i++) {
            ((GtCampoNObjecto) dependents.get(i)).setValues(boctx);
        }
    }

    public void calculateListAutomicFields(EboContext boctx, GtTemplate template)
        throws boRuntimeException {
        HttpServletRequest request = boctx.getRequest();

        //vai calcular os parâmetros automáticos
        ArrayList dependents = getListDependents();

        //N
        for (int i = 0; i < dependents.size(); i++) {
            ((GtCampoNObjecto) dependents.get(i)).calculateAutomicFields(boctx,
                template);
        }
    }

    public void calculateAutomicFields(EboContext boctx, GtTemplate template)
        throws boRuntimeException {
        HttpServletRequest request = boctx.getRequest();

        //vai calcular os parâmetros automáticos
        for (int i = 0; i < camposObjecto.size(); i++) {
            ((GtCampoObjecto) camposObjecto.get(i)).calculateAutomicFields(boctx,
                template);
        }

        for (int i = 0; i < camposformulas.size(); i++) {
            ((GtCampoFormula) camposformulas.get(i)).calculateAutomicFields(boctx,
                template);
        }

        //N
        for (int i = 0; i < camposNObjecto.size(); i++) {
            ((GtCampoNObjecto) camposNObjecto.get(i)).calculateAutomicFields(boctx,
                template);
        }

        for (int i = 0; i < camposNformulas.size(); i++) {
            ((GtCampoNFormula) camposNformulas.get(i)).calculateAutomicFields(boctx,
                template);
        }

        for (int i = 0; i < camposNJava.size(); i++) {
            ((GtCampoNJava) camposNJava.get(i)).calculateAutomicFields(boctx,
                template);
        }

        //os campos do tipo Java dependem do java da query
        if (camposJava.size() > 0) {
            calculateJavaFields(boctx, template);
        }
    }

    public ArrayList getCamposJava() {
        return camposJava;
    }

    public ArrayList getCamposNJava() {
        return camposNJava;
    }

    public ArrayList getCamposNFormulas() {
        return camposNformulas;
    }

    public ArrayList getCamposNObjectos() {
        return camposNObjecto;
    }

    public boObject getAnswerObject(EboContext boctx) throws boRuntimeException {
        //parâmetro
        return parametro.getAnswerObject(boctx);
    }

    public void setReferencias(boObject generatedObj)   throws boRuntimeException
    {
        //parâmetro classificação
        parametro.setReferencias(generatedObj);
        //Campos
//        for (int i = 0; i < camposNJava.size(); i++) 
//        {
//            ((GtCampoNJava)camposNJava.get(i)).setReferencias(generatedObj);
//        }
//        for (int i = 0; i < camposNObjecto.size(); i++) 
//        {
//            ((GtCampoNObjecto)camposNObjecto.get(i)).setReferencias(generatedObj);
//        }
    }
    public void validate(EboContext boctx, ArrayList errors)
        throws boRuntimeException {
        //parâmetro
        if(parametro.getFormula() == null || "".equals(parametro.getFormula()) || parametro.askUser())
        {
            parametro.validate(boctx, errors);
        }
        
        if(template.hasListDependents() && template.hasFillListParams())
        {
            //Campos
            for (int i = 0; i < camposNformulas.size(); i++) 
            {
                ((GtCampoNFormula)camposNformulas.get(i)).validate(boctx, errors);
            }
            for (int i = 0; i < camposNJava.size(); i++) 
            {
                ((GtCampoNJava)camposNJava.get(i)).validate(boctx, errors);
            }
            for (int i = 0; i < camposNObjecto.size(); i++) 
            {
                ((GtCampoNObjecto)camposNObjecto.get(i)).validate(boctx, errors);
            }
        }
        else
        {
            for (int i = 0; i < camposformulas.size(); i++) 
            {
                ((GtCampoFormula)camposformulas.get(i)).validate(boctx, errors);
            }
            for (int i = 0; i < camposJava.size(); i++) 
            {
                ((GtCampoJava)camposJava.get(i)).validate(boctx, errors);
            }
            for (int i = 0; i < camposObjecto.size(); i++) 
            {
                ((GtCampoObjecto)camposObjecto.get(i)).validate(boctx, errors);
            }
        }

        //própria querie
        if ((errors.size() == 0) && (validacao != null) &&
                (validacao.length() > 0)) {
            javaValidation(boctx, errors);
        }
    }

    private boolean javaValidation(EboContext boctx, ArrayList erros)
        throws boRuntimeException 
    {
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
        javaExec.addTypedVariable(getNome(), Query.class,
            new Query(boctx, this), null);
        javaExec.addTypedVariable("modelo", Modelo.class, new Modelo(boctx, template), null);

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
            String sErrorMessage = "Erro a validar query [" + this.getNome() + "].\n" + 
                    javaExec.getErrorMessage();
            contexto.addErro( sErrorMessage );
            return false;
        }
        return true;
    }

    public void calculateJavaFields(EboContext boctx, GtTemplate template)
        throws boRuntimeException 
    {

        long init = System.currentTimeMillis();

        GtParametro param = getParametro();
        Object o = param.getValue() != null ? param.getValue().getValue():null;

        if ( o != null && String.valueOf( o ).length() > 0 ) 
        {
            JavaExecuter javaExec = new JavaExecuter(getNome());

            //imports
            javaExec.addImport("netgest.bo");
            javaExec.addImport("netgest.bo.def");
            javaExec.addImport("netgest.utils");
            javaExec.addImport("netgest.bo.runtime");
            javaExec.addImport("netgest.bo.utils");
            javaExec.addImport("netgest.bo.impl.document.merge.gestemp");
            javaExec.addImport("netgest.bo.impl.document.merge.gestemp.validation");

            //variaveis
            javaExec.addTypedVariable("contexto", Contexto.class,
                new Contexto(boctx), null);
            javaExec.addTypedVariable("modelo", Modelo.class,
                new Modelo(boctx, template), null);
            javaExec.addTypedVariable(getNome(), Query.class,
                new Query(boctx, this), null);

            //javaCode
            javaExec.setJavaCode(getJavaCode());
            javaExec.execute();
            
            if( !javaExec.sucess() )
            {
                    throw new boRuntimeException("",
                        "Não foi possível calcular [" + getPergunta() +
                        "]. Verifique a formula.\n" + javaExec.getErrorMessage(), null);
            }
        
            logger.info("Java caculate campo ["+getNome()+"] ["+(System.currentTimeMillis()-init)+"]ms");
        }
    }  

    public void setCampos(String[] fieldsName, Object[] fieldsValue) {
        ArrayList campos = getCamposJava();
        String cNome;
        int pos = -1;
        GtValue val;

        for (int i = 0; i < campos.size(); i++) {
            cNome = ((GtCampo) campos.get(i)).getNome();

            if ((pos = getPos(fieldsName, cNome)) >= 0) {
                val = new GtValue();
                val.addValue(fieldsValue[pos]);
                ((GtCampo) campos.get(i)).setValue(val);
            }
        }
    }

    private int getPos(String[] fieldsName, String name) {
        if ((fieldsName != null) && (fieldsName.length > 0) && (name != null)) {
            for (int i = 0; i < fieldsName.length; i++) {
                if (name.equalsIgnoreCase(fieldsName[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    public void setAnswer(bridgeHandler bh) throws boRuntimeException {
        parametro.setAnswer(bh);

        ArrayList r = getListDependents();

        for (int i = 0; i < r.size(); i++) {
            ((GtCampoNObjecto) r.get(i)).setAnswer(bh);
        }
    }

    public ArrayList getListDependents() {
        ArrayList toRet = new ArrayList();
        ArrayList r = getCamposNObjectos();

        if (r != null) {
            for (int i = 0; i < r.size(); i++) {
                if ("2".equals(((GtCampoNObjecto) r.get(i)).getTipoSeleccao())) {
                    toRet.add((GtCampoNObjecto) r.get(i));
                }
            }
        }

        return toRet;
    }

    public boolean hasListDependents() {
        ArrayList r = getCamposNObjectos();

        if (r != null) {
            for (int i = 0; i < r.size(); i++) {
                if ("2".equals(((GtCampoNObjecto) r.get(i)).getTipoSeleccao())) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setReferenceByTemplate(boolean value)
    {
//        System.out.println(getNome() + " - " + (value ? "Sim":"Não"));
        this.referenceByTemplate = value;
    }
    
    public boolean referenceByTemplate()
    {
        return referenceByTemplate;
    }
}
