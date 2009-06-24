/*Enconding=UTF-8*/
package netgest.bo.parser;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefMethod;

import netgest.bo.parser.symbol.AssignExpression;
import netgest.bo.parser.symbol.BinaryExpression;
import netgest.bo.parser.symbol.BlockStatement;
import netgest.bo.parser.symbol.BooleanLiteral;
import netgest.bo.parser.symbol.CharLiteral;
import netgest.bo.parser.symbol.ConstructorExpression;
import netgest.bo.parser.symbol.EList;
import netgest.bo.parser.symbol.ExpressionStatement;
import netgest.bo.parser.symbol.ExpressionSymbol;
import netgest.bo.parser.symbol.IfStatement;
import netgest.bo.parser.symbol.JavaTokenTypes;
import netgest.bo.parser.symbol.MethodInvocation;
import netgest.bo.parser.symbol.NumericLiteral;
import netgest.bo.parser.symbol.ReturnStatement;
import netgest.bo.parser.symbol.StatementSymbol;
import netgest.bo.parser.symbol.StringLiteral;
import netgest.bo.parser.symbol.Symbol;
import netgest.bo.parser.symbol.TypeClause;
import netgest.bo.parser.symbol.UnaryExpression;
import netgest.bo.parser.symbol.VarDeclarationStatement;
import netgest.bo.parser.symbol.VarLiteral;
import netgest.bo.parser.symbol.WhileStatement;

import netgest.bo.runtime.boRuntimeException;

