/*Enconding=UTF-8*/
package netgest.bo.parser.beautifier;

import java.io.*;

import java.util.*;


public class JSBeautifier
{
    // headers[] - an array of headers that require indentation
    private static String[] headers = 
    {
        "if", "else", "for", "while", "do", "try", "catch", "synchronized",
        "switch", "case", "default", "static"
    };

    // nonParenHeaders[] - an array of headers that DONT require parenthesies after them
    private static String[] nonParenHeaders = { "else", "do", "try", "static" };

    // preBlockStatements[] - an array of headers that exist within statements immediately preceding blocks
    private static String[] preBlockStatements = { "class", "interface", "throws" };

    // assignmentOperators[] - an array of assignment operators
    private static String[] assignmentOperators = 
    {
        "<<", ">>", "=", "+=", "-=", "*=", "/=", "%=", "|=", "&=", "return"
    };

    // nonAssignmentOperators[] - an array of non-assignment operators
    private static String[] nonAssignmentOperators = { "==", "++", "--", "!=" };

    // headerStack - a stack of the headers responsible for indentations of the current char
    private Stack headerStack;

    // tempStacks - a stack of Stacks. Each inner stack holds the current header-list in a { } block.
    // The innermost { } block's stack sits at the top of the tempStacks.
    private Stack tempStacks;

    // blockParenDepthStack - stack of the number of parenthesies that are open when new NESTED BLOCKS are created.
    private Stack blockParenDepthStack;

    // blockStatementStack - stack of the states of 'isInStatement' when new NESTED BLOCKS are created.
    private Stack blockStatementStack;

    // parenStatementStack - stack of the states of 'isInStatement' when new NESTED PARENTHESIES are created.
    private Stack parenStatementStack;

    // inStatementIndentStack - stack of LOCATIONS of in-statement indents
    private Stack inStatementIndentStack;

    // inStatementIndentStackSizeStack - stack of SIZES of inStatementIndentStack stacks
    private Stack inStatementIndentStackSizeStack;

    // parenIndentStack - stack of LOCATIONS of '(' or '[' chars
    private Stack parenIndentStack;

    // bracketBlockStateStack - stack of types of nested '{' brackets.
    // Each element of the stack is either True (=the beginner of a block), or False (=the beginner of a
    // static array).
    private Stack bracketBlockStateStack;

    // isSpecialChar - true if a there exists a '\' preceding the current chararacter.
    //   i.e. \n, \t, \\, ...
    private boolean isSpecialChar;

    // isInQuote - true when the current character is part of a quote (i.e. 'g' or "ffff")
    private boolean isInQuote;

    // isInComment - true when current character is part of a /* */ comment
    private boolean isInComment;

    // isInCase - true if in middle of a case statement (inside a switch);
    private boolean isInCase;

    // isInQuestion - true if in the middle of a '? :' statement
    private boolean isInQuestion;

    // isInStatement - true when current character is a part of an ongoing statement
    private boolean isInStatement;

    // isInClassHeader - true if inside a 'class' statement
    private boolean isInClassHeader;

    // isInClassHeaderTab - true if a special tab has been activated for the 'class statement'
    private boolean isInClassHeaderTab;

    // switchIndent - true if switch blocks should have an additional internal indent.
    private boolean switchIndent;

    // bracketIndent - true if brackets should have an added indent.
    private boolean bracketIndent;

    // quoteChar - the quote delimeter of a quote (' or ")
    private char quoteChar;

    // commmentIndent - the number of spaces to indent when in a comment
    private int commentIndent = 1;

    // parenDepth - the depth of parenthesies around the current character
    private int parenDepth;

    // indentString - the String to be used for every indentation
    // - either a "\t" or a String of n spaces.
    private String indentString;

    // indentLength - the length of one indent unit.
    private int indentLength;

