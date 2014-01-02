package netgest.utils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLSchemaErrors implements ErrorHandler
{
    public XMLSchemaErrors()
    {
    }
    public void error(SAXParseException exception) throws SAXException
    {
        exception.printStackTrace();
    }
    
    public void fatalError(SAXParseException exception) throws SAXException
    {
        exception.printStackTrace();
    }
    
    public void warning(SAXParseException exception) throws SAXException
    {
        exception.printStackTrace();
    }
}