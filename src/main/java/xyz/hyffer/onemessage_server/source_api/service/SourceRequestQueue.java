package xyz.hyffer.onemessage_server.source_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Response;
import xyz.hyffer.onemessage_server.source_api.service.message_handler.ReqRespPair;

import java.util.ArrayList;
import java.util.List;

public abstract class SourceRequestQueue {

    private final ObjectMapper objectMapper;

    protected final int _SID;

    private final ArrayList<ReqRespPair> REQUEST_QUEUE;
    private boolean waitingResponse;

    public SourceRequestQueue(ObjectMapper objectMapper, int _SID) {
        this.objectMapper = objectMapper;
        this._SID = _SID;
        REQUEST_QUEUE = new ArrayList<>();
        waitingResponse = false;
    }

    public void addRequest(ReqRespPair pair) {
        pair.getResponseHandler().set_SID(_SID);
        REQUEST_QUEUE.add(pair);
        trySendHeadRequest();
    }

    public void addRequests(List<ReqRespPair> pairs) {
        for (ReqRespPair p : pairs) p.getResponseHandler().set_SID(_SID);
        REQUEST_QUEUE.addAll(pairs);
        trySendHeadRequest();
    }

    public void onResponse(String payload) {
        waitingResponse = false;
        if (!REQUEST_QUEUE.isEmpty()) {
            ReqRespPair expectedResponse = REQUEST_QUEUE.remove(0);
            try {
                Response response = (Response) objectMapper.readValue(payload, expectedResponse.getResponseClass());
                expectedResponse.getResponseHandler().onResponse(response);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            trySendHeadRequest();
        }
    }

    /**
     * Send the first request of request queue,
     * if request queue not empty and
     * if not waiting for a response
     */
    protected void trySendHeadRequest() {
        if (!REQUEST_QUEUE.isEmpty() && !waitingResponse) {
            try {
                sendRequest(objectMapper.writeValueAsString(REQUEST_QUEUE.get(0).getApi()));
                waitingResponse = true; // TODO: Mutex
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void sendRequest(String s);
}
