package netgest.bo.message.server.fax;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;
import netgest.bo.*;

import netgest.bo.controller.xwf.XwfController;
import netgest.bo.ejb.*;

import netgest.bo.impl.document.print.PrintHelper;
import netgest.bo.message.GarbageController;
import netgest.bo.message.server.mail.*;
import netgest.bo.message.utils.Attach;

import netgest.bo.message.utils.MessageUtils;
import netgest.bo.message.utils.XEOIDUtil;
import netgest.bo.runtime.*;

import netgest.io.*;
import netgest.io.FSiFile;

import netgest.utils.*;
import netgest.utils.ClassUtils;

import netgest.utils.HTMLRemover;
import netgest.xwf.EngineGate;
import netgest.xwf.core.*;
import netgest.bo.system.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.rmi.RemoteException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

public class FaxParser 
{
        //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.message.server.fax.FaxParser");
    private static final String ERROR_FAX_STR = "SENT FAX FAILED TO";
    private static final String SUCCESS_FAX_STR = "FAX SENT TO";
    private static final String TOFIND1 = "\\s- XEOFAX \\["+boConfig.getMailPrefix()+".*?\\]";
    private static final String TOFIND2 = "\\s XEOFAX \\["+boConfig.getMailPrefix()+".*?\\]";

    private String subject = "";
    private boolean sucess = false;
    private String faxNumber = "";
    private String sender = "";
    private String senderMail = "";
    private String status = "";
    private String msgDate = "";
    private String speed = "";
    private String connTime = "";
    private String pages = "";
    private String totalPages = "";
    private String resolution = "";
    private String remoteId = "";
    private String lineNumber = "";
    private String retries = "";
    private String description = "";
    private String content;
    
    
    public FaxParser(MailMessage msg)
    {
        if(msg != null)
        {
            //vou ler o conteudo do email e não o subject
            if(msg.getContent() != null && msg.getContent().length() > 0)
            {
                if(!parseContent(msg.getContent()))
                {
                    parseSubject(msg.getSubject());
                }
            }
            else if(msg.getContentHTML() != null && msg.getContentHTML().length() > 0)
            {
                if(!parseContent(msg.getContentHTML()))
                {
                    parseSubject(msg.getSubject());
                }
            }
            else if(msg.getSubject() != null && msg.getSubject().length() > 0)
            {
                parseSubject(msg.getSubject());
            }
        }
    }
    
    public FaxParser(String subject, String content)
    {
        if(!parseContent(content))
        {
            parseSubject(subject);
        }
    }
    
    public boolean parseContent(String _content)
    {
        try
        {
            this.content = ClassUtils.htmlToText(_content);
            String [] lines = content.split("\n");
            String line;
            String upperLine;
            int pos = -1;
            for (int i = 0; i < lines.length; i++) 
            {
                line = lines[i];
                if(line != null && line.length() > 0)
                {
                    line = line.trim();
                    upperLine = line.toUpperCase();
                    if(upperLine.startsWith("SUBJECT:"))
                    {
                        subject = removeXEOTags(getValue(line));
                        if(upperLine.indexOf("FAILURE:") > -1)
                        {
                            parseErrorSubject(upperLine);
                        }
                        else if(upperLine.indexOf("SUCCESS:") > -1)
                        {
                            parseSucessSubject(upperLine);
                        }
                    }
                    else if(upperLine.startsWith("SENDER:"))
                    {
                        sender = getValue(line);
                    }
                    else if(upperLine.startsWith("SENDER EMAIL:"))
                    {
                        senderMail = getValue(line);
                    }
                    else if(upperLine.startsWith("STATUS:"))
                    {
                        status = getValue(line);
                    }
                    else if(upperLine.startsWith("DATE/TIME:"))
                    {
                        msgDate = getValue(line);
                    }
                    else if(upperLine.startsWith("SPEED:"))
                    {
                        speed = getValue(line);
                    }
                    else if(upperLine.startsWith("CONNECTION TIME:"))
                    {
                        connTime = getValue(line);
                    }
                    else if(upperLine.startsWith("PAGES:"))
                    {
                        pages = getValue(line);
                    }
                    else if(upperLine.startsWith("TOTAL PAGES:"))
                    {
                        totalPages = getValue(line);
                    }
                    else if(upperLine.startsWith("RESOLUTION:"))
                    {
                        resolution = getValue(line);
                    }
                    else if(upperLine.startsWith("REMOTE ID:"))
                    {
                        remoteId = getValue(line);
                    }
                    else if(upperLine.startsWith("LINE NUMBER:"))
                    {
                        lineNumber = getValue(line);
                    }
                    else if(upperLine.startsWith("RETRIES:"))
                    {
                        retries = getValue(line);
                    }
                    else if(upperLine.startsWith("DESCRIPTION"))
                    {
                        description = getValue(line);
                    }
                }
            }
            return true;
        }
        catch (Exception e)
        {
           logger.severe("", e);
        }
        return false;
    }
    
