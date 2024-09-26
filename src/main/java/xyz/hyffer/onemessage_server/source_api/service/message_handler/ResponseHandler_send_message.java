package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import xyz.hyffer.onemessage_server.client_api.service.ClientPushService;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.Message;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Response;

import java.sql.Timestamp;

public class ResponseHandler_send_message extends ResponseHandler {

    private final Contact contact;
    private final Message message;

    public ResponseHandler_send_message(Contact contact, Message message) {
        this.contact = contact;
        this.message = message;
    }

    @Override
    public void onResponse(Response response) {
        message.setTime(new Timestamp(System.currentTimeMillis()));
//        contact.setTotal(contact.getTotal() + 1);
//        contact.setLastMsgTime(message.getTime());

//        messageMapper.addMessageRecord(contact.get_CID(), message);
//        messageContentMapper.saveMessageContent(contact.get_CID(), message);
//        contactMapper.updateContactStatus(contact);

//        ClientPushService.pushStatus(contact.get_CID(), SendBody.PushBody.PushEvent.RECEIVE_MESSAGE);
    }
}
