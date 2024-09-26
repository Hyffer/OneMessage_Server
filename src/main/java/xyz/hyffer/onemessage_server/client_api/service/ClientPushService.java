package xyz.hyffer.onemessage_server.client_api.service;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import xyz.hyffer.onemessage_server.client_api.controller_ws.ClientPushController;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientNotification;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.Message;

import java.util.List;

@Service
public class ClientPushService {

    @Resource
    ClientPushController clientPushController;

    public void PushNotification(List<Contact> contact_changes, List<Message> message_changes) {
        clientPushController.pushNotification(
                new ClientNotification(contact_changes, message_changes)
        );
    }

}
