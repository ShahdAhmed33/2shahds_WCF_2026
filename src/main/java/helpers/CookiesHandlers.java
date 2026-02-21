package helpers;
import java.security.SecureRandom;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.ws.rs.core.NewCookie;
import edu.csus.ecs.pc2.convert.Base64;
public abstract class CookiesHandlers {
	
    public static final String AUTH_COOKIE_NAME = "awt_jwt";
    private static final String SECRET_KEY = "change-this-secret-key";
    /**
     * CREATES the cookie to be sent to the client.
     */
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
    public static CookieData createAuthCookie() throws Exception {
    	//SecureRandom random = new SecureRandom();
    	byte[] bytes = new byte[32];
    	//random.nextBytes(bytes);
    	String tokenValue = Base64.encode(bytes);
    //	String signature = hmacSha256(tokenValue, SECRET_KEY);
    	String signature = hmacSha256(tokenValue, SECRET_KEY);

    	//String tokenValue= UUID.randomUUID().toString();
         NewCookie cookie= new NewCookie(
            AUTH_COOKIE_NAME, 
            tokenValue,       
            "/api",           // Path
            null,             // Domain
            "WTI auth token", 
            3600,             // 1 hour expiry
            false,            // Secure (set true for HTTPS)
            true              // HttpOnly (Security: prevents JS access)
        );
        return new CookieData(tokenValue, cookie);
    }

 
    
    private static String hmacSha256(String data, String secret) throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec secretKey =
                new SecretKeySpec(secret.getBytes(), "HmacSHA256");

        mac.init(secretKey);

        byte[] hash = mac.doFinal(data.getBytes());

        return Base64.encode(hash);
    }
    
    
    
    
    
    public static class CookieData {
        private final String token;
        private final NewCookie cookie;

        public CookieData(String token, NewCookie cookie) {
            this.token = token;
            this.cookie = cookie;
        }

        public String getToken() {
            return token;
        }

        public NewCookie getCookie() {
            return cookie;
        }
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