package helps;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import edu.csus.ecs.pc2.api.ServerConnection;
import exceptions.NoCookiesExceptions;
import exceptions.NoHttpRequestExcepion;
import exceptions.NoServerConnectionException;
import exceptions.NotLoggedIn;

/*
 * All routings that check a value, and raise an exception in case of failure
 */
public class Checkers {

	/*
	 * Check if the authenitcation header is exist or no
	 */
	public static void authHeader(final @Context HttpServletRequest req) throws NoHttpRequestExcepion {
		if ( req == null ) {
			throw new NoHttpRequestExcepion("Missing authenticaiton header");
		}
	}
	
	
	/*
	 * Check if there is cookies part 
	 */
	public static void checkCookies(Cookie[] c) throws NoCookiesExceptions {
		if ( c == null) {
			throw new NoCookiesExceptions("No coockies, may be disbaled, enable it");
		}
	}
	
	/*
	 * Check if the there is connection in hashmap
	 */
	public static void isConnectionExist(ServerConnection c) throws NoServerConnectionException {
		if ( c == null ) {
			throw new NoServerConnectionException("Server connection is not exist");
		}
	}
	
	public static void isConnected(ServerConnection c) throws NotLoggedIn {
		if ( c.isLoggedIn() == false  ) 
			throw new NotLoggedIn("Not logged in to PC2 server");
	}
}
