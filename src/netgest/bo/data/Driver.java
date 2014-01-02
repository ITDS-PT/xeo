/*Enconding=UTF-8*/
package netgest.bo.data;

import java.sql.Connection;

import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.runtime.EboContext;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public interface Driver
{
	public static final byte SEQUENCE_NEXTVAL = 0;
	public static final byte SEQUENCE_CURRENTVAL = 1;

	public static final byte SEQUENCE_SYSTEMDS = 0;
	public static final byte SEQUENCE_DATADS = 1;

	public ReaderAdapter createReaderAdapter(EboContext ctx);

    public WriterAdapter createWriterAdapter(EboContext ctx);

    public DriverUtils getDriverUtils();

    public void initializeDriver(String name, String dmlDataSource,
        String ddlDataSource);

    public Connection getConnection();

    public Connection getDedicatedConnection();

    public Connection getConnection(String username, String password);

    public Connection getDedicatedConnection(String username, String password);

    public OracleDBM getDBM();

    public String getName();
    
    public String getEscapeCharStart();
    
    public String getEscapeCharEnd();
    
    public long getDBSequence( EboContext ctx, String sequenceName, int dsType, int OPER );
    
    public String getDatabaseTimeConstant();
    
    public boolean validateConnection(Connection cn);
    
    
}
