package xyz.hyffer.onemessage_server.client_api.service.cs_api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientRequestConverter implements Converter<String, ClientRequest> {

    @Resource
    ObjectMapper objectMapper;

    @Override
    public ClientRequest convert(@Nonnull String source) {
        try {
            return deserialize(source);
        } catch (ClientException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Convert json string to {@link ClientRequest} object.
     *
     * @param json request in json string
     * @return a {@link ClientRequest} object
     * @throws ClientException if request cannot be deserialized, or violates the specification
     */
    public ClientRequest deserialize(String json) throws ClientException {
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.size() != 2 || !root.has("cmd") || !root.has("body"))
                throw new ClientException(ClientException.Type.FORMAT_INCORRECT,
                        "Request syntax incorrect.\nrequest: " + json);

            ClientRequest.CMD cmd = ClientRequest.CMD.valueOf(root.get("cmd").asText().toUpperCase());
            ClientRequestBody body = deserializeBody(cmd, root.get("body").toString());

            return new ClientRequest(cmd, body);

        } catch (JsonProcessingException | IllegalArgumentException e) {
            throw new ClientException(ClientException.Type.FORMAT_INCORRECT,
                    "Request syntax incorrect.\nrequest: " + json, e);
        }
    }

    /**
     * Convert json string to a valid {@link ClientRequestBody} object.
     *
     * @param cmd  request command
     * @param body request body in json string
     * @return a {@link ClientRequestBody} object complying with the specification
     * @throws ClientException if request body cannot be deserialized, or violates the specification
     */
    public ClientRequestBody deserializeBody(ClientRequest.CMD cmd, String body) throws ClientException {
        try {
            ClientRequestBody requestBody;
            switch (cmd) {
                case GET_CONTACTS -> {
                    JsonNode root = objectMapper.readTree(body);
                    if (root.has("pinned")) {
                        requestBody = objectMapper.readValue(body, ClientRequestBody.GetContacts2.class);
                    } else if (root.has("key")) {
                        requestBody = objectMapper.readValue(body, ClientRequestBody.GetContacts3.class);
                    } else {
                        requestBody = objectMapper.readValue(body, ClientRequestBody.GetContacts1.class);
                    }
                }

                case GET_MESSAGES -> {
                    JsonNode root = objectMapper.readTree(body);
                    if (root.has("_CID")) {
                        requestBody = objectMapper.readValue(body, ClientRequestBody.GetMessages2.class);
                    } else {
                        requestBody = objectMapper.readValue(body, ClientRequestBody.GetMessages1.class);
                    }
                }

                case UPDATE_STATE -> {
                    requestBody = objectMapper.readValue(body, ClientRequestBody.UpdateState.class);
                }

                case POST_MESSAGE -> {
                    requestBody = objectMapper.readValue(body, ClientRequestBody.PostMessage.class);
                }

                default -> {
                    throw new ClientException(ClientException.Type.INTERNAL_ERROR,
                            "switch case not handled");
                }
            }

            assert requestBody != null;
            if (requestBody.isValid()) {
                return requestBody;
            }

            throw new ClientException(ClientException.Type.FORMAT_INCORRECT,
                    "Values do not meet requirements of the specification.\nrequest body: " + body
                            + "\n" + requestBody);

        } catch (JsonProcessingException e) {
            throw new ClientException(ClientException.Type.FORMAT_INCORRECT,
                    cmd.name().toLowerCase() + " request body json processing error.\nrequest body: " + body, e);
        }
    }
}
