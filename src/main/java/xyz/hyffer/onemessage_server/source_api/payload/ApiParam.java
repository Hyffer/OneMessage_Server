package xyz.hyffer.onemessage_server.source_api.payload;

import lombok.Data;
import xyz.hyffer.onemessage_server.storage.component.MessageSegment;

import java.util.List;

public abstract class ApiParam {

    @Data
    public static class ApiParam_no_content extends ApiParam {

    }

    @Data
    public static class ApiParam_send_message extends ApiParam {
        String message_type;
        long user_id;
        long group_id;
        List<MessageSegment> message;
        boolean auto_escape;

        public ApiParam_send_message() {
            this.auto_escape = false;
        }
    }

}
