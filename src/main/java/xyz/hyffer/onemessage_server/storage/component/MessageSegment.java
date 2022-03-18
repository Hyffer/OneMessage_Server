package xyz.hyffer.onemessage_server.storage.component;

import lombok.Data;

@Data
public class MessageSegment {
    String type;
    MessageSegmentContent content;

    public MessageSegment(MessageSegmentContent content) {
        this.content = content;
        if (content instanceof MessageSegmentContent.Plaintext)
            type = "plaintext";
        else if (content instanceof MessageSegmentContent.Image)
            type = "image";
    }
}
