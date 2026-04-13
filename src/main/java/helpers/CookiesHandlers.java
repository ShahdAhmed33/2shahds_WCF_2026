package helpers;

import javax.servlet.http.Cookie;
import javax.ws.rs.core.NewCookie;
import exceptions.NoCookieException;
import exceptions.NotVerifiedCookieException;

/**
 * All cookies operations: creating, signing, and verifying cookies.
 */
public class CookiesHandlers {

    // 1. Consistency is key: Use this constant EVERYWHERE
    private static final String SecretKey = "D0ntEverL0gHereAgainDudeInAnyACPCContest";
    public static final String AUTH_COOKIE_NAME = "awt_jwt";

    /**
     * Verifies the JWT inside the CookieID value.
     */
    public static boolean verifyCookie(String CookieID) throws NotVerifiedCookieException {
        try {
            JWTOps.verifyJWT(CookieID, SecretKey);
            return true;
        } catch (RuntimeException e) {
            // Re-throw as your custom exception for the API to catch
            throw new NotVerifiedCookieException("Invalid or Expired Cookie: " + e.getMessage());
        }
    }

    /**
     * Generates a JWT string to be used as a Cookie ID.
     */
    public static String genCookieID(String clientRole, String clientLogin, String clientDisplayName) {
        // Expiration set to 300 minutes
        return JWTOps.createJWT("auth", "acpc", clientRole, clientLogin, clientDisplayName, 300, SecretKey);
    }

    /**
     * Extracts the value of a specific cookie from the request.
     */
    public static String getCookie(Cookie[] cookies, String name) throws NoCookieException {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        // If we reach here, the cookie header exists but our specific cookie doesn't, or cookies are null
        throw new NoCookieException("Authentication required.");    }

    /**
     * Creates a NewCookie object with security flags.
     * Path is set to "/" to ensure all @Path endpoints can see it.
     */
    public static NewCookie createCookie(String CookieID) {
        return new NewCookie(
            AUTH_COOKIE_NAME,       // name: "awt_jwt"
            CookieID,               // value: The JWT
            "/",                    // path: Root path so all APIs see it
            null,                   // domain
            "WTI JWT auth token",   // comment
            18000,                  // maxAge: 300 minutes in seconds (300 * 60)
            false,                  // secure: Set to true only if using HTTPS
            true                    // httpOnly: CRITICAL - prevents JS from stealing the token
        );
    }

    /**
     * Helper for manually building a Header string if needed.
     */
    public static String getCookieHeader(String token) {
        return AUTH_COOKIE_NAME + "=" + token + 
               "; Path=/" + 
               "; Max-Age=18000" + 
               "; HttpOnly" + 
               "; SameSite=Strict";
    }
}