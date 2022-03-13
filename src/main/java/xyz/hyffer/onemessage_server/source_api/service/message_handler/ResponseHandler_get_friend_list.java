package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import xyz.hyffer.onemessage_server.source_api.payload.Api;
import xyz.hyffer.onemessage_server.source_api.payload.Response;
import xyz.hyffer.onemessage_server.source_api.service.storage_maintainer.ContactMaintainer;

public class ResponseHandler_get_friend_list extends ResponseHandler {

    @Override
    public ReqRespPair onResponse(Response response) {
        if (response.getRetcode() == 0) {
            ContactMaintainer.migrateContacts(sourceName, ((Response.Response_get_contact_list) response).getData());
        }
        return new ReqRespPair(
                new Api.Api_get_group_list(),
                Response.Response_get_contact_list.class,
                new ResponseHandler_get_group_list());
    }
}