    private void parseErrorSubject(String upperSubject)
    {
        //tratamento da string:
        //Subject: Failure: Subject:Teste de Fax XPTO (Sent fax failed to 214702350)
        int pos = upperSubject.indexOf(ERROR_FAX_STR);
        sucess = false;
        if(pos > -1)
        {
            //final do paranteses
            int pos2 = upperSubject.indexOf(")", pos);
            
            //o tamanho da Str + o espaço em branco até o fecho do parenteses
            faxNumber = upperSubject.substring(pos+ERROR_FAX_STR.length()+1, pos2);
            if(faxNumber.startsWith("0"))
            {
                faxNumber = faxNumber.substring(1);
            }
        }
    }
    
    private void parseSucessSubject(String upperSubject)
    {
        //tratamento da string:
        //Subject: Failure: Subject:Teste de Fax XPTO (Sent fax failed to 214702350)
        int pos = upperSubject.indexOf(SUCCESS_FAX_STR);
        sucess = true;
        if(pos > -1)
        {
            //final do paranteses
            int pos2 = upperSubject.indexOf(")", pos);
            
            //o tamanho da Str + o espaço em branco até o fecho do parenteses
            faxNumber = upperSubject.substring(pos+SUCCESS_FAX_STR.length()+1, pos2);
        }
    }
    
    private void parseSubject(String subject)
    {
        //tratamento da string:
        //Subject: Failure: Subject:Teste de Fax XPTO (Sent fax failed to 214702350)
        String upperSubject = subject.toUpperCase();
        this.subject = subject;
        if(upperSubject.indexOf("FAILURE:") > -1)
        {
            parseErrorSubject(upperSubject);
        }
        else if(upperSubject.indexOf("SUCCESS:") > -1)
        {
            parseSucessSubject(upperSubject);
        }
    }
    
    private static String getValue(String line)
    {
        int pos = line.indexOf(":");
        if(pos > 0)
        {
            return line.substring(pos + 1).trim();
        }
        return "";
    }
    
    public String getSubject()
    {
        return subject;
    }
    
    public boolean sucessReport()
    {
        return sucess;
    }
    
    public String getFaxNumber()
    {
        return faxNumber;
    }
    
    public String getSender()
    {
        return sender;
    }
    
