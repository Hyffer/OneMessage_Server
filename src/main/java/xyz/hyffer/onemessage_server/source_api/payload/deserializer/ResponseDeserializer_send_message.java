package xyz.hyffer.onemessage_server.source_api.payload.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import xyz.hyffer.onemessage_server.source_api.payload.Response;

import java.io.IOException;

public class ResponseDeserializer_send_message extends JsonDeserializer<Response.Response_send_message> {
    @Override
    public Response.Response_send_message deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);

        if (node.get("status") == null || node.get("retcode") == null) throw new UnexpectedResponseException("");
        String status = node.get("status").asText();
        int retcode = node.get("retcode").asInt();
        Response.Response_send_message response = new Response.Response_send_message(status, retcode);

        JsonNode data = node.get("data");

        return response;
    }
}