    // blockTabCount - stores number of tabs to add to begining of line
    // due to statements with INNER blocks inside open parenthesies.
    private int blockTabCount;
    private int statementTabCount;
    private int leadingWhiteSpaces;
    private int maxInStatementIndent;
    private char prevNonSpaceCh;
    private char currentNonSpaceCh;
    private String currentHeader;
    private boolean isInHeader;
    private String immediatelyPreviousAssignmentOp;

    /**
     * JSBeautifier's constructor.
     */
    public JSBeautifier()
    {
        init();
        setSpaceIndentation(4); // the default indentation of a JSBeautifier object is of 4 spaces per indent
        setMaxInStatementIndetation(40);
        setBracketIndent(false);
        setSwitchIndent(true);
    }

    /**
     * beautify input from inreader to outWriter
     *
     * @param      inReader     a BufferedReader from which to input original source code
     * @param      outWriter    a PrintWriter to output beutified source code to
     *
     * @exception  IOException
     */
    public void beautifyReader(BufferedReader inReader, PrintWriter outWriter)
        throws IOException
    {
        String line = null;

        // beautify source code lines
        try
        {
            while (true)
            {
                line = inReader.readLine();

                if (line == null)
                {
                    break;
                }

                outWriter.println(beautify(line));
            }
        }
        catch (IOException e)
        {
        }
    }

    /**
     * initiate the JSBeautifier.
     *
     * init() should be called every time a JSBeautifier object is to start
     * beautifying a NEW source file.
     */
    public void init()
    {
        headerStack = new Stack();
        tempStacks = new Stack();
        tempStacks.push(new Stack());

        blockParenDepthStack = new Stack();
        blockStatementStack = new Stack();
        parenStatementStack = new Stack();

        bracketBlockStateStack = new Stack();
        bracketBlockStateStack.push(new Boolean(true));

        inStatementIndentStack = new Stack();
        inStatementIndentStackSizeStack = new Stack();
        inStatementIndentStackSizeStack.push(new Integer(0));
        parenIndentStack = new Stack();

        isSpecialChar = false;
        isInQuote = false;
        isInComment = false;
        isInStatement = false;
        isInCase = false;
        isInQuestion = false;
        isInClassHeader = false;
        isInClassHeaderTab = false;
        isInHeader = false;

        immediatelyPreviousAssignmentOp = null;

        parenDepth = 0;
        blockTabCount = 0;
        statementTabCount = -1;
        leadingWhiteSpaces = 0;

        prevNonSpaceCh = '{';
        currentNonSpaceCh = '{';
    }

    /**
     * ident using one tab per identation
     */
    public void setTabIndentation()
    {
        indentString = "\t";
        indentLength = 4;
    }

    /**
     * ident a number of spaces for each identation.
     *
     * @param   length     number of spaces per indent.
     */
    public void setSpaceIndentation(int length)
    {
        char[] spaces = new char[length];

        for (int i = 0; i < length; i++)
            spaces[i] = ' ';

        indentString = new String(spaces);

        indentLength = length;
    }

    /**
     * set the maximum indentation between two lines in a multi-line statement.
     *
     * @param   max     maximum indentation length.
     */
    public void setMaxInStatementIndetation(int max)
    {
        maxInStatementIndent = max;
    }

    /**
     * set the state of the bracket indentation option. If true, brackets will
     * be indented one additional indent.
     *
     * @param   state             state of option.
     */
    public void setBracketIndent(boolean state)
    {
        bracketIndent = state;
    }

    /**
     * set the state of the switch indentation option. If true, blocks of 'switch'
     * statements will be indented one additional indent.
     *
     * @param   state             state of option.
     */
    public void setSwitchIndent(boolean state)
    {
        switchIndent = state;
    }

