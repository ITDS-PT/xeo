package netgest.bo.impl.document.merge.gestemp;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import netgest.bo.boConfig;
import netgest.bo.controller.Controller;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.impl.document.merge.MergeResultSetFactory;
import netgest.bo.impl.document.merge.Tabela;
import netgest.bo.impl.document.merge.gestemp.presentation.GtTemplateViewer;
import netgest.bo.impl.document.merge.gestemp.validation.Contexto;
import netgest.bo.impl.document.merge.gestemp.validation.JavaExecuter;
import netgest.bo.impl.document.merge.gestemp.validation.Modelo;
import netgest.bo.impl.document.print.GDOCXUtilsStub;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.io.iFile;
import netgest.io.iFilePermissionDenied;
import netgest.utils.CommandLineExecuter;

import org.apache.log4j.Logger;

import com.softartisans.wordwriter.WordTemplate;

//TODO:Implement Interface LUSITANIA
//import pt.lusitania.events.Message;
//TODO:Implement Interface LUSITANIA
//import pt.lusitania.gd.ctt.NumeroRegistado;

public class GtTemplate 
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.impl.document.merge.gestemp.GtCampoManual"); //$NON-NLS-1$

    private final static String WORDPROCESS_SERIAL_KEY = "PFKFGU-54LF-KFEE-W5LBMA"; //$NON-NLS-1$

    //External Program
    private final static String CSCRIPT = boConfig.getCScriptPath();
    private final static String TRATADOC = boConfig.getVBProgPath();
    
    public static final byte TYPE_OMISSO = 0;
    public static final byte TYPE_CARTA = 1;
    public static final byte TYPE_FAX = 2;
    public static final byte TYPE_SMS = 3;
    public static final byte TYPE_EMAIL = 4;
    
    public static final byte ATTACH_FORBIDDEN = 1;
    public static final byte ATTACH_OPTIONAL = 2;
    public static final byte ATTACH_REQUIRED = 3;
    
    //fields
    private ArrayList fields;
    private ArrayList bookmarks;
    
    
    //TEMPLATE TYPES
    public static final byte TEXT_TEMPLATE = 0;
    public static final byte WORD_TEMPALTE = 1;
    public static final byte CLF_TEMPALTE = 2;
    
    //boui
    private long boui = -1;
    
    //atributos do template
    private String codigo = null;
    private String nome = null;
    private String descricao = null;
    private boolean canEdit = true;
    private String printType = null;
    
    //atributo validação deverá ser corrido antes de gerar e conforme o retorno
    //gera ou não
    private String validacao = null;
    
    //se a geração de vem de uma actividade do workflow
    private long owfBoui = -1;
    
    //help url
    private String helpUrl = null;
    
    //campos manuais
    private ArrayList manuais = null;
    private ArrayList manuaisSimples = null;
    private ArrayList manuaisLista = null;

    //Queries
    private ArrayList queries = null;

    //templates
    private iFile tempOmissao = null;
    private iFile tempCarta = null;
    private iFile tempFax = null;
    //template de SMS texto
    private String tempSMS = null;
    //template de Email texto - assunto e texto
    private String tempEmailAssunto = null;
    private String tempEmailTexto = null;
    private boolean anexarCarta = false;
    private boolean anexarFax = false;
    
    //folhas de rosto
    private iFile rostoOmissao = null;
    private iFile rostoCarta = null;
    private iFile rostoFax = null;
    
    //type
    private boolean hasSelected = false;
    private byte channel = -1;
    private byte allowattachs = ATTACH_FORBIDDEN;
    
    //modeTes
    private boolean modeTest = false;
    
    //objecto que gerou
    long genBoui = -1;
    
    //mensagem que gerou
    long actSendBoui = -1;
    
    //identificador da mensagem
    String  msgId = null;
    
    //Erros
    private String erro;

    private boolean fillParams = false;
    private boolean fillListParams = false;

    //Tipos de Fax's
    private boolean hasSelectedFaxType = false;
    private boolean urgent = false;
    private static final String FAX_URGENT_PRG = "faxUrgent"; //$NON-NLS-1$
    private boolean review = false;
    private static final String FAX_REVIEW_PRG = "faxReview"; //$NON-NLS-1$
    private boolean coment = false;
    private static final String FAX_COMENT_PRG = "faxComent"; //$NON-NLS-1$
    private boolean answer = false;
    private static final String FAX_ANSWER_PRG = "faxAnswer"; //$NON-NLS-1$
    private String toFaxNumber = ""; //$NON-NLS-1$
    private static final String FAX_NUMBER_PRG = "faxNumber"; //$NON-NLS-1$
    private String toEmailAddress = ""; //$NON-NLS-1$
    private static final String EMAIL_ADDRESS_PRG = "emailAddress"; //$NON-NLS-1$
    private String msgAnexos = ""; //$NON-NLS-1$
    private static final String FAX_ANEXOS_PRG = "faxAnexos"; //$NON-NLS-1$
    private static final String MSG_ANEXOS_PRG = "msgAnexos"; //$NON-NLS-1$
    
    //Envios para as cartas
    private boolean hasSelectedLetterType = false;
    private boolean simples = false;
    private static final String LETTER_SIMPLES = "letterSimples"; //$NON-NLS-1$
    private boolean registada = false;
    private static final String LETTER_REGISTADA = "letterRegistada"; //$NON-NLS-1$
    private boolean avisoRecepcao = false;
    private static final String LETTER_AVISO = "letterAviso"; //$NON-NLS-1$
    //propriedades do template para o envio de cartas
    private String letterType = null;
    private boolean canChooseLetterType = false;
    private String registoNr=""; //$NON-NLS-1$
    private String codeBarClienteCTT=""; //$NON-NLS-1$

    //reply; replyAll; reencaminhar
    private String sendType = null;
    private long msgReplyFromBoui = -1;
    private long actvReplyFromBoui = -1;
    
    //protocolo Aprovação
    private long protocoloBoui = -1;
    private long signatureBoui = -1;
    private Date signatureDate = null;
    
    //tags
    private static final String TAG_CTTCODEBAR39N = Helper.getWordTag("cttcodbar39n"); //$NON-NLS-1$
    private static final String TAG_CTTCODEBAR39N_PRF = TAG_CTTCODEBAR39N+"%*"; //$NON-NLS-1$
    private static final String TAG_CTTCODEBAR39N_PRFNOR = TAG_CTTCODEBAR39N+"*"; //$NON-NLS-1$
    private static final String TAG_CTTCODEBAR39N_SFX = "*"+TAG_CTTCODEBAR39N; //$NON-NLS-1$
    private static final String TAG_DCRRRM4SCC = Helper.getWordTag("dcrrrm4scc"); //$NON-NLS-1$
    private static final String TAG_DCRRRM4SCC_PRF = TAG_DCRRRM4SCC + "("; //$NON-NLS-1$
    private static final String TAG_DCRRRM4SCC_SFX = ")" + TAG_DCRRRM4SCC; //$NON-NLS-1$

    public GtTemplate()
    {
        manuais = new ArrayList();
        manuaisSimples = new ArrayList();
        manuaisLista = new ArrayList();
        queries = new ArrayList();
    }
    
    //Métodos Set's
    public void setBoui(long boui)
    {
        this.boui = boui;
    }
    
    public void setValidacao(String validacao)
    {
        this.validacao = validacao;
    }
    
    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }
    
    public void setNome(String nome)
    {
        this.nome = nome;
    }
    
    public void setHelpURL(String url)
    {
        this.helpUrl = url;
    }
    
    public void setDescricao(String descricao)
    {
        this.descricao = descricao;
    }
    
    public void setEdit(boolean newValue)
    {
        this.canEdit = newValue;
    }
    
    public void setPrintType(String newValue)
    {
        this.printType = newValue;
    }
    
    public void setTempOmissao(iFile file)
    {
        this.tempOmissao = file;
    }
    
    public void setTempCarta(iFile file)
    {
        this.tempCarta = file;
    }
    
    public void setTempSMS(String smsTexto)
    {
        this.tempSMS = smsTexto;
    }
    
    public void setTempEmail(String emailTexto)
    {
        this.tempEmailTexto = emailTexto;
    }
    
    public void setTempEmailAssunto(String emailAssunto)
    {
        this.tempEmailAssunto = emailAssunto;
    }

    public void setAnexarCarta(boolean newValue)
    {
        this.anexarCarta = newValue;
    }
    
    public void setAnexarFax(boolean newValue)
    {
        this.anexarFax = newValue;
    }
    
    public void setTempFax(iFile file)
    {
        this.tempFax = file;
    }
    
    public void setRostoOmissao(iFile file)
    {
        this.rostoOmissao = file;
    }
    
    public void setRostoCarta(iFile file)
    {
        this.rostoCarta = file;
    }
    
    public void setRostoFax(iFile file)
    {
        this.rostoFax = file;
    }
    
    public void setOwfBoui(long newValue)
    {
        this.owfBoui = newValue;
    }
    
    public void setCanChooseLetterType(boolean newValue)
    {
        this.canChooseLetterType = newValue;
    }
    
    public void setLetterType(String newValue)
    {
        simples = false;registada=false;avisoRecepcao=false;
        hasSelectedLetterType = false;
        this.letterType = newValue;
        if("0".equals(newValue)) //$NON-NLS-1$
        {
            simples = true;
        }
        else if("1".equals(newValue)) //$NON-NLS-1$
        {
            registada = true;
        }
        else if("2".equals(newValue)) //$NON-NLS-1$
        {
            avisoRecepcao = true;
        }
        else
        {
            simples = true;
        }
        if(!canChooseLetterType) hasSelectedLetterType = true;
    }
    
    
    private void addCampoManual(GtCampo newValue)
    {
        if(newValue != null)
        {
            manuais.add(newValue);
        }
    }
    private void addCampoManualSimples(GtCampoManual newValue)
    {
        if(newValue != null)
        {
            manuaisSimples.add(newValue);
        }
    }
    private void addCampoManualLista(GtCampoNManual newValue)
    {
        if(newValue != null)
        {
            manuaisLista.add(newValue);
        }
    }
    
    private void addQuery(GtQuery newValue)
    {
        if(newValue != null)
        {
            queries.add(newValue);
        }
    }
    
    public void setSendType(String sendType)
    {
        this.sendType = sendType;
    }
    
    public void setMsgReplyFrom(long msgReplyFromBoui)
    {
        this.msgReplyFromBoui = msgReplyFromBoui;
    }
    
    public void setActvReplyFrom(long actvReplyFromBoui)
    {
        this.actvReplyFromBoui = actvReplyFromBoui;
    }
    
    public void setProtocolo(long protocoloBoui)
    {
        this.protocoloBoui = protocoloBoui;
    }
    
    public void setSignatureUserBoui(long boui)
    {
        signatureBoui = boui;
    }
    
    public void setSignatureDate(Date signDate)
    {
        signatureDate = signDate;
    }
    
    public void setMsgID(String msgId)
    {
        this.msgId = msgId;
    }
    
    //Métodos Get's
    public long getBoui()
    {
        return this.boui;
    }
    
    public String getCode()
    {
        return this.codigo;
    }
    
    public String getNome()
    {
        return this.nome;
    }
    
    public String getHelpURL()
    {
        return this.helpUrl;
    }
    
    public String getDescricao()
    {
        return this.descricao;
    }
    
    public boolean getEdit()
    {
        return this.canEdit;
    }
    
    public boolean isLocalPrint()
    {
        return "1".equals(printType) || "3".equalsIgnoreCase(printType); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public boolean isCentralPrint()
    {
        return "2".equals(printType) || "3".equalsIgnoreCase(printType); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public boolean isLocalAndCentralPrint()
    {
        return "3".equalsIgnoreCase(printType); //$NON-NLS-1$
    }
    
    public boolean isOnlyLocalPrint()
    {
        return "1".equals(printType); //$NON-NLS-1$
    }
    
    public boolean isOnlyCentralPrint()
    {
        return "2".equals(printType); //$NON-NLS-1$
    }
    
    public iFile getTempOmissao()
    {
        return this.tempOmissao;
    }
    
    public iFile getTempCarta()
    {
        return this.tempCarta;
    }
    
    public String getTempSMS()
    {
        return this.tempSMS;
    }
    
    public String getTempEmail()
    {
        return this.tempEmailTexto;
    }
    
    public String getTempEmailAssunto()
    {
        return this.tempEmailAssunto;
    }
    
    public boolean canChooseLetterType()
    {
        return this.canChooseLetterType;
    }
    
    public String getRegistoNr()
    {
        return registoNr;
    }
    
    public String getCodeBarCliente()
    {
        return codeBarClienteCTT;
    }
    public boolean anexarCarta()
    {
        return this.anexarCarta;
    }
    
    public boolean anexarFax()
    {
        return this.anexarFax;
    }
    
    public iFile getTempFax()
    {
        return this.tempFax;
    }
    
    public iFile getRostoOmissao()
    {
        return this.rostoOmissao;
    }
    
    public iFile getRostoCarta()
    {
        return this.rostoCarta;
    }
    
    public iFile getRostoFax()
    {
        return this.rostoFax;
    }
    
    
    public byte getChannel()
    {
        return channel;
    }
    
    public long getOwfBoui()
    {
        return this.owfBoui;
    }
    
    public String getMsgID()
    {
        return this.msgId;
    }
    
    public GtCampo[] getCamposManuais()
    {
        return (GtCampo[])manuais.toArray(new GtCampo[manuais.size()]);
    }
    
    public GtCampoManual[] getCamposManuaisSimples()
    {
        return (GtCampoManual[])manuaisSimples.toArray(new GtCampoManual[manuaisSimples.size()]);
    }
    
    public GtCampoNManual[] getCamposManuaisLista()
    {
        return (GtCampoNManual[])manuaisLista.toArray(new GtCampoNManual[manuaisLista.size()]);
    }
    
    public GtQuery[] getQueries()
    {
        return (GtQuery[])queries.toArray(new GtQuery[queries.size()]);
    }
    
    public static GtTemplate getTemplate(boObject template) throws boRuntimeException
    {
        GtTemplate newTemplate = null;
        if(template != null)
        {
            newTemplate = new GtTemplate();
            newTemplate.setBoui(template.getBoui());
            newTemplate.setCodigo(
                template.getAttribute("code").getValueString() //$NON-NLS-1$
            );
            newTemplate.setNome(
                template.getAttribute("nome").getValueString() //$NON-NLS-1$
            );
            newTemplate.setDescricao(
                template.getAttribute("descricaoBreve").getValueString() //$NON-NLS-1$
            );
            newTemplate.setEdit(
                "1".equals(template.getAttribute("editavel").getValueString()) //$NON-NLS-1$ //$NON-NLS-2$
            );
            newTemplate.setPrintType(
                template.getAttribute("impLocal").getValueString() //$NON-NLS-1$
            );
            newTemplate.setDescricao(
                template.getAttribute("descricaoBreve").getValueString() //$NON-NLS-1$
            );
            newTemplate.setHelpURL(
                template.getAttribute("helpURL").getValueString() //$NON-NLS-1$
            );
            newTemplate.setValidacao(
                template.getAttribute("validacao").getValueString() //$NON-NLS-1$
            );
            
            //campos relativos ao envio de carta
            newTemplate.setCanChooseLetterType(
                 "1".equals(template.getAttribute("envioUtilizador").getValueString()) //$NON-NLS-1$ //$NON-NLS-2$
            );
            newTemplate.setLetterType(
                template.getAttribute("envioCarta").getValueString() //$NON-NLS-1$
            );
            
            //Templates
            if(template.getAttribute("tempOmissao").getObject() != null) //$NON-NLS-1$
            {
                newTemplate.setTempOmissao(
                    template.getAttribute("tempOmissao").getObject().getAttribute("file").getValueiFile() //$NON-NLS-1$ //$NON-NLS-2$
                );
            }
            if(template.getAttribute("tempCarta").getObject() != null) //$NON-NLS-1$
            {
                newTemplate.setTempCarta(
                    template.getAttribute("tempCarta").getObject().getAttribute("file").getValueiFile() //$NON-NLS-1$ //$NON-NLS-2$
                );
            }
            if(template.getAttribute("tempFax").getObject() != null) //$NON-NLS-1$
            {
                newTemplate.setTempFax(
                    template.getAttribute("tempFax").getObject().getAttribute("file").getValueiFile() //$NON-NLS-1$ //$NON-NLS-2$
                );
            }
            if(template.getAttribute("tempSMS").getObject() != null) //$NON-NLS-1$
            {
                newTemplate.setTempSMS(
                    template.getAttribute("tempSMS").getObject().getAttribute("texto").getValueString() //$NON-NLS-1$ //$NON-NLS-2$
                );
            }
            if(template.getAttribute("tempEmail").getObject() != null) //$NON-NLS-1$
            {
                boObject tempEmailObj = template.getAttribute("tempEmail").getObject();   //$NON-NLS-1$
                newTemplate.setTempEmail(
                    tempEmailObj.getAttribute("texto").getValueString() //$NON-NLS-1$
                );
                newTemplate.setTempEmailAssunto(
                    tempEmailObj.getAttribute("assunto").getValueString() //$NON-NLS-1$
                );
                newTemplate.setAnexarCarta("1".equals(tempEmailObj.getAttribute("anexarCarta").getValueString())); //$NON-NLS-1$ //$NON-NLS-2$
                newTemplate.setAnexarFax("1".equals(tempEmailObj.getAttribute("anexarFax").getValueString())); //$NON-NLS-1$ //$NON-NLS-2$
                //Deve faltar os anexos
            }
            
            //Rostos
            if(template.getAttribute("rostoOmissao").getObject() != null) //$NON-NLS-1$
            {
                newTemplate.setRostoOmissao(
                    template.getAttribute("rostoOmissao").getObject().getAttribute("file").getValueiFile() //$NON-NLS-1$ //$NON-NLS-2$
                );
            }
            if(template.getAttribute("rostoCarta").getObject() != null) //$NON-NLS-1$
            {
                newTemplate.setRostoCarta(
                    template.getAttribute("rostoCarta").getObject().getAttribute("file").getValueiFile() //$NON-NLS-1$ //$NON-NLS-2$
                );
            }
            if(template.getAttribute("rostoFax").getObject() != null) //$NON-NLS-1$
            {
                newTemplate.setRostoFax(
                    template.getAttribute("rostoFax").getObject().getAttribute("file").getValueiFile() //$NON-NLS-1$ //$NON-NLS-2$
                );
            }
            
            //Canal por omissão
            String cOmisso = template.getAttribute("canalOmisso").getValueString();  //$NON-NLS-1$
            if("1".equals(cOmisso)) //$NON-NLS-1$
            {
                newTemplate.setChannel(TYPE_CARTA);
            }
            else if("2".equals(cOmisso)) //$NON-NLS-1$
            {
                newTemplate.setChannel(TYPE_FAX);
            }
            else if("3".equals(cOmisso)) //$NON-NLS-1$
            {
                newTemplate.setChannel(TYPE_EMAIL);
            }
            else if("4".equals(cOmisso)) //$NON-NLS-1$
            {
                newTemplate.setChannel(TYPE_SMS);
            }
            
            String allowAttachs = template.getAttribute("permiteAnexos").getValueString(); //$NON-NLS-1$
            if( "1".equals( allowAttachs ) ) //$NON-NLS-1$
            {
                newTemplate.setAllowAttachs( GtTemplate.ATTACH_FORBIDDEN );
            }
            else if( "2".equals( allowAttachs ) ) //$NON-NLS-1$
            {
                newTemplate.setAllowAttachs( GtTemplate.ATTACH_OPTIONAL );
            }
            else if( "3".equals( allowAttachs ) ) //$NON-NLS-1$
            {
                newTemplate.setAllowAttachs( GtTemplate.ATTACH_REQUIRED );
            }
            else
            {
                newTemplate.setAllowAttachs( GtTemplate.ATTACH_FORBIDDEN );
            }
            
            //protocolo
            if(template.getAttribute("protocolo").getValueLong() > -1) //$NON-NLS-1$
            {
                newTemplate.setProtocolo(
                    template.getAttribute("protocolo").getValueLong() //$NON-NLS-1$
                );
            }
            
//            //campos manuais
//            boBridgeIterator bit = template.getBridge("camposManuais").iterator();
//            bit.beforeFirst();
//            boObject aux = null;
//            while(bit.next())
//            {
//                aux = bit.currentRow().getObject();
//                newTemplate.addCampoManual(GtCampoManual.getCampo(aux));
//            }
//
//            //Queries
//            bit = template.getBridge("queries").iterator();
//            bit.beforeFirst();
//            aux = null;
//            while(bit.next())
//            {
//                aux = bit.currentRow().getObject();
//                newTemplate.addQuery(GtQuery.getQuery(aux));
//            }
            
        }
        return newTemplate;
    }
    
    private void getFieldAndqueries(EboContext boctx) throws boRuntimeException
    {
        boObject template = boObject.getBoManager().loadObject(boctx, getBoui());
        //campos manuais
        boBridgeIterator bit = template.getBridge("camposManuais").iterator(); //$NON-NLS-1$
        bit.beforeFirst();
        boObject aux = null;
        GtCampoManual cm = null;
        GtCampoNManual cnm = null;
        while(bit.next())
        {
            aux = bit.currentRow().getObject();
            if(Helper.isMarkedForTemplate(null, aux, this))
            {
                if("GESTEMP_CampoManual".equals(aux.getName())) //$NON-NLS-1$
                {
                    cm = GtCampoManual.getCampo(this, null, aux);
                    addCampoManual(cm);
                    addCampoManualSimples(cm);
                }
                else
                {
                    cnm = GtCampoNManual.getCampo(this, null, aux);
                    addCampoManual(cnm);
                    addCampoManualLista(cnm);
                }
            }
        }

        //Queries
        bit = template.getBridge("queries").iterator(); //$NON-NLS-1$
        bit.beforeFirst();
        aux = null;
        while(bit.next())
        {
            aux = bit.currentRow().getObject();
            if(Helper.isMarkedForTemplate(null, aux, this))
            {
                addQuery(GtQuery.getQuery(this, aux));
            }
        }
    }
    
    
    public static GtTemplate getTemplate(EboContext boctx,  long templateBoui) throws boRuntimeException
    {
        GtTemplate aux = null;
        try
        {
            boObject temp = boObject.getBoManager().loadObject(boctx, templateBoui);
            if(temp != null)
            {
                aux = GtTemplate.getTemplate(temp);
            }
        }
        catch(boRuntimeException e )
        {
            logger.error("Erro a carregar Template ["+templateBoui+"] EboContext["+(boctx!=null?boctx.poolUniqueId():"")+"]" , e ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            throw e;
        }
        return aux;        
    }
    
    public Tabela getParametros(EboContext boctx,  Tabela toRet, byte templateType) throws boRuntimeException
    {
        GtCampoManual cManual = null;
        
        setMessageParametros( boctx, toRet );
        
        //campos Manuais Simples
        for (int i = 0; i < manuaisSimples.size(); i++) 
        {
            ((GtCampoManual)manuaisSimples.get(i)).setData(boctx, toRet, templateType);
        }
        
        if(getChannel() == TYPE_FAX)
        {// se for fax vou colocar o valor dos tipos
            setFaxParametros(boctx, toRet);
        }
        else if(getChannel() == TYPE_CARTA)
        {
            setCartaParametros(boctx, toRet);
        }
        
        if(getChannel() != TYPE_SMS)
        {
            setAnexosParametros(boctx, toRet);
        }
        
        //assinatura do aprovador caso exista
        setAproverSign(boctx, toRet);
        
        return toRet;
    }
    public void setFaxParametros(EboContext boctx,  Tabela toRet) throws boRuntimeException
    {
        toRet.insert(urgent ? Messages.getString("GtTemplate.89"):Messages.getString("GtTemplate.88"), SpecialField.FAX_URGENT, Types.VARCHAR); //$NON-NLS-1$ //$NON-NLS-2$
        toRet.insert(review ? Messages.getString("GtTemplate.89"):Messages.getString("GtTemplate.88"), SpecialField.FAX_REVER, Types.VARCHAR); //$NON-NLS-1$ //$NON-NLS-2$
        toRet.insert(coment ? Messages.getString("GtTemplate.89"):Messages.getString("GtTemplate.88"), SpecialField.FAX_COMENTAR, Types.VARCHAR); //$NON-NLS-1$ //$NON-NLS-2$
        toRet.insert(answer ? Messages.getString("GtTemplate.89"):Messages.getString("GtTemplate.88"), SpecialField.FAX_RESPONDER, Types.VARCHAR); //$NON-NLS-1$ //$NON-NLS-2$
        toRet.insert(GtCampoManual.redifineValue(toFaxNumber), SpecialField.FAX_NUMERO, Types.VARCHAR);
    }
    public void setAnexosParametros(EboContext boctx,  Tabela toRet) throws boRuntimeException
    {
        if(msgAnexos != null && !"".equals(msgAnexos)) //$NON-NLS-1$
        {
            String[] aBouis = msgAnexos.split(";"); //$NON-NLS-1$
            StringBuffer anexos = new StringBuffer(""); //$NON-NLS-1$
            boObject docObj;
            for (int i = 0; i < aBouis.length; i++) 
            {
                try
                {
                    docObj = boObject.getBoManager().loadObject(boctx, Long.parseLong(aBouis[i]));
                    anexos.append(" ").append(i+1).append("- "); //$NON-NLS-1$ //$NON-NLS-2$
                    
                    if(docObj.getAttribute("tipoDoc").getValueString() != null && !"".equals(docObj.getAttribute("tipoDoc").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    {
                        boObject lov;
                        lov = docObj.getBoManager().loadObject(boctx,
                            "Ebo_LOV", "name='" + docObj.getAttribute("tipoDoc").getDefAttribute().getLOVName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

                        if (lov.exists())
                        {
                            bridgeHandler lovdetails = lov.getBridge("details"); //$NON-NLS-1$
                            lovdetails.beforeFirst();
        
                            boObject det;
        
                            while (lovdetails.next())
                            {
                                det = lovdetails.getObject();
        
                                if (docObj.getAttribute("tipoDoc").getValueString().equalsIgnoreCase(det.getAttribute("value") //$NON-NLS-1$ //$NON-NLS-2$
                                                                  .getValueString()))
                                {
                                    anexos.append(det.getAttribute("description").getValueString()).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
                                }
                            }
                        }
                    }
                    else if(docObj.getAttribute("description").getValueString() != null && !"".equals(docObj.getAttribute("description").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    {
                        anexos.append(docObj.getAttribute("description").getValueString()).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    else if(docObj.getAttribute("fileName").getValueString() != null && !"".equals(docObj.getAttribute("fileName").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    {
                        anexos.append(docObj.getAttribute("fileName").getValueString()).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    else
                    {
                        anexos.append(Messages.getString("GtTemplate.129")); //$NON-NLS-1$
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            //manter para os modelos antigos
            toRet.insert(anexos.toString(), SpecialField.FAX_ANEXOS, Types.VARCHAR);
            toRet.insert(anexos.toString(), SpecialField.MESSAGE_ANEXOS, Types.VARCHAR);
        }
    }
    public void setCartaParametros(EboContext boctx,  Tabela toRet) throws boRuntimeException
    {
        //código barras do cliente válido para todo o tipo de carta. 
        //Deve ser calculado sempre caso mude a morada
        codeBarClienteCTT = Helper.getCodeBarClienteCTT(boctx, this);
        if(registada)
        {
            if(registoNr == null || "".equals(registoNr)) //$NON-NLS-1$
            {
                if(!modeTest)
                {//API pode devolver um erro como exemplo "Não é possível enviar cartas registadas: Tranche esgotada"
                    
                    //TODO:Implement Interface LUSITANIA
                    //registoNr = NumeroRegistado.getNumero();
                    if(registoNr == null || "".equals(registoNr))
                    {
                        throw new boRuntimeException("",Messages.getString("GtTemplate.133"), null); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                else
                {
                    registoNr = "TESTE908776PT"; //$NON-NLS-1$
                }
            }
            //API o nº cliente e o nº srp
            //TODO:Implement Interface LUSITANIA
            String srpCTT = null;
            String clienteCTT = null;
            
            //String srpCTT = NumeroRegistado.getNumeroSRP(boctx); // "200111"; //API.getSTP();
            //String clienteCTT = NumeroRegistado.getNumeroClienteCTT(boctx);// "12312213"; //API.getSTP();

            toRet.insert("Registada", SpecialField.LETTER_ENVIO, Types.VARCHAR); //$NON-NLS-1$
            toRet.insert("R S Domingos à Lapa 35", SpecialField.LETTER_REMETENTE_1, Types.VARCHAR); //$NON-NLS-1$
            toRet.insert("1249-130 Lisboa", SpecialField.LETTER_REMETENTE_2, Types.VARCHAR); //$NON-NLS-1$
            toRet.insert(TAG_CTTCODEBAR39N_PRF + registoNr + TAG_CTTCODEBAR39N_SFX, SpecialField.LETTER_REGISTO, Types.VARCHAR);
            toRet.insert(registoNr, SpecialField.LETTER_REGISTO_CONTENT, Types.VARCHAR);
            toRet.insert(srpCTT, SpecialField.LETTER_SRP, Types.VARCHAR);
            toRet.insert(clienteCTT, SpecialField.LETTER_CLIENTE, Types.VARCHAR);
            toRet.insert(srpCTT + " - " + clienteCTT, SpecialField.LETTER_SRP_CLIENTE, Types.VARCHAR); //$NON-NLS-1$
        }
        else if(avisoRecepcao) 
        {
            if(registoNr == null || "".equals(registoNr)) //$NON-NLS-1$
            {
                if(!modeTest)
                {//API pode devolver um erro como exemplo "Não é possível enviar cartas registadas: Tranche esgotada"
                    //TODO:Implement Interface LUSITANIA
                    //registoNr = NumeroRegistado.getNumero();
                    if(registoNr == null || "".equals(registoNr)) //$NON-NLS-1$
                    {
                        throw new boRuntimeException("",Messages.getString("GtTemplate.133"), null); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                else
                {
                    registoNr = "TESTE908776PT"; //$NON-NLS-1$
                }
            }
            String srpCTT = null;
            String clienteCTT = null;
            
            //TODO:Implement Interface LUSITANIA
            //String srpCTT = NumeroRegistado.getNumeroSRP(boctx); // "200111"; //API.getSTP();
            //String clienteCTT = NumeroRegistado.getNumeroClienteCTT(boctx);// "12312213"; //API.getSTP();

            toRet.insert("Registada C/ Aviso Recepção", SpecialField.LETTER_ENVIO, Types.VARCHAR); //$NON-NLS-1$
            toRet.insert("R S Domingos à Lapa 35", SpecialField.LETTER_REMETENTE_1, Types.VARCHAR); //$NON-NLS-1$
            toRet.insert("1249-130 Lisboa", SpecialField.LETTER_REMETENTE_2, Types.VARCHAR); //$NON-NLS-1$
            toRet.insert(TAG_CTTCODEBAR39N_PRF + registoNr + TAG_CTTCODEBAR39N_SFX, SpecialField.LETTER_REGISTO, Types.VARCHAR);
            toRet.insert(registoNr, SpecialField.LETTER_REGISTO_CONTENT, Types.VARCHAR);
            toRet.insert(srpCTT, SpecialField.LETTER_SRP, Types.VARCHAR);
            toRet.insert(clienteCTT, SpecialField.LETTER_CLIENTE, Types.VARCHAR);
            toRet.insert(srpCTT + " - " + clienteCTT, SpecialField.LETTER_SRP_CLIENTE, Types.VARCHAR); //$NON-NLS-1$
        }
        else
        {
            toRet.insert("", SpecialField.LETTER_ENVIO, Types.VARCHAR); //$NON-NLS-1$
            if(registoNr != null && !"".equals(registoNr)) //$NON-NLS-1$
            {
                if(!modeTest)
                {//chamar API para desregistar o este número
                    //TODO:Implement Interface LUSITANIA
                    //NumeroRegistado.rollBack(registoNr);
                }
                registoNr = ""; //$NON-NLS-1$
            }            
            toRet.insert("", SpecialField.LETTER_REMETENTE_1, Types.VARCHAR); //$NON-NLS-1$
            toRet.insert("", SpecialField.LETTER_REMETENTE_2, Types.VARCHAR); //$NON-NLS-1$
            toRet.insert("", SpecialField.LETTER_REGISTO, Types.VARCHAR); //$NON-NLS-1$
            toRet.insert("", SpecialField.LETTER_REGISTO_CONTENT, Types.VARCHAR); //$NON-NLS-1$
            toRet.insert("", SpecialField.LETTER_SRP, Types.VARCHAR); //$NON-NLS-1$
            toRet.insert("", SpecialField.LETTER_CLIENTE, Types.VARCHAR); //$NON-NLS-1$
            toRet.insert("", SpecialField.LETTER_SRP_CLIENTE, Types.VARCHAR); //$NON-NLS-1$
        }
        toRet.insert(TAG_DCRRRM4SCC_PRF + codeBarClienteCTT + TAG_DCRRRM4SCC_SFX, SpecialField.LETTER_4ESTADOS, Types.VARCHAR);
        toRet.insert(codeBarClienteCTT, SpecialField.LETTER_4ESTADOS_CONTENT, Types.VARCHAR);
        
    }
    
    public void setMessageParametros(EboContext boctx, Tabela toRet )
    {
        if( this.msgId == null || this.msgId.length() == 0 )
        {
            //TODO:Implement Interface LUSITANIA
            //this.msgId = Message.generateMessageId(boctx, this.getNome());
        }
        toRet.insert( msgId, SpecialField.MESSAGE_ID, Types.VARCHAR );
        toRet.insert( TAG_CTTCODEBAR39N_PRFNOR + msgId + TAG_CTTCODEBAR39N_SFX, SpecialField.MESSAGE_ID, Types.VARCHAR );
        toRet.insert( new Timestamp((new Date()).getTime()), SpecialField.MESSAGE_DATE, Types.TIMESTAMP );
    }
    
    
    
    public void setAproverSign(EboContext boctx,  Tabela toRet) throws boRuntimeException
    {
        if(signatureBoui == -1)
        {
            toRet.insert(null, SpecialField.APPROVER_SIGNATURE, Types.BLOB);
            toRet.insert("", SpecialField.APPROVER_NAME, Types.VARCHAR); //$NON-NLS-1$
            toRet.insert(null, SpecialField.APPROVER_DATE, Types.DATE);
        }
        else
        {
            if(Assinaturas.hasAssinatura(boctx, signatureBoui))
            {
                boObject perf = boObject.getBoManager().loadObject(boctx, signatureBoui);
                toRet.insert(Assinaturas.getAssinatura(boctx, signatureBoui), SpecialField.APPROVER_SIGNATURE, Types.BLOB);
                toRet.insert(perf.getAttribute("name").getValueString(), SpecialField.APPROVER_NAME, Types.VARCHAR); //$NON-NLS-1$
                if(signatureDate == null)
                {
                    toRet.insert(new Date(), SpecialField.APPROVER_DATE, Types.DATE);
                }
                else
                {
                    toRet.insert(signatureDate, SpecialField.APPROVER_DATE, Types.DATE);
                }
            }
            else
            {
                throw new boRuntimeException("", Messages.getString("GtTemplate.161"), null); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }
    
    private String[] getHeaderFromDoc(EboContext boctx,iFile file ) throws boRuntimeException, iFilePermissionDenied
    {
        setWordTemplateFields(boctx, file);
        if(file != null)
        {
            return getHeaderFromDoc(boctx, file.getInputStream());
        }
        return null;
    }
    
    private void setWordTemplateFields(EboContext boctx,iFile file ) throws boRuntimeException, iFilePermissionDenied
    {
        if(file != null)
        {
            setWordTemplateFields(boctx, file.getInputStream());
        }
    }
    
    
    
    private String generate(EboContext boctx,String filename,iFile file ) throws Exception
    {
        if(file != null)
        {
            return generate(boctx, filename, file.getInputStream());
        }
        return null;
    }

    public boObject generate(EboContext boctx) throws Exception
    {
        return generate(boctx, false, false);
    }
    public boObject bindAndGenerate(EboContext boctx) throws Exception
    {
        return generate(boctx, true, false);
    }
    private boObject generate(EboContext boctx, boolean bind, boolean setMsg) throws Exception
    {
        try
        {
            String rosto=null, doc=null, text=null, textAssunto=null;
//            logger.debug("--------------------------------Geração-----------------------------------------");
            if(bind)
            {
                setValues(boctx);
            }
            long ti = System.currentTimeMillis(); 
//            erro = Helper.valida(boctx, this, getChannel());
//            if(erro == null || erro.length() == 0)
//            {
                switch(getChannel())
                {
                    case TYPE_CARTA:
                        rosto = generate(boctx, Helper.getRostoDocumentName(), rostoCarta);
                        doc = generate(boctx, Helper.getDocumentName(), tempCarta);
                        break;
                    case TYPE_FAX:
                        rosto = generate(boctx, Helper.getRostoDocumentName(), rostoFax);
                        doc = generate(boctx, Helper.getDocumentName(), tempFax);
                        break;
                    case TYPE_SMS:
                        text = textGenerate(boctx, null, tempSMS);
                        break;
                    case TYPE_EMAIL:
                        textAssunto = textGenerate(boctx, null, tempEmailAssunto);
                        text = textGenerate(boctx, null, tempEmailTexto);
                        if(anexarCarta())
                        {
                            //rosto = generate(boctx, Helper.getRostoDocumentName(), rostoCarta);
                            doc = generate(boctx, Helper.getDocumentName(), tempCarta);
                        }
                        else if(anexarFax())
                        {
                            //setWordTemplateFields(boctx, rostoFax);
                            setWordTemplateFields(boctx, tempFax);
                            //rosto = generate(boctx, Helper.getRostoDocumentName(), rostoFax);
                            doc = generate(boctx, Helper.getDocumentName(), tempFax);
                        }
                        break;
                }
                boObject actvSend = null;

                if(rosto != null || doc != null)
                {
                    //vou gerar o objecto xeo com o resultado
    //                generated = Helper.getGestempGenerated(boctx, this, rosto, doc, null, null);
                    //vou criar a mensagem de envio
    //                boObject dst = boObject.getBoManager().loadObject(boctx, 90102133);
                    if(CSCRIPT != null && TRATADOC != null)
                    {
                        //correr macros
                        String [] args = null;
                        if(rosto != null && doc != null)
                        {
                            args = new String[2];
                            //args[0] = TRATADOC;
                            args[0] = rosto;
                            args[1] = doc;
                        }
                        else
                        {
                            args = new String[1];
                            //args[0] = TRATADOC;
                            args[0] = (rosto != null ? rosto:doc);
                        }
                        //runExternalProgram(args);
                        runWordMacroOnDocument( args );
                        
                    }
                    if(!modeTest)
                    {
                        if(!setMsg)
                            actvSend = Helper.getMessage(boctx, this, rosto, doc, null, null);
                        else
                            actvSend = Helper.setMessage(boctx, this, rosto, doc, null, null);
                    }
                    else
                    {
                        actvSend = Helper.getGestempGenerated(boctx, this, rosto, doc, text, textAssunto);
                    }
                }
                if(text != null && text.length() > 0)
                {
                    //vou gerar o objecto xeo com o resultado
    //                generated = Helper.getGestempGenerated(boctx, this, null, null, text, textAssunto);
                    //vou criar a mensagem de envio
    //                boObject dst = boObject.getBoManager().loadObject(boctx, 90102133);
                    if(!modeTest)
                    {
                        if(!setMsg)
                            actvSend = Helper.getMessage(boctx, this, rosto, doc, text, textAssunto);
                        else
                            actvSend = Helper.setMessage(boctx, this, rosto, doc, text, textAssunto);
                    }
                    else
                    {
                        actvSend = Helper.getGestempGenerated(boctx, this, rosto, doc, text, textAssunto);
                    }
                    
                }
                else if ( rosto == null && doc == null )
                {
                    erro = Messages.getString("GtTemplate.162"); //$NON-NLS-1$
                }
                if(erro == null || erro.length() == 0)
                {
//                    actvSend.update();
                    setActvSendBoui(actvSend.getBoui());
                    long tf = System.currentTimeMillis();
                    logger.debug("Tempo Total da Geração (" + (float)(Math.round((float)(tf-ti)/100f))/10f +"s)"); //$NON-NLS-1$ //$NON-NLS-2$
//                    logger.debug("--------------------------------------------------------------------------------");
                    if(canChooseLetterType())
                        hasSelectedLetterType = false;
                    return actvSend;
    //                setGeneratedBoui(generated.getBoui());
    //                System.out.println(generated.getBridge("respostas").getValueString());
    //                generated.update();
    //                return generated;
                }
//            }          
        }
        finally
        {
            fillParams = false;
            fillListParams = false;
        }
        
        return null;
    }
    
    private String[] generateContent(EboContext boctx) throws boRuntimeException
    {
        try
        {
            String rosto=null, doc=null, text=null, textAssunto=null;
            long ti = System.currentTimeMillis(); 
            switch(getChannel())
            {
                case TYPE_CARTA:
                    rosto = generate(boctx, Helper.getRostoDocumentName(), rostoCarta);
                    doc = generate(boctx, Helper.getDocumentName(), tempCarta);
                    break;
                case TYPE_FAX:
                    rosto = generate(boctx, Helper.getRostoDocumentName(), rostoFax);
                    doc = generate(boctx, Helper.getDocumentName(), tempFax);
                    break;
                case TYPE_SMS:
                    text = textGenerate(boctx, null, tempSMS);
                    break;
                case TYPE_EMAIL:
                    textAssunto = textGenerate(boctx, null, tempEmailAssunto);
                    text = textGenerate(boctx, null, tempEmailTexto);
                    if(anexarCarta())
                    {
                        rosto = generate(boctx, Helper.getRostoDocumentName(), rostoCarta);
                        doc = generate(boctx, Helper.getDocumentName(), tempCarta);
                    }
                    else if(anexarFax())
                    {
                        setWordTemplateFields(boctx, rostoFax);
                        setWordTemplateFields(boctx, tempFax);
                        rosto = generate(boctx, Helper.getRostoDocumentName(), rostoFax);
                        doc = generate(boctx, Helper.getDocumentName(), tempFax);
                    }
                    break;
            }
            return new String[]{rosto, doc, text, textAssunto};
        }
        catch (boRuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new boRuntimeException("", e.getMessage(), e); //$NON-NLS-1$
        }
    }
    
    public String[] getHeaderFromText(EboContext boctx, String smsTexto) throws boRuntimeException
    {
        try
        {

            TextTemplate template = new TextTemplate();
            template.open(smsTexto);
            String[] fields = template.getFieldMarkers();
            return fields;
        }
        catch (Exception ex)
        {
            throw new boRuntimeException("", ex.getMessage(), ex); //$NON-NLS-1$
        }
        finally
        {
            
        }
    }
    
    public void setTextTemplateFields(EboContext boctx, String smsTexto) throws boRuntimeException
    {
        try
        {

            TextTemplate template = new TextTemplate();
            template.open(smsTexto);
            String[] tfields = template.getFieldMarkers();
            for (int i = 0; tfields != null && i < tfields.length; i++) 
            {
                if(fields.indexOf(tfields[i]) == -1)
                {
                    fields.add(tfields[i]);
                }
            }
            bookmarks  = template.getBookmarks();
        }
        catch (Exception ex)
        {
            throw new boRuntimeException("", ex.getMessage(), ex); //$NON-NLS-1$
        }
        finally
        {
            
        }
    }
    
    public String textGenerate(EboContext boctx, String fileName, String smsTexto) throws boRuntimeException
    {
        FileOutputStream out = null;
        String result = null;
        File ff = null;

        try
        {
//            logger.debug("--------------------------------Text Merge-----------------------------------------");
            long ti = System.currentTimeMillis();
            TextTemplate template = new TextTemplate();
            template.open(smsTexto);
            
            setDataSources(boctx, template);
            
            template.process();

            if(fileName != null && fileName.length() > 0)
            {
                String dir = DocumentHelper.getTempDir();
                if(!dir.endsWith(File.separator))
                {
                    dir += File.separator;
                }
                dir += System.currentTimeMillis();
    
                File ndir = new File(dir);
    
                if (!ndir.exists())
                {
                    ndir.mkdirs();
                }
    
                
                ff = new File(dir + File.separator + fileName);
                out = new FileOutputStream(ff);
                template.save(out);
                result = ff.getAbsolutePath();
                logger.error("Gerou para: " +result); //$NON-NLS-1$
            }
            else
            {
                result = template.getResult();
            }
            long tf = System.currentTimeMillis();
            logger.debug("Tempo Total do Text Merge (" + (float)(Math.round((float)(tf-ti)/100f))/10f +"s)"); //$NON-NLS-1$ //$NON-NLS-2$
//            logger.debug("--------------------------------------------------------------------------------");
        }
        catch(boRuntimeException e)
        {
            throw e;
        }
        catch (Exception ex)
        {
            throw new boRuntimeException("", ex.getMessage(), ex); //$NON-NLS-1$
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
//                logger.error("", e);
            }
        }

        return result;
    }
    
    private String[] getHeaderFromDoc(EboContext boctx, InputStream file) throws boRuntimeException
    {
        InputStream input = null;
        try
        {
            input = new BufferedInputStream(file);

            WordTemplate template = new WordTemplate();
            template.setLicenseKey(WORDPROCESS_SERIAL_KEY);
            template.open(input);
            
            String [] fields = template.getFieldMarkers();
            return fields;
            
        }
        catch(boRuntimeException e)
        {
            throw e;
        }
        catch (Exception ex)
        {
            throw new boRuntimeException("", ex.getMessage(), ex); //$NON-NLS-1$
        }
        finally
        {
            try
            {
                if (input != null)
                {
                    input.close();
                }

                if (file != null)
                {
                    file.close();
                }
            }
            catch (IOException e)
            {
//                logger.error("", e);
            }
        }
    }
    
    
    private void setWordTemplateFields(EboContext boctx, InputStream file) throws boRuntimeException
    {
        InputStream input = null;
        try
        {
            input = new BufferedInputStream(file);

            WordTemplate template = new WordTemplate();
            template.setLicenseKey(WORDPROCESS_SERIAL_KEY);
            template.open(input);
            
            String [] tfields = template.getFieldMarkers();
            
            String [] bookmarks = template.getBookmarks();
            
            for (int i = 0; tfields != null && i < tfields.length; i++) 
            {
                if(fields.indexOf(tfields[i]) == -1)
                {
                    fields.add(tfields[i]);
                }
            }
            
            String auxBook;
            String [] bookMarkfields = null; 
            for (int i = 0; i < bookmarks.length; i++) 
            {
                auxBook = bookmarks[i];
                bookMarkfields = template.getFieldMarkers(auxBook);
                if(bookMarkfields != null && bookMarkfields.length > 0 )
                {
                    addBookmark( new Bookmark(auxBook, bookMarkfields) );
                }
            }
        }
        catch(boRuntimeException e)
        {
            throw e;
        }
        catch (Exception ex)
        {
            throw new boRuntimeException("", ex.getMessage(), ex); //$NON-NLS-1$
        }
        finally
        {
            try
            {
                if (input != null)
                {
                    input.close();
                }

                if (file != null)
                {
                    file.close();
                }
            }
            catch (IOException e)
            {
//                logger.error("", e);
            }
        }
    }
    
    private void addBookmark( Bookmark _bookmark )
    {
        if(this.bookmarks == null) 
        {
            this.bookmarks = new ArrayList();
        }
        boolean found = false;
        String bname = _bookmark.getBookmarkName();
        for (int i = 0; i < this.bookmarks.size(); i++) 
        {
            if( bname.equals( ((Bookmark)this.bookmarks.get( i )).getBookmarkName() ) )
            {
                found = true;
                bookmarks.set( i, _bookmark );
                break;
            }
        }
        if( !found )
        {
            bookmarks.add( _bookmark );
        }
        
    }
    
    
    private String generate(EboContext boctx, String fileName, InputStream file) throws Exception
    {
        FileOutputStream out = null;
        InputStream input = null;
        String filePath = null;
        File ff = null;

        try
        {
//            logger.debug("--------------------------------WORD Merge-----------------------------------------");
            long ti = System.currentTimeMillis();
            input = new BufferedInputStream(file);

            WordTemplate template = new WordTemplate();
            template.setLicenseKey(WORDPROCESS_SERIAL_KEY);
            template.open(input);
            
            setDataSources(boctx, template);
            
            template.process();

            String dir = DocumentHelper.getTempDir();
            if(!dir.endsWith(File.separator))
            {
                dir += File.separator;
            }
            dir += System.currentTimeMillis();

            File ndir = new File(dir);

            if (!ndir.exists())
            {
                ndir.mkdirs();
            }

            ff = new File(dir + File.separator + fileName);
            out = new FileOutputStream(ff);
            template.save(out);
            filePath = ff.getAbsolutePath();
            logger.debug("Gerou para: " +filePath); //$NON-NLS-1$
            long tf = System.currentTimeMillis();
            logger.debug("Tempo Total WORD MERGE (" + (float)(Math.round((float)(tf-ti)/100f))/10f +"s)"); //$NON-NLS-1$ //$NON-NLS-2$
//            logger.debug("--------------------------------------------------------------------------------");
        }
        finally
        {
            try
            {
                if (input != null)
                {
                    input.close();
                }

                if (file != null)
                {
                    file.close();
                }

                if (out != null)
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
//                logger.error("", e);
            }
        }

        return filePath;
    }
    

    private static boolean runWordMacroOnDocument(String[] filePaths)
    {
        boolean toRet = true;
        long ti = System.currentTimeMillis();

        try
        {
            for (int i=0;i < filePaths.length; i++ )
            {
                
                runGDUtilsMethod( "ExecuteMacroOnDocument", new String[] { filePaths[i], "GDFormatDocument" }, 10000 ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        long tf = System.currentTimeMillis();
        logger.debug("Tempo Total WORD MACRO (" + (float)(Math.round((float)(tf-ti)/100f))/10f +"s)"); //$NON-NLS-1$ //$NON-NLS-2$
        return toRet;
    }

    public static String runGDUtilsMethod( final String method, final String[] args, int timeout ) throws Exception
    {
        GDOCXUtilsStub ocxCall = new GDOCXUtilsStub();
        String hn = InetAddress.getLocalHost().getHostName();

        final boolean runLocal = true;//hn.startsWith("jpnbook");
        if( runLocal )
        {
            ocxCall.setEndpoint( "http://localhost:8888/xeoRemoteConversion/GDOCXUtils" ); //$NON-NLS-1$
        }
        
        return ocxCall.runGDUtilsMethod( method, args, new Long(timeout) );
            
    }

//    public static UnknownPtr gdUtilsOCX = null; 
/*    
    public static Object runGDUtilsMethod( final String method, final String[] args, int timeout ) throws Exception
    {
        return runGDUtilsMethod( method, args, true, timeout  );
    }

    public static Object runGDUtilsMethod( final String method, final String[] args, boolean autoRetry, int timeout ) throws Exception
    {

        String hn = InetAddress.getLocalHost().getHostName();
        final boolean runLocal = hn.startsWith("jpnbook");

        final Vector ret = new Vector();
        final Vector ex = new Vector();

        //ConvertImagesV2Stub cisStub = new ConvertImagesV2Stub();
        Thread t = new Thread(
            new Runnable()
            {
                public void run()
                {
                    try
                    {
                        if( runLocal )
                        {
                            localRun( method, args );   
                            ret.add( "" );
                        }
                        else
                        {
                            GDOCXUtilsStub cisStub = new GDOCXUtilsStub();
                            cisStub.runGDUtilsMethod( method, args );
                            ret.add( ret );
                        }
                    }
                    catch (Exception e)
                    {
                        ex.add( e );
                    }
                }
            }
        );
        
        long wElapsed = 0;
        long tsInit   = System.currentTimeMillis();
        boolean error = false;
        
        t.start();
        
        while( t.isAlive() )
        {
            wElapsed = System.currentTimeMillis() - tsInit;
            if( wElapsed > timeout & !error )
            {
                synchronized( GtTemplate.class )
                {
                    try
                    {
                        // kill winword.exe
                        if( gdUtilsOCX != null )
                        {
                            gdUtilsOCX.close();       
                        }
                        gdUtilsOCX = new UnknownPtr("XEO.Utils");
                        gdUtilsOCX.invokeN( "KillProcessSub", new Object[]{"winword.exe"} );
                        
                    }
                    catch (Exception e)
                    {
                        logger.error("ERROR Kiling Processes",e);
                    }
                    
                    if( !runLocal )
                    {
                        try
                        {
                            GDOCXUtilsStub cisStub = new GDOCXUtilsStub();
                            cisStub.closeOCX();
                        }
                        catch (Exception e)
                        {
                            logger.error("ERROR Releasing OCX Remote ",e);
                        }
                    }
                    
                }
                error = true;
                t.interrupt();
            }
            if( wElapsed > (timeout*1.15) )
            {
                error = true;
                t.interrupt();
                break;
            }
            Thread.sleep(50);
        }
        
        if( ( error || ex.size() > 0 ) && autoRetry )
        {
            logger.error("ERRO - RETRY Runnig Method:" + method,ex.size()>0?(Exception)ex.get(0):new Exception("Timeout running") );
            return runGDUtilsMethod( method, args, false, timeout );
        }

        if( ex.size() > 0 )     
        {  
            throw (Exception)ex.get( 0 );
        }
        if( error )
        {
            throw new Exception("Error the timeout executando o metodo:" + method );
        }
        
        return ret.get(0);
    }
 
    private static boolean runWordMacroOnDocument(String[] filePaths)
    {
        boolean toRet = true;
        long ti = System.currentTimeMillis();

        try
        {
            for (int i=0;i < filePaths.length; i++ )
            {
                runGDUtilsMethod( "ExecuteMacroOnDocument", new String[] { filePaths[i], "GDFormatDocument" }, 30000 );
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        long tf = System.currentTimeMillis();
        logger.debug("Tempo Total WORD MACRO (" + (float)(Math.round((float)(tf-ti)/100f))/10f +"s)");
        return toRet;
    }
        
    private static void localRun(String method, String[] filePaths)
    {
        boolean toRet = true;
        long ti = System.currentTimeMillis();

        try
        {
            if ( gdUtilsOCX == null )
            {
                gdUtilsOCX = new UnknownPtr("XEO.Utils");
            }
            gdUtilsOCX.invokeN( method, filePaths );
        }
        catch (COMException e)
        {
            gdUtilsOCX.close();
            try
            {
                gdUtilsOCX = new UnknownPtr("XEO.Utils");
                gdUtilsOCX.invokeN( "KillProcessSub", new Object[]{"winword.exe"} );
            }
            catch (Exception ex)
            {
                gdUtilsOCX.close();
                gdUtilsOCX = null;
            }
            throw new RuntimeException(e);
        }
    }
*/

    // Agora é utilizado o OCX para fazer este trabalho.
    private static boolean runExternalProgram(String[] filePaths)
    {
        boolean toRet = true;
//        logger.debug("----------------------------START EXTERNAL PROG----------------------------------------");
        long ti = System.currentTimeMillis();
        if(filePaths != null && filePaths.length > 0)
        {
            CommandLineExecuter ce = new CommandLineExecuter();
            if ( !ce.execute( CSCRIPT, filePaths, 60000 ) )
            {
                if (ce.getCreateProcessErrorMessage() != null)
                {
                    throw new RuntimeException( "Error a criar o processo:" + ce.getCreateProcessErrorMessage()  ); //$NON-NLS-1$
                } 
                else  
                {
                    throw new RuntimeException
                    ( 
                        Messages.getString("GtTemplate.183") //$NON-NLS-1$
                        +
                        "Exit(" + ce.getProcessMonitor().getExitCode() + ")" //$NON-NLS-1$ //$NON-NLS-2$
                        +
                        "StdOut(" + new String(ce.getProcessMonitor().getOutBytes()) + ")" //$NON-NLS-1$ //$NON-NLS-2$
                        + 
                        "StdErr(" + new String(ce.getProcessMonitor().getErrBytes()) + ")" //$NON-NLS-1$ //$NON-NLS-2$
                    );
                }
            }  
            
        }
        long tf = System.currentTimeMillis();
        logger.debug("Tempo Total EXTERNAL PROG (" + (float)(Math.round((float)(tf-ti)/100f))/10f +"s)"); //$NON-NLS-1$ //$NON-NLS-2$
//        logger.debug("----------------------------FIM EXTERNAL PROG----------------------------------------");
        return toRet;
    }
    
    private void setDataSources(EboContext boctx, WordTemplate template) throws boRuntimeException
    {
        Tabela tabelaParametros = new Tabela();
        Tabela tabela = null; 
        tabelaParametros.startEditingLine();
        getParametros(boctx, tabelaParametros, WORD_TEMPALTE);
        GtQuery q = null;
        for (int i = 0; i < queries.size(); i++) 
        {
        
            q = (GtQuery)queries.get(i);
            q.setData(boctx, tabelaParametros, WORD_TEMPALTE);
        }
        tabelaParametros.endEditingline();
        try
        {
            template.setDataSource(MergeResultSetFactory.getResultSet(tabelaParametros));
        }
        catch (Exception e)
        {
            throw new boRuntimeException("GtTemplate:setDataSources", "", e); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        
        String bookName;
        Tabela bookLines = null;
        for (int i = 0; bookmarks != null && i < bookmarks.size(); i++) 
        {
            bookName = ((Bookmark)bookmarks.get(i)).getBookmarkName();
            bookLines = new Tabela();
            if(bookName.indexOf("__") == -1) //$NON-NLS-1$
            {
                //campos Manuais Lista        
                for (int j = 0; j < manuaisLista.size(); j++) 
                {
                    if(bookName.equals(((GtCampoNManual)manuaisLista.get(j)).getNome()))
                    {
                        
                        ((GtCampoNManual)manuaisLista.get(j)).setData(boctx, bookLines, WORD_TEMPALTE);
                    }
                }
            }
            else//query__lista
            {
                String query = bookName.split("__")[0]; //$NON-NLS-1$
                //campos Manuais Lista        
                for (int j = 0; j < queries.size(); j++) 
                {
                    if(query.equals(((GtQuery)queries.get(j)).getNome()))
                    {
                        ((GtQuery)queries.get(j)).setData(boctx, bookName, bookLines, WORD_TEMPALTE);
                    }
                }
            }

            try
            {
                template.setRepeatBlock(MergeResultSetFactory.getResultSet(bookLines), bookName);
            }
            catch (Exception e)
            {
                throw new boRuntimeException("GtTemplate:setDataSources", "", e); //$NON-NLS-1$ //$NON-NLS-2$
            }
            
        }
        
        
    }

    public void calculate(EboContext boctx, GtAutoClassif autoClf) throws boRuntimeException
    {
        Tabela tabelaParametros = new Tabela();
        Tabela tabela = null; 
        tabelaParametros.startEditingLine();
        getParametros(boctx, tabelaParametros, CLF_TEMPALTE);
        
        GtQuery q = null;
        for (int i = 0; i < queries.size(); i++) 
        {
        
            q = (GtQuery)queries.get(i);
            q.setData(boctx, tabelaParametros, CLF_TEMPALTE);
        }
        tabelaParametros.endEditingline();
        autoClf.setDataSource(tabelaParametros);
        
        
        String bookName;
        Tabela bookLines = null;
        for (int i = 0; bookmarks != null && i < bookmarks.size(); i++) 
        {
            bookName = ((Bookmark)bookmarks.get(i)).getBookmarkName();
            bookLines = new Tabela();
            if(bookName.indexOf("__") == -1) //$NON-NLS-1$
            {
                //campos Manuais Lista        
                for (int j = 0; j < manuaisLista.size(); j++) 
                {
                    if(bookName.equals(((GtCampoNManual)manuaisLista.get(j)).getNome()))
                    {
                        
                        ((GtCampoNManual)manuaisLista.get(j)).setData(boctx, bookLines, CLF_TEMPALTE);
                        
                    }
                }
            }
            else//query__lista
            {
                String query = bookName.split("__")[0]; //$NON-NLS-1$
                //campos Manuais Lista        
                for (int j = 0; j < queries.size(); j++) 
                {
                    if(query.equals(((GtQuery)queries.get(j)).getNome()))
                    {
                        ((GtQuery)queries.get(j)).setData(boctx, bookName, bookLines, CLF_TEMPALTE);
                    }
                }
            }
            autoClf.setRepeatBlock(bookLines, bookName);
        }
    }

    private void setDataSources(EboContext boctx, TextTemplate template) throws boRuntimeException
    {
        Tabela tabelaParametros = new Tabela();
        Tabela tabela = null; 
        tabelaParametros.startEditingLine();
        getParametros(boctx, tabelaParametros, TEXT_TEMPLATE);

        GtQuery q = null;
        for (int i = 0; i < queries.size(); i++) 
        {
            q = (GtQuery)queries.get(i);
            q.setData(boctx, tabelaParametros, TEXT_TEMPLATE);
        }
        tabelaParametros.endEditingline();
        template.setDataSource(MergeResultSetFactory.getResultSet(tabelaParametros));
        
        String bookName;
        Tabela bookLines = null;
        for (int i = 0; bookmarks != null && i < bookmarks.size(); i++) 
        {
            bookName = ((Bookmark)bookmarks.get(i)).getBookmarkName();
            bookLines = new Tabela();
            if(bookName.indexOf("__") == -1) //$NON-NLS-1$
            {
                //campos Manuais Lista        
                for (int j = 0; j < manuaisLista.size(); j++) 
                {
                    if(bookName.equals(((GtCampoNManual)manuaisLista.get(j)).getNome()))
                    {
                        
                        ((GtCampoNManual)manuaisLista.get(j)).setData(boctx, bookLines, TEXT_TEMPLATE);
                    }
                }
            }
            else//query__lista
            {
                String query = bookName.split("__")[0]; //$NON-NLS-1$
                //campos Manuais Lista        
                for (int j = 0; j < queries.size(); j++) 
                {
                    if(query.equals(((GtQuery)queries.get(j)).getNome()))
                    {
                        ((GtQuery)queries.get(j)).setData(boctx, bookName, bookLines, TEXT_TEMPLATE);
                    }
                }
            }

            try
            {
                template.setRepeatBlock(MergeResultSetFactory.getResultSet(bookLines), bookName);
            }
            catch (Exception e)
            {
                throw new boRuntimeException("GtTemplate:setDataSources", "", e); //$NON-NLS-1$ //$NON-NLS-2$
            }
            
        }
    }
    
    public boolean hasSelected()
    {
        return hasSelected;
    }
    
    public void setTypeValues(EboContext boctx) throws boRuntimeException
    {
//        try
//        {
            HttpServletRequest request = boctx.getRequest();
            String vCarta =request.getParameter("letter_channel"); //$NON-NLS-1$
            String vFax =request.getParameter("fax_channel"); //$NON-NLS-1$
            String vEmail =request.getParameter("email_channel"); //$NON-NLS-1$
            String vSms =request.getParameter("sms_channel"); //$NON-NLS-1$
            
            setTypeValues(boctx, "1".equals(vCarta), "1".equals(vFax), "1".equals(vEmail), "1".equals(vSms)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//        }
//        catch (Exception e)
//        {
//            erro = e.getMessage();
//            if(erro != null && erro.indexOf(":") > -1)
//            {
//                String []msg = erro.split(":");
//                if(msg.length == 3 && msg[1].length() > 0)
//                {
//                    erro = msg[1];
//                }
//            }
//            logger.error(e);
//        }
    }
    
    private void getHeaders(EboContext boctx) throws boRuntimeException
    {
        try
        {
            String rosto=null, doc=null, text=null, textAssunto=null;
            switch(getChannel())
            {
                case TYPE_CARTA:
                    setWordTemplateFields(boctx, rostoCarta);
                    setWordTemplateFields(boctx, tempCarta);
                    break;
                case TYPE_FAX:
                    setWordTemplateFields(boctx, rostoFax);
                    setWordTemplateFields(boctx, tempFax);
                    break;
                case TYPE_SMS:
                    setTextTemplateFields(boctx, tempSMS);
                    break;
                case TYPE_EMAIL:
                    setTextTemplateFields(boctx, tempEmailAssunto);
                    setTextTemplateFields(boctx, tempEmailTexto);
                    if(anexarCarta()) 
                    {
                        //setWordTemplateFields(boctx, rostoCarta);
                        setWordTemplateFields(boctx, tempCarta);
                    }
                    else if(anexarFax())
                    {
                        //setWordTemplateFields(boctx, rostoFax);
                        setWordTemplateFields(boctx, tempFax);
                    }
                    break;
            }
        }
        catch (boRuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new boRuntimeException("", e.getMessage(), e); //$NON-NLS-1$
        }
        
    }
    
    public void setChannel(byte c)
    {
        channel = c;
    }
    
    public void setTypeValues(EboContext boctx, boolean bCarta, boolean bFax, boolean bEmail, boolean bSms) throws boRuntimeException
    {
//        try
//        {
            hasSelected = true;
            if(bCarta)
            {
                channel = TYPE_CARTA;
            }
            else if(bFax)
            {
                channel = TYPE_FAX;
            }
            else if(bEmail)
            {
                channel = TYPE_EMAIL;
            }
            else if(bSms)
            {
                channel = TYPE_SMS;
            }
            else
            {
                hasSelected = false;
                channel = TYPE_OMISSO;
                erro = Messages.getString("GtTemplate.213"); //$NON-NLS-1$
            }
            if(erro == null || erro.length() == 0)
            {
                if(fields == null)
                {
                    fields = new ArrayList();
                }
                else
                {
                    fields.clear();
                }
                getHeaders(boctx);
                getFieldAndqueries(boctx);
            }
//        }
//        catch (Exception e)
//        {
//            erro = e.getMessage();
//            if(erro != null && erro.indexOf(":") > -1)
//            {
//                String []msg = erro.split(":");
//                if(msg.length == 3 && msg[1].length() > 0)
//                {
//                    erro = msg[1];
//                }
//            }
//            logger.error(e);
//        }
    }
    
    private void setValues(EboContext boctx) throws boRuntimeException
    {
        String executingQuery = null;
        try
        {
           
            HttpServletRequest request = boctx.getRequest();
            for (int i = 0; i < manuaisSimples.size(); i++) 
            {
                ((GtCampoManual)manuaisSimples.get(i)).setValues(boctx);
                if (((GtCampoManual)manuaisSimples.get(i)).getObrigatorio()) 
                {
                    GtValue v = ((GtCampoManual)manuaisSimples.get(i)).getValue();
                    if (v == null || v.getValue() == null) 
                    {
                        erro = Messages.getString("GtTemplate.214"); //$NON-NLS-1$
                        return;
                    } 
                }
            }
            
            for (int i = 0; i < manuaisLista.size(); i++) 
            {
                ((GtCampoNManual)manuaisLista.get(i)).setValues(boctx);
                if (((GtCampoNManual)manuaisLista.get(i)).getObrigatorio()) 
                {
                    GtValue v = ((GtCampoNManual)manuaisLista.get(i)).getValue();
                    if (v == null || v.getValues() == null || v.getValues().size() == 0) 
                    {
                        erro = Messages.getString("GtTemplate.214"); //$NON-NLS-1$
                        return;
                    } 
                }
            }
            
            for (int i = 0; i < queries.size(); i++) 
            {
                ((GtQuery)queries.get(i)).setValues(boctx);
                GtParametro parametro = ((GtQuery)queries.get(i)).getParametro(); 
                if (parametro.getObrigatorio()) 
                {
                    GtValue v = parametro.getValue();
                    if (v == null || v.getValue() == null) 
                    {
                        erro = Messages.getString("GtTemplate.214"); //$NON-NLS-1$
                        return;
                    } 
                }
            }
            for (int i = 0; i < queries.size(); i++) 
            {
                executingQuery = ((GtQuery)queries.get(i)).getNome();
                ((GtQuery)queries.get(i)).calculateAutomicFields(boctx, this);        
            }
            fillParams = true;
        }
        catch (Exception e)
        {
            erro = e.getMessage();
            if(erro != null && erro.indexOf(":") > -1) //$NON-NLS-1$
            {
                String []msg = erro.split(":"); //$NON-NLS-1$
                if(msg.length >= 2 && msg[1].length() > 0)
                {
                    erro = msg[1];
                }
                else
                {
                    erro = Messages.getString("GtTemplate.219"); //$NON-NLS-1$
                }
            }
            else
            {
                erro = Messages.getString("GtTemplate.84") + executingQuery == null ? "":executingQuery +"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            logger.error(e,e);
            fillParams = false;
            fillListParams = false;
        }
    }
    
    private void setListValues(EboContext boctx) throws boRuntimeException
    {
        try
        {
            for (int i = 0; i < queries.size(); i++) 
            {
                if(((GtQuery)queries.get(i)).hasListDependents())
                {
                    ((GtQuery)queries.get(i)).setListValues(boctx);
                    ((GtQuery)queries.get(i)).calculateAutomicFields(boctx, this);
                }
            }
            fillListParams = true;
        }
        catch (Exception e)
        {
            erro = e.getMessage();
            if(erro != null && erro.indexOf(":") > -1) //$NON-NLS-1$
            {
                String []msg = erro.split(":"); //$NON-NLS-1$
                if(msg.length == 3 && msg[1].length() > 0)
                {
                    erro = msg[1];
                }
            }
            logger.error(e);
            fillListParams = false;
        }
    }
    
    public String getErro()
    {
        return erro;
    }
    
    public boObject process(EboContext boctx) throws boRuntimeException
    {
        HttpServletRequest request=boctx.getRequest();
        HttpServletResponse response=boctx.getResponse();
        try
        {
            PageContext pageContext=boctx.getPageContext();
            return generate(boctx);
        }
        catch (Exception e)
        {
            erro = e.getMessage();
            if(erro != null && erro.indexOf(":") > -1) //$NON-NLS-1$
            {
                String []msg = erro.split(":"); //$NON-NLS-1$
                if(msg.length == 3 && msg[1].length() > 0)
                {
                    erro = msg[1];
                }
            }
            logger.error("Erro a gerar template Modelo ["+this.getDescricao()+"] User:["+ boctx.getSysUser().getUserName() +"]",e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        return null;
    }
    
    public void renderParameters(Controller controller,PageContext pageContext,int idx) throws IOException, boRuntimeException
    {
        GtTemplateViewer.renderParameters(controller,pageContext,idx, this);
    }
    
    public void validateAnexos(EboContext boctx, ArrayList erros)
    {
        // Validar Anexos
        if( erro == null || "".equals(erro) ) //$NON-NLS-1$
        {
            if ( getAllowAttachs() == ATTACH_REQUIRED && ( msgAnexos == null || msgAnexos.length() == 0 ) )
            {
                erros.add(Messages.getString("GtTemplate.231")); //$NON-NLS-1$
            }
        }
    }

    public void validate(EboContext boctx, ArrayList erros) throws boRuntimeException
    {
        // Query's
        long ti = System.currentTimeMillis();
//        logger.debug("--------------------------------Validação-----------------------------------------");
        setValues(boctx);
        
        if( Helper.getDestinatario( boctx, this ) == null )
        {
            erro = Messages.getString( "GtTemplate.311" );
        }
        
        if(erro == null || "".equals(erro)) //$NON-NLS-1$
        {
            GtQuery[] gtQuery = getQueries();
            GtParametro parametro = null;
            for (int i = 0; i < gtQuery.length; i++) 
            {
                gtQuery[i].validate(boctx, erros);
            }
            
            //Campos Manuais Simples
            GtCampoManual[] gtManual = getCamposManuaisSimples();
            for (int i = 0; i < gtManual.length; i++) 
            {
                gtManual[i].validate(boctx, erros); 
            }
            
            //Campos Manuais Lista
            GtCampoNManual[] gtNManual = getCamposManuaisLista();
            for (int i = 0; i < gtNManual.length; i++) 
            {
                gtNManual[i].validate(boctx, erros); 
            }
            if(!hasListDependents())
            {
                if(validacao != null && validacao.length() > 0)
                    javaValidation(boctx, erros);
            }
            long tf = System.currentTimeMillis();
            logger.debug("Tempo Total da Validação (" + (float)(Math.round((float)(tf-ti)/100f))/10f +"s)"); //$NON-NLS-1$ //$NON-NLS-2$
        }
//        logger.debug("--------------------------------------------------------------------------------");
    }
    
    public void listValidate(EboContext boctx, ArrayList erros) throws boRuntimeException
    {
        // Query's
        long ti = System.currentTimeMillis();
//        logger.debug("--------------------------------Validação-----------------------------------------");
        setListValues(boctx);
        GtQuery[] gtQuery = getQueries();
        GtParametro parametro = null;
        for (int i = 0; i < gtQuery.length; i++) 
        {
            if(gtQuery[i].hasListDependents())
            {
               ArrayList r = gtQuery[i].getListDependents();
               for (int j = 0; j < r.size(); j++) 
               {
                  ((GtCampoNObjecto)r.get(j)).validate(boctx, erros);
               }
            }
        }
        long tf = System.currentTimeMillis();
        logger.debug("Tempo Total da Validação (" + (float)(Math.round((float)(tf-ti)/100f))/10f +"s)"); //$NON-NLS-1$ //$NON-NLS-2$
//        logger.debug("--------------------------------------------------------------------------------");
    }
    
    public void templateValidate(EboContext boctx, ArrayList erros) throws boRuntimeException
    {
         if(validacao != null && validacao.length() > 0)
            javaValidation(boctx, erros);
    }
    
    public void setActvSendBoui(long boui)
    {
        this.actSendBoui = boui;
    }
    
    public long getActvSendBoui()
    {
        return this.actSendBoui;
    }
    
    public void clearErrors()
    {
        if(erro != null)
        {
            erro = null;
        }
    }
    public void setAnswer(boObject msg)  throws boRuntimeException
    {
        boolean carta = false, fax = false, email = false, sms = false;
        msgId = msg.getAttribute("docSeq").getValueString(); //$NON-NLS-1$
        if(msg.getName().startsWith("GESTEMP_Generated")) //$NON-NLS-1$
        {
            carta = "1".equals(msg.getAttribute("carta").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            fax = "1".equals(msg.getAttribute("fax").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            email = "1".equals(msg.getAttribute("email").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            sms = "1".equals(msg.getAttribute("sms").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            
            setTypeValues(msg.getEboContext(), carta, fax, email, sms);
        }
        else
        {
            carta = "messageLetter".equals(msg.getName()); //$NON-NLS-1$
            fax = "messageFax".equals(msg.getName()); //$NON-NLS-1$
            email = "messageMail".equals(msg.getName()); //$NON-NLS-1$
            sms = "messageSMS".equals(msg.getName()); //$NON-NLS-1$
            
            if(msg.getAttribute("registoCTT").getValueString() != null && //$NON-NLS-1$
                !"".equals(msg.getAttribute("registoCTT").getValueString()) //$NON-NLS-1$ //$NON-NLS-2$
            )
            {
                registoNr = msg.getAttribute("registoCTT").getValueString(); //$NON-NLS-1$
            }
            setLetterType(msg.getAttribute("envioCarta").getValueString()); //$NON-NLS-1$
            
            setTypeValues(msg.getEboContext(), carta, fax, email, sms);
        }
        //Fax: Urgente Rever Comentar Responder
        bridgeHandler bh = msg.getBridge("respostas"); //$NON-NLS-1$
        setAnswer(bh);
        
        for (int i = 0; i < queries.size(); i++) 
        {
            ((GtQuery)queries.get(i)).setAnswer(bh);
        }
        for (int i = 0; i < manuaisSimples.size(); i++) 
        {
            ((GtCampoManual)manuaisSimples.get(i)).setAnswer(bh);
        }
        for (int i = 0; i < manuaisLista.size(); i++) 
        {
            ((GtCampoNManual)manuaisLista.get(i)).setAnswer(bh);
        }
    }
    
    public void setAnswer(bridgeHandler bh)  throws boRuntimeException
    {
        boBridgeIterator bit = bh.iterator();
        bit.beforeFirst();
        String pergunta;
        while(bit.next())
        {
            pergunta = bit.currentRow().getObject().getAttribute("pergunta").getValueString(); //$NON-NLS-1$
            String v = bit.currentRow().getObject().getAttribute("resposta").getValueString(); //$NON-NLS-1$
            if(FAX_URGENT_PRG.equalsIgnoreCase(pergunta))
            {
                urgent = "1".equals(v); //$NON-NLS-1$
            }
            else if(FAX_REVIEW_PRG.equalsIgnoreCase(pergunta))
            {
                review = "1".equals(v); //$NON-NLS-1$
            }
            else if(FAX_COMENT_PRG.equalsIgnoreCase(pergunta))
            {
                 coment = "1".equals(v); //$NON-NLS-1$
            }
            else if(FAX_ANSWER_PRG.equalsIgnoreCase(pergunta))
            {
                 answer = "1".equals(v); //$NON-NLS-1$
            }
            else if(FAX_NUMBER_PRG.equalsIgnoreCase(pergunta))
            {
                toFaxNumber = v;
            }
            else if(EMAIL_ADDRESS_PRG.equalsIgnoreCase(pergunta))
            {
                toEmailAddress = v;
            }
            else if(FAX_ANEXOS_PRG.equalsIgnoreCase(pergunta) || MSG_ANEXOS_PRG.equalsIgnoreCase(pergunta))
            {
                msgAnexos = v;
            }
        }
    }
    
    public void setAnswerBridge(bridgeHandler bh)  throws boRuntimeException
    {
        if(urgent)
        {
            boObject answerObj = bh.addNewObject();
            answerObj.getAttribute("pergunta").setValueString(FAX_URGENT_PRG); //$NON-NLS-1$
            answerObj.getAttribute("resposta").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
        }        
        if(review)
        {
            boObject answerObj = bh.addNewObject();
            answerObj.getAttribute("pergunta").setValueString(FAX_REVIEW_PRG); //$NON-NLS-1$
            answerObj.getAttribute("resposta").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if(coment)
        {
            boObject answerObj = bh.addNewObject();
            answerObj.getAttribute("pergunta").setValueString(FAX_COMENT_PRG); //$NON-NLS-1$
            answerObj.getAttribute("resposta").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if(answer)
        {
            boObject answerObj = bh.addNewObject();
            answerObj.getAttribute("pergunta").setValueString(FAX_ANSWER_PRG); //$NON-NLS-1$
            answerObj.getAttribute("resposta").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if(toFaxNumber != null && !"".equals(toFaxNumber)) //$NON-NLS-1$
        {
            boObject answerObj = bh.addNewObject();
            answerObj.getAttribute("pergunta").setValueString(FAX_NUMBER_PRG); //$NON-NLS-1$
            answerObj.getAttribute("resposta").setValueString(toFaxNumber); //$NON-NLS-1$
        }
        if(toEmailAddress != null && !"".equals(toEmailAddress)) //$NON-NLS-1$
        {
            boObject answerObj = bh.addNewObject();
            answerObj.getAttribute("pergunta").setValueString(EMAIL_ADDRESS_PRG); //$NON-NLS-1$
            answerObj.getAttribute("resposta").setValueString(toEmailAddress); //$NON-NLS-1$
        }
        if(msgAnexos != null && !"".equals(msgAnexos)) //$NON-NLS-1$
        {
            boObject answerObj = bh.addNewObject();
            answerObj.getAttribute("pergunta").setValueString(MSG_ANEXOS_PRG); //$NON-NLS-1$
            answerObj.getAttribute("resposta").setValueString(msgAnexos); //$NON-NLS-1$
        }
    }
    
    
    public void setModeTest(boolean value)
    {
        modeTest = value;
    }
    
    public boolean hasSelectedFaxType()
    {
        return hasSelectedFaxType;
    }
    
    public boolean isUrgent()
    {
        return urgent;
    }
    
    public boolean isReview()
    {
        return review;
    }
    
    public boolean isComent()
    {
        return coment;
    }
    
    public boolean isAnswer()
    {
        return answer;
    }
    
    public String getFaxNumber()
    {
        return toFaxNumber;
    }
    
    public String getEmailAddress()
    {
        return toEmailAddress;
    }
    
    public String getFaxAnexos()
    {
        return msgAnexos;
    }
    
    public void setFaxTypeValues(EboContext boctx) throws boRuntimeException
    {
        try
        {
            HttpServletRequest request = boctx.getRequest();
            String fUrgent =request.getParameter("fax_urgent"); //$NON-NLS-1$
            String fReview =request.getParameter("fax_review"); //$NON-NLS-1$
            String fComent =request.getParameter("fax_coment"); //$NON-NLS-1$
            String fAnswer =request.getParameter("fax_answer"); //$NON-NLS-1$
            String fNumber =request.getParameter("fax_number"); //$NON-NLS-1$
            String eAddress =request.getParameter("email_address"); //$NON-NLS-1$
            String fAnexos =request.getParameter("faxAnexos"); //$NON-NLS-1$
            
            setFaxTypeValues(boctx, "1".equals(fUrgent), "1".equals(fReview), "1".equals(fComent), "1".equals(fAnswer), fNumber, eAddress, fAnexos); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
        catch (Exception e)
        {
            erro = e.getMessage();
            if(erro != null && erro.indexOf(":") > -1) //$NON-NLS-1$
            {
                String []msg = erro.split(":"); //$NON-NLS-1$
                if(msg.length == 3 && msg[1].length() > 0)
                {
                    erro = msg[1];
                }
            }
            logger.error(e);
        }
    }
    
    public void setFaxTypeValues(EboContext boctx, boolean bUrgent, boolean bReview, boolean bComent, boolean bAnswer, String faxNumber, String emailAddress, String bAnexos) throws boRuntimeException
    {
        try
        {
            hasSelectedFaxType = true;
            urgent = bUrgent;
            review = bReview;
            coment = bComent;
            answer = bAnswer;
            toFaxNumber = faxNumber;
            toEmailAddress = emailAddress;
            msgAnexos = bAnexos;
            
            if(getChannel() == TYPE_FAX)
            {
                if (toFaxNumber == null || toFaxNumber.trim().length() == 0)
                {
                    erro = Messages.getString("GtTemplate.297"); //$NON-NLS-1$
                }
                else if ( toFaxNumber.trim().length() < 9 )
                {
                    erro = Messages.getString("GtTemplate.298"); //$NON-NLS-1$
                }
                else  
                {

                    if (!Pattern.matches( "([0-9\\-\\s\\+]){9,}", toFaxNumber.trim() )) //$NON-NLS-1$
                    {
                        erro = Messages.getString("GtTemplate.300");  //$NON-NLS-1$
                    }
                }
                 
            }
            else if(getChannel() == TYPE_EMAIL && (toEmailAddress == null || toEmailAddress.trim().length() == 0))
            {
                erro = Messages.getString("GtTemplate.301"); //$NON-NLS-1$
            }
            
        }
        catch (Exception e)
        {
            erro = e.getMessage();
            if(erro != null && erro.indexOf(":") > -1)
            {
                String []msg = erro.split(":"); //$NON-NLS-1$
                if(msg.length == 3 && msg[1].length() > 0)
                {
                    erro = msg[1];
                }
            }
            logger.error(e); 
        }
    }
    
    public void setLetterTypeValues(EboContext boctx) throws boRuntimeException
    {
        try
        {
            HttpServletRequest request = boctx.getRequest();
            String simplesL =request.getParameter("simples_letter"); //$NON-NLS-1$
            String registadaL =request.getParameter("registada_letter"); //$NON-NLS-1$
            String avisoL =request.getParameter("aviso_letter"); //$NON-NLS-1$
            simples = false;registada=false;avisoRecepcao=false;
            erro = null;
            hasSelectedLetterType = true;
            if("1".equals(simplesL)) //$NON-NLS-1$
            {
                simples = true;
            }
            else if("1".equals(registadaL)) //$NON-NLS-1$
            {
                registada = true;
            }
            else if("1".equals(avisoL)) //$NON-NLS-1$
            {
                avisoRecepcao = true;
            }
            else
            {
                hasSelectedLetterType = false;
                erro = Messages.getString("GtTemplate.310"); //$NON-NLS-1$
            }
        }
        catch (Exception e)
        {
            erro = e.getMessage();
            if(erro != null && erro.indexOf(":") > -1) //$NON-NLS-1$
            {
                String []msg = erro.split(":"); //$NON-NLS-1$
                if(msg.length == 3 && msg[1].length() > 0)
                {
                    erro = msg[1];
                }
            }
            logger.error(e);
        }
    }
    
    public boolean hasSelectedLetterType()
    {
        return hasSelectedLetterType;
    }
    
    public boolean isSimples()
    {
        return simples;
    }
    
    public boolean isRegistada()
    {
        return registada;
    }
    
    public boolean isAviso()
    {
        return avisoRecepcao;
    }
    
    public boolean hasFillParams()
    {
        return fillParams;
    }
    
    public void setFillParams(boolean value)
    {
        fillParams = false;
    }
    public boolean hasFillListParams()
    {
        return fillListParams;
    }
    
    public void setFillListParams(boolean value)
    {
        fillListParams = false;
    }
    public String getSendType()
    {
        return sendType;
    }
    
    public long getMsgReplyFrom()
    {
        return msgReplyFromBoui;
    }
    
    public long getActvReplyFrom()
    {
        return actvReplyFromBoui;
    }
    
    public long getProtocolo()
    {
        return this.protocoloBoui;
    }
    
    public static void regenerateWsignature(boObject sendActv, long performer, Date signDate) throws Exception
    {
        boObject variable = sendActv.getAttribute("message").getObject(); //$NON-NLS-1$
        boObject value = variable.getAttribute("value").getObject(); //$NON-NLS-1$
        boObject msg = value.getAttribute("valueObject").getObject(); //$NON-NLS-1$
        long templateBoui = msg.getAttribute("usedTemplate").getValueLong(); //$NON-NLS-1$
        GtTemplate template = GtTemplate.getTemplate(sendActv.getEboContext(), templateBoui);
        template.setAnswer(msg);
        template.setActvSendBoui(sendActv.getBoui());
        GtQuery queries[] = template.getQueries();
        for (int i = 0; i < queries.length; i++) 
        {
            ((GtQuery)queries[i]).calculateAutomicFields(sendActv.getEboContext(), template);        
        }
        template.setSignatureUserBoui(performer);
        template.setSignatureDate(signDate);
        template.generate(sendActv.getEboContext(), false, true);
    }
    
    private boolean javaValidation(EboContext boctx, ArrayList erros) throws boRuntimeException
    {
        JavaExecuter javaExec = new JavaExecuter(getNome());
        //imports
        javaExec.addImport("netgest.bo"); //$NON-NLS-1$
        javaExec.addImport("netgest.bo.def"); //$NON-NLS-1$
        javaExec.addImport("netgest.utils"); //$NON-NLS-1$
        javaExec.addImport("netgest.bo.runtime"); //$NON-NLS-1$
        javaExec.addImport("netgest.bo.utils"); //$NON-NLS-1$
        javaExec.addImport("netgest.bo.impl.document.merge.gestemp"); //$NON-NLS-1$
    
        //variaveis
        Contexto contexto = new Contexto(boctx);
        javaExec.addTypedVariable( "contexto", Contexto.class, contexto, null); //$NON-NLS-1$
        javaExec.addTypedVariable( "modelo", Modelo.class, new Modelo(boctx, this), null); //$NON-NLS-1$

        //javaCode
        javaExec.setJavaCode(validacao);

        Object result = javaExec.execute();
        if(result != null && result instanceof Boolean)
        {
            if(!((Boolean)result).booleanValue())
            {
                for (int i = 0; i < contexto.getErros().size(); i++) 
                {
                    erros.add(contexto.getErros().get(i));
                }
                return false;
            }
        }
        return true;
    }
    
    public ArrayList getTemplateFields()
    {
        return fields;
    }
    
    public ArrayList getTemplateBookmarks()
    {
        return bookmarks;
    }
    
    public boolean hasListDependents()
    {
        for (int i = 0; i < queries.size(); i++) 
        {
            if(((GtQuery)queries.get(i)).hasListDependents())
            {
                return true;
            }
        }
        return false;
    }
    
    public void setAnexos(EboContext boctx) throws boRuntimeException
    {
        if("forward".equals(getSendType())) //$NON-NLS-1$
        {
            boObject msgOriginal = boObject.getBoManager().loadObject(boctx, getMsgReplyFrom());
            if(msgOriginal != null)
            {
                boBridgeIterator bit = msgOriginal.getBridge("documents").iterator(); //$NON-NLS-1$
                bit.beforeFirst();
                String aux = null;
                while(bit.next())
                {
                    if(msgAnexos == null) msgAnexos = ""; //$NON-NLS-1$
                    aux = bit.currentRow().getValueLong()+";"; //$NON-NLS-1$
                    if(msgAnexos.indexOf(aux) < 0)
                        msgAnexos += bit.currentRow().getValueLong()+";"; //$NON-NLS-1$
                }
            }
        }
    }


    public void setAllowAttachs(byte allowattachs)
    {
        this.allowattachs = allowattachs;
    }


    public byte getAllowAttachs()
    {
        return allowattachs;
    }
    
}