package netgest.bo.data.constraints;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import netgest.bo.data.WriterException;


/**
 * 
 * Class that reports (throws an exception to be processed higher in the chain)
 * Unique Constraint violations
 * 
 * @author PedroRio
 *
 */
public class UniqueContraintViolationReporter {

	private Pattern pattern;
	private String query;
	
	/**
	 * @param patternExpr A regex that identifies the constraint name from an SQL Exception message string
	 * @param query Query to execute that returns the name of the column that triggered the violation 
	 */
	public UniqueContraintViolationReporter(String patternExpr, String query){
		this.pattern = Pattern.compile( patternExpr );
		this.query = query;
	}
	
	public void reportUniqueContraint(Connection conn, SQLException e) throws WriterException{
		
		List<String> columnNames = new LinkedList<String>();
		
		Matcher matcher = pattern.matcher(e.getMessage());
		if (matcher.find())
		{
		    String constraint = matcher.group(1);
		    PreparedStatement st = null;
		    ResultSet set = null;
		    try {
		    	st = conn.prepareStatement( query );
		    	st.setString( 1, constraint );
		    	if (st.execute()){
					set = st.getResultSet();
					
					while (set.next()){
						String columnName = set.getString( "COLUMN_NAME" );
						if (columnName.endsWith( "$" ))
							columnName = columnName.substring( 0 , columnName.length() - 1 );
						columnNames.add( columnName );
					}
				}
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
				throw new WriterException(WriterException.UNIQUE_KEY_VIOLATED,
					    e.getMessage(), e, columnNames);
			} finally {
				closeDatabaseResources( conn, st, set );
			}
		}
		throw new WriterException(WriterException.UNIQUE_KEY_VIOLATED,
		    e.getMessage(), e, columnNames);
		
	}

	private void closeDatabaseResources( Connection conn, PreparedStatement st, ResultSet set ) {
		if (st != null){
			try {
				st.close();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
			}
		}
		if (set != null){
			try {
				set.close();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
			}
		}
		if (conn != null){
			try {
				conn.close();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
			}
		}
	}

}
