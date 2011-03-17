package netgest.bo.impl.document.print;

import java.util.Vector;
import netgest.utils.IOUtils;
import java.io.*;
import netgest.utils.ngtXMLUtils;
import netgest.utils.ngtXMLHandler;
import oracle.xml.parser.v2.XMLElement;
import org.w3c.dom.Node;
import netgest.bo.boConfig;
import netgest.bo.impl.document.print.remote.ConvertImagesStub;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.system.boApplication;
import java.nio.channels.FileLock;

public class PDFConvert
{

    public static String APPLICATION_MSWORD = "doc";
    
    public PDFConvert()
    {
    }
    
    protected static String getTMPDirectory() 
    {
        String tmp = System.getProperty("java.io.tmpdir");
        File f = new File(tmp+File.separator+"XEOPREVIEWCACHE");
        if (!f.exists())
            f.mkdir();
        return tmp+File.separator+"XEOPREVIEWCACHE";
    }
    
    static String getPDFDirectory() throws RuntimeException
    {
        ngtXMLHandler xnode;       
        try 
        {
            boApplication.getApplicationFromStaticContext("XEO").addAContextToThread();
            ngtXMLHandler root = new ngtXMLHandler(ngtXMLUtils.loadXMLFile( boConfig.getNgtHome()+"boconfig.xml" ).getDocumentElement());
            xnode = root.getChildNode("pdf").getChildNode("path");
        } 
        catch (Exception e) 
        {
            xnode = null;   
        }
        
        if (xnode == null) 
        {
            throw new RuntimeException(MessageLocalizer.getMessage("THE_PDF_CONVERTER_WAS_NOT_DEFINED_IN_THE_BOCONFIG_FILE"));
        }
               
        return xnode.getText();
    }
    
/*    
    // tem sempre que indicar o caminho completo dos ficheiros
    static public void convert( String fileName, String outputFileName, boolean useCache ) throws RuntimeException
    {
        // retira a terminaÃ§Ã£o do nome do ficheiro
        int index = fileName.lastIndexOf('.');
        if (index == -1)
        {
            throw new RuntimeException("NÃ£o foi indicado a terminaÃ§Ã£o do ficheiro.");
        }
        
        // fileType guarda a terminaÃ§Ã£o do ficheiro, assume o texto entre o ultimo . e o fim da string
        String fileType = fileName.substring(index+1, fileName.length()).toLowerCase();
        
        // se for um documento do Microsoft Word
        if (fileType.equals(APPLICATION_MSWORD) )
        {
            long init = System.currentTimeMillis();
                
//            String programDef = getPDFDirectory();
//            fileName = fileName.replaceAll("\\\\", "\\\\\\\\");
//            programDef = programDef.replaceFirst("#FILENAME#", fileName );
//            
//            try
//            {
//                Process process = Runtime.getRuntime().exec(programDef);
//                process.waitFor();
//            }
//            catch (InterruptedException e)
//            {
//            }
//            catch (IOException e)
//            {
//                throw new RuntimeException("Erro na ConversÃ£o " + fileName);   
//            }
        
            
            try
            { 
                ConvertToImagesV2Stub cisStub = new ConvertToImagesV2Stub();
                cisStub.setEndpoint("http://localhost:8888/xeoRemoteConversion/ConvertImages");
                String pdfFileName = cisStub.converIFile( fileName, "0" , "pdf", useCache ? Boolean.TRUE:Boolean.FALSE );
            }
            catch( Exception e )
            {
                e=e;
            }

            String pdfFileName = fileName.replaceFirst("\\.doc","\\.pdf");     
            File outFile = new File( pdfFileName );            
                
            if( !outFile.getAbsolutePath().equalsIgnoreCase( outputFileName ) )
            {
                IOUtils.copy( outFile, outputFileName );
                outFile.delete();
            }
        }
        else
        {
            throw new RuntimeException("ConversÃ£o para PDF nÃ£o suportada para ficheiros " + fileType);  
        }
        
    
    }
*/    

    static public void convert( String oper, String user, String doc, String out, boolean useCache )
    {
        try
        {
            File fdoc = new File(doc);
            byte[] fbytes = new byte[ (int)fdoc.length() ];
            FileInputStream fin = new FileInputStream( fdoc );
            fin.read( fbytes );
            fin.close();
            byte[] bout = convert( oper, user, fdoc.getName(), fbytes, useCache );
            FileOutputStream fout = new FileOutputStream( out );
            fout.write( bout );
            fout.close();
            
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);            
        }
        
        
    }

    static public byte[] convert( String oper, String user, String mimeType, byte[] file, boolean useCache )
    {
        if (mimeType.toLowerCase().equals("application/msword"))
        {
            long time = System.currentTimeMillis();
            try {
                ConvertImagesStub cisStub = new ConvertImagesStub();

                cisStub.setEndpoint(
                    boApplication.getApplicationFromStaticContext("XEO")
                        .getApplicationConfig()
                        .getConvertImagesEndPoint()
                );

                String[] files = cisStub.convertBytes( oper, user, "tmp" + time + "." + APPLICATION_MSWORD, file, String.valueOf( time ), "pdf" );
                return cisStub.getCachedFile( files[0] );
            } 
            catch (Exception e) 
            {
                throw new RuntimeException(e);    
            }
        }
        else
        {
            throw new RuntimeException(MessageLocalizer.getMessage("CONVERSION_TO_PDF_IS_NOT_SUPPORTED_FOR_FILES")+" " + mimeType);     
        }  
    }
    
    static public void convert( String oper, String user, String mimeType, InputStream file, OutputStream outputPdf, boolean useCache )
    {
        try 
        {
            int b;
            Vector vin = new Vector();
            
            do 
            {
                b = file.read();
                if (b != -1)
                {
                    vin.add(new Integer(b));
                }
            } 
            while (b!= -1);
            
            byte[] in = new byte[vin.size()];
            
            for (int i=0; i < in.length; i++)
                in[i] = (byte)Integer.parseInt(vin.get(i).toString());
                
            byte[] out = convert(oper, user, mimeType, in, useCache);
            outputPdf.write(out, 0, out.length);
            
        }  
        catch (IOException e)
        {
            throw new RuntimeException(MessageLocalizer.getMessage("PROBLEMS_CONVERTING_TO_PDF"), e);
        }
    }


    /**
     *
     * @param args
     */
    public static void main(String[] args) throws Exception
    {

    }
    
}
