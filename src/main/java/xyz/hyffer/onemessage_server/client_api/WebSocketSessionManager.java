package xyz.hyffer.onemessage_server.client_api;

import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Vector;

public class WebSocketSessionManager {

    private static final Vector<WebSocketSession> SESSION_POOL = new Vector<>();

    public static void add(WebSocketSession session) {
        SESSION_POOL.add(session);
    }

    public static void remove(WebSocketSession session) {
        SESSION_POOL.remove(session);
    }

    public static ArrayList<WebSocketSession> getAll() {
        return new ArrayList<>(SESSION_POOL);
    }

}
