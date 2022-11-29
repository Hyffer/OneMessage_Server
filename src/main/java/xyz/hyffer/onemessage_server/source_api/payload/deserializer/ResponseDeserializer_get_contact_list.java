package xyz.hyffer.onemessage_server.source_api.payload.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import xyz.hyffer.onemessage_server.source_api.payload.Response;
import xyz.hyffer.onemessage_server.storage.component.Contact;

import java.io.IOException;
import java.util.ArrayList;

public class ResponseDeserializer_get_contact_list extends JsonDeserializer<Response.Response_get_contact_list> {

    @Override
    public Response.Response_get_contact_list deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);

        if (node.get("status") == null || node.get("retcode") == null) throw new UnexpectedResponseException("");
        String status = node.get("status").asText();
        int retcode = node.get("retcode").asInt();
        Response.Response_get_contact_list response = new Response.Response_get_contact_list(status, retcode);

        JsonNode data = node.get("data");
        if (!(data instanceof ArrayNode))
            throw new UnexpectedResponseException("");

        int length = data.size();
        ArrayList<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            JsonNode element = data.get(i);
            String type;
            long id;
            String name;
            String remark;
            boolean isUser = element.has("user_id");
            if (isUser) {
                type = "Friend";
                id = element.get("user_id").asLong();
                name = element.get("nickname").asText();
                if (element.has("remark"))
                    remark = element.get("remark").asText();
                else
                    remark = name;
            } else {
                type = "Group";
                id = element.get("group_id").asLong();
                name = element.get("group_name").asText();
                remark = name;
            }
            contacts.add(new Contact(type, id, name, remark));
        }
        response.setData(contacts);

        return response;
    }
}
