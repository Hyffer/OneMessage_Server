package xyz.hyffer.onemessage_server.source_api.controller_onebot;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import xyz.hyffer.onemessage_server.source_api.service.SourceHandler;
import xyz.hyffer.onemessage_server.source_api.service.SourceHandlerContext;

import java.util.List;

@Service("source_websocket_handler")
public class SWebSocketHandler extends TextWebSocketHandler {

    @Resource
    private SourceHandlerContext ctx;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        int _SID = get_SID(session);
        SourceHandler sourceHandler = new SourceHandler(ctx, _SID, session);
        SourceHandlerManager.put(_SID, sourceHandler);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        int _SID = get_SID(session);
        String payload = message.getPayload();
        SourceHandlerManager.get(_SID).onReceiveMessage(payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        int _SID = get_SID(session);
        SourceHandlerManager.remove(_SID);
    }

    private static int get_SID(WebSocketSession session) {
        List<String> strings = session.getHandshakeHeaders().get("_SID");
        assert (strings != null && strings.size() > 0);
        return Integer.parseInt(strings.get(0));
    }
}
