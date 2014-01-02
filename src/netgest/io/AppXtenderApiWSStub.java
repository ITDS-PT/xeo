package netgest.io;
import oracle.soap.transport.http.OracleSOAPHTTPConnection;
import org.apache.soap.encoding.SOAPMappingRegistry;
import java.net.URL;
import org.apache.soap.rpc.Call;
import org.apache.soap.Constants;
import java.util.Vector;
import org.apache.soap.rpc.Parameter;
import org.apache.soap.rpc.Response;
import org.apache.soap.Fault;
import org.apache.soap.SOAPException;
import java.util.Properties;
/**
 * Generated by the Oracle JDeveloper 10g Web Services Stub/Skeleton Generator.
 * Date Created: Thu Aug 30 17:39:12 BST 2007
 * WSDL URL: http://localhost:8989/xeoRemoteConversion/AppXtenderApiWS?WSDL
 * 
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */

public class AppXtenderApiWSStub 
{
    public AppXtenderApiWSStub()
    {
        m_httpConnection = new OracleSOAPHTTPConnection();
        m_smr = new SOAPMappingRegistry();
    }

    private String _endpoint = "http://lusworkflow.lusitania-cs.pt/xeoRemoteConversion/AppXtenderApiWS";

    public String getEndpoint()
    {
        return _endpoint;
    }

    public void setEndpoint(String endpoint)
    {
        _endpoint = endpoint;
    }

    private OracleSOAPHTTPConnection m_httpConnection = null;
    private SOAPMappingRegistry m_smr = null;

    public String[] getPagesFiles(String appName, Long docId) throws Exception
    {
        String[] returnVal = null;

        URL endpointURL = new URL(_endpoint);
        Call call = new Call();
        call.setSOAPTransport(m_httpConnection);
        call.setTargetObjectURI("AppXtenderApiWS");
        call.setMethodName("getPagesFiles");
        call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);

        Vector params = new Vector();
        params.addElement(new Parameter("appName", String.class, appName, null));
        params.addElement(new Parameter("docId", Long.class, docId, null));
        call.setParams(params);

        call.setSOAPMappingRegistry(m_smr);

        Response response = call.invoke(endpointURL, "");

        if (!response.generatedFault())
        {
            Parameter result = response.getReturnValue();
            returnVal = (String[])result.getValue();
        }
        else
        {
            Fault fault = response.getFault();
            throw new SOAPException(fault.getFaultCode(), fault.getFaultString());
        }

        return returnVal;
    }

    public void setMaintainSession(boolean maintainSession)
    {
        m_httpConnection.setMaintainSession(maintainSession);
    }

    public boolean getMaintainSession()
    {
        return m_httpConnection.getMaintainSession();
    }

    public void setTransportProperties(Properties props)
    {
        m_httpConnection.setProperties(props);
    }

    public Properties getTransportProperties()
    {
        return m_httpConnection.getProperties();
    }
}