package xyz.hyffer.onemessage_server.source_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Event;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Response;
import xyz.hyffer.onemessage_server.source_api.service.message_handler.EventHandler;
import xyz.hyffer.onemessage_server.source_api.service.message_handler.ReqRespPair;
import xyz.hyffer.onemessage_server.source_api.service.message_handler.ResponseHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SourceHandler is a message handler for a source connection
 *
 * A new instance will be created when a source establish a connection,
 * and it will be deleted after that source is disconnected.
 */
@Slf4j
public class SourceHandler {

    private final SourceHandlerContext ctx;

    private final int _SID;
    private final WebSocketSession session;

    private final EventHandler eventHandler;

    private final ConcurrentLinkedDeque<ReqRespPair> requestQueue = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean requestBusy = new AtomicBoolean(false);

    public SourceHandler(SourceHandlerContext ctx, int _SID, WebSocketSession session) {
        this.ctx = ctx;
        this._SID = _SID;
        this.session = session;
        eventHandler = new EventHandler(_SID, ctx);
    }

    public void onReceiveMessage(String payload) {
        log.trace("[Source] Receive from #{}:\t{}", this._SID, payload);

        List<ReqRespPair> newReqs = null;
        try {
            // receive event
            Event event = ctx.objectMapper.readValue(payload, Event.class);
            if (event == null) {
                return; // event not supported
            }
            newReqs = eventHandler.onEvent(event);

        } catch (JsonProcessingException e) {
            // receive response
            if (!requestQueue.isEmpty()) {
                // TODO: better noise-proof to prevent a series of mismatched responses
                ResponseHandler responseHandler = requestQueue.poll().getResponseHandler();
                requestBusy.set(false);
                try {
                    Response response = ctx.objectMapper.readValue(payload, responseHandler.getExpectedResponseClass());
                    newReqs = responseHandler.onResponse(response);
                } catch (JsonProcessingException ex) {
                    log.warn("Response parse failed: expecting {}",
                            responseHandler.getExpectedResponseClass().getSimpleName());
                    log.warn("Exception trace: ", ex);
                }
            }

            // TODO: finer exception classify
//            e.printStackTrace();
        }

        if (newReqs != null) {
            requestQueue.addAll(newReqs);
        }
        trySendHeadRequest();
    }

    /**
     * Send the first request of request queue,
     * if request queue not empty and
     * if not waiting for a response
     */
    private void trySendHeadRequest() {
        if (requestQueue.isEmpty() ||
                !requestBusy.compareAndSet(false, true)) {
            return;
        }

        new Thread(() -> {
            try {
                String s = ctx.objectMapper.writeValueAsString(requestQueue.getFirst().getRequestPayload());
                log.trace("[Source] Send to #{}:\t{}", this._SID, s);
                session.sendMessage(new TextMessage(s));
            } catch (JsonProcessingException e) {
                log.error("Exception trace: ", e);
            } catch (IOException e) {
                log.error("IOException. Exception trace: ", e);
            }
        }).start();
    }

    public void callUserAPI(ReqRespPair pair) {
        requestQueue.add(pair);
        trySendHeadRequest();
    }

}
