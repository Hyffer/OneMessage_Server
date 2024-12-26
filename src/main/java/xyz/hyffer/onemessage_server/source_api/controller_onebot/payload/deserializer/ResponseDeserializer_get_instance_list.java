package xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import xyz.hyffer.onemessage_server.model.ContactInstance;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Response;

import java.io.IOException;
import java.util.ArrayList;

public class ResponseDeserializer_get_instance_list extends JsonDeserializer<Response.Response_get_instance_list> {

    @Override
    public Response.Response_get_instance_list deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);

        if (node.get("status") == null || node.get("retcode") == null) throw new UnexpectedResponseException("");
        String status = node.get("status").asText();
        int retcode = node.get("retcode").asInt();
        Response.Response_get_instance_list response = new Response.Response_get_instance_list(status, retcode);

        JsonNode data = node.get("data");
        if (!(data instanceof ArrayNode))
            throw new UnexpectedResponseException("");

        int length = data.size();
        ArrayList<ContactInstance> instances = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            JsonNode element = data.get(i);
            String type;
            String id;
            String name;
            String remark;
            boolean isUser = element.has("user_id");
            if (isUser) {
                type = "Friend";
                id = element.get("user_id").asText();
                name = element.get("nickname").asText();
                if (element.has("remark"))
                    remark = element.get("remark").asText();
                else
                    remark = name;
            } else {
                type = "Group";
                id = element.get("group_id").asText();
                name = element.get("group_name").asText();
                remark = name;
            }
            instances.add(ContactInstance.builder()
                    .type(type)
                    .id(id)
                    .name(name)
                    .remark(remark)
                    .build()
            );
        }
        response.setData(instances);

        return response;
    }
}
