/*Enconding=UTF-8*/
package netgest.bo.parser.beautifier;

import java.awt.event.ActionEvent;

import java.io.*;

import javax.swing.text.BadLocationException;

//import org.jext.*;
import javax.swing.text.Document;
import javax.swing.text.Element;


public class CallBeautifier
{
    //	private int prefLineLength;
    //private String nameFile;
    private JSFormatter format;

    public CallBeautifier(int prefLineLength, int spaceNum, int maxIndent,
        boolean bracketIndent, boolean switchIndent, boolean tabIndent,
        String inDir, String outDir)
    {
        format = new JSFormatter();
        setLineLength(prefLineLength);
        setBracketBreak(bracketIndent);
        setSwitchIndent(switchIndent);
        setSpaceIndentation(spaceNum);
        setMaxIdentation(maxIndent);
        setTabIndentation(tabIndent);
        init();
        execute(inDir, outDir);
    }

    public void setLineLength(int max)
    {
        if (max <= 0)
        {
            format.setPreferredLineLength(70);
        }
        else
        {
            format.setPreferredLineLength(max);
        }
    }

    public void setBracketBreak(boolean bool)
    {
        format.setBracketBreak(bool);
    }

    public void setSwitchIndent(boolean bool)
    {
        format.setSwitchIndent(bool);
    }

    public void setMaxIdentation(int max)
    {
        format.beautifier.setMaxInStatementIndetation(max);
    }

    public void setSpaceIndentation(int tam)
    {
        format.beautifier.setSpaceIndentation(tam);
    }

    public void setTabIndentation(boolean set)
    {
        if (set)
        {
            format.beautifier.setTabIndentation();
        }
    }

    public void init()
    {
        format.init();
    }

    public void executeDirectory(File file)
    {
    }

    public void execute(String inDir, String outDir)
    {
        try
        {
            File fileIn = new File(inDir);
            File fileOut = new File(outDir);

            executeFile(fileIn, fileOut);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void executeFile(File in, File out)
    {
        try
        {
            InputStreamReader isr = null;
            FileOutputStream fos = null;
            PrintWriter pw = null;
            LineNumberReader lr = null;
            StringBuffer buf = new StringBuffer();

            try
            {
                isr = new InputStreamReader(new FileInputStream(in));
                fos = new FileOutputStream(out);
                pw = new PrintWriter(fos);
                lr = new LineNumberReader(isr);

                String line;

                while (true)
                {
                    while (!format.hasMoreFormattedLines())
                    {
                        if ((line = lr.readLine()) == null)
                        {
                            throw new NullPointerException();
                        }

                        format.formatLine(line);
                    }

                    while (format.hasMoreFormattedLines())
                        buf.append(format.nextFormattedLine() + '\n');
                }
            }
            catch (NullPointerException npe)
            {
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.exit(1);
            }

            pw.print(buf.toString());
            format.summarize();

            while (format.hasMoreFormattedLines())
                pw.println(format.nextFormattedLine());

            lr.close();
            isr.close();
            pw.close();
            fos.close();
        }
        catch (Exception e)
        {
        }
    }
}
