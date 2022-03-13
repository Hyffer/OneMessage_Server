package xyz.hyffer.onemessage_server.source_api.service.storage_maintainer;

import org.springframework.stereotype.Component;
import xyz.hyffer.onemessage_server.storage.mapper.ContactMapper;
import xyz.hyffer.onemessage_server.storage.mapper.MessageMapper;

import javax.annotation.Resource;

@Component
public class StaticStorage {

    public static ContactMapper contactMapper;

    public static MessageMapper messageMapper;

    @Resource(name = "contactMapper")
    public void setContactMapper(ContactMapper contactMapper) {
        StaticStorage.contactMapper = contactMapper;
    }

    @Resource(name = "messageMapper")
    public void setMessageMapper(MessageMapper messageMapper) {
        StaticStorage.messageMapper = messageMapper;
    }

}