    /**
     * beautify a line of source code.
     *
     * every line of source code in a java source code file should be sent
     * one after the other to the beautify method.
     */
    public String beautify(String line)
    {
        boolean isInLineComment = false; // true when the current character is in a // comment (such as this line ...)
        boolean isInSwitch = false;
        char ch = ' '; // the current character
        char prevCh; // previous char
        StringBuffer outBuffer = new StringBuffer(); // the newly idented line is bufferd here
        int tabCount = 0; // number of indents before line
        String lastLineHeader = null; // last header found within line
        boolean closingBracketReached = false;
        int spaceTabCount = 0;
        boolean usePreviousTabCount = false;
        int previousTabCount = 0;

        int headerStackSize = headerStack.size();
        boolean isLineInStatement = isInStatement;
        boolean shouldIndentBrackettedLine = true;

        currentHeader = null;

        // handle and remove white spaces around the line:
        // If not in comment, first find out size of white space before line,
        // so that possible comments starting in the line continue in
        // relation to the preliminary white-space.
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

        if (line.length() == 0)
        {
            return line;
        }

        if (!inStatementIndentStack.isEmpty())
        {
            spaceTabCount = ((Integer) inStatementIndentStack.peek()).intValue();
        }

        // calculate preliminary indentation based on data from past lines
        for (int i = 0; i < headerStackSize; i++)
        {
            if (!((i > 0) && !"{".equals(headerStack.elementAt(i - 1)) &&
                    "{".equals(headerStack.elementAt(i))))
            {
                tabCount++;
            }

            // is the switchIndent option is on, indent switch statements an additional indent.
            if (switchIndent && (i > 1) &&
                    "switch".equals(headerStack.elementAt(i - 1)) &&
                    "{".equals(headerStack.elementAt(i)))
            {
                tabCount++;
                isInSwitch = true;
            }
        }

        if (isInSwitch && switchIndent && (headerStackSize >= 2) &&
                "switch".equals(headerStack.elementAt(headerStackSize - 2)) &&
                "{".equals(headerStack.elementAt(headerStackSize - 1)) &&
                (line.charAt(0) == '}'))
        {
            tabCount--;
        }

        if (isInClassHeader)
        {
            isInClassHeaderTab = true;
            tabCount += 2;
        }

        //if (isInStatement)
        //    if (!headerStack.isEmpty() && !"{".equals(headerStack.lastElement()))
        //	tabCount--;
        // parse characters in the current line.
        for (int i = 0; i < line.length(); i++)
        {
            prevCh = ch;
            ch = line.charAt(i);

            if ((ch == '\n') || (ch == '\r'))
            {
                continue;
            }

            outBuffer.append(ch);

            if ((ch == ' ') || (ch == '\t'))
            {
                continue;
            }

            // handle special characters (i.e. backslash+character such as \n, \t, ...)
            if (isSpecialChar)
            {
                isSpecialChar = false;

                continue;
            }

            if (!(isInComment || isInLineComment) &&
                    line.regionMatches(false, i, "\\\\", 0, 2))
            {
                outBuffer.append('\\');
                i++;

                continue;
            }

            if (!(isInComment || isInLineComment) && (ch == '\\'))
            {
                isSpecialChar = true;

                continue;
            }

            // handle quotes (such as 'x' and "Hello Dolly")
            if (!(isInComment || isInLineComment) &&
                    ((ch == '"') || (ch == '\'')))
            {
                if (!isInQuote)
                {
                    quoteChar = ch;
                    isInQuote = true;
                }
                else if (quoteChar == ch)
                {
                    isInQuote = false;
                    isInStatement = true;

                    continue;
                }
            }

            if (isInQuote)
            {
                continue;
            }

            // handle comments
            if (!(isInComment || isInLineComment) &&
                    line.regionMatches(false, i, "//", 0, 2))
            {
                isInLineComment = true;
                outBuffer.append("/");
                i++;

                continue;
            }
            else if (!(isInComment || isInLineComment) &&
                    line.regionMatches(false, i, "/*", 0, 2))
            {
                isInComment = true;
                outBuffer.append("*");
                i++;

                continue;
            }
            else if ((isInComment || isInLineComment) &&
                    line.regionMatches(false, i, "*/", 0, 2))
            {
                isInComment = false;
                outBuffer.append("/");
                i++;

                continue;
            }

            if (isInComment || isInLineComment)
            {
                continue;
            }

            // if we have reached this far then we are NOT in a comment or string of special character...
            prevNonSpaceCh = currentNonSpaceCh;
            currentNonSpaceCh = ch;

            if (isInHeader)
            {
                isInHeader = false;
                currentHeader = (String) headerStack.peek();
            }
            else
            {
                currentHeader = null;
            }

            // handle parenthesies
            if ((ch == '(') || (ch == '[') || (ch == ')') || (ch == ']'))
            {
                if ((ch == '(') || (ch == '['))
                {
                    if (parenDepth == 0)
                    {
                        parenStatementStack.push(new Boolean(isInStatement));
                        isInStatement = true;
                    }

                    parenDepth++;

                    inStatementIndentStackSizeStack.push(new Integer(
                            inStatementIndentStack.size()));

                    if (currentHeader != null)
                    {
                        //spaceTabCount-=indentLength;
                        inStatementIndentStack.push(new Integer((indentLength * 2) +
                                spaceTabCount));
                        parenIndentStack.push(new Integer((indentLength * 2) +
                                spaceTabCount));
                    }
                    else
                    {
                        registerInStatementIndent(line, i, spaceTabCount,
                            isLineInStatement, true);
                    }
                }
                else if ((ch == ')') || (ch == ']'))
                {
                    parenDepth--;

                    if (parenDepth == 0)
                    {
                        isInStatement = ((Boolean) parenStatementStack.pop()).booleanValue();
                        ch = ' ';
                    }

                    if (!inStatementIndentStackSizeStack.isEmpty())
                    {
                        int previousIndentStackSize = ((Integer) inStatementIndentStackSizeStack.pop()).intValue();

                        while (previousIndentStackSize < inStatementIndentStack.size())
                            inStatementIndentStack.pop();

                        if (!parenIndentStack.isEmpty())
                        {
                            Object poppedIndent = parenIndentStack.pop();

                            if (i == 0)
                            {
                                spaceTabCount = ((Integer) poppedIndent).intValue();
                            }
                        }
                    }
                }

                continue;
            }

            if (ch == '{')
            {
                boolean isBlockOpener = false;

                // first, check if '{' is a block-opener or an static-array opener
                isBlockOpener |= ((prevNonSpaceCh == '{') &&
                ((Boolean) bracketBlockStateStack.peek()).booleanValue());

                isBlockOpener |= ((prevNonSpaceCh == ')') ||
                (prevNonSpaceCh == ';'));

                isBlockOpener |= isInClassHeader;

                isInClassHeader = false;

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
                    if (((line.length() - i) == getNextProgramCharDistance(
                                line, i)) &&
                            (immediatelyPreviousAssignmentOp != null)) // && !inStatementIndentStack.isEmpty() - actually not needed
                    {
                        inStatementIndentStack.pop();
                    }

                    inStatementIndentStackSizeStack.push(new Integer(
                            inStatementIndentStack.size()));
                    registerInStatementIndent(line, i, spaceTabCount,
                        isLineInStatement, true);

                    //parenIndentStack.push(new Integer(i+spaceTabCount));
                    parenDepth++;

                    if (i == 0)
                    {
                        shouldIndentBrackettedLine = false;
                    }

                    continue;
                }

                if (isInClassHeader)
                {
                    isInClassHeader = false;
                }

                if (isInClassHeaderTab)
                {
                    isInClassHeaderTab = false;
                    tabCount -= 2;
                }

                blockParenDepthStack.push(new Integer(parenDepth));
                blockStatementStack.push(new Boolean(isInStatement));

                inStatementIndentStackSizeStack.push(new Integer(
                        inStatementIndentStack.size()));

                blockTabCount += (isInStatement ? 1 : 0);
                parenDepth = 0;
                isInStatement = false;

                tempStacks.push(new Stack());
                headerStack.push("{");
                lastLineHeader = "{";

                continue;
            }

            //check if a header has been reached
            if (prevCh == ' ')
            {
                boolean isDoubleHeader = false;
                int h = findLegalHeader(line, i, headers);

                if (h > -1)
                {
                    // if we reached here, then this is a header...
                    isInHeader = true;

                    Stack lastTempStack = (Stack) tempStacks.peek();

                    // if a new block is opened, push a new stack into tempStacks to hold the
                    // future list of headers in the new block.
                    //if ("{".equals(headers[h]))
                    //    tempStacks.push(new Stack());
                    // take care of the special case: 'else if (...)'
                    if ("if".equals(headers[h]) &&
                            "else".equals(lastLineHeader))
                    {
                        headerStack.pop();
                    }

                    // take care of 'else'
                    else if ("else".equals(headers[h]))
                    {
                        String header;

                        if (lastTempStack != null)
                        {
                            int indexOfIf = lastTempStack.indexOf("if");

                            if (indexOfIf != -1)
                            {
                                // recreate the header list in headerStack up to the previous 'if'
                                // from the temporary snapshot stored in lastTempStack.
                                int restackSize = lastTempStack.size() -
                                    indexOfIf - 1;

                                for (int r = 0; r < restackSize; r++)
                                    headerStack.push(lastTempStack.pop());

                                if (!closingBracketReached)
                                {
                                    tabCount += restackSize;
                                }
                            }

                            /*
                             * If the above if is not true, i.e. no 'if' before the 'else',
                             * then nothing beautiful will come out of this...
                             * I should think about inserting an Exception here to notify the caller of this...
                             */
                        }
                    }

                    // check if 'while' closes a previous 'do'
                    else if ("while".equals(headers[h]))
                    {
                        String header;

                        if (lastTempStack != null)
                        {
                            int indexOfDo = lastTempStack.indexOf("do");

                            if (indexOfDo != -1)
                            {
                                // recreate the header list in headerStack up to the previous 'do'
                                // from the temporary snapshot stored in lastTempStack.
                                int restackSize = lastTempStack.size() -
                                    indexOfDo - 1;

                                for (int r = 0; r < restackSize; r++)
                                    headerStack.push(lastTempStack.pop());

                                if (!closingBracketReached)
                                {
                                    tabCount += restackSize;
                                }
                            }
                        }
                    }

                    // check if 'catch' closes a previous 'try' or 'catch'
                    else if ("catch".equals(headers[h]))
                    {
                        String header;

                        if (lastTempStack != null)
                        {
                            int indexOfTry = lastTempStack.indexOf("try");

                            if (indexOfTry == -1)
                            {
                                indexOfTry = lastTempStack.indexOf("catch");
                            }

                            if (indexOfTry != -1)
                            {
                                // recreate the header list in headerStack up to the previous 'do'
                                // from the temporary snapshot stored in lastTempStack.
                                int restackSize = lastTempStack.size() -
                                    indexOfTry - 1;

                                for (int r = 0; r < restackSize; r++)
                                    headerStack.push(lastTempStack.pop());

                                //lastTempStack.pop();
                                //headerStack.push("try");
                                if (!closingBracketReached)
                                {
                                    tabCount += restackSize;
                                }
                            }
                        }
                    }
                    else if ("case".equals(headers[h]) ||
                            "default".equals(headers[h]))
                    {
                        isInCase = true;
                        --tabCount;
                    }

                    else if (("static".equals(headers[h]) ||
                            "synchronized".equals(headers[h])) &&
                            !headerStack.isEmpty() &&
                            ("static".equals(headerStack.lastElement()) ||
                            "synchronized".equals(headerStack.lastElement())))
                    {
                        isDoubleHeader = true;
                    }

                    if (!isDoubleHeader)
                    {
                        spaceTabCount -= indentLength;
                        headerStack.push(headers[h]);
                    }

                    lastLineHeader = headers[h];

                    outBuffer.append(headers[h].substring(1));
                    i += (headers[h].length() - 1);

                    //if (parenDepth == 0)
                    isInStatement = false;
                }
            }

            if (ch == '?')
            {
                isInQuestion = true;
            }

            // special handling of 'case' statements
            if (ch == ':')
            {
                if (isInQuestion)
                {
                    isInQuestion = false;
                }
                else
                {
                    currentNonSpaceCh = ';'; // so that brackets after the ':' will appear as block-openers

                    if (isInCase)
                    {
                        isInCase = false;
                        ch = ';'; // from here on, treat char as ';'
                    }
                }
            }

            if (((ch == ';') || (ch == ',')) &&
                    !inStatementIndentStackSizeStack.isEmpty())
            {
                while ((((Integer) inStatementIndentStackSizeStack.peek()).intValue() +
                        ((parenDepth > 0) ? 1 : 0)) < inStatementIndentStack.size())
                    inStatementIndentStack.pop();
            }

            // handle ends of statements
            if (((ch == ';') && (parenDepth == 0)) || (ch == '}') ||
                    ((ch == ',') && (parenDepth == 0)))
            {
                if (ch == '}')
                {
                    // first check if this '}' closes a previous block, or a static array...
                    if (!bracketBlockStateStack.isEmpty() &&
                            !((Boolean) bracketBlockStateStack.pop()).booleanValue())
                    {
                        if (!inStatementIndentStackSizeStack.isEmpty())
                        {
                            int previousIndentStackSize = ((Integer) inStatementIndentStackSizeStack.pop()).intValue();

                            while (previousIndentStackSize < inStatementIndentStack.size())
                                inStatementIndentStack.pop();

                            parenDepth--;

                            if (i == 0)
                            {
                                shouldIndentBrackettedLine = false;
                            }

                            if (!parenIndentStack.isEmpty())
                            {
                                Object poppedIndent = parenIndentStack.pop();

                                if (i == 0)
                                {
                                    spaceTabCount = ((Integer) poppedIndent).intValue();
                                }
                            }
                        }

                        continue;
                    }

                    if (!inStatementIndentStackSizeStack.isEmpty())
                    {
                        inStatementIndentStackSizeStack.pop();
                    }

                    if (!blockParenDepthStack.isEmpty())
                    {
                        parenDepth = ((Integer) blockParenDepthStack.pop()).intValue();
                        isInStatement = ((Boolean) blockStatementStack.pop()).booleanValue();

                        if (isInStatement)
                        {
                            blockTabCount--;
                        }
                    }

                    closingBracketReached = true;

                    int headerPlace = headerStack.search("{");

                    if (headerPlace != -1)
                    {
                        while (!"{".equals(headerStack.pop()))
                            ;

                        if (!tempStacks.isEmpty())
                        {
                            tempStacks.pop();
                        }
                    }

                    ch = ' '; // needed due to cases such as '}else{', so that headers ('else' tn tih case) will be identified...
                }

                //else if (ch == ';' /* parenDepth == 0*/)
                //    while (((Integer) inStatementIndentStackSizeStack.peek()).intValue() < inStatementIndentStack.size())
                //	inStatementIndentStack.pop();

                /*
                 * Create a temporary snapshot of the current block's header-list in the
                 * uppermost inner stack in tempStacks, and clear the headerStack up to
                 * the begining of the block.
                 * Thus, the next future statement will think it comes one indent past
                 * the block's '{' unless it specifically checks for a companion-header
                 * (such as a previous 'if' for an 'else' header) within the tempStacks,
                 * and recreates the temporary snapshot by manipulating the tempStacks.
                 */
                if (!((Stack) tempStacks.peek()).isEmpty())
                {
                    ((Stack) tempStacks.peek()).removeAllElements();
                }

                while (!headerStack.isEmpty() &&
                        !"{".equals(headerStack.peek()))
                    ((Stack) tempStacks.peek()).push(headerStack.pop());

                if ((parenDepth == 0) && (ch == ';'))
                {
                    isInStatement = false;
                }

                continue;
            }

            if (prevCh == ' ')
            {
                int headerNum = findLegalHeader(line, i, preBlockStatements);

                if (headerNum > -1)
                {
                    isInClassHeader = true;
                    outBuffer.append(preBlockStatements[headerNum].substring(1));
                    i += (preBlockStatements[headerNum].length() - 1);
                }
            }

            // PRECHECK if a '==' or '--' or '++' operator was reached.
            // If not, then register an indent IF an assignment operator was reached.
            // The precheck is important, so that statements such as 'i--==2' are not recognized
            // to have assignment operators (here, '-=') in them . . .
            immediatelyPreviousAssignmentOp = null;

            boolean isNonAssingmentOperator = false;

            for (int n = 0; n < nonAssignmentOperators.length; n++)
                if (line.regionMatches(false, i, nonAssignmentOperators[n], 0,
                            nonAssignmentOperators[n].length()))
                {
                    outBuffer.append(nonAssignmentOperators[n].substring(1));
                    i++;

                    /*  the above two lines do the same as the next since all listed  non Assignment operators are 2 chars long...
                    if (nonAssignmentOperators[n].length() > 1)
                    {
                        outBuffer.append(nonAssignmentOperators[n].substring(1));
                        i += nonAssignmentOperators[n].length() - 1;
                    }
                    */
                    isNonAssingmentOperator = true;

                    break;
                }

            if (!isNonAssingmentOperator)
            {
                for (int a = 0; a < assignmentOperators.length; a++)
                    if (line.regionMatches(false, i, assignmentOperators[a], 0,
                                assignmentOperators[a].length()))
                    {
                        if (assignmentOperators[a].length() > 1)
                        {
                            outBuffer.append(assignmentOperators[a].substring(1));
                            i += (assignmentOperators[a].length() - 1);
                        }

                        registerInStatementIndent(line, i, spaceTabCount,
                            isLineInStatement, false);
                        immediatelyPreviousAssignmentOp = assignmentOperators[a];

                        break;
                    }
            }

            if ((parenDepth > 0) || !(isLegalNameChar(ch) || (ch == ':')))
            {
                isInStatement = true;
            }
        }

