package xyz.hyffer.onemessage_server.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class MessageSegment implements Serializable {
    String type;
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes(value = {
            @JsonSubTypes.Type(value = MessageSegmentContent.Plaintext.class, name = "plaintext"),
            @JsonSubTypes.Type(value = MessageSegmentContent.Image.class, name = "image"),
    })
    MessageSegmentContent content;

    public MessageSegment() {}

    public MessageSegment(MessageSegmentContent content) {
        this.content = content;
        if (content instanceof MessageSegmentContent.Plaintext)
            type = "plaintext";
        else if (content instanceof MessageSegmentContent.Image)
            type = "image";
    }
}
