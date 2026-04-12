package helps;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

public class JWTOps {
	
	public static String createJWT(String subject, String issuer,String clientRole,String clientLogin, String clientDisplayName, long expirationMinutes, String secretKeyBase64Encoded) {
		String JWTString="";
	     // We need a signing key
       Key key = Keys.hmacShaKeyFor(secretKeyBase64Encoded.getBytes(java.nio.charset.StandardCharsets.UTF_8));
       
       Instant now = Instant.now();
       Date issuedAt = Date.from(now);
       Date expiration = Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES));

       JWTString = Jwts.builder()
               .setSubject(subject) // The subject/principal of the token
               .setIssuer(issuer) // Who created the token
               .setIssuedAt(issuedAt) // When the token was issued
               .setExpiration(expiration) // Time when the token will expire
               .setNotBefore(issuedAt) // Time before which the token is not yet valid
               .setId(java.util.UUID.randomUUID().toString()) // Unique identifier for the token
               .signWith(key, SignatureAlgorithm.HS256) // Sign the JWT using the key and algorithm
               .claim("log", clientLogin)
               .claim("dis", clientDisplayName)
               .claim("rol", clientRole)
               .compact(); // Build the JWT and serialize it to a compact, URL-safe string
      
		return JWTString;
	}
	
	public static Claims verifyJWT(String token, String secretKeyBase64Encoded) {
		 
		SecretKey KEY =
	            Keys.hmacShaKeyFor(secretKeyBase64Encoded.getBytes(StandardCharsets.UTF_8));
		try {
		        return Jwts.parserBuilder()
		                .setSigningKey(KEY)
		                .build()
		                .parseClaimsJws(token)
		                .getBody();

		    } catch (ExpiredJwtException e) {
		        throw new RuntimeException("JWT expired", e);

		    } catch (UnsupportedJwtException e) {
		        throw new RuntimeException("Unsupported JWT", e);

		    } catch (MalformedJwtException e) {
		        throw new RuntimeException("Malformed JWT", e);

		    } catch (SignatureException e) {
		        throw new RuntimeException("Invalid signature", e);

		    } catch (IllegalArgumentException e) {
		        throw new RuntimeException("JWT string is empty", e);
		    }
	}
}
