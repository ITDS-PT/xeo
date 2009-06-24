/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge;

import netgest.bo.runtime.boRuntimeException;

import java.io.File;
import java.io.IOException;

import java.util.Calendar;


/**
 * Criação de um ficheiro temporário para a geração de um PDF's.
 * <P>
 * @author Francisco Câmara
 */
public class TempFile extends Object
{
    private static final String DEFAULT_SUFIX = ".xeo";

    public static File createTempFile(File directorio)
        throws boRuntimeException
    {
        return createTempFile(directorio, null, null);
    }

    public static String createNameForTempFile() throws boRuntimeException
    {
        return createNameForTempFile(null, null);
    }

    /**
     * Cria um ficheiro temporário para a geração de um PDF
     *
     * @param directorio directorio raiz
     */
    public static File createTempFile(File directorio, String prefix,
        String sufix) throws boRuntimeException
    {
        try
        {
            if (!directorio.exists())
            {
                directorio.mkdirs();
            }

            Calendar c = Calendar.getInstance();
            String s = directorio.getAbsolutePath();

            if (!s.endsWith("\\") || !s.endsWith("/"))
            {
                s += "\\";
            }

            StringBuffer sb = new StringBuffer(100);
            sb.append(s);

            if ((prefix != null) && !"".equals(prefix))
            {
                sb.append(prefix);
                sb.append("_");
            }

            sb.append(c.get(Calendar.YEAR)).append("_")
              .append(padding(2, (c.get(Calendar.MONTH) + 1))).append("_")
              .append(padding(2, c.get(Calendar.DATE))).append("_")
              .append(padding(2, c.get(Calendar.HOUR_OF_DAY))).append("_")
              .append(padding(2, c.get(Calendar.MINUTE))).append("_")
              .append(padding(2, c.get(Calendar.SECOND))).append("_").append(padding(
                    3, c.get(Calendar.MILLISECOND)));

            if ((sufix != null) && !"".equals(sufix))
            {
                sb.append(sufix);
            }

            File auxFile = new File(sb.toString());
            auxFile.createNewFile();

            return auxFile;
        }
        catch (IOException e)
        {
            throw new boRuntimeException("TempFile", "createTempFile", e);
        }
    }

    /**
     * Cria um ficheiro temporário para a geração de um PDF
     *
     * @param directorio directorio raiz
     */
    public static String createNameForTempFile(String prefix, String sufix)
        throws boRuntimeException
    {
        try
        {
            Calendar c = Calendar.getInstance();
            StringBuffer sb = new StringBuffer(100);

            if ((prefix != null) && !"".equals(prefix))
            {
                sb.append(prefix);
                sb.append("_");
            }

            sb.append(c.get(Calendar.YEAR)).append("_")
              .append(padding(2, (c.get(Calendar.MONTH) + 1))).append("_")
              .append(padding(2, c.get(Calendar.DATE))).append("_")
              .append(padding(2, c.get(Calendar.HOUR_OF_DAY))).append("_")
              .append(padding(2, c.get(Calendar.MINUTE))).append("_")
              .append(padding(2, c.get(Calendar.SECOND))).append("_").append(padding(
                    3, c.get(Calendar.MILLISECOND)));

            if ((sufix != null) && !"".equals(sufix))
            {
                sb.append(sufix);
            }

            return sb.toString();
        }
        catch (Exception e)
        {
            throw new boRuntimeException("TempFile", "createNameForTempFile", e);
        }
    }

    /**
     * Cria um ficheiro temporário para a geração de um PDF
     *
     * @param directorio directorio raiz
     */
    public static File createTempFile(int report, File directorio, String prefix)
        throws boRuntimeException
    {
        try
        {
            if (!directorio.exists())
            {
                directorio.mkdirs();
            }

            Calendar c = Calendar.getInstance();
            String s = directorio.getAbsolutePath();

            if (!s.endsWith("\\") || !s.endsWith("/"))
            {
                s += "\\";
            }

            StringBuffer sb = new StringBuffer(100);
            sb.append(s);

            if ((prefix != null) && !"".equals(prefix))
            {
                sb.append(prefix);
                sb.append("_");
            }

            sb.append(c.get(Calendar.YEAR)).append("_")
              .append(padding(2, (c.get(Calendar.MONTH) + 1))).append("_")
              .append(padding(2, c.get(Calendar.DATE))).append("_")
              .append(padding(2, c.get(Calendar.HOUR_OF_DAY))).append("_")
              .append(padding(2, c.get(Calendar.MINUTE))).append("_")
              .append(padding(2, c.get(Calendar.SECOND))).append("_").append(padding(
                    3, c.get(Calendar.MILLISECOND)));

            //sb.append(SUFIX);
            File auxFile = new File(sb.toString());
            auxFile.createNewFile();

            return auxFile;
        }
        catch (IOException e)
        {
            throw new boRuntimeException("TempFile", "createTempFile", e);
        }
    }

    private static String padding(int n, int data)
    {
        String s = new String();

        if ((data < 10) && (n > 1))
        {
            for (int i = 0; i < (n - 1); i++)
            {
                s += "0";
            }

            s += String.valueOf(data);
        }
        else if ((data < 100) && (n > 2))
        {
            for (int i = 0; i < (n - 2); i++)
            {
                s += "0";
            }

            s += String.valueOf(data);
        }
        else
        {
            return String.valueOf(data);
        }

        return s;
    }
}
