package xyz.hyffer.onemessage_server.client_api.service.cs_api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test {@link ClientRequest} deserialization.
 * <p>
 * More tests on {@link ClientRequestBody} can be found in {@link ClientRequestBodyDeserializationTest}.
 */
@ExtendWith(MockitoExtension.class)
public class ClientRequestDeserializationTest {

    static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserialize_valid_request() {
        try {
            ClientRequest request = objectMapper.readValue("""
                    {
                        "cmd": "get_contacts",
                        "body": {
                            "pinned": true,
                            "limit": 50
                        }
                    }
                    """, ClientRequest.class
            );
            assertThat(request.getCmd()).isEqualTo(ClientRequest.CMD.GET_CONTACTS);
            assertThat(request.getBody()).isInstanceOf(ClientRequestBody.GetContacts2.class);

            request = objectMapper.readValue("""
                    {
                        "cmd": "get_messages",
                        "body": {
                            "_CID": 3,
                            "post_rank": 100,
                            "limit": 15
                        }
                    }
                    """, ClientRequest.class
            );
            assertThat(request.getCmd()).isEqualTo(ClientRequest.CMD.GET_MESSAGES);
            assertThat(request.getBody()).isInstanceOf(ClientRequestBody.GetMessages2.class);

            request = objectMapper.readValue("""
                    {
                        "cmd": "update_state",
                        "body": {
                            "_CID": 1,
                            "read": true
                        }
                    }
                    """, ClientRequest.class
            );
            assertThat(request.getCmd()).isEqualTo(ClientRequest.CMD.UPDATE_STATE);
            assertThat(request.getBody()).isInstanceOf(ClientRequestBody.UpdateState.class);

        } catch (JsonProcessingException | RuntimeException e) {
            fail();
        }
    }

    @Test
    void deserialize_invalid_request() {
        assertThrows(JsonProcessingException.class, () -> objectMapper.readValue("""
                {
                    "cmd": "abc",
                    "body": {
                        "pinned": true,
                        "limit": "50"
                    }
                }
                """, ClientRequest.class
        ));

        assertThrows(JsonProcessingException.class, () -> objectMapper.readValue("""
                {
                    "cmd": false,
                    "body": {
                        "pinned": true,
                        "limit": "50"
                    }
                }
                """, ClientRequest.class
        ));

        assertThrows(JsonProcessingException.class, () -> objectMapper.readValue("""
                {
                    "cmd": "get_contacts"
                }
                """, ClientRequest.class
        ));

        assertThrows(JsonProcessingException.class, () -> objectMapper.readValue("""
                {
                    "cmd": "get_contacts",
                    "body": {
                        "pinned": true,
                        "limit" 50
                    }
                }
                """, ClientRequest.class
        ));

        RuntimeException rtEx = assertThrows(RuntimeException.class, () -> objectMapper.readValue("""
                {
                    "cmd": "get_messages",
                    "body": {
                        "_CID": 1,
                        "limit": null
                    }
                }
                """, ClientRequest.class
        ));
        assertThat(rtEx).isInstanceOf(IllegalArgumentException.class);
        assertThat(rtEx.getCause()).isInstanceOf(ClientException.class);
    }
}
