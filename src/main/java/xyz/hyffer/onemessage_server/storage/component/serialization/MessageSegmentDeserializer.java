package xyz.hyffer.onemessage_server.storage.component.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import xyz.hyffer.onemessage_server.storage.component.MessageSegment;
import xyz.hyffer.onemessage_server.storage.component.MessageSegmentContent;

import java.io.IOException;

public class MessageSegmentDeserializer extends JsonDeserializer<MessageSegment> {

    @Override
    public MessageSegment deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);

        final String type = node.get("type").asText();
        switch (type) {
            case "plaintext":
                return new MessageSegment(
                        new MessageSegmentContent.Plaintext(node.get("content").get("text").asText())
                );
            case "image":
                return new MessageSegment(
                        new MessageSegmentContent.Image(node.get("content").get("url").asText())
                );
            default:
                return new MessageSegment(
                        new MessageSegmentContent.Plaintext("[NotSupportSegment]")
                );
        }
    }
}
