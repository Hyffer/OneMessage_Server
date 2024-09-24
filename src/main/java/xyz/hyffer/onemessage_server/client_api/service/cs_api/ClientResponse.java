package xyz.hyffer.onemessage_server.client_api.service.cs_api;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.Message;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonView(ClientResponse.NoStatusCodeView.class)
public abstract class ClientResponse {

    // some form of communication, like http, have their native implementation of status code
    // so it can be erased from response body
    public interface NoStatusCodeView {}
    public interface WithStatusCodeView extends NoStatusCodeView {}

    @JsonView(WithStatusCodeView.class)
    int code;

    @Getter
    public static class GetContacts extends ClientResponse {

        public List<Contact> contacts;

        public GetContacts(List<Contact> contacts) {
            super(200);
            this.contacts = contacts;
        }
    }

    @Getter
    public static class GetMessages extends ClientResponse {

        public List<Message> messages;

        public GetMessages(List<Message> messages) {
            super(200);
            this.messages = messages;
        }
    }

    @Getter
    public static class UpdateState extends ClientResponse {

        public UpdateState() {
            super(200);
        }
    }

    @Getter
    public static class PostMessage extends ClientResponse {

        int _MID;

        public PostMessage(int _MID) {
            super(202);
            this._MID = _MID;
        }
    }

    @Getter
    public static class Error extends ClientResponse {

        String msg;

        public Error(int code, String msg) {
            super(code);
            this.msg = msg;
        }

        public Error(ClientException e) {
            super(e.getCode());
            this.msg = e.getMsg();
        }
    }
}
