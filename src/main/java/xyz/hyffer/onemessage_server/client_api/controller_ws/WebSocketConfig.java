package xyz.hyffer.onemessage_server.client_api.controller_ws;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.annotation.Resource;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    @Qualifier("client_websocket_handler")
    private TextWebSocketHandler textWebSocketHandler;

    @Resource
    @Qualifier("client_handshake_interceptor")
    private HandshakeInterceptor handshakeInterceptor;

    @Value("${om.allowed-origins}")
    private String[] ALLOW_ORIGINS;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(textWebSocketHandler, "app")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOriginPatterns(ALLOW_ORIGINS);
    }
}
