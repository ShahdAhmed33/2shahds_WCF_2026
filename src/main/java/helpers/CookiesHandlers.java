package helpers;
import javax.servlet.http.Cookie;
import javax.ws.rs.core.NewCookie;
public class CookiesHandlers {
    public static final String AUTH_COOKIE_NAME = "awt_jwt";

    /**
     * CREATES the cookie to be sent to the client.
     */
    public static NewCookie createAuthCookie(String tokenValue) {
        return new NewCookie(
            AUTH_COOKIE_NAME, 
            tokenValue,       //soso
            "/api",           // Path
            null,             // Domain
            "WTI auth token", 
            3600,             // 1 hour expiry
            false,            // Secure (set true for HTTPS)
            true              // HttpOnly (Security: prevents JS access)
        );
    }

    /**
     * READS the cookie value from the incoming request.
     */
    public static String getCookie(Cookie[] cookies, String tokenName) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}