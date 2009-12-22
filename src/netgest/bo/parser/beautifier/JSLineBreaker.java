/*Enconding=UTF-8*/
package netgest.bo.parser.beautifier;

import java.util.*;
import netgest.bo.system.Logger;

class JSLineBreaker
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.parser.beautifier.JSLineBreaker");

    private static final int BEFORE = 0;
    private static final int AFTER = 1;
    private static Hashtable prefTable;

    static
    {
        prefTable = new Hashtable();
        prefTable.put("()", new Integer(80 /*35*/)); // () appears here so that it will not be cut in the middle...

        prefTable.put("().", new Integer(90)); // () appears here so that it will not be cut in the middle...
        prefTable.put(").", new Integer(90)); // () appears here so that it will not be cut in the middle...

        prefTable.put("(", new Integer(80)); //<--
        prefTable.put(")", new Integer(80));
        prefTable.put("[", new Integer(80));
        prefTable.put("]", new Integer(80));
        prefTable.put(",", new Integer(10));
        prefTable.put(";", new Integer(5));

        prefTable.put("=", new Integer(20));
        prefTable.put("+=", new Integer(20));
        prefTable.put("-=", new Integer(20));
        prefTable.put("*=", new Integer(20));
        prefTable.put("/=", new Integer(20));
        prefTable.put("|=", new Integer(20));
        prefTable.put("&=", new Integer(20));
        prefTable.put("^=", new Integer(20));

        prefTable.put("?", new Integer(25));
        prefTable.put(":", new Integer(25));

        prefTable.put("||", new Integer(30));
        prefTable.put("&&", new Integer(30));

        prefTable.put("==", new Integer(40));
        prefTable.put("!=", new Integer(40));
        prefTable.put(">=", new Integer(40));
        prefTable.put("<=", new Integer(40));
        prefTable.put(">", new Integer(40));
        prefTable.put("<", new Integer(40));

        prefTable.put("+", new Integer(50));
        prefTable.put("-", new Integer(50));
        prefTable.put("*", new Integer(60));
        prefTable.put("/", new Integer(60));
        prefTable.put("%", new Integer(60));

        //prefTable.put("!", new Integer(70));
        prefTable.put("&", new Integer(70));
        prefTable.put("|", new Integer(70));
        prefTable.put("^", new Integer(70));
    }

    private String[] prefs = 
    {
        "().", "()", ").", "+=", "-=", "*=", "/=", "%=", "^=", "||", "&&", "==",
        "!=", ">=", "<=", "(", ")", "[", "]", "?", ":", ",", ";", "=", "<", ">",
        "+", "-", "*", "/", "&", "|", /*"!",*/
        "^"
    };
    private Vector brokenLineVector;
    private StringBuffer wsBuffer;
    private char quoteChar;
    private boolean isInQuote;
    private boolean isInComment;
    private boolean isNestedConnection = true;
    private boolean isCut;
    private boolean isLineComment; // true when the current character is in a // comment (such as this line ...)
    private int parenDepth;
    private int breakDepth;
    private int preferredLineLength = 70;
    private int lineLengthDeviation = 5;
    private LineBreak previousLineBreak = null;

    JSLineBreaker()
    {
        init();
    }

    void init()
    {
        brokenLineVector = new Vector();

        parenDepth = 0;
        breakDepth = 0; // <------ 2

        isInQuote = false;
        isInComment = false;
        isCut = false;
        isLineComment = false;
        wsBuffer = new StringBuffer();
    }

    void setPreferredLineLength(int length)
    {
        preferredLineLength = length;
    }

    void setLineLengthDeviation(int dev)
    {
        lineLengthDeviation = dev;
    }

    void setNestedConnection(boolean nest)
    {
        isNestedConnection = nest;
    }

    void breakLine(String line)
    {
        StringBuffer outBuffer = new StringBuffer();
        Stack lineBreakStack = new Stack();
        String previousAfterCut = "";
        boolean isSpecialChar = false;
        char ch = ' '; // the current character
        char prevCh = 0;
        int i;
        int ws;
        int regBreak = 0;
        int wsBreak = 0;
        int chosenBreak = 0;
        int BufLength;
        int bufferStart = 0;

        if (line.trim().length() == 0)
        {
            brokenLineVector.addElement("");

            return;
        }

        ch = line.charAt(0);
        ws = 0;

        if (!isLineComment)
        {
            isCut = false;
        }

        isLineComment = false;

        if (!isCut)
        {
            wsBuffer = new StringBuffer();

            while (((ch == ' ') || (ch == '\t')) && (ws < (line.length() - 1)))
            {
                wsBuffer.append(ch);
                ch = line.charAt(++ws);
            }
        }

        // parse characters in the current line.
        for (i = ws; i < line.length(); i++)
        {
            if ((ch != ' ') && (ch != '\t'))
            {
                prevCh = ch;
            }

            ch = line.charAt(i);

            // handle special characters (i.e. backslash+character such as \n, \t, ...)
            if (isSpecialChar)
            {
                outBuffer.append(ch);
                isSpecialChar = false;

                continue;
            }

            if (!(isInComment || isLineComment) &&
                    line.regionMatches(false, i, "\\\\", 0, 2))
            {
                outBuffer.append("\\\\");
                i++;

                continue;
            }

            if (!(isInComment || isLineComment) && (ch == '\\'))
            {
                outBuffer.append(ch);
                isSpecialChar = true;

                continue;
            }

            // handle comments
            if (!isInQuote && !(isInComment || isLineComment) &&
                    line.regionMatches(false, i, "//", 0, 2))
            {
                isLineComment = true;
                outBuffer.append("//");
                i++;

                continue;
            }
            else if (!isInQuote && !(isInComment || isLineComment) &&
                    line.regionMatches(false, i, "/*", 0, 2))
            {
                isInComment = true;
                outBuffer.append("/*");
                i++;

                continue;
            }
            else if (!isInQuote && (isInComment || isLineComment) &&
                    line.regionMatches(false, i, "*/", 0, 2))
            {
                isInComment = false;
                outBuffer.append("*/");
                i++;

                continue;
            }

            if (isInComment || isLineComment)
            {
                outBuffer.append(ch);

                continue;
            }

            // handle quotes (such as 'x' and "Hello Dolly")
            if ((ch == '"') || (ch == '\''))
            {
                if (!isInQuote)
                {
                    quoteChar = ch;
                    isInQuote = true;
                }
                else if (quoteChar == ch)
                {
                    isInQuote = false;
                    outBuffer.append(ch);

                    continue;
                }
            }

            if (isInQuote)
            {
                outBuffer.append(ch);

                continue;
            }

            outBuffer.append(ch);

            for (int p = 0; p < prefs.length; p++)
            {
                String key = (String) prefs[p];

                if (line.regionMatches(false, i, key, 0, key.length()))
                {
                    int breakType = AFTER;

                    if ((ch == '(') || (ch == '[') || (ch == ')') ||
                            (ch == ']'))
                    {
                        if ("(".equals(key) /*ch == '('*/ || (ch == '['))
                        {
                            parenDepth++;
                        }
                        else if ( /*")".equals(key)*/
                            (ch == ')') || (ch == ']'))
                        {
                            parenDepth--;
                        }

                        breakDepth = parenDepth;

                        if ((ch == ')') || (ch == ']') || key.startsWith("()"))
                        {
                            breakDepth++;
                        }

                        if ((ch == '(') || (ch == '['))
                        {
                            if (((prevCh >= 'a') && (prevCh <= 'z')) ||
                                    ((prevCh >= 'A') && (prevCh <= 'Z')) ||
                                    ((prevCh >= '0') && (prevCh <= '9')) ||
                                    (prevCh == '.'))
                            {
                                breakType = AFTER;
                            }
                            else
                            {
                                breakType = BEFORE;
                            }
                        }
                        else
                        {
                            breakType = AFTER;
                        }
                    }

                    if (key.length() > 1)
                    {
                        outBuffer.append(key.substring(1));
                        i += (key.length() - 1);
                    }

                    registerLineBreak(lineBreakStack,
                        new LineBreak(key, outBuffer.length() + bufferStart,
                            breakDepth, breakType));

                    breakDepth = parenDepth;

                    break;
                }
            }

            int bufLength = outBuffer.length() + wsBuffer.length() +
                previousAfterCut.length() + (isCut ? 8 : 0);

            LineBreak curBreak = null;

            if ((bufLength > preferredLineLength) &&
                    (i < (line.length() - lineLengthDeviation)))
            {
                while (!lineBreakStack.isEmpty())
                {
                    curBreak = (LineBreak) lineBreakStack.elementAt(0);

                    if ((curBreak.breakWhere - bufferStart) < 1) // <-----
                    {
                        curBreak = null;
                        lineBreakStack.removeElementAt(0);
                    }
                    else
                    {
                        break;
                    }
                }

                if (curBreak != null)
                {
                    lineBreakStack.removeElementAt(0);
                }
            }

            if (curBreak != null) // in future, think of: && line.length()>i+10) (that was used in the past...)
            {
                int cutWhere = curBreak.breakWhere - bufferStart -
                    ((curBreak.breakType == BEFORE)
                    ? curBreak.breakStr.length() : 0);

                if (cutWhere < 8)
                {
                    continue;
                }

                StringBuffer brokenLineBuffer = new StringBuffer();
                String outString = outBuffer.toString();
                String beforeCut = outString.substring(0, cutWhere);

                //brokenLineBuffer.append(wsBuffer);
                /*
                if (isCut)
                    brokenLineBuffer.append("        ");
                brokenLineBuffer.append(beforeCut);
                brokenLineVector.addElement(brokenLineBuffer.toString());
                */
                brokenLineBuffer.append(beforeCut);
                addBrokenLine(wsBuffer.toString(), brokenLineBuffer.toString(),
                    curBreak, breakDepth, isCut);

                //previousAfterCut = outString.substring(cutWhere);
                bufferStart += cutWhere;
                outBuffer = new StringBuffer(outString.substring(cutWhere));

                //lineBreakStack = new Stack();
                isCut = true;
            }
        }

        // at end of line:
        StringBuffer brokenLineBuffer = new StringBuffer();

        //brokenLineBuffer.append(wsBuffer);

        /*
        if (isCut)
            brokenLineBuffer.append("        ");
        brokenLineBuffer.append(outBuffer);
        brokenLineVector.addElement(brokenLineBuffer.toString());
        */
        brokenLineBuffer.append(outBuffer);
        addBrokenLine(wsBuffer.toString(), brokenLineBuffer.toString(), null,
            breakDepth, isCut);
    }

    private void registerLineBreak(Stack lineBreakStack, LineBreak newBreak)
    {
        LineBreak lastBreak;

        while (!lineBreakStack.isEmpty())
        {
            lastBreak = (LineBreak) lineBreakStack.peek();

            if (compare(lastBreak, newBreak) < 0)
            {
                lineBreakStack.pop();
            }
            else
            {
                break;
            }
        }

        lineBreakStack.push(newBreak);

    }

    private void addBrokenLine(String whiteSpace, String brokenLine,
        LineBreak lineBreak, int breakDepth, boolean isCut)
    {
        boolean isLineAppended = false;

        brokenLine = brokenLine.trim();

        if (previousLineBreak != null) //&& brokenLine.length() > 0)
        {
            String previousBrokenLine = (String) brokenLineVector.lastElement();

            if (((brokenLine.length() + previousBrokenLine.length()) <= (preferredLineLength +
                    lineLengthDeviation)) || brokenLine.startsWith("{"))
            {
                if ((lineBreak == null) ||
                        (isNestedConnection &&
                        !",".equals(previousLineBreak.breakStr)) ||
                        (lineBreak.breakDepth < previousLineBreak.breakDepth) || //dfds
                        ((lineBreak.breakDepth == previousLineBreak.breakDepth) &&
                        ((!isNestedConnection &&
                        !",".equals(previousLineBreak.breakStr)) ||
                        ",".equals(lineBreak.breakStr) ||
                        ";".equals(lineBreak.breakStr) ||
                        ")".equals(lineBreak.breakStr) ||
                        "]".equals(lineBreak.breakStr))))
                {
                    brokenLineVector.setElementAt((previousBrokenLine + " " +
                        brokenLine), brokenLineVector.size() - 1);
                    isLineAppended = true;
                }
            }
        }

        if (!isLineAppended)
        {
            if (isCut &&
                    !((previousLineBreak != null) &&
                    ",".equals(previousLineBreak.breakStr) &&
                    (previousLineBreak.breakDepth == 0)))
            {
                brokenLine = "        " + brokenLine;
            }

            brokenLine = whiteSpace + brokenLine;
            brokenLineVector.addElement(brokenLine);
        }

        previousLineBreak = lineBreak;
    }

    private int compare(LineBreak br1, LineBreak br2)
    {
        if (br1.breakDepth < br2.breakDepth)
        {
            return 1;
        }
        else if (br1.breakDepth > br2.breakDepth)
        {
            return -1;
        }

        int ord1 = ((Integer) prefTable.get(br1.breakStr)).intValue();
        int ord2 = ((Integer) prefTable.get(br2.breakStr)).intValue();

        if (ord1 < ord2)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }

    boolean hasMoreBrokenLines()
    {
        return brokenLineVector.size() > 0;
    }

    String nextBrokenLine()
    {
        String nextLine;

        if (hasMoreBrokenLines())
        {
            nextLine = (String) brokenLineVector.firstElement();
            brokenLineVector.removeElementAt(0);
        }
        else
        {
            return nextLine = "";
        }

        return nextLine;
    }

    class LineBreak
    {
        String breakStr;
        int breakWhere;
        int breakDepth;
        int breakType;

        LineBreak(String str, int wh, int dp, int tp)
        {
            breakStr = str;
            breakWhere = wh;
            breakDepth = dp;
            breakType = tp;
        }

        void dump()
        {
            logger.finer("LB: str=" + breakStr + " wh=" + breakWhere +
                " dep=" + breakDepth + " tp=" + breakType);
        }
    }
}
