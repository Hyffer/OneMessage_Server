package xyz.hyffer.onemessage_server.client_api.service;

import org.springframework.stereotype.Service;
import xyz.hyffer.onemessage_server.client_api.payload.RequestBody;
import xyz.hyffer.onemessage_server.client_api.payload.SendBody;
import xyz.hyffer.onemessage_server.storage.component.Contact;
import xyz.hyffer.onemessage_server.storage.mapper.ContactMapper;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ClientRequestServiceImpl implements ClientRequestService {

    @Resource
    private ContactMapper contactMapper;

    @Override
    public SendBody.ResponseBody getContacts(RequestBody.RequestBody_get_contacts requestBody) throws UnexpectedPayloadException {
        if (requestBody.getSort().equals("Default")) {
            List<Contact> contacts = contactMapper.getContacts(requestBody.getNum());
            return new SendBody.ResponseBody.ResponseBody_get_contacts(contacts);
        } else if (requestBody.getSort().equals("Search")) {
            List<Contact> contacts = contactMapper.searchContacts(requestBody.getKey(), requestBody.getNum());
            return new SendBody.ResponseBody.ResponseBody_get_contacts(contacts);
        } else {
            throw new UnexpectedPayloadException();
        }
    }

    @Override
    public SendBody.ResponseBody getMessages(RequestBody.RequestBody_get_messages requestBody) {
        return null;
    }

    @Override
    public SendBody.ResponseBody updateStatus(RequestBody.RequestBody_update_status requestBody) {
        return null;
    }

    @Override
    public SendBody.ResponseBody postMessage(RequestBody.RequestBody_post_message requestBody) {
        return null;
    }
}
