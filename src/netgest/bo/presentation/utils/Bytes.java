/*Enconding=UTF-8*/
package netgest.bo.presentation.utils;
import java.util.Calendar;
import java.util.Date;

/**
 * Esta classe possúi vários métodos utilitários para trabalhar com bytes.
 *
 * @author Francisco Câmara
 * @version 
 */
public final class Bytes {
    /**
     * Representação de um array vazio de bytes.
     */
    static final byte[] EMPTY_BYTES = new byte[0];

    /**
     * Constructor.
     */
    private Bytes() {
    }

    /**
     * Devolve a representação de um inteiro em bytes.
     *
     * @param i inteiro a converter
     * @return a representaçao de um inteiro em bytes
     */
    public static byte[] valueOf(int i) {
        return String.valueOf(i).getBytes();
    }

    /**
     * Devolve a representação de um long em bytes.
     *
     * @param l long a converter
     * @return a representaçao de um long em bytes
     */
    public static byte[] valueOf(long l) {
        return String.valueOf(l).getBytes();
    }

    /**
     * Devolve a representação de um double em bytes.
     *
     * @param d double a converter
     * @return a representaçao de um double em bytes
     */
    public static byte[] valueOf(double d) {
        return String.valueOf(d).getBytes();
    }

    /**
     * Devolve a representação de um float em bytes.
     *
     * @param f float a converter
     * @return a representaçao de um float em bytes
     */
    public static byte[] valueOf(float f) {
        return String.valueOf(f).getBytes();
    }

    /**
     * Devolve a representação de um boolean em bytes.
     *
     * @param b boolean a converter
     * @return a representaçao de um boolean em bytes
     */
    public static byte[] valueOf(boolean b) {
        return String.valueOf(b).getBytes();
    }

    /**
     * Devolve a representação de um array de caracteres em bytes.
     *
     * @param data array de caracteres a converter
     * @return a representaçao de um array de caracteres em bytes
     * @exception NullPointerException se o char[] seja null
     */
    public static byte[] valueOf(char[] data) {
        return String.valueOf(data).getBytes();
    }

    /**
     * Devolve a representação de um subarray de caracteres em bytes.
     *
     * @param data array de caracteres a converter
     * @param offset indice do primeiro caracter
     * @param count número de caracteres
     * @return a representaçao de um array de caracteres em bytes
     * @exception NullPointerException se o char[] seja null
     * @exception StringIndexOutOfBoundsException se <code>offset</code> é negativo,
     *          ou <code>count</code> é negatico, ou <code>offset+count</code>
     *          é maior que <code>data.length</code>
     */
    public static byte[] valueOf(char[] data, int offset, int count) {
        return String.valueOf(data, offset, count).getBytes();
    }

    /**
     * Devolve a representação de uma data em bytes.
     *
     * @param date data a converter
     * @return a representaçao de uma data em bytes
     */
    public static byte[] valueOf(Date date) {
        return dateToBytes(date, false);
    }

    public static byte[] dateToBytes(Date date, boolean incomplete) {
        if (date == null) {
            return Bytes.EMPTY_BYTES;
        }

        String str;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        if (!incomplete) {
            final int month = cal.get(Calendar.MONTH) + 1;
            final int day = cal.get(Calendar.DATE);

            StringBuffer sb = new StringBuffer(10);
            sb.append( cal.get(Calendar.YEAR) + 1900 );
            sb.append("/");

            if (month < 10) {
                sb.append("0");
            }
            sb.append(month);

            sb.append("/");

            if (day < 10) {
                sb.append("0");
            }
            sb.append(day);

            str = sb.toString();
        }
        else {
            final int month = cal.get(Calendar.MONTH) + 1;
            final int day = cal.get(Calendar.DATE);

            StringBuffer sb = new StringBuffer(5);

            if (month < 10) {
                sb.append("0");
            }
            sb.append(month);

            sb.append("/");

            if (day < 10) {
                sb.append("0");
            }
            sb.append(day);

            str = sb.toString();
        }

        return Bytes.valueOf(str);
    }

    /**
     * Devolve a representação de um objecto de caracteres em bytes.
     *
     * @param data array de caracteres a converter
     * @return a representaçao de um array de caracteres em bytes
     */
    public static byte[] valueOf(Object obj) {
        String str = obj == null ? null : obj.toString();

        return str == null || str.length() == 0 ? EMPTY_BYTES : str.getBytes();
    }
}
