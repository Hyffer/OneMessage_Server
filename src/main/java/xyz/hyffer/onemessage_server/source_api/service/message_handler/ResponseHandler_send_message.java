package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.Message;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Response;
import xyz.hyffer.onemessage_server.source_api.service.SourceHandlerContext;

import java.sql.Timestamp;
import java.util.List;

public class ResponseHandler_send_message extends ResponseHandler {

    private final Contact contact;
    private final Message message;

    public ResponseHandler_send_message(int _SID, SourceHandlerContext ctx, Contact contact, Message message) {
        super(_SID, ctx);
        expectedResponseClass = Response.Response_send_message.class;
        this.contact = contact;
        this.message = message;
    }

    @Override
    public List<ReqRespPair> onResponse(Response response) {
        message.setTime(new Timestamp(System.currentTimeMillis()));
//        contact.setTotal(contact.getTotal() + 1);
//        contact.setLastMsgTime(message.getTime());

//        messageMapper.addMessageRecord(contact.get_CID(), message);
//        messageContentMapper.saveMessageContent(contact.get_CID(), message);
//        contactMapper.updateContactStatus(contact);

//        ClientPushService.pushStatus(contact.get_CID(), SendBody.PushBody.PushEvent.RECEIVE_MESSAGE);
        return null;
    }
}
