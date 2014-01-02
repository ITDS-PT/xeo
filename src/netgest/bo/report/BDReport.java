/*Enconding=UTF-8*/
package netgest.bo.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import java.io.OutputStreamWriter;
import java.sql.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import netgest.bo.boConfig;
import netgest.bo.builder.boBuildRepository;
import netgest.bo.builder.boBuilder;
import netgest.bo.def.boDefHandler;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boRepository;
import netgest.bo.system.Logger;

/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class BDReport
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.report.BDReport");

    private static boConfig p_bcfg = new boConfig();
    private String packageName;
    private String fileName;
    private EboContext ebo;
    private Connection conn;
    private boBuildRepository repository;

    /**
     *
     * @Company Enlace3
     * @since
     */
    public BDReport(EboContext ebo, String packageName, String fileName)
    {
        this.ebo = ebo;
        repository = new boBuildRepository(ebo.getBoSession().getRepository());
        this.packageName = packageName;
        this.fileName = fileName;
        this.conn = ebo.getConnectionDef();
    }

    public String createHtmlReport() throws boRuntimeException
    {
        boolean mybuild = false;

        try
        {
            boRepository rep = boRepository.getDefaultRepository( boApplication.getApplicationFromConfig("XEO") );
            boBuildRepository brep = new boBuildRepository( rep );
            
            boDefHandler defs[] = boBuilder.listUndeployedDefinitions( brep, null );
            ArrayList toReport = new ArrayList();            
            
            
            //dúvidas
            //File eboobjdir = new File(p_bcfg.getDefinitiondir());
                        
            File[] xfiles = repository.getXMLFilesFromDefinition();
            Vector todeploy = new Vector();

            if ((packageName != null) && !"".equals(packageName))
            {
                for (int i = 0; i < defs.length; i++)
                {
                    if (xfiles[i].getName().toLowerCase().endsWith(".xeomodel"))
                    {
                        int pos = xfiles[i].getName().indexOf(".xeomodel");
                        if (xfiles[i].getAbsolutePath().indexOf(packageName) != -1)
                        {
                            for (int x = 0; x < defs.length; x++) 
                            {
                                if(defs[x].getName().equals(xfiles[i].getName().substring(0, pos)))
                                {
                                    toReport.add(defs[x]);
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                toReport.addAll( Arrays.asList( defs ) );
            }

            createHtmlReport(ebo, toReport);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return MessageLocalizer.getMessage("COULD_NOT_GENERATE_REPORT")+": " +
            e.getLocalizedMessage() + ".";
        }
        finally
        {
//            try
//            {
//                if (conn != null)
//                {
//                    conn.close();
//                }
//            }
//            catch (Exception e)
//            {
//            }
        }

        return MessageLocalizer.getMessage("REPORT_GENERATED_SUCCESSFULLY");
    }

    private void createHtmlReport(EboContext ebo, ArrayList files)
        throws Exception
    {
        File xboObject = null;
        boDefHandler bodef;
        ArrayList objList = new ArrayList();
        XMLObject xmlOb;

        for (int i = 0; i < files.size(); i++)
        {
            //String s = ((File) files.get(i)).getName();

            //            logger.finest("--------" + s);
            //            if(s.equals("forumtopic$bo.xml"))
            //            {
            //                logger.finest("STOP");
            //            }
            //s = s.substring(0, s.indexOf("$"));
            //bodef = boDefHandler.getBoDefinition(s);
            bodef = (boDefHandler)files.get( i );

            if ((bodef.getClassType() != boDefHandler.TYPE_ABSTRACT_CLASS) &&
                    (bodef.getClassType() != boDefHandler.TYPE_INTERFACE))
            {
                xmlOb = new XMLObject(conn);
                xmlOb.setXMLObject(ebo, bodef);
                objList.add(xmlOb);
            }
        }

        //ordenar a list pelas nome das tabelas
        Collections.sort(objList, new TableComparator());
        giveCap(objList);

        BDHTMLBuilder bdHtml = new BDHTMLBuilder(objList);
        String codeHtml = bdHtml.generate();
        File f = new File(fileName);
        FileOutputStream fos = new FileOutputStream(f);
        OutputStreamWriter outW = new OutputStreamWriter(fos, "UTF-8");
        outW.write(codeHtml.toCharArray());        
        outW.close();
        fos.close();
    }

    private void giveCap(ArrayList arr)
    {
        String lastTable = null;
        int lastCap = 1;

        for (int i = 0; i < arr.size(); i++)
        {
            if ((lastTable == null) ||
                    !((XMLObject) arr.get(i)).getTableName().equals(lastTable))
            {
                ((XMLObject) arr.get(i)).setCap(getCap(lastCap));
                ((XMLObject) arr.get(i)).setAnchor(getAnchor(lastCap));
                lastTable = ((XMLObject) arr.get(i)).getTableName();
                lastCap++;
            }
            else
            {
                ((XMLObject) arr.get(i)).setAnchor(getAnchor(lastCap));
            }
        }
    }

    private static String getCap(int i)
    {
        if (i < 10)
        {
            return "000" + i;
        }

        if (i < 100)
        {
            return "00" + i;
        }

        if (i < 1000)
        {
            return "0" + i;
        }

        return String.valueOf(i);
    }

    private static String getAnchor(int i)
    {
        return "a" + String.valueOf(i);
    }
}
