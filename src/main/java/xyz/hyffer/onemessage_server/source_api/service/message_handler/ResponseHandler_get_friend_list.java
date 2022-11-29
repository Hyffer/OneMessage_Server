package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import xyz.hyffer.onemessage_server.source_api.payload.Response;
import xyz.hyffer.onemessage_server.source_api.service.storage_maintainer.ContactMaintainer;

public class ResponseHandler_get_friend_list extends ResponseHandler {

    @Override
    public void onResponse(Response response) {
        if (response.getRetcode() == 0) {
            ContactMaintainer.migrateContacts(_SID, ((Response.Response_get_contact_list) response).getData());
        }
    }
}
