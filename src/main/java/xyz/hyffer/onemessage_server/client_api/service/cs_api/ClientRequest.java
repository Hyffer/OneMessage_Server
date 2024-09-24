package xyz.hyffer.onemessage_server.client_api.service.cs_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientRequest {

    public enum CMD {
        @JsonProperty("get_contacts")
        GET_CONTACTS,
        @JsonProperty("get_messages")
        GET_MESSAGES,
        @JsonProperty("update_state")
        UPDATE_STATE,
        @JsonProperty("post_message")
        POST_MESSAGE
    }

    CMD cmd;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "cmd")
    @JsonSubTypes(value = {
            @JsonSubTypes.Type(value = ClientRequestBody.GetContacts.class, name = "get_contacts"),
            @JsonSubTypes.Type(value = ClientRequestBody.GetMessages.class, name = "get_messages"),
            @JsonSubTypes.Type(value = ClientRequestBody.UpdateState.class, name = "update_state"),
            @JsonSubTypes.Type(value = ClientRequestBody.PostMessage.class, name = "post_message"),
    })
    ClientRequestBody body;
}
