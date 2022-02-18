package xyz.hyffer.onemessage_server.client_api.data;

import lombok.Data;
import xyz.hyffer.onemessage_server.storage.component.Message;

public abstract class RequestBody {

    @Data
    public static class RequestBody_get_contacts extends RequestBody {
        String sort;
        String key;
        int num;
    }

    @Data
    public static class RequestBody_get_messages extends RequestBody {
        int _CID;
        int lastMsg_MID;
        int num;
    }

    @Data
    public static class RequestBody_update_status extends RequestBody {
        int _CID;
        String status;
    }

    @Data
    public static class RequestBody_post_message extends RequestBody {
        int _CID;
        Message message;
    }

}
