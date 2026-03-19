package websocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
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
	        String token = session.getUpgradeRequest()
	                              .getParameterMap()
	                              .getOrDefault("token", java.util.List.of(""))
	                              .get(0);

	        System.out.println("[WS] Token received: " + token);
	        System.out.println("[WS] Token length: " + (token != null ? token.length() : "null"));

	        if (token == null || token.isEmpty()) {
	            System.out.println("[WS] Rejected — no token");
	            session.close(1008, "Unauthorized");
	            return;
	        }

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
	 
	 
	 
	 @OnWebSocketMessage
	    public void onMessage(Session session, String message) {
	        System.out.println("Received WebSocket message: " + message);
	    }
	 
	 
	 public static void broadcast(String message) {
	        connections.keySet().forEach(session -> {
	            try {
	                if (session != null && session.isOpen()) {
	                    session.getRemote().sendStringByFuture(message);
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
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