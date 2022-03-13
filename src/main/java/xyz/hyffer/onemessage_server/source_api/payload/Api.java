package xyz.hyffer.onemessage_server.source_api.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class Api {
    String action;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ApiParam params;

    @Data
    public static class Api_get_friend_list extends Api {
        public Api_get_friend_list() {
            action = "get_friend_list";
        }
    }

    @Data
    public static class Api_get_group_list extends Api {
        public Api_get_group_list() {
            action = "get_group_list";
        }
    }

}
