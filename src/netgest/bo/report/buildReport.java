/*Enconding=UTF-8*/
package netgest.bo.report;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;
import javax.naming.*;
import netgest.bo.*;
import netgest.bo.dochtml.*;
import netgest.bo.ejb.*;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.*;
import netgest.bo.runtime.robots.*;
import netgest.bo.system.*;
import netgest.utils.*;
import netgest.system.*;
import netgest.bo.builder.*;
import javax.transaction.*;
import javax.sql.*;
import java.sql.*;
import oracle.xml.parser.v2.*;
//import org.faceless.report.ReportParser;
//import org.faceless.pdf2.PDF;
import org.w3c.dom.*;
//import org.w3c.tidy.*;
import org.xml.sax.*;

/**
 * 
 * @author JMF
 */
public class buildReport 
{
    public buildReport()
    {
    }
    
    
    public static void createPDF(File xf, OutputStream out)
    {
    try
    {
        //ReportParser parser = ReportParser.getInstance();
       // CharArrayReader cr = new CharArrayReader( xmlfile.toCharArray() ); 
       // InputSource ip = new InputSource( cr );
        //File xf= new File( "templateReports\\boform.xml");
      // PDF  pdf = parser.parse(xf);
       // pdf.render(out);
            
    }
    catch( Exception e)
    {
        e.printStackTrace();
    }
    
    }
    
    public static void createObjectCardReport( docHTML doc, boObject o , String ViewerName ,String formName, OutputStreamWriter out )
    {
    
            try
            {
                
                boConfig cnf= new boConfig();
                
                String template=cnf.getDeployJspDir()+"templateReports//boformHTML.xml";
                XMLDocument xmlTemplate = ngtXMLUtils.loadXMLFile( template );
                
                String xml=ngtXMLUtils.getXML( xmlTemplate );
                String mydate = DateFormat.getDateTimeInstance( DateFormat.FULL,DateFormat.FULL ).format( new java.util.Date() );
                xml=xml.replaceAll("#DATE#",  mydate );
                boObject performer = null;//boObject.getBoManager().loadObject( doc.getEboContext(),"Ebo_Perf", doc.getEboContext().getBoSession().getPerformerBoui()  );
                boObjectList list = boObjectList.list(doc.getEboContext(), "select iXEOUser where boui = " + doc.getEboContext().getBoSession().getPerformerBoui());
                list.beforeFirst();
                if(list.next())
                {
                    performer = list.getObject();
                }
                
                xml=xml.replaceAll("#USERNAME#", performer.getAttribute("name").getValueString()  );
                if ( o.getMode() == o.MODE_EDIT_TEMPLATE )
                {   
                    boObject tmpl = o.getAttribute("TEMPLATE").getObject();
                    
                    xml=xml.replaceAll("#REPORTNAME#", "Ficha do Modelo  "+ tmpl.getAttribute("name").getValueString() );
                }
                else
                {
                    xml=xml.replaceAll("#REPORTNAME#", "Ficha de "+o.getBoDefinition().getLabel()  );    
                }
                
                
                xml=xml.replaceAll("#URL#", doc.getEboContext().getApplicationUrl() );
                
               xml=xml.replaceAll("#CARDID#", o.getCARDID(false).toString()  );
                                
                ngtXMLHandler xmlToPrint= new ngtXMLHandler( xml.replaceAll("&","&amp;") ); 
                
                
                //Node x= xmlToPrint.getDocument().selectSingleNode("//p[@id='reportName']");
                // Node reportName = xmlTemplate.createTextNode(" TESTE ");
                //x.appendChild( reportName );
                
                Node  body = xmlToPrint.getDocument().selectSingleNode("//div[@class='reportBody']");
                
                
                
               
                if ( o.getBoDefinition().hasForm(ViewerName,formName ) )
                {
                    //uild( docHTML doc, boObject o, XMLDocument dom  ,XMLNode node ,  ngtXMLHandler xmlForm )
                    if ( o.isChanged() )
                    {
                         Element e = xmlToPrint.getDocument().createElement("div");
                         e.setAttribute("class","note");
                         e.appendChild( xmlToPrint.getDocument().createTextNode(MessageLocalizer.getMessage("NOTE_SOME_OF_THIS_DATA_IS_STILL_NOT_SAVED")) );
                         body.appendChild(e);
                        
                    }
                    buildXML.buildCard( doc, o , null , ViewerName , formName , xmlToPrint.getDocument(), body ,  o.getBoDefinition().getViewer(ViewerName).getForm( formName) ,"");
                    if ( o.getMode() == o.MODE_EDIT_TEMPLATE )
                    {
                    
                         Element e = xmlToPrint.getDocument().createElement("div");
                         e.setAttribute("class","templateTitle");
                         e.appendChild( xmlToPrint.getDocument().createTextNode("Resumo do Modelo") );
                         body.appendChild(e);
                         
                         boObject tmpl = o.getAttribute("TEMPLATE").getObject();
                         formName="edit";
                         buildXML.buildCard( doc, tmpl , null , ViewerName , formName , xmlToPrint.getDocument(), body ,  tmpl.getBoDefinition().getViewer(ViewerName).getForm( formName) ,"");
                         
                    }
                    if ( o.isChanged() )
                    {
                         Element e = xmlToPrint.getDocument().createElement("div");
                         e.setAttribute("class","note");
                         e.appendChild( xmlToPrint.getDocument().createTextNode("NOTA : Alguns destes dados ainda não estão gravados") );
                         body.appendChild(e);
                        
                    }
                }
                else
                {
                    Node reportName = xmlToPrint.getDocument().createTextNode( " Definições não encontradas?!!");
                    body.appendChild( reportName );
                }
                
                xml = ngtXMLUtils.getXML( xmlToPrint.getDocument() );
                //]]>
                xml=xml.replaceAll("<!\\[CDATA\\[","");
                xml=xml.replaceAll("\\]\\]>","");
                xml=xml.replaceAll("NBSP","&nbsp;");
                
                xml=xml.replaceAll("#STATE#", o.getICONComposedState() );
                xml=xml.replaceAll("#STATUS#", o.getSTATUS().toString()+"  ["+o.getBoui() +"]" );
              //  CharArrayReader cr = new CharArrayReader( xml.toCharArray() );
                out.write( xml );
                
                //InputSource ip = new InputSource( cr );
                
              //  ReportParser parser = ReportParser.getInstance();
                
              //  PDF  pdf = parser.parse( ip );
              //  pdf.render(out);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
          //  catch (SAXException e)
          //  {
          //      e.printStackTrace();
          //  }
            catch ( boRuntimeException e )
            {
                e.printStackTrace();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            catch (XSLException e)
            {
                e.printStackTrace();
            }
            
            
            
        
        
    }
    
    
    
}