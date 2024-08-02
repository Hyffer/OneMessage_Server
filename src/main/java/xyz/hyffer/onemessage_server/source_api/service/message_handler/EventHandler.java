package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Api;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Event;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Response;

import java.util.Arrays;
import java.util.List;

public class EventHandler {

    private final int _SID;

    public EventHandler(int _SID) {
        this._SID = _SID;
    }

    public List<ReqRespPair> onEvent(Event event) {
        if (event instanceof Event.MetaEvent.LifeCycle) {
            return Arrays.asList(
                    new ReqRespPair(
                        new Api.Api_get_friend_list(),
                        Response.Response_get_contact_list.class,
                        new ResponseHandler_get_friend_list()),
                    new ReqRespPair(
                        new Api.Api_get_group_list(),
                        Response.Response_get_contact_list.class,
                        new ResponseHandler_get_group_list())
            );
        }
        else if (event instanceof Event.MessageEvent) {
//            long contact_id = ((Event.MessageEvent) event).getContact_id();
//            Message message = ((Event.MessageEvent) event).getMessage();
//            message.set_SID(_SID);
//            Contact contact = StaticStorage.contactMapper.findContactById(_SID, contact_id);
//            contact.setTotal(contact.getTotal() + 1);
//            contact.setUnread(contact.getUnread() + 1);
//            contact.setLastMsgTime(message.getTime());
//
//            StaticStorage.messageMapper.addMessageRecord(contact.get_CID(), message);
//            StaticStorage.messageContentMapper.saveMessageContent(contact.get_CID(), message);
//            StaticStorage.contactMapper.updateContactStatus(contact);
//
//            ClientPushService.pushStatus(contact.get_CID(), RECEIVE_MESSAGE);
            return null;
        }
        return null;
    }

}
