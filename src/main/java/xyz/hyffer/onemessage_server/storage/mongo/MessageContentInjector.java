package xyz.hyffer.onemessage_server.storage.mongo;

import com.mongodb.MongoException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
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

    @Resource
    private MappingMongoConverter mongoConverter;

    public void injectContent(List<Message> messages, int _CID, int first_MID, int last_MID) {
        Query query = new Query(Criteria.where("_id").gte(first_MID).lte(last_MID));
        mongoTemplate.executeQuery(query, "msgcontent_" + _CID, new DocumentCallbackHandler() {
            int i = 0;
            @Override
            public void processDocument(Document document) throws MongoException, DataAccessException {
                ArrayList<MessageSegment> segments = new ArrayList<>();
                for (Object segmentBson : ((List) document.get("segments"))) {
                    segments.add(mongoConverter.read(MessageSegment.class, (Bson) segmentBson));
                }
                messages.get(i).setSegments(segments);
                i++;
            }
        });
    }

}
