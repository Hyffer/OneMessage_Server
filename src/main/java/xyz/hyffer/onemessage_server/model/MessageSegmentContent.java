package xyz.hyffer.onemessage_server.model;

import lombok.Data;

import java.io.Serializable;

public abstract class MessageSegmentContent implements Serializable {

    @Data
    public static class Plaintext extends MessageSegmentContent {
        String text;
        public Plaintext() {}
        public Plaintext(String text) {
            this.text = text;
        }
    }

    @Data
    public static class Image extends MessageSegmentContent {
        String url;
        public Image() {}
        public Image(String url) {
            this.url = url;
        }
    }

}
