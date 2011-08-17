/*Enconding=UTF-8*/
package netgest.bo.utils;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.*;
import netgest.bo.*;
import netgest.utils.StringUtils;
import netgest.utils.StringEncrypter;

public class boEncrypter 
{

  private static final String ENCRIPT_PRFX = "{CIFRADO}";
  
  public static void main(String[] args) throws Exception {
	  System.out.println( staticDecrypt("0C03AFC0A30B71C72AC3DF32A403FDF080C9E8C20AED7C57") );
  }

  public boEncrypter()
  {
  }
  
  public static String encrypt(EboContext ctx,String toencrypt) throws boRuntimeException
  {
    //passar a vir do boconfig
    boolean developerMode=boConfig.isDeveloperMode();
    if (developerMode) return toencrypt;
    try
    {
      if (toencrypt!=null)
      {
        StringEncrypter enc= new StringEncrypter(StringEncrypter.DESEDE_ENCRYPTION_SCHEME,ctx.getBoSession().getId());
        return enc.encrypt(toencrypt);        
      }
      else return null;
    }
    catch (Exception e)
    {
      throw new boRuntimeException(MessageLocalizer.getMessage("ERROR_ENCRYPTING_THE_STRING"), "", e);      
    }
  }

  public static String decrypt(EboContext ctx,String todecrypt) throws boRuntimeException
  {
    boolean developerMode=boConfig.isDeveloperMode();
    if (developerMode) return todecrypt;
    try
    {
       if (todecrypt!=null)
      {
        StringEncrypter enc=new StringEncrypter(StringEncrypter.DESEDE_ENCRYPTION_SCHEME,ctx.getBoSession().getId());    
        return enc.decrypt(todecrypt);
      }
      else return null;
    }
    catch (Exception e)
    {
      throw new boRuntimeException(MessageLocalizer.getMessage("ERROR_DECRYPTING_THE_STRING"), "", e);
    }      
  }
  
   public static String staticEncrypt(String toencrypt) throws boRuntimeException
  {
    //passar a vir do boconfig
    try
    {
      if (toencrypt!=null)
      {
        StringEncrypter enc= new StringEncrypter(StringEncrypter.DESEDE_ENCRYPTION_SCHEME,"#o$aJ23.*aa45#qwertyucjslbvmchJ%JJJ345jkda");
        String valor = null;
        try{valor = enc.decrypt(toencrypt);}catch (Exception e){/*ignore*/}
        if(valor == null || !valor.startsWith(ENCRIPT_PRFX))
        {
            return enc.encrypt(ENCRIPT_PRFX + toencrypt);
        }
        return toencrypt;
      }
      else return null;
    }
    catch (Exception e)
    {
      throw new boRuntimeException(MessageLocalizer.getMessage("ERROR_ENCRYPTING_THE_STRING"), "", e);      
    }
  }

  public static String staticDecrypt(String todecrypt) throws boRuntimeException
  {
    try
    {
       if (todecrypt!=null)
      {
        StringEncrypter enc=new StringEncrypter(StringEncrypter.DESEDE_ENCRYPTION_SCHEME,"#o$aJ23.*aa45#qwertyucjslbvmchJ%JJJ345jkda");
        String valor = enc.decrypt(todecrypt);
        if(valor == null || valor.startsWith(ENCRIPT_PRFX))
        {
            return valor.substring(ENCRIPT_PRFX.length());
        }
        return valor;
      }
      else return null;
    }
    catch (Exception e)
    {
      throw new boRuntimeException(MessageLocalizer.getMessage("ERROR_DECRYPTING_THE_STRING"), "", e);
    }      
  }
  
  
}