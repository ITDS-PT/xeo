package netgest.bo.data.mysql;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

import netgest.bo.data.Driver;
import netgest.bo.data.DriverUtils;
import netgest.bo.data.ReaderAdapter;
import netgest.bo.data.WriterAdapter;
import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class MysqlDriver implements Driver
{
    private String p_ddlds;
    private String p_dmlds;
    private String p_name;


    /**
     *
     * @since
     */
    public MysqlDriver()
    {
    }

	public ReaderAdapter createReaderAdapter(EboContext ctx)
    {
        return new MysqlReaderAdapter(p_dmlds);
    }

    public WriterAdapter createWriterAdapter(EboContext ctx)
    {
        return new MysqlWriterAdapter(p_dmlds);
    }

    public void initializeDriver(String name, String dmlDataSource,
        String ddlDataSource)
    {
        p_ddlds = ddlDataSource;
        p_dmlds = dmlDataSource;
        p_name = name;
    }

    public DriverUtils getDriverUtils()
    {
        return new MysqlUtils(p_dmlds);
    }

    private Connection getConnection(String dataSource, String username,
        String password)
    {
        final int retrycount = 5;
        int retries = 0;
        Connection ret = null;

        while (ret == null)
        {
            try
            {
                ret = getDataSource(dataSource).getConnection(username, password);
                // IMBR
//                ret.setAutoCommit(false);

                return ret;
            }
            catch (SQLException e)
            {
                if (retries >= retrycount)
                {
                    throw new RuntimeException(MessageLocalizer.getMessage("FAILED_TO_CREATE_CONNECTION")+
                        " \n" +
                        e.getClass().getName() + "\n" + e.getMessage());
                }
            }

            retries++;
        }

        return ret;
    }

    private static final Connection getConnection(String dataSource)
    {
        final int retrycount = 5;
        int retries = 0;
        Connection ret = null;

        while (ret == null)
        {
            try
            {
                ret = getDataSource(dataSource).getConnection();
                // IMBR
//                ret.setAutoCommit(false);

                return ret;
            }
            catch (SQLException e)
            {
                ret = null;
                if (retries >= retrycount) 
                {
                    throw new RuntimeException(MessageLocalizer.getMessage("FAILED_TO_CREATE_CONNECTION")+
                        " \n" +
                        e.getClass().getName() + "\n" + e.getMessage());
                }
            }

            retries++;
        }

        return ret;
    }

    private static final DataSource getDataSource(String dataSourceName)
    {
        try
        {
            final InitialContext ic = new InitialContext();

            return (DataSource) ic.lookup(dataSourceName);
        }
        catch (NamingException e)
        {
            throw new RuntimeException(MessageLocalizer.getMessage("ERROR_LOOKING_FOR_DATASOURCE_NAME")+" [" +
                dataSourceName + "].\n" + e.getMessage());
        }
    }

    public Connection getDedicatedConnection()
    {
       Connection cn=null;
        try
        {
            cn = getConnection(p_ddlds);
            cn.setAutoCommit(false);
        }
        catch (SQLException e) {
            System.out.println(MessageLocalizer.getMessage("FAILED_TO_OBTAIN_DEDICATED_CONNECTION_OR_DISABL_AUTOCOMIT"));
        }
        return cn;    
    }

    public Connection getConnection()
    {
        return getConnection(p_dmlds);
    }

    public Connection getDedicatedConnection(String username, String password)
    {
       Connection cn=null;
        try
        {
            cn = getConnection(p_ddlds, username, password);
            cn.setAutoCommit(false);
        }
        catch (SQLException e) {
            System.out.println(MessageLocalizer.getMessage("FAILED_TO_OBTAIN_DEDICATED_CONNECTION_OR_DISABL_AUTOCOMIT"));
        }
        return cn;
    }

    public Connection getConnection(String username, String password)
    {
        return getConnection(p_dmlds, username, password);
    }

    public OracleDBM getDBM()
    {
        return new MysqlDBM();
    }

    public String getName()
    {
        return p_name;
    }

    public void setName(String name)
    {
        p_name = name;
    }

	public String getEscapeCharEnd() {
		return "`";
	}

	public String getEscapeCharStart() {
		return "`";
	}

	public long getDBSequence( EboContext ctx, String sequenceName, int dsType, int OPER) {
		
		Connection cn;
		switch( dsType ) {
			case Driver.SEQUENCE_SYSTEMDS:
				cn = ctx.getConnectionSystem();
				break;
			default:
				cn = ctx.getConnectionData();
				break;
		}
		
		PreparedStatement cstm = null;
		ResultSet	rslt = null;
		try {
			cstm = cn.prepareStatement( "select nextval(?)" );
			cstm.setString(  1, sequenceName );
			cstm.execute();
			rslt = cstm.executeQuery();
			rslt.next();
			BigDecimal val = rslt.getBigDecimal( 1 );
			cstm.close();
			if( val == null ) {
				synchronized( MysqlDriver.class ) {
					PreparedStatement pstm = null;
					try {
						pstm = cn.prepareStatement( "insert into ( sequence_name ) values ( ? )" );
						pstm.setString( 1, sequenceName );
						pstm.executeUpdate();
					}
					catch( SQLException e ) {
						throw new RuntimeException( e );
					}
					finally {
						pstm.close();
					}
					cstm = cn.prepareStatement( "select nextval(?)" );
					cstm.setString(  1, sequenceName );
					cstm.execute();
					rslt = cstm.executeQuery();
					rslt.next();
					val = rslt.getBigDecimal( 1 );
					cstm.close();
					rslt.close();
				}
			}
			if( val != null )
				return val.longValue();
			
			throw new RuntimeException("XEO MYSQL Driver: "+MessageLocalizer.getMessage("ERROR_OBTAINING_SEQUENCE")+" [" + sequenceName + "] ");
		} catch (SQLException e) {
			throw new RuntimeException( e );
		}
		finally {
			try {
				if( rslt != null ) {
					rslt.close();
				}
				if( cstm != null ) {
					cstm.close();
				}
			} catch (SQLException e) {
			}
		}
	}

    public String getDatabaseTimeConstant() {
		return "NOW()";
	}
    
    public boolean validateConnection(Connection cn)
    {
    	return true;
    }
	
}
