/*Enconding=UTF-8*/
package netgest.bo.report;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import netgest.bo.boConfig;
import netgest.bo.dochtml.docHTML;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.utils.DifferenceContainer;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XSLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public final class ShowDiff 
{
    
    public static void createObjectCardReport( DifferenceContainer diffContainer,docHTML doc, boObject srcObj ,boObject dstObj, String ViewerName ,String formName, PrintWriter out ,EboContext boctxDst )
    {
            try
            {
                boConfig cnf= new boConfig();
                
                String template=cnf.getDeployJspDir()+"templateReports//showDiffHTML.xml";
                XMLDocument xmlTemplate = ngtXMLUtils.loadXMLFile( template );
                
                String xml=ngtXMLUtils.getXML( xmlTemplate );
                String mydate = DateFormat.getDateTimeInstance( DateFormat.FULL,DateFormat.FULL ).format( new java.util.Date() );
                xml=xml.replaceAll("#DATE#",  mydate );
//                boObject performer = boObject.getBoManager().loadObject( doc.getEboContext(),"Ebo_Perf", doc.getEboContext().getBoSession().getPerformerBoui()  );
                boObject performer =null;
                boObjectList list = boObjectList.list(doc.getEboContext(), "select iXEOUser where boui = " + doc.getEboContext().getBoSession().getPerformerBoui());
                list.beforeFirst();
                if(list.next())
                {
                    performer = list.getObject();
                } 

                
                xml=xml.replaceAll("#USERNAME#", performer.getAttribute("name").getValueString()  );
                xml=xml.replaceAll("#REPORTNAME#", MessageLocalizer.getMessage("DIFFERENCES_BETWEEN_OBJECTS_OF_TYPE")+" " + srcObj.getBoDefinition().getLabel());                                            
                xml=xml.replaceAll("#URL#", doc.getEboContext().getApplicationUrl() );                
                xml=xml.replaceAll("#CARDID#", srcObj.getCARDID().toString() );
                xml=xml.replaceAll("#CARDIDDST#", dstObj.getCARDID().toString() );
                                
                ngtXMLHandler xmlToPrint= new ngtXMLHandler( xml );                             
                
                Node  body = xmlToPrint.getDocument().selectSingleNode("//div[@class='reportBody']");                                              
               
                if ( srcObj.getBoDefinition().hasForm(ViewerName,formName ) )
                {
                    // Resumo das Diferenças
                    if(!diffContainer.isEmpty())
                    {

                        Element ex = xmlToPrint.getDocument().createElement("div");
                        ex.setAttribute("class","resumeTitle");
                        ex.appendChild( xmlToPrint.getDocument().createTextNode(MessageLocalizer.getMessage("DIFFERENCES_RESUME")) );
                        body.appendChild(ex);
                        body.appendChild(xmlToPrint.getDocument().createElement("br")); 
                        
                        buildXML.buildDifferenceResume(doc,diffContainer,srcObj, dstObj, xmlToPrint.getDocument(), body, boctxDst) ;
                        
                        Element ex2 = xmlToPrint.getDocument().createElement("div");
                        ex2.setAttribute("class","resumeTitleBottom");
                        ex2.appendChild( xmlToPrint.getDocument().createTextNode(MessageLocalizer.getMessage("END_OF_RESUME")) );
                        body.appendChild(ex2);
                    }
                  
                    if ( srcObj.isChanged() )
                    {
                         Element e = xmlToPrint.getDocument().createElement("div");
                         e.setAttribute("class","note");
                         e.appendChild( xmlToPrint.getDocument().createTextNode(MessageLocalizer.getMessage("NOTE_SOME_OF_THIS_DATA_IS_STILL_NOT_SAVED")) );
                         body.appendChild(e);
                        
                    }
                    
                    buildXML.buildCard(diffContainer, doc, srcObj , dstObj, null ,null, ViewerName , formName , xmlToPrint.getDocument(), body ,  srcObj.getBoDefinition().getViewer(ViewerName).getForm( formName) );                   

                    if ( srcObj.isChanged() )
                    {
                         Element e = xmlToPrint.getDocument().createElement("div");
                         e.setAttribute("class","note");
                         e.appendChild( xmlToPrint.getDocument().createTextNode(MessageLocalizer.getMessage("NOTE_SOME_OF_THIS_DATA_IS_STILL_NOT_SAVED")) );
                         body.appendChild(e);
                        
                    }
                }
                else
                {
                    Node reportName = xmlToPrint.getDocument().createTextNode( MessageLocalizer.getMessage("DEFINITIONS_NOT_FOUND"));
                    body.appendChild( reportName );
                }
                
                xml = ngtXMLUtils.getXML( xmlToPrint.getDocument() );

                xml=xml.replaceAll("<!\\[CDATA\\[","");
                xml=xml.replaceAll("\\]\\]>","");
                xml=xml.replaceAll("NBSP","&nbsp;");
                
                xml=xml.replaceAll("#STATE#", srcObj.getICONComposedState() );
                xml=xml.replaceAll("#STATUS#", srcObj.getSTATUS().toString()+"  ["+srcObj.getBoui() +"]" );

                out.write( xml.toCharArray() );
                
            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
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