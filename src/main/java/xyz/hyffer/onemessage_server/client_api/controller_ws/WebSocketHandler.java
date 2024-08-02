package xyz.hyffer.onemessage_server.client_api.controller_ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import xyz.hyffer.onemessage_server.client_api.controller_ws.payload.Request;
import xyz.hyffer.onemessage_server.client_api.controller_ws.payload.RequestBody;
import xyz.hyffer.onemessage_server.client_api.controller_ws.payload.Send;
import xyz.hyffer.onemessage_server.client_api.controller_ws.payload.SendBody;
import xyz.hyffer.onemessage_server.client_api.service.ClientRequestService;
import xyz.hyffer.onemessage_server.client_api.service.UnexpectedValueException;

import jakarta.annotation.Resource;
import java.io.IOException;

import static xyz.hyffer.onemessage_server.client_api.controller_ws.payload.SendBody.ResponseBody.ResponseCode.UNEXPECTED_REQUEST;
import static xyz.hyffer.onemessage_server.client_api.controller_ws.payload.SendBody.ResponseBody.ResponseCode.UNEXPECTED_VALUE;

@Service("client_websocket_handler")
public class WebSocketHandler extends TextWebSocketHandler {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ClientRequestService requestService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        WebSocketSessionManager.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        System.out.println("[Client] Receive:\t" + payload);
        Request request;
        Send send = new Send();
        try {
            request = objectMapper.readValue(payload, Request.class);
            RequestBody requestBody = request.getBody();
            SendBody.ResponseBody responseBody;
            if (requestBody instanceof RequestBody.RequestBody_get_contacts) {
                responseBody = requestService.getContacts((RequestBody.RequestBody_get_contacts) requestBody);
            } else if (requestBody instanceof RequestBody.RequestBody_get_messages) {
                responseBody = requestService.getMessages((RequestBody.RequestBody_get_messages) requestBody);
            } else if (requestBody instanceof RequestBody.RequestBody_update_status) {
                responseBody = requestService.updateStatus((RequestBody.RequestBody_update_status) requestBody);
            } else {
                responseBody = requestService.postMessage((RequestBody.RequestBody_post_message) requestBody);
            }
            send.construct(responseBody);
        } catch (JsonProcessingException e) {
            send.construct(new SendBody.ResponseBody.ResponseBody_error(UNEXPECTED_REQUEST));
        } catch (UnexpectedValueException e) {
            send.construct(new SendBody.ResponseBody.ResponseBody_error(UNEXPECTED_VALUE));
        }

        try {
            WebSocketSessionManager.get(session.getId())
                    .sendMessage(new TextMessage(objectMapper.writeValueAsString(send)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        WebSocketSessionManager.remove(session.getId());
    }

}
