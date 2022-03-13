package xyz.hyffer.onemessage_server.source_api.payload.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import xyz.hyffer.onemessage_server.source_api.payload.Event;

import java.io.IOException;

public class EventDeserializer extends JsonDeserializer<Event> {

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
                break;
        }
        return null;
    }
}
