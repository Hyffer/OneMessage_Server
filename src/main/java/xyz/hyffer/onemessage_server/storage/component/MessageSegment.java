package xyz.hyffer.onemessage_server.storage.component;

import lombok.Data;

@Data
public class MessageSegment {
    String type;
    MessageSegmentContent content;
}
