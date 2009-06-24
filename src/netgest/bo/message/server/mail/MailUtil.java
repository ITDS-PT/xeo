/*Enconding=UTF-8*/
package netgest.bo.message.server.mail;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.security.*;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeUtility;

import netgest.bo.message.Address;
import netgest.bo.message.utils.Attach;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.utils.CleanDirectory;
import netgest.utils.ClassUtils;
import netgest.utils.ConvertUTF7;
import netgest.utils.TempFile;

import org.apache.log4j.Logger;

import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.provider.*;
import org.bouncycastle.mail.smime.SMIMESigned;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class MailUtil
{
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.message.server.mail.MailUtil");

    /**
     *
     * @Company Enlace3
     * @since
     */
    private MailUtil()
    {
    }

    public static byte getMessageType(MimeMessage message)
        throws MessagingException
    {
        if ((message.getContentType() != null) &&
                (message.getContentType().toLowerCase().indexOf("report") != -1))
        {
            String reportType = getHeader("report-type", message);

            if ((reportType != null) &&
                    (message.getHeader(reportType, ";") != null) &&
                    (message.getHeader(reportType, ";").toLowerCase().indexOf("delivery-status") != -1))
            {
//                logger.info("Recibo de entrega");
                return Mail.DELIVERED_RECEIPT;
            }
            else if ((reportType != null) &&
                    (message.getHeader(reportType, ";") != null) &&
                    (message.getHeader(reportType, ";").toLowerCase().indexOf("delivery-notification") != -1))
            {
//                logger.info("Recibo de leitura");
                return Mail.READ_RECEIPT;
            }
            else if (message.getContentType().toLowerCase().indexOf("delivery-status") != -1)
            {
//                logger.info("Recibo de entrega");
                return Mail.DELIVERED_RECEIPT;
            }
            else
            {
//                logger.info("Recibo de leitura");
                return Mail.READ_RECEIPT;
            }
        }
//        logger.info("Email normal");
        return Mail.MESSAGE;
    }

    public static String getContextThread(MimeMessage message)
        throws MessagingException
    {
        String headerAux = getHeader("Thread-Topic", message);
        String thread = (headerAux == null) ? null
                                            : convertString(message.getHeader(
                    headerAux, ";"));

        if ((thread == null) || "".equals(thread.trim()))
        {
            headerAux = getHeader("In-Reply-To", message);
            thread = (headerAux == null) ? null
                                         : convertString(message.getHeader(
                        "In-Reply-To", ";"));
        }

        if ((thread == null) || "".equals(thread.trim()))
        {
            headerAux = getHeader("References", message);
            thread = (headerAux == null) ? null
                                         : convertString(message.getHeader(
                        "References", ";"));
        }

        if (((thread == null) || "".equals(thread.trim())))
        {
            return null;
        }

        return thread;
    }

    public static String getHeader(String header, MimeMessage msg)
    {
        try
        {
            Enumeration oEnum = msg.getAllHeaders();
            Header headerAux;

            while (oEnum.hasMoreElements())
            {
                headerAux = (Header) oEnum.nextElement();

                if ((headerAux != null) &&
                        headerAux.getName().toLowerCase().equals(header.toLowerCase()))
                {
                    return headerAux.getName();
                }
            }
        }
         catch (MessagingException e)
        {
            logger.info("", e);
        }

        return null;
    }

    public static void copyFile(InputStream fis, OutputStream fos)
        throws IOException
    {
        byte[] b = new byte[1024 * 10];
        int numBytes = 0;

        for (long i = 0; (numBytes = fis.read(b)) != -1; i++)
        {
            fos.write(b, 0, numBytes);
        }
    }

    public static String convertString(String s)
    {
        String auxText = null;
        String aux = null;

        try
        {
            auxText = MimeUtility.decodeText(s);
            auxText = correctDecoding(auxText);
            auxText = specialCase(auxText);
            auxText = ((aux = new ConvertUTF7(auxText).convertUTF7()) == null)
                ? auxText : aux;
        }
         catch (Exception _e)
        {
            if (auxText != null)
            {
                return auxText;
            }

            return s;
        }

        if (auxText != null)
        {
            return auxText;
        }

        return s;
    }

    public static String convertFromFaxString(String s)
    {
        String auxText = null;
        String aux = null;

        try
        {
            auxText = MimeUtility.decodeText(s);
        }
         catch (Exception _e)
        {
            if (auxText != null)
            {
                return auxText;
            }

            return s;
        }

        if (auxText != null)
        {
            return auxText;
        }

        return s;
    }

    public static Date stringCompleteToDate(String data)
        throws ParseException
    {
        return DateFormat.getInstance().parse(data);
    }

    public static boolean isToReturnReceipt(MimeMessage message)
        throws MessagingException
    {
        String headerAux = getHeader("Disposition-Notification-To", message);
        String disposition = (headerAux == null) ? null
                                                 : message.getHeader(headerAux,
                ";");

        if ((disposition == null) || "".equals(disposition.trim()))
        {
            headerAux = getHeader("Read-Receipt-To", message);
            disposition = (headerAux == null) ? null
                                              : message.getHeader(headerAux, ";");
        }

        if ((disposition == null) || "".equals(disposition.trim()))
        {
            headerAux = getHeader("X-Confirm-reading-to", message);
            disposition = (headerAux == null) ? null
                                              : message.getHeader(headerAux, ";");
        }

        if ((disposition == null) || "".equals(disposition.trim()))
        {
            return false;
        }

        //    logger.warn("Return to: " + disposition);
        return true;
    }

    public static String dateCompleteToString(Date data)
    {
        try
        {
            return DateFormat.getInstance().format(data);
        }
         catch (Exception ex)
        {
            return null;
        }
    }

    /**
    * Process all the parts of a mail message
    *
    *@param mp - JavaMail Multipart Object
    */
    public static void processMultipart(Multipart mp, MailMessage mailmsg)
        throws MessagingException, boRuntimeException
    {
        int n = mp.getCount();

        for (int i = 0; i < n; i++)
        {
            processPart(mp.getBodyPart(i), mailmsg);
        }
    }

    /**
    * Process all the parts of a mail message
    *
    *@param mp - JavaMail Multipart Object
    */
    public static void processMultipart(SMIMESigned mp, MailMessage mailmsg)
        throws MessagingException, boRuntimeException
    {
        //
        // extract the content
        //
        try
        {
            mailmsg.setSignedMsg(verify(mp));
        }
         catch (boRuntimeException e)
        {
            logger.error("", e);
        }

        MimeBodyPart content = mp.getContent();
        processPart(content, mailmsg);
    }

    /**
     * Process a single part of a mail message this part can
     * be either an inline Text part (a body) or a binary one (an Attachment)
     *@param p - JavaMail Part Object
     *
     */
    public static void processPart(Part p, MailMessage mailmsg) throws boRuntimeException
    {
        try
        {
            String contenttype = p.getContentType();
            String disposition = p.getDisposition();
            String filename = convertString(p.getFileName());
            Object mp = null;

            try
            {
                mp = p.getContent();
            }
             catch (Exception e)
            {
                //caso do utf-7 ou sem encode
//                ByteArrayOutputStream bao = new ByteArrayOutputStream();
//                p.writeTo(bao);
//                mp = bao.toString();
                mp = getEncodedContentAsString(p);
            }

            // caso existam outras partes dentro desta parte
            try
            {
                if (mp instanceof Multipart)
                {
//                    logger.info("Process Multipart");
                    processMultipart((Multipart) mp, mailmsg);
                }
                else if(mp instanceof MimeMessage)
                {
                    MimeMessage m = (MimeMessage)mp;
                    Integer attachnum = new Integer(mailmsg.getAttach().length + 1);
                    Attach att = MailUtil.getBinaryMail(m.getSubject(), attachnum.toString(), m);
                    mailmsg.addAttach(att);
                }
                else if(mp instanceof InputStream)
                {
                    if(filename == null || filename.length() == 0)
                    {
                        filename = "att" + System.currentTimeMillis()+".txt";
                    }
                }
            }
             catch (Exception e)
            {
                mailmsg.setReadError(true);

                if (!getContentAfterError((Multipart) mp, mailmsg))
                {
//                    getContentAfterError(p, mailmsg);
                }
            }
            String auxStr = contenttype.toLowerCase();

            // se vier no email como attach
            if (filename != null)
            {
                filename = removeSpecialCharacters(filename);
                // se for uma mensagem de email dentro de outra mensagem
                Integer attachnum = new Integer(mailmsg.getAttach().length + 1);

                if (filename.equals(".msg"))
                {
                    filename = "email" + (attachnum) + ".eml";
                }

                // guarda o ficheiro
                //           File f=new File(p_sysuser.getOutBox() +filename);
                InputStream in = new BufferedInputStream(p.getInputStream());

                String suffix = getExtension(filename);
                String prefix = getFirstPart(filename);
                File temFile = createTempFile(prefix, suffix);
                FileOutputStream out = new FileOutputStream(temFile);
                try
                {
                    copyFile(in, out);
                }
                catch (Exception e)
                {
                    logger.error("", e);
                }
                finally
                {
                    try{out.close();}catch (Exception e){}
                    try{in.close();}catch (Exception e){}
                }

                boolean inline = false;

                //para o caso de ser uma imagem contida dentro do body de uma mensagem
                String inlineid = "";

                if (!(Part.ATTACHMENT.equals(disposition)))
                {
                    inline = true;

                    String[] farraux = p.getHeader("Content-ID");

                    if ((farraux != null) && (farraux.length > 0))
                    {
                        inlineid = farraux[0].substring(1,
                                farraux[0].length() - 1);
                    }
                    else
                    {
                        inline = false;
                    }
                }

                Attach att = new Attach(filename, temFile.getAbsolutePath(), attachnum.toString(), true,
                        inline);
                att.setInlineID(inlineid);
                mailmsg.addAttach(att);
            }

            // Body da msg texto apenas
            // Body da msg quando esta e enviado em HTML
            else if (auxStr.indexOf("text/plain") != -1)
            {
                if (mailmsg.getContent() != null)
                {
                    String aStr = mailmsg.getContent() + ClassUtils.textToHtml(getContext(mp));
                    mailmsg.setContent(aStr);
                }
                else
                {
                    mailmsg.setContent(ClassUtils.textToHtml(getContext(mp)));
                }
            }
            else if (auxStr.indexOf("text/html") != -1)
            {
                if (mailmsg.getContentHTML() != null)
                {
                    String aStr = mailmsg.getContentHTML() + getContext(mp);
                    mailmsg.setContentHTML(aStr);
                }
                else
                {
                    mailmsg.setContentHTML(getContext(mp));
                }
            }
        }
         catch (Exception e)
        {
            //        e.printStackTrace();
            logger.error("", e);
            throw new boRuntimeException("", "", e);
        }
    }

    public static String getExtension(String aux)
    {
        try
        {
            int pos = aux.lastIndexOf(".");
            if(pos >= 0)
            {
                return aux.substring(pos);
            }
        }
        catch (Exception e)
        {

        }
        return null;
    }

    public static String getFirstPart(String aux)
    {
        try
        {
            int pos = aux.lastIndexOf(".");
            if(pos >= 0)
            {
                return aux.substring(0,pos);
            }
        }
        catch (Exception e)
        {

        }
        return aux;
    }

    private static String removeSpecialCharacters(String aux)
    {
        try
        {
            if(aux != null)
            {
                aux = aux.replaceAll("\t", " ");
                aux = aux.replaceAll("\r", " ");
                aux = aux.replaceAll("\n", " ");
                aux = aux.replaceAll("/", " ");
                aux = aux.replaceAll(":", " ");
                aux = aux.replaceAll("\\|", " ");
                aux = aux.replaceAll("\"", " ");
                aux = aux.replaceAll("\"", " ");
                aux = aux.replaceAll(">", " ");
                aux = aux.replaceAll("<", " ");
                aux = aux.replaceAll("\\?", " ");
                aux = aux.replaceAll("\\*", " ");
                aux = aux.replaceAll("\\^", " ");
                aux = aux.replaceAll("\\\\", " ");
                if(aux.length() > 254)
                {
                    int pos = aux.lastIndexOf(".");
                    if(pos > 0)
                    {
                        String prefix = aux.substring(0, pos);
                        String sufix = aux.substring(pos);
                        if(prefix.length() > (254-sufix.length()))
                        {
                            aux = prefix.substring(0, 254-sufix.length());
                        }
                        else
                        {
                            aux = prefix;
                        }
                        aux = aux + sufix;
                    }
                    else
                    {
                        aux = aux.substring(0, 254);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return aux;
    }
    private static String getContext(Object mp)
    {
        if(mp == null) return "";
        if(mp instanceof String)
        {
            return (String) mp;
        }
        else
        {
            try
            {
                logger.info("EMAIL WITH DIFERENT TYPE = " + mp.getClass().getName());
            }
            catch (Exception e)
            {
                //ignore
            }
            return mp.toString();
        }
    }

    public static boolean getContentAfterError(Multipart mp, MailMessage mailmsg)
    {
        FileOutputStream out = null;
        try
        {
            // se for uma mensagem de email dentro de outra mensagem
            Integer attachnum = new Integer(mailmsg.getAttach().length + 1);
            File tempFile = createTempFile("error", "txt");
            out = new FileOutputStream(tempFile);
            mp.writeTo(out);
            out.close();

            Attach att = new Attach("error.txt",tempFile.getAbsolutePath(), attachnum.toString(), true, false);
            att.setInlineID("");
            mailmsg.addAttach(att);
        }
         catch (FileNotFoundException e)
        {
            logger.warn("", e);

            return false;
        }
         catch (IOException e)
        {
            logger.warn("", e);

            return false;
        }
         catch (MessagingException e)
        {
            logger.warn("", e);

            return false;
        }
         catch (Exception e)
        {
            logger.warn("", e);

            return false;
        }
        finally
        {
            if(out != null)
            {
                try{out.close();}catch (Exception e){}
            }
        }

        return true;
    }

//    public static boolean getContentAfterError(Part p, MailMessage mailmsg)
//        throws Exception
//    {
//        InputStream i = null;
//        ByteArrayOutputStream bao = null;
//
//        try
//        {
//            i = p.getInputStream();
//            bao = new ByteArrayOutputStream();
//            copyFile(i, bao);
//
//            if (mailmsg.getContent() != null &&
//                    mailmsg.getContent().length() > 0)
//            {
//                String aStr = mailmsg.getContent() + "\nError Reading Mail:\n" +
//                    bao.toString();
//                mailmsg.setContent(aStr);
//            }
//            else
//            {
//                mailmsg.setContent(bao.toString());
//            }
//        }
//         finally
//        {
//            try
//            {
//                if (i != null)
//                {
//                    i.close();
//                }
//            }
//             catch (Exception e)
//            {
//            }
//
//            try
//            {
//                if (bao != null)
//                {
//                    bao.close();
//                }
//            }
//             catch (Exception e)
//            {
//            }
//        }
//
//        return true;
//    }

    public static void setAskReceipt(boolean deliver, Properties props,
        Message message, InternetAddress from)
    {
        try
        {
            if (deliver)
            {
                StringBuffer sb = new StringBuffer(
                        "SUCCESS,FAILURE ORCPT=rfc822;");
                sb.append(from.getAddress());
                props.put("mail.smtp.dsn.notify", sb.toString());
                message.setHeader("Return-Receipt-To", from.getAddress());
            }
            else
            {
                //RFC 2298
                message.setHeader("Disposition-Notification-To",
                    from.getAddress());
                message.setHeader("Read-Receipt-To", from.getAddress());

                //pegasus
                message.setHeader("X-Confirm-reading-to", from.getAddress());
            }
        }
         catch (MessagingException e)
        {
            logger.info("", e);
        }
    }

    public static int getPriority(MimeMessage message)
        throws MessagingException
    {
        String hPr = MailUtil.getHeader("X-Priority", message);

        if (hPr != null)
        {
            String priority = message.getHeader(hPr, ";");

            if (priority != null)
            {
                //            logger.warn(priority);
                int n = Mail.NORMAL;

                try
                {
                    n = Integer.parseInt(priority);
                }
                 catch (Exception e)
                {
                    //ignore
                }

                if (n > Mail.NORMAL)
                {
                    return Mail.LOWEST;
                }

                if (n < Mail.NORMAL)
                {
                    return Mail.HIGHEST;
                }
            }
        }

        return Mail.NORMAL;
    }

    public static void setPriority(int priority, Message message)
    {
        try
        {
            message.setHeader("X-Priority", String.valueOf(priority));

            if (priority > Mail.NORMAL)
            {
                message.setHeader("Priority", Mail.PR_LOW);
                message.setHeader("Importance", Mail.IMP_LOW);
            }
            else if (priority < Mail.NORMAL)
            {
                message.setHeader("Priority", Mail.PR_HIGH);
                message.setHeader("Importance", Mail.IMP_HIGH);
            }
            else
            {
                message.setHeader("Priority", Mail.PR_NORMAL);
                message.setHeader("Importance", Mail.IMP_NORMAL);
            }
        }
         catch (MessagingException e)
        {
            logger.info("", e);
        }
    }

    public static Address getFrom(MimeMessage message)
    {
        String auxText = null;

        try
        {
            auxText = convertString(message.getFrom()[0].toString());
        }
         catch (Exception e)
        {
        }

        return (auxText != null) ? new MailAddress(auxText) : null;
    }

    public static Address getFromFax(MimeMessage message)
    {
        String auxText = null;

        try
        {
            auxText = convertFromFaxString(message.getFrom()[0].toString());
        }
         catch (Exception e)
        {
        }

        return (auxText != null) ? new MailAddress(auxText) : null;
    }

    public static Address getReplyTo(MimeMessage message)
    {
        String auxText = null;

        try
        {
            auxText = convertString(message.getReplyTo()[0].toString());
        }
         catch (Exception e)
        {
        }

        return (auxText != null) ? new MailAddress(auxText) : null;
    }

    public static MailAddress[] getAddress(Message.RecipientType addressType,
        MimeMessage message)
    {
        javax.mail.Address[] address = null;
        MailAddress[] toRet = null;

        try
        {
            address = message.getRecipients(addressType);
        }
         catch (Exception e)
        {
            address = null;
        }

        if (address != null)
        {
            int m = address.length;
            toRet = new MailAddress[m];

            for (int j = 0; j < m; j++)
                toRet[j] = new MailAddress(convertString(address[j].toString()));
        }

        return toRet;
    }

    public static Object getMailContent(MimeMessage message)
        throws MessagingException, IOException
    {
        Object content = null;

        try
        {
            content = message.getContent();
        }
         catch (Exception e)
        {
            getEncodedContentAsString(message);
        }

        return content;
    }

    /**
     * verify the signature (assuming the cert is contained in the message)
     */
    private static boolean verify(SMIMESigned s) throws boRuntimeException
    {
        try
        {
            //
            // extract the information to verify the signatures.
            //
            //
            // certificates and crls passed in the signature
            //
            Security.addProvider(new BouncyCastleProvider());
            CertStore certs = s.getCertificatesAndCRLs("Collection", "BC");

            //
            // SignerInfo blocks which contain the signatures
            //
            SignerInformationStore signers = s.getSignerInfos();

            Collection c = signers.getSigners();
            Iterator it = c.iterator();

            //
            // check each signer
            //
            while (it.hasNext())
            {
                SignerInformation signer = (SignerInformation) it.next();
                Collection certCollection = certs.getCertificates(signer.getSID());

                Iterator certIt = certCollection.iterator();
                X509Certificate cert = (X509Certificate) certIt.next();

                //
                // verify that the sig is correct and that it was generated
                // when the certificate was current
                //
                if (!signer.verify(cert, "BC"))
                {
//                    return "O email foi assinado no entanto a assinatura falhou.";
                      return false;
                }
            }
//            return "O email foi assinado e a assinatura foi verificada.";
            return true;
        }
         catch (Exception e)
        {
            logger.error("", e);
            return false;
        }
    }

    private static String getEncodedContentAsString(Part p)
    {
        InputStream is = null;
        InputStream isDecoded = null;
        DataHandler dh = null;
        String toRet = null;

        try
        {
            dh = p.getDataHandler();

            DataSource dsrc = dh.getDataSource();
            is = dsrc.getInputStream();

            // stream read/write buffer
            byte[] buffer = new byte[2048];
            StringBuffer contStr = new StringBuffer();
            int readCount = 0;
            String enconding = ((MimePart) p).getEncoding();

            if("base64".equalsIgnoreCase(enconding)||
                "quoted-printable".equalsIgnoreCase(enconding)||
                "7bit".equalsIgnoreCase(enconding)||
                "8bit".equalsIgnoreCase(enconding) ||
                "binary".equalsIgnoreCase(enconding))
            {
                isDecoded = MimeUtility.decode(is, ((MimePart) p).getEncoding());
                do
                {
                    // get data from input stream
                    readCount = isDecoded.read(buffer);

                    //MimeUtility.decode(is,"8bit");
                    // if not EOF, write the data to output stream.
                    if (readCount > -1)
                    {
                        contStr.append(new String(buffer, 0, readCount));
                    }
                }
                while (readCount > -1);
                isDecoded.close();
                toRet = new netgest.utils.ConvertUTF7(contStr.toString()).convertUTF7();
            }
            else if(enconding.toLowerCase().indexOf("utf-7") != -1)
            {
                do
                {
                    // get data from input stream
                    readCount = is.read(buffer);
                    // if not EOF, write the data to output stream.
                    if (readCount > -1)
                    {
                        contStr.append(new String(buffer, 0, readCount));
                    }
                }
                 while (readCount > -1);
                 toRet = new netgest.utils.ConvertUTF7(contStr.toString()).convertUTF7();
            }

            is.close();
        }
        catch (Exception e)
        {
            logger.error("", e);
            try
            {
                //caso do utf-7 ou sem encode
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                p.writeTo(bao);
                toRet = bao.toString();
                if ((p.getContentType() != null) &&
                        (p.getContentType().toUpperCase().indexOf("UTF-7") != -1))
                {
                    toRet = new ConvertUTF7((String) toRet).convertUTF7();
                }
            }
             catch (Exception _e)
            {
                //ignore
            }
        }
        return toRet;
    }


   private static String correctDecoding (String str)
   {
        try
        {
          if (str.indexOf("=?")!=-1 && str.indexOf("?=")!=-1)
          {
             StringTokenizer strTok = new StringTokenizer(str," ");
             String          token  = null;
             StringBuffer    strBuf = new StringBuffer("");

             while (strTok.hasMoreTokens())
             {
                while (strTok.hasMoreTokens() &&
                        ((token=strTok.nextToken()).indexOf("=?")==-1 ||
                        token.indexOf("?=")==-1))
                {
                   strBuf.append(token);
                   if(strTok.hasMoreTokens())
                   {
                    strBuf.append(" ");
                   }
                }

                if (token.indexOf("=?")!=-1 && token.indexOf("?=")!=-1)
                {
                   String tokenSauv=null;

                   while(token.indexOf("=?")!=-1 && token.indexOf("?=")!=-1)
                   {
                      int indexDeb = token.indexOf("=?");
                      strBuf.append(token.substring(0,indexDeb));
                      token = token.substring(indexDeb);
                      int codeIndex;

                      if ((codeIndex=token.indexOf("?Q?"))==-1)
                          codeIndex=token.indexOf("?P?");

                      int indexFin=token.indexOf("?=",codeIndex+3);

                      String codedStr=token.substring(0,indexFin+2);
                      token=token.substring(indexFin+2);

                      try
                      {
//                         System.out.println("CodeStr : "+codedStr);
                         strBuf.append(MimeUtility.decodeText(codedStr));
                      }
                      catch(java.io.UnsupportedEncodingException uEE)
                      {
                         strBuf.append(codedStr);
                      }
                   }
                   if (!token.equals(""))
                   {
                      strBuf.append(token);
                      if(strTok.hasMoreTokens())
                      {
                        strBuf.append(" ");
                      }
                   }
                }
                else if(strTok.hasMoreTokens())
                {
                    strBuf.append(token+" ");
                }
             }

             return strBuf.toString();
          }
          else
             return str;
      }
      catch(Exception e)
      {
        return str;
      }
    }

    private static String specialCase(String s)
    {
        try
        {
            boolean found = true;
            int start = -1;
            while(found)
            {
                start = s.indexOf("=?", start);
                if(start >=0)
                {
                    int end = s.indexOf("?=", start);
                    if(end >=0);
                    String subs = s.substring(start, end + 2);
                    subs = subs.replaceAll(" ", "=20");
                    String decodeStr = null;
                    try
                    {
                        decodeStr = MimeUtility.decodeText(subs);
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        decodeStr = subs;
                    }
                    s = s.substring(0, start) + decodeStr + s.substring(end + 2);
                    start = start + decodeStr.length();
                    found = s.indexOf("=?", start) >=0;
                }
                else
                {
                    found = false;
                }
            }
            return s;
        }
        catch(Exception e)
        {
            return s;
        }
    }

    public static Attach getBinaryMail(String name, String attachNum, MimeMessage msg)
    {
        FileOutputStream out = null;
        File tempFile = null;
        try
        {
            tempFile = createTempFile("binaryEmail", ".eml");
            out = new FileOutputStream(tempFile);
            try
            {
                msg.writeTo(out);
            }
            catch (MessagingException e)
            {
                Attach toRet = getBinaryMailAfterError(tempFile, out, msg);
                if(toRet == null)
                {
                    try
                    {
                        tempFile.delete();
                    }
                    catch (Exception _e)
                    {
                        //ignore
                    }
                }
                return toRet;
            }
            out.close();

            Attach att = new Attach(name, tempFile.getAbsolutePath(), "0", true, false);
            att.setInlineID("");
            return att;
        }
        catch (FileNotFoundException e)
        {
            logger.warn("", e);
        }
        catch (IOException e)
        {
            logger.warn("", e);
        }
        finally
        {
            try{if (out != null){out.close();}}catch (Exception e){/*ignore*/}
        }
        if(tempFile != null && tempFile.exists())
        {
            try{tempFile.delete();}catch (Exception _e){/*ignore*/}
        }
        return null;
    }

    private static Attach getBinaryMailAfterError(File tempFile, FileOutputStream fos, MimeMessage msg)
    {
        InputStream i = null;
        try
        {
            i = msg.getRawInputStream();
            copyFile(i, fos);
            fos.close();
            Attach att = new Attach("email.eml", tempFile.getAbsolutePath(), "0", true, false);
            att.setInlineID("");
        }
        catch(Exception e)
        {
            return null;
        }
        finally
        {
            try
            {
                if (i != null)
                {
                    i.close();
                }
            }
             catch (Exception e)
            {
            }
        }
        return null;
    }

    public static File createTempFile(String prefix, String suffix)
    {
        try
        {
            File xxx = File.createTempFile("netgest", ".none");
            String tmpPath = xxx.getParent();
            xxx.delete();
            xxx = new File(tmpPath + File.separator + "ngtbo");

            CleanDirectory.run( xxx );

            return TempFile.createTempDirFile(prefix, suffix, xxx);
        }
        catch (Exception e)
        {
            logger.warn("", e);
        }
        return null;
    }
}
