package xyz.hyffer.onemessage_server.source_api.payload;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import xyz.hyffer.onemessage_server.source_api.payload.deserializer.EventDeserializer;

@Data
@JsonDeserialize(using = EventDeserializer.class)
public class Event {
    long time;
    long self_id;
    String post_type;

    public static class MetaEvent extends Event {
        String meta_event_type;

        @Data
        public static class LifeCycle extends MetaEvent {
            String sub_type;

            public LifeCycle(long time, long self_id, String post_type, String meta_event_type, String sub_type) {
                this.time = time;
                this.self_id = self_id;
                this.post_type = post_type;
                this.meta_event_type = meta_event_type;
                this.sub_type = sub_type;
            }
        }

        @Data
        public static class HeartBeat {

        }

    }


    public static class MessageEvent extends Event {
        String message_type;

        @Data
        public static class Private extends MessageEvent {

        }

        @Data
        public static class Group extends MessageEvent {

        }
    }

}
