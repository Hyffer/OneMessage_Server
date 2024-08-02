package xyz.hyffer.onemessage_server.client_api.controller_ws.payload;

import lombok.Data;

@Data
public class Send {
    String type;
    SendBody body;

    public void construct(SendBody body) {
        if (body instanceof SendBody.ResponseBody) type = "response";
        else if (body instanceof SendBody.PushBody) type = "push";
        this.body = body;
    }

}
