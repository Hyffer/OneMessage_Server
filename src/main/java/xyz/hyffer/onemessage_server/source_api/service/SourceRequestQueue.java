package xyz.hyffer.onemessage_server.source_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import xyz.hyffer.onemessage_server.source_api.payload.Response;
import xyz.hyffer.onemessage_server.source_api.service.message_handler.ReqRespPair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SourceRequestQueue {

    private final ObjectMapper objectMapper;

    private final String sourceName;
    private final WebSocketSession session;

    private final ArrayList<ReqRespPair> REQUEST_QUEUE;
    private boolean waitingResponse;

    public SourceRequestQueue(ObjectMapper objectMapper, String sourceName, WebSocketSession session) {
        this.objectMapper = objectMapper;
        this.sourceName = sourceName;
        this.session = session;
        REQUEST_QUEUE = new ArrayList<>();
        waitingResponse = false;
    }

    public void addRequest(ReqRespPair pair) {
        pair.getResponseHandler().setSourceName(sourceName);
        REQUEST_QUEUE.add(pair);
        if (!waitingResponse) {
            waitingResponse = true;
            sendHeadRequest();
        }
    }

    public void addRequests(List<ReqRespPair> pairs) {
        for (ReqRespPair p : pairs) p.getResponseHandler().setSourceName(sourceName);
        REQUEST_QUEUE.addAll(pairs);
        if (!waitingResponse) {
            waitingResponse = true;
            sendHeadRequest();
        }
    }

    public void onResponse(String payload) {
        if (!REQUEST_QUEUE.isEmpty()) {
            ReqRespPair expectedResponse = REQUEST_QUEUE.remove(0);
            try {
                Response response = (Response) objectMapper.readValue(payload, expectedResponse.getResponseClass());
                expectedResponse.getResponseHandler().onResponse(response);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            if (!REQUEST_QUEUE.isEmpty()) {
                sendHeadRequest();
            } else {
                waitingResponse = false;
            }
        }
    }

    /**
     * Send the first request of request queue
     * need request queue not empty
     */
    protected void sendHeadRequest() {
        try {
            System.out.println(objectMapper.writeValueAsString(REQUEST_QUEUE.get(0).getApi()));
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(REQUEST_QUEUE.get(0).getApi())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
