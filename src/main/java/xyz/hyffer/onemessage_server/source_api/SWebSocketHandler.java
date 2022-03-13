package xyz.hyffer.onemessage_server.source_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import xyz.hyffer.onemessage_server.source_api.service.SourceHandler;

import javax.annotation.Resource;

@Service("source_websocket_handler")
public class SWebSocketHandler extends TextWebSocketHandler {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String name = getName(session);
        SourceHandler sourceHandler = new SourceHandler(objectMapper, name, session);
        SourceHandlerManager.put(name, sourceHandler);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String name = getName(session);
        String payload = message.getPayload();
        System.out.println(payload);
        SourceHandlerManager.get(name).onReceiveMessage(payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String name = getName(session);
        SourceHandlerManager.remove(name);
    }

    private static String getName(WebSocketSession session) {
        // get("authorization") will return a nonNull object
        // null was caught and blocked during handshake process
        String authorization = session.getHandshakeHeaders().get("authorization").get(0);
        return authorization.substring(authorization.lastIndexOf(' ') + 1);
    }
}
