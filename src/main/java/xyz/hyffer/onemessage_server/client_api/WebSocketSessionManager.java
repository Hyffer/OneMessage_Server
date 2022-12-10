package xyz.hyffer.onemessage_server.client_api;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessionManager {

    private static final ConcurrentHashMap<String, WebSocketSession> SESSION_POOL = new ConcurrentHashMap<>();

    public static void put(String id, WebSocketSession session) {
        SESSION_POOL.put(id, new ConcurrentWebSocketSessionDecorator(session, 2000, 4096));
    }

    public static void remove(String id) {
        SESSION_POOL.remove(id);
    }

    public static WebSocketSession get(String id) {
        return SESSION_POOL.get(id);
    }

    public static ArrayList<WebSocketSession> getAll() {
        return new ArrayList<>(SESSION_POOL.values());
    }

}
