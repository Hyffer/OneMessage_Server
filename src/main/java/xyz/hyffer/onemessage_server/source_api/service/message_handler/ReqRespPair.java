package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import org.springframework.data.util.Pair;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Api;

/**
 * ReqRespPair is a pair of request and corresponding response handler
 *
 * Every time when SourceHandler receive a message,
 * onEvent() in EventHandler or onResponse() in ResponseHandler will be called depending on whether it is an event or a response.
 * After processed the message received, a return list of ReqRespPair is expected.
 *
 * If a request is needed, it should be placed in first of the pair, with the corresponding responseHandler in second.
 * If no need for request, just simply return null.
 */
public class ReqRespPair {
    Pair<Api, ResponseHandler> pair;

    public ReqRespPair(Api api, ResponseHandler responseHandler) {
        pair = Pair.of(api, responseHandler);
    }

    public Api getRequestPayload() {
        return pair.getFirst();
    }

    public ResponseHandler getResponseHandler() {
        return pair.getSecond();
    }
}