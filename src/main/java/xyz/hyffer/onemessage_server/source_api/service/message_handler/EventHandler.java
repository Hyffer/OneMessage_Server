package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.ContactInstance;
import xyz.hyffer.onemessage_server.model.Message;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Api;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Event;
import xyz.hyffer.onemessage_server.source_api.service.SourceHandlerContext;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class EventHandler {

    private final int _SID;

    private final SourceHandlerContext ctx;

    public List<ReqRespPair> onEvent(Event event) {
        if (event instanceof Event.MetaEvent.LifeCycle) {
            return Arrays.asList(
                    new ReqRespPair(
                        new Api.Api_get_friend_list(),
                        new ResponseHandler_get_instance_list(_SID, ctx)),
                    new ReqRespPair(
                        new Api.Api_get_group_list(),
                        new ResponseHandler_get_instance_list(_SID, ctx))
            );
        }
        else if (event instanceof Event.MessageEvent e) {
            String instance_id = e.getInstance_id();
            ctx.transactionWrapper.serializableTransaction_wrappedByRetry(() -> {
                receiveMessage(_SID, instance_id, e.getMessage());
            });
//            ClientPushService.pushStatus(contact.get_CID(), RECEIVE_MESSAGE);
            return null;
        }
        return null;
    }

    private void receiveMessage(int _SID, String instanceId, Message message) {
        Optional<ContactInstance> r = ctx.instanceRepository.findBy_SIDAndId(_SID, instanceId);
        if (r.isEmpty()) {
            log.warn("Received message from instance (_SID={}, id={}), which is not found in database. " +
                    "It might be newly added.", _SID, instanceId);
            return;
        }
        ContactInstance instance = r.get();
        // message deserialized from event does not have _CiID
        message.set_CiID(instance.get_CiID());
        ctx.messageRepository.saveAndFlush(message);

        Contact contact = instance.getAttachedContact();
        contact.setLastMsg(message);
        contact.setUnread(contact.getUnread() + 1);
        if (message.getRank() != 0) {
            contact.setLastMsgRank(message.getRank());
        } else {
            log.error("Unexpected error: persisted message has rank 0.\nMessage: {}", message);
        }
        ctx.contactRepository.saveAndFlush(contact);
    }

}
