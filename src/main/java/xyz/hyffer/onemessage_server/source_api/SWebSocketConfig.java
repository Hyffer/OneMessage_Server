package xyz.hyffer.onemessage_server.source_api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;

@Configuration
@EnableWebSocket
public class SWebSocketConfig implements WebSocketConfigurer {

    @Resource
    @Qualifier("source_websocket_handler")
    private TextWebSocketHandler sWebSocketHandler;

    @Resource
    @Qualifier("source_handshake_interceptor")
    private HandshakeInterceptor sHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(sWebSocketHandler, "source")
                .addInterceptors(sHandshakeInterceptor);
    }
}