import netgest.bo.utils.Calculate;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.log4j.Logger;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class XEORecognizer
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.parser.XEORecognizer");

    private static String[] reservedWords = new String[]
        {
            "if", "null", "else", "return", "N", "NO", "n", "no", "Y", "YES",
            "y", "yes", "addErrorMessage", "while"
        };
    private String commands = null;
    private int pos = 0;
    private boDefAttribute[] attributes;
    private boDefAttribute thisAttribute;
    private boDefHandler bodef;
    private ArrayList parentTypes;
    private String className;
    private String type;
    private ArrayList refreshAttrib;
    private boolean formula = false;
    private boolean defaultValue = false;
    private int height = 0;
    private ArrayList attributesHeight;
    private boolean bridge = false;
    private String bridgeName;
    private boDefAttribute attBridgeBodef = null;

    //resume casos do SUM, SUBTRACT
    private boolean resume = false;
    private boolean method = false;
    private String resumeBridge;
    private Hashtable resumeTable = new Hashtable();
    private Hashtable formatts;

    /**
     *
     * @since
     */
    public XEORecognizer(String xeoCommands, boDefHandler bodef,
        boDefAttribute thisAttribute, boDefAttribute[] attributes,
        ArrayList refreshAttrib, String type, boolean bridge,
        String bridgAttName, Hashtable formatts) throws boRuntimeException
    {
        String g = bodef.getBoName();
        String ga = bodef.getName();
        this.commands = xeoCommands.trim();
        this.attributes = attributes;
        this.thisAttribute = thisAttribute;
        this.bodef = bodef;
        className = bodef.getName();
        this.refreshAttrib = refreshAttrib;
        this.type = type;

        if ("formula".equals(type))
        {
            this.formula = true;
            attributesHeight = new ArrayList();
            height = 0;
        }
        else if ("defaultValue".equals(type))
        {
            this.defaultValue = true;
            attributesHeight = new ArrayList();
        }

        this.bridge = bridge;
        this.bridgeName = bridgAttName;

        if (bridge)
        {
            setAttBridgeBodef(bridgeName);
        }

        //formatos
        this.formatts = formatts;
    }

    public Hashtable getResumeCode()
    {
        return resumeTable;
    }

    private void setAttBridgeBodef(String bridgeName)
    {
        boDefAttribute[] atts = bodef.getBoAttributes();

        for (int i = 0; i < atts.length; i++)
        {
            if (bridgeName.equals(atts[i].getName()))
            {
                attBridgeBodef = atts[i];
            }
        }
    }

    public int getHeight()
    {
        return height;
    }

    public Symbol getStatement() throws boRuntimeException
    {
        int startSave = pos;
        Object token = getNextToken();

        if (!(token instanceof String))
        {
            return noReservedWord(token, startSave);
        }
        else
        {
            if (((String) token).equals("if"))
            {
                IfStatement ifexp = new IfStatement();
                commandGoto('(');
                ifexp.setCondition((ExpressionSymbol) getExpression());
                ifexp.setThenStatement((StatementSymbol) getStatement());

                int commandPos = pos;
                token = getNextToken();

                if (token != null && ((String) token).equals("else"))
                {
                    pos++;
                    ifexp.setElseStatement((StatementSymbol) getStatement());
                }
                else
                {
                    pos = commandPos;
                    ifexp.setElseStatement(null);
                }

                commandPos = pos;

                Object token2 = getNextToken();

                if (!(token2 instanceof String) ||
                        !((String) token2).equals("}"))
                {
                    pos = commandPos;
                }

                return ifexp;
            }

            if (((String) token).equals("while"))
            {
                WhileStatement whileStat = new WhileStatement();
                commandGoto('(');
                whileStat.setCondition((ExpressionSymbol) getExpression());
                whileStat.setBody((StatementSymbol) getStatement());

                int commandPos = pos;

                Object token2 = getNextToken();

                if (!(token2 instanceof String) ||
                        !((String) token2).equals("}"))
                {
                    pos = commandPos;
                }

                return whileStat;
            }
            else if (((String) token).equals("{"))
            {
                BlockStatement block = new BlockStatement();
                ArrayList statsList = new ArrayList();
                boolean end = false;
                int commandPos;
                Symbol st;

                while (!end)
                {
                    commandPos = pos;
                    st = getStatement();

                    if (st != null)
                    {
                        statsList.add(st);

                        if (commands.charAt(pos - 1) == '}')
                        {
                            end = true;
                            if(commands.length() > pos)
                            {
                                pos++;
                            }
                        }
                        else if (commands.charAt(pos - 1) == ';')
                        {
                            commandPos = pos;

                            Object token2 = getNextToken();

                            if (!(token2 instanceof String) ||
                                    !((String) token2).equals("}"))
                            {
                                pos = commandPos;
                            }
                            else
                            {
                                end = true;
//                                if(commands.length() > pos)
//                                {
//                                    pos++;
//                                }
                            }

                            //pos ++;
                        }
                    }
                    else
                    {
                        end = true;
                    }
                }

                block.setStatements(statsList);

                return block;
            }
            else if ("return".equalsIgnoreCase((String) token))
            {
                ReturnStatement r = new ReturnStatement();
                int commandPos = pos;
                token = getNextToken();

                if (token instanceof String &&
                        ("{".equalsIgnoreCase((String) token) ||
                        "null".equalsIgnoreCase((String) token)))
                {
                    if ("null".equalsIgnoreCase((String) token))
                    {
                        r.setReturnExp(new VarLiteral("null"));

                        //por ser um caso especial temos comer o prox token
                        token = getNextToken();
                    }
                    else
                    {
                        //vou retornar algo do género String[]{"a", "b", "c"}
                        //pos++;
                        ConstructorExpression ce = new ConstructorExpression();
                        ce.setExpression(new VarLiteral("String"));
                        ce.setArrayInit((ExpressionSymbol) getExpression());
                        r.setReturnExp(ce);

                        //por ser um caso especial temos comer o prox token
                        token = getNextToken();
                    }
                }
                else
                {
                    pos = commandPos;

                    if ("defaultValue".equals(type))
                    {
                        ExpressionSymbol exp = (ExpressionSymbol) getExpression();
                        r.setReturnExp(convertToString(exp));
                    }
                    else if ("formula".equals(type))
                    {
                        Symbol s = getExpression();

                        if (s instanceof ExpressionSymbol)
                        {
                            if (s instanceof NumericLiteral ||
                                    s instanceof MethodInvocation)
                            {
                                r.setReturnExp(convertToString(
                                        (ExpressionSymbol) s));
                            }
                            else if (s instanceof VarLiteral &&
                                    ((VarLiteral) s).getValue().startsWith("resume_ret"))
                            {
                                BinaryExpression bString = new BinaryExpression();
                                bString.setLeftExpression((VarLiteral) s);
                                bString.setOperator(JavaTokenTypes.DOT);
                                bString.setRightExpression(new VarLiteral(
                                        "toString"));

                                MethodInvocation mi = new MethodInvocation();
                                mi.setInvocation(bString);
                                mi.setReturnType("String");
                                r.setReturnExp(mi);

                                return r;
                            }
                            else
                            {
                                r.setReturnExp((ExpressionSymbol) s);
                            }
                        }
                        else
                        {
                            return s;
                        }
                    }
                    else
                    {
                        r.setReturnExp((ExpressionSymbol) getExpression());
                    }
                }

                return r;
            }

            else if ((((String) token).equals("y") ||
                    ((String) token).equals("yes") ||
                    ((String) token).equals("Y") ||
                    ((String) token).equals("N") ||
                    ((String) token).equals("YES") ||
                    ((String) token).equals("n") ||
                    ((String) token).equals("no") ||
                    ((String) token).equals("NO")))
            {
                ReturnStatement r = new ReturnStatement();
                BooleanLiteral bl = new BooleanLiteral();
                bl.setValue(((String) token).toLowerCase().startsWith("y"));
                r.setReturnExp(bl);
                pos++;

                return r;
            }
            else if ("addErrorMessage".equals((String) token))
            {
                BinaryExpression be = new BinaryExpression();
                be.setLeftExpression(new VarLiteral("this"));
                be.setOperator(JavaTokenTypes.DOT);
                be.setRightExpression(new VarLiteral("getParent"));

                MethodInvocation mparent = new MethodInvocation();
                mparent.setReturnType("object");
                mparent.setInvocation(be);

                BinaryExpression b = new BinaryExpression();
                b.setLeftExpression(mparent);
                b.setOperator(JavaTokenTypes.DOT);
                b.setRightExpression(new VarLiteral("addErrorMessage"));

                MethodInvocation mi = new MethodInvocation();
                mi.setInvocation(b);
                commandGoto('(');
                token = getNextToken();

                if (!(token instanceof StringLiteral))
                {
                    throw new boRuntimeException("XEORecognizer",
                        "Erro ao verificar o attributo: " +
                        thisAttribute.getName() + " do objecto: " +
                        bodef.getBoName(), new Exception());
                }

                EList ps = new EList();
                ArrayList p = new ArrayList();
                p.add(new VarLiteral("this"));
                p.add(token);
                ps.setList(p);
                mi.setParameters(ps);
                token = getNextToken();
                pos++;

                return new ExpressionStatement(mi);
            }
            else
            {
                return noReservedWord(token, startSave);
            }

            /*
                        else if ("formula".equals(type))
                        {
                            return getExpression();
                        }
                        else
                        {
                            throw new boRuntimeException("XEORecognizer",
                                "Erro ao verificar o attributo: " +
                                thisAttribute.getName() + " do objecto: " +
                                bodef.getBoName(), new Exception());
                        }
            */
        }
    }

    private Symbol noReservedWord(Object token, int start)
        throws boRuntimeException
    {
        ReturnStatement r = new ReturnStatement();
        int commandPos = (start == 0) ? (start + 1) : start;

        if (token instanceof String &&
                ("{".equalsIgnoreCase((String) token) ||
                "null".equalsIgnoreCase((String) token)))
        {
            if ("null".equalsIgnoreCase((String) token))
            {
                r.setReturnExp(new VarLiteral("null"));

                //por ser um caso especial temos comer o prox token
                token = getNextToken();
            }
            else
            {
                //vou retornar algo do género String[]{"a", "b", "c"}
                //pos++;
                ConstructorExpression ce = new ConstructorExpression();
                ce.setExpression(new VarLiteral("String"));
                ce.setArrayInit((ExpressionSymbol) getExpression());
                r.setReturnExp(ce);

                //por ser um caso especial temos comer o prox token
                token = getNextToken();
            }
        }
        else
        {
            pos = commandPos;

            if ("defaultValue".equals(type))
            {
                ExpressionSymbol exp = (ExpressionSymbol) getExpression();
                r.setReturnExp(convertToString(exp));
            }
            else if ("formula".equals(type))
            {
                Symbol s = getExpression();

                if (s instanceof ExpressionSymbol)
                {
                    if (s instanceof NumericLiteral ||
                            s instanceof MethodInvocation)
                    {
                        r.setReturnExp(convertToString((ExpressionSymbol) s));
                    }
                    else if (s instanceof VarLiteral &&
                            ((VarLiteral) s).getValue().startsWith("resume_ret"))
                    {
                        BinaryExpression bString = new BinaryExpression();
                        bString.setLeftExpression((VarLiteral) s);
                        bString.setOperator(JavaTokenTypes.DOT);
                        bString.setRightExpression(new VarLiteral("toString"));

                        MethodInvocation mi = new MethodInvocation();
                        mi.setInvocation(bString);
                        mi.setReturnType("String");
                        r.setReturnExp(mi);

                        return r;
                    }
                    else
                    {
                        r.setReturnExp((ExpressionSymbol) s);
                    }
                }
                else
                {
                    return s;
                }
            }
            else
            {
                r.setReturnExp((ExpressionSymbol) getExpression());
            }
        }

        return r;
    }

    private ExpressionSymbol convertToString(ExpressionSymbol exp)
    {
        if (exp instanceof StringLiteral)
        {
            return exp;
        }
        else if (exp instanceof MethodInvocation)
        {
            if ("String".equals(((MethodInvocation) exp).getReturnType()))
            {
                return exp;
            }
            else
            {
                BinaryExpression be = new BinaryExpression();
                be.setLeftExpression(new VarLiteral("boConvertUtils"));
                be.setOperator(JavaTokenTypes.DOT);
                be.setRightExpression(new VarLiteral("convertToString"));

                MethodInvocation convert = new MethodInvocation();
                convert.setReturnType("String");
                convert.setInvocation(be);

                EList ps = new EList();
                ArrayList p = new ArrayList();
                p.add(exp);
                p.add(new VarLiteral("this"));
                ps.setList(p);
                convert.setParameters(ps);

                return convert;
            }
        }
        else
        {
            BinaryExpression be = new BinaryExpression();
            be.setLeftExpression(new VarLiteral("String"));
            be.setOperator(JavaTokenTypes.DOT);
            be.setRightExpression(new VarLiteral("valueOf"));

            MethodInvocation convert = new MethodInvocation();
            convert.setReturnType("String");
            convert.setInvocation(be);

            EList ps = new EList();
            ArrayList p = new ArrayList();
            p.add(exp);
            ps.setList(p);
            convert.setParameters(ps);

            return convert;
        }
    }

    private void commandGoto(char c)
    {
        for (; (pos < commands.length()) && (commands.charAt(pos) != c);
                pos++)
            ;

        pos++;
    }

    public Symbol getExpression() throws boRuntimeException
    {
        String aux = null;
        Object auxOb;
        ArrayList statements = new ArrayList();
        boolean endExp = false;
        boolean resumeV = false;
        parentTypes = new ArrayList();

        while (!endExp && ((auxOb = getNextToken()) != null))
        {
            if (!((auxOb instanceof String) &&
                    (auxOb.equals(";") || auxOb.equals(")") ||
                    auxOb.equals("}"))))
            {
                if (auxOb.equals("("))
                {
                    //pos++;
                    int commandPos = pos;
                    Object token = getNextToken();

                    if (token instanceof String && ")".equals(token))
                    {
                        //method Void
                        auxOb = token;
                        statements.add("void");
                    }
                    else
                    {
                        pos = commandPos;
                        statements.add(getExpression());

                        if (resumeV)
                        {
                            ArrayList n = new ArrayList();
                            int nPos = statements.size();
                            n.add(statements.get(nPos - 2));
                            n.add(statements.get(nPos - 1));

                            StatementSymbol resumeCode = (StatementSymbol) resumeFields(n);
                            String varName = getResumeVarName();
                            resumeTable.put(varName, resumeCode);
                            statements.remove(nPos - 1);
                            statements.set(nPos - 2, new VarLiteral(varName));
                        }
                    }
                }
                else
                {
                    statements.add(auxOb);
                }

                if ((auxOb instanceof VarLiteral) &&
                        ("SUM".equals(((VarLiteral) auxOb).getValue()) ||
                        "SUBTRACT".equals(((VarLiteral) auxOb).getValue())))
                {
                    resumeV = true;
                }
            }
            else
            {
                endExp = true;
            }
        }

        //pode ser: IFExpression, AssignExpression, BinaryExpression, MethodInvocation
        //          UnaryExpression
        if ((statements.indexOf("&&") != -1) ||
                (statements.indexOf("||") != -1))
        {
            reviewVarLiteral_e_ou(statements);

            ArrayList left = new ArrayList();
            ArrayList right = new ArrayList();
            boolean constrLeft = true;
            boolean constrRight = false;
            String last = null;
            BinaryExpression be = null;
            BinaryExpression bLast = null;

            for (int i = 0; i < statements.size(); i++)
            {
                if (statements.get(i) instanceof String)
                {
                    if (((String) statements.get(i)).equals("&&") ||
                            ((String) statements.get(i)).equals("||"))
                    {
                        //last = (String) statements.get(i);
                        if (constrLeft)
                        {
                            constrLeft = false;
                            constrRight = true;

                            if (be == null)
                            {
                                be = new BinaryExpression();
                                be.setLeftExpression(expressionConstructor(left));

                                if (((String) statements.get(i)).equals("&&"))
                                {
                                    be.setOperator(JavaTokenTypes.LAND);
                                }
                                else
                                {
                                    be.setOperator(JavaTokenTypes.LOR);
                                }
                            }
                        }
                        else if (constrRight)
                        {
                            if ((be == null) && (bLast == null))
                            {
                                throw new boRuntimeException("XEORecognizer",
                                    "Erro ao verificar o attributo: " +
                                    thisAttribute.getName() + " do objecto: " +
                                    bodef.getBoName(), new Exception());
                            }
                            else
                            {
                                if (be == null)
                                {
                                    be = new BinaryExpression();
                                    be.setLeftExpression(bLast);

                                    if (last.equals("&&"))
                                    {
                                        be.setOperator(JavaTokenTypes.LAND);
                                    }
                                    else
                                    {
                                        be.setOperator(JavaTokenTypes.LOR);
                                    }

                                    last = (String) statements.get(i);
                                }
                                else
                                {
                                    last = (String) statements.get(i);
                                }
                            }

                            be.setRightExpression(expressionConstructor(right));
                            bLast = be;
                            be = null;
                            right.clear();
                        }
                    }
                    else
                    {
                        if (constrLeft)
                        {
                            left.add(statements.get(i));
                        }
                        else
                        {
                            right.add(statements.get(i));
                        }
                    }
                }
                else
                {
                    if (constrLeft)
                    {
                        left.add(statements.get(i));
                    }
                    else
                    {
                        right.add(statements.get(i));
                    }
                }

                if ((i + 1) == statements.size())
                {
                    if ((be == null) && (bLast == null))
                    {
                        throw new boRuntimeException("XEORecognizer",
                            "Erro ao verificar o attributo: " +
                            thisAttribute.getName() + " do objecto: " +
                            bodef.getBoName(), new Exception());
                    }
                    else if (bLast != null)
                    {
                        be = new BinaryExpression();
                        be.setLeftExpression(bLast);

                        if (last.equals("&&"))
                        {
                            be.setOperator(JavaTokenTypes.LAND);
                        }
                        else
                        {
                            be.setOperator(JavaTokenTypes.LOR);
                        }
                    }

                    be.setRightExpression(expressionConstructor(right));
                }
            }

            return be;
        }
        else
        {
            reviewVarLiteral(statements);

            return expressionConstructor(statements);
        }
    }

    private String getResumeVarName()
    {
        return "resume" + "_" + "ret" + (resumeTable.size() + 1);
    }

    private Symbol resumeFields(ArrayList statements) throws boRuntimeException
    {
        resume = true;
        reviewVarLiteral(statements);
        resume = false;

        return (StatementSymbol) statements.get(0);
    }

    private String isDateCmp(String aux)
    {
        if (aux.startsWith("==") || aux.startsWith("===") ||
                aux.startsWith(">") || aux.startsWith(">=") ||
                aux.startsWith("<") || aux.startsWith("<==") ||
                aux.startsWith(">==") || aux.startsWith("!=") ||
                aux.startsWith("!==") || aux.startsWith(">>") ||
                aux.startsWith("<<") || aux.startsWith("<="))
        {
            String toRet = new String();
            int i = aux.length() - 1;
            boolean end = false;

            while ((i > 0) && !end)
            {
                if (aux.charAt(i) == 'y')
                {
                    toRet = "y" + toRet;
                }
                else if (aux.charAt(i) == 'M')
                {
                    toRet = "M" + toRet;
                }
                else if (aux.charAt(i) == 'd')
                {
                    toRet = "d" + toRet;
                }
                else if (aux.charAt(i) == 'h')
                {
                    toRet = "h" + toRet;
                }
                else if (aux.charAt(i) == 'm')
                {
                    toRet = "m" + toRet;
                }
                else if (aux.charAt(i) == 's')
                {
                    toRet = "s" + toRet;
                }
                else
                {
                    end = true;
                }

                i--;
            }

            return (toRet.length() == 0) ? null : toRet;
        }

        return null;
    }

    private ExpressionSymbol expressionConstructor(ArrayList statements)
        throws boRuntimeException
    {
        ArrayList elist = null;
        boolean bElist = false;
        boolean unary = false;
        boolean aritm = false;
        String aux;
        boolean built = false;

        while (statements.size() > 1)
        {
            for (int i = 0; i < statements.size(); i++)
            {
                if (statements.get(i) instanceof String)
                {
                    aux = (String) statements.get(i);

                    if (i == 0)
                    {
                        //it's unary
                        if ("++".equals(aux))
                        {
                            UnaryExpression un = new UnaryExpression();
                            i = contructUnary(statements,
                                    JavaTokenTypes.UNARY_PLUS, i, un);
                            unary = true;
                        }

                        if ("--".equals(aux))
                        {
                            UnaryExpression un = new UnaryExpression();
                            i = contructUnary(statements,
                                    JavaTokenTypes.UNARY_MINUS, i, un);
                            unary = true;
                        }
                        if("!".equals(aux))
                        {
                            UnaryExpression un = new UnaryExpression();
                            i = contructUnary(statements,
                                    JavaTokenTypes.LNOT, i, un);
                            unary = true;
                        }
                    }

                    if (!unary)
                    {
                        boolean[] bo = new boolean[] { built };

                        if (".".equals(aux))
                        {
                            i = contructBinary(statements, JavaTokenTypes.DOT,
                                    i, bo);
                        }
                        else if ("+".equals(aux))
                        {
                            aritm = true;
                            i = contructBinary(statements, JavaTokenTypes.PLUS,
                                    i, bo);
                            built = bo[0];
                        }
                        else if ("-".equals(aux))
                        {
                            aritm = true;
                            i = contructBinary(statements,
                                    JavaTokenTypes.MINUS, i, bo);
                            built = bo[0];
                        }
                        else if ("*".equals(aux))
                        {
                            aritm = true;
                            i = contructBinary(statements, JavaTokenTypes.STAR,
                                    i, bo);
                            built = bo[0];
                        }
                        else if ("/".equals(aux))
                        {
                            aritm = true;
                            i = contructBinary(statements, JavaTokenTypes.DIV,
                                    i, bo);
                            built = bo[0];
                        }
                        else if ("==".equals(aux) || "===".equals(aux) ||
                                "&&".equals(aux) || "||".equals(aux) ||
                                ">".equals(aux) || ">=".equals(aux) ||
                                "<".equals(aux) || "<==".equals(aux) ||
                                ">==".equals(aux) || "!=".equals(aux) ||
                                "!==".equals(aux) || ">>".equals(aux) ||
                                "<<".equals(aux) || "<=".equals(aux) ||
                                (isDateCmp(aux) != null))
                        {
                            ExpressionSymbol lst;
                            ExpressionSymbol rst;

                            if (statements.get(i - 1) instanceof String)
                            {
                                lst = new VarLiteral((String) statements.get(i -
                                            1));
                            }
                            else
                            {
                                lst = (ExpressionSymbol) statements.get(i - 1);
                            }

                            if (statements.get(i + 1) instanceof String)
                            {
                                rst = new VarLiteral((String) statements.get(i +
                                            1));
                            }
                            else
                            {
                                rst = (ExpressionSymbol) statements.get(i + 1);
                            }

                            MethodInvocation b = getCompareMethod(lst, rst,
                                    removeDtCmpStr(aux), isDateCmp(aux));

                            statements.remove(i - 1);
                            statements.remove(i - 1);
                            statements.remove(i - 1);

                            if (statements.size() > 0)
                            {
                                statements.add(i - 1, b);
                            }
                            else
                            {
                                statements.add(b);
                            }

                            i = i - 1;
                        }
                        else if ("=".equals(aux))
                        {
                            BinaryExpression b = new BinaryExpression();
                            b.setLeftExpression((ExpressionSymbol) statements.get(i -
                                    1));
                            b.setOperator(JavaTokenTypes.ASSIGN);
                            b.setRightExpression((ExpressionSymbol) statements.get(i +
                                    1));
                            statements.remove(i - 1);
                            statements.remove(i - 1);
                            statements.remove(i - 1);

                            if (statements.size() > 0)
                            {
                                statements.add(i - 1, b);
                            }
                            else
                            {
                                statements.add(b);
                            }

                            i = i - 1;
                        }
                        else if ("&&".equals(aux) || "||".equals(aux))
                        {
                            BinaryExpression b = new BinaryExpression();

                            if (statements.get(i - 1) instanceof String)
                            {
                                b.setLeftExpression(new VarLiteral(
                                        (String) statements.get(i - 1)));
                            }
                            else
                            {
                                b.setLeftExpression((ExpressionSymbol) statements.get(i -
                                        1));
                            }

                            b.setRightExpression((ExpressionSymbol) statements.get(i +
                                    1));
                            b.setOperator(getBinaryOperator(aux));
                            statements.remove(i - 1);
                            statements.remove(i - 1);
                            statements.remove(i - 1);

                            if (statements.size() > 0)
                            {
                                statements.add(i - 1, b);
                            }
                            else
                            {
                                statements.add(b);
                            }

                            i = i - 1;
                        }
                        else if (",".equals(aux))
                        {
                            bElist = true;

                            if (elist == null)
                            {
                                elist = new ArrayList();
                                elist.add(statements.get(i - 1));
                                statements.remove(i - 1);
                                statements.remove(i - 1);
                                i = i - 2;
                            }
                            else
                            {
                                elist.add(statements.get(i - 1));
                                statements.remove(i - 1);
                                statements.remove(i - 1);
                                i = i - 2;
                            }

                            if (statements.size() == 1)
                            {
                                elist.add(statements.get(0));

                                EList e = new EList();
                                e.setList(elist);
                                statements.set(0, e);
                            }
                        }
                    }
                }
            }
        }

        if (aritm)
        {
            return transformCalcMethods((ExpressionSymbol) statements.get(0));
        }
        else
        {
            return (ExpressionSymbol) statements.get(0);
        }
    }

    private int getBinaryOperator(String aux) throws boRuntimeException
    {
        if (aux.startsWith(">="))
        {
            return JavaTokenTypes.GE;
        }
        else if (aux.startsWith(">"))
        {
            return JavaTokenTypes.GT;
        }
        else if (aux.startsWith("<="))
        {
            return JavaTokenTypes.LE;
        }
        else if (aux.startsWith("<"))
        {
            return JavaTokenTypes.LT;
        }
        else if (aux.startsWith("!="))
        {
            return JavaTokenTypes.NOT_EQUAL;
        }

        throw new boRuntimeException("XEORecognizer",
            "Erro ao verificar o attributo: " + thisAttribute.getName() +
            " do objecto: " + bodef.getBoName(), new Exception());
    }

    private int constructEqualsIgnoreCase(ArrayList statements, int i,
        String aux, BinaryExpression b)
    {
        if ("==".equals(aux))
        {
            b.setRightExpression(new VarLiteral("equalsIgnoreCase"));
        }
        else
        {
            b.setRightExpression(new VarLiteral("equals"));
        }

        MethodInvocation mtInv = new MethodInvocation();
        mtInv.setInvocation(b);
        mtInv.setReturnType("boolean");
        mtInv.setParameters((ExpressionSymbol) statements.get(i + 1));
        statements.remove(i - 1);
        statements.remove(i - 1);
        statements.remove(i - 1);

        if (statements.size() > 0)
        {
            statements.add(i - 1, mtInv);
        }
        else
        {
            statements.add(mtInv);
        }

        return i - 1;
    }

    private int contructBinary(ArrayList statements, int op, int i,
        boolean[] built)
    {
        if ((op == JavaTokenTypes.STAR) || (op == JavaTokenTypes.DIV))
        {
            if ((built[0] == true) &&
                    statements.get(i - 1) instanceof BinaryExpression)
            {
                if ((((BinaryExpression) statements.get(i - 1)).getOperator() != JavaTokenTypes.STAR) &&
                        (((BinaryExpression) statements.get(i - 1)).getOperator() != JavaTokenTypes.DIV) &&
                        (((BinaryExpression) statements.get(i - 1)).getOperator() != JavaTokenTypes.DOT))
                {
                    BinaryExpression last = (BinaryExpression) statements.get(i -
                            1);
                    BinaryExpression b = new BinaryExpression();
                    b.setLeftExpression(last.getRightExpression());
                    b.setRightExpression((ExpressionSymbol) statements.get(i +
                            1));
                    b.setOperator(op);

                    if (statements.get(i + 1) instanceof BinaryExpression)
                    {
                        built[0] = false;
                    }
                    else
                    {
                        built[0] = true;
                    }

                    last.setRightExpression(b);
                    statements.remove(i - 1);
                    statements.remove(i - 1);
                    statements.remove(i - 1);

                    if (statements.size() > 0)
                    {
                        statements.add(i - 1, last);
                    }
                    else
                    {
                        statements.add(last);
                    }

                    return i - 1;
                }
            }
        }

        BinaryExpression b = new BinaryExpression();
        b.setLeftExpression((ExpressionSymbol) statements.get(i - 1));
        b.setRightExpression((ExpressionSymbol) statements.get(i + 1));
        b.setOperator(op);

        if (statements.get(i + 1) instanceof BinaryExpression)
        {
            built[0] = false;
        }
        else
        {
            built[0] = true;
        }

        statements.remove(i - 1);
        statements.remove(i - 1);
        statements.remove(i - 1);

        if (statements.size() > 0)
        {
            statements.add(i - 1, b);
        }
        else
        {
            statements.add(b);
        }

        return i - 1;
    }

    private int contructUnary(ArrayList statements, int op, int i,
        UnaryExpression u)
    {
        u.setOperator(op);
        if(statements.size() == i)
        {
            u.setExpression((ExpressionSymbol) statements.get(i - 1));
            statements.set(i-1, u);
            statements.remove(i);
            return i-1;
        }
        else
        {
            u.setExpression((ExpressionSymbol) statements.get(i + 1));
            statements.set(i, u);
            statements.remove(i + 1);
            return i;
        }
    }

    private Object getNextToken()
    {
        String ret = "";

        if (pos >= commands.length())
        {
            return null;
        }

        while ((pos < commands.length()) &&
                ((commands.charAt(pos) != ' ') || (ret.trim().length() == 0)) &&
                (commands.charAt(pos) != '+') && (commands.charAt(pos) != '-') &&
                (commands.charAt(pos) != '(') && (commands.charAt(pos) != ')') &&
                (commands.charAt(pos) != '\'') &&
                (commands.charAt(pos) != '\"') &&
                (commands.charAt(pos) != '{') && (commands.charAt(pos) != '=') &&
                (commands.charAt(pos) != '&') && (commands.charAt(pos) != '|') &&
                (commands.charAt(pos) != '>') && (commands.charAt(pos) != '<') &&
                (commands.charAt(pos) != '!') && (commands.charAt(pos) != '*') &&
                (commands.charAt(pos) != '/') && (commands.charAt(pos) != ';') &&
                (commands.charAt(pos) != '{') && (commands.charAt(pos) != '}') &&
                (commands.charAt(pos) != ',') && (commands.charAt(pos) != '.'))
        {
            ret += String.valueOf(commands.charAt(pos));
            pos++;
        }

        ret = ret.trim();

        if ("".equals(ret) && (pos < commands.length()))
        {
            //casos: "; '; =; ...
            ret += commands.charAt(pos);
            pos++;

            //casos especiais em q temos ++; --; ===; ==; >>; <<; &&; ||;
            //comparações de datas
            if ((ret.charAt(0) == '+') || (ret.charAt(0) == '-') ||
                    (ret.charAt(0) == '=') || (ret.charAt(0) == '<') ||
                    (ret.charAt(0) == '>') || (ret.charAt(0) == '&') ||
                    (ret.charAt(0) == '|'))
            {
                while ((pos < commands.length()) &&
                        (commands.charAt(pos) == ret.charAt(0)))
                {
                    ret += commands.charAt(pos);
                    pos++;
                }
            }

            //CharLiteral
            if (ret.equals("'"))
            {
                ret = "";

                while ((pos < commands.length()) &&
                        (commands.charAt(pos) != '\''))
                {
                    ret += commands.charAt(pos);
                    pos++;
                }

                pos++;

                CharLiteral c = new CharLiteral();
                c.setValue(ret.charAt(0));

                return c;
            }

            //StringLiteral
            if (ret.equals("\""))
            {
                ret = "";

                while ((pos < commands.length()) &&
                        (commands.charAt(pos) != '"'))
                {
                    ret += commands.charAt(pos);
                    pos++;
                }

                pos++;

                return new StringLiteral(ret);
            }

            //Casos: >=; <=; !=; >==, <==; !==
            //Casos: >>; <<; 
            if (ret.equals(">") || ret.equals("<") || ret.equals("!"))
            {
                int auxPos = pos;
                char auxC;
                auxC = commands.charAt(auxPos);

                while (auxC == ' ')
                {
                    auxC = commands.charAt(auxPos);
                    auxPos++;
                }

                boolean start = false;

                while (auxC == '=')
                {
                    ret += String.valueOf(auxC);

                    if (!start)
                    {
                        pos = auxPos;
                        start = true;
                    }

                    pos++;
                    auxC = commands.charAt(pos);
                }
            }

            //Casos: yMdhms   
            if (ret.equals(">") || ret.equals("<") || ret.equals("!=") ||
                    ret.equals("=") || ret.equals(">>") || ret.equals("<<") ||
                    ret.equals("==") || ret.equals("===") || ret.equals(">>=") ||
                    ret.equals("<<=") || ret.equals("==") || ret.equals(">=") ||
                    ret.equals("<=") || ret.equals("<==") || ret.equals(">==") ||
                    ret.equals("!=="))
            {
                int auxPos = pos;
                char auxC;
                auxC = commands.charAt(auxPos);

                boolean start = false;

                while ((auxC == 'y') || (auxC == 'M') || (auxC == 'd') ||
                        (auxC == 'h') || (auxC == 'm') || (auxC == 'n'))
                {
                    ret += String.valueOf(auxC);

                    if (!start)
                    {
                        pos = auxPos;
                        start = true;
                    }

                    pos++;
                    auxC = commands.charAt(pos);
                }
            }

            return ret;
        }
        else if ("parent".equals(ret))
        {
            if (commands.charAt(pos) != '.')
            {
                getNextToken(); //queimei o parênteses

                Object classtype = getNextToken(); //obtive a classe

                return new VarLiteral(ret +
                    ((VarLiteral) classtype).getValue());
            }
        }
        else if (isReserved(ret))
        {
            return ret;
        }
        else if ("y".equalsIgnoreCase(ret) || "yes".equalsIgnoreCase(ret) ||
                "true".equalsIgnoreCase(ret))
        {
            BooleanLiteral bool = new BooleanLiteral();
            bool.setValue(true);

            return bool;
        }
        else if ("n".equalsIgnoreCase(ret) || "no".equalsIgnoreCase(ret) ||
                "false".equalsIgnoreCase(ret))
        {
            BooleanLiteral bool = new BooleanLiteral();
            bool.setValue(false);

            return bool;
        }
        else
        {
            try
            {
                BigDecimal bg = new BigDecimal(ret);
                NumericLiteral n = new NumericLiteral();
                n.setValue(ret);

                return n;
            }
            catch (NumberFormatException e)
            {
                //ignora
            }
        }

        return new VarLiteral(ret);
    }

    private void reviewVarLiteral_e_ou(ArrayList statements)
        throws boRuntimeException
    {
        ArrayList r = new ArrayList();
        int start = 0;
        int end = 0;

        for (int i = 0; i < statements.size(); i++)
        {
            if (statements.get(i) instanceof String)
            {
                if (((String) statements.get(i)).equals("&&") ||
                        ((String) statements.get(i)).equals("||"))
                {
                    end = i - 1;
                    reviewVarLiteral(r);

                    for (int j = 0; j < r.size(); j++)
                    {
                        statements.set(start, r.get(j));
                        start++;
                    }

                    int h = start;

                    while (h <= end)
                    {
                        statements.remove(start);
                        h++;
                        i--;
                    }

                    start = i + 1;
                    r.clear();
                }
                else
                {
                    r.add(statements.get(i));
                }
            }
            else
            {
                r.add(statements.get(i));
            }

            if ((i + 1) == statements.size())
            {
                end = i;
                reviewVarLiteral(r);

                for (int j = 0; j < r.size(); j++)
                {
                    statements.set(start, r.get(j));
                    start++;
                }

                int h = start;

                while (h <= end)
                {
                    statements.remove(start);
                    h++;
                    i--;
                }
            }
        }
    }

    private void reviewVarLiteral(ArrayList statements)
        throws boRuntimeException
    {
        VarLiteral v = null;
        ArrayList st = new ArrayList();
        boolean found = false;
        int start = -1;
        int end = -1;
        String auxS;

        for (int i = 0; i < statements.size(); i++)
        {
            if (statements.get(i) instanceof VarLiteral)
            {
                if ((i + 1) < statements.size())
                {
                    if (!found && statements.get(i + 1) instanceof String &&
                            ".".equals((String) statements.get(i + 1)))
                    {
                        found = true;
                        start = i;
                        st.add(statements.get(i));
                    }
                    else if (found && statements.get(i + 1) instanceof String &&
                            ".".equals((String) statements.get(i + 1)))
                    {
                        st.add(statements.get(i));
                    }

                    if (found &&
                            (!(statements.get(i + 1) instanceof String) ||
                            !".".equals((String) statements.get(i + 1))))
                    {
                        end = i;
                        st.add(statements.get(i));
                    }
                }
                else
                {
                    if (found)
                    {
                        end = i;
                        st.add(statements.get(i));
                    }
                }

                if (!found)
                {
                    boolean two = false;
                    st.add(statements.get(i));
                    auxS = ((VarLiteral) statements.get(i)).getValue();

                    if ((auxS.indexOf("TO_DATE") != -1) ||
                            (auxS.indexOf("CONCAT") != -1) ||
                            (auxS.indexOf("ROUND") != -1) ||
                            (auxS.indexOf("SUM") != -1) ||
                            (auxS.equals("DIFF_IN_UTIL_DAYS")) ||
                            (auxS.equals("DIFF_IN_DAYS")) ||
                            (auxS.indexOf("SUBTRACT") != -1) ||
                            (auxS.indexOf("IS_NULL") != -1) ||
                            (auxS.indexOf("UPPER") != -1) ||
                            (auxS.indexOf("LOWER") != -1))
                    {
                        if (((i + 1) < statements.size()) &&
                                !(statements.get(i + 1) instanceof VarLiteral))
                        {
                            st.add(statements.get(i + 1));
                            two = true;
                        }
                    }

                    Symbol e;
                    method = false;

                    if (two)
                    {
                        e = toExpression(st, 0, new ArrayList(),
                                statements.size() == 2);
                    }
                    else
                    {
                        e = toExpression(st, 0, new ArrayList(),
                                statements.size() == 1);
                    }

                    if (method)
                    {
                        MethodInvocation mi = (MethodInvocation) e;

                        if ((i + 1) < statements.size())
                        {
                            if (statements.get(i + 1) instanceof EList)
                            {
                                mi.setParameters((EList) statements.get(i + 1));
                            }
                            else if (!(statements.get(i + 1) instanceof String &&
                                    "void".equals(
                                        (String) statements.get(i + 1))))
                            {
                                ArrayList param = new ArrayList();
                                param.add(statements.get(i + 1));

                                EList el = new EList();
                                el.setList(param);
                                mi.setParameters(el);
                            }

                            int till = statements.size();

                            if ((i + 2) < statements.size())
                            {
                                //append next words until found space or operator
                                ArrayList nextW = new ArrayList();
                                till = methodAppTill(i + 2, statements);

                                for (int j = i + 2; j < till; j++)
                                {
                                    nextW.add(statements.get(j));
                                }

                                mi.setMethodInst(nextW);
                            }
                            else
                            {
                                mi.setMethodInst(null);
                            }

                            for (int j = i; j < till; j++)
                            {
                                statements.remove(i);
                            }

                            statements.add(i, mi);
                        }
                        else
                        {
                            throw new boRuntimeException("XEORecognizer",
                                "Chamada a um método sem passar parâmetros: " +
                                commands, new Exception());
                        }
                    }
                    else if (e != null)
                    {
                        statements.set(i, e);

                        if (two)
                        {
                            statements.remove(i + 1);
                        }
                    }

                    st.clear();
                }
                else if ((start != -1) && (end != -1))
                {
                    ExpressionSymbol e = (ExpressionSymbol) toExpression(st, 0,
                            new ArrayList(), statements.size() == 1);

                    if (e != null)
                    {
                        if (method)
                        {
                            MethodInvocation mi = (MethodInvocation) e;

                            if ((i + 1) < statements.size())
                            {
                                if (statements.get(i + 1) instanceof EList)
                                {
                                    mi.setParameters((EList) statements.get(i +
                                            1));
                                }
                                else if (!(statements.get(i + 1) instanceof String &&
                                        "void".equals(
                                            (String) statements.get(i + 1))))
                                {
                                    ArrayList param = new ArrayList();
                                    param.add(statements.get(i + 1));

                                    EList el = new EList();
                                    el.setList(param);
                                    mi.setParameters(el);
                                }

                                int till = statements.size();

                                if ((i + 2) < statements.size())
                                {
                                    //append next words until found space or operator
                                    ArrayList nextW = new ArrayList();
                                    till = methodAppTill(i + 2, statements);

                                    for (int j = i + 2; j < till; j++)
                                    {
                                        nextW.add(statements.get(j));
                                    }

                                    mi.setMethodInst(nextW);
                                }
                                else
                                {
                                    mi.setMethodInst(null);
                                }

                                for (int j = start; j < till; j++)
                                {
                                    statements.remove(start);
                                }

                                statements.add(start, mi);
                                i = start + 1;
                            }
                            else
                            {
                                throw new boRuntimeException("XEORecognizer",
                                    "Chamada a um método sem passar parâmetros: " +
                                    commands, new Exception());
                            }
                        }
                        else
                        {
                            for (int j = start; j <= end; j++)
                            {
                                statements.remove(start);
                            }

                            statements.add(start, e);
                            i--;
                            start = -1;
                            end = -1;
                        }
                    }

                    found = false;
                    st.clear();
                }
            }
        }
    }

    private Symbol toExpression(ArrayList statement, int pos, ArrayList path,
        boolean alone) throws boRuntimeException
    {
        VarLiteral v = null;
        BinaryExpression b = new BinaryExpression();

        if (pos >= statement.size())
        {
            throw new boRuntimeException("XEORecognizer",
                "Atributo ou objecto inexistente: " + commands, new Exception());
        }

        //        if(pos == 0)
        //        {
        if (statement.get(pos) instanceof VarLiteral)
        {
            v = (VarLiteral) statement.get(pos);

            if ((pos == 0) && isThisAttribute(v.getValue()))
            {
                add(v.getValue());

                if (statement.size() == 1)
                {
                    b.setLeftExpression(new VarLiteral("this"));
                    b.setOperator(JavaTokenTypes.DOT);

                    String mthType = getMethodType(thisAttribute.getType(),
                            thisAttribute.getDecimals());
                    b.setRightExpression(new VarLiteral(mthType));

                    MethodInvocation mi = new MethodInvocation();
                    mi.setReturnType(getTypeofMethodString(mthType));
                    mi.setInvocation(b);

                    return mi;
                }
                else
                {
                    path.add(v.getValue());
                    statement.set(0, new VarLiteral("this"));
                }
            }
            else if (bridge && isAttributeFromBridge(v.getValue()))
            {
                if (formula || defaultValue)
                {
                    if (!attributesHeight.contains(v.getValue()))
                    {
                        height += getHeight(v.getValue());
                        attributesHeight.add(v.getValue());
                    }
                }

                add(v.getValue());
                path.add(v.getValue());

                if (pos == 0)
                {
                    //p_bridge.getAttribute("assd")
                    b.setLeftExpression(new VarLiteral("p_bridge"));
                    b.setOperator(JavaTokenTypes.DOT);
                    b.setRightExpression(new VarLiteral("getAttribute"));

                    MethodInvocation getAttr = new MethodInvocation();
                    getAttr.setReturnType("object");
                    getAttr.setInvocation(b);

                    ArrayList r = new ArrayList();
                    r.add(new StringLiteral(v.getValue()));

                    EList el = new EList();
                    el.setList(r);
                    getAttr.setParameters(el);

                    if (statement.size() == 1)
                    {
                        path.add(v.getValue());

                        BinaryExpression bR = new BinaryExpression();
                        bR.setLeftExpression(getAttr);
                        bR.setOperator(JavaTokenTypes.DOT);

                        String hlp = getValueOfBridgeAtt(v.getValue());
                        bR.setRightExpression(new VarLiteral(hlp));

                        MethodInvocation miR = new MethodInvocation();
                        miR.setReturnType(getTypeofMethodString(hlp));
                        miR.setInvocation(bR);

                        return miR;
                    }
                    else
                    {
                        statement.set(0, getAttr);
                    }
                }
                else
                {
                    b.setLeftExpression((ExpressionSymbol) statement.get(pos -
                            1));
                    b.setOperator(JavaTokenTypes.DOT);
                    b.setRightExpression(new VarLiteral("getAttribute"));

                    MethodInvocation mi = new MethodInvocation();
                    mi.setReturnType("object");
                    mi.setInvocation(b);

                    EList e = new EList();
                    e.addExpr(new StringLiteral(v.getValue()));
                    mi.setParameters(e);
                    statement.set(0, mi);
                }

                if (pos != 0)
                {
                    statement.remove(pos);
                    pos--;
                }

                if (statement.size() == 1)
                {
                    BinaryExpression bR = new BinaryExpression();
                    bR.setLeftExpression((MethodInvocation) statement.get(0));
                    bR.setOperator(JavaTokenTypes.DOT);

                    String hlp = getValueByType(path, 0, bodef);
                    bR.setRightExpression(new VarLiteral(hlp));

                    MethodInvocation miR = new MethodInvocation();
                    miR.setReturnType(getTypeofMethodString(hlp));
                    miR.setInvocation(bR);

                    return miR;
                }
            }
            else if ((pos == 0) && isAttribute(v.getValue()))
            {
                if (formula || defaultValue)
                {
                    if (!attributesHeight.contains(v.getValue()))
                    {
                        height += getHeight(v.getValue());
                        attributesHeight.add(v.getValue());
                    }
                }

                add(v.getValue());
                path.add(v.getValue());

                if (pos == 0)
                {
                    BinaryExpression be = new BinaryExpression();
                    be.setLeftExpression(new VarLiteral("this"));
                    be.setOperator(JavaTokenTypes.DOT);
                    be.setRightExpression(new VarLiteral("getParent"));

                    MethodInvocation mparent = new MethodInvocation();
                    mparent.setReturnType("object");
                    mparent.setInvocation(be);
                    b.setLeftExpression(mparent);
                }
                else
                {
                    b.setLeftExpression((ExpressionSymbol) statement.get(pos -
                            1));
                }

                b.setOperator(JavaTokenTypes.DOT);
                b.setRightExpression(new VarLiteral("getAttribute"));

                MethodInvocation mi = new MethodInvocation();
                mi.setReturnType("object");
                mi.setInvocation(b);

                EList e = new EList();
                e.addExpr(new StringLiteral(v.getValue()));
                mi.setParameters(e);

                if ((statement.size() == 1) || ((pos + 1) == statement.size()))
                {
                    BinaryExpression bR = new BinaryExpression();
                    bR.setLeftExpression(mi);
                    bR.setOperator(JavaTokenTypes.DOT);

                    String hlp = getValueByType(path, 0, bodef);
                    bR.setRightExpression(new VarLiteral(hlp));

                    MethodInvocation miR = new MethodInvocation();
                    miR.setReturnType(getTypeofMethodString(hlp));
                    miR.setInvocation(bR);

                    return miR;
                }

                //                    else
                //                    {
                BinaryExpression bR = new BinaryExpression();
                bR.setLeftExpression(mi);
                bR.setOperator(JavaTokenTypes.DOT);
                bR.setRightExpression(new VarLiteral("getObject"));

                MethodInvocation getObj = new MethodInvocation();
                getObj.setReturnType("object");
                getObj.setInvocation(bR);
                statement.set(0, getObj);

                //                    }
                if (pos != 0)
                {
                    statement.remove(pos);
                    pos--;
                }
            }
            else if (v.getValue().indexOf("parent") != -1)
            {
                if (pos == 0)
                {
                    BinaryExpression be = new BinaryExpression();
                    be.setLeftExpression(new VarLiteral("this"));
                    be.setOperator(JavaTokenTypes.DOT);
                    be.setRightExpression(new VarLiteral("getParent"));

                    MethodInvocation mparent = new MethodInvocation();
                    mparent.setReturnType("object");
                    mparent.setInvocation(be);

                    b.setLeftExpression(mparent);
                    b.setOperator(JavaTokenTypes.DOT);
                    b.setRightExpression(new VarLiteral("getParent"));

                    MethodInvocation mthInv = new MethodInvocation();
                    mthInv.setReturnType("object");
                    mthInv.setInvocation(b);

                    if (statement.size() == 1)
                    {
                        BinaryExpression bR = new BinaryExpression();
                        bR.setLeftExpression(mthInv);
                        bR.setOperator(JavaTokenTypes.DOT);
                        bR.setRightExpression(new VarLiteral("getValueLong"));

                        MethodInvocation miR = new MethodInvocation();
                        mthInv.setReturnType("long");
                        miR.setInvocation(bR);

                        return miR;
                    }
                    else
                    {
                        path.add(v.getValue());
                        statement.set(0, mthInv);
                    }
                }
                else
                {
                    BinaryExpression be = new BinaryExpression();
                    be.setLeftExpression((ExpressionSymbol) statement.get(pos -
                            1));
                    be.setOperator(JavaTokenTypes.DOT);
                    be.setRightExpression(new VarLiteral("getParent"));

                    MethodInvocation mparent = new MethodInvocation();
                    mparent.setReturnType("object");
                    mparent.setInvocation(be);

                    if ((pos + 1) == statement.size())
                    {
                        throw new boRuntimeException("XEORecognizer",
                            "Erro ao verificar o attributo: " +
                            thisAttribute.getName() + " do objecto: " +
                            bodef.getBoName(), new Exception());
                    }

                    statement.remove(pos);
                    pos--;
                    statement.set(0, mparent);
                    path.add(v.getValue());
                }
            }
            else if ((v.getValue().indexOf("_DATE") == -1) &&
                    (v.getValue().indexOf("DATE_") != -1))
            {
                //new java.util.GregorianCalendar().getTimeInMillis();
                //java.util
                BinaryExpression bJavaUtil = new BinaryExpression();
                bJavaUtil.setLeftExpression(new VarLiteral("java"));
                bJavaUtil.setOperator(JavaTokenTypes.DOT);
                bJavaUtil.setRightExpression(new VarLiteral("util"));

                //java.util.GregorianCalendar
                BinaryExpression bGreg = new BinaryExpression();
                bGreg.setLeftExpression(bJavaUtil);
                bGreg.setOperator(JavaTokenTypes.DOT);
                bGreg.setRightExpression(new VarLiteral("GregorianCalendar"));

                //java.util.GregorianCalendar()                    
                ConstructorExpression cGreg = new ConstructorExpression();
                cGreg.setExpression(bGreg);
                cGreg.setParameters(getDateParameters(v.getValue()));

                if (alone)
                {
                    return cGreg;
                }

                //java.util.GregorianCalendar().getTimeInMillis
                BinaryExpression bTimeM = new BinaryExpression();
                bTimeM.setLeftExpression(cGreg);
                bTimeM.setOperator(JavaTokenTypes.DOT);
                bTimeM.setRightExpression(new VarLiteral("getTimeInMillis"));

                //java.util.GregorianCalendar().getTimeInMillis()
                MethodInvocation timeMills = new MethodInvocation();
                timeMills.setReturnType("long");
                timeMills.setInvocation(bTimeM);
                statement.set(pos, timeMills);

                return timeMills;
            }
            else if ((v.getValue().indexOf("SUM_DATE") != -1) ||
                    (v.getValue().indexOf("SUBTRACT_DATE") != -1) ||
                    (v.getValue().indexOf("SUM_UTIL_DATE") != -1) ||
                    (v.getValue().indexOf("SUBTRACT_UTIL_DATE") != -1) ||
                    (v.getValue().indexOf("DIFF_IN_UTIL_DAYS") != -1)||
                    (v.getValue().indexOf("DIFF_IN_DAYS") != -1))
            {
                MethodInvocation getEboctx = null;
                BinaryExpression netBoUtlDt = getNetgestBoUtils("DateUtils");

                BinaryExpression bDate = new BinaryExpression();
                bDate.setLeftExpression(netBoUtlDt);
                bDate.setOperator(JavaTokenTypes.DOT);

                if (v.getValue().indexOf("SUM_DATE") != -1)
                {
                    bDate.setRightExpression(new VarLiteral("sumToDate"));
                }
                else if (v.getValue().indexOf("SUM_UTIL_DATE") != -1)
                {
                    bDate.setRightExpression(new VarLiteral("sumToUtilDate"));
                    getEboctx = getEbocontextCode();
                }
                else if (v.getValue().indexOf("SUBTRACT_UTIL_DATE") != -1)
                {
                    bDate.setRightExpression(new VarLiteral(
                            "subtractToUtilDate"));
                    getEboctx = getEbocontextCode();
                }
                else if (v.getValue().indexOf("SUBTRACT_DATE") != -1)
                {
                    bDate.setRightExpression(new VarLiteral("subtractToDate"));
                }
                else if (v.getValue().indexOf("DIFF_IN_UTIL_DAYS") != -1)
                {
                    bDate.setRightExpression(new VarLiteral("diffInUtilsDays"));
                    getEboctx = getEbocontextCode();
                }
                else if (v.getValue().indexOf("DIFF_IN_DAYS") != -1)
                {
                    bDate.setRightExpression(new VarLiteral("diffInDays"));
                }

                MethodInvocation date = new MethodInvocation();
                date.setReturnType("Date");
                date.setInvocation(bDate);

                if (statement.get(pos + 1) instanceof EList)
                {
                    if (getEboctx == null)
                    {
                        date.setParameters((EList) statement.get(pos + 1));
                    }
                    else
                    {
                        EList el = (EList) statement.get(pos + 1);
                        ArrayList params = el.getList();
                        params.add(getEboctx);
                        date.setParameters(el);
                    }
                }
                else
                {
                    EList el = new EList();
                    ArrayList rr = new ArrayList(1);
                    rr.add(statement.get(pos + 1));

                    if (getEboctx != null)
                    {
                        rr.add(getEboctx);
                    }

                    el.setList(rr);
                    date.setParameters(el);
                }

                //                if (alone)
                //                {
                return date;

                //                }

                /*
                                BinaryExpression bTimeMill = new BinaryExpression();
                                bTimeMill.setLeftExpression(date);
                                bTimeMill.setOperator(JavaTokenTypes.DOT);
                                bTimeMill.setRightExpression(new VarLiteral("getTime"));

                                MethodInvocation timeMills = new MethodInvocation();
                                timeMills.setReturnType("long");
                                timeMills.setInvocation(bTimeMill);
                                statement.set(pos, timeMills);

                                return timeMills;
                */
            }
            else if ((v.getValue().indexOf("NOW") != -1) ||
                    (v.getValue().indexOf("TODAY") != -1))
            {
                //netgest.bo.utils.DateUtils.get...().getTime()
                BinaryExpression netBoUtlDt = getNetgestBoUtils("DateUtils");

                BinaryExpression bDate = new BinaryExpression();
                bDate.setLeftExpression(netBoUtlDt);
                bDate.setOperator(JavaTokenTypes.DOT);
                bDate.setRightExpression(getDateMethod(v.getValue()));

                MethodInvocation date = new MethodInvocation();
                date.setReturnType("Date");
                date.setInvocation(bDate);

                if (alone)
                {
                    return date;
                }

                BinaryExpression bTimeMill = new BinaryExpression();
                bTimeMill.setLeftExpression(date);
                bTimeMill.setOperator(JavaTokenTypes.DOT);
                bTimeMill.setRightExpression(new VarLiteral("getTime"));

                MethodInvocation timeMills = new MethodInvocation();
                timeMills.setReturnType("long");
                timeMills.setInvocation(bTimeMill);
                statement.set(pos, timeMills);

                return timeMills;
            }
            else if(v.getValue().indexOf("SYSUSER") != -1)
            {
                //netgest.bo.utils.UserUtils;
                BinaryExpression netBoUtlDt = getNetgestBoUtils("UserUtils");
                
                BinaryExpression getUser = new BinaryExpression();
                getUser.setLeftExpression(netBoUtlDt);
                getUser.setOperator(JavaTokenTypes.DOT);
                getUser.setRightExpression(new VarLiteral("getUser"));
                
                ArrayList params = new ArrayList();
                params.add(getEbocontextCode());
                params.add(new StringLiteral("SYSUSER"));
                EList el = new EList();
                el.setList(params);

                MethodInvocation mgetUser = new MethodInvocation();
                mgetUser.setReturnType("long");
                mgetUser.setInvocation(getUser);
                mgetUser.setParameters(el);

                return mgetUser;
            }
            else if(v.getValue().indexOf("USER") != -1)
            {
                //getEboContext().getBoSession().getPerformerBoui();
                MethodInvocation getEboContext = getEbocontextCode();
                
                BinaryExpression b1 = new BinaryExpression();
                b1.setLeftExpression(getEboContext);
                b1.setOperator(JavaTokenTypes.DOT);
                b1.setRightExpression(new VarLiteral("getBoSession"));
                
                MethodInvocation getBoSession = new MethodInvocation();
                getBoSession.setReturnType("boSession");
                getBoSession.setInvocation(b1);
                
                BinaryExpression b2 = new BinaryExpression();
                b2.setLeftExpression(getBoSession);
                b2.setOperator(JavaTokenTypes.DOT);
                b2.setRightExpression(new VarLiteral("getPerformerBoui"));
                
                MethodInvocation getPerformerBoui = new MethodInvocation();
                getPerformerBoui.setReturnType("long");
                getPerformerBoui.setInvocation(b2);

                return getPerformerBoui;
            }
            else if (v.getValue().indexOf("ROUND") != -1)
            {
                int size = Integer.parseInt(v.getValue().substring(v.getValue()
                                                                    .indexOf("_") +
                            1));
                BinaryExpression sum = new BinaryExpression();
                BinaryExpression netBoCalc = getNetgestBoUtils("Calculate");

                sum.setLeftExpression(netBoCalc);
                sum.setOperator(JavaTokenTypes.DOT);
                sum.setRightExpression(new VarLiteral("round"));

                ArrayList params = new ArrayList();

                //String.valueOf()
                BinaryExpression stringVal = new BinaryExpression();
                stringVal.setLeftExpression(new VarLiteral("String"));
                stringVal.setOperator(JavaTokenTypes.DOT);
                stringVal.setRightExpression(new VarLiteral("valueOf"));

                MethodInvocation strCall = new MethodInvocation();
                strCall.setReturnType("String");
                strCall.setInvocation(stringVal);

                ArrayList strParams = new ArrayList();
                strParams.add(statement.get(pos + 1));

                EList listP = new EList();
                listP.setList(strParams);
                strCall.setParameters(listP);

                params.add(strCall);

                NumericLiteral nm = new NumericLiteral();
                nm.setValue(size);
                params.add(nm);

                EList elP = new EList();
                elP.setList(params);

                MethodInvocation ret = new MethodInvocation();
                ret.setReturnType("BigDecimal");
                ret.setInvocation(sum);
                ret.setParameters(elP);

                if (!formula)
                {
                    String type = (size == 0) ? "getValueLong" : "getValueDouble";

                    BinaryExpression valueRet = new BinaryExpression();
                    valueRet.setLeftExpression(ret);
                    valueRet.setOperator(JavaTokenTypes.DOT);
                    valueRet.setRightExpression(new VarLiteral(
                            translateToBigDecimal(type)));

                    MethodInvocation toRet = new MethodInvocation();
                    toRet.setReturnType(type);
                    toRet.setInvocation(valueRet);

                    return toRet;
                }

                //                logger.debug(ret);
                return ret;
            }
            else if (v.getValue().indexOf("CONCAT") != -1)
            {
                BinaryExpression sum = new BinaryExpression();

                BinaryExpression netBoCalc = getNetgestBoUtils("Calculate");

                sum.setLeftExpression(netBoCalc);
                sum.setOperator(JavaTokenTypes.DOT);
                sum.setRightExpression(new VarLiteral("concat"));

                ArrayList params = new ArrayList();
                params.add(statement.get(pos + 1));

                EList listP = new EList();
                listP.setList(params);

                MethodInvocation concatCall = new MethodInvocation();
                concatCall.setReturnType("String");
                concatCall.setInvocation(sum);
                concatCall.setParameters(listP);

                return concatCall;
            }
            else if (v.getValue().indexOf("TO_DATE") != -1)
            {
                String format = v.getValue().substring(8);
                BinaryExpression sum = new BinaryExpression();

                BinaryExpression netBoCalc = getNetgestBoUtils("Calculate");

                BinaryExpression netBoCalcType = new BinaryExpression();
                netBoCalcType.setLeftExpression(netBoCalc);
                netBoCalcType.setOperator(JavaTokenTypes.DOT);
                netBoCalcType.setRightExpression(new VarLiteral("to_date"));

                ArrayList params = new ArrayList();
                params.add(statement.get(pos + 1));
                params.add(new StringLiteral((String) formatts.get(format)));

                MethodInvocation strCall = new MethodInvocation();
                strCall.setInvocation(netBoCalcType);

                EList listP = new EList();
                listP.setList(params);
                strCall.setParameters(listP);
                strCall.setReturnType("Date");

                return strCall;
            }
            else if ((v.getValue().indexOf("UPPER") != -1) ||
                    (v.getValue().indexOf("LOWER") != -1))
            {
                BinaryExpression stringUtils = getNetgestUtils("StringUtils");
                BinaryExpression netBoString = new BinaryExpression();
                netBoString.setLeftExpression(stringUtils);
                netBoString.setOperator(JavaTokenTypes.DOT);

                if (v.getValue().indexOf("UPPER") != -1)
                {
                    netBoString.setRightExpression(new VarLiteral("upper"));
                }
                else
                {
                    netBoString.setRightExpression(new VarLiteral("lower"));
                }

                ArrayList params = new ArrayList();
                params.add(statement.get(pos + 1));

                MethodInvocation strCall = new MethodInvocation();
                strCall.setInvocation(netBoString);

                EList listP = new EList();
                listP.setList(params);
                strCall.setParameters(listP);
                strCall.setReturnType("String");

                return strCall;
            }
            else if ((v.getValue().indexOf("IS_NULL") != -1))
            {
                BinaryExpression stringUtils = getNetgestUtils("StringUtils");
                BinaryExpression netBoString = new BinaryExpression();
                netBoString.setLeftExpression(stringUtils);
                netBoString.setOperator(JavaTokenTypes.DOT);
                netBoString.setRightExpression(new VarLiteral("isNull"));                

                ArrayList params = new ArrayList();                
                if (statement.get(pos + 1) instanceof MethodInvocation)
                {
                    MethodInvocation hlp = (MethodInvocation) statement.get(pos +
                            1);
                    if (!"object".equals(hlp.getReturnType()))
                    {
                        if(hlp.getInvocation() instanceof BinaryExpression)
                        {
                            BinaryExpression bi = (BinaryExpression) hlp.getInvocation();
                            if(bi.getRightExpression() instanceof VarLiteral)
                            {
                                VarLiteral sl = (VarLiteral)bi.getRightExpression();
                                if(sl.getValue() != null && sl.getValue().startsWith("getValue"))
                                {
                                    bi.setRightExpression(new VarLiteral(
                                        "getValueObject"));                                    
                                }
                            }
                        }
                    }
                }
                params.add(statement.get(pos + 1));
                MethodInvocation strCall = new MethodInvocation();
                strCall.setInvocation(netBoString);

                EList listP = new EList();
                listP.setList(params);
                strCall.setParameters(listP);
                strCall.setReturnType("boolean");

                return strCall;
            }
            else if ((v.getValue().indexOf("SUM_DATE") == -1) &&
                    (v.getValue().indexOf("SUBTRACT_DATE") == -1) &&
                    ((v.getValue().indexOf("SUM") != -1) ||
                    (v.getValue().indexOf("SUBTRACT") != -1)))
            {
                ExpressionSymbol mthInvValue = null;

                if (statement.get(pos + 1) instanceof MethodInvocation)
                {
                    MethodInvocation hlp = (MethodInvocation) statement.get(pos +
                            1);

                    if ("object".equals(hlp.getReturnType()))
                    {
                        BinaryExpression getValueObj = new BinaryExpression();
                        getValueObj.setLeftExpression((ExpressionSymbol) statement.get(pos +
                                1));
                        getValueObj.setOperator(JavaTokenTypes.DOT);
                        getValueObj.setRightExpression(new VarLiteral(
                                "getValueObject"));
                        mthInvValue = new MethodInvocation();
                        ((MethodInvocation) mthInvValue).setInvocation(getValueObj);
                    }
                    else
                    {
                        mthInvValue = (ExpressionSymbol) statement.get(pos + 1);
                    }
                }

                if (v.getValue().indexOf("SUM") != -1)
                {
                    StatementSymbol ss = getResumeCode(resumeBridge,
                            mthInvValue, "sum");

                    return ss;
                }
                else
                {
                    return getResumeCode(resumeBridge, mthInvValue, "subtract");
                }
            }
            else
            {
                ArrayList aux = new ArrayList(path);
                aux.add(v.getValue());

                if (isObjAttribute(aux, 0, bodef))
                {
                    b.setLeftExpression((ExpressionSymbol) statement.get(pos -
                            1));
                    b.setOperator(JavaTokenTypes.DOT);
                    b.setRightExpression(new VarLiteral("getAttribute"));

                    MethodInvocation mi = new MethodInvocation();

                    //                        mi.setReturnType(getValueByType(aux, 0, bodef));
                    mi.setReturnType("object");
                    mi.setInvocation(b);

                    EList e = new EList();
                    e.addExpr(new StringLiteral(v.getValue()));
                    mi.setParameters(e);

                    if ((pos + 1) == statement.size())
                    {
                        if (defaultValue)
                        {
                            BinaryExpression exp = new BinaryExpression();
                            exp.setLeftExpression(mi);
                            exp.setOperator(JavaTokenTypes.DOT);

                            String hlp = getValueByType(aux, 0, bodef);
                            exp.setRightExpression(new VarLiteral(hlp));

                            MethodInvocation typeCall = new MethodInvocation();
                            typeCall.setReturnType(hlp);
                            typeCall.setReturnType(getTypeofMethodString(hlp));
                            typeCall.setInvocation(exp);

                            return typeCall;
                        }

                        if (formula || defaultValue)
                        {
                            if (!attributesHeight.contains(getString(aux)))
                            {
                                //dúvidas a cerca do peso
                                //height += getHeight(v.getValue());
                                add(getString(aux));
                                attributesHeight.add(getString(aux));
                            }
                        }

                        return mi;
                    }
                    else
                    {
                        //foi colocado de forma a efecuar o getObject
                        BinaryExpression bR = new BinaryExpression();
                        bR.setLeftExpression(mi);
                        bR.setOperator(JavaTokenTypes.DOT);
                        bR.setRightExpression(new VarLiteral("getObject"));

                        MethodInvocation getObj = new MethodInvocation();
                        getObj.setReturnType("object");
                        getObj.setInvocation(bR);
                        statement.set(0, getObj);

                        statement.remove(pos);
                        pos--;
                        statement.set(pos, getObj);
                        path.add(v.getValue());
                    }
                }
                else if (bridge && (path.size() == 0))
                {
                    String aux2 = remove_bridge(v.getValue());

                    //p_bridge.getObject().getAttribute("assd").getValueObject();
                    b.setLeftExpression(new VarLiteral("p_bridge"));
                    b.setOperator(JavaTokenTypes.DOT);
                    b.setRightExpression(new VarLiteral("getObject"));

                    MethodInvocation getObj = new MethodInvocation();
                    getObj.setReturnType("object");
                    getObj.setInvocation(b);

                    BinaryExpression be = new BinaryExpression();
                    be.setLeftExpression(getObj);
                    be.setOperator(JavaTokenTypes.DOT);
                    be.setRightExpression(new VarLiteral("getAttribute"));

                    MethodInvocation getAttr = new MethodInvocation();
                    getAttr.setReturnType("object");
                    getAttr.setInvocation(be);

                    ArrayList r = new ArrayList();
                    r.add(new StringLiteral(aux2));

                    EList el = new EList();
                    el.setList(r);
                    getAttr.setParameters(el);

                    if (statement.size() == 1)
                    {
                        path.add(v.getValue());

                        BinaryExpression bR = new BinaryExpression();
                        bR.setLeftExpression(getAttr);
                        bR.setOperator(JavaTokenTypes.DOT);

                        String hlp = getValueByType(aux2, attBridgeBodef);
                        bR.setRightExpression(new VarLiteral(hlp));

                        MethodInvocation miR = new MethodInvocation();
                        miR.setReturnType(getTypeofMethodString(hlp));
                        miR.setInvocation(bR);

                        return miR;
                    }
                    else
                    {
                        //dúvidas
                        path.add(v.getValue());
                        statement.set(0, getAttr);
                    }
                }
                else if (v.getValue().indexOf("resume_ret") != -1)
                {
                    return v;
                }

                //method: isObjMethod
                else if (isObjMethod(aux, 0, bodef))
                {
                    method = true;

                    if (pos == 0)
                    {
                        BinaryExpression be = new BinaryExpression();
                        be.setLeftExpression(new VarLiteral("this"));
                        be.setOperator(JavaTokenTypes.DOT);
                        be.setRightExpression(new VarLiteral("getParent"));

                        MethodInvocation mparent = new MethodInvocation();
                        mparent.setReturnType("object");
                        mparent.setInvocation(be);
                        b.setLeftExpression(mparent);
                    }
                    else
                    {
                        b.setLeftExpression((ExpressionSymbol) statement.get(pos -
                                1));
                    }

                    b.setOperator(JavaTokenTypes.DOT);
                    b.setRightExpression(new VarLiteral(v.getValue()));

                    MethodInvocation mi = new MethodInvocation();
                    mi.setReturnType("object");
                    mi.setInvocation(b);

                    return mi;
                }
            }

            pos++;
        }

        return toExpression(statement, pos, path, alone);
    }

    private String getValueByType(String attName, boDefAttribute attDef)
        throws boRuntimeException
    {
        String objType = attDef.getType();

        if ((objType == null) || "".equals(objType))
        {
            throw new boRuntimeException("XEORecognizer",
                "Erro ao verificar o attributo: " + attName + " da bridge: " +
                attDef.getName(), new Exception());
        }
        else
        {
            int ind;

            if ((ind = objType.indexOf("object.")) != -1)
            {
                objType = objType.substring(ind + 7, objType.length());
            }

            ArrayList path = new ArrayList();
            path.add(attName);

            return getValueByType(path, 0, boDefHandler.getBoDefinition(objType));
        }
    }

    private String getValueOfBridgeAtt(String aux) throws boRuntimeException
    {
        for (int j = 0; j < attributes.length; j++)
        {
            if (aux.equals(attributes[j].getName()))
            {
                return getMethodType(attributes[j].getType(),
                    attributes[j].getDecimals());
            }
        }

        return "getValueObject";
    }

    private MethodInvocation getEbocontextCode()
    {
        MethodInvocation getEboctx = new MethodInvocation();
        getEboctx.setReturnType("object");
        getEboctx.setInvocation(new VarLiteral("getEboContext"));

        return getEboctx;
    }

    private String getValueByType(ArrayList path, int pos, boDefHandler classDef)
        throws boRuntimeException
    {
        String aux = (String) path.get(pos);

        if (aux.startsWith("parent"))
        {
            if (path.size() == (pos + 1))
            {
                //                return "getValueLong";
                return "object";
            }
            else if (aux.startsWith("parent_"))
            {
                String className = aux.substring(7, aux.length());

                return getValueByType(path, pos + 1,
                    boDefHandler.getBoDefinition(className));
            }
            else
            {
                return getValueByType(path, pos + 1, classDef);
            }
        }
        else if (aux.startsWith("DATE_"))
        {
            if (path.size() == (pos + 1))
            {
                return "long";
            }
        }
        else
        {
            boDefAttribute[] attrs = classDef.getAttributesDef();

            if (attrs != null)
            {
                boDefAttribute attr = null;

                for (int j = 0; j < attrs.length; j++)
                {
                    attr = attrs[j];

                    if (aux.equals(attr.getName()))
                    {
                        if (path.size() == (pos + 1))
                        {
                            return getMethodType(attr.getType(),
                                attr.getDecimals());
                        }
                        else
                        {
                            if (attr.getMaxOccurs() > 1)
                            {
                                if (!aux.startsWith("bridgeObject_"))
                                {
                                    //bridge
                                    pos++;

                                    String aux2 = (String) path.get(pos);
                                    boDefAttribute[] bridgeAttr = attr.getBridge()
                                                                      .getBoAttributes();

                                    if ((bridgeAttr != null) ||
                                            (bridgeAttr.length > 0))
                                    {
                                        for (int i = 0; i < bridgeAttr.length;
                                                i++)
                                        {
                                            if (aux2.equals(
                                                        bridgeAttr[i].getName()))
                                            {
                                                if (path.size() == (pos + 1))
                                                {
                                                    return getMethodType(bridgeAttr[i].getType(),
                                                        bridgeAttr[i].getDecimals());
                                                }
                                                else
                                                {
                                                    int ind;
                                                    String objType = bridgeAttr[i].getType();

                                                    if ((ind = objType.indexOf(
                                                                    "object.")) != -1)
                                                    {
                                                        objType = objType.substring(ind,
                                                                objType.length());
                                                    }

                                                    return getValueByType(path,
                                                        pos + 1,
                                                        boDefHandler.getBoDefinition(
                                                            objType));
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //atributo
                            String objType = attr.getType();

                            if ((objType == null) || "".equals(objType))
                            {
                                throw new boRuntimeException("XEORecognizer",
                                    "Erro ao verificar o attributo: " +
                                    thisAttribute.getName() + " do objecto: " +
                                    bodef.getBoName(), new Exception());
                            }
                            else
                            {
                                int ind;

                                if ((ind = objType.indexOf("object.")) != -1)
                                {
                                    objType = objType.substring(ind + 7,
                                            objType.length());
                                }

                                if (aux.startsWith("bridgeObject_"))
                                {
                                    path.set(pos, remove_bridge(aux));

                                    return getValueByType(path, pos,
                                        boDefHandler.getBoDefinition(objType));
                                }

                                return getValueByType(path, pos + 1,
                                    boDefHandler.getBoDefinition(objType));
                            }
                        }
                    }
                }
            }
            else
            {
                throw new boRuntimeException("XEORecognizer",
                    "Erro ao verificar o attributo: " +
                    thisAttribute.getName() + " do objecto: " +
                    bodef.getBoName(), new Exception());
            }
        }

        return "getValueObject";
    }

    private String getMethodType(String type, int d)
    {
        String upper = type.toUpperCase();

        if (upper.indexOf("CHAR") != -1)
        {
            return "getValueString";
        }

        if (upper.indexOf("NUMBER") != -1)
        {
            if ((d == 0) && (upper.indexOf(",") == -1))
            {
                return "getValueLong";
            }

            return "getValueDouble";
        }

        if (upper.indexOf("DATE") != -1)
        {
            return "getValueDate";
        }

        //iFile
        return "getValueObject";
    }

    private String translateToBigDecimal(String type)
    {
        if (type.equals("getValueLong"))
        {
            return "longValue";
        }

        if (type.equals("getValueDouble"))
        {
            return "doubleValue";
        }

        return "toString";
    }

    private boolean isTypeofMethodString(String mth)
    {
        if (mth.indexOf("String") != -1)
        {
            return true;
        }

        return false;
    }

    private String getTypeofMethodString(String mth) throws boRuntimeException
    {
        try
        {
            if (mth == null)
            {
                return "object";
            }

            if (mth.indexOf("String") != -1)
            {
                return "String";
            }

            if (mth.indexOf("Long") != -1)
            {
                return "long";
            }

            if (mth.indexOf("Double") != -1)
            {
                return "double";
            }

            if (mth.indexOf("Date") != -1)
            {
                return "Date";
            }

            if (mth.indexOf("Object") != -1)
            {
                return "object";
            }

            return "object";
        }
        catch (Exception e)
        {
            throw new boRuntimeException("XEORecognizer",
                "Erro ao verificar o attributo: " + thisAttribute.getName() +
                " do objecto: " + bodef.getBoName(), e);
        }
    }

    private boolean isAttribute(String s)
    {
        boDefAttribute[] atts = bodef.getAttributesDef();

        for (int i = 0; i < atts.length; i++)
        {
            if (s.equals(atts[i].getName()))
            {
                if (atts[i].getMaxOccurs() > 1)
                {
                    resumeBridge = s;
                }

                return true;
            }
        }

        return false;
    }

    public int getHeight(String s)
    {
        for (int i = 0; i < attributes.length; i++)
        {
            //            logger.debug(attributes[i].getName());
            if (s.equals(attributes[i].getName()))
            {
                if ((attributes[i].getFormula() == null) ||
                        "".equals(attributes[i].getFormula()))
                {
                    return 1;
                }

                return 2;
            }
        }

        return getHeightAttributeFromBridge(s);
    }

    private int getHeightAttributeFromBridge(String aux)
    {
        boDefAttribute[] attrs = bodef.getAttributesDef();

        if (attrs != null)
        {
            boDefAttribute attr = null;

            for (int j = 0; j < attrs.length; j++)
            {
                attr = attrs[j];

                if (aux.equals(attr.getName()))
                {
                    if ((attr.getFormula() == null) ||
                            "".equals(attr.getFormula()))
                    {
                        return 1;
                    }

                    return 2;
                }
            }
        }

        return 1;
    }

    private boolean isAttributeFromBridge(String aux)
    {
        for (int j = 0; j < attributes.length; j++)
        {
            if (aux.equals(attributes[j].getName()))
            {
                return true;
            }
        }

        return false;
    }

    private boolean isObjAttribute(ArrayList path, int pos,
        boDefHandler classDef) throws boRuntimeException
    {
        String aux = (String) path.get(pos);

        if (aux.startsWith("parent"))
        {
            if (path.size() == (pos + 1))
            {
                //                return "getValueLong";
                return false;
            }
            else if (aux.startsWith("parent_"))
            {
                String className = aux.substring(7, aux.length());

                return isObjAttribute(path, pos + 1,
                    boDefHandler.getBoDefinition(className));
            }
            else
            {
                return isObjAttribute(path, pos + 1, classDef);
            }
        }
        else if (aux.startsWith("DATE_"))
        {
            return false;
        }

        //        else if(bridge && aux.startsWith("bridgeObject_"))
        //        {
        //            verify_bridgeObj();
        //        }
        else
        {
            boDefAttribute[] attrs = classDef.getAttributesDef();

            if (attrs != null)
            {
                boDefAttribute attr = null;

                for (int j = 0; j < attrs.length; j++)
                {
                    attr = attrs[j];

                    if (aux.equals(attr.getName()))
                    {
                        if (path.size() == (pos + 1))
                        {
                            if (attr.getMaxOccurs() > 1)
                            {
                                resumeBridge = attr.getName();
                            }

                            return true;
                        }
                        else
                        {
                            if (attr.getMaxOccurs() > 1)
                            {
                                //bridge
                                pos++;

                                String aux2 = (String) path.get(pos);
                                boDefAttribute[] bridgeAttr = attr.getBridge()
                                                                  .getBoAttributes();

                                if ((bridgeAttr != null) ||
                                        (bridgeAttr.length > 0))
                                {
                                    for (int i = 0; i < bridgeAttr.length;
                                            i++)
                                    {
                                        if (aux2.equals(bridgeAttr[i].getName()))
                                        {
                                            if (path.size() == (pos + 1))
                                            {
                                                resumeBridge = attr.getName();

                                                return true;
                                            }
                                            else
                                            {
                                                int ind;
                                                String objType = bridgeAttr[i].getType();

                                                if ((ind = objType.indexOf(
                                                                "object.")) != -1)
                                                {
                                                    objType = objType.substring(ind,
                                                            objType.length());
                                                }

                                                return isObjAttribute(path,
                                                    pos,
                                                    boDefHandler.getBoDefinition(
                                                        objType));
                                            }
                                        }
                                    }

                                    //não encontrou nos atributos da bridge vou procurar no objecto                                    
                                }

                                pos--;
                            }

                            //atributo
                            String objType = attr.getType();

                            if ((objType == null) || "".equals(objType))
                            {
                                throw new boRuntimeException("XEORecognizer",
                                    "Erro ao verificar o attributo: " +
                                    thisAttribute.getName() + " do objecto: " +
                                    bodef.getBoName(), new Exception());
                            }
                            else
                            {
                                int ind;

                                if ((ind = objType.indexOf("object.")) != -1)
                                {
                                    objType = objType.substring(ind + 7,
                                            objType.length());
                                }

                                return isObjAttribute(path, pos + 1,
                                    boDefHandler.getBoDefinition(objType));
                            }
                        }
                    }
                }
            }
            else
            {
                throw new boRuntimeException("XEORecognizer",
                    "Erro ao verificar o attributo: " +
                    thisAttribute.getName() + " do objecto: " +
                    bodef.getBoName(), new Exception());
            }
        }

        return false;
    }

    private boolean isBridgeAttribute(ArrayList path, int pos,
        boDefHandler classDef) throws boRuntimeException
    {
        String aux = (String) path.get(pos);

        if (aux.startsWith("parent"))
        {
            if (path.size() == (pos + 1))
            {
                throw new boRuntimeException("XEORecognizer",
                    "Erro ao verificar o attributo: " +
                    thisAttribute.getName() + " do objecto: " +
                    bodef.getBoName(), new Exception());
            }
            else if (aux.startsWith("parent_"))
            {
                String className = aux.substring(7, aux.length());

                return isBridgeAttribute(path, pos + 1,
                    boDefHandler.getBoDefinition(className));
            }
            else
            {
                return isBridgeAttribute(path, pos + 1, classDef);
            }
        }
        else
        {
            boDefAttribute[] attrs = classDef.getAttributesDef();

            if (attrs != null)
            {
                boDefAttribute attr = null;

                for (int j = 0; j < attrs.length; j++)
                {
                    attr = attrs[j];

                    if (aux.equals(attr.getName()))
                    {
                        if (path.size() == (pos + 1))
                        {
                            return false;
                        }
                        else
                        {
                            if (attr.getMaxOccurs() > 1)
                            {
                                //bridge
                                pos++;

                                String aux2 = (String) path.get(pos);
                                boDefAttribute[] bridgeAttr = attr.getBridge()
                                                                  .getBoAttributes();

                                if ((bridgeAttr != null) ||
                                        (bridgeAttr.length > 0))
                                {
                                    for (int i = 0; i < bridgeAttr.length;
                                            i++)
                                    {
                                        if (aux2.equals(bridgeAttr[i].getName()))
                                        {
                                            if (path.size() == (pos + 1))
                                            {
                                                return true;
                                            }
                                            else
                                            {
                                                int ind;
                                                String objType = bridgeAttr[i].getType();

                                                if ((ind = objType.indexOf(
                                                                "object.")) != -1)
                                                {
                                                    objType = objType.substring(ind,
                                                            objType.length());
                                                }

                                                return isBridgeAttribute(path,
                                                    pos + 1,
                                                    boDefHandler.getBoDefinition(
                                                        objType));
                                            }
                                        }
                                    }
                                }
                            }

                            //atributo
                            String objType = attr.getType();

                            if ((objType == null) || "".equals(objType))
                            {
                                throw new boRuntimeException("XEORecognizer",
                                    "Erro ao verificar o attributo: " +
                                    thisAttribute.getName() + " do objecto: " +
                                    bodef.getBoName(), new Exception());
                            }
                            else
                            {
                                int ind;

                                if ((ind = objType.indexOf("object.")) != -1)
                                {
                                    objType = objType.substring(ind + 7,
                                            objType.length());
                                }

                                return isBridgeAttribute(path, pos,
                                    boDefHandler.getBoDefinition(objType));
                            }
                        }
                    }
                }
            }
            else
            {
                throw new boRuntimeException("XEORecognizer",
                    "Erro ao verificar o attributo: " +
                    thisAttribute.getName() + " do objecto: " +
                    bodef.getBoName(), new Exception());
            }
        }

        return false;
    }

    private boolean isThisAttribute(String s)
    {
        for (int j = 0; j < attributes.length; j++)
        {
            if (s.equals(thisAttribute.getName()))
            {
                if (attributes[j].getMaxOccurs() > 1)
                {
                    resumeBridge = s;
                }

                return true;
            }
        }

        return false;
    }

    private boolean isReserved(String s)
    {
        for (int i = 0; i < reservedWords.length; i++)
        {
            if (s.equals(reservedWords[i]))
            {
                return true;
            }
        }

        return false;
    }

    private VarLiteral getDateMethod(String aux) throws boRuntimeException
    {
        if (aux.equals("TODAY"))
        {
            return new VarLiteral("getToday");
        }

        if (aux.equals("NOW"))
        {
            return new VarLiteral("getNow");
        }

        if (aux.equals("NOW_HOURS"))
        {
            return new VarLiteral("getNowHours");
        }

        if (aux.equals("NOW_MINUTES"))
        {
            return new VarLiteral("getNowMinutes");
        }

        throw new boRuntimeException("XEORecognizer",
            "Erro ao verificar o attributo: " + thisAttribute.getName() +
            " do objecto: " + bodef.getBoName(), new Exception());
    }

    private EList getDateParameters(String s) throws boRuntimeException
    {
        EList ret = new EList();
        ArrayList r = new ArrayList();

        if (s.endsWith("_"))
        {
            return null;
        }
        else
        {
            int pos = s.indexOf("DATE_");
            NumericLiteral num = null;
            String aux = "";
            int n;

            for (int i = pos + 5; i < s.length(); i++)
            {
                while ((i < s.length()) && (s.charAt(i) != '_'))
                {
                    aux += s.charAt(i);
                    i++;
                }

                try
                {
                    num = new NumericLiteral();

                    if (r.size() == 1)
                    {
                        //mês
                        n = Integer.parseInt(aux.trim()) - 1;
                        num.setValue((n < 0) ? 0 : n);
                    }
                    else
                    {
                        num.setValue(Integer.parseInt(aux.trim()));
                    }
                }
                catch (Exception e)
                {
                    throw new boRuntimeException("XEORecognizer",
                        "Erro ao verificar o attributo: " +
                        thisAttribute.getName() + " do objecto: " +
                        bodef.getBoName(), e);
                }

                r.add(num);
                aux = "";

                //i++;
            }

            ret.setList(r);
        }

        return ret;
    }

    private int comparingDate(ArrayList statements, int i, BinaryExpression b)
        throws boRuntimeException
    {
        return comparingDate(statements, i, b, null);
    }

    //compare
    private int comparingDate(ArrayList statements, int i, BinaryExpression b,
        String aux) throws boRuntimeException
    {
        if ((statements.get(i - 1) instanceof MethodInvocation) &&
                ("Date".equals(
                    ((MethodInvocation) statements.get(i - 1)).getReturnType()) ||
                "long".equalsIgnoreCase(
                    ((MethodInvocation) statements.get(i - 1)).getReturnType())))
        {
            BinaryExpression toReturn = new BinaryExpression();

            if ("Date".equals(
                        ((MethodInvocation) statements.get(i - 1)).getReturnType()))
            {
                b.setRightExpression(new VarLiteral("toLong"));

                MethodInvocation mtInvLeft = new MethodInvocation();
                mtInvLeft.setInvocation(b);

                BinaryExpression right = new BinaryExpression();
                right.setLeftExpression((ExpressionSymbol) statements.get(i +
                        1));
                right.setRightExpression(new VarLiteral("getTime"));
                right.setOperator(JavaTokenTypes.DOT);

                MethodInvocation mtInvRight = new MethodInvocation();
                mtInvRight.setInvocation(right);

                toReturn.setLeftExpression(mtInvLeft);

                if (aux == null)
                {
                    toReturn.setOperator(JavaTokenTypes.EQUAL);
                }
                else
                {
                    toReturn.setOperator(getBinaryOperator(aux));
                }

                toReturn.setRightExpression(mtInvRight);
            }
            else
            {
                if (aux == null)
                {
                    b.setOperator(JavaTokenTypes.EQUAL);
                }
                else
                {
                    b.setOperator(getBinaryOperator(aux));
                }

                BinaryExpression right = new BinaryExpression();
                right.setLeftExpression((ExpressionSymbol) statements.get(i +
                        1));
                right.setRightExpression(new VarLiteral("getTime"));
                right.setOperator(JavaTokenTypes.DOT);

                MethodInvocation mtInvRight = new MethodInvocation();
                mtInvRight.setInvocation(right);

                b.setRightExpression(mtInvRight);
                toReturn = b;
            }

            statements.remove(i - 1);
            statements.remove(i - 1);
            statements.remove(i - 1);

            if (statements.size() > 0)
            {
                statements.add(i - 1, toReturn);
            }
            else
            {
                statements.add(toReturn);
            }

            i = i - 1;
        }
        else
        {
            if (aux == null)
            {
                b.setOperator(JavaTokenTypes.EQUAL);
            }
            else
            {
                b.setOperator(getBinaryOperator(aux));
            }

            b.setRightExpression((ExpressionSymbol) statements.get(i + 1));
            statements.remove(i - 1);
            statements.remove(i - 1);
            statements.remove(i - 1);

            if (statements.size() > 0)
            {
                statements.add(i - 1, b);
            }
            else
            {
                statements.add(b);
            }

            i = i - 1;
        }

        return i;
    }

    private void add(String value)
    {
        if (formula)
        {
            String aux = bridge ? (bridgeName + "." + value) : value;

            if (refreshAttrib.indexOf(aux) < 0)
            {
                refreshAttrib.add(aux);
            }
        }
        else if (!bridge && !refreshAttrib.contains(value))
        {
            refreshAttrib.add(value);
        }
    }

    private String remove_bridge(String aux)
    {
        return (aux.indexOf("bridgeObject_") != -1)
        ? aux.substring(13, aux.length()) : aux;
    }

    public ArrayList getDependence()
    {
        return attributesHeight;
    }

    private ExpressionSymbol transformCalcMethods(ExpressionSymbol formula)
    {
        if (formula instanceof BinaryExpression)
        {
            BinaryExpression be = (BinaryExpression) formula;

            if ((be.getOperator() == JavaTokenTypes.STAR) ||
                    (be.getOperator() == JavaTokenTypes.DIV) ||
                    (be.getOperator() == JavaTokenTypes.PLUS) ||
                    (be.getOperator() == JavaTokenTypes.MINUS))
            {
                MethodInvocation mi = getTypeOfCalc(be.getOperator());
                EList el = new EList();
                el.addExpr(transformCalcMethods(be.getLeftExpression()));
                el.addExpr(transformCalcMethods(be.getRightExpression()));
                mi.setParameters(el);

                return mi;
            }
        }

        return formula;
    }

    private MethodInvocation getTypeOfCalc(int op)
    {
        //netgest.bo.utils.Calculate.
        BinaryExpression netBoCalc = getNetgestBoUtils("Calculate");

        BinaryExpression netBoCalcType = new BinaryExpression();
        netBoCalcType.setLeftExpression(netBoCalc);
        netBoCalcType.setOperator(JavaTokenTypes.DOT);

        if (op == JavaTokenTypes.STAR)
        {
            netBoCalcType.setRightExpression(new VarLiteral("multiply"));
        }
        else if (op == JavaTokenTypes.DIV)
        {
            netBoCalcType.setRightExpression(new VarLiteral("divide"));
        }
        else if (op == JavaTokenTypes.PLUS)
        {
            netBoCalcType.setRightExpression(new VarLiteral("sum"));
        }
        else if (op == JavaTokenTypes.MINUS)
        {
            netBoCalcType.setRightExpression(new VarLiteral("subtract"));
        }

        MethodInvocation calcInv = new MethodInvocation();
        calcInv.setInvocation(netBoCalcType);

        return calcInv;
    }

    private MethodInvocation getCompareMethod(ExpressionSymbol left,
        ExpressionSymbol right, String op, String dtCmp)
        throws boRuntimeException
    {
        //netgest.bo.utils.Calculate.
        BinaryExpression netBoCalc = getNetgestBoUtils("Calculate");

        BinaryExpression netBoCalcType = new BinaryExpression();
        netBoCalcType.setLeftExpression(netBoCalc);
        netBoCalcType.setOperator(JavaTokenTypes.DOT);
        netBoCalcType.setRightExpression(new VarLiteral("compare"));

        MethodInvocation calcInv = new MethodInvocation();
        calcInv.setInvocation(netBoCalcType);

        ArrayList params = new ArrayList();
        EList liP = new EList();
        liP.setList(params);
        params.add(left);
        params.add(right);
        params.add(getTypeComp(op));

        //se fôr uma comparação de datas
        if (dtCmp != null)
        {
            params.add(new StringLiteral(dtCmp));
        }

        calcInv.setParameters(liP);

        return calcInv;
    }

    private NumericLiteral getTypeComp(String aux) throws boRuntimeException
    {
        boolean ignoreCase = true;

        /* SImbolos
        "==" "==="
        ">";">>";">=";">==";
        "<";"<<";"<=";"<==";
         "!=";"!=="

        */
        NumericLiteral nm = new NumericLiteral();

        if (aux.equals(">="))
        {
            nm.setValue(Calculate.IGNORECASE_BIGGER_EQUAL);

            return nm;
        }

        if (aux.equals(">=="))
        {
            nm.setValue(Calculate.BIGGER_EQUAL);

            return nm;
        }

        if (aux.equals(">"))
        {
            nm.setValue(Calculate.IGNORECASE_BIGGER);
            return nm;
        }

        if (aux.equals(">>"))
        {
            nm.setValue(Calculate.BIGGER);
            return nm;
        }

        if (aux.equals("<="))
        {
            nm.setValue(Calculate.IGNORECASE_LESS_EQUAL);

            return nm;
        }

        if (aux.equals("<=="))
        {
            nm.setValue(Calculate.LESS_EQUAL);

            return nm;
        }

        if (aux.equals("<"))
        {
            nm.setValue(Calculate.IGNORECASE_LESS);

            return nm;
        }

        if (aux.equals("<<"))
        {
            nm.setValue(Calculate.LESS);

            return nm;
        }

        if (aux.equals("!="))
        {
            nm.setValue(Calculate.IGNORECASE_NOT_EQUAL);

            return nm;
        }

        if (aux.equals("!=="))
        {
            nm.setValue(Calculate.NOT_EQUAL);

            return nm;
        }

        if (aux.equals("=="))
        {
            nm.setValue(Calculate.IGNORECASE_EQUAL);

            return nm;
        }

        if (aux.equals("==="))
        {
            nm.setValue(Calculate.EQUAL);

            return nm;
        }

        throw new boRuntimeException("XEORecognizer",
            "Erro ao verificar o attributo: " + thisAttribute.getName() +
            " do objecto: " + bodef.getBoName(), new Exception());
    }

    private StatementSymbol getResumeCode(String nameOfBridge,
        ExpressionSymbol exp, String op)
    {
        String varToRetName = getResumeVarName();

        //BigDecimal resume_ret1 = new BigDecimal(0);
        VarDeclarationStatement ret = new VarDeclarationStatement();
        TypeClause tcBig = new TypeClause("BigDecimal");
        ret.setType(tcBig);
        ret.setName(varToRetName);

        //init
        AssignExpression bigDecimalInit = new AssignExpression();
        ConstructorExpression ce = new ConstructorExpression();
        ce.setExpression(new VarLiteral("BigDecimal"));

        EList el1 = new EList();
        ArrayList param2 = new ArrayList();
        NumericLiteral nm = new NumericLiteral();
        nm.setValue(0);
        param2.add(nm);
        el1.setList(param2);
        ce.setParameters(el1);
        bigDecimalInit.setExpression(ce);
        ret.setInit(bigDecimalInit);

        String varToRet = getResumeVarName();
        ArrayList returnStatments = new ArrayList(7);
        VarDeclarationStatement vds = new VarDeclarationStatement();
        TypeClause tc = new TypeClause("bridgeHandler");
        vds.setType(tc);
        vds.setName("bh");

        //init
        AssignExpression assignInitBh = new AssignExpression();
        BinaryExpression be = new BinaryExpression();
        be.setLeftExpression(new VarLiteral("this"));
        be.setOperator(JavaTokenTypes.DOT);
        be.setRightExpression(new VarLiteral("getParent"));
        MethodInvocation miParent = new MethodInvocation();
        miParent.setInvocation(be);
        
        BinaryExpression beBridge = new BinaryExpression();
        beBridge.setLeftExpression(miParent);
        beBridge.setOperator(JavaTokenTypes.DOT);
        beBridge.setRightExpression(new VarLiteral("getBridge"));
        
        MethodInvocation mi = new MethodInvocation();
        mi.setInvocation(beBridge);

        EList el = new EList();
        ArrayList param = new ArrayList();
        param.add(new StringLiteral(nameOfBridge));
        el.setList(param);
        mi.setParameters(el);
        assignInitBh.setExpression(mi);
        vds.setInit(assignInitBh);

        //methodInvocation
        MethodInvocation bfFirst = new MethodInvocation();
        BinaryExpression bh_bf = new BinaryExpression();
        bh_bf.setLeftExpression(new VarLiteral("bh"));
        bh_bf.setOperator(JavaTokenTypes.DOT);
        bh_bf.setRightExpression(new VarLiteral("beforeFirst"));
        bfFirst.setInvocation(bh_bf);

        //if(!bh.haveVL)
        //        IfStatement ifstat = new IfStatement();
        //        BinaryExpression haveVl = new BinaryExpression();
        //        haveVl.setLeftExpression(new VarLiteral("bh"));
        //        haveVl.setOperator(JavaTokenTypes.DOT);
        //        haveVl.setRightExpression(new VarLiteral("haveVL"));
        //        MethodInvocation mhave = new MethodInvocation();
        //        mhave.setInvocation(haveVl);
        //
        //        UnaryExpression un = new UnaryExpression();
        //        un.setOperator(JavaTokenTypes.LNOT);
        //        un.setExpression(mhave);
        //        ifstat.setCondition(un);
        //then block
        //while(bh.next)
        WhileStatement whilStat = new WhileStatement();
        BinaryExpression haveNext = new BinaryExpression();
        haveNext.setLeftExpression(new VarLiteral("bh"));
        haveNext.setOperator(JavaTokenTypes.DOT);
        haveNext.setRightExpression(new VarLiteral("next"));

        MethodInvocation mhaveNext = new MethodInvocation();
        mhaveNext.setInvocation(haveNext);
        whilStat.setCondition(mhaveNext);

        //block for while
        BlockStatement whileBlock = new BlockStatement();
        ArrayList blockWhileExp = new ArrayList();
        BinaryExpression bRetAss = new BinaryExpression();
        bRetAss.setLeftExpression(new VarLiteral(varToRetName));
        bRetAss.setOperator(JavaTokenTypes.ASSIGN);

        BinaryExpression netBoCalc = getNetgestBoUtils("Calculate");

        BinaryExpression netBoCalcType = new BinaryExpression();
        netBoCalcType.setLeftExpression(netBoCalc);
        netBoCalcType.setOperator(JavaTokenTypes.DOT);
        netBoCalcType.setRightExpression(new VarLiteral(op));

        MethodInvocation retAdd = new MethodInvocation();
        retAdd.setInvocation(netBoCalcType);

        ArrayList pAdd = new ArrayList();
        pAdd.add(new VarLiteral(varToRetName));
        pAdd.add(changeToBH(exp));

        EList pAddElist = new EList();
        pAddElist.setList(pAdd);
        retAdd.setParameters(pAddElist);

        bRetAss.setRightExpression(retAdd);
        blockWhileExp.add(new ExpressionStatement(bRetAss));
        whileBlock.setStatements(blockWhileExp);
        whilStat.setBody(whileBlock);

        //        ifstat.setThenStatement(whilStat);
        //        ReturnStatement retValue = new ReturnStatement(new VarLiteral("ret"));
        returnStatments.add(vds);
        returnStatments.add(new ExpressionStatement(bfFirst));

        //returnStatments.add(ifstat);
        returnStatments.add(whilStat);

        //        returnStatments.add(retValue);
        BlockStatement blockMiddle = new BlockStatement(returnStatments);

        ArrayList upp = new ArrayList();
        upp.add(ret);
        upp.add(blockMiddle);

        BlockStatement blockReturn = new BlockStatement(upp);

        return blockReturn;
    }

    private ExpressionSymbol changeToBH(ExpressionSymbol b)
    {
        MethodInvocation mth;

        //this.getParent().getAttribute("xxx").getObject() -> bh
        //this.getParent().getAttribute("xxx") -> bh
        if (b instanceof MethodInvocation)
        {
            mth = (MethodInvocation) b;

            if (isCalculate(mth))
            {
                EList p = (EList) mth.getParameters();
                ArrayList l = p.getList();

                for (int i = 0; i < l.size(); i++)
                {
                    l.set(i, removeThisParent((ExpressionSymbol) l.get(i)));
                }

                p.setList(l);
                mth.setParameters(p);

                return mth;
            }
            else
            {
                return removeThisParent(b);
            }
        }

        return b;
    }

    private ExpressionSymbol removeThisParent(ExpressionSymbol exp)
    {
        if (exp instanceof BinaryExpression)
        {
            BinaryExpression be = (BinaryExpression) exp;
            be.setLeftExpression(removeThisParent(be.getLeftExpression()));
            be.setRightExpression(removeThisParent(be.getRightExpression()));
        }
        else if (exp instanceof MethodInvocation)
        {
            MethodInvocation meth = (MethodInvocation) exp;

            if (meth.getInvocation() instanceof BinaryExpression)
            {
                BinaryExpression be = (BinaryExpression) meth.getInvocation();

                if (be.getRightExpression() instanceof VarLiteral &&
                        "getAttribute".equals(
                            ((VarLiteral) be.getRightExpression()).getValue()))
                {
                    if (be.getLeftExpression() instanceof MethodInvocation)
                    {
                        MethodInvocation methLeftExp = (MethodInvocation) be.getLeftExpression();

                        if (methLeftExp.getInvocation() instanceof BinaryExpression)
                        {
                            BinaryExpression beThis = (BinaryExpression) methLeftExp.getInvocation();

                            if (beThis.getLeftExpression() instanceof VarLiteral &&
                                    beThis.getRightExpression() instanceof VarLiteral &&
                                    "this".equals(
                                        ((VarLiteral) beThis.getLeftExpression()).getValue()) &&
                                    "getParent".equals(
                                        ((VarLiteral) beThis.getRightExpression()).getValue()))
                            {
                                return new VarLiteral("bh");
                            }
                        }
                    }
                }

                be.setLeftExpression(removeThisParent(be.getLeftExpression()));
                be.setRightExpression(removeThisParent(be.getRightExpression()));

                return meth;
            }

            meth.setInvocation(removeThisParent(meth.getInvocation()));
        }

        return exp;
    }

    private boolean isCalculate(MethodInvocation mi)
    {
        if (mi.getInvocation() instanceof BinaryExpression)
        {
            BinaryExpression inv = (BinaryExpression) mi.getInvocation();

            if (inv.getLeftExpression() instanceof BinaryExpression)
            {
                BinaryExpression left;
                left = (BinaryExpression) inv.getLeftExpression();

                if (left.getRightExpression() instanceof VarLiteral)
                {
                    if ("Calculate".equals(
                                ((VarLiteral) left.getRightExpression()).getValue()))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private String removeDtCmpStr(String aux)
    {
        int lasPos = aux.length() - 1;
        boolean end = false;

        while ((lasPos > 0) && !end)
        {
            if (aux.charAt(lasPos) == 'y')
            {
                lasPos--;
            }
            else if (aux.charAt(lasPos) == 'M')
            {
                lasPos--;
            }
            else if (aux.charAt(lasPos) == 'd')
            {
                lasPos--;
            }
            else if (aux.charAt(lasPos) == 'h')
            {
                lasPos--;
            }
            else if (aux.charAt(lasPos) == 'm')
            {
                lasPos--;
            }
            else if (aux.charAt(lasPos) == 's')
            {
                lasPos--;
            }
            else
            {
                end = true;
            }
        }

        return aux.substring(0, lasPos + 1);
    }

    //---------------Method IDENTIFICATION
    private boolean isObjMethod(ArrayList path, int pos, boDefHandler classDef)
        throws boRuntimeException
    {
        String aux = (String) path.get(pos);

        if (path.size() == (pos + 1))
        {
            boDefMethod[] mtds = classDef.getBoMethods();

            for (int i = 0; i < mtds.length; i++)
            {
                if (aux.equals(mtds[i].getName()))
                {
                    return true;
                }
            }

            return false;
        }

        if (aux.startsWith("parent"))
        {
            if (aux.startsWith("parent_"))
            {
                String className = aux.substring(7, aux.length());

                return isObjMethod(path, pos + 1,
                    boDefHandler.getBoDefinition(className));
            }
            else
            {
                return isObjMethod(path, pos + 1, classDef);
            }
        }
        else if (aux.startsWith("DATE_"))
        {
            return false;
        }
        else
        {
            boDefAttribute[] attrs = classDef.getAttributesDef();

            if (attrs != null)
            {
                boDefAttribute attr = null;

                for (int j = 0; j < attrs.length; j++)
                {
                    attr = attrs[j];

                    if (aux.equals(attr.getName()))
                    {
                        if (attr.getMaxOccurs() > 1)
                        {
                            //bridge
                            pos++;

                            String aux2 = (String) path.get(pos);
                            boDefAttribute[] bridgeAttr = attr.getBridge()
                                                              .getBoAttributes();

                            if ((bridgeAttr != null) ||
                                    (bridgeAttr.length > 0))
                            {
                                for (int i = 0; i < bridgeAttr.length; i++)
                                {
                                    if (aux2.equals(bridgeAttr[i].getName()))
                                    {
                                        int ind;
                                        String objType = bridgeAttr[i].getType();

                                        if ((ind = objType.indexOf("object.")) != -1)
                                        {
                                            objType = objType.substring(ind,
                                                    objType.length());
                                        }

                                        return isObjMethod(path, pos,
                                            boDefHandler.getBoDefinition(
                                                objType));
                                    }
                                }
                            }

                            pos--;
                        }

                        //atributo
                        String objType = attr.getType();

                        if ((objType == null) || "".equals(objType))
                        {
                            throw new boRuntimeException("XEORecognizer",
                                "Erro ao verificar o attributo: " +
                                thisAttribute.getName() + " do objecto: " +
                                bodef.getBoName(), new Exception());
                        }
                        else
                        {
                            int ind;

                            if ((ind = objType.indexOf("object.")) != -1)
                            {
                                objType = objType.substring(ind + 7,
                                        objType.length());
                            }

                            return isObjMethod(path, pos + 1,
                                boDefHandler.getBoDefinition(objType));
                        }
                    }
                }
            }
            else
            {
                throw new boRuntimeException("XEORecognizer",
                    "Erro ao verificar o attributo: " +
                    thisAttribute.getName() + " do objecto: " +
                    bodef.getBoName(), new Exception());
            }
        }

        return false;
    }

    private int methodAppTill(int i, ArrayList statements)
    {
        String aux;

        for (; i < statements.size(); i++)
        {
            if (statements.get(i) instanceof String)
            {
                aux = (String) statements.get(i);

                if ((aux.charAt(0) == ' ') || (aux.charAt(0) == '+') ||
                        (aux.charAt(0) == '-') || (aux.charAt(0) == '(') ||
                        (aux.charAt(0) == ')') || (aux.charAt(0) == '\'') ||
                        (aux.charAt(0) == '\"') || (aux.charAt(0) == '{') ||
                        (aux.charAt(0) == '=') || (aux.charAt(0) == '&') ||
                        (aux.charAt(0) == '|') || (aux.charAt(0) == '>') ||
                        (aux.charAt(0) == '<') || (aux.charAt(0) == '!') ||
                        (aux.charAt(0) == '*') || (aux.charAt(0) == '/') ||
                        (aux.charAt(0) == ';') || (aux.charAt(0) == '{') ||
                        (aux.charAt(0) == '}') || (aux.charAt(0) == ','))
                {
                    return i;
                }
            }
        }

        return statements.size();
    }

    private static BinaryExpression getNetgestBoUtils(String className)
    {
        BinaryExpression netBo = new BinaryExpression();
        netBo.setLeftExpression(new VarLiteral("netgest"));
        netBo.setOperator(JavaTokenTypes.DOT);
        netBo.setRightExpression(new VarLiteral("bo"));

        BinaryExpression netBoUtl = new BinaryExpression();
        netBoUtl.setLeftExpression(netBo);
        netBoUtl.setOperator(JavaTokenTypes.DOT);
        netBoUtl.setRightExpression(new VarLiteral("utils"));

        BinaryExpression netBoCalc = new BinaryExpression();
        netBoCalc.setLeftExpression(netBoUtl);
        netBoCalc.setOperator(JavaTokenTypes.DOT);
        netBoCalc.setRightExpression(new VarLiteral(className));

        return netBoCalc;
    }
     private static BinaryExpression getNetgestUtils(String className)
    {
        BinaryExpression netBo = new BinaryExpression();
        netBo.setLeftExpression(new VarLiteral("netgest"));
        netBo.setOperator(JavaTokenTypes.DOT);
        netBo.setRightExpression(new VarLiteral("utils"));

        
        BinaryExpression netBoCalc = new BinaryExpression();
        netBoCalc.setLeftExpression(netBo);
        netBoCalc.setOperator(JavaTokenTypes.DOT);
        netBoCalc.setRightExpression(new VarLiteral(className));

        return netBoCalc;
    }

    private static String getString(ArrayList r)
    {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < r.size(); i++)
        {
            sb.append(r.get(i));

            if ((i + 1) < r.size())
            {
                sb.append(".");
            }
        }

        return sb.toString();
    }
}
