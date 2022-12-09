package xyz.hyffer.onemessage_server.source_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import xyz.hyffer.onemessage_server.source_api.payload.Event;
import xyz.hyffer.onemessage_server.source_api.service.message_handler.EventHandler;
import xyz.hyffer.onemessage_server.source_api.service.message_handler.ReqRespPair;

import java.io.IOException;
import java.util.List;

/**
 * SourceHandler is a message handler for a source connection
 *
 * A new instance will be created when a source establish a connection,
 * and it will be deleted after that source is disconnected.
 */
public class SourceHandler {

    private final ObjectMapper objectMapper;

    private final int _SID;
    private final WebSocketSession session;

    private final EventHandler eventHandler;
    private final SourceRequestQueue requestQueue;

    public SourceHandler(ObjectMapper objectMapper, int _SID, WebSocketSession session) {
        this.objectMapper = objectMapper;
        this._SID = _SID;
        this.session = session;
        eventHandler = new EventHandler(_SID);
        requestQueue = new SourceRequestQueue(objectMapper, _SID) {
            @Override
            protected void sendRequest(String s) {
                try {
                    System.out.println("[Source] Send to #" + this._SID + ":\t" + s);
                    session.sendMessage(new TextMessage(s));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void onReceiveMessage(String payload) {
        System.out.println("[Source] Receive from #" + this._SID + ":\t" + payload);
        try {
            // receive event
            Event event = objectMapper.readValue(payload, Event.class);
            if (event == null) {
                return; // event not supported
            }
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
