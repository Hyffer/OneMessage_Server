package xyz.hyffer.onemessage_server.client_api.service.cs_api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.Message;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class ClientNotification {
    List<Contact> contacts;
    List<Message> messages;
}
