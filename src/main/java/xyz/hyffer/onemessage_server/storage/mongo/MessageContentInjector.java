package xyz.hyffer.onemessage_server.storage.mongo;

import com.mongodb.MongoException;
import org.bson.Document;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import xyz.hyffer.onemessage_server.storage.component.Message;
import xyz.hyffer.onemessage_server.storage.component.MessageSegment;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageContentInjector {

    @Resource
    private MongoTemplate mongoTemplate;

    public void injectContent(List<Message> messages, int _CID, int first_MID, int last_MID) {
        Query query = new Query(Criteria.where("_id").gte(first_MID).lte(last_MID));
        query.fields().exclude("segments.content._class");
        mongoTemplate.executeQuery(query, "msgcontent_" + _CID, new DocumentCallbackHandler() {
            int i = 0;
            @Override
            public void processDocument(Document document) throws MongoException, DataAccessException {
                messages.get(i).setSegments((ArrayList<MessageSegment>) document.get("segments"));
                i++;
            }
        });
    }

}