        // handle special cases of unindentation:

        /*
         * if '{' doesn't follow an immediately previous '{' in the headerStack
         * (but rather another header such as "for" or "if", then unindent it
         * by one indentation relative to its block.
         */
        if ((outBuffer.length() > 0) && (outBuffer.charAt(0) == '{') &&
                !((headerStack.size() > 1) &&
                "{".equals(headerStack.elementAt(headerStack.size() - 2))) &&
                shouldIndentBrackettedLine)
        {
            tabCount--;
        }

        else if ((outBuffer.length() > 0) && (outBuffer.charAt(0) == '}') &&
                shouldIndentBrackettedLine)
        {
            tabCount--;
        }

        if (tabCount < 0)
        {
            tabCount = 0;
        }

        // take care of extra bracket indentatation option...
        if (bracketIndent && (outBuffer.length() > 0) &&
                shouldIndentBrackettedLine)
        {
            if ((outBuffer.charAt(0) == '{') || (outBuffer.charAt(0) == '}'))
            {
                tabCount++;
            }
        }

        // finally, insert indentations into begining of line
        for (int i = 0; i < tabCount; i++)
            outBuffer.insert(0, indentString);

        while ((spaceTabCount--) > 0)
            outBuffer.insert(0, ' ');

        if (!inStatementIndentStack.isEmpty())
        {
            if (statementTabCount < 0)
            {
                statementTabCount = tabCount;
            }
        }
        else
        {
            statementTabCount = -1;
        }

