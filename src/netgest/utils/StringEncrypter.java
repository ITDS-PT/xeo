/*Enconding=UTF-8*/
package netgest.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import netgest.bo.localizations.MessageLocalizer;

public class StringEncrypter
{
	
	public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
	public static final String DES_ENCRYPTION_SCHEME = "DES";
	public static final String			DEFAULT_ENCRYPTION_KEY	= "CHAVE POR DEFEITO";
	public static final int 	STRING_REQUIRED_SIZE = 24;
	
	private KeySpec				keySpec;
	private SecretKeyFactory	keyFactory;
	private Cipher				cipher;
	
	private static final String	UNICODE_FORMAT			= "UTF8";

	public StringEncrypter( String encryptionScheme ) throws EncryptionException
	{
		this( encryptionScheme, DEFAULT_ENCRYPTION_KEY );
	}

	public StringEncrypter( String encryptionScheme, String encryptionKey )
			throws EncryptionException
	{

		if ( encryptionKey == null )
				throw new IllegalArgumentException( MessageLocalizer.getMessage("ENCHYPTION_KEY_WAS_NULL") );
		if ( encryptionKey.trim().length() < STRING_REQUIRED_SIZE )
				throw new IllegalArgumentException(
						MessageLocalizer.getMessage("ENCHYPTION_KEY_WAS_LESS_THAN_24_CHARACTERS"));

		try
		{
			byte[] keyAsBytes = encryptionKey.getBytes( UNICODE_FORMAT );

			if ( encryptionScheme.equals( DESEDE_ENCRYPTION_SCHEME) )
			{
				keySpec = new DESedeKeySpec( keyAsBytes );
			}
			else if ( encryptionScheme.equals( DES_ENCRYPTION_SCHEME ) )
			{
				keySpec = new DESKeySpec( keyAsBytes );
			}
			else
			{
				throw new IllegalArgumentException( MessageLocalizer.getMessage("ENCHYPTION_SCHEME_NOT_SUPPORTED")+": "
													+ encryptionScheme );
			}

			keyFactory = SecretKeyFactory.getInstance( encryptionScheme );
			cipher = Cipher.getInstance( encryptionScheme );

		}
		catch (InvalidKeyException e)
		{
			throw new EncryptionException( e );
		}
		catch (UnsupportedEncodingException e)
		{
			throw new EncryptionException( e );
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new EncryptionException( e );
		}
		catch (NoSuchPaddingException e)
		{
			throw new EncryptionException( e );
		}

	}

	public String encrypt( String unencryptedString ) throws EncryptionException
	{
		if ( unencryptedString == null || unencryptedString.trim().length() == 0 )
				throw new IllegalArgumentException(MessageLocalizer.getMessage("UNENCRYPTED_STRING_WAS_NULL_OR_EMPTY"));

		try
		{
			SecretKey key = keyFactory.generateSecret( keySpec );
			cipher.init( Cipher.ENCRYPT_MODE, key );
			byte[] cleartext = unencryptedString.getBytes( UNICODE_FORMAT );
			byte[] ciphertext = cipher.doFinal( cleartext );
      return byteArrayToHexString(ciphertext);
		}
		catch (Exception e)
		{
			throw new EncryptionException( e );
		}
	}

	public String decrypt( String encryptedString ) throws EncryptionException
	{
		if ( encryptedString == null || encryptedString.trim().length() <= 0 )
				throw new IllegalArgumentException(MessageLocalizer.getMessage("ENCRYPTED_STRING_WAS_NULL_OR_EMPTY") );

		try
		{
			SecretKey key = keyFactory.generateSecret( keySpec );
			cipher.init( Cipher.DECRYPT_MODE, key );
    byte[] hextext = hexStringToByteArray(encryptedString);
    byte[] ciphertext = cipher.doFinal( hextext );

			return bytes2String( ciphertext );
		}
		catch (Exception e)
		{
			throw new EncryptionException( e );
		}
	}

	private static String bytes2String( byte[] bytes )
	{
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++)
		{
			stringBuffer.append( (char) bytes[i] );
		}
		return stringBuffer.toString();
	}

	public static class EncryptionException extends Exception
	{
		public EncryptionException( Throwable t )
		{
			super( t );
		}
	}
  
  public static String byteArrayToHexString(byte[] b){
      StringBuffer sb = new StringBuffer(b.length * 2);
      for (int i = 0; i < b.length; i++){
        int v = b[i] & 0xff;
        if (v < 16) {
          sb.append('0');
        }
        sb.append(Integer.toHexString(v));
      }
      return sb.toString().toUpperCase();
  }
  
  public static byte[] hexStringToByteArray(String s) {
      byte[] b = new byte[s.length() / 2];
      for (int i = 0; i < b.length; i++){
        int index = i * 2;
        int v = Integer.parseInt(s.substring(index, index + 2), 16);
        b[i] = (byte)v;
      }
      return b;
  }
}