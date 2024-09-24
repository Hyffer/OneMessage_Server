package xyz.hyffer.onemessage_server.client_api.service.cs_api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Deserializer for {@link ClientRequestBody} objects.
 * It also checks if the object violates the specification.
 *
 * @param <T> One of the direct subclasses of {@link ClientRequestBody}.
 *            Because of the way client API is designed, deserializer needs to know request type beforehand.
 */
public class ClientRequestBodyDeserializer<T extends ClientRequestBody> extends JsonDeserializer<ClientRequestBody> {

    /**
     * Deserialize {@link ClientRequestBody} object from JSON.
     * @param p Parsed used for reading JSON content
     * @param ctxt Context that can be used to access information about
     *   this deserialization activity.
     *
     * @return a {@link ClientRequestBody} object
     * @throws IllegalArgumentException wrapping a {@link ClientException} if request body cannot be deserialized, or violates the specification
     */
    @Override
    public ClientRequestBody deserialize(JsonParser p, DeserializationContext ctxt) {
        try {
            @SuppressWarnings("unchecked")
            Class<T> Tclass = (Class<T>) GenericTypeResolver.resolveTypeArgument(this.getClass(), ClientRequestBodyDeserializer.class);
            assert Tclass != null;

            String camelCaseName = Tclass.getSimpleName();
            String snakeCaseName = camelCaseName.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
            ClientRequest.CMD cmd = ClientRequest.CMD.valueOf(snakeCaseName.toUpperCase());

            String json = p.readValueAsTree().toString();
            return deserializeOfType(cmd, json);

        } catch (ClientException | IOException _e) {
            ClientException ex;
            if (_e instanceof ClientException e) {
                ex = e;
            } else {
                ex = new ClientException(ClientException.Type.FORMAT_INCORRECT, "ClientRequestBody json deserialize error.", _e);
            }
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * ObjectMapper used for deserialization.
     * <p>
     * Here inside the custom deserializer of {@link ClientRequestBody}, we need to take special care when using ObjectMapper.
     * `@JsonDeserialize(using=...)` annotation is picked up as default for subclasses, so to avoid infinite recursion,
     * ObjectMapper need resetting to default behavior before any deserialization call.
     * (refer to: <a href="https://stackoverflow.com/questions/32551983/how-use-jackson-objectmapper-inside-custom-deserializer">
     * java - How use jackson ObjectMapper inside custom deserializer? - Stack Overflow</a>)
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonDeserialize
    private interface DefaultJsonDeserializer {
        // Reset default json deserializer
    }

    static {
        // scan for all subclasses of ClientRequestBody
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(@Nonnull AnnotatedBeanDefinition beanDefinition) {
                return true;
            }
        };
        provider.addIncludeFilter(new AssignableTypeFilter(ClientRequestBody.class));
        String basePackage = ClientRequestBody.class.getPackageName();
        Set<BeanDefinition> components = provider.findCandidateComponents(basePackage);

        for (BeanDefinition component : components) {
            try {
                Class<?> cls = Class.forName(component.getBeanClassName());
                // use Jackson Mix-in Annotations to remove the custom deserializer
                objectMapper.addMixIn(cls, DefaultJsonDeserializer.class);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
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
    static ClientRequestBody deserializeOfType(ClientRequest.CMD cmd, String body) throws ClientException {
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
