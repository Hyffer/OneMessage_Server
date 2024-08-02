package xyz.hyffer.onemessage_server.client_api.service;

import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.Message;

import java.sql.Timestamp;
import java.util.List;

/**
 * See <a href="docs/API_CS.md">API_CS.md</a>
 */
public interface ClientService {
    List<Contact> getContacts(int _CID, String key, Timestamp start, int limit);
    List<Message> getMessages(int _CID, int lastMsg_MID, int num);
//    List<Contact> catchupContacts();
//    List<Message> catchupMessages();

    void postMessage();
    void readContact();
    void editContact();

    void push();
}
