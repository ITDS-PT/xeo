/*Enconding=UTF-8*/
package netgest.utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import netgest.bo.localizations.MessageLocalizer;
import oracle.xml.parser.schema.XMLSchema;
import oracle.xml.parser.schema.XSDBuilder;
import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XMLParseException;
import oracle.xml.parser.v2.XMLParser;
import oracle.xml.parser.v2.XSLException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class ngtXMLUtils  {
    
  public static final void saveXML( XMLDocument xmldoc , String file )
  {
      saveXML( xmldoc , new File ( file ) );
  }
  
  public static final void saveXML( XMLDocument xmldoc , File file )
  {
     
      try
      {
          xmldoc.setVersion(xmldoc.getVersion());
          xmldoc.setEncoding(xmldoc.getEncoding());
          FileOutputStream fo = new FileOutputStream( file , false );
          xmldoc.print( fo );
          fo.close();
      }
      catch (FileNotFoundException e)
      {
          throw new RuntimeException( MessageLocalizer.getMessage("ERROR_FILE_NOT_FOUND")+e.getMessage() );
      }
      catch (IOException e)
      {
          throw new RuntimeException( MessageLocalizer.getMessage("ERROR_WRITING_TO")+" ["+file.getAbsolutePath()+"].\n"+e.getMessage() );
      }
  }
  public static XMLDocument loadXML(InputStream input)
  {
        try {            
            XMLDocument xmldoc = parseXML( input );
            input.close();
            return (XMLDocument)xmldoc;
        } 
        catch (IOException e) { 
            throw new RuntimeException(MessageLocalizer.getMessage("IO_ERROR_IN_THE_XML_INPUTSTREAM")+e.getMessage()+"\n"+e.getMessage()+"\n XML Source:\n"+input);
        }
        catch (SAXException e) 
        { 
            throw new RuntimeException(MessageLocalizer.getMessage("ERROR_IN_THE_XML_PARSING")+e.getMessage()+"\n"+e.getMessage()+"\n "+MessageLocalizer.getMessage("XML_SOURCE")+":\n"+input);
        }      
  }
  
  private static final XMLDocument parseXML( InputStream in ) throws XMLParseException, SAXException, IOException
  {
      DOMParser parser = new DOMParser();
      parser.setPreserveWhitespace( false );
      parser.parse( in );
      return parser.getDocument();
  }
  
  public static XMLDocument loadXMLFile(String filename) 
  {
        try {
            FileInputStream cr = new FileInputStream(filename);
            Document xmldoc = parseXML( cr );
            cr.close();
            return (XMLDocument)xmldoc;
        } 
        catch (IOException e) { 
            throw new RuntimeException(MessageLocalizer.getMessage("XML_FILE_IO_ERROR")+e.getMessage()+"\n"+e.getMessage()+"\n "+MessageLocalizer.getMessage("XML_SOURCE")+":\n"+filename);
        }
        catch (SAXException e) 
        { 
            throw new RuntimeException(MessageLocalizer.getMessage("ERROR_IN_THE_XML_PARSING")+e.getMessage()+"\n"+e.getMessage()+"\n "+MessageLocalizer.getMessage("XML_SOURCE")+":\n"+filename);
        }
  }  
  
  public static XMLDocument loadXMLFile_FileReader(String filename) throws RuntimeException 
  {
        DOMParser xmlp = new DOMParser();
        try {
            FileReader cr = new FileReader(filename);
            xmlp.parse(cr);
            cr.close();
            return xmlp.getDocument();
        } 
        catch (IOException e) { 
            throw(new RuntimeException(MessageLocalizer.getMessage("XML_FILE_IO_ERROR")+e.getMessage()+"\n "+MessageLocalizer.getMessage("XML_SOURCE")+":\n"+filename));
        }
        catch (SAXException e) { 
            throw(new RuntimeException(MessageLocalizer.getMessage("ERROR_IN_THE_XML_PARSING")+e.getMessage()+"\n "+MessageLocalizer.getMessage("XML_SOURCE")+":\n"+filename));
        }
  }  
  
  public static byte[] getXMLBytes(XMLDocument xml) 
  {
      try {
          ByteArrayOutputStream bo = new ByteArrayOutputStream();
          xml.print(bo,"UTF-8");
          return bo.toByteArray();
      } catch (IOException e) {
              throw new RuntimeException(MessageLocalizer.getMessage("ERROR_EXTRACTING_XML_FROM_XMLDOCUMENT")+"\n"+"Utils.getXML");
      }
  }
  public static String getXML(XMLDocument xml) 
  {
      try {
          CharArrayWriter cw = new CharArrayWriter(16000);
          PrintWriter pr = new PrintWriter(cw);
          xml.print(pr);
          pr.flush();
          pr.close();
          cw.flush();
          return cw.toString();
      } catch(IOException e) {
            throw new RuntimeException(MessageLocalizer.getMessage("ERROR_EXTRACTING_XML_FROM_XMLDOCUMENT")+"\n"+"Utils.getXML");
      }
  }
  public static XMLDocument loadXML(byte[] xmldoc) 
  {
        try {
            ByteArrayInputStream cr = new ByteArrayInputStream(xmldoc);
            Document xml = parseXML( cr );
            cr.close();
            return (XMLDocument)xml;
        } 
        catch (IOException e) { 
            throw new RuntimeException(MessageLocalizer.getMessage("ERROR_IN_THE_XML_PARSING")+"\n"+e.getMessage()+"\n "+MessageLocalizer.getMessage("XML_SOURCE")+":\n"+xmldoc+"\n"+"Utils.loadXML");
        }
        catch (SAXException e) { 
            throw new RuntimeException(MessageLocalizer.getMessage("ERROR_IN_THE_XML_PARSING")+"\n"+e.getMessage()+"\n "+MessageLocalizer.getMessage("XML_SOURCE")+":\n"+xmldoc+"\n"+"Utils.loadXML");
        }
  }
  
//  public static final DocumentBuilder getDocumentBuilder()
//  {
//        try {
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            dbf.setIgnoringElementContentWhitespace( true );
//            dbf.setCoalescing( true );
//            dbf.setIgnoringComments( true );
//            DocumentBuilder doc = dbf.newDocumentBuilder();
//            return doc;
//        }
//        catch (ParserConfigurationException e) 
//        { 
//            throw new RuntimeException( "ParserConfigurationException\n" + e.getMessage() );
//        }
//  }
  
  public static XMLDocument loadXML(String xmldoc) 
  {
        try {
            ByteArrayInputStream cr = new ByteArrayInputStream( xmldoc.getBytes("UTF-8") );
            Document xml = parseXML( cr );
            return (XMLDocument)xml;
        } 
        catch (IOException e) { 
            throw new RuntimeException(MessageLocalizer.getMessage("ERROR_IN_THE_XML_PARSING")+"\n"+e.getMessage()+"\n "+MessageLocalizer.getMessage("XML_SOURCE")+":\n"+xmldoc+"\n"+"Utils.loadXML");
        }
        catch (SAXException e) { 
            throw new RuntimeException(MessageLocalizer.getMessage("ERROR_IN_THE_XML_PARSING")+"\n"+e.getMessage()+"\n "+MessageLocalizer.getMessage("XML_SOURCE")+":\n"+xmldoc+"\n"+"Utils.loadXML");
        }
  }
  
    public static final boolean validateXmlwithSchema( String documentFile, String schemaFile )
    {
        try
        {
            validateXmlwithSchema( new URL( "file:/"+documentFile ), new URL( "file:/"+schemaFile ) );
            return false;
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException( e );
        }
    }

  
    public static final boolean validateXmlwithSchema( URL documentUrl, URL schemaUrl )
    {
        try
        {
            DOMParser domParser=new DOMParser();  
            domParser.setValidationMode(XMLParser.SCHEMA_VALIDATION);
            XSDBuilder builder = new XSDBuilder();
            XMLSchema schemadoc = (XMLSchema)builder.build( schemaUrl );
            domParser.setXMLSchema(schemadoc);
            
            ErrorHandler handler=new XMLSchemaErrors(); 
            domParser.setErrorHandler( handler );
            domParser.parse( documentUrl );
            return true;
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
  
  
  public static final int MERGE_BY_ATTNAME_OR_NODENAME = 0;
  public static final int MERGE_BY_ATTNAME_OR_NODEINDEX = 1;
  
  public static void mergeNodes(XMLNode src,XMLNode dest, int mode ) {
      // Merge Attributes
      String srcNodeName = src.getNodeName();
      String destNodeName = dest.getNodeName();
      
      NamedNodeMap srcatts = src.getAttributes();
      for (short i = 0;srcatts != null && i < srcatts.getLength(); i++)  {
          NamedNodeMap destatts = dest.getAttributes();        
          if(destatts==null || destatts.getNamedItem(srcatts.item(i).getNodeName())==null) {
              dest.getAttributes().setNamedItem(dest.getOwnerDocument().importNode(srcatts.item(i),true));
          }
      }
      // Merge Nodes
      try 
      {
          NodeList srcchilds = src.getChildNodes();
          for (short i = 0; i < srcchilds.getLength(); i++)  
          {
              String nodeid;
              Node srcChild  = srcchilds.item(i);
              Node destChild = null; 
              if(srcChild.getAttributes()!=null && srcChild.getAttributes().getNamedItem("name")!=null
                )
              {
                  nodeid=srcChild.getAttributes().getNamedItem("name").getNodeValue();
                  NodeList clist = dest.getChildNodes();
                  for(short k=0;k<clist.getLength();k++) 
                  {
                      if(clist.item(k).getAttributes()!=null && 
                         clist.item(k).getAttributes().getNamedItem("name") != null &&
                         clist.item(k).getAttributes().getNamedItem("name").getNodeValue().equals(nodeid)
                        )
                      {
                          destChild = clist.item(k); 
                      }
                  }
              } 
              else 
              {
                  if( srcChild.getNodeType() == Node.ELEMENT_NODE &&  mode == MERGE_BY_ATTNAME_OR_NODENAME )
                  {
                        String xpath = srcChild.getNodeName();
                        destChild = dest.selectSingleNode( xpath );
                  }
                  else
                  {
                      destChild = dest.getChildNodes().item(i);
                      if(destChild!= null && destChild.getAttributes()!=null && destChild.getAttributes().getNamedItem("name")!=null) 
                      {
                            destChild=null;
                      }
                  }
              }

              if(destChild==null) 
              {
                  destChild = dest.getOwnerDocument().importNode(srcchilds.item(i),true);
                  dest.appendChild(destChild);
              } 
              else 
              { 
                  if(destChild.getNodeName().equals(srcchilds.item(i).getNodeName())) 
                  {
                      if(destChild.getNodeType()==Node.ELEMENT_NODE) 
                      {
                          mergeNodes((XMLNode)srcchilds.item(i),(XMLNode)destChild, mode);
                      }
                  } 
                  else 
                  {
                      dest.insertBefore(dest.getOwnerDocument().importNode(srcchilds.item(i),true),destChild);    
                  }
              }
          }
         sortNodesByAttOrder(dest); 
          
      } 
      catch (XSLException e) 
      {
        throw new RuntimeException(e.getMessage());
      } finally {};
  }
  
  public static final void sortNodesByAttOrder(Node node) 
  {
      NodeList nodes = node.getChildNodes();
      boolean ordered=false;
      for (int k = 0; k < nodes.getLength(); k++) 
      {
        if(nodes.item(k).getAttributes()!=null && nodes.item(k).getAttributes().getNamedItem("order")!=null) { 
            if(Integer.parseInt(nodes.item(k).getAttributes().getNamedItem("order").getNodeValue())==-1) {
                node.removeChild(nodes.item(k));
                k--;
            } 
            else 
            {
                ordered = true;    
            }
        }
      }
      if(ordered) {
          nodes = node.getChildNodes();
          Node[] anodes = new Node[nodes.getLength()];
          for (int k = 0; k < anodes.length; k++) 
          {
            anodes[k] = nodes.item(k);
          }
          ObjectSorter sort = new ObjectSorter();
          sort.sort(anodes,new ObjectSorter.Comparer() {
                public long compare(Object l,Object r) 
                {
                    Node left=(Node)l;
                    Node right=(Node)r;
                    int vr=0;
                    int vl=0;
                    Node aleft=null,aright=null;
                    
                    if(left.getAttributes()!=null && (aleft=left.getAttributes().getNamedItem("order"))!=null) 
                    {
                        vl = Integer.parseInt(aleft.getNodeValue());
                    } 
                    if(right.getAttributes()!=null && (aright=right.getAttributes().getNamedItem("order"))!=null) 
                    {
                        vr = Integer.parseInt(aright.getNodeValue());
                    }
                    return vl-vr;         
                }
            }
          );
          for (int k = 0; k < anodes.length; k++) 
          {
            node.removeChild(anodes[k]);
            if(anodes[k].getAttributes()!=null && anodes[k].getAttributes().getNamedItem("order")!=null) { 
                if(Integer.parseInt(anodes[k].getAttributes().getNamedItem("order").getNodeValue())!=-1) {
                    node.appendChild(anodes[k]);
                }
            } 
            else 
            {
                node.appendChild(anodes[k]);
            }
          }
      }
  }
  
  
  public static void print(Node node, StringBuffer out) {

   // is there anything to do?
   if ( node == null ) {
      return;
   }

   int type = node.getNodeType();
   switch ( type ) {
      // print document
      case Node.DOCUMENT_NODE: {
			Document doc = (Document)node;
			DocumentType dt = doc.getDoctype();
			String systemId = null;
			String publicId = null;
			String dtName = null;
			if (dt != null){
				systemId = dt.getSystemId();
				publicId = dt.getPublicId();
				dtName = dt.getName();
			}

			out.append("<?xml version=\"1.0\"");
			if (dt != null){
				out.append(" standalone=\"no\"?>");
                
				out.append("<!DOCTYPE " + dtName);
				if (systemId != null)
					out.append(" SYSTEM \"" + systemId + "\">");
				else if (publicId != null)
					out.append(" PUBLIC \"" + publicId + "\">");
				else
					out.append("\">");
			}
			else
				out.append("?>");

			print(doc.getDocumentElement(), out);
			
			break;
      }
      // print element with attributes
      case Node.ELEMENT_NODE: {
			out.append('<');
			out.append(node.getNodeName());
			Attr attrs[] = sortAttributes(node.getAttributes());
			for ( int i = 0; i < attrs.length; i++ ) {
				Attr attr = attrs[i];
				out.append(' ');
				out.append(attr.getNodeName());
				out.append("=\"");
				out.append(normalize(attr.getNodeValue()));
				out.append('"');
			}
			out.append('>');
			NodeList children = node.getChildNodes();
			if ( children != null ) {
				int len = children.getLength();
				for ( int i = 0; i < len; i++ ) {
					print(children.item(i), out);
				}
			}
			break;
		}
      // handle entity reference nodes
      case Node.ENTITY_REFERENCE_NODE: {
			if ( EXPAND_ENTITIES ) {
				NodeList children = node.getChildNodes();
				if ( children != null ) {
					int len = children.getLength();
					for ( int i = 0; i < len; i++ ) {
						print(children.item(i), out);
					}
				}
			} else {
				out.append('&');
				out.append(node.getNodeName());
				out.append(';');
			}
			break;
		}
      // print cdata sections
      case Node.CDATA_SECTION_NODE: {
			if ( EXPAND_CDATA_SECTION ) {
				out.append(normalize(node.getNodeValue()));
			} else {
				out.append("<![CDATA[");
				out.append(node.getNodeValue());
				out.append("]]>");
			}
			break;
		}
      // print text
      case Node.TEXT_NODE: {
			out.append(normalize(node.getNodeValue()));
			break;
		}
      // print processing instruction
      case Node.PROCESSING_INSTRUCTION_NODE: {
			out.append("<?");
			out.append(node.getNodeName());
			String data = node.getNodeValue();
			if ( data != null && data.length() > 0 ) {
				out.append(' ');
				out.append(data);
			}
			out.append("?>");
			break;
		}
   }

   if ( type == Node.ELEMENT_NODE ) {
      out.append("</");
      out.append(node.getNodeName());
      out.append(">");
   }

   

} // print(Node)

private static final boolean EXPAND_ENTITIES = false;
private static final boolean EXPAND_CDATA_SECTION = false;

/** Returns a sorted list of attributes. */
private static Attr[] sortAttributes(NamedNodeMap attrs) {

   int len = (attrs != null) ? attrs.getLength() : 0;
   Attr array[] = new Attr[len];
   for ( int i = 0; i < len; i++ ) {
      array[i] = (Attr)attrs.item(i);
   }
   for ( int i = 0; i < len - 1; i++ ) {
      String name  = array[i].getNodeName();
      int    index = i;
      for ( int j = i + 1; j < len; j++ ) {
         String curName = array[j].getNodeName();
         if ( curName.compareTo(name) < 0 ) {
            name  = curName;
            index = j;
         }
      }
      if ( index != i ) {
         Attr temp    = array[i];
         array[i]     = array[index];
         array[index] = temp;
      }
   }

   return (array);

} // sortAttributes(NamedNodeMap):Attr[]


/** Normalizes the given string. */
private static String normalize(String s) {
   StringBuffer str = new StringBuffer();

   int len = (s != null) ? s.length() : 0;
   for ( int i = 0; i < len; i++ ) {
      char ch = s.charAt(i);
      switch ( ch ) {
         case '<': {
               str.append("&lt;");
               break;
            }
         case '>': {
               str.append("&gt;");
               break;
            }
         case '&': {
               str.append("&amp;");
               break;
            }
         case '"': {
               str.append("&quot;");
               break;
            }
         case '\r':
         case '\n': {
               if ( EXPAND_ENTITIES ) {
                  str.append("&#");
                  str.append(Integer.toString(ch));
                  str.append(';');
                  break;
               }
               // else, default append char
            }
         default: {
               str.append(ch);
            }
      }
   }

   return (str.toString());

} // normalize(String):String

  
}