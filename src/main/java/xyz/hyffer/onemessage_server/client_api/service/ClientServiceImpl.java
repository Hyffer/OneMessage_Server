package xyz.hyffer.onemessage_server.client_api.service;

import org.springframework.stereotype.Service;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientRequestBody;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientResponse;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.Message;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    @Override
    public ClientResponse.GetContacts getContacts(ClientRequestBody.GetContacts requestBody) {
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
    public ClientResponse.GetMessages getMessages(ClientRequestBody.GetMessages requestBody) {
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
    public ClientResponse.UpdateState updateStatus(ClientRequestBody.UpdateState requestBody) {
        return null;
    }

    @Override
    public ClientResponse.PostMessage postMessage(ClientRequestBody.PostMessage requestBody) {
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

    List<Contact> catchupContacts(Integer _CID_l, Integer _CID_r, Integer pre_cOrd, Integer pre_sOrd, int limit) {
        return null;
    }

    List<Message> catchupMessages(Integer _MID_l, Integer _MID_r, Integer pre_rank, Integer pre_cOrd, int limit) {
        return null;
    }

    List<Contact> getContacts(boolean pinned, Integer post_lMRank, int limit) {
        return null;
    }

    List<Contact> getContacts(String key, int limit) {
        return null;
    }

    List<Message> getMessages(int _CID, Integer post_rank, int limit) {
        return null;
    }

    void updateState() {

    }
    void postMessage() {

    }
//    void editContact() {
//
//    }

}
