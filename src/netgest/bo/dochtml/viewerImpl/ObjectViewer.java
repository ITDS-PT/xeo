/*Enconding=UTF-8*/
package netgest.bo.dochtml.viewerImpl;
import java.sql.SQLException;

import netgest.bo.dochtml.docHTML;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.utils.*;
import org.w3c.dom.Element;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public interface ObjectViewer 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public void setContextObject(boObject objTarget);
    public boObject getContextObject();

 
    
    public StringBuffer getCARDIDwNoIMG() throws boRuntimeException;

    public StringBuffer getCARDIDwNoIMG(boolean cut) throws boRuntimeException;

    public String getSrcForIcon16() throws boRuntimeException;

    public StringBuffer getCARDID() throws boRuntimeException;
    
    public StringBuffer getCARDID(boolean cut) throws boRuntimeException;
    public Element getCARDID(ngtXMLHandler xmlToPrint, Element root, boolean cut) throws boRuntimeException;

    public StringBuffer getCARDIDwLink() throws boRuntimeException;
    
    public StringBuffer getCARDIDwLink(boolean doubleEscape) throws boRuntimeException;

    public StringBuffer getCARDIDwLink(boolean doubleEscape,String extraParameters )
        throws boRuntimeException;
    public StringBuffer getCARDIDwLink(boolean newPage, boolean doubleEscape,String extraParameters )
        throws boRuntimeException;

    public StringBuffer getURL() throws boRuntimeException;

    public StringBuffer getExplainProperties(docHTML doc) throws boRuntimeException;

    public StringBuffer getCARDIDwState() throws boRuntimeException;
    public StringBuffer getCARDIDwStatewLink() throws boRuntimeException;

    public StringBuffer getSTATUS() throws boRuntimeException, SQLException;

    public StringBuffer getTextCARDID() throws boRuntimeException;

    public String getICONComposedState() throws boRuntimeException;

    public String getStringComposedState() throws boRuntimeException;
    
    public StringBuffer getOpenObjectScript() throws boRuntimeException;
    
    public String getLabel() throws boRuntimeException;
    
}