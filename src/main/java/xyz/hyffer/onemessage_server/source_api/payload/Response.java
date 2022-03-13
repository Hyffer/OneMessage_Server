package xyz.hyffer.onemessage_server.source_api.payload;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import xyz.hyffer.onemessage_server.source_api.payload.deserializer.ResponseDeserializer_get_contact_list;
import xyz.hyffer.onemessage_server.storage.component.Contact;

import java.util.List;

@Data
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

}
