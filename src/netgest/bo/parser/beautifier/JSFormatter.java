/*Enconding=UTF-8*/
package netgest.bo.parser.beautifier;

import java.io.*;

import java.util.*;
import org.apache.log4j.Logger;


public class JSFormatter
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.parser.beautifier.JSFormatter");
    
    // headers[] - an array of headers that require indentation
    private static String[] headers = 
    {
        "if", "else", "for", "while", "do", "try", "catch", "synchronized",
        "switch", "static"
    };

    // parenHeaders[] - an array of the headers that require parenthesies after them, i.e. while (...)
    private static String[] parenHeaders = 
    {
        "if", "for", "while", "catch", "synchronized", "switch"
    };
    private static String[] nonParenHeaders = { "else", "do", "try", "static" };
    private static String[] statementHeaders = { "class", "interface", "throws" };
    private static String[] longOperators = 
    {
        "==", "!=", ">=", "<=", "+=", "-=", "*=", "/=", "%=", "^=", "|=", "&=",
        "++", "--", "&&", "||", ".*"
    };
    private static Hashtable closingHeaders;

    static
    {
        closingHeaders = new Hashtable();
        closingHeaders.put("if", "else");
        closingHeaders.put("do", "while");
        closingHeaders.put("try", "catch");
    }

    public JSBeautifier beautifier;
    private JSLineBreaker lineBreaker;
    private StringBuffer outBuffer; // current parsed-line buffer
    private String tempLine = ""; // parts of previous line that haven't been parsed yet
    private Stack openingStack;
    private Stack parenDepthsStack;
    private Stack bracketBlockStateStack;
    private char quoteChar;
    private int parenDepth;
    private int leadingWhiteSpaces;
    private String currentHeader;
    private boolean isInHeader;
    private boolean isSpecialChar; // true if a char of type '\X' (i.e. '\n'...)
    private boolean isInQuote; // true if in quote
    private boolean isInComment; // true if in /**/ comment
    private boolean isBlockNeeded; // ---not currently used.
    private boolean isSpecialBlock; // ---not currently used.
    private boolean isCloseSpecialBlock; // ---not currently used.
    private boolean isParenNeeded; // true if a parenthesis statement is expected (i.e. right after a 'while' header...)
    private boolean isNewLineNeeded; // true if current formatted line has reached its end.
    private boolean checkBlockOpen; // hint for  checking if a '{' has been reached
    private boolean checkBlockClose; // hint for  checking if a '}' has been reached
    private boolean checkIf;
    private boolean checkClosingHeader;
    private boolean foundOrigLineBreak;
    private boolean isInQuestion;
    private boolean isSummarized;
    private boolean isInBracketOpen;
    private boolean isInBracketClose;
    private boolean isInClassStatement;
    private boolean bracketBreak = false;
    private char prevNonSpaceCh;
    private char currentNonSpaceCh;

    /**
     * Constructor for JSFormatter
     */
    public JSFormatter()
    {
        beautifier = new JSBeautifier();
        lineBreaker = new JSLineBreaker();
        init();
    }

    public static void main(String[] args)
    {
        JSFormatter formatter = new JSFormatter();
        Vector fileNameVector = new Vector();
        BufferedReader inReader = null;
        PrintWriter outWriter = null;
        boolean isHelpShown = false;

        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];

            if (arg.equals("-b"))
            {
                formatter.setBracketBreak(true);
            }
            else if ("-ib".equals(arg))
            {
                formatter.setBracketBreak(true);
                formatter.setBracketIndent(true);
            }
            else if ("-fs".equals(arg))
            {
                formatter.setSwitchIndent(false);
            }
            else if (arg.startsWith("-ll"))
            {
                int length = 70;

                try
                {
                    length = Integer.valueOf(arg.substring(3)).intValue();
                }
                catch (NumberFormatException e)
                {
                }

                ;
                formatter.setPreferredLineLength(length);
            }
            else if (arg.startsWith("-ld"))
            {
                int dev = 5;

                try
                {
                    dev = Integer.valueOf(arg.substring(3)).intValue();
                }
                catch (NumberFormatException e)
                {
                }

                ;
                formatter.setLineLengthDeviation(dev);
            }
            else if (arg.equals("-nn"))
            {
                formatter.setNestedConnection(false);
            }
            else if (arg.startsWith("-") && !isHelpShown)
            {
                isHelpShown = true;

                logger.error("");
                logger.error(
                    "Usage  : java jstyle.JSFormatter [options] < Original.java > Formatted.java");
                logger.error(
                    "         java jstyle.JSFormatter [options] Foo.java Bar.java  [...]");
                logger.error("");
                logger.error(
                    "When given a specific file, JSFormatter will create an output file with a");
                logger.error(
                    "suffix of \".js\" added to the original filename, i.e: Foo.java --> Foo.java.js");
                logger.error("");
                logger.error(
                    "Options: -ll#  Set preferred line length to #");
                logger.error(
                    "         -ld#  Set preferred upper line length deviation to #");
                logger.error(
                    "         -b    Break lines BEFORE '{' brackets (ala C++ style)");
                logger.error(
                    "         -ib   Same as '-b', but add extra indentation to brackets");
                logger.error(
                    "         -fs   flush (i.e. don't indent) 'switch' blocks");
                logger.error("         -h    Print this help message");
                System.exit(0);
            }
            else
            {
                fileNameVector.addElement(arg);
            }
        }

        if (fileNameVector.isEmpty())
        {
            inReader = new BufferedReader(new InputStreamReader(System.in));
            outWriter = new PrintWriter(System.out);

            try
            {
                formatter.format(inReader, outWriter);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                inReader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            outWriter.close();
        }
        else
        {
            for (int i = 0; i < fileNameVector.size(); i++)
            {
                try
                {
                    String fileName = (String) fileNameVector.elementAt(i);
                    inReader = new BufferedReader(new FileReader(fileName));
                    outWriter = new PrintWriter(new FileWriter(fileName +
                                ".js"), true);

                    formatter.format(inReader, outWriter);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                outWriter.close();

                try
                {
                    inReader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
      * Initialize the formatter so that it is ready for the formation
      * of a new file of source code.
      */
    public void init()
    {
        beautifier.init();
        lineBreaker.init();

        outBuffer = new StringBuffer();
        openingStack = new Stack();
        parenDepthsStack = new Stack();
        bracketBlockStateStack = new Stack();
        bracketBlockStateStack.push(new Boolean(true));
        tempLine = "";
        parenDepth = 0;
        isSpecialChar = false;
        isInQuote = false;
        isInComment = false;
        isBlockNeeded = false;
        isParenNeeded = false;
        isSpecialBlock = false;
        isCloseSpecialBlock = false;
        isNewLineNeeded = false;
        checkIf = false;
        checkBlockOpen = false;
        checkClosingHeader = false;
        checkBlockClose = false;
        foundOrigLineBreak = false;
        isInQuestion = false;
        isSummarized = false;
        isInBracketOpen = false;
        isInBracketClose = false;
        leadingWhiteSpaces = 0;
        isInHeader = false;
        isInClassStatement = false;

        prevNonSpaceCh = '{';
        currentNonSpaceCh = '{';
    }

    /**
      * Format source code that is read from inReader, and print the
      * formatted result to outWriter.
      *
      * @param      inReader     a BufferedReader from which to input original source code
      * @param      outWriter    a PrintWriter to output beutified source code to
      *
      * @exception  IOException
      */
    public void format(BufferedReader inReader, PrintWriter outWriter)
        throws IOException
    {
        String line = null;

        init();

        try
        {
            while (true)
            {
                while (!hasMoreFormattedLines())
                {
                    line = inReader.readLine();

                    if (line == null)
                    {
                        throw new NullPointerException();
                    }

                    formatLine(line);
                }

                while (hasMoreFormattedLines())
                    outWriter.println(nextFormattedLine());
            }
        }
        catch (NullPointerException e)
        {
        }

        summarize();

        while (hasMoreFormattedLines())
            outWriter.println(nextFormattedLine());
    }

    /**
      * Check if the formatter has more formatted lines to return.
      *
      * As long as there are more formatted lines to return, the
      * caller should NOT call the method formatLine() with a new
      * line of source code, but rather collect the current available
      * formatted lines with the method nextFormattedLine(), i.e:
      *
      * while (formatter.hasMoreFormattedLines())
      *     logger.debug(formatter.nextFormattedLine()
      */
    public boolean hasMoreFormattedLines()
    {
        if (lineBreaker.hasMoreBrokenLines())
        {
            return true;
        }
        else
        {
            while ((!isSummarized && !isNewLineRequested()) ||
                    (isSummarized && hasMoreSummarizedLines()))
            {
                String formatResult = format(null);

                if (formatResult != null)
                {
                    lineBreaker.breakLine(formatResult);

                    return true;
                }
            }
        }

        return false;
    }

    /**
      * format a line of source code. formatLine should NOT be called
      * if there are still formatted lines ready to be collected.
      * This can be checked with the method hasMoreFormattedLines()
      *
      * @param      line       a line of source code to be formatted.
      */
    public void formatLine(String line)
    {
        String formatResult = format(line);

        if (formatResult != null)
        {
            lineBreaker.breakLine(formatResult);
        }
    }

    /**
      * Get the next formatted line. This should be called ONLY after
      * checking with the method hasMoreFormattedLines() that there
      * actualy is a formatted line ready to be collected.
      */
    public String nextFormattedLine()
    {
        return lineBreaker.nextBrokenLine();
    }

    /**
      * summarize() is to be called when there are no more lines
      * of unformatted source code to be passed to the formatter.
      */
    public void summarize()
    {
        formatLine("");
        isSummarized = true;
    }

    public void setBracketBreak(boolean br)
    {
        bracketBreak = br;
    }

    public void setBracketIndent(boolean state)
    {
        beautifier.setBracketIndent(state);
    }

    public void setSwitchIndent(boolean state)
    {
        beautifier.setSwitchIndent(state);
    }

    public void setPreferredLineLength(int length)
    {
        lineBreaker.setPreferredLineLength(length);
    }

    public void setLineLengthDeviation(int dev)
    {
        lineBreaker.setLineLengthDeviation(dev);
    }

    public void setNestedConnection(boolean nest)
    {
        lineBreaker.setNestedConnection(nest);
    }

    /*
      * Does the formatter request a new line?
      * This should be checked only if we have a new line to
      * actually give the formatter via the format(line) method.
      */
    private boolean isNewLineRequested()
    {
        return ((tempLine.indexOf("//") == -1) &&
        (tempLine.indexOf("/*") == -1) && (tempLine.indexOf("*/") == -1));
    }

    /*
      * Does formatter have more formatted lines in its belly?
      * This should be called only after there are no more original
      * lines to send the formatter.
      * Until false, the new formatted lines can be retreived by
      * calling the format method with an empty string, i.e. format("");
      */
    private boolean hasMoreSummarizedLines()
    {
        return !((tempLine.length() == 0) ||
        ((tempLine.length() == 2) && (tempLine.charAt(0) == '\r') &&
        (tempLine.charAt(1) == '\n')));
    }

    /*
      * Format the original line sent.
      * Actually, the returned String is the next parsed line that is ready,
      * and may be a part of a formerly sent original line
      */
    public String format(String line)
    {
        boolean isLineComment = false; // true when the current character is in a // comment (such as this line ...)
        char ch = ' '; // the current character
        char prevCh = ' ';
        String outString = null;
        int i;
        boolean shouldPublish = false;
        boolean isBreakCalled = false;

        currentHeader = null;

        // connect new unparsed line to former unparsed line.
        if (line == null)
        {
            line = "";
        }
        else
        {
            // remove the white-space around the current line
            if (!isInComment)
            {
                leadingWhiteSpaces = 0;

                while ((leadingWhiteSpaces < line.length()) &&
                        ((line.charAt(leadingWhiteSpaces) == ' ') ||
                        (line.charAt(leadingWhiteSpaces) == '\t')))
                    leadingWhiteSpaces++;

                line = line.trim();
            }
            else
            {
                int trimSize;

                for (trimSize = 0;
                        (trimSize < line.length()) &&
                        (trimSize < leadingWhiteSpaces) &&
                        ((line.charAt(trimSize) == ' ') ||
                        (line.charAt(trimSize) == '\t')); trimSize++)
                    ;

                line = line.substring(trimSize);
            }

            //line = line.trim();
            if ("".equals(line))
            {
                line = "\n";
            }
        }

        line = tempLine + " \r" + line;

        // parse characters in the current line.
        for (i = 0; i < line.length(); i++)
        {
            prevCh = ch;

            ch = line.charAt(i);

            //shouldPublish = false;
            if (!isInComment && !isLineComment && (ch == '\t'))
            {
                ch = ' ';
            }

            // '\n' exists when an empty line has been sent
            if (ch == '\n')
            {
                /*if (checkClosingHeader)
                 {
                     isDoubleBreak = true;
                     //checkClosingHeader = false;
                 }
                 */

                //if (foundOrigLineBreak)
                //    foundOrigLineBreak = false;
                isBreakCalled = true;

                break;
            }

            // '\r' exists at the connection points between original lines
            if (ch == '\r')
            {
                ch = ' ';

                if (isBreakCalled)
                {
                    break;
                }
                else if (checkBlockClose)
                {
                    checkBlockClose = false;
                    isBreakCalled = true;

                    break;
                }
                else
                {
                    foundOrigLineBreak = true;

                    continue;
                }
            }

            if ((ch != ' ') && (ch != '\t') && !isInComment && !isLineComment &&
                    !isInQuote && !line.regionMatches(false, i, "//", 0, 2) &&
                    !line.regionMatches(false, i, "/*", 0, 2))
            {
                prevNonSpaceCh = currentNonSpaceCh;
                currentNonSpaceCh = ch;
            }

            // minimize white-space
            // and remove spaces that come right after parenthesies...
            if (!isInComment && !isLineComment && !isInQuote && (ch == ' '))
            {
                if ((currentNonSpaceCh != '(') && (currentNonSpaceCh != ')') &&
                        (currentNonSpaceCh != '[') &&
                        (currentNonSpaceCh != ']'))
                {
                    appendSpace(outBuffer);
                }

                continue;
            }

            //if (!isInComment && !isInQuote && (ch == ' ' || ch == '\t'))
            //{
            //    if (prevCh != ' ' && prevCh != '\t')
            //        outBuffer.append(ch);
            //    continue;
            //}
            shouldPublish = false; // called specifically AFTER white space is treated.

            if (checkBlockClose)
            {
                checkBlockClose = false;

                if (ch != '}')
                {
                    isBreakCalled = true;
                }
            }

            // handle comments
            if (!isInQuote && !(isInComment || isLineComment) &&
                    line.regionMatches(false, i, "//", 0, 2))
            {
                if (foundOrigLineBreak)
                {
                    foundOrigLineBreak = false;

                    if (checkClosingHeader)
                    {
                        checkClosingHeader = false;
                        i--;
                        isBreakCalled = true;

                        break;
                    }
                }

                isLineComment = true;
                checkClosingHeader = false;
                outBuffer.append("//");
                i++;

                continue;
            }
            else if (!isInQuote && !(isInComment || isLineComment) &&
                    line.regionMatches(false, i, "/*", 0, 2))
            {
                if (foundOrigLineBreak)
                {
                    foundOrigLineBreak = false;

                    if (checkClosingHeader)
                    {
                        checkClosingHeader = false;
                        i--;
                        isBreakCalled = true;

                        break;
                    }
                }

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
                shouldPublish = true;
                i++;

                continue;
            }

            if (isInComment || isLineComment)
            {
                outBuffer.append(ch);

                if (outBuffer.toString().regionMatches(false, 0, "/*", 0, 2))
                {
                    outBuffer.insert(0, ' ');
                }

                continue;
            }

            // if we have reached here, then we are NOT in a comment
            if (isInHeader)
            {
                isInHeader = false;
                currentHeader = (String) openingStack.peek();
            }
            else
            {
                currentHeader = null;
            }

            foundOrigLineBreak = false;

            if (isBreakCalled)
            {
                i--;

                break;
            }

            /**/
            if (checkClosingHeader)
            {
                checkClosingHeader = false;

                if (bracketBreak)
                {
                    if (ch != ';')
                    {
                        i--;
                        isBreakCalled = true;

                        break;
                    }
                    else
                    {
                        i--;

                        continue;
                    }
                }

                while (!"{".equals(openingStack.pop()))
                    ;

                if (!openingStack.isEmpty())
                {
                    String openingHeader = (String) openingStack.peek();
                    String closingHeader = (String) closingHeaders.get(openingHeader);
                    i--;

                    if ((closingHeader == null) ||
                            !line.regionMatches(false, i + 1, closingHeader, 0,
                                closingHeader.length()))
                    {
                        if (ch != ';')
                        {
                            outString = outBuffer.toString();
                            outBuffer.setLength(0);

                            break;
                        }
                        else
                        {
                            i++;
                        }
                    }
                    else
                    {
                        int lastBufCharPlace = outBuffer.length() - 1;

                        if ((lastBufCharPlace >= 0) &&
                                (outBuffer.charAt(lastBufCharPlace) != ' '))
                        {
                            appendSpace(outBuffer); //outBuffer.append(' ');
                        }

                        ch = ' ';
                        openingStack.pop(); // pop the opening header

                        continue;
                    }
                }
            }

            /**/
            if (checkIf)
            {
                checkIf = false;

                if (line.regionMatches(false, i, "if", 0, 2))
                {
                    isNewLineNeeded = false;
                }
            }

            if (!isParenNeeded && checkBlockOpen)
            {
                checkBlockOpen = false;

                if ((ch == '{') || "static".equals(currentHeader))
                {
                    isNewLineNeeded = false;
                }
            }

            if (isNewLineNeeded && !isParenNeeded)
            {
                isNewLineNeeded = false;
                i--;
                isBreakCalled = true;

                continue;
            }

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
                isSpecialChar = true;
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

            // handle parenthesies
            if ((ch == '(') || (ch == '[') || (ch == ')') || (ch == ']'))
            {
                if ((ch == '(') || (ch == '['))
                {
                    parenDepth++;
                }
                else if ((ch == ')') || (ch == ']'))
                {
                    parenDepth--;
                }

                if ((parenDepth == 0) && isParenNeeded)
                {
                    isParenNeeded = false;
                    checkBlockOpen = true;
                }

                //outBuffer.append(ch);
                //continue;
            }

            //don't do special parsing as long as parenthesies are open...

            /*
            if (parenDepth != 0)
            {
                outBuffer.append(ch);
                continue;
            }
            */
            /*
                        if (isNewLineNeeded && !isParenNeeded)
                        {
                            isNewLineNeeded = false;
                            i--;
                            isBreakCalled = true;
                            continue;
                        }
            */
            if (prevCh == ' ')
            {
                boolean foundHeader = false;

                for (int h = 0; h < headers.length; h++)
                {
                    if (line.regionMatches(false, i, headers[h], 0,
                                headers[h].length()))
                    {
                        int lineLength = line.length();
                        int headerEnd = i + headers[h].length();
                        char endCh = 0;

                        if (headerEnd < lineLength)
                        {
                            endCh = line.charAt(headerEnd);
                        }

                        if ((headerEnd > lineLength) ||
                                ((endCh >= 'a') && (endCh <= 'z')) ||
                                ((endCh >= 'A') && (endCh <= 'Z')) ||
                                ((endCh >= '0') && (endCh <= '9')))
                        {
                            break;
                        }

                        foundHeader = true;
                        outBuffer.append(headers[h]);
                        i += (headers[h].length() - 1);

                        if ("else".equals(headers[h]))
                        {
                            checkIf = true;
                        }

                        checkBlockOpen = true;
                        isNewLineNeeded = true;
                        isBlockNeeded = false;
                        openingStack.push(headers[h]);

                        appendSpace(outBuffer);
                        ; //outBuffer.append(' ');
                        ch = ' ';

                        int p;

                        for (p = 0; p < parenHeaders.length; p++)
                            if (headers[h].equals(parenHeaders[p]))
                            {
                                isParenNeeded = true;

                                break;
                            }

                        break;
                    }
                }

                if (foundHeader)
                {
                    isInHeader = true;

                    continue;
                }
            }

            if (ch == '?')
            {
                isInQuestion = true;
            }

            if (ch == ':') //isInCase)
            {
                if (isInQuestion)
                {
                    isInQuestion = false;
                }
                else
                {
                    //isInCase = false;
                    outBuffer.append(ch);
                    isBreakCalled = true;

                    continue;
                }
            }

            if ((ch == ';') && (parenDepth == 0))
            {
                outBuffer.append(ch);
                isBreakCalled = true;

                continue;
            }

            if (ch == '{')
            {
                if (!(bracketBreak && isInBracketOpen))
                {
                    boolean isBlockOpener = false;

                    // first, check if '{' is a block-opener or an static-array opener
                    isBlockOpener |= ((prevNonSpaceCh == '{') &&
                    ((Boolean) bracketBlockStateStack.peek()).booleanValue());

                    isBlockOpener |= ((prevNonSpaceCh == ')') ||
                    (prevNonSpaceCh == ';'));

                    isBlockOpener |= isInClassStatement;

                    isBlockOpener |= ((prevNonSpaceCh == ':') && !isInQuestion);

                    isInClassStatement = false;

                    if (!isBlockOpener && (currentHeader != null))
                    {
                        for (int n = 0; n < nonParenHeaders.length; n++)
                            if (currentHeader.equals(nonParenHeaders[n]))
                            {
                                isBlockOpener = true;

                                break;
                            }
                    }

                    bracketBlockStateStack.push(new Boolean(isBlockOpener));

                    if (!isBlockOpener)
                    {
                        outBuffer.append('{');

                        continue;
                    }
                }

                // if I have reached here, then I am in a block...
                if (bracketBreak)
                {
                    if (isInBracketOpen)
                    {
                        isInBracketOpen = false;
                    }
                    else
                    {
                        isInBracketOpen = true;
                        isBreakCalled = true;
                        i--;

                        break;
                    }
                }

                checkBlockClose = true;

                int lastBufCharPlace = outBuffer.length() - 1;

                if ((lastBufCharPlace >= 0) &&
                        (outBuffer.charAt(lastBufCharPlace) != ' '))
                {
                    appendSpace(outBuffer); //outBuffer.append(' ');
                }

                outBuffer.append('{');
                openingStack.push("{");

                //checkBlockClose = true;
                parenDepthsStack.push(new Integer(parenDepth));
                parenDepth = 0;

                continue;
            }

            if (ch == '}')
            {
                // first check if this '}' closes a previous block, or a static array...
                if (!((Boolean) bracketBlockStateStack.pop()).booleanValue())
                {
                    outBuffer.append(ch);

                    continue;
                }

                if (!parenDepthsStack.isEmpty())
                {
                    parenDepth = ((Integer) parenDepthsStack.pop()).intValue();
                }

                outBuffer.append(ch);
                checkClosingHeader = true;

                continue;
            }

            if (prevCh == ' ')
            {
                boolean foundHeader = false;

                for (int h = 0; h < statementHeaders.length; h++)
                {
                    if (line.regionMatches(false, i, statementHeaders[h], 0,
                                statementHeaders[h].length()))
                    {
                        int lineLength = line.length();
                        int headerEnd = i + statementHeaders[h].length();
                        char endCh = 0;

                        if (headerEnd < lineLength)
                        {
                            endCh = line.charAt(headerEnd);
                        }

                        if ((headerEnd > lineLength) ||
                                ((endCh >= 'a') && (endCh <= 'z')) ||
                                ((endCh >= 'A') && (endCh <= 'Z')) ||
                                ((endCh >= '0') && (endCh <= '9')))
                        {
                            break;
                        }

                        isInClassStatement = true;

                        break;
                    }
                }
            }

            if ((prevCh == ' ') &&
                    line.regionMatches(false, i, "return", 0, 6))
            {
                int lineLength = line.length();
                int headerEnd = i + 6;
                char endCh = 0;

                if (headerEnd < lineLength)
                {
                    endCh = line.charAt(headerEnd);
                }

                if (!((headerEnd > lineLength) ||
                        ((endCh >= 'a') && (endCh <= 'z')) ||
                        ((endCh >= 'A') && (endCh <= 'Z')) ||
                        ((endCh >= '0') && (endCh <= '9'))))
                {
                    outBuffer.append("return");
                    i += 5;
                    currentNonSpaceCh = '-'; // treat 'return' a an operator.

                    continue;
                }
            }

            // add space when a non-operator follows a closing parenthesis
            if (((prevNonSpaceCh == ')') || (prevNonSpaceCh == ']')) &&
                    Character.isLetterOrDigit(ch) && (ch != '.') &&
                    (ch != '_') && (ch != '$') && (ch != '(') && (ch != '[') &&
                    (ch != ')') && (ch != ']'))
            {
                appendSpace(outBuffer);
            }

            if ((!Character.isLetterOrDigit(ch) && (ch != '.') && (ch != '_') &&
                    (ch != '$') && (ch != '(') && (ch != '[') && (ch != ')') &&
                    (ch != ']')) &&
                    (Character.isLetterOrDigit(prevNonSpaceCh) ||
                    (prevNonSpaceCh == '.') || (prevNonSpaceCh == '_') ||
                    (prevNonSpaceCh == '$') || (prevNonSpaceCh == ')') ||
                    (prevNonSpaceCh == ']')))
            {
                boolean isLongOperator = false;
                String longOperator = null;

                for (int l = 0; l < longOperators.length; l++)
                {
                    if (line.regionMatches(false, i, longOperators[l], 0,
                                longOperators[l].length()))
                    {
                        isLongOperator = true;
                        longOperator = longOperators[l];

                        break;
                    }
                }

                if (isLongOperator)
                {
                    if (!"--".equals(longOperator) &&
                            !"++".equals(longOperator) &&
                            !".*".equals(longOperator))
                    {
                        appendSpace(outBuffer);
                        outBuffer.append(longOperator);
                        appendSpace(outBuffer);
                        ch = ' ';
                    }
                    else
                    {
                        outBuffer.append(longOperator);
                        currentNonSpaceCh = '0'; // hide the operator
                    }

                    i += 1; // since all long operators are 2 chars long...
                }
                else if (!((ch == '*') && (prevNonSpaceCh == '.'))) // not '.*'
                {
                    if ((ch != ',') && (ch != ';')) // && ch != ')' && ch != ']')
                    {
                        appendSpace(outBuffer);
                    }

                    outBuffer.append(ch);
                    appendSpace(outBuffer);
                    ch = ' ';
                }
                else
                {
                    outBuffer.append(ch);
                }

                continue;
            }

            if ((ch == ')') || (ch == ']'))
            {
                clearPaddingSpace(outBuffer);
            }

            // default
            outBuffer.append(ch);
        }

        try
        {
            tempLine = line.substring(i + ((i < line.length()) ? 1 : 0));
        }
        catch (Exception e)
        { /*is this exception really needed??? - check if the above ?: solves the problem...*/
            tempLine = "";
        }

        if (isBreakCalled || isInComment || isLineComment || shouldPublish)
        {
            outString = outBuffer.toString();
            outBuffer.setLength(0);
        }

        if ((outString != null) && !"".equals(outString))
        {
            outString = beautifier.beautify(outString);
        }
        else if ((ch != '[') && (ch != ']') && (ch != '(') && (ch != ')') &&
                (ch != '.') && (ch != '_') && (ch != '$'))
        {
            appendSpace(outBuffer); //outBuffer.append(" ");
        }

        return outString;
    }

    private void appendSpace(StringBuffer buf)
    {
        if ((buf.length() == 0) || (buf.charAt(buf.length() - 1) != ' '))
        {
            buf.append(' ');
        }
    }

    private void clearPaddingSpace(StringBuffer buf)
    {
        int bufLength = buf.length();

        if ((bufLength != 0) && (buf.charAt(bufLength - 1) == ' '))
        {
            buf.setLength(bufLength - 1);
        }
    }
}
