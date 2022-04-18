package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import lombok.Data;
import xyz.hyffer.onemessage_server.source_api.payload.Api;

/**
 * ReqRespPair is a pair of request and corresponding response handler
 *
 * Every time when SourceHandler receive a message,
 * onEvent() in EventHandler or onResponse() in ResponseHandler will be called depending on whether it is an event or a response.
 * After processed the message received, a return list of ReqRespPair is expected.
 *
 * If a request is needed, it should be placed in attribute `api`, with responseClass and responseHandler set accordingly.
 * If no need for request, just simply return null.
 */
@Data
public class ReqRespPair {
    private Api api;
    private Class responseClass;
    private ResponseHandler responseHandler;

    public ReqRespPair(Api api, Class responseClass, ResponseHandler responseHandler) {
        this.api = api;
        this.responseClass = responseClass;
        this.responseHandler = responseHandler;
    }
}
