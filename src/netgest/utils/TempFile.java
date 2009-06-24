/*Enconding=UTF-8*/
package netgest.utils;

import java.util.Random;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import netgest.bo.runtime.*;
import org.apache.log4j.Logger;


/**
 * Criação de um ficheiro temporário para a geração de um PDF's.
 * <P>
 * @author Francisco Câmara
 */
public class TempFile extends Object{

    private static final String DEFAULT_PREFIX = "temp";
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.utils.TempFile");
	/**
	 * Cria um ficheiro temporário
	 *
	 * @param prefix prefixo para o nome do ficehiro 
     * @param suffix extensão do ficheiro
     * @param directory directoria de raiz
	 */
    
    public static File createTempFile(String prefix, String suffix, File directory) throws boRuntimeException
    {
    	try {
            if(!directory.exists()){
                directory.mkdirs();
            }
            Calendar c = Calendar.getInstance();
            String s = directory.getAbsolutePath();
            if(!s.endsWith("/") && !s.endsWith("\\")){
                s += File.separator;
            }
            StringBuffer sb = new StringBuffer(100);
            sb.append(s);
            if(prefix == null || "".equals(prefix))
            {
                sb.append(DEFAULT_PREFIX);
            }
            else{
                sb.append(prefix);
            }
            sb.append("_");
            sb.append(c.get(Calendar.YEAR)).append("_").append(padding(2, (c.get(Calendar.MONTH) + 1)))
                    .append("_").append(padding(2, c.get(Calendar.DATE))).append("_")
                    .append(padding(2, c.get(Calendar.HOUR_OF_DAY))).append("_")
                    .append(padding(2, c.get(Calendar.MINUTE))).append("_")
                    .append(padding(2, c.get(Calendar.SECOND))).append("_")
                    .append(padding(3, c.get(Calendar.MILLISECOND)));
            if(suffix != null && !"".equals(suffix))
            {
                if(!suffix.startsWith("."))
                {
                    sb.append(".");
                }
                sb.append(suffix);
            }
			File auxFile = new File(sb.toString());
            auxFile.createNewFile();
            logger.debug(sb.toString());
			return auxFile;
        }
        catch(IOException e){
			throw new boRuntimeException("","",e);
        }
    }
    
    public static File createTempDirFile(String prefix, String suffix, File directory) throws boRuntimeException
    {
    	try {
            boolean exists = true;
            File auxFile = null;
            while(exists)
            {
                if(!directory.exists()){
                    directory.mkdirs();
                }
                Calendar c = Calendar.getInstance();
                String s = directory.getAbsolutePath();
                if(!s.endsWith("/") && !s.endsWith("\\")){
                    s += File.separator;
                }
                StringBuffer sb = new StringBuffer(100);
                sb.append(s);
                sb.append(c.get(Calendar.YEAR)).append("_").append(padding(2, (c.get(Calendar.MONTH) + 1)))
                        .append("_").append(padding(2, c.get(Calendar.DATE))).append("_")
                        .append(padding(2, c.get(Calendar.HOUR_OF_DAY))).append("_")
                        .append(padding(2, c.get(Calendar.MINUTE))).append("_")
                        .append(padding(2, c.get(Calendar.SECOND))).append("_")
                        .append(padding(3, c.get(Calendar.MILLISECOND)));
                
                sb.append(File.separator);
                if(prefix == null || "".equals(prefix))
                {
                    sb.append(DEFAULT_PREFIX);
                }
                else{
                    sb.append(prefix);
                }
                if(suffix != null && !"".equals(suffix))
                {
                    if(!suffix.startsWith("."))
                    {
                        sb.append(".");
                    }
                    sb.append(suffix);
                }
                auxFile = new File(sb.toString());
                exists = auxFile.exists();
                logger.debug(sb.toString());
            }
            if(!auxFile.getParentFile().exists())
            {
                auxFile.getParentFile().mkdirs();
            }
            auxFile.createNewFile();
			return auxFile;
        }
        catch(IOException e){
            logger.error("", e);
			throw new boRuntimeException("","",e);
        }
    }

    private static String padding(int n, int data){
        String s = new String();
        if(data < 10 && n > 1){
            for(int i = 0; i < n - 1; i++){
                s += "0";
            }
            s +=String.valueOf(data);
        }
        else if(data < 100  && n > 2){
            for(int i = 0; i < n - 2; i++){
                s += "0";
            }
            s +=String.valueOf(data);
        }
        else return String.valueOf(data);
        return s;
    }
    public static File createTempFile(String prefix, String suffix)
    {
        try
        {
            File xxx = File.createTempFile("netgest", ".none");
            String tmpPath = xxx.getParent();
            xxx.delete();
            xxx = new File(tmpPath + File.separator + "ngtbo");
    
            return TempFile.createTempDirFile(prefix, suffix, xxx);
        }
        catch (Exception e)
        {
            logger.warn("", e);
        }
        return null;
    }
}

