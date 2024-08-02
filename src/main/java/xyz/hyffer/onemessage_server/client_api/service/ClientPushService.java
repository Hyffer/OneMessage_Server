package xyz.hyffer.onemessage_server.client_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import xyz.hyffer.onemessage_server.client_api.controller_ws.WebSocketSessionManager;
import xyz.hyffer.onemessage_server.client_api.controller_ws.payload.Send;
import xyz.hyffer.onemessage_server.client_api.controller_ws.payload.SendBody;

import jakarta.annotation.Resource;

import java.io.IOException;

@Service
public class ClientPushService {

    private static ObjectMapper objectMapper;

    @Resource
    public void setObjectMapper(ObjectMapper objectMapper) {
        ClientPushService.objectMapper = objectMapper;
    }

    public static void pushStatus(int _CID, SendBody.PushBody.PushEvent event) {
        Send send = new Send();
        send.construct(new SendBody.PushBody(_CID, event));
        TextMessage textMessage;
        try {
            textMessage = new TextMessage(objectMapper.writeValueAsString(send));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        for (WebSocketSession session : WebSocketSessionManager.getAll()) {
            try {
                session.sendMessage(textMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
