package xyz.hyffer.onemessage_server.source_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import xyz.hyffer.onemessage_server.source_api.payload.Api;
import xyz.hyffer.onemessage_server.source_api.payload.Event;
import xyz.hyffer.onemessage_server.source_api.payload.Response;
import xyz.hyffer.onemessage_server.source_api.service.message_handler.EventHandler;
import xyz.hyffer.onemessage_server.source_api.service.message_handler.ReqRespPair;
import xyz.hyffer.onemessage_server.source_api.service.message_handler.ResponseHandler;

import java.io.IOException;

/**
 * SourceHandler is a message handler for a source connection
 *
 * A new instance will be created when a source establish a connection,
 * and it will be deleted after that source is disconnected.
 */
public class SourceHandler {

    private final ObjectMapper objectMapper;

    private final String sourceName;
    private final WebSocketSession session;

    private final EventHandler eventHandler;

    private ResponseHandler responseHandler;
    private Class responseClass;

    public SourceHandler(ObjectMapper objectMapper, String sourceName, WebSocketSession session) {
        this.objectMapper = objectMapper;
        this.sourceName = sourceName;
        this.session = session;
        this.eventHandler = new EventHandler(sourceName);
    }

    public void onReceiveMessage(String payload) {
        ReqRespPair pair = null;
        try {
            // receive event
            Event event = objectMapper.readValue(payload, Event.class);
            pair = eventHandler.onEvent(event);

        } catch (JsonProcessingException e) {
            if (responseHandler != null) {
                try {
                    // receive response
                    Response response = (Response) objectMapper.readValue(payload, responseClass);
                    pair = responseHandler.onResponse(response);

                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
            // TODO: finer exception classify
//            e.printStackTrace();
        }

        if (pair != null) {
            if (pair.getApi() != null) {
                responseClass = pair.getResponseClass();
                responseHandler = pair.getResponseHandler();
                responseHandler.setSourceName(sourceName);
                sendRequest(pair.getApi());
            } else {
                responseClass = null;
                responseHandler = null;
            }
        }
    }

    public void callUserAPI(ReqRespPair pair) {
        responseClass = pair.getResponseClass();
        responseHandler = pair.getResponseHandler();
        responseHandler.setSourceName(sourceName);
        sendRequest(pair.getApi());
    }

    private void sendRequest(Api api) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(api)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
