package xyz.hyffer.onemessage_server.source_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.WebSocketSession;
import xyz.hyffer.onemessage_server.source_api.payload.Event;
import xyz.hyffer.onemessage_server.source_api.service.message_handler.EventHandler;
import xyz.hyffer.onemessage_server.source_api.service.message_handler.ReqRespPair;

import java.util.List;

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
    private final SourceRequestQueue requestQueue;

    public SourceHandler(ObjectMapper objectMapper, String sourceName, WebSocketSession session) {
        this.objectMapper = objectMapper;
        this.sourceName = sourceName;
        this.session = session;
        eventHandler = new EventHandler(sourceName);
        requestQueue = new SourceRequestQueue(objectMapper, sourceName, session);
    }

    public void onReceiveMessage(String payload) {
        try {
            // receive event
            Event event = objectMapper.readValue(payload, Event.class);
            List<ReqRespPair> reqs = eventHandler.onEvent(event);
            if (reqs != null) {
                requestQueue.addRequests(reqs);
            }
        } catch (JsonProcessingException e) {
            // receive response
            requestQueue.onResponse(payload);

            // TODO: finer exception classify
//            e.printStackTrace();
        }
    }

    public void callUserAPI(ReqRespPair pair) {
        requestQueue.addRequest(pair);
    }

}
