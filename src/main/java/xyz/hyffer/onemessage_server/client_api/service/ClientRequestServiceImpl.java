package xyz.hyffer.onemessage_server.client_api.service;

import org.springframework.stereotype.Service;
import xyz.hyffer.onemessage_server.client_api.controller_ws.payload.RequestBody;
import xyz.hyffer.onemessage_server.client_api.controller_ws.payload.SendBody;

@Service
public class ClientRequestServiceImpl implements ClientRequestService {

    @Override
    public SendBody.ResponseBody getContacts(RequestBody.RequestBody_get_contacts requestBody) throws UnexpectedValueException {
        return null;
//        if (requestBody.getNum() < 0)
//            throw new UnexpectedValueException();
//
//        if (requestBody.getSort().equals("Default")) {
//            List<Contact> contacts = contactMapper.getContacts(requestBody.getNum());
//            return new SendBody.ResponseBody.ResponseBody_get_contacts(contacts);
//        } else if (requestBody.getSort().equals("Search")) {
//            List<Contact> contacts = contactMapper.searchContacts(requestBody.getKey(), requestBody.getNum());
//            return new SendBody.ResponseBody.ResponseBody_get_contacts(contacts);
//        } else {
//            throw new UnexpectedValueException();
//        }
    }

    @Override
    public SendBody.ResponseBody getMessages(RequestBody.RequestBody_get_messages requestBody) throws UnexpectedValueException {
        return null;
//        int _CID = requestBody.get_CID();
//        Contact contact = contactMapper.findContactByCID(_CID);
//        if (contact == null)
//            throw new UnexpectedValueException();
//
//        int total = contact.getTotal();
//        int last_MID = requestBody.getLastMsg_MID();
//        if (last_MID == 0) last_MID = total;
//        int first_MID = last_MID - requestBody.getNum() + 1;
//        if (first_MID <= 0) first_MID = 1;
//        List<Message> messages = messageMapper.getMessages(_CID, first_MID, last_MID);
//
//        messageContentInjector.injectContent(messages, _CID, first_MID, last_MID);
//
//        return new SendBody.ResponseBody.ResponseBody_get_messages(messages);
    }

    @Override
    public SendBody.ResponseBody updateStatus(RequestBody.RequestBody_update_status requestBody) {
        return null;
    }

    @Override
    public SendBody.ResponseBody postMessage(RequestBody.RequestBody_post_message requestBody) throws UnexpectedValueException {
        return null;
//        int _CID = requestBody.get_CID();
//        int _SID = requestBody.get_SID();
//        Contact contact = contactMapper.findContactByCID(_CID);
//        if (contact == null)
//            throw new UnexpectedValueException();
//
//        Message message = requestBody.getMessage();
//        message.set_SID(_SID);
//        message.setDirection("Out");
//        if (contact.getType().equals("Group")) {
//            message.setType("Normal");
//            message.setSenderId(contact.getContactInfo(_SID).getId());
//            message.setSenderName(contact.getContactInfo(_SID).getName());
//        }
//
//        UserApiService.postMessage(_SID, contact, message);
//
//        return new SendBody.ResponseBody.ResponseBody_no_content();
    }
}
