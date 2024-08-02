package xyz.hyffer.onemessage_server.source_api.controller_onebot.payload;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.deserializer.ResponseDeserializer_get_contact_list;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.deserializer.ResponseDeserializer_send_message;

import java.util.List;

@Data
// TODO: use Generics to simplify ResponseDeserializer and reduce duplicated code
public class Response {
    String status;
    int retcode;

    @Data
    @JsonDeserialize(using = ResponseDeserializer_get_contact_list.class)
    public static class Response_get_contact_list extends Response {
        List<Contact> data;

        public Response_get_contact_list(String status, int retcode) {
            this.status = status;
            this.retcode = retcode;
        }
    }

    @JsonDeserialize(using = ResponseDeserializer_send_message.class)
    public static class Response_send_message extends Response {
        D data;

        private static class D {
            int message_id;
        }

        public Response_send_message(String status, int retcode) {
            this.status = status;
            this.retcode = retcode;
        }
    }

}
