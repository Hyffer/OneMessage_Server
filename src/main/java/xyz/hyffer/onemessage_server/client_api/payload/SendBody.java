package xyz.hyffer.onemessage_server.client_api.payload;

import lombok.Data;
import xyz.hyffer.onemessage_server.storage.component.Contact;
import xyz.hyffer.onemessage_server.storage.component.Message;

import java.util.ArrayList;
import java.util.List;

public abstract class SendBody {

    @Data
    public static class ResponseBody extends SendBody {
        int code;

        public enum ResponseCode {
            SUCCESS,
            AUTHENTICATE_FAILED,
            UNEXPECTED_REQUEST,
            UNEXPECTED_VALUE
        }

        public ResponseBody(ResponseCode code) {
            this.code = code.ordinal();
        }

        public ResponseBody() {
            this.code = ResponseCode.SUCCESS.ordinal();
        }


        @Data
        public static class ResponseBody_get_contacts extends ResponseBody {
            ArrayList<Contact> contacts;

            public ResponseBody_get_contacts(List<Contact> contacts) {
                super();
                this.contacts = (ArrayList<Contact>) contacts;
            }
        }

        @Data
        public static class ResponseBody_get_messages extends ResponseBody {
            ArrayList<Message> messages;

            public ResponseBody_get_messages(List<Message> messages) {
                super();
                this.messages = (ArrayList<Message>) messages;
            }
        }

        @Data
        public static class ResponseBody_no_content extends ResponseBody {
        }

        @Data
        public static class ResponseBody_error extends ResponseBody {
            String msg;

            public ResponseBody_error(ResponseCode code) {
                super(code);
                switch (code) {
                    case AUTHENTICATE_FAILED:
                        msg = "Authenticate failed.";
                        break;
                    case UNEXPECTED_REQUEST:
                        msg = "Request cannot be resolved.";
                        break;
                    case UNEXPECTED_VALUE:
                        msg = "Unexpected value.";
                        break;
                }
            }
        }

    }

    @Data
    public static class PushBody extends SendBody {
        String event;
        int _CID;
    }

}
