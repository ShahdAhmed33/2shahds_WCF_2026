/*package services;

import com.fasterxml.jackson.databind.ObjectMapper;

import websocket.ClockMessage;
import websocket.ContestSocket;

public class ClockBroadcaster {
	 private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	    public static void broadcastClock(ClockMessage message) {
	        try {
	            String json = OBJECT_MAPPER.writeValueAsString(message);
	            ContestSocket.broadcast(json);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}*/
