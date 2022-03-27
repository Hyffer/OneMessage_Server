package xyz.hyffer.onemessage_server.client_api.service;

import org.springframework.stereotype.Service;
import xyz.hyffer.onemessage_server.client_api.payload.RequestBody;
import xyz.hyffer.onemessage_server.client_api.payload.SendBody;
import xyz.hyffer.onemessage_server.source_api.service.UserApiService;
import xyz.hyffer.onemessage_server.storage.component.Contact;
import xyz.hyffer.onemessage_server.storage.component.Message;
import xyz.hyffer.onemessage_server.storage.mapper.ContactMapper;
import xyz.hyffer.onemessage_server.storage.mapper.MessageMapper;
import xyz.hyffer.onemessage_server.storage.mongo.MessageContentInjector;
import xyz.hyffer.onemessage_server.storage.mongo.MessageContentMapper;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

@Service
public class ClientRequestServiceImpl implements ClientRequestService {

    @Resource
    private ContactMapper contactMapper;

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private MessageContentInjector messageContentInjector;

    @Resource
    private MessageContentMapper messageContentMapper;

    @Override
    public SendBody.ResponseBody getContacts(RequestBody.RequestBody_get_contacts requestBody) throws UnexpectedValueException {
        if (requestBody.getNum() < 0)
            throw new UnexpectedValueException();

        if (requestBody.getSort().equals("Default")) {
            List<Contact> contacts = contactMapper.getContacts(requestBody.getNum());
            return new SendBody.ResponseBody.ResponseBody_get_contacts(contacts);
        } else if (requestBody.getSort().equals("Search")) {
            List<Contact> contacts = contactMapper.searchContacts(requestBody.getKey(), requestBody.getNum());
            return new SendBody.ResponseBody.ResponseBody_get_contacts(contacts);
        } else {
            throw new UnexpectedValueException();
        }
    }

    @Override
    public SendBody.ResponseBody getMessages(RequestBody.RequestBody_get_messages requestBody) throws UnexpectedValueException {
        int _CID = requestBody.get_CID();
        Contact contact = contactMapper.findContactByCID(_CID);
        if (contact == null)
            throw new UnexpectedValueException();

        int total = contact.getTotal();
        int last_MID = requestBody.getLastMsg_MID();
        if (last_MID == 0) last_MID = total;
        int first_MID = last_MID - requestBody.getNum() + 1;
        if (first_MID <= 0) first_MID = 1;
        List<Message> messages = messageMapper.getMessages(_CID, first_MID, last_MID);

        messageContentInjector.injectContent(messages, _CID, first_MID, last_MID);

        return new SendBody.ResponseBody.ResponseBody_get_messages(messages);
    }

    @Override
    public SendBody.ResponseBody updateStatus(RequestBody.RequestBody_update_status requestBody) {
        return null;
    }

    @Override
    public SendBody.ResponseBody postMessage(RequestBody.RequestBody_post_message requestBody) throws UnexpectedValueException {
        int _CID = requestBody.get_CID();
        Contact contact = contactMapper.findContactByCID(_CID);
        if (contact == null)
            throw new UnexpectedValueException();

        Message message = requestBody.getMessage();
        message.setTime(new Timestamp(System.currentTimeMillis()));
        message.setDirection("Out");
        if (contact.getType().equals("Group")) {
            message.setType("Normal");
            message.setSenderId(contact.getId());
            message.setSenderName(contact.getName());
        }

        contact.setTotal(contact.getTotal() + 1);
        contact.setLastMsgTime(message.getTime());

        messageMapper.addMessageRecord(_CID, message);
        messageContentMapper.saveMessageContent(_CID, message);
        contactMapper.updateContactStatus(contact);

        UserApiService.postMessage("QQ", contact, message);
        ClientPushService.pushStatus(contact.get_CID(), SendBody.PushBody.PushEvent.RECEIVE_MESSAGE);

        return new SendBody.ResponseBody.ResponseBody_no_content();
    }
}
