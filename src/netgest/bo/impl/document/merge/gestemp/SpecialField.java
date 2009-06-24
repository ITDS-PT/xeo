package netgest.bo.impl.document.merge.gestemp;
import java.util.Hashtable;

public class SpecialField 
{
    // Message Fields
    public static final String MESSAGE_ID           = "mensagemID";
    public static final String MESSAGE_ID_CONTENT   = "mensagemIDExt";
    public static final String MESSAGE_DATE         = "mensagemData";
    public static final String MESSAGE_ANEXOS       = "msgAnexos";

/*
    // Criador
    public static final String MESSAGE_CREATOR_ID        = "mensagemCriadorId";
    public static final String MESSAGE_CREATOR_USERNAME  = "mensagemCriadorUsername";
    public static final String MESSAGE_CREATOR_NAME      = "mensagemCriadorNome";
    public static final String MESSAGE_CREATOR_LASTNAME  = "mensagemCriadorApelido";
    public static final String MESSAGE_CREATOR_EMAIL     = "mensagemCriadorEmail";

    // De
    public static final String MESSAGE_DE_ID        = "mensagemDeId";
    public static final String MESSAGE_DE_NAME      = "mensagemDeNome";
    public static final String MESSAGE_DE_LASTNAME  = "mensagemDeApelido";
    public static final String MESSAGE_DE_EMAIL     = "mensagemDeEmail";
    public static final String MESSAGE_DE_TELEFONE  = "mensagemDeTelefone";
    public static final String MESSAGE_DE_FAX       = "mensagemDeFax";
*/

    //Fax Fields
    public static final String FAX_URGENT       = "faxUrgente";
    public static final String FAX_REVER        = "faxRever";
    public static final String FAX_COMENTAR     = "faxComentar";
    public static final String FAX_RESPONDER    = "faxResponder";
    public static final String FAX_NUMERO       = "faxNumero";
    public static final String FAX_ANEXOS       = "faxAnexos";
    
    //Letter Fields
    public static final String LETTER_ENVIO             = "cartaTipoEnvio";
    public static final String LETTER_REGISTO           = "cartaRegistoCTT";
    public static final String LETTER_REMETENTE_1       = "cartaRemetenteCTT1";
    public static final String LETTER_REMETENTE_2       = "cartaRemetenteCTT2";
    public static final String LETTER_REGISTO_CONTENT   = "cartaRegistoCTTExt";
    public static final String LETTER_SRP               = "cartaSrpCTT";
    public static final String LETTER_CLIENTE           = "cartaClienteCTT";
    public static final String LETTER_SRP_CLIENTE       = "cartaSrpClienteCTT";
    public static final String LETTER_4ESTADOS          = "cartaEstadosCTT";
    public static final String LETTER_4ESTADOS_CONTENT  = "cartaEstadosCTTExt";

    //Signature Fields
    public static final String APPROVER_SIGNATURE   = "aprovadorAssinatura";
    public static final String APPROVER_NAME        = "aprovadorNome";
    public static final String APPROVER_FUNCAO      = "aprovadorFuncao";
    public static final String APPROVER_DATE        = "aprovadorData";
        
    public static boolean isSpecialField(String fieldName)
    {
        
        Hashtable ht = new Hashtable();
        
        ht.put(MESSAGE_ID,Object.class);
        ht.put(MESSAGE_ID_CONTENT,Object.class);
        ht.put(MESSAGE_DATE,Object.class);
        ht.put(MESSAGE_ANEXOS,Object.class);
        
/*
 *      ht.put(MESSAGE_CREATOR_ID,Object.class);
        ht.put(MESSAGE_CREATOR_USERNAME,Object.class);
        ht.put(MESSAGE_CREATOR_NAME,Object.class);
        ht.put(MESSAGE_CREATOR_LASTNAME,Object.class);
        ht.put(MESSAGE_CREATOR_EMAIL,Object.class);
        
        ht.put(MESSAGE_DE_ID,Object.class);
        ht.put(MESSAGE_DE_NAME,Object.class);
        ht.put(MESSAGE_DE_LASTNAME,Object.class);
        ht.put(MESSAGE_DE_EMAIL,Object.class);
        ht.put(MESSAGE_DE_TELEFONE,Object.class);
        ht.put(MESSAGE_DE_FAX,Object.class);
*/        
        ht.put(FAX_URGENT,Object.class);
        ht.put(FAX_REVER,Object.class);
        ht.put(FAX_COMENTAR,Object.class);
        ht.put(FAX_RESPONDER,Object.class);
        ht.put(FAX_NUMERO,Object.class);
        ht.put(FAX_ANEXOS,Object.class);
        
        ht.put(LETTER_ENVIO,Object.class);
        ht.put(LETTER_REGISTO,Object.class);
        ht.put(LETTER_REMETENTE_1,Object.class);
        ht.put(LETTER_REMETENTE_2,Object.class);
        ht.put(LETTER_REGISTO_CONTENT,Object.class);
        ht.put(LETTER_SRP_CLIENTE,Object.class);
        ht.put(LETTER_CLIENTE,Object.class);
        ht.put(LETTER_4ESTADOS,Object.class);
        ht.put(LETTER_4ESTADOS_CONTENT,Object.class);
        
        ht.put(APPROVER_SIGNATURE,Object.class);
        ht.put(APPROVER_NAME,Object.class);
        ht.put(APPROVER_FUNCAO,Object.class);
        ht.put(APPROVER_DATE,Object.class);
        
        return ht.containsKey( fieldName );
    }
    
}