package xyz.hyffer.onemessage_server.client_api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    @Qualifier("client_websocket_handler")
    private TextWebSocketHandler textWebSocketHandler;

    @Resource
    @Qualifier("client_handshake_interceptor")
    private HandshakeInterceptor handshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(textWebSocketHandler, "app")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("http://localhost:3000");
    }
}
