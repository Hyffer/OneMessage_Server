package xyz.hyffer.onemessage_server.storage.component.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import xyz.hyffer.onemessage_server.storage.component.MessageSegment;
import xyz.hyffer.onemessage_server.storage.component.MessageSegmentContent;

import java.io.IOException;

public class OneBotMessageSegmentSerializer extends JsonSerializer<MessageSegment> {

    @Override
    public void serialize(MessageSegment segment, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        switch (segment.getType()) {
            case "plaintext":
                jsonGenerator.writeObjectField("type", "text");
                jsonGenerator.writeFieldName("data");
                jsonGenerator.writeStartObject();
                jsonGenerator.writeObjectField("text", ((MessageSegmentContent.Plaintext) segment.getContent()).getText());
                jsonGenerator.writeEndObject();
                break;
            case "image":
                jsonGenerator.writeObjectField("type", "image");
                jsonGenerator.writeFieldName("data");
                jsonGenerator.writeStartObject();
                jsonGenerator.writeObjectField("file", ((MessageSegmentContent.Image) segment.getContent()).getUrl());
                jsonGenerator.writeEndObject();
                break;
            default:
                jsonGenerator.writeObjectField("type", "text");
                jsonGenerator.writeFieldName("data");
                jsonGenerator.writeStartObject();
                jsonGenerator.writeObjectField("text", "[NotSupportSegment]");
                jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndObject();
    }

}
