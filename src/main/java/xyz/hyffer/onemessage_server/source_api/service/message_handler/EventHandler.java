package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import xyz.hyffer.onemessage_server.source_api.payload.Api;
import xyz.hyffer.onemessage_server.source_api.payload.Event;
import xyz.hyffer.onemessage_server.source_api.payload.Response;

public class EventHandler {

    private final String sourceName;

    public EventHandler(String sourceName) {
        this.sourceName = sourceName;
    }

    public ReqRespPair onEvent(Event event) {
        if (event instanceof Event.MetaEvent.LifeCycle) {
            return new ReqRespPair(
                    new Api.Api_get_friend_list(),
                    Response.Response_get_contact_list.class,
                    new ResponseHandler_get_friend_list());
        }
        return null;
    }

}
