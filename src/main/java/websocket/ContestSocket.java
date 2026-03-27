package websocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import controllers.maincontroller;
import helpers.CookiesHandlers;
@WebSocket
public class ContestSocket {
	
	private static final Map<Session, String> connections = new ConcurrentHashMap<>();
	
	
	@OnWebSocketConnect
	public void onConnect(Session session) {
	    System.out.println("WS CONNECTED");
	    try {
	        // Read from query parameter
	       /* String token = session.getUpgradeRequest()
	                              .getParameterMap()
	                              .getOrDefault("token", java.util.Collections.singletonList(""))
	                              .get(0);
             */
	    	String token = CookiesHandlers.getCookie(
	    		    session.getUpgradeRequest().getCookies(),
	    		    CookiesHandlers.AUTH_COOKIE_NAME
	    		);
	    	
	    	if (token == null || token.trim().isEmpty()) {
	            token = session.getUpgradeRequest()
	                .getParameterMap()
	                .getOrDefault("token", java.util.Collections.singletonList(""))
	                .get(0);
	        }
	        System.out.println("[WS] Token received: " + token);
	        System.out.println("[WS] Token length: " + (token != null ? token.length() : "null"));

	        if (token == null || token.isEmpty()) {
	            System.out.println("[WS] Rejected — no token");
	            session.close(1008, "Unauthorized");
	            return;
	        }
	        

	        if (!maincontroller.isValidToken(token)) {
	            System.out.println("[WS] Rejected — invalid token");
	            System.out.println("[WS] verifySignature: " + CookiesHandlers.verifyTokenSignature(token));
	            session.close(1008, "Unauthorized");
	            return;
	        }
	        session.setIdleTimeout(60L * 60L * 1000L);
	        connections.put(session, token);
	        session.getRemote().sendString(
	            "{\"type\":\"WS_CONNECTED\",\"message\":\"websocket connected successfully\"}"
	        );

	    } catch (Exception e) {
	        e.printStackTrace();
	        try { session.close(); } catch (Exception ex) { ex.printStackTrace(); }
	    }
	}
	 
	 
	 @OnWebSocketClose
	    public void onClose(Session session, int statusCode, String reason) {
	        connections.remove(session);
	        System.out.println("WebSocket closed: " + reason);
	    }
	 
	 @OnWebSocketError
	    public void onError(Session session, Throwable error) {
	        connections.remove(session);
	        System.err.println("[WS] Error: " + (error != null ? error.getMessage() : "unknown"));
	    }
	 
	 @OnWebSocketMessage
	    public void onMessage(Session session, String message) {
	        System.out.println("Received WebSocket message: " + message);
	    }
	 
	 
	 /*public static void broadcast(String message) {
	        connections.keySet().forEach(session -> {
	            try {
	                if (session != null && session.isOpen()) {
	                    session.getRemote().sendStringByFuture(message);
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        });
	    }*/
	  public static void broadcast(String message) {
	        connections.keySet().forEach(session -> {
	            try {
	                if (session != null && session.isOpen()) {
	                    session.getRemote().sendStringByFuture(message);
	                } else {
	                    connections.remove(session);
	                }
	            } catch (Exception e) {
	                connections.remove(session);
	                try {
	                    if (session != null && session.isOpen()) {
	                        session.disconnect();
	                    }
	                } catch (Exception ignored) {
	                }
	                System.err.println("[WS] Broadcast failure: " + e.getMessage());
	            }
	        });
	    }
	  
	  
	  
	  
	  
	  public static void sendToToken(String token, String message) {
	        connections.forEach((session, sessionToken) -> {
	            try {
	                if (session == null || !session.isOpen()) {
	                    connections.remove(session);
	                    return;
	                }

	                if (sessionToken != null && sessionToken.equals(token)) {
	                    session.getRemote().sendStringByFuture(message);
	                }
	            } catch (Exception e) {
	                connections.remove(session);
	                try {
	                    if (session != null && session.isOpen()) {
	                        session.disconnect();
	                    }
	                } catch (Exception ignored) {
	                }
	                System.err.println("[WS] Targeted send failure: " + e.getMessage());
	            }
	        });
	    }
	  
	  

	 public static int getConnectedClientsCount() {
	        return connections.size();
	    } 
/*	private static Map<Session, String> connections =
	        new ConcurrentHashMap<>();
	
	
	 @OnWebSocketConnect
	    public void onConnect(Session session) {
	        // 1) Extract token from cookie
	        String token = null;
	        Cookie[] cookies = session.getUpgradeRequest().getCookies();
	        if (cookies != null) {
	            for (Cookie c : cookies) {
	                if (CookiesHandlers.AUTH_COOKIE_NAME.equals(c.getName())) {
	                    token = c.getValue();
	                    break;
	                }
	            }
	        }

	        // 2) Validate token + existence in your current store
	        // IMPORTANT: verify token BEFORE using it.
	        if (token == null
	                || !CookiesHandlers.verifyTokenSignature(token)
	                || !maincontroller.sessionsContains(token)) { // see note below
	            session.close();
	            return;
	        }
	        */
	
}