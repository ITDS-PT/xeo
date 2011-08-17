package netgest.bo.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JspRuntime {

	public static final int toInt(int v) {
		return v;
	}

	public static final int toInt(String v) {
		return Integer.valueOf(v).intValue();
	}

	public static final int toInt(Object v) {
		return toInt(v.toString());
	}

	public static final int toInt(float v) {
		return (int) v;
	}

	public static final int toInt(double v) {
		return (int) v;
	}

	public static final int toInt(long v) {
		return (int) v;
	}

	public static final int toInt(byte v) {
		return v;
	}

	public static final int toInt(char v) {
		return v;
	}

	public static final int toInt(short v) {
		return v;
	}

	public static final int toInt(boolean v) {
		if (v) {
			return 1;
		}
		return 0;
	}

	public static final Integer toIntObject(int v) {
		return new Integer(v);
	}

	public static final Integer toIntObject(String v) {
		return new Integer(toInt(v));
	}

	public static final String toStr(String v) {
		return v;
	}

	public static final String toStr(int v) {
		return Integer.toString(v);
	}

	public static final String toStr(byte v) {
		return Byte.toString(v);
	}

	public static final String toStr(char v) {
		Character c = new Character(v);
		return c.toString();
	}

	public static final String toStr(short v) {
		return Short.toString(v);
	}

	public static final String toStr(long v) {
		return Long.toString(v);
	}

	public static final String toStr(boolean v) {
		Boolean b = new Boolean(v);
		return b.toString();
	}

	public static final String toStr(Object v) {
		return v.toString();
	}

	public static final String toStr(float v) {
		return Float.toString(v);
	}

	public static final String toStr(double v) {
		return Double.toString(v);
	}

	public static final boolean toBoolean(Boolean v) {
		return v.booleanValue();
	}

	public static final boolean toBoolean(boolean b) {
		return b;
	}

	public static final boolean toBoolean(String v) {
		return Boolean.valueOf(v).booleanValue();
	}

	public static final boolean toBoolean(int v) {
		return v != 0;
	}

	public static final boolean toBoolean(byte v) {
		return toBoolean(toInt(v));
	}

	public static final boolean toBoolean(char v) {
		return toBoolean(toInt(v));
	}

	public static final boolean toBoolean(short v) {
		return toBoolean(toInt(v));
	}

	public static final boolean toBoolean(long v) {
		return toBoolean(toInt(v));
	}

	public static final boolean toBoolean(float v) {
		return toBoolean(toInt(v));
	}

	public static final boolean toBoolean(double v) {
		return toBoolean(toInt(v));
	}

	public static final boolean toBoolean(Object v) {
		return toBoolean(v.toString());
	}

	public static final Boolean toBooleanObject(boolean v) {
		return new Boolean(v);
	}

	public static final Boolean toBooleanObject(String v) {
		return new Boolean(toBoolean(v));
	}

	public static final byte toByte(Byte v) {
		return v.byteValue();
	}

	public static final byte toByte(byte v) {
		return v;
	}

	public static final byte toByte(String v) {
		return Byte.valueOf(v).byteValue();
	}

	public static final byte toByte(Object v) {
		return Byte.valueOf(v.toString()).byteValue();
	}

	public static final byte toByte(int v) {
		return (byte) v;
	}

	public static final byte toByte(char v) {
		return (byte) v;
	}

	public static final byte toByte(short v) {
		return (byte) v;
	}

	public static final byte toByte(long v) {
		return (byte) (int) v;
	}

	public static final byte toByte(float v) {
		return (byte) (int) v;
	}

	public static final byte toByte(double v) {
		return (byte) (int) v;
	}

	public static final byte toByte(boolean v) {
		return (byte) toInt(v);
	}

	public static final Byte toByteObject(byte v) {
		return new Byte(v);
	}

	public static final Byte toByteObject(String v) {
		return new Byte(toByte(v));
	}

	public static final short toShort(Short v) {
		return v.shortValue();
	}

	public static final short toShort(short v) {
		return v;
	}

	public static final short toShort(String v) {
		return Short.valueOf(v).shortValue();
	}

	public static final short toShort(Object v) {
		return Short.valueOf(v.toString()).shortValue();
	}

	public static final short toShort(int v) {
		return (short) v;
	}

	public static final short toShort(char v) {
		return (short) v;
	}

	public static final short toShort(byte v) {
		return (short) v;
	}

	public static final short toShort(long v) {
		return (short) (int) v;
	}

	public static final short toShort(float v) {
		return (short) (int) v;
	}

	public static final short toShort(double v) {
		return (short) (int) v;
	}

	public static final short toShort(boolean v) {
		return (short) toInt(v);
	}

	public static final Short toShortObject(short v) {
		return new Short(v);
	}

	public static final Short toShortObject(String v) {
		return Short.valueOf(v);
	}

	public static final char toCharacter(Character v) {
		return v.charValue();
	}

	public static final char toCharacter(char v) {
		return v;
	}

	public static final char toCharacter(String v) {
		if (v.length() > 0) {
			return v.charAt(0);
		}
		return '\000';
	}

	public static final char toCharacter(Object v) {
		return toCharacter(v.toString());
	}

	public static final char toCharacter(boolean v) {
		if (v) {
			return '\001';
		}
		return '\000';
	}

	public static final char toCharacter(int v) {
		return (char) v;
	}

	public static final char toCharacter(short v) {
		return (char) v;
	}

	public static final char toCharacter(byte v) {
		return (char) v;
	}

	public static final char toCharacter(long v) {
		return (char) (int) v;
	}

	public static final char toCharacter(float v) {
		return (char) (int) v;
	}

	public static final char toCharacter(double v) {
		return (char) (int) v;
	}

	public static final Character toCharacterObject(char v) {
		return new Character(v);
	}

	public static final Character toCharacterObject(String v) {
		return new Character(toCharacter(v));
	}

	public static final double toDouble(Double v) {
		return v.doubleValue();
	}

	public static final double toDouble(double v) {
		return v;
	}

	public static final double toDouble(String v) {
		return Double.valueOf(v).doubleValue();
	}

	public static final double toDouble(Object v) {
		return toDouble(v.toString());
	}

	public static final double toDouble(int v) {
		return v;
	}

	public static final double toDouble(char v) {
		return v;
	}

	public static final double toDouble(byte v) {
		return v;
	}

	public static final double toDouble(long v) {
		return v;
	}

	public static final double toDouble(float v) {
		return v;
	}

	public static final double toDouble(short v) {
		return v;
	}

	public static final double toDouble(boolean v) {
		return toInt(v);
	}

	public static final Double toDoubleObject(double v) {
		return new Double(v);
	}

	public static final Double toDoubleObject(String v) {
		return Double.valueOf(v);
	}

	public static final float toFloat(Float v) {
		return v.floatValue();
	}

	public static final float toFloat(float v) {
		return v;
	}

	public static final float toFloat(String v) {
		return Float.valueOf(v).floatValue();
	}

	public static final float toFloat(Object v) {
		return toFloat(v.toString());
	}

	public static final float toFloat(int v) {
		return v;
	}

	public static final float toFloat(char v) {
		return v;
	}

	public static final float toFloat(byte v) {
		return v;
	}

	public static final float toFloat(long v) {
		return (float) v;
	}

	public static final float toFloat(double v) {
		return (float) v;
	}

	public static final float toFloat(short v) {
		return v;
	}

	public static final float toFloat(boolean v) {
		return toInt(v);
	}

	public static final Float toFloatObject(float v) {
		return new Float(v);
	}

	public static final Float toFloatObject(String v) {
		return Float.valueOf(v);
	}

	public static final long toLong(Long v) {
		return v.longValue();
	}

	public static final long toLong(long v) {
		return v;
	}

	public static final long toLong(String v) {
		return Long.valueOf(v).longValue();
	}

	public static final long toLong(Object v) {
		return toLong(v.toString());
	}

	public static final long toLong(int v) {
		return v;
	}

	public static final long toLong(char v) {
		return v;
	}

	public static final long toLong(byte v) {
		return v;
	}

	public static final long toLong(short v) {
		return v;
	}

	public static final long toLong(boolean v) {
		return toInt(v);
	}

	public static final Long toLongObject(long v) {
		return new Long(v);
	}

	public static final Long toLongObject(String v) {
		return Long.valueOf(v);
	}

	public static String genPageUrl(String oUrl, HttpServletRequest req,
			String[] names, String[] values) {
		return genPageUrl(oUrl, req, names, values, req.getCharacterEncoding());
	}

	public static String genPageUrl(String oUrl, HttpServletRequest req,
			HttpServletResponse resp, String[] names, String[] values) {
		return genPageUrl(oUrl, req, names, values, getCharacterEncoding(req,
				resp));
	}

	public static String getCharacterEncoding(HttpServletRequest req,
			HttpServletResponse resp) {
		String encoding = null;

		encoding = req.getCharacterEncoding();
		if (encoding != null) {
			return encoding;
		}
		encoding = resp.getCharacterEncoding();
		if (encoding != null) {
			return encoding;
		}

		return encoding;
	}

	private static String encodeQueryStringName(String value, String encoding) {
		try {
			if (encoding != null && encoding.length() > 0)
				return URLEncoder.encode(value, encoding);
			else
				return URLEncoder.encode(value);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

	}

	public static String genPageUrl(String oUrl, HttpServletRequest req,
			String[] names, String[] values, String encoding) {
		StringBuffer sb = new StringBuffer();

		if (names.length != values.length) {
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < names.length; i++) {
			sb.append('&');
			sb.append(encodeQueryStringName(names[i], encoding));
			sb.append('=');
			sb.append(encodeQueryStringName(values[i], encoding));
		}

		if (sb.length() == 0) {
			return oUrl;
		}

		if (oUrl.indexOf("?") == -1) {
			sb.setCharAt(0, '?');
		}

		return oUrl + sb;
	}

	public static String addQueryStrFromParameters(String oUrl,
			HttpServletRequest req, String givenEncoding) {
		StringBuffer sb = new StringBuffer();

		Enumeration e = req.getParameterNames();

		while (e.hasMoreElements()) {
			String paramName = (String) e.nextElement();
			String encodedParamName = encodeQueryStringName(paramName,
					givenEncoding);
			String[] paramValues = req.getParameterValues(paramName);
			for (int i = 0; i < paramValues.length; i++) {
				sb.append('&');
				sb.append(encodedParamName);
				sb.append('=');
				sb.append(encodeQueryStringName(paramValues[i], givenEncoding));
			}
		}

		if (sb.length() == 0) {
			return oUrl;
		}

		if (oUrl.indexOf("?") == -1) {
			sb.setCharAt(0, '?');
		}

		return oUrl + sb;
	}

}
