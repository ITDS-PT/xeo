/*Enconding=UTF-8*/
package netgest.bo.mapConstructor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.StringBuffer;
public class HTMLBuilder {
    private static final String HTML_BEGIN = "<HTML>";
    private static final String HEAD_BEGIN = "<HEAD>";    
    private static final String NEW_LINE = "\r\n";
    private static final String HTML_END = "</HTML>";
    private static final String HEAD_END = "</HEAD>";
    private static final String LEVEL_BEGIN = "<UL><UL>";    
    private static final String LEVEL_END = "</UL></UL>";
    private static final String ELEMENT_BEGIN = "<LI>";
    private static final String ELEMENT_END = "</LI>";
    private static final String IMAGE_1 = "<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' title='";;
    private static final String IMAGE_2 = "' src='";
    private static final String IMAGE_3 = "'/>";
    private static final String OBJ_INVALID_MSG_ERROR = "<p><font color=\"#FF0000\">Objecto inválido ou inexistente</font></p>";
    private static final String NODE_BEGIN = "<li>";
    private static final String NODE_END = "</li>";
    private static final String NODE_STYLE_BEGIN = "<p align=\"left\">";
    private static final String NODE_STYLE_END = "</p>";
    private static final String LABEL_BEGIN = "<font size=\"1\"><b>Label: </b>";
    private static final String LABEL_END = "</font>";   
    private static final String DESCRIPTION_BEGIN = "<font size=\"1\"><b>Description: </b>";
    private static final String DESCRIPTION_END = "</font>"; 
    
    public static void writeBegin(StringBuffer out) throws IOException {
        out.append(HTML_BEGIN);
        out.append(NEW_LINE);
        out.append(HEAD_BEGIN);
        out.append(NEW_LINE);
        out.append(NEW_LINE);
    }

    public static void writeEnd(StringBuffer out) throws IOException {
        out.append(HTML_END);
    }

    public static void writeEndHead(StringBuffer out) throws IOException {
        out.append(HEAD_END);
    }

    public static void writeBeginLine(StringBuffer out, boolean big, boolean right) throws IOException {

    }
    public static void writeEndLine(StringBuffer out) throws IOException {

    }    
  
    public static void writeInvalidObject(StringBuffer out) throws IOException {
        out.append(OBJ_INVALID_MSG_ERROR);
    }

    public static void startSons(StringBuffer out) throws IOException {    
        out.append(LEVEL_BEGIN);
    }
    public static void writeNode(StringBuffer out, String nodeName) throws IOException {
        out.append(NODE_BEGIN);
        out.append(NEW_LINE);
        out.append(NODE_STYLE_BEGIN);        
        out.append(nodeName);
        out.append(" ");
        out.append(NODE_STYLE_END);
        out.append(NEW_LINE);
        out.append(NODE_END);
    }
    public static void writeNode(StringBuffer out, String nodeName, String imageURL) throws IOException {
        out.append(NODE_BEGIN);
        out.append(NEW_LINE);
        out.append(NODE_STYLE_BEGIN);
        out.append(IMAGE_1);
        out.append(imageURL);
        out.append(IMAGE_2);
        out.append(imageURL);
        out.append(IMAGE_3);
        out.append(nodeName);
        out.append(" ");
        out.append(NODE_STYLE_END);
        out.append(NEW_LINE);
        out.append(NODE_END);
    }    
    public static void writeNode(StringBuffer out, String nodeName, String imageURL, String desc, String label) throws IOException {
        out.append(NODE_BEGIN);
        out.append(NEW_LINE);
        out.append(NODE_STYLE_BEGIN);
        out.append(IMAGE_1);
        out.append(imageURL);
        out.append(IMAGE_2);
        out.append(imageURL);
        out.append(IMAGE_3);
        out.append(nodeName);
        out.append(" ");
        out.append(LABEL_BEGIN);
        out.append(label);
        out.append(LABEL_END);
        out.append(" ");
        out.append(DESCRIPTION_BEGIN);
        out.append(desc);
        out.append(DESCRIPTION_END);            
        out.append(NODE_STYLE_END);
        out.append(NEW_LINE);
        out.append(NODE_END);
    }
    public static void writeNodeDesc(StringBuffer out, String nodeName, String imageURL, String desc) throws IOException {
        out.append(NODE_BEGIN);
        out.append(NEW_LINE);
        out.append(NODE_STYLE_BEGIN);
        out.append(IMAGE_1);
        out.append(imageURL);
        out.append(IMAGE_2);
        out.append(imageURL);
        out.append(IMAGE_3);
        out.append(nodeName);
        out.append(" ");
        out.append(DESCRIPTION_BEGIN);
        out.append(desc);
        out.append(DESCRIPTION_END);            
        out.append(NODE_STYLE_END);
        out.append(NEW_LINE);
        out.append(NODE_END);
    }    
    public static void writeNodeLabel(StringBuffer out, String nodeName, String imageURL, String label) throws IOException {
        out.append(NODE_BEGIN);
        out.append(NEW_LINE);
        out.append(NODE_STYLE_BEGIN);
        out.append(IMAGE_1);
        out.append(imageURL);
        out.append(IMAGE_2);
        out.append(imageURL);
        out.append(IMAGE_3);
        out.append(nodeName);
        out.append(" ");
        out.append(LABEL_BEGIN);
        out.append(label);
        out.append(LABEL_END);            
        out.append(NODE_STYLE_END);
        out.append(NEW_LINE);
        out.append(NODE_END);
    }
    public static void endSons(StringBuffer out) throws IOException {    
        out.append(LEVEL_END);
    }
    public static void writeDescription(StringBuffer out, String desc) throws IOException {
        out.append(DESCRIPTION_BEGIN);
        out.append(desc);
        out.append(DESCRIPTION_END);    
    }
    
    public static void writeLabel(StringBuffer out, String label) throws IOException {
        out.append(LABEL_BEGIN);
        out.append(label);
        out.append(LABEL_END);
        
    }
    
}
