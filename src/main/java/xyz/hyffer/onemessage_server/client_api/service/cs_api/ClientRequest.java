package xyz.hyffer.onemessage_server.client_api.service.cs_api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientRequest {

    public enum CMD {
        GET_CONTACTS,
        GET_MESSAGES,
        UPDATE_STATE,
        POST_MESSAGE
    }

    CMD cmd;

    ClientRequestBody body;
}
