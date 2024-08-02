package xyz.hyffer.onemessage_server.client_api.controller_ws.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
public class Request {
    String cmd;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "cmd")
    @JsonSubTypes(value = {
            @JsonSubTypes.Type(value = RequestBody.RequestBody_get_contacts.class, name = "get_contacts"),
            @JsonSubTypes.Type(value = RequestBody.RequestBody_get_messages.class, name = "get_messages"),
            @JsonSubTypes.Type(value = RequestBody.RequestBody_update_status.class, name = "update_status"),
            @JsonSubTypes.Type(value = RequestBody.RequestBody_post_message.class, name = "post_message")
    })
    RequestBody body;
}
