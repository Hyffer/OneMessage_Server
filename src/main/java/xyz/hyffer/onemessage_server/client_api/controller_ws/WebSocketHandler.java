package xyz.hyffer.onemessage_server.client_api.controller_ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import xyz.hyffer.onemessage_server.client_api.service.ClientService;

import jakarta.annotation.Resource;

@Service("client_websocket_handler")
public class WebSocketHandler extends TextWebSocketHandler {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ClientService clientService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        WebSocketSessionManager.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
//        String payload = message.getPayload();
//        System.out.println("[Client] Receive:\t" + payload);
//        Request request;
//        Send send = new Send();
//        try {
//            request = objectMapper.readValue(payload, Request.class);
//            ClientRequestBody requestBody = request.getBody();
//            SendBody.ResponseBody responseBody;
//            if (requestBody instanceof ClientRequestBody.GetContacts1) {
//                responseBody = clientService.getContacts((ClientRequestBody.GetContacts) requestBody);
//            } else if (requestBody instanceof ClientRequestBody.GetMessages1) {
//                responseBody = clientService.getMessages((ClientRequestBody.GetMessages) requestBody);
//            } else if (requestBody instanceof ClientRequestBody.UpdateState) {
//                responseBody = clientService.updateStatus((ClientRequestBody.UpdateState) requestBody);
//            } else {
//                responseBody = clientService.postMessage((ClientRequestBody.PostMessage) requestBody);
//            }
//            send.construct(responseBody);
//        } catch (JsonProcessingException e) {
//            send.construct(new SendBody.ResponseBody.ResponseBody_error(UNEXPECTED_REQUEST));
//        } catch (UnexpectedValueException e) {
//            send.construct(new SendBody.ResponseBody.ResponseBody_error(UNEXPECTED_VALUE));
//        }
//
//        try {
//            WebSocketSessionManager.get(session.getId())
//                    .sendMessage(new TextMessage(objectMapper.writeValueAsString(send)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        WebSocketSessionManager.remove(session.getId());
    }

}
