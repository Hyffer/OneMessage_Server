package xyz.hyffer.onemessage_server.source_api.service;

import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.Message;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.SourceHandlerManager;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.ApiParam;

public class UserApiService {

    public static void postMessage(int _SID, Contact contact, Message message) {
//        SourceHandler handler = SourceHandlerManager.get(_SID);
//
//        ApiParam.ApiParam_send_message apiParam = new ApiParam.ApiParam_send_message();
//        String type = contact.getType();
//        if (type.equals("Friend")) {
//            apiParam.setMessage_type("private");
//            apiParam.setUser_id(contact.getContactInfo(_SID).getId());
//        } else if (type.equals("Group")) {
//            apiParam.setMessage_type("group");
//            apiParam.setGroup_id(contact.getContactInfo(_SID).getId());
//        }
//        apiParam.setMessage(message.getSegments());
//
//        ReqRespPair reqRespPair = new ReqRespPair(new Api.Api_send_message(apiParam),
//                Response.Response_send_message.class,
//                new ResponseHandler_send_message(contact, message));
//
//        if (handler != null) {
//            handler.callUserAPI(reqRespPair);
//        }
    }

}
