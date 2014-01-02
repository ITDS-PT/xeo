/*Enconding=UTF-8*/
package netgest.utils;
import java.sql.*;

import netgest.bo.localizations.MessageLocalizer;
//import netgest.document.DataNode;

public class ExpressionParser
{

    private ResultSet p_rslt;
    public static final String stoplist = "=,!=,||,),(,&&,<,>, ,";

    public Object parseExpression(ResultSet vars,String expression) {
        p_rslt = vars;
        Object[] _slots = new Object[256];
        _slots[255] = new Integer(0);
        String exp = parseWords(expression);
        exp = parseExpression(exp,_slots);
        return parseValues(exp,_slots);
    }
    private String  parseExpression(String exp,Object[] _slots) {
        while (exp.indexOf("(")!=-1) {
            int posb = exp.indexOf("(")+1;
            int pose = exp.indexOf(")");
            int tmpposb;
            while((tmpposb = exp.indexOf("(",posb))<pose && tmpposb!=-1) {
                posb =++tmpposb;
            }
            if(pose == -1)
                throw new RuntimeException(MessageLocalizer.getMessage("UNBALANCE")+" (");
            exp = exp.substring(0,posb-1)+
                  parseExpression(exp.substring(posb,pose),_slots)+
                  exp.substring(pose+1);
        }
        return execute(exp,_slots);
    }
    private String execute(String exp,Object[] _slots) {
        String lo;
        String ro;
        exp = exp.trim();
        return executeOperators(exp,_slots);
    }
    private String executeOperators(String exp,Object[] _slots) {
        String op=null;
        while (
              (exp.lastIndexOf("<") != -1) ||
              (exp.lastIndexOf(">") != -1) ||
              (exp.lastIndexOf("<") != -1) ||
              (exp.lastIndexOf("&&") != -1) ||
              (exp.lastIndexOf("||") != -1) ||
              (exp.lastIndexOf("=") != -1)
              ) {
              if(exp.lastIndexOf("!=")!=-1)
                op = "!=";
              else if(exp.lastIndexOf("<")!=-1)
                op = "<";
              else if(exp.lastIndexOf(">")!=-1)
                op = ">";
              else if(exp.lastIndexOf("=")!=-1)
                op = "=";
              else if(exp.lastIndexOf("&&")!=-1)
                op = "&&";
              else if(exp.lastIndexOf("||")!=-1)
                op = "||";

              String lo = exp.substring(0,exp.lastIndexOf(op));
              int looffset = exp.lastIndexOf(op)-(lo.length()-lo.trim().length());
              lo = lo.trim();
              StringBuffer loo = new StringBuffer();
              int ix=1;
              while((lo.length()+1)-ix > 0 && (
                    (ix>1 && lo.charAt(lo.length()-1) == '\'' && lo.charAt(0)!=lo.charAt(lo.length()-ix)) ||
                    (ix>1 && lo.charAt(lo.length()-1) == '\"' && lo.charAt(0)!=lo.charAt(lo.length()-ix)) ||
                    lo.charAt(lo.length()-ix) != ' '
                    ))
              {
                  loo.insert(0,lo.charAt(lo.length()-ix));
                  ix++;
              }
              looffset-=ix-1;
              String ro = exp.substring(exp.lastIndexOf(op)+op.length());
              int rooffset = exp.lastIndexOf(op)+op.length()+(ro.length()-ro.trim().length());
              ro = ro.trim();
              StringBuffer roo = new StringBuffer();
              ix=0;
              while(ix < ro.length() && (
                    (ix<=1 || ro.charAt(0) == '\'' && ro.charAt(0) != ro.charAt(ix-1)) ||
                    (ix<=1 || ro.charAt(0) == '\"' && ro.charAt(0) != ro.charAt(ix-1)) ||
                    (ro.charAt(0) != '\'' && ro.charAt(0)!='\"' && ro.charAt(ix-1) != ' ')
                    )) {
                  roo.append(ro.charAt(ix));
                  ix++;
              }
              rooffset += ix;

              Object lop = parseValues(loo.toString().trim(),_slots);
              Object rop = parseValues(roo.toString().trim(),_slots);
              Object ret = new Boolean(executeBoolean(lop,rop,op));
              exp = exp.substring(0,looffset)+getSlot(ret,_slots) +
                    exp.substring(rooffset);
        }

        return exp;
    }
    private boolean executeBoolean(Object lo,Object ro,String operator) {
        if(lo==null || ro == null) {
            if(operator.equals("=")) {
                return lo == ro;
            } else if(operator.equals("!=")) {
                return lo != ro;
            }
        }
        if(lo instanceof java.lang.String) {
            if(operator.equals("=")) {
                return ((String)lo).equals(ro);
            } else if(operator.equals("!=")) {
                return !((String)lo).equals(ro);
            }
        } else if (lo instanceof Float) {
            if(ro instanceof Float) {
                if(operator.equals("=")) {
                    return ((Float)lo).floatValue() == ((Float)ro).floatValue();
                } else if (operator.equals("!=")) {
                    return !(((Float)lo).floatValue() == ((Float)ro).floatValue());
                } else if (operator.equals(">")) {
                    return (((Float)lo).floatValue() > ((Float)ro).floatValue());
                } else if (operator.equals("<")) {
                    return ((Float)lo).floatValue() < ((Float)ro).floatValue();
                }
            } else {
                throw new RuntimeException(MessageLocalizer.getMessage("TYPE_MISMATCH")+" "+lo.getClass().getName()+","+lo.getClass().getName());
            }
        } else if (lo instanceof Boolean && ro instanceof Boolean) {
            if(operator.equals("&&")) {
                return ((Boolean)lo).booleanValue() && ((Boolean)ro).booleanValue();
            } else if (operator.equals("||")) {
                return ((Boolean)lo).booleanValue() || ((Boolean)ro).booleanValue();
            }
        }
        throw new RuntimeException(MessageLocalizer.getMessage("TYPE_MISMATCH")+" "+lo.getClass().getName()+","+ro.getClass().getName());
    }
    public String getSlot(Object val,Object[] _slots) {
        Integer ms = (Integer)_slots[255];
        int idx = ms.intValue();
        _slots[idx] = val;
        _slots[255] = new Integer(idx+1);
        return "_slot_"+idx;
    }
    public Object parseValues(String value,Object[] _slots) {
        if(value.indexOf("_slot_")!=-1) {
            String nslot = value.substring(value.indexOf("_slot_")+6);
            return _slots[Integer.parseInt(nslot)];
        } else if (value.indexOf("\"")!= -1)  {
            return value.substring(value.indexOf("\"")+1,value.lastIndexOf("\""));
        } else if (value.indexOf("'")!= -1)  {
            return value.substring(value.indexOf("'")+1,value.lastIndexOf("'"));
        } else {
            try {
                return new Float(Float.parseFloat(value));
            } catch (NumberFormatException e) {}
            //return "TESTE";

            try {
                ResultSet rslt = p_rslt;
                /* comentado JMF 
                if( rslt instanceof DataNode &&
                    value.indexOf(".")>-1 &&
                    ((DataNode)rslt).getOwnerDocument()!=null )
                {
                    String refnode = value.substring(0,value.indexOf("."));
                    rslt = ((DataNode)rslt).getRecordChild(refnode);
                    value = value.substring(value.indexOf(".")+1);
                }
                */
                ResultSetMetaData rm=rslt.getMetaData();
                int col=-1;
                for(int z=1;z<=rm.getColumnCount();z++) {
                    if(rm.getColumnName(z).equalsIgnoreCase(value)) {
                        col = z;
                        break;
                    }
                }
                if(col!=-1) {
                    String tn = rm.getColumnTypeName(col);
                    if(tn.indexOf("CHAR")!=-1) {
                        return rslt.getString(col);
                    } else {
                        return new Float(p_rslt.getFloat(col));
                    }
                }
                throw new RuntimeException(MessageLocalizer.getMessage("EXPRESSIONPARSER_ERROR")+":"+MessageLocalizer.getMessage("VARIABLE_EXPECTED")+" ["+value+"]");
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
    public String parseWords(String exp) {
        int idx = 0;
        StringBuffer result= new StringBuffer();
        String lstoplist = stoplist;
        StringBuffer word=new StringBuffer();
        while(idx<exp.length()) {
            if(stoplist.indexOf(exp.charAt(idx))!=-1 || (idx<exp.length()-1 && stoplist.indexOf(exp.charAt(idx+1))!=-1 )) {
                boolean wsp = true;
                if(exp.charAt(idx)!=' ') {
                    word.append(exp.charAt(idx));
                    wsp = false;
                }
                if(exp.charAt(idx)=='<' && exp.charAt(idx+1)=='>') {
                    word.append(exp.charAt(idx+1));
                    idx++;
                }
                word = new StringBuffer(word.toString().trim());
                String sword = word.toString();
                if(!sword.startsWith("\"") && !sword.startsWith("'")) {
                    sword = parseWordOperators(sword);
                }
                result.append(sword).append(wsp?" ":"");
                word.delete(0,word.length());
                lstoplist = stoplist;
            } else {
                if("\"".toCharArray()[0]==exp.charAt(idx))
                    lstoplist = "\",\n";

                word.append(exp.charAt(idx));
            }
            idx++;
        }
        result.append(word);
        return result.toString();
    }
    public String parseWordOperators(String word) {
        if(word.equalsIgnoreCase("OR"))
            return "||";
        else if (word.equalsIgnoreCase("AND"))
            return "&&";
        else if (word.equalsIgnoreCase("<>"))
            return "!=";
        return word;
    }
}