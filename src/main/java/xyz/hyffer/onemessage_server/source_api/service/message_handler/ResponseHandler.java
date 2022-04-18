package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import xyz.hyffer.onemessage_server.source_api.payload.Response;

public abstract class ResponseHandler {

    protected String sourceName;

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public abstract void onResponse(Response response);

}
