package xyz.hyffer.onemessage_server.client_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientException;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientRequestBody;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientResponse;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.Message;
import xyz.hyffer.onemessage_server.storage.ContactRepository;
import xyz.hyffer.onemessage_server.storage.MessageRepository;

import java.util.List;
import java.util.Optional;

/**
 * RequestBody is deserialized by {@link xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientRequestBodyDeserializer}.
 * So the param is guaranteed to be valid.
 */
@Service
public class ClientService {

    final ClientCustomQuery clientCustomQuery;

    final ContactRepository contactRepository;
    final MessageRepository messageRepository;

    @Autowired
    public ClientService(ClientCustomQuery clientCustomQuery, ContactRepository contactRepository, MessageRepository messageRepository) {
        this.clientCustomQuery = clientCustomQuery;
        this.contactRepository = contactRepository;
        this.messageRepository = messageRepository;
    }

    public ClientResponse.GetContacts getContacts(ClientRequestBody.GetContacts requestBody) {
        List<Contact> contacts;
        if (requestBody instanceof ClientRequestBody.GetContacts1 catchupContacts) {
            contacts = clientCustomQuery.catchupContacts(
                    catchupContacts.get_CID_l(),
                    catchupContacts.get_CID_r(),
                    catchupContacts.getPre_cOrd(),
                    catchupContacts.getPre_sOrd(),
                    catchupContacts.getLimit()
            );
        } else if (requestBody instanceof ClientRequestBody.GetContacts2 getContacts) {
            contacts = clientCustomQuery.getContacts(
                    getContacts.getPinned(),
                    getContacts.getPost_lMRank(),
                    getContacts.getLimit()
            );
        } else if (requestBody instanceof ClientRequestBody.GetContacts3 searchContacts) {
            contacts = clientCustomQuery.searchContacts(
                    searchContacts.getKey(),
                    searchContacts.getLimit()
            );
        } else {
            throw new RuntimeException(
                    new ClientException(ClientException.Type.INTERNAL_ERROR,
                            "Not supposed to reach here.\n" + requestBody)
            );
        }

        // load transient field
        if (requestBody.getAll_attr()) {
            contacts.forEach(contact -> {
                Optional<Message> r = messageRepository.findByRank(contact.getLastMsgRank());
                r.ifPresent(contact::setLastMsg);
            });
        }
        return new ClientResponse.GetContacts(contacts);
    }

    public ClientResponse.GetMessages getMessages(ClientRequestBody.GetMessages requestBody) {
        List<Message> messages;
        if (requestBody instanceof ClientRequestBody.GetMessages1 catchupMessages) {
            messages = clientCustomQuery.catchupMessages(
                    catchupMessages.get_MID_l(),
                    catchupMessages.get_MID_r(),
                    catchupMessages.getPre_rank(),
                    catchupMessages.getPre_cOrd(),
                    catchupMessages.getLimit()
            );
        } else if (requestBody instanceof ClientRequestBody.GetMessages2 getMessages) {
            Optional<Contact> r = contactRepository.findById(getMessages.get_CID());
            if (r.isEmpty()) {
                throw new RuntimeException(
                        new ClientException(ClientException.Type.UNEXPECTED_VALUE,
                                "No such contact with _CID: " + getMessages.get_CID() + "\n" + requestBody)
                );
            }
            Contact contact = r.get();
            messages = clientCustomQuery.getMessages(
                    contact.getInstanceIds(),
                    getMessages.getPost_rank(),
                    getMessages.getLimit()
            );
        } else {
            throw new RuntimeException(
                    new ClientException(ClientException.Type.INTERNAL_ERROR,
                            "Not supposed to reach here.\n" + requestBody)
            );
        }
        return new ClientResponse.GetMessages(messages);
    }

    public ClientResponse.UpdateState updateStatus(ClientRequestBody.UpdateState requestBody) {
        return null;
    }

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

}
