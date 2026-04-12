package helps;

import javax.servlet.http.Cookie;
import javax.ws.rs.core.NewCookie;

import exceptions.NoCookieException;
import exceptions.NotVerifiedCookieException;

/*
 * All cookies operations, creating, sign, verify cookie
 */
public class CookiesOps {
	
	private static String SecretKey="D0ntEverL0gHereAgainDudeInAnyACPCContest";
	
	/*
	 * Return true if valud cookie
	 */
	//To be implemented, verify CookieID value 
	public static boolean verifyCookie(String CookieID) throws NotVerifiedCookieException {
		/*boolean verifiedCookie = true;

		
		if ( ! verifiedCookie ) 
			throw new NotVerifiedCookieException("Invalid/Not signed cookie");
		return true;
		*/
		try {
		JWTOps.verifyJWT(CookieID, SecretKey);
		return true;
		} catch (RuntimeException e) {
			throw e;		}
	}
	/*
	 * Return string as cookie id
	 */
	//To be implemented, create the CookieID and sign it
	public static String genCookieID(String clientRole,String clientLogin,String clientDisplayName) {
		String ID = JWTOps.createJWT("auth", "acpc",clientRole,clientLogin, clientDisplayName, 300, SecretKey);

		return ID;
	}
	
	
	/*
	 * Receive list of cookies and return value of one of cookies
	 */
	public static String getCookieValue(Cookie[] cookies, String name) throws NoCookieException {
		String res = null;
		
		if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if (name.equals(cookie.getName())) {
	                res = cookie.getValue();
	            }
	        }
	    } else
	    	throw new NoCookieException("Cookie identified is not found");
		return res;
	}
	
	
	/*
	 * Create a new cookies object from the cookie id
	 */
	public static NewCookie createCookie(String CookeID) {
		// 4) Build HttpOnly cookie containing the JWT
        //    - name: "jwt"
        //    - path: "/api" -> only sent to your API endpoints
        //    - maxAge: 3600 seconds (must match token expiry ideally)
        //    - httpOnly: true -> JS cannot read it (XSS protection)
        //    - secure: true in production (HTTPS only)
        NewCookie jwtCookie = new NewCookie(
                "awt-jwt",                 // name
                CookeID,                 // value
                "/api",                // path
                null,                  // domain (null -> current host)
                "WTI JWT auth token",      // comment
                3600,                  // max age in seconds
                false,                 // secure (set true when using HTTPS)
                true                   // httpOnly
        );
        
        return jwtCookie;
	}
}