        return outBuffer.toString();
    }

    private void registerInStatementIndent(String line, int i,
        int spaceTabCount, boolean isLineInStatement, boolean updateParenStack)
    {
        int inStatementIndent;
        int remainingCharNum = line.length() - i;
        int nextNonWSChar = 1;

        /*
        while (nextNonWSChar < remainingCharNum
               && (line.charAt(i+nextNonWSChar) == ' ' ||
                   line.charAt(i+nextNonWSChar) == '\t') )
            nextNonWSChar++;
        */
        nextNonWSChar = getNextProgramCharDistance(line, i);

        // if indent is around the last char in the line, indent instead 2 spaces from the previous indent
        if (nextNonWSChar == remainingCharNum)
        {
            int previousIndent = spaceTabCount;

            if (!inStatementIndentStack.isEmpty())
            {
                previousIndent = ((Integer) inStatementIndentStack.peek()).intValue();
            }

            inStatementIndentStack.push(new Integer(2 /*indentLength*/ +
                    previousIndent)); //2

            if (updateParenStack)
            {
                parenIndentStack.push(new Integer(previousIndent));
            }

            return;
        }

        if (updateParenStack)
        {
            parenIndentStack.push(new Integer(i + spaceTabCount));
        }

        inStatementIndent = i + nextNonWSChar + spaceTabCount;

        if ((i + nextNonWSChar) > maxInStatementIndent)
        {
            inStatementIndent = (indentLength * 2) + spaceTabCount;
        }

        if (!inStatementIndentStack.isEmpty() &&
                (inStatementIndent < ((Integer) inStatementIndentStack.peek()).intValue()))
        {
            inStatementIndent = ((Integer) inStatementIndentStack.peek()).intValue();
        }

        //else if (!isLineInStatement && i + nextNonWSChar < 8)
        //    inStatementIndent =  8 + spaceTabCount;
        inStatementIndentStack.push(new Integer(inStatementIndent));
    }

    // get distance to the next non-white sspace, non-comment character in the line.
    // if no such character exists, return the length remaining to the end of the line.
    private int getNextProgramCharDistance(String line, int i)
    {
        int inStatementIndent;
        boolean inComment = false;
        int remainingCharNum = line.length() - i;
        int charDistance = 1;
        int ch;

        for (charDistance = 1; charDistance < remainingCharNum;
                charDistance++)
        {
            ch = line.charAt(i + charDistance);

            if (inComment)
            {
                if (line.regionMatches(false, i + charDistance, "*/", 0, 2))
                {
                    charDistance++;
                    inComment = false;
                }

                continue;
            }
            else if ((ch == ' ') || (ch == '\t'))
            {
                continue;
            }
            else if (ch == '/')
            {
                if ((line.regionMatches(false, i + charDistance, "//", 0, 2)))
                {
                    return remainingCharNum;
                }
                else if ((line.regionMatches(false, i + charDistance, "/*", 0, 2)))
                {
                    charDistance++;
                    inComment = true;
                }
            }
            else
            {
                return charDistance;
            }
        }

        return charDistance;
    }

    private boolean isLegalNameChar(char ch)
    {
        return (((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z')) ||
        ((ch >= '0') && (ch <= '9')) || (ch == '.') || (ch == '_') ||
        (ch == '$'));
    }

    private int findLegalHeader(String line, int i, String[] possibleHeaders)
    {
        int maxHeaders = possibleHeaders.length;
        int p;

        for (p = 0; p < maxHeaders; p++)
            if (line.regionMatches(false, i, possibleHeaders[p], 0,
                        possibleHeaders[p].length()))
            {
                // first check that this is a header and not the begining of a longer word...
                int lineLength = line.length();
                int headerEnd = i + possibleHeaders[p].length();
                char endCh = 0;

                if (headerEnd < lineLength)
                {
                    endCh = line.charAt(headerEnd);
                }

                if ((headerEnd >= lineLength) || !isLegalNameChar(endCh))
                {
                    return p;
                }
                else
                {
                    return -1;
                }
            }

        return -1;
    }
}
