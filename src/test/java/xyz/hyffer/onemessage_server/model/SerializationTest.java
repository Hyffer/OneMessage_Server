package xyz.hyffer.onemessage_server.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class SerializationTest {

    @Test
    void message_serialization() {
        Message message = DataGenerator.getTestMessages().get(0);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode tree = mapper.valueToTree(message);
            JsonNode segmentRoot = tree.get("segments").get(0);
            // check nested subclass is serialized properly
            assertThat(segmentRoot.get("content")).isNotNull();
            assertThat(segmentRoot.get("content").get("type")).isNull();

            String str = mapper.writeValueAsString(message);
            System.out.println(message);
            System.out.println(str);
            // test deserialization
            Message message1 = mapper.readValue(str, new TypeReference<>() {});
            assertThat(message1).usingRecursiveComparison()
                    .isEqualTo(message);
        } catch (JsonProcessingException e) {
            fail();
        }
    }
}
