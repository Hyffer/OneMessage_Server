package xyz.hyffer.onemessage_server.source_api.payload;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import xyz.hyffer.onemessage_server.source_api.payload.deserializer.EventDeserializer;
import xyz.hyffer.onemessage_server.storage.component.Message;

@Data
@JsonDeserialize(using = EventDeserializer.class)
public class Event {
    long time;
    String self_id;
    String post_type;

    public static class MetaEvent extends Event {
        String meta_event_type;

        @Data
        public static class LifeCycle extends MetaEvent {
            String sub_type;

            public LifeCycle(long time, String self_id, String post_type, String meta_event_type, String sub_type) {
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


    @Data
    public static class MessageEvent extends Event {
        String message_type;
        String sub_type;
        String contact_id;
        Message message;

        public MessageEvent(long time, String self_id, String post_type,
                            String message_type, String sub_type, String contact_id) {
            this.time = time;
            this.self_id = self_id;
            this.post_type = post_type;
            this.message_type = message_type;
            this.sub_type = sub_type;
            this.contact_id = contact_id;
        }

    }

}
