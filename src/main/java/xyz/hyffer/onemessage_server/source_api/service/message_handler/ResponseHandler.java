package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import xyz.hyffer.onemessage_server.source_api.payload.Response;

public abstract class ResponseHandler {

    protected int _SID;

    public void set_SID(int _SID) {
        this._SID = _SID;
    }

    public abstract void onResponse(Response response);

}
