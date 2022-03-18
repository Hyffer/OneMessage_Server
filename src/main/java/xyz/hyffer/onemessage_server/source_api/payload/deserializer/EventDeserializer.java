package xyz.hyffer.onemessage_server.source_api.payload.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import xyz.hyffer.onemessage_server.source_api.payload.Event;
import xyz.hyffer.onemessage_server.storage.component.Message;
import xyz.hyffer.onemessage_server.storage.component.MessageSegment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class EventDeserializer extends JsonDeserializer<Event> {

    @Resource
    ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        // set custom MessageSegmentDeserializer
        SimpleModule module = new SimpleModule();
        module.addDeserializer(MessageSegment.class, new MessageSegmentDeserializer());
        objectMapper.registerModule(module);
    }

    @Override
    public Event deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, NotEventException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);

        if (node.get("time") == null || node.get("self_id") == null || node.get("post_type") == null) throw new NotEventException("");
        final long time = node.get("time").asLong();
        final long self_id = node.get("self_id").asLong();
        final String post_type = node.get("post_type").asText();

        switch (post_type) {
            case "meta_event":
                final String meta_event_type = node.get("meta_event_type").asText();
                switch (meta_event_type) {
                    case "lifecycle":
                        final String sub_type = node.get("sub_type").asText();
                        return new Event.MetaEvent.LifeCycle(
                                time, self_id, post_type, meta_event_type, sub_type
                        );
                    case "heartbeat":
                        break;
                }
                break;

            case "message":
                final String message_type = node.get("message_type").asText();
                final String sub_type = node.get("sub_type").asText();
                long contact_id = 0;
                Message message = null;
                switch (message_type) {
                    case "private":
                        if (!sub_type.equals("friend")) {
                            break;
                        }
                        contact_id = node.get("user_id").asLong();
                        message = new Message(new Timestamp(time * 1000), "In");
                        break;
                    case "group":
                        String type;
                        long senderId;
                        String senderName;
                        if (sub_type.equals("normal")) {
                            type = "Normal";
                            senderId = node.get("sender").get("user_id").asLong();
                            senderName = node.get("sender").get("card").asText();
                        } else if (sub_type.equals("anonymous")) {
                            type = "Anonymous";
                            senderId = node.get("anonymous").get("id").asLong();
                            senderName = node.get("anonymous").get("name").asText();
                        } else {
                            break;
                        }
                        contact_id = node.get("group_id").asLong();
                        message = new Message(new Timestamp(time * 1000), "In", type, senderId, senderName);
                        break;
                }
                if (message != null) {
                    ArrayList<MessageSegment> segments = new ArrayList<>();
                    for (JsonNode n : node.get("message")) {
                        MessageSegment segment = objectMapper.treeToValue(n, MessageSegment.class);
                        segments.add(segment);
                    }
                    message.setSegments(segments);
                    Event.MessageEvent event = new Event.MessageEvent(time, self_id, post_type, message_type, sub_type, contact_id);
                    event.setMessage(message);
                    return event;
                }
                break;
        }
        return null;
    }
}
