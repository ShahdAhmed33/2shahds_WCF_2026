package helpers;
import javax.servlet.http.Cookie;

public class CookiesHandlers {

	public static String getCookie(Cookie[] cookies, String token) {
        String res = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (token.equals(cookie.getName())) {
                    res = cookie.getValue();
                    break; // Recommended: stop searching once found
                }
            }
        }
        return res;
    }
}
