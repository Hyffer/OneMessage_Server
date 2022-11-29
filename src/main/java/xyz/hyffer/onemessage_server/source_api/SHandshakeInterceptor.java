package xyz.hyffer.onemessage_server.source_api;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("source_handshake_interceptor")
public class SHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    SourceHandlerManager sourceHandlerManager;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        List<String> strings = request.getHeaders().get("authorization");
        if (strings != null && strings.size() > 0) {
            String authorization = strings.get(0);
            String name = authorization.substring(authorization.lastIndexOf(' ') + 1);
            int _SID = sourceHandlerManager.registerSource(name);
            request.getHeaders().set("_SID", String.valueOf(_SID));
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
