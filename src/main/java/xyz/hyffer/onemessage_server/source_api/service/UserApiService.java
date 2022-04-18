package xyz.hyffer.onemessage_server.source_api.service;

import xyz.hyffer.onemessage_server.source_api.SourceHandlerManager;
import xyz.hyffer.onemessage_server.source_api.payload.Api;
import xyz.hyffer.onemessage_server.source_api.payload.ApiParam;
import xyz.hyffer.onemessage_server.source_api.payload.Response;
import xyz.hyffer.onemessage_server.source_api.service.message_handler.ReqRespPair;
import xyz.hyffer.onemessage_server.source_api.service.message_handler.ResponseHandler_send_message;
import xyz.hyffer.onemessage_server.storage.component.Contact;
import xyz.hyffer.onemessage_server.storage.component.Message;

public class UserApiService {

    public static void postMessage(String sourceName, Contact contact, Message message) {
        SourceHandler handler = SourceHandlerManager.get(sourceName);

        ApiParam.ApiParam_send_message apiParam = new ApiParam.ApiParam_send_message();
        String type = contact.getType();
        if (type.equals("Friend")) {
            apiParam.setMessage_type("private");
            apiParam.setUser_id(contact.getId());
        } else if (type.equals("Group")) {
            apiParam.setMessage_type("group");
            apiParam.setGroup_id(contact.getId());
        }
        apiParam.setMessage(message.getSegments());

        ReqRespPair reqRespPair = new ReqRespPair(new Api.Api_send_message(apiParam),
                Response.Response_send_message.class,
                new ResponseHandler_send_message(contact, message));

        if (handler != null) {
            handler.callUserAPI(reqRespPair);
        }
    }

}
