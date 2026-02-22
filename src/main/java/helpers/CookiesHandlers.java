package helpers;

import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.ws.rs.core.NewCookie;
import java.util.Arrays;

public abstract class CookiesHandlers {
    
    public static final String AUTH_COOKIE_NAME = "awt_jwt";
    private static final String SECRET_KEY = "your-very-secure-secret-key-here";

    /**
     * Creates a fixed 64-character token.
     * [32 chars random payload][32 chars signature]
     */
    public static CookieData createAuthCookie() throws Exception {
        // 1. Generate 16 bytes of random data (results in 32 hex chars)
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        String rawPayload = bytesToHex(bytes); // 32 chars

        // 2. Sign the payload and take the first 16 bytes of the HMAC (32 hex chars)
        String signature = hmacSha256Short(rawPayload, SECRET_KEY); // 32 chars

        // 3. Combine into exactly 64 characters (no dot, no base64)
        String fixedToken64 = rawPayload + signature;

        NewCookie cookie = new NewCookie(
            AUTH_COOKIE_NAME, 
            fixedToken64,       
            "/api",           
            null,             
            "WTI auth token", 
            3600,             
            false,            
            true              
        );

        return new CookieData(fixedToken64, cookie);
    }

    /**
     * Verifies the signature by splitting the 64-char token at the halfway point.
     */
    public static boolean verifyTokenSignature(String token) {
        try {
            // Must be exactly 64 characters
            if (token == null || token.length() != 64) {
                return false;
            }

            // Split: first 32 is payload, last 32 is signature
            String payload = token.substring(0, 32);
            String providedSignature = token.substring(32);

            String expectedSignature = hmacSha256Short(payload, SECRET_KEY);

            return expectedSignature.equals(providedSignature);
        } catch (Exception e) {
            return false;
        }
    }

    private static String hmacSha256Short(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(secretKey);
        byte[] hashBytes = mac.doFinal(data.getBytes());
        
        // Truncate to 16 bytes so the hex string is exactly 32 characters
        byte[] shortHash = Arrays.copyOfRange(hashBytes, 0, 16);
        return bytesToHex(shortHash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static class CookieData {
        private final String token;
        private final NewCookie cookie;

        public CookieData(String token, NewCookie cookie) {
            this.token = token;
            this.cookie = cookie;
        }

        public String getToken() { return token; }
        public NewCookie getCookie() { return cookie; }
    }

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