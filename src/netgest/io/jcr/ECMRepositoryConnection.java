package netgest.io.jcr;

import java.util.Map;

import javax.jcr.Session;

/**
 * 
 * This interface is used to create a {@link Session} instance
 * for an ECM Repository
 * 
 * @author Pedro Rio
 *
 */
public interface ECMRepositoryConnection {
	
		/**
		 * 
		 * Retrieves a session with a repository
		 *
		 * @param username The username to connect to the repository
		 * @param password The password of the username 
		 * @param parameters A set of parameters to initialize connection
		 * 
		 * @return A session with the repository
		 */
		public Session getConnection(String username, String password, Map<String,String> parameters);

}
