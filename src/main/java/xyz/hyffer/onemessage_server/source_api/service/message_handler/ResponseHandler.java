package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import lombok.Getter;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Response;
import xyz.hyffer.onemessage_server.source_api.service.SourceHandlerContext;

import java.util.List;

@Getter
public abstract class ResponseHandler {

    protected Class<? extends Response> expectedResponseClass;

    protected int _SID;

    protected SourceHandlerContext ctx;

    public ResponseHandler(int _SID, SourceHandlerContext ctx) {
        this._SID = _SID;
        this.ctx = ctx;
    }

    /**
     * onResponse is called when a response is received
     *
     * @param response the response received, of type `expectedResponseClass`
     * @return a list of ReqRespPair to be sent
     */
    public abstract List<ReqRespPair> onResponse(Response response);

}
