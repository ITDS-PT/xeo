package netgest.utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLRemover 
{
    private static final Pattern pat = Pattern.compile("\\<\\/?(p|div|img|td|tr|b|table|title|col|a|body|tbody|head|span|strong|small|strike)(\\s.*?)?\\>");
    private static final String SPECIAL_CHAR[] = 
    {
        "&lsquo;" , 
        "&rsquo;" , 
        "&sbquo;" , 
        "&ldquo;" , 
        "&rdquo;" , 
        "&bdquo;" , 
        "&dagger;", 
        "&Dagger;", 
        "&permil;", 
        "&lsaquo;", 
        "&rsaquo;", 
        "&ndash;" , 
        "&mdash;" , 
        "&nbsp;"  , 
        "&iexcl;" , 
        "&cent;"  , 
        "&pound;" , 
        "&curren;", 
        "&yen;"   , 
        "&brvbar;", 
        "&brkbar;", 
        "&sect;"  , 
        "&uml;"   , 
        "&die;"   , 
        "&copy;"  , 
        "&ordf;"  , 
        "&laquo;" , 
        "&not;"   , 
        "&shy;"   , 
        "&reg;"   , 
        "&macr;"  , 
        "&hibar;" , 
        "&deg;"   , 
        "&plusmn;", 
        "&sup2;"  , 
        "&sup3;"  , 
        "&acute;" , 
        "&micro;" , 
        "&para;"  , 
        "&middot;", 
        "&cedil;" , 
        "&sup1;"  , 
        "&ordm;"  , 
        "&raquo;" , 
        "&frac14;", 
        "&frac12;", 
        "&frac34;", 
        "&iquest;", 
        "&Agrave;", 
        "&Aacute;", 
        "&Acirc;" , 
        "&Atilde;", 
        "&Auml;"  , 
        "&Aring;" , 
        "&AElig;" , 
        "&Ccedil;", 
        "&Egrave;", 
        "&Eacute;", 
        "&Ecirc;" , 
        "&Euml;"  , 
        "&Igrave;", 
        "&Iacute;", 
        "&Icirc;" , 
        "&Iuml;"  , 
        "&ETH;"   , 
        "&Ntilde;", 
        "&Ograve;", 
        "&Oacute;", 
        "&Ocirc;" , 
        "&Otilde;", 
        "&Ouml;"  , 
        "&times;" , 
        "&Oslash;", 
        "&Ugrave;", 
        "&Uacute;", 
        "&Ucirc;" , 
        "&Uuml;"  , 
        "&Yacute;", 
        "&THORN;" , 
        "&szlig;" , 
        "&agrave;", 
        "&aacute;", 
        "&acirc;" , 
        "&atilde;", 
        "&auml;"  , 
        "&aring;" , 
        "&aelig;" , 
        "&ccedil;", 
        "&egrave;", 
        "&eacute;", 
        "&ecirc;" , 
        "&euml;"  , 
        "&igrave;", 
        "&iacute;", 
        "&icirc;" , 
        "&iuml;"  , 
        "&eth;"   , 
        "&ntilde;", 
        "&ograve;", 
        "&oacute;", 
        "&ocirc;" , 
        "&otilde;", 
        "&ouml;"  , 
        "&divide;", 
        "&oslash;", 
        "&ugrave;", 
        "&uacute;", 
        "&ucirc;" , 
        "&uuml;"  , 
        "&yacute;", 
        "&thorn;" , 
        "&yuml;"  ,
        "&#150;",
        "&#151;",
        "&#160;",
        "&#161;",
        "&#162;",
        "&#163;",
        "&#164;",
        "&#165;",
        "&#166;",
        "&#167;",
        "&#168;",
        "&#169;",
        "&#170;",
        "&#171;",
        "&#172;",
        "&#173;",
        "&#174;",
        "&#175;",
        "&#175;",
        "&#176;",
        "&#177;",
        "&#178;",
        "&#179;",
        "&#180;",
        "&#181;",
        "&#182;",
        "&#183;",
        "&#184;",
        "&#185;",
        "&#186;",
        "&#187;",
        "&#188;",
        "&#189;",
        "&#190;",
        "&#191;",
        "&#192;",
        "&#193;",
        "&#194;",
        "&#195;",
        "&#196;",
        "&#197;",
        "&#198;",
        "&#199;",
        "&#200;",
        "&#201;",
        "&#202;",
        "&#203;",
        "&#204;",
        "&#205;",
        "&#206;",
        "&#207;",
        "&#208;",
        "&#209;",
        "&#210;",
        "&#211;",
        "&#212;",
        "&#213;",
        "&#214;",
        "&#215;",
        "&#216;",
        "&#217;",
        "&#218;",
        "&#219;",
        "&#220;",
        "&#221;",
        "&#222;",
        "&#223;",
        "&#224;",
        "&#225;",
        "&#226;",
        "&#227;",
        "&#228;",
        "&#229;",
        "&#230;",
        "&#231;",
        "&#232;",
        "&#233;",
        "&#234;",
        "&#235;",
        "&#236;",
        "&#237;",
        "&#238;",
        "&#239;",
        "&#240;",
        "&#241;",
        "&#242;",
        "&#243;",
        "&#244;",
        "&#245;",
        "&#246;",
        "&#247;",
        "&#248;",
        "&#249;",
        "&#250;",
        "&#251;",
        "&#252;",
        "&#253;",
        "&#254;",
        "&#255;"
    };
    private static final String SPECIAL_CHAR_REPLACE[] = 
    {
        "‘",
        "’",
        "‚",
        "“",
        "”",
        "„",
        "†",
        "‡",
        "‰",
        "‹",
        "›",
        "–",
        "—",
        " ",
        "¡",
        "¢",
        "£",
        "¤",
        "¥",
        "¦",
        "¦",
        "§",
        "¨",
        "¨",
        "©",
        "ª",
        "«",
        "¬",
        "­",
        "®",
        "¯",
        "¯",
        "°",
        "±",
        "²",
        "³",
        "´",
        "µ",
        "¶",
        "·",
        "¸",
        "¹",
        "º",
        "»",
        "¼",
        "½",
        "¾",
        "¿",
        "À",
        "Á",
        "Â",
        "Ã",
        "Ä",
        "Å",
        "Æ",
        "Ç",
        "È",
        "É",
        "Ê",
        "Ë",
        "Ì",
        "Í",
        "Î",
        "Ï",
        "Ð",
        "Ñ",
        "Ò",
        "Ó",
        "Ô",
        "Õ",
        "Ö",
        "×",
        "Ø",
        "Ù",
        "Ú",
        "Û",
        "Ü",
        "Ý",
        "Þ",
        "ß",
        "à",
        "á",
        "â",
        "ã",
        "ä",
        "å",
        "æ",
        "ç",
        "è",
        "é",
        "ê",
        "ë",
        "ì",
        "í",
        "î",
        "ï",
        "ð",
        "ñ",
        "ò",
        "ó",
        "ô",
        "õ",
        "ö",
        "÷",
        "ø",
        "ù",
        "ú",
        "û",
        "ü",
        "ý",
        "þ",
        "ÿ",
        "–",
        "—",
        " ",
        "¡",
        "¢",
        "£",
        "¤",
        "¥",
        "¦",
        "§",
        "¨",
        "©",
        "ª",
        "«",
        "¬",
        "­",
        "®",
        "¯",
        "¯",
        "°",
        "±",
        "²",
        "³",
        "´",
        "µ",
        "¶",
        "·",
        "¸",
        "¹",
        "º",
        "»",
        "¼",
        "½",
        "¾",
        "¿",
        "À",
        "Á",
        "Â",
        "Ã",
        "Ä",
        "Å",
        "Æ",
        "Ç",
        "È",
        "É",
        "Ê",
        "Ë",
        "Ì",
        "Í",
        "Î",
        "Ï",
        "Ð",
        "Ñ",
        "Ò",
        "Ó",
        "Ô",
        "Õ",
        "Ö",
        "×",
        "Ø",
        "Ù",
        "Ú",
        "Û",
        "Ü",
        "Ý",
        "Þ",
        "ß",
        "à",
        "á",
        "â",
        "ã",
        "ä",
        "å",
        "æ",
        "ç",
        "è",
        "é",
        "ê",
        "ë",
        "ì",
        "í",
        "î",
        "ï",
        "ð",
        "ñ",
        "ò",
        "ó",
        "ô",
        "õ",
        "ö",
        "÷",
        "ø",
        "ù",
        "ú",
        "û",
        "ü",
        "ý",
        "þ",
        "ÿ"
    };
    public HTMLRemover()
    {
    }
    
    public static String remove(String code)
    {
        try
        {
            String toRet;
            Matcher m = pat.matcher(code);
            toRet = m.replaceAll("");
            for (int i = 0; i < SPECIAL_CHAR.length; i++) 
            {
                toRet = toRet.replaceAll(SPECIAL_CHAR[i], SPECIAL_CHAR_REPLACE[i]);
            }
            return toRet;
        }
        catch (Exception e)
        {
            /*IGNORE*/
        }
        return code;
    }
}