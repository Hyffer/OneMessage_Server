package xyz.hyffer.onemessage_server.storage.mongo;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import xyz.hyffer.onemessage_server.storage.component.Message;

import javax.annotation.Resource;

@Component
public class MessageContentMapper {

    @Resource
    private MongoTemplate mongoTemplate;

    public void saveMessageContent(int _CID, Message message) {
        mongoTemplate.insert(message, "msgcontent_" + _CID);
    }

}
