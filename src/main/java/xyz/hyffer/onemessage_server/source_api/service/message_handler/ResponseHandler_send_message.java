package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import xyz.hyffer.onemessage_server.source_api.payload.Response;

public class ResponseHandler_send_message extends ResponseHandler {

    @Override
    public ReqRespPair onResponse(Response response) {
        // TODO:
        //  add request queue in SourceHandler,
        //  move write storage and push status operation in `ClientRequestService.postMessage()` here
        return new ReqRespPair(null, null, null);
    }
}