    public String getSenderMail()
    {
        return senderMail;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public String getSentDate()
    {
        return msgDate;
    }
    
    public String getSpeed()
    {
        return speed;
    }
    
    public String getConnectionTime()
    {
        return connTime;
    }
    
    public String getPagesSended()
    {
        return pages;
    }
    
    public String getNumberOfPages()
    {
        return totalPages;
    }
    
    public String getResolution()
    {
        return resolution;
    }
    
    public String getRemoteId()
    {
        return remoteId;
    }
    
    public String getLineNumber()
    {
        return lineNumber;
    }
    
    public String getRetries()
    {
        return retries;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public static void main(String[] args)
    {
        
//        FaxParser f = new FaxParser(
//            " Failure: Subject:Teste de Fax XPTO (Sent fax failed to 214702350) [::resend=f4e69638]",
//            "\n"+
//"*********************************************************\n"+
//"SENT FAX REPORT\n"+
//"*********************************************************\n"+
//"\n"+
//"Subject: Failure: Subject:Teste de Fax XPTO (Sent fax failed to 214702350)\n"+
//"Sender: Joo Carreira\n"+
//"Sender email: joao.carreira@itds.pt\n"+
//"Status: Failed\n"+
//"Date/Time: 05-06-2007 20:44:35\n"+
//"Speed: 9600 bps\n"+
//"Connection time: 05:56\n"+
//"Pages: 0\n"+
//"Total pages: 1\n"+
//"Resolution: Fine\n"+
//"Remote ID: \n"+
//"Line number: 0\n"+
//"Retries: 3\n"+
//"Description: Failed to send fax : No answer from remote machine\n"+
//"\n"+
//"*********************************************************\n"+
//"\n"+
//"\n"+
//"                              \n"+
//"Teste de Fax XPTO, Hello World\n");

//        FaxParser f = new FaxParser(
//            "Failure: Subject:Teste de Fax XPTO (Sent fax failed to 214702350) [::resend=f035c794]",
//            "\n"+
//"*********************************************************\n"+
//"SENT FAX REPORT\n"+
//"*********************************************************\n"+
//"\n"+
//"Subject: Failure: Subject:Teste de Fax XPTO (Sent fax failed to 214702350)\n"+
//"Sender: Joo Carreira\n"+
//"Sender email: joao.carreira@itds.pt\n"+
//"Status: Failed\n"+
//"Date/Time: 05-06-2007 20:28:05\n"+
//"Speed: 9600 bps\n"+
//"Connection time: 00:24\n"+
//"Pages: 0\n"+
//"Total pages: 1\n"+
//"Resolution: Fine\n"+
//"Remote ID: \n"+
//"Line number: 0\n"+
//"Retries: 3\n"+
//"Description: Failed to send fax : No dialtone\n"+
//"\n"+
//"*********************************************************\n"+
//"\n");

//        FaxParser f = new FaxParser(
//            "Success: Subject:Teste de Fax XPTO (Fax sent to 214702380) [::resend=s0107034]",
//            "\n"+
//"*********************************************************\n"+
//"SENT FAX REPORT\n"+
//"*********************************************************\n"+
//"\n"+
//"Subject: Success: Subject:Teste de Fax XPTO (Fax sent to 214702380)\n"+
//"Sender: Joo Carreira\n"+
//"Sender email: joao.carreira@itds.pt\n"+
//"Status: Sent\n"+
//"Date/Time: 05-06-2007 21:09:07\n"+
//"Speed: 14400 bps\n"+
//"Connection time: 01:13\n"+
//"Pages: 2\n"+
//"Total pages: 2\n"+
//"Resolution: Fine\n"+
//"Remote ID: \n"+
//"Line number: 0\n"+
//"Retries: 0\n"+
//"Description: Fax sent successfully : Success\n"+
//"\n"+
//"*********************************************************\n"+
//"\n"+
//"\n"+
//"\n"+
//"Teste de Fax XPTO, Hello World\n"+
//"\n"+
//"\n");

    FaxParser f = new FaxParser(
            "Subject:Teste de Fax XPTO",
            "\n"+
"*********************************************************\n"+
"ERROR FAX REPORT\n"+
"*********************************************************\n"+
"\n"+
"Status: Failed\n"+
"Date/Time: 05-06-2007 21:05:24\n"+
"Description: Attachment conversion failed : 1701\n"+
"\n"+
"*********************************************************\n"+
"\n"+
"1701 : Document Conversion Timeout. Ensure that the FAXmaker printer driver on the server machine is installed on the GFIFAX port and is set up as default printer. Check that the application program is installed on the server and that there is association to print with the files.\n"+
"\n"+
"\n"+
"\n"+
"Teste de Fax XPTO, Hello World\n"+
"\n"+
"\n");



        try
        {
            System.out.println("Assunto:" + f.getSubject()        );
            System.out.println("Sucesso:" + f.sucessReport()      );
            System.out.println("N fax: " + f.getFaxNumber()      );
            System.out.println("Sender: " + f.getSender()         );
            System.out.println("Sender Mail: " + f.getSenderMail()     );
            System.out.println("Estado: " + f.getStatus()         );
            System.out.println("Data de envio: " + f.getSentDate()       );
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            if(f.getSentDate() != null && !"".equals(f.getSentDate()))
                System.out.println("Data de envio2: " + sdf.parse(f.getSentDate())      );
            System.out.println("Velocidade: " + f.getSpeed()          );
            System.out.println("Temp Ligação: " + f.getConnectionTime() );
            System.out.println("Pags. Enviadas: " + f.getPagesSended()    );
            System.out.println("Pags para Enviar: " + f.getNumberOfPages()  );
            System.out.println("Resolução: " + f.getResolution()     );
            System.out.println("Maq. Remota: " + f.getRemoteId()       );
            System.out.println("N Linha: " + f.getLineNumber()     );
            System.out.println("Tentativas: " + f.getRetries()        );
            System.out.println("Descrição: " + f.getDescription()    );
    //        System.out.println(f.getContent()      );
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }
    
    public static String removeXEOTags(String msg)
    {
        if(msg == null || "".equals(msg) ) return "";

        Pattern p = Pattern.compile(TOFIND1);
        Matcher m = p.matcher(msg);
        msg = m.replaceAll("");
        
        p = Pattern.compile(TOFIND2);
        m = p.matcher(msg);
        return m.replaceAll("");
    }
    
    public static String friendlyMsg(String errorDescription)
    {
        String toRet = errorDescription;
        
        if("Attachment conversion failed : 1701".equalsIgnoreCase(errorDescription))
        {
            toRet = "Existe um documento em anexo que não pode ser enviado por Fax.";
        }
        else if("Failed to send fax : A timeout occurred".equalsIgnoreCase(errorDescription))
        {
            toRet = "Não houve resposta do número especificado.";
        }
        else if("Failed to send fax : Busy tone detected".equalsIgnoreCase(errorDescription))
        {
            toRet = "Linha ocupada.";
        }
        else if("Failed to send fax : Call failed".equalsIgnoreCase(errorDescription))
        {
            toRet = "Número inválido ou não atribuído.";
        }
        else if("Failed to send fax : Handshaking failed".equalsIgnoreCase(errorDescription))
        {
            toRet = "Dificuldades na comunicação devido a problemas da linha.";
        }
        else if("Failed to send fax : No answer from remote machine".equalsIgnoreCase(errorDescription))
        {
            toRet = "Não houve resposta do número especificado.";
        }
        else if("Failed to send fax : Operation was aborted by user".equalsIgnoreCase(errorDescription))
        {
            toRet = "O envio do fax foi cancelado pelos técnicos.";
        }
        return toRet;
    }
}