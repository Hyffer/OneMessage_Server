package xyz.hyffer.onemessage_server.client_api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import xyz.hyffer.onemessage_server.client_api.data.Request;
import xyz.hyffer.onemessage_server.client_api.data.RequestBody;
import xyz.hyffer.onemessage_server.client_api.data.Send;
import xyz.hyffer.onemessage_server.client_api.data.SendBody;

import javax.annotation.Resource;
import java.io.IOException;

import static xyz.hyffer.onemessage_server.client_api.data.SendBody.ResponseBody.ResponseCode.UNEXPECTED_REQUEST;

@Service
public class WebSocketHandler extends TextWebSocketHandler {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        WebSocketSessionManager.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        System.out.println(payload);
        Request request;
        Send send = new Send();
        try {
            request = objectMapper.readValue(payload, Request.class);
            if (request.getBody() instanceof RequestBody.RequestBody_get_contacts) System.out.println("get_contacts");
            else if (request.getBody() instanceof RequestBody.RequestBody_get_messages) System.out.println("get_messages");
            else if (request.getBody() instanceof RequestBody.RequestBody_update_status) System.out.println("update_status");
            else if (request.getBody() instanceof RequestBody.RequestBody_post_message) System.out.println("post_message");
            send.construct(new SendBody.ResponseBody.ResponseBody_no_content());
        } catch (JsonProcessingException e) {
            send.construct(new SendBody.ResponseBody.ResponseBody_error(UNEXPECTED_REQUEST));
        }

        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(send)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        WebSocketSessionManager.remove(session);
    }

}
