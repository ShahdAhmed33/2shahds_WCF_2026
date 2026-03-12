package websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/ws/chat")
public class ChatEndpoint {

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("WebSocket opened: " + session.getId());
        broadcast("User " + session.getId() + " joined the chat.");
    }

    @OnMessage
    public void onMessage(String message, Session sender) {
        System.out.println("Message from " + sender.getId() + ": " + message);
        broadcast("[" + sender.getId() + "]: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("WebSocket closed: " + session.getId());
        broadcast("User " + session.getId() + " left the chat.");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket error on session " + session.getId() + ": " + error.getMessage());
        sessions.remove(session);
    }

    private static void broadcast(String message) {
        synchronized (sessions) {
            for (Session s : sessions) {
                if (s.isOpen()) {
                    try {
                        s.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        System.err.println("Failed to send message to " + s.getId() + ": " + e.getMessage());
                    }
                }
            }
        }
    }
}
