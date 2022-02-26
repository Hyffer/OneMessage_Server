package xyz.hyffer.onemessage_server.storage.component;

import lombok.Data;

public abstract class MessageSegmentContent {

    @Data
    public static class Plaintext extends MessageSegmentContent {
        String text;
    }

    @Data
    public static class Image extends MessageSegmentContent {
        String url;
    }

}
