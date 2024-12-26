package xyz.hyffer.onemessage_server.source_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import xyz.hyffer.onemessage_server.storage.ContactInstanceRepository;
import xyz.hyffer.onemessage_server.storage.ContactRepository;
import xyz.hyffer.onemessage_server.storage.MessageRepository;
import xyz.hyffer.onemessage_server.storage.TransactionWrapper;

@Component
public class SourceHandlerContext {

    @Resource(name = "ObjectMapperOBMSS")
    public ObjectMapper objectMapper;

    @Resource
    public ContactRepository contactRepository;
    @Resource
    public ContactInstanceRepository instanceRepository;
    @Resource
    public MessageRepository messageRepository;

    @Resource
    public TransactionWrapper transactionWrapper;
}
