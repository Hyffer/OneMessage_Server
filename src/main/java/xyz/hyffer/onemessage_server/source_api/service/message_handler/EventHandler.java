package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import xyz.hyffer.onemessage_server.client_api.service.ClientPushService;
import xyz.hyffer.onemessage_server.source_api.payload.Api;
import xyz.hyffer.onemessage_server.source_api.payload.Event;
import xyz.hyffer.onemessage_server.source_api.payload.Response;
import xyz.hyffer.onemessage_server.source_api.service.storage_maintainer.StaticStorage;
import xyz.hyffer.onemessage_server.storage.component.Contact;
import xyz.hyffer.onemessage_server.storage.component.Message;

import static xyz.hyffer.onemessage_server.client_api.payload.SendBody.PushBody.PushEvent.RECEIVE_MESSAGE;

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
        else if (event instanceof Event.MessageEvent) {
            long contact_id = ((Event.MessageEvent) event).getContact_id();
            Message message = ((Event.MessageEvent) event).getMessage();
            Contact contact = StaticStorage.contactMapper.findContactById(contact_id);
            contact.setTotal(contact.getTotal() + 1);
            contact.setUnread(contact.getUnread() + 1);
            contact.setLastMsgTime(message.getTime());

            StaticStorage.messageMapper.addMessageRecord(contact.get_CID(), message);
            StaticStorage.messageContentMapper.saveMessageContent(contact.get_CID(), message);
            StaticStorage.contactMapper.updateContactStatus(contact);

            ClientPushService.pushStatus(contact.get_CID(), RECEIVE_MESSAGE);
            return null;
        }
        return null;
    }

}
