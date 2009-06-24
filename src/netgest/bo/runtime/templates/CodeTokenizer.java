/*Enconding=UTF-8*/
package netgest.bo.runtime.templates;
import java.util.*;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeTokenizer  {
    public static final byte CHARACTER_LITERAL=2;
    public static final byte STRING_LITERAL=3;
    public static final byte STRING_LITERAL_QUOTES=5;
    public static final byte NUMBER=7;
    public static final byte NEXT_LINE=11;
    public static final byte OPERATOR=13;
    public static final byte VARIABLE=15;
    
    private Matcher p_matcher;
    private String  p_code;
    
    public CodeTokenizer(String code) {
        Vector patts = new Vector();
        patts.add("'(?:\\\\[^']+|[^'])'"); // Character Literal
        patts.add("(\"(?:\\\\.|[^\"\\\\])*\")"); // String literal
        patts.add("(\'(?:\\\\.|[^\'\\\\])*\')"); // Java Sitaxe error string betwin '
        patts.add("(([0-9]|\\.[0-9]+)+(\\.[0-9]+)?)"); // Any number
        patts.add("([\\n])"); // Next line
        patts.add("([^a-zA-Z0-9 \n])+"); // Operators +,-,*,...
        patts.add("([a-zA-Z]\\w*)"); //  Any word
        String pattern = "("+ join(patts,")|(") + ")";
        Pattern xx = Pattern.compile(pattern);
        p_matcher = xx.matcher(code);
        p_code = code;
    }
    public Token getNextToken() {
        Token ret=null;
        if(p_matcher.find()) {
            int pos=p_matcher.start();
            String str=null;
            byte i;
            for (i = 1;str==null && i <= p_matcher.groupCount(); i++)  {
                str=p_matcher.group(i);
            }
            if(str!=null)
                ret = new Token(str,i,pos);
        }
        return ret;
    }
    public LinkedList getTokens() {
        LinkedList ret = new LinkedList();
        Token x;
        while((x=getNextToken())!=null) {
            ret.add(x);
        }
        return ret;
    }
    public class Token {
        byte p_type;
        String p_value;
        int p_pos;
        Token(String value,byte type,int pos) {
            p_type = type;
            p_value=value;
            p_pos=pos;
        }
        public byte getType() {
            return p_type;
        }
        public String getString() {
            return p_value;
        }
        public int getPosition() {
            return p_pos;
        }
    }
    public static final String join(Vector xxx,String chara) {
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < xxx.size(); i++)  {
            ret.append(xxx.get(i));
            if(i<xxx.size()-1) ret.append(chara);
        }
        return ret.toString();
    }
    
}