package xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import xyz.hyffer.onemessage_server.model.Message;
import xyz.hyffer.onemessage_server.model.MessageSegment;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Event;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class EventDeserializer extends JsonDeserializer<Event> {

    @Override
    public Event deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, NotEventException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);

        if (node.get("time") == null || node.get("self_id") == null || node.get("post_type") == null) throw new NotEventException("");
        final long time = node.get("time").asLong();
        final String self_id = node.get("self_id").asText();
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
                String instance_id = null;
                Message message = null;
                switch (message_type) {
                    case "private":
                        if (!sub_type.equals("friend")) {
                            break;
                        }
                        instance_id = node.get("user_id").asText();
                        message = Message.builder()
                                .time(new Timestamp(time * 1000))
                                .status(Message.Status.IN)
                                .build();
                        break;
                    case "group":
                        String type;
                        String senderId;
                        String senderName;
                        if (sub_type.equals("normal")) {
                            type = "Normal";
                            senderId = node.get("sender").get("user_id").asText();
                            senderName = node.get("sender").get("card").asText();
                        } else if (sub_type.equals("anonymous")) {
                            type = "Anonymous";
                            senderId = node.get("anonymous").get("id").asText();
                            senderName = node.get("anonymous").get("name").asText();
                        } else {
                            break;
                        }
                        instance_id = node.get("group_id").asText();
                        message = Message.builder()
                                .type(type)
                                .senderId(senderId)
                                .senderName(senderName)
                                .time(new Timestamp(time * 1000))
                                .status(Message.Status.IN)
                                .build();
                        break;
                }
                if (message != null) {
                    ArrayList<MessageSegment> segments = new ArrayList<>();
                    for (JsonNode n : node.get("message")) {
                        MessageSegment segment = codec.treeToValue(n, MessageSegment.class);
                        segments.add(segment);
                    }
                    message.setSegments(segments);
                    Event.MessageEvent event = new Event.MessageEvent(time, self_id, post_type, message_type, sub_type, instance_id);
                    event.setMessage(message);
                    return event;
                }
                break;
        }
        return null;
    }
}
