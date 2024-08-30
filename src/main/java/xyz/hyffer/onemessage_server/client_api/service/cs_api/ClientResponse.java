package xyz.hyffer.onemessage_server.client_api.service.cs_api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.Message;

import java.util.List;

@Getter
@AllArgsConstructor
public abstract class ClientResponse {

    int code;

    public static class GetContacts extends ClientResponse {

        public List<Contact> contacts;

        public GetContacts(List<Contact> contacts) {
            super(200);
            this.contacts = contacts;
        }
    }

    public static class GetMessages extends ClientResponse {

        public List<Message> messages;

        public GetMessages(List<Message> messages) {
            super(200);
            this.messages = messages;
        }
    }

    public static class UpdateState extends ClientResponse {

        public UpdateState() {
            super(200);
        }
    }

    public static class PostMessage extends ClientResponse {

        int _MID;

        public PostMessage(int _MID) {
            super(202);
            this._MID = _MID;
        }
    }

    public static class Error extends ClientResponse {

        String msg;

        public Error(int code, String msg) {
            super(code);
            this.msg = msg;
        }
    }
}
